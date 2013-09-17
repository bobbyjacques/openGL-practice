package com.meetthejacques.openglpractice.glutils;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_LINEAR;

import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLUtils.texImage2D;

import com.meetthejacques.openglpractice.utils.LoggerConfig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TextureHelper {
	
	private static final String TAG = "TEXTURE_HELPER";
	
	public static int loadTexture(Context context, int resourceId) {
		//create the texture
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);
		
		if(textureObjectIds[0] == 0) {
			if (LoggerConfig.ON) {
				Log.e(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}
		
		//set the bitmap to be the original unscaled image
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		//decode the actual image
		final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		
		if (bitmap == null) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
			}
			//deletes the texture if the bitmap load failed
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		//bind the texture
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		
		//set the texture filters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		
		//load the bitmap to the texture and then release the bitmap
		texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		//unbind the texture
		glBindTexture(GL_TEXTURE_2D, 0);
		
		
		return textureObjectIds[0];
	}
}
