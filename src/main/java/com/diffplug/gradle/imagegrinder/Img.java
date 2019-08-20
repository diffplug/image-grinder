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

import com.diffplug.common.base.Errors;

/**
 * Wrapper for an image, with methods for writing it into
 * the destination directory with whatever processing you
 * would like.
 */
public abstract class Img<T extends Size.Has> implements Size.Has {
	private final ImageGrinderTask task;
	private final Subpath subpath;
	private final T parsed;

	public Img(ImageGrinderTask task, Subpath subpath, T parsed) {
		this.task = task;
		this.subpath = subpath;
		this.parsed = parsed;
	}

	public Subpath subpath() {
		return subpath;
	}

	public T parsed() {
		return parsed;
	}

	@Override
	public Size size() {
		return parsed().size();
	}

	/** Renders the image with the given suffix. */
	public void render(String suffix) {
		render(suffix, 1);
	}

	/** Renders the image with the given suffix at the given scale. */
	public void render(String suffix, double scale) {
		render(suffix, size().scaled(scale));
	}

	/** Renders the image with the given suffix at the given size. */
	public void render(String suffix, Size size) {
		renderFull(subpath().withoutExtension() + suffix, size);
	}

	/** Renders the image with the given full path (relative to the task's dst folder) at the given size. */
	public void renderFull(String fullPath, Size size) {
		String extension = Subpath.extension(fullPath);
		try {
			switch (extension) {
			case "png":
				File file = registerDstFile(fullPath);
				renderPng(file, size);
				break;
			default:
				throw new IllegalArgumentException("We only support render to .png, not " + fullPath);
			}
		} catch (Exception e) {
			throw Errors.asRuntime(e);
		}
	}

	File registerDstFile(String fullPath) {
		File file = new File(task.getDstDir().getAsFile().get(), fullPath);
		FileMisc.mkdirs(file.getParentFile());
		synchronized (task.map) {
			task.map.put(new File(task.getSrcDir().getAsFile().get(), subpath.full()), file);
		}
		return file;
	}

	protected abstract void renderPng(File file, Size size) throws Exception;
}
