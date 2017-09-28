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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

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
	private Location myLocation = null;
	private boolean toShowDialog = true;

	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
	public static final String INTENT_EXTRA_SHOW_DIALOG_NAME = "CheckLocationDialog";
	/**
	 * 1 hour
	 */
	private static final double LOCATION_VALID_TIME_MILISEC_INTERVAL = 1000 * 60 * 60 * 2;

	public LocationUtil(Context c) {
		this.context = c;
		Log.d(LOG_TAG, "Context: " + context.getClass().getName());
		//setLatLng(0, 0);
		init();
	}

	public LocationUtil(Context c, boolean showProviderDialog) {
		this.toShowDialog = showProviderDialog;
		this.context = c;
		Log.d(LOG_TAG, "Context: " + context.getClass().getName());
		//setMyLocation(new Location());
		init();
	}


	/*
		public void setLatLng(double la, double lo) {
			Log.d(LOG_TAG, "Setting LAT LONG: " + la + " | " + lo);
			this.ll = new LatLng(la, lo);
		}
	*/
	@Nullable
	public Location getLocation() {
		Log.d(LOG_TAG, "getLocation myLocation: " + myLocation);
		return myLocation;
	}

	private void setMyLocation(Location l) {
		Log.d(LOG_TAG, "Setting my location: " + l.getLatitude() + " | " + l.getLongitude());
		this.myLocation = l;
	}

	public boolean jeNaPoziciGeostezky() {
		Log.d(LOG_TAG, "kontroluji pozici...");
		if (this.getLocation() == null) {
			return false;
		} else {
			return Config.poziceGeostezky(new LatLng(getLocation().getLatitude(), getLocation().getLongitude()));
		}
	}

	private boolean isLocationOld(Location l) {
		Log.d(LOG_TAG, "Checking time: " + l.getTime() + " | " + System.currentTimeMillis());
		Date t = new Date(l.getTime());
		Log.d(LOG_TAG, "Last location time: " + t.toString());

		Log.d(LOG_TAG, "Checking time: " + (System.currentTimeMillis() - l.getTime()));
		return (l.getTime() < System.currentTimeMillis() - LOCATION_VALID_TIME_MILISEC_INTERVAL);
	}

	public void checkLocationStatusByUser() {
		Log.d(LOG_TAG, "HIT Status");
		if (locman == null || !locman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(LOG_TAG, "getLocation - not enableed provider or is null");
			showProviderDialog();
		} else {
			Toast.makeText(context, "Probíhá ověřování lokace ...", Toast.LENGTH_LONG).show();
			locman.requestSingleUpdate(LocationManager.GPS_PROVIDER, loclisten, null);
			Log.d(LOG_TAG, "waiting ...");
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					setMyLocation(locman.getLastKnownLocation(LocationManager.GPS_PROVIDER));
					Log.d(LOG_TAG, "last known: " + myLocation);
					Log.d(LOG_TAG, "is MOCK: " + isLocationFromMock(myLocation));
					// neni znama pozice
					if (myLocation == null || isLocationOld(myLocation) || isLocationFromMock(myLocation)) {
						showPositionResultDialog(false);
					}
				}
			}, 2000);
		}
	}

	public void checkLocationStatus() {
		Log.d(LOG_TAG, "Cecking Status");
		if (locman == null || !locman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(LOG_TAG, "getLocation - not enableed provider or is null");
			showProviderDialog();
		} else {
			locman.requestSingleUpdate(LocationManager.GPS_PROVIDER, loclisten, null);

			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					setMyLocation(locman.getLastKnownLocation(LocationManager.GPS_PROVIDER));
					Log.d(LOG_TAG, "last known: " + myLocation);
					Log.d(LOG_TAG, "is MOCK: " + isLocationFromMock(myLocation));
					// neni znama pozice
					if (myLocation == null || isLocationOld(myLocation) || isLocationFromMock(myLocation)) {
						showPositionResultDialog(false);
					}
				}
			}, 2000);
		}
	}

	private boolean isLocationFromMock(Location loc) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			return loc.isFromMockProvider();
		} else {
			/// we don't check for older versions
			/// is MOCK settings ON
			return false;
		}
	}

	public void setIfShowProviderDialog(boolean value) {
		this.toShowDialog = value;
	}

	private boolean checkSelfPermission() {
		return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
				&&
				ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
	}

	private void init() {
		if (this.checkSelfPermission()) {
			//overeno
			locman = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
			Log.d(LOG_TAG, "Locman is null: " + locman.toString() + " - " + String.valueOf(locman == null));
			Log.d(LOG_TAG, "Setting new location listener |show dialog: " + toShowDialog);
			loclisten = new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					Log.d(LOG_TAG, "Location changed ");
					setMyLocation(location);
					if (context instanceof WelcomeActivity && toShowDialog) {
						((WelcomeActivity) context).showProgressBar(false);
					}
					if (!jeNaPoziciGeostezky() && toShowDialog) {
						showPositionResultDialog(false);
					} else if (context instanceof WelcomeActivity && toShowDialog) {
						showPositionResultDialog(true);
					}
					killLocationProcess();
				}

				@Override
				public void onStatusChanged(String s, int i, Bundle bundle) {
					Log.d(LOG_TAG, "Status changed " + s);
					if (s == LocationManager.GPS_PROVIDER && i != LocationProvider.AVAILABLE) {
						setMyLocation(null);
					}
				}

				@Override
				public void onProviderEnabled(String s) {
					Log.d(LOG_TAG, "On provider enabled: " + s + " |show dialog: " + toShowDialog);
				}

				@Override
				public void onProviderDisabled(String s) {
					Log.d(LOG_TAG, "On provider disabled: " + s + " |show dialog: " + toShowDialog);
					if (toShowDialog) {
						showProviderDialog();
					}
				}
			};
			locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, loclisten);
		} else {
			ActivityCompat.requestPermissions((Activity) context, new String[]{
							Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION},
					MY_PERMISSIONS_REQUEST_LOCATION);
		}
	}

	public void showPositionResultDialog(boolean correct) {
		Log.d(LOG_TAG, "showing Result dialog..." + context.getClass().getName());
		if(context instanceof WelcomeActivity) {
			((WelcomeActivity)context).showProgressBar(false);
		}
		/// Neni nastaveno ignorovani pozice ///
		if(!Config.isPositionCheckOn(context)) {
			final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			if (correct) {
				dialog.setMessage(R.string.location_result_message_ok);
				dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						// TODO Auto-generated method stub
						Intent myIntent = new Intent(context, DashboardActivity.class);
						context.startActivity(myIntent);
						((Activity) context).finish();
					}
				});
			} else {
				dialog.setMessage(R.string.location_result_message_false);
				dialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface, int paramInt) {
						if (!(context instanceof WelcomeActivity)) {
							showWelcomeScreen(context, false);
						}
					}


				});
			}
			dialog.show();
		}
	}

	public boolean showPositionResultDialogReturn(boolean correct) {
		showPositionResultDialog(correct);
		return correct;
	}

	public void showProviderDialog() {
		Log.d(LOG_TAG, "showing Provider dialog..." + context.getPackageName() + " | " + context.getClass().getName());
		if(context instanceof WelcomeActivity) {
			((WelcomeActivity)context).showProgressBar(false);
		}
		/// Neni nastaveno ignorovani pozice ///
		if(!Config.isPositionCheckOn(context)) {
			final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setMessage(R.string.start_location_service);
			dialog.setPositiveButton(R.string.got_to_settings, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					// TODO Auto-generated method stub
					Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					context.startActivity(myIntent);
					//get gps
				}
			});
			dialog.setNegativeButton(R.string.closeDialog, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					if (!(context instanceof WelcomeActivity)) {
						showWelcomeScreen(context, false);
					}
				}
			});
			dialog.show();
		}
	}

	public void killLocationProcess() {
		Log.d(LOG_TAG, "killing process..." + locman.toString());
		if (locman != null) {
			this.locman.removeUpdates(loclisten);
		}
	}

	public void showWelcomeScreen(Context c, boolean showDialog) {
		Intent welcome = new Intent(c, WelcomeActivity.class);
		welcome.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NEW_TASK);
		welcome.putExtra(INTENT_EXTRA_SHOW_DIALOG_NAME, showDialog);
		c.startActivity(welcome);
		((Activity) c).finish();
	}
}
