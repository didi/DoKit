#!/bin/bash

#定义颜色的变量
RED_COLOR="\033[1;31m"  #红
GREEN_COLOR="\033[1;32m" #绿
YELOW_COLOR="\033[1;33m" #黄
BLUE_COLOR="\033[1;34m"  #蓝
PINK="\033[1;35m"    #粉红
RES="\033[0m"

./gradlew checkUploadConfig4Maven || ! echo -e  "${RED_COLOR}未通过打包的配置检测！！！ ${RES}" || exit
./gradlew copyPluginSource
./gradlew clean
#./gradlew assembleRelease
./gradlew :dokit-plugin:assemble --stacktrace
./gradlew :dokit:assembleRelease --stacktrace
./gradlew :dokit-no-op:assembleRelease --stacktrace
./gradlew :dokit-okhttp-api:assembleRelease --stacktrace
./gradlew :dokit-okhttp-v3:assembleRelease --stacktrace
./gradlew :dokit-okhttp-v4:assembleRelease --stacktrace
./gradlew :dokit-ft:assembleRelease --stacktrace
./gradlew :dokit-test:assembleRelease --stacktrace
./gradlew :dokit-autotest:assembleRelease --stacktrace
./gradlew :dokit-mc:assembleRelease --stacktrace
./gradlew :dokit-util:assembleRelease --stacktrace
./gradlew :dokit-weex:assembleRelease --stacktrace
./gradlew :dokit-pthread-hook:assembleRelease --stacktrace
./gradlew :dokit-gps-mock:assembleRelease --stacktrace
#publish
./gradlew :dokit-plugin:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-no-op:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-okhttp-api:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-okhttp-v3:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-okhttp-v4:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-ft:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-test:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-autotest:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-mc:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-weex:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-util:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-pthread-hook:publishReleasePublicationToMavenCentralRepository
./gradlew :dokit-gps-mock:publishReleasePublicationToMavenCentralRepository
echo -e  "${GREEN_COLOR} 打包上传到MavenCenter()仓库完成！！！ ${RES}"
