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
import org.thenesis.pjogles.math.GLVector4f;

/**
 * @author tdinneen
 */
public final class GLTransformState implements GLConstants {
	/**
	 * Current mode of the matrix stack.  This determines what effect
	 * the various matrix operations (load, mult, scale) apply to.
	 */
	public int matrixMode = GL_MODELVIEW;

	/**
	 * User clipping planes in eye space.  These are the user clip planes
	 * projected into eye space.
	 */
	public final GLVector4f[] eyeClipPlanes = new GLVector4f[MAX_USER_CLIP_PLANES];

	public GLTransformState() {
		// by default, all clipping planes are defined as (0,0,0,0) in eye coordinates

		for (int i = MAX_USER_CLIP_PLANES; --i >= 0;)
			eyeClipPlanes[i] = new GLVector4f(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public final GLTransformState get() {
		final GLTransformState s = new GLTransformState();
		s.set(this);

		return s;
	}

	public final void set(final GLTransformState s) {
		matrixMode = s.matrixMode;

		for (int i = MAX_USER_CLIP_PLANES; --i >= 0;)
			eyeClipPlanes[i].set(s.eyeClipPlanes[i]);
	}
}
