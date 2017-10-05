package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.Collections;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;

/**
 * Created by tomason on 13.09.2017.
 */

public class TaskDragDropAdapter extends BaseAdapter {
	private static final String LOG_TAG = "Geo - TaskDDAdapter";
	private static int IMAGE_RADIUS = 0;
	private Context c;
	private ImageView[] items;

	public TaskDragDropAdapter(Context c, ImageView[] items) {
		this.c = c;
		this.items = items;
		Collections.shuffle(Arrays.asList(items));
		Log.d("GEO - NEW TaskDD adp", "pocet polozek: " + items.length);
	}

	public static final int getImageRadius() {
		if(IMAGE_RADIUS == 0) {
			Resources r = BaseApp.getInstance().getResources();
			IMAGE_RADIUS = ImageAndDensityHelper.getDensityDependSize(r, (int) r.getDimension(R.dimen.dimTaskDragDrop_sourceImg_width));
		}
		return IMAGE_RADIUS;
	}

	@Override
	public int getCount() {
		return this.items.length;
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup viewGroup) {
		Log.d(LOG_TAG, pos + " pozice");
		ImageView butt;
		if(view == null) {
			Log.d(LOG_TAG, "nova polozka ImageView");
			butt = this.items[pos];
		}else {
			Log.d(LOG_TAG, "recycled");
			butt = (ImageView) view;
		}
		Bitmap bm = ((BitmapDrawable)butt.getDrawable()).getBitmap();
		butt.setImageBitmap(ImageAndDensityHelper.getRoundedCornerBitmap(bm, getImageRadius(), true));
		Log.d(LOG_TAG, "width: " + butt.getWidth());
		return butt;
	}
}
