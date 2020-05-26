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

import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.primitives.GLFragment;
import org.thenesis.pjogles.primitives.GLSpan;

/**
 * The Depth buffer (ZBuffer), a float array of z values.
 * Should really make this an int array.
 *
 * @author tdinneen
 */
public final class GLDepthBuffer extends GLBuffer {
	private final GLSoftwareContext gc;
	private float[] buffer;

	public GLDepthBuffer(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void clear() {
		clear(buffer, Float.MAX_VALUE * gc.state.depth.clear, gc);
	}

	public final void resize(final int width, final int height) {
		if (buffer != null && (buffer.length == width * height))
			return;

		buffer = new float[width * height];
	}

	public final int store(final GLSpan span) {
		if (buffer == null)
			return 0;

		final long start = System.currentTimeMillis();

		int passed = 0;
		final boolean[] mask = span.mask;
		final int length = span.length;
		final float[] z = span.z;
		final int offset = span.offset;
		final float[] zbuffer = buffer;

		final boolean[] depthFail = span.depthFail;
		final boolean[] depthPass = span.depthPass;

		// switch cases ordered from
		// most frequent to less frequent

		switch (gc.state.depth.testFunc) {
		case GL_LESS:
			if (gc.state.depth.writeEnable) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] < zbuffer[offset + i]) {
							zbuffer[offset + i] = z[i];
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			} else // don't update ZBuffer
			{
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] < zbuffer[offset + i]) {
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			}
			break;
		case GL_LEQUAL:
			if (gc.state.depth.writeEnable) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] <= zbuffer[offset + i]) {
							zbuffer[offset + i] = z[i];
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			} else // don't update ZBuffer
			{
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] <= zbuffer[offset + i]) {
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			}
			break;
		case GL_GEQUAL:
			if (gc.state.depth.writeEnable) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] >= zbuffer[offset + i]) {
							zbuffer[offset + i] = z[i];
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			} else // don't update ZBuffer
			{
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] >= zbuffer[offset + i]) {
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			}
			break;
		case GL_GREATER:
			if (gc.state.depth.writeEnable) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] > zbuffer[offset + i]) {
							zbuffer[offset + i] = z[i];
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			} else // don't update ZBuffer
			{
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] > zbuffer[offset + i]) {
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			}
			break;
		case GL_NOTEQUAL:
			if (gc.state.depth.writeEnable) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] != zbuffer[offset + i]) {
							zbuffer[offset + i] = z[i];
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			} else // don't update ZBuffer
			{
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] != zbuffer[offset + i]) {
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			}
			break;
		case GL_EQUAL:
			if (gc.state.depth.writeEnable) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] == zbuffer[offset + i]) {
							zbuffer[offset + i] = z[i];
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			} else // don't update ZBuffer
			{
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						if (z[i] == zbuffer[offset + i]) {
							++passed;
							depthPass[i] = true;
						} else // fail
						{
							depthFail[i] = true;
							mask[i] = false;
						}
					}
				}
			}
			break;
		case GL_ALWAYS:
			if (gc.state.depth.writeEnable) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						zbuffer[offset + i] = z[i];
						++passed;
						depthPass[i] = true;
					}
				}
			} else // don't update ZBuffer
			{
				passed = length;
			}
			break;
		case GL_NEVER:
			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					mask[i] = false;
					depthPass[i] = false;
					depthFail[i] = true;
				}
			}
			break;
		default:
			throw new GLInvalidEnumException("GLDepthBuffer.store(GLSpan)");
		}

		gc.benchmark.depthTest += System.currentTimeMillis() - start;

		return passed;
	}

	public final boolean store(final GLFragment fragment) {
		if (buffer == null)
			return false;

		final int x = fragment.x;
		final int y = fragment.y;
		final float zVal = fragment.z;

		final int index = x + y;

		if (testFunction(index, zVal)) {
			buffer[index] = zVal;
			return true;
		} else
			return false;
	}

	private boolean testFunction(final int index, final float zVal) {
		final float bufferVal = buffer[index];

		switch (gc.state.depth.testFunc) {
		case GL_NEVER:
			return false;
		case GL_LESS:
			return (zVal < bufferVal);
		case GL_EQUAL:
			return (zVal == bufferVal);
		case GL_LEQUAL:
			return (zVal <= bufferVal);
		case GL_GREATER:
			return (zVal > bufferVal);
		case GL_NOTEQUAL:
			return (zVal != bufferVal);
		case GL_GEQUAL:
			return (zVal >= bufferVal);
		case GL_ALWAYS:
			return true;
		default:
			throw new GLInvalidEnumException("GLDepthBuffer.store(GLFragment)");
		}
	}
}
