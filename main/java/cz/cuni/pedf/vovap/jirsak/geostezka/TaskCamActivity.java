package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.TaskResultDialog;

public class TaskCamActivity extends BaseTaskActivity {
    private final static String LOG_TAG = "Geo TaskCam";

	SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    CamTask ct;
    int pocetPolozek;
    int steps;
    String[] vysledek;
    ToggleButton[] tbs;
    RelativeLayout rlts;
    private int pokus;
    InitDB db = new InitDB(this);

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
                        }
                    catch (IOException e) {
                        e.printStackTrace();
                        }
                    }
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_cam);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        // je treba upravit v pripade pridani CAMTASK do normalnich uloh
        ct = (CamTask) Config.vratIntroUlohuPodleID(predaneID);
        steps = 0;
        db.open();
        if (db.vratStavUlohy(ct.getId())==0)
            db.odemkniUlohu(ct.getId());
        db.close();
        UkazZadani(ct.getNazev(), ct.getZadani());
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // camtask potreby - barcode reader, camera atp.
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult = (TextView) findViewById(R.id.txtResult);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize((metrics.heightPixels), (metrics.widthPixels))
                .setAutoFocusEnabled(true)
                .build();
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TaskCamActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release()
            {
                barcodeDetector.release();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrcodes = detections.getDetectedItems();

                if (qrcodes.size() != 0)
                {
                    Log.d(LOG_TAG, String.valueOf(qrcodes.valueAt(0)));
                    Log.d(LOG_TAG, String.valueOf(qrcodes.valueAt(0).displayValue));
                        /// projdi vsechny vysledky a porovnej spravnost
                        for(int k = 0; k<vysledek.length; k++)
                        {
                            Log.d(LOG_TAG,String.valueOf(qrcodes.valueAt(0).displayValue));
                            if (String.valueOf(qrcodes.valueAt(0).displayValue).equals(vysledek[k]))
                            {
                                vysledek[k] = getString(R.string.CamTaskStringFinished);
                                zapisVysledek(k);
                                pokus = k;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tbs[pokus].setChecked(true);

                                    }
                                });
                                if (checkIfComplete() && (ct.getRetezId()==-1)) {
									Log.d(LOG_TAG," prvni podminka");
									showResultDialog(true, ct.getNazev(), ct.getResultTextOK(), true);
									/*
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Log.d(LOG_TAG,"TaskCamAct"," prvni podminka");
											Toast.makeText(getApplicationContext(),"Uloha dokoncena",Toast.LENGTH_LONG).show();
											startActivity(new Intent(TaskCamActivity.this, DashboardActivity.class));
											finish();
										}
									});*/
								} else if (checkIfComplete()) {
                                    Log.d(LOG_TAG," druha podminka");
									showResultDialog(true, ct.getNazev(), ct.getResultTextOK(), true);

                                }
                            }
                        }
                    //}
                }
            }
        });
        rlts = (RelativeLayout) findViewById(R.id.rlToggles);
        pocetPolozek = ct.getPocetCilu();
        vysledek = updateTask(ct);
        checkIfComplete();
        tbs = new ToggleButton [pocetPolozek];

        for (int k = 0; k<pocetPolozek;k++)
        {
            RelativeLayout.LayoutParams newParams = new RelativeLayout
                    .LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            //vysledek[k]= String.valueOf(k);
            tbs[k] = new ToggleButton(this);
            tbs[k].setTextOff("0");
            tbs[k].setTextOn("1");
            tbs[k].setEnabled(false);
            //tbs[k].setTag(vysledek[k]);
            if (vysledek[k].equals(getString(R.string.CamTaskStringFinished))){
                tbs[k].setChecked(true);
                Log.d(LOG_TAG,"Already done " + String.valueOf(k));
            }
            tbs[k].setId(100+k);
            tbs[k].setLayoutParams(newParams);
            /// serazeni toggleu
            if (k==4){
                newParams.addRule(RelativeLayout.BELOW, 100);
                Log.d(LOG_TAG,"over " + String.valueOf(k));
                Log.d(LOG_TAG,"over " + String.valueOf(k-1));
            } else if (k>4) {
                newParams.addRule(RelativeLayout.RIGHT_OF, 99+k);
                newParams.addRule(RelativeLayout.BELOW, 100);
                Log.d(LOG_TAG,"over " + String.valueOf(k));
                Log.d(LOG_TAG,"over " + String.valueOf(k-1));
            } else if (k>0) {
                newParams.addRule(RelativeLayout.RIGHT_OF, 99+k);
                Log.d(LOG_TAG,"over " + String.valueOf(k));
                Log.d(LOG_TAG,"over " + String.valueOf(k-1));
            }
            else {
                newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                Log.d(LOG_TAG,"over " +String.valueOf(k));
            }
            tbs[k].setLayoutParams(newParams);
            rlts.addView(tbs[k]);
        }
    }

    private void zapisVysledek(int target) {
        //Calendar c = Calendar.getInstance();
        Log.d(LOG_TAG,"Write steps");
        InitDB db = new InitDB(this);
        try {
            db.open();
            db.zapisCamTaskTarget(ct.getId(),target, (int) System.currentTimeMillis());
            db.close();
        } catch (Exception e) {
            Log.d(LOG_TAG,"e:" + e.toString());
        }

    }

    public String[] updateTask(CamTask c) {
        String[] origo;
        int[] targety;
        InitDB db = new InitDB(this);
        db.open();
        origo = c.getVysledky();
        targety = db.vratVsechnyTargetyCamTaskPodleId(c.getId());
        Log.d(LOG_TAG,"what does target carry?" + targety.length);

        if (targety.length == origo.length)
        {
           Log.d(LOG_TAG, "Task completed");
            Toast.makeText(this,"Uloha dokoncena",Toast.LENGTH_SHORT).show();
            // runFromResultDialog(true, true);
            // todo : zkontrolovat spravnost ?!?!?!
            for (int k=0; k < origo.length;k++){
                origo[targety[k]] = getString(R.string.CamTaskStringFinished);
                Log.d(LOG_TAG,"Vysledky z DB: " + String.valueOf(targety[k]));
            }
        } else if (targety != null)
        {
            for (int i=0; i<targety.length;i++)
            {
                origo[targety[i]] = getString(R.string.CamTaskStringFinished);
                Log.d(LOG_TAG,"Vysledky z DB: " + String.valueOf(targety[i]));
            }
        }
        db.close();
        return origo;
    }
    private boolean checkIfComplete()
    {
        Log.d(LOG_TAG,"checkuju");
        int check = 0;
        for (int i = 0; i<vysledek.length;i++){
            if(vysledek[i].equals(getString(R.string.CamTaskStringFinished)))
            {
                check++;
            }
        }
        if (check==vysledek.length){
            InitDB db = new InitDB(this);
            db.open();
            db.zapisTaskDoDatabaze(ct.getId(), System.currentTimeMillis());
            db.close();
            return true;
        } else {
            return false;
        }
    }

	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
		Log.d(LOG_TAG, "Run from Dialog... ");
		if(result) {
			Task t = Config.vratIntroUlohuPodleID(ct.getRetezId());
			final int idDalsi = ct.getRetezId();
			Log.d(LOG_TAG, "TaskCamAct idDalsi: " + idDalsi + "/// typ: " + t.getTyp());
			switch (t.getTyp()) {
				case 1:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Intent i = new Intent(TaskCamActivity.this, TaskCamActivity.class);
							i.putExtra("id", idDalsi);
							startActivity(i);
							finish();
						}
					});
					break;
				case 2:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Intent i = new Intent(TaskCamActivity.this, TaskDragDropActivity.class);
							i.putExtra("id", idDalsi);
							startActivity(i);
						}
					});

					break;
				case 3:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Intent i = new Intent(TaskCamActivity.this, TaskQuizActivity.class);
							i.putExtra("id", idDalsi);
							startActivity(i);
						}
					});

					break;
				case 4:
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Intent i = new Intent(TaskCamActivity.this, TaskARTestActivity.class);
							i.putExtra("id", idDalsi);
							startActivity(i);
						}
					});

					break;
			}
		}
	}

    /*@Override
    public void SetCurentTask(int ID) {
        getSharedPreferences("ACTIVE_TASK", MODE_PRIVATE).edit().putInt(getString(R.string.taskNumber), ID).apply();
    }
    @Override
    public int GetCurentTask(){
        return getSharedPreferences("ACTIVE_TASK", MODE_PRIVATE).getInt("AktivniUloha", 1);
    }*/
}
