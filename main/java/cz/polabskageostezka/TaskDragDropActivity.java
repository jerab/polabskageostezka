package cz.polabskageostezka;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.polabskageostezka.tasks.DragDropTask;
import cz.polabskageostezka.utils.BaseTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.DragDropTargetLayout;
import cz.polabskageostezka.utils.ImageAndDensityHelper;
import cz.polabskageostezka.utils.InitDB;
import cz.polabskageostezka.utils.TaskDragDropAdapter;

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
	String[] textyCileAfter;
	ImageView backgroundImage;
	TaskDragDropAdapter gridAdapter;
    ArrayList<ImageView> ivs = null;
	ImageView confirmButt;
    DragDropTargetLayout[] tvs;
    Point[] pObjs;
    Point[] pTrgs;
	Point[] pTrgsRozmer;
    InitDB db;
    int odpocet = 0;
    int stav = 0;
	int dragWidth;
	int[] hotoveStepy;
	boolean zapsan = false;
	boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        dd = (DragDropTask) Config.vratUlohuPodleID(predaneID);
        super.init(dd.getNazev(), dd.getZadani(), dd.getId());

		// nastav layout a view
		setContentView(dd.getLayoutDraw());
		confirmButt = (ImageView)findViewById(R.id.confirmTask);
		llDD = (GridView) findViewById(R.id.llDD);
		rlDD = (RelativeLayout) findViewById(R.id.rlDD);
		backgroundImage = (ImageView) findViewById(R.id.ivDDBck);

		/// nastaveni sekundarniho pozadi za primarnim pozadim (pro slepenec)
		if(dd.getBackgroundDrawCount() > 1) {
			backgroundImage.setImageResource(dd.getBackgroundDraw(1));
			ImageView bckImViewFront = (ImageView) findViewById(R.id.ivDDBckFront);
			/// nastavit 'clonu' jako horni pozadi nad oblasti DROP
			bckImViewFront.setImageResource(dd.getBackgroundDraw(0));
			//bckImViewFront.setAlpha(0.3f);
		/// napr. Zula
		}else {
			backgroundImage.setImageResource(dd.getBackgroundDraw(0));
		}

		db = new InitDB(this);
		db.open();
        stav = db.vratStavUlohy(dd.getId());
        if (stav == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(dd.getId());
			UkazZadani(dd.getNazev(), dd.getZadani());
		} else {
			hotoveStepy = db.vratVsechnyTargetyDragDropTaskPodleId(dd.getId());
		}
	    if (stav==Config.TASK_STATUS_DONE) {
			allowConfirmBuut();
		}
        db.close();

        mContext = this;
        obrazky = dd.getBankaObrazku();
        obrazkyCile = dd.getBankaObrCile();
        obrazkyCileAfter = dd.getBankaObrCile2();
        textyCileAfter = dd.getBankaTextCile2();
        pObjs = dd.getSouradniceObj();
        pTrgs = dd.getSouradniceCil();
		pTrgsRozmer = dd.getRozmeryCil();

		Resources r = getResources();
		Log.d(LOG_TAG, "display width: " + r.getDisplayMetrics().widthPixels);

		//dragWidth = ImageAndDensityHelper.getDensityDependSize(r, (int) r.getDimension(R.dimen.dimTaskDragDrop_sourceImg_width));
		dragWidth = (int) r.getDimension(R.dimen.dimTaskDragDrop_sourceImg_width);
		Log.d(LOG_TAG, "Image top width: " + dragWidth);

		/// nastaveni policek pro pretahovani
		setDragField();
		/// nastaveni cilovych policek pro pretahovani
		setDropArea();
    }

    private void setDragField() {
		Log.d(LOG_TAG, "Setting DragField ... ");
		GridView.LayoutParams gwLayoutParams = new GridView.LayoutParams((int) dragWidth, (int) dragWidth);
		ivs = new ArrayList<>();
		ImageView im;
		for (int i = 0; i < obrazky.length;i++)
		{
			im = new ImageView(this);
			im.setImageResource(obrazky[i]);
			im.setId(i+100);
			im.setTag(String.valueOf(obrazky[i]));
			im.setLayoutParams(gwLayoutParams);
			im.setOnTouchListener(new MyTouchListener());
			if(Config.TASK_SLEPENEC_ID == dd.getId()) {
				im.setRotation((float)(Math.random() * 360));
			}
			ivs.add(i,im);
		}
		Collections.shuffle(ivs);

	}

    public void removeViewFromGrid(String tag, boolean isGridFull) {
		Log.d(LOG_TAG, "Removing item with tag: " + tag);
		int rim = -1;
    	for(int i = 0; i < ivs.size(); i++) {
			Log.d(LOG_TAG, "......Removing check item: " + ivs.get(i).getTag());
    		if(tag.equals(ivs.get(i).getTag())) {
				Log.d(LOG_TAG, "......Removing item: " + ivs.get(i).getTag());
    			rim = i;
    			break;
			}
		}
		if(rim > -1) {
			Log.d(LOG_TAG, "Remove on index: " + rim);
			if(isGridFull) {
				gridAdapter.removeItem(rim);
			}else {
				ivs.remove(rim);
			}
		}

	}

	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
    	if(closeTask) {
			runNextQuest(dd.getRetezId(), mContext);
		}else if(result && finished) {
			allowConfirmBuut();
		}
	}

	@Override
	public void runFromStartTaskDialog() {

	}

	private void allowConfirmBuut() {
		confirmButt.setVisibility(View.VISIBLE);
    	confirmButt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(LOG_TAG, "spoustim dalsi ulohu ...");
				runNextQuest(dd.getRetezId(), mContext);
				finish();
			}
		});
	}

	private void setHotoveStepy() {
		int step;
    	for (int k=0;k<hotoveStepy.length;k++) {
			step = hotoveStepy[k] - 1000;
			tvs[step].setTargetResult1(obrazky[step]);
			tvs[step].changeStatusAndTargetResource(0);
			removeViewFromGrid(String.valueOf(obrazky[step]), false);
		}
		odpocet=hotoveStepy.length;
	}

	private void setDropArea() {
		final int dropWidthTarget;
		if(Config.TASK_ZULA_ID == dd.getId()) {
			dropWidthTarget = (int)getResources().getDimension(R.dimen.dimTaskDragDrop_targetImg_width_zula);
		}else if(Config.TASK_UHLI_ID == dd.getId()) {
			dropWidthTarget = (int) getResources().getDimension(R.dimen.dimTaskDragDrop_targetImg_width_uhli);
		}else {
			dropWidthTarget = (int) getResources().getDimension(R.dimen.dimTaskDragDrop_targetImg_size_def);
		}
		//Log.d(LOG_TAG, "Task ID: " + dd.getId());
		Log.d(LOG_TAG, "DropTarget width: " + dropWidthTarget);


		final ViewTreeObserver vto = backgroundImage.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				/// nastaveni cilovych policek pro pretahovani
				tvs = new DragDropTargetLayout[pTrgs.length];
				RelativeLayout.LayoutParams layoutParams;

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);

				int polovina = (int) (size.x / 2);
				Log.d(LOG_TAG, "POLOVINA: " + polovina + " | " + pTrgs[2].x);
				int after, target;
				String[] afterT;

				float scaleFactor = backgroundImage.getWidth() / REAL_SIRKA_PODKLADOV_OBR;
				Log.d(LOG_TAG, "background width: " + backgroundImage.getWidth());
				Log.d(LOG_TAG, "scale factor pro odsazeni: " + scaleFactor);
				Resources r = mContext.getResources();
				int w,h,leftExtra,topExtra;

				for (int i = 0; i<pTrgs.length;i++)
				{
					if(pTrgsRozmer.length > i) {
						w = (int) (pTrgsRozmer[i].x * scaleFactor);
						h = (int) (pTrgsRozmer[i].y * scaleFactor);
						leftExtra = pTrgsRozmer[i].x / 2;
						topExtra = pTrgsRozmer[i].y / 2;
					}else {
						h = w = ImageAndDensityHelper.getDensityDependSize(r, (int)dropWidthTarget, 10);
						topExtra = leftExtra = 0;
					}
					layoutParams = new RelativeLayout.LayoutParams(w, h);
					layoutParams.leftMargin = (int) ((pTrgs[i].x - leftExtra) * scaleFactor);
					if(dd.getOrientaceDropZony(i) == "left") {
						layoutParams.leftMargin -= w;
					}
					layoutParams.topMargin = (int) ((pTrgs[i].y + topExtra) * scaleFactor) - h;
					Log.d(LOG_TAG, "left Margin " + i + " : " + layoutParams.leftMargin);

					if(obrazkyCile.length > i) {
						target = obrazkyCile[i];
					}else {
						target = 0;
					}

					if(obrazkyCileAfter.length > i) {
						after = obrazkyCileAfter[i];
					}else {
						after = 0;
					}

					afterT = new String[] {"", dd.getNadpisCile2(i)};

					if(textyCileAfter.length > i) {
						afterT[0] = textyCileAfter[i];
					}else {
						afterT[0] = "";
					}

					tvs[i] = new DragDropTargetLayout(mContext, i+1000,
							new int[] {w,h},
							target,
							after,
							afterT,
							dd.getId(),
							String.valueOf(obrazky[i]),
							(dd.getOrientaceDropZony(i) == "left")
					);
					rlDD.addView(tvs[i], 1, layoutParams);
				}

				if (hotoveStepy!=null) {
					setHotoveStepy();
				}

				gridAdapter = new TaskDragDropAdapter(mContext, ivs);
				llDD.setAdapter(gridAdapter);

				Log.d(LOG_TAG, "number of IVSs: " + ivs.size());

				removeOnGlobalLayoutListener(backgroundImage, this);
			}
		});
	}

	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
		v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
	}

	public static class MyTouchListener implements View.OnTouchListener{
            @Override
            public boolean onTouch(final View view, MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    String viewTag = String.valueOf(view.getTag());

                    ClipData.Item item = new ClipData.Item(viewTag);
                    ClipData dragData = new ClipData(
                            viewTag,
                            new String[] {ClipDescription.MIMETYPE_TEXT_PLAIN},item
                    );
                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(view) {

						@Override
						public void onDrawShadow(Canvas canvas) {
							//canvas.rotate(view.getRotation());
							super.onDrawShadow(canvas);
						}
						@Override
						public void onProvideShadowMetrics(Point shadowSize,
														   Point shadowTouchPoint) {
							shadowSize.set(view.getWidth(), view.getHeight());
							shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
						}
					};
                    // Starts the drag
                    view.startDrag(
                            dragData, //  the data to be drag
                            myShadow, // the drag shadow builder
                            view, // no need to use local data
                            0 // flags
                    );
                    return true;
                }

                return false;
            }
        }

    public void zaznamenejOdpoved(int idOdpovedi) {
		odpocet++;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext, "Správně!",Toast.LENGTH_SHORT).show();
			}
		});
		if (hotoveStepy == null){
			Log.d(LOG_TAG," podminka 1 - jeste nebyl zapsan zadny step");
			zapisStep(idOdpovedi);
		} else {
			for (int i = 0;i < hotoveStepy.length; i++){
				if (hotoveStepy[i] == idOdpovedi)
					zapsan = true;
			}
			Log.d(LOG_TAG," podminka 2 - step cislo: " + String.valueOf(idOdpovedi)+ " a je zapsan: " + String.valueOf(zapsan));
			if (!zapsan) {
				Log.d(LOG_TAG," podminka 3 - nenasel v hotovych - pridej do db");
				zapisStep(idOdpovedi);
			}
			zapsan=false;
		}
		Log.d(LOG_TAG, "Odpoved k zaznamenani: " + idOdpovedi + " / celkem zbyva: " + (obrazkyCile.length - odpocet));

		/// uloha je dokoncena
		if (odpocet >= pTrgs.length) {
			finished = true;
			InitDB db = new InitDB(getApplicationContext());
			db.open();
			db.zapisTaskDoDatabaze(dd.getId(),System.currentTimeMillis());
			db.close();
			/// kdyz je jeste neco pro klikani na after, tak nezavirej ulohu
			showResultDialog(true, dd.getNazev(), dd.getResultTextOK(), !(obrazkyCileAfter.length > 0 || dd.getBankaTextCile2().length > 0));
		}
	}

	private void zapisStep(int id) {
		Log.d(LOG_TAG," Zapisuji step DD do DB, cislo:" + String.valueOf(id));
		db.open();
		db.zapisDragDropTaskTarget(dd.getId(),id,(int) System.currentTimeMillis());
		db.close();
	}

}
