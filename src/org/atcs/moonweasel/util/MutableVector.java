package org.atcs.moonweasel.util;

public class MutableVector {
	public float x, y, z;
	
	public MutableVector() {
		this(0, 0, 0);
	}
	
	public MutableVector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void minus(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
	}
	
	public void sum(Vector v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
	}
	
	public Vector toVector() {
		return new Vector(x, y, z);
	}
}
