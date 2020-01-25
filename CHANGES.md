# ImageGrinder

## [Unreleased]
### Fixed
- Improved deprecation warning message.

## [2.1.0] - 2020-01-24
### Changed
- Plugin id is now `com.diffplug.image-grinder` ([reasoning](https://dev.to/nedtwigg/names-in-java-maven-and-gradle-2fm2#gradle-plugin-id)).
    - You can still use the legacy `com.diffplug.gradle.image-grinder` if you want, but you'll get a deprecation warning.
- Upgraded Batik from `1.11` to `1.12`.
- Upgraded build to [blowdryer](https://github.com/diffplug/blowdryer).

## [2.0.2] - 2019-08-19
- Now uses the new Worker API introduced in `5.6`, no longer uses the Worker API scheduled to be deprecated in `6.0` ([#2](https://github.com/diffplug/image-grinder/pull/2))
    - Bumped minimum required Gradle from `4.1` to `5.6`
    - Bumped all deps to latest, especially batik from `1.9.1` to `1.11`
- Now compatible with the gradle build cache, and uses the new incremental API ([#3](https://github.com/diffplug/image-grinder/pull/3))

## [2.0.1] - 2018-01-02
- Fixed a bug where image-grinder would barf on folders that got included in the input files.

## [2.0.0] - 2017-09-30
- If a task name starts with `process`, then it will be made into a requirement for the `processResources` task.
- Bump batik dependency from `1.7` to `1.9.1`.
- ~~Fix discrepancy in PNG render on mac vs win.~~ output is still a few bits different, but visually indistinguishable
- Fixes up-to-date checking for the case that [a changed file changes what the output files are](https://github.com/diffplug/image-grinder/commit/eac358437f29e4270a308c6a45f283e89be10395).

## [1.0.0] - 2017-09-22
- First stable release.
