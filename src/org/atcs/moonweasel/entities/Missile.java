package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.physics.BoundingBox;
import org.atcs.moonweasel.physics.BoundingShape;
import org.atcs.moonweasel.util.Matrix;

public class Missile extends ModelEntity 
{
	ModelEntity target;
	private Missile() 
	{
		super(new BoundingBox(1,2,3), 120, Matrix.IDENTITY);
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

}
