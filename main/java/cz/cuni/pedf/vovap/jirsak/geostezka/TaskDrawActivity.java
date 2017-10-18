package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DrawTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.DrawTaskCanvas;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;

public class TaskDrawActivity extends BaseTaskActivity {
    private static final String LOG_TAG = "GEO TaskDrawActivity";
    DrawTask dt;
    InitDB db;
    boolean finished;
    DrawTaskCanvas canvas;

	//ImageView bckImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 7);
        dt = (DrawTask) Config.vratUlohuPodleID(predaneID);
        super.init(dt.getNazev(), dt.getZadani());

		setContentView(dt.getLayout());
		LinearLayout bck = (LinearLayout) findViewById(R.id.dtBckImage);
		bck.setBackgroundResource(dt.getBckFinal());

		canvas = (DrawTaskCanvas) findViewById(R.id.dtCanvas);
		canvas.setBtmResourceId(dt.getBckStart());


		db = new InitDB(this);
        db.open();
        if (db.vratStavUlohy(dt.getId()) == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(dt.getId());
			UkazZadani(dt.getNazev(), dt.getZadani());
		}else if(db.vratStavUlohy(dt.getId()) == Config.TASK_STATUS_DONE) {
            finished = true;
        }
        db.close();
    }

    @Override
    public void runFromResultDialog(boolean result, boolean closeTask) {

    }
}
