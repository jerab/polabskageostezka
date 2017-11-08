package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

/**
 * Created by tomason on 06.11.2017.
 */

public class GridTaskItem {
	private int cislo;
	private int obrazek;
	private String text;
	private String reakce;
	private boolean spravne;

	public String getReakce() {
		return reakce;
	}

	public GridTaskItem(int cislo, int obrId, String popisek, String reakce, boolean jeSpravne) {
		this.cislo = cislo;
		obrazek = obrId;
		text = popisek;
		this.reakce = reakce;
		spravne = jeSpravne;
	}

	public int getCislo() {
		return cislo;
	}

	public int getObrazek() {
		return obrazek;
	}

	public String getText() {
		return text;
	}

	public boolean isSpravne() {
		return spravne;
	}
}
