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

/**
 * @author tdinneen
 */
public final class GLHintState implements GLConstants {
	public int perspectiveCorrection = GL_DONT_CARE;
	public int pointSmooth = GL_DONT_CARE;
	public int lineSmooth = GL_DONT_CARE;
	public int polygonSmooth = GL_DONT_CARE;
	public int fog = GL_DONT_CARE;
	public int textureBuffer = GL_DONT_CARE;

	public final GLHintState get() {
		final GLHintState s = new GLHintState();
		s.set(this);

		return s;
	}

	public final void set(final GLHintState s) {
		perspectiveCorrection = s.perspectiveCorrection;
		pointSmooth = s.pointSmooth;
		lineSmooth = s.lineSmooth;
		polygonSmooth = s.polygonSmooth;
		fog = s.fog;
		textureBuffer = s.textureBuffer;
	}
}
