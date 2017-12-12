package cz.polabskageostezka.utils.ar_utils;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;

import cz.polabskageostezka.utils.BaseArTaskActivity;
import cz.polabskageostezka.utils.ar_support.ArVuforiaAppRenderer;
import cz.polabskageostezka.utils.ar_support.ArVuforiaAppRendererControl;
import cz.polabskageostezka.utils.ar_support.ArVuforiaApplicationSession;


// The mRenderer class for the ImageTargets sample.
public class ArRenderer implements GLSurfaceView.Renderer, ArVuforiaAppRendererControl {
	private static final String LOGTAG = "GEO-ArRenderer";

	private ArVuforiaApplicationSession appSession;
	private BaseArTaskActivity mActivity;
	private ArVuforiaAppRenderer mRenderer;

	private Vector<Texture> mTextures;

	private int shaderProgramID;
	private int vertexHandle;
	private int textureCoordHandle;
	private int mvpMatrixHandle;
	private int texSampler2DHandle;

	private MeshObject object;

	private boolean mIsActive = false;
	private boolean mModelIsLoaded = false;

	private float objectScaleFloat = 0.003f;
	private float maxObjectScale = 0.006f;
	private float minObjectScale = 0.0008f;
	private float objectRotateFloatZ = 0;
	private float objectRotateFloatY = 0;


	public ArRenderer(BaseArTaskActivity activity, ArVuforiaApplicationSession session) {
		mActivity = activity;
		appSession = session;
		mRenderer = new ArVuforiaAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);
	}


	// Called to draw the current frame.
	@Override
	public void onDrawFrame(GL10 gl) {
		if (!mIsActive)
			return;

		// Call our function to render content from ArVuforiaAppRenderer class
		mRenderer.render();
	}


	public void setActive(boolean active) {
		mIsActive = active;

		if (mIsActive)
			mRenderer.configureVideoBackground();
	}

	public void zoomInObject() {
		if(objectScaleFloat < maxObjectScale) {
			objectScaleFloat += 0.0002f;
		}
	}

	public void zoomOutObject() {
		if(objectScaleFloat > minObjectScale) {
			objectScaleFloat -= 0.0002f;
		}
	}

	public void rotateObjectLeftZ() {
		if(objectRotateFloatZ <= 0) {
			objectRotateFloatZ = 360.0f;
		}
		objectRotateFloatZ -= 10.0f;
	}
	public void rotateObjectRightZ() {
		if(objectRotateFloatZ >= 360.0f) {
			objectRotateFloatZ = 0;
		}
		objectRotateFloatZ += 10.0f;
	}

	public void rotateObjectLeftY() {
		if(objectRotateFloatY <= 0) {
			objectRotateFloatY = 360.0f;
		}
		objectRotateFloatY -= 10.0f;
	}
	public void rotateObjectRightY() {
		if(objectRotateFloatY >= 360.0f) {
			objectRotateFloatY = 0;
		}
		objectRotateFloatY += 10.0f;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

		appSession.onSurfaceCreated();

		mRenderer.onSurfaceCreated();
	}


	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

		appSession.onSurfaceChanged(width, height);

		// RenderingPrimitives to be updated when some rendering change is done
		mRenderer.onConfigurationChanged(mIsActive);

		initRendering();
	}


	// Function for initializing the mRenderer.
	private void initRendering() {
		mActivity.showDebugMsg(" initRendering");
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f : 1.0f);

		for (Texture t : mTextures) {
			GLES20.glGenTextures(1, t.mTextureID, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
					GLES20.GL_UNSIGNED_BYTE, t.mData);
		}

		shaderProgramID = ArUtils.createProgramFromShaderSrc(
				Shaders.CUBE_MESH_VERTEX_SHADER,
				Shaders.CUBE_MESH_FRAGMENT_SHADER);

		vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexPosition");
		textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexTexCoord");
		mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"modelViewProjectionMatrix");
		texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"texSampler2D");

		if (!mModelIsLoaded) {
			//object = new Teapot();
			object = mActivity.get3DObject();
			objectScaleFloat *= object.getDefScale();
			minObjectScale *= object.getDefScale();
			maxObjectScale *= object.getDefScale();

			mActivity.showDebugMsg("Loading model Teapot");
			// Hide the Loading Dialog
			//mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
		}

	}

	public void updateConfiguration() {
		mRenderer.onConfigurationChanged(mIsActive);
	}

	// The state is owned by ArVuforiaAppRenderer which is controlling it's lifecycle.
	// State should not be cached outside this method.
	public void renderFrame(State state, float[] lPMatrix) {
		// Renders video background replacing Renderer.DrawVideoBackground()
		mRenderer.renderVideoBackground();

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		// Did we find any trackables this frame?
		for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
			TrackableResult result = state.getTrackableResult(tIdx);
			//Trackable trackable = result.getTrackable();
			//printUserData(trackable);
			Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());

			float[] lMVMatrix = modelViewMatrix_Vuforia.getData();
			float[] lMVPMatrix = new float[16];

			//String trackName = trackable.getName();
			int textureIndex = 0;

			//mActivity.showDebugMsg(trackable.getName());

			// deal with the modelview and projection matrices


			Matrix.translateM(lMVMatrix, 0, 0.0f, 0.0f, objectScaleFloat);
			Matrix.scaleM(lMVMatrix, 0, objectScaleFloat, objectScaleFloat, objectScaleFloat);
			/// rotate based on gestures
			Matrix.rotateM(lMVMatrix, 0, objectRotateFloatZ, 0, 0, 1.0f);
			Matrix.rotateM(lMVMatrix, 0, objectRotateFloatY, 0, 1.0f, 0);

			Matrix.multiplyMM(lMVPMatrix, 0, lPMatrix, 0, lMVMatrix, 0);

			// activate the shader program and bind the vertex/normal/tex coords
			GLES20.glUseProgram(shaderProgramID);

			GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, object.getVertices());
			GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, object.getTexCoords());

			GLES20.glEnableVertexAttribArray(vertexHandle);
			GLES20.glEnableVertexAttribArray(textureCoordHandle);

			// bind the texture and pass to shader
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
			GLES20.glUniform1i(texSampler2DHandle, 0);

			// pass the model view matrix to the shader
			GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, lMVPMatrix, 0);

			// vykreslit objekt
			// if indices are set in 3D object
			if(object.getNumObjectIndex() > 0) {
				GLES20.glDrawElements(GLES20.GL_TRIANGLES, object.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
						object.getIndices());
				Log.d(LOGTAG, "Draw Elements with INDICES");
			}else {
				GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, object.getNumObjectVertex());
				Log.d(LOGTAG, "Draw Elements without INDICES");
			}

			// disable the enabled arrays
			GLES20.glDisableVertexAttribArray(vertexHandle);
			GLES20.glDisableVertexAttribArray(textureCoordHandle);
			ArUtils.checkGLError("Render Frame");
		}

		GLES20.glDisable(GLES20.GL_BLEND);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);

	}

	public void setTextures(Vector<Texture> textures) {
		mTextures = textures;

	}

}
