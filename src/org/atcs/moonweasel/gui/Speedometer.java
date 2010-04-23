package org.atcs.moonweasel.gui;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.entities.players.Player;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Vector;

import com.sun.opengl.util.gl2.GLUT;



public class Speedometer extends UIElement
{
	private Player p;
	
	public Speedometer(Vector v, Player pl) {
		super(v);
		p = pl;
		// TODO Auto-generated constructor stub
	}

	public void draw(GL2 gl) 
	{
		Ship ship = p.getShip();
		gl.glColor4f(0.0f, 0.2f, 0.8f, 0.75f);
			gl.glPushAttrib(GL2.GL_CURRENT_BIT);
				gl.glRasterPos2i(650, 20);
				gl.glColor3f(0.0f, 0.0f, 1.0f);
				WeaselView.glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "SPEED:");
				WeaselView.glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, Float.toString(ship.getState().velocity.length()*100000));
				
			gl.glPopAttrib();
	}
}
