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

import java.util.ArrayList;
import java.util.List;

import cz.cuni.pedf.vovap.jirsak.geostezka.SettingsActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.WelcomeActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ArTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.CamTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DragDropTask;
import cz.cuni.pedf.vovap.jirsak.geostezka.R;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.DrawTask;
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
	public static final int TYP_ULOHY_DRAW = 7;

	public static final int TASK_STATUS_NOT_VISITED = 0;
	public static final int TASK_STATUS_OPENED = 1;
	public static final int TASK_STATUS_DONE = 2;

	public static final int TASK_ZULA_ID = 2;
	public static final int TASK_SLEPENEC_ID = 3;

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
					"http://0",
					1),
			new CamTask(1,
                    "B",
					new String[]{"0", "1"},
					"Hledání horniny",
					"Poznáš, z jaké horniny je výbrus na obrázku? Najdi tuto horninu v geoparku a načti její QR kód.",
					"Výborně! Odemkl jsi hlavní sadu úloh. Přejeme hodně štěstí.",
					"http://1",
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
					R.layout.activity_task_drag_drop_zula,
					new int[]{R.drawable.granit_liberec},
					new int[]{
							R.drawable.kremen_s, R.drawable.slida_s, R.drawable.zivec_s, R.drawable.sira_s, R.drawable.pyrit_s,
							R.drawable.halit_s, R.drawable.augit_s, R.drawable.beryl_s},
                    new int[]{R.drawable.zula_kremen_zoom, R.drawable.zula_biotit_zoom, R.drawable.zula_zivec_zoom},
					new int[]{R.drawable.afterclick, R.drawable.afterclick, R.drawable.afterclick},
                    /// koordinatory na obr. sirky 1080px
					new Point[] {new Point(325,360), new Point(387,503), new Point(680,380)},
					new Point[]{},
					new String[] {"left","right","right"},
					//new Point[] {new Point(0,360), new Point(960,503), new Point(1920,380)},

					"http://4",
					-1),
            // DragDropTask Slepenec -> retez na DrawTask
			new DragDropTask(3,
					"2a",
					"Slepenec 1",
					"Zasaď valouny na správná místa.",
					"Výborně! Teď už jenom tmel.",
					R.layout.activity_task_drag_drop,
					new int[]{R.drawable.slepenec_cb_bezvalounu, R.drawable.slepenec_barva_final},
					new int[]{
							R.drawable.slep_valoun1, R.drawable.slep_valoun2, R.drawable.slep_valoun3, R.drawable.slep_valoun4,
							R.drawable.slep_valoun5, R.drawable.slep_valoun6, R.drawable.slep_valoun7},
					//new int[]{R.drawable.slep_valoun1, R.drawable.slep_valoun2, R.drawable.slep_valoun3},
					new int[]{},
					new int[]{},
					/// koordinatory na obr. sirky 1080px
					/*new Point[] {new Point(431,193), new Point(47,765), new Point(265,375), new Point(683,307),
								new Point(565,619), new Point(617,786), new Point(890,430)},*/
					new Point[] {new Point(497,135), new Point(47,765), new Point(265,375), new Point(683,307),
							new Point(565,619), new Point(617,786), new Point(890,430)},
					/// sirka,vyska ciloveho policka
					new Point[]{new Point(117,131), new Point(250,195), new Point(60,74), new Point(140,110),
								new Point(93,123), new Point(145,120)},
					new String[] {},
					//new Point[] {new Point(0,360), new Point(960,503), new Point(1920,380)},
					"http://4",
					4),
			new DrawTask(4,
					"2b",
					"Slepenec 2",
					"Nyní vyplň tmel mezi valouny, aby se pěkně spojili.",
					R.layout.activity_task_draw,
					R.drawable.slepenec_barva_bezspar,
					R.drawable.slepenec_barva_final,
					"Skvělé! Teď máš kompletní slepenec. Můžeš ho porovnat s opravdovým vzorkem.",
					"http://4",
					-1),
			new ArTask(5,
                    "3",
					TYP_ULOHY_AR,
					"Gabro",
					"Namiř kamerou na obrázek na podstavci a prohlédněte si, jak vypadá gabro.",
					new String[] {"Gabro"},
					"Geostezka.xml",
					"Výborně! pomocí tažením nahoru/dolů a doprava/doleva můžeš kamenem otáčet a měnit jeho velikost.",
					"http://ARtest"),
            // DragDropTask Uhli
			new DragDropTask(6,
					"2a",
					"Slepenec 1",
					"Zasaď valouny na správná místa.",
					"Výborně! Teď už jenom tmel.",
					R.layout.activity_task_drag_drop,
					new int[]{R.drawable.slepenec_cb_bezvalounu, R.drawable.slepenec_barva_final},
					new int[]{
							R.drawable.slep_valoun1, R.drawable.slep_valoun2, R.drawable.slep_valoun3, R.drawable.slep_valoun4,
							R.drawable.slep_valoun5, R.drawable.slep_valoun6, R.drawable.slep_valoun7},
					new int[]{},
					new int[]{},
					/// koordinatory stredu (k sirce 1080px)
					new Point[] {new Point(497,140),
							new Point(165,660),
							new Point(300,340),
							new Point(753,246),
							new Point(594,583),
							new Point(665,725),
							new Point(980,340)},
					/// sirka,vyska ciloveho policka
					new Point[]{new Point(117,135),
							new Point(255,200),
							new Point(75,80),
							new Point(150,120),
							new Point(75,85),
							new Point(103,123),
							new Point(188,222)},
					new String[] {},
					"http://4",
					-1),
			/*new DragDropTask(6,
					"4",
					"Uhlí",
					"Přesuň správné minerály k vyznačeným místům na obrázku výbrusu žuly. Po správném přiřazení můžete poklepat na minerál pro zobrazení jeho krystalické mřížky.",
					"Výborně! Nyní se podívej, jak vypadá mikroskopická struktura jednotlivých minerálů (poklepáním na minerál si můžeš změnit jeho zobrazení).",
					R.layout.activity_task_drag_drop_zula,
					R.drawable.granit_liberec,
					new int[]{
							R.drawable.kremen, R.drawable.slida_muskovit, R.drawable.zivec_ortoklas, R.drawable.biotit4, R.drawable.biotit4,
							R.drawable.kremen2, R.drawable.plagioklas1},
					new int[]{R.drawable.kremen, R.drawable.slida_muskovit, R.drawable.zivec_ortoklas},
					new int[]{R.drawable.afterclick, R.drawable.afterclick, R.drawable.afterclick},
					/// koordinatory na obr. sirky 1080px
					new Point[] {new Point(325,360), new Point(387,503), new Point(680,380)},
					new Point[] {},
					new String[] {"left","right","right"},
					//new Point[] {new Point(0,360), new Point(960,503), new Point(1920,380)},

					"http://4",
					-1),*/
            // ArTask Zkamenele drevo
			new ArTask(7,
					"5",
					TYP_ULOHY_AR,
					"Zkamenělé dřevo",
					"Namiř kamerou na obrázek na podstavci a prohlédněte si, jak vypadá gabro.",
					new String[] {"Drevo"},
					"Geostezka.xml",
					"Výborně! pomocí tažením nahoru/dolů a doprava/doleva můžeš kamenem otáčet a měnit jeho velikost.",
					"http://ARtest"),
			// Fylit
			new GridTask(8,
                    "6",
					"Fylit",
					"Vyberte správný obrázek.",
					"Výborně",
					"http://6",
					new int[] {R.drawable.zivec_s, R.drawable.slida_s, R.drawable.sira_s, R.drawable.zoom,
							R.drawable.afterclick, R.drawable.augit_s, R.drawable.zoom, R.drawable.pyrit_s,},
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
			/**
			* id | label | nazev | zadani | dilci zadani | odpovedi | URI | navaznost
			*/
			new QuizTask(9,
                    "7",
                    "Metabazalt",
                    "Vyberte spravne odpovedi na otazky",
                    new String[] {"Z ceho se sklada zula?", "Jaky je nejcasteji se vyskytujici se kamen?"},
					new QuizTaskItemConfig[] {
							new QuizTaskItemConfig("Biotit","Ano, Biotit je Metabazalt", true, 0),
							new QuizTaskItemConfig("Kremen","Špatně, křemen", false, 0),
							new QuizTaskItemConfig("Moznost neni k dispozici, ale ukazuje priklad dlouheho retezce v odpovedi","Špatně, není k dispozici",
									false,	0),

							new QuizTaskItemConfig("Slida","Ano, Slída je Biotit", true, 1),
							new QuizTaskItemConfig("Uvidíme 1","Špatně uvidíme 1", false, 1),
							new QuizTaskItemConfig("Uvidíme 2","Špatně uvidíme 2", false, 1),
							new QuizTaskItemConfig("Uvidíme 3","Špatně uvidíme 3", false, 1),
					},
                    "http://5",
                    -1),
			new QuizTask(10,
					"8",
					"Migmatit",
					"Vyberte spravne odpovedi na otazky",
					new String[] {"Z ceho se sklada zula?", "Jaky je nejcasteji se vyskytujici se kamen?"},
					//new int[] {3, 4},
					new QuizTaskItemConfig[] {
							new QuizTaskItemConfig("Biotit","Ano, Biotit je Metabazalt", true, 0),
							new QuizTaskItemConfig("Kremen","Špatně, křemen", false, 0),
							new QuizTaskItemConfig("Moznost neni k dispozici, ale ukazuje priklad dlouheho retezce v odpovedi","Špatně, není k dispozici",
									false,	0),

							new QuizTaskItemConfig("Slida","Ano, Slída je Biotit", true, 1),
							new QuizTaskItemConfig("Uvidíme 1","Špatně uvidíme 1", false, 1),
							new QuizTaskItemConfig("Uvidíme 2","Špatně uvidíme 2", false, 1),
							new QuizTaskItemConfig("Uvidíme 3","Špatně uvidíme 3", false, 1),
					},
					"http://5",
					-1),
            // ArTask Mandlovec
			new ArTask(11,
					"9",
					TYP_ULOHY_AR,
					"Mandlovec",
					"Namiř kamerou na obrázek na podstavci a prohlédněte si, jak vypadá gabro.",
					new String[] {"Achat"},
					"Geostezka.xml",
					"Výborně! pomocí tažením nahoru/dolů a doprava/doleva můžeš kamenem otáčet a měnit jeho velikost.",
					"http://ARtest"),
			new ArTask(12,
					"10",
					TYP_ULOHY_AR,
					"Čedič",
					"Namiř kamerou na obrázek na podstavci a prohlédněte si, jak vypadá gabro.",
					new String[] {"Lava"},
					"Geostezka.xml",
					"Výborně! pomocí tažením nahoru/dolů a doprava/doleva můžeš kamenem otáčet a měnit jeho velikost.",
					"http://ARtest"),
            // ArTask Cedic
            new SwipeTask(13,
                    "11",
                    "Řeka",
                    "Poznáš podle uspořádání kamenů v korytě, kudy tekla řeka?",
					new String[] {
							"Výborně! Řeka usměrnila valouny ve směru svého toku. Pokračuj na další úlohu.",
							"Ale ne, tudy řeka netekla."},
                    "http://swipetask",
                    -1),
			new CamTask(14,
					"CT",
					new String[]{"0", "1"},
					"Vyvřelé horniny",
					"Najdi QR 0 a 1",
					"Výborně! Jdi na další úlohu.",
					"http://0.cz",
					-1),
			new CamTask(15,
					"CT",
					new String[]{"0", "1"},
					"Vyvřelé horniny",
					"Najdi QR 0 a 1",
					"Výborně! Jdi na další úlohu.",
					"http://0.cz",
					-1),
			new DragDropTask(16,
					"Z",
					"Žula",
					"Přesuň správné minerály k vyznačeným místům na obrázku výbrusu žuly. Po správném přiřazení můžete poklepat na minerál pro zobrazení jeho krystalické mřížky.",
					"Výborně! Nyní se podívej, jak vypadá mikroskopická struktura jednotlivých minerálů (poklepáním na minerál si můžeš změnit jeho zobrazení).",
					R.layout.activity_task_drag_drop_zula,
					new int[]{R.drawable.granit_liberec},
					new int[]{
							R.drawable.kremen_s, R.drawable.slida_s, R.drawable.zivec_s, R.drawable.sira_s, R.drawable.pyrit_s,
							R.drawable.halit_s, R.drawable.augit_s, R.drawable.beryl_s},
					new int[]{R.drawable.zula_kremen_zoom, R.drawable.zula_biotit_zoom, R.drawable.zula_zivec_zoom},
					new int[]{R.drawable.afterclick, R.drawable.afterclick, R.drawable.afterclick},
					/// koordinatory na obr. sirky 1080px
					new Point[] {new Point(325,360), new Point(387,503), new Point(680,380)},
					new Point[]{},
					new String[] {"left","right","right"},
					//new Point[] {new Point(0,360), new Point(960,503), new Point(1920,380)},

					"http://4",
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
