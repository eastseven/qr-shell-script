#!/bin/bash

apk_file=quickride-car-release.apk
rm -rf *.apk

project_name=QuickRide-Car-Client
cd $project_name
ant profile-product main
var_date=`date "+%Y%m%d%H%M%S"`
apk_file_product=qr-car-$var_date-product.apk
cp target/$apk_file ../$apk_file_product

versionName=`grep versionName AndroidManifest.xml | grep -oE "[0-9]+\.[0-9]+\.[0-9]+"`;
versionCode=`grep versionCode AndroidManifest.xml | grep -oE "[0-9]+"`;

echo "insert into application values(seq_application.nextval, 'car-formal', "${versionCode}", '"${versionName}"', 1236.89, 2, 'Android', '2.2', 'com.quickride.car', '/apkfile/product/"${apk_file_product}"', empty_clob(), 8, 1, sysdate);" >> ../${apk_file_product}.sql;

scp -r ../${apk_file_product}.sql root@www.idingche.com.cn/home/oracle
scp -r ../${apk_file_product} root@www.idingche.com.cn:/var/www/html/apkfile/product
echo "done..."
