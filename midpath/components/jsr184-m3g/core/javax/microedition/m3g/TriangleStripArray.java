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


public class TriangleStripArray extends IndexBuffer {

	public TriangleStripArray(int firstIndex, int[] stripLengths) {
		int sum = checkInput(stripLengths);
		if (firstIndex < 0 || sum > 65535)
			throw new IndexOutOfBoundsException("firstIndex must be in [0, sum(stripLengths)]");

		// fill indexbuffer
		allocate(sum + (stripLengths.length - 1) * 3);
		int index = firstIndex;
		for (int i = 0; i < stripLengths.length; i++) {
			if (i != 0) {
				// if this is not the first strip,
				// we need to connect the strips
				put(index - 1);
				put(index);
				if ((buffer.position() % 2) == 1) // may need extra index for correct winding
					put(index);
			}
			for (int s = 0; s < stripLengths[i]; ++s) {
				put(index++);
			}
		}
		// reset position and set limit
		buffer.flip();
	}
	
	private void put(int value) {
		buffer.put((short)(value & 0xFFFF));
	}

	public TriangleStripArray(int[] indices, int[] stripLengths) {
		if (indices == null)
			throw new NullPointerException("indices can not be null");

		for (int i = 0; i < indices.length; i++) {
			if (indices[i] < 0 || indices[i] > 65535)
				throw new IndexOutOfBoundsException("all elements in indices must be in [0,65535]");
		}

		int sum = checkInput(stripLengths);
		if (indices.length < sum)
			throw new IllegalArgumentException("length of indices must be greater or equal to sum(stripLengths)]");

		// fill index buffer
		allocate(sum + (stripLengths.length - 1) * 3);
		int index = 0;
		for (int i = 0; i < stripLengths.length; i++) {
			if (i != 0) {
				// if this is not the first strip,
				// we need to connect the strips
				put(indices[index - 1]);
				put(indices[index]);
				if ((buffer.position() % 2) == 1) // may need extra index for correct winding
					put(indices[index]);
			}
			for (int s = 0; s < stripLengths[i]; ++s) {
				put(indices[index++]);
			}
		}
		// reset position and set limit
		buffer.flip();
	}

	private TriangleStripArray() {
	}

	public void getIndices(int[] indices) {
		if (indices != null)
			throw new NullPointerException("Indices can not be null");
		if (indices.length < getIndexCount())
			throw new IllegalArgumentException("Length of indices array must be " + getIndexCount());
		// TODO: fill indices with triangle-data
	}

	private int checkInput(int[] stripLengths) {
		int sum = 0;
		if (stripLengths == null)
			throw new NullPointerException("stripLengths can not be null");
		int l = stripLengths.length;
		if (l == 0)
			throw new IllegalArgumentException("stripLenghts can not be empty");
		for (int i = 0; i < l; i++) {
			if (stripLengths[i] < 3)
				throw new IllegalArgumentException("stripLengths must not contain elemets less than 3");

			sum += stripLengths[i];
		}
		return sum;
	}
	
	Object3D duplicateImpl() {
		TriangleStripArray copy = new TriangleStripArray();
		buffer.rewind();
		copy.allocate(getIndexCount());
		copy.buffer.put(buffer);
		copy.buffer.flip();
		return copy;
	}

}
