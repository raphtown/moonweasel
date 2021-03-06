package org.atcs.moonweasel.util;


import java.io.Serializable;

public class Quaternion implements Serializable {	
	private static final long serialVersionUID = -5604971247198938994L;

	public static final Quaternion ZERO = new Quaternion(1, 1, 1, 1);
	
	public static Quaternion add(Quaternion... quaternions) {
		float w = 0;
		float x = 0;
		float y = 0;
		float z = 0;
		
		for (Quaternion q : quaternions) {
			w += q.w;
			x += q.x;
			y += q.y;
			z += q.z;
		}
		
		return new Quaternion(w, x, y, z);
	}
	
	// COOLbeans... this is a spherical linear interpolation between two
	// Quaternions
	public static Quaternion slerp(Quaternion a, Quaternion b, float t) {
		final float epsilon = 0.00001f;
		float flip = 1;

		float cosine = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;

		if (cosine < 0) {
			cosine = -cosine;
			flip = -1;
		}

		if ((1 - cosine) < epsilon)
			return a.scale(1 - t).add(b.scale(t * flip));
		else {
			float theta = (float) Math.acos(cosine);
			float sine = (float) Math.sin(theta);
			float beta = (float) Math.sin((1 - t) * theta) / sine;
			float alpha = (float) Math.sin(t * theta) / sine * flip;

			return a.scale(beta).add(b.scale(alpha));
		}
	}

	public final float w, x, y, z;
	private final float length;
	private final float lengthSq;
	private final AxisAngle axisAngle;

	public Quaternion(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
		
		lengthSq = w * w + x * x + y * y + z * z;
		length = (float)Math.sqrt(lengthSq);

		float angle = (float)Math.acos(w) * 2.f;
		Vector axis = new Vector(x / length, y / length, z / length);			
		axisAngle = new AxisAngle(angle, axis);
	}

	public Quaternion add(Quaternion o) {
		return new Quaternion(w + o.w, x + o.x, y + o.y, z + o.z);
	}
	
	public Quaternion inverse() {
		Quaternion inverse = new Quaternion(w, -x, -y, -z);
		float scaleFactor = w*w + x*x + y*y + z*z;
		return inverse.scale(1/scaleFactor);
	}

	public float length() {
		return length;
	}

	public Quaternion multiply(Quaternion o) {
		return new Quaternion(
				w * o.w - x * o.x - y * o.y - z * o.z, 
				w * o.x	+ x * o.w + y * o.z - z * o.y, 
				w * o.y - x * o.z + y * o.w + z * o.x, 
				w * o.z + x * o.y - y * o.x + z * o.w);
	}

	public Quaternion normalize() {
		float nw = 1;
		float nx = 0;
		float ny = 0;
		float nz = 0;
		
		if (lengthSq != 0) {
			nw = w / length;
			nx = x / length;
			ny = y / length;
			nz = z / length;			
		}
		
		return new Quaternion(nw, nx, ny, nz);
	}

	// Supposedly this should rotate a vector according to the orientation of
	// this quaternion.
	// See "Pseudo-code for rotating using a quaternion" on 
	// http://en.wikipedia.org/wiki/Quaternions_and_spatial_rotation
	public Vector rotate(Vector v) {
		float t2 = w * x;
		float t3 = w * y;
		float t4 = w * z;
		float t5 = -x * x;
		float t6 = x * y;
		float t7 = x * z;
		float t8 = -y * y;
		float t9 = y * z;
		float t10 = -z * z;
		
		float nx, ny, nz;
		nx = 2 * ((t8 + t10) * v.x + (t6 - t4) * v.y + (t3 + t7) * v.z) + v.x;
		ny = 2 * ((t4 + t6) * v.x + (t5 + t10) * v.y + (t9 - t2) * v.z) + v.y;
		nz = 2 * ((t7 - t3) * v.x + (t2 + t9) * v.y + (t5 + t8) * v.z) + v.z;
		return new Vector(nx, ny, nz);
	}
	
	public Quaternion scale(float o) {
		return new Quaternion(o * w, o * x, o * y, o * z);
	}
	
	public AxisAngle toAxisAngle() {
		return axisAngle;
	}

	public Matrix toMatrix() {
		float fTx = 2.0f * x;
		float fTy = 2.0f * y;
		float fTz = 2.0f * z;
		float fTwx = fTx * w;
		float fTwy = fTy * w;
		float fTwz = fTz * w;
		float fTxx = fTx * x;
		float fTxy = fTy * x;
		float fTxz = fTz * x;
		float fTyy = fTy * y;
		float fTyz = fTz * y;
		float fTzz = fTz * z;

		return new Matrix(
				1.0f - (fTyy + fTzz), fTxy - fTwz, fTxz + fTwy, 
				fTxy + fTwz, 1.0f - (fTxx + fTzz), fTyz - fTwx, 
				fTxz - fTwy, fTyz + fTwx, 1.0f - (fTxx + fTyy));
	}
	
	public String toString() {
		return String.format("<%s, %s, %s, %s>", w, x, y, z);
	}
}
