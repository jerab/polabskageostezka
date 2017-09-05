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
    public QuizTask(int id, String nazev, String zadani, String[] otazky, int[] pocetOdpovediKOtazce, String[] odpovedi, String uri, int retez){
        super(id, Config.TYP_ULOHY_QUIZ, nazev, zadani, uri, retez);
        this.otazky = otazky;
        this.odpovedi = odpovedi;
        this.pocetOdpovediKOtazce = pocetOdpovediKOtazce;
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
}
