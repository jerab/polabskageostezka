package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.fitness.data.Application;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class TaskCamActivity extends BaseTaskActivity {
    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    CamTask ct;
    int pocetPolozek;
    String[] vysledek;
    ToggleButton[] tbs;
    RelativeLayout rlts;

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
        ct = (CamTask) Config.vratUlohuPodleID(predaneID);

        UkazZadani(this, ct.getNazev(), ct.getZadani());

        // camtask potreby - barcode reader, camera atp.
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        txtResult = (TextView) findViewById(R.id.txtResult);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(720, 480)
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
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if (qrcodes.size() != 0)
                {

                    for (int i = 0; i<qrcodes.size();i++)
                    {
                        Log.d("GEO", String.valueOf(qrcodes.valueAt(i)));
                        Log.d("GEO", String.valueOf(qrcodes.valueAt(i).displayValue));
                        /*switch (qrcodes.valueAt(i).displayValue)
                        {
                            case "0":

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tbs[0].setChecked(true);
                                    }
                                });
                                break;
                            case "1":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tbs[1].setChecked(true);
                                    }
                                });
                                break;
                            case "2":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tbs[2].setChecked(true);
                                    }
                                });
                                break;
                            case "3":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tbs[3].setChecked(true);
                                    }
                                });
                        }*/
                        /// projdi vsechny vysledky a porovnej spravnost
                        for(int k = 0; k<vysledek.length; k++)
                        {
                            if (String.valueOf(qrcodes.valueAt(i)).equals(vysledek[k]))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        /// jak se dostat ke spravnemu toggleu??? + zapis autoincrement do db (podtasksplnen)
                                        /// metoda??
                                        tbs[0].setChecked(true);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
        rlts = (RelativeLayout) findViewById(R.id.rlToggles);
        pocetPolozek = ct.getPocetCilu();
        vysledek = ct.getVysledky();
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
            tbs[k].setId(100+k);
            tbs[k].setLayoutParams(newParams);
            /// serazeni toggleu
            if (k>0){
                newParams.addRule(RelativeLayout.RIGHT_OF, 99+k);
                Log.d("GEO over", String.valueOf(k));
                Log.d("GEO over", String.valueOf(k-1));
            } else {
                newParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                newParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                Log.d("GEO over", String.valueOf(k));
            }
            tbs[k].setLayoutParams(newParams);
            rlts.addView(tbs[k]);
        }
    }

    public void UkazZadani (Context ctx, String nazev, String zadani)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle(nazev);
        alertDialog.setMessage(zadani);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void SetCurentTask(int ID) {
        getSharedPreferences("ACTIVE_TASK", MODE_PRIVATE).edit().putInt(getString(R.string.taskNumber), ID).apply();
    }
    @Override
    public int GetCurentTask(){
        return getSharedPreferences("ACTIVE_TASK", MODE_PRIVATE).getInt("AktivniUloha", 1);
    }
}
