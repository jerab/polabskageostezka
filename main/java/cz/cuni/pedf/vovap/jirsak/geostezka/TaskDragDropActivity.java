package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.DragDropTargetLayout;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.TaskDragDropAdapter;

public class TaskDragDropActivity extends BaseTaskActivity {
    DragDropTask dd;
    GridView llDD;
    RelativeLayout rlDD;
    int[] obrazky;
    int[] obrazkyCile;
    int[] obrazkyCileAfter;
    ImageView[] ivs;
    DragDropTargetLayout[] tvs;
    //TextView resultInfo;
    Context mContext;
    Point[] pObjs;
    Point[] pTrgs;
    InitDB db = new InitDB(this);
    int odpocet = 0;
    int stav = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_drag_drop_zula);

        //nacti spravny task podle intentu
        Intent mIntent = getIntent();
        int predaneID = mIntent.getIntExtra("id", 0);
        dd = (DragDropTask) Config.vratUlohuPodleID(predaneID);
        db.open();
        stav = db.vratStavUlohy(dd.getId());
        if (stav == 0)
            db.odemkniUlohu(dd.getId());
		else if (stav == 2)
        	//findViewById(R.id.btnDDBack).setVisibility(View.VISIBLE);
        db.close();
        UkazZadani(dd.getNazev(), dd.getZadani());
        mContext = getApplicationContext();
        obrazky = dd.getBankaObrazku();
        obrazkyCile = dd.getBankaObrCile();
        obrazkyCileAfter = dd.getBankaObrCile2();
        pObjs = dd.getSouradniceObj();
        pTrgs = dd.getSouradniceCil();
        rlDD = (RelativeLayout) findViewById(R.id.rlDD);
        //rlDD.setBackground(getResources().getDrawable(obrazky[0]));
        ImageView iv = (ImageView) findViewById(R.id.ivDDZula);
        iv.setImageResource(obrazky[0]);

		llDD = (GridView) findViewById(R.id.llDD);

		/*resultInfo = (TextView) findViewById(R.id.tvDDResultInfo);
        */
        Resources r = getResources();
        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, r.getDisplayMetrics());
        float height = width;//TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());


		/// nastaveni policek pro pretahovani
		ivs = new ImageView[obrazky.length-1];
		GridView.LayoutParams gwLayoutParams = new AbsListView.LayoutParams((int) width, (int) height);
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
		tvs = new DragDropTargetLayout[obrazkyCile.length];
		RelativeLayout.LayoutParams layoutParams;

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		int polovina = (int) (size.x / 2);
		Log.d("Geo Task DD", "POLOVINA: " + polovina + " | " + (int)pTrgs[2].x);
		int after = R.drawable.afterclick;
        for (int i = 0; i<tvs.length;i++)
        {
			/*Log.d("GEO DD Task", "top Margin " + i + " : " + layoutParams.topMargin);
			Log.d("GEO DD Task", "left Margin " + i + " : " + layoutParams.leftMargin);*/
			layoutParams =  new RelativeLayout.LayoutParams((int) width + 10,(int) height + 10);
			layoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pTrgs[i].x, r.getDisplayMetrics());
			layoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pTrgs[i].y, r.getDisplayMetrics());

			if(obrazkyCileAfter[i] > 0) {
				after = obrazkyCileAfter[i];
			}
			Log.d("GEO DD Task", "new ITEM " + i + " : " + layoutParams.topMargin);
			tvs[i] = new DragDropTargetLayout(this, i+1000,
					obrazkyCile[i],
					after,
					new int[]{(int)pTrgs[i].x, (int)pTrgs[i].y},
					String.valueOf(obrazky[i+1]),
					(layoutParams.leftMargin > polovina)
			);
			//Log.d("Geo Task DD", "POLOVINA pro polozku: " + String.valueOf(layoutParams.leftMargin > polovina));
			rlDD.addView(tvs[i], layoutParams);
        }

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

    public void navratDashboard(View view) {
        startActivity(new Intent(TaskDragDropActivity.this, DashboardActivity.class));
    }

    public void zaznamenejOdpoved(int idOdpovedi) {
		odpocet++;
		if (odpocet == obrazkyCile.length)		{
			Toast.makeText(getApplicationContext(), "Uloha dokoncena", Toast.LENGTH_SHORT).show();
			InitDB db = new InitDB(getApplicationContext());
			db.open();
			db.zapisTaskDoDatabaze(dd.getId(),System.currentTimeMillis());
			db.close();
			//findViewById(R.id.btnDDBack).setVisibility(View.VISIBLE);
		}
	}
}
