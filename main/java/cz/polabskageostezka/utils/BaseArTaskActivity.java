package cz.polabskageostezka.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.vuforia.CameraDevice;
import com.vuforia.ObjectTracker;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;

import java.util.Vector;

import cz.polabskageostezka.R;
import cz.polabskageostezka.tasks.ArTask;
import cz.polabskageostezka.tasks.ar_content.Achat;
import cz.polabskageostezka.tasks.ar_content.Cube;
import cz.polabskageostezka.tasks.ar_content.Gabro;
import cz.polabskageostezka.utils.ar_support.ArVuforiaApplicationControl;
import cz.polabskageostezka.utils.ar_support.ArVuforiaApplicationException;
import cz.polabskageostezka.utils.ar_support.ArVuforiaApplicationSession;
import cz.polabskageostezka.utils.ar_utils.ArRenderer;
import cz.polabskageostezka.utils.ar_utils.LoadingDialogHandler;
import cz.polabskageostezka.utils.ar_utils.MeshObject;
import cz.polabskageostezka.utils.ar_utils.ArSurfaceView;
import cz.polabskageostezka.utils.ar_utils.Texture;

/**
 * Created by tomason on 09.06.2017.
 */

public abstract class BaseArTaskActivity extends BaseTaskActivity implements ArVuforiaApplicationControl, TaskResultDialog.TaskResultDialogInterface {

	private static final String LOGTAG = "GEO  BaseArTaskActiv";
	protected ArVuforiaApplicationSession baseArActivitySession;
	protected ArTask task;
	private InitDB db;
	private int status;
	protected TextView debugTw = null;
	private boolean isInitializedAr = false;

	protected LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
	protected LinearLayout baseMainUILayout;
	protected LinearLayout baseUILayout;

	private int mainUILayoutId;

	private boolean mFlash = false;
	private boolean mContAutofocus = false;
	boolean mIsDroidDevice = false;

	private boolean bGestureEnabled = false;
	protected GestureDetector baseGestureDetector = null;

	// Our OpenGL view:
	protected ArSurfaceView baseGlView;
	// Our renderer:
	protected ArRenderer baseRenderer;
	// The textures we will use for rendering:
	protected Vector<Texture> baseTextures;

	// Alert Dialog used to display SDK errors
	private AlertDialog mErrorDialog;

