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

package org.thenesis.pjogles.texture;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;

/**
 * @author tdinneen
 */
public final class GLTextureUnitState implements GLConstants {
	/**
	 * Per coordinate texture state (set with glTexGen).
	 */
	public final GLTextureGenState s = new GLTextureGenState();
	public final GLTextureGenState t = new GLTextureGenState();
	public final GLTextureGenState r = new GLTextureGenState();
	public final GLTextureGenState q = new GLTextureGenState();

	/**
	 * Per texture environment binding state.
	 */
	public final GLTextureEnvState[] env = new GLTextureEnvState[NUMBER_OF_TEXTURE_ENV_BINDINGS_PER_UNIT]; // set in derived class

	public GLTextureObject current1D;
	public GLTextureObject current2D;
	public GLTextureObject current3D;

	public GLTextureObject currentTexture;

	public GLTextureUnitState() {
		for (int i = NUMBER_OF_TEXTURE_ENV_BINDINGS_PER_UNIT; --i >= 0;)
			env[i] = new GLTextureEnvState();
	}

	public final void set(final GLTextureUnitState s) {
		this.s.set(s.s);

		t.set(s.t);
		r.set(s.r);
		q.set(s.q);

		for (int i = NUMBER_OF_TEXTURE_ENV_BINDINGS_PER_UNIT; --i >= 0;)
			env[i].set(s.env[i]);

		current1D = s.current1D;
		current2D = s.current2D;
		current3D = s.current3D;

		currentTexture = s.currentTexture;
	}

	public final void setTexture(final int target, final int level, final int border, final GLTextureBuffer buffer) {
		switch (target) {
		case GL_TEXTURE_1D:
			current1D.setTexture(level, border, buffer);
			break;
		case GL_TEXTURE_2D:
			current2D.setTexture(level, border, buffer);
			break;
		case GL_TEXTURE_3D:
			current3D.setTexture(level, border, buffer);
			break;
		default:
			throw new GLInvalidEnumException("GLTextureUnitState.setTexture(int, int, int, GLTextureBuffer)");
		}
	}

	public final void setParameter(final int target, final int pname, final float p) {
		switch (target) {
		case GL_TEXTURE_1D:
			current1D.setParameter(pname, p);
			break;
		case GL_TEXTURE_2D:
			current2D.setParameter(pname, p);
			break;
		case GL_TEXTURE_3D:
			current3D.setParameter(pname, p);
			break;
		default:
			throw new GLInvalidEnumException("GLTextureUnitState.setParameter(int, int, float)");
		}
	}

	public final void setParameter(final int target, final int pname, final float[] pv) {
		switch (target) {
		case GL_TEXTURE_1D:
			current1D.setParameter(pname, pv);
			break;
		case GL_TEXTURE_2D:
			current2D.setParameter(pname, pv);
			break;
		case GL_TEXTURE_3D:
			current3D.setParameter(pname, pv);
			break;
		default:
			throw new GLInvalidEnumException("GLTextureUnitState.setParameter(int, int, float[])");
		}
	}
}
