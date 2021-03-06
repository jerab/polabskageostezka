package cz.polabskageostezka.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import cz.polabskageostezka.DashboardActivity;
import cz.polabskageostezka.R;
import cz.polabskageostezka.TaskArActivity;
import cz.polabskageostezka.TaskCamActivity;
import cz.polabskageostezka.TaskDragDropActivity;
import cz.polabskageostezka.TaskDrawActivity;
import cz.polabskageostezka.TaskGridActivity;
import cz.polabskageostezka.TaskQuizActivity;
import cz.polabskageostezka.TaskSwipeActivity;

/**
 * Created by Fogs on 14.5.2017.
 */

public abstract class BaseTaskActivity extends Activity implements TaskResultDialog.TaskResultDialogInterface {
	private static final String LOG_TAG = "GEO BaseTaskActivity";
	AlertDialog alertDialog;
	LocationUtil location;
	protected String baseNazev;
	protected String baseZadani;
	protected int taskId;
	protected boolean isIntroTask = false;
	protected int extraOpenDialog = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	protected void init(String nazev, String zadani, int taskId) {
		baseNazev = nazev;
		baseZadani = zadani;
		this.setTitle(nazev);
		this.taskId = taskId;
	}

	protected void init(String nazev, String zadani, int taskId, int openDialogLayout) {
		extraOpenDialog = openDialogLayout;
		this.init(nazev, zadani, taskId);
	}

	public void UkazZadani(String nazev, String zadani) {
		Log.d(LOG_TAG, "Ukaz zadani 1");
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(nazev);
		alertDialog.setMessage(zadani);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						runFromStartTaskDialog();
					}
				});
		alertDialog.show();

	}

	public void UkazZadani(String nazev, String zadani, int customViewLayout) {
		Log.d(LOG_TAG, "Ukaz zadani 2");
		alertDialog = new AlertDialog.Builder(this).create();
		LayoutInflater inflater = LayoutInflater.from(BaseTaskActivity.this);
		final View view = inflater.inflate(customViewLayout, null);
		alertDialog.setView(view);
		alertDialog.setTitle(nazev);
		alertDialog.setMessage(zadani);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						runFromStartTaskDialog();
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
		if(!Config.isPositionCheckingDone(this)) {
			location.checkLocationStatus();
		}
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
				Log.d(LOG_TAG, "Task ID " + getTaskId());
				if(taskId == Config.TASK_INTRO_B_ID) {
					UkazZadani(baseNazev, baseZadani, R.layout.intro_task_b_dialog);
				}else if(extraOpenDialog > 0) {
					UkazZadani(baseNazev, baseZadani, extraOpenDialog);
				}else {
					UkazZadani(baseNazev, baseZadani);
				}
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

	protected void runNextQuest(final int nextTask, final Context c) {
		// navrat na dashboard
		if (nextTask == -1) {
			runDashboard(c);
			/// pokracujeme na dasli INTRO Task
		} else if(isIntroTask) {
			runTask(Config.vratIntroUlohuPodleID(nextTask), c);
			/// pokracujeme na dasli Task
		}else {
			runTask(Config.vratUlohuPodleID(nextTask), c);
		}
	}

	protected void runDashboard(Context c) {
		Intent i = new Intent(c, DashboardActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}

	private void runTask(Task t, final Context c) {
		Log.d("RunNextActivity:", "id: " + t.getId() + "/// typ: " + t.getTyp());
		Intent i = new Intent();
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
				i.setClass(c, TaskArActivity.class);
				break;
			case Config.TYP_ULOHY_DRAW :
				i.setClass(c, TaskDrawActivity.class);
				break;
		}
		i.putExtra("id", t.getId());
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(i);
	}
}
