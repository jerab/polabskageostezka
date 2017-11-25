package cz.polabskageostezka.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;

/**
 * Created by tomason on 24.09.2017.
 */

public class ImageAndDensityHelper {

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixelRadiusOfCorner, boolean borderAround) {
		//Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		if(w > h) {
			w = h;
		}else {
			h = w;
		}
		Log.d("Geo ROUNDING", "new w|h: " + w + " | " + h);
		Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff420042;
		final Paint paint = new Paint();
		int x = 0;
		int y = 0;
		if(bitmap.getWidth() > w) {
			x = (bitmap.getWidth() - w) / 2;
		}
		if(bitmap.getHeight() > h) {
			y = (bitmap.getHeight() - h) / 2;
		}


		Log.d("Geo ROUNDING", "x|y: " + x + " | " + y);
		//final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		final float roundPx = getDensityDependSize(BaseApp.getInstance().getResources(), pixelRadiusOfCorner);

		final Rect sRect = new Rect(x, y, bitmap.getWidth() - x, bitmap.getHeight() - y);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, sRect, rect, paint);
/*
		if(borderAround) {
			Paint cPaint = new Paint();
			cPaint.setColor(Color.WHITE);
			cPaint.setStyle(Paint.Style.STROKE);
			cPaint.setStrokeWidth(2);
			canvas.drawCircle(w/2, h/2, (float) (w/2), cPaint);
		}
*/
		return output;
	}

	public static Bitmap getBitmapFromDrawable(Resources r, int drawable) {
		return BitmapFactory.decodeResource(r, drawable);
	}

	public static int getDensityDependSize(Resources r, int densityValue) {
		//return (int) (r.getDisplayMetrics().density * densityValue + 0.5f);
		Log.d("IMAGEANDDENSITY HELPER", " scale density: " + r.getDisplayMetrics().density);
		Log.d("IMAGEANDDENSITY HELPER", " density value: " + densityValue);
		Log.d("IMAGEANDDENSITY HELPER", "jina metoda: " + (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityValue, r.getDisplayMetrics()));
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityValue, r.getDisplayMetrics());
	}

	public static int getDensityDependSize(Resources r, int existingPxValue, int plusDensityValue) {
		return existingPxValue + getDensityDependSize(r, plusDensityValue);
	}

	public static int getTextDensityDependSize(Resources r, int densityValue) {
		Log.d("IMAGEANDDENSITY HELPER", " scale density: " + r.getDisplayMetrics().scaledDensity);
		Log.d("IMAGEANDDENSITY HELPER", " density value: " + densityValue);
		Log.d("IMAGEANDDENSITY HELPER", "jina metoda: " + (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, densityValue, r.getDisplayMetrics()));
		return (int) (r.getDisplayMetrics().scaledDensity * densityValue + 0.5f);
	}
	public static int getTextDensityDependSize(Resources r, int existingPxValue, int plusDensityValue) {
		return existingPxValue + getTextDensityDependSize(r, plusDensityValue);
	}
}
