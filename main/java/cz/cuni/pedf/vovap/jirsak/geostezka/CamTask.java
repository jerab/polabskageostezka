package cz.cuni.pedf.vovap.jirsak.geostezka;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Fogs on 15.5.2017.
 */

public class CamTask extends Task {
    String[] vysledky;
    int pocetCilu;
    public CamTask(int id, int pocetCilu, String[] vysledky, String nazev, String zadani) {
        super(id, 1, nazev, zadani);
        this.pocetCilu = pocetCilu;
        this.vysledky = vysledky;
    }
    public int getPocetCilu() {
        return pocetCilu;
    }

    public String[] getVysledky() {
        return vysledky;
    }

}
