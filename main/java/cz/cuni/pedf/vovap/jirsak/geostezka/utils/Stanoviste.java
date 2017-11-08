package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

/**
 * Created by tomason on 06.11.2017.
 */

public class Stanoviste {
	private int cislo;
	private String nazev;
	private String url;

	public Stanoviste(int cislo, String nazev, String url) {
		this.nazev = nazev;
		this.url = url;
		this.cislo = cislo;
	}

	public String getNazev() {
		return nazev;
	}

	public String getUrl() {
		return url;
	}

	public int getCislo() {
		return cislo;
	}
}
