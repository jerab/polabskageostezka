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
import android.widget.LinearLayout;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.DashboardAdapter;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.DashboardButton;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUloh;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleID;


public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        int stav;
        InitDB db = new InitDB(this);
        Boolean isFirstRun = getSharedPreferences("FIRST", MODE_PRIVATE).getBoolean(getString(R.string.firstRunValue), true);
        if (isFirstRun) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            //Toast.makeText(DashboardActivity.this, "First Run", Toast.LENGTH_SHORT).show();
        }
        GridView ulohyLL = (GridView) findViewById(R.id.llUlohy);
        DashboardButton[] ulohyBtns = new DashboardButton[vratPocetUloh()];
        if (!isFirstRun){
            for (int i=0; i<(vratPocetUloh());i++)
            {
                Task t = vratUlohuPodleID(i);
                stav = db.vratStavUlohy(t.getId());
				ulohyBtns[i] = new DashboardButton(this, t.getNazev(), t.getTyp(), stav, t.getId());
                /*switch (stav){
                    case 0:
                        // nezobrazujeme - je zamcena
						Log.d("GEO DashboardActivity", "stav 0");
						ulohyBtns[i] = new DashboardButton(this, t.getNazev(), stav);
						ulohyBtns[i].setText(t.getNazev());
                        break;
                    case 1:
                        // odemcena aktivni
                        ulohyBtns[i] = new DashboardButton(this, t.getNazev(), stav);
                        ulohyBtns[i].setText(t.getNazev());
                        //setOnClick(ulohyBtns[i],t.getId(),t.getTyp());
						Log.d("GEO log - TYP: ", "i: " + i + " | " + t.getTyp() + " ID: "+ String.valueOf(t.getId()));
                        //ulohyBtns[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        //ulohyLL.addView(ulohyBtns[i]);
                        break;
                    case 2:
                        // hotovo
                        ulohyBtns[i] = new DashboardButton(this, t.getNazev(), stav);
                        ulohyBtns[i].setText(t.getNazev()+" - Splneno");
                        //setOnClick(ulohyBtns[i],t.getId(),t.getTyp());
                        Log.d("GEO log - TYP: ", "i: " + i + " | " + t.getTyp() + " ID: "+ String.valueOf(t.getId()));
                        //ulohyBtns[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        //ulohyLL.addView(ulohyBtns[i]);
                        break;
                    default:
                        Log.d("GEO DashboardActivity", "Something didnt work");
                        break;
                }*/

            }
        }
        Log.d("GEO log - ulohy", ulohyBtns.length + " pocet");
		ulohyLL.setAdapter(new DashboardAdapter(this, ulohyBtns));
	}

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
        }
    }

}
