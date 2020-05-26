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
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;

/**
 * @author tdinneen
 */
public final class GLCurrentState implements GLConstants {
	/**
	 * Current color and colorIndex. These variables are also used for
	 * the current rasterPos color and colorIndex as set by the user.
	 */
	public final GLColor color = new GLColor(1.0f, 1.0f, 1.0f, 1.0f);
	public float colorIndex = 0.0f;

	public final GLVector4f normal = new GLVector4f(0.0f, 0.0f, 1.0f, 0.0f);

	public final GLVector4f[] texture = new GLVector4f[NUMBER_OF_TEXTURE_UNITS];

	/**
	 * Raster position valid bit.
	 */
	public boolean validRasterPosition = GL_FALSE;

	public final GLVertex raster = new GLVertex();

	public GLCurrentState() {
		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			texture[i] = new GLVector4f();

		raster.color.set(1.0f, 1.0f, 1.0f, 1.0f); // TOMD what's default raster color
	}

	public final GLCurrentState get() {
		final GLCurrentState s = new GLCurrentState();
		s.set(this);

		return s;
	}

	public final void set(final GLCurrentState s) {
		color.set(s.color);
		colorIndex = s.colorIndex;

		normal.set(s.normal);

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			texture[i].set(s.texture[i]);

		raster.set(s.raster);
		validRasterPosition = s.validRasterPosition;
	}
}
