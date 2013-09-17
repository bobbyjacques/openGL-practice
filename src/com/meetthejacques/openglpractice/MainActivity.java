package com.meetthejacques.openglpractice;

import com.meetthejacques.openglpractice.glutils.GameRenderer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

public class MainActivity extends Activity {

	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo config = am.getDeviceConfigurationInfo();
		final boolean supportsEs2 = config.reqGlEsVersion >= 0x20000;
		
		
		glSurfaceView = new GLSurfaceView(this);
		
		if(supportsEs2){
			glSurfaceView.setEGLContextClientVersion(2);
			glSurfaceView.setRenderer(new GameRenderer(this));
			rendererSet = true;
		} else {
			Toast.makeText(this, "OpenGL ES 2.0 Not Available" , Toast.LENGTH_SHORT).show();
			return;
		}
		
		this.setContentView(glSurfaceView);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(rendererSet){
			glSurfaceView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(rendererSet){
			glSurfaceView.onResume();
		}
	}

}
