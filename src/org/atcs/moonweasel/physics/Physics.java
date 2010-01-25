package org.atcs.moonweasel.physics;

import java.util.ArrayList;

import org.atcs.moonweasel.entities.*;
import org.atcs.moonweasel.util.*;

public class Physics 
{

	public static void main(String[] args)
	{
		State me1 = new State(0, null);
		State me2 = new State(0, null);
		
		Vector pt1 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt2 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt3 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt4 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt5 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt6 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt7 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt8 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		
		Vector pt9 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt10 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt11 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt12 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt13 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt14 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt15 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		Vector pt16 = new Vector((float) (6*Math.random()),(float)( 6*Math.random()), (float) (6*Math.random()));
		
		
		ArrayList<Vector> cube1 = new ArrayList<Vector>();
		cube1.add(pt1);
		cube1.add(pt2);
		cube1.add(pt3);
		cube1.add(pt4);
		cube1.add(pt5);
		cube1.add(pt6);
		cube1.add(pt7);
		cube1.add(pt8);
		
		ArrayList<Vector> cube2 = new ArrayList<Vector>();
		cube2.add(pt9);
		cube2.add(pt10);
		cube2.add(pt11);
		cube2.add(pt12);
		cube2.add(pt13);
		cube2.add(pt14);
		cube2.add(pt15);
		cube2.add(pt16);
		
		me1.verticesOfBoundingRegion = cube1;
		me2.verticesOfBoundingRegion = cube2;
		
		System.out.println("Handling cube 1.");
		System.out.println("Cube 1: " + cube1);
		
		System.out.println("Projection of cube1 into XY plane: " + projectOntoXY(me1));
		System.out.println("Convex Hull in XY plane: ");
		System.out.println(convexHullFinderXY(projectOntoXY(me1)));
		
		System.out.println("Projection of cube1 into YZ plane: " + projectOntoYZ(me1));
		System.out.println("Convex Hull in YZ plane: ");
		System.out.println(convexHullFinderYZ(projectOntoYZ(me1)));
		
		System.out.println("Projection of cube1 into XZ plane: " + projectOntoXZ(me1));
		System.out.println("Convex Hull in XZ plane: ");
		System.out.println(convexHullFinderXZ(projectOntoXZ(me1)));
		
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
	
	
	public static ArrayList<Vector> projectOntoXY(State A)
	{
		ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
		for(Vector v : A.verticesOfBoundingRegion)
		{
			projectedComponents.add(v.projectIntoXY());
		}
		return projectedComponents;
			
		
	}
	public static ArrayList<Vector> projectOntoXZ(State A)
	{
		ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
		for(Vector v : A.verticesOfBoundingRegion)
		{
			projectedComponents.add(v.projectIntoXZ());
		}
		return projectedComponents;
	}
	public static ArrayList<Vector> projectOntoYZ(State A)
	{
		ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
		for(Vector v : A.verticesOfBoundingRegion)
		{
			projectedComponents.add(v.projectIntoYZ());
		}
		return projectedComponents;
	}
	
	public static ArrayList<Vector> convexHullFinderXY(ArrayList<Vector> projectedComponents)
	{
		ArrayList<Vector> convexHullPoints = new ArrayList<Vector>();
		
		Vector leftMostPoint = projectedComponents.get(0);
		for(Vector v : projectedComponents)
		{
			if(v.x < leftMostPoint.x)
			{
				leftMostPoint = v;
			}
		}
		convexHullPoints.add(leftMostPoint);
		Vector referenceVector = new Vector(0,1,0);
		
		Vector lastPoint = convexHullPoints.get(0);
		Vector testVector = null;
		Vector bestVector = null;
		float bestAlpha = 6.28318531f; //2pi
		boolean done = false;
		
		while(!done)
		{
			for(Vector testPoint : projectedComponents)
			{
				referenceVector = referenceVector.normalize();
				testVector = testPoint.subtract(lastPoint);
				float alpha = testVector.angleBetween(referenceVector);
				if(alpha < bestAlpha)
				{
					bestAlpha = alpha;
					bestVector = testVector;
				}
			}
			
			Vector newPoint = bestVector.add(lastPoint);
			
			convexHullPoints.add(newPoint);
			System.out.println("Added " + newPoint + " to the convex hull.");
			referenceVector = bestVector; //updates the line of reference
			lastPoint = newPoint;
			bestVector = null;
			bestAlpha = 6.28318531f; //2pi
			
			if(lastPoint.equals(convexHullPoints.get(0))) //first point will be added twice, so remove it once
			{
				done = true;
				convexHullPoints.remove(lastPoint);
			}
		}
		
		
		return convexHullPoints;
		
	}
	
	
	public static ArrayList<Vector> convexHullFinderXZ(ArrayList<Vector> projectedComponents)
	{
		ArrayList<Vector> convexHullPoints = new ArrayList<Vector>();
		
		Vector leftMostPoint = projectedComponents.get(0);
		for(Vector v : projectedComponents)
		{
			if(v.x < leftMostPoint.x)
			{
				leftMostPoint = v;
			}
		}
		convexHullPoints.add(leftMostPoint);
		Vector referenceVector = new Vector(0,0,1);
		
		Vector lastPoint = convexHullPoints.get(0);
		Vector testVector = null;
		Vector bestVector = null;
		float bestAlpha = 6.28318531f; //2pi
		boolean done = false;
		
		while(!done)
		{
			for(Vector testPoint : projectedComponents)
			{
				testVector = testPoint.subtract(lastPoint);
				float alpha = testVector.angleBetween(referenceVector);
				if(alpha < bestAlpha)
				{
					bestAlpha = alpha;
					bestVector = testVector;
				}
			}
			
			Vector newPoint = bestVector.add(lastPoint);
			
			convexHullPoints.add(newPoint);
			System.out.println("Added " + newPoint + " to the convex hull.");
			referenceVector = bestVector; //updates the line of reference
			lastPoint = newPoint;
			bestVector = null;
			bestAlpha = 6.28318531f; //2pi
			
			if(lastPoint.equals(convexHullPoints.get(0))) //first point will be added twice, so remove it once
			{
				done = true;
				convexHullPoints.remove(lastPoint);
			}
		}
		
		
		return convexHullPoints;
		
	}
	
	
	public static ArrayList<Vector> convexHullFinderYZ(ArrayList<Vector> projectedComponents)
	{
		ArrayList<Vector> convexHullPoints = new ArrayList<Vector>();
		
		Vector leftMostPoint = projectedComponents.get(0);
		for(Vector v : projectedComponents)
		{
			if(v.y < leftMostPoint.y)
			{
				leftMostPoint = v;
			}
		}
		convexHullPoints.add(leftMostPoint);
		Vector referenceVector = new Vector(0,1,0);
		
		Vector lastPoint = convexHullPoints.get(0);
		Vector testVector = null;
		Vector bestVector = null;
		float bestAlpha = 6.28318531f; //2pi
		boolean done = false;
		
		while(!done)
		{
			for(Vector testPoint : projectedComponents)
			{
				testVector = testPoint.subtract(lastPoint);
				float alpha = testVector.angleBetween(referenceVector);
				if(alpha < bestAlpha)
				{
					bestAlpha = alpha;
					bestVector = testVector;
				}
			}
			
			Vector newPoint = bestVector.add(lastPoint);
			
			convexHullPoints.add(newPoint);
			System.out.println("Added " + newPoint + " to the convex hull.");
			referenceVector = bestVector; //updates the line of reference
			lastPoint = newPoint;
			bestVector = null;
			bestAlpha = 6.28318531f; //2pi
			
			if(lastPoint.equals(convexHullPoints.get(0))) //first point will be added twice, so remove it once
			{
				done = true;
				convexHullPoints.remove(lastPoint);
			}
		}
		
		
		return convexHullPoints;
		
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
