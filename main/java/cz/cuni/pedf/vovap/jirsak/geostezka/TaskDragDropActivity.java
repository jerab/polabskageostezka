package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseApp;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.DragDropTargetLayout;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ImageAndDensityHelper;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.TaskDragDropAdapter;

public class TaskDragDropActivity extends BaseTaskActivity {
	private static final String LOG_TAG = "Geo - DD activity";
	private static final float REAL_SIRKA_PODKLADOV_OBR = 1080;
	Context mContext;
	DragDropTask dd;
    GridView llDD;
    RelativeLayout rlDD;
    int[] obrazky;
    int[] obrazkyCile;
    int[] obrazkyCileAfter;
	ImageView backgroundImage;
    ImageView[] ivs;
	ImageView confirmButt;
    DragDropTargetLayout[] tvs;
    Point[] pObjs;
    Point[] pTrgs;
    InitDB db = new InitDB(this);
    int odpocet = 0;
    int stav = 0;
	float dragWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_drag_drop_zula);

		confirmButt = (ImageView)findViewById(R.id.confirmTask);
		llDD = (GridView) findViewById(R.id.llDD);
		rlDD = (RelativeLayout) findViewById(R.id.rlDD);
		backgroundImage = (ImageView) findViewById(R.id.ivDDZula);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        dd = (DragDropTask) Config.vratUlohuPodleID(predaneID);
        db.open();
        stav = db.vratStavUlohy(dd.getId());
        if (stav == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(dd.getId());
			UkazZadani(dd.getNazev(), dd.getZadani());
		}
        db.close();

        mContext = this;
        obrazky = dd.getBankaObrazku();
        obrazkyCile = dd.getBankaObrCile();
        obrazkyCileAfter = dd.getBankaObrCile2();
        pObjs = dd.getSouradniceObj();
        pTrgs = dd.getSouradniceCil();

		Resources r = getResources();
		backgroundImage.setImageResource(obrazky[0]);
		Log.d(LOG_TAG, "display width: " + r.getDisplayMetrics().widthPixels);



		//dragWidth = ImageAndDensityHelper.getDensityDependSize(r, (int) r.getDimension(R.dimen.dimTaskDragDrop_sourceImg_width));
		dragWidth = (int) r.getDimension(R.dimen.dimTaskDragDrop_sourceImg_width);
        //float height = width;
		Log.d(LOG_TAG, "Image top width: " + dragWidth);

		/// nastaveni policek pro pretahovani
		ivs = new ImageView[obrazky.length-1];
		GridView.LayoutParams gwLayoutParams = new GridView.LayoutParams((int) dragWidth, (int) dragWidth);
        for (int i = 0; i < (obrazky.length - 1);i++)
        {
            ivs[i] = new ImageView(this);
            ivs[i].setImageResource(obrazky[i+1]);
            ivs[i].setId(i+100);
            ivs[i].setTag(String.valueOf(obrazky[i+1]));
            ivs[i].setLayoutParams(gwLayoutParams);
            ivs[i].setOnTouchListener(new MyTouchListener());
        }
		llDD.setAdapter(new TaskDragDropAdapter(this, ivs));

		/// nastaveni cilovych policek pro pretahovani
		setDropArea();
    }

	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
		if(result) {
			confirmButt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					startActivity(new Intent(TaskDragDropActivity.this, DashboardActivity.class));
					finish();
				}
			});
		}
	}

	private void setDropArea() {
		final int dragWidthTarget = (int)getResources().getDimension(R.dimen.dimTaskDragDrop_targetImg_width);
		final ViewTreeObserver vto = backgroundImage.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				/// nastaveni cilovych policek pro pretahovani
				tvs = new DragDropTargetLayout[obrazkyCile.length];
				RelativeLayout.LayoutParams layoutParams;

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);

				int polovina = (int) (size.x / 2);
				Log.d(LOG_TAG, "POLOVINA: " + polovina + " | " + pTrgs[2].x);
				int after = 0;

				float scaleFactor = backgroundImage.getWidth() / REAL_SIRKA_PODKLADOV_OBR;
				Log.d(LOG_TAG, "zula width: " + backgroundImage.getWidth());
				Log.d(LOG_TAG, "scale factor pro odsazeni: " + scaleFactor);
				Resources r = mContext.getResources();
				for (int i = 0; i<tvs.length;i++)
				{
					int newWH = ImageAndDensityHelper.getDensityDependSize(r, (int)dragWidthTarget, 10);
					layoutParams =  new RelativeLayout.LayoutParams(newWH, newWH);
					layoutParams.leftMargin = (int) (pTrgs[i].x * scaleFactor);
					if(dd.getOrientaceDropZony(i) == "left") {
						layoutParams.leftMargin -= newWH;
					}
					layoutParams.topMargin = (int) (pTrgs[i].y * scaleFactor) - newWH;
					Log.d(LOG_TAG, "left Margin " + i + " : " + layoutParams.leftMargin);
					if(obrazkyCileAfter.length > i) {
						after = obrazkyCileAfter[i];
					}else {
						after = 0;
					}
					Log.d(LOG_TAG, "new ITEM " + i + " : " + layoutParams.topMargin);
					tvs[i] = new DragDropTargetLayout(mContext, i+1000,
							obrazkyCile[i],
							after,
							new int[]{(int)pTrgs[i].x, (int)pTrgs[i].y},
							String.valueOf(obrazky[i+1]),
							(dd.getOrientaceDropZony(i) == "left")
					);
					//Log.d("Geo Task DD", "POLOVINA pro polozku: " + String.valueOf(layoutParams.leftMargin > polovina));
					rlDD.addView(tvs[i], layoutParams);
				}
				removeOnGlobalLayoutListener(backgroundImage, this);
			}
		});
	}

	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
		v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
	}

	private static class MyTouchListener implements View.OnTouchListener{
            @Override
            public boolean onTouch(View view, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    String viewTag = String.valueOf(view.getTag());

                    ClipData.Item item = new ClipData.Item(viewTag);
                    ClipData dragData = new ClipData(
                            viewTag,
                            new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},item
                    );
                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view);
                    // Starts the drag
                    view.startDrag(
                            dragData, //  the data to be drag
                            myShadow, // the drag shadow builder
                            null, // no need to use local data
                            0 // flags
                    );
                    //view.setVisibility(View.INVISIBLE);
                    return true;
                }

                return false;
            }
        }

    public void zaznamenejOdpoved(int idOdpovedi) {
		odpocet++;
		Log.d(LOG_TAG, "Odpoved k zaznamenani: " + idOdpovedi + " / celkem zbyva: " + (obrazkyCile.length - odpocet));
		if (odpocet == obrazkyCile.length)		{
			//Toast.makeText(getApplicationContext(), "Uloha dokoncena", Toast.LENGTH_SHORT).show();
			InitDB db = new InitDB(getApplicationContext());
			db.open();
			db.zapisTaskDoDatabaze(dd.getId(),System.currentTimeMillis());
			db.close();
			confirmButt.setVisibility(View.VISIBLE);
			showResultDialog(true, dd.getNazev(), dd.getResultTextOK(), false);
		}
	}
}
