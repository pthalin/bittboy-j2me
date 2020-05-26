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

import org.thenesis.pjogles.GL11;
import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLUtils;

/**
 * Encapsulates all the array data from calls to <strong>glVertexPointer</strong>,
 * <strong>glNormalPointer</strong> etc..
 * and provides functionality for  <strong>glArrayElement</strong>
 * and <strong>glDrawArrays</strong> etc..
 *
 * @see org.thenesis.pjogles.GL#glVertexPointer glVertexPointer
 * @see org.thenesis.pjogles.GL#glNormalPointer glNormalPointer
 * @see org.thenesis.pjogles.GL#glArrayElement glArrayElement
 * @see org.thenesis.pjogles.GL#glDrawArrays glDrawArrays
 *
 * @author tdinneen
 */
public final class GLInterleavedArray implements GLConstants {
	public final GLArrayState vertex = new GLArrayState();
	public final GLArrayState normal = new GLArrayState();
	public final GLArrayState color = new GLArrayState();
	public final GLArrayState colorIndex = new GLArrayState();
	public GLArrayState texture[]; // new GLArrayState();
	public final GLArrayState edgeFlag = new GLArrayState();

	public int interleavedFormat = GL_NONE;
	public int index;
	public int mask;
	public int textureEnables;

	public final void glVertexPointer(final int size, final int type, final int stride, final float[] pointer) {
		vertex.fpointer = pointer;
		vertex.size = size;
		vertex.type = type;
		vertex.stride = stride != 0 ? stride : size;

		interleavedFormat = GL_NONE;
	}

	public final void glNormalPointer(final int type, final int stride, final float[] pointer) {
		normal.fpointer = pointer;
		normal.size = 3;
		normal.type = type;
		normal.stride = stride != 0 ? stride : 3;

		interleavedFormat = GL_NONE;
	}

	public final void glColorPointer(final int size, final int type, final int stride, final float[] pointer) {
		color.fpointer = pointer;
		color.size = size;
		color.type = type;
		color.stride = stride != 0 ? stride : size;

		interleavedFormat = GL_NONE;
	}

	public final void glArrayElement(final int i, final GL11 gl) {
		if ((index & VERTARRAY_N_INDEX) != 0)
			normalElement(i, gl);

		if ((index & VERTARRAY_C_INDEX) != 0)
			colorElement(i, gl);

		if ((index & VERTARRAY_T_INDEX) != 0) {
			for (int j = 0; j < NUMBER_OF_TEXTURE_UNITS; ++j) {
				if ((textureEnables & (1 << j)) != 0)
					textureElement(i, j, gl);
			}
		}

		if ((index & VERTARRAY_I_INDEX) != 0)
			indexElement(i, gl);

		if ((index & VERTARRAY_E_INDEX) != 0)
			edgeFlagElement(i, gl);

		if ((index & VERTARRAY_V_INDEX) != 0)
			vertexElement(i, gl);
	}

	public final void glDrawArrays(final int mode, final int first, final int count, final GL11 gl) {
		final int last = first + count;

		gl.glBegin(mode);

		for (int i = first; i < last; ++i)
			glArrayElement(i, gl);

		gl.glEnd();
	}

	public void glDrawElements(int mode, int count, int type, short[] indices, final GL11 gl) {
		gl.glBegin(mode);
		for (int i = 0; i < count; i++) {
			glArrayElement(indices[i], gl);
		}
		gl.glEnd();
	}

	private void normalElement(final int i, final GL11 gl) {
		final int index = i * normal.stride;
		final float[] fpointer = normal.fpointer;
		final int[] ipointer = normal.ipointer;
		final byte[] bpointer = normal.bpointer;
		final short[] spointer = normal.spointer;

		switch (normal.size) {
		case 3:
			switch (normal.type) {
			case GL_BYTE:
				gl.glNormal3f(bpointer[index], bpointer[index + 1], bpointer[index + 2]);
				break;
			case GL_SHORT:
				gl.glNormal3f(spointer[index], spointer[index + 1], spointer[index + 2]);
				break;
			case GL_FIXED:
				gl.glNormal3f(GLUtils.FPtoF(ipointer[index]), GLUtils.FPtoF(ipointer[index + 1]), GLUtils
						.FPtoF(ipointer[index + 2]));
				break;
			case GL_FLOAT:
				gl.glNormal3f(fpointer[index], fpointer[index + 1], fpointer[index + 2]);
				break;
			default:
				throw new GLInvalidEnumException("GLInterleavedArray.vertexElement(int, GL11)");
			}
			break;
		default:
			throw new GLInvalidEnumException("GLInterleavedArray.vertexElement(int, GL11)");
		}
	}

