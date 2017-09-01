# <img align="left" src="images/image-grinder.png">ImageGrinder:<br>image manipulation for Gradle

<!---freshmark shields
output = [
	link(shield('Gradle plugin', 'plugins.gradle.org', 'com.diffplug.gradle.image-grinder', 'blue'), 'https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless'),
	link(shield('Maven central', 'mavencentral', 'com.diffplug.gradle:image-grinder', 'blue'), 'http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.diffplug.gradle%22%20AND%20a%3A%22image-grinder%22'),
	'',
	link(shield('Changelog', 'changelog', '{{version}}', 'brightgreen'), 'CHANGES.md'),
	link(shield('Javadoc', 'javadoc', '{{stable}}', 'brightgreen'), 'https://{{org}}.github.io/{{name}}/javadoc/{{stable}}/'),
	link(image('Travis CI', 'https://travis-ci.org/{{org}}/{{name}}.svg?branch=master'), 'https://travis-ci.org/{{org}}/{{name}}'),
	link(shield('Live chat', 'gitter', 'chat', 'brightgreen'), 'https://gitter.im/{{org}}/{{name}}'),
	link(shield('License Apache', 'license', 'apache', 'brightgreen'), 'https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)')
	].join('\n');
-->
[![Gradle plugin](https://img.shields.io/badge/plugins.gradle.org-com.diffplug.gradle.image--grinder-blue.svg)](https://plugins.gradle.org/plugin/com.diffplug.gradle.spotless)
[![Maven central](https://img.shields.io/badge/mavencentral-com.diffplug.gradle%3Aimage--grinder-blue.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.diffplug.gradle%22%20AND%20a%3A%22image-grinder%22)

[![Changelog](https://img.shields.io/badge/changelog-1.0.0--SNAPSHOT-brightgreen.svg)](CHANGES.md)
[![Javadoc](https://img.shields.io/badge/javadoc-unreleased-brightgreen.svg)](https://diffplug.github.io/image-grinder/javadoc/unreleased/)
[![Travis CI](https://travis-ci.org/diffplug/image-grinder.svg?branch=master)](https://travis-ci.org/diffplug/image-grinder)
[![Live chat](https://img.shields.io/badge/gitter-chat-brightgreen.svg)](https://gitter.im/diffplug/image-grinder)
[![License Apache](https://img.shields.io/badge/license-apache-brightgreen.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))
<!---freshmark /shields -->

<!---freshmark javadoc
output = prefixDelimiterReplace(input, 'https://{{org}}.github.io/{{name}}/javadoc/', '/', stable);
-->

## IDE-as-build-artifact.

It is possible to have many installations of the Eclipse IDE share a common set of installed artifacts, called a "bundlepool".  This means it is fast and efficient to get a purpose-built IDE for every project, preconfigured with all the plugins and settings appropriate for the project at hand.

When you run `gradlew ide`, it builds and downloads an IDE into `build/oomphIde` with just the features you need.  Takes ~15 seconds and 1MB of disk space once all the common artifacts have been cached at `~/.goomph`.

```groovy
imageGrinder {
svg {
	inputDir = file('src/main/svg')
	outputDir = file('src/main/resources/images')
	processor = EclipseSVG
}
}
```

<!---freshmark /javadoc -->

## Acknowledgements

* [Tony McCrary](https://github.com/enleeten) and [l33t labs](http://www.l33tlabs.com/) for their [org.eclipse.ui.images.renderer](https://github.com/tomsontom/org.eclipse.ui-split/tree/0402ebd10a57f9c2ca5cd2da3479470f98f70973/bundles/org.eclipse.ui.images.renderer) maven plugin.
* Maintained by [DiffPlug](https://www.diffplug.com/).
