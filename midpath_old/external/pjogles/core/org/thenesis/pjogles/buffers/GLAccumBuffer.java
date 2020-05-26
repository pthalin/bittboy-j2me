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

import org.thenesis.pjogles.GLInvalidOperationException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.lighting.GLColor;

/**
 * The Accumulation buffer, a 32bit (int) array of RGBA colour values.
 *
 * @author tdinneen
 */
public final class GLAccumBuffer extends GLBuffer {
	protected final GLSoftwareContext gc;
	protected int[] buffer;

	protected final GLColor c1 = new GLColor();
	protected final GLColor c2 = new GLColor();

	public GLAccumBuffer(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void clear() {
		clear(buffer, gc.state.accum.clear.getRGBAi(), gc);
	}

	public final void resize(final int width, final int height) {
		if (buffer != null && (buffer.length == width * height))
			return;

		buffer = new int[width * height];
	}

	public final void doAccumulate(final float value) {
		// TOMD - scissoring

		//    int x0 = gc.transform.clipX0;
		//    int x1 = gc.transform.clipX1;
		//    int y0 = gc.transform.clipY0;
		//    int y1 = gc.transform.clipY1;

		final int width = gc.frameBuffer.width;

		final int x0 = gc.state.viewport.x;
		final int x1 = gc.state.viewport.width;
		final int y0 = gc.state.viewport.y * width;
		final int y1 = gc.state.viewport.height * width;

		final int[] cfb = gc.frameBuffer.readBuffer.buffer;
		final int[] localBuffer = buffer;

		final boolean scale = value != 1.0f;

		for (int x = x0, index; x < x1; ++x) {
			for (int y = y0; y < y1; y += width) {
				index = x + y;

				// GL_ACCUM Obtains R, G, B, and A values from the buffer currently selected for
				// reading (see glReadBuffer). Each component value is divided by 2^n– 1,
				// where n is the number of bits allocated to each color component in the currently
				// selected buffer. The result is a floating-point value in the range [0,1],
				// which is multiplied by value and added to the corresponding pixel component in
				// the accumulation buffer, thereby updating the accumulation buffer.

				c1.set(cfb[index]);

				// scale
				if (scale) {
					c1.r *= value;
					c1.g *= value;
					c1.b *= value;
					c1.a *= value;
				}

				c2.set(localBuffer[index]);

				// accumulate
				c2.r += c1.r;
				c2.g += c1.g;
				c2.b += c1.b;
				c2.a += c1.a;

				localBuffer[index] = c2.getRGBAi();
			}
		}
	}

	public final void doLoad(final float value) {
		throw new GLInvalidOperationException("GLAccumBuffer.doLoad(float) - Not Implemented !");
	}

	public final void doReturn(final float value) {
		// TOMD - scissoring ?

		//    int x0 = gc.transform.clipX0;
		//    int x1 = gc.transform.clipX1;
		//    int y0 = gc.transform.clipY0;
		//    int y1 = gc.transform.clipY1;

		final int width = gc.frameBuffer.width;

		final int x0 = gc.state.viewport.x;
		final int x1 = gc.state.viewport.width;
		final int y0 = gc.state.viewport.y * width;
		final int y1 = gc.state.viewport.height * width;

		final int[] cfb = gc.frameBuffer.readBuffer.buffer;

		final boolean scale = value != 1.0f;

		for (int x = x0, index; x < x1; ++x) {
			for (int y = y0; y < y1; y += width) {
				index = x + y;

				// GL_RETURN  Transfers accumulation buffer values to the color buffer or buffers
				// currently selected for writing. Each R, G, B, and A component is multiplied by value,
				// then multiplied by 2n– 1, clamped to the range [0, 2n – 1 ], and stored in the
				// corresponding display buffer cell. The only fragment operations that are applied to
				// this transfer are pixel ownership, scissor, dithering, and color writemasks.

				c1.set(buffer[index]);

				// scale
				if (scale) {
					c1.r *= value;
					c1.g *= value;
					c1.b *= value;
					c1.a *= value;
				}

				// clamp
				c1.r = c1.r < 0.0f ? 0.0f : (c1.r > 1.0f ? 1.0f : c1.r);
				c1.g = c1.g < 0.0f ? 0.0f : (c1.g > 1.0f ? 1.0f : c1.g);
				c1.b = c1.b < 0.0f ? 0.0f : (c1.b > 1.0f ? 1.0f : c1.b);
				c1.a = c1.a < 0.0f ? 0.0f : (c1.a > 1.0f ? 1.0f : c1.a);

				cfb[index] = c1.getRGBAi();
			}
		}
	}

	public final void doMult(final float value) {
		throw new GLInvalidOperationException("GLAccumBuffer.doMult(float) - Not Implemented !");
	}

	public final void doAdd(final float value) {
		throw new GLInvalidOperationException("GLAccumBuffer.doAdd(float) - Not Implemented !");
	}
}
