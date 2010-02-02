package org.atcs.moonweasel.gui;

import javax.media.nativewindow.NativeWindowFactory;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;

import com.sun.javafx.newt.Display;
import com.sun.javafx.newt.KeyEvent;
import com.sun.javafx.newt.KeyListener;
import com.sun.javafx.newt.NewtFactory;
import com.sun.javafx.newt.Screen;
import com.sun.javafx.newt.Window;
import com.sun.javafx.newt.WindowEvent;
import com.sun.javafx.newt.WindowListener;
import com.sun.javafx.newt.opengl.GLWindow;

public abstract class View implements GLEventListener, WindowListener, KeyListener {
	private GLWindow window;
	
	private volatile boolean quit;
	protected volatile float alpha;
	
	public View(int width, int height, boolean fullscreen) {
		this.quit = false;
		
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(profile);
		
        Display display = NewtFactory.createDisplay(NativeWindowFactory.TYPE_AWT, null);
        Screen screen  = NewtFactory.createScreen(NativeWindowFactory.TYPE_AWT, display, 0);
        Window nWindow = NewtFactory.createWindow(NativeWindowFactory.TYPE_AWT, screen, caps);
        window = GLWindow.create(nWindow);

        window.setTitle("Moonweasel");
		window.setSize(width, height);
		window.setFullscreen(fullscreen);
		
		window.addGLEventListener(this);
		window.addWindowListener(this);
		
		window.setVisible(true);
	}
	
	public void destroy() {
		window.destroy();
	}
	
	public Window getWindow() {
		return window;
	}
	
	public final void render(float alpha) {
		this.alpha = alpha;
		window.display();
	}
	
	protected abstract void display(GL2 gl, float alpha);
	
	public final void display(GLAutoDrawable drawable) {
		display(drawable.getGL().getGL2(), alpha);
	}

	public boolean shouldQuit() {
		return quit;
	}

	@Override
	public void windowDestroyNotify(WindowEvent arg0) {
		this.quit = true;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) { }

	@Override
	public void windowLostFocus(WindowEvent arg0) {	}

	@Override
	public void windowMoved(WindowEvent arg0) {	}

	@Override
	public void windowResized(WindowEvent arg0) { }
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.isMetaDown() && arg0.getKeyCode() == KeyEvent.VK_Q)
			quit = true;
	}

	@Override
	public void keyReleased(KeyEvent arg0) { }

	@Override
	public void keyTyped(KeyEvent arg0) { }
}
