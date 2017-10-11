package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.SwipeTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.SwipeTaskCanvas;

public class TaskSwipeActivity extends BaseTaskActivity {
    private static final String LOG_TAG = "GEO TaskSwipeActivity";
    SwipeTask st;
    InitDB db;
    boolean finished;
    SwipeTaskCanvas canvas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_swipe);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 7);
        st = (SwipeTask) Config.vratUlohuPodleID(predaneID);
        db = new InitDB(this);
        db.open();
        if (db.vratStavUlohy(st.getId())==0)
            db.odemkniUlohu(st.getId());
        else if(db.vratStavUlohy(st.getId())==2) {
            finished = true;
        }
        db.close();
        UkazZadani(st.getNazev(), st.getZadani());
        canvas = (SwipeTaskCanvas) findViewById(R.id.canvas);
    }

    @Override
    public void runFromResultDialog(boolean result, boolean closeTask) {

    }
}
