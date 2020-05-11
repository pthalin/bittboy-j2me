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

package org.thenesis.pjogles.arrays;

/**
 * Used by GLInterleavedArray to build up arrays of vertex, normal, colour etc.
 * Check out <strong>glVertexPointer</strong>, <strong>glNormalPointer</strong>,
 * <strong>glColorPointer</strong> etc.
 *
 * @see org.thenesis.pjogles.GL#glVertexPointer glVertexPointer
 * @see org.thenesis.pjogles.GL#glNormalPointer glNormalPointer
 * @see org.thenesis.pjogles.GL#glColorPointer glColorPointer
 *
 * @author tdinneen
 */
public final class GLArrayState {
	/**
	 * No void* in Java so we have a pointer as such to an array
	 * of each supported 'type'.
	 */
	public int ipointer[];
	public float fpointer[];
	public byte bpointer[];
	public short spointer[];

	public int stride;
	public int size;
	public int type;
}
