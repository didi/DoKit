#!/bin/bash

./gradlew checkUploadConfig4Maven || ! echo "执行当前打包上传任务必须修改config.gradle配置中的archives_type = 2" || exit
./gradlew copyPluginSource
./gradlew clean
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
echo "打包上传到MavenCenter()仓库完成！！！"