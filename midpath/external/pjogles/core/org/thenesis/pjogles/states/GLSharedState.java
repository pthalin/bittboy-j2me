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

import java.util.Hashtable;
import java.util.List;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLDisplayList;
import org.thenesis.pjogles.texture.GLTextureObject;

/**
 * @author tdinneen
 */
public final class GLSharedState implements GLConstants {
	public final Hashtable displayLists = new Hashtable();
	public final Hashtable textureObjects = new Hashtable();

	// Default texture objects (shared by all multi-texture units)
	public final GLTextureObject default1D = new GLTextureObject(0, GL_TEXTURE_1D);
	public final GLTextureObject default2D = new GLTextureObject(0, GL_TEXTURE_2D);
	public final GLTextureObject default3D = new GLTextureObject(0, GL_TEXTURE_3D);
}
