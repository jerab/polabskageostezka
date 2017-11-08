package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Stanoviste;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratStanovistePodleUri;
import static cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config.vratUlohuPodleUri;

public class QRReadActivity extends BaseActivity {
    SurfaceView cameraPreview;
    final Context context = this;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    Intent intToWeb;
    String url;
    Button btnWeb, btnTask;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
        setContentView(R.layout.activity_qrread);

        // reader potreby - barcode reader, camera atp.
        cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
        //txtResult = (TextView) findViewById(R.id.txtResult);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        btnTask = (Button) findViewById(R.id.btnQRtask);
        btnWeb = (Button) findViewById(R.id.btnQRweb);
        btnTask.setEnabled(false);
        btnWeb.setEnabled(false);
        intToWeb = new Intent(Intent.ACTION_VIEW);

        btnWeb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(intToWeb);
			}
		});

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize((int)(metrics.heightPixels*0.9), (int)(metrics.widthPixels*0.9))
                .setAutoFocusEnabled(true)
                .build();
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRReadActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
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
                    Log.d("GEO QR ", String.valueOf(qrcodes.valueAt(0)));
                    Log.d("GEO QR ", String.valueOf(qrcodes.valueAt(0).displayValue));
					Log.d("GEO QR ", String.valueOf(qrcodes.valueAt(0).rawValue));
                    url = String.valueOf(qrcodes.valueAt(0).displayValue);

					try {
						final Stanoviste st = vratStanovistePodleUri(url);
						Log.d("GEO QR ", "Stanoviste: " + st.getUrl());
						if(st != null) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showWebTaskDialog(st);
									cameraSource.stop();
								}
							});

							// jedna se o validni URL => zobrazeni tlacitka pro prechod na web
						}else if(URLUtil.isValidUrl(url)){
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									intToWeb.setData(Uri.parse(url));
									btnWeb.setEnabled(true);
								}
							});
						}
					}catch (Exception e){
						Log.d("GEO QR catch", e.toString());
						//Toast.makeText(QRReadActivity.this,"Nactena uloha neexistuje",Toast.LENGTH_SHORT).show();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								btnTask.setEnabled(false);
							}
						});
					}


                        // todo muzes jit jen na link z naseho seznamu
                    if(URLUtil.isValidUrl(url)){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnWeb.setEnabled(true);
                            }
                        });
                    }
                }
            }
        });

    }

    private void showWebTaskDialog(@Nullable final Stanoviste st) {
    	//cameraSource.stop();
    	final Dialog d = new Dialog(context);
		//d.setTitle(getResources().getString(R.string.otevreniUlohyTitle) + t.getLabel());
		d.setContentView(R.layout.open_task_dialog);
		Button butWeb = (Button) d.findViewById(R.id.buttonWeb);
		Button butTask = (Button) d.findViewById(R.id.buttonTask);
		ImageButton closeBtn = (ImageButton) d.findViewById(R.id.closeButton);
		TextView title = (TextView) d.findViewById(R.id.title_txt);
		TextView popis = (TextView) d.findViewById(R.id.content_txt);
		title.setText("Stanoviště " + st.getNazev() + " (" + st.getCislo() + ")");

		final Task t = vratUlohuPodleUri(st.getUrl());
		Log.d("GEO QR", "Dialog Task: " + t.toString());
		if(t == null) {
			butTask.setVisibility(View.GONE);
			popis.setText("Toto stanoviště není součástí úloh v rámci aplikace. Můžete se podívat na webové stránky projektu pro bližší informace o " +
					"hornině.");
		}else {
			popis.setText("Podařilo se Vám načíst další úlohu. Buď můžete úlohu spustit nebo se podívat na webové stránky projektu pro bližší informace o " +
					"hornině.");
			butTask.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					runNextQuest(t.getId(), QRReadActivity.this);
				}
			});
		}

		butWeb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				d.dismiss();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(st.getUrl()));
				startActivity(i);
			}
		});

		closeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("GEO QR", "Dialog closing...");
				try {
					cameraSource.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				d.dismiss();
			}
		});

		d.show();

	}

    public void openTask(View view) {
        // todo
        // k seznamu uloh pole s odkazy?
    }

    public void goWeb(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW,
                Uri.parse(url));
        startActivity(i);
    }
}
