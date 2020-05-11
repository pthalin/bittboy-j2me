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

package org.thenesis.pjogles.pixel;

import org.thenesis.pjogles.GLSoftwareContext;

/**
 * @author tdinneen
 */
public final class GLPixelTransferModeState {
	// Pixel scale and bias
	public float redScale = 1.0f;
	public float greenScale = 1.0f;
	public float blueScale = 1.0f;
	public float alphaScale = 1.0f;

	public float redBias = 0.0f;
	public float greenBias = 0.0f;
	public float blueBias = 0.0f;
	public float alphaBias = 0.0f;

	// Depth scale and bias
	public float depthScale = 1.0f;
	public float depthBias = 0.0f;

	public float postConvolutionRedScale = 1.0f;
	public float postConvolutionGreenScale = 1.0f;
	public float postConvolutionBlueScale = 1.0f;
	public float postConvolutionAlphaScale = 1.0f;

	public float postConvolutionRedBias = 0.0f;
	public float postConvolutionGreenBias = 0.0f;
	public float postConvolutionBlueBias = 0.0f;
	public float postConvolutionAlphaBias = 0.0f;

	public float postColorMatrixRedScale = 1.0f;
	public float postColorMatrixGreenScale = 1.0f;
	public float postColorMatrixBlueScale = 1.0f;
	public float postColorMatrixAlphaScale = 1.0f;

	public float postColorMatrixRedBias = 0.0f;
	public float postColorMatrixGreenBias = 0.0f;
	public float postColorMatrixBlueBias = 0.0f;
	public float postColorMatrixAlphaBias = 0.0f;

	public final float zoomX = 1.0f;
	public final float zoomY = 1.0f;

	public int indexShift = 0;
	public int indexOffset = 0;

	public boolean mapColor;
	public boolean mapStencil;

	public boolean hasTransfer(final GLSoftwareContext gc) {
		if (redScale != 1.0f || redBias != 0.0f || greenScale != 1.0f || greenBias != 0.0f || blueScale != 1.0f
				|| blueBias != 0.0f || alphaScale != 1.0f || alphaBias != 0.0f)
			return true;

		if (indexShift != 0 || indexOffset != 0)
			return true;

		if (mapColor)
			return true;

		if (gc.state.enables.convolution1D || gc.state.enables.convolution2D) {
			if (postConvolutionRedScale != 1.0f || postConvolutionRedBias != 0.0f || postConvolutionGreenScale != 1.0f
					|| postConvolutionGreenBias != 0.0f || postConvolutionBlueScale != 1.0f
					|| postConvolutionBlueBias != 0.0f || postConvolutionAlphaScale != 1.0f
					|| postConvolutionAlphaBias != 0.0f)
				return true;
		}

		// TOMD - not supporting postColorMatrix etc...

		return false;
	}
}
