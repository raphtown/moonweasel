package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

public class Laser extends ParticleEntity
{
	private static final float VELOCITY = 100.0f;
	private static final int LIFESPAN = 50;
	
	private int age;
	private Ship source;
	
	public Laser() {
	}
	
	@Override
	public void draw()
	{
		GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
		GL11.glScalef(5, 5, 5);
		GL11.glColor3f(0.0f, 0.1f, 0.9f);
		Cylinder cylinder = new Cylinder();
		cylinder.draw(0.001f, 0.001f, 1.0f, 30, 30);
		GL11.glPopAttrib();
	}
	
	public void setSource(Ship ship) {
		this.source = ship;
	}
	
	@Override
	public void spawn()
	{
		assert source != null;
		
		age = 0;
		this.setPosition(source.getState().position);
		this.setOrientation(source.getState().orientation);
		
		scheduleThink(50);
	}
	
	@Override
	public void think() {
		if (age == LIFESPAN) {
			destroy();
			return;
		}
		age++;
		
		Vector speed = new Vector(0.0f, 0.0f, -VELOCITY * 0.02f);
		this.setPosition(getPosition().add(getOrientation().rotate(speed)));
		
		scheduleThink(50);
	}
}
