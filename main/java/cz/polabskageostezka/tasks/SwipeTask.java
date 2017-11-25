package cz.polabskageostezka.tasks;

import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.Task;

/**
 * Created by Fogs on 27.09.2017.
 */

public class SwipeTask extends Task {
    public SwipeTask(int id, String label, String nazev, String zadani, String[] zpetnaVazba, String uri, int retezId) {
        super(id, label, Config.TYP_ULOHY_SWIPE, nazev, zadani, zpetnaVazba, uri, retezId);
        //swipe jen urcuje smer - prida se sipka x podle barvz overeni uspechu
    }
}
