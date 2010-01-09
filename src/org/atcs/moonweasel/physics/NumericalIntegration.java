package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.util.Derivative;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public class NumericalIntegration
{
	public Derivative evaluate(State initial, float t, float dt, Derivative d)
	{
		State state = null; //arbitrary initialization to keep eclipse happy. i don't think this actually has to be here.
	    state.position = initial.position.add(  (d.dx).scale(dt)  );
	    state.velocity = initial.velocity.add(  (d.dv).scale(dt)  );

	    Derivative output = new Derivative(state.velocity, acceleration(state, t+dt));
	    return output;
	}

	public Vector acceleration(State state, float timeBasedParameter) //here is a parameter that we can use to parametrize some force vector 
	{
		Vector linearAccelVec = state.forceApplied.scale(1 / (state.mass));
		return linearAccelVec;
		//should return an acceleration vector.. needs to take into account force acceleration code
	}
	
	public void integrate(State state, float t, float dt)
    {
        Derivative a = evaluate(state, t, 0, new Derivative(state.velocity, acceleration(state, t)));  //if there are bugs, it's probably in the calculation of the very first derivative.
        Derivative b = evaluate(state, t+dt*(float) 0.5, dt*(float) 0.5, a);
        Derivative c = evaluate(state, t+dt* (float) 0.5, dt* (float) 0.5, b);
        Derivative d = evaluate(state, (float) t+dt, (float) dt, c);

         Vector dxdt = a.dx.add(  (b.dx.add(c.dx)).scale(2)  ).add(d.dx).scale((float) 1.0/6);
         Vector dvdt = a.dv.add(  (b.dv.add(c.dv)).scale(2)  ).add(d.dv).scale((float) 1.0/6);

        state.position = state.position.add(  dxdt.scale(dt)  );
        state.velocity = state.velocity.add(  dvdt.scale(dt)  );
   }
	
}


