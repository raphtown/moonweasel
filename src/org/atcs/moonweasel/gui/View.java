package org.atcs.moonweasel.gui;

import javax.media.nativewindow.NativeWindowFactory;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import com.sun.javafx.newt.Display;
import com.sun.javafx.newt.NewtFactory;
import com.sun.javafx.newt.Screen;
import com.sun.javafx.newt.Window;
import com.sun.javafx.newt.WindowEvent;
import com.sun.javafx.newt.WindowListener;
import com.sun.javafx.newt.opengl.GLWindow;

public abstract class View implements GLEventListener, WindowListener {
	private GLWindow window;
	
	private volatile boolean quit;
	
	public View(int width, int height) {
		this.quit = false;
		
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(profile);
		
		NewtFactory.setUseEDT(true);
        Display display = NewtFactory.createDisplay(NativeWindowFactory.TYPE_AWT, null); // local display
        Screen screen  = NewtFactory.createScreen(NativeWindowFactory.TYPE_AWT, display, 0); // screen 0
        Window nWindow = NewtFactory.createWindow(NativeWindowFactory.TYPE_AWT, screen, caps);
        window = GLWindow.create(nWindow);
        
        window.setTitle("Moonweasel");
		window.setSize(width, height);
		
		window.addGLEventListener(this);
		window.addWindowListener(this);
		
		window.setVisible(true);
	}
	
	public void destroy() {
		window.destroy();
	}	
	
	public void render() {
		window.display();		
	}
	
	public boolean shouldQuit() {
		return quit;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void windowDestroyNotify(WindowEvent arg0) {
		this.quit = true;
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowMoved(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowResized(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
