package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends Activity {
    TextView scrollView;
    Button btnContinue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        scrollView = (TextView) findViewById(R.id.tvObsah);
        scrollView.setMovementMethod(new ScrollingMovementMethod());
        btnContinue = (Button) findViewById(R.id.btnZacit);
        if (firstrun()) {
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.firstRunValue), false);
                    editor.apply();*/
                    getSharedPreferences("FIRST", MODE_PRIVATE).edit().putBoolean(getString(R.string.firstRunValue), false).apply();
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

    public boolean firstrun() {
        return getSharedPreferences("FIRST", MODE_PRIVATE).getBoolean(getString(R.string.firstRunValue), true);
    }
}
