package org.atcs.moonweasel.util;

public class Vector 
{
	public float x, y, z;
	
	public Vector() 
	{
		this(0, 0, 0);
	}
	
	public boolean equals(Vector v)
	{
		if(this.x == v.x && this.y == v.y && this.z == v.z) return true;
		else return false;
	}
	
	public Vector(float x, float y, float z) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public String toString()
	{
		return "<" + x + ", " + y + ", " + z + ">";
	}
	
	
	public Vector add(Vector o) 
	{
		return new Vector(x + o.x, y + o.y, z + o.z);
	}
	
	public Vector cross(Vector o) 
	{
		return new Vector(y * o.z - z * o.y, z*o.x - x*o.z, x*o.y - y*o.x);
	}
	
	public float distance(Vector o)
	{
		return this.subtract(o).length();
	}
	
	public float dot(Vector o) 
	{
		return x * o.x + y * o.y + x * o.z;
	}
	
	public float length() 
	{
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector normalize()
	{
		float length = this.length();
		return new Vector(x / length, y / length, z / length);
	}
	
	public Vector scale(float scalar) 
	{
		return new Vector(scalar * x, scalar * y, scalar * z);
	}
	
	public Vector subtract(Vector o)
	{
		return new Vector(x - o.x, y - o.y, z - o.z);
	}
	
	public Vector projectIntoXY()
	{
		return new Vector(x, y, 0);
	}
	public Vector projectIntoXZ()
	{
		return new Vector(x, 0, z);
	}
	public Vector projectIntoYZ()
	{
		return new Vector(0, y, z);
	}
	
	public float angleBetween(Vector v)
	{
		
		return (float) Math.acos(this.dot(v) / (v.length() * this.length()));
	}
	
	public Vector clone()
	{
		return new Vector(x,y,z);
	}
	
}
