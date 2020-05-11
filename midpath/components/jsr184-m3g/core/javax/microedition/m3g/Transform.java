/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA  
 */
package javax.microedition.m3g;

import javax.microedition.khronos.opengles.GL10;

import org.thenesis.m3g.engine.vecMath.Constants;
import org.thenesis.m3g.engine.vecMath.Vector3;
import org.thenesis.m3g.engine.vecMath.Vector4;

public class Transform {

	// The matrix is stored in row-major format i.e
	//
	//  0  1  2  3
	//  4  5  6  7
	//  8  9 10 11
	// 12 13 14 15

	protected float[] m = { 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1 };

	public Transform() {
	}

	public Transform(Transform transform) {
		set(transform);
	}

	public void get(float[] matrix) {
		if (matrix == null)
			throw new NullPointerException("matrix can not be null");
		if (matrix.length < 16)
			throw new IllegalArgumentException("matrix must be of length 16");

		matrix[0] = m[0];
		matrix[1] = m[1];
		matrix[2] = m[2];
		matrix[3] = m[3];
		matrix[4] = m[4];
		matrix[5] = m[5];
		matrix[6] = m[6];
		matrix[7] = m[7];
		matrix[8] = m[8];
		matrix[9] = m[9];
		matrix[10] = m[10];
		matrix[11] = m[11];
		matrix[12] = m[12];
		matrix[13] = m[13];
		matrix[14] = m[14];
		matrix[15] = m[15];
	}

	public void set(float[] matrix) {
		if (matrix == null)
			throw new NullPointerException("matrix can not be null");
		if (matrix.length < 16)
			throw new IllegalArgumentException("matrix must be of length 16");

		m[0] = matrix[0];
		m[1] = matrix[1];
		m[2] = matrix[2];
		m[3] = matrix[3];
		m[4] = matrix[4];
		m[5] = matrix[5];
		m[6] = matrix[6];
		m[7] = matrix[7];
		m[8] = matrix[8];
		m[9] = matrix[9];
		m[10] = matrix[10];
		m[11] = matrix[11];
		m[12] = matrix[12];
		m[13] = matrix[13];
		m[14] = matrix[14];
		m[15] = matrix[15];
	}

	public void set(Transform transform) {
		if (transform == null)
			throw new NullPointerException("transform can not be null");

		transform.get(m);
	}

	public void setIdentity() {
		m[0] = 1;
		m[1] = 0;
		m[2] = 0;
		m[3] = 0;

		m[4] = 0;
		m[5] = 1;
		m[6] = 0;
		m[7] = 0;

		m[8] = 0;
		m[9] = 0;
		m[10] = 1;
		m[11] = 0;

		m[12] = 0;
		m[13] = 0;
		m[14] = 0;
		m[15] = 1;
	}

	public void transform(float[] vectors) {
		if (vectors == null)
			throw new NullPointerException("vectors can not be null");
		if ((vectors.length % 4) != 0)
			throw new IllegalArgumentException("Number of elements in vector array must be a multiple of 4");

		int l = vectors.length;
		for (int i = 0; i < l; i += 4) {
			float x = vectors[i];
			float y = vectors[i + 1];
			float z = vectors[i + 2];
			float w = vectors[i + 3];

			vectors[i] = x * m[0] + y * m[1] + z * m[2] + w * m[3];
			vectors[i + 1] = x * m[4] + y * m[5] + z * m[6] + w * m[7];
			vectors[i + 2] = x * m[8] + y * m[9] + z * m[10] + w * m[11];
			vectors[i + 3] = x * m[12] + y * m[13] + z * m[14] + w * m[15];
		}
	}

	public void transform(VertexArray in, float[] out, boolean W) {
		if (in == null)
			throw new NullPointerException("in can not be null");
		if (out == null)
			throw new NullPointerException("out can not be null");
		if (out.length < in.getVertexCount() * 4)
			throw new IllegalArgumentException("Number of elements in out array must be at least vertexCount*4");

		int cc = in.getComponentCount();
		int vc = in.getVertexCount();

		if (in.getComponentType() == 1) {
			byte[] values = new byte[vc * cc];
			in.get(0, vc, values);
			for (int i = 0, j = 0; i < vc * cc; i += cc, j += 4) {
				float x = values[i];
				float y = (cc >= 2 ? (float) values[i + 1] : 0.0f);
				float z = (cc >= 3 ? (float) values[i + 2] : 0.0f);
				float w = (cc >= 4 ? (float) values[i + 3] : (W ? 1 : 0));

				out[j] = x * m[0] + y * m[1] + z * m[2] + w * m[3];
				out[j + 1] = x * m[4] + y * m[5] + z * m[6] + w * m[7];
				out[j + 2] = x * m[8] + y * m[9] + z * m[10] + w * m[11];
				out[j + 3] = x * m[12] + y * m[13] + z * m[14] + w * m[15];
			}
		} else {
			short[] values = new short[vc * cc];
			in.get(0, vc, values);
			for (int i = 0, j = 0; i < vc * cc; i += cc, j += 4) {
				float x = values[i];
				float y = (cc >= 2 ? (float) values[i + 1] : 0.0f);
				float z = (cc >= 3 ? (float) values[i + 2] : 0.0f);
				float w = (cc >= 4 ? (float) values[i + 3] : (W ? 1 : 0));

				out[j] = x * m[0] + y * m[1] + z * m[2] + w * m[3];
				out[j + 1] = x * m[4] + y * m[5] + z * m[6] + w * m[7];
				out[j + 2] = x * m[8] + y * m[9] + z * m[10] + w * m[11];
				out[j + 3] = x * m[12] + y * m[13] + z * m[14] + w * m[15];
			}
		}
	}

