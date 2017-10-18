package cz.cuni.pedf.vovap.jirsak.geostezka.utils;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.widget.GridView;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskCamActivity;

/**
 * Created by tomason on 13.09.2017.
 */

public class DashboardButton extends RelativeLayout {

	private static final String LOG_TAG = "Geo DashboardButton";
	Context parentContext;
	/**
	 *  0 - not open; 1 - open; 2 - finished
	 */
	public int taskStatus = 0;
	public int taskId = 0;
	public int taskTyp = 0;
	ImageView inButt;
	TextView inLabel;
	ImageView inStatus;

	private boolean isIntroTask;

	public DashboardButton(Context context, String popisek, int typ, int status, int id, boolean introTasks) {
		super(context, null, R.style.GeoThemeDashboardButton);
		this.parentContext = context;
		/// params for Grid view where the buttons are added
		Resources r = getResources();
		int w;
		this.isIntroTask = introTasks;
		if(introTasks) {
			w = (int)r.getDimension(R.dimen.dimTaskButtIntroWidth);
			//w = ImageAndDensityHelper.getTextDensityDependSize(r, (int)r.getDimension(R.dimen.dimTaskButtIntroWidth));// - 30));
			Log.d(LOG_TAG, "Width/Height intro: " + w);
		}else {
			w = (int)r.getDimension(R.dimen.dimTaskButtMainWidth);
			// w = ImageAndDensityHelper.getTextDensityDependSize(r, (int)r.getDimension(R.dimen.dimTaskButtMainWidth));//));
			Log.d(LOG_TAG, "Width/Height: " + w);
		}

		this.setLayoutParams(new GridView.LayoutParams(w,w));
		this.setBackgroundResource(android.R.color.transparent);

		LayoutInflater.from(context).inflate(R.layout.dashboard_button, this, true);

		RelativeLayout rl = (RelativeLayout) getChildAt(0);
		this.inButt = (ImageView) rl.getChildAt(0);
		this.inLabel = (TextView) rl.getChildAt(1);
		this.inStatus = (ImageView) getChildAt(1);
		if(introTasks) {
			RelativeLayout.LayoutParams params = (LayoutParams) inStatus.getLayoutParams();
			//params.width = ImageAndDensityHelper.getTextDensityDependSize(r, (int)r.getDimension(R.dimen.dimTaskStatusImgIntroWidth));
			params.width = (int)r.getDimension(R.dimen.dimTaskStatusImgIntroWidth);
			params.height = params.width;
			inStatus.setLayoutParams(params);
		}

		this.inLabel.setText(popisek);
		this.taskId = id;
		this.taskTyp = typ;
		this.taskStatus = status;

		this.checkStatus();

		/*inButt.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				Log.d("Geo - TOUCH EVENT", " is " + motionEvent.getAction());
				switch (motionEvent.getAction()) {
					case MotionEvent.ACTION_CANCEL:
						setImageByStatus();
						break;
					case MotionEvent.ACTION_DOWN:
						((ImageView) view).setImageResource(R.drawable.ic_stanoviste_bck1_down);
						break;
				}
				return false;
			}

		});*/

		setClickListenerToButton();
	}

