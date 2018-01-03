package cz.polabskageostezka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.TrackerManager;

import java.util.ArrayList;

import cz.polabskageostezka.tasks.ArTask;
import cz.polabskageostezka.utils.BaseArTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.ar_utils.Texture;

public class TaskArAchatActivity extends BaseArTaskActivity
{
    private static final String LOGTAG = "GEO TaskArActivity";

    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    //private int mStartDatasetsIndex = 0;
    //private int mDatasetsNumber = 0;
	private ArrayList<String> mDatasetStrings = new ArrayList<String>();

    private boolean mSwitchDatasetAsap = false;
    /// 0 - neni nacten, 1 - objevil se,  2 - vysoupnut ven,  3 - natocen a konec
    //protected int stepTaskModel = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//nacti spravny task podle intentu
		Intent mIntent = getIntent();
		super.initTask((ArTask) Config.vratUlohuPodleID(mIntent.getIntExtra("id", 0)));

		mDatasetStrings.add(task.getTarget());
    }

	@Override
    protected void loadBaseTextures()
	{
		String[] texts = this.get3DObjectTextures();
		for(int i = 0; i < texts.length; i++) {
			baseTextures.add(Texture.loadTextureFromApk(texts[i], getAssets()));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)	{
		if(baseGestureDetector != null) {
			return baseGestureDetector.onTouchEvent(event);
		}
		return false;
	}


    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData()
    {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset == null)
            mCurrentDataset = objectTracker.createDataSet();

        if (mCurrentDataset == null)
            return false;

        if (!mCurrentDataset.load(
			mDatasetStrings.get(mCurrentDatasetSelectionIndex),
            STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;

        if (!objectTracker.activateDataSet(mCurrentDataset))
            return false;

        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++)
        {
            Trackable trackable = mCurrentDataset.getTrackable(count);
            if(isExtendedTrackingActive())
            {
                trackable.startExtendedTracking();
            }

            String name = "Current Trackable: " + trackable.getName();
            trackable.setUserData(name);
        }

        return true;
    }


    @Override
    public boolean doUnloadTrackersData()
    {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        try {
			TrackerManager tManager = TrackerManager.getInstance();
			ObjectTracker objectTracker = (ObjectTracker) tManager
					.getTracker(ObjectTracker.getClassType());
			if (objectTracker == null)
				return false;
			if (mCurrentDataset != null && mCurrentDataset.isActive())
			{
				if (objectTracker.getActiveDataSet(0).equals(mCurrentDataset)
						&& !objectTracker.deactivateDataSet(mCurrentDataset))
				{
					result = false;
				} else if (!objectTracker.destroyDataSet(mCurrentDataset))
				{
					result = false;
				}

				mCurrentDataset = null;
			}

			return result;
		}catch (Exception e) {
        	return false;
		}
    }

	@Override
	public void onVuforiaUpdate(State state) {
		if (mSwitchDatasetAsap)
		{
			mSwitchDatasetAsap = false;
			TrackerManager tm = TrackerManager.getInstance();
			ObjectTracker ot = (ObjectTracker) tm.getTracker(ObjectTracker
					.getClassType());
			if (ot == null || mCurrentDataset == null
					|| ot.getActiveDataSet(0) == null)
			{
				Log.d(LOGTAG, "Failed to swap datasets");
				return;
			}

			doUnloadTrackersData();
			doLoadTrackersData();
		}
	}

	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
		allowConfirmButt();
	}

	@Override
	protected void setGestureEvent() {
		baseGestureDetector = new GestureDetector(this, new GestureListener());
	}

	@Override
	protected void setStartTaskValues() {
    	if(!taskFinished) {
			baseRenderer.rotateObjectRightY(180);
			baseRenderer.rotateObjectRightZ(180);
		}
	}

	@Override
	public void setFirstLoading() {
		if(stepTaskModel == 0) {
			if(taskFinished) {
				stepTaskModel = 3;
			}else {
				stepTaskModel = 1;
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setDescriptionTextView(task.getArInfo(stepTaskModel));
				}
			});

		}
	}

	private void checkStatus() {
		switch (stepTaskModel) {
			case 1 :
				setDescriptionTextView(task.getArInfo(stepTaskModel));
				break;
			case 2:
				setDescriptionTextView(task.getArInfo(stepTaskModel));
				baseRenderer.zoomObjectBy(0.007f);
				break;
			case 3:
				setDescriptionTextView(task.getArInfo(stepTaskModel));
				//baseRenderer.moveInZ(2);
				baseRenderer.rotateObjectRightY(180);
				baseRenderer.rotateObjectRightZ(180);
				if(!taskFinished) {
					zapisVysledek();
					showResultDialog(true, task.getNazev(), task.getResultTextOK(), false);
				}
				break;
		}
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.e(LOGTAG, "Double Tap ..." + stepTaskModel + " - " + baseRenderer.getObjectScaleFloat());
			// pouze, kdyz je model videt
			if(baseRenderer.isModelVisible() && stepTaskModel > 0 && stepTaskModel < 3) {
				stepTaskModel++;
				checkStatus();
			}
			return super.onDoubleTap(e);
		}
/*
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			//showDebugMsg("singleTapUp Y: " + e.getY());
			// Generates a Handler to trigger autofocus
			// after 1 second
			autofocusHandler.postDelayed(new Runnable() {
				public void run() {
					boolean result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

					//if (!result)
						//Log.e("SingleTapUp", "Unable to trigger focus");
				}
			}, 1000L);

			return true;
		}
*/
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if(baseRenderer.isModelVisible() && stepTaskModel == 3) {
				float diffX = e2.getX() - e1.getX();
				float diffY = e2.getY() - e1.getY();
				//showDebugMsg("Dif Y: " + diffY + "  Dif X: " + diffX + "| distXY: " + distanceX + " | " + distanceY);
				/// left-right
				if (Math.abs(diffX) - Math.abs(diffY) > 50) {
					if (distanceX > 0) {
						baseRenderer.rotateObjectRightY();
					} else {
						baseRenderer.rotateObjectLeftY();
					}
				}

				/// bottom-up
				if (Math.abs(diffY) - Math.abs(diffX) > 50) {
					if(distanceY > 0 && baseRenderer.getObjectScaleFloat() < 0.05) {
						baseRenderer.zoomInObject();
					}else if(baseRenderer.getObjectScaleFloat() > 0.003) {
						baseRenderer.zoomOutObject();
					}
				}
			}
			return false;
		}
	}
}
