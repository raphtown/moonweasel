package org.atcs.moonweasel.util;


public class Derivative
{
	public Vector velocity;
	public Vector force;
	public Quaternion spin;
	public Vector torque;
	
	public Derivative(){};
	
	public Derivative(Vector velocity, Vector force, Quaternion spin, Vector torque)
	{
		this.velocity = velocity;
		this.force = force;
		this.spin = spin; //angular velocity
		this.torque = torque;
	}

}
