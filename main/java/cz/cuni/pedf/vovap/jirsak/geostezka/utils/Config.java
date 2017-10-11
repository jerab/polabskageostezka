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
import cz.cuni.pedf.vovap.jirsak.geostezka.WelcomeActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ArTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.GridTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.QuizTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.SwipeTask;

import static android.content.Context.MODE_PRIVATE;

public class Config {

    public static final int TYP_ULOHY_CAM = 1;
    public static final int TYP_ULOHY_DRAGDROP = 2;
    public static final int TYP_ULOHY_QUIZ = 3;
    public static final int TYP_ULOHY_AR = 4;
	public static final int TYP_ULOHY_GRID = 5;
    public static final int TYP_ULOHY_SWIPE = 6;

	public static final int TASK_STATUS_NOT_VISITED = 0;
	public static final int TASK_STATUS_OPENED = 1;
	public static final int TASK_STATUS_DONE = 2;

	private static Boolean DEBUG_MODE = null;

	public static final boolean poziceGeostezky(LatLng pozice) {
		//todo doplnit polygon geostezky
		ArrayList<LatLng> points = new ArrayList<>();
		points.add(new LatLng(50.189739, 14.663800));
		points.add(new LatLng(50.190215, 14.663639));
		points.add(new LatLng(50.190303, 14.663961));
		points.add(new LatLng(50.189800, 14.664768));

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
                    "A",
					new String[]{"0", "1"},
					"Vyvřelé horniny",
					"Najdi všechny vyvřelé horniny v geoparku. Použij kameru pro načtení QR kódu na informačních tabulích u hornin.",
					"Výborně! Jdi na další úlohu.",
					"http://0.cz",
					1),
			new CamTask(1,
                    "B",
					new String[]{"0", "1"},
					"Hledání horniny",
					"Poznáš, z jaké horniny je výbrus na obrázku? Najdi tuto horninu v geoparku a načti její QR kód.",
					"Výborně! Odemkl jsi hlavní sadu úloh. Přejeme hodně štěstí.",
					"http://1.cz",
					-1)
	};


	/**
	 * vytvoreni pole se seznamem uloh
	 * ruzne konstruktory dle typu uloh
	 * obecne task: id | pocet cilu | vysledky | nazev | zadani | zpetna vazba
	 */
    private static final Task[] SEZNAM_ULOH = {
			// DD task: id | label | nazev | zadani | zpetna vazba OK | pozadi + zdrojove polozky | cilove polozky | afterclick | pozice cilu |QR | navaznost
            new DragDropTask(2,
                    "1",
                    "Žula",
                    "Přesuň správné minerály k vyznačeným místům na obrázku výbrusu žuly. Po správném přiřazení můžete poklepat na minerál pro zobrazení jeho krystalické mřížky.",
					"Výborně! Nyní se podívej, jak vypadá mikroskopická struktura jednotlivých minerálů (poklepáním na minerál si můžeš změnit jeho zobrazení).",
                    new int[]{R.drawable.granit_liberec,
							R.drawable.kremen, R.drawable.slida_muskovit, R.drawable.zivec_ortoklas, R.drawable.biotit4, R.drawable.biotit4,
							R.drawable.kremen2, R.drawable.plagioklas1},
                    new int[]{R.drawable.kremen, R.drawable.slida_muskovit, R.drawable.zivec_ortoklas},
					new int[]{R.drawable.afterclick, R.drawable.afterclick, R.drawable.afterclick},
                    /// koordinatory na obr. sirky 1080px
					new Point[] {new Point(325,360), new Point(387,503), new Point(680,380)},
					new String[] {"left","right","right"},
					//new Point[] {new Point(0,360), new Point(960,503), new Point(1920,380)},

					"http://4",
					-1),
            // DragDropTask Slepenec -> retez na DrawTask
			new ArTask(3,
                    "3",
					TYP_ULOHY_AR,
					"Gabro",
					"Namiř kamerou na obrázek na podstavci a prohlédněte si, jak vypadá gabro.",
					"Výborně! pomocí tažením nahoru/dolů a doprava/doleva můžeš kamenem otáčet a měnit jeho velikost.",
					"http://ARtest"),
            // DragDropTask Uhli
            // ArTask Zkamenele drevo
			new GridTask(4,
                    "6",
					"Fylit",
					"Vyberte správný obrázek.",
					"Výborně",
					"http://6",
					new int[] {R.drawable.biotit4, R.drawable.ortoklas3, R.drawable.afterclick, R.drawable.zoom,
							R.drawable.afterclick, R.drawable.biotit4, R.drawable.zoom, R.drawable.ortoklas3,},
					new String[] {"Ano, to je dobre",
							"spatne",
							"spatne",
							"spatne",
							"spatne na druhou",
							"spatne na druhou",
							"spatne na druhou",
							"Zkouska spravnosti"},
					new String[] {"Ano, to je dobre", "Zkouska spravnosti"},
					-1),
            new QuizTask(5,
                    "7",
                    "Metabazalt",
                    "Vyberte spravne odpovedi na otazky",
                    new String[] {"Z ceho se sklada zula?", "Jaky je nejcasteji se vyskytujici se kamen?"},
                    new int[] {3, 4},
                    new String[] {  "Biotit",
                            "Slida",
                            "Kremen",
                            "Moznost neni k dispozici, ale ukazuje priklad dlouheho retezce v odpovedi",
                            "Tohle nevypada nejlepe",
                            "Uvidime jak to dopadne",
                            "Uvidime jak to dopadne"
                    },
					/**
					 * 0 - spravne v prvni sade, 1 az n - spatne v dane sade, n + 1 - spravne v dalsi sade, atd.
					 */
					new String[] {"Ano, Biotit je Metabazalt",
							"Špatně slída",
							"Špatně křemen",
								"Ano, tohle je dobře",
							"Špatně co to je....",
							"Špatně uvidíme 1",
							"Špatně uvidíme 2"
					},
                    "http://5",
                    -1),
            new QuizTask(6,
                    "88",
                    "Migmatit",
                    "Vyberte spravne odpovedi na otazky",
                    new String[] {"Z ceho se sklada zula?", "Jaky je nejcasteji se vyskytujici se kamen?"},
                    new int[] {4,3},
                    new String[] {  "Biotit",
                            "Slida",
                            "Kremen",
                            "Moznost D neni k dispozici, ale ukazuje priklad dlouheho retezce v odpovedi",
                            "Dobře",
                            "Uvidime jak to dopadne 1",
                            "Uvidime jak to dopadne 2"},
					/**
					 * 0 - spravne v prvni sade, 1 az n - spatne v dane sade, n + 1 - spravne v dalsi sade, atd.
					 */
					new String[] {"Ano, Biotit je Migmatit",
							"Špatně slída",
							"Špatně křemen",
							"Špatně co to je....",
								"Ano, tohle je dobře",
							"Špatně uvidíme 1",
							"Špatně uvidíme 2"
					},
                    "http://5",
                    -1),
            // ArTask Mandlovec
            // ArTask Cedic
            new SwipeTask(7,
                    "11",
                    "Řeka",
                    "Poznáš podle uspořádání kamenů v korytě řeky, kudy tekla řeka?",
					new String[] {
							"Výborně! Řeka usměrnila valouny ve směru svého toku. Pokračuj na další úlohu.",
							"Ale ne, tudy řeka netekla."},
                    "http://swipetask",
                    -1),
			new CamTask(8,
					"TT",
					new String[]{"0", "1"},
					"Vyvřelé horniny",
					"Najdi QR 0 a 1",
					"Výborně! Jdi na další úlohu.",
					"http://0.cz",
					-1)
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

    public static final Task vratIntroUlohuPodleID(int hledaneid) {
        for(int i = 0; i < SEZNAM_ULOH_INTRO.length; i++) {
            if(SEZNAM_ULOH_INTRO[i].getId() == hledaneid) {
                return SEZNAM_ULOH_INTRO[i];
            }
        }
        return null;
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

	public static boolean isPositionCheckOn(Context c) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		return !sp.getBoolean("pref_locationoff", false);
	}

	public static void nastavDebugMode(boolean stav, Context c) {
		DEBUG_MODE = stav;
	}

}
