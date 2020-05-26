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

package org.thenesis.pjogles.math;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.clipping.GLClipping;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.texture.GLTextureUnitState;

/**
 * A GLVertex is used by the GLPipeline to maintain state information.
 * Whenever a <strong>glVertex</strong> command is called the current normal, color
 * and relative texture state is copied into this GLVertex structure.
 * <p>
 * This GLVertex is also used to house the clipped, transformed and projected position.
 *
 * @see org.thenesis.pjogles.GL#glVertex4f glVertex
 *
 * @author tdinneen
 */
public final class GLVertex implements GLConstants {
	/*
	 ** Coordinates straight from client. These fields may not be
	 ** set depending on the active modes. The normal and texture
	 ** coordinate are used by lighting and texturing.  These cells
	 ** may be overwritten by the eyeNormal and the generated texture
	 ** coordinate, depending on the active modes.
	 */
	public final GLVector4f position = new GLVector4f();

	public final GLVector4f normal = new GLVector4f();
	public final GLVector4f transformedNormal = new GLVector4f();

	public final GLColor color = new GLColor(); // set in derived

	/*
	 ** Projected clip coordinate. This field is filled in when the users
	 ** object coordinate has been multiplied by the combined modelview
	 ** and projection matrix.
	 */
	public final GLVector4f clip = new GLVector4f();

	public int clipCodes;

	/*
	 ** For value for this vertices. Used only for cheap fogging.
	 */
	public float fog;

	public final GLVector4f normalizedDevice = new GLVector4f();

	/*
	 ** Window and eye coordinate. This field is filled in when the window
	 ** clip coordinate is converted to a drawing surface relative "window"
	 ** coordinate.
	 ** NOTE: the window.w coordinate contains 1/clip.w.
	 */
	public final GLVector4f window = new GLVector4f();
	public final GLVector4f eye = new GLVector4f();

	/*
	 ** Coordinates straight from client. These fields may not be
	 ** set depending on the active modes.  The normal and texture
	 ** coordinate are used by lighting and texturing.  These cells
	 ** may be overwritten by the eyeNormal and the generated texture
	 ** coordinate, depending on the active modes.
	 */
	public final GLVector4f[] texture = new GLVector4f[NUMBER_OF_TEXTURE_UNITS];

	public GLVertex() {
		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			texture[i] = new GLVector4f();
	}

	public final void set(final GLVertex v) {
		position.set(v.position);
		normal.set(v.normal);
		transformedNormal.set(v.transformedNormal);
		clip.set(v.clip);
		clipCodes = v.clipCodes;

		fog = v.fog;
		normalizedDevice.set(v.normalizedDevice);

		window.set(v.window);
		eye.set(v.eye);

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
			texture[i].set(v.texture[i]);

		color.set(v.color);
	}

	public final void frustumClipCodes() {
		// need to remember the user clipCode flags
		// set userClipCodes() below !

		clipCodes |= GLClipping.frustumClipCodes(clip);
	}

	/*
	 ** Clip check against the frustum and user clipping planes.
	 */
	public final void userClipCodes(final GLSoftwareContext gc) {
		// user clip code are calculated first,
		// so we can clear the clipCodes flag

		clipCodes = GLClipping.userClipCodes(gc, eye);
	}

