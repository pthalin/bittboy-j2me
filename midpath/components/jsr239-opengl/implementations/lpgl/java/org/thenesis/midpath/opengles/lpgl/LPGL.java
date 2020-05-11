/*
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package org.thenesis.midpath.opengles.lpgl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.sun.jsr239.Errors;
import com.sun.jsr239.GLConfiguration;

public class LPGL {

	static final boolean DEBUG = false;

	/* Parameter constants */

	// Common
	public static final int GPGL_TYPE_U8 = 0;
	public static final int GPGL_TYPE_S8 = 1;
	public static final int GPGL_TYPE_U16 = 2;
	public static final int GPGL_TYPE_S16 = 3;
	public static final int GPGL_TYPE_S32 = 4;
	// Error management
	public static final int GPGL_ERROR_NONE = 0;
	public static final int GPGL_ERROR_INVALID_PARAMETER = 1;
	// Global
	public static final int GPGL_SURFACES = 0x00000001;
	public static final int GPGL_FRAMEBUFFER = 0x00000002;
	public static final int GPGL_PIXEL = 0x00000004;
	public static final int GPGL_GEOMETRY = 0x00000008;
	public static final int GPGL_PRIMITIVE = 0x00000010;
	public static final int GPGL_LIGHTING = 0x00000020;
	public static final int GPGL_SHADING = 0x00000040;
	public static final int GPGL_TEXTURING = 0x00000080;
	public static final int GPGL_ALL = 0xffffffff;
	// Surface
	public static final int GPGL_MAX_SURFACES = 256;
	public static final int GPGL_SURFACE_FLAG_MIPMAP = 0x00000001;
	public static final int GPGL_PIXEL_FORMAT_I = 0; // 8-bit indexed.
	public static final int GPGL_PIXEL_FORMAT_L = 1; // 8-bit luminance.
	public static final int GPGL_PIXEL_FORMAT_AL = 2; // 8-bit alpha and luminance.
	public static final int GPGL_PIXEL_FORMAT_RGB = 3; // 8-bit RGB.
	public static final int GPGL_PIXEL_FORMAT_ARGB = 4; // 8-bit ARGB.
	public static final int GPGL_PIXEL_FORMAT_Z = 5; // 32-bit depth.
	public static final int GPGL_PIXEL_FORMATS = 6; // 8-bit indexed.
	// Framebuffer surfaces
	public static final int GPGL_MAX_WIDTH = 65536;
	public static final int GPGL_MAX_HEIGHT = 65536;
	// Pixel operations on framebuffer
	public static final int GPGL_FACTOR_0 = 0;
	public static final int GPGL_FACTOR_1 = 1;
	public static final int GPGL_FACTOR_CS = 2;
	public static final int GPGL_FACTOR_1CS = 3;
	public static final int GPGL_FACTOR_CD = 4;
	public static final int GPGL_FACTOR_1CD = 5;
	public static final int GPGL_FACTOR_AS = 6;
	public static final int GPGL_FACTOR_1AS = 7;
	public static final int GPGL_FACTOR_AD = 8;
	public static final int GPGL_FACTOR_1AD = 9;
	public static final int GPGL_FACTOR_CC = 10;
	public static final int GPGL_FACTOR_1CC = 11;
	public static final int GPGL_FACTOR_AC = 12;
	public static final int GPGL_FACTOR_1AC = 13;
	public static final int GPGL_FACTORS = 14;
	public static final int GPGL_NEVER = 0;
	public static final int GPGL_ALWAYS = 1;
	public static final int GPGL_EQUAL = 2;
	public static final int GPGL_NOT_EQUAL = 3;
	public static final int GPGL_LESS = 4;
	public static final int GPGL_LESS_EQUAL = 5;
	public static final int GPGL_GREATER_EQUAL = 6;
	public static final int GPGL_GREATER = 7;
	public static final int GPGL_TESTS = 8;
	// Geometry
	public static final int GPGL_CULLING_NONE = 0;
	public static final int GPGL_CULLING_CW = 1;
	public static final int GPGL_CULLING_CCW = 2;
	// Primitive
	public static final int GPGL_PRIMITIVE_POINT_LIST = 0;
	public static final int GPGL_PRIMITIVE_LINE_LIST = 1;
	public static final int GPGL_PRIMITIVE_LINE_STRIP = 2;
	public static final int GPGL_PRIMITIVE_LINE_LOOP = 3;
	public static final int GPGL_PRIMITIVE_TRIANGLE_LIST = 4;
	public static final int GPGL_PRIMITIVE_TRIANGLE_STRIP = 5;
	public static final int GPGL_PRIMITIVE_TRIANGLE_FAN = 6;
	public static final int GPGL_PRIMITIVE_QUAD_LIST = 7;
	public static final int GPGL_PRIMITIVE_QUAD_STRIP = 8;
	public static final int GPGL_PRIMITIVE_COUNT = 9;
	public static final int GPGL_VERTEX_POSITION = 0;
	public static final int GPGL_VERTEX_NORMAL = 1;
	public static final int GPGL_VERTEX_COLOR_DIFFUSE = 2;
	public static final int GPGL_VERTEX_COLOR_SPECULAR = 3;
	public static final int GPGL_VERTEX_TEXTURE_0 = 4;
	public static final int GPGL_VERTEX_TEXTURE_1 = 5;
	public static final int GPGL_VERTEX_TEXTURE_2 = 6;
	public static final int GPGL_VERTEX_TEXTURE_3 = 7;
	public static final int GPGL_VERTEX_ATTRIBUTES = 8;
	// Lighting
	public static final int GPGL_LIGHT_MAX = 8;
	public static final int GPGL_LIGHT_DIRECTIONAL = 0;
	public static final int GPGL_LIGHT_POINT = 1;
	public static final int GPGL_LIGHT_SPOT = 2;
	// Pixel shading
	public static final int GPGL_SHADING_FLAT = 0;
	public static final int GPGL_SHADING_SMOOTH = 1;
	// Texturing
	public static final int GPGL_TEXTURE_MAX = 4;
	public static final int GPGL_TEXTURE_FLAG_WRAP_U = 0x00000001;
	public static final int GPGL_TEXTURE_FLAG_WRAP_V = 0x00000002;
	public static final int GPGL_TEXTURE_FLAG_MIRROR_U = 0x00000004;
	public static final int GPGL_TEXTURE_FLAG_MIRROR_V = 0x00000008;
	public static final int GPGL_TEXTURE_FLAG_MIPMAP = 0x00000010;
	public static final int GPGL_FILTER_NEAREST = 0;
	public static final int GPGL_FILTER_BILINEAR = 0;
	public static final int GPGL_FILTER_TRILINEAR = 0;
	public static final int GPGL_TEXTURE_REPLACE = 0;
	public static final int GPGL_TEXTURE_MODULATE = 1;
	public static final int GPGL_TEXTURE_DECAL = 2;
	public static final int GPGL_TEXTURE_ADD = 3;
	public static final int GPGL_TEXTURE_BLEND = 4;

	/* Commands */

	// Framebuffer surfaces
	public static final int CMD_SET_CONTEXT = 1;
	public static final int CMD_SET_COLOR_BUFFER = 2;
	public static final int CMD_SET_DEPTH_BUFFER = 3;
	public static final int CMD_SET_STENCIL_BUFFER = 4;
	public static final int CMD_CLEAR_COLOR_BUFFER = 5;
	public static final int CMD_CLEAR_DEPTH_BUFFER = 6;
	public static final int CMD_CLEAR_STENCIL_BUFFER = 7;
	// Pixel operations on framebuffer
	public static final int CMD_SET_SCISSOR_TEST = 8;
	public static final int CMD_ENABLE_BLENDING = 9;
	public static final int CMD_SET_BLENDING_MODE = 10;
	public static final int CMD_ENABLE_COLOR_WRITE = 11;
	public static final int CMD_SET_DEPTH_TEST = 12;
	public static final int CMD_ENABLE_DEPTH_WRITE = 13;
	public static final int CMD_SET_STENCIL_TEST = 14;
	public static final int CMD_ENABLE_STENCIL_WRITE = 15;
	// Geometry
	public static final int CMD_SET_PROJECTION_MATRIX = 16;
	public static final int CMD_SET_MODEL_VIEW_MATRIX = 17;
	public static final int CMD_SET_VIEWPORT = 18;
	public static final int CMD_SET_DEPTH_RANGE = 19;
	public static final int CMD_SET_CULLING_MODE = 20;
	// Primitive    
	public static final int CMD_ENABLE_ARRAYS = 21;
	public static final int CMD_SET_VERTEX_ARRAY = 22;
	public static final int CMD_DRAW_RANGE = 23;
	public static final int CMD_DRAW_INDEXED = 24;
	// Lighting
	public static final int CMD_ENABLE_LIGHTING = 25;
	public static final int CMD_ENABLE_LIGHTS = 26;
	public static final int CMD_SET_MATERIAL = 27;
	public static final int CMD_SET_LIGHT = 28;
	// Shading
	public static final int CMD_SET_SHADING_MODE = 29;
	// Texturing
	public static final int CMD_ENABLE_TEXTURING = 30;
	public static final int CMD_ENABLE_TEXTURES = 31;
	public static final int CMD_SET_TEXTURE = 32;
	// Fog
	public static final int CMD_ENABLE_FOG = 33;
	public static final int CMD_SET_FOG_TABLE = 34;
	public static final int CMD_SET_FOG_RANGE = 35;

	static final String[] commandNames = { null, "CMD_ACTIVE_TEXTURE", "ALPHA_FUNC", "ALPHA_FUNCX", "BIND_BUFFER",
			"BIND_TEXTURE", "BLEND_FUNC", "BUFFER_DATA", "BUFFER_SUB_DATA", "CLEAR", "CLEAR_COLOR", "CLEAR_COLORX",
			"CLEAR_DEPTHF", "CLEAR_DEPTHX", "CLEAR_STENCIL", "CLIENT_ACTIVE_TEXTURE", "CLIP_PLANEF", "CLIP_PLANEFB",
			"CLIP_PLANEX", "CLIP_PLANEXB", "COLOR4F", "COLOR4X", "COLOR4UB", "COLOR_MASK", "COLOR_POINTER",
			"COLOR_POINTER_VBO", "COMPRESSED_TEX_IMAGE_2D", "COMPRESSED_TEX_SUB_IMAGE_2D", "COPY_TEX_IMAGE_2D",
			"COPY_TEX_SUB_IMAGE_2D", "CULL_FACE", "CURRENT_PALETTE_MATRIX", "DELETE_BUFFERS", "DELETE_BUFFERSB",
			"DELETE_TEXTURES", "DELETE_TEXTURESB", "DEPTH_FUNC", "DEPTH_MASK", "DEPTH_RANGEF", "DEPTH_RANGEX",
			"DISABLE", "DISABLE_CLIENT_STATE", "DRAW_ARRAYS", "DRAW_ELEMENTSB", "DRAW_ELEMENTS_VBO", "DRAW_TEXF",
			"DRAW_TEXFB", "DRAW_TEXI", "DRAW_TEXIB", "DRAW_TEXS", "DRAW_TEXSB", "DRAW_TEXX", "DRAW_TEXXB", "ENABLE",
			"ENABLE_CLIENT_STATE", "FOGF", "FOGFB", "FOGFV", "FOGX", "FOGXB", "FOGXV", "FRONT_FACE", "FRUSTUMF",
			"FRUSTUMX", "HINT", "LIGHTF", "LIGHTFB", "LIGHTFV", "LIGHTX", "LIGHTXB", "LIGHTXV", "LIGHT_MODELF",
			"LIGHT_MODELFB", "LIGHT_MODELFV", "LIGHT_MODELX", "LIGHT_MODELXB", "LIGHT_MODELXV", "LINE_WIDTH",
			"LINE_WIDTHX", "LOAD_IDENTITY", "LOAD_MATRIXF", "LOAD_MATRIXFB", "LOAD_MATRIXX", "LOAD_MATRIXXB",
			"LOAD_PALETTE_FROM_MODEL_VIEW_MATRIX", "LOGIC_OP", "MATERIALF", "MATERIALFB", "MATERIALFV", "MATERIALX",
			"MATERIALXB", "MATERIALXV", "MATRIX_INDEX_POINTER", "MATRIX_INDEX_POINTER_VBO", "MATRIX_MODE",
			"MULTI_TEXT_COORD4F", "MULTI_TEXT_COORD4X", "MULT_MATRIXF", "MULT_MATRIXFB", "MULT_MATRIXX",
			"MULT_MATRIXXB", "NORMAL3F", "NORMAL3X", "NORMAL_POINTER", "NORMAL_POINTER_VBO", "ORTHOF", "ORTHOX",
			"PIXEL_STOREI", "POINT_PARAMETERF", "POINT_PARAMETERFB", "POINT_PARAMETERFV", "POINT_PARAMETERX",
			"POINT_PARAMETERXB", "POINT_PARAMETERXV", "POINT_SIZE", "POINT_SIZEX", "POINT_SIZE_POINTER",
			"POINT_SIZE_POINTER_VBO", "POLYGON_OFFSET", "POLYGON_OFFSETX", "POP_MATRIX", "PUSH_MATRIX", "ROTATEF",
			"ROTATEX", "SAMPLE_COVERAGE", "SAMPLE_COVERAGEX", "SCALEF", "SCALEX", "SCISSOR", "SHADE_MODEL",
			"STENCIL_FUNC", "STENCIL_MASK", "STENCIL_OP", "TEX_COORD_POINTER", "TEX_COORD_POINTER_VBO", "TEX_ENVF",
			"TEX_ENVFB", "TEX_ENVFV", "TEX_ENVI", "TEX_ENVIB", "TEX_ENVIV", "TEX_ENVX", "TEX_ENVXB", "TEX_ENVXV",
			"TEX_IMAGE_2D", "TEX_PARAMETERF", "TEX_PARAMETERFB", "TEX_PARAMETERFV", "TEX_PARAMETERI",
			"TEX_PARAMETERIB", "TEX_PARAMETERIV", "TEX_PARAMETERX", "TEX_PARAMETERXB", "TEX_PARAMETERXV",
			"TEX_SUB_IMAGE_2D", "TRANSLATEF", "TRANSLATEX", "VERTEX_POINTER", "VERTEX_POINTER_VBO", "VIEWPORT",
			"WEIGHT_POINTER", "WEIGHT_POINTER_VBO", "FINISH", "FLUSH", "TEX_GENF", "TEX_GENI", "TEX_GENX", "TEX_GENFB",
			"TEX_GENIB", "TEX_GENXB", "TEX_GENFV", "TEX_GENIV", "TEX_GENXV", "BLEND_EQUATION", "BLEND_FUNC_SEPARATE",
			"BLEND_EQUATION_SEPARATE", "BIND_RENDERBUFFER", "DELETE_RENDERBUFFERS", "DELETE_RENDERBUFFERSB",
			"GEN_RENDERBUFFERSB", "RENDERBUFFER_STORAGE", "BIND_FRAMEBUFFER", "DELETE_FRAMEBUFFERS",
			"DELETE_FRAMEBUFFERSB", "GEN_FRAMEBUFFERSB", "FRAMEBUFFER_TEXTURE2D", "FRAMEBUFFER_RENDERBUFFER",
			"GENERATE_MIPMAP", "GEN_BUFFERSB", "GEN_TEXTURESB" };

	public static final int QUEUE_SIZE = 4096;
	
	private VertexArray[] vertexArrays = new VertexArray[GPGL_VERTEX_ATTRIBUTES];

	/** 
	 * Execute a queue of commands
	 * @param queue
	 * @param count
	 */
	native void execute(int[] queue, int count);

	int[] queue = new int[QUEUE_SIZE];
	int index = 0;
	int commandsLeft;

	void throwIAE(String message) {
		throw new IllegalArgumentException(message);
	}

	public void flushQueue() {
		if (DEBUG) {
			System.out.println("Flushing the queue, index = " + index);
		}
		execute(queue, index);

		// Reset queue
		index = 0;

		// Put the context on top of the queue
		enqueue(CMD_SET_CONTEXT, 1);
		enqueue(context.contextHandle);

	}

	void enqueue(int cmd, int count) {
		if (index + count + 1 >= GLConfiguration.COMMAND_QUEUE_SIZE) {
			flushQueue();
		}
		if (DEBUG) {
			System.out.println("Queueing command " + commandNames[cmd] + " with " + count + " args from thread "
					+ Thread.currentThread());
		}
		queue[index++] = cmd;
		commandsLeft = count;
	}

	void enqueue(int i) {
		queue[index++] = i;

		if (DEBUG) {
			System.out.println("Queueing integer " + i + " from thread " + Thread.currentThread());
		}

		if (GLConfiguration.flushQueueAfterEachCommand && (--commandsLeft == 0)) {
			if (DEBUG) {
				System.out.println("Last arg for command, flushing");
			}
			flushQueue();
		}
	}

	void enqueue(Buffer buf) {
		enqueue(pointer(buf));

		if (DEBUG) {
			System.out.println("Queueing buffer pointer " + pointer(buf) + " from thread " + Thread.currentThread());
		}

		if (GLConfiguration.flushQueueAfterEachCommand && (--commandsLeft == 0)) {
			if (DEBUG) {
				System.out.println("Last arg for command, flushing");
			}
			flushQueue();
		}
	}

	// offset will be shifted according to the buffer datatype
	// and added to the native base address
	static native int _getNativeAddress(Buffer buffer, int offset);

	int pointer(Buffer buffer) {
		int offset = buffer.position();
		int nativeAddress = _getNativeAddress(buffer, offset);

		return nativeAddress;
	}

	int offset(ByteBuffer buffer) {
		return buffer.arrayOffset() + buffer.position();
	}

	int offset(ShortBuffer buffer) {
		return buffer.arrayOffset() + buffer.position();
	}

	int offset(IntBuffer buffer) {
		return buffer.arrayOffset() + buffer.position();
	}

	int offset(FloatBuffer buffer) {
		return buffer.arrayOffset() + buffer.position();
	}

	boolean isDirect(Buffer buf) {
		if (buf instanceof ByteBuffer) {
			return ((ByteBuffer) buf).isDirect();
		} else if (buf instanceof ShortBuffer) {
			return ((ShortBuffer) buf).isDirect();
		} else if (buf instanceof IntBuffer) {
			return ((IntBuffer) buf).isDirect();
		} else if (buf instanceof FloatBuffer) {
			return ((FloatBuffer) buf).isDirect();
		} else {
			throw new IllegalArgumentException(Errors.GL_UNKNOWN_BUFFER);
		}
	}

	Buffer createDirectCopy(Buffer data) {
		int length = data.remaining();

		ByteBuffer direct;
		if (data instanceof ByteBuffer) {
			direct = ByteBuffer.allocateDirect(length);
			direct.put((ByteBuffer) data);
			return direct;
		} else if (data instanceof ShortBuffer) {
			direct = ByteBuffer.allocateDirect(2 * length);
			ShortBuffer directShort = direct.asShortBuffer();
			directShort.put((ShortBuffer) data);
			return directShort;
		} else if (data instanceof IntBuffer) {
			direct = ByteBuffer.allocateDirect(4 * length);
			IntBuffer directInt = direct.asIntBuffer();
			directInt.put((IntBuffer) data);
			return directInt;
		} else if (data instanceof FloatBuffer) {
			direct = ByteBuffer.allocateDirect(4 * length);
			FloatBuffer directFloat = direct.asFloatBuffer();
			directFloat.put((FloatBuffer) data);
			return directFloat;
		} else {
			throw new IllegalArgumentException(Errors.GL_UNKNOWN_BUFFER);
		}
	}

	/**
	 * Utility for common error checking.
	 * 
	 * @exception <code>IllegalArgumentException</code> if
	 * <code>param</code> is <code>null</code> or shorter than
	 * <code>length</code>.
	 */
	void checkLength(boolean[] params, int length, int offset) {
		if (params == null) {
			throwIAE(Errors.GL_PARAMS_NULL);
		}
		if (offset < 0) {
			throwIAE(Errors.GL_OFFSET_NEGATIVE);
		}
		if (params.length - offset < length) {
			throwIAE(Errors.GL_BAD_LENGTH);
		}
	}

	/**
	 * Utility for common error checking.
	 * 
	 * @exception <code>IllegalArgumentException</code> if
	 * <code>param</code> is <code>null</code> or shorter than
	 * <code>length</code>.
	 */
	void checkLength(short[] params, int length, int offset) {
		if (params == null) {
			throwIAE(Errors.GL_PARAMS_NULL);
		}
		if (offset < 0) {
			throwIAE(Errors.GL_OFFSET_NEGATIVE);
		}
		if (params.length - offset < length) {
			throwIAE(Errors.GL_BAD_LENGTH);
		}
	}

	/**
	 * Utility for common error checking.
	 * 
	 * @exception <code>IllegalArgumentException</code> if
	 * <code>param</code> is <code>null</code> or shorter than
	 * <code>length</code>.
	 */
	void checkLength(int[] params, int length, int offset) {
		if (params == null) {
			throwIAE(Errors.GL_PARAMS_NULL);
		}
		if (offset < 0) {
			throwIAE(Errors.GL_OFFSET_NEGATIVE);
		}
		if (params.length - offset < length) {
			throwIAE(Errors.GL_BAD_LENGTH);
		}
	}

	/**
	 * Utility for common error checking.
	 * 
	 * @exception <code>IllegalArgumentException</code> if
	 * <code>param</code> is <code>null</code> or shorter than
	 * <code>length</code>.
	 */
	void checkLength(float[] params, int length, int offset) {
		if (params == null) {
			throwIAE(Errors.GL_PARAMS_NULL);
		}
		if (offset < 0) {
			throwIAE(Errors.GL_OFFSET_NEGATIVE);
		}
		if (params.length - offset < length) {
			throwIAE(Errors.GL_BAD_LENGTH);
		}
	}

	/**
	 * Utility for common error checking.
	 * 
	 * @exception <code>IllegalArgumentException</code> if
	 * <code>param</code> is <code>null</code> or shorter than
	 * <code>length</code>.
	 */
	void checkLength(Buffer params, int length) {
		if (params == null) {
			throwIAE(Errors.GL_PARAMS_NULL);
		}
		if (params.remaining() < length) {
			throwIAE(Errors.GL_BAD_LENGTH);
		}
	}

	/*	
	 * LPGL methods 
	 */

	/*	Context */
	public native int getContextSize(int maxWidth, int maxHeight);

	public class Context {
		int contextHandle;
	};

	Context context;

	public class Surface {
		int surfaceHandle;
		int flags;
		int pixelFormat;
		int width, height;
		int stride;
		int address;
	};

	public void createContext(int maxWidth, int maxHeight, ByteBuffer contextBuffer) {
		context = new Context();
		context.contextHandle = createLPGLContext(maxWidth, maxHeight, contextBuffer);
	}

	private native int createLPGLContext(int maxWidth, int maxHeight, ByteBuffer buffer);

	public void destroyContext() {
		destroyContext(context.contextHandle);
	}

	private native void destroyContext(int contextHandle);

	/* Error management */
	public int getLastError(StringBuffer sBuffer) {
		flush();
		int error = getLastError(context.contextHandle, sBuffer);
		return error;
	}

	private native int getLastError(int contextHandle, StringBuffer sBuffer);

	public void clearLastError() {
		clearLastError(context.contextHandle);
	}

	private native void clearLastError(int contextHandle);

	/* Global */
	public void reset(int flags) {
		reset(context.contextHandle, flags);
	}

	public native void reset(int contextHandle, int flags);

	public void flush() {
		flush(context.contextHandle);
	}

	public native void flush(int contextHandle);

	/* Framebuffer surfaces. */

	public void setFrameBufferSize(int width, int height) {
		setFrameBufferSize(context.contextHandle, width, height);
	}

	public native void setFrameBufferSize(int contextHandle, int width, int height);

	public void setColorBuffer(Surface surface) {
		enqueue(CMD_SET_COLOR_BUFFER, 1);
		enqueue(surface.surfaceHandle);
		flushQueue();
	}

	public void setDepthBuffer(Surface surface) {
		enqueue(CMD_SET_DEPTH_BUFFER, 1);
		enqueue(surface.surfaceHandle);
		flushQueue();
	}

	public void setStencilBuffer(Surface surface) {
		enqueue(CMD_SET_STENCIL_BUFFER, 1);
		enqueue(surface.surfaceHandle);
		flushQueue();
	}

	public void clearColorBuffer(int r, int g, int b, int a) {
		enqueue(CMD_CLEAR_COLOR_BUFFER, 4);
		enqueue(r);
		enqueue(g);
		enqueue(b);
		enqueue(a);
	}

	public void clearDepthBuffer(int z) {
		enqueue(CMD_CLEAR_COLOR_BUFFER, 1);
		enqueue(z);
	}

	public void clearStencilBuffer(int v) {
		enqueue(CMD_CLEAR_COLOR_BUFFER, 1);
		enqueue(v);
	}

	/*	 Pixel operations on framebuffer. */

	public void setScissorTest(int x, int y, int w, int h) {
		enqueue(CMD_SET_SCISSOR_TEST, 4);
		enqueue(x);
		enqueue(y);
		enqueue(w);
		enqueue(h);
	}

	public void enableBlending(int flag) {
		enqueue(CMD_ENABLE_BLENDING, 1);
		enqueue(flag);
	}

	public void setBlendingMode(int srcBlendingFactor, int dstBlendingFactor) {
		enqueue(CMD_SET_BLENDING_MODE, 2);
		enqueue(srcBlendingFactor);
		enqueue(dstBlendingFactor);
	}

	public void enableColorWrite(int flag) {
		enqueue(CMD_ENABLE_BLENDING, 1);
		enqueue(flag);

	}

	public void setDepthTest(int testMode) {
		enqueue(CMD_SET_DEPTH_TEST, 1);
		enqueue(testMode);
	}

	public void enableDepthWrite(int flag) {
		enqueue(CMD_ENABLE_DEPTH_WRITE, 1);
		enqueue(flag);
	}

	public void setStencilTest(int mode) {
		enqueue(CMD_SET_STENCIL_TEST, 1);
		enqueue(mode);
	}

	public void enableStencilWrite(char flag) {
		enqueue(CMD_ENABLE_STENCIL_WRITE, 1);
		enqueue(flag);
	}

	/*	 Geometry */

	void setProjectionMatrix(int[] matrix) {
		enqueue(CMD_SET_PROJECTION_MATRIX, 16);
		for (int i = 0; i < 16; i++) {
			enqueue(matrix[i]);
		}
	}

	void setModelViewMatrix(int[] matrix) {
		enqueue(CMD_SET_MODEL_VIEW_MATRIX, 16);
		for (int i = 0; i < 16; i++) {
			enqueue(matrix[i]);
		}
	}

	void setViewport(int x, int y, int w, int h) {
		enqueue(CMD_SET_VIEWPORT, 4);
		enqueue(x);
		enqueue(y);
		enqueue(w);
		enqueue(h);
	}

	void setDepthRange(int near, int far) {
		enqueue(CMD_SET_DEPTH_RANGE, 2);
		enqueue(near);
		enqueue(far);
	}

	void setCullingMode(int cullingMode) {
		enqueue(CMD_SET_CULLING_MODE, 1);
		enqueue(cullingMode);
	}

	/* Primitive */

	private class VertexArray {

		int size;
		int type;
		int stride;
		IntBuffer pointer;

		public void set(int size, int stride, IntBuffer pointer) {
			this.size = size;
			this.stride = stride;
			this.pointer = pointer;
		}
	}

	public void enableArrays(int flags) {
		enqueue(CMD_ENABLE_ARRAYS, 1);
		enqueue(flags);
	}

	public void setVertexArray(int index, int stride, IntBuffer vertexBuffer) {

		if (!isDirect(vertexBuffer)) {
			throw new IllegalArgumentException("Buffer is not direct");
		}

		// Keep a copy of the vertex array infos
		vertexArrays[index] = new VertexArray();
		// FIXME set size to correct values
		vertexArrays[index].set(0, stride, vertexBuffer);

		enqueue(CMD_SET_VERTEX_ARRAY, 3);
		enqueue(index);
		enqueue(stride);
		enqueue(vertexBuffer);

		//qflush();

	}

	public void drawRange(int primitiveType, int start, int length) {

		// TODO check bounds

		enqueue(CMD_DRAW_RANGE, 3);
		enqueue(primitiveType);
		enqueue(start);
		enqueue(length);
		// qflush();
	}

	public void drawIndexed(int primitiveType, IntBuffer indices, int offset, int length) {
		// TODO check bounds

		enqueue(CMD_DRAW_INDEXED, 4);
		enqueue(primitiveType);
		enqueue(indices);
		enqueue(offset);
		enqueue(length);
		// qflush();

	}

	/* Lighting */

	class LightParameters {
		int lightType;
		int[] position = new int[3];
		int[] direction = new int[3];
		int cutOffAngle;
		int intensity;
		int[] color = new int[3];
	}

	class MaterialParameters {
		int[] ambientColor = new int[3];
		int[] diffuseColor = new int[3];
		int[] specularColor = new int[3];
		int specularExponent;
	}

	void enableLighting(int flag) {
		enqueue(CMD_ENABLE_LIGHTING, 1);
		enqueue(flag);
	}

	void enableLights(int flags) {
		enqueue(CMD_ENABLE_LIGHTS, 1);
		enqueue(flags);
	}

	void setMaterial(MaterialParameters ml) {
		enqueue(CMD_SET_MATERIAL, 10);
		for (int i = 0; i < 3; i++) {
			enqueue(ml.ambientColor[i]);
		}
		for (int i = 0; i < 3; i++) {
			enqueue(ml.diffuseColor[i]);
		}
		for (int i = 0; i < 3; i++) {
			enqueue(ml.specularColor[i]);
		}
		enqueue(ml.specularExponent);

	}

	void setLight(int unit, LightParameters lp) {
		enqueue(CMD_SET_LIGHT, 12);
		enqueue(lp.lightType);
		for (int i = 0; i < 3; i++) {
			enqueue(lp.position[i]);
		}
		for (int i = 0; i < 3; i++) {
			enqueue(lp.direction[i]);
		}
		enqueue(lp.cutOffAngle);
		enqueue(lp.intensity);
		for (int i = 0; i < 3; i++) {
			enqueue(lp.color[i]);
		}
	}

	/* Shading */

	void setShadingMode(int shadingMode) {
		enqueue(CMD_SET_SHADING_MODE, 1);
		enqueue(shadingMode);
	}

	/* Texturing */

	void enableTexturing(int flag) {
		enqueue(CMD_ENABLE_TEXTURING, 1);
		enqueue(flag);
	}

	void enableTextures(int flags) {
		enqueue(CMD_ENABLE_TEXTURES, 1);
		enqueue(flags);
	}

	void setTexture(int unit, int flags, int textureFilter, int textureBlending, Surface s) {
		enqueue(CMD_SET_TEXTURE, 4);
		enqueue(flags);
		enqueue(textureFilter);
		enqueue(textureBlending);
		enqueue(s.surfaceHandle);
	}

	/*	Fog */

	void enableFog(int flag) {
		enqueue(CMD_ENABLE_FOG, 1);
		enqueue(flag);
	}

	void setFogTable(int offset, int size, int[] color) {
		enqueue(CMD_SET_FOG_TABLE, size + 1);
		enqueue(size);
		for (int i = 0; i < size; i++) {
			enqueue(color[offset + i]);
		}
	}

	void setFogRange(int start, int end) {
		enqueue(CMD_SET_FOG_RANGE, 2);
		enqueue(start);
		enqueue(end);
	}

}
