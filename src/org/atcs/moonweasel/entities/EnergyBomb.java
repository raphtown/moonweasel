package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.TimedDerivative;
import org.atcs.moonweasel.util.Vector;

public class EnergyBomb extends ModelEntity
{
	private static final int THINK_TIME = 20;
	
	private ModelEntity target;
	private EnergyBomb() 
	{
		super(100, Matrix.IDENTITY);
	}

	public void spawn()
	{
		assert target != null;
		scheduleThink(15);
	}
	
	public void setTarget(ModelEntity x)
	{
		target = x;
	}
	
	public void think()
	{
		State state = getState();
		
		float forceConstant = .000014f;
		float torqueConstant = .000182f;
		
		if(getPosition().equals(target.getPosition())) //if they are in the same position
		{
			scheduleThink(THINK_TIME);
			return;
		}
		
		Vector currentPosition = getPosition();
		Vector targetPosition = target.getPosition();
		
		Vector force = currentPosition.subtract(targetPosition).scale(-1 * forceConstant);

		state.addDerivative(new TimedDerivative(getTime(), force, Vector.ZERO));

		state.orientation = new Quaternion(0, (float) (Math.random()), (float)(Math.random()), (float)(Math.random())).normalize();

		scheduleThink(THINK_TIME);
	}
}
