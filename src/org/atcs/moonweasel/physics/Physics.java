package org.atcs.moonweasel.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.atcs.moonweasel.entities.EntityManager;
import org.atcs.moonweasel.entities.ModelEntity;
import org.atcs.moonweasel.physics.ConvexHull.Projection;
import org.atcs.moonweasel.util.Matrix;
import org.atcs.moonweasel.util.State;
import org.atcs.moonweasel.util.Vector;

public class Physics 
{
	private NumericalIntegration integrator;
	
	public static final double MIN_COLLIDE_DISTANCE = 0.7; //tuning parameter
	
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
		
		Map<State, State> collidingStates = new HashMap<State, State>();
		
		//Generating the macro list of all of the states, pre split
		//we split this list into "possible colliders" (stored in statesTocheck) 
		//and "definitely safe" (stored in normalStates). 
		
		
		
		
		//This method grabs the state from each model entity in the manager.
		for(ModelEntity e : em.getAllOfType(ModelEntity.class))
		{
			allStates.add(e.getState());
			e.getState().setDangerZone(dt);
			e.getState().recalculate();
		}
		
		//State integration
		for(State s : allStates)
		{
			//integrate everything, and if something collides, we'll handle it later
			integrator.integrate(s,t,dt);
		}
		
		//Separation based on "danger zone" concept
		for(State s : allStates)
		{
			for(State check : allStates)
			{
				if(!s.equals(check))
				{
					if(s.position.squareDistance(check.position) < Math.pow((s.dangerZoneRadius + check.dangerZoneRadius), 2))
					{
						//DANGER WILL ROBINSON! Collision imminent!
						statesToCheck.add(s);
						//System.out.println("added a point IN THE DANGER ZONE!!!");
					}
				}
			}
		}

		for(State s : statesToCheck)
		{
			for(State check : statesToCheck)
			{
				if(!s.equals(check))
				{
					if(polyhedralCollision(s, check))
					{
						//map should not have duplicates, so this final condition is needed
						if(!collidingStates.keySet().contains(check))
						{
							s.entity.collidedWith(check.entity);
							check.entity.collidedWith(s.entity);
							collidingStates.put(s, check);
						}
					}
				}
			}
		}
		
		for(State s : collidingStates.keySet())
		{
			integrator.collisionResponse(s, collidingStates.get(s));
			integrator.integrate(s,t,dt);
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
	public Matrix computeInertiaTensor(List<Vector> vertexListInBodyCoords)
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

	public static boolean pointInPolygon(Vector tp, Vector[] polygon)
	{
		for(Vector v : polygon)
		{
			if(v.equals(tp)) return true;
		}
		
		int crossings = 0;
		int n = polygon.length;

		for(int i = 0; i < n; i++)
		{
			if((polygon[i].x < tp.x && tp.x < polygon[(i+1) % n].x) || (polygon[i].x > tp.x && tp.x > polygon[(i+1) % n].x))
			{
				double t = (tp.x - polygon[(i+1) % n].x) / (polygon[i].x - polygon[(i+1) % n].x);
				double cy = t*(polygon[i].y) + (1 - t)*(polygon[(i+1) % n].y);
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
			
			if (polygon[i].x == tp.x && polygon[i].y <= tp.y) 
			{
	            if (polygon[i].y == tp.y)
	            {
	            	return true;
	            	//on the boundary
	            }
	            if (polygon[(i+1) % n].x == tp.x)
	            {
	                if ((polygon[i].y <= tp.y && tp.y <= polygon[(i+1) % n].y) || (polygon[i].y >= tp.y && tp.y >= polygon[(i+1) % n].y))
	                {
	                	return true;
	                	//on boundary
	                }         
	            } 
	            else if (polygon[(i+1) % n].x > tp.x) crossings++;
	            if (polygon[(i-1) % n].x > tp.x) crossings++;
	        }
		}

		if(crossings % 2 == 1) return true;
		else return false;	
	}
	public static boolean polygonCollision(Vector[] p1, Vector[] p2)
	{
		for(Vector v1 : p1)
		{
			if(pointInPolygon(v1, p2)) return true;
		}
		
		return false;
	}
	
	public static boolean polyhedralCollision(State s1, State s2)
	{
		Vector[] s1body, s2body, s1world, s2world;
		for (Projection p : Projection.values()) {
			s1body = ConvexHull.getConvexHull(s1.entity.getClass(), p);
			s2body = ConvexHull.getConvexHull(s2.entity.getClass(), p);
			
			s1world = new Vector[s1body.length];
			for (int i = 0; i < s1body.length; i++) {
				s1world[i]  = p.project(s1.bodyToWorld.transform(s1body[i], p));
			}

			s2world = new Vector[s2body.length];
			for (int i = 0; i < s2body.length; i++) {
				s2world[i]  = p.project(s2.bodyToWorld.transform(s2body[i], p));
			}

			if (!polygonCollision(s1world, s2world)) {
				return false;
			}
		}
		
		return true;
	}
}
