/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_utils;

import java.io.IOException;
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
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;

import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ar_content.Gabro;
import cz.cuni.pedf.vovap.jirsak.geostezka.tasks.ar_content.Teapot;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.BaseArTaskActivity;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_support.ArVuforiaAppRenderer;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_support.ArVuforiaAppRendererControl;
import cz.cuni.pedf.vovap.jirsak.geostezka.utils.ar_support.ArVuforiaApplicationSession;


// The renderer class for the ImageTargets sample. 
public class ImageTargetRenderer implements GLSurfaceView.Renderer, ArVuforiaAppRendererControl {
	private static final String LOGTAG = "GEO-ImageTargetRenderer";

	private ArVuforiaApplicationSession vuforiaAppSession;
	private BaseArTaskActivity mActivity;
	private ArVuforiaAppRenderer mSampleAppRenderer;

	private Vector<Texture> mTextures;

	private int shaderProgramID;
	private int vertexHandle;
	private int textureCoordHandle;
	private int mvpMatrixHandle;
	private int texSampler2DHandle;

	private MeshObject mTeapot;

	private float kBuildingScale = 0.012f;
	//private SampleApplication3DModel mBuildingsModel;

	private boolean mIsActive = false;
	private boolean mModelIsLoaded = false;

	private float objectScaleFloat = 0.003f;
	private float maxObjectScale = 0.006f;
	private float minObjectScale = 0.0008f;
	//private float objectScaleFloat = 1f;
	private float objectRotateFloatZ = 0;
	private float objectRotateFloatY = 0;


	public ImageTargetRenderer(BaseArTaskActivity activity, ArVuforiaApplicationSession session) {
		mActivity = activity;
		vuforiaAppSession = session;
		// ArVuforiaAppRenderer used to encapsulate the use of RenderingPrimitives setting
		// the device mode AR/VR and stereo mode
		mSampleAppRenderer = new ArVuforiaAppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f, 5f);
	}


	// Called to draw the current frame.
	@Override
	public void onDrawFrame(GL10 gl) {
		if (!mIsActive)
			return;

		// Call our function to render content from ArVuforiaAppRenderer class
		mSampleAppRenderer.render();
	}


	public void setActive(boolean active) {
		mIsActive = active;

		if (mIsActive)
			mSampleAppRenderer.configureVideoBackground();
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

	// Called when the surface is created or recreated.
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");

		// Call Vuforia function to (re)initialize rendering after first use
		// or after OpenGL ES context was lost (e.g. after onPause/onResume):
		vuforiaAppSession.onSurfaceCreated();

		mSampleAppRenderer.onSurfaceCreated();
	}


	// Called when the surface changed size.
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");

		// Call Vuforia function to handle render surface size changes:
		vuforiaAppSession.onSurfaceChanged(width, height);

		// RenderingPrimitives to be updated when some rendering change is done
		mSampleAppRenderer.onConfigurationChanged(mIsActive);

		initRendering();
	}


	// Function for initializing the renderer.
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

		shaderProgramID = SampleUtils.createProgramFromShaderSrc(
				CubeShaders.CUBE_MESH_VERTEX_SHADER,
				CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

		vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexPosition");
		textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexTexCoord");
		mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"modelViewProjectionMatrix");
		texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"texSampler2D");

		if (!mModelIsLoaded) {
			//mTeapot = new Teapot();
			mTeapot = mActivity.get3DObject();
			objectScaleFloat *= mTeapot.getDefScale();
			minObjectScale *= mTeapot.getDefScale();
			maxObjectScale *= mTeapot.getDefScale();

			mActivity.showDebugMsg("Loading model Teapot");
			// Hide the Loading Dialog
			//mActivity.loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
		}

	}

	public void updateConfiguration() {
		mSampleAppRenderer.onConfigurationChanged(mIsActive);
	}

	// The render function called from ArVuforiaAppRendering by using RenderingPrimitives views.
	// The state is owned by ArVuforiaAppRenderer which is controlling it's lifecycle.
	// State should not be cached outside this method.
	public void renderFrame(State state, float[] projectionMatrix) {
		// Renders video background replacing Renderer.DrawVideoBackground()
		mSampleAppRenderer.renderVideoBackground();

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// handle face culling, we need to detect if we are using reflection
		// to determine the direction of the culling
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);

		// Did we find any trackables this frame?
		for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
			TrackableResult result = state.getTrackableResult(tIdx);
			Trackable trackable = result.getTrackable();
			//printUserData(trackable);
			Matrix44F modelViewMatrix_Vuforia = Tool.convertPose2GLMatrix(result.getPose());
			float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

			//int textureIndex = trackable.getName().equalsIgnoreCase("stones") ? 0 : 2;
			int textureIndex = 0;

			//mActivity.showDebugMsg(trackable.getName());

			// deal with the modelview and projection matrices
			float[] modelViewProjection = new float[16];

			Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f, objectScaleFloat);
			Matrix.scaleM(modelViewMatrix, 0, objectScaleFloat, objectScaleFloat, objectScaleFloat);
			/// rotate based on gestures
			Matrix.rotateM(modelViewMatrix, 0, objectRotateFloatZ, 0, 0, 1.0f);
			Matrix.rotateM(modelViewMatrix, 0, objectRotateFloatY, 0, 1.0f, 0);

			Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

			// activate the shader program and bind the vertex/normal/tex coords
			GLES20.glUseProgram(shaderProgramID);

			GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT, false, 0, mTeapot.getVertices());
			GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());

			GLES20.glEnableVertexAttribArray(vertexHandle);
			GLES20.glEnableVertexAttribArray(textureCoordHandle);

			// activate texture 0, bind it, and pass to shader
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures.get(textureIndex).mTextureID[0]);
			GLES20.glUniform1i(texSampler2DHandle, 0);

			// pass the model view matrix to the shader
			GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, modelViewProjection, 0);

			// finally draw the teapot
			// if indices are set in 3D object
			if(mTeapot.getNumObjectIndex() > 0) {
				GLES20.glDrawElements(GLES20.GL_TRIANGLES, mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
						mTeapot.getIndices());
			}else {
				GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mTeapot.getNumObjectVertex());
			}

			// disable the enabled arrays
			GLES20.glDisableVertexAttribArray(vertexHandle);
			GLES20.glDisableVertexAttribArray(textureCoordHandle);
			SampleUtils.checkGLError("Render Frame");
		}

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);

	}

	private void printUserData(Trackable trackable) {
		String userData = (String) trackable.getUserData();
		Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
		mActivity.showDebugMsg("UserData:Retreived User Data	\"" + userData + "\"");
	}


	public void setTextures(Vector<Texture> textures) {
		mTextures = textures;

	}

}
