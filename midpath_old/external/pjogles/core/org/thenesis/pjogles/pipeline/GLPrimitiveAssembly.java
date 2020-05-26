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
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.clipping.GLClipping;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLPolygon;
import org.thenesis.pjogles.render.GLRenderException;
import org.thenesis.pjogles.render.GLRendererException;
import org.thenesis.pjogles.states.GLViewportState;

/**
 * Assembles primitives based on the begin mode set via
 * <strong>glBegin</strong>. Once we have a complete
 * primitive (ie. in the case of a glBegin(GL_TRIANGLE), then glVertex has been
 * called three times) we pass the primitive to clipping. If the primitive is
 * has not been completely clipped then we project it's vertices and test for
 * culling. If the primitive is still with us, we perform lighting, texturing and
 * fogging if required and pass the primitive down the pipe to the associated
 * GLRenderer.
 *
 * @see GLPipeline GLPipeline
 *
 * @author tdinneen
 */
public final class GLPrimitiveAssembly implements GLConstants {
	private final GLSoftwareContext gc;

	/**
	 * Set by GLPipeline when glRenderMode(int) is called.
	 */
	public GLRenderer renderer;

	/**
	 * The line and polygon clipper. Called in line() and polygon(), points are
	 * trivally rejected based on their clipcodes.
	 */
	protected final GLClipping clipping;

	/**
	 * The array of GLVertex's used to assemble the primitive.
	 */
	private final GLVertex[] vertices = new GLVertex[GLPipeline.MAX_NUMBER_VERTICES];

	/**
	 * The current index into the above GLVertex array.
	 */
	private int vertexIndex;

	private GLVertex v0, v1, v2; // locals used in point(), line() and polygon()
	private final GLPolygon polygon = new GLPolygon(); // local used in polygon()

	public GLPrimitiveAssembly(final GLSoftwareContext gc) {
		this.gc = gc;
		clipping = new GLClipping(gc);
	}

	public final void begin() {
		vertexIndex = 0; // reset the assembly of primitives
	}

	public final void end() throws GLRenderException {
		switch (gc.beginMode) {
		case GL_LINE_LOOP:

			if (vertexIndex == 2)
				line(1, 0); // 0 implicitly ends the loop => it's the provoking vertex

			return;

		case GL_POLYGON:

			if (vertexIndex < 3) {
				switch (vertexIndex) {
				case 0:
					return;
				case 1:
					point(0);
					return;
				case 2:
					line(0, 1);
					return;
				}
			} else
				polygon(vertexIndex);

			return;

		default:

			return;
		}
	}

