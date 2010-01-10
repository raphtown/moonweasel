package org.atcs.moonweasel.gui;

import java.io.FileInputStream;

import javax.media.opengl.GL2;

public class ObjLoader extends Loader {
	private static final String EXTENSION = "obj";
	
	protected String getExtension() {
		return EXTENSION;
	}
	
	@Override
	protected boolean loadModel(FileInputStream stream, GL2 gl) {
		return true;
	}
}
