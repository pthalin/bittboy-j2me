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
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLInvalidOperationException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.pixel.GLPixelPacking;
import org.thenesis.pjogles.states.GLScissorState;

/**
 * Encapsulates the GLColorBuffer, GLStencilBuffer, GLDepthBuffer
 * and GLAccumBuffer. Pimeraly used to handle resizing.
 *
 * @author tdinneen
 */
public final class GLFrameBuffer implements GLConstants {
	private final GLSoftwareContext gc;

	public final GLColorBuffer frontBuffer;
	public GLColorBuffer backBuffer;
	public final GLStencilBuffer stencilBuffer;
	public final GLDepthBuffer depthBuffer;
	public final GLAccumBuffer accumBuffer;

	public int width;
	public int height;

	// validated

	public GLColorBuffer readBuffer;
	public GLColorBuffer drawBuffer;

	// internal

	public final GLColor c = new GLColor();

	// calculated from FrameBuffers's width / height
	// and Scissor x, y, width, height

	public int xmin, ymin; // inclusive
	public int xmax, ymax; // exclusive

	public GLFrameBuffer(final GLSoftwareContext gc) {
		this.gc = gc;

		frontBuffer = new GLColorBuffer(gc);
		// backBuffer no back buffer
		stencilBuffer = new GLStencilBuffer(gc);
		depthBuffer = new GLDepthBuffer(gc);
		accumBuffer = new GLAccumBuffer(gc);

		drawBuffer = frontBuffer;
		readBuffer = drawBuffer;
	}

	public final void validate(final GLSoftwareContext gc) {
		// set draw buffer pointer
		switch (gc.state.colorBuffer.drawBuffer) {
		case GL_FRONT:

			drawBuffer = gc.frameBuffer.frontBuffer;
			break;

		case GL_FRONT_AND_BACK:

			if (gc.doubleBuffer)
				drawBuffer = gc.frameBuffer.backBuffer;
			else
				drawBuffer = gc.frameBuffer.frontBuffer;

			break;

		case GL_BACK:

			drawBuffer = gc.frameBuffer.backBuffer;
			break;

		case GL_AUX0:
		case GL_AUX1:
		case GL_AUX2:
		case GL_AUX3:

			break;

		default:

			throw new GLInvalidEnumException("GLFrameBuffer.validate(GLSoftwareContext)");
		}

		// TODO - sort out !!! where should readBuffer be kept, see GLPixelState

		readBuffer = drawBuffer;

		// calc scissor window
		if (gc.state.enables.scissor) {
			GLScissorState scissor = gc.state.scissor;

			xmin = scissor.x;
			ymin = scissor.y;
			xmax = scissor.x + scissor.width;
			ymax = scissor.y + scissor.height;
		} else {
			xmin = 0;
			ymin = 0;
			xmax = width;
			ymax = height;
		}
	}

	public final void resize(final int width, final int height) {
		this.width = width;
		this.height = height;

		// resize buffers to the new window size

		frontBuffer.resize(width, height);

		//        if (modes.doubleBuffer)
		//            backBuffer.resize(width, height);

		if (gc.haveAccumBuffer)
			accumBuffer.resize(width, height);

		if (gc.haveDepthBuffer) // && gc.state.enables.depthTest)
			depthBuffer.resize(width, height);

		if (gc.haveStencilBuffer) // && gc.state.enables.stencil)
			stencilBuffer.resize(width, height);

		// TODO - (*gc->procs.computeClipBox)(gc);
	}

	public final void readPixels(final int x, final int y, final int width, final int height, final int format,
			final int type, final Object pixels) {
		switch (format) {
		case GL_COLOR_INDEX:
			readIndexPixels(x, y, width, height, type, pixels);
			break;
		case GL_STENCIL_INDEX:
			readStencilPixels(x, y, width, height, type, pixels);
			break;
		case GL_DEPTH_COMPONENT:
			readDepthPixels(x, y, width, height, type, pixels);
			break;
		case GL_RED:
		case GL_GREEN:
		case GL_BLUE:
		case GL_ALPHA:
		case GL_RGB:
		case GL_LUMINANCE:
		case GL_LUMINANCE_ALPHA:
		case GL_RGBA:
			readRGBAPixels(x, y, width, height, format, type, pixels);
			break;
		default:
			throw new GLInvalidEnumException("GLFrameBuffer.readPixels(int, int, int, int, int, int, Object)");
		}
	}

