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

package org.thenesis.pjogles.clipping;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.pipeline.GLPipeline;
import org.thenesis.pjogles.primitives.GLPolygon;

/**
 * Houses all the clipping functionality, ie. calculating the clip codes for the
 * frustum and user clip planes along with the actual clipping of lines and polygons.
 *
 * @see org.thenesis.pjogles.pipeline.GLPrimitiveAssembly GLPrimitiveAssembly
 *
 * @author tdinneen
 */
public final class GLClipping implements GLConstants {
	private static final int NUMBER_OF_FRUSTUM_CLIP_PLANES = 6;
	private static final GLVector4f frustumClipPlanes[] = new GLVector4f[NUMBER_OF_FRUSTUM_CLIP_PLANES];

	protected static final int MAX_NUMBER_GENERATED = 3;

	private final GLSoftwareContext gc;

	private GLVertex[] vertices;

	private final GLVertex[] out = new GLVertex[GLPipeline.MAX_NUMBER_VERTICES];
	protected final GLVertex[] clippedEyeToPlaneVertices = new GLVertex[MAX_NUMBER_GENERATED];
	protected final GLVertex[] clippedToPlaneVertices = new GLVertex[MAX_NUMBER_GENERATED];

	static {
		frustumClipPlanes[0] = new GLVector4f(1.0f, 0.0f, 0.0f, 1.0f); // left
		frustumClipPlanes[1] = new GLVector4f(-1.0f, 0.0f, 0.0f, 1.0f); // right
		frustumClipPlanes[2] = new GLVector4f(0.0f, 1.0f, 0.0f, 1.0f); // bottom
		frustumClipPlanes[3] = new GLVector4f(0.0f, -1.0f, 0.0f, 1.0f); // top
		frustumClipPlanes[4] = new GLVector4f(0.0f, 0.0f, 1.0f, 1.0f); // near
		frustumClipPlanes[5] = new GLVector4f(0.0f, 0.0f, -1.0f, 1.0f); // far
	}

	public GLClipping(final GLSoftwareContext gc) {
		this.gc = gc;

		for (int i = MAX_NUMBER_GENERATED; --i >= 0;) {
			clippedEyeToPlaneVertices[i] = new GLVertex();
			clippedToPlaneVertices[i] = new GLVertex();
		}
	}

	public static int frustumClipCodes(final GLVector4f v) {
		int clipCodes = 0;

		// Do frustum checks.
		// NOTE: it is possible for x to be less than negW and greater than w
		// (if w is negative). Otherwise there would be "else" clauses here.

		final float x = v.x;
		final float y = v.y;
		final float z = v.z;
		final float w = v.w;

		final float negW = -w;

		if (x < negW)
			clipCodes |= CLIP_LEFT;
		else if (x > w)
			clipCodes |= CLIP_RIGHT;

		if (y < negW)
			clipCodes |= CLIP_BOTTOM;
		else if (y > w)
			clipCodes |= CLIP_TOP;

		if (z < negW)
			clipCodes |= CLIP_NEAR;
		else if (z > w)
			clipCodes |= CLIP_FAR;

		return clipCodes;
	}

	/**
	 * Clip check against the frustum and user clipping planes.
	 */
	public static int userClipCodes(final GLSoftwareContext gc, final GLVector4f v) {
		int clipCodes = 0;

		// Now do user clip plane checks

		int clipPlanesMask = gc.state.enables.clipPlanes;

		if (clipPlanesMask != 0) {
			int bit = CLIP_USER0;

			final GLVector4f[] eyeClipPlanes = gc.state.transform.eyeClipPlanes;
			GLVector4f plane;

			// TOMD - we can speed this up, conditional should be --i >= 0

			for (int i = 0; i < MAX_USER_CLIP_PLANES && clipPlanesMask != 0; ++i) {
				if ((clipPlanesMask & 1) != 0) {
					plane = eyeClipPlanes[i];

					// dot the vertices clip coordinate against the clip plane and see
					// if the sign is negative. If so, then the point is out.

					if (plane.dotProduct(v) < 0.0)
						clipCodes |= bit;
				}

				clipPlanesMask >>= 1;
				bit <<= 1;
			}
		}

		return clipCodes;
	}

