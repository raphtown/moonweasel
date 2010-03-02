package org.atcs.moonweasel.gui;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.util.Vector;

public class HealthBar extends UIElement
{

	private int xmin = 5;
	private int xmax = 120;
	private int ymin = 0;
	private int ymax = 20;
	
	private Player p;
	
	private float healthPercent;
	
	public HealthBar(Vector v, Player pl) 
	{
		super(v);
		p = pl;
		healthPercent = (float)(p.getShip().getHealth()) / p.getShip().MAX_HEALTH;
	}
	
	public void setHealthPercent(float p)
	{
		healthPercent = p;
	}

	@Override
	public void draw(GL2 gl) 
	{		
		gl.glPushMatrix();
			gl.glPushAttrib(GL2.GL_CURRENT_BIT);
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glColor3f(1, 0, 0);
				gl.glVertex3f(xmin, ymin, 0);
				gl.glVertex3f(xmax, ymin, 0);
				gl.glColor3f(0,0,0);
				gl.glVertex3f(xmax, ymax, 0);
				gl.glVertex3f(xmin, ymax, 0);
			gl.glEnd();
			
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			gl.glColor3f(0, 1, 0);
				gl.glVertex3f(xmin, ymin, 1);
				gl.glVertex3f((xmax*healthPercent), ymin, 1);
			gl.glColor4f(1, 1, 1, (float) 0.1);
				gl.glVertex3f((xmax*healthPercent), ymax, 1);
				gl.glVertex3f(xmin, ymax, 1);
			gl.glEnd();
			
			gl.glPopAttrib();
			
		gl.glPopMatrix();
			
	}

}
