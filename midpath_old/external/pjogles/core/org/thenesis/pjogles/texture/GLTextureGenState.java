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
import org.thenesis.pjogles.math.GLVector4f;

/**
 * @author tdinneen
 */
public final class GLTextureGenState implements GLConstants {
	/**
	 * How coordinates are being generated.
	 */
	public int mode = GL_EYE_LINEAR;

	/**
	 * Eye plane equation (used iff mode == GL_EYE_LINEAR).
	 */
	public final GLVector4f eyePlaneEquation = new GLVector4f();

	/**
	 * Object plane equation (used iff mode == GL_OBJECT_LINEAR).
	 */
	public final GLVector4f objectPlaneEquation = new GLVector4f();

	public final void set(final GLTextureGenState s) {
		mode = s.mode;
		eyePlaneEquation.set(s.eyePlaneEquation);
		objectPlaneEquation.set(s.objectPlaneEquation);
	}
}
