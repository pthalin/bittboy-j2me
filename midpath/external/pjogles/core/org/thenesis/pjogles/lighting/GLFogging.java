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

package org.thenesis.pjogles.lighting;

import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.primitives.GLSpan;

/**
 * Encapsulates all fogging operations.
 *
 * @see org.thenesis.pjogles.GL#glFogf glFogf
 * @see org.thenesis.pjogles.pipeline.GLFragmentOperations GLFragmentOperations
 * 
 * @author tdinneen
 */
public final class GLFogging {
	protected final GLSoftwareContext gc;

	public GLFogging(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void apply(final GLSpan span) {
		final long start = System.currentTimeMillis();

		final GLColor[] localColor = span.color;
		final GLColor fogColor = gc.state.fog.color;

		final float fr = fogColor.r;
		final float fg = fogColor.g;
		final float fb = fogColor.b;

		final boolean[] mask = span.mask;
		final float[] fog = span.fog;

		GLColor src;
		float f;

		for (int k = span.length; --k >= 0;) {
			if (mask[k]) {
				src = localColor[k];
				f = fog[k];

				src.r = (f * (src.r - fr)) + fr;
				src.g = (f * (src.g - fg)) + fg;
				src.b = (f * (src.b - fb)) + fb;
			}
		}

		gc.benchmark.fogTime += System.currentTimeMillis() - start;
	}
}
