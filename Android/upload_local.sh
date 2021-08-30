#!/bin/bash

./gradlew checkUploadConfig4Local || ! echo "执行当前打包上传任务必须修改config.gradle配置中的archives_type = 0" || exit
./gradlew copyPluginSource
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
echo "本地打包完成！！！"