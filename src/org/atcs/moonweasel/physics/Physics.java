package org.atcs.moonweasel.physics;

import org.atcs.moonweasel.entities.*;
import org.atcs.moonweasel.util.*;

public class Physics 
{

	public void destroy() 
	{
		
	}
	
	NumericalIntegration Integrator = new NumericalIntegration();
	
	public void updateAllModels(float t, float dt) 
	{
		EntityManager em = new EntityManager(); //getEntityManagerFromServer();
		for(Entity e : em)
		{
			if(e.getClass().equals(ModelEntity.class)) //it's a modelEntity
			{
				State oldState = ((ModelEntity) e).getState();
				Integrator.integrate(oldState, t, dt); //refreshes the previous state and saves new values
			}	
		}
	}
	
	public void computeInertiaTensor()
	{
		//go here, read how they do it, and IMPLEMENT: http://en.wikipedia.org/wiki/Moment_of_inertia#Moment_of_inertia_tensor
	}
	
	
}
