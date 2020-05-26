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

package org.thenesis.pjogles.buffers;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLSoftwareContext;

/**
 * The base buffer class for all buffers. Basically
 * this just provides shared routines for clearing a primitive array.
 *
 * @author tdinneen
 */
public abstract class GLBuffer implements GLConstants {
	public abstract void clear();

	public abstract void resize(int width, int height);

	public static void clear(final int[] buffer, final int value, final GLSoftwareContext gc) {
		if (buffer == null)
			return;

		if (gc.state.enables.scissor) {
			final GLFrameBuffer frameBuffer = gc.frameBuffer;

			final int rows = frameBuffer.ymax - frameBuffer.ymin;
			final int cols = frameBuffer.xmax - frameBuffer.xmin;

			final int rowStride = frameBuffer.width;

			int index = (frameBuffer.ymin * rowStride) + frameBuffer.xmin;

			for (int i = 0; i < rows; ++i) {
				for (int j = 0; j < cols; ++j)
					buffer[j + index] = value;

				index += rowStride;
			}
		} else {
			int size = buffer.length - 1;
			int cleared = 1;
			int index = 1;

			buffer[0] = value;

			while (cleared < size) {
				System.arraycopy(buffer, 0, buffer, index, cleared);

				size -= cleared;
				index += cleared;
				cleared <<= 1;
			}

			System.arraycopy(buffer, 0, buffer, index, size);
		}
	}

	public static void clear(final float[] buffer, final float value, final GLSoftwareContext gc) {
		if (buffer == null)
			return;

		if (gc.state.enables.scissor) {
			final GLFrameBuffer frameBuffer = gc.frameBuffer;

			final int rows = frameBuffer.ymax - frameBuffer.ymin;
			final int cols = frameBuffer.xmax - frameBuffer.xmin;

			final int rowStride = frameBuffer.width;

			int index = (frameBuffer.ymin * rowStride) + frameBuffer.xmin;

			for (int i = 0; i < rows; ++i) {
				for (int j = 0; j < cols; ++j)
					buffer[j + index] = value;

				index += rowStride;
			}
		} else {
			int size = buffer.length - 1;
			int cleared = 1;
			int index = 1;

			buffer[0] = value;

			while (cleared < size) {
				System.arraycopy(buffer, 0, buffer, index, cleared);

				size -= cleared;
				index += cleared;
				cleared <<= 1;
			}

			System.arraycopy(buffer, 0, buffer, index, size);
		}
	}
}
