TRAFFIC RUSH:

Traffic Rush is an Android app for making traveling much easier by giving information about traffic details.

Copyright (c) 2017 Hemanth Lam, Nikhitha Durvasula

This project is licensed under the "MIT License". Please see the file LICENSE in this distribution for license terms https://github.com/NikhithaDurvasula/Traffic-Rush/blob/master/LICENSE

#PROJECT DESCRIPTION: An open source android application which helps people to know the traffic details from the current location to the desired destination address. This is all about knowing the occurrence of traffic incidents like accidents, traffic issues, cop traps, in your way to your destination.

This app allows users to register using email id and login. It provides users with mainly four options
1. Login or Register
2. Enter Destination Address
3. Get Directions to the destination
4. Get traffic information

You can find the source code at https://github.com/NikhithaDurvasula/Traffic-Rush

#INSTALLATION:
Firstly, clone the git repository.
git clone https://github.com/NikhithaDurvasula/Traffic-Rush

And then get all the packages required for the application by using the command.
You have to follow the instructions in the links provided below for all the APIs to generate your own keys. 

The app starts with login or registering with a google account. Registering is done by connecting the button with the Google sign-in web page.

Then it requests for destination address, it takes source address as your current location. The destination address can be filled using the auto complete suggestions. This is done by using Places AutoComplete API: https://developers.google.com/places/web-service/autocomplete

The longitude and latitude values of both the source(your current location) and the destination are known using the geocoding API: https://developers.google.com/maps/documentation/geocoding/intro

The Marker is used to check the point where the destination points to.

The directions to the destination is given with the help of the Directions API: https://developers.google.com/maps/documentation/directions/start

Then other details like traffic, presence of a police etc are known by connecting Traffic Rush to another app named waze using Waze Deep Links: https://developers.google.com/waze/api/

#TECHNICAL DETAILS:
Android Studio 2.3.3
JRE 1.8.0_112-release-b06 x86_64
JVM: OpenJDK 64-Bt Server VM by JetBrains s.r.o
compile sdk version 21

#SOURCE CODE: https://github.com/NikhithaDurvasula/Traffic-Rush

#.apk File You can get the apk file from : 

#Bug or Issue Tracker You can report the bugs in the following link: https://github.com/NikhithaDurvasula/Traffic-Rush/issues

#FUTURE ENHANCEMENTS:
1. Add maps functionality for taking source address from the user
2. Adding backend to store registration data and user history
3. To use Bing Maps Traffic Incidents API instead of wage API
4. To integrate this app with there vehicle related apps like uber, left etc, or other vehicles for communication.

#REFERENCES:
1. Android Studio - http://www.tutorialspoint.com/android/
2. https://developer.android.com/training/index.html
3. GoogleMaps: https://developers.google.com/maps/documentation/directions/intro ,
4. https://developers.google.com/maps/documentation/android/start#getting_the_google_maps_android_api_v2
5. Kinvey User guide : http://devcenter.kinvey.com/android/guides/users

#Final Presentation and screenshots of the app can be found at https://github.com/lamhemanth/Traffic-Rush/tree/master/docs

#Demo video https://drive.google.com/drive/folders/0ByTz45D8AFAua016N3FIU3FMYlE?usp=sharing

#Author Hemanth Lam & Nikhitha Durvasula

#Contact Information You can email to lamhem@pdx.edu / nd6@pdx.edu if you have any queries on this project