	// We want to load specific textures from the APK, which we will later use
	// for rendering.
	protected abstract void loadBaseTextures();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(Config.jeDebugOn(this.getBaseContext())) {
			debugTw = Config.getDebugTw(this);
		}
		startLoadingAnimation();
		baseArActivitySession = new ArVuforiaApplicationSession(this);
	}

	/**
	 * Metoda volaná z třídy, která dědí tuto třídu
	 *
	 * @param mTask
	 */
	protected void initTask(ArTask mTask) {
		this.task = mTask;
		super.init(task.getNazev(), task.getZadani());
		db = new InitDB(this);
		db.open();
		status = db.vratStavUlohy(task.getId());
		if (status == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(task.getId());
			UkazZadani(task.getNazev(), task.getZadani());
		}else {
			runFromStartTaskDialog();
		}

		db.close();

		/*
		DialogFragment dialog = TaskDialog.newInstance(task.getId(), task.getNazev(), task.getZadani());
		dialog.show(getSupportFragmentManager(), task.getNazev());
		*/
	}

	protected void setMainUiLayout(int uiLayout) {
		mainUILayoutId = uiLayout;
	}

	public void enableGestureDetector(boolean set) {
		bGestureEnabled = set;
	}

	protected void startLoadingAnimation() {
		Log.d(LOGTAG, "startLoadingAnimation()");
		Log.d(LOGTAG, Thread.currentThread().getStackTrace().toString());

		baseUILayout = (LinearLayout) View.inflate(this, R.layout.ar_camera_overlay,
				null);
		baseUILayout.setVisibility(View.VISIBLE);
		baseUILayout.setBackgroundColor(Color.BLACK);

		// Gets a reference to the loading dialog
		loadingDialogHandler.mLoadingDialogContainer = baseUILayout.findViewById(R.id.ar_loading_indicator);
		loadingDialogHandler.mLoadingDialogText = baseUILayout.findViewById(R.id.ar_loading_text);

		// Shows the loading indicator at start
		loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

		// Adds the inflated layout to the view
		addContentView(baseUILayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

	}

	// Initializes AR application components.
	protected void initApplicationAR() {
		Log.d(LOGTAG, Thread.currentThread().getName());
		Log.d(LOGTAG, "initApplicationAR()");
		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = Vuforia.requiresAlpha();

		baseGlView = new ArSurfaceView(this);
		baseGlView.init(translucent, depthSize, stencilSize);

		baseRenderer = new ArRenderer(this, baseArActivitySession);
		baseRenderer.setTextures(baseTextures);
		baseGlView.setRenderer(baseRenderer);
	}

	// Called when the activity will start interacting with the user.
	@Override
	protected void onResume() {
		Log.d(LOGTAG, "onResume");
		super.onResume();

		// This is needed for some Droid devices to force portrait
		if (mIsDroidDevice) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			baseArActivitySession.resumeAR();
		} catch (ArVuforiaApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Resume the GL view:
		if (baseGlView != null) {
			baseGlView.setVisibility(View.VISIBLE);
			baseGlView.onResume();
		}

	}

	// Callback for configuration changes the activity handles itself
	@Override
	public void onConfigurationChanged(Configuration config) {
		Log.d(LOGTAG, "onConfigurationChanged");
		super.onConfigurationChanged(config);
		baseArActivitySession.onConfigurationChanged();
	}

	// The final call you receive before your activity is destroyed.
	@Override
	protected void onDestroy() {
		Log.d(LOGTAG, "onDestroy");
		super.onDestroy();

		try {
			baseArActivitySession.stopAR();
		} catch (ArVuforiaApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Unload texture:
		baseTextures.clear();
		baseTextures = null;

		System.gc();
	}

	// Called when the system is about to start resuming a previous activity.
	@Override
	protected void onPause() {
		Log.d(LOGTAG, "onPause");
		super.onPause();

		if (baseGlView != null) {
			baseGlView.setVisibility(View.INVISIBLE);
			baseGlView.onPause();
		}

		/*
		// Turn off the flash
		if (mFlashOptionView != null && mFlash) {
			// OnCheckedChangeListener is called upon changing the checked state
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				((Switch) mFlashOptionView).setChecked(false);
			} else {
				((CheckBox) mFlashOptionView).setChecked(false);
			}
		}*/

		try {
			baseArActivitySession.pauseAR();
		} catch (ArVuforiaApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}
	}

	@Override
	public boolean doInitTrackers() {
		// Indicate if the trackers were initialized correctly
		boolean result = true;

		TrackerManager tManager = TrackerManager.getInstance();
		Tracker tracker;

		// Trying to initialize the image tracker
		tracker = tManager.initTracker(ObjectTracker.getClassType());
		if (tracker == null) {
			Log.e(
					LOGTAG,
					"Tracker not initialized. Tracker already initialized or the camera is already started");
			result = false;
		} else {
			Log.i(LOGTAG, "Tracker successfully initialized");
		}
		return result;
	}

	@Override
	public boolean doLoadTrackersData() {
		return false;
	}

	@Override
	public boolean doStartTrackers() {
		// Indicate if the trackers were started correctly
		boolean result = true;

		Tracker objectTracker = TrackerManager.getInstance().getTracker(
				ObjectTracker.getClassType());
		if (objectTracker != null)
			objectTracker.start();

		return result;
	}

	@Override
	public boolean doStopTrackers() {
		// Indicate if the trackers were stopped correctly
		boolean result = true;

		Tracker objectTracker = TrackerManager.getInstance().getTracker(
				ObjectTracker.getClassType());
		if (objectTracker != null)
			objectTracker.stop();

		return result;
	}

	@Override
	public boolean doUnloadTrackersData() {
		return false;
	}

	@Override
	public boolean doDeinitTrackers() {
		// Indicate if the trackers were deinitialized correctly
		boolean result = true;

		TrackerManager tManager = TrackerManager.getInstance();
		tManager.deinitTracker(ObjectTracker.getClassType());

		return result;
	}

	@Override
	public void onInitARDone(ArVuforiaApplicationException e) {
		Log.d(LOGTAG, Thread.currentThread().getName());
		if (e == null) {
			initApplicationAR();

			baseRenderer.setActive(true);

			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.

			baseMainUILayout = (LinearLayout) View.inflate(this, mainUILayoutId, null);

			if(Config.jeDebugOn(this.getBaseContext())) {
				/// pridani debug TextView do main layoutu
				baseMainUILayout.addView(debugTw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
			}
			/// pridani overlay view
			baseMainUILayout.addView(baseGlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			addContentView(baseMainUILayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

			// Sets the UILayout to be drawn in front of the camera
			baseUILayout.bringToFront();

			// Sets the layout background to transparent
			baseUILayout.setBackgroundColor(Color.TRANSPARENT);

			try {
				baseArActivitySession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
			} catch (ArVuforiaApplicationException ee) {
				Log.e(LOGTAG, ee.getString());
			}

			if (CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO))
				mContAutofocus = true;
			else
				Log.e(LOGTAG, "Unable to enable continuous autofocus");

			loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
			Log.d(LOGTAG, "Hiding progress bar");

		} else {
			Log.e(LOGTAG, e.getString());
			showInitializationErrorMessage(e.getString());
		}
	}

	// Shows initialization error messages as System dialogs
	public void showInitializationErrorMessage(String message) {
		final String errorMessage = message;
		runOnUiThread(new Runnable() {
			public void run() {
				if (mErrorDialog != null) {
					mErrorDialog.dismiss();
				}

				// Generates an Alert Dialog to show the error message
				AlertDialog.Builder builder = new AlertDialog.Builder(
						BaseArTaskActivity.this);
				builder
						.setMessage(errorMessage)
						.setTitle(getString(R.string.INIT_ERROR))
						.setCancelable(false)
						.setIcon(0)
						.setPositiveButton(getString(R.string.button_OK),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										finish();
									}
								});

				mErrorDialog = builder.create();
				mErrorDialog.show();
			}
		});
	}

	public boolean isExtendedTrackingActive() {
		return false;
	}

	/// vola se po zavreni dialogu
	@Override
	public void runFromStartTaskDialog() {
		if(!isInitializedAr) {
			isInitializedAr = true;
			Log.d(LOGTAG, "initAR");
			baseArActivitySession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			if (bGestureEnabled) {
				baseGestureDetector = new GestureDetector(this, new GestureListener());
			}
			Log.d(LOGTAG, "loadTexture");
			// Load any sample specific textures:
			baseTextures = new Vector<Texture>();
			loadBaseTextures();

			Log.d(LOGTAG, "startWith");
			mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid");
		}else {
			Log.d(LOGTAG, "app uz bezi");
		}
	}


	// Process Single Tap event to trigger autofocus
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		// Used to set autofocus one second after a manual focus is triggered
		private final Handler autofocusHandler = new Handler();


		@Override
		public boolean onDown(MotionEvent e) {
			//showDebugMsg("Source: " + e.getSource() + " | Y: " + e.getAxisValue(MotionEvent.AXIS_Y));
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			showDebugMsg("singleTapUp Y: " + e.getY());
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
			float diffX = e2.getX() - e1.getX();
			float diffY = e2.getY() - e1.getY();
			showDebugMsg("Dif Y: " + diffY + "  Dif X: " + diffX + "| distXY: " + distanceX + " | " + distanceY);
			/// rotate
			if(Math.abs(diffX) - Math.abs(diffY) > 50) {
				if(distanceX > 0) {
					baseRenderer.rotateObjectRightY();
				}else {
					baseRenderer.rotateObjectLeftY();
				}
			/// zoom
			}else if(Math.abs(diffY) - Math.abs(diffX) > 50) {
				if(distanceY > 0) {
					baseRenderer.zoomInObject();
					//baseRenderer.rotateObjectRightY();
				}else {
					baseRenderer.zoomOutObject();
					//baseRenderer.rotateObjectLeftY();
				}
			}
			return false;
		}
	}

	public void showDebugMsg(final String msg) {
		Log.d(LOGTAG, "AR DEBUG msg: " + msg);
		Config.showDebugMsg(debugTw, msg, this);
	}

	public MeshObject get3DObject() {
		switch (task.getContent3d(0)) {
			default:
				return null;
			case "Cube" :
				return (MeshObject)new Cube();
			case "Gabro" :
				return (MeshObject)new Gabro();
			case "Uhli" :
				return (MeshObject)new Cube();
			case "Drevo" :
				return (MeshObject)new Cube();
			case "Lava" :
				return (MeshObject)new Cube();
			case "Achat" :
				return (MeshObject)new Achat();
		}
	}

	public String[] get3DObjectTextures() {
		switch (task.getContent3d(0)) {
			default:
				return null;
			case "Cube" :
				return Cube.getTextures();
			case "Gabro" :
				return Gabro.getTextures();
			case "Uhli" :
				return Cube.getTextures();
			case "Drevo" :
				return Cube.getTextures();
			case "Lava" :
				return Cube.getTextures();
			case "Achat" :
				return Achat.getTextures();
		}
	}
}
