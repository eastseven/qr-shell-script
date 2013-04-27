#!/bin/bash

apk_file=quickride-car-release.apk
rm -rf *.apk

project_name=QuickRide-Car-Client
cd $project_name
ant profile-test-yuqiman main
var_date=`date "+%Y%m%d%H%M%S"`
cp target/$apk_file ../qr-car-$var_date-101-yuqiman.apk
echo "done..."
