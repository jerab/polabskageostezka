package cz.polabskageostezka.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import cz.polabskageostezka.tasks.ar_content.Gabro;
import cz.polabskageostezka.tasks.ar_content.VybrusZula;
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

	private int status;
	protected TextView debugTw = null;
	private boolean isInitializedAr = false;
	Context mContext;

	protected LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
	protected RelativeLayout baseMainUILayout;
	protected LinearLayout baseUILayout;

	private int mainUILayoutId;

	private boolean mFlash = false;
	private boolean mContAutofocus = false;
	boolean mIsDroidDevice = false;

	protected GestureDetector baseGestureDetector = null;

	// Our OpenGL view:
	protected ArSurfaceView baseGlView;
	// Our renderer:
	protected ArRenderer baseRenderer = null;
	// The textures we will use for rendering:
	protected Vector<Texture> baseTextures;

	// Alert Dialog used to display SDK errors
	private AlertDialog mErrorDialog;

	// We want to load specific textures from the APK, which we will later use
	// for rendering.
	protected abstract void loadBaseTextures();

	private int extraOpenDialog = 0;
	private TextView arInfoTV;

	protected int stepTaskModel = 0;

	InitDB db = new InitDB(this);
	ImageView confirmButt;

	protected boolean taskFinished = false;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if(requestCode == Config.REQUEST_CODE_CAMERA) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				startActivity();
				/*
				try {
					//cameraSource.start(cameraPreview.getHolder());

				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMainUiLayout(R.layout.activity_task_ar);

		if(Config.jeDebugOn(this.getBaseContext())) {
			debugTw = Config.getDebugTw(this);
		}
		startLoadingAnimation();
		baseArActivitySession = new ArVuforiaApplicationSession(this);
	}

	private boolean isCameraEnabled() {
		return ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

	private void startActivity() {
		Log.d(LOGTAG, "...starting activity...");
		if(!isCameraEnabled()) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Config.REQUEST_CODE_CAMERA);
		}else {
			Log.d(LOGTAG, "Povoleni na kameru OK");
			db = new InitDB(this);
			db.open();
			status = db.vratStavUlohy(task.getId());
			if (status == Config.TASK_STATUS_NOT_VISITED) {
				db.odemkniUlohu(task.getId());
				if(extraOpenDialog > 0) {
					UkazZadani(task.getNazev(), task.getZadani(), extraOpenDialog);
				}else {
					UkazZadani(task.getNazev(), task.getZadani());
				}
			} else if(status == Config.TASK_STATUS_DONE) {
				taskFinished = true;
				runFromStartTaskDialog();
			}else {
				runFromStartTaskDialog();
			}
			db.close();
			/*
			DialogFragment dialog = TaskDialog.newInstance(task.getId(), task.getNazev(), task.getZadani());
			dialog.show(getSupportFragmentManager(), task.getNazev());
			*/
		}
	}

	/**
	 * Metoda volaná z třídy, která dědí tuto třídu
	 * @param mTask
	 */
	protected void initTask(ArTask mTask) {
		this.task = mTask;
		if(task.extraDialogLayout > 0) {
			this.extraOpenDialog = task.extraDialogLayout;
			super.init(task.getNazev(), task.getZadani(), task.getId(), task.extraDialogLayout);
		}else {
			super.init(task.getNazev(), task.getZadani(), task.getId());
		}
		startActivity();
	}

	protected void setMainUiLayout(int uiLayout) {
		mainUILayoutId = uiLayout;
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
		loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_DIALOG);

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
			if(isInitializedAr) {
				baseArActivitySession.stopAR();
				// Unload texture:
				baseTextures.clear();
			}
		} catch (ArVuforiaApplicationException e) {
			Log.e(LOGTAG, e.getString());
		}
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

	protected void setDescriptionTextView(String text) {
		arInfoTV.setText(text);
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
			baseMainUILayout = (RelativeLayout) View.inflate(this, mainUILayoutId, null);
			arInfoTV = (TextView) baseMainUILayout.findViewById(R.id.arTask_description);
			confirmButt = (ImageView) baseMainUILayout.findViewById(R.id.confirmTask);

			setStartTaskValues();
			Log.d(LOGTAG, "OnInitArDone - finished task: " + taskFinished);
			if(taskFinished) {
				allowConfirmButt();
			}

			if(task.getArInfoCount() > 0) {
				setDescriptionTextView(task.getArInfo(0));
			}else {
				arInfoTV.setVisibility(View.GONE);
			}

			if(Config.jeDebugOn(this.getBaseContext())) {
				/// pridani debug TextView do main layoutu
				baseMainUILayout.addView(debugTw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200));
			}
			/// pridani overlay view
			baseMainUILayout.addView(baseGlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			addContentView(baseMainUILayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			arInfoTV.bringToFront();
			confirmButt.bringToFront();

			// UILayout zobrazit nad vrstvou
			baseUILayout.bringToFront();

			// UIlayout pruhledny
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

			loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_DIALOG);
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
		if(!isCameraEnabled()) {
			startActivity();
		}else if(!isInitializedAr) {
			isInitializedAr = true;
			Log.d(LOGTAG, "initAR");
			baseArActivitySession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setGestureEvent();
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

	protected abstract void setGestureEvent();

	public void showDebugMsg(final String msg) {
		Log.d(LOGTAG, "AR DEBUG msg: " + msg);
		//Config.showDebugMsg(debugTw, msg, this);
	}

	public MeshObject get3DObject() {
		switch (task.getContent3d(0)) {
			default:
				return null;
			case "VybrusZula" :
				return (MeshObject)new VybrusZula();
			case "Gabro" :
				return (MeshObject)new Gabro();
			case "Drevo" :
				return (MeshObject)new Gabro();
			case "Lava" :
				return (MeshObject)new Gabro();
			case "Achat" :
				return (MeshObject)new Achat();
		}
	}

	public String[] get3DObjectTextures() {
		switch (task.getContent3d(0)) {
			default:
				return null;
			case "VybrusZula" :
				return VybrusZula.getTextures();
			case "Gabro" :
				return Gabro.getTextures();
			case "Drevo" :
				return Gabro.getTextures();
			case "Lava" :
				return Gabro.getTextures();
			case "Achat" :
				return Achat.getTextures();
		}
	}

	protected abstract void setStartTaskValues();

	// called from ArRenderer
	public void setFirstLoading() {
		if(stepTaskModel == 0) {
			stepTaskModel = 1;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String text = task.getArInfo(1);
					if(text != null) {
						setDescriptionTextView(text);
					}
				}
			});

		}
	}

	protected void zapisVysledek() {
		Log.d(LOGTAG, "Write steps to DB");
		try {
			db.open();
			/// TODO
			db.zapisTaskDoDatabaze(task.getId(),System.currentTimeMillis());
			//db.zapisArTaskTarget(task.getId(), target, (int) System.currentTimeMillis());
			db.close();
		} catch (Exception e) {
			Log.d(LOGTAG, "e:" + e.toString());
		}

	}

	protected void allowConfirmButt() {
		Log.d(LOGTAG, "Povoleni ConfirmButton");
		confirmButt.setVisibility(View.VISIBLE);
		confirmButt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(LOGTAG, "spoustim dalsi ulohu ...");
				runNextQuest(task.getRetezId(), mContext);
			}
		});
	}
}