		public void invert() {
			//	float det = determinant();
			//	if (det == 0)
			//		throw new ArithmeticException("This transform is not invertible");
	
			// This will only work for ON-matrices, but it's really fast! :)
			float[] n = { m[0], m[4], m[8], -m[0] * m[3] - m[4] * m[7] - m[8] * m[11], m[1], m[5], m[9],
					-m[1] * m[3] - m[5] * m[7] - m[9] * m[11], m[2], m[6], m[10],
					-m[2] * m[3] - m[6] * m[7] - m[10] * m[11], 0, 0, 0, 1 };
			set(n);
		}

//	public void invert() {
//		
//		// cf. http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
//		
//		float m00 = m[0];
//		float m01 = m[1];
//		float m02 = m[2];
//		float m03 = m[3];
//		float m10 = m[4];
//		float m11 = m[5];
//		float m12 = m[6];
//		float m13 = m[7];
//		float m20 = m[8];
//		float m21 = m[9];
//		float m22 = m[10];
//		float m23 = m[11];
//		float m30 = m[12];
//		float m31 = m[13];
//		float m32 = m[14];
//		float m33 = m[15];
//		
//		
//		float invDet = 1 / (m03 * m12 * m21 * m30 - m02 * m13 * m21 * m30 - m03 * m11 * m22 * m30 + m01 * m13 * m22
//				* m30 + m02 * m11 * m23 * m30 - m01 * m12 * m23 * m30 - m03 * m12 * m20 * m31 + m02 * m13 * m20 * m31
//				+ m03 * m10 * m22 * m31 - m00 * m13 * m22 * m31 - m02 * m10 * m23 * m31 + m00 * m12 * m23 * m31 + m03
//				* m11 * m20 * m32 - m01 * m13 * m20 * m32 - m03 * m10 * m21 * m32 + m00 * m13 * m21 * m32 + m01 * m10
//				* m23 * m32 - m00 * m11 * m23 * m32 - m02 * m11 * m20 * m33 + m01 * m12 * m20 * m33 + m02 * m10 * m21
//				* m33 - m00 * m12 * m21 * m33 - m01 * m10 * m22 * m33 + m00 * m11 * m22 * m33);
//
//		m00 = m12 * m23 * m31 - m13 * m22 * m31 + m13 * m21 * m32 - m11 * m23 * m32 - m12 * m21 * m33 + m11 * m22 * m33;
//		m01 = m03 * m22 * m31 - m02 * m23 * m31 - m03 * m21 * m32 + m01 * m23 * m32 + m02 * m21 * m33 - m01 * m22 * m33;
//		m02 = m02 * m13 * m31 - m03 * m12 * m31 + m03 * m11 * m32 - m01 * m13 * m32 - m02 * m11 * m33 + m01 * m12 * m33;
//		m03 = m03 * m12 * m21 - m02 * m13 * m21 - m03 * m11 * m22 + m01 * m13 * m22 + m02 * m11 * m23 - m01 * m12 * m23;
//		m10 = m13 * m22 * m30 - m12 * m23 * m30 - m13 * m20 * m32 + m10 * m23 * m32 + m12 * m20 * m33 - m10 * m22 * m33;
//		m11 = m02 * m23 * m30 - m03 * m22 * m30 + m03 * m20 * m32 - m00 * m23 * m32 - m02 * m20 * m33 + m00 * m22 * m33;
//		m12 = m03 * m12 * m30 - m02 * m13 * m30 - m03 * m10 * m32 + m00 * m13 * m32 + m02 * m10 * m33 - m00 * m12 * m33;
//		m13 = m02 * m13 * m20 - m03 * m12 * m20 + m03 * m10 * m22 - m00 * m13 * m22 - m02 * m10 * m23 + m00 * m12 * m23;
//		m20 = m11 * m23 * m30 - m13 * m21 * m30 + m13 * m20 * m31 - m10 * m23 * m31 - m11 * m20 * m33 + m10 * m21 * m33;
//		m21 = m03 * m21 * m30 - m01 * m23 * m30 - m03 * m20 * m31 + m00 * m23 * m31 + m01 * m20 * m33 - m00 * m21 * m33;
//		m22 = m01 * m13 * m30 - m03 * m11 * m30 + m03 * m10 * m31 - m00 * m13 * m31 - m01 * m10 * m33 + m00 * m11 * m33;
//		m23 = m03 * m11 * m20 - m01 * m13 * m20 - m03 * m10 * m21 + m00 * m13 * m21 + m01 * m10 * m23 - m00 * m11 * m23;
//		m30 = m12 * m21 * m30 - m11 * m22 * m30 - m12 * m20 * m31 + m10 * m22 * m31 + m11 * m20 * m32 - m10 * m21 * m32;
//		m31 = m01 * m22 * m30 - m02 * m21 * m30 + m02 * m20 * m31 - m00 * m22 * m31 - m01 * m20 * m32 + m00 * m21 * m32;
//		m32 = m02 * m11 * m30 - m01 * m12 * m30 - m02 * m10 * m31 + m00 * m12 * m31 + m01 * m10 * m32 - m00 * m11 * m32;
//		m33 = m01 * m12 * m20 - m02 * m11 * m20 + m02 * m10 * m21 - m00 * m12 * m21 - m01 * m10 * m22 + m00 * m11 * m22;
//
//		
//		m[0] = m00 * invDet;
//		m[1] = m01 * invDet;
//		m[2] = m02 * invDet;
//		m[3] = m03 * invDet;
//		m[4] = m10 * invDet;
//		m[5] = m11 * invDet;
//		m[6] = m12 * invDet;
//		m[7] = m13 * invDet;
//		m[8] = m20 * invDet;
//		m[9] = m21 * invDet;
//		m[10] = m22 * invDet;
//		m[11] = m23 * invDet;
//		m[12] = m30 * invDet;
//		m[13] = m31 * invDet;
//		m[14] = m32 * invDet;
//		m[15] = m33 * invDet;
//
//	}

