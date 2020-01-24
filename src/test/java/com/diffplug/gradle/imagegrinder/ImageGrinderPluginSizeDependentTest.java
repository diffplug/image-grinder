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


import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Test;

public class ImageGrinderPluginSizeDependentTest extends GradleHarness {
	@Test
	public void testUpToDate() throws Exception {
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
				"      if (img.size().width() <= 16) {",
				"        // hiDpi for small images",
				"        img.render('.png')",
				"        img.render('@2x.png', 2)",
				"      } else {",
				"        // but not for big images",
				"        img.render('.png')",
				"      }",
				"    }",
				"  }",
				"}");
		// this image is 16x16, so it should be rendered with HiDPI
		write("src/refresh.svg", readTestResource("refresh.svg"));
		runAndAssert(TaskOutcome.SUCCESS);
		runAndAssert(TaskOutcome.UP_TO_DATE);
		assertFolderContent("dst").containsExactly("refresh.png", "refresh@2x.png");

		// this image is large, so it should only have a 1x rendering
		write("src/refresh.svg", readTestResource("refresh-large.svg"));
		runAndAssert(TaskOutcome.SUCCESS);
		runAndAssert(TaskOutcome.UP_TO_DATE);
		assertFolderContent("dst").containsExactly("refresh.png");
	}
}
