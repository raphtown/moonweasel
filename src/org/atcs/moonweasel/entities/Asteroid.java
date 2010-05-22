package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.TimedDerivative;
import org.atcs.moonweasel.util.Vector;

public class Asteroid extends ModelEntity
{
	private static final long serialVersionUID = 8565600289625737618L;

	public Asteroid()
	{
		super(10000, Matrix.IDENTITY.scale(100));
	}

	@Override
	public void spawn() {
		this.scheduleThink(100);
	}
	
	public void think() {
		Vector dampTorque = new Vector( 5f * state.angularVelocity.x,
				5f * state.angularVelocity.y,
				5f * state.angularVelocity.z).scale(-1);
				getState().addDerivative(new TimedDerivative(getTime(), Vector.ZERO, dampTorque));

//		this.getState().angularMomentum = Vector.ZERO;
//		this.getState().angularVelocity = Vector.ZERO;
		this.scheduleThink(100);
	}
}
