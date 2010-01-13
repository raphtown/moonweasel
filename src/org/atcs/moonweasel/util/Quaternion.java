package org.atcs.moonweasel.util;

public class Quaternion {
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
		return (float) Math.sqrt(w * w + x * x + y * y + z * z);
	}

	public Quaternion multiply(Quaternion o) {
		return new Quaternion(
				w * o.w - x * o.x - y * o.y - z * o.z, 
				w * o.x	+ x * o.w + y * o.z - z * o.y, 
				w * o.y - x * o.z + y * o.w + z * o.x, 
				w * o.z + x * o.y - y * o.x + z * o.w);
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

	public Quaternion scale(float o) {
		return new Quaternion(o * w, o * x, o * y, o * z);
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
				1.0f - (fTyy + fTzz), 
				fTxy - fTwz, fTxz + fTwy, 
				fTxy + fTwz, 1.0f - (fTxx + fTzz), 
				fTyz - fTwx, fTxz - fTwy,
				fTyz + fTwx, 1.0f - (fTxx + fTyy));
	}
	

}
