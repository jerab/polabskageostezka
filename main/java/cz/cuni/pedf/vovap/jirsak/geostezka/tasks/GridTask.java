package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

/**
 * Created by Fogs on 05.09.2017.
 */

public class GridTask extends Task {
    private int[] images;
    //private int[] correctImg;
    private String[] texts;
    private String[] correctText;

    public GridTask(int id, String nazev, String zadani, String uri, int[] images, String[] texts, String[] correctText,int retez ){
        super(id, Config.TYP_ULOHY_GRID, nazev, zadani, uri, retez);
        this.images = images;
        //this.correctImg = correctImg;
        this.texts = texts;
        this.correctText = correctText;
    }

    public int[] getImages() {
        return images;
    }

   /* public int[] getCorrectImg() {
        return correctImg;
    }*/

    public String[] getTexts() {
        return texts;
    }

    public String[] getCorrectText() {
        return correctText;
    }
}
