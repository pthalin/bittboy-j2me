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

package org.thenesis.pjogles;

import org.thenesis.pjogles.math.GLMatrix4f;

/**
 * An encapsulation that represents a transform state (whether it's a
 * modelview, projection or texture transform) that can be
 * pushed and popped onto and off of the transform stack (GLTransformStack) that is associated
 * with a GL context (in this case GLSoftwareContext).
 *
 * @author tdinneen
 */
public final class GLTransform implements GLConstants {
	public final GLMatrix4f matrix = new GLMatrix4f();
	public final GLMatrix4f inverseTranspose = new GLMatrix4f();

	public boolean updateInverseTranspose = false;

	public float rescaleFactor = 0.0f; // Scaling factor for GL_RESCALE_NORMAL

	public GLTransform() {
	}

	public GLTransform(final GLTransform t) {
		if (t == null)
			throw new IllegalArgumentException("GLTransform(GLTransform) - The supplied GLTransform is null.");

		set(t);
	}

	public final void set(final GLTransform t) {
		if (t == null)
			throw new IllegalArgumentException("GLTransform.set(GLTransform) - The supplied GLTransform is null.");

		matrix.set(t.matrix);
		inverseTranspose.set(t.inverseTranspose);
		updateInverseTranspose = t.updateInverseTranspose;
	}

	public final void computeInverseTranspose(final GLSoftwareContext gc) {
		if (gc == null)
			throw new IllegalArgumentException(
					"GLTransform.computeInverseTranspose(GLSoftwareContext) - The supplied GLSoftwareContext is null.");

		GLMatrix4f.invertTransposeMatrix(matrix, inverseTranspose);

		if (gc.state.enables.rescaleNormals) {
			final float[] m = inverseTranspose.m;
			final float factor = (float) Math.sqrt(m[2] * m[2] + m[6] * m[6] + m[10] * m[10]);

			if (factor == 0.0f)
				rescaleFactor = 1.0f;
			else
				rescaleFactor = 1.0f / factor;
		}

		updateInverseTranspose = GL_FALSE;
	}
}
