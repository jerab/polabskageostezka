package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;

import static cz.cuni.pedf.vovap.jirsak.geostezka.TaskCamActivity.UkazZadani;

public class TaskDragDropActivity extends BaseTaskActivity {
    DragDropTask dd;
    LinearLayout llDD;
    int[] obrazky;
    ImageView[] ivs;
    Random r = new Random();
    float f = r.nextFloat(),g = r.nextFloat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_drag_drop);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        dd = (DragDropTask) Config.vratUlohuPodleID(predaneID);
        UkazZadani(this, dd.getNazev(), dd.getZadani());

        obrazky = dd.getBankaObrazku();
        llDD = (LinearLayout) findViewById(R.id.llDD);
        llDD.setBackground(getResources().getDrawable(obrazky[0]));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        ivs = new ImageView[obrazky.length];
        for (int i = 1; i < obrazky.length;i++)
        {
            ivs[i] = new ImageView(this);
            ivs[i].setImageResource(obrazky[i]);
            ivs[i].setX(f);
            ivs[i].setY(g);
            ivs[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //Log.d("GEO", "typ: " + event.getAction());
                    float x = event.getX();
                    float y = event.getY();
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN :
                            Log.d("GEO", "pozice x: " + String.valueOf(x) + " y: " + y);
                            //v.setX(x);
                            //v.setY(y);
                            break;
                        case MotionEvent.ACTION_MOVE :
                            Log.d("GEO", "pozice x: " + String.valueOf(x) + " y: " + y);
                            v.setY(y);
                            v.setX(x);
                            break;
                        case MotionEvent.ACTION_UP :
                            Log.d("GEO", "pozice x: " + String.valueOf(x) + "y: " + y);
                            break;
                    }
                    return true;
                }
            });
            ivs[i].setLayoutParams(layoutParams);
            llDD.addView(ivs[i]);
        }
    }

    
}
