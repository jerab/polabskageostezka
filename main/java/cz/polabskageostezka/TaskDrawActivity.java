package cz.polabskageostezka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cz.polabskageostezka.tasks.DrawTask;
import cz.polabskageostezka.utils.BaseTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.DrawTaskCanvas;
import cz.polabskageostezka.utils.InitDB;

public class TaskDrawActivity extends BaseTaskActivity {
    private static final String LOG_TAG = "GEO TaskDrawActivity";
    public DrawTask dt;
    InitDB db;
    public boolean finished;
    DrawTaskCanvas canvas;
    public ImageView back;

	//ImageView bckImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 7);
        dt = (DrawTask) Config.vratUlohuPodleID(predaneID);
        super.init(dt.getNazev(), dt.getZadani(), dt.getId());
		setContentView(dt.getLayout());
		RelativeLayout bck = (RelativeLayout) findViewById(R.id.dtBckImage);
		bck.setBackgroundResource(dt.getBckFinal());
        back = (ImageView)findViewById(R.id.confirmTask);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(TaskDrawActivity.this,DashboardActivity.class));
				runNextQuest(dt.getRetezId(), TaskDrawActivity.this);
				finish();
            }
        });
		canvas = (DrawTaskCanvas) findViewById(R.id.dtCanvas);
		canvas.setBtmResourceId(dt.getBckStart());
        canvas.setCtx(this);

		db = new InitDB(this);
        db.open();
        if (db.vratStavUlohy(dt.getId()) == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(dt.getId());
			UkazZadani(dt.getNazev(), dt.getZadani());
		}else if(db.vratStavUlohy(dt.getId()) == Config.TASK_STATUS_DONE) {
            finished = true;
            //canvas.setVisibility(View.INVISIBLE);
            canvas.finishTask();
        }
        db.close();
    }

    public boolean isFinished(){return finished;}

    @Override
    public void runFromResultDialog(boolean result, boolean closeTask) {
        if(result) {
            /// bylo pouze zobrazeni spravne odpovedi
            if(closeTask) {
                startActivity(new Intent(TaskDrawActivity.this, DashboardActivity.class));
                finish();
            }
        }else {
            Log.d(LOG_TAG, "FAULT RESULT do nothing");
        }

    }

	@Override
	public void runFromStartTaskDialog() {

	}
}
