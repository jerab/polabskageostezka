package cz.polabskageostezka.tasks;

import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.Stanoviste;
import cz.polabskageostezka.utils.Task;

/**
 * Created by tomason on 13.10.2017.
 */

public class DrawTask extends Task {
	private int layoutId;
	private int bckStartDrawId;
	private int bckFinalDrawId;

	public DrawTask(int id, Stanoviste stanoviste, String zadani, int layoutSource, int bckStartDrawable, int bckFinalDrawable, String zpetVazba, int retezId) {
		super(id, stanoviste, Config.TYP_ULOHY_DRAW, zadani, new String[]{zpetVazba}, retezId);
		layoutId = layoutSource;
		bckFinalDrawId = bckFinalDrawable;
		bckStartDrawId = bckStartDrawable;
	}
	public DrawTask(int id, Stanoviste stanoviste, String zadani, int layoutSource, int bckStartDrawable, int bckFinalDrawable, String zpetVazba, int
			retezId, String extraLabel, String extraNazev) {
		super(id, stanoviste, Config.TYP_ULOHY_DRAW, zadani, new String[]{zpetVazba}, retezId);
		layoutId = layoutSource;
		bckFinalDrawId = bckFinalDrawable;
		bckStartDrawId = bckStartDrawable;
		if(!extraLabel.isEmpty())
			super.setLabel(getLabel() + extraLabel);
		if(!extraNazev.isEmpty())
			super.setNazev(getNazev() + extraNazev);
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
