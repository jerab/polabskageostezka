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
        super.init(st.getNazev(), st.getZadani());

		db = new InitDB(this);
        db.open();
        if (db.vratStavUlohy(st.getId()) == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(st.getId());
			UkazZadani(st.getNazev(), st.getZadani());
		}else if(db.vratStavUlohy(st.getId()) == Config.TASK_STATUS_DONE) {
            finished = true;
        }
        db.close();

        canvas = (SwipeTaskCanvas) findViewById(R.id.canvas);
    }

    @Override
    public void runFromResultDialog(boolean result, boolean closeTask) {

    }
}
