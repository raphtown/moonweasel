package org.atcs.moonweasel.util;

//a 4x4 matrix class that should do pretty much everything useful
//convention is row-col ordering

public class Matrix 
{
	public float m11, m12, m13, m14, m21, m22, m23, m24, m31, m32, m33, m34, m41, m42, m43, m44;

	public Matrix()
	{
		this(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	// construct a matrix from explicit values for the 3x3 sub matrix.
	// note: the rest of the matrix (row 4 and column 4 are set to identity)
	
	public Matrix(float m11, float m12, float m13, float m21, float m22, float m23, float m31, float m32, float m33)
	{
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m14 = 0;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m24 = 0;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
		this.m34 = 0;
		this.m41 = 0;
		this.m42 = 0;
		this.m43 = 0;
		this.m44 = 1;
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
	}

	public void setToZero()
	{
		m11 = 0;
		m12 = 0;
		m13 = 0;
		m14 = 0;
		m21 = 0;
		m22 = 0;
		m23 = 0;
		m24 = 0;
		m31 = 0;
		m32 = 0;
		m33 = 0;
		m34 = 0;
		m41 = 0;
		m42 = 0;
		m43 = 0;
		m44 = 0;
	}

	// set matrix to identity.

	public void setToIdentity()
	{
		m11 = 1;
		m12 = 0;
		m13 = 0;
		m14 = 0;
		m21 = 0;
		m22 = 1;
		m23 = 0;
		m24 = 0;
		m31 = 0;
		m32 = 0;
		m33 = 1;
		m34 = 0;
		m41 = 0;
		m42 = 0;
		m43 = 0;
		m44 = 1;
	}

	// set to a translation matrix.

	public void setAsTranslation(Vector v)
	{
		m11 = 1;		  // 1 0 0 x 
		m12 = 0;		  // 0 1 0 y
		m13 = 0;		  // 0 0 1 z
		m14 = v.x;   	  // 0 0 0 1
		m21 = 0;
		m22 = 1;
		m23 = 0;
		m24 = v.y;
		m31 = 0;
		m32 = 0;
		m33 = 1;
		m34 = v.z;
		m41 = 0;
		m42 = 0;
		m43 = 0;
		m44 = 1;
	}

	// calculate determinant of 3x3 sub matrix.

	public float determinant()
	{
		return -m13*m22*m31 + m12*m23*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33;
	}

	// determine if matrix is invertible.
	// note: currently only checks 3x3 sub matrix determinant.

	public boolean invertible()
	{
		return (this.determinant() != 0);
	}

	// calculate inverse of matrix

	public Matrix inverse()
	{
		Matrix returnMat = new Matrix();
		float determinant = this.determinant();

		if(invertible())
		{

			float k = 1.0f / determinant;

			returnMat.m11 = (m22*m33 - m32*m23) * k;
			returnMat.m12 = (m32*m13 - m12*m33) * k;
			returnMat.m13 = (m12*m23 - m22*m13) * k;
			returnMat.m21 = (m23*m31 - m33*m21) * k;
			returnMat.m22 = (m33*m11 - m13*m31) * k;
			returnMat.m23 = (m13*m21 - m23*m11) * k;
			returnMat.m31 = (m21*m32 - m31*m22) * k;
			returnMat.m32 = (m31*m12 - m11*m32) * k;
			returnMat.m33 = (m11*m22 - m21*m12) * k;

			returnMat.m14 = -(returnMat.m11*m14 + returnMat.m12*m24 + returnMat.m13*m34);
			returnMat.m24 = -(returnMat.m21*m14 + returnMat.m22*m24 + returnMat.m23*m34);
			returnMat.m34 = -(returnMat.m31*m14 + returnMat.m32*m24 + returnMat.m33*m34);

			returnMat.m41 = m41;
			returnMat.m42 = m42;
			returnMat.m43 = m43;
			returnMat.m44 = m44;

			return returnMat;
		}
		
		else
		{
			returnMat.setToIdentity(); 
			//NOTE!!!
			//WARNING WARNING WARNING!!! 
			//THIS IS BAD!!
			//SHOULD THROW AN ERROR BACK TO THE THING THAT CALLS IT
			return returnMat;
		}
			
	}


	// calculate transpose of matrix and write to parameter matrix.

	public Matrix transpose()
	{
		Matrix transpose = new Matrix();
		transpose.m11 = m11;
		transpose.m12 = m21;
		transpose.m13 = m31;
		transpose.m14 = m41;
		transpose.m21 = m12;
		transpose.m22 = m22;
		transpose.m23 = m32;
		transpose.m24 = m42;
		transpose.m31 = m13;
		transpose.m32 = m23;
		transpose.m33 = m33;
		transpose.m34 = m43;
		transpose.m41 = m14;
		transpose.m42 = m24;
		transpose.m43 = m34;
		transpose.m44 = m44;
		return transpose;
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

	public Matrix mtm(Matrix matrix)
	{
		Matrix result = new Matrix();
		result.m11 = m11*matrix.m11 + m12*matrix.m21 + m13*matrix.m31 + m14*matrix.m41;
		result.m12 = m11*matrix.m12 + m12*matrix.m22 + m13*matrix.m32 + m14*matrix.m42;
		result.m13 = m11*matrix.m13 + m12*matrix.m23 + m13*matrix.m33 + m14*matrix.m43;
		result.m14 = m11*matrix.m14 + m12*matrix.m24 + m13*matrix.m34 + m14*matrix.m44;
		result.m21 = m21*matrix.m11 + m22*matrix.m21 + m23*matrix.m31 + m24*matrix.m41;
		result.m22 = m21*matrix.m12 + m22*matrix.m22 + m23*matrix.m32 + m24*matrix.m42;
		result.m23 = m21*matrix.m13 + m22*matrix.m23 + m23*matrix.m33 + m24*matrix.m43;
		result.m24 = m21*matrix.m14 + m22*matrix.m24 + m23*matrix.m34 + m24*matrix.m44;
		result.m31 = m31*matrix.m11 + m32*matrix.m21 + m33*matrix.m31 + m34*matrix.m41;
		result.m32 = m31*matrix.m12 + m32*matrix.m22 + m33*matrix.m32 + m34*matrix.m42;
		result.m33 = m31*matrix.m13 + m32*matrix.m23 + m33*matrix.m33 + m34*matrix.m43;
		result.m34 = m31*matrix.m14 + m32*matrix.m24 + m33*matrix.m34 + m34*matrix.m44;
		result.m41 = m41*matrix.m11 + m42*matrix.m21 + m43*matrix.m31 + m44*matrix.m41;
		result.m42 = m41*matrix.m12 + m42*matrix.m22 + m43*matrix.m32 + m44*matrix.m42;
		result.m43 = m41*matrix.m13 + m42*matrix.m23 + m43*matrix.m33 + m44*matrix.m43;
		result.m44 = m41*matrix.m14 + m42*matrix.m24 + m43*matrix.m34 + m44*matrix.m44;
		return result;
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
	
	public Vector getOrientation()
	{
		return new Vector(m11+m21+m31, m12+m22+m32, m13+m23+m33).normalize();
	}
	
	public Matrix clone()
	{
		return new Matrix(m11, m12, m13, m14, m21, m22, m23, m24, m31, m32, m33, m34, m41, m42, m43, m44);
	}
	
}
