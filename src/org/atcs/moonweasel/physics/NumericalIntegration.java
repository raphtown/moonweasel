package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.networking.Input;
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
		State state = new State(); //arbitrary initialization to keep eclipse happy. i don't think this actually has to be here.
	    state.position = initial.position.add(d.velocity.scale(dt));
	    state.momentum = initial.momentum.add(d.force.scale(dt))  ;
	    state.orientation = initial.orientation.add( d.spin.scale(dt));
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
		force.zero();
		torque.zero();
		
		Input inputController = new Input();
		//inputController.repoll();
		damping(state, force, torque);
		//collisionResponse();
		control(inputController, state, force, torque);
	}

	
	public void damping(State state, Vector force, Vector torque)
	{
		//arbitrarily lose 0.1% of energy per timestep to simulate heat loss
		force = force.subtract(state.velocity.scale(0.001f));
		torque = torque.subtract(state.angularVelocity.scale(0.001f));
	}
	
	public void collisionResponse()
	{
		
	}
	
	public void control(Input input, State state, Vector force, Vector torque)
	{
		float f = 50.0f; //50 newtons or 50 newton-meters, depending on context

        if (input.left()) torque.z -= f; //a rotation to the left is torque vector like (0,0,-Pi) given right-handed coords
        							   //this corresponds to the "roll" or aileron control system
        if (input.right()) torque.z += f;

        if (input.up()) torque.x -= f; //a nose-dive down is a torque vector like (-10,0,0), corresponding to pitch control

        if (input.down()) torque.x += f;
        
        //thrusters
        if (input.ctrl()) force.add(state.orientation.toMatrix().getOrientation()); //adds some thrust in the current orientation
        
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


