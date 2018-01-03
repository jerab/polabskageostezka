package cz.polabskageostezka.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cz.polabskageostezka.R;
import cz.polabskageostezka.TaskDragDropActivity;

//import static com.google.android.gms.internal.zzagz.runOnUiThread;
import static com.google.android.gms.internal.zzir.runOnUiThread;
//import static com.google.android.gms.internal.zzir.runOnUiThread;
//import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by tomason on 25.09.2017.
 */

public class DragDropTargetLayout extends RelativeLayout {

	private static final String LOG_TAG = "Geo DDTargetLayout";


	Context context;
	ImageView targetImg;
	ImageView zoomIcon = null;
	/**
	 * MUST BE SET IF zoomIcon IS NOT NULL
	 * 0 - def, 1 - active, 2 - correct
 	 */
	int[] targetHighlight = null;
	int targetId;
	int taskId;
	String targetResponse;
	/**
	 * can be 0, 1 or 2
	 */
	int targetStatusResult = 0;
	/**
	 * drawable resources for statut of image when fits
	 */
	Bitmap targetResult1;
	/**
	 * drawable resource after click
	 */
	@Nullable
	Bitmap targetResult2 = null;

	/// 0 - text, 1 - nadpis
	String[] targetResult2TextNadpis;

	/**


	 */
	public DragDropTargetLayout(Context context, int id, int[] wh, int targetImage, int afterImage, String[] afterText, int taskId, String tag, boolean
			isRightDirection) {
		super(context, null);
		this.context = context;
		this.taskId = taskId;
		targetId = id;
		targetResponse = tag;

		Resources r = getResources();
		//int wh = ImageAndDensityHelper.getDensityDependSize(r, R.dimen.dimTaskDragDrop_targetImg_width);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(wh[0], wh[1]);
		//this.setBackgroundColor(Color.WHITE);
		//setLayoutParams(params);

		/// pokud je nastaven afterImage ///
		if(afterImage > 0) {
			targetResult2 = ImageAndDensityHelper.getRoundedCornerBitmap(
					ImageAndDensityHelper.getBitmapFromDrawable(r, afterImage),
					TaskDragDropAdapter.getImageRadius(), false);
		}

		targetResult2TextNadpis = afterText;
		Log.d(LOG_TAG, "INIT - " + targetResult2TextNadpis[1]);


		if(taskId == Config.TASK_ZULA_ID) {
			targetHighlight = new int[]{R.drawable.zoom_default_w, R.drawable.zoom_default, R.drawable.zoom_correct};
			LayoutInflater.from(context).inflate(R.layout.dragdrop_target_zula, this, true);
			zoomIcon = (ImageView) getChildAt(0);
			targetImg = (ImageView) getChildAt(1);
			if (isRightDirection) {
				zoomIcon.setScaleX(-1);
				Log.d(LOG_TAG, "PadingLeft: " + targetImg.getPaddingLeft());
				targetImg.setPadding(targetImg.getPaddingRight(), targetImg.getPaddingTop(), targetImg.getPaddingLeft(), targetImg.getPaddingBottom());
				Log.d(LOG_TAG, "PadingRight after: " + targetImg.getPaddingRight());
			}
		}else {
			LayoutInflater.from(context).inflate(R.layout.dragdrop_target, this, true);
			targetImg = (ImageView) getChildAt(0);
			if(taskId == Config.TASK_SLEPENEC_ID) {
				/// hide zoom icon
				getChildAt(1).setVisibility(GONE);
			}else {
				targetHighlight = new int[]{R.drawable.ic_droptarget_default, R.drawable.ic_droptarget_active, R.drawable.ic_droptarget_correct};
				zoomIcon = (ImageView) getChildAt(1);
			}
		}
		setLayoutParams(params);

		//RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		Log.d(LOG_TAG, "Nastaveni pozadi targetImg");
		/// Slepenec - nastav cerne pozadi za vrstvu nad
		if(taskId == Config.TASK_SLEPENEC_ID) {
			targetImg.setImageResource(R.drawable.dd_target_bck_def);
			targetImg.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		/// ostatni, kde existuje pozadi
		}else if(targetImage > 0) {// || targetImage != R.drawable.ic_droptarget_bck_default) {
			targetImg.setImageBitmap(ImageAndDensityHelper.getRoundedCornerBitmap(
					ImageAndDensityHelper.getBitmapFromDrawable(r, targetImage),
					TaskDragDropAdapter.getImageRadius(), false));
		/// nastav defaultni obrazek na pozadi
		}else {
			targetImg.setImageResource(R.drawable.ic_droptarget_bck_default);
			targetImg.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}

		Log.d("Geo DDTargetLayout", "Target image " + id + " W: " + targetImg.getWidth());

		targetImg.setOnDragListener(new MyDragEventListener());
	}

