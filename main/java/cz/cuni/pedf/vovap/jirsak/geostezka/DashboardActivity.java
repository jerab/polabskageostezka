package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUloh;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleID;


public class DashboardActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Boolean isFirstRun = getSharedPreferences("FIRST", MODE_PRIVATE).getBoolean(getString(R.string.firstRunValue), true);
        if (isFirstRun) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(DashboardActivity.this, "First Run", Toast.LENGTH_LONG).show();
        }
        LinearLayout ulohyLL = (LinearLayout) findViewById(R.id.llUlohy);
        Button[] ulohyBtns = new Button[vratPocetUloh()];

        for (int i=0; i<(vratPocetUloh());i++)
        {

            Task t = vratUlohuPodleID(i);
            ulohyBtns[i] = new Button(this);
            ulohyBtns[i].setText(t.getNazev());
            setOnClick(ulohyBtns[i],t.getId(),t.getTyp());
            Log.d("GEO log - TYP: ", t.getTyp() + " ID: "+ String.valueOf(t.getId()));
            ulohyBtns[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ulohyLL.addView(ulohyBtns[i]);
        }

    }

    public void setOnClick(final Button btn, final int id, final int typ){
        Log.d("GEO log - TYP: ", typ + " ID: "+ String.valueOf(id));
        switch (typ)
        {
            case Config.TYP_ULOHY_CAM:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // camtask
                        Intent i = new Intent(DashboardActivity.this, TaskCamActivity.class);
                        i.putExtra("id", id);
                        startActivity(i);
                        //finish();
                    }
                });
                break;
            case Config.TYP_ULOHY_DRAGDROP:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // dragdrop
                        Intent i = new Intent(DashboardActivity.this, TaskDragDropActivity.class);
                        i.putExtra("id", id);
                        startActivity(i);
                        //finish();
                    }
                });
                break;
            case Config.TYP_ULOHY_QUIZ:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // quiztask
                        Intent i = new Intent(DashboardActivity.this, TaskQuizActivity.class);
                        i.putExtra("id", id);
                        startActivity(i);
                    }
                });
                break;
            case Config.TYP_ULOHY_AR:
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // artask
						// quiztask
						Intent i = new Intent(DashboardActivity.this, TaskARTestActivity.class);
						i.putExtra("id", id);
						startActivity(i);
                        //Toast.makeText(DashboardActivity.this, "Augmented Reality: " + String.valueOf(id), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

    }

}