	private void colorElement(final int i, final GL11 gl) {
		final int index = i * color.stride;
		final float[] fpointer = color.fpointer;
		final int[] ipointer = color.ipointer;
		final byte[] bpointer = color.bpointer;
		final short[] spointer = color.spointer;

		switch (color.size) {
		case 4:
			switch (color.type) {
			case GL_UNSIGNED_BYTE:
				gl.glColor4f(GLUtils.BtoF(bpointer[index]), GLUtils.BtoF(bpointer[index + 1]), GLUtils
						.BtoF(bpointer[index + 2]), GLUtils.BtoF(bpointer[index + 3]));
				break;
			case GL_FIXED:
				gl.glColor4f(GLUtils.FPtoF(ipointer[index]), GLUtils.FPtoF(ipointer[index + 1]), GLUtils
						.FPtoF(ipointer[index + 2]), GLUtils.FPtoF(ipointer[index + 3]));
				break;
			case GL_FLOAT:
				gl.glColor4f(fpointer[index], fpointer[index + 1], fpointer[index + 2], fpointer[index + 3]);
				break;
			default:
				throw new GLInvalidEnumException("GLInterleavedArray.colorElement(int, GL11)");
			}
			break;
		default:
			throw new GLInvalidEnumException("GLInterleavedArray.colorElement(int, GL11)");
		}
	}

	private void textureElement(final int i, final int j, final GL11 gl) {
		// TOMD - not implemented
	}

	private void indexElement(final int i, final GL11 gl) {
		// TOMD - not implemented
	}

	private void edgeFlagElement(final int i, final GL11 gl) {
		// TOMD - not implemented
	}

	private void vertexElement(final int i, final GL11 gl) {
		final int index = i * vertex.stride;
		final float[] fpointer = vertex.fpointer;
		final int[] ipointer = vertex.ipointer;
		final byte[] bpointer = vertex.bpointer;
		final short[] spointer = vertex.spointer;

		switch (vertex.size) {
		case 4:
			switch (vertex.type) {
			case GL_BYTE:
				gl.glVertex4f(bpointer[index], bpointer[index + 1], bpointer[index + 2], bpointer[index + 3]);
				break;
			case GL_SHORT:
				gl.glVertex4f(spointer[index], spointer[index + 1], spointer[index + 2], spointer[index + 3]);
				break;
			case GL_FIXED:
				gl.glVertex4f(GLUtils.FPtoF(ipointer[index]), GLUtils.FPtoF(ipointer[index + 1]), GLUtils
						.FPtoF(ipointer[index + 2]), GLUtils.FPtoF(ipointer[index + 3]));
				break;
			case GL_FLOAT:
				gl.glVertex4f(fpointer[index], fpointer[index + 1], fpointer[index + 2], fpointer[index + 3]);
				break;
			default:
				throw new GLInvalidEnumException("GLInterleavedArray.vertexElement(int, GL11)");
			}
			break;
		case 3:
			switch (vertex.type) {
			case GL_BYTE:
				gl.glVertex3f(bpointer[index], bpointer[index + 1], bpointer[index + 2]);
				break;
			case GL_SHORT:
				gl.glVertex3f(spointer[index], spointer[index + 1], spointer[index + 2]);
				break;
			case GL_FIXED:
				gl.glVertex3f(GLUtils.FPtoF(ipointer[index]), GLUtils.FPtoF(ipointer[index + 1]), GLUtils
						.FPtoF(ipointer[index + 2]));
				break;
			case GL_FLOAT:
				gl.glVertex3f(fpointer[index], fpointer[index + 1], fpointer[index + 2]);
				break;
			default:
				throw new GLInvalidEnumException("GLInterleavedArray.vertexElement(int, GL11)");
			}
			break;
		case 2:
			switch (vertex.type) {
			case GL_BYTE:
				gl.glVertex2f(bpointer[index], bpointer[index + 1]);
				break;
			case GL_SHORT:
				gl.glVertex2f(spointer[index], spointer[index + 1]);
				break;
			case GL_FIXED:
				gl.glVertex2f(GLUtils.FPtoF(ipointer[index]), GLUtils.FPtoF(ipointer[index + 1]));
				break;
			case GL_FLOAT:
				gl.glVertex2f(fpointer[index], fpointer[index + 1]);
				break;
			default:
				throw new GLInvalidEnumException("GLInterleavedArray.vertexElement(int, GL11)");
			}
			break;
		default:
			throw new GLInvalidEnumException("GLInterleavedArray.vertexElement(int, GL11)");
		}
	}
}
