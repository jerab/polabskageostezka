package cz.polabskageostezka;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cz.polabskageostezka.utils.BaseActivity;
import cz.polabskageostezka.utils.BaseTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.DashboardAdapter;
import cz.polabskageostezka.utils.DashboardButton;
import cz.polabskageostezka.utils.ImageAndDensityHelper;
import cz.polabskageostezka.utils.Task;

import static cz.polabskageostezka.utils.Config.TASK_STATUS_DONE;
import static cz.polabskageostezka.utils.Config.TASK_STATUS_NOT_VISITED;
import static cz.polabskageostezka.utils.Config.vratIntroUlohuPodleID;
import static cz.polabskageostezka.utils.Config.vratPocetUloh;
import static cz.polabskageostezka.utils.Config.vratPocetUlohIntro;
import static cz.polabskageostezka.utils.Config.vratUlohuPodleID;


public class DashboardActivity extends BaseActivity {
	private static final String LOG_TAG = "Geo DashBoard";


	GridView ulohyLL;
	DashboardButton[] ulohyBtns;
	boolean isIntro = false;
	DashboardAdapter dbAdapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

		isIntro = false;

		/// spust 1. ulohu geostezky, pokud nebyla nikdy spustena
		if(isIntroSection() && db.vratStavUlohy(0) == TASK_STATUS_NOT_VISITED) {
			startTask(0, vratIntroUlohuPodleID(0).getTyp());
		}

		/// debug set or for intro tasks
		if(Config.isDebugTaskGroupOn(this) && Config.isDebugTaskGroupIntro(this)) {
			isIntro = true;
		}

		// zkusi nastavit Intro tasky a take promennou isIntro
		setIntroTasks();

