# ImageGrinder

### Version 1.1.0 - TBD ([javadoc](http://diffplug.github.io/image-grinder/javadoc/snapshot/), [snapshot](https://oss.sonatype.org/content/repositories/snapshots/com/diffplug/gradle/image-grinder/))

- If a task name starts with `process`, then it will be made into a requirement for the `processResources` task.
- Bump batik dependency from `1.7` to `1.9.1`.
- Fix discrepancy in PNG render on mac vs win.
- Fixes up-to-date checking for the case that [a changed file changes what the output files are](https://github.com/diffplug/image-grinder/commit/eac358437f29e4270a308c6a45f283e89be10395).

### Version 1.0.0 - September 22, 2017 ([javadoc](http://diffplug.github.io/image-grinder/javadoc/1.0.0/), [jcenter](https://bintray.com/diffplug/opensource/image-grinder/1.0.0/view))

- First stable release.
