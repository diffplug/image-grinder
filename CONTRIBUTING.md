# Contributing to Goomph

Pull requests are welcome!  In order to get merged, you'll need to:

- [ ] have tests
- [ ] update the changelog

Once you meet those requirements, we'll do our best to merge and release your stuff within 24 hrs.

Every successful Travis CI build on branch `master` is automatically published to [`https://oss.sonatype.org/content/repositories/snapshots`](https://oss.sonatype.org/content/repositories/snapshots/com/diffplug/), and its javadoc are published [here](http://diffplug.github.io/image-grinder/javadoc/snapshot/).

## Build instructions

It's a bog-standard gradle build.

`gradlew eclipse`
* creates an Eclipse project file for you.

`gradlew build`
* builds the jar
* runs FindBugs
* checks the formatting
* runs the tests

If you're getting style warnings, `gradlew spotlessApply` will apply anything necessary to fix formatting. For more info on the formatter, check out [spotless](https://github.com/diffplug/spotless).

## Test locally

To make changes to ImageGrinder and test those changes on a local project, add the following to the top of your local project's `build.gradle` (the project you want to use ImageGrinder on, not ImageGrinder itself):

```groovy
buildscript {
	repositories {
		mavenLocal()
		jcenter()
		configurations.all {
			resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
		}
	}

	dependencies {
		classpath 'com.diffplug.gradle:image-grinder:+'
	}
}

```

To test your changes, run `gradlew publishToMavenLocal` on your ImageGrinder project.  Now you can make changes and test them on your project.

## License

By contributing your code, you agree to license your contribution under the terms of the APLv2: https://github.com/diffplug/image-grinder/blob/master/LICENSE

All files are released with the Apache 2.0 license as such:

```
Copyright 2017 DiffPlug

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
