package org.atcs.moonweasel.physics;

import java.util.ArrayList;

import org.atcs.moonweasel.entities.*;
import org.atcs.moonweasel.util.*;

public class Physics 
{

	public void destroy() 
	{
		
	}
	
	NumericalIntegration Integrator = new NumericalIntegration();
	
	public void update(long t, int dt) //updates all models
	{
		EntityManager em = EntityManager.getEntityManager();
		for(ModelEntity e : em.getAllOfType(ModelEntity.class))
		{
			State oldState = e.getState();
			Integrator.integrate(e.getState(), t, dt); //refreshes the previous state and saves new values
			State futureState = e.getState();
			e.getState().setDangerZone(dt);
		}
	}
	
	//ASSUMPTION: uniform mass distribution over all objects
	
	public Vector computeCentroid(ArrayList<Vector> vertexList)
	{
		Vector returnVector = new Vector(0,0,0);
		for(Vector vertex : vertexList)
		{
			returnVector.add(vertex);
		}
		returnVector.scale(1.0f / vertexList.size());
		
		//averages all of the vertices;
		return returnVector;
		
		//Returns: a vector that gives the location of the centroid in world coordinates
	}
	
	
	//given a list of a body's coordinates (centered around the centroid), compute the inertia tensor
	//helpful if you already have the vertices in body coordinates
	public Matrix computeInertiaTensor(ArrayList<Vector> vertexListInBodyCoords)
	{
		//IMPLEMENTATION OF http://en.wikipedia.org/wiki/Moment_of_inertia#Moment_of_inertia_tensor	
		float i11 = 1, i22 = 1, i33 = 1, i12 = 0, i13 = 0, i23 = 0, i21 = 0, i31 = 0, i32 = 0;
		
		for(Vector v : vertexListInBodyCoords)
		{
			i11 += v.y * v.y + v.z * v.z; //principal moments of inertia go on the diagonal
			i22 += v.x * v.x + v.z * v.z;
			i33 += v.x * v.x + v.y * v.y;
			i12 += v.x * v.y; //products of inertia fill the sides symmetrically
			i13 += v.x * v.z;
			i23 += v.y * v.z;
		}
		
		i12 = -i12; //invert sign on products of inertia
		i13 = -i13;
		i23 = -i23;
		
		i21 = i12; //clone other entries
		i32 = i23;
		i31 = i13;
		
		return new Matrix(i11, i12, i13, i21, i22, i23, i31, i32, i33);
	}
	
	
	
	public State predictFutureState(ModelEntity me, int dt)
	{
		State futureState = new State(me.getState().mass, me.getState().inertiaTensor);
		futureState.angularMomentum = me.getState().angularMomentum.clone();
		futureState.momentum = me.getState().momentum.clone();
		futureState.orientation = me.getState().orientation.clone();
		futureState.position = me.getState().position.clone();
		futureState.inverseInertiaTensor = me.getState().inverseInertiaTensor.clone();
		futureState.inverseMass = me.getState().inverseMass;
		futureState.recalculate();
		Integrator.integrate(futureState,0,dt);
		
		return futureState;

		
		
	}
	
	
	
	
	
	public boolean collisionDetected(ModelEntity A, ModelEntity B)
	{
		boolean collisionDetected = false;
		if(A.getBoundingShape() instanceof BoundingSphere && 
		   B.getBoundingShape() instanceof BoundingSphere) //sphere on sphere collision
		{
			BoundingSphere ASphere = (BoundingSphere)A.getBoundingShape();
			BoundingSphere BSphere = (BoundingSphere)B.getBoundingShape();
			if(ASphere.radius + BSphere.radius > B.getState().position.subtract(A.getState().position).length())
			{
				collisionDetected = true;
			}
		}
		else if(A.getBoundingShape() instanceof BoundingBox && 
				B.getBoundingShape() instanceof BoundingBox) //both box case
		{
			BoundingBox ABox = (BoundingBox)A.getBoundingShape();
			BoundingBox BBox = (BoundingBox)B.getBoundingShape();
		}
		else if(A.getBoundingShape() instanceof BoundingBox && 
				B.getBoundingShape() instanceof BoundingSphere) //box on sphere collision
		{
			BoundingBox ABox = (BoundingBox)A.getBoundingShape();
			BoundingSphere BSphere = (BoundingSphere)B.getBoundingShape();
			float r = BSphere.radius;
			for (int i = 0; i < A.getState().verticesOfBoundingRegion.length; i++)
			{
				if(A.getState().verticesOfBoundingRegion[i].subtract(B.getState().position).length() < r)
				{
					collisionDetected = true;
					break;
				}
			}
		}
		else //sphere on box collision
		{
			BoundingSphere ASphere = (BoundingSphere)A.getBoundingShape();
			BoundingBox BBox = (BoundingBox)B.getBoundingShape();
			float r = ASphere.radius;
			for (int i = 0; i <B.getState().verticesOfBoundingRegion.length; i++)
			{
				if(B.getState().verticesOfBoundingRegion[i].subtract(A.getState().position).length() < r)
				{
					collisionDetected = true;
					break;
				}
			}
		}
		return collisionDetected;
	}
	
	
	
}
