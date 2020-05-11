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

import org.thenesis.pjogles.*;
import org.thenesis.pjogles.math.GLMatrix4f;
import org.thenesis.pjogles.math.GLVector4f;

/**
 * Encapsulates a light source state.
 *
 * @see org.thenesis.pjogles.states.GLLightState GLLightState
 *
 * @author tdinneen
 */
public final class GLLightSourceState implements GLConstants {
	// The params parameter contains four integer or floating-point values that specify the ambient
	// RGBA intensity of the lighting. Integer values are mapped linearly such that the most positive
	// representable value maps to 1.0, and the most negative representable value maps to –1.0.
	// Floating-point values are mapped directly. Neither integer nor floating-point values are clamped.
	// The default ambient lighting intensity is (0.0, 0.0, 0.0, 1.0).

	public final GLColor ambient = new GLColor(0.0f, 0.0f, 0.0f, 1.0f);;

	// The params parameter contains four integer or floating-point values that specify the diffuse RGBA
	// intensity of the lighting. Integer values are mapped linearly such that the most positive representable
	// value maps to 1.0, and the most negative representable value maps to –1.0.
	// Floating-point values are mapped directly. Neither integer nor floating-point values are clamped.
	// The default diffuse intensity is (0.0, 0.0, 0.0, 1.0) for all lights other than lighting zero.
	// The default diffuse intensity of lighting zero is (1.0, 1.0, 1.0, 1.0).

	public final GLColor diffuse = new GLColor(0.0f, 0.0f, 0.0f, 1.0f);

	// The params parameter contains four integer or floating-point values that specify the specular RGBA
	// intensity of the lighting. Integer values are mapped linearly such that the most positive representable
	// value maps to 1.0, and the most negative representable value maps to –1.0.
	// Floating-point values are mapped directly. Neither integer nor floating-point values are clamped.
	// The default specular intensity is (0.0, 0.0, 0.0, 1.0) for all lights other than lighting zero.
	// The default specular intensity of lighting zero is (1.0, 1.0, 1.0, 1.0).

	public final GLColor specular = new GLColor(0.0f, 0.0f, 0.0f, 1.0f);

	// The params parameter contains four integer or floating-point values that specify the position of the
	// lighting in homogeneous object coordinates. Both integer and floating-point values are mapped directly.
	// Neither integer nor floating-point values are clamped.
	// The position is transformed by the modelview matrix when glLight is called
	// (just as if it were a point), and it is stored in eye coordinates.
	// If the w component of the position is 0.0, the lighting is treated as a directional source.
	// Diffuse and specular lighting calculations take the lights direction, but not its actual position,
	// into account, and attenuation is disabled. Otherwise, diffuse and specular lighting calculations
	// are based on the actual location of the lighting in eye coordinates, and attenuation is enabled.
	// The default position is (0,0,1,0); thus, the default lighting source is directional, parallel to,
	// and in the direction of the –z axis.

	public final GLVector4f eyePosition = new GLVector4f(0.0f, 0.0f, 1.0f, 0.0f);

	// The params parameter contains three integer or floating-point values that specify the direction of
	// the lighting in homogeneous object coordinates. Both integer and floating-point values are mapped
	// directly. Neither integer nor floating-point values are clamped.
	// The spot direction is transformed by the inverse of the modelview matrix when glLight is called
	// (just as if it were a normal), and it is stored in eye coordinates. It is significant only when
	// GL_SPOT_CUTOFF is not 180, which it is by default. The default direction is (0,0,–1).

	public final GLVector4f eyeDirection = new GLVector4f(0.0f, 0.0f, -1.0f, 0.0f);

	// The params parameter is a single integer or floating-point value that specifies the intensity
	// distribution of the lighting. Integer and floating-point values are mapped directly.
	// Only values in the range [0,128] are accepted.
	// Effective lighting intensity is attenuated by the cosine of the angle between the direction of
	// the lighting and the direction from the lighting to the vertices being lighted, raised to the power
	// of the spot exponent. Thus, higher spot exponents result in a more focused lighting source,
	// egardless of the spot cutoff angle (see the following paragraph).
	// The default spot exponent is 0, resulting in uniform lighting distribution.

	public float spotLightExponent = 0.0f;

	// The params parameter is a single integer or floating-point value that specifies the maximum
	// spread angle of a lighting source. Integer and floating-point values are mapped directly.
	// Only values in the range [0,90], and the special value 180, are accepted.
	// If the angle between the direction of the lighting and the direction from the lighting to the
	// vertices being lighted is greater than the spot cutoff angle, then the lighting is completely masked.
	// Otherwise, its intensity is controlled by the spot exponent and the attenuation factors.
	// The default spot cutoff is 180, resulting in uniform lighting distribution.

