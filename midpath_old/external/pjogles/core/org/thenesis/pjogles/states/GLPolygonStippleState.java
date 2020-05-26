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
public final class GLPolygonStippleState {
	private byte stipple[] = new byte[32 * 4];

	public final void setStipple(final byte[] stipple) {
		if (this.stipple == null)
			this.stipple = new byte[32 * 4];

		System.arraycopy(stipple, 0, this.stipple, 0, 32 * 4);
	}

	public final GLPolygonStippleState get() {
		final GLPolygonStippleState s = new GLPolygonStippleState();
		s.set(this);

		return s;
	}

	public final void set(final GLPolygonStippleState s) {
		System.arraycopy(stipple, 0, s.stipple, 0, 32 * 4);
	}
}
