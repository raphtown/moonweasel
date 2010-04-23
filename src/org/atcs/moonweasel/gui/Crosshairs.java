package org.atcs.moonweasel.gui;

import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;

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

	public void draw() 
	{
		GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
				GL11.glBegin(GL11.GL_LINES);
				GL11.glColor3f(0, 1, 0);
				GL11.glVertex2i(x1,y3);
				GL11.glVertex2i(x2,y4);
				GL11.glVertex2i(x3, y4);
				GL11.glVertex2i(x4, y3);
				GL11.glVertex2i(x4, y2);
				GL11.glVertex2i(x3, y1);
				GL11.glVertex2i(x2, y1);
				GL11.glVertex2i(x1, y2);
				GL11.glVertex2i((x1 + x2)/10, (y3 + y4)/10);
				GL11.glVertex2i((x3 + x4)/10, (y1 + y2)/10);
				GL11.glVertex2i((x1 + x2)/10, (y1 + y2)/10);
				GL11.glVertex2i((x3 + x4)/10, (y3 + y4)/10);
			GL11.glEnd();
			GL11.glPopAttrib();
		GL11.glPopMatrix();

	}
}
