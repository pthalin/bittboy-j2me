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
 * The Stencil buffer, a 32bit (int) array.
 * This could really be something smaller, like a char array.
 * Or we could limit the depth buffer to 24bits and use the top 8bits for the stencil.
 *
 * @author tdinneen
 */
public final class GLStencilBuffer extends GLBuffer {
	private final GLSoftwareContext gc;
	private int[] buffer;

	public GLStencilBuffer(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void clear() {
		clear(buffer, gc.state.stencil.clear, gc);
	}

	public final void resize(final int width, final int height) {
		if (buffer != null && (buffer.length == width * height))
			return;

		buffer = new int[width * height];
	}

	public final boolean testFunction(final GLSpan span) {
		final long start = System.currentTimeMillis();

		final int length = span.length;
		final boolean[] mask = span.mask;
		final boolean[] stencilFail = span.stencilFail;

		int passed = 0;

		final int valueMask = gc.state.stencil.valueMask;
		final int reference = gc.state.stencil.reference & valueMask;
		int value;

		final int offset = span.offset;
		final int[] localBuffer = buffer;

		switch (gc.state.stencil.testFunc) {
		case GL_NEVER:

			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					mask[i] = false;
					stencilFail[i] = true;
				}
			}

			passed = 0;

			break;

		case GL_LESS:

			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					value = localBuffer[offset + i] & valueMask;

					if (reference < value)
						++passed;
					else {
						stencilFail[i] = true;
						mask[i] = false;
					}
				}
			}

			break;

		case GL_LEQUAL:

			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					value = localBuffer[offset + i] & valueMask;

					if (reference <= value)
						++passed;
					else {
						stencilFail[i] = true;
						mask[i] = false;
					}
				}
			}

			break;

		case GL_GREATER:

			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					value = localBuffer[offset + i] & valueMask;

					if (reference > value)
						++passed;
					else {
						stencilFail[i] = true;
						mask[i] = false;
					}
				}
			}

			break;

		case GL_GEQUAL:

			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					value = localBuffer[offset + i] & valueMask;

					if (reference >= value)
						++passed;
					else {
						stencilFail[i] = true;
						mask[i] = false;
					}
				}
			}

			break;

		case GL_EQUAL:

			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					value = localBuffer[offset + i] & valueMask;

					if (reference == value)
						++passed;
					else {
						stencilFail[i] = true;
						mask[i] = false;
					}
				}
			}

			break;

		case GL_NOTEQUAL:

			for (int i = 0; i < length; ++i) {
				if (mask[i]) {
					value = localBuffer[offset + i] & valueMask;

					if (reference != value)
						++passed;
					else {
						stencilFail[i] = true;
						mask[i] = false;
					}
				}
			}

			break;

		case GL_ALWAYS:

			passed = length;
			break;

		default:

			throw new GLInvalidEnumException("GLStencilBuffer.store(GLSpan)");
		}

		gc.benchmark.stencilTest += System.currentTimeMillis() - start;

		return passed != 0;
	}

	public final boolean testFunction(final GLFragment fragment) {
		// Stencil test function. When the stencil is enabled this
		// function is applied to the reference value and the stored stencil
		// value as follows:
		//		result = ref comparision (valueMask & stencilBuffer[x][y])
		// If the test fails then the fail op is applied and rendering of
		// the pixel stops.

		final int mask = gc.state.stencil.valueMask;
		final int reference = gc.state.stencil.reference & mask;
		final int value = buffer[fragment.x + fragment.y];

		switch (gc.state.stencil.testFunc) {
		case GL_NEVER:
			return false;
		case GL_LESS:
			return reference < (value & mask);
		case GL_GEQUAL:
			return reference >= (value & mask);
		case GL_LEQUAL:
			return reference <= (value & mask);
		case GL_GREATER:
			return reference > (value & mask);
		case GL_NOTEQUAL:
			return reference != (value & mask);
		case GL_EQUAL:
			return reference == (value & mask);
		case GL_ALWAYS:
			return true;
		default:
			throw new GLInvalidEnumException("GLStencilBuffer.store(GLFragment)");
		}
	}

	public final void failOperation(final GLSpan span) {
		operation(span, gc.state.stencil.fail, span.stencilFail);
	}

	public final void depthFailOperation(final GLSpan span) {
		operation(span, gc.state.stencil.depthFail, span.depthFail);
	}

	public final void depthPassOperation(final GLSpan span) {
		operation(span, gc.state.stencil.depthPass, span.depthPass);
	}

	public final void failOperation(final GLFragment fragment) {
		operation(fragment, gc.state.stencil.fail);
	}

	public final void depthFailOperation(final GLFragment fragment) {
		operation(fragment, gc.state.stencil.depthFail);
	}

	public final void depthPassOperation(final GLFragment fragment) {
		operation(fragment, gc.state.stencil.depthPass);
	}

	private void operation(final GLSpan span, final int operation, final boolean[] mask) {
		if (operation == GL_KEEP)
			return;

		final long start = System.currentTimeMillis();

		final int __GL_MAX_STENCIL_VALUE = ((1 << gc.stencilSize) - 1);

		final int length = span.length;

		final int writeMask = gc.state.stencil.writeMask;
		final int invMask = ~writeMask;

		final int reference = gc.state.stencil.reference;

		final int offset = span.offset;
		final int[] localBuffer = buffer;

		int value;

		switch (operation) {
		case GL_ZERO:
			if (invMask == 0) {
				for (int i = 0; i < length; ++i) {
					if (mask[i])
						localBuffer[offset + i] = 0;
				}
			} else {
				for (int i = 0; i < length; ++i) {
					if (mask[i])
						localBuffer[offset + i] &= invMask;
				}
			}
			break;
		case GL_REPLACE:
			if (invMask == 0) {
				for (int i = 0; i < length; ++i) {
					if (mask[i])
						localBuffer[offset + i] = reference;
				}
			} else {
				for (int i = 0; i < length; ++i) {
					if (mask[i])
						localBuffer[offset + i] = (localBuffer[offset + i] & invMask) | (reference & writeMask);
				}
			}
			break;
		case GL_INCR:
			if (invMask == 0) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						value = localBuffer[offset + i];

						if (value < __GL_MAX_STENCIL_VALUE)
							localBuffer[offset + i] = value + 1;
					}
				}
			} else {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						value = localBuffer[offset + i];

						if (value < __GL_MAX_STENCIL_VALUE)
							localBuffer[offset + i] = (value & invMask) | ((value + 1) & writeMask);
					}
				}
			}
			break;
		case GL_DECR:
			if (invMask == 0) {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						value = localBuffer[offset + i];

						if (value > 0)
							localBuffer[offset + i] = value - 1;
					}
				}
			} else {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						value = localBuffer[offset + i];

						if (value > 0)
							localBuffer[offset + i] = (value & invMask) | ((value - 1) & writeMask);
					}
				}
			}
			break;
		//            case GL_INCR_WRAP_EXT:
		//                if (invMask == 0)
		//                {
		//                    for (i = 0; i < n; i++)
		//                    {
		//                        if (mask[i])
		//                        {
		//                            GLstencil * sptr = STENCIL_ADDRESS(x[i], y[i]);
		//                            *sptr = (GLstencil) ( * sptr + 1);
		//                        }
		//                    }
		//                }
		//                else
		//                {
		//                    for (i = 0; i < n; i++)
		//                    {
		//                        if (mask[i])
		//                        {
		//                            GLstencil * sptr = STENCIL_ADDRESS(x[i], y[i]);
		//                            *sptr = (GLstencil) ((invmask & * sptr) | (wrtmask & ( * sptr + 1)));
		//                        }
		//                    }
		//                }
		//                break;
		//            case GL_DECR_WRAP_EXT:
		//                if (invMask == 0)
		//                {
		//                    for (i = 0; i < n; i++)
		//                    {
		//                        if (mask[i])
		//                        {
		//                            GLstencil * sptr = STENCIL_ADDRESS(x[i], y[i]);
		//                            *sptr = (GLstencil) ( * sptr - 1);
		//                        }
		//                    }
		//                }
		//                else
		//                {
		//                    for (i = 0; i < n; i++)
		//                    {
		//                        if (mask[i])
		//                        {
		//                            GLstencil * sptr = STENCIL_ADDRESS(x[i], y[i]);
		//                            *sptr = (GLstencil) ((invmask & * sptr) | (wrtmask & ( * sptr - 1)));
		//                        }
		//                    }
		//                }
		//                break;
		case GL_INVERT:
			if (invMask == 0) {
				for (int i = 0; i < length; ++i) {
					if (mask[i])
						localBuffer[offset + i] = ~localBuffer[offset + i];
				}
			} else {
				for (int i = 0; i < length; ++i) {
					if (mask[i]) {
						value = localBuffer[offset + i];
						localBuffer[offset + i] = (value & invMask) | ((~value) & writeMask);
					}
				}
			}
			break;
		default:
			throw new GLInvalidEnumException("GLStencilBuffer.operation(GLSpan, int, int[])");
		}

		gc.benchmark.stencilTest += System.currentTimeMillis() - start;
	}

	private void operation(final GLFragment fragment, final int operation) {
		if (operation == GL_KEEP)
			return;

		final int __GL_MAX_STENCIL_VALUE = ((1 << gc.stencilSize) - 1);

		final int value = buffer[fragment.x + fragment.y];
		final int newValue;

		switch (operation) {
		case GL_ZERO:

			newValue = 0;
			break;

		case GL_REPLACE:

			newValue = gc.state.stencil.reference;
			break;

		case GL_INVERT:

			newValue = ~value;
			break;

		case GL_INCR:

			if (value == __GL_MAX_STENCIL_VALUE) // clamp so no overflow occurs
				newValue = value;
			else
				newValue = value + 1;

			break;

		case GL_DECR:

			if (value == 0) // clamp so no underflow occurs
				newValue = 0;
			else
				newValue = value - 1;

			break;

		default:
			throw new GLInvalidEnumException("GLStencilBuffer.operation(GLFragment, int)");
		}

		buffer[fragment.x + fragment.y] = newValue & gc.state.stencil.writeMask;
	}
}
