package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskSwipeActivity;

/**
 * Created by Fogs on 11.10.2017.
 */

public class SwipeTaskArrow extends View {

    private static final String LOG_TAG = "GEO SwipeTaskArrow";

    Context mContext;
    float startx;
    float starty;
    private int idUlohy;
    RotateDrawable[] arrowColor2;
    GradientDrawable[] arrowColor;

    public int getIdUlohy() {
        return idUlohy;
    }

    public void setIdUlohy(int idUlohy) {
        this.idUlohy = idUlohy;
    }



    public SwipeTaskArrow(Context context) {
        super(context);
        mContext = context;
        init();
    }
    public SwipeTaskArrow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        Log.d(LOG_TAG, "Vytvarim sipku");
        this.setVisibility(VISIBLE);
        /*
        arrowColor = new GradientDrawable[3];
        LayerDrawable aBg = (LayerDrawable) this.getBackground();
        arrowColor[0] = (GradientDrawable)
                aBg.findDrawableByLayerId(R.id.stPrvni);
        Log.d(LOG_TAG," set array with " + aBg.findDrawableByLayerId(R.id.stPrvni).toString());
        Log.d(LOG_TAG," value of array " + arrowColor[0].toString());
        arrowColor[1] = (GradientDrawable)
                aBg.findDrawableByLayerId(R.id.stDruhy);
        Log.d(LOG_TAG," set array with " + aBg.findDrawableByLayerId(R.id.stDruhy).toString());
        Log.d(LOG_TAG," value of array " + arrowColor[1].toString());
        arrowColor[2] = (GradientDrawable)
                aBg.findDrawableByLayerId(R.id.stTreti);
        Log.d(LOG_TAG," set array with " + aBg.findDrawableByLayerId(R.id.stTreti).toString());
        Log.d(LOG_TAG," value of array " + arrowColor[2].toString());
        prebarviSipku(Color.TRANSPARENT);*/
        LayerDrawable layers = (LayerDrawable) this.getBackground();
        arrowColor2 = new RotateDrawable[] {(RotateDrawable) layers.findDrawableByLayerId(R.id.stPrvni), (RotateDrawable) layers.findDrawableByLayerId(R.id.stDruhy), (RotateDrawable) layers.findDrawableByLayerId(R.id.stTreti)};

        arrowColor = new GradientDrawable[3];
            for (int i = 0; i<3;i++){
                arrowColor[i] = (GradientDrawable) arrowColor2[i].getDrawable();
            }
        prebarviSipku(Color.TRANSPARENT);

    }
    private void prebarviSipku(int c){
        for (int i=0;i<3;i++){
            arrowColor[i].setStroke(10,c);
            Log.d(LOG_TAG," Barvim");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(LOG_TAG, " typ: " + event.getAction());
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                startx = x;
                starty = y;
                break;
            case MotionEvent.ACTION_MOVE :
                break;
            case MotionEvent.ACTION_UP :
                showArrow(startx, starty, x, y);
                Log.d(LOG_TAG,"Zacatek X = " + startx + " // Zacatek Y = " + starty + " // konec X = " + x + " // konec Y = " + y);
                invalidate();
                break;
        }

        return true;
        //return super.onTouchEvent(event);
    }

    private void showArrow(float xA, float yA, float xZ, float yZ){
        if (xZ < xA && yZ > yA) {
            // correct = zelena sipka- leva dolni 135
            Log.d(LOG_TAG," vysledek 135 ");
            this.setRotation(135);
            prebarviSipku(Color.GREEN);
            InitDB db = new InitDB(this.getContext());
            try {
                db.open();
                db.zapisTaskDoDatabaze(this.getIdUlohy(),System.currentTimeMillis());
                db.close();
            } catch (Exception e) {
                Log.d(LOG_TAG,"db error");
            }
            TaskSwipeActivity tsa = (TaskSwipeActivity) mContext;
            tsa.runFromResultDialog(true,true);
            //this.setOnTouchListener(null);

        } else if (xZ > xA && yZ < yA) {
            // wrong = ruda sipka - prava horni 315
            Log.d(LOG_TAG," vysledek 315 ");
            this.setRotation(315);
            this.setVisibility(VISIBLE);
            prebarviSipku(Color.RED);
        } else if (xZ < xA && yZ < yA) {
            // wrong = ruda sipka - leva horni 225
            Log.d(LOG_TAG," vysledek 225 ");
            this.setRotation(225);
            this.setVisibility(VISIBLE);
            prebarviSipku(Color.RED);
        } else if (xZ > xA && yZ > yA) {
            Log.d(LOG_TAG," vysledek 45 ");
        // wrong = ruda sipka - prava dolni 45
            this.setRotation(45);
        this.setVisibility(VISIBLE);
        prebarviSipku(Color.RED);
    }

    }
    public void setFinal() {
        this.setRotation(135);
        this.setVisibility(VISIBLE);
        prebarviSipku(Color.GREEN);
        /*this.setOnTouchListener(null);*/
    }
}