	public final int vertex(final GLVertex v) throws GLRenderException {
		switch (gc.beginMode) {
		case GL_POINTS:

			vertices[0] = v; // v = index 0
			point(0);

			return 0; // we can reuse vertex[0]

		case GL_LINES:

			if (vertexIndex == 1) {
				vertices[1] = v; // v = index 1
				line(0, 1);
				vertexIndex = 0;

				return 0; // we can reuse vertex[0]
			} else {
				vertices[0] = v; // v = index 0
				vertexIndex = 1;

				return 1; // we can use vertex[1]
			}

		case GL_LINE_STRIP:

			if (vertexIndex == 1) {
				vertices[1] = v; // v = index 1
				line(0, 1);
				vertices[0].set(vertices[1]); // map 1 -> 0

				return 1; // but we can reuse vertex[1]
			} else {
				vertices[0] = v; // v = index 0
				vertexIndex = 1;

				return 1; // we can use vertex[1]
			}

		case GL_LINE_LOOP:

			if (vertexIndex == 2) {
				vertices[2] = v; // v = index 2
				line(1, 2);
				vertices[1] = vertices[2]; // index 2 in use

				return 1; // we can reuse vertex[1]
			} else if (vertexIndex == 1) {
				vertices[1] = v; // v = index 1
				line(0, 1);
				vertices[0] = vertices[1];

				return 2; // we need to keep 0 and 1 in use so use vertex[2]
			} else {
				vertices[0] = v; // v = index 0
				vertices[1] = v;

				vertexIndex = 2;

				return 1; // we can use vertex[1]
			}

		case GL_TRIANGLES:

			if (vertexIndex == 2) {
				vertices[2] = v; // v = index 2
				polygon(3);

				vertexIndex = 0;

				return 0; // we can start over again
			} else {
				vertices[vertexIndex] = v; // v = vertex[vertexIndex]

				return ++vertexIndex; // we can use vertex[vertexIndex + 1]
			}

		case GL_TRIANGLE_STRIP:

			if (vertexIndex == 0) {
				vertices[vertexIndex] = v; // v = index 0

				return ++vertexIndex; // we can use vertex[1]
			} else if (vertexIndex == 1) {
				vertices[vertexIndex++] = v; // v = index 1

				return ++vertexIndex; // we can use vertex[2]
			} else {
				vertices[2] = v; // v = index 2
				polygon(3);

				if (vertexIndex == 2) {
					vertices[0] = vertices[2]; // index 2 in use
					vertexIndex = 3;

					return 0; // we can reuse vertex[0]
				} else {
					vertices[1] = vertices[2]; // vertex 2 in use
					vertexIndex = 2;

					/////////////////////////////////

					return 1;
				}
			}

		case GL_TRIANGLE_FAN:

			if (vertexIndex == 2) // v = index 2
			{
				vertices[2] = v;
				polygon(3);
				vertices[1] = vertices[2]; // index 2 in use

				return 1; // we can reuse vertex[1]
			} else {
				vertices[vertexIndex] = v; // v = index vertexIndex

				return ++vertexIndex; // we can use vertexIndex + 1
			}

		case GL_QUADS:

			if (vertexIndex == 3) {
				vertices[3] = v; // v = index 3
				polygon(4);

				vertexIndex = 0;

				return 0; // we can start over again
			} else {
				vertices[vertexIndex] = v; // v = vertex[vertexIndex]
				return ++vertexIndex; // we can use vertex[vertexIndex + 1]
			}

		case GL_QUAD_STRIP:

			if (vertexIndex == 3) {
				vertices[2] = v;
				polygon(4);

				vertices[0] = vertices[3];
				vertices[1] = vertices[2];

				vertexIndex = 2;

				return 1;
			} else if (vertexIndex == 2) {
				vertices[3] = v; // v = index 2
				vertexIndex = 3;

				return 0;
			} else {
				vertices[vertexIndex] = v; // v = vertexIndex
				return ++vertexIndex; // we can use vertexIndex + 1
			}

			//////////////////////////////////////////////

		case GL_POLYGON:

			if (vertexIndex >= GLPipeline.MAX_NUMBER_VERTICES - 1)
				throw new GLRendererException("GLPrimitiveAssembly.vertex(GLVertex) - The maximum GL_POLYGON size is '"
						+ GLPipeline.MAX_NUMBER_VERTICES + "'");
			else
				vertices[vertexIndex] = v; // v = index vertexIndex

			return ++vertexIndex; // we can use vertex[vertexIndex + 1]

		default:

			throw new GLInvalidEnumException("GLPrimitiveAssembly.vertex(GLVertex)");
		}
	}

	private void point(final int i) {
		v0 = vertices[i];

		if (v0.clipCodes != 0)
			return;

		// If point passes the clipping test then we need to homogenize the
		// projected / clip coordinates. One geometrical interpretation of this
		// is that it projects the point x, y, z, onto the plane w = 1

		// The projection transform that we applied in the previous stage does not
		// project onto the plane w = 1, rather it transforms the view volume into
		// the canonical view volume. After applying this transform we get a point
		// q = (qx, qy, qz, q). The w component, q, will (most often) be non-zero
		// and not equal to one. To get the projected point, p, we need to divide by q:
		// p = (qx / q, qy / q, qz / q, 1). So after the projection transform and
		// homogenization (division by w) we get the normalized device coordinates (NDC).
		// Note that projection transform always sees to it that z = far plane maps to +1
		// and z = near plane maps to -1.

		// One effect of using a perspective tranform is that the computed depth value
		// does not vary linearly with the input pz value. For example if the near plane
		// is 10 and the far 110, with a pz of 60 (ie. the half way point) the NDC is 0.885, and
		// not 0.0 (half way between -1 and 1) => the placement of the near plane and far planes
		// is important with repect to the precision of the Z-Buffer.

		// According to Jim Blinn, in Jim Blinn's Corner: A Trip Down the Graphics Pipeline,
		// (Morgan Kaufman, San Francisco, 1996), the decision to use w or z for depth buffering is
		// best determined based on the ratio of z-near to z-far (zmin to zmax). His conclusions are:

		// If z-near/z-far = 0.3, use w-buffering
		// If z-near/z-far > 0.3, probably use z-buffering
		// And if z-near/z-far is > 0.5, definitely use z-buffering

		GLViewportState.perspectiveDivision(v0.clip, v0.normalizedDevice);

		// Here we perform the screen mapping, the screen mapping is simply a translation
		// followed by a scale based on the viewport.

		gc.state.viewport.normalizedDeviceToWindow(v0.normalizedDevice, v0.window);

		renderer.render(v0);
	}

