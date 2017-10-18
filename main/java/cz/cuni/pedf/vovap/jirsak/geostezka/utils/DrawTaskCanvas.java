package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;

import static android.R.attr.x;
import static android.graphics.Bitmap.createBitmap;

/**
 * Created by Fogs on 11.10.2017.
 */

public class DrawTaskCanvas extends View {

    private static final String LOG_TAG = "GEO DrawTaskCanvas";
	private static final float TOUCH_TOLERANCE = 5;
	Paint mPaint;
	Paint mPaintEr;
	Paint defPaint = new Paint();
    Bitmap mBitmapa;
	Bitmap bckBitmapa;

	int btmResourceId;
    Path cesta;
    Canvas mCanvas;
	boolean start = false;

    public DrawTaskCanvas(Context context) {
        super(context);
        init();
    }
    public DrawTaskCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

	public void setBtmResourceId(int btmResourceId) {
		this.btmResourceId = btmResourceId;
	}

    private void init() {
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        /*mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(30);
        */

		mPaintEr = new Paint();
		mPaintEr.setColor(Color.TRANSPARENT);
		mPaintEr.setStyle(Paint.Style.STROKE);
		mPaintEr.setStrokeCap(Paint.Cap.ROUND);
		mPaintEr.setStrokeJoin(Paint.Join.ROUND);
		mPaintEr.setStrokeWidth(100);
		mPaintEr.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

		cesta = new Path();
		cesta.setFillType(Path.FillType.EVEN_ODD);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmapa = BitmapFactory.decodeResource(getResources(), btmResourceId);
		//mBitmapa = convertToMutable(w, h, mBitmapa);
		//mBitmapa = mBitmapa.copy(Bitmap.Config.ARGB_8888, true);
		mBitmapa = Bitmap.createScaledBitmap(mBitmapa, w, h, false);
		//mBitmapa = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmapa);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
		Log.d(LOG_TAG, "onDraw ...");
		canvas.drawBitmap(mBitmapa, 0, 0, defPaint);
		if(!start) {
			Log.d(LOG_TAG, "onDraw START");
			start = true;
			cesta.reset();
		}else {
			canvas.drawPath(cesta, mPaintEr);
		}
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
		Log.d(LOG_TAG, "typ: " + event.getAction());
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				//this.setVisibility(VISIBLE);
				cesta.moveTo(x, y);
				break;
			case MotionEvent.ACTION_MOVE :
				cesta.lineTo(x, y);
				//mCanvas.drawPath(cesta, mPaint);
				invalidate();
				break;
			case MotionEvent.ACTION_UP :
				//this.setVisibility(INVISIBLE);
				cesta.lineTo(x, y);
				//mCanvas.drawPath(cesta, mPaint);
				invalidate();
				break;
		}

		return true;
        //return super.onTouchEvent(event);
    }

	public static Bitmap convertToMutable(int w, int h, Bitmap imgIn) {
		try {
			//this is the file going to use temporally to save the bytes.
			// This file will not be a image, it will store the raw image data.
			File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

			//Open an RandomAccessFile
			//Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
			//into AndroidManifest.xml file
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

			// get the width and height of the source bitmap.
			int width = imgIn.getWidth();
			int height = imgIn.getHeight();

			//Copy the byte to the file
			//Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*h);
			imgIn.copyPixelsToBuffer(map);
			//recycle the source bitmap, this will be no longer used.
			imgIn.recycle();
			System.gc();// try to force the bytes from the imgIn to be released

			//Create a new bitmap to load the bitmap again. Probably the memory will be available.
			imgIn = createBitmap(w, h, Bitmap.Config.ARGB_8888);
			map.position(0);
			//load it back from temporary
			imgIn.copyPixelsFromBuffer(map);
			//close the temporary file and channel , then delete that also
			channel.close();
			randomAccessFile.close();

			// delete the temp file
			file.delete();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imgIn;
	}
}