package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import android.util.Log;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Stanoviste;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;



public class CamTask extends Task {
    Stanoviste[] vysledky;
    //int pocetCilu;
    String[] zpetnaVazbaOk;
    String zpetnaVazbaFalse;
    private final static String ZPET_VAZBA_FALSE = "Špatně!";
	private final static String ZPET_VAZBA_OK = "Správně!";

    public CamTask(int id, String label, int[] spravneStanov, String nazev, String zadani,
				   String zpetVazbaFinal,
				   String fdbckFalse,
				   String[] fdbckOk,
				   String uri, int retez) {
        super(id, label, Config.TYP_ULOHY_CAM, nazev, zadani, new String[]{zpetVazbaFinal}, uri, retez);
        int pocetCilu = spravneStanov.length;
		int i;

		vysledky = new Stanoviste[spravneStanov.length];
		for(i = 0; i < spravneStanov.length; i++) {
			vysledky[i] = Config.vratStanovistePodleCisla(spravneStanov[i]);
		}

		if(fdbckFalse.isEmpty()) {
			this.zpetnaVazbaFalse = ZPET_VAZBA_FALSE;
		}else {
			this.zpetnaVazbaFalse = fdbckFalse;
		}

		this.zpetnaVazbaOk = new String[pocetCilu];
		for(i = 0; i < fdbckOk.length; i++) {
			Log.d("Geo - CamTask", "zpetnaVazbaOK " + i + ": " + fdbckOk[i]);
			zpetnaVazbaOk[i] = ZPET_VAZBA_OK + "\n" + fdbckOk[i];
		}
		for(i = fdbckOk.length; i < pocetCilu; i++) {
			Log.d("Geo - CamTask", "zpetnaVazbaOK DEF " + i + ": " + ZPET_VAZBA_OK);
			zpetnaVazbaOk[i] = ZPET_VAZBA_OK;
		}
    }

    public int getPocetCilu() {
        return vysledky.length;
    }

    /*
    public String[] getVysledky() {
        return vysledky;
    }
    */

    public Stanoviste[] getStanoviste() {
    	return this.vysledky;
	}

	public int getIndexStanovistePodleCisla(int cislo) {
		for(int i = 0; i < vysledky.length; i++) {
			if(vysledky[i].getCislo() == cislo) {
				return i;
			}
		}
		return -1;
	}


	public String getZpetnaVazbaOk(int ind) {
		Log.d("Geo - CamTask", "return zpetnaVazbaOK " + ind);
		return zpetnaVazbaOk[ind];
	}

	public String getZpetnaVazbaFalse() {
		return zpetnaVazbaFalse;
	}
}
