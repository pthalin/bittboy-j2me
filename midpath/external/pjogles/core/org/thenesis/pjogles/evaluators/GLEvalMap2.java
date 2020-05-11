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

package org.thenesis.pjogles.evaluators;

/**
 * @author tdinneen
 */
public final class GLEvalMap2 {
	public final int dimension;
	public int majorOrder, minorOrder; // GL_ORDER: 2D map orders
	public float u1, u2, v1, v2; // GL_DOMAIN: 2D domain endpoints
	public float points[][][]; // GL_COEFF: 2D control points

	public GLEvalMap2(final int dimension) {
		this.dimension = dimension;
		this.majorOrder = 1;
		this.minorOrder = 1;
		this.u1 = 0.0f;
		this.u2 = 1.0f;
		this.v1 = 0.0f;
		this.v2 = 1.0f;
	}

	public final void fillMap2f(final int uOrder, final int vOrder, final int uStride, final int vStride,
			final float[][][] points) {
		if (uStride > vStride)
			this.points = new float[uOrder][vOrder][dimension];
		else
			this.points = new float[vOrder][uOrder][dimension];

		for (int i = 0; i < uOrder; ++i) {
			for (int j = 0; j < vOrder; ++j) {
				for (int k = 0; k < dimension; ++k) {
					if (uStride > vStride)
						this.points[i][j][k] = points[i][j][k];
					else
						this.points[j][i][k] = points[i][j][k];
				}
			}
		}
	}
}
