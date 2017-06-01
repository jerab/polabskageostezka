package cz.cuni.pedf.vovap.jirsak.geostezka;


import android.view.View;

/**
 * Created by Fogs on 31.5.2017.
 */

public class DragDropTask extends Task {
    private int[] bankaObrazku;
    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku) {
        super(id, 2, nazev, zadani);
        this.bankaObrazku = bankaObrazku;
    }

    public int[] getBankaObrazku() {
        return bankaObrazku;
    }
}
