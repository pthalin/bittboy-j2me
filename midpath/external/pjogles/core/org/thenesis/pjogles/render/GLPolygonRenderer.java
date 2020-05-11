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
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.pipeline.GLFragmentOperations;
import org.thenesis.pjogles.pipeline.GLRasterizer;
import org.thenesis.pjogles.primitives.GLEdge;
import org.thenesis.pjogles.primitives.GLPolygon;
import org.thenesis.pjogles.primitives.GLSpan;

/**
 * @author tdinneen
 */
public final class GLPolygonRenderer implements GLConstants {
	private final GLRasterizer rasterizer; // used to pass back rendering of points and lines
	protected final GLSoftwareContext gc;

	protected final GLEdge[] table1 = new GLEdge[MAX_WINDOW_HEIGHT]; // left
	protected final GLEdge[] table2 = new GLEdge[MAX_WINDOW_HEIGHT]; // and right GLEdge for given y

	protected final GLSpan span = new GLSpan(); // current Span for given y

	protected final GLVector4f[] texture = new GLVector4f[NUMBER_OF_TEXTURE_UNITS];
	protected final GLVector4f[] dtdy = new GLVector4f[NUMBER_OF_TEXTURE_UNITS];

	private boolean smoothShading; // shared accross fillPolygon() and edge()
	private boolean textureEnabled; // shared accross fillPolygon() and edge()
	private boolean fogEnabled; // shared accross fillPolygon() and edge()
	private int fogHint; // shared accross fillPolygon() and edge()

	public GLPolygonRenderer(final GLRasterizer rasterizer, final GLSoftwareContext gc) {
		this.rasterizer = rasterizer;
		this.gc = gc;

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			texture[i] = new GLVector4f();
			dtdy[i] = new GLVector4f();
		}

