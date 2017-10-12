package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

/**
 * Created by tomason on 12.10.2017.
 */

public class QuizTaskItemConfig {

	private String text;
	private String reakce;
	private boolean spravne;
	private int cisloSadyOtazek;

	public QuizTaskItemConfig(String text, String reakce, boolean spravnost, int sada) {
		this.text = text;
		this.reakce = reakce;
		this.spravne = spravnost;
		cisloSadyOtazek = sada;
	}

	public String getPopisek() {
		return text;
	}

	public String getReakce() {
		return reakce;
	}

	public boolean isSpravne() {
		return spravne;
	}

	public int getCisloSadyOtazek() {
		return cisloSadyOtazek;
	}
}
