package org.atcs.moonweasel.entities;

import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.Vector;

public class State {
	public Vector position;
	public Vector momentum;	
	
	public Quaternion orientation;
	public Vector angularMomentum;
	
	public float mass;
}
