package cz.cuni.pedf.vovap.jirsak.geostezka.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.pedf.vovap.jirsak.geostezka.SettingsActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ArTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.QuizTask;

public class Config {

    public static final int TYP_ULOHY_CAM = 1;
    public static final int TYP_ULOHY_DRAGDROP = 2;
    public static final int TYP_ULOHY_QUIZ = 3;
    public static final int TYP_ULOHY_AR = 4;

	public static final int TASK_STATUS_NOT_VISITED = 0;
	public static final int TASK_STATUS_OPENED = 1;
	public static final int TASK_STATUS_DONE = 2;

	private static Boolean DEBUG_MODE = null;


	public static final boolean poziceGeostezky(LatLng pozice)
	{
		//todo doplnit polygon geostezky
		ArrayList<LatLng> points = new ArrayList<>();
		points.add(new LatLng(50.716055, 12.347630));
		points.add(new LatLng(50.586957, 18.852539));
		points.add(new LatLng(48.825190, 18.430982));
		points.add(new LatLng(48.825190, 12.321623));

		return isPointInPolygon(pozice, points);
	}
	private static boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
		int intersectCount = 0;
		for(int j=0; j<vertices.size()-1; j++) {
			if( LineIntersect(tap, vertices.get(j), vertices.get(j+1)) ) {
				intersectCount++;
			}
		}
		return (intersectCount%2) == 1; // odd = inside, even = outside;
	}
	private static boolean LineIntersect(LatLng tap, LatLng vertA, LatLng vertB) {
		double aY = vertA.latitude;
		double bY = vertB.latitude;
		double aX = vertA.longitude;
		double bX = vertB.longitude;
		double pY = tap.latitude;
		double pX = tap.longitude;
		if ( (aY>pY && bY>pY) || (aY<pY && bY<pY) || (aX<pX && bX<pX) ) {
			return false; }
		double m = (aY-bY) / (aX-bX);
		double bee = (-aX) * m + aY;                // y = mx + b
		double x = (pY - bee) / m;
		return x > pX;
	}

