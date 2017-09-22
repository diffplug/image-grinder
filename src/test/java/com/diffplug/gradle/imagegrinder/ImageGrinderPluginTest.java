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

import java.io.IOException;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.Test;

public class ImageGrinderPluginTest extends ResourceHarness {
	private GradleRunner gradleRunner() throws IOException {
		return GradleRunner.create().withProjectDir(rootFolder()).withPluginClasspath();
	}

	@Test
	public void test() throws Exception {
		write("build.gradle",
				"plugins {",
				"    id 'com.diffplug.gradle.image-grinder'",
				"}",
				"imageGrinder {",
				"  eclipseSvg {",
				"    srcDir = file('src')",
				"    dstDir = file('dst')",
				"    bumpThisNumberWhenTheGrinderChanges = 1",
				"    grinder { img ->",
				"        img.render('.png')",
				"        img.render('@2x.png', 2)",
				"    }",
				"  }",
				"}");
		write("src/refresh.svg", readTestResource("refresh.svg"));
		gradleRunner().withArguments("eclipseSvg", "--stacktrace").build();
		assertFile("dst/refresh.png").isEqualTo(readTestResource("refresh16.png"));
		assertFile("dst/refresh@2x.png").isEqualTo(readTestResource("refresh32.png"));
	}
}
