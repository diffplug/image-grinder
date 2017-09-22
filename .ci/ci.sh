#!/bin/bash

# Do the Gradle build
./gradlew build || exit 1

if [ "$TRAVIS_REPO_SLUG" == "diffplug/image-gradle" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
	# Publish the artifacts
	./gradlew publish publishPlugins -Dgradle.publish.key=$gradle_key -Dgradle.publish.secret=$gradle_secret || exit 1
	# Push the javadoc
	./gradlew gitPublishPush || exit 1
fi
