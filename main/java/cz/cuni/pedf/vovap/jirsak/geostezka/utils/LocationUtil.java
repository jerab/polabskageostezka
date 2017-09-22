package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.WelcomeActivity;

/**
 * Created by tomason on 22.09.2017.
 */

public class LocationUtil {
	private static final String LOG_TAG = "GEO - Location";
	private Context context;

	private LocationManager locman = null;
	private LocationListener loclisten;
	private LatLng ll;
	private boolean toShowDialog = true;

	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	public static final String INTENT_EXTRA_SHOW_DIALOG_NAME = "CheckLocationDialog";

	public LocationUtil(Context c) {
		this.context = c;
		Log.d(LOG_TAG, "Context: " + context.getClass().getName());
		ll = new LatLng(0,0);
		init();
	}

	public LocationUtil(Context c, boolean showProviderDialog) {
		this.toShowDialog = showProviderDialog;
		this.context = c;
		Log.d(LOG_TAG, "Context: " + context.getClass().getName());
		ll = new LatLng(0,0);
		init();
	}

	public void setLatLng(double la, double lo) {
		this.ll = new LatLng(la, lo);
	}

	public boolean jeNaPoziciGeostezky() {
		return Config.poziceGeostezky(getLocation());
	}

	public void checkLocationStatus() {
		Log.d(LOG_TAG, "hit Status");
		if(!locman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(LOG_TAG, "getLocation - not enableed provider");
			showProviderDialog();
		}else {
			locman.requestSingleUpdate(LocationManager.GPS_PROVIDER, loclisten, null);
		}
	}

	public LatLng getLocation() {
		Log.d(LOG_TAG, "getLocation: " + ll.toString());
		return ll;
	}

	public void setIfShowProviderDialog(boolean value) {
		this.toShowDialog = value;
	}

	private void init() {
		//context = this.getApplicationContext();
		if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
				PackageManager.PERMISSION_GRANTED &&
				ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
						PackageManager.PERMISSION_GRANTED) {
			//overeno
			locman = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
			loclisten = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					Log.d(LOG_TAG, "Location changed ");
					setLatLng(location.getLatitude(), location.getLongitude());
					if(!jeNaPoziciGeostezky() && toShowDialog) {
						showPositionResultDialog(false);
					}else if(context instanceof WelcomeActivity && toShowDialog) {
						showPositionResultDialog(true);
					}
					killLocationProcess();
					/*lat = location.getLatitude();
					lng = location.getLongitude();*/
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					Log.d(LOG_TAG, "Status changed " + status);
					if(provider == LocationManager.GPS_PROVIDER && status != LocationProvider.AVAILABLE) {
						setLatLng(0,0);
					}
				}

				@Override
				public void onProviderEnabled(String provider) {

				}

				@Override
				public void onProviderDisabled(String provider) {
					Log.d(LOG_TAG, "On provider disabled: " + provider + " |show dialog: " + toShowDialog);
					if(toShowDialog) {
						showProviderDialog();
					}
				}
			};
			locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loclisten);
		} else {
			ActivityCompat.requestPermissions((Activity)context, new String[] {
							Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION },
					MY_PERMISSIONS_REQUEST_LOCATION);
		}
	}

	public void showPositionResultDialog(boolean correct) {
		Log.d(LOG_TAG, "showing Result dialog..." + context.getClass().getName());
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		if(correct) {
			dialog.setMessage(R.string.location_result_message_ok);
			dialog.setPositiveButton (android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					// TODO Auto-generated method stub
					Intent myIntent = new Intent(context, DashboardActivity.class);
					context.startActivity(myIntent);
					((Activity)context).finish();
				}
			});
		}else {
			dialog.setMessage(R.string.location_result_message_false);
			dialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					if(!(context instanceof WelcomeActivity)) {
						showWelcomeScreen(context, false);
					}
				}
			});
		}
		dialog.show();
	}

	public void showProviderDialog() {
		Log.d(LOG_TAG, "showing Provider dialog..." + context.getPackageName() + " | " + context.getClass().getName());
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setMessage(R.string.start_location_service);
		dialog.setPositiveButton (R.string.got_to_settings, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				// TODO Auto-generated method stub
				Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(myIntent);
				//get gps
			}
		});
		dialog.setNegativeButton(R.string.closeDialog, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				if(!(context instanceof WelcomeActivity)) {
					showWelcomeScreen(context, false);
				}
			}
		});
		dialog.show();
	}

	public void killLocationProcess() {
		Log.d(LOG_TAG, "killing process..." + locman.toString());
		if(locman != null) {
			this.locman.removeUpdates(loclisten);
		}
	}

	public void showWelcomeScreen(Context c, boolean showDialog) {
		Intent welcome = new Intent(c, WelcomeActivity.class);
		welcome.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NEW_TASK);
		welcome.putExtra(INTENT_EXTRA_SHOW_DIALOG_NAME, showDialog);
		c.startActivity(welcome);
		((Activity)c).finish();
	}
}
