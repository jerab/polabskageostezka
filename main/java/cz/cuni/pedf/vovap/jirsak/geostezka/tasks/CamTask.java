package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;


public class CamTask extends Task {
    String[] vysledky;
    int pocetCilu;
    public CamTask(int id, String[] vysledky, String nazev, String zadani, String uri) {
        super(id, Config.TYP_ULOHY_CAM, nazev, zadani, uri);
        this.pocetCilu = vysledky.length;
        this.vysledky = vysledky;
    }
    public int getPocetCilu() {
        return pocetCilu;
    }

    public String[] getVysledky() {
        return vysledky;
    }

}
