package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import android.util.Log;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.QuizTaskItemConfig;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;


/**
 * Created by student on 8.6.2017.
 */

public class QuizTask extends Task {

	String[] otazky;
	QuizTaskItemConfig[] odpovedi;
    //int[] pocetOdpovediKOtazce;
	//String[] zpetneVazby;
/*
    public QuizTask(int id, String label, String nazev, String zadani, String[] otazky,
					int[] pocetOdpovediKOtazce, String[] odpovedi, String[] zpetneVazby,
					String uri,	int retez) {
        super(id, label, Config.TYP_ULOHY_QUIZ, nazev, zadani, new String[]{}, uri, retez);
        this.otazky = otazky;
        this.odpovedi = odpovedi;
        this.pocetOdpovediKOtazce = pocetOdpovediKOtazce;
		this.zpetneVazby = zpetneVazby;
    }
*/
	public QuizTask(int id, String label, String nazev, String zadani, String[] otazky, QuizTaskItemConfig[] odpovedi, String uri,	int retez) {
		super(id, label, Config.TYP_ULOHY_QUIZ, nazev, zadani, new String[]{}, uri, retez);
		this.otazky = otazky;
		this.odpovedi = odpovedi;
	}

    public String[] getOtazky() {
        return otazky;
    }

    public QuizTaskItemConfig[] getOdpovedi() {
        return odpovedi;
    }

    public String getSpravnaOdpoved(int cisloSady) {
		for (int i = 0; i < odpovedi.length; i++) {
			if(odpovedi[i].getCisloSadyOtazek() == cisloSady && odpovedi[i].isSpravne()) {
				return odpovedi[i].getReakce();
			}
		}
		return getResultTextOK();
	}

    public int getPocetOdpovediKOtazce(int cisloSady) {
        int count = 0;
		for (int i = 0; i < odpovedi.length; i++) {
			if(odpovedi[i].getCisloSadyOtazek() == cisloSady) {
				count++;
			}
		}
		return count;
    }

    public QuizTaskItemConfig[] getOdpovediKOtazce(int cisloSady) {
		QuizTaskItemConfig[] count = new QuizTaskItemConfig[getPocetOdpovediKOtazce(cisloSady)];
		Log.d("Geo - QUIZ TASK", "Pocet odpovedi: " + count);
		int k = 0;
		for (int i = 0; i < odpovedi.length; i++) {
			if(odpovedi[i].getCisloSadyOtazek() == cisloSady) {
				count[k] = odpovedi[i];
				k++;
			}
			if(k >= count.length) {
				break;
			}
		}
		return count;
	}

	@Override
    public String getResultTextOK() {
		return "Výborně! Pokračuj na další úlohu.";
	}

	public String getZpetnaVazba(QuizTaskItemConfig odpoved, boolean resultCorrect) {
		if(odpoved != null) {
			return odpoved.getReakce();
		}else if(resultCorrect) {
			return this.getResultTextOK();
		}else {
			return this.getResultTextNO();
		}
	}

	@Override
	public String getResultTextNO() {
		return "Špatně. Zkus to znovu.";
	}
}
