#!/bin/sh

project_name=QuickRide-Car-Client
svn_username=$1
svn_password=$2

rm -rf $project_name
svn --username $svn_username --password $svn_password checkout svn://192.168.1.101/quickride/trunck/$project_name
echo "sdk.dir=$ANDROID_HOME" > $project_name/local.properties
echo "target=android-8" >> $project_name/project.properties
echo "android.library.reference.1=../QuickRide-Android-Core" >> $project_name/project.properties

project_name=QuickRide-Android-Core
rm -rf $project_name
svn --username $svn_username --password $svn_password checkout svn://192.168.1.101/quickride/trunck/$project_name
echo "sdk.dir=$ANDROID_HOME" > $project_name/local.properties

project_name=QuickRide-Passenger-Client
rm -rf $project_name
svn --username $svn_username --password $svn_password checkout svn://192.168.1.101/quickride/trunck/$project_name
echo "sdk.dir=$ANDROID_HOME" > $project_name/local.properties
