package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

/**
 * Created by Fogs on 27.09.2017.
 */

public class SwipeTask extends Task {
    public SwipeTask(int id, String label, String nazev, String zadani, String uri, int retezId) {
        super(id, label, Config.TYP_ULOHY_SWIPE, nazev, zadani, uri, retezId);
        //swipe jen urcuje smer - prida se sipka x podle barvz overeni uspechu
    }
}
