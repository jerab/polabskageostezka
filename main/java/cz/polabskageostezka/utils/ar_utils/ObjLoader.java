package cz.polabskageostezka.utils.ar_utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by tomason on 23.10.2017.
 */

public final class ObjLoader {
	private Context mContext;
	public float[] normals;
	public float[] textureCoordinates;
	public float[] vertices;
	public short[] indices;

	public ObjLoader(Context context, String file) {
		this(context, file, false);
	}

	public ObjLoader(Context context, String file, boolean usingIndices) {
		mContext = context;
		BufferedReader reader = null;

		InputStreamReader in = null;
		try {
			in = new InputStreamReader(mContext.getAssets().open(file));
			reader = new BufferedReader(in);

			// read file until EOF
			if(usingIndices) {
				parseWithIndices(reader);
			}else {
				parseWithoutIndices(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseWithIndices(BufferedReader reader) {
		Vector<Float> normales = new Vector<>();
		Vector<Float> textures = new Vector<>();
		Vector<String> faces = new Vector<>();
		Vector<Float> v = new Vector<>();
		try {
			// read file until EOF
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ");
				switch (parts[0]) {
					case "v":
						// vertices
						v.add(Float.valueOf(parts[1]));
						v.add(Float.valueOf(parts[2]));
						v.add(Float.valueOf(parts[3]));
						break;
					case "vt":
						// textures
						textures.add(Float.valueOf(parts[1]));
						textures.add(Float.valueOf(parts[2]));
						break;
					case "vn":
						// normals
						normales.add(Float.valueOf(parts[1]));
						normales.add(Float.valueOf(parts[2]));
						normales.add(Float.valueOf(parts[3]));
						break;
					case "f":
						// triangular faces: 3 for line
						faces.add(parts[1]);
						faces.add(parts[2]);
						faces.add(parts[3]);
						break;
				}
			}
		} catch (IOException e) {
			// cannot load or read file
		}

		vertices = new float[v.size()];
		int i = 0;
		for(Float vtF : v) {
			vertices[i++] = vtF.floatValue();
		}

		int numFaces = faces.size();
		normals = new float[numFaces * 3];
		textureCoordinates = new float[numFaces * 2];
		indices = new short[numFaces];
		int index = 0;
		int normalIndex = 0;
		int textureIndex = 0;
		int indiIndex = 0;
		Log.d("Geo - LOADER", "vertexSize: " + vertices.length);
		Log.d("Geo - LOADER", "textureSize: " + textureCoordinates.length);
		Log.d("Geo - LOADER", "indicesSize: " + indices.length);
		for (String face : faces) {
			/// face: vertex/texture/normal
			String[] parts = face.split("/");

			this.indices[indiIndex++] = (short) (Short.valueOf(parts[0]) -1);

			index = 2 * (Short.valueOf(parts[1]) - 1);
			//Log.d("Geo - LOADER", "index: " + index + ", textIndex: " + textureIndex);
			textureCoordinates[textureIndex++] = textures.get(index++);
			// NOTE: Bitmap gets y-inverted
			textureCoordinates[textureIndex++] = 1.0f - textures.get(index);
			//textureCoordinates[textureIndex++] = textures.get(index);

			if(parts.length > 2) {
				index = 3 * (Short.valueOf(parts[2]) - 1);
				this.normals[normalIndex++] = normales.get(index++);
				this.normals[normalIndex++] = normales.get(index++);
				this.normals[normalIndex++] = normales.get(index);
			}else {
				this.normals[normalIndex++] = 0;
				this.normals[normalIndex++] = 0;
				this.normals[normalIndex++] = 0;
			}
		}
	}
	private void parseWithoutIndices(BufferedReader reader) {
		Vector<Float> normales = new Vector<>();
		Vector<Float> textures = new Vector<>();
		Vector<String> faces = new Vector<>();
		Vector<Float> v = new Vector<>();
		try {
			// read file until EOF
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ");
				switch (parts[0]) {
					case "v":
						// vertices
						v.add(Float.valueOf(parts[1]));
						v.add(Float.valueOf(parts[2]));
						v.add(Float.valueOf(parts[3]));
						break;
					case "vt":
						// textures
						textures.add(Float.valueOf(parts[1]));
						textures.add(Float.valueOf(parts[2]));
						break;
					case "vn":
						// normals
						normales.add(Float.valueOf(parts[1]));
						normales.add(Float.valueOf(parts[2]));
						normales.add(Float.valueOf(parts[3]));
						break;
					case "f":
						// triangular faces: 3 for line
						faces.add(parts[1]);
						faces.add(parts[2]);
						faces.add(parts[3]);
						break;
				}
			}
		} catch (IOException e) {
			// cannot load or read file
		}

		int numFaces = faces.size();
		normals = new float[numFaces * 3];
		textureCoordinates = new float[numFaces * 2];
		vertices = new float[numFaces * 3];
		indices = new short[0];
		int index = 0;
		int normalIndex = 0;
		int textureIndex = 0;
		//int indiIndex = 0;
		int vertIndex = 0;
		//Log.d("Geo - LOADER", "vertexSize: " + vertices.length);
		//Log.d("Geo - LOADER", "textureSize: " + textureCoordinates.length);
		//Log.d("Geo - LOADER", "indicesSize: " + indices.length);
		for (String face : faces) {
			/// face: vertex/texture/normal
			String[] parts = face.split("/");

			//indices[indiIndex++] = (short) (Short.valueOf(parts[0]) - 1);

			/// vertices ///
			index = 3 * (Short.valueOf(parts[0]) - 1);
			vertices[vertIndex++] = v.get(index++); /// x
			vertices[vertIndex++] = v.get(index++); /// y
			vertices[vertIndex++] = v.get(index); /// z

			index = 2 * (Short.valueOf(parts[1]) - 1);
			//Log.d("Geo - LOADER", "index: " + index + ", textIndex: " + textureIndex);
			textureCoordinates[textureIndex++] = textures.get(index++);
			// NOTE: Bitmap gets y-inverted
			textureCoordinates[textureIndex++] = textures.get(index);

			if (parts.length > 2) {
				index = 3 * (Short.valueOf(parts[2]) - 1);
				this.normals[normalIndex++] = normales.get(index++);
				this.normals[normalIndex++] = normales.get(index++);
				this.normals[normalIndex++] = normales.get(index);
			} else {
				this.normals[normalIndex++] = 0;
				this.normals[normalIndex++] = 0;
				this.normals[normalIndex++] = 0;
			}
		}
	}

	public short[] getIndices() {
		return indices;
	}

	public float[] getVerts() {
		return vertices;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getTextureCoordinates() {
		return textureCoordinates;
	}
}
