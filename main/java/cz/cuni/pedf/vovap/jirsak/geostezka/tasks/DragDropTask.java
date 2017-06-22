package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;


import android.graphics.Point;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.InitDB;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;


/**
 * Created by Fogs on 31.5.2017.
 */

public class DragDropTask extends Task {
    private int[] bankaObrazku;
    private int[] bankaObrCile;
    private int[] bankaObrCile2;
    private Point[] souradniceObj;
    private Point[] souradniceCil;

    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku, String uri) {
        super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani, uri);
        this.bankaObrazku = bankaObrazku;
    }
    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku, int[] bankaObrCile, int[] bankaObrCile2, Point[] tgs, String uri) {
        super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani,uri);
        this.bankaObrazku = bankaObrazku;
        this.bankaObrCile = bankaObrCile;
        this.bankaObrCile2 = bankaObrCile2;
        this.souradniceCil = tgs;
    }
    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku, Point[] objs, Point[] tgs, String uri) {
        super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani,uri);
        this.bankaObrazku = bankaObrazku;
        this.souradniceObj = objs;
        this.souradniceCil = tgs;
    }

    public int[] getBankaObrazku() {
        return bankaObrazku;
    }

    public int[] getBankaObrCile() {
        return bankaObrCile;
    }

    public int[] getBankaObrCile2() {
        return bankaObrCile2;
    }

    public Point[] getSouradniceObj() {
        return souradniceObj;
    }

    public Point[] getSouradniceCil() {
        return souradniceCil;
    }
}
