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

package org.thenesis.pjogles.primitives;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.render.GLFragmentTexture;

/**
 * A GL fragment, used to render lines and bitmaps. Polygons are
 * rendered via GLSpans.
 *
 * @author tdinneen
 */
public final class GLFragment implements GLConstants {
	/**
	 * Screen x, y.
	 */
	public int x, y;

	/**
	 * Z coordinate in form used by depth buffer.
	 */
	public float z;

	/**
	 * Colors of the fragment. When in colorIndexMode only the r component
	 * is valid.
	 */
	//public GLColor color[] = new GLColor[__GL_NUM_FRAGMENT_COLORS];
	//public int color[] = new int[__GL_NUM_FRAGMENT_COLORS];
	/**
	 * Texture information for the fragment.
	 */
	public final GLFragmentTexture[] texture = new GLFragmentTexture[NUMBER_OF_TEXTURE_UNITS];

	/**
	 * Fog information for the fragment.
	 */
	public float fog;

	public final GLColor color = new GLColor();

	public GLFragment() {
		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			texture[i] = new GLFragmentTexture();
	}
}
