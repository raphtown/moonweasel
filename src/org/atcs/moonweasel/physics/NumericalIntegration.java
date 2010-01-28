package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.players.UserCommand.Commands;
import org.atcs.moonweasel.util.Derivative;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.MutableVector;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public class NumericalIntegration
{
	public Derivative evaluate(State initial, long t)
	{
		Derivative output = new Derivative();
		output.velocity = initial.velocity;
		output.spin = initial.spin;
		forces(initial, t, output);
		return output;
	}
	
	//overloading the evaluate() method to account for the other derivatives in RK4
	
	public Derivative evaluate(State initial, long t, int dt, Derivative d)
	{
		State state = initial;
	    state.position = initial.position.add(d.velocity.scale(dt));
	    state.momentum = initial.momentum.add(d.force.scale(dt))  ;
	    state.orientation = initial.orientation.add( d.spin.scale(dt));
	    state.angularMomentum = initial.angularMomentum.add(d.torque.scale(dt));
	    state.recalculate();

	    Derivative output = new Derivative();
	    output.velocity = state.velocity;
	    output.spin = state.spin;
	    forces(state, t+dt, output);
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
	
		//Check if it's a player ship...
		
		control(new UserCommand(t - 1000, new Vector(-0.5f, 0, 0)), state, output);
	}

	
	public void damping(State state, Derivative output)
	{
		//arbitrarily lose 0.1% of energy per timestep to simulate heat loss
		output.force = output.force.subtract(state.velocity.scale(0.001f));
		output.torque = output.torque.subtract(state.angularVelocity.scale(0.001f));
	}
	
	public void collisionResponse()
	{
		
	}
	
	public void control(UserCommand input, State state, Derivative output)
	{
		float f = 50.0f; //50 newtons or 50 newton-meters, depending on context

		MutableVector relativeForce = new MutableVector();
		MutableVector torque = new MutableVector();
		
		// Mouse movement in x axis.
		if (input.get(Commands.ROLLING)) { // User wants to roll.
			torque.z += f * input.getMouse().x; // Scale mouse position. 
		} else { // Turn rather than roll.
			torque.y += f * input.getMouse().x;
		}

		// Mouse movement in y axis.
		torque.x += f * input.getMouse().y;
        
        // Thrusters
		if (input.get(Commands.FORWARD)) {
			relativeForce.z -= f;
		} 
		if (input.get(Commands.BACKWARD)) {
			relativeForce.z += f;
		}
		if (input.get(Commands.BOOST)) {
			relativeForce.z *= 2;
		}
		
		if (input.get(Commands.LEFT)) {
			relativeForce.x -= f;
		}
		if (input.get(Commands.RIGHT)) {
			relativeForce.x += f;
		}
		
		if (input.get(Commands.UP)) {
			relativeForce.y += f;
		}
		if (input.get(Commands.DOWN)) {
			relativeForce.y -= f;
		}
		
		output.force = state.orientation.rotate(relativeForce.toVector());
		output.torque = torque.toVector();
	}
	
	
	public void integrate(State state, long t, int dt)
    {
        Derivative a = evaluate(state, t);
        Derivative b = evaluate(state, t + dt / 2, dt / 2, a);
        Derivative c = evaluate(state, t + dt / 2, dt / 2, b);
        Derivative d = evaluate(state, t+dt, dt, c);

        state.position = state.position.add((a.velocity.add(b.velocity).add(b.velocity).add(c.velocity).add(c.velocity).add(d.velocity)).scale(dt/6));
        state.momentum = state.momentum.add((a.force.add(b.force).add(b.force).add(c.force).add(c.force).add(d.force)).scale(dt/6));
        state.orientation = state.orientation.add((a.spin.add(b.spin).add(b.spin).add(c.spin).add(c.spin).add(d.spin)).scale(dt/6));
        state.angularMomentum = state.angularMomentum.add((a.torque.add(b.torque).add(b.torque).add(c.torque).add(c.torque).add(d.torque)).scale(dt/6));
        state.recalculate();
   }
	
	
}