	private void readRGBAPixels(final int x, final int y, final int width, final int height, final int format,
			final int type, final Object pixels) {
		// try optimized path first - GL_RGBA / GL_UNSIGNED_BYTE with no transfer / packing modes
		if (!readFastRGBAPixels(x, y, width, height, format, type, pixels))
			throw new GLInvalidOperationException("GLFrameBuffer.readRGBAPixels(int, int, int, int, int, int, Object)");
	}

	private boolean readFastRGBAPixels(final int x, final int y, final int width, final int height, final int format,
			final int type, final Object pixels) {
		// can't do scale, bias, mapping
		if (gc.state.pixel.transferMode.hasTransfer(gc))
			return GL_FALSE;

		final GLPixelPacking packing = gc.clientState.clientPixel.packing;

		// can't do fancy pixel packing
		if (packing.alignment != 1 || packing.swapEndian || packing.lsbFirst)
			return GL_FALSE;

		int srcX = x;
		int srcY = y;
		int readWidth = width; // actual width read
		int readHeight = height; // actual height read
		int skipPixels = packing.skipPixels;
		int skipRows = packing.skipRows;
		final int rowLength;

		if (packing.rowLength > 0)
			rowLength = packing.rowLength;
		else
			rowLength = width;

		// horizontal clipping
		if (srcX < 0) {
			skipPixels -= srcX;
			readWidth += srcX;
			srcX = 0;
		}

		if (srcX + readWidth > this.width)
			readWidth -= (srcX + readWidth - this.width);

		if (readWidth <= 0)
			return GL_TRUE;

		// vertical clipping
		if (srcY < 0) {
			skipRows -= srcY;
			readHeight += srcY;
			srcY = 0;
		}

		if (srcY + readHeight > this.height)
			readHeight -= (srcY + readHeight - this.height);

		if (readHeight <= 0)
			return GL_TRUE;

		if (format == GL_RGBA && type == GL_UNSIGNED_BYTE) {
			int readIndex = srcY * this.height + srcX;
			int drawIndex = (skipRows * rowLength + skipPixels) << 2;

			final int rowLengthBy4 = rowLength << 2;

			// note - origin bottom left, y is read bottom up
			drawIndex += (readHeight - 1) * rowLengthBy4;

			final int[] readBuffer = this.readBuffer.buffer;
			final byte[] drawBuffer = (byte[]) pixels;

			for (int j = readHeight; --j >= 0;) {
				int k = drawIndex;

				for (int i = 0; i < readWidth; ++i) {
					c.set(readBuffer[readIndex + i]);

					drawBuffer[k++] = (byte) c.r;
					drawBuffer[k++] = (byte) c.g;
					drawBuffer[k++] = (byte) c.b;
					drawBuffer[k++] = (byte) c.a;
				}

				readIndex += this.width;
				drawIndex -= rowLengthBy4; // bottom up
			}

			return GL_TRUE;
		} else
			return GL_FALSE;
	}

	private void readDepthPixels(final int x, final int y, final int width, final int height, final int type,
			final Object pixels) {
		throw new GLInvalidOperationException("GLFrameBuffer.readDepthPixels(int, int, int, int, int, int, Object)");
	}

	private void readStencilPixels(final int x, final int y, final int width, final int height, final int type,
			final Object pixels) {
		throw new GLInvalidOperationException("GLFrameBuffer.readStencilPixels(int, int, int, int, int, int, Object)");
	}

	private void readIndexPixels(final int x, final int y, final int width, final int height, final int type,
			final Object pixels) {
		throw new GLInvalidOperationException("GLFrameBuffer.readIndexPixels(int, int, int, int, int, int, Object)");
	}
}
