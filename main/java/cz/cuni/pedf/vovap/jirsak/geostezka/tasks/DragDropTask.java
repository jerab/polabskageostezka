package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;


import android.graphics.Point;
import android.util.Log;

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
	private Point[] rozmeryCil;
	private String[] orientaceDrop;
	private int layoutDraw;
	private int[] backgroundDraw;

	/*
		public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku, String uri, int retez) {
			super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani, uri, retez);
			this.bankaObrazku = bankaObrazku;
		}
		*/
    public DragDropTask(int id, String label, String nazev, String zadani, String zpetVazbaOk,
						int layoutSource, int[] bckDrawable,
						int[] bankaObrazku, int[] bankaObrCile, int[] bankaObrCile2,
						Point[] tgs, Point[] rozmeryCilu, String[] orientaceDropZon, String uri, int retez) {
        super(id, label,Config.TYP_ULOHY_DRAGDROP, nazev, zadani, new String[]{zpetVazbaOk}, uri, retez);
        this.bankaObrazku = bankaObrazku;
        this.bankaObrCile = bankaObrCile;
        this.bankaObrCile2 = bankaObrCile2;
        this.souradniceCil = tgs;
		this.rozmeryCil= rozmeryCilu;
		orientaceDrop = orientaceDropZon;
		layoutDraw = layoutSource;
		backgroundDraw = bckDrawable;
    }
    /*
    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku, Point[] objs, Point[] tgs, String uri, int retez) {
        super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani,uri, retez);
        this.bankaObrazku = bankaObrazku;
        this.souradniceObj = objs;
        this.souradniceCil = tgs;
    }*/

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

	public Point[] getRozmeryCil() {
		return rozmeryCil;
	}

    public Point[] getSouradniceCil() {
        return souradniceCil;
    }

    public String getOrientaceDropZony(int idZony) {
		if(orientaceDrop.length > idZony) {
			return orientaceDrop[idZony];
		}else {
			return "-";
		}
	}

	public int getLayoutDraw() {
		return layoutDraw;
	}

	public int getBackgroundDraw(int ind) {
		if(ind > backgroundDraw.length) {
			return getBackgroundDraw(ind - 1);
		}
    	return backgroundDraw[ind];
	}

	public int getBackgroundDrawCount() {
		Log.d("Geo - DD Task", "background images length: " + backgroundDraw.length);
    	return backgroundDraw.length;
	}

}
