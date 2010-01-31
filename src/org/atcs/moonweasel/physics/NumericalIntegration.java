package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.entities.players.UserCommand;
import org.atcs.moonweasel.entities.players.UserCommand.Commands;
import org.atcs.moonweasel.entities.ships.Ship;
import org.atcs.moonweasel.ranges.Range;
import org.atcs.moonweasel.util.MutableVector;
import org.atcs.moonweasel.util.Quaternion;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public class NumericalIntegration
{
	private ModelEntity curEntity;
	
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
	
		if (curEntity instanceof Ship) {
			Range<UserCommand> commands = ((Ship)curEntity).getPilot().getCommandsBefore(t);
			for (UserCommand command : commands) {
				control(command, state, output);				
			}
		}
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
		float f = 1000 * 0.00005f; //50 newtons or 50 newton-meters, depending on context
		Vector relativeVelocity = state.orientation.inverse().rotate(state.velocity);
		
		MutableVector relativeForce = new MutableVector();
		MutableVector relativeTorque = new MutableVector();
		
		// Mouse movement in x axis.
		if (input.get(Commands.ROLLING)) { // User wants to roll.
			relativeTorque.z += 0.001 * input.getMouse().x; // Scale mouse position. 
		} else { // Turn rather than roll.
			relativeTorque.y += 0.001 * input.getMouse().x;			
		}

		// Mouse movement in y axis.
		relativeTorque.x += 0.001 * input.getMouse().y;
				
		// Damp that angular motion!!!
		Vector dampTorque = null;
		if (input.get(Commands.AUTOMATIC_THRUSTER_CONTROL)) {
			dampTorque = new Vector(0.1f * state.angularVelocity.x,
									0.1f * state.angularVelocity.y,
									0.1f * state.angularVelocity.z);
		}

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
		
		if (input.get(Commands.LEFT) && input.get(Commands.RIGHT)) {
		} else if (input.get(Commands.LEFT)) {
			relativeForce.x -= f;
		} else if (input.get(Commands.RIGHT)) {
			relativeForce.x += f;
		} else if (input.get(Commands.AUTOMATIC_THRUSTER_CONTROL)) {
			relativeForce.x -= 10 * relativeVelocity.x;
		}
		
		if (input.get(Commands.UP) && input.get(Commands.DOWN)) {
		} else if (input.get(Commands.UP)) {
			relativeForce.y += f;
		} else if (input.get(Commands.DOWN)) {
			relativeForce.y -= f;
		} else if (input.get(Commands.AUTOMATIC_THRUSTER_CONTROL)) {
			relativeForce.y -= 10 * relativeVelocity.y;
		}
		
		output.force = output.force.add(state.orientation.rotate(relativeForce.toVector()));
		output.torque = output.torque.add(state.orientation.rotate(relativeTorque.toVector()));
		
		if (dampTorque != null) {
			output.torque= output.torque.subtract(dampTorque);
		}		
	}
	
	public void integrate(ModelEntity entity, long t, int dt) {
		integrate(entity, entity.getState(), t, dt);
	}
	
	public void integrate(ModelEntity entity, State state, long t, int dt)
    {
		this.curEntity = entity;
		
        Derivative a = evaluate(state.clone(), t);
        Derivative b = evaluate(state.clone(), t, dt / 2, a);
        Derivative c = evaluate(state.clone(), t, dt / 2, b);
        Derivative d = evaluate(state.clone(), t, dt, c);
        
        state.position = state.position.add(
        		Vector.add(a.velocity, b.velocity, b.velocity, b.velocity, 
        				c.velocity, c.velocity, d.velocity).scale(dt / 6));
        state.momentum = state.momentum.add(
        		Vector.add(a.force, b.force, b.force, c.force, c.force, d.force).scale(dt/6));
        state.orientation = state.orientation.add(
        		Quaternion.add(a.spin, b.spin, b.spin, c.spin, c.spin, d.spin).scale(dt/6));
        state.angularMomentum = state.angularMomentum.add(
        		Vector.add(a.torque, b.torque, b.torque, c.torque, c.torque, d.torque).scale(dt/6));
        state.recalculate();
   }
}


