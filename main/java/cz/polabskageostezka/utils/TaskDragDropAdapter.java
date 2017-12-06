package cz.polabskageostezka.utils;

import android.content.Context;
import android.content.res.Resources;
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

import cz.polabskageostezka.R;
import cz.polabskageostezka.TaskDragDropActivity;

/**
 * Created by tomason on 13.09.2017.
 */

public class TaskDragDropAdapter extends BaseAdapter {
	private static final String LOG_TAG = "Geo - TaskDDAdapter";
	private static int IMAGE_RADIUS = 0;
	private Context c;
	private ArrayList<ImageView> items;

	public TaskDragDropAdapter(Context c, ArrayList<ImageView> items) {
		this.c = c;
		this.items = items;
		Collections.shuffle(Arrays.asList(items));
		Log.d("GEO - NEW TaskDD adp", "pocet polozek: " + items.size());
	}

	public static final int getImageRadius() {
		if(IMAGE_RADIUS == 0) {
			Resources r = BaseApp.getInstance().getResources();
			IMAGE_RADIUS = ImageAndDensityHelper.getDensityDependSize(r, (int) r.getDimension(R.dimen.dimTaskDragDrop_sourceImg_width));
		}
		return IMAGE_RADIUS;
	}

	public void removeItem(int position){
		Log.d(LOG_TAG, "Removing item: " + position);
		items.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.items.size();
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
		Log.d(LOG_TAG, "GetView... pozice: " + pos);
		ImageView butt;
		/// musime vzdy prekreslit, protoze nacitame cely objekt - kvuli skrolovani
		butt = this.items.get(pos);
		Bitmap bm = ((BitmapDrawable)butt.getDrawable()).getBitmap();
		butt.setImageBitmap(ImageAndDensityHelper.getRoundedCornerBitmap(bm, getImageRadius(), true));
		Log.d(LOG_TAG, "width: " + butt.getWidth());
		return butt;
	}

	@Override
	public void notifyDataSetChanged() {
		Log.d(LOG_TAG, "Data set Changed " + getCount());
		super.notifyDataSetChanged();
		notifyDataSetInvalidated();
	}

	@Override
	public void notifyDataSetInvalidated() {
		Log.d(LOG_TAG, "Invalidate..."  + getCount());
		super.notifyDataSetInvalidated();
	}
}
