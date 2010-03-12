package org.atcs.moonweasel.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.atcs.moonweasel.entities.*;
import org.atcs.moonweasel.util.*;
import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.Vector;

public class Physics 
{	
	
	private NumericalIntegration integrator;
	
	public static Map<State, State> collidingStates = new HashMap<State, State>();
	
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
		ArrayList<State> allStates = new ArrayList<State>();
		ArrayList<State> normalStates = new ArrayList<State>();
		ArrayList<State> statesToCheck = new ArrayList<State>();
		
		
		
		//Generating the macro list of all of the states, pre split
		//we split this list into "possible colliders" and "definitely safe"
		//possible colliders are handled with more precision later
		for(ModelEntity e : em.getAllOfType(ModelEntity.class))
		{
			allStates.add(e.getState());
			e.getState().setDangerZone(dt);
			e.getState().recalculate();
		}
		
		//integrate all the states
		for(State s : allStates)
		{
			integrator.integrate(s,t,dt);
		}
		
		//Separation based on "danger zone" concept
		for(State s : allStates)
		{
//			System.out.println("ConvexHullWorld: " + s.XYConvexHullWorld);
//			System.out.println(s.dangerZoneRadius);
			//System.out.println("There are " + allStates.size() + " states");
			for(State check : allStates)
			{
				if(!s.equals(check))
				{
					//System.out.println("Checking danger zones");
					if(s.position.squareDistance(check.position) < Math.pow((s.dangerZoneRadius + check.dangerZoneRadius), 2))
					{
						//DANGER WILL ROBINSON
						
						statesToCheck.add(s);
						System.out.println("added a point IN THE DANGER ZONE!!!");
					}
				}
			}
		}
		
		//integrate the safe states like normal
		//System.out.println("about to start integrating normal states");
//		for(State s : normalStates)
//		{
//			//System.out.println("integrating normal (non-colliding) states now");
//			integrator.integrate(s, t, dt);
//		}
		
		//integrate the dangerous states
		//System.out.println("about to start integrating the things we have to predict");
//		for(State s : statesToCheck)
//		{
//			//System.out.println("integrating potential colliding states now");
//			integrator.integrate(s, t, dt);
//		}
		
		for(State s : statesToCheck)
		{
			for(State check : statesToCheck)
			{
				if(!s.equals(check))
				{
					if(polyhedralCollision(s, check))
					{
						System.out.println("COLLISION!!!");
						collidingStates.put(s, check);
					}
				}
			}
		}
		
		for(State s : collidingStates.keySet())
		{
			integrator.collisionResponse(s, collidingStates.get(s));
		}
		allStates.clear();
		normalStates.clear();
		statesToCheck.clear();
		collidingStates.clear();
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
//	public State predictFutureState(ModelEntity me, int dt)
//	{
//		State futureState = new State(me.getState().mass, me.getState().inertiaTensor);
//		futureState.angularMomentum = me.getState().angularMomentum;
//		futureState.momentum = me.getState().momentum;
//		futureState.orientation = me.getState().orientation;
//		futureState.position = me.getState().position;
//		futureState.recalculate();
//		Integrator.integrate(futureState,0,dt);
//
//		return futureState;
//	}
	public static void removeDuplicates(ArrayList<Vector> listIn)
	{
		//exploits set nature
		Set<Vector> out = new HashSet<Vector>();
		for(Vector v : listIn)
		{
			out.add(v);
		}
		listIn.clear();
		for(Vector v : out)
		{
			listIn.add(v);
		}
	}
	public static ArrayList<Vector> projectOntoPlane(ArrayList<Vector> A, String s)
	{
		if(s.equals("xy"))
		{
			ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
			for(Vector v : A)
			{
				projectedComponents.add(v.projectIntoXY());
			}
			removeDuplicates(projectedComponents);
			return projectedComponents;
		}
		else if(s.equals("yz"))
		{
			ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
			for(Vector v : A)
			{
				projectedComponents.add(v.projectIntoYZ());
			}
			removeDuplicates(projectedComponents);
			return projectedComponents;
		}
		else
		{
			ArrayList<Vector> projectedComponents = new ArrayList<Vector>();
			for(Vector v : A)
			{
				projectedComponents.add(v.projectIntoZX());
			}
			removeDuplicates(projectedComponents);
			return projectedComponents;
		}
	}
	
