#!/bin/bash

# Do the Gradle build
./gradlew build --stacktrace || exit 1

if [ "$TRAVIS_REPO_SLUG" == "diffplug/image-grinder" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
	# Publish the artifacts
	./gradlew publish publishPlugins -Dgradle.publish.key=$gradle_key -Dgradle.publish.secret=$gradle_secret --stacktrace || exit 1
	# Push the javadoc
	./gradlew gitPublishPush --stacktrace || exit 1
fi
