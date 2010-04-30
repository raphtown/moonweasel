package org.atcs.moonweasel.gui;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Font;



public class Speedometer extends UIElement
{
	private Player p;
	
	public Speedometer(Vector v, Player pl) {
		super(v);
		p = pl;
		// TODO Auto-generated constructor stub
	}

	
	public void draw() 
	{
		Ship ship = p.getShip();
		GL11.glColor4f(0.0f, 0.2f, 0.8f, 0.75f);
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
				GL11.glRasterPos2i(650, 20);
				GL11.glColor3f(0.0f, 0.0f, 1.0f);	
				
			GL11.glPopAttrib();
	}
}
