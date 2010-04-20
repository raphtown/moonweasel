package org.atcs.moonweasel.util;

import java.util.ArrayList;

import org.atcs.moonweasel.physics.Physics;

public class ConvexHull
{

	public ArrayList<Edge> CH = new ArrayList<Edge>();
	private ArrayList<Vector> remainingPoints = new ArrayList<Vector>();
	private String plane;
	
	public ConvexHull(ArrayList<Vector> points, String p)
	{
		plane = p;
		remainingPoints.addAll(points);
		Physics.removeDuplicates(remainingPoints);
		if(p.equals("xy"))
		{
			remainingPoints = Physics.projectOntoPlane(remainingPoints, plane);
		}
		else if(p.equals("yz"))
		{
			remainingPoints = Physics.projectOntoPlane(remainingPoints, plane);
		}
		else
		{
			remainingPoints = Physics.projectOntoPlane(remainingPoints, plane);
		}
		this.hullMe();
	}
	
	public ArrayList<Edge> getCH()
	{
		return CH;
	}
	
	public String toString()
	{
		return this.toPolygon().toString();
	}
	
	public ArrayList<Vector> toPolygon()
	{
		ArrayList<Vector> output = new ArrayList<Vector>();
		for(int i = 0; i < CH.size(); i++)
		{
			output.add(CH.get(i).getStartPoint());
		}
		return output;
	}
	
	private void hullMe()
	{
		initialize();
		while(remainingPoints.size() > 0)
		{
			addPoint(plane);
		}
	}
	
	private void initialize()
	{
		Vector pHighest = findHighest(plane);
		remainingPoints.remove(pHighest);
		
		Vector pLeftest = findLeftest(plane);
		remainingPoints.remove(pLeftest);
		
		Vector pLowest = findLowest(plane);
		remainingPoints.remove(pLowest);
		
		Edge firstEdge = new Edge(pHighest, pLeftest);
		Edge secondEdge = new Edge(pLeftest, pLowest);
		Edge thirdEdge = new Edge(pLowest, pHighest);
	
		//OMG something is being overwritten DIE DIE DIE
		//this has been handled. 
		
		
		CH.add(0, firstEdge);
		CH.add(1, secondEdge);
		CH.add(2, thirdEdge);
	}
	
	private Vector findHighest(String p)
	{
		Vector leftestPoint = remainingPoints.get(0);
		if(plane.equalsIgnoreCase("xy"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.y > leftestPoint.y)
				{
					leftestPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("yz"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.z > leftestPoint.z)
				{
					leftestPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("zx"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.x > leftestPoint.x)
				{
					leftestPoint = v;
				}
			}
		}
		return leftestPoint;
	}

	private Vector findLeftest(String p)
	{
		Vector leftestPoint = remainingPoints.get(0);
		if(plane.equalsIgnoreCase("xy"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.x < leftestPoint.x)
				{
					leftestPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("yz"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.y < leftestPoint.y)
				{
					leftestPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("zx"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.z < leftestPoint.z)
				{
					leftestPoint = v;
				}
			}
		}
		return leftestPoint;
	}

	private Vector findLowest(String p)
	{
		Vector lowestPoint = remainingPoints.get(0);
		if(plane.equalsIgnoreCase("xy"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.y < lowestPoint.y)
				{
					lowestPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("yz"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.z < lowestPoint.z)
				{
					lowestPoint = v;
				}
			}
		}
		else if (plane.equalsIgnoreCase("zx"))
		{
			for(Vector v : remainingPoints)
			{
				if(v.x < lowestPoint.x)
				{
					lowestPoint = v;
				}
			}
		}
		return lowestPoint;
	}

	private void addPoint(String p)
	{
		Vector point = remainingPoints.get(0);
		remainingPoints.remove(0);
	
		
		ArrayList<Edge> visibleEdges = new ArrayList<Edge>();
		int index = findIndexOfNonvisibleEdge(point);
		
		if(index == -1)
		{
			//System.out.println("Point " + point + " inside convex hull, not added.");
			return;
		}
		
		for(int i = 0; i < CH.size(); i++)
		{
			if (CH.get((index+i) % CH.size()).visibleFromPoint(point, plane))
			{
				visibleEdges.add(CH.get((index+i)%CH.size()));
			}
		}
		Vector startConnector = visibleEdges.get(0).getStartPoint();
		Vector endConnector = visibleEdges.get(visibleEdges.size()-1).getEndPoint();
		CH.removeAll(visibleEdges);
		CH.add(index, new Edge(startConnector, point));
		CH.add(index+1, new Edge(point, endConnector));
		
		//System.out.println("Added " + point + " to the convex hull");
	}
	
	public int findIndexOfNonvisibleEdge(Vector p)
	{
		for(Edge e : CH)
		{
			if(e.visibleFromPoint(p, plane))
			{
				return CH.indexOf(e);
			}
		}
		//System.out.println("oh damn, " + p + " can see every single edge in CH");
		return -1;
	}
}
