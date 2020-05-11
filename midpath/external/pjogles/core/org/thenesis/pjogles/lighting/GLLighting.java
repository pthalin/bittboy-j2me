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

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.math.GLMath;
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;

/**
 * Encapsulates all lighting operations.
 *
 * @see org.thenesis.pjogles.GL#glLightf glLightf
 *
 * @author tdinneen
 */
public final class GLLighting implements GLConstants {
	protected final GLSoftwareContext gc;

	private final GLColor ambient = new GLColor();
	private final GLColor diffuse = new GLColor();
	private final GLColor specular = new GLColor();

	private final GLVector4f direction = new GLVector4f();
	private final GLVector4f lightDirection = new GLVector4f();

	public GLLighting(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void setColor(final float red, final float green, final float blue, final float alpha) {
		// Neither floating-point nor signed integer values are clamped to the range [0,1]
		// before updating the current color. However, color components are clamped to
		// this range before they are interpolated or written into a color buffer.

		final GLColor color = gc.state.current.color;
		color.set(red, green, blue, alpha);

		if (gc.state.enables.colorMaterial)
			gc.state.lighting.setColor(color);
	}

	public final void apply(final GLVertex v, final int face) {
		final long start = System.currentTimeMillis();

		final GLColor color;
		final GLMaterialState material;
		final float nx, ny, nz;

		/* Pick material to use */
		if (face == __GL_FRONTFACE) {
			color = v.color;
			// specularColor = v.color[__GL_FRONTFACE_SPECULAR];
			material = gc.state.lighting.front;
			nx = v.transformedNormal.x;
			ny = v.transformedNormal.y;
			nz = v.transformedNormal.z;
		} else {
			// TOMD - what's going on below !!! why __GL_BACKFACE not set

			//color = v.color[__GL_BACKFACE];
			color = v.color;
			// specularColor = v.color[__GL_BACKFACE_SPECULAR];
			material = gc.state.lighting.back;
			nx = -v.transformedNormal.x;
			ny = -v.transformedNormal.y;
			nz = -v.transformedNormal.z;
		}

		GLColor color1 = material.emissive;
		GLColor color2 = gc.state.lighting.model.ambient;
		final GLColor color3 = material.ambient;

		float r = color1.r + color2.r * color3.r;
		float g = color1.g + color2.g * color3.g;
		float b = color1.b + color2.b * color3.b;
		final float a = material.diffuse.a;

		int lightMask = gc.state.enables.lights;
		GLLightSourceState lss;
		float attenuation;
		float dot;
		float spotlightEffect;
		float d, t;
		float specularCoefficient;
		float sx, sy, sz;

		if (lightMask != 0) {
			for (int i = 0; i < MAX_NUMBER_OF_LIGHTS && lightMask != 0; ++i) {
				if ((lightMask & 1) != 0) {
					lss = gc.state.lighting.lightSources[i];

					color1 = lss.ambient;
					color2 = material.ambient;

					ambient.r = color1.r * color2.r;
					ambient.g = color1.g * color2.g;
					ambient.b = color1.b * color2.b;

					// Compute lightDirection and attenuation
					if (lss.eyePosition.w == 0.0f) {
						// Directional lighting
						lightDirection.set(lss.eyePosition);
						lightDirection.normalize();

						// TOMD - we should have a precalculated normalised direction

						attenuation = 1.0f;
					} else {
						// Positional lighting
						lightDirection.x = lss.eyePosition.x - v.eye.x;
						lightDirection.y = lss.eyePosition.y - v.eye.y;
						lightDirection.z = lss.eyePosition.z - v.eye.z;

						d = lightDirection.normalize();

						attenuation = lss.constantAttenuation + d
								* (lss.linearAttenuation + d * lss.quadraticAttenuation);

						attenuation = attenuation == 0.0f ? 1.0f : 1.0f / attenuation;
					}

					dot = lightDirection.x * nx + lightDirection.y * ny + lightDirection.z * nz;

					// Diffuse and specular terms
					if (dot <= 0.0f) {
						// Surface triangles away from lighting, no diffuse or specular
						r += attenuation * ambient.r;
						g += attenuation * ambient.g;
						b += attenuation * ambient.b;
						// Done with this lighting
					} else {
						color1 = lss.diffuse;
						color2 = material.diffuse;

						// Diffuse term
						diffuse.r = dot * color1.r * color2.r;
						diffuse.g = dot * color1.g * color2.g;
						diffuse.b = dot * color1.b * color2.b;

						if (lss.spotLightCutOffAngle == 180.0f) // Not a spot lighting
							spotlightEffect = 1.0f;
						else {
							// direction points from lighting to vertices
							d = -lightDirection.x * lss.eyeDirection.x + -lightDirection.y * lss.eyeDirection.y
									+ -lightDirection.z * lss.eyeDirection.z;

							if (d <= 0.0f || Math.acos(d) * GLMath.RAD2DEG > lss.spotLightCutOffAngle)
								spotlightEffect = 0.0f; // Outside of cone
							else
								spotlightEffect = (float) Math.pow(dot, lss.spotLightExponent);
						}

						if (gc.state.lighting.model.localViewer) {
							direction.set(v.eye);
							direction.normalize();

							sx = lightDirection.x - direction.x;
							sy = lightDirection.y - direction.y;
							sz = lightDirection.z - direction.z;
						} else {
							sx = lightDirection.x;
							sy = lightDirection.y;
							sz = lightDirection.z;
						}

						dot = sx * nx + sy * ny + sz * nz;

						if (dot <= 0.0f)
							specular.set(0.0f, 0.0f, 0.0f, 0.0f);
						else {
							dot = dot / (float) Math.sqrt(sx * sx + sy * sy + sz * sz);
							specularCoefficient = (float) Math.pow(dot, material.specularExponent);

							if (specularCoefficient < 1.0e-10)
								specular.set(0.0f, 0.0f, 0.0f, 0.0f);
							else {
								color1 = lss.specular;
								color2 = material.specular;

								specular.r = specularCoefficient * color1.r * color2.r;
								specular.g = specularCoefficient * color1.g * color2.g;
								specular.b = specularCoefficient * color1.b * color2.b;
							}
						}

						t = attenuation * spotlightEffect;

						r += t * (ambient.r + diffuse.r + specular.r);
						g += t * (ambient.g + diffuse.g + specular.g);
						b += t * (ambient.b + diffuse.b + specular.b);
					}
				}

				lightMask >>= 1;
			}

			color.set(r < 0.0f ? 0.0f : (r > 1.0f ? 1.0f : r), g < 0.0f ? 0.0f : (g > 1.0f ? 1.0f : g), b < 0.0f ? 0.0f
					: (b > 1.0f ? 1.0f : b), a < 0.0f ? 0.0f : (a > 1.0f ? 1.0f : a));

			gc.benchmark.lightingTime += System.currentTimeMillis() - start;
		}
	}
}
