package com.gps.locator.demo;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author dipenp
 *
 */
public class GpsLocator implements LocationListener{

	/* Android Document Says:  The minimum time interval for notifications, in milliseconds. 
	 * This field is only used as a hint to conserve power, 
	 * and actual time between location updates may be greater or lesser than this value.*/
	public static long MIN_TIME_BW_LOCATION_UPDATES = 0; //Minimum time to update location
	
	/*
	 * the minimum distance interval for notifications, in meters*/
	public static float MIN_DISTANCE_CHANGE_FOR_LOCATION_UPDATES = 0; //Minimum distance to update location
	
	private LocationManager locationManager;
	private Location location;
	private Location currentlyUsedLocation;
	
	private Context _context;
	private LinearLayout backgroundView;
	
	private boolean isLocationProviderEnable;
	/*
	 * Color array to change background when location change*/
	int[] colorArray = { Color.BLUE, Color.RED, Color.CYAN, Color.DKGRAY, Color.GREEN, Color.MAGENTA };

	
	static int i = 0;
	
	/**
	 * @param _context
	 * @param background
	 */
	public GpsLocator(Context _context, LinearLayout background){
		this._context = _context;
		this.backgroundView = background;
		
		locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
		
		boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		/*
		 * Checking if location provider is enable or not 
		 */
		if(isGPSEnabled || isNetworkEnabled){
			isLocationProviderEnable = true;
		}
		
		if (isNetworkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER,
					MIN_TIME_BW_LOCATION_UPDATES,
					MIN_DISTANCE_CHANGE_FOR_LOCATION_UPDATES, 
					this);

			/*
			 * Initializing location with NETWORK_PROVIDER to find current location
			 */
			if (locationManager != null) {
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
		}
		
		if (isGPSEnabled) {
			/*
			 * if location is not initialized with NETWORK_PROVIDER. We will try to initialize with GPS_PROVIDER 
			 */
			if (location == null) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER,
						MIN_TIME_BW_LOCATION_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_LOCATION_UPDATES, 
						this);
				
				/*
				 * Initializing location with GPS_PROVIDER to find current location
				 */
				if (locationManager != null) {
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
			}
		}
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		
		/*
		 * Checking if new location is better than previous location.
		 * This method can be helpful when we have to do some map update or anything related to location.
		 * Currently we are just showing current location so it is not helpful as it can be when doing some heavy operation on location change.   
		 */
		if(isBetterLocation(location, currentlyUsedLocation)){
			currentlyUsedLocation = location;
			Toast.makeText(_context, "Location Changed::: isBetterLocation:::  TRUE"  , Toast.LENGTH_LONG).show();
		}else {
			Toast.makeText(_context, "Location Changed::: isBetterLocation::: FALSE" , Toast.LENGTH_LONG).show();
		}
		
		/*
		 * Changing background color when user's location changed
		 */
		backgroundView.setBackgroundColor(colorArray[i]);
		
		/*
		 * Incrementing constant value to get new color for background next time
		 */
		if((colorArray.length-1) == i){
			i = 0;
		}else{
			i = i+1;	
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Toast.makeText(_context, "onStatusChanged:::"+status , Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(_context, "onProviderEnabled::: ON :::"+provider  , Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(_context, "onProviderDisabled::: OFF :::"+provider , Toast.LENGTH_LONG).show();
	}
	
	public boolean isLocationProviderEnable() {
		return isLocationProviderEnable;
	}

	public void setLocationProviderEnable(boolean isLocationProviderEnable) {
		this.isLocationProviderEnable = isLocationProviderEnable;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	public void stopLocationListner(){
		if(null != locationManager){
			locationManager.removeUpdates(this);
		}	
	}
	
	
	/**********************************************************************************************************************************
	 * Android has provide demo code to check whether current new location is better than previous one or not.
	 * (Reference: http://developer.android.com/guide/topics/location/strategies.html)
	 * 
	 * 
	 **********************************************************************************************************************************/
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
