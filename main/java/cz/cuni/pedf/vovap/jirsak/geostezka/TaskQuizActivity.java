package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.QuizTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;


public class TaskQuizActivity extends BaseTaskActivity {
	private static final String LOG_TAG = "GEO TaskQuizActivity";
	QuizTask qt;
    InitDB db = new InitDB(this);
    int[] pocetOdpovediNaOtazku;
    String[] otazky;
    String[] odpovedi;
    boolean finished = false;
    RadioGroup radioGroup;
    RadioButton[] radioButtons;
    TextView zadani;
    Button odeslat, zpet, dalsi;
    int cisloAktualniOtazky = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_quiz);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        qt = (QuizTask) Config.vratUlohuPodleID(predaneID);
        db.open();
        if (db.vratStavUlohy(qt.getId())==0)
            db.odemkniUlohu(qt.getId());
        else if(db.vratStavUlohy(qt.getId())==2) {
            finished = true;
        }
        cisloAktualniOtazky = db.posledniOtazka(qt.getId());
        db.close();

		Log.d(LOG_TAG, "Cislo aktualni otazky: " + cisloAktualniOtazky);
		UkazZadani(qt.getNazev(), qt.getZadani());
        pocetOdpovediNaOtazku = qt.getPocetOdpovediKOtazce();
		Log.d(LOG_TAG, "Pocet odpovedi: " + pocetOdpovediNaOtazku[cisloAktualniOtazky]);
        otazky = qt.getOtazky();
        odpovedi = qt.getOdpovedi();

		zadani = (TextView) findViewById(R.id.tvQtOtazka);
        radioGroup = (RadioGroup) findViewById(R.id.rgQtOdpovedi);

		// nastavit odpovedi
		//setRadioButtons();

		/*{(RadioButton) findViewById(R.id.rb0),
                (RadioButton) findViewById(R.id.rb1),
                (RadioButton) findViewById(R.id.rb2),
                (RadioButton) findViewById(R.id.rb3),
                (RadioButton) findViewById(R.id.rb4)};
*/
		odeslat = (Button) findViewById(R.id.btnQtSend);
        if (finished) {
            odeslat.setVisibility(View.INVISIBLE);
            dalsi = (Button) findViewById(R.id.btnQtNext);
            dalsi.setVisibility(View.VISIBLE);
            dalsi.setEnabled(false);
            dalsi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cisloAktualniOtazky++;
                    if (cisloAktualniOtazky==otazky.length-1)
                        dalsi.setEnabled(false);
                    if (!zpet.isEnabled())
                        zpet.setEnabled(true);
                    NactiAktivniUlohu();
                }
            });
            zpet = (Button) findViewById(R.id.btnQtBack);
            zpet.setVisibility(View.VISIBLE);
            zpet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cisloAktualniOtazky=cisloAktualniOtazky-1;
                    if (cisloAktualniOtazky == 0)
                        zpet.setEnabled(false);
                    if (!dalsi.isEnabled())
                        dalsi.setEnabled(true);
                    NactiAktivniUlohu();
                }
            });
        } else {
            odeslat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i=0; i < radioButtons.length; i++) {
                        if (radioButtons[i].isChecked()){
                            if(overOdpoved(radioButtons[i].getText())){
                                //zapis do db uspech
                                // intent na dalsi otazku
                                Toast.makeText(getApplicationContext(),"Tato odpoved je spravne, nasleduje dalsi otazka",Toast.LENGTH_SHORT).show();
                                if ( cisloAktualniOtazky <  otazky.length-1){
                                    ZapisOtazkyDoDB(2);
                                    cisloAktualniOtazky++;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            NactiAktivniUlohu();
                                        }
                                    });
                                }  else {
                                    //zapis do db, ukonci
                                    db.open();
                                    db.zapisTaskDoDatabaze(qt.getId(),System.currentTimeMillis());
                                    db.close();
                                    if (qt.getRetezId() == -1) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"Uloha dokoncena",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        startActivity(new Intent(TaskQuizActivity.this, DashboardActivity.class));
                                        finish();
                                    } else {
                                        Task t = Config.vratUlohuPodleID(qt.getRetezId());
                                        final int idDalsi = qt.getRetezId();
                                        Log.d("TaskCamAct","idDalsi: " + idDalsi + "/// typ: " + t.getTyp());
                                        switch (t.getTyp()) {
                                            case 1:
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent i = new Intent(TaskQuizActivity.this, TaskCamActivity.class);
                                                        i.putExtra("id", idDalsi);
                                                        startActivity(i);
                                                        finish();
                                                    }
                                                });
                                                break;
                                            case 2:
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent i = new Intent(TaskQuizActivity.this, TaskDragDropActivity.class);
                                                        i.putExtra("id", idDalsi);
                                                        startActivity(i);
                                                    }
                                                });

                                                break;
                                            case 3:
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent i = new Intent(TaskQuizActivity.this, TaskQuizActivity.class);
                                                        i.putExtra("id", idDalsi);
                                                        startActivity(i);
                                                    }
                                                });

                                                break;
                                            case 4:
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent i = new Intent(TaskQuizActivity.this, TaskARTestActivity.class);
                                                        i.putExtra("id", idDalsi);
                                                        startActivity(i);
                                                    }
                                                });

                                                break;
                                        /*default:
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(),"Uloha dokoncena",Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(TaskQuizActivity.this, DashboardActivity.class));
                                                    finish();
                                                }
                                            });
                                            break;*/
                                        }
                                    }

                                }

                            } else {
                                Toast.makeText(getApplicationContext(),"Tato odpoved neni spravne, zkuste to znovu",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
        NactiAktivniUlohu();
    }

    private void setRadioButtons(String[] odp) {
		radioButtons = new RadioButton[odp.length];
		for(int i = 0; i < radioButtons.length; i++) {
			Log.d(LOG_TAG, "Creating radiobutton ...");
			radioButtons[i] = getRadioButton(radioGroup.getContext(), odp[i]);
		}
		//this.zamichejOdpovedi(radioButtons);
		for (int i = 0; i < radioButtons.length; i++) {
			Log.d(LOG_TAG, "Adding radiobutton ...");
			radioGroup.addView(radioButtons[i]);
		}
	}

    private RadioButton[] zamichejOdpovedi(RadioButton[] odpovedi) {
		Collections.shuffle(Arrays.asList(odpovedi));
		return odpovedi;
	}

    private boolean overOdpoved(CharSequence text) {
        int zacniOd = 0;
        for (int k=0; k < cisloAktualniOtazky; k++){
            zacniOd += pocetOdpovediNaOtazku[k];
        }
        if (text.equals(odpovedi[zacniOd])){
            return true;
        } else {
            return false;
        }

    }

    private void NactiAktivniUlohu() {
        resetRadioButtons();
        String[] meziOdpovedi = new String[pocetOdpovediNaOtazku[cisloAktualniOtazky]];
        int zacniOd = 0;
        zadani.setText(otazky[cisloAktualniOtazky]);
        if (!finished)
        ZapisOtazkyDoDB();
        // todo preklik otazek + dovyznacena spravna odpoved
        for (int k=0; k < cisloAktualniOtazky; k++){
            zacniOd += pocetOdpovediNaOtazku[k];
        }
        for (int i=0; i < pocetOdpovediNaOtazku[cisloAktualniOtazky]; i++) {
            meziOdpovedi[i] = odpovedi[zacniOd+i];
        }
        List<String> strList = Arrays.asList(meziOdpovedi);
        Collections.shuffle(strList);
        meziOdpovedi = strList.toArray(new String[strList.size()]);

        /*for (int i=0; i < pocetOdpovediNaOtazku[cisloAktualniOtazky]; i++) {
			radioButtons[i].setText(meziOdpovedi[i]);
            radioButtons[i].setVisibility(View.VISIBLE);
        }*/
        setRadioButtons(meziOdpovedi);
    }

    private RadioButton getRadioButton(Context parent, String text) {
		RadioButton r = new RadioButton(parent, null, R.style.GeoThemeRadioButt);
		r.setText(text);
		return r;
	}

    private void resetRadioButtons(){
        radioGroup.removeAllViewsInLayout();
        /*for (int i=0; i < radioButtons.length;i++){
            radioButtons[i].setVisibility(View.INVISIBLE);
            radioButtons[i].setText("");
        }*/
    }
    private void ZapisOtazkyDoDB(){
        InitDB db = new InitDB(this);
        db.open();
        db.otazkaDoDB(qt.getId(),cisloAktualniOtazky);
        db.close();
    }
    private void ZapisOtazkyDoDB(int stav){
        InitDB db = new InitDB(this);
        db.open();
        db.otazkaDoDB(qt.getId(),cisloAktualniOtazky, stav);
        db.close();
    }
}
