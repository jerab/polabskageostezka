package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.TaskResultDialog;


public class TaskQuizActivity extends BaseTaskActivity {
	private static final String LOG_TAG = "GEO TaskQuizActivity";
	private static final int RADIO_BUTT_ID_PLUS = 10;
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
                    NactiAktivniUlohu(true);
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
                    NactiAktivniUlohu(true);
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
                                //Toast.makeText(getApplicationContext(),"Tato odpoved je spravne, nasleduje dalsi otazka",Toast.LENGTH_SHORT).show();
                                if ( cisloAktualniOtazky <  otazky.length-1){
									// ukaz dialog //
									showResultDialog(true, qt.getZpetnaVazba(radioButtons[i].getId() - RADIO_BUTT_ID_PLUS, true), true);
                                    ZapisOtazkyDoDB(2);
                                    cisloAktualniOtazky++;
                                    /// dalsi uloha je spustena z result dialogu
									/*
									runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            NactiAktivniUlohu();
                                        }
                                    });*/
                                }  else {
                                    //zapis do db, ukonci
                                    db.open();
                                    db.zapisTaskDoDatabaze(qt.getId(),System.currentTimeMillis());
                                    db.close();
                                    if (qt.getRetezId() == -1) {
										showResultDialog(true, qt.getZpetnaVazba(radioButtons[i].getId() - RADIO_BUTT_ID_PLUS, true), false);
                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"Uloha dokoncena",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        startActivity(new Intent(TaskQuizActivity.this, DashboardActivity.class));
                                        finish();
                                        */
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
								//showResultDialog(false, "Spatne, spatne", false);
								showResultDialog(false, qt.getZpetnaVazba(radioButtons[i].getId() - RADIO_BUTT_ID_PLUS, false), false);
								//Toast.makeText(getApplicationContext(),"Tato odpoved neni spravne, zkuste to znovu",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
        NactiAktivniUlohu(false);
    }

	private void showResultDialog(boolean status, String resultInfo, boolean nextQuest) {
		Dialog dialog = new TaskResultDialog(this, this.qt.getNazev(), resultInfo, status, nextQuest);
		dialog.show();
	}

	private RadioButton getRadioButton(Context parent, int id, String text) {
		RadioButton r = new RadioButton(parent);
		r.setId(RADIO_BUTT_ID_PLUS+id);
		r.setText(text);
		r.setButtonDrawable(R.drawable.radio_button_task);
		r.setGravity(Gravity.CENTER_VERTICAL);
		r.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		r.setPadding(30, 0, 5, 0);
		r.setClickable(true);
		return r;
	}

    private void setRadioButtons(String[] odp, boolean correctAnswer) {
		radioButtons = new RadioButton[odp.length];
		for(int i = 0; i < radioButtons.length; i++) {
			Log.d(LOG_TAG, "Creating radiobutton ...");
			radioButtons[i] = getRadioButton(radioGroup.getContext(), i, odp[i]);
		}

		if(correctAnswer) {
			radioButtons[0].setButtonDrawable(R.drawable.ic_radio_button_correct);
		}
		//this.zamichejOdpovedi(radioButtons);
		LinearLayout.LayoutParams lp;
		for (int i = 0; i < radioButtons.length; i++) {
			Log.d(LOG_TAG, "Adding radiobutton ...");
			radioGroup.addView(radioButtons[i]);
			lp = (LinearLayout.LayoutParams) radioButtons[i].getLayoutParams();
			lp.bottomMargin = 50;
			radioButtons[i].setLayoutParams(lp);
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

    private void NactiAktivniUlohu(boolean zobrazitVysledek) {
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
        /*
        List<String> strList = Arrays.asList(meziOdpovedi);
        Collections.shuffle(strList);
        meziOdpovedi = strList.toArray(new String[strList.size()]);
		*/

        setRadioButtons(meziOdpovedi, zobrazitVysledek);
    }

    private void resetRadioButtons(){
        radioGroup.removeAllViewsInLayout();
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

	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
		if(result) {
			this.NactiAktivniUlohu(false);
		}
	}
}
