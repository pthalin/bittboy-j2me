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
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLBitmap;
import org.thenesis.pjogles.primitives.GLPolygon;
import org.thenesis.pjogles.render.GLRenderException;

/**
 * The base class for GLRasterizer, GLFeedback and GLSelect, the three
 * supported <strong>glRenderMode</strong> states.
 *
 * @see org.thenesis.pjogles.GL#glRenderMode glRenderMode
 * @see GLRasterizer GLRasterizer
 * @see GLFeedback GLFeedback
 * @see GLSelect GLSelect
 *
 * @author tdinneen
 */
public interface GLRenderer extends GLConstants {
	public void render(GLVertex v);

	public void render(GLVertex v1, GLVertex v2);

	public void render(GLPolygon polygon) throws GLRenderException;

	public void render(GLBitmap bitmap);
}
