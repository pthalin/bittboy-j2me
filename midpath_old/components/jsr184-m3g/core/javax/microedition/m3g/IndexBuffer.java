/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
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
package javax.microedition.m3g;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public abstract class IndexBuffer extends Object3D {
	protected ShortBuffer buffer = null;

	public int getIndexCount() {
		return buffer.limit();
	}

	public abstract void getIndices(int[] indices);

	protected void allocate(int numElements) {
		buffer = ByteBuffer.allocateDirect(numElements * 2).asShortBuffer();
		//buffer = BufferUtil.newIntBuffer(numElements);
	}

	ShortBuffer getBuffer() {
		return buffer;
	}
}
