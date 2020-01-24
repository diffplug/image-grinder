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

public class Size {
	private final int width, height;

	public static Size createRect(int width, int height) {
		return new Size(width, height);
	}

	public static Size createSquare(int size) {
		return new Size(size, size);
	}

	public Size scaled(double factor) {
		return createRect((int) Math.round(width * factor), (int) Math.round(height * factor));
	}

	public Size withMaxDim(int max) {
		double thisMax = Math.max(width, height);
		return scaled(max / thisMax);
	}

	private Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	@Override
	public int hashCode() {
		// 997 is prime, and most of our sizes are gonna be <<< 1000 px
		return 997 * height + width;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Size) {
			Size o = (Size) obj;
			return o.width == width && o.height == height;
		} else {
			return false;
		}
	}

	public interface Has {
		Size size();
	}
}
