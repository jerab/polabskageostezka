package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUloh;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUlohIntro;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleID;


public class DashboardActivity extends BaseActivity {

	InitDB db;
	GridView ulohyLL;
	DashboardButton[] ulohyBtns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		Boolean isFirstRun = getSharedPreferences("FIRST", MODE_PRIVATE).getBoolean(getString(R.string.firstRunValue), true);
		if (isFirstRun) {
			Intent intent = new Intent(this, WelcomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			//Toast.makeText(DashboardActivity.this, "First Run", Toast.LENGTH_SHORT).show();
		}


		setContentView(R.layout.activity_dashboard);
        db = new InitDB(this);

        ulohyLL = (GridView) findViewById(R.id.llUlohy);

		/// nejsou splneny Intro ulohy
		if(!this.setIntroTasks()) {
			ImageView mapa = (ImageView) this.findViewById(R.id.dbMapaImg);
			mapa.setVisibility(View.VISIBLE);
			this.setMainTasks();
		}
		Log.d("GEO log - ulohy", ulohyBtns.length + " pocet");
		ulohyLL.setAdapter(new DashboardAdapter(this, ulohyBtns));
	}

	private boolean setIntroTasks() {
		ulohyBtns = new DashboardButton[vratPocetUlohIntro()];
		Boolean splneno = false;
		Task t;
		int stav;
		for (int i=0; i<(vratPocetUlohIntro());i++) {
			t = vratUlohuPodleID(i);
			stav = db.vratStavUlohy(t.getId());
			ulohyBtns[i] = new DashboardButton(this, t.getNazev(), t.getTyp(), stav, t.getId());
			splneno = (splneno || stav == TASK_STATUS_DONE);
			Log.d("Geo - DashBoard", i + " - Intro task status: " + stav);
		}
		return splneno;
	}

	private void setMainTasks() {
		ulohyBtns = new DashboardButton[vratPocetUloh()];
		Task t;
		int stav;
		for (int i=0; i<(vratPocetUloh());i++) {
			t = vratUlohuPodleID(i);
			stav = db.vratStavUlohy(t.getId());
			ulohyBtns[i] = new DashboardButton(this, t.getNazev(), t.getTyp(), stav, t.getId());
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
