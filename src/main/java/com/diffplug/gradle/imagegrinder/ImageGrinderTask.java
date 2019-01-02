/*
 * Copyright 2017 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.gradle.imagegrinder;

import java.io.File;
import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.WorkerExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diffplug.common.collect.HashMultimap;

/**
 * See [README.md](https://github.com/diffplug/image-grinder) for usage instructions.
 * 
 * ## Tedious thing #1: Worker bypass
 * 
 * Worker requires that all arguments to its worker runnables ({@link ProcessFile}
 * in this case) be Serializable.  There's no way to serialize our {@link #grinder(Action)}, so we had
 * to use {@link SerializableRef} to sneakily pass our task to the worker.
 * 
 * ## Tedious thing #2: Removal handling
 * 
 * Tedious thing #2: .java to .class has a 1:1 mapping.  But that is not true for these images - a pipeline
 * might create two images from one source, and the number of outputs might even change based on the content
 * of the input (e.g. skip hi-res versions of very large images).
 * 
 * That means that when the user removes or changes an image, we need to remember exactly which files it
 * created last time, or else we might end up with stale results lying around.  So, this task has the
 * {@link #map} field which is a multimap from source file to the dst files it created.  When the task starts,
 * it reads this map from disk, and when the task finishes, it writes it to disk.  Whenever an {@link Img} is
 * rendered, the filename that was written is saved to this map via the {@link Img#registerDstFile(String)}
 * method.
 */
public class ImageGrinderTask extends DefaultTask {
	private final WorkerExecutor workerExecutor;

	@Inject
	public ImageGrinderTask(WorkerExecutor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	@InputDirectory
	File srcDir;

	@OutputDirectory
	File dstDir;

	@Input
	Serializable bumpThisNumberWhenTheGrinderChanges = new NeverUpToDateBetweenRuns();

	static class NeverUpToDateBetweenRuns extends LazyForwardingEquality<Integer> {
		private static final long serialVersionUID = 1L;
		private static final Random RANDOM = new Random();

		@Override
		protected Integer calculateState() throws Exception {
			return RANDOM.nextInt();
		}
	}

	@Internal
	Action<Img<?>> grinder;

	public void grinder(Action<Img<?>> grinder) {
		this.grinder = grinder;
	}

	@TaskAction
	public void performAction(IncrementalTaskInputs inputs) throws Exception {
		Objects.requireNonNull(srcDir, "srcDir");
		Objects.requireNonNull(dstDir, "dstDir");
		Objects.requireNonNull(grinder, "grinder");

		File cache = new File(getProject().getBuildDir(), "cache" + getName());
		if (!inputs.isIncremental()) {
			getProject().delete(dstDir);
			map = HashMultimap.create();
		} else {
			readFromCache(cache);
		}
		inputs.outOfDate(outOfDate -> {
			// skip anything that isn't a folder
			if (!outOfDate.getFile().isFile()) {
				return;
			}
			logger.info("outOfDate: " + Subpath.subpath(srcDir, outOfDate.getFile()));
			if (outOfDate.isModified()) {
				remove(outOfDate.getFile());
			}
			workerExecutor.submit(ProcessFile.class, workerConfig -> {
				workerConfig.setIsolationMode(IsolationMode.NONE);
				workerConfig.setParams(SerializableRef.create(ImageGrinderTask.this), outOfDate.getFile());
			});
		});
		inputs.removed(removed -> {
			logger.info("removed: " + Subpath.subpath(srcDir, removed.getFile()));
			remove(removed.getFile());
		});
		workerExecutor.await();
		writeToCache(cache);
	}

	private void remove(File srcFile) {
		synchronized (map) {
			map.removeAll(srcFile).forEach(getProject()::delete);
		}
	}

	@Internal
	public boolean debug = false;

	@Internal
	HashMultimap<File, File> map;

	@SuppressWarnings("unchecked")
	private void readFromCache(File file) {
		if (file.exists()) {
			map = SerializableMisc.fromFile(HashMultimap.class, file);
		} else {
			map = HashMultimap.create();
		}
	}

	private void writeToCache(File file) {
		synchronized (map) {
			SerializableMisc.toFile(map, file);
		}
	}

	public static class ProcessFile implements Runnable {
		final ImageGrinderTask task;
		final File sourceFile;

		@Inject
		public ProcessFile(SerializableRef<ImageGrinderTask> taskRef, File sourceFile) {
			this.task = taskRef.value();
			this.sourceFile = sourceFile;
		}

		@Override
		public void run() {
			Subpath subpath = Subpath.from(task.srcDir, sourceFile);
			Img<?> img;
			switch (subpath.extension()) {
			case "svg":
				ParsedSVG parsed = ParsedSVG.parse(sourceFile);
				img = new Img<ParsedSVG>(task, subpath, parsed) {
					@Override
					protected void renderPng(File file, Size size) throws Exception {
						parsed.renderPng(file, size);
					}
				};
				break;
			default:
				throw new IllegalArgumentException("Can only handle svg, not " + subpath);
			}
			task.grinder.execute(img);
		}
	}

	/////////////////////////////////////
	// Autogenerated getters / setters //
	/////////////////////////////////////
	public File getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(File srcDir) {
		this.srcDir = srcDir;
	}

	public File getDstDir() {
		return dstDir;
	}

	public void setDstDir(File dstDir) {
		this.dstDir = dstDir;
	}

	public Serializable getBumpThisNumberWhenTheGrinderChanges() {
		return bumpThisNumberWhenTheGrinderChanges;
	}

	public void setBumpThisNumberWhenTheGrinderChanges(Serializable bumpThisNumberWhenTheGrinderChanges) {
		this.bumpThisNumberWhenTheGrinderChanges = bumpThisNumberWhenTheGrinderChanges;
	}

	private static final Logger logger = LoggerFactory.getLogger(ImageGrinderTask.class);
}
