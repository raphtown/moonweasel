package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.util.Derivative;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public class NumericalIntegration
{
	public Derivative evaluate(State initial, float t)
	{
		Derivative output = new Derivative();
		output.velocity = initial.velocity;
		output.spin = initial.spin;
		forces(initial, t, output.force, output.torque);
		return output;
	}
	
	//overloading the evaluate() method to account for the other derivatives in RK4
	
	public Derivative evaluate(State initial, float t, float dt, Derivative d)
	{
		State state = null; //arbitrary initialization to keep eclipse happy. i don't think this actually has to be here.
	    state.position = initial.position.add(  d.velocity.scale(dt)  );
	    state.momentum = initial.momentum.add(  d.force.scale(dt)  )  ;
	    state.orientation = initial.orientation.add( d.spin.scale(dt) );
	    state.angularMomentum = initial.angularMomentum.add(d.torque.scale(dt));
	    state.recalculate();

	    Derivative output = new Derivative();
	    output.velocity = state.velocity;
	    output.spin = state.spin;
	    forces(state, t+dt, output.force, output.torque);
	    return output;
	}

	// Calculate force and torque for physics state at time t.
    // Due to the way that the RK4 integrator works we need to calculate
    // force implicitly from state rather than explictly applying forces
    // to the rigid body once per update. This is because the RK4 achieves
    // its accuracy by detecting curvature in derivative values over the 
    // timestep so we need our force values to supply the curvature.
	public void forces(State state, float t, Vector force, Vector torque)
	{
		//sum of all forces acting on the object.. 
		//this will probably be done with a sum over all objects in the EntityManager
		//some stuff goes here, like gravity, etc... 
		//right now this draws us toward the origin
		force = state.position.scale(-10); 
		
		//for testing purposes, this is basically arbitrary torque in random directions depending on time
		torque.x = (float) Math.sin(t * 0.9 + 0.5);
		torque.y = (float) (1.1 * Math.sin(t * 0.5 + 0.4));
		torque.z = (float) (1.2 * Math.sin(t * 0.7 + 0.9));
	}

	
	public void integrate(State state, float t, float dt)
    {
        Derivative a = evaluate(state, t);
        Derivative b = evaluate(state, t+dt*0.5f, dt*0.5f, a);
        Derivative c = evaluate(state, t+dt*0.5f, dt*0.5f, b);
        Derivative d = evaluate(state, t+dt, dt, c);

        state.position = state.position.add((a.velocity.add(b.velocity).add(b.velocity).add(c.velocity).add(c.velocity).add(d.velocity)).scale(dt/6));
        state.momentum = state.momentum.add((a.force.add(b.force).add(b.force).add(c.force).add(c.force).add(d.force)).scale(dt/6));
        state.orientation = state.orientation.add((a.spin.add(b.spin).add(b.spin).add(c.spin).add(c.spin).add(d.spin)).scale(dt/6));
        state.angularMomentum = state.angularMomentum.add((a.torque.add(b.torque).add(b.torque).add(c.torque).add(c.torque).add(d.torque)).scale(dt/6));
        state.recalculate();
   }
	
}


