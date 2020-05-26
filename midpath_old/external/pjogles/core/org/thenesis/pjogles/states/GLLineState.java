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

/**
 * @author tdinneen
 */
public final class GLLineState {
	public float requestedWidth = 1.0f;
	public float smoothWidth = 1.0f;
	public int aliasedWidth = 1;
	public short stipple = (short) 0xFFFF;
	public int stippleRepeat = 1;

	public final GLLineState GLLineState() {
		final GLLineState s = new GLLineState();
		s.set(this);

		return s;
	}

	public final GLLineState get() {
		final GLLineState s = new GLLineState();
		s.set(this);

		return s;
	}

	public final void set(final GLLineState s) {
		requestedWidth = s.requestedWidth;
		smoothWidth = s.smoothWidth;
		aliasedWidth = s.aliasedWidth;
		stipple = s.stipple;
		stippleRepeat = s.stippleRepeat;
	}
}
