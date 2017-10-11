package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Fogs on 11.10.2017.
 */

public class SwipeTaskCanvas extends View {

    private static final String LOG_TAG = "GEO SwipeTaskCanvas";
    Paint mPaint;
    Bitmap mBitmapa;
    Path cesta;
    Canvas mCanvas;
    int width;
    int height;
    float startx;
    float starty;

    public SwipeTaskCanvas(Context context) {
        super(context);
        init();
    }
    public SwipeTaskCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);

        cesta = new Path();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmapa = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        width = w;
        height = h;
        mCanvas = new Canvas(mBitmapa);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawRect(10, 10, 200, 50, mPaint);
        canvas.drawPath(cesta, mPaint);
        canvas.drawBitmap(mBitmapa, 0, 0, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(LOG_TAG, " typ: " + event.getAction());
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                mBitmapa = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                startx = x;
                starty = y;
                cesta.reset();
                cesta.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE :
                cesta.lineTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                cesta.lineTo(x, y);
                fillArrow(mCanvas, startx, starty, x, y);
                mCanvas.drawPath(cesta, mPaint);
                invalidate();
                break;
        }

        return true;
        //return super.onTouchEvent(event);
    }


    public void vymazatObsah() {
        cesta.reset();
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    private void fillArrow(Canvas canvas, float x0, float y0, float x1, float y1) {

        mPaint.setStyle(Paint.Style.FILL);

        float deltaX = x1 - x0;
        float deltaY = y1 - y0;
        float frac = (float) 0.1;

        float point_x_1 = x0 + (float) ((1 - frac) * deltaX + frac * deltaY);
        float point_y_1 = y0 + (float) ((1 - frac) * deltaY - frac * deltaX);

        float point_x_2 = x1;
        float point_y_2 = y1;

        float point_x_3 = x0 + (float) ((1 - frac) * deltaX - frac * deltaY);
        float point_y_3 = y0 + (float) ((1 - frac) * deltaY + frac * deltaX);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        path.moveTo(point_x_1, point_y_1);
        path.lineTo(point_x_2, point_y_2);
        path.lineTo(point_x_3, point_y_3);
        path.lineTo(point_x_1, point_y_1);
        path.lineTo(point_x_1, point_y_1);
        path.close();

        canvas.drawPath(path, mPaint);
    }
}
