package org.atcs.moonweasel.gui;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.util.Vector;

public class HealthBar extends UIElement
{

	public HealthBar(Vector p) 
	{
		super(p);
	}

	@Override
	public void draw(GL2 gl) 
	{
		gl.glPushMatrix();
			gl.glColor3f(10, 0, 0);
			gl.glBegin(gl.GL_TRIANGLE_FAN);
				gl.glVertex3f(-5, -5, 0);
				gl.glVertex3f(-3, -5, 0);
				gl.glVertex3f(-3, -4, 0);
				gl.glVertex3f(-5, -4, 0);
			gl.glEnd();
		gl.glPopMatrix();
			
	}

}
