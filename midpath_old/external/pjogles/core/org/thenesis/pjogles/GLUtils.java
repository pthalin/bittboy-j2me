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

package org.thenesis.pjogles;

/**
 * A collection of utility methods.
 *
 * @author tdinneen
 */
public final class GLUtils {
	// TOMD - get rid of clamp, just inline it

	public static int clamp(final int v, final int min, final int max) {
		if (v < min)
			return min;
		else if (v > max)
			return max;
		else
			return v;
	}

	// TOMD - get rid of clamp, just inline it

	public static float clamp(final float v, final float min, final float max) {
		if (v < min)
			return min;
		else if (v > max)
			return max;
		else
			return v;
	}

	public static float FPtoF(int fp) {
		return ((float) fp) / 65536;
	}

	public static int FtoFP(float f) {
		return (int) (f * 65536);
	}

	public static float BtoF(byte x) {
		return (float) (x & 0xff) / 255.0f;
	}
}