	public void transpose() {

		transpose(m);

		//		float t = m[1];
		//		m[1] = m[4];
		//		m[4] = t;
		//
		//		t = m[2];
		//		m[2] = m[8];
		//		m[8] = t;
		//
		//		t = m[3];
		//		m[12] = m[3];
		//		m[3] = t;
		//
		//		t = m[6];
		//		m[6] = m[9];
		//		m[9] = t;
		//
		//		t = m[7];
		//		m[7] = m[13];
		//		m[13] = t;
		//
		//		t = m[11];
		//		m[11] = m[14];
		//		m[14] = t;
	}

	public void postMultiply(Transform transform) {
		if (transform == null)
			throw new NullPointerException("transform can not be null");

		float[] l = new float[16];
		get(l);
		float[] r = transform.m;

		m[0] = l[0] * r[0] + l[1] * r[4] + l[2] * r[8] + l[3] * r[12];
		m[1] = l[0] * r[1] + l[1] * r[5] + l[2] * r[9] + l[3] * r[13];
		m[2] = l[0] * r[2] + l[1] * r[6] + l[2] * r[10] + l[3] * r[14];
		m[3] = l[0] * r[3] + l[1] * r[7] + l[2] * r[11] + l[3] * r[15];

		m[4] = l[4] * r[0] + l[5] * r[4] + l[6] * r[8] + l[7] * r[12];
		m[5] = l[4] * r[1] + l[5] * r[5] + l[6] * r[9] + l[7] * r[13];
		m[6] = l[4] * r[2] + l[5] * r[6] + l[6] * r[10] + l[7] * r[14];
		m[7] = l[4] * r[3] + l[5] * r[7] + l[6] * r[11] + l[7] * r[15];

		m[8] = l[8] * r[0] + l[9] * r[4] + l[10] * r[8] + l[11] * r[12];
		m[9] = l[8] * r[1] + l[9] * r[5] + l[10] * r[9] + l[11] * r[13];
		m[10] = l[8] * r[2] + l[9] * r[6] + l[10] * r[10] + l[11] * r[14];
		m[11] = l[8] * r[3] + l[9] * r[7] + l[10] * r[11] + l[11] * r[15];

		m[12] = l[12] * r[0] + l[13] * r[4] + l[14] * r[8] + l[15] * r[12];
		m[13] = l[12] * r[1] + l[13] * r[5] + l[14] * r[9] + l[15] * r[13];
		m[14] = l[12] * r[2] + l[13] * r[6] + l[14] * r[10] + l[15] * r[14];
		m[15] = l[12] * r[3] + l[13] * r[7] + l[14] * r[11] + l[15] * r[15];
	}

