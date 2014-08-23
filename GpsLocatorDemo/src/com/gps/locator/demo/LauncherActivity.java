package com.gps.locator.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @author dipenp
 *
 */
public class LauncherActivity extends Activity {
	
	private LinearLayout background;
	private Button getLocationBtn;
	private GpsLocator gpsLocator;
	public static final int GPS_SETTING_REQUEST_CODE = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);

		background = (LinearLayout)findViewById(R.id.parent_view);
		getLocationBtn = (Button)findViewById(R.id.get_location_btn);
		
		/*
		 * 
		 * Open dialog to get input value for Location update */
		getLocationUpdateDetailDialog(LauncherActivity.this).show();
		
		getLocationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				gpsLocator = new GpsLocator(LauncherActivity.this, background);
				
				/*
				 * 
				 * Checking whether location provider is enable or not*/
				if(gpsLocator.isLocationProviderEnable()){
					Location location = gpsLocator.getLocation();
					
					if(location != null){
						Toast.makeText(LauncherActivity.this, "Location Lat:: "+location.getLatitude()+" Long::: "+location.getLongitude() , Toast.LENGTH_LONG).show();
					}
				}else {
					/*
					 * Open setting to enable gps
					 */
					showSettingsAlert(LauncherActivity.this);
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case GPS_SETTING_REQUEST_CODE:
			Toast.makeText(LauncherActivity.this, "onActivityResult:::  "+resultCode , Toast.LENGTH_LONG).show();
			/*
			 *  Performing click to getLocation listner so if location is enable it will show current location 
			 */
			getLocationBtn.performClick();
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPause() {
		/*
		 * Stopping location listner
		 */
		if(null != gpsLocator){
			gpsLocator.stopLocationListner();
		}
		super.onPause();
	}
	
	/**
	 * @param context
	 * @return dialog to get input value for location update detail
	 */
	public Dialog getLocationUpdateDetailDialog(Context context){
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.update_input_dialog);
		dialog.setTitle("Location Update Detail");
		
		final EditText minTimeEdtTxt = (EditText)dialog.findViewById(R.id.minimum_time_edttxt);
		final EditText minDistEdtTxt = (EditText)dialog.findViewById(R.id.minimum_dist_edttxt);
		Button okButton = (Button)dialog.findViewById(R.id.okButton);
		
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String minTime = minTimeEdtTxt.getText().toString().trim();
				String minDist = minDistEdtTxt.getText().toString().trim();
				
				if(minTime.equals("")){
					minTimeEdtTxt.setError("Enter Value");
				}else if(minDist.equals("")){
					minDistEdtTxt.setError("Enter Value");
				}else{
					GpsLocator.MIN_TIME_BW_LOCATION_UPDATES = 1000 * 60 * Integer.parseInt(minTime);
					GpsLocator.MIN_DISTANCE_CHANGE_FOR_LOCATION_UPDATES = Integer.parseInt(minDist);
					dialog.cancel();
				}
			}
		});
		
		dialog.setCancelable(false);
		
		return dialog;
	}
	
	/**
	 * @param _context
	 * 
	 * Show dialog to open GPS setting 
	 */
	public void showSettingsAlert(final Context _context) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);

		alertDialog.setTitle("Enable GPS Settings");

		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						((Activity) _context).startActivityForResult(intent, GPS_SETTING_REQUEST_CODE);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialog.show();
	}
}
