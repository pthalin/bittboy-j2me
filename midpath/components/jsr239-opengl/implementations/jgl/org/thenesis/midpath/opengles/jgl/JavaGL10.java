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
package org.thenesis.midpath.opengles.jgl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Hashtable;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import jgl.GL;

import com.sun.jsr239.ContextAccess;
import com.sun.jsr239.GLConfiguration;

public class JavaGL10 implements GL10 {

	static final boolean DEBUG = false;

	private static final int MAX_TEXTURE_UNITS = 1;

	private GL gl;

	/**
	 * <code>context</code> is the context the GL10
	 * is created for. 
	 */
	EGLContext context = null;

	private static EGL10 egl;

	private Pointer colorPointer = new Pointer();
	private Pointer normalPointer = new Pointer();
	private Pointer texCoordPointer = new Pointer();
	private Pointer vertexPointer = new Pointer();

	private boolean isGLColorArrayEnabled = false;
	private boolean isGLNormalArrayEnabled = false;
	private boolean isGLTextureCoordArrayEnabled = false;
	private boolean isGLVertexArrayEnabled = false;

	/**
	 * IMPL_NOTE: <code>contextsByThread</code> may lead to the Java memory
	 * leaks (when a thread dies, an associated context will not be ever
	 * collected). As the possible workaround the following solution can be
	 * used: a private <code>Hashtable</code> member can be added for class
	 * <code>Thread</code> to keep a reference to the context. In this case
	 * the life span of a context will not be longer then that of a thread.
	 */
	// Map Thread -> EGLContext
	public static Hashtable contextsByThread = new Hashtable();

	//	 If GLConfiguration.singleThreaded is true, the context that is
	// current on the (single) native thread, or EGL_NO_CONTEXT.  If
	// singleThreaded is false, this variable has no meaning.
	public static EGLContext currentContext = EGL10.EGL_NO_CONTEXT;

	public JavaGL10(JavaEGLContext context) {
		this.context = context;
		gl = new jgl.GL(context.getJGLContext());
		egl = (EGL10) EGLContext.getEGL();
	}

	void checkThread() {

		Thread boundThread = ((ContextAccess) context).getBoundThread();
		if (Thread.currentThread() != boundThread) {
			throw new IllegalStateException("GL call from improper thread");
		}
	}

	/**
	 * Set the context associated with the current Java thread as the
	 * native context.  This is only necessary if we are on a
	 * single-threaded VM and the context is not already current.
	 */
	public static void grabContext() {
		if (!GLConfiguration.singleThreaded) {
			return;
		}

		// Locate the desired context for this Java thread
		Thread currentThread = Thread.currentThread();
		EGLContext newContext = (EGLContext) contextsByThread.get(currentThread);
		if (newContext == JavaGL10.currentContext) {
			return;
		}

		if (newContext != null) {
			EGLDisplay display = ((ContextAccess) newContext).getDisplay();
			EGLSurface draw = ((ContextAccess) newContext).getDrawSurface();
			EGLSurface read = ((ContextAccess) newContext).getReadSurface();
			egl.eglMakeCurrent(display, draw, read, newContext);
			JavaGL10.currentContext = newContext;
		}
	}

	GL getJGL() {
		return gl;
	}

	private float convertFPToFloat(int fp) {
		return ((float) fp) / 65536;
	}

	private int convertFloatToFP(float f) {
		return (int) (f * 65536);
	}

	/* GL10 interface */

	public void glActiveTexture(int texture) {
		checkThread();
		
		int textureUnit = texture - GL10.GL_TEXTURE0;

		if (textureUnit > MAX_TEXTURE_UNITS) {
			gl.throwGLError(GL.GL_INVALID_ENUM, "glActiveTexture");
			return;
		}

		// Note: Does nothing really useful as there is only one texture unit supported

	}

	public void glAlphaFunc(int func, float ref) {
		checkThread();
		if (DEBUG)
			System.out.println("GL10.glAlphaFunc() is not supported yet");
	}

	public void glAlphaFuncx(int func, int ref) {
		checkThread();
		if (DEBUG)
			System.out.println("GL10.glAlphaFuncx() is not supported yet");
	}

	public void glBindTexture(int target, int texture) {
		checkThread();
		gl.glBindTexture(target, texture);
	}

	public void glBlendFunc(int sfactor, int dfactor) {
		checkThread();
		gl.glBlendFunc(sfactor, dfactor);
	}

	public void glClear(int mask) {
		checkThread();
		gl.glClear(mask);
	}

	public void glClearColor(float red, float green, float blue, float alpha) {
		checkThread();
		gl.glClearColor(red, green, blue, alpha);
	}

	public void glClearColorx(int red, int green, int blue, int alpha) {
		//checkThread();
		glClearColor(convertFPToFloat(red), convertFPToFloat(green), convertFPToFloat(blue), convertFPToFloat(alpha));
	}

	public void glClearDepthf(float depth) {
		checkThread();
		gl.glClearDepth(depth);
	}

	public void glClearDepthx(int depth) {
		//checkThread();
		glClearDepthf(convertFPToFloat(depth));
	}

	public void glClearStencil(int s) {
		checkThread();
		gl.glClearStencil(s);
	}

	public void glClientActiveTexture(int texture) {
		checkThread();
		int textureUnit = texture - GL10.GL_TEXTURE0;
		if (textureUnit > MAX_TEXTURE_UNITS) {
			gl.throwGLError(GL.GL_INVALID_ENUM, "glClientActiveTexture");
			return;
		}

		// Note: Does nothing really useful as there is only one texture unit supported
	}

	public void glColor4f(float red, float green, float blue, float alpha) {
		checkThread();
		gl.glColor4f(red, green, blue, alpha);
	}

