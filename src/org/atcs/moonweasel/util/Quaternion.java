package org.atcs.moonweasel.util;

public class Quaternion {
	public float w, x, y, z;
	
	public Quaternion() {
	}
	
	public Quaternion(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Quaternion add(Quaternion o) {
		return new Quaternion(w + o.w, x + o.x, y + o.y, z + o.z);
	}
	
	public float length() {
		return (float)Math.sqrt(w * w + x * x + y * y + z * z);
	}
	
	public Quaternion normalize() {
		float length = this.length();
		Quaternion quaternion = new Quaternion();
		quaternion.w = w / length;
		quaternion.x = x / length;
		quaternion.y = y / length;
		quaternion.z = z / length;
		return quaternion;
	}
}
