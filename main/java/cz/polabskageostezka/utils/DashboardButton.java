package cz.polabskageostezka.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.GridView;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import cz.polabskageostezka.DashboardActivity;
import cz.polabskageostezka.R;

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

	@SuppressLint("ClickableViewAccessibility")
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

		setClickListenerToButton();
	}

	private void setClickListenerToButton() {
		//if(parentContext instanceof DashboardActivity){
			Log.d("GEO DbButton", "setting click listener for task button: " + this.taskId);
			inButt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(LOG_TAG, "Listener - clicking: " + taskId);
					if (taskStatus > Config.TASK_STATUS_NOT_VISITED) {
						((DashboardActivity) parentContext).startTask(taskId, taskTyp);
					}else if(taskId == Config.TASK_SLEPENEC2_ID) {
						Toast.makeText(parentContext, "Tato úloha se ti otevře až po splnění úlohy 5.1", Toast.LENGTH_SHORT).show();
					}else if(taskId >= Config.vratPocetUlohIntro()) {
						Toast.makeText(parentContext, R.string.nacistUlohuPomociQR, Toast.LENGTH_SHORT).show();
					}else if(taskId == 0) {
						//Toast.makeText(parentContext, "Úlohu A otevří pomocí načtení QR kódu na informační tabuli.", Toast.LENGTH_SHORT).show();
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
				//this.inButt.setImageResource(R.drawable.ic_stanoviste_bck);
				this.inButt.setImageResource(R.drawable.db_button_bck_open);
				break;
			// splneno
			case 2 :
				//this.inButt.setImageResource(R.drawable.ic_stanoviste_bck);
				this.inButt.setImageResource(R.drawable.db_button_bck_open);
				break;
			// nenavstiveno
			case 0 :
			default:
				//this.inButt.setImageResource(R.drawable.ic_stanoviste_bck_empty);
				this.inButt.setImageResource(R.drawable.db_button_bck);
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
}
