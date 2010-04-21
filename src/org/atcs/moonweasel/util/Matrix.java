package org.atcs.moonweasel.util;

import org.atcs.moonweasel.physics.ConvexHull.Projection;
import org.atcs.moonweasel.util.Vector.Direction;

//a 4x4 matrix class that should do pretty much everything useful
//convention is row-col ordering

public class Matrix 
{
	public static final Matrix IDENTITY =
		new Matrix(1, 0, 0, 0,
				   0, 1, 0, 0,
				   0, 0, 1, 0,
				   0, 0, 0, 1);

	public static final Matrix ZERO = 
		new Matrix(0, 0, 0, 0,
				   0, 0, 0, 0,
				   0, 0, 0, 0,
				   0, 0, 0, 0);
		
	private final float m11, m12, m13, m14, m21, m22, m23, m24, m31, m32, m33, m34, m41, m42, m43, m44;
	private final float determinant;
	
	// construct a matrix from explicit values for the 3x3 sub matrix.
	// note: the rest of the matrix (row 4 and column 4 are set to identity)
	public Matrix(float m11, float m12, float m13, 
				  float m21, float m22, float m23, 
				  float m31, float m32, float m33)
	{
		this(m11, m12, m13, 0,
			 m21, m22, m23, 0,
			 m31, m32, m33, 0,
			 0,   0,   0,   1);
	}
	
	public Matrix(Vector translation) {
		this(1, 0, 0, translation.x,
			 0, 1, 0, translation.y,
			 0, 0, 1, translation.z,
			 0, 0, 0, 1);
	}

	// construct a matrix from explicit entry values for the whole 4x4 matrix.
	public Matrix(float m11, float m12, float m13, float m14,
			float m21, float m22, float m23, float m24,
			float m31, float m32, float m33, float m34,
			float m41, float m42, float m43, float m44)
	{
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m14 = m14;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m24 = m24;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
		this.m34 = m34;
		this.m41 = m41;
		this.m42 = m42;
		this.m43 = m43;
		this.m44 = m44;
		
		this.determinant = calcDeterminant();
	}

	// calculate determinant of 3x3 sub matrix.
	private float calcDeterminant()
	{
		return -m13*m22*m31 + m12*m23*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33;
	}

	public float determinant() {
		return determinant;
	}
	
	// determine if matrix is invertible.
	// note: currently only checks 3x3 sub matrix determinant.

	public boolean invertible()
	{
		return determinant != 0;
	}

	// calculate inverse of matrix

	public Matrix inverse()
	{
		if (determinant == 0) 
			return null;
		
		float k = 1.0f / determinant;

		float nm11 = (m22*m33 - m32*m23) * k;
		float nm12 = (m32*m13 - m12*m33) * k;
		float nm13 = (m12*m23 - m22*m13) * k;
		float nm21 = (m23*m31 - m33*m21) * k;
		float nm22 = (m33*m11 - m13*m31) * k;
		float nm23 = (m13*m21 - m23*m11) * k;
		float nm31 = (m21*m32 - m31*m22) * k;
		float nm32 = (m31*m12 - m11*m32) * k;
		float nm33 = (m11*m22 - m21*m12) * k;

		float nm14 = -(nm11*m14 + nm12*m24 + nm13*m34);
		float nm24 = -(nm21*m14 + nm22*m24 + nm23*m34);
		float nm34 = -(nm31*m14 + nm32*m24 + nm33*m34);

		float nm41 = m41;
		float nm42 = m42;
		float nm43 = m43;
		float nm44 = m44;

		return new Matrix(nm11, nm12, nm13, nm14,
						  nm21, nm22, nm23, nm24,
						  nm31, nm32, nm33, nm34,
						  nm41, nm42, nm43, nm44);
	}

	// add another matrix to this matrix.
	public Matrix add(Matrix o)
	{
		return new Matrix(m11 + o.m11, m12 + o.m12, m13 + o.m13, m14 + o.m14, 
				m21 + o.m21, m22 + o.m22, m23 + o.m23, m24 + o.m24,
				m31 + o.m31, m32 + o.m32, m33 + o.m33, m34 + o.m34,
				m41 + o.m41, m42 + o.m42, m43 + o.m43, m44 + o.m44);
		
	}

