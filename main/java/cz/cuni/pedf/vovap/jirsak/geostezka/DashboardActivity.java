package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.DashboardAdapter;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.DashboardButton;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.TASK_STATUS_DONE;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratIntroUlohuPodleID;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUloh;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUlohIntro;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleID;


public class DashboardActivity extends BaseActivity {

	InitDB db;
	GridView ulohyLL;
	DashboardButton[] ulohyBtns;
	boolean isIntro = false;
	DashboardAdapter dbAdapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new InitDB(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/// jsou splneny vsechny Intro ulohy
		isIntro = false;
		this.setIntroTasks();
		if(!isIntro) {
			this.setMainTasks();
			setContentView(R.layout.activity_dashboard);
		}else {
			setContentView(R.layout.activity_dashboard_intro);
		}
		Log.d("GEO log - ulohy", ulohyBtns.length + " pocet");
		ulohyLL = (GridView) findViewById(R.id.llUlohy);

		dbAdapter = new DashboardAdapter(this, ulohyBtns);
		Log.d("GEO log - WIDTH COLWID", ulohyBtns[0].getLayoutParams().width + " | " + ulohyLL.getColumnWidth());
		ulohyLL.setColumnWidth(ulohyBtns[0].getLayoutParams().width + 10);
		ulohyLL.setAdapter(dbAdapter);
	}

	private void setIntroTasks() {
		int pocetUloh = vratPocetUlohIntro();
		int i;
		Task[] ts = new Task[pocetUloh];
		int[] stav = new int[pocetUloh];
		for (i=0; i<pocetUloh;i++) {
			ts[i] = vratIntroUlohuPodleID(i);
			stav[i] = db.vratStavUlohy(ts[i].getId());
			if(stav[i] != TASK_STATUS_DONE) {
				Log.d("Geo - DashBoard intro", "task status type: " + stav[i]);
				isIntro = true;
			}

			Log.d("Geo - DashBoard", i + " - Intro task status: " + stav[i]);
		}

		if(isIntro) {
			ulohyBtns = new DashboardButton[vratPocetUlohIntro()];
			for (i = 0; i < pocetUloh; i++) {
				ulohyBtns[i] = new DashboardButton(this, ts[i].getLabel(), ts[i].getTyp(), stav[i], ts[i].getId(), true);
			}
		}
	}

	private void setMainTasks() {
		ulohyBtns = new DashboardButton[vratPocetUloh()];
		Task t;
		int stav;
		for (int i=2; i<(vratPocetUloh()+2);i++) {
			t = vratUlohuPodleID(i);
			stav = db.vratStavUlohy(t.getId());
			ulohyBtns[i-2] = new DashboardButton(this, t.getLabel(), t.getTyp(), stav, t.getId(), false);
		}
	}

	/**
	 * Tato metoda se vola hlavne z tridy DasboardButton pri kliknuti na tlacitko ulohy
	 * @param id
	 * @param typ
	 */
    public void startTask(final int id, final int typ){
        Log.d("GEO log - TYP: ", typ + " ID: "+ String.valueOf(id));
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
				i = new Intent(DashboardActivity.this, TaskDragDropActivity.class);
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
				i = new Intent(DashboardActivity.this, TaskARTestActivity.class);
				i.putExtra("id", id);
				startActivity(i);
				//Toast.makeText(DashboardActivity.this, "Augmented Reality: " + String.valueOf(id), Toast.LENGTH_SHORT).show();
                break;
			case Config.TYP_ULOHY_GRID:
				// artask
				i = new Intent(DashboardActivity.this, TaskGridActivity.class);
				i.putExtra("id", id);
				startActivity(i);
				//Toast.makeText(DashboardActivity.this, "Augmented Reality: " + String.valueOf(id), Toast.LENGTH_SHORT).show();
				break;
        }
    }
}
