package cz.polabskageostezka.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cz.polabskageostezka.R;
import cz.polabskageostezka.TaskSwipeActivity;

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
    float g = 0, r = 0;
    float correctDirection = 45;
    boolean finishedTask = false;

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
            arrowColor[i].setStroke(15,c);
            Log.d(LOG_TAG," Barvim " + c);
        }
    }


    public void showArrow(float xA, float yA, float xZ, float yZ){
        Point ori =  new Point((int)xA,(int)yA);
        Point tar =  new Point((int)xZ,(int)yZ);
        double smer = GetAngleDegree(ori, tar);
        float smerCil = correctDirection;
        float odchylka = 0;
        Log.d(LOG_TAG,"smer tahu : " + smer);
            // zamena za hodnoty v objektu SwipeTasku?
        if (smer > smerCil-10 && smer <smerCil+10) {
            g=255;
            r=0;
            // correct = zelena sipka- leva dolni 135
            Log.d(LOG_TAG," vysledek correct ");
            this.setRotation((float)smer);
            InitDB db = new InitDB(this.getContext());
            try {
                db.open();
                db.zapisTaskDoDatabaze(this.getIdUlohy(),System.currentTimeMillis());
                db.close();
            } catch (Exception e) {
                Log.d(LOG_TAG,"db error");
            }
            ((TaskSwipeActivity) mContext).showResultDialog(true, ((TaskSwipeActivity) mContext).st.getNazev(), ((TaskSwipeActivity) mContext).st.getResultTextOK(), false);
            ((TaskSwipeActivity) mContext).back.setVisibility(View.VISIBLE);
            setFinal();
        } else {
            if (smer>180+smerCil){
                // presah pres 180 od cile = jdi od pocatku (+45)
                odchylka = (float)smer-smerCil-35;
                nastavBarvu(odchylka);
            } else if (smer>smerCil) {
                // odchylka za cilem
                odchylka = (float)smer-smerCil;
                nastavBarvu(odchylka);
            } else if (smer<smerCil){
                //odchylka pred cilem
                odchylka = smerCil-(float)smer;
                nastavBarvu(odchylka);
            }
            this.setRotation((float)smer);
            //((TaskSwipeActivity) mContext).showResultDialog(false, ((TaskSwipeActivity) mContext).st.getNazev(), ((TaskSwipeActivity) mContext).st.getResultTextNO(), false);
            Toast.makeText(mContext,((TaskSwipeActivity) mContext).st.getResultTextNO(),Toast.LENGTH_SHORT).show();
        }

        prebarviSipku(Color.rgb((int)(r),(int)(g),0));
        Log.d(LOG_TAG, "r: " + (int)r + " | g: " + (int)g);
        Log.d(LOG_TAG, "barva: " + Color.rgb((int)(r),(int)(g),0));
    }

    private void nastavBarvu(float odchylka) {
        Log.d(LOG_TAG,"hodnota odchylky: " + odchylka);
        r = odchylka/180;
        r = 255*r;
        r = (int)r;
        g = 255 - r;
        Log.d(LOG_TAG,"hodnota po prepoctu g: " + g + " hodnota po prepoctu r: " + r);
    }

    public static double GetAngleDegree(Point origin, Point target) {
        double n = 180 + (Math.atan2(origin.y - target.y, origin.x - target.x)) * 180 / Math.PI;
        return n % 360;
    }
    public void setFinal() {
        this.setRotation(correctDirection);
        this.setVisibility(VISIBLE);
        prebarviSipku(Color.GREEN);
        this.setClickable(false);
		this.setOnTouchListener(null);
    }
}
