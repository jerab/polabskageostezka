package cz.cuni.pedf.vovap.jirsak.geostezka;


/**
 * Created by Fogs on 31.5.2017.
 */

public class DragDropTask extends Task {
    public DragDropTask(int id, int typ, String nazev, String zadani) {
        super(id, R.integer.TYP_ULOHY_DRAGDROP, nazev, zadani);
    }
}
