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

public abstract class Img<T extends Size.Has> implements Size.Has {
	private final ImageGrinderTask task;
	private final Subpath subpath;
	private final T raw;

	public Img(ImageGrinderTask task, Subpath subpath, T raw) {
		this.task = task;
		this.subpath = subpath;
		this.raw = raw;
	}

	public Subpath subpath() {
		return subpath;
	}

	public T raw() {
		return raw;
	}

	@Override
	public Size size() {
		return raw().size();
	}

	public void render(String ext) {
		render(ext, 1);
	}

	public void render(String ext, double scale) {
		render(ext, size().scaled(scale));
	}

	public void render(String ext, Size size) {
		renderFull(subpath().withoutExtension + ext, size);
	}

	public void renderFull(String full, Size size) {
		String extension = Subpath.extension(full);
		try {
			switch (extension) {
			case "png":
				File file = registerDstFile(full);
				renderPng(file, size);
				break;
			default:
				throw new IllegalArgumentException("We only support render to .png, not " + full);
			}
		} catch (Exception e) {
			throw Errors.asRuntime(e);
		}
	}

	private File registerDstFile(String full) {
		File file = new File(task.dstDir, full);
		file.getParentFile().mkdirs();
		synchronized (task.map) {
			task.map.put(new File(task.srcDir, subpath.full), file);
		}
		return file;
	}

	protected abstract void renderPng(File fuile, Size size) throws Exception;
}
