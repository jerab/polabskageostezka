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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.QRReadActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.SettingsActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.WelcomeActivity;

/**
 * Created by Fogs on 13.5.2017.
 */

public class BaseActivity extends Activity {

    private LocationManager locman;
    private LocationListener loclisten;
    private double lat,lng;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    Context context;

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_nastenka:
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            case R.id.menu_nastaveni:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_o_app:
                startActivity(new Intent(this, WelcomeActivity.class));
                return true;
            case R.id.menu_qr_reader:
                startActivity(new Intent(this, QRReadActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public LatLng vratPozici() {
        context = this.getApplicationContext();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            //overeno
            locman = (LocationManager) getSystemService(LOCATION_SERVICE);
            loclisten = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(BaseActivity.this);
                    dialog.setMessage("Je treba zapnout GPS");
                    dialog.setPositiveButton ("Prejit do nastaveni", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            BaseActivity.this.startActivity(myIntent);
                            //get gps
                        }
                    });
                    dialog.setNegativeButton("Zavrit", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            // TODO Auto-generated method stub
                        }
                    });
                    dialog.show();
                }
            };
            locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loclisten);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }


        return new LatLng(lat, lng);
    }

    public void killPozici(){
        locman.removeUpdates(loclisten);
    }

}
