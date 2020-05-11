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

import java.util.Hashtable;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLInvalidOperationException;
import org.thenesis.pjogles.GLSoftwareContext;

/**
 * @author tdinneen
 */
public final class GLTextureManager implements GLConstants {
	public final GLSoftwareContext gc;

	public GLTextureManager(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void setTexture(final int target, final int level, final int border, final GLTextureBuffer buffer) {
		gc.state.texture.currentUnit.setTexture(target, level, border, buffer);
	}

	public final void setParameter(final int target, final int pname, final float p) {
		gc.state.texture.currentUnit.setParameter(target, pname, p);
	}

	public final void setParameter(final int target, final int pname, final float[] pv) {
		gc.state.texture.currentUnit.setParameter(target, pname, pv);
	}

	public final void glGenTextures(final int n, final int[] textures) {
		final Hashtable textureObjects = gc.state.shared.textureObjects;

		for (int i = 0, name = 1; i < n; ++name) {
			if (textureObjects.get(new Integer(name)) == null) {
				textures[i++] = name;
				textureObjects.put(new Integer(name), new GLTextureObject(name, 0));
			}
		}
	}

	public final void glBindTexture(final int target, final int name) {
		final GLTextureUnitState textureUnit = gc.state.texture.currentUnit;
		final GLTextureObject oldTexture;

		switch (target) {
		case GL_TEXTURE_1D:
			oldTexture = textureUnit.current1D;
			break;
		case GL_TEXTURE_2D:
			oldTexture = textureUnit.current2D;
			break;
		case GL_TEXTURE_3D:
			oldTexture = textureUnit.current3D;
			break;
		default:
			throw new GLInvalidEnumException("GLTextureManager.glBindTexture(int, int)");
		}

		if (oldTexture != null && oldTexture.name == name)
			return; // rebinding the same texture - no change

		if (name == 0) {
			switch (target) {
			case GL_TEXTURE_1D:
				textureUnit.current1D = gc.state.shared.default1D;
				break;
			case GL_TEXTURE_2D:
				textureUnit.current2D = gc.state.shared.default2D;
				break;
			case GL_TEXTURE_3D:
				textureUnit.current3D = gc.state.shared.default3D;
				break;
			default:
				throw new GLInvalidEnumException("GLTextureManager.glBindTexture(int, int)");
			}
		} else {
			GLTextureObject newTexture = (GLTextureObject) gc.state.shared.textureObjects.get(new Integer(name));

			if (newTexture != null) {
				if (newTexture.target != 0 && newTexture.target != target)
					throw new GLInvalidOperationException(
							"GLTextureManager.glBindTexture(int, int) - Binding a GLTextureObject with the wrong dimension !");
			} else
				newTexture = new GLTextureObject(name, target);

			switch (target) {
			case GL_TEXTURE_1D:
				textureUnit.current1D = newTexture;
				break;
			case GL_TEXTURE_2D:
				textureUnit.current2D = newTexture;
				break;
			case GL_TEXTURE_3D:
				textureUnit.current3D = newTexture;
				break;
			default:
				throw new GLInvalidEnumException("GLTextureManager.glBindTexture(int, int)");
			}
		}
	}
}
