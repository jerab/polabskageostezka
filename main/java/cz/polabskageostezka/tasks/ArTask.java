package cz.polabskageostezka.tasks;

import cz.polabskageostezka.utils.Task;


/**
 * Created by tomason on 01.06.2017.
 */

public class ArTask extends Task {

	private String[] objects3D;
	private String target;

	public ArTask(int id, String label, int typ, String nazev, String zadani, String[] content3d, String target, String zpetVazba, String uri) {
		super(id, label, typ, nazev, zadani, new String[]{zpetVazba}, uri, -1);
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
