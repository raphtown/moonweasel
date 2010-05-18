package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;
import org.atcs.moonweasel.util.Quaternion;

public class Laser extends ParticleEntity
{
	private static final long serialVersionUID = -7650624412188994157L;

	private static final int LIFESPAN = 2;
	
	private int age;
	private Ship source;
	
	private boolean autoTargeting = false;
	private ModelEntity target;
	
	public Laser() {
		System.out.println("wow2");
	}
	
	public void setTarget(ModelEntity me){
		autoTargeting = true;
		target = me;
	}
	
	@Override
	public void draw()
	{
		if (!autoTargeting)
		{
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			GL11.glColor3f(0.0f, 0.3f, 0.9f);
			Vector laserEnd = source.getState().bodyToWorld.transform(new Vector(0,0,-10000));
			
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(source.getPosition().x, source.getPosition().y, source.getPosition().z);
			GL11.glVertex3f(laserEnd.x, laserEnd.y, laserEnd.z);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			
		}
		else
		{
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
			GL11.glColor3f(0.0f, 0.1f, 0.9f);
			
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(source.getPosition().x, source.getPosition().y, source.getPosition().z);
			GL11.glVertex3f(target.getState().position.x, target.getState().position.y, target.getState().position.z);
			GL11.glEnd();
			GL11.glPopMatrix();
			GL11.glPopAttrib();
			
		}
	}
	
	public void setSource(Ship ship) {
		this.source = ship;
	}
	
	@Override
	public void spawn()
	{
		assert source != null;
	
		age = 0;

		this.setOrientation(Quaternion.ZERO);
		this.setPosition(source.getPosition());
//		this.setPosition(getPosition().add(getOrientation().rotate(offset)));
		
		scheduleThink(50);
	}
	
	
	@Override
	public void think() {
		if (age == LIFESPAN) {
			destroy();
			return;
		}
		age++;
		
		scheduleThink(30);
	}
}
