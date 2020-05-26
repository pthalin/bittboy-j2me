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
public final class GLTextureParamState implements GLConstants {
	/**
	 * wrap modes
	 */
	public int sWrapMode = GL_REPEAT;
	public int tWrapMode = GL_REPEAT;
	public int rWrapMode = GL_REPEAT;

	/**
	 * min and mag filter
	 */
	public int minFilter = GL_NEAREST_MIPMAP_LINEAR;
	public int magFilter = GL_LINEAR;

	/**
	 * border color
	 */
	public final GLColor borderColor = new GLColor(0.0f, 0.0f, 0.0f, 1.0f);

	/**
	 * priority
	 */
	public float priority;

	/**
	 * level of detail controls
	 */
	public int baseLevel;
	public int maxLevel;
	public float minLOD;
	public float maxLOD;

	public final void set(final GLTextureParamState s) {
		sWrapMode = s.sWrapMode;
		tWrapMode = s.tWrapMode;
		rWrapMode = s.rWrapMode;

		minFilter = s.minFilter;
		magFilter = s.magFilter;

		borderColor.set(s.borderColor);

		priority = s.priority;

		baseLevel = s.baseLevel;
		maxLevel = s.maxLevel;
		minLOD = s.minLOD;
		maxLOD = s.maxLOD;
	}
}
