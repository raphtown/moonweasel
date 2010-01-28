package org.atcs.moonweasel.util;

public class State 
{
	// interpolation used for animating in between states
	public static State interpolate(State a, State b, float alpha) 
	{
		State interpolatedState = b;
		interpolatedState.position = a.position.scale(1 - alpha).add(b.position.scale(alpha));
		interpolatedState.momentum = a.momentum.scale(1 - alpha).add(b.momentum.scale(alpha));
		interpolatedState.orientation = Quaternion.slerp(a.orientation,b.orientation, alpha);
		interpolatedState.angularMomentum = a.angularMomentum.scale(1 - alpha).add(b.angularMomentum.scale(alpha));
		interpolatedState.recalculate();
		return interpolatedState;
	}
	
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
	public float dangerZoneRadius;
	
	// constant
	public float mass;
	public float inverseMass;
	public Matrix inertiaTensor;
	public Matrix inverseInertiaTensor;
	
	public State() {
		
	}

	public State(float mass, Matrix inertia) 
	{
		this.position = Vector.ZERO;
		this.momentum = Vector.ZERO;
		this.velocity = Vector.ZERO;

		this.orientation = new Quaternion();
		this.angularMomentum = Vector.ZERO;
		this.spin = new Quaternion();
		this.angularVelocity = Vector.ZERO;

		this.mass = mass;
		this.inverseMass = 1 / mass;
		this.inertiaTensor = inertia;
		this.inverseInertiaTensor = inertia.inverse();
		
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
	
	public void setDangerZone(float dt)
	{
		dangerZoneRadius = velocity.scale(dt).length();
	}
}
