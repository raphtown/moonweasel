package org.atcs.moonweasel.gui;

import org.atcs.moonweasel.util.Vector;

import javax.media.opengl.GL2;

public class Crosshairs extends UIElement
{
	private int x1 = -40;
	private int x2 = -10;
	private int x3 = 10;
	private int x4 = 40;
	private int y1 = -40;
	private int y2 = -10;
	private int y3 = 10;
	private int y4 = 40;
	
	public Crosshairs(Vector v)
	{
		super(v);
	}

	public void draw(GL2 gl) 
	{
		gl.glPushMatrix();
			gl.glPushAttrib(GL2.GL_CURRENT_BIT);
				gl.glBegin(GL2.GL_LINES);
				gl.glColor3f(0, 1, 0);
				gl.glVertex2i(x1,y3);
				gl.glVertex2i(x2,y4);
				gl.glVertex2i(x3, y4);
				gl.glVertex2i(x4, y3);
				gl.glVertex2i(x4, y2);
				gl.glVertex2i(x3, y1);
				gl.glVertex2i(x2, y1);
				gl.glVertex2i(x1, y2);
				gl.glVertex2i((x1 + x2)/10, (y3 + y4)/10);
				gl.glVertex2i((x3 + x4)/10, (y1 + y2)/10);
				gl.glVertex2i((x1 + x2)/10, (y1 + y2)/10);
				gl.glVertex2i((x3 + x4)/10, (y3 + y4)/10);
			gl.glEnd();
			gl.glPopAttrib();
		gl.glPopMatrix();

	}
}