	private void setClickListenerToButton() {
		//if(parentContext instanceof DashboardActivity){
			Log.d("GEO DbButton", "setting click listener for task button: " + this.taskId);
			inButt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(LOG_TAG, "Listener - clicking: " + taskId);
					if(taskStatus < 0) {
						Toast.makeText(parentContext, "Úlohu můžete otevřít pomocí načtení QR kódu", Toast.LENGTH_SHORT).show();
						setImageByStatus();
					}else {
						((DashboardActivity) parentContext).startTask(taskId, taskTyp);
					}
				}
			});
		//}
	}

	public void setStatus(int status) {
		this.taskStatus = status;
		this.checkStatus();
	}

	private void setImageByStatus() {
		Log.d(LOG_TAG, "setting...by task status: " + this.taskStatus);
		switch (this.taskStatus) {
			// otevreno
			case 1 :
				this.inButt.setImageResource(R.drawable.ic_stanoviste_bck2);
				break;
			// splneno
			case 2 :
				this.inButt.setImageResource(R.drawable.ic_stanoviste_bck2);
				break;
			// nenavstiveno
			case 0 :
			default:
				this.inButt.setImageResource(R.drawable.ic_stanoviste_bck_empty);
				break;
		}
	}

	public void checkStatus() {
		Log.d(LOG_TAG, "id/status: " + this.taskId + "/" + this.taskStatus);
		switch (this.taskStatus) {
			// otevreno
			case 1 :
				this.inStatus.setImageResource(R.drawable.ic_status_opened);
				setImageByStatus();
				this.inLabel.setTextColor(ContextCompat.getColor(parentContext, R.color.colorTextTmava2));
				break;
			// splneno
			case 2 :
				this.inStatus.setImageResource(R.drawable.ic_check_ok);
				setImageByStatus();
				this.inLabel.setTextColor(ContextCompat.getColor(parentContext, R.color.colorTextTmava2));
				break;
			// nenavstiveno
			case 0 :
			default:
				//this.inStatus.setImageResource(android.R.color.transparent);
				//setImageByStatus();
				//this.inLabel.setTextColor(ContextCompat.getColor(parentContext, R.color.colorTextTmava));
				break;
		}
	}

/*
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = getResources().getDisplayMetrics().widthPixels;
		GridView.LayoutParams p = (GridView.LayoutParams)this.getLayoutParams();
		int fullW = p.width + this.getPaddingLeft() + this.getPaddingRight();
		Log.d(LOG_TAG, "MESUARE w, p.width, this w, full w, display w " + MeasureSpec.getSize(widthMeasureSpec) + ", " + p.width + ", " + this
				.getWidth() + " , " +
				fullW +	" , " + 	w);

		if(isIntroTask) {
			// we have 2 cols => padding Start and End + space between + extra
			/// new width / old width * num of Cols
			float scale = (float)(w - 160) / (fullW * 2 );
			int colwid = ImageAndDensityHelper.getTextDensityDependSize(parentContext.getResources(), (int)parentContext.getResources().getDimension(R.dimen
					.dimTaskButtIntroWidth));
			if( scale >= 1 && colwid < fullW + 20) {
				scale = colwid / fullW;
			}
			if( scale < 1) {
				p.width = (int)(p.width * scale);
				p.height = p.width;
				this.setLayoutParams(p);
				Log.d(LOG_TAG, "id:" + this.taskId + ": scale / set new Size: " + scale + " / " + p.width);

				RelativeLayout.LayoutParams pi = (RelativeLayout.LayoutParams) inStatus.getLayoutParams();
				pi.width = (int)(pi.width * scale);
				pi.height = pi.width;
				this.inStatus.setLayoutParams(pi);

				widthMeasureSpec = MeasureSpec.makeMeasureSpec(p.width, MeasureSpec.EXACTLY);
				heightMeasureSpec = widthMeasureSpec;
			}
		}else {
			// we have 5 cols => padding Start and End + space between + extra
			/// new width / old width * num of Cols
			//float scale = (float)(w - 160) / (fullW * 5 );
			float scale = w / fullW;
			int colwid = ImageAndDensityHelper.getTextDensityDependSize(parentContext.getResources(), (int)parentContext.getResources().getDimension(R.dimen
					.dimTaskButtMainWidth));
			Log.d(LOG_TAG, "FULLW:" + fullW + ", COLWID: " + colwid);
			if( scale >= 1 && colwid < fullW + 20) {
				scale = colwid / fullW;
			}
			Log.d(LOG_TAG, "SCALE:" + scale);
			if( scale < 1 ) {
				p.width = (int)(p.width * scale);
				p.height = p.width;
				this.setLayoutParams(p);
				Log.d(LOG_TAG, "id:" + this.taskId + ": scale / set new Size: " + scale + " / " + p.width);

				RelativeLayout.LayoutParams pi = (RelativeLayout.LayoutParams) inStatus.getLayoutParams();
				pi.width = (int)(pi.width * scale);
				pi.height = pi.width;
				this.inStatus.setLayoutParams(pi);

				widthMeasureSpec = MeasureSpec.makeMeasureSpec(p.width, MeasureSpec.EXACTLY);
				heightMeasureSpec = widthMeasureSpec;
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	*/
}
