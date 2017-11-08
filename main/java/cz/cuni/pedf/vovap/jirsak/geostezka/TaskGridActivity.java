package cz.cuni.pedf.vovap.jirsak.geostezka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.GridTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.GridTaskAdapter;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.GridTaskItem;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;


public class TaskGridActivity extends BaseTaskActivity {
	private static final String LOG_TAG = "Geo - GridTask";
	public static final String VIEW_TAG_CORRECT = "xxxCORECTxxx";
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

	GridView itemWrap;
	ArrayList<GridTaskItem> actualSada;
	boolean finish = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_grid);

		//nacti spravny task podle intentu
		Intent mIntent = getIntent();
		gt = (GridTask) Config.vratUlohuPodleID(mIntent.getIntExtra("id", 0));
		super.init(gt.getNazev(), gt.getZadani());

		db = new InitDB(this);
		db.open();
		stav = db.vratStavUlohy(gt.getId());
		if (stav == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(gt.getId());
			UkazZadani(gt.getNazev(), gt.getZadani());
		}else if (stav == Config.TASK_STATUS_DONE) {
			finished = Config.TASK_STATUS_DONE;
		}
		db.close();

		mContext = getApplicationContext();
		/*targets = new ImageView[]{
				(ImageView) findViewById(R.id.gTiV1),
				(ImageView) findViewById(R.id.gTiV2),
				(ImageView) findViewById(R.id.gTiV3),
				(ImageView) findViewById(R.id.gTiV4)};*/
		itemWrap = (GridView) findViewById(R.id.gt_gridview);
		//GridView.LayoutParams gwLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		start = 0;
		iterace = 0;
		setActualSada();
		if(!finish) {
			loadImages();
		}
	}

	private void setActualSada() {
		start++;
		actualSada = gt.getSada(start);
		Log.d(LOG_TAG, "Sada SIZE: " + start + " - " + actualSada.size());
		finish = (actualSada.size() <= 0);
	}

	private void loadImages() {
		itemWrap.setAdapter(new GridTaskAdapter(mContext, actualSada));
		itemWrap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				// excelent! done!
				if (view.getTag().equals(VIEW_TAG_CORRECT)) {
					zapisVysledek(false);
					showResultDialog(true, gt.getNazev() + " - část " + start, actualSada.get(i).getReakce(), false);
				} else {
					Toast.makeText(mContext, actualSada.get(i).getReakce(), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}




	private void zapisVysledek(boolean konec) {
		db.open();
		db.zapisTaskDoDatabaze(gt.getId(), System.currentTimeMillis());
		db.close();
		/// TODO
		if(konec) {
			// TODO zapsani statusu DONE
		}else {
			// TODO zapsani jen dilciho reseni (jako je to u QuizTasku)
		}
	}



	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
		setActualSada();
		if(finish && !closeTask) {
			zapisVysledek(true);
			showResultDialog(true, gt.getNazev() + " - Konec", gt.getResultTextOK(), true);
		}else if(closeTask) {
			runNextQuest(gt.getRetezId(), mContext);
		}else {
			loadImages();
		}
	}
}


