package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;

public class GridTaskItemLayout extends LinearLayout {

	private int mPosition;
	private ImageView obrazek;

	// Public constructor
	public GridTaskItemLayout(Context context) {
		super(context);
		Log.d("Geo - GridTask ItemLay", "new ITEM");
	}

	// Public constructor
	public GridTaskItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d("Geo - GridTask ItemLay", "new ITEM");
	}

	/**
	 * Set the position of the view cell
	 * @param position
	 */
	public void setPosition(int position) {
		mPosition = position;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		obrazek = (ImageView) findViewById(R.id.gtItem_image);
		// Do not calculate max height if column count is only one
		int w = getMeasuredWidth();
		w = obrazek.getMeasuredWidth();

		//Log.d("Geo - GridTask ItemLay", "W | H : " + getMeasuredWidth()+ " | " + getMeasuredHeight());
		//Log.d("Geo - GridTask ItemLay", "Obr w | h : " + obrazek.getMeasuredWidth()+ " | " + obrazek.getMeasuredHeight());

		//setMeasuredDimension(w, w);
		LayoutParams lp = (LayoutParams) obrazek.getLayoutParams();
		lp.width = w;
		lp.height = w;
		obrazek.setLayoutParams(lp);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}
}
