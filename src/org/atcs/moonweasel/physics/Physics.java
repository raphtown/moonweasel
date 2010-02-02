package org.atcs.moonweasel.physics;

import java.util.ArrayList;

import org.atcs.moonweasel.entities.*;
import org.atcs.moonweasel.util.*;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Vector;

public class Physics 
{
	private NumericalIntegration integrator;
	
	public Physics() 
	{
		this.integrator = new NumericalIntegration();
	}
	
	public void destroy() 
	{

	}

	NumericalIntegration Integrator = new NumericalIntegration();	

	public void update(long t, int dt) //updates all models
	{
		EntityManager em = EntityManager.getEntityManager();
		for(ModelEntity e : em.getAllOfType(ModelEntity.class))
		{
			integrator.integrate(e, t, dt);
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
		futureState.angularMomentum = me.getState().angularMomentum;
		futureState.momentum = me.getState().momentum;
		futureState.orientation = me.getState().orientation;
		futureState.position = me.getState().position;
		futureState.recalculate();
		Integrator.integrate(me, futureState,0,dt);

		return futureState;
	}
	public static ArrayList<Vector> projectOntoXY(ArrayList<Vector> A)
	{
		ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
		for(Vector v : A)
		{
			projectedComponents.add(v.projectIntoXY());
		}
		return projectedComponents;
	}
	public static ArrayList<Vector> projectOntoXZ(ArrayList<Vector> A)
	{
		ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
		for(Vector v : A)
		{
			projectedComponents.add(v.projectIntoXZ());
		}
		return projectedComponents;
	}
	public static ArrayList<Vector> projectOntoYZ(ArrayList<Vector> A)
	{
		ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
		for(Vector v : A)
		{
			projectedComponents.add(v.projectIntoYZ());
		}
		return projectedComponents;
	}
	public static ArrayList<Vector> convexHull(ArrayList<Vector> projcomps, String plane)
	{
		ArrayList<Vector> convexHull = new ArrayList<Vector>();
		convexHull.add(findMostExtremalPoint(projcomps, plane));
		Vector referenceVector = setReferenceVector(plane);
		boolean done = false;


		while(!done)
		{	
			Vector nextPoint = findNextPoint(projcomps, referenceVector, convexHull.get(convexHull.size()-1));
			convexHull.add(nextPoint);
			referenceVector = updateReferenceVector(convexHull);
			if(convexHull.get(0).equals(convexHull.get(convexHull.size()-1)))
			{
				done = true;
			}
		}
		
		return convexHull;

	}
	public static Vector findMostExtremalPoint(ArrayList<Vector> projcomps, String plane)
	{
		Vector extremalPoint = projcomps.get(0);
		if(plane.equalsIgnoreCase("xy"))
		{
			for(Vector v : projcomps)
			{
				if(v.x < extremalPoint.x)
				{
					extremalPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("yz"))
		{
			for(Vector v : projcomps)
			{
				if(v.y < extremalPoint.y)
				{
					extremalPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("zx"))
		{
			for(Vector v : projcomps)
			{
				if(v.z < extremalPoint.z)
				{
					extremalPoint = v;
				}
			}
		}
		return extremalPoint;
	}
	public static Vector setReferenceVector(String plane)

	{
		if(plane.equalsIgnoreCase("xy"))
		{
			return new Vector(0,1,0);
		}
		else if(plane.equalsIgnoreCase("yz"))
		{
			return new Vector(0,0,1);
		}
		else if(plane.equalsIgnoreCase("zx"))
		{
			return new Vector(1,0,0);
		}
		else return null;
	}
	public static Vector findNextPoint(ArrayList<Vector> projcomps, Vector referenceVector, Vector lastPointAdded)
	{
		double tempAlpha = 6.28318531; //2pi
		double bestAlpha = tempAlpha;
		
		Vector tempEdge = null;
		Vector bestEdge = null;
		Vector bestPoint = null;
		for(Vector v : projcomps)
		{
			if(!v.equals(lastPointAdded))
			{
				tempEdge = v.subtract(lastPointAdded);
				tempAlpha = tempEdge.angleBetween(referenceVector);
				if(tempAlpha < bestAlpha)
				{
					bestAlpha = tempAlpha;
					bestEdge = tempEdge;
					bestPoint = v;
				}
			}
		}
		return bestPoint;
	}
	public static Vector updateReferenceVector(ArrayList<Vector> convexHull)
	{
		return convexHull.get(convexHull.size()-1).subtract(convexHull.get(convexHull.size()-2));
	}

}
