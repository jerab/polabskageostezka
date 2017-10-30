package cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseApp;

/**
 * Created by tomason on 23.10.2017.
 */

public final class ObjLoader {
	private Context mContext;
	public final int numFaces;

	public final float[] normals;
	public final float[] textureCoordinates;
	public final float[] positions;

	public ObjLoader(Context context, String file) {
		mContext = context;
		Vector<Float> vertices = new Vector<>();
		Vector<Float> normals = new Vector<>();
		Vector<Float> textures = new Vector<>();
		Vector<String> faces = new Vector<>();

		BufferedReader reader = null;
		try {
			InputStreamReader in = new InputStreamReader(mContext.getAssets().open(file));
			//inputStream = assets.open(fileName, AssetManager.ACCESS_BUFFER);
			reader = new BufferedReader(in);

			// read file until EOF
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" ");
				switch (parts[0]) {
					case "v":
						// vertices
						vertices.add(Float.valueOf(parts[1]));
						vertices.add(Float.valueOf(parts[2]));
						vertices.add(Float.valueOf(parts[3]));
						break;
					case "vt":
						// textures
						textures.add(Float.valueOf(parts[1]));
						textures.add(Float.valueOf(parts[2]));
						break;
					case "vn":
						// normals
						normals.add(Float.valueOf(parts[1]));
						normals.add(Float.valueOf(parts[2]));
						normals.add(Float.valueOf(parts[3]));
						break;
					case "f":
						// faces: vertex/texture/normal
						faces.add(parts[1]);
						faces.add(parts[2]);
						faces.add(parts[3]);
						break;
				}
			}
		} catch (IOException e) {
			// cannot load or read file
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					//log the exception
				}
			}
		}

		numFaces = faces.size();
		this.normals = new float[numFaces * 3];
		textureCoordinates = new float[numFaces * 2];
		positions = new float[numFaces * 3];
		int positionIndex = 0;
		int normalIndex = 0;
		int textureIndex = 0;
		for (String face : faces) {
			String[] parts = face.split("/");

			int index = 3 * (Short.valueOf(parts[0]) - 1);
			positions[positionIndex++] = vertices.get(index++);
			positions[positionIndex++] = vertices.get(index++);
			positions[positionIndex++] = vertices.get(index);

			index = 2 * (Short.valueOf(parts[1]) - 1);
			textureCoordinates[normalIndex++] = textures.get(index++);
			// NOTE: Bitmap gets y-inverted
			textureCoordinates[normalIndex++] = 1 - textures.get(index);

			index = 3 * (Short.valueOf(parts[2]) - 1);
			this.normals[textureIndex++] = normals.get(index++);
			this.normals[textureIndex++] = normals.get(index++);
			this.normals[textureIndex++] = normals.get(index);
		}
	}

	public float[] getPositions() {
		return positions;
	}

	public int getNumFaces() {
		return numFaces;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getTextureCoordinates() {
		return textureCoordinates;
	}

	private void parseMtl(String mtlFile) {
		BufferedReader reader = null;
		String[] mtl = null;
		String[] contents;
		try {
			InputStreamReader in = new InputStreamReader(mContext.getAssets().open(mtlFile));
			reader = new BufferedReader(in);
			// read file until EOF
			String line;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(" ");
				if (values.length < 1) {
					continue;
				}
				if (values[0] == "newmtl") {
					//mtl = contents[values[1]] = {}
					mtl = new String[] {values[1]};
				} else if (mtl == null) {
					Log.d("GEO OBJ Loader", "mtl file doesn't start with newmtl stmt");
					break;
				} else if (values[0] == "map_Kd") {
					// load the texture referred to by this declaration
					// TODO
					/*mtl[values[0]] = values[1];
					surf = pygame.image.load(mtl['map_Kd'])
					image = pygame.image.tostring(surf, 'RGBA', 1)
					ix, iy = surf.get_rect().size
					texid = mtl['texture_Kd'] = glGenTextures(1)
					glBindTexture(GL_TEXTURE_2D, texid)
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
							GL_LINEAR)
					glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
							GL_LINEAR)
					glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, ix, iy, 0, GL_RGBA,
							GL_UNSIGNED_BYTE, image)
							*/
				} else {
					//TODO
					//mtl[values[0]] = map(float,values[1:])
					//return contents;
				}
			}
		} catch (IOException e) {
				// cannot load or read file
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					//log the exception
				}
			}
		}
	}
}
