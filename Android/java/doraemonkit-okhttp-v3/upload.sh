#!/usr/bin/env bash
echo -n  "please enter bintray userid ->"
read  userid_bintray
echo -n  "please enter bintray apikey ->"
read  apikey_bintray
../gradlew  clean build  --stacktrace --info   bintrayUpload -PbintrayUser=${userid_bintray} -PbintrayKey=${apikey_bintray} -PdryRun=false