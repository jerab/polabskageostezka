package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.GridTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;


public class TaskGridActivity extends BaseTaskActivity {
    GridTask gt;
    InitDB db;
    int stav;
    Context mContext;
    ImageView[] targets;
    int finished;
    int[] images;
    int[] correctImg;
    String[] texts;
    String[] correctText;
    String textholder;
    int start;
    int iterace;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_grid);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        gt = (GridTask) Config.vratUlohuPodleID(predaneID);
        db = new InitDB(this);
        db.open();
        stav = db.vratStavUlohy(gt.getId());
        if (stav == 0)
            db.odemkniUlohu(gt.getId());
        else if (stav == 2)
            finished=2;
        db.close();
        UkazZadani(gt.getNazev(), gt.getZadani());
        mContext = getApplicationContext();
        targets = new ImageView[] {(ImageView) findViewById(R.id.gTiV1),
                (ImageView) findViewById(R.id.gTiV2),
                (ImageView) findViewById(R.id.gTiV3),
                (ImageView) findViewById(R.id.gTiV4)};
        images = gt.getImages();
        texts = gt.getTexts();
        correctText = gt.getCorrectText();
        //correctImg = gt.getCorrectImg();
        start=0;
        iterace=0;
        loadImages();

    }

    private void loadImages() {
        /*
        List<Integer> imagesNew = new ArrayList<>();
        for (int i=0; i < 4; i++)
            imagesNew.add(images[i]);
        Collections.shuffle(imagesNew);
        for (int i = 0; i < 4; i++) {
            targets[i].setImageResource(imagesNew.get(i));
            for (int k=0;k<4; k++){
                if targets[i]
                targets[i].setTag();
            }*/
        for (int i=0; i < 4; i++) {
            targets[i].setImageResource(images[start+i]);
            targets[i].setTag(texts[start+i]);
            textholder = texts[start+i];
            if (texts[start+i].equals(correctText[iterace]))
            targets[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // excelent! done! alert dialog
                    alertDialog = new AlertDialog.Builder(TaskGridActivity.this).create();
                    alertDialog.setTitle("Výborně!");
                    alertDialog.setMessage(v.getTag().toString());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                    if (finished!=2){
                        for (int i=0; i < 4; i++) {
                            targets[i].setOnClickListener(null);
                        }
                        loadImages();
                    }
                    else {
                        if (gt.getRetezId() == -1) {
                            /*
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Uloha dokoncena",Toast.LENGTH_SHORT).show();
                                }
                            });*/
                            db.open();
                            db.zapisTaskDoDatabaze(gt.getId(),System.currentTimeMillis());
                            db.close();
                            startActivity(new Intent(TaskGridActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            Task t = Config.vratUlohuPodleID(gt.getRetezId());
                            final int idDalsi = gt.getRetezId();
                            Log.d("TaskCamAct","idDalsi: " + idDalsi + "/// typ: " + t.getTyp());
                            switch (t.getTyp()) {
                                case 1:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(TaskGridActivity.this, TaskCamActivity.class);
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
                                            Intent i = new Intent(TaskGridActivity.this, TaskDragDropActivity.class);
                                            i.putExtra("id", idDalsi);
                                            startActivity(i);
                                        }
                                    });

                                    break;
                                case 3:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(TaskGridActivity.this, TaskQuizActivity.class);
                                            i.putExtra("id", idDalsi);
                                            startActivity(i);
                                        }
                                    });

                                    break;
                                case 4:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(TaskGridActivity.this, TaskARTestActivity.class);
                                            i.putExtra("id", idDalsi);
                                            startActivity(i);
                                        }
                                    });

                                    break;
                                case 5:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(TaskGridActivity.this, TaskGridActivity.class);
                                            i.putExtra("id", idDalsi);
                                            startActivity(i);
                                        }
                                    });

                                    break;
                                        /*default:
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(),"Uloha dokoncena",Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(TaskQuizActivity.this, DashboardActivity.class));
                                                    finish();
                                                }
                                            });
                                            break;*/
                            }
                        }
                    }
                }
            });
            else
                targets[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // boo not cool
                        Toast.makeText(mContext, textholder, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        start = start + 4;
        iterace++;
        if (gt.getImages().length == start)
            finished=2;
        }
    }


