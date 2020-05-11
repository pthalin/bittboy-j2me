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

import org.thenesis.pjogles.evaluators.GLEvalGrid2;

/**
 * @author tdinneen
 */
public final class GLEvaluatorState {
	public final GLEvalGrid2 u1 = new GLEvalGrid2();
	public final GLEvalGrid2 u2 = new GLEvalGrid2();
	public final GLEvalGrid2 v2 = new GLEvalGrid2();

	public final GLEvaluatorState get() {
		final GLEvaluatorState s = new GLEvaluatorState();
		s.set(this);

		return s;
	}

	public final void set(final GLEvaluatorState s) {
		u1.set(s.u1);
		u2.set(s.u2);
		v2.set(s.v2);
	}
}
