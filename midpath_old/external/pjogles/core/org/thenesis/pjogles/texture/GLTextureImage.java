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

package org.thenesis.pjogles.texture;

import org.thenesis.pjogles.math.GLMath;

/**
 * @author tdinneen
 */
public final class GLTextureImage {
	/**
	 * These are the texels for this mipmap level.
	 */
	public final GLTextureBuffer buffer;

	/**
	 * Image dimensions, including border.
	 */
	public final int width;
	public final int height;
	public final int depth;
	public int imageSize;

	// Image dimensions, doesn't include border
	// public int width2, height2, depth2;
	// public float width2f, height2f, depth2f;

	/**
	 * log2 of width2 & height2.
	 */
	public final int widthLog2;
	public final int heightLog2;
	public final int depthLog2;

	/**
	 * Border size.
	 */
	public final int border;

	// Requested internal format
	// GLenum requestedFormat;

	/**
	 * Base internal format.
	 */
	public final int baseFormat;

	//
	//    /* Actual internal format */
	//    GLenum internalFormat;
	//
	//    /* Component resolution */
	//    GLint redSize;
	//    GLint greenSize;
	//    GLint blueSize;
	//    GLint alphaSize;
	//    GLint luminanceSize;
	//    GLint intensitySize;
	//
	//    const __GLtextureFormat *texFormat;

	public GLTextureImage(final int border, final GLTextureBuffer buffer) {
		this.border = border;
		this.buffer = buffer;

		width = buffer.width;
		height = buffer.height;
		depth = buffer.depth;

		baseFormat = buffer.format;
		widthLog2 = GLMath.logbase2(width - 2 * border);

		if (height == 1) // 1-D texture
			heightLog2 = 0;
		else
			heightLog2 = GLMath.logbase2(height - 2 * border);

		if (depth == 1) // 2-D texture
			depthLog2 = 0;
		else
			depthLog2 = GLMath.logbase2(depth - 2 * border);
	}
}
