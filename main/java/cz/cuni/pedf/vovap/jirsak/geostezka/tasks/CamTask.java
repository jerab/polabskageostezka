package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;



public class CamTask extends Task {
    String[] vysledky;
    int pocetCilu;
    public CamTask(int id, String label, String[] vysledky, String nazev, String zadani, String zpetVazbaOk, String uri, int retez) {
        super(id, label, Config.TYP_ULOHY_CAM, nazev, zadani, new String[]{zpetVazbaOk}, uri, retez);
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