		for (int i = MAX_WINDOW_HEIGHT; --i >= 0;) {
			table1[i] = new GLEdge();
			table2[i] = new GLEdge();
		}
	}

	public final void renderPolygon(final GLPolygon polygon, final GLFragmentOperations fragmentOperations)
			throws GLRenderException {
		final GLVertex[] vertices = polygon.vertices;
		final int numberOfVertices = polygon.numberOfVertices;

		switch (polygon.facing == GL_FRONT ? gc.state.polygon.frontMode : gc.state.polygon.backMode) {
		case GL_POINT:

			for (int i = 0; i < numberOfVertices; ++i)
				rasterizer.render(vertices[i]);

			break;

		case GL_LINE:

			for (int i = 1; i < numberOfVertices; ++i)
				rasterizer.render(vertices[i - 1], vertices[i]);

			rasterizer.render(vertices[numberOfVertices - 1], vertices[0]);

			break;

		case GL_FILL:

			fillPolygon(polygon, fragmentOperations);

			break;
		}
	}

	// http://www.gameprogrammer.com/5-poly.html
	private void fillPolygon(final GLPolygon p, final GLFragmentOperations fragmentOperations) {
		final GLVertex[] v = p.vertices;
		final int n = p.numberOfVertices;

		float miny = v[0].window.y;
		float maxy = miny;
		int iminy = 0, imaxy = 0;

		float temp;

		// The following loop finds the maximum Y coordinate,
		// the minimum Y coordinate and the indices in the point vector
		// of the points that contain the minimum and maximum Y values.
		// These are the top and bottom extremes of the polygon.

		for (int i = 1; i < n; ++i) {
			temp = v[i].window.y;

			if (temp > maxy) {
				maxy = temp;
				imaxy = i;
			}

			if (temp < miny) {
				miny = temp;
				iminy = i;
			}
		}

		int iy1 = (int) (miny + 0.5f); // adjust
		final int iy2 = (int) (maxy + 0.5f); // adjust

		// If the adjusted maximum Y is the same as the adjusted minimum Y we have a
		// horizontal line, not a polygon. So punt, we don't draw lines.

		if (iy1 == iy2)
			return;

		// In the following two loops we start at an extreme and walk our way around the
		// polygon until be get to another extreme. At each vertices we call edge()
		// to fill in the edge table for that part of the path.

		int pnt1 = iminy;
		int pnt2 = iminy + 1;

		if (pnt2 >= n)
			pnt2 = 0;

		smoothShading = gc.state.lighting.shadingModel == GL_SMOOTH;
		textureEnabled = gc.state.texture.enabledUnits != 0;
		fogEnabled = gc.state.enables.fog;
		fogHint = gc.state.hints.fog;

		do {
			edge(table1, v[pnt1], v[pnt2]);

			pnt1 = pnt2;
			++pnt2;

			if (pnt2 >= n)
				pnt2 = 0;
		} while (pnt1 != imaxy);

		pnt1 = imaxy;
		pnt2 = imaxy + 1;

		if (pnt2 >= n)
			pnt2 = 0;

		do {
			edge(table2, v[pnt1], v[pnt2]);

			pnt1 = pnt2;
			++pnt2;

			if (pnt2 >= n)
				pnt2 = 0;
		} while (pnt1 != iminy);

		// The final loop walks through the two edge tables calling span()
		// with each span of the polygon.

		span.smoothShading = smoothShading;
		span.fogEnabled = fogEnabled;
		span.textureEnabled = textureEnabled;

		final int width = gc.frameBuffer.width;

		do {
			span.left = table1[iy1];
			span.right = table2[iy1];

			if (span.init(iy1, width))
				fragmentOperations.apply(span);
		} while (++iy1 < iy2);
	}

	private final void edge(final GLEdge[] table, GLVertex v1, GLVertex v2) {
		// The following routine starts with a pair of vertices that define an edge and
		// produces a list of all the scan lines that cross the edge and the x  coordinates
		// of the crossings:

		if (v2.window.y < v1.window.y) {
			final GLVertex vertex = v1;
			v1 = v2;
			v2 = vertex;
		}

		int iy1 = (int) (v1.window.y + 0.5f); // adjust
		final int iy2 = (int) (v2.window.y + 0.5f); // adjust
		float idy = iy2 - iy1;

		// The next step is to give up if the edge starts and ends in the same row of pixels.
		// If idy is zero then we know that the edge was horizontal, very nearly horizontal,
		// or spanned less than one pixel. In any of those cases the edge will only confuse
		// the render by introducing extraneous end points or it is too small to have any visual effect.
		// So, we just ignore it.

		if (idy == 0)
			return;

		// To implement the "top and left in, right and bottom out" rule that keeps you from writing
		// pixels more than once, you just don't draw the last scan line an edge crosses or the last
		// pixel of each span that you do fill in. But, you want to cover the full range of X coordinates
		// for an edge. So, you decrement idy by one. And, since you divide by idy you have to make
		// sure that idy is never zero

		if (--idy < 2)
			idy = 2;

		final float oneOver_idy = 1.0f / idy;

		float x = v1.window.x;
		final float dx = (v2.window.x - x) * oneOver_idy;

		float z = v1.window.z * Float.MAX_VALUE;
		final float dz = ((v2.window.z * Float.MAX_VALUE) - z) * oneOver_idy;

		float r, g, b, a;
		float dr = 0.0f, dg = 0.0f, db = 0.0f, da = 0.0f;
		GLColor color;

		if (smoothShading) {
			color = v1.color;

			r = color.r;
			g = color.g;
			b = color.b;
			a = color.a;

			color = v2.color;

			dr = (color.r - r) * oneOver_idy;
			dg = (color.g - g) * oneOver_idy;
			db = (color.b - b) * oneOver_idy;
			da = (color.a - a) * oneOver_idy;
		} else {
			color = gc.pipeline.provoking.color;

			r = color.r;
			g = color.g;
			b = color.b;
			a = color.a;
		}

		final GLVector4f[] localTexture = texture;
		final GLVector4f[] localdtdy = dtdy;
		GLVector4f t1, t2;
		GLVector4f dt;

		float fog = 0.0f, dfog = 0.0f;
		float eyeZ = 0.0f, deyeZ = 0.0f;

		if (textureEnabled) {
			for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
				t1 = v1.texture[i];
				t2 = v2.texture[i];

				localTexture[i].set(t1);
				dt = localdtdy[i];

				dt.x = (t2.x - t1.x) * oneOver_idy;
				dt.y = (t2.y - t1.y) * oneOver_idy;
				dt.z = (t2.z - t1.z) * oneOver_idy;
				dt.w = (t2.w - t1.w) * oneOver_idy;
			}
		}

		if (fogEnabled) {
			// Vertex fogging computes the fog factor at the
			// vertex and then interpolates that. High quality fogging
			// (GL_FOG_HINT set to GL_NICEST) interpolates the eyeZ at then
			// evaluates the fog function for each fragment.

			if (fogHint == GL_NICEST) {
				eyeZ = v1.eye.z;
				deyeZ = (v2.eye.z - eyeZ) * oneOver_idy;
			} else // interpolate fog at vertex
			{
				fog = v1.fog;
				dfog = (v2.fog - fog) * oneOver_idy;
			}
		}

		GLEdge localTable;

		do {
			localTable = table[iy1];

			localTable.x = x;
			localTable.z = z;

			color = localTable.color;

			color.r = r;
			color.g = g;
			color.b = b;
			color.a = a;

			if (textureEnabled) {
				for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
					localTable.texture[i].set(localTexture[i]);
			}

			if (fogEnabled) {
				if (gc.state.hints.fog == GL_NICEST)
					localTable.eyeZ = eyeZ;
				else
					localTable.fog = fog;
			}

			x += dx;
			z += dz;

			if (smoothShading) {
				r += dr;
				g += dg;
				b += db;
				a += da;
			}

			if (textureEnabled) {
				for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
					t1 = localTexture[i];
					dt = localdtdy[i];

					t1.x += dt.x;
					t1.y += dt.y;
					t1.z += dt.z;
					t1.w += dt.w;
				}
			}

			if (fogEnabled) {
				if (fogHint == GL_NICEST)
					eyeZ += deyeZ;
				else
					fog += dfog;
			}
		} while (++iy1 < iy2);
	}

	/*
	 private void span(GLEdge e1, GLEdge e2, GLFragmentOperations fragmentOperations)
	 {
	 if (e2.x < e1.x)
	 {
	 edge1 = e1;
	 e1 = e2;
	 e2 = edge1;
	 }

	 ix1 = (int) (e1.x + 0.5f);
	 ix2 = (int) (e2.x + 0.5f);
	 idx = ix2 - ix1;

	 if (idx == 0)
	 return;

	 if (--idx < 2)
	 idx = 2;

	 z = e1.z;
	 dz = (e2.z - z) / (float) idx;

	 if (smoothShading)
	 {
	 r = e1.color.r;
	 g = e1.color.g;
	 b = e1.color.b;
	 a = e1.color.a;

	 dr = (e2.color.r - r) / (float) idx;
	 dg = (e2.color.g - g) / (float) idx;
	 db = (e2.color.b - b) / (float) idx;
	 da = (e2.color.a - a) / (float) idx;
	 }

	 if (textureEnabled)
	 {
	 s = e1.s;
	 t = e1.t;
	 w = e1.w;

	 ds = (e2.s - s) / (float) idx;
	 dt = (e2.t - t) / (float) idx;
	 dw = (e2.w - w) / (float) idx;
	 }

	 if (fogEnabled)
	 {
	 if (gc.state.hints.fog == GL_NICEST)
	 {
	 eyeZ = e1.eyeZ;
	 deyeZ = (e2.eyeZ - eyeZ) / (float) idx;
	 }
	 else
	 {
	 fog = e1.fog;
	 dfog = (e2.fog - fog) / (float) idx;
	 }
	 }

	 do
	 {
	 fragment.x = ix1;
	 fragment.z = z;

	 if (smoothShading)
	 fragment.color.set(r, g, b, a);

	 if (textureEnabled)
	 {
	 fragment.texture[0].s = s;
	 fragment.texture[0].t = t;
	 fragment.texture[0].q = w;
	 }

	 if (fogEnabled)
	 {
	 // Vertex fogging computes the fog factor at the
	 // vertex and then interpolates that. High quality fogging
	 // (GL_FOG_HINT set to GL_NICEST) interpolates the eyeZ at then
	 // evaluates the fog function for each fragment.

	 if (gc.state.hints.fog == GL_NICEST)
	 fragment.fog = GLVertex.fogVertex(gc, eyeZ);
	 else
	 fragment.fog = fog;
	 }

	 fragmentOperations.apply(fragment);

	 z += dz;

	 if (smoothShading)
	 {
	 r += dr;
	 g += dg;
	 b += db;
	 a += da;
	 }

	 if (textureEnabled)
	 {
	 s += ds;
	 t += dt;
	 w += dw;
	 }

	 if (fogEnabled)
	 {
	 if (gc.state.hints.fog == GL_NICEST)
	 eyeZ += deyeZ;
	 else
	 fog += dfog;
	 }
	 }
	 while (++ix1 < ix2);
	 }
	 */
}
