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
 * @author tdinneen
 */
public final class GLMath {
	public static final float RAD2DEG = (float) (180.0 / Math.PI);

	public static int logbase2(final int n) {
		final int m = n;
		int i = 1;
		int log2 = 0;

		if (n < 0)
			return -1;

		while (m > i) {
			i = 2 * i;
			log2++;
		}

		if (m != n)
			return -1;
		else
			return log2;
	}

}
