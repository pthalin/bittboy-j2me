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
import org.thenesis.pjogles.GLInvalidEnumException;

/**
 * Encapsulates a material state.
 *
 * @see org.thenesis.pjogles.states.GLLightState GLLightState
 *
 * @author tdinneen
 */
public final class GLMaterialState implements GLConstants {
	public final GLColor ambient = new GLColor(0.2f, 0.2f, 0.2f, 1.0f);
	public final GLColor diffuse = new GLColor(0.8f, 0.8f, 0.8f, 1.0f);
	public final GLColor specular = new GLColor(0.0f, 0.0f, 0.0f, 1.0f);
	public final GLColor emissive = new GLColor(0.0f, 0.0f, 0.0f, 1.0f);

	public float specularExponent = 0.0f; // shininess

	public final void setMaterial(final int p, final float pv[]) {
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
		case GL_EMISSION:
			emissive.set(pv);
			break;
		case GL_SHININESS:
			specularExponent = pv[0];
			break;
		case GL_AMBIENT_AND_DIFFUSE:
			ambient.set(pv);
			diffuse.set(pv);
			break;
		case GL_COLOR_INDEXES:
			throw new GLInvalidEnumException("GLMaterialState.setColor(int, GLColor) - Index color mode not supported.");
		default:
			throw new GLInvalidEnumException("GLMaterialState.setColor(int, GLColor)");
		}
	}

	public final void setColor(final int p, final GLColor color) {
		switch (p) {
		case GL_AMBIENT:
			ambient.set(color);
			break;
		case GL_DIFFUSE:
			diffuse.set(color);
			break;
		case GL_SPECULAR:
			specular.set(color);
			break;
		case GL_EMISSION:
			emissive.set(color);
			break;
		case GL_AMBIENT_AND_DIFFUSE:
			ambient.set(color);
			diffuse.set(color);
			break;
		default:
			throw new GLInvalidEnumException("GLMaterialState.setColor(int, GLColor)");
		}
	}

	public final void set(final GLMaterialState s) {
		ambient.set(s.ambient);
		diffuse.set(s.diffuse);
		specular.set(s.specular);
		emissive.set(s.emissive);
		specularExponent = s.specularExponent;
	}
}
