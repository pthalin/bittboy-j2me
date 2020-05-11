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
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.pipeline.GLFragmentOperations;
import org.thenesis.pjogles.primitives.GLBitmap;
import org.thenesis.pjogles.primitives.GLFragment;

/**
 * @author tdinneen
 */
public final class GLBitmapRenderer implements GLConstants {
	private final GLSoftwareContext gc;
	protected final GLFragment fragment = new GLFragment();

	public GLBitmapRenderer(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void renderBitmap(final GLBitmap bitmap, final GLFragmentOperations fragmentOperations) {
		// Check if current colorBuffer position is valid.  Do not render if invalid.
		// Also, if selection is in progress skip the rendering of the
		// bitmap.  Bitmaps are invisible to selection and do not generate
		// selection hits.

		if (!gc.state.current.validRasterPosition)
			return;

		final GLVector4f v = gc.state.current.raster.window;

		final int fx = (int) (v.x - bitmap.xOrig);
		final int width = gc.frameBuffer.width;

		fragment.y = (int) (v.y + bitmap.yOrig) * width;
		fragment.z = v.z;
		fragment.color.set(gc.state.current.raster.color);

		// TOMD - scissoring

		//    int x0 = gc.transform.clipX0;
		//    int x1 = gc.transform.clipX1;
		//    int y0 = gc.transform.clipY0;
		//    int y1 = gc.transform.clipY1;

		final int x0 = gc.state.viewport.x;
		final int x1 = gc.state.viewport.width;
		final int y0 = gc.state.viewport.y * width;
		final int y1 = gc.state.viewport.height * width;

		final byte[] bits = bitmap.bitmap;
		final int length = bits.length;

		int index = 0;
		byte data = 0;

		int bit = -1;

		for (int y = 0; y < bitmap.height; ++y) {
			fragment.x = fx;

			if (bit != 7) {
				bit = 7;

				if (index < length)
					data = bits[index++];
			}

			for (int x = 0; x < bitmap.width; ++x) {
				if (y0 <= fragment.y && fragment.y < y1 && x0 <= fragment.x && fragment.x < x1) {
					if ((data & (1 << bit)) != 0)
						fragmentOperations.apply(fragment);
				}

				++fragment.x;

				if (--bit < 0) {
					bit = 7;

					if (index < length)
						data = bits[index++];
				}
			}

			fragment.y -= width;
		}

		v.x += bitmap.xMove;
		v.y -= bitmap.yMove;
	}
}
