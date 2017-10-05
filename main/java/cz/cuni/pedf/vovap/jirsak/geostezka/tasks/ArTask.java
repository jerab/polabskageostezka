package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;


/**
 * Created by tomason on 01.06.2017.
 */

public class ArTask extends Task {

	public ArTask(int id, String label, int typ, String nazev, String zadani, String zpetVazba, String uri) {
		super(id, label, typ, nazev, zadani, new String[]{zpetVazba}, uri, -1);
	}

	public void setTargets() {

	}
}
