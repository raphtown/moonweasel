package org.atcs.moonweasel.networking;

import java.io.Serializable;

import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.Vector;

public class IState implements Serializable
{
	public Vector position;
	public Vector momentum;
	public Quaternion orientation;
	public Vector angularMomentum;
	
	public int ownerID;
	
	public IState(Vector position, Vector momentum, Quaternion orientation, Vector angularMomentum, int ownerID)
	{
		this.position = position;
		this.momentum = momentum;
		this.orientation = orientation;
		this.angularMomentum = angularMomentum;
		this.ownerID = ownerID;
	}
}
