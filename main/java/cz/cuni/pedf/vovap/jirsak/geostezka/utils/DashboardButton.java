package cz.cuni.pedf.vovap.jirsak.geostezka.utils;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.AttributeSet;
//import android.widget.Button;

import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskCamActivity;

/**
 * Created by tomason on 13.09.2017.
 */

public class DashboardButton extends RelativeLayout {
	/**
	 *  0 - not open; 1 - open; 2 - finished
	 */
	Context parentContext;
	public int taskStatus = 0;
	public int taskId = 0;
	public int taskTyp = 0;
	ImageView inButt;
	TextView inLabel;
	ImageView inStatus;

	public DashboardButton(Context context, String nazev, int typ, int status, int id, boolean introTasks) {
		super(context, null, R.style.GeoThemeDashboardButton);
		/// params for Grid view where the buttons are added
		Resources r = getResources();
		int w;
		if(introTasks) {
			w = (int)r.getDimension(R.dimen.dimTaskButtIntroWidth) - 30;
			this.setLayoutParams(new GridView.LayoutParams(w, w));
		}else {
			w = (int)r.getDimension(R.dimen.dimTaskButtMainWidth) - 10;
			this.setLayoutParams(new GridView.LayoutParams(w, w));
		}

		this.setBackgroundResource(android.R.color.transparent);

		Log.d("GEO DbButton", "width: " + w);

		LayoutInflater.from(context).inflate(R.layout.dashboard_button, this, true);
		this.parentContext = context;

		//RelativeLayout root = (RelativeLayout) getChildAt(0);
		RelativeLayout rl = (RelativeLayout) getChildAt(0);
		this.inButt = (ImageView) rl.getChildAt(0);
		this.inLabel = (TextView) rl.getChildAt(1);
		this.inStatus = (ImageView) getChildAt(1);



		this.inLabel.setText(""+id);
		this.taskId = id;
		this.taskTyp = typ;
		this.taskStatus = status;

		this.checkStatus();

		inButt.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				((ImageView) view).setImageResource(R.drawable.ic_stanoviste_bck1_down);
				return false;
			}
		});

		if(parentContext instanceof DashboardActivity){
			Log.d("GEO DbButton", "setting click listener for task button: " + this.taskId);
			inButt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d("GEO DbButton", "butt width: " + inButt.getWidth());
					Log.d("GEO DbButton listener", "clicking: " + taskId);
					if(taskStatus < 0) {
						Toast.makeText(parentContext, "Úlohu můžete otevřít pomocí načtení QR kódu", Toast.LENGTH_SHORT).show();
						setImageByStatus();
					}else {
						((DashboardActivity) parentContext).startTask(taskId, taskTyp);
					}
				}
			});
		}
	}

	public void setStatus(int status) {
		this.taskStatus = status;
		this.checkStatus();
	}

	private void setImageByStatus() {
		switch (this.taskStatus) {
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
				this.inButt.setImageResource(R.drawable.ic_stanoviste_bck);
				break;
		}
	}

	public void checkStatus() {
		Log.d("GEO Dashboard - status", "id/status: " + this.taskId + "/" + this.taskStatus);
		switch (this.taskStatus) {
			case 1 :
				this.inStatus.setImageResource(R.drawable.ic_status_opened);
				setImageByStatus();
				//this.inLabel.setTextColor(ContextCompat.getColor(parentContext, R.color.colorTextTmava2));
				break;
			// splneno
			case 2 :
				this.inStatus.setImageResource(R.drawable.ic_check_ok);
				setImageByStatus();
				//this.inButt.setImageResource(R.drawable.ic_stanoviste_bck2);
				//this.inLabel.setTextColor(ContextCompat.getColor(parentContext, R.color.colorTextTmava2));
				break;
			// nenavstiveno
			case 0 :
			default:
				this.inStatus.setImageResource(android.R.color.transparent);
				setImageByStatus();
				//this.inButt.setImageResource(R.drawable.ic_stanoviste_bck);
				//this.inLabel.setTextColor(ContextCompat.getColor(parentContext, R.color.colorTextTmava));
				break;
		}
	}


}
