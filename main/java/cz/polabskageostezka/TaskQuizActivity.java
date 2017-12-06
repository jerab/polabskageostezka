package cz.polabskageostezka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.polabskageostezka.tasks.QuizTask;
import cz.polabskageostezka.utils.BaseTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.InitDB;
import cz.polabskageostezka.utils.QuizTaskItemConfig;


public class TaskQuizActivity extends BaseTaskActivity {

	private static final String LOG_TAG = "GEO TaskQuizActivity";
	private static final int RADIO_BUTT_ID_PLUS = 10;

	QuizTask qt;
    InitDB db = new InitDB(this);
    String[] otazky;
    QuizTaskItemConfig[] odpovedi;
    boolean finished = false;
    RadioGroup radioGroup;
    RadioButton[] radioButtons;
    TextView zadani;
    Button odeslat, zpet, dalsi;
	ImageView infoOtazka;
    int cisloAktualniOtazky = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_quiz);

		zadani = (TextView) findViewById(R.id.tvQtOtazka);
		radioGroup = (RadioGroup) findViewById(R.id.rgQtOdpovedi);
		odeslat = (Button) findViewById(R.id.btnQtSend);
		infoOtazka = (ImageView) findViewById(R.id.infoQuestion);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        qt = (QuizTask) Config.vratUlohuPodleID(predaneID);
        db.open();
		super.init(qt.getNazev(), qt.getZadani());
		cisloAktualniOtazky = db.posledniOtazka(qt.getId());
        if (db.vratStavUlohy(qt.getId()) == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(qt.getId());
			UkazZadani(qt.getNazev(), qt.getZadani());
		} else if(db.vratStavUlohy(qt.getId()) == Config.TASK_STATUS_DONE) {
            finished = true;
        }
        db.close();

		Log.d(LOG_TAG, "Aktualni: " + cisloAktualniOtazky);

        otazky = qt.getOtazky();

        /// pouze prohlizeni odpovedi
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
			infoOtazka.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					showResultDialog(true, qt.getSpravnaOdpoved(cisloAktualniOtazky), false);
				}
			});
        /// reseni odpovedi
		} else {
            odeslat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
					for (int i=0; i < radioButtons.length; i++) {
						if (radioButtons[i].isChecked()) {
							//zapis do db uspech
							if(overOdpoved(odpovedi[i])){
								/// pokracujeme na dalsi otázku
								if ( cisloAktualniOtazky <  otazky.length-1){
									ZapisOtazkyDoDB(Config.TASK_STATUS_DONE);
									// ukaz dialog //
									showResultDialog(true, qt.getZpetnaVazba(odpovedi[i], true), true);
								/// // byla poslední otázka - zapis do db, ukonci
								}  else {
									db.open();
									db.zapisTaskDoDatabaze(qt.getId(),System.currentTimeMillis());
									db.close();
									showResultDialog(true, qt.getZpetnaVazba(odpovedi[i], true), false);
								}
							} else {
								showResultDialog(false, qt.getZpetnaVazba(odpovedi[i], false), false);
							}
						}
					}
                }
            });
        }
		NactiAktivniUlohu(finished);
    }

	private RadioButton getRadioButton(Context parent, int id, String text) {
		RadioButton r = new RadioButton(parent);
		r.setId(RADIO_BUTT_ID_PLUS+id);
		r.setText(text);
		r.setButtonDrawable(R.drawable.radio_button_task);
		r.setGravity(Gravity.CENTER_VERTICAL);
		r.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		r.setPadding(30, 0, 5, 0);
		r.setClickable(true);
		return r;
	}

    private void setRadioButtons(QuizTaskItemConfig[] odp, boolean correctAnswer) {
		this.setOdpovediArray(odp.length);
		radioButtons = new RadioButton[odp.length];

		LinearLayout.LayoutParams lp;
		for(int i = 0; i < radioButtons.length; i++) {
			Log.d(LOG_TAG, "Creating radiobutton ..." + i);
			radioButtons[i] = getRadioButton(radioGroup.getContext(), i, odp[i].getPopisek());
			radioButtons[i].setTag(odp[i].isSpravne());
			if(correctAnswer) {
				radioButtons[i].setClickable(false);
				if(odp[i].isSpravne()) {
					radioButtons[i].setChecked(true);
					radioButtons[i].setButtonDrawable(R.drawable.ic_radio_button_correct);
				}
			}else {
				radioButtons[i].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if(radioGroup.getCheckedRadioButtonId() >= 0) {
							odeslat.setEnabled(true);
						}else {
							odeslat.setEnabled(false);
						}
					}
				});
			}
			radioGroup.addView(radioButtons[i]);
			lp = (LinearLayout.LayoutParams) radioButtons[i].getLayoutParams();
			lp.bottomMargin = 50;
			radioButtons[i].setLayoutParams(lp);
			this.addToOdpovedi(i, odp[i]);
		}
	}

	private void setOdpovediArray(int delka) {
		odpovedi = new QuizTaskItemConfig[delka];
	}
	private void addToOdpovedi(int index, QuizTaskItemConfig item) {
		odpovedi[index] = item;
	}

    private boolean overOdpoved(QuizTaskItemConfig item) {
        Log.d(LOG_TAG, "Overuju odpoved: " + item.isSpravne());
		return item.isSpravne();
    }

    private void NactiAktivniUlohu(boolean zobrazitVysledek) {
        resetRadioButtons();
        QuizTaskItemConfig[] aktualniOdpovedi = qt.getOdpovediKOtazce(cisloAktualniOtazky);
		Log.d(LOG_TAG, "Akt uloha - sada: " + cisloAktualniOtazky);
		Log.d(LOG_TAG, "Pocet odpovedi: " + aktualniOdpovedi.length);

		zadani.setText(otazky[cisloAktualniOtazky]);
        if (!finished) {
			ZapisOtazkyDoDB();
			odeslat.setEnabled(false);
		}
		if(!zobrazitVysledek) {
			List<QuizTaskItemConfig> strList = Arrays.asList(aktualniOdpovedi);
			Collections.shuffle(strList);
			aktualniOdpovedi = strList.toArray(new QuizTaskItemConfig[strList.size()]);
			infoOtazka.setVisibility(View.INVISIBLE);
		}else {
			infoOtazka.setVisibility(View.VISIBLE);
		}
        setRadioButtons(aktualniOdpovedi, zobrazitVysledek);
    }

    private void resetRadioButtons(){
		radioGroup.clearCheck();
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

	private void showResultDialog(boolean status, String resultInfo, boolean nextQuest) {
		Log.d(LOG_TAG, "Show dialog RESULT: " + String.valueOf(status) + " | " + String.valueOf(nextQuest));
		super.showResultDialog(status, qt.getNazev(), resultInfo, nextQuest);
	}

	@Override
	public void runFromResultDialog(boolean result, boolean nextQuest) {
		Log.d(LOG_TAG, "Run from RESULT: " + String.valueOf(result) + " | " + String.valueOf(nextQuest));
		if(result) {
			/// bylo pouze zobrazeni spravne odpovedi
			if(finished) {
				Log.d(LOG_TAG, "Info RESULT do nothing");
			}else if(nextQuest) {
				cisloAktualniOtazky++;
				if(cisloAktualniOtazky < otazky.length) {
					NactiAktivniUlohu(false);
				}else {
					runNextQuest(qt.getRetezId(), this);
				}
			}else {
				startActivity(new Intent(TaskQuizActivity.this, DashboardActivity.class));
				finish();
			}
		}else {
			Log.d(LOG_TAG, "FAULT RESULT do nothing");
		}
	}

	@Override
	public void runFromStartTaskDialog() {

	}
/*
	private void runNextQuest() {
		final int idDalsi = qt.getRetezId();
		// navrat na dashboard
		if (idDalsi == -1) {
			startActivity(new Intent(TaskQuizActivity.this, DashboardActivity.class));
		/// pokracujeme na dasli Task
		} else {
			Task t = Config.vratUlohuPodleID(idDalsi);
			Log.d("TaskCamAct", "idDalsi: " + idDalsi + "/// typ: " + t.getTyp());
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
							finish();
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
							finish();
						}
					});

					break;
				case 4:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Intent i = new Intent(TaskQuizActivity.this, TaskArActivity.class);
							i.putExtra("id", idDalsi);
							startActivity(i);
							finish();
						}
					});

					break;
			}
		}
	}
	*/
}
