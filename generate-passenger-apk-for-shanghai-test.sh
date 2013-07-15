#!/bin/bash

apk_file=quickride-passenger-release.apk
rm -rf *-passenger-*.apk

project_name=QuickRide-Passenger-Client
cd $project_name
sh refresh.sh
ant profile-test-shanghai main
var_date=`date "+%Y%m%d%H%M%S"`
cp target/$apk_file ../qr-passenger-$var_date-test.apk
echo "done..."
