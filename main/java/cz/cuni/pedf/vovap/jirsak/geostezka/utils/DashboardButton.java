package cz.cuni.pedf.vovap.jirsak.geostezka.utils;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.GridView;
//import android.util.AttributeSet;
//import android.widget.Button;

import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskCamActivity;

/**
 * Created by tomason on 13.09.2017.
 */

public class DashboardButton extends android.support.v7.widget.AppCompatButton {
	/**
	 *  0 - not open; 1 - open; 2 - finished
	 */
	Context parentContext;
	int status = 0;
	int taskId = 0;
	int taskTyp = 0;

	public DashboardButton(Context context, String nazev, int typ, int status, int id) {
		super(context, null, R.style.GeoTheme);
		this.parentContext = context;
		this.setLayoutParams(new GridView.LayoutParams(55, 55));
		this.setText(id);
		this.taskId = id;
		this.taskTyp = typ;

		if(parentContext instanceof DashboardActivity){
			this.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((DashboardActivity)parentContext).startTask(taskId, taskTyp);
				}
			});
		}
		this.checkStatus();
	}

	public void setStatus(int status) {
		this.status = status;
		this.checkStatus();
	}

	private void checkStatus() {
		switch (this.status) {
			case 0 :
				this.setBackgroundResource(R.drawable.stanoviste_not_visited);
				this.setText(this.taskId);
				break;
			case 1 :
				this.setBackgroundResource(R.drawable.stanoviste_opened);
				break;
			case 2 :
				this.setBackgroundResource(R.drawable.stanoviste_done);
				break;
		}
	}


}
