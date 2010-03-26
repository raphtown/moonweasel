package org.atcs.moonweasel.util;


import java.util.ArrayList;
import java.util.PriorityQueue;

import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.ranges.TimeRange;

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
	public ArrayList<Vector> verticesOfBoundingRegion;
	public float dangerZoneRadius;

	// constant
	public final float mass;
	public final float inverseMass;
	public final Matrix inertiaTensor;
	public final Matrix inverseInertiaTensor;

	public ArrayList<Vector> XYConvexHullBody;
	public ArrayList<Vector> YZConvexHullBody;
	public ArrayList<Vector> ZXConvexHullBody;

	public ArrayList<Vector> XYConvexHullWorld;
	public ArrayList<Vector> YZConvexHullWorld;
	public ArrayList<Vector> ZXConvexHullWorld;



	private PriorityQueue<TimedDerivative> derivatives;

	public State(float mass, Matrix inertia) 
	{
		this(Vector.ZERO, Vector.ZERO, Quaternion.ZERO, Vector.ZERO, mass, 1 / mass, inertia, inertia.inverse());


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

		this.derivatives = new PriorityQueue<TimedDerivative>();
	}

	private State(State other) {
		this.position = other.position;
		this.momentum = other.momentum;
		this.orientation = other.orientation;
		this.angularMomentum = other.angularMomentum;

		this.velocity = other.velocity;
		this.angularVelocity = other.angularVelocity;
		this.spin = other.spin;
		this.bodyToWorld = other.bodyToWorld;
		this.worldToBody = other.worldToBody;
		this.verticesOfBoundingRegion = other.verticesOfBoundingRegion;
		this.dangerZoneRadius = other.dangerZoneRadius;

		this.mass = other.mass;
		this.inverseMass = other.inverseMass;
		this.inertiaTensor = other.inertiaTensor;
		this.inverseInertiaTensor = other.inverseInertiaTensor;

		this.derivatives = other.derivatives;
	}

	public void addDerivative(TimedDerivative derivative) {
		this.derivatives.add(derivative);
	}

	public void clearDerivativesBefore(long t) {
		while (!derivatives.isEmpty() &&
				derivatives.peek().getTime() < t) {
			derivatives.remove();
		}
	}

	public State clone() {
		return new State(this);
	}

	public Range<TimedDerivative> getDerivativesBefore(long t) {
		return new TimeRange<TimedDerivative>(0, t, derivatives.iterator());
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

		if(XYConvexHullWorld != null)
		{
			XYConvexHullWorld.clear();
			YZConvexHullWorld.clear();
			ZXConvexHullWorld.clear();

			for(Vector point : XYConvexHullBody)
			{
				XYConvexHullWorld.add(bodyToWorld.transform(point).projectIntoXY());
			}
			for(Vector point : YZConvexHullBody)
			{
				YZConvexHullWorld.add(bodyToWorld.transform(point).projectIntoYZ());
			}
			for(Vector point : ZXConvexHullBody)
			{
				ZXConvexHullWorld.add(bodyToWorld.transform(point).projectIntoZX());
			}
		}

	}

	public void setDangerZone(float dt)
	{
		dangerZoneRadius = 5 + velocity.scale(dt).length();
	}


	public void setInitialConvexHull()
	{
		XYConvexHullBody = (new ConvexHull(verticesOfBoundingRegion, "xy").toPolygon());
		YZConvexHullBody = (new ConvexHull(verticesOfBoundingRegion, "yz").toPolygon());
		ZXConvexHullBody = (new ConvexHull(verticesOfBoundingRegion, "zx").toPolygon());

		XYConvexHullWorld = new ArrayList<Vector>();
		YZConvexHullWorld = new ArrayList<Vector>();
		ZXConvexHullWorld = new ArrayList<Vector>();
	}
}
