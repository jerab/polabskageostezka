package cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ar_content;

import android.util.Log;

import java.nio.Buffer;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseApp;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils.MeshObject;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils.ObjLoader;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils.ObjLoader2;

/**
 * Created by tomason on 24.10.2017.
 */

public class Cube extends MeshObject {

	private Buffer mVertBuff;
	private Buffer mTexCoordBuff;
	private Buffer mNormBuff;
	private Buffer mIndBuff;

	private int indicesNumber = 0;
	private int verticesNumber = 0;

	private static String[] TEXTURES = new String[] {
			"obj/capsule0.jpg"
	};

	public static String[] getTextures() {
		return TEXTURES;
	}


	public Cube()
	{
		ObjLoader2 obj = new ObjLoader2(BaseApp.getInstance(), "obj/capsule.obj");
		setVerts(obj.getVerts());
		setTexCoords(obj.getTextureCoordinates());
		setNorms(obj.getNormals());
		setIndices(obj.getIndices());
		this.defScale = 20f;
	}


	private void setVerts(Float[] verts) {
		for(int i = 0; i < verts.length; i=i+3) {
			Log.d("Geo - CUBE", "VERTS " + i + ": " + verts[i] + ", " + verts[i+1] + ", " + verts[i+2]);
		}
		mVertBuff = fillBuffer(verts);
		verticesNumber = verts.length / 3;
		/*double[] cube_VERTS = {
				1.000000,-1.000000,-1.000000,
				1.000000,-1.000000,1.000000,
				-1.000000,-1.000000,1.000000,
				-1.000000,-1.000000,-1.000000,
				1.000000,1.000000,-0.999999,
				0.999999,1.000000,1.000001,
				-1.000000,1.000000,1.000000,
				-1.000000,1.000000,-1.000000,
		};
		mVertBuff = fillBuffer(cube_VERTS);
		verticesNumber = cube_VERTS.length / 3;
		*/
	}
	private void setNorms(float[] norms) {
		for(int i = 0; i < norms.length; i=i+3) {
			Log.d("Geo - CUBE", "NORMS " + i + ": " + norms[i] + ", " + norms[i+1] + ", " + norms[i+2]);
		}
		mNormBuff = fillBuffer(norms);
		/*double[] cube_NORMS = {
				0.000000,-1.000000,0.000000,
				0.000000,1.000000,0.000000,
				1.000000,-0.000000,0.000000,
				-0.000000,-0.000000,1.000000,
				-1.000000,-0.000000,-0.000000,
				0.000000,0.000000,-1.000000,
		};
		mNormBuff = fillBuffer(cube_NORMS);*/
	}
	private void setTexCoords(float[] coords) {
		for(int i = 0; i < coords.length; i=i+2) {
			Log.d("Geo - CUBE", "TEXTCOORDS " + i + ": " + coords[i] + ", " + coords[i+1]);
		}
		mTexCoordBuff = fillBuffer(coords);
		/*double[] cube_TEX_COORDS = {
				0.624375,0.500624,
				0.624375,0.749375,
				0.375625,0.749375,
				0.375624,0.003126,
				0.624373,0.003126,
				0.624374,0.251874,
				0.375624,0.500625,
				0.375625,0.251875,
				0.873126,0.749375,
				0.873126,0.998126,
				0.624375,0.998126,
				0.375625,0.998126,
				0.126874,0.749375,
				0.126874,0.998126,
		};
		mTexCoordBuff = fillBuffer(cube_TEX_COORDS);*/
	}

	private void setIndices(short[] indices)
	{
		/*short[] cube_INDICES = {
				/*0,1,2,2,1,0,3,2,0,
				7,3,1,6,4,1,5,5,1,
				0,6,2,4,7,2,5,5,2,
				1,8,3,5,9,3,6,10,3,
				6,10,4,7,11,4,3,2,4,
				0,12,5,3,2,5,7,11,5,
				0,6,0,1,0,0,3,2,0,
				4,7,1,7,3,1,5,5,1,
				1,0,2,0,6,2,5,5,2,
				2,1,3,1,8,3,6,10,3,
				2,1,4,6,10,4,3,2,4,
				4,13,5,0,12,5,7,11,5

		};
		*/
		for(int i = 0; i < indices.length; i++) {
			Log.d("Geo - CUBE", "IND " + i + ": " + indices[i]);
		}
		mIndBuff = fillBuffer(indices);
		indicesNumber = indices.length;
	}

	@Override
	public int getNumObjectIndex()
	{
		return indicesNumber;
	}

	@Override
	public int getNumObjectVertex()
	{
		return verticesNumber;
	}

	@Override
	public Buffer getBuffer(BUFFER_TYPE bufferType)
	{
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
}
