package org.atcs.moonweasel.gui;

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

public class View implements GLEventListener, WindowListener {
	private float y;
	private float direction;
	private boolean quit;
	
	public View(int width, int height) {
		this.y = 2;
		this.direction = 0.01f;
		this.quit = false;
		
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities caps = new GLCapabilities(profile);
		
		NewtFactory.setUseEDT(true);
		Window fwindow = null;
		Display display = NewtFactory.createDisplay(null);
		Screen screen = NewtFactory.createScreen(display, 0);
		fwindow = NewtFactory.createWindow(screen, caps);
		GLWindow window = GLWindow.create(fwindow);
		window.enablePerfLog(true);
		window.setSize(width, height);
		
		window.addGLEventListener(this);
		
		window.setVisible(true);
		
		do {
			window.display();
		} while (!quit);
		
		window.destroy();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.2f + y / 3, 0.2f, 0.2f, 0);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();
		
//		System.out.println(y);
		if (Math.abs(y) > 3) {
			direction *= -1;
		}
		y += direction;
		gl.glTranslatef(0, y, 0);
		
		gl.glBegin(GL.GL_TRIANGLES);
			gl.glVertex3f(2, 0, -3);
			gl.glVertex3f(0, 2, -3);
			gl.glVertex3f(-2, 0, -3);
		gl.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.setSwapInterval(1);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glOrtho(-5, 5, -5, 5, 0.01, 10);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		
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
