package org.atcs.moonweasel.gui;

import java.io.FileInputStream;

import javax.media.opengl.GL2;

public class ObjLoader extends Loader {
	private static final String EXTENSION = "obj";
	
	protected String getExtension() {
		return EXTENSION;
	}
	
	protected int loadModel(FileInputStream stream, GL2 gl) {
		return -1;
	}
}
