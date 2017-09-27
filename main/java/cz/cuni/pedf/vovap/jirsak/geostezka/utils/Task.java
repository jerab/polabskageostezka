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

    public Task(int id, String label, int typ, String nazev, String zadani, String uri, int retezId) {
        this.id = id;
        this.label = label;
        this.typ = typ;
        this.nazev = nazev;
        this.zadani = zadani;
        this.uri = uri;
        this.retezId = retezId;
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

}
/// Vyvoreni staticke tridy uvnitr configu - tyto tridy by mohly byt samozrejme samostatne mimo config