		if(isIntro) {
			setContentView(R.layout.activity_dashboard_intro);
		}else {
			this.setMainTasks();
			setContentView(R.layout.activity_dashboard);
			ImageView qrReader = (ImageView) findViewById(R.id.dbStartQReader);
			qrReader.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent in = new Intent(DashboardActivity.this, QRReadActivity.class);
					in.setFlags(in.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(in);
				}
			});
			boolean nothingOpened = true;
			for(int i = 0; i < ulohyBtns.length; i++) {
				if(ulohyBtns[i].taskStatus > TASK_STATUS_NOT_VISITED) {
					nothingOpened = false;
					break;
				}
			}
			// zobrazit uvodni dialog
			if(nothingOpened) {
				final AlertDialog dial = new AlertDialog.Builder(this).create();
				LayoutInflater inflater = LayoutInflater.from(this);
				final ImageView view = new ImageView(this);
				view.setImageResource(R.drawable.qr_reader_icon);
				//view.setBackgroundColor(0xFF000000);
				/*
				view.setPadding(5,5,5,5);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lp.width = ImageAndDensityHelper.getDensityDependSize(getResources(), 100);
				view.setMinimumWidth(lp.width);
				view.setLayoutParams(lp);
				*/
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent in = new Intent(DashboardActivity.this, QRReadActivity.class);
						in.setFlags(in.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(in);
						dial.dismiss();
					}
				});
				dial.setView(view);
				dial.setTitle("Nástěnka hlavních úloh");
				dial.setMessage(getString(R.string.db_intro_info));
				dial.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dial.dismiss();
							}
						});
				dial.show();
			}
		}

		Log.d(LOG_TAG, "pocet uloh: " + ulohyBtns.length);
		ulohyLL = (GridView) findViewById(R.id.llUlohy);

		dbAdapter = new DashboardAdapter(this, ulohyBtns);
		Log.d(LOG_TAG, "WIDTH COLWID: " + ulohyBtns[0].getLayoutParams().width + " | " + ulohyLL.getColumnWidth());
		ulohyLL.setColumnWidth(ulohyBtns[0].getLayoutParams().width + 10);
		ulohyLL.setAdapter(dbAdapter);
	}

	private void setIntroTasks() {

		if(Config.isDebugTaskGroupOn(this) && !Config.isDebugTaskGroupIntro(this)) {
			isIntro = false;
		}else {
			int pocetUloh = vratPocetUlohIntro();
			int i;
			Task[] ts = new Task[pocetUloh];
			int[] stav = new int[pocetUloh];
			for (i = 0; i < pocetUloh; i++) {
				ts[i] = vratIntroUlohuPodleID(i);
				stav[i] = db.vratStavUlohy(ts[i].getId());
				if (stav[i] != TASK_STATUS_DONE) {
					Log.d(LOG_TAG, "task status type: " + stav[i]);
					isIntro = true;
				}

				Log.d(LOG_TAG, i + " - Intro task status: " + stav[i]);
			}

			if (isIntro) {
				ulohyBtns = new DashboardButton[vratPocetUlohIntro()];
				for (i = 0; i < pocetUloh; i++) {
					ulohyBtns[i] = new DashboardButton(this, ts[i].getLabel(), ts[i].getTyp(), stav[i], ts[i].getId(), true);
				}
			}
		}
	}

	private void setMainTasks() {
		int startCisloUloh = vratPocetUlohIntro();
		ulohyBtns = new DashboardButton[vratPocetUloh()];
		Task t;
		int stav;

		for (int i = startCisloUloh; i < (vratPocetUloh() + startCisloUloh); i++) {
			t = vratUlohuPodleID(i);
			Log.d(LOG_TAG, "task: " + t.toString());
			stav = db.vratStavUlohy(t.getId());
			ulohyBtns[i-startCisloUloh] = new DashboardButton(this, t.getLabel(), t.getTyp(), stav, t.getId(), false);
		}
	}

	/**
	 * Tato metoda se vola hlavne z tridy DasboardButton pri kliknuti na tlacitko ulohy
	 * @param id
	 * @param typ
	 */
    public void startTask(final int id, final int typ){
        Log.d(LOG_TAG, "TYP: " + typ + " ID: "+ String.valueOf(id));
		Intent i;
		switch (typ) {
            case Config.TYP_ULOHY_CAM:
                // camtask
				i = new Intent(DashboardActivity.this, TaskCamActivity.class);
				i.putExtra("id", id);
				startActivity(i);
                break;
            case Config.TYP_ULOHY_DRAGDROP:
				// dragdrop
				if(id == Config.TASK_UHLI_ID) {
					i = new Intent(DashboardActivity.this, TaskDragDropActivity.class);
				}else {
					i = new Intent(DashboardActivity.this, TaskDragDropActivity.class);
				}
				i.putExtra("id", id);
				startActivity(i);
                break;
            case Config.TYP_ULOHY_QUIZ:
				// quiztask
				i = new Intent(DashboardActivity.this, TaskQuizActivity.class);
				i.putExtra("id", id);
				startActivity(i);
                break;
            case Config.TYP_ULOHY_AR:
            	// artask
				if(id == Config.TASK_ACHAT_ID) {
					i = new Intent(DashboardActivity.this, TaskArAchatActivity.class);
				}else {
					i = new Intent(DashboardActivity.this, TaskArActivity.class);
				}
				i.putExtra("id", id);
				startActivity(i);
				//Toast.makeText(DashboardActivity.this, "Augmented Reality: " + String.valueOf(id), Toast.LENGTH_SHORT).show();
                break;
			case Config.TYP_ULOHY_GRID:
				// gridtask
				i = new Intent(DashboardActivity.this, TaskGridActivity.class);
				i.putExtra("id", id);
				startActivity(i);
				//Toast.makeText(DashboardActivity.this, "Augmented Reality: " + String.valueOf(id), Toast.LENGTH_SHORT).show();
				break;
			case Config.TYP_ULOHY_SWIPE:
				// swipetask
				i = new Intent(DashboardActivity.this, TaskSwipeActivity.class);
				i.putExtra("id", id);
				startActivity(i);
				break;
			case Config.TYP_ULOHY_DRAW:
				// drawtask
				i = new Intent(DashboardActivity.this, TaskDrawActivity.class);
				i.putExtra("id", id);
				startActivity(i);
				break;
        }
    }
}
