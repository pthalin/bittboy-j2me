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
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.texture.GLTextureUnitState;

/**
 * @author tdinneen
 */
public final class GLTextureState implements GLConstants {
	/**
	 * Current active texture unit.
	 */
	public int currentUnitIndex = 0;

	/**
	 * Current state for all texture units.
	 */
	public final GLTextureUnitState[] unit = new GLTextureUnitState[NUMBER_OF_TEXTURE_UNITS];

	// validated

	/**
	 * Currently active GLTextureUnitState.
	 */
	public GLTextureUnitState currentUnit;

	public int enabledUnits; // one bit set for each enabled unit
	public int textureGenEnabledUnits; // one bit set for each enabled unit

	public GLTextureState() {
		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			unit[i] = new GLTextureUnitState();

		currentUnit = unit[currentUnitIndex];
	}

	public final GLTextureState get() {
		final GLTextureState s = new GLTextureState();
		s.set(this);

		return s;
	}

	public final void set(final GLTextureState s) {
		currentUnitIndex = s.currentUnitIndex;

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			unit[i].set(s.unit[i]);

		currentUnit = unit[0];
	}

	public final void validate(final GLSoftwareContext gc) {
		enabledUnits = 0;
		textureGenEnabledUnits = 0;

		final int[] textureEnables = gc.state.enables.texture;

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			final int bit = 1 << i;

			final GLTextureUnitState textureUnit = unit[i];
			textureUnit.currentTexture = null;

			if ((textureEnables[i] & TEXTURE_3D_ENABLE) != 0) {
				textureUnit.currentTexture = textureUnit.current3D;
				enabledUnits |= bit;
			} else if ((textureEnables[i] & TEXTURE_2D_ENABLE) != 0) {
				textureUnit.currentTexture = textureUnit.current2D;
				enabledUnits |= bit;
			} else if ((textureEnables[i] & TEXTURE_1D_ENABLE) != 0) {
				textureUnit.currentTexture = textureUnit.current1D;
				enabledUnits |= bit;
			}

			if ((textureEnables[i] & TEXTURE_GEN_ENABLE_MASK) != 0)
				textureGenEnabledUnits |= bit;
		}
	}
}
