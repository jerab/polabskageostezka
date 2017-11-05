package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.TrackerManager;

import java.util.ArrayList;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ArTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseArTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils.Texture;

public class TaskARTestActivity extends BaseArTaskActivity
{
    private static final String LOGTAG = "GEO-TaskARTestActivity";

    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    //private int mStartDatasetsIndex = 0;
    //private int mDatasetsNumber = 0;
	private ArrayList<String> mDatasetStrings = new ArrayList<String>();

    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;

    
    // Called when the activity first starts or the user navigates back to an
    // activity.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setMainUiLayout(R.layout.activity_task_artest);
		Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

		//mDatasetStrings.add("StonesAndChips.xml");
		//mDatasetStrings.add("Tarmac.xml");
		mDatasetStrings.add("Geostezka.xml");

		super.enableGestureDetector(true);

		//nacti spravny task podle intentu
		Intent mIntent = getIntent();
		int predaneID = mIntent.getIntExtra("id", 0);
		super.initTask((ArTask) Config.vratUlohuPodleID(predaneID));
    }

	@Override
    protected void loadBaseTextures()
	{
		String[] texts = this.get3DObjectTextures();
		for(int i = 0; i < texts.length; i++) {
			baseTextures.add(Texture.loadTextureFromApk(texts[i], getAssets()));
		}

		//baseTextures.add(Texture.loadTextureFromApk("TextureTeapotBrass.png", getAssets()));
		//baseTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png", getAssets()));
		//baseTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png", getAssets()));
		//baseTextures.add(Texture.loadTextureFromApk("ImageTargets/Buildings.jpeg", getAssets()));
		//baseTextures.add(Texture.loadTextureFromApk("obj/gabro.jpg", getAssets()));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{

		// Process the Gestures
        /*
		if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event))
            return true;
        */
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

    /*final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;
    */
    
}
