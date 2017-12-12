package cz.polabskageostezka.tasks;

import cz.polabskageostezka.utils.Config;
import cz.polabskageostezka.utils.Stanoviste;
import cz.polabskageostezka.utils.Task;


/**
 * Created by tomason on 01.06.2017.
 */

public class ArTask extends Task {

	private String[] objects3D;
	/// name of xml with target info in assets/
	private String target;
	public int extraDialogLayout = 0;

	public ArTask(int id, Stanoviste stanoviste, String zadani, String[] content3d, String target, String zpetVazba, int retezId) {
		super(id, stanoviste, Config.TYP_ULOHY_AR, zadani, new String[]{zpetVazba}, retezId);
		this.objects3D = content3d;
		this.target = target;
	}

	public ArTask(int id, Stanoviste stanoviste, String zadani, String[] content3d, String target, String zpetVazba, int dialogLayout, int retezId) {
		super(id, stanoviste, Config.TYP_ULOHY_AR, zadani, new String[]{zpetVazba}, retezId);
		extraDialogLayout = dialogLayout;
		this.objects3D = content3d;
		this.target = target;
	}

	public String getContent3d(int index) {
		if(index >= objects3D.length) {
			return objects3D[0];
		}
		return objects3D[index];
	}

	public String getTarget() {
		return this.target;
	}

	private static class fullObjectContent {
		public static String[] textures;
		public static String objClass;
		public fullObjectContent(String[] sources) {
			objClass = sources[0];
			for(int i = 1; i < sources.length; i++) {
				textures[i-1] = sources[i];
			}
		}
	}
}
