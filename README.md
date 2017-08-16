# Traffic-Rush
An Android app which gives us information about Traffic details.

The app starts with login or registering with a google account.
Registering is done by connecting the button with the Google signin web page.

Then it requests for destination address, it takes source address as your current location.
The destination address can be filled using the auto complete suggestions.
This is done by using Places AutoComplete API: 
https://developers.google.com/places/web-service/autocomplete

The longitude and latitude values of both the source(your current location) and the destination are known using the geocoding API: 
https://developers.google.com/maps/documentation/geocoding/intro

The Marker is used to check the point where the destination points to.

The directions to the destination is given with the help of the Directions API: 
https://developers.google.com/maps/documentation/directions/start

Then other details like traffic, presence of a police etc are known by connecting Traffic Rush to another app named waze using Waze Deep Links:
https://developers.google.com/waze/api/
