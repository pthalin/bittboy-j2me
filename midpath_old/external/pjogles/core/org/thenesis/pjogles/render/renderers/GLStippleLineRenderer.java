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

package org.thenesis.pjogles.render.renderers;

import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.render.GLRenderRenderer;

/**
 * @author tdinneen
 */
public final class GLStippleLineRenderer {
	private int repeat;
	private GLSoftwareContext gc;

	public GLStippleLineRenderer(final GLRenderRenderer renderer) {
	}

	public final void renderLine(final GLVertex v1, final GLVertex v2) {
		final int[] raster = gc.frameBuffer.drawBuffer.buffer;
		final int width = gc.frameBuffer.width;

		final int value = gc.state.current.color.getRGBAi();

		final int x1 = (int) (v1.window.x + 0.5f);
		final int y1 = (int) (v1.window.y + 0.5f);
		final int x2 = (int) (v2.window.x + 0.5f);
		final int y2 = (int) (v2.window.y + 0.5f);

		stippleLineBresenham(x1, y1, x2, y2, raster, width, value);
	}

	private void stippleLineBresenham(int x0, int y0, final int x1, int y1, final int[] raster, final int width,
			final int color) {
		final int stippleRepeat = gc.state.line.stippleRepeat;
		int stipplePosition = 0;
		int currentBit = 1;
		final int stipple = gc.state.line.stipple;

		int dy = y1 - y0;
		int dx = x1 - x0;

		final int stepx;
		final int stepy;

		if (dy < 0) {
			dy = -dy;
			stepy = -width;
		} else {
			stepy = width;
		}

		if (dx < 0) {
			dx = -dx;
			stepx = -1;
		} else {
			stepx = 1;
		}

		dy <<= 1;
		dx <<= 1;

		y0 *= width;
		y1 *= width;

		if ((stipple & currentBit) != 0) {
			raster[x0 + y0] = color;
		}

		if (dx > dy) {
			int fraction = dy - (dx >> 1);

			while (x0 != x1) {
				if (fraction >= 0) {
					y0 += stepy;
					fraction -= dx;
				}

				x0 += stepx;
				fraction += dy;

				// begin stipple

				if (++repeat >= stippleRepeat) {
					stipplePosition = (stipplePosition + 1) & 0xf;
					currentBit = 1 << stipplePosition;
					repeat = 0;
				}

				if ((stipple & currentBit) != 0) {
					raster[x0 + y0] = color;
				}

				// end stipple
			}
		} else {
			int fraction = dx - (dy >> 1);

			while (y0 != y1) {
				if (fraction >= 0) {
					x0 += stepx;
					fraction -= dy;
				}

				y0 += stepy;
				fraction += dx;

				// begin stipple

				if (++repeat >= stippleRepeat) {
					stipplePosition = (stipplePosition + 1) & 0xf;
					currentBit = 1 << stipplePosition;
					repeat = 0;
				}

				if ((stipple & currentBit) != 0) {
					raster[x0 + y0] = color;
				}

				// end stipple
			}
		}
	}
}
