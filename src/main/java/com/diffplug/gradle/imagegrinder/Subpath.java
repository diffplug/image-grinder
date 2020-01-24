/*
 * Copyright 2020 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.gradle.imagegrinder;


import com.diffplug.common.base.Preconditions;
import java.io.File;

public class Subpath {
	private final String full;
	private final String extension;
	private final String withoutExtension;

	public static Subpath from(File root, File child) {
		String rootPath = root.getAbsolutePath().replace('\\', '/') + '/';
		String childPath = child.getAbsolutePath().replace('\\', '/');
		Preconditions.checkArgument(childPath.startsWith(rootPath), "%s needs to start with %s", childPath, rootPath);
		return new Subpath(childPath.substring(rootPath.length()));
	}

	public String full() {
		return full;
	}

	public String extension() {
		return extension;
	}

	public String withoutExtension() {
		return withoutExtension;
	}

	private Subpath(String full) {
		this.full = full;
		this.extension = extension(full);
		this.withoutExtension = full.substring(0, full.length() - extension.length() - 1);
	}

	static String subpath(File root, File child) {
		String rootPath = root.getAbsolutePath().replace('\\', '/') + '/';
		String childPath = child.getAbsolutePath().replace('\\', '/');
		Preconditions.checkArgument(childPath.startsWith(rootPath), "%s needs to start with %s", childPath, rootPath);
		return childPath.substring(rootPath.length());
	}

	static String extension(String subpath) {
		int idx = subpath.lastIndexOf('.');
		Preconditions.checkArgument(idx >= 0, "'%s' must contain a '.'", subpath);
		Preconditions.checkArgument(idx < subpath.length() - 1, "'%s' can't end in '.'", subpath);
		return subpath.substring(idx + 1);
	}
}