	/**
	 * Clip a line against the frustum clip planes and any user clipping planes.
	 * If an edge remains after clipping then compute the window coordinates
	 * and invoke the renderer.
	 * <p>
	 * Notice:  This algorithim is an example of an implementation that is
	 * different than what the spec says.  This is equivalent in functionality
	 * and meets the spec, but doesn't clip in eye space.  This clipper clips
	 * in NTVP (clip) space.
	 * <p>
	 * Trivial accept/reject has already been dealt with.
	 */
	public final boolean clipLine(final GLVertex v1, final GLVertex v2) {
		// Check for trivial pass of the line
		int allClipCodes = (v1.clipCodes | v2.clipCodes);

		// Do user clip planes first, because we will maintain eye coordinates
		// only while doing user clip planes.  They are ignored for the
		// frustum clipping planes.

		int userClipCodes = allClipCodes >> NUMBER_OF_FRUSTUM_CLIP_PLANES;

		GLVector4f plane;
		float d1, d2, t;

		for (int i = 0; i < MAX_USER_CLIP_PLANES && userClipCodes != 0; ++i) {
			plane = gc.state.transform.eyeClipPlanes[i];

			// See if this clip plane has anything out of it.  If not,
			// press onward to check the next plane.  Note that we
			// shift this valueMask to the right at the bottom of the loop.

			if ((userClipCodes & CLIP_LEFT) != 0) {
				d1 = plane.dotProduct(v1.eye);
				d2 = plane.dotProduct(v2.eye);

				if (d1 < 0.0f) // v1 is out
				{
					if (d2 < 0.0f)
						return true; // v1 & v2 are out

					// A is out and B is in.  Compute new A coordinate
					// clipped to the plane.

					t = d2 / (d2 - d1);

					v1.eye.w = t * (v1.eye.w - v2.eye.w) + v2.eye.w;
					v1.eye.x = t * (v1.eye.x - v2.eye.x) + v2.eye.x;
					v1.eye.y = t * (v1.eye.y - v2.eye.y) + v2.eye.y;
					v1.eye.z = t * (v1.eye.z - v2.eye.z) + v2.eye.z;

					clipParameters(v1, v2, t, v1);
				} else // v1 is in
				{
					if (d2 < 0.0f) {
						// A is in and B is out.  Compute new B
						// coordinate clipped to the plane.
						//
						// NOTE: To avoid cracking in polygons with
						// shared clipped edges we always compute "t"
						// from the out vertices to the in vertices.  The
						// above clipping code gets this for free (v2 is
						// in and v1 is out).  In this code v2 is out and v1
						// is in, so we reverse the t computation and the
						// argument order to (*clip).

						t = d1 / (d1 - d2);

						v2.eye.w = t * (v2.eye.w - v1.eye.w) + v1.eye.w;
						v2.eye.x = t * (v2.eye.x - v1.eye.x) + v1.eye.x;
						v2.eye.y = t * (v2.eye.y - v1.eye.y) + v1.eye.y;
						v2.eye.z = t * (v2.eye.z - v1.eye.z) + v1.eye.z;

						clipParameters(v2, v1, t, v2);
					}
					// else A and B are in
				}
			}

			userClipCodes >>= 1;
		}

		allClipCodes &= CLIP_FRUSTUM_MASK;

		for (int i = 0; i < 6 && allClipCodes != 0; ++i) {
			plane = frustumClipPlanes[i];

			// See if this clip plane has anything out of it.  If not,
			// press onward to check the next plane.  Note that we
			// shift this valueMask to the right at the bottom of the loop.

			if ((allClipCodes & CLIP_LEFT) != 0) {
				d1 = plane.dotProduct(v1.clip);
				d2 = plane.dotProduct(v2.clip);

				if (d1 < 0.0f) // v1 is out
				{
					if (d2 < 0.0f)
						return true; // v1 & v2 are out

					// A is out and B is in. Compute new A coordinate
					// clipped to the plane.

					t = d2 / (d2 - d1);

					clipParameters(v1, v2, t, v1);
				} else // v1 is in
				{
					if (d2 < 0.0f) {
						// A is in and B is out.  Compute new B
						// coordinate clipped to the plane.

						// NOTE: To avoid cracking in polygons with
						// shared clipped edges we always compute "t"
						// from the out vertices to the in vertices.  The
						// above clipping code gets this for free (v2 is
						// in and v1 is out).  In this code v2 is out and v1
						// is in, so we reverse the t computation and the
						// argument order to (*clip).

						t = d1 / (d1 - d2);

						clipParameters(v2, v1, t, v2);
					}
					// else A and B are in
				}
			}

			allClipCodes >>= 1;
		}

		return false;
	}

