package cz.polabskageostezka.tasks;


import android.graphics.Point;
import android.util.Log;

import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.Stanoviste;
import cz.polabskageostezka.utils.Task;


/**
 * Created by Fogs on 31.5.2017.
 */

public class DragDropTask extends Task {
	private String[] bankaNadpisCile2;
	private int[] bankaObrazku;
    private int[] bankaObrCile;
    private int[] bankaObrCile2;
    private String[] bankaTextCile2;
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
    public DragDropTask(int id, Stanoviste stanoviste, String zadani, String zpetVazbaOk,
						int layoutSource, int[] bckDrawable,
						int[] bankaObrazku, int[] bankaObrCile, int[] bankaObrCile2,
						Point[] tgs, Point[] rozmeryCilu, String[] orientaceDropZon, String[] afterClickTexty, String[] afterClickNadpisy, int retez) {

    	super(id, stanoviste, Config.TYP_ULOHY_DRAGDROP, zadani, new String[]{zpetVazbaOk}, retez);
        this.bankaObrazku = bankaObrazku;
        this.bankaObrCile = bankaObrCile;
        this.bankaObrCile2 = bankaObrCile2;
        this.bankaTextCile2 = afterClickTexty;
		this.bankaNadpisCile2 = afterClickNadpisy;

        this.souradniceCil = tgs;
		this.rozmeryCil= rozmeryCilu;
		orientaceDrop = orientaceDropZon;
		layoutDraw = layoutSource;
		backgroundDraw = bckDrawable;
    }

	public DragDropTask(int id, Stanoviste stanoviste, String zadani, String zpetVazbaOk,
						int layoutSource, int[] bckDrawable,
						int[] bankaObrazku, int[] bankaObrCile, int[] bankaObrCile2,
						Point[] tgs, Point[] rozmeryCilu, String[] orientaceDropZon, int retez,
						String extraLabel, String extraNazev) {

		super(id, stanoviste, Config.TYP_ULOHY_DRAGDROP, zadani, new String[]{zpetVazbaOk}, retez);
		this.bankaObrazku = bankaObrazku;
		this.bankaObrCile = bankaObrCile;
		this.bankaObrCile2 = bankaObrCile2;
		this.bankaTextCile2 = new String[]{};
		this.bankaNadpisCile2 = new String[]{};
		this.souradniceCil = tgs;
		this.rozmeryCil= rozmeryCilu;
		orientaceDrop = orientaceDropZon;
		layoutDraw = layoutSource;
		backgroundDraw = bckDrawable;
		if(!extraLabel.isEmpty())
			super.setLabel(getLabel() + extraLabel);
		if(!extraNazev.isEmpty())
			super.setNazev(getNazev() + extraNazev);
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

    public String[] getBankaTextCile2() {
    	return bankaTextCile2;
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

	public String getNadpisCile2(int i) {
    	if(bankaNadpisCile2.length > i) {
			return bankaNadpisCile2[i];
		}else {
			return "";
		}
	}
}
