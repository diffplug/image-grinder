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
import com.diffplug.common.base.Throwing;

/** Miscellaneous utilties for copying files around. */
class FileMisc {

	/** Calls {@link File#mkdirs()} and throws an exception if it fails. */
	public static void mkdirs(File d) {
		retry(d, dir -> {
			java.nio.file.Files.createDirectories(dir.toPath());
			return null;
		});
	}

	private static final int MS_RETRY = 500;

	/**
	 * Retries an action every ms, for 250ms, until it finally works or fails. 
	 *
	 * Makes FS operations more reliable.
	 */
	private static <T> T retry(File input, Throwing.Function<File, T> function) {
		long start = System.currentTimeMillis();
		Throwable lastException;
		do {
			try {
				return function.apply(input);
			} catch (Throwable e) {
				lastException = e;
				Errors.suppress().run(() -> Thread.sleep(1));
			}
		} while (System.currentTimeMillis() - start < MS_RETRY);
		throw Errors.asRuntime(lastException);
	}
}
