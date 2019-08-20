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
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileType;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.ChangeType;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkQueue;
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
@CacheableTask
public abstract class ImageGrinderTask extends DefaultTask {
	private final WorkerExecutor workerExecutor;

	@Inject
	public ImageGrinderTask(WorkerExecutor workerExecutor) {
		this.workerExecutor = workerExecutor;
	}

	@Incremental
	@PathSensitive(PathSensitivity.RELATIVE)
	@InputDirectory
	public abstract DirectoryProperty getSrcDir();

	@OutputDirectory
	public abstract DirectoryProperty getDstDir();

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

	Action<Img<?>> grinder;

	public void grinder(Action<Img<?>> grinder) {
		this.grinder = grinder;
	}

	@TaskAction
	public void performAction(InputChanges inputChanges) throws Exception {
		Objects.requireNonNull(grinder, "grinder");

		File cache = new File(getProject().getBuildDir(), "cache" + getName());
		if (!inputChanges.isIncremental()) {
			getProject().delete(getDstDir().getAsFile().get());
			map = HashMultimap.create();
		} else {
			readFromCache(cache);
		}
		WorkQueue queue = workerExecutor.noIsolation();
		for (FileChange fileChange : inputChanges.getFileChanges(getSrcDir())) {
			if (fileChange.getFileType() == FileType.DIRECTORY) {
				continue;
			}
			boolean modifiedOrRemoved = fileChange.getChangeType() == ChangeType.MODIFIED || fileChange.getChangeType() == ChangeType.REMOVED;
			boolean modifiedOrAdded = fileChange.getChangeType() == ChangeType.MODIFIED || fileChange.getChangeType() == ChangeType.ADDED;
			if (modifiedOrRemoved) {
				logger.info("removing: " + fileChange.getNormalizedPath());
				remove(fileChange.getFile());
			}
			if (modifiedOrAdded) {
				logger.info("submitted to render:" + fileChange.getNormalizedPath());
				queue.submit(RenderSvg.class, params -> {
					params.getSourceFile().set(fileChange.getFile());
					params.getTaskRef().set(SerializableRef.create(ImageGrinderTask.this));
				});
			}
		}
		queue.await();
		writeToCache(cache);
	}

	private void remove(File srcFile) {
		synchronized (map) {
			map.removeAll(srcFile).forEach(getProject()::delete);
		}
	}

	public boolean debug = false;

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

	public interface RenderSvgParams extends WorkParameters {
		RegularFileProperty getSourceFile();

		Property<SerializableRef<ImageGrinderTask>> getTaskRef();
	}

	public static abstract class RenderSvg implements WorkAction<RenderSvgParams> {
		@Override
		public void execute() {
			File sourceFile = getParameters().getSourceFile().get().getAsFile();
			ImageGrinderTask task = getParameters().getTaskRef().get().value();
			Subpath subpath = Subpath.from(task.getSrcDir().getAsFile().get(), sourceFile);
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
	public Serializable getBumpThisNumberWhenTheGrinderChanges() {
		return bumpThisNumberWhenTheGrinderChanges;
	}

	public void setBumpThisNumberWhenTheGrinderChanges(Serializable bumpThisNumberWhenTheGrinderChanges) {
		this.bumpThisNumberWhenTheGrinderChanges = bumpThisNumberWhenTheGrinderChanges;
	}

	private static final Logger logger = LoggerFactory.getLogger(ImageGrinderTask.class);
}
