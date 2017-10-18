package cz.cuni.pedf.vovap.jirsak.geostezka.tasks;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Config;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.Task;

/**
 * Created by tomason on 13.10.2017.
 */

public class DrawTask extends Task {
	private int layoutId;
	private int bckStartDrawId;
	private int bckFinalDrawId;

	public DrawTask(int id, String label, String nazev, String zadani, int layoutSource, int bckStartDrawable, int bckFinalDrawable, String zpetVazba, String
			uri, int retezId) {
		super(id, label, Config.TYP_ULOHY_DRAW, nazev, zadani, new String[]{zpetVazba}, uri, retezId);
		layoutId = layoutSource;
		bckFinalDrawId = bckFinalDrawable;
		bckStartDrawId = bckStartDrawable;
	}

	public int getLayout() {
		return layoutId;
	}

	public int getBckStart() {
		return bckStartDrawId;
	}

	public int getBckFinal() {
		return bckFinalDrawId;
	}

}
