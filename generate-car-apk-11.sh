#!/bin/bash

apk_file=quickride-car-release.apk
rm -rf *.apk

project_name=QuickRide-Car-Client
cd $project_name
sh refresh.sh

profile_name=profile-test-chengdu-11
ant $profile_name main
var_date=`date "+%Y%m%d%H%M%S"`
cp target/$apk_file ../qr-car-$var_date-11.apk
echo "done..."
