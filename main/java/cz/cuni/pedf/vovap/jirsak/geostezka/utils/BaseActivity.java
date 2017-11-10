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
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.QRReadActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.SettingsActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskARTestActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskCamActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskDragDropActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskDrawActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskGridActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskQuizActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskSwipeActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.WelcomeActivity;

/**
 * Created by Fogs on 13.5.2017.
 */

public class BaseActivity extends Activity {

    /*private LocationManager locman;
    private LocationListener loclisten;
    private double lat,lng;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;*/
    protected LocationUtil location;
	private static final String LOG_TAG = "GEO - BaseActivity";
    //Context context;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(LOG_TAG, "- " + getApplicationContext().getClass().getName() + " | onResume");
		Intent volano = getIntent();
		if(volano.getBooleanExtra(LocationUtil.INTENT_EXTRA_SHOW_DIALOG_NAME, true)) {
			location = new LocationUtil(this);
			// dont show dialog
		}else {
			location = new LocationUtil(this, false);
		}
		location.checkLocationStatus();
	}



	@Override
	protected void onPause() {
		super.onPause();
		Log.d(LOG_TAG, "- " + getApplicationContext().getClass().getName() + " | onPause - killing Location");
		location.killLocationProcess();
		location = null;
	}

	@Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent mi;
    	switch (item.getItemId()) {
            case R.id.menu_nastenka:
				mi = new Intent(this, DashboardActivity.class);
				mi.setFlags(mi.getFlags());
				startActivity(mi);
                this.finish();
                return true;
            case R.id.menu_nastaveni:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_o_app:
            	mi = new Intent(this, WelcomeActivity.class);
				mi.putExtra(LocationUtil.INTENT_EXTRA_SHOW_DIALOG_NAME, false);
                startActivity(mi);
                return true;
            case R.id.menu_qr_reader:
				mi = new Intent(this, QRReadActivity.class);
				mi.setFlags(mi.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(mi);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	protected void runNextQuest(final int nextTask, final Context c) {
		Intent i = new Intent();
    	// navrat na dashboard
		if (nextTask == -1) {
			i.setClass(c, DashboardActivity.class);
			/// pokracujeme na dasli Task
		} else {
			Task t = Config.vratUlohuPodleID(nextTask);
			Log.d("RunNextActivity:", "id: " + nextTask + "/// typ: " + t.getTyp());
			switch (t.getTyp()) {
				case Config.TYP_ULOHY_CAM:
					i.setClass(c, TaskCamActivity.class);
					break;
				case Config.TYP_ULOHY_DRAGDROP:
					i.setClass(c, TaskDragDropActivity.class);
					break;
				case Config.TYP_ULOHY_QUIZ:
					i.setClass(c, TaskQuizActivity.class);
					break;
				case Config.TYP_ULOHY_GRID:
					i.setClass(c, TaskGridActivity.class);
					break;
				case Config.TYP_ULOHY_SWIPE:
					i.setClass(c, TaskSwipeActivity.class);
				case Config.TYP_ULOHY_AR:
					i.setClass(c, TaskARTestActivity.class);
					break;
				case Config.TYP_ULOHY_DRAW :
					i.setClass(c, TaskDrawActivity.class);
					break;
			}
			i.putExtra("id", nextTask);
			i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		}
		startActivity(i);
	}
}
