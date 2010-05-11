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
	
	private interface IDirection {
		public float get(Vector v);
	}
	
	public enum Direction {
		X(new IDirection() {
			public float get(Vector v) {
				return v.x;
			}
		}),
		Y(new IDirection() {
			public float get(Vector v) {
				return v.y;
			}
		}),
		Z(new IDirection() {
			public float get(Vector v) {
				return v.z;
			}
		});
		
		private IDirection impl;
		
		private Direction(IDirection impl) {
			this.impl = impl;
		}
		
		public float get(Vector v) {
			return impl.get(v);
		}
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
//		if(o.equals(this))
//		{
//			System.out.println("crap. zero crossproduct");
//		}
		return new Vector(y * o.z - z * o.y, z*o.x - x*o.z, x*o.y - y*o.x);
	}
	
	public float distance(Vector o)
	{
		return this.subtract(o).length();
	}
	
	public float squareDistance(Vector o)
	{
		return (x-o.x)*(x-o.x) + (y-o.y)*(y-o.y) + (z-o.z)*(z-o.z);
	}
	
	public float dot(Vector o) 
	{
		return x * o.x + y * o.y + z * o.z;
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

	public float get(Direction d) {
		return d.get(this);
	}
	
	public float length() {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector normalize()
	{
		float length = this.length();
		if(length == 0f)
		{
			System.out.println("Attempting to normalize, about to divide by zero.");
		}
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
	
	public Vector projectOnto(Vector v)
	{
		return v.normalize().scale(this.dot(v.normalize()));
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
