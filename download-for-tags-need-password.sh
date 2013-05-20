#!/bin/sh

project_name=QuickRide-Car-Client
svn_username=$3
svn_password=$4
svn_ip=127.0.0.1

version_core=$1
version_car=$2

if [[ -z $version_core ]]
then
    read -p 'Enter version of QuickRide-Android-Core: ' version_core;
fi

if [[ -z $version_car ]]
then
    read -p 'Enter version of QuickRide-Android-Car: ' version_car;
fi

echo 'core version: ' $version_core;
echo 'car version: ' $version_car;

rm -rf $project_name
svn --username $svn_username --password $svn_password checkout svn://$svn_ip/quickride/tags/$project_name-$version_car $project_name
echo "sdk.dir=$ANDROID_HOME" > $project_name/local.properties

project_name=QuickRide-Android-Core
rm -rf $project_name
svn --username $svn_username --password $svn_password checkout svn://$svn_ip/quickride/tags/$project_name-$version_core $project_name
echo "sdk.dir=$ANDROID_HOME" > $project_name/local.properties

#project_name=QuickRide-Passenger-Client
#rm -rf $project_name
#svn --username $svn_username --password $svn_password checkout svn://192.168.1.101/quickride/trunck/$project_name
#echo "sdk.dir=$ANDROID_HOME" > $project_name/local.properties

echo `date` ' : download QuickRide-Android-Core version '${version_core}', QuickRide-Android-Car version '${version_car} >> /www/qr-android-log.txt;
