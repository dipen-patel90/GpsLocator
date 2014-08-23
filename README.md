GpsLocator
==========

Gps Locator Demo App

Android: Location Listener app
In this demo we are going to fetch device current location using:

@ LocationListener
@ NETWORK_PROVIDER or GPS_PROVIDER 

First of all we have to set "uses-permission" in manifest:

As we are using both (NETWORK_PROVIDER  and GPS_PROVIDER ) we have to set ACCESS_FINE_LOCATION permission in manifest.
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />

we will take user's input to update location , 
Here user can set how frequently user want to update its location.


According to Android documentation:
The minimum time interval for notifications, This field is only used as a hint to conserve power, 
and actual time between location updates may be greater or lesser than this value.

Now when user tries to get its current location, we have to check if location provider is enable or not.
If location provider is disable we can give user option to enable "Access to my location" from settings.

When user will enable "Access to my location" setting user will get back to our activity 
and user will see its current location, 
we are also changing background color of screen when "onLocationChanged" called.
