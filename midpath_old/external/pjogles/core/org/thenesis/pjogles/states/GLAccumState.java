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

import org.thenesis.pjogles.lighting.GLColor;

/**
 * @author tdinneen
 */
public final class GLAccumState {
	public final GLColor clear = new GLColor(0.0f, 0.0f, 0.0f, 0.0f); // accumulation buffer clear color

	public final GLAccumState get() {
		final GLAccumState s = new GLAccumState();
		s.set(this);

		return s;
	}

	public final void set(final GLAccumState s) {
		clear.set(s.clear);
	}
}
