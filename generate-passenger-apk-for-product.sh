#!/bin/bash

apk_file=quickride-passenger-release.apk
rm -rf *-passenger-*.apk

project_name=QuickRide-Passenger-Client
cd $project_name
ant profile-product main
var_date=`date "+%Y%m%d%H%M%S"`
cp target/$apk_file ../qr-passenger-$var_date-product.apk
echo "done..."
