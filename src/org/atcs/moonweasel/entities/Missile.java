package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.physics.BoundingBox;
import org.atcs.moonweasel.physics.BoundingShape;
import org.atcs.moonweasel.util.*;


public class Missile extends ModelEntity 
{
	ModelEntity target;
	private Missile() 
	{
		super(new BoundingBox(1,2,3), 120, Matrix.IDENTITY);
		scheduleThink(15);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void spawn() {
		// TODO Auto-generated method stub
		
	}
	public void setTarget(ModelEntity x)
	{
		target = x;
		
	}
	public void think()
	{
		float targetX = target.getPosition().x;
		float targetY = target.getPosition().y;
		float targetZ = target.getPosition().z;
		
		float mX = this.getPosition().x;
		float mY = this.getPosition().y;
		float mZ = this.getPosition().z;
		
		setVelocity(new Vector(targetX - mX, targetY - mY, targetZ - mZ));
		
		scheduleThink(15);
	}

}
