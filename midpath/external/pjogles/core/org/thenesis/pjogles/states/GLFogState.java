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
import org.thenesis.pjogles.lighting.GLColor;

/**
 * @author tdinneen
 */
public final class GLFogState implements GLConstants {
	public int mode = GL_EXP;
	public final GLColor color = new GLColor(0.0f, 0.0f, 0.0f, 0.0f); // TOMD, check if this is the default fog color
	public float index; // color index
	public float density = 1.0f;
	public float start = 0.0f;
	public float end = 1.0f;

	// derived
	public float oneOverEMinusS;

	public final GLFogState get() {
		final GLFogState s = new GLFogState();
		s.set(this);

		return s;
	}

	public final void set(final GLFogState s) {
		mode = s.mode;
		color.set(s.color);
		index = s.index;
		density = s.density;
		start = s.start;
		end = s.end;

		// derived
		oneOverEMinusS = s.oneOverEMinusS;
	}
}
