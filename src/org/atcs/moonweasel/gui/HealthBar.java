package org.atcs.moonweasel.gui;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;

public class HealthBar extends UIElement
{
	private int xmin = 5;
	private int xmax = 120;
	private int ymin = 0;
	private int ymax = 20;
		
	private Player p;
	
	public HealthBar(Vector v, Player pl) 
	{
		super(v);
		p = pl;
	}

	@Override
	public void draw() 
	{
		Ship ship = p.getShip();
		float healthPercent = ((float)ship.getHealth()) / ship.getOriginalHealth();
		
		GL11.glPushMatrix();
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor3f(1, 0, 0);
				GL11.glVertex3f(xmin, ymin, 0);
				GL11.glVertex3f(xmax, ymin, 0);
				GL11.glColor3f(0,0,0);
				GL11.glVertex3f(xmax, ymax, 0);
				GL11.glVertex3f(xmin, ymax, 0);
			GL11.glEnd();
			
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			GL11.glColor3f(0, 1, 0);
				GL11.glVertex3f(xmin, ymin, 1);
				GL11.glVertex3f((xmax*healthPercent), ymin, 1);
			GL11.glColor4f(1, 1, 1, (float) 0.1);
				GL11.glVertex3f((xmax*healthPercent), ymax, 1);
				GL11.glVertex3f(xmin, ymax, 1);
			GL11.glEnd();
			
			GL11.glPopAttrib();
			
		GL11.glPopMatrix();
	}
}
