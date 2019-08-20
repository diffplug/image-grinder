# ImageGrinder

### Version 2.1.0 - TBD ([javadoc](http://diffplug.github.io/image-grinder/javadoc/snapshot/), [snapshot]

### Version 2.0.2 - August 19th, 2019 ([javadoc](http://diffplug.github.io/image-grinder/javadoc/2.0.2/), [jcenter](https://bintray.com/diffplug/opensource/image-grinder/2.0.2/view))

- Now uses the new Worker API introduced in `5.6`, no longer uses the Worker API scheduled to be deprecated in `6.0` ([#2](https://github.com/diffplug/image-grinder/pull/2))
    - Bumped minimum required Gradle from `4.1` to `5.6`
    - Bumped all deps to latest, especially batik from `1.9.1` to `1.11`
- Now compatible with the gradle build cache, and uses the new incremental API ([#3](https://github.com/diffplug/image-grinder/pull/3))

### Version 2.0.1 - January 2, 2018 ([javadoc](http://diffplug.github.io/image-grinder/javadoc/2.0.1/), [jcenter](https://bintray.com/diffplug/opensource/image-grinder/2.0.1/view))

- Fixed a bug where image-grinder would barf on folders that got included in the input files.

### Version 2.0.0 - September 30, 2017 ([javadoc](http://diffplug.github.io/image-grinder/javadoc/2.0.0/), [jcenter](https://bintray.com/diffplug/opensource/image-grinder/2.0.0/view))

- If a task name starts with `process`, then it will be made into a requirement for the `processResources` task.
- Bump batik dependency from `1.7` to `1.9.1`.
- ~~Fix discrepancy in PNG render on mac vs win.~~ output is still a few bits different, but visually indistinguishable
- Fixes up-to-date checking for the case that [a changed file changes what the output files are](https://github.com/diffplug/image-grinder/commit/eac358437f29e4270a308c6a45f283e89be10395).

### Version 1.0.0 - September 22, 2017 ([javadoc](http://diffplug.github.io/image-grinder/javadoc/1.0.0/), [jcenter](https://bintray.com/diffplug/opensource/image-grinder/1.0.0/view))

- First stable release.
