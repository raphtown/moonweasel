package org.atcs.moonweasel.entities.particles;

import org.atcs.moonweasel.entities.ParticleEntity;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Exhaust extends ParticleEntity {
	
	Ship s;
	
	public Exhaust() {
	}
	
	public void draw() {
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		Vector v = s.getPosition().add(new Vector(0f,0f,6f));
		GL11.glTranslatef(v.x,v.y,v.z);
		
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		float red = s.getState().velocity.length();
		GL11.glColor3f(red, 0.3f, 0.3f);
		Cylinder cylinder = new Cylinder();
		cylinder.draw(0.001f, .05f, .25f, 30, 30);
		GL11.glPopAttrib();
		GL11.glPopMatrix();
	}
	
	public void setShip(Ship sh)
	{
		s = sh;
	}
	
	public void spawn() {
		this.setOrientation(Quaternion.ZERO);
	}
}