	private void line(final int i1, final int i2) {
		v0 = vertices[i1];
		v1 = vertices[i2];

		if ((v0.clipCodes | v1.clipCodes) != 0) {
			/*
			 ** The line must be clipped more carefully. Cannot trivially
			 ** accept the lines.
			 */
			if ((v0.clipCodes & v1.clipCodes) != 0) {
				/*
				 ** Trivially reject the line. If anding the codes is non-zero then
				 ** every vertices in the line is outside of the same set of
				 ** clipping planes (at least one).
				 */
				return;
			}

			if (clipping.clipLine(v0, v1))
				return;
		}

		// If point passes the clipping test then we need to homogenize the
		// projected / clip coordinates. One geometrical interpretation of this
		// is that it projects the point x, y, z, onto the plane w = 1

		// The projection transform that we applied in the previous stage does not
		// project onto the plane w = 1, rather it transforms the view volume into
		// the canonical view volume. After applying this transform we get a point
		// q = (qx, qy, qz, q). The w component, q, will (most often) be non-zero
		// and not equal to one. To get the projected point, p, we need to divide by q:
		// p = (qx / q, qy / q, qz / q, 1). So after the projection transform and
		// homogenization (division by w) we get the normalized device coordinates (NDC).
		// Note that projection transform always sees to it that z = far plane maps to +1
		// and z = near plane maps to -1.

		// One effect of using a perspective tranform is that the computed depth value
		// does not vary linearly with the input pz value. For example if the near plane
		// is 10 and the far 110, with a pz of 60 (ie. the half way point) the NDC is 0.885, and
		// not 0.0 (half way between -1 and 1) => the placement of the near plane and far planes
		// is important with repect to the precision of the Z-Buffer.

		GLViewportState.perspectiveDivision(v0.clip, v0.normalizedDevice);

		// Here we perform the screen mapping, the screen mapping is simply a translation
		// followed by a scale based on the viewport.

		gc.state.viewport.normalizedDeviceToWindow(v0.normalizedDevice, v0.window);

		GLViewportState.perspectiveDivision(v1.clip, v1.normalizedDevice);
		gc.state.viewport.normalizedDeviceToWindow(v1.normalizedDevice, v1.window);

		gc.pipeline.provoking = v1;

		renderer.render(v0, v1);
	}

