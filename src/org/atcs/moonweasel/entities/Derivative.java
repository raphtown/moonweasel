package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Vector;

public class Derivative
{
	public Vector dx; //this is velocity vector
	public Vector dv; //this is acceleration vector
	
	public Derivative(Vector dx, Vector dv)
	{
		this.dx = dx;
		this.dv = dv;
	}
	//public Vector dtheta; //this is angular velocity
	//public Vector domega; //this is angular acceleration
}
