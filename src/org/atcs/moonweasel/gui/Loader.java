package org.atcs.moonweasel.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.media.opengl.GL2;

public abstract class Loader {
	private static Loader loader;
	
	public static boolean load(String name, GL2 gl) {
		if (loader == null) {
			loader = new ObjLoader();
		}
		
		String filename = String.format("data/models/%s.%s", name, loader.getExtension());
		FileInputStream stream;
		
		try {
			stream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			System.out.println("err");
			return false;
		}
		
		return loader.loadModel(stream, gl);
	}
	
	protected abstract String getExtension();
	protected abstract boolean loadModel(FileInputStream stream, GL2 gl);
}
