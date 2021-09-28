#!/bin/bash

#定义颜色的变量
RED_COLOR="\033[1;31m"  #红
GREEN_COLOR="\033[1;32m" #绿
YELOW_COLOR="\033[1;33m" #黄
BLUE_COLOR="\033[1;34m"  #蓝
PINK="\033[1;35m"    #粉红
RES="\033[0m"

./gradlew checkUploadConfig4Didi || ! echo -e "${RED_COLOR}执行当前打包上传任务必须修改config.gradle配置中的archives_type = 1 ${RES}" || exit
./gradlew copyPluginSource
./gradlew clean
./gradlew :dokit-plugin:assemble
./gradlew assembleRelease
./gradlew :dokit:publish
./gradlew :dokit-ft:publish
./gradlew :dokit-mc:publish
./gradlew :dokit-no-op:publish
./gradlew :dokit-okhttp-api:publish
./gradlew :dokit-okhttp-v3:publish
./gradlew :dokit-okhttp-v4:publish
./gradlew :dokit-plugin:publish
./gradlew :dokit-rpc:publish
./gradlew :dokit-rpc-mc:publish
./gradlew :dokit-util:publish
./gradlew :dokit-weex:publish
./gradlew :dokit-dmap:publish
echo -e  "${GREEN_COLOR} 打包上传到滴滴内部仓库完成！！！${RES}"