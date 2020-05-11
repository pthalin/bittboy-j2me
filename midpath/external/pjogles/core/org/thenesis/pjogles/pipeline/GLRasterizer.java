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

import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLBitmap;
import org.thenesis.pjogles.primitives.GLPolygon;
import org.thenesis.pjogles.render.GLBitmapRenderer;
import org.thenesis.pjogles.render.GLLineRenderer;
import org.thenesis.pjogles.render.GLPointRenderer;
import org.thenesis.pjogles.render.GLPolygonRenderer;
import org.thenesis.pjogles.render.GLRenderException;

/**
 * The GLRasterizer is the GLRenderer that GLPrimitiveAssembly passes
 * primtives to if the render mode is GL_RENDER (the default).
 * <p>
 * GLRasterizer dispatches calls to the appropriate primitive renderer.
 *
 * @see GLPrimitiveAssembly GLPrimitiveAssembly
 *
 * @author tdinneen
 */
public final class GLRasterizer implements GLRenderer {
	private final GLPointRenderer pointRenderer;
	protected final GLLineRenderer lineRenderer;
	protected final GLPolygonRenderer polygonRenderer;
	protected final GLBitmapRenderer bitmapRenderer;

	protected final GLFragmentOperations fragmentOperations;

	public GLRasterizer(final GLSoftwareContext gc) {
		pointRenderer = new GLPointRenderer(gc);

		lineRenderer = new GLLineRenderer(gc);
		bitmapRenderer = new GLBitmapRenderer(gc);
		polygonRenderer = new GLPolygonRenderer(this, gc); // needs to be able to callback on
		// line and point rendering
		fragmentOperations = new GLFragmentOperations(gc);
	}

	public final void render(final GLVertex v) {
		pointRenderer.renderPoint(v, fragmentOperations);
	}

	public final void render(final GLVertex v1, final GLVertex v2) {
		lineRenderer.renderLine(v1, v2, fragmentOperations);
	}

	public final void render(final GLPolygon polygon) throws GLRenderException {
		polygonRenderer.renderPolygon(polygon, fragmentOperations);
	}

	public final void render(final GLBitmap bitmap) {
		bitmapRenderer.renderBitmap(bitmap, fragmentOperations);
	}
}