	private void polygon(int numberOfVertices) throws GLRenderException {
		// polygon is only ever called with a minimum of 3 vertices

		// Generate polygon from verticies. And all the
		// clip codes together while we are at it.

		int andCodes = ~0;
		int orCodes = 0;

		polygon.numberOfVertices = 0;

		for (int i = 0; i < numberOfVertices; ++i) {
			v0 = vertices[i];

			andCodes &= v0.clipCodes;
			orCodes |= v0.clipCodes;

			polygon.addVertex(v0);
		}

		if ((andCodes & CLIP_MASK) != 0) {
			// Trivially reject the polygon.  If andCodes is non-zero then
			// every vertices in the polygon is outside of the same set of
			// clipping planes (at least one).

			return;
		}

		// for a polygon, the last vertex provokes it's assembly
		// lets set this before clipping, just to get the color right
		gc.pipeline.provoking = vertices[numberOfVertices - 1];

		if (clipping.clipPolygon(polygon, orCodes & CLIP_MASK))
			return;

		// the polygon may have added vertices to itself

		numberOfVertices = polygon.numberOfVertices;
		final GLVertex[] vertices = polygon.vertices;

		for (int i = 0; i < numberOfVertices; ++i) {
			v0 = vertices[i];

			// If point passes the clipping test then we need to homogenize the
			// projected / clip coordinates. One geometrical interpretation of this
			// is that it projects the point x, y, z, onto the plane w = 1

			// The projection transform that we applied in the previous stage does not
			// project onto the plane w = 1, rather it transforms the view volume into
			// the canonical view volume. After applying this transform we get a point
			// q = (qx, qy, qz, q). The w component, q, will (most often) be non-zero
			// and not equal to one. To get the projected point, p, we need to divide by q:
			// p = (qx / q, qy / q, qz / q, 1). So after the projection transform and
			// homogenization (division by w) we get the normalized device coordinates (NDC).
			// Note that projection transform always sees to it that z = far plane maps to +1
			// and z = near plane maps to -1.

			// One effect of using a perspective tranform is that the computed depth value
			// does not vary linearly with the input pz value. For example if the near plane
			// is 10 and the far 110, with a pz of 60 (ie. the half way point) the NDC is 0.885, and
			// not 0.0 (half way between -1 and 1) => the placement of the near plane and far planes
			// is important with repect to the precision of the Z-Buffer.

			GLViewportState.perspectiveDivision(v0.clip, v0.normalizedDevice);

			// Here we perform the screen mapping, the screen mapping is simply a translation
			// followed by a scale based on the viewport.

			gc.state.viewport.normalizedDeviceToWindow(v0.normalizedDevice, v0.window);
		}

		// determine the direction that the polygon is facing
		// note that this method is called with at least three vertices, so this is safe

		v0 = vertices[0];
		v1 = vertices[1];
		v2 = vertices[2];

		// compute signed area
		// of the triangle

		final float dxAC = v0.window.x - v2.window.x;
		final float dxBC = v1.window.x - v2.window.x;
		final float dyAC = v0.window.y - v2.window.y;
		final float dyBC = v1.window.y - v2.window.y;
		final float area = dxAC * dyBC - dxBC * dyAC;

		final boolean ccw = area < 0.0f ? true : false;

		if (gc.state.polygon.frontFaceDirection == GL_CCW) {
			if (ccw)
				polygon.facing = GL_FRONT;
			else
				polygon.facing = GL_BACK;
		} else // gc.state.polygon.frontFaceDirection == GL_CW
		{
			if (ccw)
				polygon.facing = GL_BACK;
			else
				polygon.facing = GL_FRONT;
		}

		// trivally reject the polygon at this state if culling enabled
		// and the polygon triangles the direction of culled polygons

		if (gc.state.enables.cullFace) {
			final int cull = gc.state.polygon.cull;

			if (cull == GL_FRONT_AND_BACK)
				return;
			else if (polygon.facing == cull)
				return;
		}

		// choose colors for the vertices

		if (gc.state.enables.lighting) {
			if (gc.state.lighting.shadingModel == GL_SMOOTH) {
				for (int i = 0; i < numberOfVertices; ++i) {
					if (polygon.facing == GL_FRONT)
						gc.pipeline.lighting.apply(vertices[i], __GL_FRONTFACE);
					else if (polygon.facing == GL_BACK)
						gc.pipeline.lighting.apply(vertices[i], __GL_BACKFACE);
				}
			} else // GL_FLAT
			{
				// set all vertex colours
				// to be the provoking vertex colour

				final GLVertex pv = gc.pipeline.provoking;

				if (polygon.facing == GL_FRONT)
					gc.pipeline.lighting.apply(pv, __GL_FRONTFACE);
				else if (polygon.facing == GL_BACK)
					gc.pipeline.lighting.apply(pv, __GL_BACKFACE);

				final GLColor color = pv.color;

				for (int i = 0; i < numberOfVertices; ++i)
					vertices[i].color.set(color);
			}
		}

		if (gc.state.texture.enabledUnits != 0)
			gc.pipeline.textureOperations.processCoordinates(vertices, numberOfVertices);

		if (gc.state.enables.fog) {
			// need fog value at vertex if not nicest

			if (gc.state.hints.fog != GL_NICEST) {
				for (int i = 0; i < numberOfVertices; ++i)
					vertices[i].fogVertexWithEye(gc);
			}
		}

		renderer.render(polygon);
	}
}