	public static boolean pointInPolygon(Vector tp, ArrayList<Vector> polygon, String plane)
	{
		for(Vector v : polygon)
		{
			if(v.equals(tp)) return true;
		}
		
		int crossings = 0;
		int n = polygon.size();
		
		if(plane.equals("xy"))
		{
			for(int i = 0; i < n; i++)
			{
				if((polygon.get(i).x < tp.x && tp.x < polygon.get((i+1) % n).x) || (polygon.get(i).x > tp.x && tp.x > polygon.get((i+1) % n).x))
				{
					double t = (tp.x - polygon.get((i+1) % n).x) / (polygon.get(i).x - polygon.get((i+1) % n).x);
					double cy = t*(polygon.get(i).y) + (1 - t)*(polygon.get((i+1) % n).y);
					if(tp.y == cy)
					{
						return true;
						// on the boundary
					}
					else if(tp.y > cy)
					{
						crossings++;
					}
				}
				
				if (polygon.get(i).x == tp.x && polygon.get(i).y <= tp.y) 
				{
		            if (polygon.get(i).y == tp.y)
		            {
		            	return true;
		            	//on the boundary
		            }
		            if (polygon.get((i+1) % n).x == tp.x)
		            {
		                if ((polygon.get(i).y <= tp.y && tp.y <= polygon.get((i+1) % n).y) || (polygon.get(i).y >= tp.y && tp.y >= polygon.get((i+1) % n).y))
		                {
		                	return true;
		                	//on boundary
		                }         
		            } 
		            else if (polygon.get((i+1) % n).x > tp.x) crossings++;
		            if (polygon.get((i-1) % n).x > tp.x) crossings++;
		        }
			}
		}
		else if(plane.equals("yz"))
		{
			for(int i = 0; i < n; i++)
			{
				if((polygon.get(i).y < tp.y && tp.y < polygon.get((i+1) % n).y) || (polygon.get(i).y > tp.y && tp.y > polygon.get((i+1) % n).y))
				{
					double t = (tp.y - polygon.get((i+1) % n).y) / (polygon.get(i).y - polygon.get((i+1) % n).y);
					double cz = t*(polygon.get(i).z) + (1 - t)*(polygon.get((i+1) % n).z);
					if(tp.z == cz)
					{
						return true;
						// on the boundary
					}
					else if(tp.z > cz)
					{
						crossings++;
					}
				}
				
				if (polygon.get(i).y == tp.y && polygon.get(i).z <= tp.z) 
				{
		            if (polygon.get(i).z == tp.z)
		            {
		            	return true;
		            	//on the boundary
		            }
		            if (polygon.get((i+1) % n).y == tp.y)
		            {
		                if ((polygon.get(i).z <= tp.z && tp.z <= polygon.get((i+1) % n).z) || (polygon.get(i).z >= tp.z && tp.z >= polygon.get((i+1) % n).z))
		                {
		                	return true;
		                	//on boundary
		                }         
		            } 
		            else if (polygon.get((i+1) % n).y > tp.y) crossings++;
		            if (polygon.get((i-1) % n).y > tp.y) crossings++;
		        }
			}
		}
		else if(plane.equals("zx"))
		{
			for(int i = 0; i < n; i++)
			{
				if((polygon.get(i).z < tp.z && tp.z < polygon.get((i+1) % n).z) || (polygon.get(i).z > tp.z && tp.z > polygon.get((i+1) % n).z))
				{
					double t = (tp.z - polygon.get((i+1) % n).z) / (polygon.get(i).z - polygon.get((i+1) % n).z);
					double cx = t*(polygon.get(i).x) + (1 - t)*(polygon.get((i+1) % n).x);
					if(tp.x == cx)
					{
						return true;
						// on the boundary
					}
					else if(tp.x > cx)
					{
						crossings++;
					}
				}
				
				if (polygon.get(i).z == tp.z && polygon.get(i).x <= tp.x) 
				{
		            if (polygon.get(i).x == tp.x)
		            {
		            	return true;
		            	//on the boundary
		            }
		            if (polygon.get((i+1) % n).z == tp.z)
		            {
		                if ((polygon.get(i).x <= tp.x && tp.x <= polygon.get((i+1) % n).x) || (polygon.get(i).x >= tp.x && tp.x >= polygon.get((i+1) % n).x))
		                {
		                	return true;
		                	//on boundary
		                }         
		            } 
		            else if (polygon.get((i+1) % n).z > tp.z) crossings++;
		            if (polygon.get((i-1) % n).z > tp.z) crossings++;
		        }
			}
		}
		if(crossings % 2 == 1) return true;
		else return false;	
	}
	public static Boolean polygonCollision(ArrayList<Vector> p1, ArrayList<Vector> p2, String plane)
	{
		for(Vector v1 : p1)
		{
			if(pointInPolygon(v1, p2, plane)) return true;
		}
		
		return false;
	}
	
	
	
	public static Boolean polyhedralCollision(State s1, State s2)
	{	
		boolean b1 = polygonCollision(s1.XYConvexHullWorld, s2.XYConvexHullWorld, "xy");
		
		boolean b2 = polygonCollision(s1.YZConvexHullWorld, s2.YZConvexHullWorld, "yz");
		
		boolean b3 = polygonCollision(s1.ZXConvexHullWorld, s2.ZXConvexHullWorld, "zx");
		
//		System.out.println(b1);
//		System.out.println(b2);
//		System.out.println(b3);
		return (b1 && b2 && b3);
			
	}

}
