package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskGridActivity;


/**
 * Created by tomason on 07.11.2017.
 */

public class GridTaskAdapter extends BaseAdapter {
	private static final String LOG_TAG = "Geo - GridTask";
	private static int IMAGE_RADIUS = 0;
	private Context c;
	private ArrayList<GridTaskItem> items;

	public GridTaskAdapter(Context c, ArrayList<GridTaskItem> items) {
		this.c = c;
		this.items = items;
		Log.d(LOG_TAG, "pocet polozek: " + items.size());
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
		Log.d(LOG_TAG, pos + " pozice");
		GridTaskItem item = items.get(pos);
		if(item == null) {
			return null;
		}
		/*
		View it;
		if(view == null) {
			Log.d(LOG_TAG, "nova polozka");
			LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			it = inflater.inflate(R.layout.gridtask_item, null);
		}else {
			Log.d(LOG_TAG, "recycled");
			it = view;
		}*/
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View it = inflater.inflate(R.layout.gridtask_item, null);

		ImageView itImg = (ImageView) it.findViewById(R.id.gtItem_image);
		itImg.setImageResource(item.getObrazek());
		TextView itText = (TextView) it.findViewById(R.id.gtItem_text);
		itText.setText(item.getText());
		if (item.isSpravne()) {
			it.setTag(TaskGridActivity.VIEW_TAG_CORRECT);
		} else {
			it.setTag("null");
		}
		//it.measure();
		int w = View.MeasureSpec.makeMeasureSpec(260, View.MeasureSpec.EXACTLY);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		it.requestLayout();
		it.measure(w, h);
		return it;
	}

	/**
	 * Run a pass through each item and force a measure to determine the max height for each row
	 */
	public void measureItems(int columnWidth) {
		// Obtain system inflater
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// Inflate temp layout object for measuring
		GridTaskItemLayout itemView = (GridTaskItemLayout) inflater.inflate(R.layout.gridtask_item, null);

		// Create measuring specs
		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(columnWidth, View.MeasureSpec.EXACTLY);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

		// Loop through each data object
		for(int index = 0; index < items.size(); index++) {
			GridTaskItem item = items.get(index);

			// Set position and data
			itemView.setPosition(index);
			//itemView.updateItemDisplay(item, mLanguage);
			//itemView.update
			//item.getObrazek()
			// Force measuring
			itemView.requestLayout();
			itemView.measure(widthMeasureSpec, heightMeasureSpec);
		}
		itemView.requestLayout();
		itemView.measure(widthMeasureSpec, heightMeasureSpec);
	}
}