	public float spotLightCutOffAngle = 180.0f; // in degrees

	// The params parameter is a single integer or floating-point value that specifies one of the three
	// lighting attenuation factors. Integer and floating-point values are mapped directly.
	// Only nonnegative values are accepted. If the lighting is positional, rather than directional,
	// its intensity is attenuated by the reciprocal of the sum of: the constant factor,
	// the linear factor multiplied by the distance between the lighting and the vertices being lighted,
	// and the quadratic factor multiplied by the square of the same distance.
	// The default attenuation factors are (1,0,0), resulting in no attenuation.

	public float constantAttenuation = 1.0f;
	public float linearAttenuation = 0.0f;
	public float quadraticAttenuation = 0.0f;

	public final void setParameter(final GLSoftwareContext gc, final int p, final float[] pv) {
		switch (p) {
		case GL_AMBIENT:

			ambient.set(pv);
			break;

		case GL_DIFFUSE:

			diffuse.set(pv);
			break;

		case GL_SPECULAR:

			specular.set(pv);
			break;

		case GL_POSITION:

			eyePosition.set(pv);
			GLMatrix4f.transform(eyePosition, gc.transform.modelView.matrix, eyePosition);
			break;

		case GL_SPOT_DIRECTION:

			eyeDirection.x = pv[0];
			eyeDirection.y = pv[1];
			eyeDirection.z = pv[2];
			eyeDirection.w = 1.0f;

			transformLightDirection(gc);

			break;

		case GL_SPOT_EXPONENT:

			// a spot exponent value was specified outside the range [0,128] ?

			if ((pv[0] < 0.0f) || (pv[0] > 128.0f))
				throw new GLInvalidValueException("GLLightSourceState.setParameter(GLSoftwareContext, int, float[])");

			spotLightExponent = pv[0];

			break;

		case GL_SPOT_CUTOFF:

			// a spot cutoff was specified outside the range [0,90]
			// (except for the special value 180), or if a negative attenuation factor was specified ?

			if ((pv[0] != 180.0f) && ((pv[0] < 0.0f) || (pv[0] > 90.0f)))
				throw new GLInvalidValueException("GLLightSourceState.setParameter(GLSoftwareContext, int, float[])");

			spotLightCutOffAngle = pv[0];

			break;

		case GL_CONSTANT_ATTENUATION:

			if (pv[0] < 0.0f)
				throw new GLInvalidValueException("GLLightSourceState.setParameter(GLSoftwareContext, int, float[])");

			constantAttenuation = pv[0];

			break;

		case GL_LINEAR_ATTENUATION:

			if (pv[0] < 0.0f)
				throw new GLInvalidValueException("GLLightSourceState.setParameter(GLSoftwareContext, int, float[])");

			linearAttenuation = pv[0];

			break;

		case GL_QUADRATIC_ATTENUATION:

			if (pv[0] < 0.0f)
				throw new GLInvalidValueException("GLLightSourceState.setParameter(GLSoftwareContext, int, float[])");

			quadraticAttenuation = pv[0];

			break;

		default:

			throw new GLInvalidEnumException("GLLightSourceState.setParameter(GLSoftwareContext, int, float[])");
		}
	}

	private void transformLightDirection(final GLSoftwareContext gc) {
		//        final float q;
		//
		//        if (eyePosition.w != 0.0f)
		//            q = -(eyeDirection.x * eyePosition.x + eyeDirection.y * eyePosition.y + eyeDirection.z * eyePosition.z) / eyePosition.w;
		//        else
		//            q = 0.0f;
		//
		//        eyeDirection.w = q;

		final GLTransform transform = gc.transform.modelView;

		if (transform.updateInverseTranspose)
			transform.computeInverseTranspose(gc);

		GLMatrix4f.transform(eyeDirection, transform.inverseTranspose, eyeDirection);
		eyeDirection.normalize();
	}

	public final void set(final GLLightSourceState state) {
		ambient.set(state.ambient);
		diffuse.set(state.diffuse);
		specular.set(state.specular);
		eyePosition.set(state.eyePosition);
		eyeDirection.set(state.eyeDirection);
		spotLightExponent = state.spotLightExponent;
		spotLightCutOffAngle = state.spotLightCutOffAngle;
		constantAttenuation = state.constantAttenuation;
		linearAttenuation = state.linearAttenuation;
		quadraticAttenuation = state.quadraticAttenuation;
	}
}
