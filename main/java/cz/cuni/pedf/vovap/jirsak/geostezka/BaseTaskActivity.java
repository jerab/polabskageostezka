package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Fogs on 14.5.2017.
 */

public abstract class BaseTaskActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public abstract void SetCurentTask(int ID);
    public abstract int GetCurentTask();

}