	public void glColor4x(int red, int green, int blue, int alpha) {
		checkThread();
		gl.glColor4f(convertFPToFloat(red), convertFPToFloat(green), convertFPToFloat(blue), convertFPToFloat(alpha));
	}

	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		checkThread();
		gl.glColorMask(red, green, blue, alpha);
	}

	public void glColorPointer(int size, int type, int stride, Buffer pointer) {
		checkThread();
		colorPointer.set(size, type, stride, pointer);
	}

	public void glCompressedTexImage2D(int target, int level, int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		checkThread();
		System.out.println("GL10.glCompressedTexImage2D() is not supported yet");
	}

	public void glCompressedTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height,
			int format, int imageSize, Buffer data) {
		checkThread();
		System.out.println("GL10.glCompressedTexSubImage2D() is not supported yet");
	}

	public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y, int width, int height,
			int border) {
		checkThread();
		gl.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}

	public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
		checkThread();
		gl.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	public void glCullFace(int mode) {
		checkThread();
		gl.glCullFace(mode);
	}

	public void glDeleteTextures(int n, int[] textures, int offset) {
		checkThread();
		int[] t = textures;
		if (offset > 0) {
			int length = textures.length - offset;
			t = new int[length];
			System.arraycopy(textures, offset, t, 0, length);
		}
		gl.glDeleteTextures(n, t);
	}

	public void glDeleteTextures(int n, IntBuffer textures) {
		//checkThread();
		glDeleteTextures(n, textures.array(), 0);
	}

	public void glDepthFunc(int func) {
		checkThread();
		gl.glDepthFunc(func);
	}

	public void glDepthMask(boolean flag) {
		checkThread();
		gl.glDepthMask(flag);
	}

	public void glDepthRangef(float zNear, float zFar) {
		checkThread();
		gl.glDepthRange(zNear, zFar);
	}

	public void glDepthRangex(int zNear, int zFar) {
		//checkThread();
		glDepthRangef(convertFPToFloat(zNear), convertFPToFloat(zFar));
	}

	public void glDisable(int cap) {
		checkThread();
		gl.glDisable(cap);
	}

	public void glDisableClientState(int array) {
		checkThread();
		switch (array) {
		case GL_COLOR_ARRAY:
			isGLColorArrayEnabled = false;
			break;
		case GL_NORMAL_ARRAY:
			isGLNormalArrayEnabled = false;
			break;
		case GL_TEXTURE_COORD_ARRAY:
			isGLTextureCoordArrayEnabled = false;
			break;
		case GL_VERTEX_ARRAY:
			isGLVertexArrayEnabled = false;
			break;
		}
	}

	private void drawElement(int indice) {
		checkThread();
		// Color
		if (isGLColorArrayEnabled) {
			if (colorPointer.type == GL_UNSIGNED_BYTE) {
				byte c0 = 0, c1 = 0, c2 = 0, c3 = 0;
				ByteBuffer vBuffer = (ByteBuffer) colorPointer.pointer;
				int offset = indice * (colorPointer.size + colorPointer.stride);
				vBuffer.position(offset);
				c0 = vBuffer.get();
				c1 = vBuffer.get();
				c2 = vBuffer.get();
				c3 = vBuffer.get();
				gl.glColor4b(c0, c1, c2, c3);
			} else if (colorPointer.type == GL_FIXED) {
				// FIXME
				if (colorPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				float c0 = 0, c1 = 0, c2 = 0, c3 = 0;
				IntBuffer vBuffer = (IntBuffer) colorPointer.pointer;
				int offset = indice * colorPointer.size;
				vBuffer.position(offset);
				c0 = convertFPToFloat(vBuffer.get());
				c1 = convertFPToFloat(vBuffer.get());
				c2 = convertFPToFloat(vBuffer.get());
				c3 = convertFPToFloat(vBuffer.get());
				gl.glColor4f(c0, c1, c2, c3);
			} else if (colorPointer.type == GL_FLOAT) {
				// FIXME
				if (colorPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				float c0 = 0, c1 = 0, c2 = 0, c3 = 0;
				FloatBuffer vBuffer = (FloatBuffer) colorPointer.pointer;
				int offset = indice * colorPointer.size;
				vBuffer.position(offset);
				c0 = vBuffer.get();
				c1 = vBuffer.get();
				c2 = vBuffer.get();
				c3 = vBuffer.get();
				gl.glColor4f(c0, c1, c2, c3);
			}
		}

		// Normal
		if (isGLNormalArrayEnabled) {
			if (normalPointer.type == GL_BYTE) {
				byte v0 = 0, v1 = 0, v2 = 0;
				ByteBuffer vBuffer = (ByteBuffer) normalPointer.pointer;
				int offset = indice * (normalPointer.size + normalPointer.stride);
				vBuffer.position(offset);
				v0 = vBuffer.get();
				v1 = vBuffer.get();
				v2 = vBuffer.get();
				gl.glNormal3b(v0, v1, v2);
			} else if (normalPointer.type == GL_SHORT) {
				// FIXME
				if (normalPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				short v0 = 0, v1 = 0, v2 = 0;
				ShortBuffer vBuffer = (ShortBuffer) normalPointer.pointer;
				int offset = indice * normalPointer.size;
				vBuffer.position(offset);
				v0 = vBuffer.get();
				v1 = vBuffer.get();
				v2 = vBuffer.get();
				gl.glNormal3s(v0, v1, v2);
			} else if (normalPointer.type == GL_FIXED) {
				// FIXME
				if (normalPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				float v0 = 0, v1 = 0, v2 = 0;
				IntBuffer vBuffer = (IntBuffer) normalPointer.pointer;
				int offset = indice * normalPointer.size;
				vBuffer.position(offset);
				v0 = convertFPToFloat(vBuffer.get());
				v1 = convertFPToFloat(vBuffer.get());
				v2 = convertFPToFloat(vBuffer.get());
				gl.glNormal3f(v0, v1, v2);
			} else if (normalPointer.type == GL_FLOAT) {
				// FIXME
				if (normalPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				float v0 = 0, v1 = 0, v2 = 0;
				FloatBuffer vBuffer = (FloatBuffer) normalPointer.pointer;
				int offset = indice * normalPointer.size;
				vBuffer.position(offset);
				v0 = vBuffer.get();
				v1 = vBuffer.get();
				v2 = vBuffer.get();
				gl.glNormal3f(v0, v1, v2);
			}
		}

		// Texture
		if (isGLTextureCoordArrayEnabled) {
			if (texCoordPointer.type == GL_BYTE) {
				int v0 = 0, v1 = 0, v2 = 0, v3 = 0;
				ByteBuffer vBuffer = (ByteBuffer) texCoordPointer.pointer;
				int offset = indice * (texCoordPointer.size + texCoordPointer.stride);
				vBuffer.position(offset);
				if (texCoordPointer.size == 2) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					gl.glTexCoord2i(v0, v1);
				} else if (texCoordPointer.size == 3) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					gl.glTexCoord3i(v0, v1, v2);
				} else if (texCoordPointer.size == 4) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					v3 = vBuffer.get();
					gl.glTexCoord4i(v0, v1, v2, v3);
				}
			} else if (texCoordPointer.type == GL_SHORT) {
				// FIXME
				if (texCoordPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				short v0 = 0, v1 = 0, v2 = 0, v3 = 0;
				ShortBuffer vBuffer = (ShortBuffer) texCoordPointer.pointer;
				int offset = indice * texCoordPointer.size;
				vBuffer.position(offset);
				if (texCoordPointer.size == 2) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					gl.glTexCoord2s(v0, v1);
				} else if (texCoordPointer.size == 3) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					gl.glTexCoord3s(v0, v1, v2);
				} else if (texCoordPointer.size == 4) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					v3 = vBuffer.get();
					gl.glTexCoord4s(v0, v1, v2, v3);
				}
			} else if (texCoordPointer.type == GL_FIXED) {
				// FIXME
				if (texCoordPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				float v0 = 0, v1 = 0, v2 = 0, v3 = 0;
				IntBuffer vBuffer = (IntBuffer) texCoordPointer.pointer;
				int offset = indice * texCoordPointer.size;
				vBuffer.position(offset);
				if (texCoordPointer.size == 2) {
					v0 = convertFPToFloat(vBuffer.get());
					v1 = convertFPToFloat(vBuffer.get());
					gl.glTexCoord2f(v0, v1);
				} else if (texCoordPointer.size == 3) {
					v0 = convertFPToFloat(vBuffer.get());
					v1 = convertFPToFloat(vBuffer.get());
					v2 = convertFPToFloat(vBuffer.get());
					gl.glTexCoord3f(v0, v1, v2);
				} else if (texCoordPointer.size == 4) {
					v0 = convertFPToFloat(vBuffer.get());
					v1 = convertFPToFloat(vBuffer.get());
					v2 = convertFPToFloat(vBuffer.get());
					v3 = convertFPToFloat(vBuffer.get());
					gl.glTexCoord4f(v0, v1, v2, v3);
				}
			} else if (texCoordPointer.type == GL_FLOAT) {
				// FIXME
				if (texCoordPointer.stride != 0) {
					throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
				}
				float v0 = 0, v1 = 0, v2 = 0, v3 = 0;
				FloatBuffer vBuffer = (FloatBuffer) texCoordPointer.pointer;
				int offset = indice * texCoordPointer.size;
				vBuffer.position(offset);
				if (texCoordPointer.size == 2) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					gl.glTexCoord2f(v0, v1);
				} else if (texCoordPointer.size == 3) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					gl.glTexCoord3f(v0, v1, v2);
				} else if (texCoordPointer.size == 4) {
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					v3 = vBuffer.get();
					gl.glTexCoord4f(v0, v1, v2, v3);
				}
			}
		}
		
		// Vertex
		if (isGLVertexArrayEnabled) {
			if (vertexPointer.size == 2) {
				if (vertexPointer.type == GL_BYTE) {
					int v0 = 0, v1 = 0;
					ByteBuffer vBuffer = (ByteBuffer) vertexPointer.pointer;
					int offset = indice * (vertexPointer.size + vertexPointer.stride);
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					gl.glVertex2i(v0, v1);
				} else if (vertexPointer.type == GL_SHORT) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					short v0 = 0, v1 = 0;
					ShortBuffer vBuffer = (ShortBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					gl.glVertex2s(v0, v1);
				} else if (vertexPointer.type == GL_FIXED) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					float v0 = 0, v1 = 0;
					IntBuffer vBuffer = (IntBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = convertFPToFloat(vBuffer.get());
					v1 = convertFPToFloat(vBuffer.get());
					gl.glVertex2f(v0, v1);
				} else if (vertexPointer.type == GL_FLOAT) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					float v0 = 0, v1 = 0;
					FloatBuffer vBuffer = (FloatBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					gl.glVertex2f(v0, v1);
				}
			} else if (vertexPointer.size == 3) {
				if (vertexPointer.type == GL_BYTE) {
					int v0 = 0, v1 = 0, v2 = 0;
					ByteBuffer vBuffer = (ByteBuffer) vertexPointer.pointer;
					int offset = indice * (vertexPointer.size + vertexPointer.stride);
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					gl.glVertex3i(v0, v1, v2);
				} else if (vertexPointer.type == GL_SHORT) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					short v0 = 0, v1 = 0, v2 = 0;
					ShortBuffer vBuffer = (ShortBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					gl.glVertex3s(v0, v1, v2);
				} else if (vertexPointer.type == GL_FIXED) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					float v0 = 0, v1 = 0, v2 = 0;
					IntBuffer vBuffer = (IntBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = convertFPToFloat(vBuffer.get());
					v1 = convertFPToFloat(vBuffer.get());
					v2 = convertFPToFloat(vBuffer.get());
					gl.glVertex3f(v0, v1, v2);
				} else if (vertexPointer.type == GL_FLOAT) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					float v0 = 0, v1 = 0, v2 = 0;
					FloatBuffer vBuffer = (FloatBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					gl.glVertex3f(v0, v1, v2);
				}
			} else if (vertexPointer.size == 4) {
				if (vertexPointer.type == GL_BYTE) {
					int v0 = 0, v1 = 0, v2 = 0, v3 = 0;
					ByteBuffer vBuffer = (ByteBuffer) vertexPointer.pointer;
					int offset = indice * (vertexPointer.size + vertexPointer.stride);
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					v3 = vBuffer.get();
					gl.glVertex4i(v0, v1, v2, v3);
				} else if (vertexPointer.type == GL_SHORT) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					short v0 = 0, v1 = 0, v2 = 0, v3 = 0;
					ShortBuffer vBuffer = (ShortBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					v3 = vBuffer.get();
					gl.glVertex4s(v0, v1, v2, v3);
				} else if (vertexPointer.type == GL_FIXED) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					float v0 = 0, v1 = 0, v2 = 0, v3 = 0;
					IntBuffer vBuffer = (IntBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = convertFPToFloat(vBuffer.get());
					v1 = convertFPToFloat(vBuffer.get());
					v2 = convertFPToFloat(vBuffer.get());
					v3 = convertFPToFloat(vBuffer.get());
					gl.glVertex4f(v0, v1, v2, v3);
				} else if (vertexPointer.type == GL_FLOAT) {
					// FIXME
					if (vertexPointer.stride != 0) {
						throw new UnsupportedOperationException("stride != 0 : not supported by this implementation");
					}
					float v0 = 0, v1 = 0, v2 = 0, v3 = 0;
					FloatBuffer vBuffer = (FloatBuffer) vertexPointer.pointer;
					int offset = indice * vertexPointer.size;
					vBuffer.position(offset);
					v0 = vBuffer.get();
					v1 = vBuffer.get();
					v2 = vBuffer.get();
					v3 = vBuffer.get();
					gl.glVertex4f(v0, v1, v2, v3);
				}
			}
		}
		
	}

	/** 
	 * Convert jGL rendering mode values to OpenGL ES compliant values 
	 * @param mode
	 * @return
	 */
	private int convertMode(int mode) {
		// Convert type
		int targetMode = -1;
		switch (mode) {
		case GL_POINTS:
			targetMode = GL.GL_POINTS;
			break;
		case GL_LINE_STRIP:
			targetMode = GL.GL_LINE_STRIP;
			break;
		case GL_LINE_LOOP:
			targetMode = GL.GL_LINE_LOOP;
			break;
		case GL_LINES:
			targetMode = GL.GL_LINES;
			break;
		case GL_TRIANGLES:
			targetMode = GL.GL_TRIANGLES;
			break;
		case GL_TRIANGLE_STRIP:
			targetMode = GL.GL_TRIANGLE_STRIP;
			break;
		case GL_TRIANGLE_FAN:
			targetMode = GL.GL_TRIANGLE_FAN;
			break;
		}

		return targetMode;
	}

	public void glDrawArrays(int mode, int first, int count) {
		checkThread();
		
		// Save current position of the buffers
		colorPointer.mark();
		vertexPointer.mark();
		normalPointer.mark();
		texCoordPointer.mark();
		
		int targetMode = convertMode(mode);
		gl.glBegin(targetMode);
		for (int i = 0; i < count; i++) {
			drawElement(first + i);
		}
		gl.glEnd();
		
		// Restore buffer positions
		colorPointer.reset();
		vertexPointer.reset();
		normalPointer.reset();
		texCoordPointer.reset();

	}

	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		checkThread();
		
		// Save current position of the buffers
		colorPointer.mark();
		vertexPointer.mark();
		normalPointer.mark();
		texCoordPointer.mark();
		int savedPosition = indices.position();
		
		int targetMode = convertMode(mode);
		gl.glBegin(targetMode);
		for (int i = 0; i < count; i++) {

			// Get the current indice
			int indice = 0;
			if (type == GL_UNSIGNED_BYTE) {
				indice = ((ByteBuffer) indices).get(i);
			} else if (type == GL_UNSIGNED_SHORT) {
				indice = ((ShortBuffer) indices).get(i);
			}
			drawElement(indice);
		}
		gl.glEnd();
		
		// Restore buffer positions
		colorPointer.reset();
		vertexPointer.reset();
		normalPointer.reset();
		texCoordPointer.reset();
		indices.position(savedPosition);

	}

	public void glEnable(int cap) {
		checkThread();
		gl.glEnable(cap);
	}

	public void glEnableClientState(int array) {
		checkThread();
		switch (array) {
		case GL_COLOR_ARRAY:
			isGLColorArrayEnabled = true;
			break;
		case GL_NORMAL_ARRAY:
			isGLNormalArrayEnabled = true;
			break;
		case GL_TEXTURE_COORD_ARRAY:
			isGLTextureCoordArrayEnabled = true;
			break;
		case GL_VERTEX_ARRAY:
			isGLVertexArrayEnabled = true;
			break;
		}

	}

	public void glFinish() {
		//checkThread();
		glFlush();
	}

	public void glFlush() {
		checkThread();
		gl.glFlush();
	}

	public void glFogf(int pname, float param) {
		checkThread();
		if (DEBUG)
			System.out.println("[DEBUG] GL10.glFogf() is not supported");
	}

	public void glFogfv(int pname, float[] params, int offset) {
		checkThread();
		if (DEBUG)
			System.out.println("[DEBUG] GL10.glFogfv() is not supported");
	}

	public void glFogfv(int pname, FloatBuffer params) {
		//checkThread();
		glFogfv(pname, params.array(), 0);
	}

	public void glFogx(int pname, int param) {
		checkThread();
		if (DEBUG)
			System.out.println("[DEBUG] GL10.glFogfx() is not supported");
	}

	public void glFogxv(int pname, int[] params, int offset) {
		checkThread();
		if (DEBUG)
			System.out.println("[DEBUG] GL10.glFogxv() is not supported");
	}

	public void glFogxv(int pname, IntBuffer params) {
		//checkThread();
		glFogxv(pname, params.array(), 0);
	}

	public void glFrontFace(int mode) {
		checkThread();
		gl.glFrontFace(mode);
	}

	public void glFrustumf(float left, float right, float bottom, float top, float near, float far) {
		checkThread();
		gl.glFrustum(left, right, bottom, top, near, far);
	}

	public void glFrustumx(int left, int right, int bottom, int top, int near, int far) {
		//checkThread();
		glFrustumf(convertFPToFloat(left), convertFPToFloat(right), convertFPToFloat(bottom), convertFPToFloat(top),
				convertFPToFloat(near), convertFPToFloat(far));
	}

	public void glGenTextures(int n, int[] textures, int offset) {
		checkThread();
		int[] t = textures;
		if (offset > 0) {
			int length = textures.length - offset;
			t = new int[length];
			System.arraycopy(textures, offset, t, 0, length);
		}
		gl.glGenTextures(n, t);
	}

	public void glGenTextures(int n, IntBuffer textures) {
		//checkThread();
		glGenTextures(n, textures.array(), 0);
	}

	public int glGetError() {
		checkThread();
		return gl.glGetError();
	}

	public void glGetIntegerv(int pname, int[] params, int offset) {
		checkThread();
		switch (pname) {
		case GL_ALIASED_POINT_SIZE_RANGE:
			params[offset] = 1;
			params[offset + 1] = 1;
			break;
		case GL_ALIASED_LINE_WIDTH_RANGE:
			params[offset] = 1;
			params[offset + 1] = 1;
			break;
		case GL_ALPHA_BITS:
			params[offset] = 8;
			break;
		case GL_BLUE_BITS:
			params[offset] = 8;
			break;
		case GL_COMPRESSED_TEXTURE_FORMATS:
			params[offset] = GL_PALETTE4_RGB8_OES;
			params[offset + 1] = GL_PALETTE4_RGBA8_OES;
			params[offset + 2] = GL_PALETTE4_R5_G6_B5_OES;
			params[offset + 3] = GL_PALETTE4_RGBA4_OES;
			params[offset + 4] = GL_PALETTE4_RGB5_A1_OES;
			params[offset + 5] = GL_PALETTE8_RGB8_OES;
			params[offset + 6] = GL_PALETTE8_RGBA8_OES;
			params[offset + 7] = GL_PALETTE8_R5_G6_B5_OES;
			params[offset + 8] = GL_PALETTE8_RGBA4_OES;
			params[offset + 9] = GL_PALETTE8_RGB5_A1_OES;
			break;
		case GL_DEPTH_BITS:
			params[offset] = 32; // FIXME ?
			break;
		case GL_GREEN_BITS:
			params[offset] = 8;
			break;
		case GL_IMPLEMENTATION_COLOR_READ_FORMAT_OES:
			params[offset] = GL_RGBA;
			break;
		case GL_IMPLEMENTATION_COLOR_READ_TYPE_OES:
			params[offset] = GL_UNSIGNED_BYTE;
			break;
		case GL_MAX_ELEMENTS_INDICES:
			params[offset] = 4096;
			break;
		case GL_MAX_ELEMENTS_VERTICES:
			params[offset] = 4096;
			break;
		case GL_MAX_LIGHTS:
			params[offset] = 8;
			break;
		case GL_MAX_MODELVIEW_STACK_DEPTH:
			params[offset] = 16;
			break;
		case GL_MAX_PROJECTION_STACK_DEPTH:
			params[offset] = 2;
			break;
		case GL_MAX_TEXTURE_SIZE:
			params[offset] = 256;
			break;
		case GL_MAX_TEXTURE_STACK_DEPTH:
			params[offset] = 2;
			break;
		case GL_MAX_TEXTURE_UNITS:
			params[offset] = MAX_TEXTURE_UNITS;
			break;
		case GL_MAX_VIEWPORT_DIMS:
			params[offset] = 1024;
			params[offset + 1] = 1024;
			break;
		case GL_NUM_COMPRESSED_TEXTURE_FORMATS:
			params[offset] = 10;
			break;
		case GL_RED_BITS:
			params[offset] = 8;
			break;
		case GL_SMOOTH_LINE_WIDTH_RANGE:
			params[offset] = 1;
			params[offset + 1] = 1;
			break;
		case GL_SMOOTH_POINT_SIZE_RANGE:
			params[offset] = 1;
			params[offset + 1] = 1;
			break;
		case GL_STENCIL_BITS:
			params[offset] = 24; // FIXME ?
			break;
		case GL_SUBPIXEL_BITS:
			params[offset] = 4; // FIXME ?
			break;

		}

		// FIXME Handle the offset
		//gl.glGetIntegerv(pname, params);
	}

	public void glGetIntegerv(int pname, IntBuffer params) {
		//checkThread();
		int[] b = new int[params.remaining()];
		glGetIntegerv(pname, b, 0);
		params.put(b);
	}

	public String glGetString(int name) {
		checkThread();
		return gl.glGetString(name);
	}

	public void glHint(int target, int mode) {
		checkThread();
		if (DEBUG)
			System.out.println("glHint() is not supported");
	}

	public void glLightModelf(int pname, float param) {
		checkThread();
		gl.glLightModelf(pname, param);
	}

	public void glLightModelfv(int pname, float[] params, int offset) {
		checkThread();
		float[] p = params;
		if (offset > 0) {
			int length = params.length - offset;
			p = new float[length];
			System.arraycopy(params, offset, p, 0, length);
		}
		gl.glLightModelfv(pname, p);
	}

	public void glLightModelfv(int pname, FloatBuffer params) {
		//checkThread();
		glLightModelfv(pname, params.array(), 0);
	}

	public void glLightModelx(int pname, int param) {
		checkThread();
		gl.glLightModelf(pname, convertFPToFloat(param));
	}

	public void glLightModelxv(int pname, int[] params, int offset) {
		checkThread();
		int length = params.length - offset;
		float[] p = new float[length];
		for (int i = 0; i < length; i++) {
			p[i] = convertFPToFloat(params[i + offset]);
		}
		gl.glLightModelfv(pname, p);
	}

	public void glLightModelxv(int pname, IntBuffer params) {
		//checkThread();
		glLightModelxv(pname, params.array(), 0);
	}

	public void glLightf(int light, int pname, float param) {
		checkThread();
		gl.glLightf(light, pname, param);
	}

	public void glLightfv(int light, int pname, float[] params, int offset) {
		checkThread();
		float[] p = params;
		if (offset > 0) {
			int length = params.length - offset;
			p = new float[length];
			System.arraycopy(params, offset, p, 0, length);
		}
		gl.glLightfv(light, pname, p);
	}

	public void glLightfv(int light, int pname, FloatBuffer params) {
		//checkThread();
		glLightfv(light, pname, params.array(), 0);
	}

	public void glLightx(int light, int pname, int param) {
		checkThread();
		gl.glLightf(light, pname, convertFPToFloat(param));
	}

	public void glLightxv(int light, int pname, int[] params, int offset) {
		checkThread();
		int length = params.length - offset;
		float[] p = new float[length];
		for (int i = 0; i < length; i++) {
			p[i] = convertFPToFloat(params[i + offset]);
		}
		gl.glLightfv(light, pname, p);
	}

	public void glLightxv(int light, int pname, IntBuffer params) {
		//checkThread();
		glLightxv(light, pname, params.array(), 0);
	}

	public void glLineWidth(float width) {
		checkThread();
		gl.glLineWidth(width);
	}

	public void glLineWidthx(int width) {
		//checkThread();
		glLineWidth(convertFPToFloat(width));
	}

	public void glLoadIdentity() {
		checkThread();
		gl.glLoadIdentity();
	}

	public void glLoadMatrixf(float[] m, int offset) {
		checkThread();
		float[] p = m;
		if (offset > 0) {
			int length = m.length - offset;
			p = new float[length];
			System.arraycopy(m, offset, p, 0, length);
		}
		gl.glLoadMatrixf(p);
	}

	public void glLoadMatrixf(FloatBuffer m) {
		//checkThread();
		glLoadMatrixf(m.array(), 0);
	}

	public void glLoadMatrixx(int[] m, int offset) {
		checkThread();
		int length = m.length - offset;
		float[] fm = new float[length];
		for (int i = 0; i < length; i++) {
			fm[i] = convertFPToFloat(m[i + offset]);
		}
		gl.glLoadMatrixf(fm);
	}

	public void glLoadMatrixx(IntBuffer m) {
		//checkThread();
		glLoadMatrixx(m.array(), 0);
	}

	public void glLogicOp(int opcode) {
		checkThread();
		//	TODO
		System.out.println("glLogicOp() is not supported yet");
	}

	public void glMaterialf(int face, int pname, float param) {
		checkThread();
		gl.glMaterialf(face, pname, param);
	}

	public void glMaterialfv(int face, int pname, float[] params, int offset) {
		checkThread();
		gl.glMaterialfv(face, pname, params);
	}

	public void glMaterialfv(int face, int pname, FloatBuffer params) {
		//checkThread();
		glMaterialfv(face, pname, params.array(), 0);
	}

	public void glMaterialx(int face, int pname, int param) {
		checkThread();
		gl.glMaterialf(face, pname, convertFPToFloat(param));
	}

	public void glMaterialxv(int face, int pname, int[] params, int offset) {
		checkThread();
		int length = params.length - offset;
		float[] fm = new float[length];
		for (int i = 0; i < length; i++) {
			fm[i] = convertFPToFloat(params[i + offset]);
		}
		gl.glMaterialfv(face, pname, fm);
	}

	public void glMaterialxv(int face, int pname, IntBuffer params) {
		//checkThread();
		glMaterialxv(face, pname, params.array(), 0);
	}

	public void glMatrixMode(int mode) {
		checkThread();
		gl.glMatrixMode(mode);
	}

	public void glMultMatrixf(float[] m, int offset) {
		checkThread();
		float[] p = m;
		if (offset > 0) {
			int length = m.length - offset;
			p = new float[length];
			System.arraycopy(m, offset, p, 0, length);
		}
		gl.glMultMatrixf(p);
	}

	public void glMultMatrixf(FloatBuffer m) {
		//checkThread();
		glMultMatrixf(m.array(), 0);
	}

	public void glMultMatrixx(int[] m, int offset) {
		//checkThread();
		float[] fm = new float[m.length];
		for (int i = 0; i < m.length; i++) {
			fm[i] = convertFPToFloat(m[i]);
		}
		glMultMatrixf(fm, offset);
	}

	public void glMultMatrixx(IntBuffer m) {
		checkThread();
		glMultMatrixx(m.array(), 0);
	}

	public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
		checkThread();
		gl.glTexCoord4f(s, t, r, q);
	}

	public void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
		checkThread();
		gl.glTexCoord4f(convertFPToFloat(s), convertFPToFloat(t), convertFPToFloat(r), convertFPToFloat(q));
	}

	public void glNormal3f(float nx, float ny, float nz) {
		checkThread();
		gl.glNormal3f(nx, ny, nz);
	}

	public void glNormal3x(int nx, int ny, int nz) {
		checkThread();
		gl.glNormal3f(convertFPToFloat(nx), convertFPToFloat(ny), convertFPToFloat(nz));
	}

	public void glNormalPointer(int type, int stride, Buffer pointer) {
		checkThread();
		normalPointer.set(3, type, stride, pointer);
	}

	public void glOrthof(float left, float right, float bottom, float top, float near, float far) {
		checkThread();
		gl.glOrtho(left, right, bottom, top, near, far);
	}

	public void glOrthox(int left, int right, int bottom, int top, int near, int far) {
		//checkThread();
		glOrthof(convertFPToFloat(left), convertFPToFloat(right), convertFPToFloat(bottom), convertFPToFloat(top),
				convertFPToFloat(near), convertFPToFloat(far));
	}

	public void glPixelStorei(int pname, int param) {
		checkThread();
		gl.glPixelStorei(pname, param);
	}

	public void glPointSize(float size) {
		checkThread();
		gl.glPointSize(size);
	}

	public void glPointSizex(int size) {
		//checkThread();
		glPointSize(convertFPToFloat(size));
	}

	public void glPolygonOffset(float factor, float units) {
		checkThread();
		if (DEBUG)
			System.out.println("glPolygonOffset() is not supported yet");
	}

	public void glPolygonOffsetx(int factor, int units) {
		checkThread();
		if (DEBUG)
			System.out.println("glPolygonOffsetx() is not supported yet");
	}

	public void glPopMatrix() {
		checkThread();
		gl.glPopMatrix();
	}

	public void glPushMatrix() {
		checkThread();
		gl.glPushMatrix();
	}

	public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels) {
		checkThread();
		
		if ((width < 0) || (height < 0)) {
			gl.throwGLError(jgl.GL.GL_INVALID_VALUE, "glReadPixels: width or height is negative");
			return;
		}

		if ((format != GL_RGBA) || (type != GL_UNSIGNED_BYTE)) {
			gl.throwGLError(jgl.GL.GL_INVALID_ENUM, "glReadPixels");
			gl.throwGLError(jgl.GL.GL_INVALID_OPERATION, "glReadPixels");
			return;
		}

		byte[][][] pixelArray = new byte[width][height][4];
		gl.glReadPixels(x, y, width, height, jgl.GL.GL_RGBA, jgl.GL.GL_UNSIGNED_BYTE, pixelArray);

		if (pixels instanceof ByteBuffer) {
			ByteBuffer buffer = (ByteBuffer) pixels;
			for (int j = 0; j < height; j++) {
				int offset = j * width;
				for (int i = 0; i < width; i++) {
					buffer.position(offset + i * 4);
					buffer.put(pixelArray[i][j][0]);
					buffer.put(pixelArray[i][j][1]);
					buffer.put(pixelArray[i][j][2]);
					buffer.put(pixelArray[i][j][3]);
				}
			}
		}

		// TODO Add IntBuffer and FloatBuffer support

	}

	public void glRotatef(float angle, float x, float y, float z) {
		checkThread();
		gl.glRotatef(angle, x, y, z);
	}

	public void glRotatex(int angle, int x, int y, int z) {
		//checkThread();
		glRotatef(convertFPToFloat(angle), convertFPToFloat(x), convertFPToFloat(y), convertFPToFloat(z));
	}

	public void glSampleCoverage(float value, boolean invert) {
		checkThread();
		//	TODO
		if (DEBUG)
			System.out.println("glSampleCoverage() is not supported yet");
	}

	public void glSampleCoveragex(int value, boolean invert) {
		checkThread();
		// TODO
		if (DEBUG)
			System.out.println("glSampleCoveragex() is not supported yet");
	}

	public void glScalef(float x, float y, float z) {
		checkThread();
		gl.glScalef(x, y, z);
	}

	public void glScalex(int x, int y, int z) {
		//checkThread();
		glScalef(convertFPToFloat(x), convertFPToFloat(y), convertFPToFloat(z));
	}

	public void glScissor(int x, int y, int width, int height) {
		checkThread();
		if (DEBUG)
			System.out.println("[DEBUG] glScissor() is not supported yet");
	}

	public void glShadeModel(int mode) {
		checkThread();
		gl.glShadeModel(mode);
	}

	public void glStencilFunc(int func, int ref, int mask) {
		checkThread();
		gl.glStencilFunc(func, ref, mask);
	}

	public void glStencilMask(int mask) {
		checkThread();
		gl.glStencilMask(mask);
	}

	public void glStencilOp(int fail, int zfail, int zpass) {
		checkThread();
		gl.glStencilOp(fail, zfail, zpass);
	}

	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
		checkThread();
		texCoordPointer.set(size, type, stride, pointer);
	}

	public void glTexEnvf(int target, int pname, float param) {
		checkThread();
		gl.glTexEnvf(target, pname, param);
	}

	public void glTexEnvfv(int target, int pname, float[] params, int offset) {
		checkThread();
		float[] p = params;
		if (offset > 0) {
			int length = params.length - offset;
			p = new float[length];
			System.arraycopy(params, offset, p, 0, length);
		}
		gl.glTexEnvfv(target, pname, p);
	}

	public void glTexEnvfv(int target, int pname, FloatBuffer params) {
		checkThread();
		glTexEnvfv(target, pname, params.array(), 0);
	}

	public void glTexEnvx(int target, int pname, int param) {
		checkThread();
		gl.glTexEnvf(target, pname, convertFPToFloat(param));
	}

	public void glTexEnvxv(int target, int pname, int[] params, int offset) {
		checkThread();
		int length = params.length - offset;
		float[] fm = new float[length];
		for (int i = 0; i < length; i++) {
			fm[i] = convertFPToFloat(params[i + offset]);
		}
		gl.glTexEnvfv(target, pname, fm);
	}

	public void glTexEnvxv(int target, int pname, IntBuffer params) {
		//checkThread();
		glTexEnvxv(target, pname, params.array(), 0);
	}

	public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format,
			int type, Buffer pixels) {
		checkThread();
		
		// Save current position of the buffer
		int savedPosition = pixels.position();
		
		byte[][][] pArray = new byte[width][height][4];

		// Convert image pixels to GL_RGBA/GL_UNSIGNED_BYTE
		switch (type) {
		case GL_UNSIGNED_BYTE:
			switch (format) {
			case GL_RGB: {
				ByteBuffer byteBuf = (ByteBuffer) pixels;
				for (int j = 0; j < height; j++) {
					//byteBuf.position(j * width * 3);
					for (int i = 0; i < width; i++) {
						pArray[i][j][0] = byteBuf.get();
						pArray[i][j][1] = byteBuf.get();
						pArray[i][j][2] = byteBuf.get();
						pArray[i][j][3] = (byte) 0xFF;
					}
				}
			}
				break;
			case GL_RGBA: {
				ByteBuffer byteBuf = (ByteBuffer) pixels;
				for (int j = 0; j < height; j++) {
					//byteBuf.position(j * width * 4);
					for (int i = 0; i < width; i++) {
						pArray[i][j][0] = byteBuf.get();
						pArray[i][j][1] = byteBuf.get();
						pArray[i][j][2] = byteBuf.get();
						pArray[i][j][3] = byteBuf.get();
					}
				}
			}
				break;
			case GL_ALPHA: {
				ByteBuffer byteBuf = (ByteBuffer) pixels;
				for (int j = 0; j < height; j++) {
					//byteBuf.position(j * width);
					for (int i = 0; i < width; i++) {
						pArray[i][j][0] = 0;
						pArray[i][j][1] = 0;
						pArray[i][j][2] = 0;
						pArray[i][j][3] = byteBuf.get();
					}
				}
			}
				break;
			case GL_LUMINANCE: {
				ByteBuffer byteBuf = (ByteBuffer) pixels;
				for (int j = 0; j < height; j++) {
					//byteBuf.position(j * width);
					for (int i = 0; i < width; i++) {
						byte val = byteBuf.get();
						pArray[i][j][0] = val;
						pArray[i][j][1] = val;
						pArray[i][j][2] = val;
						pArray[i][j][3] = (byte) 0xFF;
					}
				}
			}
				break;
			case GL_LUMINANCE_ALPHA: {
				ByteBuffer byteBuf = (ByteBuffer) pixels;
				for (int j = 0; j < height; j++) {
					//byteBuf.position(j * width);
					for (int i = 0; i < width; i++) {
						byte val = byteBuf.get();
						pArray[i][j][0] = val;
						pArray[i][j][1] = val;
						pArray[i][j][2] = val;
						pArray[i][j][3] = byteBuf.get();
					}
				}
			}
				break;
			} // switch
			break;
		case GL_UNSIGNED_SHORT_5_6_5: {
			ShortBuffer shortBuf = (ShortBuffer) pixels;
			for (int j = 0; j < height; j++) {
				//byteBuf.position(j * width * 2);
				for (int i = 0; i < width; i++) {
					int rgb = shortBuf.get();
					int r = (rgb >> 11) & 0x1f;
					int g = (rgb >> 5) & 0x3f;
					int b = rgb & 0x1f;
					// Replicate the high bits into the low bits 
					pArray[i][j][0] = (byte) (((r << 3) | (r >> 2)) & 0xff);
					pArray[i][j][1] = (byte) (((g << 2) | (g >> 4)) & 0xff);
					pArray[i][j][2] = (byte) (((b << 3) | (b >> 2)) & 0xff);
					pArray[i][j][3] = (byte) 0xff;
				}
			}
		}
			break;
		case GL_UNSIGNED_SHORT_4_4_4_4: {
			ShortBuffer shortBuf = (ShortBuffer) pixels;
			for (int j = 0; j < height; j++) {
				//byteBuf.position(j * width * 2);
				for (int i = 0; i < width; i++) {
					int rgb = shortBuf.get();
					int r = (rgb >> 12) & 0xF;
					int g = (rgb >> 8) & 0xF;
					int b = (rgb >> 4) & 0xF;
					int a = rgb & 0xF;
					// Replicate the high bits into the low bits 
					pArray[i][j][0] = (byte) ((r << 4) | r);
					pArray[i][j][1] = (byte) ((g << 4) | g);
					pArray[i][j][2] = (byte) ((b << 4) | b);
					pArray[i][j][3] = (byte) ((a << 4) | a);
				}
			}
		}
			break;
		case GL_UNSIGNED_SHORT_5_5_5_1: {
			ShortBuffer shortBuf = (ShortBuffer) pixels;
			for (int j = 0; j < height; j++) {
				//byteBuf.position(j * width * 2);
				for (int i = 0; i < width; i++) {
					int rgb = shortBuf.get();
					int r = (rgb >> 11) & 0x1F;
					int g = (rgb >> 6) & 0x1F;
					int b = (rgb >> 1) & 0x1F;
					// Replicate the high bits into the low bits 
					pArray[i][j][0] = (byte) ((r << 3) | (r >> 2));
					pArray[i][j][1] = (byte) ((g << 3) | (g >> 2));
					pArray[i][j][2] = (byte) ((b << 3) | (b >> 2));
					pArray[i][j][3] = (byte) ((rgb & 0x1) << 8);
				}
			}
		}
			break;
		}
		
		// Restore buffer position
		pixels.position(savedPosition);

		gl.glTexImage2D(target, level, GL_RGBA, width, height, border, GL_RGBA, GL_UNSIGNED_BYTE, pArray);
	}

	private int getCompatibleTexParameter(int param) {
		checkThread();
		if (param == GL_CLAMP_TO_EDGE) {
			return GL.GL_CLAMP;
		}
		return param;
	}

	public void glTexParameterf(int target, int pname, float param) {
		checkThread();
		gl.glTexParameterf(target, pname, getCompatibleTexParameter((int) param));
	}

	public void glTexParameterx(int target, int pname, int param) {
		checkThread();
		gl.glTexParameterf(target, pname, getCompatibleTexParameter((int) convertFPToFloat(param)));
	}

	public void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format,
			int type, Buffer pixels) {
		checkThread();
		gl.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	public void glTranslatef(float x, float y, float z) {
		checkThread();
		gl.glTranslatef(x, y, z);
	}

	public void glTranslatex(int x, int y, int z) {
		//checkThread();
		glTranslatef(convertFPToFloat(x), convertFPToFloat(y), convertFPToFloat(z));
	}

	public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		checkThread();
		vertexPointer.set(size, type, stride, pointer);
	}

	public void glViewport(int x, int y, int width, int height) {
		checkThread();
		gl.glViewport(x, y, width, height);

	}

	/* GL10 Ext interface */

	//	public int glQueryMatrixxOES(int[] mantissa, int mantissaOffset, int[] exponent, int exponentOffset) {
	//		System.out.println("glVertexPointer() is not supported yet");
	//		return 0;
	//	}
	private class Pointer {

		int size;
		int type;
		int stride;
		Buffer pointer;
		private int markPosition;

		public void set(int size, int type, int stride, Buffer pointer) {
			this.size = size;
			this.type = type;
			this.stride = stride;
			this.pointer = pointer;
		}
		
		public void mark() {
			if (pointer != null) {
				markPosition = pointer.position();
			}
		}
		
		public void reset() {
			if (pointer != null) {
				pointer.position(markPosition);
			}
		}
	}

	

}
