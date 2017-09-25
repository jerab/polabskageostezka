package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;

/**
 * Created by tomason on 13.09.2017.
 */

public class TaskDragDropAdapter extends BaseAdapter {
	private Context c;
	private ImageView[] items;

	public TaskDragDropAdapter(Context c, ImageView[] items) {
		this.c = c;
		this.items = items;
		Collections.shuffle(Arrays.asList(items));
		Log.d("GEO - NEW TaskDD adp", "pocet polozek: " + items.length);
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
		Log.d("GEO - TaskDD adapter", pos + " pozice");
		ImageView butt;
		if(view == null) {
			Log.d("GEO - TaskDD adapter", "nova polozka ImageView");
			butt = this.items[pos];
		}else {
			Log.d("GEO - TaskDD adapter", "recycled");
			butt = (ImageView) view;
		}
		Bitmap bm = ((BitmapDrawable)butt.getDrawable()).getBitmap();
		butt.setImageBitmap(RoundImageHelper.getRoundedCornerBitmap(bm, RoundImageHelper.DRAG_DROP_IMG_RADIUS, true));
		butt.setBackgroundResource(R.drawable.ic_round_border_24dp);
		return butt;
	}
}
