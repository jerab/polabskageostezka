package cz.polabskageostezka;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import cz.polabskageostezka.tasks.CamTask;
import cz.polabskageostezka.utils.BaseTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.InitDB;
import cz.polabskageostezka.utils.Stanoviste;

public class TaskCamActivity extends BaseTaskActivity {
	private final static String LOG_TAG = "Geo TaskCam";

	Context mContext;

	SurfaceView cameraPreview;
	BarcodeDetector barcodeDetector;
	CameraSource cameraSource;
	final int RequestCameraPermissionID = 1001;
	CamTask ct;
	int pocetPolozek;
	int steps;
	Stanoviste[] cile;
	/// pole cisel Stanovist v promenne cile
	ArrayList<Integer> vysledky;
	ToggleButton[] tbs;
	RelativeLayout rlts;
	private int pokus;
	InitDB db = new InitDB(this);
	ImageView confirmButt;
	boolean cteckaAktivni = true;
	int taskStatus;


	SurfaceHolder.Callback surfaceHolderClb;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case RequestCameraPermissionID: {
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
					try {
						cameraSource.start(cameraPreview.getHolder());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		setContentView(R.layout.activity_task_cam);
		confirmButt = (ImageView)findViewById(R.id.confirmTask);

		//nacti spravny task podle intentu
		Intent mIntent = getIntent();
		int predaneID = mIntent.getIntExtra("id", 0);
		if(predaneID < Config.vratPocetUlohIntro()) {
			ct = (CamTask) Config.vratIntroUlohuPodleID(predaneID);
			this.isIntroTask = true;
		}else {
			ct = (CamTask) Config.vratUlohuPodleID(predaneID);
		}
		super.init(ct.getNazev(), ct.getZadani());
		steps = 0;
		db.open();
		taskStatus = db.vratStavUlohy(ct.getId());
		if(taskStatus == Config.TASK_STATUS_DONE) {
			allowConfirmBuut();
		}else {
			if (taskStatus == Config.TASK_STATUS_NOT_VISITED) {
				db.odemkniUlohu(ct.getId());
				UkazZadani(ct.getNazev(), ct.getZadani());
			}
			activateReader();
		}
		db.close();

		pocetPolozek = ct.getPocetCilu();
		cile = ct.getStanoviste();
		updateTask();
		checkIfComplete();

		rlts = (RelativeLayout) findViewById(R.id.rlToggles);
		tbs = new ToggleButton[pocetPolozek];
		Log.d(LOG_TAG, "Vysledek: " + vysledky.toString());
		Log.d(LOG_TAG, "Pocet polozek: " + pocetPolozek);

		double rada;
		int sloupec;
		int pocetSloupcu = 4;
		for (int k = 0; k < pocetPolozek; k++) {
			RelativeLayout.LayoutParams newParams = new RelativeLayout
					.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

			//vysledek[k] = String.valueOf(k);
			tbs[k] = new ToggleButton(this);
			tbs[k].setTextOff("---");
			/// cislo Stanoviste
			tbs[k].setTextOn(String.valueOf(cile[k].getCislo()));
			tbs[k].setTag(cile[k].getCislo());
			tbs[k].setEnabled(false);
			//tbs[k].setTag(vysledek[k]);

			if (vysledky.contains(k)) { //.equals(getString(R.string.CamTaskStringFinished))) {
				tbs[k].setChecked(true);
				tbs[k].setEnabled(true);
				tbs[k].setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int ind = ct.getIndexStanovistePodleCisla((int)v.getTag());
						showResultDialog(true, cile[ind].getNazev(), ct.getZpetnaVazbaOk(ind), false);
						((ToggleButton)v).setChecked(true);
					}
				});
			}

			tbs[k].setId(100 + k);
			tbs[k].setLayoutParams(newParams);

			rada = Math.floor(k / pocetSloupcu); // 0 az n
			sloupec = k % pocetSloupcu; // 0 az 3


