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
public final class GLDepthState implements GLConstants {
	/**
	 * Depth buffer test function.  The z value is compared using zFunction
	 * against the current value in the zbuffer.  If the comparison
	 * succeeds the new z value is written into the z buffer masked
	 * by the z write valueMask.
	 */
	public int testFunc = GL_LESS;

	/**
	 * Writemask enable.  When GL_TRUE writing to the depth buffer is
	 * allowed.
	 */
	public boolean writeEnable = GL_TRUE;

	/**
	 * javaPrimitive used to clear the z buffer when glClear is called.
	 */
	public float clear = 1.0f;

	public final GLDepthState get() {
		final GLDepthState s = new GLDepthState();
		s.set(this);

		return s;
	}

	public final void set(final GLDepthState s) {
		testFunc = s.testFunc;
		writeEnable = s.writeEnable;
		clear = s.clear;
	}
}