	// subtract a matrix from this matrix.
	public Matrix subtract(Matrix o)
	{
		return new Matrix(m11 - o.m11, m12 - o.m12, m13 - o.m13, m14 - o.m14, 
				m21 - o.m21, m22 - o.m22, m23 - o.m23, m24 - o.m24,
				m31 - o.m31, m32 - o.m32, m33 - o.m33, m34 - o.m34,
				m41 - o.m41, m42 - o.m42, m43 - o.m43, m44 - o.m44);
		
	}

	// multiply this matrix by a scalar.
	public Matrix scale(float s)
	{
		return new Matrix(m11*s, m12*s, m13*s, m14*s, 
				m21*s, m22*s, m23*s, m24*s,
				m31*s, m32*s, m33*s, m34*s,
				m41*s, m42*s, m43*s, m44*s);
	}

	// matrix times matrix
	public Matrix multiply(Matrix matrix)
	{
		float nm11 = m11*matrix.m11 + m12*matrix.m21 + m13*matrix.m31 + m14*matrix.m41;
		float nm12 = m11*matrix.m12 + m12*matrix.m22 + m13*matrix.m32 + m14*matrix.m42;
		float nm13 = m11*matrix.m13 + m12*matrix.m23 + m13*matrix.m33 + m14*matrix.m43;
		float nm14 = m11*matrix.m14 + m12*matrix.m24 + m13*matrix.m34 + m14*matrix.m44;
		float nm21 = m21*matrix.m11 + m22*matrix.m21 + m23*matrix.m31 + m24*matrix.m41;
		float nm22 = m21*matrix.m12 + m22*matrix.m22 + m23*matrix.m32 + m24*matrix.m42;
		float nm23 = m21*matrix.m13 + m22*matrix.m23 + m23*matrix.m33 + m24*matrix.m43;
		float nm24 = m21*matrix.m14 + m22*matrix.m24 + m23*matrix.m34 + m24*matrix.m44;
		float nm31 = m31*matrix.m11 + m32*matrix.m21 + m33*matrix.m31 + m34*matrix.m41;
		float nm32 = m31*matrix.m12 + m32*matrix.m22 + m33*matrix.m32 + m34*matrix.m42;
		float nm33 = m31*matrix.m13 + m32*matrix.m23 + m33*matrix.m33 + m34*matrix.m43;
		float nm34 = m31*matrix.m14 + m32*matrix.m24 + m33*matrix.m34 + m34*matrix.m44;
		float nm41 = m41*matrix.m11 + m42*matrix.m21 + m43*matrix.m31 + m44*matrix.m41;
		float nm42 = m41*matrix.m12 + m42*matrix.m22 + m43*matrix.m32 + m44*matrix.m42;
		float nm43 = m41*matrix.m13 + m42*matrix.m23 + m43*matrix.m33 + m44*matrix.m43;
		float nm44 = m41*matrix.m14 + m42*matrix.m24 + m43*matrix.m34 + m44*matrix.m44;
		return new Matrix(nm11, nm12, nm13, nm14,
				  nm21, nm22, nm23, nm24,
				  nm31, nm32, nm33, nm34,
				  nm41, nm42, nm43, nm44);
	}

	//vector transformation (used in the case of inertia-tensor times angular momentum)
	
	public Vector transform(Vector v)
	{
		float rx = v.x * m11 + v.y * m12 + v.z * m13 + m14;
		float ry = v.x * m21 + v.y * m22 + v.z * m23 + m24;
		float rz = v.x * m31 + v.y * m32 + v.z * m33 + m34;
		Vector returnVec = new Vector(rx, ry, rz);
		return returnVec;
	}
	
	public Vector transform(Vector v, Projection p) {
		float x = p.get(Direction.X).get(v);
		float y = p.get(Direction.Y).get(v);
		float z = p.get(Direction.Z).get(v);
		float rx = x * m11 + y * m12 + z * m13 + m14;
		float ry = x * m21 + y * m22 + z * m23 + m24;
		float rz = x * m31 + y * m32 + z * m33 + m34;
		Vector returnVec = new Vector(rx, ry, rz);
		return returnVec;		
	}

	public Vector getOrientation()
	{
		return new Vector(m11+m21+m31, m12+m22+m32, m13+m23+m33).normalize();
	}
	
	public String toString() {
		return String.format(
			"[%s, %s, %s, %s,\n" +
			" %s, %s, %s, %s,\n" +
			" %s, %s, %s, %s,\n" +
			" %s, %s, %s, %s]",
			m11, m12, m13, m14,
			m21, m22, m23, m24,
			m31, m32, m33, m34,
			m41, m42, m43, m44);
	}
}
