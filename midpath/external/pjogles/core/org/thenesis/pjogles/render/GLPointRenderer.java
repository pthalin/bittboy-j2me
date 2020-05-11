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

package org.thenesis.pjogles.render;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.GLUtils;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.pipeline.GLFragmentOperations;

/**
 * @author tdinneen
 */
public final class GLPointRenderer implements GLConstants {
	private final GLSoftwareContext gc;

	public GLPointRenderer(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void renderPoint(final GLVertex v, final GLFragmentOperations frgamentOperations) {
		final int[] raster = gc.frameBuffer.drawBuffer.buffer;
		final int width = gc.frameBuffer.width;
		final GLColor color = gc.state.current.color;

		final int pointSize = gc.state.point.aliasedSize;

		if (pointSize == 1) {
			final int x = (int) (v.window.x + 0.5f);
			final int y = (int) (v.window.y + 0.5f);

			final int index = x + (y * width);
			final int value = color.getRGBAi(gc, raster[index]);

			raster[index] = value;
		} else {
			final int height = gc.frameBuffer.height;

			// compute the x and y starting coordinates
			// for rendering the square

			final int pointSizeHalf = pointSize >> 1;
			int xLeft, yBottom, xRight, yTop;

			if ((pointSize & 1) != 0) // odd point size
			{
				xLeft = ((int) (v.window.x - 0.5f)) - pointSizeHalf;
				yBottom = ((int) (v.window.y - 0.5f)) - pointSizeHalf;
			} else // even point size
			{
				xLeft = ((int) v.window.x) - pointSizeHalf;
				yBottom = ((int) v.window.y) - pointSizeHalf;
			}

			xRight = xLeft + pointSize;
			yTop = yBottom + pointSize;

			xLeft = GLUtils.clamp(xLeft, 0, width);
			xRight = GLUtils.clamp(xRight, 0, width);
			yTop = GLUtils.clamp(yTop, 0, height);
			yBottom = GLUtils.clamp(yBottom, 0, height);

			// now render the square centered on xCenter, yCenter.

			int x, y, iy, index, value;

			// TOMD - note we are writing directly to the framebuffer

			for (y = yBottom; y < yTop; ++y) {
				iy = (y * width);

				for (x = xLeft; x < xRight; ++x) {
					index = x + iy;
					value = color.getRGBAi(gc, raster[index]);

					raster[index] = value;
				}
			}
		}
	}
}
