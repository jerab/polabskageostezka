package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskDragDropActivity;

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

	/**


	 */
	public DragDropTargetLayout(Context context, int id, int[] wh, int targetImage, int afterImage, int taskId, String tag, boolean isRightDirection) {
		super(context, null);
		this.context = context;
		this.taskId = taskId;
		targetId = id;
		targetResponse = tag;

		Resources r = getResources();
		//int wh = ImageAndDensityHelper.getDensityDependSize(r, R.dimen.dimTaskDragDrop_targetImg_width);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(wh[0], wh[1]);
		//this.setBackgroundColor(Color.WHITE);
		setLayoutParams(params);

		/// pokud je nastaven afterImage ///
		if(afterImage > 0) {
			targetResult2 = ImageAndDensityHelper.getRoundedCornerBitmap(
					ImageAndDensityHelper.getBitmapFromDrawable(r, afterImage),
					TaskDragDropAdapter.getImageRadius(), false);
		}

		if(taskId == Config.TASK_ZULA_ID) {
			LayoutInflater.from(context).inflate(R.layout.dragdrop_target_zula, this, true);
			zoomIcon = (ImageView) getChildAt(0);
			targetImg = (ImageView) getChildAt(1);
			if(isRightDirection) {
				zoomIcon.setScaleX(-1);
				Log.d(LOG_TAG, "PadingLeft: " + targetImg.getPaddingLeft());
				targetImg.setPadding(targetImg.getPaddingRight(), targetImg.getPaddingTop(), targetImg.getPaddingLeft(), targetImg.getPaddingBottom());
				Log.d(LOG_TAG, "PadingRight after: " + targetImg.getPaddingRight());
			}
		}else {
			LayoutInflater.from(context).inflate(R.layout.dragdrop_target, this, true);
			targetImg = (ImageView) getChildAt(0);
		}

		/// pokud je nastaven cilovy obrazek
		if(targetImage > 0) {
			targetImg.setImageBitmap(ImageAndDensityHelper.getRoundedCornerBitmap(
					ImageAndDensityHelper.getBitmapFromDrawable(r, targetImage),
					TaskDragDropAdapter.getImageRadius(), false));
		/// nastav cerne pozadi
		}else {
			targetImg.setImageResource(R.drawable.dd_target_bck_def);
			targetImg.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		}

		Log.d("Geo DDTargetLayout", "Target image " + targetImg.toString() + " | " + id);

		targetImg.setOnDragListener(new MyDragEventListener());
	}

	protected class MyDragEventListener implements View.OnDragListener{
		// This is the method that the system calls when it dispatches a drag event to the listener
		public boolean onDrag(View view, DragEvent event){
			// Define the variable to store the action type for the incoming event
			final int action = event.getAction();
			Log.d(LOG_TAG, "onDrag Action: " + action);
			// Handles each of the expected events
			switch(action){
				case DragEvent.ACTION_DRAG_STARTED:
					// Determine if this view can accept dragged data
					if(event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
						if(zoomIcon != null) {
							zoomIcon.setBackgroundResource(R.drawable.zoom_default);
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
						zoomIcon.setBackgroundResource(R.drawable.zoom_default_w);
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
						zoomIcon.setBackgroundResource(R.drawable.zoom_default);
					}else {
						view.setAlpha((float) 0.8);
					}
					// Return true to indicate the dragged object exited the receiver view
					return true;
				case DragEvent.ACTION_DROP:
					// Get the dragged data
					Log.d(LOG_TAG, "onDrag DROP item: " + event.getClipData().getItemAt(0).toString());
					ClipData.Item item = event.getClipData().getItemAt(0);
					String dragData = (String) item.getText();

					if (targetResponse.equals(dragData)) {
						// CORRECT
						targetResult1 = ImageAndDensityHelper.getRoundedCornerBitmap(
								ImageAndDensityHelper.getBitmapFromDrawable(context.getResources(), Integer.parseInt(dragData)),
								TaskDragDropAdapter.getImageRadius(), false);

						changeStatusAndTargetResource(targetStatusResult);
						Log.d(LOG_TAG, "onDrop CORRECT: " + context.getClass().getName() );
						if(context instanceof TaskDragDropActivity) {
							((TaskDragDropActivity) context).zaznamenejOdpoved(targetId);
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
						zoomIcon.setBackgroundResource(R.drawable.zoom_default_w);
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
					zoomIcon.setBackgroundResource(R.drawable.zoom_correct);
				}
				targetImg.setImageBitmap(targetResult1);
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
			changeStatusAndTargetResource(targetStatusResult);
		}
	}
	public void setTargetResult1(String obr) {
		targetResult1 = ImageAndDensityHelper.getRoundedCornerBitmap(
				ImageAndDensityHelper.getBitmapFromDrawable(context.getResources(), Integer.parseInt(obr)),
				TaskDragDropAdapter.getImageRadius(), false);

	}
}
