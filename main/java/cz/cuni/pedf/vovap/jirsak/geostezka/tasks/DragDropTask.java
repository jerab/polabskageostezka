package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;


import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

/**
 * Created by Fogs on 31.5.2017.
 */

public class DragDropTask extends Task {
    private int[] bankaObrazku;
    public DragDropTask(int id, String nazev, String zadani, int[] bankaObrazku) {
        super(id, Config.TYP_ULOHY_DRAGDROP, nazev, zadani);
        this.bankaObrazku = bankaObrazku;
    }

    public int[] getBankaObrazku() {
        return bankaObrazku;
    }
}
