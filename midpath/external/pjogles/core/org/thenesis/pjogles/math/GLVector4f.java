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
 * Represents something that has a four dimenional direction.
 *
 * @author tdinneen
 */
public final class GLVector4f {
	public float x, y, z, w;

	public GLVector4f() {
	}

	public GLVector4f(final GLVector4f v) {
		set(v);
	}

	public GLVector4f(final float x, final float y, final float z, final float w) {
		set(x, y, z, w);
	}

	public GLVector4f(final float[] v) {
		set(v);
	}

	public final void set(final GLVector4f v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}

	public final void set(final float x, final float y, final float z, final float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public final void set(final float[] v) {
		x = v[0];
		y = v[1];
		z = v[2];
		w = v[3];
	}

	public final float normalize() {
		final float dotProduct = x * x + y * y + z * z;

		if (dotProduct <= 0.0f) {
			set(0.0f, 0.0f, 0.0f, 0.0f);
			return 0.0f;
		} else if (dotProduct != 1.0f) // if len == 1.0f then we're already normalized
		//    #if 1
		//        union {
		//            unsigned int i;
		//            float f;
		//        } seed;
		//        float xy, subexp;
		//
		//        /*
		//        ** This code calculates a reciprocal square root accurate to well over
		//        ** 16 bits using Newton-Raphson approximation.
		//        **
		//        ** To calculate the seed, the shift compresses the floating-point
		//        ** range just as sqrt() does, and the subtract inverts the range
		//        ** like reciprocation does.  The constant was chosen by trial-and-error
		//        ** to minimize the maximum error of the iterated result for all values
		//        ** over the range .5 to 2.
		//        */
		//        seed.f = len;
		//        seed.i = 0x5f375a00u - (seed.i >> 1);
		//
		//        /*
		//        ** The Newton-Raphson iteration to approximate X = 1/sqrt(Y) is:
		//        **
		//        **	X[1] = .5*X[0]*(3 - Y*X[0]^2)
		//        **
		//        ** A double iteration is:
		//        **
		//        **	X[2] = .0625*X[0]*(3 - Y*X[0]^2)*[12 - (Y*X[0]^2)*(3 - Y*X[0]^2)^2]
		//        **
		//        */
		//        xy = len * seed.f * seed.f;
		//        subexp = 3.f - xy;
		//        len = .0625f * seed.f * subexp * (12.f - xy * subexp * subexp);
		//    #else
		{
			final float length = (float) Math.sqrt(dotProduct);
			final float scale = 1.0f / length;

			x *= scale;
			y *= scale;
			z *= scale;

			return dotProduct;
		} else
			return 1.0f;
		//    #endif
	}

	public final float dotProduct(final GLVector4f v) {
		return x * v.x + y * v.y + z * v.z + w * v.w;
	}

	public final String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}
}
