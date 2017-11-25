package cz.polabskageostezka.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Created by tomason on 13.09.2017.
 */

public class DashboardAdapter extends BaseAdapter {
	private Context c;
	private DashboardButton[] items;

	public DashboardAdapter(Context c, DashboardButton[] items) {
		this.c = c;
		this.items = items;
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
		Log.d("GEO log - adapter", pos + " pozice");
		/*if(view == null) {
			Log.d("GEO log - adapter", "new butt " + this.items[pos].toString());
			DashboardButton butt = this.items[pos];
			butt.setLayoutParams(this.items[pos].getLayoutParams());
			return butt;
		}else {
			Log.d("GEO log - adapter", "recycled");
			return view;
		}*/
		DashboardButton butt = this.items[pos];
		butt.setLayoutParams(this.items[pos].getLayoutParams());
		return butt;
	}
}
