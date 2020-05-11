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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.thenesis.m3g.engine.util.Tools;

public class VertexArray extends Object3D {

	private int numVertices;
	private int numComponents;
	private int componentSize;
	private int numElements;

	private Buffer buffer;
	private FloatBuffer floatBuffer;
	
	// Hack needed for OpenGL ES
	private ByteBuffer argbBuffer;
	
	private VertexArray() {
	}

	public VertexArray(int numVertices, int numComponents, int componentSize) {
		if (numVertices < 1 || numVertices > 65535)
			throw new IllegalArgumentException("numVertices must be in [1,65535]");
		if (numComponents < 2 || numComponents > 4)
			throw new IllegalArgumentException("numComponents must be in [2,4]");
		if (componentSize < 1 || componentSize > 2)
			throw new IllegalArgumentException("componentSize must be in [1,2]");

		this.numVertices = numVertices;
		this.numComponents = numComponents;
		this.componentSize = componentSize;

		numElements = numVertices * numComponents;

		if (componentSize == 1)
			buffer = ByteBuffer.allocateDirect(numElements);
		else
			buffer = ByteBuffer.allocateDirect(numElements * 2).asShortBuffer();

		floatBuffer = ByteBuffer.allocateDirect(numElements * 4).asFloatBuffer();
	}

	public void set(int firstVertex, int numVertices, short[] values) {
		int numElements = numVertices * numComponents;
		checkShortInput(firstVertex, numVertices, numElements, values);

		ShortBuffer shortBuffer = (ShortBuffer) buffer;
		shortBuffer.position(firstVertex);
		shortBuffer.put(values, 0, numElements);

		floatBuffer.position(firstVertex);
		for (int i = 0; i < numElements; i++)
			floatBuffer.put((float) values[i]);

	}

	public void set(int firstVertex, int numVertices, byte[] values) {
		int numElements = numVertices * numComponents;
		checkByteInput(firstVertex, numVertices, numElements, values);

		ByteBuffer byteBuffer = (ByteBuffer) buffer;
		byteBuffer.position(firstVertex);
		byteBuffer.put(values, 0, numElements);

		floatBuffer.position(firstVertex);
		for (int i = 0; i < numElements; i++)
			floatBuffer.put((float) values[i]);
	}
	
	Object3D duplicateImpl() {
		VertexArray copy = new VertexArray();
		copy.numVertices = numVertices;
		copy.numComponents = numComponents;
		copy.componentSize = componentSize;
		copy.numElements = numElements;
		copy.floatBuffer = (FloatBuffer)Tools.clone(floatBuffer);
		copy.argbBuffer = (ByteBuffer)Tools.clone(argbBuffer);
		return copy;
	}

	public int getVertexCount() {
		return this.numVertices;
	}

	public int getComponentCount() {
		return this.numComponents;
	}

	public int getComponentType() {
		return this.componentSize;
	}

	public void get(int firstVertex, int numVertices, short[] values) {
		int numElements = numVertices * numComponents;
		checkShortInput(firstVertex, numVertices, numElements, values);

		ShortBuffer shortBuffer = (ShortBuffer) buffer;
		shortBuffer.position(firstVertex);
		shortBuffer.get(values, 0, numElements);
	}

	public void get(int firstVertex, int numVertices, byte[] values) {
		int numElements = numVertices * numComponents;
		checkByteInput(firstVertex, numVertices, numElements, values);

		ByteBuffer byteBuffer = (ByteBuffer) buffer;
		byteBuffer.position(firstVertex);
		byteBuffer.get(values, 0, numElements);
	}

	private void checkShortInput(int firstVertex, int numVertices, int numElements, short[] values) {
		if (values == null)
			throw new NullPointerException("values can not be null");
		if (componentSize != 2)
			throw new IllegalStateException("vertexarray created as short array. can not get byte values");
		checkInput(firstVertex, numVertices, numElements, values.length);
	}

	private void checkByteInput(int firstVertex, int numVertices, int numElements, byte[] values) {
		if (values == null)
			throw new NullPointerException("values can not be null");
		if (componentSize != 1)
			throw new IllegalStateException("vertexarray created as short array. can not set byte values");
		checkInput(firstVertex, numVertices, numElements, values.length);
	}

	private void checkInput(int firstVertex, int numVertices, int numElements, int arrayLength) {
		if (numVertices < 0)
			throw new IllegalArgumentException("numVertices must be > 0");
		if (arrayLength < numElements)
			throw new IllegalArgumentException("number of elements i values does not match numVertices");
		if (firstVertex < 0 || firstVertex + numVertices > this.numVertices)
			throw new IndexOutOfBoundsException("index out of bounds");
	}

	int getComponentTypeGL() {
		if (componentSize == 1)
			return GL10.GL_BYTE;
		else
			return GL10.GL_SHORT;
	}

	Buffer getBuffer() {
		return buffer;
	}

	FloatBuffer getFloatBuffer() {
		return floatBuffer;
	}

	
	//	 Hack needed for OpenGL ES
	ByteBuffer getARGBBuffer() {
		return argbBuffer;
	}
	void setARGBBuffer(ByteBuffer argbBuffer) {
		this.argbBuffer = argbBuffer;
	}
	
	
}
