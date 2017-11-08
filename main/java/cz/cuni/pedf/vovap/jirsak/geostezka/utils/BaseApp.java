package cz.cuni.pedf.vovap.jirsak.geostezka.utils;

import android.app.Application;
import android.content.Context;

import cz.cuni.pedf.vovap.jirsak.geostezka.R;

/**
 * Created by tomason on 04.10.2017.
 */

public class BaseApp extends Application {

	private static BaseApp instance;

	public BaseApp() {
		super();
		instance = this;
	}

	public static BaseApp getInstance() {
		return instance;
	}
}