/*
    /// Vyvoreni staticke tridy uvnitr configu - tyto tridy by mohly byt samozrejme samostatne mimo config
    public static class Uloha {
        private int id;
        private int typ;

        public Uloha(int id, int typ) {
            this.id = id;
            this.typ = typ;
        }
    }

    /// dalsi mozne rozsireni pro vetsi specifikaci uloh
    public static class UlohaCam extends Task {

        int pocetCilu;
        public UlohaCam(int id, int pocetCilu) {
            super(id, TYP_ULOHY_CAM);
            this.pocetCilu = pocetCilu;
        }
    }*/

	private static final Task[] SEZNAM_ULOH_INTRO = {
			// new Task(1,R.integer.TYP_ULOHY_CAM),
			// new Task(2,R.integer.TYP_ULOHY_DRAGDROP),
			// obecne task: id | pocet cilu | vysledky | nazev | zadani
			new CamTask(0,
					new String[]{"0", "1", "2", "3"},
					"Uloha 0",
					"Zadani ulohy 0",
					"http://0.cz",
					1),
			new CamTask(1,
					new String[]{"0", "1", "2", "3", "4", "5", "6", "7"},
					"Uloha 1",
					"Zadani ulohy 1",
					"http://1.cz",
					2)
	};



	/// vytvoreni pole se seznamem uloh
    //// Take by to slo udelat pomoci definovani 3 samostatnych trid - UlohaCam, UlohaNormal, ... s ruznymi parametry a vlastnostmi a nasledne pouzivat
    /// ruzne konstruktory (viz 3. prvek nize)
    private static final Task[] SEZNAM_ULOH = {
           // new Task(1,R.integer.TYP_ULOHY_CAM),
           // new Task(2,R.integer.TYP_ULOHY_DRAGDROP),
            // obecne task: id | pocet cilu | vysledky | nazev | zadani
            new CamTask(0,
                    new String[] {"0", "1", "2", "3"},
                    "Uloha 0",
                    "Zadani ulohy 0",
                    "http://0.cz",
					1),
            new CamTask(1,
                    new String[] {"0", "1", "2", "3", "4","5","6","7"},
                    "Uloha 1",
                    "Zadani ulohy 1",
                    "http://1.cz",
					2),
            new DragDropTask(2,
                    "Uloha DD 4",
                    "Zadani ulohy 4",
                    new int[]{R.drawable.zula0, R.drawable.plagioklas1, R.drawable.kremen2, R.drawable.ortoklas3, R.drawable.biotit4, R.drawable.biotit4, R.drawable.kremen2, R.drawable.plagioklas1},
                    new int[]{R.drawable.zoom, R.drawable.zoom, R.drawable.zoom, R.drawable.zoom},
					new int[]{R.drawable.afterclick},
                    new Point[] {new Point(100,90), new Point(200,85), new Point(15,260), new Point(250,220)},
                    "http://4",
					-1),
            new QuizTask(3,
                    "Uloha Quiz 5",
                    "Vyberte spravne odpovedi na otazky",
                    new String[] {"Z ceho se sklada zula?", "Jaky je nejcasteji se vyskytujici se kamen?"},
                    new int[] {4,3},
                    new String[] {  "Biotit",
                                    "Slida",
                                    "Kremen",
                                    "Moznost D neni k dispozici, ale ukazuje priklad dlouheho retezce v odpovedi",
                                    "Tohle nevypada nejlepe",
                                    "Uvidime jak to dopadne",
                                    "Uvidime jak to dopadne"},
                    "http://5",
					-1),
			new ArTask(4,
					TYP_ULOHY_AR,
					"Konvička AR",
					"Namiřte na kameny a koukejte na konvičku.",
					"http://ARtest")
    } ;


    /**
     * Metoda vraci objekt Uloha podle pozadovaneho id
     * @param hledaneid
     * @return
     */
    public static final Task vratUlohuPodleID(int hledaneid) {
        for(int i = 0; i < SEZNAM_ULOH.length; i++) {
            if(SEZNAM_ULOH[i].getId() == hledaneid) {
                return SEZNAM_ULOH[i];
            }
        }
        return null;
    }
    public static final Task vratUlohuPodleUri(String URI) {
        for(int i = 0; i < SEZNAM_ULOH.length; i++) {
            if(SEZNAM_ULOH[i].getUri().equals(URI)) {
                return SEZNAM_ULOH[i];
            }
        }
        return null;
    }
    public static final int vratPocetUloh()
    {
        return SEZNAM_ULOH.length;
    }

    public static final int vratPocetUlohIntro() {
		return SEZNAM_ULOH_INTRO.length;
	}


	public static TextView getDebugTw(Context c) {
		TextView tw = new TextView(c);
		tw.setIncludeFontPadding(false);
		tw.setBackgroundColor(Color.DKGRAY);
		tw.setTextColor(Color.WHITE);
		tw.setVerticalScrollBarEnabled(true);
		tw.setMovementMethod(new ScrollingMovementMethod());
		tw.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
		//tw.setTag(0);
		return tw;
	}

	public static void showDebugMsg(final TextView tw, final String msg, Context c) {
		if(jeDebugOn(c)) {
			//if (tw.isEnabled()) {
				//tw.setTag((int) tw.getTag() + 1);
				if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
					//tw.setText(tw.getTag() + "| " + msg + "\n" + tw.getText());
					tw.setText(msg + "\n" + tw.getText());
				} else {
					Log.d("GEO Debug CONFIG", msg);
					Activity ac = (Activity) c;
					ac.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//tw.setText(tw.getTag() + "| " + msg + "\n" + tw.getText());
							tw.setText(msg + "\n" + tw.getText());
						}
					});
				}
			//}
		}
	}

	public static boolean jeDebugOn(Context c) {
		if(DEBUG_MODE == null) {
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
			DEBUG_MODE = sp.getBoolean("pref_debug", false);
		}
		return DEBUG_MODE;
	}

	public static void nastavDebugMode(boolean stav, Context c) {
		DEBUG_MODE = stav;
	}

}
