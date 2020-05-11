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

package org.thenesis.pjogles;

import java.util.Stack;

import org.thenesis.pjogles.buffers.GLFrameBuffer;
import org.thenesis.pjogles.pipeline.GLPipeline;

/**
 * A specialized interface for software GLContext's.
 *
 * @author tdinneen
 */
public final class GLSoftwareContext extends GLContext {

	public boolean madeCurrent = false; // set to true when glXMakeCurrent is called on this GLSoftwareContext
	// used to establish if this is the first time that this GLSoftwareContext
	// is being made current.

	public boolean isDirect; // direct mode rendering, ignore for now, will always be true
	// might implement an X-Windows based client/server at some stage

	//public Display currentDisplay;
	//private Surface currentSurface;

	// Max values

	public final int maxBufferSize = 8; // GLX_BUFFER_SIZE
	public final int maxLevel = 0; // GLX_LEVEL

	public final boolean hasRGB = true; // GLX_RGBA
	public final boolean hasDoubleBuffer = true; // GLX_DOUBLEBUFFER
	public final boolean hasStereo = false; // GLX_STEREO

	public final int maxAuxBuffers = 0; // GLX_AUX_BUFFERS

	public final int minRedSize = 8; // GLX_RED_SIZE
	public final int minGreenSize = 8; // GLX_GREEN_SIZE
	public final int minBlueSize = 8; // GLX_BLUE_SIZE
	public final int minAlphaSize = 8; // GLX_ALPHA_SIZE

	public final int minDepthSize = 24; // GLX_DEPTH_SIZE
	public final int minStencilSize = 8; // GLX_STENCIL_SIZE

	public final int minAccumRedSize = 8; // GLX_ACCUM_RED_SIZE
	public final int minAccumGreenSize = 8; // GLX_ACCUM_GREEN_SIZE
	public final int minAccumBlueSize = 8; // GLX_ACCUM_BLUE_SIZE
	public final int minAccumAlphaSize = 8; // GLX_ACCUM_ALPHA_SIZE

	public final int maxRedSize = 8; // GLX_RED_SIZE
	public final int maxGreenSize = 8; // GLX_GREEN_SIZE
	public final int maxBlueSize = 8; // GLX_BLUE_SIZE
	public final int maxAlphaSize = 8; // GLX_ALPHA_SIZE

	public final int maxDepthSize = 24; // GLX_DEPTH_SIZE
	public final int maxStencilSize = 8; // GLX_STENCIL_SIZE

	public final int maxAccumRedSize = 8; // GLX_ACCUM_RED_SIZE
	public final int maxAccumGreenSize = 8; // GLX_ACCUM_GREEN_SIZE
	public final int maxAccumBlueSize = 8; // GLX_ACCUM_BLUE_SIZE
	public final int maxAccumAlphaSize = 8; // GLX_ACCUM_ALPHA_SIZE

	/**
	 * Mode information that describes the kind of buffers and rendering
	 * modes that this context manages.
	 */

	public final int bufferSize = maxBufferSize; // GLX_BUFFER_SIZE
	public final int level = maxLevel; // GLX_LEVEL

	public final boolean rgb = hasRGB; // GLX_RGBA
	public final boolean doubleBuffer = hasDoubleBuffer; // GLX_DOUBLEBUFFER
	public final boolean stereo = hasStereo; // GLX_STEREO

	public final int auxBuffers = maxAuxBuffers; // GLX_AUX_BUFFERS

	public final int redSize = maxRedSize; // GLX_RED_SIZE
	public final int greenSize = maxGreenSize; // GLX_GREEN_SIZE
	public final int blueSize = maxBlueSize; // GLX_BLUE_SIZE
	public final int alphaSize = maxAlphaSize; // GLX_ALPHA_SIZE

	public final int depthSize = maxDepthSize; // GLX_DEPTH_SIZE
	public final int stencilSize = maxStencilSize; // GLX_STENCIL_SIZE

	public final int accumRedSize = maxAccumRedSize; // GLX_ACCUM_RED_SIZE
	public final int accumGreenSize = maxAccumGreenSize; // GLX_ACCUM_GREEN_SIZE
	public final int accumBlueSize = maxAccumBlueSize; // GLX_ACCUM_BLUE_SIZE
	public final int accumAlphaSize = maxAccumAlphaSize; // GLX_ACCUM_ALPHA_SIZE

	// derived
	public boolean haveDepthBuffer = depthSize != 0;
	public boolean haveAccumBuffer = (accumRedSize | accumGreenSize | accumBlueSize | accumAlphaSize) != 0;
	public boolean haveStencilBuffer = stencilSize != 0;

	protected GLContext shared;

	public final GLBenchmark benchmark = new GLBenchmark();

	public GLPipeline pipeline; // set in derived class

	// Pushattrib, Popattrib Stackable state.  All of the current user
	// controllable state is resident here.

	public GLAttribute state; // set in derived class
	public final Stack attributeStack = new Stack();

	public final GLClientAttribute clientState = new GLClientAttribute();
	public final Stack clientAttributeStack = new Stack();

	// Unstackable State

	/**
	 * Current glBegin mode.  Legal values are 0 (not in begin mode), 1
	 * (in beginMode), or 2 (not in begin mode, some validation is
	 * needed).  Because all state changing routines have to fetch this
	 * value, we have overloaded state validation into it.  There is
	 * special code in the glBegin() (for software renderers) which
	 * deals with validation.
	 */
	public int beginMode = NEED_VALIDATE;

	/**
	 * Current rendering mode
	 */
	public int renderMode = GL_RENDER;

	/**
	 * Mask word for validation state.
	 */
	public int validateMask = VALIDATE_ALL;

	public final GLTransformStack transform = new GLTransformStack();

	private GLBackend backend;

	public GLFrameBuffer frameBuffer; // set in derived class

	//    public GLSoftwareContext(final GLContext context)
	//    {
	//        super(context);
	//    }

	public GLSoftwareContext(GLSoftwareContext context, GLAttribute state) {
		isDirect = true;

		if (context != null) {
			shared = context.shared;
		}

		pipeline = new GLPipeline(this);

		this.state = new GLAttribute();
		this.state.initialize(this, state);

		frameBuffer = new GLFrameBuffer(this);
	}

	public final int setRenderMode(final int mode) {
		final int rv = pipeline.setRenderMode(renderMode, mode);
		renderMode = mode;

		return rv;
	}

	protected final void resizeBuffers(final int width, final int height) {
		frameBuffer.resize(width, height);
	}

	protected final void validate() {
		if ((validateMask & VALIDATE_BUFFER) != 0)
			frameBuffer.validate(this);

		if ((validateMask & VALIDATE_TEXTURE) != 0)
			state.texture.validate(this);

		validateMask = 0;
	}

	public GLBackend getBackend() {
		return backend;
	}

	public void setBackend(GLBackend backend) {
		this.backend = backend;
	}
}
