package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.TimedDerivative;
import org.atcs.moonweasel.util.Vector;

public class Laser extends ModelEntity
{
	private static final float VELOCITY = 100.0f;
	
	private Ship source;
	
	public Laser() {
		super(1, Matrix.IDENTITY);
	}
	
	@Override
	public void collidedWith(ModelEntity e) {
		if (!(e instanceof Ship)) {
			return;
		}
		
		Ship target = (Ship)e;
		target.damage(source.getData().attack);
		if (target.isDestroyed()) {
			source.killed(target);
		}
	}

	@Override
	public void destroy()
	{
	}
	
	public void setSource(Ship ship) {
		this.source = ship;
	}
	
	@Override
	public void spawn()
	{
		assert source != null;
		
		this.getState().orientation = source.getState().orientation;
		Vector speed = new Vector(0.0f, 0.0f, -VELOCITY);
		this.getState().addDerivative(new TimedDerivative(getTime(), 
				this.getState().orientation.rotate(speed), Vector.ZERO));
	}
}
