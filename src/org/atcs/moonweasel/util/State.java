package org.atcs.moonweasel.util;


public class State {
	public Vector position;
	public Vector momentum;	
	public Vector velocity;
	public Vector forceApplied;
	
	public Quaternion orientation;
	public Vector angularMomentum;
	
	public float mass;
	
	public State() {
	}

	public State(float mass) {
		this.position = new Vector();
		this.momentum = new Vector();
		this.velocity = new Vector();
		this.forceApplied = new Vector();
		
		this.orientation = new Quaternion();
		this.angularMomentum = new Vector();
		
		this.mass = mass;
	}
}
