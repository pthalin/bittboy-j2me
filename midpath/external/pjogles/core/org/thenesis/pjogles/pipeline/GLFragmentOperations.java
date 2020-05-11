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

package org.thenesis.pjogles.pipeline;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.buffers.GLFrameBuffer;
import org.thenesis.pjogles.buffers.GLStencilBuffer;
import org.thenesis.pjogles.lighting.GLBlending;
import org.thenesis.pjogles.lighting.GLFogging;
import org.thenesis.pjogles.primitives.GLFragment;
import org.thenesis.pjogles.primitives.GLSpan;
import org.thenesis.pjogles.states.GLEnableState;

/**
 * A grouping of all the operations performed on fragments.
 * Stencil tests, followed by depth test, texturing, blending and fogging.
 *
 * @author tdinneen
 */
public final class GLFragmentOperations implements GLConstants {
	private final GLSoftwareContext gc;

	private final GLBlending blending;
	private final GLFogging fogging;

	public GLFragmentOperations(final GLSoftwareContext gc) {
		this.gc = gc;

		blending = new GLBlending(gc);
		fogging = new GLFogging(gc);
	}

	public final void apply(final GLSpan span) {
		final boolean stencilEnabled = gc.state.enables.stencil;
		final GLStencilBuffer stencilBuffer = gc.frameBuffer.stencilBuffer;

		// TOMD - note we don't do scissor tests !!!
		// TOMD - note we don't do alpha tests !!!

		// perform stencil tests, if we fail then return
		if (stencilEnabled && !stencilBuffer.testFunction(span)) {
			stencilBuffer.failOperation(span);
			return;
		}

		span.doZVal(); // need the z vals for depth testing

		// perform depth test, if we fail then return
		if (gc.state.enables.depthTest) {
			final int passed = gc.frameBuffer.depthBuffer.store(span);

			if (stencilEnabled)
				stencilBuffer.depthFailOperation(span);

			if (passed == 0)
				return;
		}

		if (stencilEnabled)
			stencilBuffer.depthPassOperation(span);

		span.doColor(); // need color vals before texturing

		final int[] drawBuffer = gc.frameBuffer.drawBuffer.buffer;

		// check if texturing enabled
		if (gc.state.texture.enabledUnits != 0) {
			span.doTexture(gc); // calc texture interps
			gc.pipeline.textureOperations.apply(span);
		}

		// check if blending enabled
		if (gc.state.enables.blend)
			blending.apply(span, drawBuffer);

		// check if fogging enabled
		if (gc.state.enables.fog) {
			span.doFog(gc); // calc fog interps
			fogging.apply(span);
		}

		// TODO - note we don't do logical operations !!!

		span.mapColor(gc, drawBuffer);

		System.arraycopy(span.c, 0, gc.frameBuffer.drawBuffer.buffer, span.offset, span.length);
	}

	public final void apply(final GLFragment fragment) {
		final GLFrameBuffer frameBuffer = gc.frameBuffer;
		final GLEnableState enables = gc.state.enables;

		final int x = fragment.x;
		final int y = fragment.y / frameBuffer.width; // TOMD - the whole fragment.y is the index to the y'th row
		// is a bit of a hack for the moment ?
		// perform scissor tests, if we fail then return
		/*if(enables.scissor)
		 {
		 if(x < frameBuffer.xmin || x >= frameBuffer.xmax)
		 return;

		 if(y < frameBuffer.ymin || y >= frameBuffer.ymax)
		 return;
		 }*/

		// TOMD - note we don't do alpha tests !!!
		// perform stencil tests, if we fail then return
		final boolean stencilEnabled = enables.stencil;
		final GLStencilBuffer stencilBuffer = frameBuffer.stencilBuffer;

		if (stencilEnabled && !stencilBuffer.testFunction(fragment)) {
			stencilBuffer.failOperation(fragment);
			return;
		}

		// perform depth test, if we fail then return
		if (enables.depthTest && (frameBuffer.depthBuffer.store(fragment) == false)) {
			if (stencilEnabled)
				stencilBuffer.depthFailOperation(fragment);

			return;
		}

		if (stencilEnabled)
			stencilBuffer.depthPassOperation(fragment);

		// check if texturing enabled
		//if (gc.textureManager.textureEnabled)
		//    gc.textureManager.calcTexture(fragment, color);
		//else
		//    color.set(fragment.color);

		// check if blending enabled
		//if (gc.state.enables.blend)
		//    blendColor(fragment, color);

		// check if fogging enabled
		//if (gc.state.enables.fog)
		//    fogColor(fragment, color);

		// TOMD - note we don't do texturing !!!
		// TOMD - note we don't do blending !!!
		// TOMD - note we don't do fogging !!!

		// TOMD - note we don't do logical operations !!!

		final int index = x + y;

		frameBuffer.drawBuffer.buffer[index] = fragment.color.getRGBAi(gc, frameBuffer.drawBuffer.buffer[index]);
	}
}
