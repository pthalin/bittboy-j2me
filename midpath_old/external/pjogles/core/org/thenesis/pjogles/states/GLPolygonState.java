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

package org.thenesis.pjogles.states;

import org.thenesis.pjogles.GLConstants;

/**
 * @author tdinneen
 */
public final class GLPolygonState implements GLConstants {
	public int frontMode = GL_FILL;
	public int backMode = GL_FILL;

	/**
	 * Culling state. Culling can be enabled/disabled and set to cull
	 * front or back triangles. The FrontFace call determines whether clockwise
	 * or counter-clockwise oriented vertices are front facing.
	 */
	public int cull = GL_BACK;
	public int frontFaceDirection = GL_CCW;

	/**
	 * Polygon offset state.
	 */
	public float factor;
	public float units;

	// TOMD - need to find defaults for above !

	public final GLPolygonState get() {
		final GLPolygonState s = new GLPolygonState();
		s.set(this);

		return s;
	}

	public final void set(final GLPolygonState s) {
		frontMode = s.frontMode;
		backMode = s.backMode;
		cull = s.cull;
		frontFaceDirection = s.frontFaceDirection;
		factor = s.factor;
		units = s.units;
	}
}
