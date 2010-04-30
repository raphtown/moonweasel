package org.atcs.moonweasel.gui;

import java.io.IOException;

import org.atcs.moonweasel.util.Vector;

public abstract class Loader {
	private static Loader loader = new ObjLoader();
	
	public static Vector[] getGeometry(String name) {
		String path = String.format("data/models/%s/", name);
		
		try {
			return loader.loadGeometry(path, name);
		} catch (IOException e) {
			throw new RuntimeException("Unable to import " + name, e);
		}
	}
	
	public static boolean load(String name) {
		String path = String.format("data/models/%s/", name);
		
		try {
			return loader.loadModel(path, name);			
		} catch (IOException e) {
			
			return false;
		}
	}
	
	protected abstract String getExtension();
	protected abstract boolean loadModel(String path, String name) throws IOException;
	protected abstract Vector[] loadGeometry(String path, String name) throws IOException;
}
