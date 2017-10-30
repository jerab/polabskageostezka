package cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ar_content;

import java.nio.Buffer;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseApp;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils.MeshObject;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils.ObjLoader;

/**
 * Created by tomason on 16.10.2017.
 */

public class Gabro extends MeshObject {
	private Buffer mVertBuff;
	private Buffer mTexCoordBuff;
	private Buffer mNormBuff;
	private Buffer mIndBuff;

	private int indicesNumber = 0;
	private int verticesNumber = 0;
	private ObjLoader obj;

	private static String[] TEXTURES = new String[] {
			"obj/gabro.jpg"
	};

	public static String[] getTextures() {
		return TEXTURES;
	}

	public Gabro() {
		obj = new ObjLoader(BaseApp.getInstance(), "obj/kamen.obj");
		setVerts();
		setTexCoords();
		setNorms();
		setIndices();
	}

	private void setIndices() {
		mIndBuff = null;
		indicesNumber = 0;
	}

	private void setVerts() {
		float[] verts = obj.getPositions();
		mVertBuff = fillBuffer(verts);
		verticesNumber = verts.length;
	}
	private void setNorms() {
		mNormBuff = fillBuffer(obj.getNormals());
	}
	private void setTexCoords() {
		mTexCoordBuff = fillBuffer(obj.getTextureCoordinates());
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
