package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

/**
 * Created by Fogs on 25.5.2017.
 */

public class Task {
    private int id;
    private int typ;
    private String nazev;
    private String zadani;
    private String uri;

    public Task(int id, int typ, String nazev, String zadani, String uri) {
        this.id = id;
        this.typ = typ;
        this.nazev = nazev;
        this.zadani = zadani;
        this.uri = uri;
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
}
/// Vyvoreni staticke tridy uvnitr configu - tyto tridy by mohly byt samozrejme samostatne mimo config



