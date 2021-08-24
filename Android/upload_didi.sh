#!/bin/bash

./gradlew checkUploadConfig4Didi || ! echo "执行当前打包上传任务必须修改config.gradle配置中的archives_type = 1" || exit
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
echo "打包上传到滴滴内部仓库完成！！！"