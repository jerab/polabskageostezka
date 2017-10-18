package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;

/**
 * Created by Fogs on 14.5.2017.
 */

public abstract class BaseTaskActivity extends Activity implements TaskResultDialog.TaskResultDialogInterface {
	private static final String LOG_TAG = "GEO BaseTaskActivity";
	AlertDialog alertDialog;
	LocationUtil location;
	protected String baseNazev;
	protected String baseZadani;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	protected void init(String nazev, String zadani) {
		baseNazev = nazev;
		baseZadani = zadani;
		this.setTitle(nazev);
	}

	public void UkazZadani(String nazev, String zadani) {
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(nazev);
		alertDialog.setMessage(zadani);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alertDialog.show();
	}
	// public abstract void SetCurentTask(int ID);
	// public abstract int GetCurentTask();

	@Override
	protected void onResume() {
		super.onResume();
		location = new LocationUtil(this);
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
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_task, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.task_menu_info:
				UkazZadani(baseNazev, baseZadani);
				return true;
			case R.id.task_menu_back:
				startActivity(new Intent(this, DashboardActivity.class));
				this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void showResultDialog(boolean status, String title, String resultInfo, boolean closeActivity) {
		Log.d(LOG_TAG, "showing Result Dialog... " + title + " | " + this.getClass().getName());
		Dialog dialog = new TaskResultDialog(this, title, resultInfo, status, closeActivity);
		dialog.show();
	}
}
