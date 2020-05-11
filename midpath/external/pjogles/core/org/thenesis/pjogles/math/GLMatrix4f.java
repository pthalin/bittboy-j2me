/*
 * PJOGLES - Copyright (C) 2008 Guillaume Legris, Mathieu Legris
 * 
 * OGLJava - Copyright (C) 2004 Tom Dinneen
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

package org.thenesis.pjogles.math;

/**
 * A 4x4 matrix for representing transformations.
 *
 * @author tdinneen
 */
public final class GLMatrix4f {
	public final float[] m = new float[16]; // TOMD - should be 16 floats, no indexing faster !
	private static final float degrees2radians = (float) Math.PI / 180.0f;

	public GLMatrix4f() {
		setIdentity();
	}

	public GLMatrix4f(final float[] f) {
		set(f);
	}

	public final void set(final GLVector4f x, final GLVector4f y, final GLVector4f z) {
		m[0] = x.x;
		m[4] = x.y;
		m[8] = x.z;
		m[12] = x.w;

		m[1] = y.x;
		m[5] = y.y;
		m[9] = y.z;
		m[13] = y.w;

		m[2] = z.x;
		m[6] = z.y;
		m[10] = z.z;
		m[14] = z.w;

		m[3] = 0.0f;
		m[7] = 0.0f;
		m[11] = 0.0f;
		m[15] = 1.0f;
	}

	public final void set(final GLMatrix4f m) {
		set(m.m);
	}

	public final void set(final float[] m) {
		System.arraycopy(m, 0, this.m, 0, 16);
	}

	public final void get(final float[] m) {
		System.arraycopy(this.m, 0, m, 0, 16);
	}

