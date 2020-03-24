#!/usr/bin/env bash
# script
echo -n  "please enter bintray userid ->"
read  userid_bintray
echo -n  "please enter bintray apikey ->"
read  apikey_bintray
../gradlew clean build bintrayUpload -PbintrayUser=${userid_bintray} -PbintrayKey=${apikey_bintray} -PdryRun=false