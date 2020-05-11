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
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.pipeline.GLFragmentOperations;
import org.thenesis.pjogles.primitives.GLFragment;

/**
 * @author tdinneen
 */
public final class GLLineRenderer implements GLConstants {
	private final GLSoftwareContext gc;
	protected final GLFragment fragment = new GLFragment();

	public GLLineRenderer(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void renderLine(final GLVertex v1, final GLVertex v2, final GLFragmentOperations fragmentOperations) {
		final int[] raster = gc.frameBuffer.drawBuffer.buffer;
		final int width = gc.frameBuffer.width;

		lineBresenham(v1.window.x, v1.window.y, v2.window.x, v2.window.y, raster, width, gc.state.current.color,
				fragmentOperations);
	}

	private void lineBresenham(final float _x0, final float _y0, final float _x1, final float _y1, final int[] raster,
			final int width, final GLColor color, final GLFragmentOperations fragmentOperations) {
		int x0 = (int) (_x0 + 0.5f);
		int y0 = (int) (_y0 + 0.5f);
		final int x1 = (int) (_x1 + 0.5f);
		int y1 = (int) (_y1 + 0.5f);

		int dy = y1 - y0;
		int dx = x1 - x0;
		final int stepx;
		final int stepy;

		if (dy < 0) {
			dy = -dy;
			stepy = -width;
		} else
			stepy = width;

		if (dx < 0) {
			dx = -dx;
			stepx = -1;
		} else
			stepx = 1;

		dy <<= 1;
		dx <<= 1;

		y0 *= width;
		y1 *= width;

		fragment.x = x0;
		fragment.y = y0;
		fragment.z = 0.9f;
		fragment.color.set(color);

		fragmentOperations.apply(fragment);

		if (dx > dy) {
			int fraction = dy - (dx >> 1);

			while (x0 != x1) {
				if (fraction >= 0) {
					y0 += stepy;
					fraction -= dx;

					fragment.y = y0;
				}

				x0 += stepx;
				fraction += dy;

				fragment.x = x0;

				fragmentOperations.apply(fragment);
			}
		} else {
			int fraction = dx - (dy >> 1);

			while (y0 != y1) {
				if (fraction >= 0) {
					x0 += stepx;
					fraction -= dy;

					fragment.x = x0;
				}

				y0 += stepy;
				fraction += dx;

				fragment.y = y0;

				fragmentOperations.apply(fragment);
			}
		}
	}
}
