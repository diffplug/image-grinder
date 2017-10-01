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

[![Changelog](https://img.shields.io/badge/changelog-1.1.0--SNAPSHOT-brightgreen.svg)](CHANGES.md)
[![Javadoc](https://img.shields.io/badge/javadoc-1.0.0-brightgreen.svg)](https://diffplug.github.io/image-grinder/javadoc/1.0.0/)
[![Travis CI](https://travis-ci.org/diffplug/image-grinder.svg?branch=master)](https://travis-ci.org/diffplug/image-grinder)
[![Live chat](https://img.shields.io/badge/gitter-chat-brightgreen.svg)](https://gitter.im/diffplug/image-grinder)
[![License Apache](https://img.shields.io/badge/license-apache-brightgreen.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))
<!---freshmark /shields -->

<!---freshmark javadoc
output = prefixDelimiterReplace(input, 'https://{{org}}.github.io/{{name}}/javadoc/', '/', stable);
-->

## Simple image processing

To use it, just [add image-grinder to your buildscript](https://plugins.gradle.org/plugin/com.diffplug.gradle.image-grinder), and configure it as so:

```groovy
imageGrinder {
	// creates a task called 'processEclipseSvg', you can name it whatever you want
	// if the name starts with 'process', then the 'processResources' task will depend on it
	processEclipseSvg {
		srcDir = file('src')
		dstDir = file('dst')
		grinder { img ->
			img.render('.png')
			img.render('@2x.png', 2)
		}
		// used for up-to-date checking, bump this if the function above changes
		bumpThisNumberWhenTheGrinderChanges = 1
	}
}
```

Every single file in `srcDir` needs to be an image that ImageGrinder can parse.  Each image will be parsed, and wrapped into an [`Img`](https://diffplug.github.io/goomph/javadoc/1.0.0/com/diffplug/gradle/imagegrinder/Img.html). Call its methods to grind it into whatever you need in the `dstDir`.

ImageGrinder uses the gradle [Worker API](https://docs.gradle.org/4.1/userguide/custom_tasks.html#worker_api) introduced in Gradle 4.1 to use all your CPU cores for grinding.  It also uses gradle's [incremental task](https://docs.gradle.org/4.1/userguide/custom_tasks.html#incremental_tasks) support to do the minimum amount of grinding required.

## Limitations

- ImageGrinder can only read SVG images.
- ImageGrinder can only write PNG images.
- ImageGrinder needs Gradle 4.1 or higher.

Not much of a grinder, but it does everything we needed.  If you need more, we're [happy to take PR's](CONTRIBUTING.md)!

<!---freshmark /javadoc -->

## Acknowledgements

* [Tony McCrary](https://github.com/enleeten) and [l33t labs](http://www.l33tlabs.com/) for their [org.eclipse.ui.images.renderer](https://github.com/tomsontom/org.eclipse.ui-split/tree/0402ebd10a57f9c2ca5cd2da3479470f98f70973/bundles/org.eclipse.ui.images.renderer) maven plugin.
* Maintained by [DiffPlug](https://www.diffplug.com/).
