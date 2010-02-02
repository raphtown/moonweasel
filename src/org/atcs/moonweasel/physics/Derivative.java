package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.Vector;

public class Derivative
{
	public Vector velocity;
	public Vector force;
	public Quaternion spin;
	public Vector torque;
	
	public Derivative() {
		this(Vector.ZERO, Vector.ZERO, Quaternion.ZERO, Vector.ZERO);
	}
	
	private Derivative(Vector velocity, Vector force, Quaternion spin, Vector torque)
	{
		this.velocity = velocity;
		this.force = force;
		this.spin = spin; //angular velocity
		this.torque = torque;
	}
}