	public final void setIdentity() {
		m[0] = 1.0f;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;
		m[4] = 0.0f;
		m[5] = 1.0f;
		m[6] = 0.0f;
		m[7] = 0.0f;
		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = 1.0f;
		m[11] = 0.0f;
		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	/*
	 ** d = v x m
	 */
	public static void transform(final GLVector4f v, final GLMatrix4f m, final GLVector4f d) {
		transform(v, m.m, d);
	}

	/*
	 ** d = v x m
	 */
	public static void transform(final GLVector4f v, final float[] m, final GLVector4f d) {
		final float x = v.x;
		final float y = v.y;
		final float z = v.z;
		final float w = v.w;

		final float f1 = m[0] * x + m[4] * y + m[8] * z + m[12] * w;
		final float f2 = m[1] * x + m[5] * y + m[9] * z + m[13] * w;
		final float f3 = m[2] * x + m[6] * y + m[10] * z + m[14] * w;
		d.w = m[3] * x + m[7] * y + m[11] * z + m[15] * w;
		d.z = f3;
		d.y = f2;
		d.x = f1;
	}

	/*
	 ** m = m1 x m2
	 */
	public static void multiply(final GLMatrix4f m1, final GLMatrix4f m2, final GLMatrix4f m) {
		multiply(m1.m, m2.m, m.m);
	}

	/*
	 ** m = m1 x m2
	 */
	public static void multiply(final float[] m1, final float[] m2, final GLMatrix4f m) {
		multiply(m1, m2, m.m);
	}

	/*
	 ** m = m1 x m2
	 */
	public static void multiply(final float[] m1, final float[] m2, final float[] m) {
		final float f0 = m1[0] * m2[0] + m1[1] * m2[4] + m1[2] * m2[8] + m1[3] * m2[12];
		final float f1 = m1[0] * m2[1] + m1[1] * m2[5] + m1[2] * m2[9] + m1[3] * m2[13];
		final float f2 = m1[0] * m2[2] + m1[1] * m2[6] + m1[2] * m2[10] + m1[3] * m2[14];
		final float f3 = m1[0] * m2[3] + m1[1] * m2[7] + m1[2] * m2[11] + m1[3] * m2[15];
		final float f4 = m1[4] * m2[0] + m1[5] * m2[4] + m1[6] * m2[8] + m1[7] * m2[12];
		final float f5 = m1[4] * m2[1] + m1[5] * m2[5] + m1[6] * m2[9] + m1[7] * m2[13];
		final float f6 = m1[4] * m2[2] + m1[5] * m2[6] + m1[6] * m2[10] + m1[7] * m2[14];
		final float f7 = m1[4] * m2[3] + m1[5] * m2[7] + m1[6] * m2[11] + m1[7] * m2[15];
		final float f8 = m1[8] * m2[0] + m1[9] * m2[4] + m1[10] * m2[8] + m1[11] * m2[12];
		final float f9 = m1[8] * m2[1] + m1[9] * m2[5] + m1[10] * m2[9] + m1[11] * m2[13];
		final float f10 = m1[8] * m2[2] + m1[9] * m2[6] + m1[10] * m2[10] + m1[11] * m2[14];
		final float f11 = m1[8] * m2[3] + m1[9] * m2[7] + m1[10] * m2[11] + m1[11] * m2[15];
		final float f12 = m1[12] * m2[0] + m1[13] * m2[4] + m1[14] * m2[8] + m1[15] * m2[12];
		final float f13 = m1[12] * m2[1] + m1[13] * m2[5] + m1[14] * m2[9] + m1[15] * m2[13];
		final float f14 = m1[12] * m2[2] + m1[13] * m2[6] + m1[14] * m2[10] + m1[15] * m2[14];
		final float f15 = m1[12] * m2[3] + m1[13] * m2[7] + m1[14] * m2[11] + m1[15] * m2[15];

		m[0] = f0;
		m[1] = f1;
		m[2] = f2;
		m[3] = f3;
		m[4] = f4;
		m[5] = f5;
		m[6] = f6;
		m[7] = f7;
		m[8] = f8;
		m[9] = f9;
		m[10] = f10;
		m[11] = f11;
		m[12] = f12;
		m[13] = f13;
		m[14] = f14;
		m[15] = f15;
	}

	public final void setTranslate(final float x, final float y, final float z) {
		m[0] = 1.0f;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;
		m[4] = 0.0f;
		m[5] = 1.0f;
		m[6] = 0.0f;
		m[7] = 0.0f;
		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = 1.0f;
		m[11] = 0.0f;
		m[12] = x;
		m[13] = y;
		m[14] = z;
		m[15] = 1.0f;
	}

	public final void setScale(final float x, final float y, final float z) {
		m[0] = x;
		m[1] = 0.0f;
		m[2] = 0.0f;
		m[3] = 0.0f;
		m[4] = 0.0f;
		m[5] = y;
		m[6] = 0.0f;
		m[7] = 0.0f;
		m[8] = 0.0f;
		m[9] = 0.0f;
		m[10] = z;
		m[11] = 0.0f;
		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	public final void setRotate(final float angle, float x, float y, float z) {
		// make sure vector is normalised

		float len = x * x + y * y + z * z;

		if (len <= 0.0f) {
			x = 0.0f;
			y = 0.0f;
			z = 0.0f;
		} else if (len != 1.0f) // if len == 1.0f then we're already normalized
		{
			len = 1.0f / (float) Math.sqrt(len);

			x *= len;
			y *= len;
			z *= len;
		}

		final float radians = angle * degrees2radians;

		// TOMD - sine / cos lookup table

		final float sine = (float) Math.sin(radians);
		final float cosine = (float) Math.cos(radians);

		final float oneMinusCosine = 1.0f - cosine;

		final float xy = x * y * oneMinusCosine;
		final float yz = y * z * oneMinusCosine;
		final float zx = z * x * oneMinusCosine;

		float t = x * x;
		float blahMultipliedBySine = x * sine;
		m[0] = t + cosine * (1.0f - t);
		m[9] = yz - blahMultipliedBySine;
		m[6] = yz + blahMultipliedBySine;

		t = y * y;
		blahMultipliedBySine = y * sine;
		m[5] = t + cosine * (1.0f - t);
		m[8] = zx + blahMultipliedBySine;
		m[2] = zx - blahMultipliedBySine;

		t = z * z;
		blahMultipliedBySine = z * sine;
		m[10] = t + cosine * (1.0f - t);
		m[4] = xy - blahMultipliedBySine;
		m[1] = xy + blahMultipliedBySine;

		m[3] = 0.0f;
		m[7] = 0.0f;
		m[11] = 0.0f;
		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;
	}

	public static void invertTransposeMatrix(final GLMatrix4f m, final GLMatrix4f i) {
		invertTransposeMatrix(m.m, i.m);
	}

	public static void invertTransposeMatrix(final float[] m, final float[] i) {
		float x00, x01, x02;
		float x10, x11, x12;
		float x20, x21, x22;
		float rcp;

		float x30, x31, x32;
		float y01, y02, y03, y12, y13, y23;
		final float z02;
		final float z03;
		final float z12;
		final float z13;
		final float z22;
		final float z23;
		final float z32;
		final float z33;

		/* read 1st two columns of matrix into registers */
		x00 = m[0];
		x01 = m[1];
		x10 = m[4];
		x11 = m[5];
		x20 = m[8];
		x21 = m[9];
		x30 = m[12];
		x31 = m[13];

		/* compute all six 2x2 determinants of 1st two columns */
		y01 = x00 * x11 - x10 * x01;
		y02 = x00 * x21 - x20 * x01;
		y03 = x00 * x31 - x30 * x01;
		y12 = x10 * x21 - x20 * x11;
		y13 = x10 * x31 - x30 * x11;
		y23 = x20 * x31 - x30 * x21;

		/* read 2nd two columns of matrix into registers */
		x02 = m[2];
		x01 = m[3];
		x12 = m[6];
		x11 = m[7];
		x22 = m[10];
		x21 = m[11];
		x32 = m[14];
		x31 = m[15];

		/* compute all 3x3 cofactors for 2nd two columns */
		z33 = x02 * y12 - x12 * y02 + x22 * y01;
		z23 = x12 * y03 - x32 * y01 - x02 * y13;
		z13 = x02 * y23 - x22 * y03 + x32 * y02;
		z03 = x22 * y13 - x32 * y12 - x12 * y23;
		z32 = x11 * y02 - x21 * y01 - x01 * y12;
		z22 = x01 * y13 - x11 * y03 + x31 * y01;
		z12 = x21 * y03 - x31 * y02 - x01 * y23;
		z02 = x11 * y23 - x21 * y13 + x31 * y12;

		/* compute all six 2x2 determinants of 2nd two columns */
		y01 = x02 * x11 - x12 * x01;
		y02 = x02 * x21 - x22 * x01;
		y03 = x02 * x31 - x32 * x01;
		y12 = x12 * x21 - x22 * x11;
		y13 = x12 * x31 - x32 * x11;
		y23 = x22 * x31 - x32 * x21;

		/* read 1st two columns of matrix into registers */
		x00 = m[0];
		x01 = m[1];
		x10 = m[4];
		x11 = m[5];
		x20 = m[8];
		x21 = m[9];
		x30 = m[12];
		x31 = m[13];

		/* compute all 3x3 cofactors for 1st column */
		x32 = x11 * y02 - x21 * y01 - x01 * y12;
		x22 = x01 * y13 - x11 * y03 + x31 * y01;
		x12 = x21 * y03 - x31 * y02 - x01 * y23;
		x02 = x11 * y23 - x21 * y13 + x31 * y12;

		/* compute 4x4 determinant & its reciprocal */
		rcp = x30 * x32 + x20 * x22 + x10 * x12 + x00 * x02;

		if (rcp == 0.0f)
			return;

		rcp = 1.0f / rcp;

		/* compute all 3x3 cofactors for 2nd column */
		x31 = x00 * y12 - x10 * y02 + x20 * y01;
		x21 = x10 * y03 - x30 * y01 - x00 * y13;
		x11 = x00 * y23 - x20 * y03 + x30 * y02;
		x01 = x20 * y13 - x30 * y12 - x10 * y23;

		/* multiply all 3x3 cofactors by reciprocal */
		i[0] = x02 * rcp;
		i[1] = x01 * rcp;
		i[4] = x12 * rcp;
		i[2] = z02 * rcp;
		i[8] = x22 * rcp;
		i[3] = z03 * rcp;
		i[12] = x32 * rcp;
		i[5] = x11 * rcp;
		i[6] = z12 * rcp;
		i[9] = x21 * rcp;
		i[7] = z13 * rcp;
		i[13] = x31 * rcp;
		i[10] = z22 * rcp;
		i[11] = z23 * rcp;
		i[14] = z32 * rcp;
		i[15] = z33 * rcp;
	}

	public final boolean invert(final GLMatrix4f m) {
		return invert(m.m);
	}

	public final boolean invert(final float[] m) {
		if (this.m == m)
			throw new GLMathException("GLMatrix4f.invert(float[]) - the supplied float[] must not be this.m");

		adjoint44(this.m, m);

		final float d = det44(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8], m[9], m[10], m[11], m[12], m[13],
				m[14], m[15]);

		if (d != 0.0f) {
			final float s = 1.0f / d;

			for (int i = 16; --i >= 0;)
				m[i] *= s;

			return true;
		} else
			return false;
	}

	public final String toString() {
		return m[0] + ", " + m[1] + ", " + m[2] + ", " + m[3] + "\n" + m[4] + ", " + m[5] + ", " + m[6] + ", " + m[7]
				+ "\n" + m[8] + ", " + m[9] + ", " + m[10] + ", " + m[11] + "\n" + m[12] + ", " + m[13] + ", " + m[14]
				+ ", " + m[15] + "\n";
	}

	private static float det22(final float a, final float b, final float c, final float d) {
		return (a * d - b * c);
	}

	private static float det33(final float a1, final float a2, final float a3, final float b1, final float b2,
			final float b3, final float c1, final float c2, final float c3) {
		return (a1 * det22(b2, b3, c2, c3) - b1 * det22(a2, a3, c2, c3) + c1 * det22(a2, a3, b2, b3));
	}

	private static float det44(final float a1, final float a2, final float a3, final float a4, final float b1,
			final float b2, final float b3, final float b4, final float c1, final float c2, final float c3,
			final float c4, final float d1, final float d2, final float d3, final float d4) {
		return (a1 * det33(b2, b3, b4, c2, c3, c4, d2, d3, d4) - b1 * det33(a2, a3, a4, c2, c3, c4, d2, d3, d4) + c1
				* det33(a2, a3, a4, b2, b3, b4, d2, d3, d4) - d1 * det33(a2, a3, a4, b2, b3, b4, c2, c3, c4));
	}

	private static void adjoint44(final float[] a, final float[] m) {
		m[0] = det33(a[5], a[6], a[7], a[9], a[10], a[11], a[13], a[14], a[15]);
		m[1] = -det33(a[4], a[6], a[7], a[8], a[10], a[11], a[12], a[14], a[15]);
		m[2] = det33(a[4], a[5], a[7], a[8], a[9], a[11], a[12], a[13], a[15]);
		m[3] = -det33(a[4], a[5], a[6], a[8], a[9], a[10], a[12], a[13], a[14]);
		m[4] = -det33(a[1], a[2], a[3], a[9], a[10], a[11], a[13], a[14], a[15]);
		m[5] = det33(a[0], a[2], a[3], a[8], a[10], a[11], a[12], a[14], a[15]);
		m[6] = -det33(a[0], a[1], a[3], a[8], a[9], a[10], a[12], a[13], a[15]);
		m[7] = det33(a[0], a[1], a[2], a[8], a[9], a[10], a[12], a[13], a[14]);
		m[9] = -det33(a[0], a[2], a[3], a[4], a[6], a[7], a[12], a[14], a[15]);
		m[10] = det33(a[0], a[1], a[3], a[4], a[5], a[7], a[12], a[13], a[15]);
		m[11] = -det33(a[0], a[1], a[2], a[4], a[5], a[6], a[12], a[13], a[14]);
		m[12] = -det33(a[1], a[2], a[3], a[5], a[6], a[7], a[9], a[10], a[11]);
		m[13] = det33(a[0], a[2], a[3], a[4], a[6], a[7], a[8], a[10], a[11]);
		m[14] = -det33(a[0], a[1], a[3], a[4], a[5], a[7], a[8], a[9], a[11]);
		m[15] = det33(a[0], a[1], a[2], a[4], a[5], a[6], a[8], a[9], a[10]);
	}

	public static void main(final String[] args) {
		final GLMatrix4f rotate = new GLMatrix4f();
		rotate.setRotate(12.0f, 32.0f, -6.0f, 5.0f);

		final GLMatrix4f translate = new GLMatrix4f();
		translate.setTranslate(32.0f, -6.0f, 5.0f);

		GLMatrix4f.multiply(translate, rotate, rotate);
		final GLMatrix4f inverse = new GLMatrix4f();

		GLMatrix4f.invertTransposeMatrix(rotate, inverse);

		System.out.println(rotate.toString());
		System.out.println(inverse.toString());
	}
}
