package org.atcs.moonweasel.gui;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class View {
	protected final DisplayMode mode;
	
	public View(DisplayMode mode, boolean fullscreen) {
		this.mode = mode;
		
		try {
			Display.setDisplayMode(mode);
			Display.setFullscreen(fullscreen);
			Display.setTitle("Moonweasel");
			Display.create();
		} catch (LWJGLException e) {
			throw new RuntimeException("Unable to create display.", e);
		}
	}
	
	public void destroy() {
		Display.destroy();
	}
	
	public abstract void render(float alpha);
	
	public boolean shouldQuit() {
		return Display.isCloseRequested();
	}
}
