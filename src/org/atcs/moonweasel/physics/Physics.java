package org.atcs.moonweasel.physics;

import java.util.ArrayList;

import org.atcs.moonweasel.entities.*;
import org.atcs.moonweasel.util.*;

public class Physics 
{
	public static ArrayList<Vector> cubeVectors = new ArrayList<Vector>();
	public static ArrayList<Vector> projectedVectors = new ArrayList<Vector>();
	public static ArrayList<Vector> convexHullVectors = new ArrayList<Vector>();
	public static final float VECTOR_PRECISION_TOLERANCE = 0.00001f;
	public static void main(String[] args)
	{
		State me1 = new State(0, null);
		State me2 = new State(0, null);

		Vector pt1 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));
		Vector pt2 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));
		Vector pt3 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));
		Vector pt4 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));
		Vector pt5 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));
		Vector pt6 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));
		Vector pt7 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));
		Vector pt8 = new Vector((int) (500*Math.random()),(int)( 500*Math.random()), (int) (500*Math.random()));


		ArrayList<Vector> cube1 = new ArrayList<Vector>();
		cube1.add(pt1);
		cube1.add(pt2);
		cube1.add(pt3);
		cube1.add(pt4);
		cube1.add(pt5);
		cube1.add(pt6);
		cube1.add(pt7);
		cube1.add(pt8);

		cubeVectors.addAll(cube1);
		projectedVectors.addAll(projectOntoXY(cube1));
		convexHullVectors.addAll(convexHull(projectOntoXY(cube1), "xy"));
		
		me1.verticesOfBoundingRegion = cube1;

		System.out.println("Handling cube 1.");
		System.out.println("Cube 1: " + cube1);
		System.out.println("Convex hull on xy plane: " + convexHull(projectOntoXY(cube1), "xy"));
		
		convexHullTester myTester = new convexHullTester();
		myTester.repaint();
		

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
			for (int i = 0; i < A.getState().verticesOfBoundingRegion.size(); i++)
			{
				if(A.getState().verticesOfBoundingRegion.get(i).subtract(B.getState().position).length() < r)
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
			for (int i = 0; i <B.getState().verticesOfBoundingRegion.size(); i++)
			{
				if(B.getState().verticesOfBoundingRegion.get(i).subtract(A.getState().position).length() < r)
				{
					collisionDetected = true;
					break;
				}
			}
		}
		return collisionDetected;
	}



}
