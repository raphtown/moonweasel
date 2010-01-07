package org.atcs.moonweasel.util;

public class Vector {
	public float x, y, z;
	
	public Vector() {
	}
	
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector add(Vector o) {
		return new Vector(x + o.x, y + o.y, z + o.z);
	}
	
	public float length() {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public Vector normalize() {
		float length = this.length();
		return new Vector(x / length, y / length, z / length);
	}
	
	public Vector scale(float scalar) {
		return new Vector(scalar * x, scalar * y, scalar * z);
	}
}
