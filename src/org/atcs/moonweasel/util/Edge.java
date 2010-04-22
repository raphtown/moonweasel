package org.atcs.moonweasel.util;

public class Edge
{
	private Vector startPoint;
	private Vector endPoint;
	
	public Edge(Vector v1, Vector v2)
	{
		startPoint = v1;
		endPoint = v2;
	}
	
	
	public Vector getStartPoint()
	{
		return startPoint;
	}


	public void setStartPoint(Vector startPoint)
	{
		this.startPoint = startPoint;
	}


	public Vector getEndPoint()
	{
		return endPoint;
	}


	public void setEndPoint(Vector endPoint)
	{
		this.endPoint = endPoint;
	}


	public boolean visibleFromPoint(Vector v)
	{
		Matrix m = new Matrix(startPoint.x, endPoint.x, v.x, startPoint.y, endPoint.y, v.y, 1, 1, 1);
		float det = m.determinant();
		if(det > 0.00001) return false;
		else return true; //we're including the collinear case as "visible"
	}

	public String toString()
	{
		return ("" + startPoint + " to " + endPoint);
	}
}
