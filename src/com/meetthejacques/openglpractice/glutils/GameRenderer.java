package com.meetthejacques.openglpractice.glutils;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;

import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetUniformLocation;

import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.meetthejacques.openglpractice.R;
import com.meetthejacques.openglpractice.utils.LoggerConfig;
import com.meetthejacques.openglpractice.utils.MatrixHelper;
import com.meetthejacques.openglpractice.utils.TextResourceReader;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

public class GameRenderer implements Renderer {

	private static final int BYTES_PER_FLOAT = 4;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	
	private static final String A_COLOR = "a_Color";
	private static final String A_POSITION = "a_Position";
	private static final String U_MATRIX = "u_Matrix";
	
	private final FloatBuffer vertexData;
	private final Context context;
	
	private final float[] projectionMatrix = new float[16];
	private final float[] modelMatrix = new float[16];
	
	private int program;
	private int aColorLocation;
	private int aPositionLocation;
	private int uMatrixLocation;
	
	public GameRenderer(Context context){
		this.context = context;
		
		float[] tableVerticies = {
		//the coordinates of the table and uses two triangles because of the way that openGL draws stuff
				   0,     0,  1f,   1f,   1f,
				-0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
				 0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
				 0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
				-0.5f,  0.8f, 0.7f, 0.7f, 0.7f,
				-0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
				 
		//the coordinates of the dividing line of the table
				-0.5f,  0f, 1f, 0f, 0f,
				 0.5f,  0f, 1f, 0f, 0f,
				 
		//the coordinated of the mallet
				 0f, -0.4f, 0f, 0f, 1f,
				 0f,  0.4f, 1f, 0f, 0f
		};
		
		//allocate space for the coordinate data into the byte buffer and then put the data in the buffer
		vertexData = ByteBuffer.allocateDirect(tableVerticies.length * BYTES_PER_FLOAT)
							   .order(ByteOrder.nativeOrder())
							   .asFloatBuffer();
		vertexData.put(tableVerticies);
		
	}
	@Override
	public void onDrawFrame(GL10 gl) {
		//clear the screen
		glClear(GL_COLOR_BUFFER_BIT);
		
		//draw the table
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
		
		//draw the center line
		GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

		//draw the mallets
		GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
		GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);

		GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//set how big the view will be
		glViewport(0, 0, width, height);
		
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, 0f, 0f, -2.5f);
		rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
		
		final float[] temp = new float[16];
		multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
		System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//set the color to black
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		//read in the shader code
		String vertexShaderData = TextResourceReader.readRawTextFileFromResource(context, R.raw.simple_vertex_shader);
		String fragmentShaderData = TextResourceReader.readRawTextFileFromResource(context, R.raw.simple_fragment_shader);
		
		//compile the shader code
		int vertexShader = ShaderHelper.compileVertexShader(vertexShaderData);
		int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderData);
		
		//link the shaders into a program
		program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
		
		if (LoggerConfig.ON){
			//validate the program to make sure everything is running right
			ShaderHelper.validateProgram(program);
		}
		
		//set the program to be used
		glUseProgram(program);
		
		//get the location of out color and position variables from within the shaders
		aColorLocation = glGetAttribLocation(program, A_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		
		//set the position of the vertesx array to 0 and read the array into the openGL program
		vertexData.position(0);
		glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
		glEnableVertexAttribArray(aPositionLocation);
		
		vertexData.position(POSITION_COMPONENT_COUNT);
		glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
		glEnableVertexAttribArray(aColorLocation);
		
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
		
	}

	
}