	public final boolean clipPolygon(final GLPolygon polygon, int allClipCodes) {
		allClipCodes &= CLIP_MASK;

		// Check each of the clipping planes by examining the allClipCodes
		// valueMask. Note that no bits will be set in allClipCodes for clip
		// planes that are not enabled.

		// Do user clip planes first, because we will maintain eye coordinates
		// only while doing user clip planes.  They are ignored for the
		// frustum clipping planes.

		int userClipCodes = allClipCodes >> 6;
		GLVector4f plane;

		for (int i = 0; i < MAX_USER_CLIP_PLANES && userClipCodes != 0; ++i) {
			plane = gc.state.transform.eyeClipPlanes[i];

			// See if this clip plane has anything out of it.  If not,
			// press onward to check the next plane.  Note that we
			// shift this valueMask to the right at the bottom of the loop.

			if ((userClipCodes & CLIP_LEFT) != 0) {
				if (clipEyeToPlane(polygon, plane) < 3)
					return true;
			}

			userClipCodes >>= 1;
		}

		allClipCodes &= CLIP_FRUSTUM_MASK;

		for (int i = 0; i < 6 && allClipCodes != 0; ++i) {
			plane = frustumClipPlanes[i];

			// See if this clip plane has anything out of it.  If not,
			// press onward to check the next plane.  Note that we
			// shift this valueMask to the right at the bottom of the loop.

			if ((allClipCodes & CLIP_LEFT) != 0) {
				if (clipToPlane(polygon, plane) < 3)
					return true;
			}

			allClipCodes >>= 1;
		}

		return false;
	}

	private int clipEyeToPlane(final GLPolygon polygon, final GLVector4f plane) {
		final int numberOfVertices = polygon.numberOfVertices;

		if (numberOfVertices <= 0)
			return 0;

		vertices = polygon.vertices;
		GLVertex newVertex, p, s = vertices[numberOfVertices - 1];
		float t, pDist, sDist = plane.dotProduct(s.eye);

		int nout = 0;
		int generated = 0;

		for (int i = 0; i < numberOfVertices; ++i) {
			p = vertices[i];
			pDist = plane.dotProduct(p.eye);

			if (pDist >= 0.0f) // p is inside the clipping plane half space
			{
				if (sDist >= 0.0f) // s is inside the clipping plane half space
					out[nout++] = p;
				else // s is outside the clipping plane half space
				{
					t = (pDist - sDist) != 0.0f ? pDist / (pDist - sDist) : 0.0f;

					newVertex = clippedEyeToPlaneVertices[generated];

					if (++generated >= 3) // Toss the non-convex polygon
						return 0;

					newVertex.eye.x = t * (s.eye.x - p.eye.x) + p.eye.x;
					newVertex.eye.y = t * (s.eye.y - p.eye.y) + p.eye.y;
					newVertex.eye.z = t * (s.eye.z - p.eye.z) + p.eye.z;
					newVertex.eye.w = t * (s.eye.w - p.eye.w) + p.eye.w;

					clipParameters(s, p, t, newVertex);

					out[nout++] = newVertex;
					out[nout++] = p;
				}
			} else // p is outside the clipping plane half space
			{
				if (sDist >= 0.0f) // s is inside the clipping plane half space
				{
					// NOTE: To avoid cracking in polygons with shared
					// clipped edges we always compute "t" from the out
					// vertices to the in vertices.  The above clipping code gets
					// this for free (p is in and s is out).  In this code p
					// is out and s is in, so we reverse the t computation
					// and the argument order to __glDoClip.

					t = (sDist - pDist) != 0.0f ? sDist / (sDist - pDist) : 0.0f;

					newVertex = clippedEyeToPlaneVertices[generated];

					if (++generated >= 3) /* Toss the non-convex polygon */
						return 0;

					newVertex.eye.x = t * (p.eye.x - s.eye.x) + s.eye.x;
					newVertex.eye.y = t * (p.eye.y - s.eye.y) + s.eye.y;
					newVertex.eye.z = t * (p.eye.z - s.eye.z) + s.eye.z;
					newVertex.eye.w = t * (p.eye.w - s.eye.w) + s.eye.w;

					clipParameters(p, s, t, newVertex);

					out[nout++] = newVertex;
				}
				// else both points are outside
			}

			s = p;
			sDist = pDist;
		}

		for (int i = nout; --i >= 0;)
			polygon.vertices[i] = out[i];

		polygon.numberOfVertices = nout;

		return nout;
	}

