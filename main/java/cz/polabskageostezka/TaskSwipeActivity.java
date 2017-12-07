package cz.polabskageostezka;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import cz.polabskageostezka.tasks.SwipeTask;
import cz.polabskageostezka.utils.BaseTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.InitDB;
import cz.polabskageostezka.utils.SwipeTaskArrow;

public class TaskSwipeActivity extends BaseTaskActivity {
    private static final String LOG_TAG = "GEO TaskSwipeActivity";
    public SwipeTask st;
    InitDB db;
    boolean finished;
    SwipeTaskArrow sipka;
    float startx=0, starty=0;
    public ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_swipe);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 7);
        st = (SwipeTask) Config.vratUlohuPodleID(predaneID);
        super.init(st.getNazev(), st.getZadani(), st.getId());

        db = new InitDB(this);
        db.open();
        if (db.vratStavUlohy(st.getId()) == Config.TASK_STATUS_NOT_VISITED) {
            db.odemkniUlohu(st.getId());
            UkazZadani(st.getNazev(), st.getZadani());
        }else if(db.vratStavUlohy(st.getId()) == Config.TASK_STATUS_DONE) {
            finished = true;
        }
        db.close();
        sipka = (SwipeTaskArrow) findViewById(R.id.sipkaView);
        sipka.setIdUlohy(st.getId());
        //canvas = (SwipeTaskCanvas) findViewById(R.id.canvas);
        back = (ImageView) findViewById(R.id.confirmTask);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TaskSwipeActivity.this,DashboardActivity.class));
            }
        });
        if (finished){
            sipka.setFinal();
            sipka.setOnTouchListener(null);
            back.setVisibility(View.VISIBLE);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!this.isFinished()) {
            //Log.d(LOG_TAG, " typ: " + event.getAction());
            float x = event.getRawX();
            float y = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startx = x;
                    starty = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    this.sipka.showArrow(startx, starty, x, y);
                    Log.d(LOG_TAG, "Zacatek X = " + startx + " // Zacatek Y = " + starty + " // konec X = " + x + " // konec Y = " + y);
                    break;
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void runFromResultDialog(boolean result, boolean closeTask) {
        if(result) {
            /// bylo pouze zobrazeni spravne odpovedi
            if(closeTask) {
                startActivity(new Intent(TaskSwipeActivity.this, DashboardActivity.class));
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
