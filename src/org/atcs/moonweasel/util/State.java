package org.atcs.moonweasel.util;

public class State 
{
	// primary
	public Vector position;
	public Vector momentum;
	public Quaternion orientation;
	public Vector angularMomentum;


	// secondary
	public Vector velocity;
	public Vector angularVelocity;
	public Quaternion spin;
	public Matrix bodyToWorld;
	public Matrix worldToBody;
	public Vector[] verticesOfBoundingRegion;
	

	// constant
	public float mass;
	public float inverseMass;
	public Matrix inertiaTensor;
	public Matrix inverseInertiaTensor;
	
	public State() {
		
	}

	public State(float mass, Matrix inertia) 
	{
		this.position = new Vector();
		this.momentum = new Vector();
		this.velocity = new Vector();

		this.orientation = new Quaternion();
		this.angularMomentum = new Vector();
		this.spin = new Quaternion();
		this.angularVelocity = new Vector();

		this.mass = mass;
		this.inverseMass = 1 / mass;
		this.inertiaTensor = inertia;
		this.inverseInertiaTensor = inertia.inverse();
	}
	

	// interpolation used for animating inbetween states
	public State interpolate(State a, State b, float alpha) 
	{
		State interpolatedState = b;
		interpolatedState.position = a.position.scale(1 - alpha).add(b.position.scale(alpha));
		interpolatedState.momentum = a.momentum.scale(1 - alpha).add(b.momentum.scale(alpha));
		interpolatedState.orientation = Quaternion.slerp(a.orientation,b.orientation, alpha);
		interpolatedState.angularMomentum = a.angularMomentum.scale(1 - alpha).add(b.angularMomentum.scale(alpha));
		interpolatedState.recalculate();
		return interpolatedState;
	}

	public void recalculate() {
		velocity = momentum.scale(inverseMass);
		angularVelocity = inverseInertiaTensor.transform(angularMomentum);
		orientation.normalize();

		Quaternion tempUpdate = new Quaternion(0, angularVelocity.x, angularVelocity.y, angularVelocity.z);
		spin = tempUpdate.scale(0.5f).multiply(orientation);

		// dealing with local vs global coordinates now
		Matrix translation = new Matrix();
		translation.setAsTranslation(position);
		bodyToWorld = translation.mtm(orientation.toMatrix());
        worldToBody = bodyToWorld.inverse();
	}
}
