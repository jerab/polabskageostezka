package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.QuizTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;


public class TaskQuizActivity extends BaseTaskActivity {
    QuizTask qt;
    InitDB db = new InitDB(this);
    int[] pocetOdpovediNaOtazku;
    String[] otazky;
    String[] odpovedi;
    RadioGroup radioGroup;
    RadioButton[] radioButtons;
    TextView zadani;
    Button odeslat;
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
        cisloAktualniOtazky = db.posledniOtazka(qt.getId());
        db.close();
        UkazZadani(qt.getNazev(), qt.getZadani());
        pocetOdpovediNaOtazku = qt.getPocetOdpovediKOtazce();
        otazky = qt.getOtazky();
        odpovedi = qt.getOdpovedi();
        zadani = (TextView) findViewById(R.id.tvQtOtazka);
        radioGroup = (RadioGroup) findViewById(R.id.rgQtOdpovedi);
        radioButtons = new RadioButton[] {(RadioButton) findViewById(R.id.rb0),
                (RadioButton) findViewById(R.id.rb1),
                (RadioButton) findViewById(R.id.rb2),
                (RadioButton) findViewById(R.id.rb3),
                (RadioButton) findViewById(R.id.rb4)};
        odeslat = (Button) findViewById(R.id.btnQtSend);
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
                                //zapis do db, ukonci smer nastenka
                                db.open();
                                db.zapisTaskDoDatabaze(qt.getId(),System.currentTimeMillis());
                                db.close();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"Uloha dokoncena",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                startActivity(new Intent(TaskQuizActivity.this, DashboardActivity.class));
                                finish();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(),"Tato odpoved neni spravne, zkuste to znovu",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        NactiAktivniUlohu();
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
        RadioButtonDefault();
        String[] meziOdpovedi = new String[pocetOdpovediNaOtazku[cisloAktualniOtazky]];
        int zacniOd = 0;
        zadani.setText(otazky[cisloAktualniOtazky]);
        ZapisOtazkyDoDB();
        for (int k=0; k < cisloAktualniOtazky; k++){
            zacniOd += pocetOdpovediNaOtazku[k];
        }
        for (int i=0; i < pocetOdpovediNaOtazku[cisloAktualniOtazky]; i++) {
            meziOdpovedi[i] = odpovedi[zacniOd+i];
        }
        List<String> strList = Arrays.asList(meziOdpovedi);
        Collections.shuffle(strList);
        meziOdpovedi = strList.toArray(new String[strList.size()]);

        for (int i=0; i < pocetOdpovediNaOtazku[cisloAktualniOtazky]; i++) {
            radioButtons[i].setText(meziOdpovedi[i]);
            radioButtons[i].setVisibility(View.VISIBLE);
        }
    }
    private void RadioButtonDefault(){
        for (int i=0; i < radioButtons.length;i++){
            radioButtons[i].setChecked(false);
            radioButtons[i].setVisibility(View.INVISIBLE);
            radioButtons[i].setText("");
        }
    }
    // todo metoda na ulozeni do jednotlivych otazek do db + znovu otevreni ulohy tam kde skoncil
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
