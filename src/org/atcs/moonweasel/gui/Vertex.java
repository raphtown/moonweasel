package org.atcs.moonweasel.gui;

public class Vertex
{
	private float xCoord;
	private float yCoord;
	private float zCoord;
	
	//Constructors
	public Vertex()
	{
		xCoord = 0;
		yCoord = 0;
		zCoord = 0;
	}
	public Vertex(float xIn, float yIn, float zIn)
	{
		xCoord = xIn;
		yCoord = yIn;
		zCoord = zIn;
	}
	
	//Mutators
	public void setX(float xIn)
	{
		xCoord = xIn;
	}
	public void setY(float yIn)
	{
		yCoord = yIn;
	}
	public void setZ(float zIn)
	{
		zCoord = zIn;
	}
	
	//Accessors
	public float getX()
	{
		return xCoord;
	}
	public float getY()
	{
		return yCoord;
	}
	public float getZ()
	{
		return zCoord;
	}
	
	public String toString()
	{
		return "X = " + xCoord + " Y = " + yCoord + " Z = " + zCoord;
	}
}
