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

package org.thenesis.pjogles.texture;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.lighting.GLColor;

/**
 * @author tdinneen
 */
public final class GLTextureEnvState implements GLConstants {
	/**
	 * Environment "blend" function.
	 */
	public int mode = GL_MODULATE;

	/**
	 * Environment color.
	 */
	public final GLColor color = new GLColor(); // 0.0, 0.0, 0.0, 0.0

	public final void set(final GLTextureEnvState s) {
		mode = s.mode;
		color.set(s.color);
	}
}
