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
import org.thenesis.pjogles.math.GLVector4f;

/**
 * @author tdinneen
 */
public final class GLEdge implements GLConstants {
	public float x, z;
	public final GLColor color = new GLColor();
	public final GLVector4f[] texture = new GLVector4f[NUMBER_OF_TEXTURE_UNITS];
	public float fog, eyeZ;

	public GLEdge() {
		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			texture[i] = new GLVector4f();
	}
}
