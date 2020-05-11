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

/**
 * @author tdinneen
 */
public final class GLStencilState implements GLConstants {
	/**
	 * Stencil test function.  When the stencil is enabled this
	 * function is applied to the reference value and the stored stencil
	 * value as follows:
	 * <pre>
	 *		result = ref comparision (valueMask & stencilBuffer[x][y])
	 * </pre>
	 * If the test fails then the fail op is applied and rendering of
	 * the pixel stops.
	 */
	public int testFunc = GL_ALWAYS;

	/**
	 * Stencil clear value.  Used by glClear.
	 */
	public int clear = 0;

	/**
	 * Reference stencil value.
	 */
	public int reference = 0;

	/**
	 * Stencil valueMask.  This is anded against the contents of the stencil
	 * buffer during comparisons.
	 */
	public int valueMask = -1; // all bits set to 1

	/**
	 * Stencil write valueMask
	 */
	public int writeMask = -1; // all bits set to 1

	/**
	 * When the stencil comparison fails this operation is applied to
	 * the stencil buffer.
	 */
	public int fail = GL_KEEP;

	/**
	 * When the stencil comparison passes and the depth test
	 * fails this operation is applied to the stencil buffer.
	 */
	public int depthFail = GL_KEEP;

	/**
	 * When both the stencil comparison passes and the depth test
	 * passes this operation is applied to the stencil buffer.
	 */
	public int depthPass = GL_KEEP;

	public final GLStencilState get() {
		final GLStencilState s = new GLStencilState();
		s.set(this);

		return s;
	}

	public final void set(final GLStencilState s) {
		testFunc = s.testFunc;
		clear = s.clear;
		reference = s.reference;
		valueMask = s.valueMask;
		writeMask = s.writeMask;
		fail = s.fail;
		depthFail = s.depthFail;
		depthPass = s.depthPass;
	}
}
