package org.atcs.moonweasel.util;

public class State 
{
	// interpolation used for animating in between states
	public static State interpolate(State a, State b, float alpha) 
	{
		State interpolated = new State(
				a.position.scale(1 - alpha).add(b.position.scale(alpha)), 
				a.momentum.scale(1 - alpha).add(b.momentum.scale(alpha)), 
				Quaternion.slerp(a.orientation,b.orientation, alpha), 
				a.angularMomentum.scale(1 - alpha).add(b.angularMomentum.scale(alpha)), 
				a.mass, 
				a.inverseMass, 
				a.inertiaTensor, a.inverseInertiaTensor);
		interpolated.recalculate();
		return interpolated;
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
	public final float mass;
	public final float inverseMass;
	public final Matrix inertiaTensor;
	public final Matrix inverseInertiaTensor;
	
	public State(float mass, Matrix inertia) 
	{
		this(Vector.ZERO, Vector.ZERO, Quaternion.ZERO,
				Vector.ZERO, mass, 1 / mass, inertia, inertia.inverse());
	}
	
	public State(Vector position, Vector momentum, Quaternion orientation, 
			Vector angularMomentum, float mass, float inverseMass, Matrix inertiaTensor,
			Matrix inverseInertiaTensor) {
		this.position = position;
		this.momentum = momentum;
		this.orientation = orientation;
		this.angularMomentum = angularMomentum;

		this.mass = mass;
		this.inverseMass = inverseMass;
		this.inertiaTensor = inertiaTensor;
		this.inverseInertiaTensor = inverseInertiaTensor;
		recalculate();
	}
	
	public State clone() {
		State clone = new State(
				position,
				momentum,
				orientation,
				angularMomentum,
				mass, 
				inverseMass,
				inertiaTensor,
				inverseInertiaTensor);
		clone.recalculate();
		return clone;
	}
	
	public void recalculate() {
		velocity = momentum.scale(inverseMass);
		angularVelocity = inverseInertiaTensor.transform(angularMomentum);
		orientation = orientation.normalize();

		Quaternion tempUpdate = new Quaternion(0, angularVelocity.x, angularVelocity.y, angularVelocity.z);
		spin = tempUpdate.scale(0.5f).multiply(orientation);
		  
		// dealing with local vs global coordinates now
		bodyToWorld = new Matrix(position).multiply(orientation.toMatrix());
		worldToBody = bodyToWorld.inverse();
	}
	 
	public void setDangerZone(float dt)
	{
		dangerZoneRadius = velocity.scale(dt).length();
	}
}
