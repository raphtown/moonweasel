package org.atcs.moonweasel.entities;

import javax.media.opengl.GL2;

import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.gui.WeaselView;
import org.atcs.moonweasel.util.Vector;

import com.sun.opengl.util.gl2.GLUT;

public class Laser extends ParticleEntity
{
	private static final float VELOCITY = 100.0f;
	
	private Ship source;
	
	public Laser() {
	}

	@Override
	public void destroy()
	{
	}
	
	@Override
	public void draw(GL2 gl)
	{
		GLUT glut = WeaselView.glut;
		gl.glPushAttrib(GL2.GL_CURRENT_BIT);
		gl.glScalef(5, 5, 5);
		gl.glColor3f(0.0f, 0.1f, 0.9f);
		glut.glutSolidCylinder(0.001, 1.0, 30, 30);
		gl.glPopAttrib();
	}
	
	public void setSource(Ship ship) {
		this.source = ship;
	}
	
	@Override
	public void spawn()
	{
		assert source != null;
		
		this.setPosition(source.getState().position);
		this.setOrientation(source.getState().orientation);
		
		scheduleThink(50);
	}
	
	@Override
	public void think() {
		Vector speed = new Vector(0.0f, 0.0f, -VELOCITY * 0.02f);
		this.setPosition(getPosition().add(getOrientation().rotate(speed)));
		
		scheduleThink(50);
	}
}
