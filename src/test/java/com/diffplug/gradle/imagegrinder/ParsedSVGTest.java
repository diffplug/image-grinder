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

import org.gradle.internal.impldep.org.junit.Assert;
import org.junit.Test;

import com.diffplug.common.io.Files;

public class ParsedSVGTest extends ResourceHarness {
	@Test
	public void testRender() throws Exception {
		File svg = newFile("test.svg", getTestResource("Refresh.svg"));
		ParsedSVG parsed = ParsedSVG.parse(svg);
		File out100 = newFile("test100.png");
		File out200 = newFile("test200.png");
		parsed.renderFile(out100, 16, 16);
		parsed.renderFile(out200, 32, 32);
		Assert.assertArrayEquals(getTestResource("Refresh100.png"), Files.toByteArray(out100));
		Assert.assertArrayEquals(getTestResource("Refresh200.png"), Files.toByteArray(out200));
	}
}
