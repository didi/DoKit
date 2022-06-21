#!/bin/bash

#定义颜色的变量
RED_COLOR="\033[1;31m"  #红
GREEN_COLOR="\033[1;32m" #绿
YELOW_COLOR="\033[1;33m" #黄
BLUE_COLOR="\033[1;34m"  #蓝
PINK="\033[1;35m"    #粉红
RES="\033[0m"

#./gradlew checkUploadConfig4Didi || ! echo -e "${RED_COLOR}未通过打包的配置检测！！！ ${RES}" || exit
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
./gradlew :dokit-gps-mock:assembleRelease --stacktrace
./gradlew :dokit-pthread-hook:assembleRelease --stacktrace
#publish
./gradlew :dokit-plugin:publish
./gradlew :dokit:publish
./gradlew :dokit-no-op:publish
./gradlew :dokit-okhttp-api:publish
./gradlew :dokit-okhttp-v3:publish
./gradlew :dokit-okhttp-v4:publish
./gradlew :dokit-ft:publish
./gradlew :dokit-test:publish
./gradlew :dokit-autotest:publish
./gradlew :dokit-mc:publish
./gradlew :dokit-util:publish
./gradlew :dokit-weex:publish
./gradlew :dokit-pthread-hook:publish
./gradlew :dokit-gps-mock:publish
echo -e  "${GREEN_COLOR} 打包上传到滴滴内部仓库完成！！！${RES}"
