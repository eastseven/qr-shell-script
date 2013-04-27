#!/bin/bash

apk_file=quickride-car-release.apk
rm -rf *.apk

project_name=QuickRide-Car-Client
cd $project_name
ant profile-test-shanghai main
var_date=`date "+%Y%m%d%H%M%S"`
cp target/$apk_file ../qr-car-$var_date-test.apk
scp -r ../qr-car-$var_date-test.apk root@www.idingche.com.cn:/var/www/html/apkfile/test
echo "done..."