	protected class MyDragEventListener implements View.OnDragListener{
		// This is the method that the system calls when it dispatches a drag event to the listener
		public boolean onDrag(View view, DragEvent event){
			// Define the variable to store the action type for the incoming event
			final int action = event.getAction();
			//Log.d(LOG_TAG, "onDrag Action: " + action);
			// Handles each of the expected events
			switch(action){
				case DragEvent.ACTION_DRAG_STARTED:
					// Determine if this view can accept dragged data
					if(event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
						if(zoomIcon != null) {
							Log.d(LOG_TAG, "onDrag Action START - changing icon");
							zoomIcon.setBackgroundResource(targetHighlight[1]);
						}else {
							view.setAlpha((float) 0.8);
						}
						// Return true to indicate that the view can accept the dragged data
						return true;
					}
					return false;
				case DragEvent.ACTION_DRAG_ENTERED:
					// When dragged item entered the receiver view area
					if(zoomIcon != null) {
						zoomIcon.setBackgroundResource(targetHighlight[0]);
					}else {
						view.setAlpha(1);
					}
					return true;
				case DragEvent.ACTION_DRAG_LOCATION:
					// Ignore the event
					return true;
				case DragEvent.ACTION_DRAG_EXITED:
					// When dragged object exit the receiver object
					if(zoomIcon != null) {
						zoomIcon.setBackgroundResource(targetHighlight[1]);
					}else {
						view.setAlpha((float) 0.8);
					}
					// Return true to indicate the dragged object exited the receiver view
					return true;
				case DragEvent.ACTION_DROP:
					// Get the dragged data
					String dragData = (String) event.getClipData().getItemAt(0).getText();
					Log.d(LOG_TAG, "onDrag DROP item: " + dragData);
					if (targetResponse.equals(dragData)) {
						// CORRECT
						targetResult1 = ImageAndDensityHelper.getRoundedCornerBitmap(
								ImageAndDensityHelper.getBitmapFromDrawable(context.getResources(), Integer.parseInt(dragData)),
								TaskDragDropAdapter.getImageRadius(), false);

						changeStatusAndTargetResource(targetStatusResult);
						Log.d(LOG_TAG, "onDrop CORRECT");
						if(context instanceof TaskDragDropActivity) {
							((TaskDragDropActivity) context).zaznamenejOdpoved(targetId);
							((TaskDragDropActivity) context).removeViewFromGrid(targetResponse, true);
						}

						// FALSE
					} else {
						runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
								Toast.makeText(context, "Špatně", Toast.LENGTH_SHORT).show();
							}
                        });
					}
					return true;
				case DragEvent.ACTION_DRAG_ENDED:
					if(zoomIcon != null) {
						zoomIcon.setBackgroundResource(targetHighlight[0]);
					}
					view.setAlpha(1);
					// Return true to indicate the drag ended
					return true;
				default:
					Log.e("Drag and Drop example","Unknown action type received.");
					break;
			}

			return false;
		}
	}

	public void changeStatusAndTargetResource(int currentStatus) {
		switch (currentStatus) {
			// uloha dokoncena v prvni fazi
			case 0 :
				targetStatusResult = 1;
				if(zoomIcon != null) {
					zoomIcon.setBackgroundResource(targetHighlight[2]);
				}
				targetImg.setImageBitmap(targetResult1);
				Log.d(LOG_TAG, "change STATUS 0 - TEXT: " + this.targetResult2TextNadpis[1]);
				/// pro slepenec se schova, aby bylo videt skrz
				if(this.taskId == Config.TASK_SLEPENEC_ID) {
					targetImg.setVisibility(INVISIBLE);
				}
				targetImg.setOnDragListener(null);
				targetImg.setOnClickListener(new DragDropTargetLayout.MyUltraDetailClick());
				break;
			// preklik do
			case 1 :
				targetStatusResult = 2;
				if(targetResult2 != null) {
					targetImg.setImageBitmap(targetResult2);
				}
				break;
			case 2 :
				targetStatusResult = 1;
				targetImg.setImageBitmap(targetResult1);
				break;
		}
	}

	private class MyUltraDetailClick implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			Log.d(LOG_TAG, "MyUltraClick - img WIDTH: " + targetImg.getWidth());
			changeStatusAndTargetResource(targetStatusResult);
			Log.d(LOG_TAG, "MyUltraClick - clicked ");

			if(targetResult2TextNadpis[0] != "") {
				//Toast.makeText(context, targetResult2TextNadpis, Toast.LENGTH_LONG).show();
				((TaskDragDropActivity) context).showResultDialog(true, targetResult2TextNadpis[1], targetResult2TextNadpis[0], false);
			}

		}
	}
	public void setTargetResult1(int obr) {
		Log.d(LOG_TAG, "setting TargetResult 1: " + obr);
		targetResult1 = ImageAndDensityHelper.getRoundedCornerBitmap(
				ImageAndDensityHelper.getBitmapFromDrawable(context.getResources(), obr),
				TaskDragDropAdapter.getImageRadius(), false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//targetResult1.setWidth(targetResult1.getWidth() - 5);
		}

	}
}
