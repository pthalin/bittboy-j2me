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

import org.thenesis.pjogles.states.GLClientPixelState;

/**
 * Represents the state variables (client side) in OpenGL that can be pushed and
 * popped via <strong>glPushClientAttrib</strong> and <strong>glPopClientAttrib</strong>.
 * Only pixel storage modes and vertex array state may be pushed and popped with
 * <strong>glPushClientAttrib</strong> and <strong>glPopClientAttrib</strong>.
 *
 * @see GL#glPushClientAttrib glPushClientAttrib
 * @see GL#glPopClientAttrib glPopClientAttrib
 *
 * @author tdinneen
 */
public final class GLClientAttribute {
	public int mask;

	public final GLClientPixelState clientPixel = new GLClientPixelState();
	//__GLclientTextureState clientTexture;
	//__GLclientVertArrayState vertexArray;
}
