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

public class GradleHarness extends ResourceHarness {
	/** A gradleRunner(). */
	protected GradleRunner gradleRunner() throws IOException {
		return GradleRunner.create().withProjectDir(rootFolder()).withPluginClasspath();
	}

	/** Runs the eclipseSvg task and asserts that the given result is applied to the given files. */
	protected IterableAssert<String> runAndAssert(TaskOutcome outcome) throws Exception {
		BuildResult result = gradleRunner().withArguments("eclipseSvg", "--stacktrace", "--info").build();
		assertThat(result.getTasks()).hasSize(1);
		assertThat(result.task(":eclipseSvg").getOutcome()).isEqualTo(outcome);

		String[] lines = result.getOutput().split("\n");
		Set<String> logged = new HashSet<>();
		for (String line : lines) {
			line = line.replace("\r", "");
			if (line.startsWith("render: ") || line.startsWith("clean: ")) {
				logged.add(line);
			}
		}
		return Assertions.assertThat(logged);
	}
}
