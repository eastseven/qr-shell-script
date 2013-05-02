#!/bin/bash

apk_file=quickride-car-release.apk
rm -rf *.apk

version=$1
sh download-for-tags.sh $version
project_name=QuickRide-Car-Client
cd $project_name
ant profile-test-shanghai main
var_date=`date "+%Y%m%d%H%M%S"`
apk_file_release=qr-car-$var_date-test.apk
cp target/$apk_file ../$apk_file_release
scp -r ../$apk_file_release root@www.idingche.com.cn:/var/www/html/apkfile/test
echo "done..."
