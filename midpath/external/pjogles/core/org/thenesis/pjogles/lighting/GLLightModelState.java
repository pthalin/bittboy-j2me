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
 * Encapsulates a light model state.
 *
 * @see org.thenesis.pjogles.states.GLLightState GLLightState
 *
 * @author tdinneen
 */
public final class GLLightModelState implements GLConstants {
	public final GLColor ambient = new GLColor(0.2f, 0.2f, 0.2f, 1.0f);
	public boolean localViewer = false; // local (or infinite) view point
	public boolean twoSided = false; // two (or one) sided lighting
	public int colorControl = GL_SINGLE_COLOR; // either GL_SINGLE_COLOR or GL_SEPARATE_SPECULAR_COLOR

	public final void setParameter(final int p, final int pv[]) {
		switch (p) {
		case GL_LIGHT_MODEL_AMBIENT:
			ambient.set(pv[0], pv[1], pv[2], pv[3]);
			break;
		case GL_LIGHT_MODEL_LOCAL_VIEWER:
			localViewer = pv[0] != 0;
			break;
		case GL_LIGHT_MODEL_TWO_SIDE:
			twoSided = pv[0] != 0;
			break;
		case GL_LIGHT_MODEL_COLOR_CONTROL:

			if (pv[0] == GL_SINGLE_COLOR || pv[0] == GL_SEPARATE_SPECULAR_COLOR)
				colorControl = pv[0];
			else
				throw new GLInvalidEnumException("GLLightModelState.setParameter(int, int[])");

			break;

		default:
			throw new GLInvalidEnumException("GLLightModelState.setParameter(int, int[])");
		}
	}

	public final void setParameter(final int p, final float pv[]) {
		switch (p) {
		case GL_LIGHT_MODEL_AMBIENT:
			ambient.set(pv[0], pv[1], pv[2], pv[3]);
			break;
		case GL_LIGHT_MODEL_LOCAL_VIEWER:
			localViewer = pv[0] != 0.0f;
			break;
		case GL_LIGHT_MODEL_TWO_SIDE:
			twoSided = pv[0] != 0.0f;
			break;
		case GL_LIGHT_MODEL_COLOR_CONTROL:

			if (pv[0] == GL_SINGLE_COLOR || pv[0] == GL_SEPARATE_SPECULAR_COLOR)
				colorControl = (int) pv[0];
			else
				throw new GLInvalidEnumException("GLLightModelState.setParameter(int, float[])");

			break;

		default:
			throw new GLInvalidEnumException("GLLightModelState.setParameter(int, float[])");
		}
	}

	public final void set(final GLLightModelState s) {
		ambient.set(s.ambient);
		localViewer = s.localViewer;
		twoSided = s.twoSided;
		colorControl = s.colorControl;
	}
}
