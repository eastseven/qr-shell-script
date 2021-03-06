#!/bin/bash

apk_file=quickride-car-release.apk
rm -rf *.apk

version=$1
project_name=QuickRide-Car-Client
sh download-for-tags.sh $version

cd $project_name
ant profile-product main
var_date=`date "+%Y%m%d%H%M%S"`
apk_file_release=qr-car-$var_date-product.apk
cp target/$apk_file ../$apk_file_release
scp -r ../$apk_file_release root@www.idingche.com.cn:/var/www/html/apkfile/product
echo "done..."
echo 'generate '$apk_file_release >> /www/qr-android-log.txt
