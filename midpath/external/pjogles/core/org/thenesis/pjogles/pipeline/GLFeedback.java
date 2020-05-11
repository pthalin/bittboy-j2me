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

import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLBitmap;
import org.thenesis.pjogles.primitives.GLPolygon;
import org.thenesis.pjogles.render.GLRenderException;

/**
 * An OpenGL feedback renderer.
 *
 * @see org.thenesis.pjogles.GL#glRenderMode glRenderMode
 *
 * @author tdinneen
 */
public final class GLFeedback implements GLRenderer {
	private final GLSoftwareContext gc;

	/**
	 * The user specified result array overflows, this bit is set.
	 */
	public boolean overFlowed = GL_FALSE;

	/**
	 * User specified result array.  As primitives are processed feedback
	 * demos.data will be entered into this array.
	 */
	public float[] resultBase;

	/**
	 * The number of GLfloat's that the array can hold.
	 */
	private int resultLength;

	/**
	 * Type of vertices wanted
	 */
	public int type;

	/**
	 * Current pointer into the result array.
	 */
	public int resultIndex;

	private GLVertex v0, v1, v2;

	public GLFeedback(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void reset() {
		resultIndex = 0;
		overFlowed = GL_FALSE;
	}

	public final void setBuffer(final int bufferLength, final int type, final float[] buffer) {
		resultLength = bufferLength;
		this.type = type;
		resultBase = buffer;

		overFlowed = GL_FALSE;
		resultIndex = 0;
	}

	public final void render(final GLVertex v) {
		feedbackValue(GL_POINT_TOKEN);
		feedback(v);
	}

	public final void render(final GLVertex v1, final GLVertex v2) {
		feedbackValue(GL_LINE_TOKEN);
		feedback(v1);
		feedback(v2);
	}

	public final void render(final GLPolygon polygon) throws GLRenderException {
		final GLVertex[] vertices = polygon.vertices;

		final int numberOfVertices = polygon.numberOfVertices;

		if (numberOfVertices < 3)
			throw new GLRenderException("GLFeedbackRenderer.renderPolygon(GLPolygon) - The number of GLVertex's is < 3");

		v0 = vertices[0];
		v1 = vertices[1];
		v2 = vertices[2];

		// compute signed area
		// of the triangle

		final float dxAC = v0.window.x - v2.window.x;
		final float dxBC = v1.window.x - v2.window.x;
		final float dyAC = v0.window.y - v2.window.y;
		final float dyBC = v1.window.y - v2.window.y;
		final float area = dxAC * dyBC - dxBC * dyAC;

		final boolean ccw = area < 0.0f ? true : false;
		final int facing;

		if (gc.state.polygon.frontFaceDirection == GL_CCW) {
			if (ccw)
				facing = GL_FRONT;
			else
				facing = GL_BACK;
		} else // gc.state.polygon.frontFaceDirection == GL_CW
		{
			if (ccw)
				facing = GL_BACK;
			else
				facing = GL_FRONT;
		}

		if (gc.state.enables.cullFace) {
			final int cull = gc.state.polygon.cull;

			if (cull == GL_FRONT_AND_BACK)
				return;
			else if (facing == cull)
				return;
		}

		// Choose colors for the vertices.

		if (gc.state.lighting.shadingModel == GL_SMOOTH) {
			for (int i = 0; i < numberOfVertices; ++i) {
				if (facing == GL_FRONT)
					gc.pipeline.lighting.apply(vertices[i], __GL_FRONTFACE);
				else if (facing == GL_BACK)
					gc.pipeline.lighting.apply(vertices[i], __GL_BACKFACE);
			}
		} else {
			v0 = gc.pipeline.provoking;

			if (facing == GL_FRONT)
				gc.pipeline.lighting.apply(v0, __GL_FRONTFACE);
			else if (facing == GL_BACK)
				gc.pipeline.lighting.apply(v0, __GL_BACKFACE);

			// Validate the lighting (and color) information in the provoking
			// vertices only.  Fill routines always use gc->vertices.provoking->color
			// to find the color.

			//final int index = facing - GL_FRONT;

			final GLColor color = v0.color;

			for (int i = 0; i < numberOfVertices; ++i)
				vertices[i].color.set(color);
		}

		if (gc.state.texture.enabledUnits != 0)
			gc.pipeline.textureOperations.processCoordinates(vertices, numberOfVertices);

		if (gc.state.enables.fog) {
			// Need fog value at vertex if not nicest

			if (gc.state.hints.fog != GL_NICEST) {
				for (int i = 0; i < numberOfVertices; ++i)
					vertices[i].fogVertexWithEye(gc);
			}
		}

		// Render triangle using the triangles polygon mode

		switch (facing == GL_FRONT ? gc.state.polygon.frontMode : gc.state.polygon.backMode) {
		case GL_POINT:

			for (int i = 0; i < numberOfVertices; ++i)
				render(vertices[i]);

			break;

		case GL_LINE:

			for (int i = 1; i < numberOfVertices; ++i)
				render(vertices[i - 1], vertices[i]);

			render(vertices[numberOfVertices - 1], vertices[0]);

			break;

		case GL_FILL:

			feedbackValue(GL_POLYGON_TOKEN);
			feedbackValue(numberOfVertices);

			for (int i = 0; i < numberOfVertices; ++i)
				feedback(vertices[i]);

			break;
		}
	}

	public final void render(final GLBitmap bitmap) {
		// Check if current colorBuffer position is valid.  Do not render if invalid.
		// Also, if selection is in progress skip the rendering of the
		// bitmap. Bitmaps are invisible to selection and do not generate
		// selection hits.

		if (!gc.state.current.validRasterPosition)
			return;

		final GLVector4f v = gc.state.current.raster.window;

		v0.window.set(v);
		v0.color.set(gc.state.current.raster.color);

		feedbackValue(GL_BITMAP_TOKEN);
		feedback(v0);

		// Advance current colorBuffer position

		v.x += bitmap.xMove;
		v.y -= bitmap.yMove;
	}

	private void feedbackValue(final float f) {
		if (!overFlowed) {
			if (resultIndex >= resultLength)
				overFlowed = GL_TRUE;
			else
				resultBase[resultIndex++] = f;
		}
	}

	private void feedback(final GLVertex v) {
		switch (type) {
		case GL_2D:
			feedbackValue(v.window.x - 0.5f);
			feedbackValue(v.window.y - 0.5f);
			break;
		case GL_3D:
		case GL_3D_COLOR:
		case GL_3D_COLOR_TEXTURE:
			feedbackValue(v.window.x - 0.5f);
			feedbackValue(v.window.y - 0.5f);
			feedbackValue(v.window.z);
			break;
		case GL_4D_COLOR_TEXTURE:
			feedbackValue(v.window.x - 0.5f);
			feedbackValue(v.window.y - 0.5f);
			feedbackValue(v.window.z);

			// NOTE: return clip.w, as window.w has no spec defined meaning.
			// It is true that this implementation uses window.w, but thats
			// something different.

			feedbackValue(v.clip.w);
			break;
		}

		switch (type) {
		case GL_3D_COLOR:
		case GL_3D_COLOR_TEXTURE:
		case GL_4D_COLOR_TEXTURE:

		{
			final GLColor c = v.color;

			feedbackValue(c.r);
			feedbackValue(c.g);
			feedbackValue(c.b);
			feedbackValue(c.a);
		}

			break;

		case GL_2D:
		case GL_3D:
			break;
		}

		switch (type) {
		case GL_3D_COLOR_TEXTURE:
		case GL_4D_COLOR_TEXTURE: {
			//	    GLuint modeFlags = gc.polygon.shader.modeFlags;
			//	    __GLfloat tx = v->texture[0].x;
			//	    __GLfloat ty = v->texture[0].y;
			//	    __GLfloat tw = v->texture[0].w;
			//
			//	    /*
			//	    ** Make sure texture coordinates are valid too.  They may not
			//	    ** be in the needs flag if texturing is disabled.  By definition
			//	    ** feedback will return the texture coordinates whether or
			//	    ** not texturing is enabled.
			//	    */
			//	    if ((v->flagBits & __GL_HAS_TEXTURE) == 0) {
			//		DO_VALIDATE(gc, v, gc->vertex.needs | __GL_HAS_TEXTURE);
			//	    }
			//
			//	    /* Undo projection */
			//	    if (modeFlags & __GL_SHADE_PROJSCALED_TEXTURE) {
			//		tx *= v->clip.w;
			//		ty *= v->clip.w;
			//		tw *= v->clip.w;
			//	    }
			//
			//	    /* Undo scaling */
			//	    if (modeFlags & __GL_SHADE_UVSCALED_TEXTURE) {
			//		__GLtexture *tex = gc->texture.active->currentTexture;
			//		__GLmipMapLevel *lp = tex->level[0];
			//
			//		tx /= lp->width2f;
			//		ty /= lp->height2f;
			//	    }
			//
			//	    __glFeedbackTag(gc, tx);
			//	    __glFeedbackTag(gc, ty);
			//	    __glFeedbackTag(gc, v->texture[0].z);
			//	    __glFeedbackTag(gc, tw);
		}
			break;
		case GL_2D:
		case GL_3D:
		case GL_3D_COLOR:
			break;
		}
	}
}
