#!/bin/bash

#定义颜色的变量
RED_COLOR="\033[1;31m"  #红
GREEN_COLOR="\033[1;32m" #绿
YELOW_COLOR="\033[1;33m" #黄
BLUE_COLOR="\033[1;34m"  #蓝
PINK="\033[1;35m"    #粉红
RES="\033[0m"

./gradlew checkUploadConfig4Maven || ! echo -e  "${RED_COLOR}执行当前打包上传任务必须修改config.gradle配置中的archives_type = 2 ${RES}" || exit
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
./gradlew :dokit-amap-api:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-amap-no-privacy:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-amap-with-privacy:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-plugin:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-rpc:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-rpc-mc:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-util:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-weex:publishReleasePublicationToMavenCentralRepository
echo -e  "${GREEN_COLOR} 打包上传到MavenCenter()仓库完成！！！ ${RES}"
