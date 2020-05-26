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

package org.thenesis.pjogles.buffers;

import org.thenesis.pjogles.GLSoftwareContext;

/**
 * The Colour buffer, a 32bit (int) array of RGBA colour values.
 *
 * @author tdinneen
 */
public final class GLColorBuffer extends GLBuffer {
	private final GLSoftwareContext gc;
	public int[] buffer;

	public GLColorBuffer(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void clear() {
		clear(buffer, gc.state.colorBuffer.clear.getRGBAi(), gc);
	}

	public final void resize(final int width, final int height) {
		if (buffer != null && (buffer.length == width * height))
			return;

		//buffer = new int[width * height];
		buffer = gc.getBackend().getColorBuffer(width * height);
	}
}
