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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.IterableAssert;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Test;

public class ImageGrinderPluginTest extends ResourceHarness {
	private GradleRunner gradleRunner() throws IOException {
		return GradleRunner.create().withProjectDir(rootFolder()).withPluginClasspath();
	}

	private void writeBuild() throws IOException {
		writeBuildWithBump(1);
	}

	private void writeBuildWithBump(Integer bump) throws IOException {
		String bumpLine = bump == null ? "" : "    bumpThisNumberWhenTheGrinderChanges = " + bump;
		write("build.gradle",
				"plugins {",
				"    id 'com.diffplug.gradle.image-grinder'",
				"}",
				"imageGrinder {",
				"  eclipseSvg {",
				"    srcDir = file('src')",
				"    dstDir = file('dst')",
				bumpLine,
				"    grinder { img ->",
				"        img.render('.png')",
				"        img.render('@2x.png', 2)",
				"    }",
				"  }",
				"}");
	}

	private IterableAssert<String> runAndAssert(TaskOutcome outcome) throws Exception {
		BuildResult result = gradleRunner().withArguments("eclipseSvg", "--stacktrace", "--info").build();
		assertThat(result.getTasks()).hasSize(1);
		assertThat(result.task(":eclipseSvg").getOutcome()).isEqualTo(outcome);

		String[] lines = result.getOutput().split("\n");
		Set<String> logged = new HashSet<>();
		for (String line : lines) {
			if (line.startsWith("outOfDate: ") || line.startsWith("removed: ")) {
				logged.add(line);
			}
		}
		return Assertions.assertThat(logged);
	}

	@Test
	public void testOnce() throws Exception {
		writeBuild();
		write("src/refresh.svg", readTestResource("refresh.svg"));
		runAndAssert(TaskOutcome.SUCCESS);
		assertFile("dst/refresh.png").hasBinaryContent(readTestResource("refresh16.png"));
		assertFile("dst/refresh@2x.png").hasBinaryContent(readTestResource("refresh32.png"));
	}

	@Test
	public void testUpToDate() throws Exception {
		writeBuild();
		write("src/refresh.svg", readTestResource("refresh.svg"));
		runAndAssert(TaskOutcome.SUCCESS);
		runAndAssert(TaskOutcome.UP_TO_DATE);

		// if we change the file, it runs again
		write("src/refresh.svg", readTestResource("diffpluglogo.svg"));
		runAndAssert(TaskOutcome.SUCCESS);
		runAndAssert(TaskOutcome.UP_TO_DATE);

		// if we don't have a bump line, it's never up-to-date
		writeBuildWithBump(null);
		runAndAssert(TaskOutcome.SUCCESS);
		runAndAssert(TaskOutcome.SUCCESS);

		// if we put the bump line back, it can become up-to-date again
		writeBuildWithBump(1);
		runAndAssert(TaskOutcome.SUCCESS);
		runAndAssert(TaskOutcome.UP_TO_DATE);

		// and if we bump the line, it has to run again, even if that's the only thing that changed
		writeBuildWithBump(2);
		runAndAssert(TaskOutcome.SUCCESS);
		runAndAssert(TaskOutcome.UP_TO_DATE);
	}

	@Test
	public void testIncremental() throws Exception {
		writeBuild();

		// one file
		write("src/refresh.svg", readTestResource("refresh.svg"));
		runAndAssert(TaskOutcome.SUCCESS).containsExactly("outOfDate: refresh.svg");
		assertFolderContent("dst").containsExactly("refresh.png", "refresh@2x.png");

		// add a file, and only it changes
		write("src/diffpluglogo.svg", readTestResource("diffpluglogo.svg"));
		runAndAssert(TaskOutcome.SUCCESS).containsExactly("outOfDate: diffpluglogo.svg");
		assertFolderContent("dst").containsExactly("diffpluglogo.png", "diffpluglogo@2x.png", "refresh.png", "refresh@2x.png");

		// remove a file, and only it is removed
		delete("src/refresh.svg");
		runAndAssert(TaskOutcome.SUCCESS).containsExactly("removed: refresh.svg");
		assertFolderContent("dst").containsExactly("diffpluglogo.png", "diffpluglogo@2x.png");

		// remove another file, and we end up with an empty directory
		delete("src/diffpluglogo.svg");
		runAndAssert(TaskOutcome.SUCCESS).containsExactly("removed: diffpluglogo.svg");
		assertFolderContent("dst").isEmpty();

		// add them both, and they're both rendered
		write("src/refresh.svg", readTestResource("refresh.svg"));
		write("src/diffpluglogo.svg", readTestResource("diffpluglogo.svg"));
		runAndAssert(TaskOutcome.SUCCESS).containsExactlyInAnyOrder("outOfDate: refresh.svg", "outOfDate: diffpluglogo.svg");
		assertFolderContent("dst").containsExactly("diffpluglogo.png", "diffpluglogo@2x.png", "refresh.png", "refresh@2x.png");
	}
}
