package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.content.Intent;
import android.widget.Toast;

import cz.cuni.pedf.vovap.jirsak.geostezka.DashboardActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.TaskCamActivity;

/**
 * Created by Fogs on 25.5.2017.
 */

public class Task {
    private int id;
    private String label;
    private int typ;
    private String nazev;
    private String zadani;
    private String uri;
    private int retezId;
	/**
	 * ind 0 - OK; [ind 1 - FALSE]
	 * ind 1 nemusi byt nastaven
	 */
	private String[] resultTexts;

    public Task(int id, String label, int typ, String nazev, String zadani, String[] zpetVazba, String uri, int retezId) {
        this.id = id;
        this.label = label;
        this.typ = typ;
        this.nazev = nazev;
        this.zadani = zadani;
        this.uri = uri;
        this.retezId = retezId;
		this.resultTexts = zpetVazba;
    }

	public Task(int id, Stanoviste stanoviste, int typ, String zadani, String[] zpetVazba, int retezId) {
    	this.id = id;
		this.label = String.valueOf(stanoviste.getCislo());
		this.typ = typ;
		this.nazev = stanoviste.getNazev();
		this.zadani = zadani;
		this.uri = stanoviste.getUrl();
		this.retezId = retezId;
		this.resultTexts = zpetVazba;
	}

    public int getRetezId()
    {
        return retezId;
    }

    public int getId() {
        return id;
    }

    public int getTyp() {
        return typ;
    }

    public String getNazev()
    {
        return nazev;
    }

    public String getZadani()
    {
        return this.zadani;
    }

    public String getUri() {
        return uri;
    }

    public String getLabel(){ return label; }

	public String getResultTextOK() {
		if(resultTexts.length > 0) {
			return resultTexts[0];
		}else {
			return "Výborně! Pokračuj na další úlohu.";
		}
	}

	public String getResultTextNO() {
		if(resultTexts.length > 1) {
			return resultTexts[1];
		}else {
			return "Špatně. Zkus to znovu.";
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setNazev(String nazev) {
		this.nazev = nazev;
	}
}
/// Vyvoreni staticke tridy uvnitr configu - tyto tridy by mohly byt samozrejme samostatne mimo config



