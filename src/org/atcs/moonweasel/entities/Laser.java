package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;
import org.lwjgl.opengl.GL11;
import org.atcs.moonweasel.util.Quaternion;

public class Laser extends ParticleEntity
{
	private static final int LIFESPAN = 2;
	
	private int age;
	private Ship source;
	
	private boolean autoTargeting = false;
	private ModelEntity target;
	
	public Laser() {
	}
	
	public void setTarget(ModelEntity me){
		autoTargeting = true;
		target = me;
	}
	
	@Override
	public void draw()
	{
		State me = State.interpolate(source.getLastRenderState(),
				source.getState(), 0.1f);
		
		if(autoTargeting == false)
		{
			GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
//			GL11.glScalef(5, 5, 5);
			GL11.glColor3f(0.0f, 0.1f, 0.9f);
//			Cylinder cylinder = new Cylinder();
//			cylinder.draw(0.005f, 0.005f, 1.0f, 3000, 300);
			Vector laserEnd = source.getState().bodyToWorld.transform(new Vector(0,0,-1000));
			
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(me.position.x, me.position.y, me.position.z);
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
			GL11.glVertex3f(me.position.x, me.position.y, me.position.z);
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
		if (autoTargeting == true) System.out.println("This laser is auto-targeting");
		if (autoTargeting == false) System.out.println("This laser is not auto-targeting");
		assert source != null;
		
		age = 0;
		//if(autoTargeting == false)
		{
//		
			this.setOrientation(Quaternion.ZERO);
			this.setPosition(source.getPosition());
//			this.setPosition(getPosition().add(getOrientation().rotate(offset)));
		}
//		else
//		{
//			Vector centroidToCentroid = target.getPosition().subtract(this.getPosition());
//			float alpha = (centroidToCentroid.angleBetween(new Vector(1,0,0)));
//			
//			Vector partialQuaternion = new Vector(1,0,0).scale((float)Math.sin(alpha/2));
//			Quaternion orientation = new Quaternion((float)Math.cos(alpha/2), partialQuaternion.x, partialQuaternion.y, partialQuaternion.z);
//			
//			this.setOrientation(orientation);
//			this.setPosition(source.getPosition());
//			this.setPosition(getPosition().add(getOrientation().rotate(offset)));
//		}
	
		
		
		scheduleThink(50);
	}
	
	
	@Override
	public void think() {
		if (age == LIFESPAN) {
			destroy();
			return;
		}
		age++;
//		
//		if (autoTargeting == false)
//		{
//			Vector speed = new Vector(0.0f, 0.0f, -VELOCITY * 0.02f);
//			this.setPosition(getPosition().add(getOrientation().rotate(speed)));
//		}
//		else
//		{
//			Vector speed = source.getState().worldToBody.transform(target.getPosition()).subtract(source.getPosition()).normalize().scale(150*0.02f);
//			this.setPosition(getPosition().add(getOrientation().rotate(speed)));
//		}
		
		scheduleThink(30);
	}
}