	public void postRotate(float angle, float ax, float ay, float az) {
		Vector3 v = new Vector3(ax, ay, az);

		if (Math.abs(angle) < 0.000001) // TODO: use constant
			return;

		if (ax == 0 && ay == 0 && az == 0)
			throw new IllegalArgumentException("Length of rotation axis vector can not be 0.");

		v.normalize();
		ax = v.x;
		ay = v.y;
		az = v.z;

		postMultiply(getRotationFromAngleAxis(angle, ax, ay, az));
	}

	public void postRotateQuat(float qx, float qy, float qz, float qw) {
		if (qx == 0 && qy == 0 && qz == 0 && qw == 0)
			throw new IllegalArgumentException("Atleast one the components of the quaternion must be non zero.");
		Vector4 v = new Vector4(qx, qy, qz, qw);
		v.normalize();

		qx = v.x;
		qy = v.y;
		qz = v.z;
		qw = v.w;

		postMultiply(getRotationFromQuaternion(qx, qy, qz, qw));
	}

	public void postScale(float sx, float sy, float sz) {
		Transform t = new Transform();
		float[] m = t.m;
		m[0] = sx;
		m[5] = sy;
		m[10] = sz;

		postMultiply(t);
	}

	public void postTranslate(float tx, float ty, float tz) {
		Transform t = new Transform();
		float[] m = t.m;
		m[3] = tx;
		m[7] = ty;
		m[11] = tz;

		postMultiply(t);
	}

	private Transform getRotationFromQuaternion(float x, float y, float z, float w) {
		Transform t = new Transform();
		float[] m = t.m;

		float xx = 2 * x * x;
		float yy = 2 * y * y;
		float zz = 2 * z * z;
		float xy = 2 * x * y;
		float xz = 2 * x * z;
		float xw = 2 * x * w;
		float yz = 2 * y * z;
		float yw = 2 * y * w;
		float zw = 2 * z * w;

		m[0] = 1 - yy + zz;
		m[1] = xy - zw;
		m[2] = xz + yw;

		m[4] = xy + zw;
		m[5] = 1 - xx + zz;
		m[6] = yz - xw;

		m[8] = xz - yw;
		m[9] = yz + xw;
		m[10] = 1 - xx + yy;

		return t;
	}

	private Transform getRotationFromAngleAxis(float angle, float ax, float ay, float az) {
		Transform t = new Transform();
		float[] m = t.m;
		float c = (float) Math.cos(angle * Constants.TO_RADIANS);
		float s = (float) Math.sin(angle * Constants.TO_RADIANS);
		float nC = 1 - c;

		float xy = ax * ay;
		float yz = ay * az;
		float xz = ax * az;

		float xs = ax * s;
		float zs = az * s;
		float ys = ay * s;

		m[0] = ax * ax * nC + c;
		m[1] = xy * nC - zs;
		m[2] = xz * nC + ys;

		m[4] = xy * nC + zs;
		m[5] = ay * ay * nC + c;
		m[6] = yz * nC - xs;

		m[8] = xz * nC - ys;
		m[9] = yz * nC + xs;
		m[10] = az * az * nC + c;

		return t;
	}

	void setGL(GL10 gl) {
		float[] matrix = new float[16];
		System.arraycopy(m, 0, matrix, 0, 16);
		transpose(matrix);
		//System.out.println("setGL: " + this);
		gl.glLoadMatrixf(matrix, 0);

		//gl.glLoadTransposeMatrixf(m, 0);
	}

	void multGL(GL10 gl) {
		float[] matrix = new float[16];
		System.arraycopy(m, 0, matrix, 0, 16);
		transpose(matrix);
		//System.out.println("multGL: " + this);
		gl.glMultMatrixf(matrix, 0);
		
		//gl.glMultTransposeMatrixf(m, 0);
	}

	private void transpose(float[] matrix) {
		float t = matrix[1];
		matrix[1] = matrix[4];
		matrix[4] = t;

		t = matrix[2];
		matrix[2] = matrix[8];
		matrix[8] = t;

		t = matrix[3];
		matrix[3] = matrix[12];
		matrix[12] = t;

		t = matrix[6];
		matrix[6] = matrix[9];
		matrix[9] = t;

		t = matrix[7];
		matrix[7] = matrix[13];
		matrix[13] = t;

		t = matrix[11];
		matrix[11] = matrix[14];
		matrix[14] = t;
	}

	//	void getGL(GL11 gl, int matrixMode) {
	//		gl.glGetFloatv(matrixMode, m, 0);
	//		transpose();
	//	}

	public String toString() {
		String ret = "{";
		for (int i = 0; i < 16; ++i) {
			if ((i % 4) == 0 && i > 0)
				ret += "\n ";
			ret += m[i] + ", ";
		}
		return ret + "}";
	}
}