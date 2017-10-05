package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;


/**
 * Created by student on 8.6.2017.
 */

public class QuizTask extends Task {
    String[] otazky;
    String[] odpovedi;
    int[] pocetOdpovediKOtazce;
	String[] zpetneVazby;

    public QuizTask(int id, String label, String nazev, String zadani, String[] otazky,
					int[] pocetOdpovediKOtazce, String[] odpovedi, String[] zpetneVazby,
					String uri,	int retez) {
        super(id, label, Config.TYP_ULOHY_QUIZ, nazev, zadani, new String[]{}, uri, retez);
        this.otazky = otazky;
        this.odpovedi = odpovedi;
        this.pocetOdpovediKOtazce = pocetOdpovediKOtazce;
		this.zpetneVazby = zpetneVazby;
    }

    public String[] getOtazky() {
        return otazky;
    }

    public String[] getOdpovedi() {
        return odpovedi;
    }

    public int[] getPocetOdpovediKOtazce() {
        return pocetOdpovediKOtazce;
    }

	@Override
    public String getResultTextOK() {
		return "Výborně! Pokračuj na další úlohu.";
	}

	public String getZpetnaVazba(int odpoved, boolean resultCorrect) {
		if(zpetneVazby.length > odpoved) {
			return zpetneVazby[odpoved];
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
