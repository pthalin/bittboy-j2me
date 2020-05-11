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

package org.thenesis.pjogles;

import java.util.Stack;

/**
 * Encapsulates the current modelView, projection and texture transforms
 * along with their associated stacks.
 *
 * @author tdinneen
 */
public final class GLTransformStack {
	/**
	 * Current transform matrix.
	 */
	public GLTransform modelView = new GLTransform();

	/**
	 * Transformation stack.
	 */
	public final Stack modelViewStack = new Stack();

	/**
	 * Current projection matrix. Used to transform eye coordinates into
	 * NTVP (or clip) coordinates.
	 */
	public GLTransform projection = new GLTransform();

	/**
	 * Projection stack.
	 */
	public final Stack projectionStack = new Stack();

	/**
	 * Current texture matrix.
	 */
	public GLTransform texture = new GLTransform();

	/**
	 * Texture matrix stack.
	 */
	public final Stack textureStack = new Stack();
}