	private int clipToPlane(final GLPolygon polygon, final GLVector4f plane) {
		final int numberOfVertices = polygon.numberOfVertices;

		if (numberOfVertices <= 0)
			return 0;

		vertices = polygon.vertices;
		GLVertex newVertex, p, s = vertices[numberOfVertices - 1];
		float t, pDist, sDist = plane.dotProduct(s.clip);

		int nout = 0;
		int generated = 0;

		for (int i = 0; i < numberOfVertices; ++i) {
			p = vertices[i];
			pDist = plane.dotProduct(p.clip);

			if (pDist >= 0.0f) // p is inside the clipping plane half space
			{

				if (sDist >= 0.0f) // s is inside the clipping plane half space
					out[nout++] = p;
				else // s is outside the clipping plane half space
				{

					t = (pDist - sDist) != 0.0f ? pDist / (pDist - sDist) : 0.0f;

					newVertex = clippedToPlaneVertices[generated];

					if (++generated >= 3) // Toss the non-convex polygon
						return 0;

					clipParameters(s, p, t, newVertex);

					out[nout++] = newVertex;
					out[nout++] = p;
				}
			} else // p is outside the clipping plane half space
			{
				if (sDist >= 0.0f) // s is inside the clipping plane half space
				{
					// NOTE: To avoid cracking in polygons with shared
					// clipped edges we always compute "t" from the out
					// vertices to the in vertices.  The above clipping code gets
					// this for free (p is in and s is out).  In this code p
					// is out and s is in, so we reverse the t computation
					// and the argument order to __glDoClip.

					t = (sDist - pDist) != 0.0f ? sDist / (sDist - pDist) : 0.0f;

					newVertex = clippedToPlaneVertices[generated];

					if (++generated >= 3) // Toss the non-convex polygon
						return 0;

					clipParameters(p, s, t, newVertex);

					out[nout++] = newVertex;
				}
				// else both points are outside
			}

			s = p;
			sDist = pDist;
		}

		for (int i = nout; --i >= 0;)
			polygon.vertices[i] = out[i];

		polygon.numberOfVertices = nout;

		return nout;
	}

	private void clipParameters(final GLVertex a, final GLVertex b, final float t, final GLVertex d) {
		GL_CLIP_CLIP(a, b, t, d);

		if (gc.state.enables.lighting)
			GL_CLIP_NORMAL(a, b, t, d);
		else
			GL_CLIP_COLOR(a, b, t, d);

		final int enabledUnits = gc.state.texture.enabledUnits;

		if (enabledUnits != 0)
			GL_CLIP_TEXTURE(a, b, t, d, enabledUnits);
	}

	private void GL_CLIP_CLIP(final GLVertex a, final GLVertex b, final float t, final GLVertex d) {
		final GLVector4f v1 = a.clip;
		final GLVector4f v2 = b.clip;

		d.clip.set(t * (v1.x - v2.x) + v2.x, t * (v1.y - v2.y) + v2.y, t * (v1.z - v2.z) + v2.z, t * (v1.w - v2.w)
				+ v2.w);
	}

	private void GL_CLIP_COLOR(final GLVertex a, final GLVertex b, final float t, final GLVertex d) {
		final GLColor c1 = (GLColor) a.color;
		final GLColor c2 = (GLColor) b.color;

		d.color.set(t * (c1.r - c2.r) + c2.r, t * (c1.g - c2.g) + c2.g, t * (c1.b - c2.b) + c2.b, t * (c1.a - c2.a)
				+ c2.a);
	}

	private void GL_CLIP_NORMAL(final GLVertex a, final GLVertex b, final float t, final GLVertex d) {
		final GLVector4f v1 = a.normal;
		final GLVector4f v2 = b.normal;

		d.normal.set(t * (v1.x - v2.x) + v2.x, t * (v1.y - v2.y) + v2.y, t * (v1.z - v2.z) + v2.z, t * (v1.w - v2.w)
				+ v2.w);
	}

	private void GL_CLIP_TEXTURE(final GLVertex a, final GLVertex b, final float t, final GLVertex d,
			final int enabledUnits) {
		final GLVector4f[] aTexture = a.texture;
		final GLVector4f[] bTexture = b.texture;
		final GLVector4f[] dTexture = d.texture;

		GLVector4f v1, v2;

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			if ((enabledUnits & (1 << i)) != 0) {
				v1 = aTexture[i];
				v2 = bTexture[i];

				dTexture[i].set(t * (v1.x - v2.x) + v2.x, t * (v1.y - v2.y) + v2.y, t * (v1.z - v2.z) + v2.z, t
						* (v1.w - v2.w) + v2.w);
			}
		}
	}
}
