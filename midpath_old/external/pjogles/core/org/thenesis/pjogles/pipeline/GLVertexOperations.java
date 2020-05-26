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
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.GLTransform;
import org.thenesis.pjogles.math.GLMatrix4f;
import org.thenesis.pjogles.math.GLVertex;

/**
 * A grouping of all the vertex operations that are performed
 * on vertices before primitive assembly.
 * 
 * @author tdinneen
 */
public final class GLVertexOperations implements GLConstants {
	private final GLSoftwareContext gc;

	public GLVertexOperations(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void vertex(final GLVertex v) {
		// saves required states, eg. if no lighting save color,
		// if lighting save normal...

		final boolean needNormal = v.saveCurrentState(gc);

		// transform model coordinate space to eye / camera space, the modelView
		// matrix is the concatenated model -> world and view / camera transform

		GLMatrix4f.transform(v.position, gc.transform.modelView.matrix, v.eye);

		if (needNormal) {
			// if we need the normal then we need to transform it

			// NB - the normal must be transformed by the transpose of the
			// inverse of the matrix used to transform the geometry, see
			// Turkowski's Gem, "Properties of Surface-Normal Transformations",
			// Graphics Gems I, pp. 539-547.

			// N = (M^-1)^T

			// Note that we do not have to compute the inverse if we know the
			// matrix is orthogonal, since the inverse of an orthogonal matrix
			// is it's transpose. We can just use M, since the two matrix
			// transposes cancel each other out. But we don't know, so just
			// bite the bullet.

			final GLTransform transform = gc.transform.modelView;

			if (transform.updateInverseTranspose)
				transform.computeInverseTranspose(gc);

			GLMatrix4f.transform(v.normal, transform.matrix, v.transformedNormal);

			// NB - renormalisation is only required if M scales the normal, in fact
			// if scaling is uniform, then we only need rescale by a rescale factor
			// which is calulated if GL_RESCALE_NORMAL is enabled. Again to reiterate
			// the normal does not have to be renormalised if the matrix is made up of just
			// rotations and translations as length is preserved.

			if (gc.state.enables.normalize)
				v.transformedNormal.normalize();
		}

		// user clip planes are represented in eye / camera coordinate space

		v.userClipCodes(gc); // calc clipcodes against user clip planes

		// project the eye coordinate space into a canonical view volume with
		// extremes [-1,-1,-1] -> [1,1,1] (Note, that different volumes can be used
		// see Blinn, Jim. "A Trip Down the Graphics Pipeline: Line Clipping", IEEE
		// Computer Graphics and Applications, vol. 11, no. 1, pp. 98-105, January 1991
		// (also in his book, Jim Blinn's Corner: Trip Down the Graphics Pipeline")).

		GLMatrix4f.transform(v.eye, gc.transform.projection.matrix, v.clip);

		// Due to the projection transformation above, the transformed clip coordinates
		// are clipped against the unit cube [-1,-1,-1] -> [1,1,1], the advantage being that
		// it makes the clipping problem consistent.

		v.frustumClipCodes(); // calc clipcodes against view volume

		// auto generate texture coords if required !!!!
	}
}
