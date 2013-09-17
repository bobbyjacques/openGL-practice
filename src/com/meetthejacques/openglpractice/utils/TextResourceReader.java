package com.meetthejacques.openglpractice.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class TextResourceReader {

	public static final String TAG = "TEXT_RESOURCE_READER";
	
	public static String readRawTextFileFromResource(Context context, int resourceId){
		StringBuilder body = new StringBuilder();
		
		try{
			InputStream is = context.getResources().openRawResource(resourceId);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String nextLine;
			
			while((nextLine = br.readLine()) != null){
				body.append(nextLine);
				body.append("\n");
			}
		} catch(IOException e){
			if(LoggerConfig.ON){
				Log.e(TAG, "Could not open Resource " + resourceId);
			}
		} catch(Resources.NotFoundException nfe){
			if(LoggerConfig.ON){
				Log.e(TAG, "Resource not found " + resourceId);
			}
		}
		
		return body.toString();
	}
}
