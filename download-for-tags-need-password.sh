#!/bin/sh

project_name_core=QuickRide-Android-Core
project_name_car=QuickRide-Car-Client
project_name_passenger=QuickRide-Passenger-Client
svn_username=$1
svn_password=$2
svn_ip=127.0.0.1

if [[ -z $svn_username ]]
then
    read -p 'Enter username: ' svn_username;
fi

if [[ -z $svn_password ]]
then
    read -p 'Enter password: ' svn_password;
fi

version_core=$3
version_car=$4
version_passenger=$5
if [[ -z $version_core ]]
then
    read -p 'Enter version of QuickRide-Android-Core: ' version_core;
fi

if [[ -z $version_car ]]
then
    read -p 'Enter version of QuickRide-Android-Car: ' version_car;
fi

if [[ -z $version_passenger ]]
then
    read -p 'Enter version of QuickRide-Android-Passenger: ' version_passenger;
fi

echo 'core version: ' $version_core;
echo 'car version: ' $version_car;
echo 'passenger version: ' $version_passenger;

rm -rf $project_name_core
svn --username $svn_username --password $svn_password checkout svn://$svn_ip/quickride/tags/$project_name_core-$version_core $project_name_core
android update project -p $project_name_core -t 10

rm -rf $project_name_car
svn --username $svn_username --password $svn_password checkout svn://$svn_ip/quickride/tags/$project_name_car-$version_car $project_name_car
android update project -p $project_name_car -t 10

rm -rf $project_name_passenger
svn --username $svn_username --password $svn_password checkout svn://$svn_ip/quickride/tags/$project_name_passenger-$version_passenger $project_name_passenger
android update project -p $project_name_passenger -t 10

echo `date` ' : download QuickRide-Android-Core version '${version_core}', QuickRide-Android-Car version '${version_car} ', QuickRide-Android-Passenger version ' ${version_passenger} >> /www/qr-android-log.txt;
