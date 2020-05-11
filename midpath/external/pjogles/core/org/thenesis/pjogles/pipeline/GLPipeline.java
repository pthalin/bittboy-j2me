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

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLDisplayList;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLInvalidOperationException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.lighting.GLLighting;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLBitmap;
import org.thenesis.pjogles.render.GLRenderException;
import org.thenesis.pjogles.render.GLRendererException;
import org.thenesis.pjogles.states.GLViewportState;
import org.thenesis.pjogles.texture.GLTextureManager;

/**
 * The GLPipeline is the entry point for <strong>glBegin</strong>, <strong>glVertex</strong>
 * and <strong>glEnd</strong> calls.
 * <p>
 * Calls to these methods perform vertex opertions and pass the results
 * down the pipe to primitive assembly. The pipeline is also responsible for
 * controling which renderer is called via <strong>glRenderMode</strong>.
 *
 * @see GL#glRenderMode glRenderMode
 * @see GLPrimitiveAssembly GLPrimitiveAssembly
 *
 * @author tdinneen
 */
public final class GLPipeline implements GLConstants {
	public static final int MAX_NUMBER_VERTICES = 1024;

	public final GLEvaluator evaluator;
	public final GLDisplayList displayList;

	public final GLLighting lighting; // set in derived class

	public final GLTextureManager textureManager;

	public final GLVertexOperations vertexOperations;
	public final GLPrimitiveAssembly primitiveAssembly; // set in derived class

	public final GLTextureOperations textureOperations;
	public final GLPixelOperations pixelOperations;

	public final GLRasterizer rasterizer; // set in derived class
	public final GLFeedback feedback;
	public final GLSelect select;

	// Provoking vertices.  For flat shaded primitives the triangle
	// renderer needs to know which vertex provoked the primitive to
	// properly assign the color during scan conversion.  This is kept
	// around as its a big pain to remember which vertices was provoking
	// during clipping (and to keep its parameters right).

	public GLVertex provoking;

	protected final GLVertex[] vertices = new GLVertex[MAX_NUMBER_VERTICES];

	private final GLSoftwareContext gc;
	private GLVertex vertex;
	private int vertexIndex;

	public GLPipeline(final GLSoftwareContext gc) {
		this.gc = gc;

		evaluator = new GLEvaluator(gc);
		displayList = new GLDisplayList(gc);

		textureManager = new GLTextureManager(gc);

		vertexOperations = new GLVertexOperations(gc);
		pixelOperations = new GLPixelOperations();

		feedback = new GLFeedback(gc);
		select = new GLSelect(gc);

		primitiveAssembly = new GLPrimitiveAssembly(gc);
		lighting = new GLLighting(gc);
		rasterizer = new GLRasterizer(gc);

		textureOperations = new GLTextureOperations(gc);

		for (int i = 0; i < MAX_NUMBER_VERTICES; ++i)
			vertices[i] = new GLVertex();

		setRenderMode(GL_RENDER, GL_RENDER); // default rendering mode GL_RENDER
	}

	public final void begin() {
		vertexIndex = 0;

		// no need for vertexOperations.begin()
		primitiveAssembly.begin();
	}

	public final void end() throws GLRenderException {
		// no need for vertexOperations.end()
		primitiveAssembly.end();
	}

	public final void vertex(final float x, final float y, final float z, final float w) throws GLRenderException {
		if (vertexIndex >= MAX_NUMBER_VERTICES - 1)
			throw new GLRendererException("GLPipeline.vertex(GLVertex) - The maximum GL_POLYGON size is '"
					+ MAX_NUMBER_VERTICES + "'");

		vertex = vertices[vertexIndex];
		vertex.position.set(x, y, z, w);

		// saves required current states, transforms normal if needed
		// calcs auto generated texture coords if enabled
		vertexOperations.vertex(vertex);

		// calc clipcodes both user and frustum
		// assembles primitives and passes down to GLRasterizer
		/* vertexIndex = */primitiveAssembly.vertex(vertex);
		++vertexIndex;
	}

	public final void raster(final GLVertex raster) {
		// saves required current states, transforms normal if needed
		// calcs auto generated texture coords if enabled
		vertexOperations.vertex(raster);

		if (raster.clipCodes != 0) {
			// the raster position is not valid
			gc.state.current.validRasterPosition = GL_FALSE;
			return;
		} else
			gc.state.current.validRasterPosition = GL_TRUE;

		// if we are a valid raster position then
		// calc clip -> NDC -> window coordinates

		GLViewportState.perspectiveDivision(raster.clip, raster.normalizedDevice);
		gc.state.viewport.normalizedDeviceToWindow(raster.normalizedDevice, raster.window);
	}

	public final void bitmap(final GLBitmap bitmap) {
		pixelOperations.bitmap(bitmap);
	}

	public final void readPixels(final int x, final int y, final int width, final int height, final int format,
			final int type, final Object pixels) {
		gc.frameBuffer.readPixels(x, y, width, height, format, type, pixels);
	}

	public final int setRenderMode(final int oldMode, final int newMode) {
		final int rv;

		// switch out of old render mode
		// get return value

		switch (oldMode) {
		case GL_RENDER:

			rv = 0;
			break;

		case GL_FEEDBACK:

			rv = feedback.overFlowed ? -1 : feedback.resultIndex;
			break;

		case GL_SELECT:

			rv = select.overFlowed ? -1 : select.hits;
			break;

		default:

			throw new GLInvalidEnumException("GLPipeline.setRenderMode(int, int)");
		}

		// switch in new render mode

		switch (newMode) {
		case GL_RENDER:

			primitiveAssembly.renderer = rasterizer;
			pixelOperations.renderer = rasterizer;

			break;

		case GL_FEEDBACK:

			if (feedback.resultBase == null)
				throw new GLInvalidOperationException("GLPipeline.setRenderMode(int, int)");

			feedback.reset();

			primitiveAssembly.renderer = feedback;
			pixelOperations.renderer = feedback;

			break;

		case GL_SELECT:

			if (select.resultBase == null)
				throw new GLInvalidOperationException("GLPipeline.setRenderMode(int, int)");

			select.reset();

			primitiveAssembly.renderer = select;
			pixelOperations.renderer = select;

			break;

		default:

			throw new GLInvalidEnumException("GLPipeline.setRenderMode(int, int)");
		}

		return rv;
	}
}
