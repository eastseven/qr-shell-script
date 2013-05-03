#!/bin/bash

apk_file=quickride-car-release.apk
rm -rf *.apk

project_name=QuickRide-Car-Client
cd $project_name

profile_name=$1
if [[ -z $profile_name ]]
then
    read -p 'Enter profile name[profile-test-chengdu / profile-test-chengdu-wan]: ' profile_name;
    if [[ -z $profile_name  ]]
    then
        profile_name=profile-test-chengdu
    fi
fi

ant $profile_name main
var_date=`date "+%Y%m%d%H%M%S"`
cp target/$apk_file ../qr-car-$var_date-101.apk
echo "done..."