	public final boolean saveCurrentState(final GLSoftwareContext gc) {
		boolean needNormal = false;

		if (gc.state.enables.lighting)
			needNormal = true;
		else
			color.set(gc.state.current.color);

		if (gc.state.texture.enabledUnits != 0) {
			final GLVector4f[] textures = gc.state.current.texture;

			for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;)
				texture[i].set(textures[i]);

			if (!needNormal) {
				// make sure that if we don't already have the normal saved
				// that we will save it if texturing requires it

				final int[] textureEnables = gc.state.enables.texture;
				final GLTextureUnitState currentUnit = gc.state.texture.currentUnit;

				for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
					if ((textureEnables[i] & TEXTURE_GEN_S_ENABLE) != 0) {
						if (currentUnit.s.mode == GL_SPHERE_MAP) {
							needNormal = true;
							break;
						}
					}

					if ((textureEnables[i] & TEXTURE_GEN_T_ENABLE) != 0) {
						if (currentUnit.t.mode == GL_SPHERE_MAP) {
							needNormal = true;
							break;
						}
					}
				}
			}
		}

		if (needNormal)
			normal.set(gc.state.current.normal);

		return needNormal;
	}

	/*
	 public void validateVertex(GLSoftwareContext gc, int needs)
	 {
	 int has = flagBits;
	 int wants = needs & ~has;

	 if ((wants & __GL_HAS_EYE) != 0)
	 {
	 GLMatrix4f.transform(position, gc.transform.modelView.matrix, eye);
	 has = flagBits |= __GL_HAS_EYE;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_CLIP) != 0)
	 {
	 GLMatrix4f.transform(eye, gc.transform.projection.matrix, clip);
	 has = flagBits |= __GL_HAS_CLIP;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_CLIPCODES) != 0)
	 {
	 clipCheckAll(gc);
	 has = flagBits |= __GL_HAS_CLIPCODES;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_NORMALIZED_DEVICE) != 0)
	 {
	 gc.state.viewport.perspectiveDivision(clip, normalizedDevice);
	 has = flagBits |= __GL_HAS_NORMALIZED_DEVICE;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_WINDOW) != 0)
	 {
	 gc.state.viewport.normalizedDeviceToWindow(normalizedDevice, window);
	 has = flagBits |= __GL_HAS_WINDOW;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_NORMAL) != 0)
	 {
	 GLTransform tr = gc.transform.modelView;

	 if (tr.updateInverseTranspose)
	 tr.computeInverseTranspose(gc);

	 if ((gc.state.enables.general & __GL_NORMALIZE_ENABLE) != 0)
	 {
	 GLMatrix4f.transform(normal, tr.matrix, transformedNormal);
	 normal.normalize();
	 }
	 else
	 GLMatrix4f.transform(normal, tr.matrix, transformedNormal);

	 has = flagBits |= __GL_HAS_NORMAL;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_FOG) != 0)
	 {
	 fog = fogVertex(gc, eye.z);
	 has = flagBits |= __GL_HAS_FOG;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_TEXTURE) != 0)
	 {
	 GLVector4f t = texture[0];
	 GLMatrix4f.transform(t, gc.transform.texture.matrix, t);

	 has = flagBits |= __GL_HAS_TEXTURE;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_FRONT_COLOR) != 0)
	 {
	 GLLighting.apply(gc, this, __GL_FRONTFACE);
	 has = flagBits |= __GL_HAS_FRONT_COLOR;
	 wants = needs & ~has;
	 }

	 if ((wants & __GL_HAS_BACK_COLOR) != 0)
	 {
	 GLLighting.apply(gc, this, __GL_BACKFACE);
	 has = flagBits |= __GL_HAS_BACK_COLOR;
	 wants = needs & ~has;
	 }
	 }
	 */

	public final void fogVertexWithEye(final GLSoftwareContext gc) {
		fog = fogVertex(gc, eye.z);
	}

	public static float fogVertex(final GLSoftwareContext gc, float eyeZ) {
		if (eyeZ < 0.0f)
			eyeZ = -eyeZ;

		float fog = 0.0f;

		switch (gc.state.fog.mode) {
		case GL_EXP:
			fog = (float) Math.pow(Math.E, -gc.state.fog.density * eyeZ);
			break;
		case GL_EXP2:
			fog = (float) Math.pow(Math.E, -(Math.pow(gc.state.fog.density * eyeZ, 2)));
			break;
		case GL_LINEAR:
			fog = (gc.state.fog.end - eyeZ) * gc.state.fog.oneOverEMinusS;
			break;
		}

		if (fog < 0.0f)
			fog = 0.0f;
		else if (fog > 1.0f)
			fog = 1.0f;

		return fog;
	}
}
