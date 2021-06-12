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


import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskProvider;

/** See [README.md](https://github.com/diffplug/image-grinder) for usage instructions. */
public class ImageGrinderPlugin implements Plugin<Project> {
	private static final String NAME = "imageGrinder";

	@Override
	public void apply(Project project) {
		LegacyPlugin.applyForCompat(project, Legacy.class);
		project.getExtensions().add(NAME, project.container(ImageGrinderTaskApi.class, new NamedDomainObjectFactory<ImageGrinderTaskApi>() {
			@Override
			public ImageGrinderTaskApi create(String name) {
				ImageGrinderTaskApi.
				project.getTasks().register(name, ImageGrinderTask.class, task -> {
					
				});
				ImageGrinderTask task = project.getTasks().create(name, ImageGrinderTask.class);
				if (name.startsWith("process")) {
					project.getTasks().named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, processResources -> {
						processResources.dependsOn(provider);
					});
				}
				return task;
			}
		}));
	}

	/** The legacy `com.diffplug.gradle.image-grinder`, does exactly the same thing as `com.diffplug.image-grinder`. */
	public static class Legacy extends LegacyPlugin {
		public Legacy() {
			super(ImageGrinderPlugin.class, "com.diffplug.image-grinder");
		}
	}
}
