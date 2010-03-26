package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.util.MutableVector;
import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.TimedDerivative;
import org.atcs.moonweasel.util.Vector;

public class NumericalIntegration
{
	public NumericalIntegration() {
	}

	public Derivative evaluate(State initial, long t)
	{
		Derivative output = new Derivative();
		output.velocity = initial.velocity;
		output.spin = initial.spin;
		forces(initial, t, output);
		return output;
	}

	//overloading the evaluate() method to account for the other derivatives in RK4
	public Derivative evaluate(State state, long t, int dt, Derivative d)
	{
		state.position = state.position.add(d.velocity.scale(dt));
		state.momentum = state.momentum.add(d.force.scale(dt))  ;
		state.orientation = state.orientation.add( d.spin.scale(dt));
		state.angularMomentum = state.angularMomentum.add(d.torque.scale(dt));
		state.recalculate();

		Derivative output = new Derivative();
		output.velocity = state.velocity;
		output.spin = state.spin;
		forces(state, t + dt, output);
		return output;
	}

	// Calculate force and torque for physics state at time t.
	// Due to the way that the RK4 integrator works we need to calculate
	// force implicitly from state rather than explictly applying forces
	// to the rigid body once per update. This is because the RK4 achieves
	// its accuracy by detecting curvature in derivative values over the 
	// timestep so we need our force values to supply the curvature.
	public void forces(State state, long t, Derivative output)
	{
		//damping(state, output);
		MutableVector force = new MutableVector();
		MutableVector torque = new MutableVector();
		for (TimedDerivative derivative : state.getDerivativesBefore(t)) 
		{
			force.sum(derivative.force);
			torque.sum(derivative.torque);
		}
		output.force = output.force.add(force.toVector());
		output.torque = output.torque.add(torque.toVector());
	}

	public void damping(State state, Derivative output)
	{
		//arbitrarily lose 1% of energy per timestep to simulate heat loss
		output.force = output.force.subtract(state.velocity.scale(0.01f));
		output.torque = output.torque.subtract(state.angularVelocity.scale(0.01f));
	}

	public void collisionResponse(State s1, State s2)
	{
		Vector normal = s1.position.subtract(s2.position);
		normal.normalize();
		System.out.println("nudging them away");
		s1.position = s1.position.add(normal.normalize().scale(.005f));
		s2.position = s2.position.subtract(normal.normalize().scale(.005f));
		
		float restitution = 0.65f;
		Vector temp = s2.momentum;
		s2.momentum = s1.momentum.scale(restitution);
		s1.momentum = temp.scale(restitution);
		
		
		System.out.println("Pre-collision momentum");
		System.out.println("s1 angular momentum: " + s1.angularMomentum);
		System.out.println("s2 angular momentum: " + s2.angularMomentum);
		
		Vector impulse = normal.scale(s1.momentum.add(s2.momentum).length()*10);
		
		System.out.println("Impulse: " + impulse);
		Vector transformVector = s1.orientation.toAxisAngle().axis.normalize();
		
		s1.angularVelocity = s1.angularVelocity.add(s1.inverseInertiaTensor.transform(impulse.cross(transformVector.scale(-s1.inverseMass))));
		s2.angularVelocity = s2.angularVelocity.subtract(s2.inverseInertiaTensor.transform(impulse.cross(transformVector.scale(-s2.inverseMass))));
		s1.angularMomentum = s1.inertiaTensor.transform(s1.angularVelocity);
		s2.angularMomentum = s2.inertiaTensor.transform(s2.angularVelocity);

		System.out.println("Post-collision momentum");
		System.out.println("s1 angular momentum: " + s1.angularMomentum);
		System.out.println("s2 angular momentum: " + s2.angularMomentum);
		
		s1.recalculate(); //updates velocity from new momentum
		s2.recalculate();

		





		//		System.out.println("precollision momenta: ");
		//		float initialTotalMomentum = s1.momentum.add(s2.momentum).length();
		//		System.out.println("s1 linear: " + s1.momentum.length() + "  s1 angular: " + s1.angularMomentum.length());
		//		System.out.println("s2 linear: " + s2.momentum.length() + "  s2 angular: " + s2.angularMomentum.length());	
		//	
		//		float fCr = 1f; //coefficient of restitution
		//		Vector normal = s1.position.subtract(s2.position).normalize();

		//		float jNumerator = 
		//			(s1.velocity.subtract(s2.velocity)).dot(normal); /* +
		//			ra.cross(normal).dot(s1.angularVelocity) -
		//			rb.cross(normal).dot(s2.angularVelocity);*/
		//		float jDenominator = 
		//			s1.inverseMass + 
		//			s2.inverseMass; /* +
		//			(ra.cross(normal).dot(s1.inverseInertiaTensor.transform(ra.cross(normal)))) + 
		//			(rb.cross(normal).dot(s2.inverseInertiaTensor.transform(rb.cross(normal)))); */
		//		float j = (-1 * (1 + fCr) * jNumerator) / (jDenominator);
		//		Vector impulse = normal.scale(j);
		//		s1.velocity = s1.velocity.add(impulse.scale(s1.inverseMass));
		//		s2.velocity = s2.velocity.subtract(impulse.scale(s2.inverseMass));
		//		s1.momentum = s1.velocity.scale(s1.mass);
		//		s2.momentum = s2.velocity.scale(s2.mass);
		//		
		//		float finalTotalMomentum = s1.momentum.add(s2.momentum).length();
		//		float scaleFactor = initialTotalMomentum / finalTotalMomentum;
		//		s1.velocity.scale(scaleFactor);
		//		s1.momentum.scale(scaleFactor);
		//		s2.velocity.scale(scaleFactor);
		//		s2.momentum.scale(scaleFactor);
		//		
		//		System.out.println(s1.worldToBody.transform(normal));
		//		

		//		
		//		System.out.println("postcol momenta: ");
		//		System.out.println("s1 linear: " + s1.momentum.length() + "  s1 angular: " + s1.angularMomentum.length());
		//		System.out.println("s2 linear: " + s2.momentum.length() + "  s2 angular: " + s2.angularMomentum.length());
		//	
		//		s1.position = s1.position.add(s1.velocity.scale(50)); //nudge code
		//		s2.position = s2.position.add(s2.velocity.scale(50));
	}

	public void integrate(State state, long t, int dt)
	{
		Derivative a = evaluate(state.clone(), t);
		Derivative b = evaluate(state.clone(), t, dt / 2, a);
		Derivative c = evaluate(state.clone(), t, dt / 2, b);
		Derivative d = evaluate(state.clone(), t, dt, c);

		state.position = state.position.add(Vector.add(a.velocity, b.velocity, b.velocity, c.velocity, c.velocity, d.velocity).scale(dt / 6));
		state.momentum = state.momentum.add(Vector.add(a.force, b.force, b.force, c.force, c.force, d.force).scale(dt/6));
		state.orientation = state.orientation.add(Quaternion.add(a.spin, b.spin, b.spin, c.spin, c.spin, d.spin).scale(dt/6));
		state.angularMomentum = state.angularMomentum.add(Vector.add(a.torque, b.torque, b.torque, c.torque, c.torque, d.torque).scale(dt/6));
		state.recalculate();

		state.clearDerivativesBefore(t + dt);
	}
}


