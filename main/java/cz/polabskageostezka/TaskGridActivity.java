package cz.polabskageostezka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.ArrayList;

import cz.polabskageostezka.tasks.GridTask;
import cz.polabskageostezka.utils.BaseTaskActivity;
import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.GridTaskAdapter;
import cz.polabskageostezka.utils.GridTaskItem;
import cz.polabskageostezka.utils.InitDB;


public class TaskGridActivity extends BaseTaskActivity {
	private static final String LOG_TAG = "Geo - GridTask";
	public static final String VIEW_TAG_CORRECT = "xxxCORECTxxx";
	GridTask gt;
	InitDB db;
	int stav;
	int max;
	Context mContext;
	ImageView infoOtazka;
	int start;
	Button zpet, dalsi;
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
		start = 0;
		if (stav == Config.TASK_STATUS_NOT_VISITED) {
			db.odemkniUlohu(gt.getId());
			UkazZadani(gt.getNazev(), gt.getZadani());
			db.gridDoDB(gt.getId(),1);
		}else if (stav == Config.TASK_STATUS_DONE) {
			finish = true;
			start = db.posledniGrid(gt.getId());
			Log.d(LOG_TAG,"db - " + String.valueOf(start));
			max = start;
		} else {
			start = db.posledniGrid(gt.getId()) - 1;
			Log.d(LOG_TAG,"db -1 " + String.valueOf(start));
		}
		db.close();

		mContext = getApplicationContext();
		itemWrap = (GridView) findViewById(R.id.gt_gridview);

		if(finish) {
			// allow browsing
			infoOtazka = (ImageView) findViewById(R.id.infoGrid);
			infoOtazka.setVisibility(View.VISIBLE);
			infoOtazka.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					showResultDialog(true, gt.getNazev() + " - část " + start, gt.getCorrectAnswer(start-1), false);
				}
			});
			if (start > 1) {
				zpet = (Button) findViewById(R.id.btnGtBack);
				dalsi = (Button) findViewById(R.id.btnGtNext);
				zpet.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						start--;
						setActualSada();
						loadImages();
						if (start == 1)
							zpet.setEnabled(false);
						dalsi.setEnabled(true);
					}
				});
				dalsi.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						start++;
						setActualSada();
						loadImages();
						if (start == max)
							dalsi.setEnabled(false);
						zpet.setEnabled(true);
					}
				});
				dalsi.setVisibility(View.VISIBLE);
				zpet.setVisibility(View.VISIBLE);
				dalsi.setEnabled(false);
			}
		}
		setActualSada();
		loadImages();
	}

	private void setActualSada() {
		Log.d(LOG_TAG,"start bef - "+String.valueOf(start));
		if(!finish){
			start++;
		}
		Log.d(LOG_TAG,"start aft - "+String.valueOf(start));
		actualSada = gt.getSada(start);
		Log.d(LOG_TAG, "Sada SIZE: " + start + " - " + actualSada.size());
		//finish = (actualSada.size() <= 0 || finish);
		if (actualSada.size()<=0){
			start--;
			finish=true;
		}
	}

	private void loadImages() {
		itemWrap.setAdapter(new GridTaskAdapter(mContext, actualSada));
		if (!finish) {
			itemWrap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					// excelent! done!
					if (view.getTag().equals(VIEW_TAG_CORRECT)) {
						zapisVysledek(false);
						showResultDialog(true, gt.getNazev() + " - část " + start, actualSada.get(i).getReakce(), false);
					} else {
						Toast.makeText(mContext, actualSada.get(i).getReakce(), Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}




	private void zapisVysledek(boolean konec) {
		/// TODO
		if(konec) {
			// TODO zapsani statusu DONE
			db.open();
			db.gridDoDB(gt.getId(),db.posledniGrid(gt.getId()),2);
			db.zapisTaskDoDatabaze(gt.getId(), System.currentTimeMillis());
			db.close();
			Log.d(LOG_TAG,"Task completed");
		}else {
			db.open();
			db.gridDoDB(gt.getId(),start,2);
			Log.d(LOG_TAG,"Iterace " + (start) + " hotovo");
			//if(gt.getPocetSad()!=start)
			//db.gridDoDB(gt.getId(),start+1);
			db.close();
			Log.d(LOG_TAG,"Iterace " + (start+1) + " do db");
			// TODO zapsani jen dilciho reseni (jako je to u QuizTasku)
		}
	}



	@Override
	public void runFromResultDialog(boolean result, boolean closeTask) {
		if (stav != Config.TASK_STATUS_DONE){
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

	@Override
	public void runFromStartTaskDialog() {}
}