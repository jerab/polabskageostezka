package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


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


    }


}
