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

package org.thenesis.pjogles.states;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.lighting.GLColor;

/**
 * @author tdinneen
 */
public final class GLColorBufferState implements GLConstants {
	/**
	 * Alpha function.  The alpha function is applied to the alpha color
	 * value and the reference value.  If it fails then the pixel is
	 * not rendered.
	 */
	public int alphaFunction = GL_ALWAYS;
	public float alphaReference;

	/**
	 * Alpha blending source and destination factors, blend op,
	 * and blend color.
	 */
	public int blendSrcRGB = GL_ONE;
	public int blendSrcA = GL_ONE;
	public int blendDstRGB = GL_ZERO;
	public int blendDstA = GL_ZERO;

	/**
	 * Constant blending color.
	 */
	public final GLColor blendColor = new GLColor(0.0f, 0.0f, 0.0f, 0.0f); // yep defaults to 0.0, 0.0, 0.0, 0.0 !!

	/**
	 * Blending equation.
	 */
	public int blendEquation = GL_FUNC_ADD_EXT;

	/**
	 * Logic op.
	 */
	public int logicOp = GL_COPY;

	/**
	 * Color to fill the color portion of the framebuffer when clear
	 * is called.
	 */
	public final GLColor clear = new GLColor();
	public float clearIndex = 0.0f; // index to use for glClear() when in color index mode

	/**
	 * Color index write valueMask.  The color values are masked with this
	 * value when writing to the frame buffer so that only the bits set
	 * in the valueMask are changed in the frame buffer.
	 */
	public int writeMask;

	/**
	 * RGBA write masks. These booleans enable or disable writing of
	 * the r, g, b, and a components.
	 */
	public boolean rMask = GL_TRUE, gMask = GL_TRUE, bMask = GL_TRUE, aMask = GL_TRUE;

	/**
	 * This state variable tracks which buffer(s) is being drawn into.
	 */
	public int drawBuffer = GL_FRONT;

	/**
	 * Draw buffer specified by user.  May be different from drawBuffer
	 * above.  If the user specifies GL_FRONT_LEFT, for example, then
	 * drawBuffer is set to GL_FRONT, and drawBufferReturn to
	 * GL_FRONT_LEFT.
	 */
	//public int drawBufferReturn;
	public final GLColorBufferState get() {
		final GLColorBufferState s = new GLColorBufferState();
		s.set(this);

		return s;
	}

	public final void set(final GLColorBufferState s) {
		alphaFunction = s.alphaFunction;
		alphaReference = s.alphaReference;

		blendSrcRGB = s.blendSrcRGB;
		blendSrcA = s.blendSrcA;
		blendDstRGB = s.blendDstRGB;
		blendDstA = s.blendDstA;

		logicOp = s.logicOp;

		clear.set(s.clear);
		clearIndex = s.clearIndex;

		writeMask = s.writeMask;

		rMask = s.rMask;
		gMask = s.gMask;
		bMask = s.bMask;
		aMask = s.aMask;

		drawBuffer = s.drawBuffer;
	}
}
