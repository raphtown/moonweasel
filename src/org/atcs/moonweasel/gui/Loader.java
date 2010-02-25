package org.atcs.moonweasel.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.media.opengl.GL2;

public abstract class Loader {
	private static Loader loader;
	
	public static boolean load(String name, GL2 gl) {
		if (loader == null) {
			loader = new ObjLoader();
		}
		
		String path = String.format("data/models/%s/", name);
		
		try {
			return loader.loadModel(path, "snowflake", gl);			
		} catch (IOException e) {
			throw new RuntimeException("Unable to import " + name, e);
		}
	}
	
	protected abstract String getExtension();
	protected abstract boolean loadModel(String path, String name, GL2 gl) throws IOException;
}
