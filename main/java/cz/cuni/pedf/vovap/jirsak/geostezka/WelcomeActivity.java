package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.poziceGeostezky;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratPocetUloh;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleID;

public class WelcomeActivity extends BaseActivity {
    TextView scrollView;
    Button btnContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        scrollView = (TextView) findViewById(R.id.tvObsah);
        scrollView.setMovementMethod(new ScrollingMovementMethod());
        btnContinue = (Button) findViewById(R.id.btnZacit);
        // overeni pozice
        LatLng pozice = vratPozici();
        Log.d("GEO","Pozice lat: " + String.valueOf(pozice.latitude) + "Pozice lng: " + String.valueOf(pozice.longitude));
        Button poz = (Button) findViewById(R.id.posit);
        poz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng pozice = vratPozici();
                if (pozice.latitude==0.0 && pozice.longitude==0.0)
                {
                    Toast.makeText(WelcomeActivity.this, "Pockejte na nacteni pozice", Toast.LENGTH_LONG).show();
                } else {
                    if (poziceGeostezky(pozice)){
                        btnContinue.setEnabled(true);
                    } else {
                        Toast.makeText(WelcomeActivity.this, "Nejste v dosahu stezky", Toast.LENGTH_LONG).show();
                    }
                }
                Log.d("GEO","Pozice lat: " + String.valueOf(pozice.latitude) + "Pozice lng: " + String.valueOf(pozice.longitude));
            }
        });
        if (pozice.latitude==0.0 && pozice.longitude==0.0)
        {
            btnContinue.setEnabled(false);
            pozice = vratPozici();
            Log.d("GEO LOK",String.valueOf(pozice));
        } else {
            poz.setVisibility(View.INVISIBLE);
        }
        // konec overeni pozice
        Log.d("GEO Welcome", "Existuje datoska? " + String.valueOf(doesDatabaseExist(this)));
        // prvotni zapis a vytvoreni db
        if (dbSet()){
            nachystejDB();
            getSharedPreferences("DB", MODE_PRIVATE).edit().putBoolean(getString(R.string.dbReadyValue), false).apply();
            Log.d("GEO Welcome","Databaze ready");
        }
        //konec db
        //zobrazeni pokracovat tlacitka
        if (firstrun()) {
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.firstRunValue), false);
                    editor.apply();*/
                    getSharedPreferences("FIRST", MODE_PRIVATE).edit().putBoolean(getString(R.string.firstRunValue), false).apply();
                    killPozici();
                    Intent intent = new Intent(WelcomeActivity.this, TaskCamActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });

        } else {
            btnContinue.setVisibility(View.GONE);
        }

    }



    private void nachystejDB() {
        InitDB db = new InitDB(this);
        try {
            db.open();
            for (int i = 0;i<vratPocetUloh();i++){
                Task t = vratUlohuPodleID(i);
                db.zapisTaskDoDatabaze(t.getId(),t.getTyp());
            }
            db.close();
        } catch (Exception e) {
            Log.d("GEO Welcome", "chyba db: " + e.toString());
        }

    }
    private boolean dbSet() {
        return getSharedPreferences("DB", MODE_PRIVATE).getBoolean(getString(R.string.dbReadyValue), true);
    }
    public boolean firstrun() {
        return getSharedPreferences("FIRST", MODE_PRIVATE).getBoolean(getString(R.string.firstRunValue), true);
    }
    private static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath("GeoStezka");
        return dbFile.exists();
    }
}
