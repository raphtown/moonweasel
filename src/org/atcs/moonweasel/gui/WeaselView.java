package org.atcs.moonweasel.gui;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

public class WeaselView extends View {
	private float value;
	
	public WeaselView(int width, int height, boolean fullscreen) {
		super(width, height, fullscreen);

		this.value = 0;
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
	public void display(GL2 gl, float alpha) {
		gl.glClearColor(0.2f + 2 * (float)Math.sin(value) / 3, 0.2f, 0.2f, 0);
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();
	
		gl.glTranslatef(0.f, 0.f, -3.f);
		if (Loader.load("graphics", gl)) {
			System.err.println("You loaded, everything is fine.");			
		} else {
			System.err.println("OWWWW");
		}
		
//		value += 0.01;
//		gl.glTranslatef(0, 2 * (float)Math.sin(value), 0);
		
//		gl.glBegin(GL.GL_TRIANGLES);
//			gl.glVertex3f(2, 0, -3);
//			gl.glVertex3f(0, 2, -3);
//			gl.glVertex3f(-2, 0, -3);
//		gl.glEnd();
	}
}
