# <img align="left" src="images/image-grinder.png">ImageGrinder:<br>image manipulation for Gradle

<!---freshmark shields
output = [
    link(shield('Gradle plugin', 'plugins.gradle.org', 'com.diffplug.image-grinder', 'blue'), 'https://plugins.gradle.org/plugin/com.diffplug.image-grinder'),
    link(shield('Maven central', 'mavencentral', 'available', 'blue'), 'https://search.maven.org/artifact/com.diffplug.gradle/image-grinder'),
    link(shield('Apache 2.0', 'license', 'apache-2.0', 'blue'), 'https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)'),
    '',
    link(shield('Changelog', 'changelog', versionLast, 'brightgreen'), 'CHANGELOG.md'),
    link(shield('Javadoc', 'javadoc', 'yes', 'brightgreen'), 'https://javadoc.io/doc/com.diffplug.gradle/image-grinder/{{versionLast}}/index.html'),
    link(shield('Live chat', 'gitter', 'chat', 'brightgreen'), 'https://gitter.im/diffplug/image-grinder'),
    link(image('CircleCI', 'https://circleci.com/gh/diffplug/image-grinder.svg?style=shield'), 'https://circleci.com/gh/diffplug/image-grinder')
    ].join('\n');
-->
[![Gradle plugin](https://img.shields.io/badge/plugins.gradle.org-com.diffplug.image--grinder-blue.svg)](https://plugins.gradle.org/plugin/com.diffplug.image-grinder)
[![Maven central](https://img.shields.io/badge/mavencentral-available-blue.svg)](https://search.maven.org/artifact/com.diffplug.gradle/image-grinder)
[![Apache 2.0](https://img.shields.io/badge/license-apache--2.0-blue.svg)](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))

[![Changelog](https://img.shields.io/badge/changelog-2.1.3-brightgreen.svg)](CHANGELOG.md)
[![Javadoc](https://img.shields.io/badge/javadoc-yes-brightgreen.svg)](https://javadoc.io/doc/com.diffplug.gradle/image-grinder/2.1.3/index.html)
[![Live chat](https://img.shields.io/badge/gitter-chat-brightgreen.svg)](https://gitter.im/diffplug/image-grinder)
[![CircleCI](https://circleci.com/gh/diffplug/image-grinder.svg?style=shield)](https://circleci.com/gh/diffplug/image-grinder)
<!---freshmark /shields -->

<!---freshmark javadoc
output = prefixDelimiterReplace(input, 'https://javadoc.io/static/com.diffplug.gradle/image-grinder/', '/', versionLast);
-->

## Simple image processing

To use it, just [add image-grinder to your buildscript](https://plugins.gradle.org/plugin/com.diffplug.image-grinder), and configure it as so:

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

Every single file in `srcDir` needs to be an image that ImageGrinder can parse.  Each image will be parsed, and wrapped into an [`Img`](https://javadoc.io/static/com.diffplug.gradle/image-grinder/2.1.3/com/diffplug/gradle/imagegrinder/Img.html). Call its methods to grind it into whatever you need in the `dstDir`.

ImageGrinder uses the gradle [Worker API](https://docs.gradle.org/5.6/userguide/custom_tasks.html#worker_api) introduced in Gradle 5.6 to use all your CPU cores for grinding.  It also uses gradle's [incremental task](https://docs.gradle.org/5.6/userguide/custom_tasks.html#incremental_tasks) support to do the minimum amount of grinding required.

## Configuration avoidance

The plugin creates tasks eagerly. If you wish to avoid this, you can rewrite the example above like this:

```gradle
def processEclipseSvg = tasks.register('processEclipseSvg', com.diffplug.gradle.imagegrinder.ImageGrinderTask) {
    srcDir = file('src')
    dstDir = file('dst')
    grinder { img ->
        img.render('.png')
        img.render('@2x.png', 2)
    }
    // used for up-to-date checking, bump this if the function above changes
    bumpThisNumberWhenTheGrinderChanges = 1
}
tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) {
  dependsOn processSvg
}
```

## Limitations

- ImageGrinder can only read SVG images.
- ImageGrinder can only write PNG images.
- ImageGrinder needs Gradle 5.6 or higher.

Not much of a grinder, but it does everything we needed.  If you need more, we're [happy to take PR's](CONTRIBUTING.md)!

<!---freshmark /javadoc -->

## Acknowledgements

* [Tony McCrary](https://github.com/enleeten) and [l33t labs](http://www.l33tlabs.com/) for their [org.eclipse.ui.images.renderer](https://github.com/tomsontom/org.eclipse.ui-split/tree/0402ebd10a57f9c2ca5cd2da3479470f98f70973/bundles/org.eclipse.ui.images.renderer) maven plugin.
* Maintained by [DiffPlug](https://www.diffplug.com/).
