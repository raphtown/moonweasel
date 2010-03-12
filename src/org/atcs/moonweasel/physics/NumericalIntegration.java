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
		damping(state, output);
		//collisionResponse();
		
		MutableVector force = new MutableVector();
		MutableVector torque = new MutableVector();
		for (TimedDerivative derivative : state.getDerivativesBefore(t)) {
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
		//for testing purposes, just zero the velocity and forces
		s1.angularMomentum = s1.angularMomentum.ZERO;
		s1.momentum = s1.momentum.scale(-1);
		System.out.println("reversed momentum");
		//s1.position = s1.position.add(s1.velocity.scale(1));
		//this should actually MOVE the stuff
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


