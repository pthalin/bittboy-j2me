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

package org.thenesis.pjogles.primitives;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;

/**
 * A GLSpan is a contiguous array of fragments. It is a lot faster
 * to process a group of contiguous fragments together (in either the x or y direction, we
 * chose an increasing x).
 *
 * @author tdinneen
 */
public final class GLSpan implements GLConstants {
	public GLEdge left, right;

	public final GLColor[] color = new GLColor[MAX_WINDOW_WIDTH];

	public int length;
	public int offset;

	public final boolean[] mask = new boolean[MAX_WINDOW_WIDTH];
	public final boolean[] stencilFail = new boolean[MAX_WINDOW_WIDTH];
	public final boolean[] depthFail = new boolean[MAX_WINDOW_WIDTH];
	public final boolean[] depthPass = new boolean[MAX_WINDOW_WIDTH];

	public final float[] z = new float[MAX_WINDOW_WIDTH];
	public final int[] c = new int[MAX_WINDOW_WIDTH];
	public final float[] fog = new float[MAX_WINDOW_WIDTH];

	public final GLTextureSpan[] textureCoordinates = new GLTextureSpan[NUMBER_OF_TEXTURE_UNITS];

	public boolean smoothShading;
	public boolean textureEnabled;
	public boolean fogEnabled;

	protected int ix1, ix2, idx;
	protected float oneOver;

	private final GLVector4f[] dtdx = new GLVector4f[NUMBER_OF_TEXTURE_UNITS];

	//private GLVector4f[] texture = new GLVector4f[__GL_NUM_TEXTURE_UNITS];

	public GLSpan() {
		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			textureCoordinates[i] = new GLTextureSpan();
			dtdx[i] = new GLVector4f();
			//texture[i] = new GLVector4f();
		}

		for (int i = MAX_WINDOW_WIDTH; --i >= 0;) {
			color[i] = new GLColor();
		}
	}

	public final boolean init(final int y, final int width) {
		// insure that edge's are
		// ordered with increasing x

		if (right.x < left.x) {
			final GLEdge edge = left;
			left = right;
			right = edge;
		}

		ix1 = (int) (left.x + 0.5f);
		ix2 = (int) (right.x + 0.5f);
		length = idx = ix2 - ix1;

		if (idx == 0)
			return false;

		if (--idx < 2) // don't overlap final right edge
			idx = 2;

		oneOver = 1.0f / idx;
		offset = ix1 + y * width;

		// TOMD - we could have an array that always true and we use System.arraycopy(..., length) !!

		for (int i = 0; i < length; ++i) {
			mask[i] = true;

			stencilFail[i] = false;
			depthPass[i] = false;
			depthFail[i] = true;
		}

		return true;
	}

	public final void doZVal() {
		float zVal = left.z;
		final float dz = (right.z - zVal) * oneOver;

		int ix1 = this.ix1;
		final int ix2 = this.ix2;
		int i = -1;

		do {
			z[++i] = zVal;
			zVal += dz;
		} while (++ix1 < ix2);
	}

	public final void doColor() {
		final GLColor lc = left.color;
		final GLColor rc = right.color;

		float r = lc.r;
		float g = lc.g;
		float b = lc.b;
		float a = lc.a;

		final float dr = (rc.r - r) / (float) idx;
		final float dg = (rc.g - g) / (float) idx;
		final float db = (rc.b - b) / (float) idx;
		final float da = (rc.a - a) / (float) idx;

		int ix1 = this.ix1;
		final int ix2 = this.ix2;
		int i = -1;

		do {
			color[++i].set(r, g, b, a);

			r += dr;
			g += dg;
			b += db;
			a += da;
		} while (++ix1 < ix2);
	}

	public final void mapColor(final GLSoftwareContext gc, final int[] buffer) {
		final GLColor[] localColor = color;
		final int[] localBuffer = buffer;
		final boolean[] localMask = mask;
		final int[] localC = c;

		for (int i = 0; i < length; ++i) {
			if (localMask[i])
				localC[i] = localColor[i].getRGBAi(gc, localBuffer[offset + i]);
			else
				localC[i] = localBuffer[offset + i];
		}
	}

	public final void doTexture(final GLSoftwareContext gc) {
		final long start = System.currentTimeMillis();

		final GLVector4f[] leftTexture = left.texture;
		final GLVector4f[] rightTexture = right.texture;
		GLVector4f t1, t2, dt;

		for (int j = NUMBER_OF_TEXTURE_UNITS; --j >= 0;) {
			t1 = leftTexture[j];
			t2 = rightTexture[j];

			dt = dtdx[j];

			dt.x = (t2.x - t1.x) * oneOver;
			dt.y = (t2.y - t1.y) * oneOver;
			dt.z = (t2.z - t1.z) * oneOver;
			dt.w = (t2.w - t1.w) * oneOver;
		}

		int ix1 = this.ix1;
		int ix2 = this.ix2;
		int i;

		final GLTextureSpan[] localTextureCoordinates = textureCoordinates;
		final GLVector4f[] localTexture = leftTexture;
		GLVector4f[] localCoordinates;

		for (int j = NUMBER_OF_TEXTURE_UNITS; --j >= 0;) {
			t1 = localTexture[j];
			localCoordinates = localTextureCoordinates[j].coordinates;

			dt = dtdx[j];

			i = 0;
			ix1 = this.ix1;
			ix2 = this.ix2;

			do {
				localCoordinates[i++].set(t1);

				t1.x += dt.x;
				t1.y += dt.y;
				t1.z += dt.z;
				t1.w += dt.w;
			} while (++ix1 < ix2);
		}

		gc.benchmark.textureTime += System.currentTimeMillis() - start;
	}

	public final void doFog(final GLSoftwareContext gc) {
		final long start = System.currentTimeMillis();

		if (gc.state.hints.fog == GL_NICEST) {
			float eyeZ = left.eyeZ;
			final float deyeZ = (left.eyeZ - eyeZ) * oneOver;

			int ix1 = this.ix1;
			final int ix2 = this.ix2;
			int i = -1;

			do {
				fog[++i] = GLVertex.fogVertex(gc, eyeZ);
				eyeZ += deyeZ;
			} while (++ix1 < ix2);
		} else {
			float f = left.fog;
			final float df = (right.fog - f) * oneOver;

			int ix1 = this.ix1;
			final int ix2 = this.ix2;
			int i = -1;

			do {
				fog[++i] = f;
				f += df;
			} while (++ix1 < ix2);
		}

		gc.benchmark.fogTime += System.currentTimeMillis() - start;
	}
}
