package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Fogs on 13.5.2017.
 */

public class BaseActivity extends Activity {
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_nastenka:
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            case R.id.menu_nastaveni:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_o_app:
                startActivity(new Intent(this, WelcomeActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