			/// serazeni toggleu
			if(rada == 0 && sloupec == 0) {
				newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				newParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				Log.d(LOG_TAG, "prvni " + String.valueOf(k));
			}else {
				if (rada > 0) {
					newParams.addRule(RelativeLayout.BELOW, 100 + k - pocetSloupcu);
					Log.d(LOG_TAG, "pod " + String.valueOf(k - pocetSloupcu));
				}
				if (sloupec > 0) {
					newParams.addRule(RelativeLayout.RIGHT_OF, 99 + k);
					Log.d(LOG_TAG, "vpravo od " + String.valueOf(k + 99));
				}
			}
			tbs[k].setLayoutParams(newParams);
			rlts.addView(tbs[k]);
		}
	}

	private void activateReader() {
		final DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		// camtask potreby - barcode reader, camera atp.
		cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);

		barcodeDetector = new BarcodeDetector.Builder(this)
				.setBarcodeFormats(Barcode.QR_CODE)
				.build();

		cameraSource = new CameraSource
				.Builder(this, barcodeDetector)
				.setRequestedPreviewSize((metrics.widthPixels), (metrics.heightPixels))
				.setAutoFocusEnabled(true)
				.build();

		surfaceHolderClb = new SurfaceHolder.Callback() {
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(TaskCamActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
					return;
				}
				try {
					cameraSource.start(cameraPreview.getHolder());
					Log.d(LOG_TAG, "Camera size: " + cameraSource.getPreviewSize().toString());
					//Log.d(LOG_TAG, "Camera size: " + );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				//Size size = cameraSource.getPreviewSize();
				//metrics.heightPixels
				//Log.d(LOG_TAG, "Camera size changed: " + cameraSource.getPreviewSize().toString());
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				cameraSource.stop();
			}
		};
		cameraPreview.getHolder().addCallback(surfaceHolderClb);

		barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
			@Override
			public void release() {
				Log.d(LOG_TAG,"barcodeDetector RELEASE");
				barcodeDetector.release();
				cameraSource.stop();
			}

			@Override
			public void receiveDetections(Detector.Detections<Barcode> detections) {
				SparseArray<Barcode> qrcodes = detections.getDetectedItems();
				if (qrcodes.size() != 0 && cteckaAktivni) {
					String qrCodeText = String.valueOf(qrcodes.valueAt(0).displayValue);
					/// projdi vsechny vysledky a porovnej spravnost
					for (int k = 0; k < cile.length; k++) {
						/// je shoda mezi cily
						if (qrCodeText.equals(cile[k].getUrl())) {
							Log.d(LOG_TAG, qrCodeText);
							pokus = k;
							/// jeste neni ve vysledcich
							if (!vysledky.contains(k)) {
								vysledky.add(k);// = getString(R.string.CamTaskStringFinished);
								zapisVysledek(k);
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										tbs[pokus].setChecked(true);
										tbs[pokus].setEnabled(true);
										tbs[pokus].setOnClickListener(new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												showResultDialog(true, cile[pokus].getNazev(), ct.getZpetnaVazbaOk(pokus), false);
												tbs[pokus].setChecked(true);
											}
										});

									}
								});
							}
							cteckaAktivni = false;
							/// zobraz dilci OK dialog
							Handler mainHandler = new Handler(mContext.getMainLooper());
							Runnable myRunnable = new Runnable() {
								@Override
								public void run() {
									showResultDialog(true, cile[pokus].getNazev(), ct.getZpetnaVazbaOk(pokus), false);
								}
							};
							mainHandler.post(myRunnable);
							break;
						}
					}

					/// prosly se vsechny cile a neni mezi spravnymi odpovedmi
					if(cteckaAktivni) {
						cteckaAktivni = false;
						/// zobraz dilci FALSE dialog
						Handler mainHandler = new Handler(mContext.getMainLooper());
						Runnable myRunnable = new Runnable() {
							@Override
							public void run() {
								showResultDialog(false, ct.getNazev(), ct.getZpetnaVazbaFalse(), false);
							}
						};
						mainHandler.post(myRunnable);
					}
					//lastQrCode = qrCodeText;
				/// odklon od QR kodu => zaktivnit ctecku
				}else {
					//lastQrCode = "";
					//cteckaAktivni = true;
				}
			}
		});
	}

	/**
	 *
	 * @param target - index v poli cile
	 */
	private void zapisVysledek(int target) {
		Log.d(LOG_TAG, "Write steps");
		InitDB db = new InitDB(this);
		try {
			db.open();
			db.zapisCamTaskTarget(ct.getId(), target, (int) System.currentTimeMillis());
			db.close();
		} catch (Exception e) {
			Log.d(LOG_TAG, "e:" + e.toString());
		}

	}

	public void updateTask() {
		vysledky = new ArrayList<>();
		InitDB db = new InitDB(this);
		db.open();
		/// jiz zanesene zaznamy o splnenych cilech
		int[] zaznamy = db.vratVsechnyTargetyCamTaskPodleId(ct.getId());
		db.close();
		Log.d(LOG_TAG, "Kolik mame jiz v DB zanesenych?" + zaznamy.length);

		for (int k = 0; k < zaznamy.length; k++) {
			vysledky.add(zaznamy[k]);
			//if(zaznamy[k] == getString(R.string.CamTaskStringFinished);
			Log.d(LOG_TAG, "Vysledky z DB " + k + " : " + String.valueOf(zaznamy[k]));
		}
		Log.d(LOG_TAG, "Celkem VYSLEDKY:" + vysledky.size());

		/// uloha je jiz dokoncena ///
		if (zaznamy.length == cile.length) {
			Log.d(LOG_TAG, "Task completed");
			Toast.makeText(this, "Úloha dokončena", Toast.LENGTH_LONG).show();
			//runFromResultDialog(true, true);
			// todo : zkontrolovat spravnost ?!?!?!
			/*for (int k = 0; k < origo.length; k++) {
				origo[targety[k]] = getString(R.string.CamTaskStringFinished);
				//Log.d(LOG_TAG, "Vysledky z DB: " + String.valueOf(targety[k]));
			}*/
		}/* else if (targety != null) {
			for (int i = 0; i < targety.length; i++) {
				origo[targety[i]] = getString(R.string.CamTaskStringFinished);
				Log.d(LOG_TAG, "Vysledky z DB: " + String.valueOf(targety[i]));
			}
		}
		*/
	}

	private boolean checkIfComplete() {
		Log.d(LOG_TAG, "checkuju");
		/// complete
		if(vysledky.size() == cile.length) {
			InitDB db = new InitDB(this);
			db.open();
			db.zapisTaskDoDatabaze(ct.getId(), System.currentTimeMillis());
			db.close();
			return true;
		} else {
			return false;
		}
	}

	private void allowConfirmBuut() {
		confirmButt.setVisibility(View.VISIBLE);
		confirmButt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(LOG_TAG, "spoustim dalsi ulohu ...");
				runNextQuest(ct.getRetezId(), mContext);
			}
		});
	}

	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
		Log.d(LOG_TAG, "Run from Dialog... ");
		if (result && closeTask) {
			runNextQuest(ct.getRetezId(), mContext);
		}else if(taskStatus != Config.TASK_STATUS_DONE
			&& checkIfComplete()) {
				Log.d(LOG_TAG, " prvni podminka " + this.getClass().getName());
				Log.d(LOG_TAG, " prvni podminka " + mContext.getClass().getName());
				showResultDialog(true, ct.getNazev(), ct.getResultTextOK(), true);
			/*Handler mainHandler = new Handler(mContext.getMainLooper());
			Runnable myRunnable = new Runnable() {
				@Override
				public void run() {
					showResultDialog(true, ct.getNazev(), ct.getResultTextOK(), true);
				}
			};
			mainHandler.post(myRunnable);
			*/
		}else {
			cteckaAktivni = true;
		}
	}

	@Override
	public void runFromStartTaskDialog() {

	}
}
