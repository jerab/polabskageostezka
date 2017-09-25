package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskDragDropActivity;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by tomason on 25.09.2017.
 */

public class DragDropTargetLayout extends RelativeLayout {

	private static final String LOG_TAG = "Geo DDTargetLayout";


	Context context;
	ImageView targetImg;
	ImageView zoomIcon;
	int targetId;
	String targetResponse;
	/**
	 * can be 0, 1 or 2
	 */
	int targetStatusResult = 0;
	/**
	 * drawable resources for statut of image when fits
	 */
	Bitmap targetResult1;
	Bitmap targetResult2;

	/**
	 *
	 * @param context
	 * @param targetImage id of @drawable image
	 * @param position
	 */
	public DragDropTargetLayout(Context context, int id, int targetImage, int afterImage, int[] position, String tag, boolean isRightDirection) {
		super(context, null);
		this.context = context;
		Resources r = getResources();
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(R.dimen.dimTaskDragDrop_targetImg_width, R.dimen
				.dimTaskDragDrop_targetImg_height);
		params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, position[0], r.getDisplayMetrics());
		params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, position[1], r.getDisplayMetrics());
		//this.setBackgroundColor(Color.WHITE);
		setLayoutParams(params);

		targetResult2 = RoundImageHelper.getRoundedCornerBitmap(
				RoundImageHelper.getBitmapFromDrawable(r, afterImage),
				RoundImageHelper.DRAG_DROP_IMG_RADIUS, false);

		LayoutInflater.from(context).inflate(R.layout.dragdrop_target, this, true);
		zoomIcon = (ImageView) getChildAt(0);
		targetImg = (ImageView) getChildAt(1);
		if(isRightDirection) {
			zoomIcon.setScaleX(-1);
			targetImg.setPadding(targetImg.getPaddingRight(), targetImg.getPaddingTop(), targetImg.getPaddingLeft(), targetImg.getPaddingBottom());

		}
		Log.d("Geo DDTargetLayout", "Target image " + targetImg.toString() + " | " + id);
		targetImg.setImageBitmap(RoundImageHelper.getRoundedCornerBitmap(
				RoundImageHelper.getBitmapFromDrawable(r, targetImage),
				RoundImageHelper.DRAG_DROP_IMG_RADIUS, false)
		);
		/*targetImg.setTag(tag);
		this.setId(id);*/
		targetId = id;
		targetResponse = tag;

		targetImg.setOnDragListener(new MyDragEventListener());
	}

	protected class MyDragEventListener implements View.OnDragListener{
		/*
			public abstract boolean onDrag (View v, DragEvent event)
				Called when a drag event is dispatched to a view. This allows listeners to get
				a chance to override base View behavior.
		*/
		// This is the method that the system calls when it dispatches a drag event to the listener
		public boolean onDrag(View view, DragEvent event){
			// Define the variable to store the action type for the incoming event
			final int action = event.getAction();
			Log.d(LOG_TAG, "onDrag Action: " + action);

            /*
                ACTION_DRAG_ENDED
                    Signals to a View that the drag and drop operation has concluded.
                ACTION_DRAG_ENTERED
                    Signals to a View that the drag point has entered the bounding box of the View.
                ACTION_DRAG_EXITED
                    Signals that the user has moved the drag shadow outside the bounding box of the View.
                ACTION_DRAG_LOCATION
                    Sent to a View after ACTION_DRAG_ENTERED if the drag shadow is still
                    within the View object's bounding box.
                ACTION_DRAG_STARTED
                    Signals the start of a drag and drop operation.
                ACTION_DROP
                    Signals to a View that the user has released the drag shadow, and the drag
                    point is within the bounding box of the View.
            */
			// Handles each of the expected events
			switch(action){
				case DragEvent.ACTION_DRAG_STARTED:
					// Determine if this view can accept dragged data
					if(event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
						// If the view view can accept dragged data
						//view.setBackgroundColor(Color.parseColor("#FFC4E4FF"));
						view.setAlpha((float)0.8);
						// Return true to indicate that the view can accept the dragged data
						return true;
					}
					return false;
				case DragEvent.ACTION_DRAG_ENTERED:
					// When dragged item entered the receiver view area
					view.setAlpha(1);

					return true;
				case DragEvent.ACTION_DRAG_LOCATION:
					// Ignore the event
					return true;
				case DragEvent.ACTION_DRAG_EXITED:
					// When dragged object exit the receiver object
					view.setAlpha((float)0.8);
					// Return true to indicate the dragged object exited the receiver view
					return true;
				case DragEvent.ACTION_DROP:
					// Get the dragged data
					Log.d(LOG_TAG, "onDrag DROP item: " + event.getClipData().getItemAt(0).toString());
					ClipData.Item item = event.getClipData().getItemAt(0);
					String dragData = (String) item.getText();
					//view.setVisibility(View.VISIBLE);
					// Cast the receiver view as a TextView object
					/*ImageView v = (ImageView) view;
					Log.d(LOG_TAG, "onDrag DROP view: " + view.getClass().getName());*/
					//if (v.getTag().equals(dragData))
					if (targetResponse.equals(dragData))
					{
						targetResult1 = RoundImageHelper.getRoundedCornerBitmap(
								RoundImageHelper.getBitmapFromDrawable(context.getResources(), Integer.parseInt(dragData)),
								RoundImageHelper.DRAG_DROP_IMG_RADIUS, false);


						//Integer.parseInt(dragData);
						changeStatusAndTargetResource(targetStatusResult);
						//resultInfo.setText("Spravne");
						if(context instanceof TaskDragDropActivity) {
							((TaskDragDropActivity) context).zaznamenejOdpoved(targetId);
						}
						/*odpocet++;
						if (odpocet == obrazkyCile.length)
						{
							Toast.makeText(getApplicationContext(), "Uloha dokoncena", Toast.LENGTH_SHORT).show();
							InitDB db = new InitDB(getApplicationContext());
							db.open();
							db.zapisTaskDoDatabaze(dd.getId(),System.currentTimeMillis());
							db.close();
							//findViewById(R.id.btnDDBack).setVisibility(View.VISIBLE);
						}*/
					} else {
						runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
								Toast.makeText(context, "Špatně", Toast.LENGTH_SHORT).show();
							}
                        });
					}
					// Change the TextView text color as dragged object background color
					//v.setTextColor(Integer.parseInt(dragData));


					//Log.d("GEO TDDAct", rlDD.getHeight() + " a sirka : " + rlDD.getWidth());
					// v.setTag(Integer.parseInt(dragData));
					// Log.d("GEO: Tag of element a ", String.valueOf(v.getTag()));
					//Log.d("GEO: ID of element ", String.valueOf(v.getId()));

					//Log.d("GEO: ", String.valueOf(dragData));
					// Return true to indicate the dragged object dop

					return true;
				case DragEvent.ACTION_DRAG_ENDED:
					// Remove the background color from view
					//view.setBackgroundColor(Color.TRANSPARENT);
					view.setAlpha(1);
					if(event.getResult()){
						Log.d("GEO TaskDragDropAct","drop was handled");
					}else {
						Log.d("GEO TaskDragDropAct","Drop wasnt handled");
					}
					// Return true to indicate the drag ended
					return true;
				default:
					Log.e("Drag and Drop example","Unknown action type received.");
					break;
			}

			return false;
		}
	}

	private void changeStatusAndTargetResource(int currentStatus) {
		switch (currentStatus) {
			case 0 :
				targetStatusResult = 1;
				zoomIcon.setBackgroundResource(R.drawable.zoom_correct);
				targetImg.setImageBitmap(targetResult1);
				targetImg.setOnDragListener(null);
				targetImg.setOnClickListener(new DragDropTargetLayout.MyUltraDetailClick());
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, "Správně!",Toast.LENGTH_SHORT).show();
					}
				});
				break;
			case 1 :
				targetStatusResult = 2;
				targetImg.setImageBitmap(targetResult2);
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
}
