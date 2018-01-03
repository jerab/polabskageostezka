package cz.polabskageostezka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.vuforia.CameraDevice;
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

public class TaskArActivity extends BaseArTaskActivity
{
    private static final String LOGTAG = "GEO TaskArActivity";

    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    //private int mStartDatasetsIndex = 0;
    //private int mDatasetsNumber = 0;
	private ArrayList<String> mDatasetStrings = new ArrayList<String>();

    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//nacti spravny task podle intentu
		Intent mIntent = getIntent();
		super.initTask((ArTask) Config.vratUlohuPodleID(mIntent.getIntExtra("id", 0)));

		mDatasetStrings.add(task.getTarget());
    }

	@Override
    protected void loadBaseTextures() {
		String[] texts = this.get3DObjectTextures();
		for(int i = 0; i < texts.length; i++) {
			baseTextures.add(Texture.loadTextureFromApk(texts[i], getAssets()));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(baseGestureDetector != null) {
			return baseGestureDetector.onTouchEvent(event);
		}
		return false;
	}
    

    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData() {
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
            Log.d(LOGTAG, "UserData:Set the following user data "
                + (String) trackable.getUserData());
			showDebugMsg("UserData:Set the following user data " + (String) trackable.getUserData());
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
		Log.d(LOGTAG, "runFromResultDialog result: " + result + ", closeTask: " + closeTask);
    	allowConfirmButt();
	}

	@Override
	protected void setGestureEvent() {
		baseGestureDetector = new GestureDetector(this, new GestureListener());
	}

	@Override
	protected void setStartTaskValues() {
		switch (task.getContent3d(0)) {
			default:
			case "VybrusZula" :
			case "Drevo" :
			case "Lava" :
				//baseRenderer.setStartPositions(0,0,0);
				break;
			case "Gabro" :
				//baseRenderer.setStartPositions(0,0,0);
				break;
		}
	}

	// Process Single Tap event to trigger autofocus
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		// Used to set autofocus one second after a manual focus is triggered
		private final Handler autofocusHandler = new Handler();

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			//showDebugMsg("singleTapUp Y: " + e.getY());
			// Generates a Handler to trigger autofocus
			// after 1 second
			autofocusHandler.postDelayed(new Runnable() {
				public void run() {
					boolean result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

					if (!result)
						Log.e("SingleTapUp", "Unable to trigger focus");
				}
			}, 1000L);

			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if(baseRenderer.isModelVisible()) {
				float diffX = e2.getX() - e1.getX();
				float diffY = e2.getY() - e1.getY();
				/// left-right
				if (Math.abs(diffX) - Math.abs(diffY) > 50) {
					if (distanceX > 0) {
						baseRenderer.rotateObjectRightZ();
					} else {
						baseRenderer.rotateObjectLeftZ();
					}
					if (stepTaskModel == 1 && !taskFinished) {
						stepTaskModel++;
						zapisVysledek();
						showResultDialog(true, task.getNazev(), task.getResultTextOK(), false);
					}
				}
			}
			/*
			/// bottom-up
			if (Math.abs(diffY) - Math.abs(diffX) > 50) {
				if (distanceY > 0) {
					//baseRenderer.zoomObjectBy();
					baseRenderer.rotateObjectRightY();
				} else {
					//baseRenderer.zoomOutObject();
					baseRenderer.rotateObjectLeftY();
				}
			}*/

			return false;
		}
	}
}
