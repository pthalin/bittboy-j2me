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

package org.thenesis.pjogles.states;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.lighting.GLLightModelState;
import org.thenesis.pjogles.lighting.GLLightSourceState;
import org.thenesis.pjogles.lighting.GLMaterialState;

/**
 * @author tdinneen
 */
public final class GLLightState implements GLConstants {
	public int colorMaterialFace = GL_FRONT_AND_BACK;
	public int colorMaterialParam = GL_AMBIENT_AND_DIFFUSE;
	public int shadingModel = GL_SMOOTH; // one of GL_FLAT / GL_SMOOTH, see glShadeModel(int)

	public final GLLightModelState model;

	public final GLMaterialState front;
	public final GLMaterialState back;

	public final GLLightSourceState[] lightSources = new GLLightSourceState[MAX_NUMBER_OF_LIGHTS];

	public GLLightState() {
		model = new GLLightModelState();

		front = new GLMaterialState();
		back = new GLMaterialState();

		for (int i = MAX_NUMBER_OF_LIGHTS; --i >= 0;)
			lightSources[i] = new GLLightSourceState();

		// The default diffuse intensity is (0.0, 0.0, 0.0, 1.0) for all lights other than lighting zero.
		// The default diffuse intensity of lighting zero is (1.0, 1.0, 1.0, 1.0).

		lightSources[0].diffuse.set(1.0f, 1.0f, 1.0f, 1.0f);

		// The default specular intensity is (0.0, 0.0, 0.0, 1.0) for all lights other than lighting zero.
		// The default specular intensity of lighting zero is (1.0, 1.0, 1.0, 1.0).

		lightSources[0].specular.set(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public final void setColor(final GLColor color) {
		switch (colorMaterialFace) {
		case GL_FRONT_AND_BACK:
			front.setColor(colorMaterialParam, color);
			back.setColor(colorMaterialParam, color);
			break;
		case GL_FRONT:
			front.setColor(colorMaterialParam, color);
			break;
		case GL_BACK:
			back.setColor(colorMaterialParam, color);
			break;
		default:
			throw new GLInvalidEnumException("GLLightState.setMaterial(int, int, GLColor)");
		}
	}

	public final void setMaterial(final int face, final int p, final float[] pv) {
		switch (face) {
		case GL_FRONT_AND_BACK:
			front.setMaterial(p, pv);
			back.setMaterial(p, pv);
			break;
		case GL_FRONT:
			front.setMaterial(p, pv);
			break;
		case GL_BACK:
			back.setMaterial(p, pv);
			break;
		default:
			throw new GLInvalidEnumException("GLLightState.setMaterial(int, int, GLColor)");
		}
	}

	public final GLLightState get() {
		final GLLightState s = new GLLightState();
		s.set(this);

		return s;
	}

	public final void set(final GLLightState s) {
		colorMaterialFace = s.colorMaterialFace;
		colorMaterialParam = s.colorMaterialParam;
		shadingModel = s.shadingModel;
		model.set(s.model);
		front.set(s.front);
		back.set(s.back);

		for (int i = MAX_NUMBER_OF_LIGHTS; --i >= 0;)
			lightSources[i].set(s.lightSources[i]);
	}
}
