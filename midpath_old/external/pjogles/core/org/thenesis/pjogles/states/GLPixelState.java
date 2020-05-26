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
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.pixel.GLPixelTransferModeState;

/**
 * @author tdinneen
 */
public final class GLPixelState implements GLConstants {
	public final GLPixelTransferModeState transferMode = new GLPixelTransferModeState();

	/**
	 * Read buffer. Where pixel reads come from.
	 */
	public int readBuffer;

	/**
	 * May be different from readBuffer
	 * above, eg. if the user specifies GL_FRONT_LEFT, for example, then
	 * readBufferSrc is set to GL_FRONT.
	 */
	public int readBufferSrc;

	//public GLColorTableState colorTable[] = new GLColorTableState[__GL_NUM_COLOR_TABLE_TARGETS];
	//public GLConvolutionFilterState convolutionFilter[] = new GLConvolutionFilterState[__GL_NUM_CONVOLUTION_TARGETS];

	private GLPixelState() {
	}

	public GLPixelState(final GLSoftwareContext gc) {
		if (gc.doubleBuffer)
			readBuffer = GL_BACK;
		else
			readBuffer = GL_FRONT;
	}

	public final GLPixelState get() {
		final GLPixelState s = new GLPixelState();
		s.set(this);

		return s;
	}

	public final void set(final GLPixelState s) {
	}
}
