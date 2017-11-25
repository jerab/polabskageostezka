package cz.polabskageostezka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import cz.polabskageostezka.utils.BaseActivity;
import cz.polabskageostezka.utils.InitDB;
import cz.polabskageostezka.utils.Task;

import static cz.polabskageostezka.utils.Config.vratIntroUlohuPodleID;
import static cz.polabskageostezka.utils.Config.vratPocetUloh;
import static cz.polabskageostezka.utils.Config.vratPocetUlohIntro;
import static cz.polabskageostezka.utils.Config.vratUlohuPodleID;

public class WelcomeActivity extends BaseActivity {
    TextView scrollView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		Log.d("GEO WA","onCreate");
		Intent volano = getIntent();
		if(volano.getAction() == Intent.ACTION_MAIN) {
			if(firstrun()) {
				Log.d("GEO WA onCreate","FIRST RUN");
				init();
			}else {
				Log.d("GEO WA onCreate","start Dashboard");
				Intent intent = new Intent(this, DashboardActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				finish();
				//Toast.makeText(DashboardActivity.this, "First Run", Toast.LENGTH_SHORT).show()
			}
		}else {
			init();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.welcommenu, menu);
		return true;
	}

	public boolean firstrun() {
		return getSharedPreferences("FIRST", MODE_PRIVATE).getBoolean(getString(R.string.firstRunValue), true);
	}

    private void init() {
		Log.d("GEO WA", "init");
		setContentView(R.layout.activity_welcome);
		scrollView = (TextView) findViewById(R.id.tvObsah);
		scrollView.setMovementMethod(new ScrollingMovementMethod());

		progressBar = (ProgressBar) findViewById(R.id.waProgressBar);
		progressBar.setVisibility(View.INVISIBLE);

		/// tlacitko pro overeni lokace
		Button poz = (Button) findViewById(R.id.posit);
		poz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("GEO WA", "clicking Overit button");
				location.setIfShowProviderDialog(true);
				showProgressBar(true);
				location.checkLocationStatusByUser();
			}
		});

		Log.d("GEO Welcome", "Existuje datoska? " + String.valueOf(doesDatabaseExist(this)));
		// prvotni zapis a vytvoreni db
		if (dbSet()) {
			nachystejDB();
			getSharedPreferences("DB", MODE_PRIVATE).edit().putBoolean(getString(R.string.dbReadyValue), false).apply();
			Log.d("GEO Welcome", "Databaze ready");
		}
		//konec db

		//prvni spusteni aplikace
		if (firstrun()) {

		} else {

		}
	}

    private void nachystejDB() {
        // Je treba upravit pokud se zmeni pocet INTRO TASKU!!!
        InitDB db = new InitDB(this);
        try {
            int pocetIntro = vratPocetUlohIntro();
			db.open();
            for (int k = 0;k<pocetIntro;k++){
                Task t = vratIntroUlohuPodleID(k);
                db.zapisTaskDoDatabaze(t.getId(),t.getTyp());
            }
            for (int i = pocetIntro; i<vratPocetUloh() + pocetIntro;i++){
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

    private static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath("GeoStezka");
        return dbFile.exists();
    }

    public void showProgressBar(boolean show) {
		if(show) {
			this.progressBar.setVisibility(View.VISIBLE);
		}else {
			this.progressBar.setVisibility(View.INVISIBLE);
		}
	}
}
