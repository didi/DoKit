#!/bin/bash

./gradlew copyPluginSource
./gradlew :dokit-plugin:assemble
./gradlew assembleRelease
./gradlew :dokit:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-ft:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-mc:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-no-op:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-okhttp-api:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-okhttp-v3:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-okhttp-v4:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-plugin:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-rpc:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-rpc-mc:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-util:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-weex:publishReleasePublicationToMavenCentralRepository