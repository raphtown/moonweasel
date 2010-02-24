package org.atcs.moonweasel.util;

public class Vector
{
	public static final Vector ZERO = new Vector(0, 0, 0);
	
	public static Vector add(Vector... vectors) {
		float x = 0;
		float y = 0;
		float z = 0;
		
		for (Vector v : vectors) {
			x += v.x;
			y += v.y;
			z += v.z;
		}
		
		return new Vector(x, y, z);
	}
	
	public final float x, y, z;
	
	public Vector(float x, float y, float z) 
	{
		this.x = x;
		this.y = y;
		this.z = z;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vector))
			return false;
		Vector other = (Vector) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	public float length() {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector normalize()
	{
		float length = this.length();
		return new Vector(x / length, y / length, z / length);
	}
	
	public Vector roundMe(int decimalPrecision)
	{
		return new Vector(
				
	(float) (Math.floor(x*(Math.pow(10,decimalPrecision)))*Math.pow(10,-1*decimalPrecision)),
	(float) (Math.floor(y*(Math.pow(10,decimalPrecision)))*Math.pow(10,-1*decimalPrecision)),
	(float) (Math.floor(z*(Math.pow(10,decimalPrecision)))*Math.pow(10,-1*decimalPrecision)) 	
		);
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
	
	public Vector projectIntoZX()
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
	
	@Override
	public String toString() {
		return String.format("<%s, %s, %s>", x, y, z);
	}
}
