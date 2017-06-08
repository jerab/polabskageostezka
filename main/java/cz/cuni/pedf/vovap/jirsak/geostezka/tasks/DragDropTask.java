package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;


import android.graphics.Point;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

/**
 * Created by Fogs on 31.5.2017.
 */

public class DragDropTask extends Task {
    private int[] bankaObrazku;
    private Point[] souradniceObj;
    private Point[] souradniceCil;

    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku) {
        super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani);
        this.bankaObrazku = bankaObrazku;
    }
    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku, Point[] objs, Point[] tgs) {
        super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani);
        this.bankaObrazku = bankaObrazku;
        this.souradniceObj = objs;
        this.souradniceCil = tgs;
    }

    public int[] getBankaObrazku() {
        return bankaObrazku;
    }

    public Point[] getSouradniceObj() {
        return souradniceObj;
    }

    public Point[] getSouradniceCil() {
        return souradniceCil;
    }
}
