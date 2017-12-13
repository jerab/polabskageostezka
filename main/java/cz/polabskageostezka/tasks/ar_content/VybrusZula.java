package cz.polabskageostezka.tasks.ar_content;

import java.nio.Buffer;

import cz.polabskageostezka.utils.BaseApp;
import cz.polabskageostezka.utils.ar_utils.MeshObject;
import cz.polabskageostezka.utils.ar_utils.ObjLoader;

/**
 * Created by tomason on 16.10.2017.
 */

public class VybrusZula extends MeshObject {
	private Buffer mVertBuff;
	private Buffer mTexCoordBuff;
	private Buffer mNormBuff;
	private Buffer mIndBuff;

	private int indicesNumber = 0;
	private int verticesNumber = 0;

	private static String[] TEXTURES = new String[] {
			"obj/vybrusy/zula15-vybrus.png"
	};

	public static String[] getTextures() {
		return TEXTURES;
	}

	public VybrusZula() {
		ObjLoader obj = new ObjLoader(BaseApp.getInstance(), "obj/vybrusy/vybrusTrans.obj");
		setVerts(obj.getVerts());
		setTexCoords(obj.getTextureCoordinates());
		setNorms(obj.getNormals());
		//setIndices(obj.getIndices());
		this.defScale = 20f;
	}

	private void setVerts(float[] verts) {
		mVertBuff = fillBuffer(verts);
		verticesNumber = verts.length / 3;
	}
	private void setNorms(float[] norms) {
		mNormBuff = fillBuffer(norms);
	}
	private void setTexCoords(float[] coords) {
		mTexCoordBuff = fillBuffer(coords);
	}

	private void setIndices(short[] indices) {
		mIndBuff = fillBuffer(indices);
		indicesNumber = indices.length;
	}

	@Override
	public Buffer getBuffer(BUFFER_TYPE bufferType) {
		Buffer result = null;
		switch (bufferType)
		{
			case BUFFER_TYPE_VERTEX:
				result = mVertBuff;
				break;
			case BUFFER_TYPE_TEXTURE_COORD:
				result = mTexCoordBuff;
				break;
			case BUFFER_TYPE_NORMALS:
				result = mNormBuff;
				break;
			case BUFFER_TYPE_INDICES:
				result = mIndBuff;
			default:
				break;

		}

		return result;
	}

	@Override
	public int getNumObjectVertex() {
		return verticesNumber;
	}

	@Override
	public int getNumObjectIndex() {
		return indicesNumber;
	}
}
