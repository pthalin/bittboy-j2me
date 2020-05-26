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

package org.thenesis.pjogles;

import jgl.GL;

import org.thenesis.pjogles.arrays.GLInterleavedArray;
import org.thenesis.pjogles.buffers.GLAccumBuffer;
import org.thenesis.pjogles.evaluators.GLEvalMap2;
import org.thenesis.pjogles.lighting.GLLightSourceState;
import org.thenesis.pjogles.math.GLMatrix4f;
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLBitmap;
import org.thenesis.pjogles.render.GLRenderException;
import org.thenesis.pjogles.states.GLClientPixelState;
import org.thenesis.pjogles.states.GLEnableState;
import org.thenesis.pjogles.states.GLScissorState;
import org.thenesis.pjogles.texture.GLTextureBuffer;
import org.thenesis.pjogles.texture.GLTextureEnvState;
import org.thenesis.pjogles.texture.GLTextureGenState;

public class GL11Software implements GL11 {

	public static boolean DEBUG = true;

	private GLSoftwareContext gc;

	private final GLInterleavedArray interleavedArray = new GLInterleavedArray();

	//private MemoryImageSource source;
	//private Surface surface;

	private final GLMatrix4f m = new GLMatrix4f();
	private final GLVector4f vf = new GLVector4f();

	private int lastError;

	public GL11Software(GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final int glRenderMode(final int mode) {
		__GL_SETUP_NOT_IN_BEGIN("glRenderMode(int)");

		switch (mode) {
		case GL_RENDER:
		case GL_FEEDBACK:
		case GL_SELECT:
			break;
		default:
			throw new GLInvalidEnumException("glRenderMode(int)");
		}

		return gc.setRenderMode(mode);
	}

	public final void glHint(final int target, final int mode) {
		__GL_SETUP_NOT_IN_BEGIN("glHint(int, int)");

		switch (mode) {
		case GL_DONT_CARE:
		case GL_FASTEST:
		case GL_NICEST:
			break;
		default:
			throw new GLInvalidEnumException("glHint(int, int)");
		}

		switch (target) {
		case GL_PERSPECTIVE_CORRECTION_HINT:
			gc.state.hints.perspectiveCorrection = mode;
			break;
		case GL_POINT_SMOOTH_HINT:
			gc.state.hints.pointSmooth = mode;
			return;
		case GL_LINE_SMOOTH_HINT:
			gc.state.hints.lineSmooth = mode;
			return;
		case GL_POLYGON_SMOOTH_HINT:
			gc.state.hints.polygonSmooth = mode;
			return;
		case GL_FOG_HINT:
			gc.state.hints.fog = mode;
			break;
		default:
			throw new GLInvalidEnumException("glHint(int, int)");
		}
	}

	public final void glDrawBuffer(final int mode) {
		// TODO - needs to be implemented
	}

	public final void glReadBuffer(final int mode) {
		__GL_SETUP_NOT_IN_BEGIN("glReadBuffer(int)");

		switch (mode) {
		case GL_FRONT:
		case GL_LEFT:
		case GL_FRONT_LEFT:

			gc.state.pixel.readBufferSrc = GL_FRONT;
			break;

		case GL_BACK:
		case GL_BACK_LEFT:

			if (!gc.doubleBuffer)
				throw new GLInvalidOperationException("glReadBuffer(int)");

			gc.state.pixel.readBufferSrc = GL_BACK;
			break;

		case GL_AUX0:
		case GL_AUX1:
		case GL_AUX2:
		case GL_AUX3:

			if (mode - GL_AUX0 >= gc.auxBuffers)
				throw new GLInvalidOperationException("glReadBuffer(int)");

			gc.state.pixel.readBufferSrc = mode;
			break;

		case GL_FRONT_RIGHT:
		case GL_BACK_RIGHT:
		case GL_RIGHT:

			throw new GLInvalidOperationException("glReadBuffer(int)");

		default:
			throw new GLInvalidEnumException("glReadBuffer(int)");
		}

		gc.state.pixel.readBuffer = mode;
	}

	public final void glScissor(final int x, final int y, final int width, final int height) {
		__GL_SETUP_NOT_IN_BEGIN("glScissor(int, int, int, int)");

		if (width < 0 || height < 0)
			throw new GLInvalidValueException("glScissor(int, int, int, int) - width < 0 || height < 0.");

		final GLScissorState scissor = gc.state.scissor;

		scissor.x = x;
		scissor.y = y;
		scissor.width = width;
		scissor.height = height;
	}

	/////////////////////// Masks ///////////////////////

	public final void glColorMask(final boolean red, final boolean green, final boolean blue, final boolean alpha) {
		__GL_SETUP_NOT_IN_BEGIN("glColorMask(boolean, boolean, boolean, boolean)");

		gc.state.colorBuffer.rMask = red;
		gc.state.colorBuffer.gMask = green;
		gc.state.colorBuffer.bMask = blue;
		gc.state.colorBuffer.aMask = alpha;
	}

	/////////////////////// Clear ///////////////////////

	public final void glClearColor(final float red, final float green, final float blue, final float alpha) {
		__GL_SETUP_NOT_IN_BEGIN("glClearColor(float, float, float, float)");

		// The glClearColor function specifies the red, green, blue, and alpha values used by
		// glClear to clear the color buffers. Values specified by glClearColor are
		// clamped to the range [0,1].

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glClearColor(red < 0.0f ? 0.0f : (red > 1.0f ? 1.0f : red), green < 0.0f ? 0.0f
					: (green > 1.0f ? 1.0f : green), blue < 0.0f ? 0.0f : (blue > 1.0f ? 1.0f : blue),
					alpha < 0.0f ? 0.0f : (alpha > 1.0f ? 1.0f : alpha));
		else
			gc.state.colorBuffer.clear.set(red < 0.0f ? 0.0f : (red > 1.0f ? 1.0f : red), green < 0.0f ? 0.0f
					: (green > 1.0f ? 1.0f : green), blue < 0.0f ? 0.0f : (blue > 1.0f ? 1.0f : blue),
					alpha < 0.0f ? 0.0f : (alpha > 1.0f ? 1.0f : alpha));
	}

	public final void glClearIndex(final float c) {
		__GL_SETUP_NOT_IN_BEGIN("glClearIndex(float)");

		// TODO - we're not masking c with redMax !!

		gc.state.colorBuffer.clearIndex = c;
	}

	public final void glClear(final int mask) {
		__GL_SETUP_NOT_IN_BEGIN("glClear(int)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glClear(mask);
		else {
			if ((mask & ~(GL_COLOR_BUFFER_BIT | GL_ACCUM_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)) != 0)
				throw new GLInvalidValueException("glClear(int)");

			final long start = System.currentTimeMillis();

			if (gc.renderMode == GL_RENDER) {
				if ((mask & GL_COLOR_BUFFER_BIT) != 0) {
					switch (gc.state.colorBuffer.drawBuffer) {
					case GL_NONE:

						break;

					case GL_FRONT:

						gc.frameBuffer.frontBuffer.clear();
						break;

					case GL_BACK:

						if (gc.doubleBuffer)
							gc.frameBuffer.backBuffer.clear();

						break;

					case GL_FRONT_AND_BACK:

						gc.frameBuffer.frontBuffer.clear();

						if (gc.doubleBuffer)
							gc.frameBuffer.backBuffer.clear();

						break;

					default:
						throw new GLInvalidEnumException("glClear(int)");
					}
				}
			}

			if ((mask & GL_DEPTH_BUFFER_BIT) != 0 && gc.haveDepthBuffer)
				gc.frameBuffer.depthBuffer.clear();

			if ((mask & GL_ACCUM_BUFFER_BIT) != 0 && gc.haveAccumBuffer)
				gc.frameBuffer.accumBuffer.clear();

			if ((mask & GL_STENCIL_BUFFER_BIT) != 0 && gc.haveStencilBuffer)
				gc.frameBuffer.stencilBuffer.clear();

			gc.benchmark.clearTime += System.currentTimeMillis() - start;
		}
	}

	/////////////////////// Transforms ///////////////////////

	public final void glTranslatef(final float x, final float y, final float z) {
		__GL_SETUP_NOT_IN_BEGIN("glTranslatef(float, float, float)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTranslatef(x, y, z);
		else {
			m.setTranslate(x, y, z);
			glMultMatrixf(m.m);
		}
	}

	public final void glScalef(final float x, final float y, final float z) {
		__GL_SETUP_NOT_IN_BEGIN("glScalef(float, float, float)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glScalef(x, y, z);
		else {
			m.setScale(x, y, z);
			glMultMatrixf(m.m);
		}
	}

	public final void glRotatef(final float angle, final float x, final float y, final float z) {
		__GL_SETUP_NOT_IN_BEGIN("glRotatef(float, float, float, float)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glRotatef(angle, x, y, z);
		else {
			m.setRotate(angle, x, y, z);
			glMultMatrixf(m.m);
		}
	}

	/////////////////////// Matrix ///////////////////////

	public final void glMatrixMode(final int mode) {
		__GL_SETUP_NOT_IN_BEGIN("glMatrixMode(int)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glMatrixMode(mode);
		else {
			switch (mode) {
			case GL_MODELVIEW:
			case GL_PROJECTION:
			case GL_TEXTURE:

				gc.state.transform.matrixMode = mode;
				break;

			case GL_COLOR:

				throw new RuntimeException("glMatrixMode(int) - we do not support GL_COLOR matrix modes !");

			default:

				throw new GLInvalidEnumException("glMatrixMode(int)");
			}
		}
	}

	public final void glLoadIdentity() {
		__GL_SETUP_NOT_IN_BEGIN("glLoadIdentity()");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glLoadIdentity();
		else {
			switch (gc.state.transform.matrixMode) {
			case GL_MODELVIEW:

				gc.transform.modelView.matrix.setIdentity();
				gc.transform.modelView.inverseTranspose.setIdentity();
				gc.transform.modelView.updateInverseTranspose = false;

				break;

			case GL_PROJECTION:

				gc.transform.projection.matrix.setIdentity();

				break;

			case GL_TEXTURE:
			case GL_COLOR:

				throw new RuntimeException(
						"glMatrixMode(int) - we do not support GL_TEXTURE and GL_COLOR matrix modes !");

			default:

				throw new GLInvalidEnumException("glMatrixMode(int)");
			}
		}
	}

	public final void glLoadMatrixf(final float[] m) {
		
		// TODO - need to implement
		glLoadIdentity();
		glMultMatrixf(m);
		
	}

	public final void glMultMatrixf(final float[] m) {
		__GL_SETUP_NOT_IN_BEGIN("glMatrixMultf(float[])");

		final GLTransform tr;

		switch (gc.state.transform.matrixMode) {
		case GL_MODELVIEW:

			tr = gc.transform.modelView;
			break;

		case GL_PROJECTION:

			tr = gc.transform.projection;
			break;

		case GL_TEXTURE:

			tr = gc.transform.texture;
			break;

		case GL_COLOR:

			throw new RuntimeException("glMatrixMult(float[]) - we do not support GL_COLOR matrix multiplication !");

		default:

			throw new GLInvalidEnumException("glMatrixMult(float[])");
		}

		GLMatrix4f.multiply(m, tr.matrix.m, tr.matrix.m);
		tr.updateInverseTranspose = GL_TRUE;
	}

	public final void glPushMatrix() {
		__GL_SETUP_NOT_IN_BEGIN("glPushMatrix()");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPushMatrix();
		else {
			switch (gc.state.transform.matrixMode) {
			case GL_MODELVIEW:
				gc.transform.modelViewStack.push(new GLTransform(gc.transform.modelView));
				break;
			case GL_PROJECTION:
				gc.transform.projectionStack.push(new GLTransform(gc.transform.projection));
				break;
			case GL_TEXTURE:
				gc.transform.textureStack.push(new GLTransform(gc.transform.texture));
				break;
			case GL_COLOR:
				throw new RuntimeException("glPushMatrix() - we do not support GL_COLOR matrix pushing !");
			default:
				throw new GLInvalidEnumException("glPushMatrix()");
			}
		}
	}

	public final void glPopMatrix() {
		__GL_SETUP_NOT_IN_BEGIN("glPopMatrix()");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPopMatrix();
		else {
			switch (gc.state.transform.matrixMode) {
			case GL_MODELVIEW:
				gc.transform.modelView = (GLTransform) gc.transform.modelViewStack.pop();
				break;
			case GL_PROJECTION:
				gc.transform.projection = (GLTransform) gc.transform.projectionStack.pop();
				break;
			case GL_TEXTURE:
				gc.transform.texture = (GLTransform) gc.transform.textureStack.pop();
				break;
			case GL_COLOR:
				throw new RuntimeException("glPopMatrix() - we do not support GL_COLOR matrix poping !");
			default:
				throw new GLInvalidEnumException("glPushMatrix()");
			}
		}
	}

	/////////////////////// Geometry ///////////////////////

	public final void glBegin(final int mode) {
		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glBegin(int)");

		switch (mode) {
		case GL_POINTS: // Treats each vertices as a single point. Vertex n defines point n.
			// N points are drawn.
		case GL_LINES: // Treats each pair of vertices as an independent line segment.
			// Vertices 2n – 1 and 2n define line n. N/2 lines are drawn.
		case GL_LINE_STRIP: // Draws a connected group of line segments from the first vertices
			// to the lastTime. Vertices n and n+1 define line n.
			// N – 1 lines are drawn.
		case GL_LINE_LOOP: // Draws a connected group of line segments from the first vertices to
			// the lastTime, then back to the first. Vertices n and n+1 define line n.
			// The lastTime line, however, is defined by vertices N and 1. N lines are drawn.
		case GL_TRIANGLES: // Treats each triplet of vertices as an independent triangle.
			// Vertices 3n – 2, 3n –1, and 3n define triangle n. N/3 triangles are drawn.
		case GL_TRIANGLE_STRIP: // Draws a connected group of triangles. One triangle is defined for
			// each vertices presented after the first two vertices. For odd n,
			// vertices n, n + 1, and n + 2 define triangle n. For even n,
			// vertices n + 1, n, and n + 2 define triangle n. N – 2 triangles
			// are drawn.
		case GL_TRIANGLE_FAN: // Draws a connected group of triangles. One triangle is defined for
			// each vertices presented after the first two vertices.
			// Vertices 1, n + 1, and n + 2 define triangle n. N – 2 triangles
			// are drawn.
		case GL_QUADS: // Treats each group of four vertices as an independent quadrilateral.
			// Vertices 4n – 3, 4n – 2, 4n – 1, and 4n define quadrilateral n.
			// N/4 quadrilaterals are drawn.
		case GL_QUAD_STRIP: // Draws a connected group of quadrilaterals. One quadrilateral is defined
			// for each pair of vertices presented after the first pair.
			// Vertices 2n – 1, 2n, 2n + 2, and 2n + 1 define quadrilateral n.
			// N quadrilaterals are drawn. Note that the order in which vertices are
			// used to construct a quadrilateral from strip demos.data is different from
			// that used with independent demos.data.
		case GL_POLYGON: // Draws a single, convex polygon. Vertices 1 through N define this polygon.

			gc.beginMode = mode;

			if (gc.pipeline.displayList.currentList != 0)
				gc.pipeline.displayList.glBegin(mode);
			else
				gc.pipeline.begin();

			break;

		default:

			gc.beginMode = NOT_IN_BEGIN;
			throw new GLInvalidEnumException("glBegin(int)");
		}
	}

	public final void glEnd() {
		__GL_SETUP_IN_BEGIN("glEnd()");

		switch (gc.beginMode) {
		case GL_POINTS:
		case GL_LINES:
		case GL_LINE_STRIP:
		case GL_LINE_LOOP:
		case GL_TRIANGLES:
		case GL_TRIANGLE_STRIP:
		case GL_TRIANGLE_FAN:
		case GL_QUADS:
		case GL_QUAD_STRIP:
		case GL_POLYGON:

			if (gc.pipeline.displayList.currentList != 0)
				gc.pipeline.displayList.glEnd();
			else {
				try {
					gc.pipeline.end();
				} catch (GLRenderException e) {
					throw new GLException("glEnd() - " + e.toString());
				}
			}

			break;

		default:

			gc.beginMode = NOT_IN_BEGIN;
			throw new GLInvalidEnumException("glEnd()");
		}

		gc.beginMode = NOT_IN_BEGIN;
	}

	public final void glFlush() {
		__GL_SETUP_NOT_IN_BEGIN("glFlush()");
	}

	public final void glFinish() {
		__GL_SETUP_NOT_IN_BEGIN("glFinish()");

		glFlush();
	}

	public final void glVertex2i(final int x, final int y) {
		// When only x and y are specified, z defaults to 0.0 and w defaults to 1.0.
		// When x, y, and z are specified, w defaults to 1.0.

		glVertex4f(x, y, 0.0f, 1.0f);
	}

	public final void glVertex2f(final float x, final float y) {
		// When only x and y are specified, z defaults to 0.0 and w defaults to 1.0.
		// When x, y, and z are specified, w defaults to 1.0.

		glVertex4f(x, y, 0.0f, 1.0f);
	}

	public final void glVertex2fv(final float[] v) {
		// When only x and y are specified, z defaults to 0.0 and w defaults to 1.0.
		// When x, y, and z are specified, w defaults to 1.0.

		glVertex4f(v[0], v[1], 0.0f, 1.0f);
	}

	public final void glVertex3i(final int x, final int y, final int z) {
		// When only x and y are specified, z defaults to 0.0 and w defaults to 1.0.
		// When x, y, and z are specified, w defaults to 1.0.

		glVertex4f(x, y, z, 1.0f);
	}

	public final void glVertex3fv(final float[] v) {
		// When only x and y are specified, z defaults to 0.0 and w defaults to 1.0.
		// When x, y, and z are specified, w defaults to 1.0.

		glVertex4f(v[0], v[1], v[2], 1.0f);
	}

	public final void glVertex3f(final float x, final float y, final float z) {
		// When only x and y are specified, z defaults to 0.0 and w defaults to 1.0.
		// When x, y, and z are specified, w defaults to 1.0.

		glVertex4f(x, y, z, 1.0f);
	}

	public final void glVertex4fv(final float[] v) {
		glVertex4f(v[0], v[1], v[2], v[3]);
	}

	/**
	 * The glVertex function commands are used within glBegin/glEnd pairs to specify point,
	 * line, and polygon vertices. The current color, normal, and textureManager coordinates are
	 * associated with the vertices when glVertex is called.
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 */
	public final void glVertex4f(final float x, final float y, final float z, final float w) {
		// Invoking glVertex outside of a glBegin/glEnd pair results in undefined behavior.

		__GL_SETUP_IN_BEGIN("glVertex4f(float, float, float, float)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glVertex4f(x, y, z, w);
		else {
			switch (gc.beginMode) {
			case GL_POINTS:
			case GL_LINES:
			case GL_LINE_STRIP:
			case GL_LINE_LOOP:
			case GL_TRIANGLES:
			case GL_TRIANGLE_STRIP:
			case GL_TRIANGLE_FAN:
			case GL_QUADS:
			case GL_QUAD_STRIP:
			case GL_POLYGON:

				try {
					gc.pipeline.vertex(x, y, z, w);
				} catch (GLRenderException e) {
					throw new GLException("glVertex4f(float, float, float, float) - " + e.toString());
				}

				break;

			default:

				throw new GLInvalidEnumException("glVertex4f(float, float, float, float)");
			}
		}
	}

	public final void glColor3ub(final byte red, final byte green, final byte blue) {
		// treat the params as unsigned, by integer promotion

		glColor4f((float) (red & 0xff) / 255.0f, (float) (green & 0xff) / 255.0f, (float) (blue & 0xff) / 255.0f, 1.0f);
	}

	public final void glColor3ubv(final byte[] v) {
		// treat the params as unsigned, by integer promotion

		glColor4f((float) (v[0] & 0xff) / 255.0f, (float) (v[1] & 0xff) / 255.0f, (float) (v[2] & 0xff) / 255.0f, 1.0f);
	}

	public final void glColor3f(final float red, final float green, final float blue) {
		// The glColor3*() variants specify new red, green, and blue values explicitly,
		// and set the current alpha value to 1.0 implicitly.

		glColor4f(red, green, blue, 1.0f);
	}

	public final void glColor3fv(final float[] v) {
		// The glColor3*() variants specify new red, green, and blue values explicitly,
		// and set the current alpha value to 1.0 implicitly.

		glColor4f(v[0], v[1], v[2], 1.0f);
	}

	public final void glColor4fv(final float[] v) {
		glColor4f(v[0], v[1], v[2], v[3]);
	}

	public final void glColor4f(final float r, final float g, final float b, final float a) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glColor4f(r, g, b, a);
		else
			gc.pipeline.lighting.setColor(r, g, b, a);
	}

	public final void glIndexi(final int c) {
		glIndexf((float) c);
	}

	public final void glIndexf(final float c) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glIndexf(c);
		else
			gc.state.current.colorIndex = c;
	}

	public final void glNormal3fv(final float[] v) {
		try {
			glNormal3f(v[0], v[1], v[2]);
		} catch (GLException e) {
			throw new GLException("glNormal3fv(float[]) - " + e.toString());
		}
	}

	public final void glNormal3f(final float x, final float y, final float z) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glNormal3f(x, y, z);
		else
			gc.state.current.normal.set(x, y, z, 0.0f);
	}

	public final void glFrontFace(final int direction) {
		__GL_SETUP_NOT_IN_BEGIN("glFrontFace(int)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glFrontFace(direction);
		else {
			switch (direction) {
			case GL_CW:
			case GL_CCW:
				gc.state.polygon.frontFaceDirection = direction;
				break;
			default:
				throw new GLInvalidEnumException("glFrontFace(int)");
			}
		}
	}

	public final void glCullFace(final int cull) {
		__GL_SETUP_NOT_IN_BEGIN("glCullFace(int)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glCullFace(cull);
		else {
			switch (cull) {
			case GL_FRONT:
			case GL_BACK:
			case GL_FRONT_AND_BACK:
				gc.state.polygon.cull = cull;
				break;
			default:
				throw new GLInvalidEnumException("glCullFace(int)");
			}
		}
	}

	public final void glLineStipple(final int factor, final short stipple) {
		__GL_SETUP_NOT_IN_BEGIN("glLineStipple(int, short)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glLineStipple(factor, stipple);
		else {
			gc.state.line.stippleRepeat = GLUtils.clamp(factor, 1, 256);
			gc.state.line.stipple = stipple;
		}
	}

	public final void glPolygonStipple(final byte[] stipple) {
		__GL_SETUP_NOT_IN_BEGIN("glPolygonStipple(byte[])");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPolygonStipple(stipple);
		else
			gc.state.polygonStipple.setStipple(stipple);
	}

	public final void glRectf(final float x0, final float y0, final float x1, final float y1) {
		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glRectf(float, float, float, float)");

		// The glRect function supports efficient specification of rectangles as two corner points.
		// ach rectangle command takes four arguments, organized either as two consecutive pairs of
		// (x, y) coordinates, or as two pointers to arrays, each containing an (x,y) pair.
		// The resulting rectangle is defined in the z = 0 plane.

		// The glRect(x1, y1, x2, y2) function is exactly equivalent to the following sequence:

		// glBegin(GL_POLYGON);
		// glVertex2(x1, y1);
		// glVertex2(x2, y1);
		// glVertex2(x2, y2);
		// glVertex2(x1, y2);
		// glEnd( );

		// Notice that if the second vertices is above and to the right of the first vertices, the rectangle
		// is constructed with a counterclockwise winding.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glRectf(x0, y0, x1, y1);
		else {
			try {
				glBegin(GL_QUADS);

				glVertex2f(x0, y0);
				glVertex2f(x1, y0);
				glVertex2f(x1, y1);
				glVertex2f(x0, y1);

				glEnd();
			} catch (GLException e) {
				throw new GLException("glRectf(float, float, float, float) - " + e.toString());
			}
		}
	}

	/////////////////////// Lighting ///////////////////////

	public final void glShadeModel(final int shadeModel) {
		__GL_SETUP_NOT_IN_BEGIN("glShadeModel(int)");

		// The default is GL_SMOOTH.

		// OpenGL primitives can have either flat or smooth shading. Smooth shading, the default,
		// causes the computed colors of vertices to be interpolated as the primitive is rasterized,
		// typically assigning different colors to each resulting pixel fragment.
		// Flat shading selects the computed color of just one vertices and assigns it to all the pixel
		// fragments generated by rasterizing a single primitive. In either case, the computed color
		// of a vertices is the result of lighting, if lighting is enabled, or it is the current color
		// at the time the vertices was specified, if lighting is disabled.

		// Flat and smooth shading are indistinguishable for points. Counting vertices and primitives
		// from one, starting when glBegin is issued, each flat-shaded line segment i is given the
		// computed color of vertices i + 1, its second vertices. Counting similarly from one,
		// each flat-shaded polygon is given the computed color of the vertices listed in the following table. This is the lastTime vertices to specify the polygon in all cases except single polygons, where the first vertices specifies the flat-shaded color.

		// Primitive type of polygon i Vertex
		// Single polygon (I=1)     1
		// Triangle strip           i + 2
		// Triangle fan             i + 2
		// Independent triangle     3i
		// Quad strip               2i + 2
		// Independent quad         4i

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glShadeModel(shadeModel);
		else {
			switch (shadeModel) {
			case GL_FLAT:
			case GL_SMOOTH:
				gc.state.lighting.shadingModel = shadeModel;
				break;
			default:
				throw new GLInvalidEnumException("glShadeModel(int)");
			}
		}
	}

	public final void glLightf(final int light, final int p, final float f) {
		__GL_SETUP_NOT_IN_BEGIN("glLightf(int, int, float)");

		// accept only enumerants that
		// correspond to single values

		switch (p) {
		case GL_SPOT_EXPONENT:
		case GL_SPOT_CUTOFF:
		case GL_CONSTANT_ATTENUATION:
		case GL_LINEAR_ATTENUATION:
		case GL_QUADRATIC_ATTENUATION:
			glLightfv(light, p, new float[] { f });
			break;
		default:
			throw new GLInvalidEnumException("glLightf(int, int, float)");
		}
	}

	public final void glLightfv(int light, final int p, final float[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glLightfv(int, int, float[])");

		// The identifier of a lighting. The number of lights available depends on the implementation,
		// but at least eight lights are supported. They are identified by symbolic names of the
		// form GL_LIGHTi where 0 ? i < GL_MAX_LIGHTS.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glLightfv(light, p, pv);
		else {
			light -= GL_LIGHT0; // It is always the case that GL_LIGHTi = GL_LIGHT0 + i.

			if (light >= MAX_NUMBER_OF_LIGHTS)
				throw new GLInvalidEnumException("glLightfv(int, int, float)");

			final GLLightSourceState lss = gc.state.lighting.lightSources[light];
			lss.setParameter(gc, p, pv);
		}
	}

	public final void glLightModeli(final int p, final int v) {
		__GL_SETUP_NOT_IN_BEGIN("glLightModeli(int, int)");

		switch (p) {
		case GL_LIGHT_MODEL_LOCAL_VIEWER:
		case GL_LIGHT_MODEL_TWO_SIDE:
			//case GL_LIGHT_MODEL_COLOR_CONTROL:
			glLightModeliv(p, new int[] { v });
			break;
		default:
			throw new GLInvalidEnumException("glLightModelf(int, float)");
		}
	}

	public final void glLightModeli(final int p, final boolean v) {
		__GL_SETUP_NOT_IN_BEGIN("glLightModeli(int, boolean)");

		glLightModeli(p, v == true ? 1 : 0);
	}

	public final void glLightModeliv(final int p, final int[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glLightModeliv(int, int[])");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glLightModeliv(p, pv);
		else
			gc.state.lighting.model.setParameter(p, pv);
	}

	public final void glLightModelf(final int p, final boolean v) {
		__GL_SETUP_NOT_IN_BEGIN("glLightModelf(int, boolean)");

		glLightModelf(p, v == true ? 1 : 0);
	}

	public final void glLightModelf(final int p, final float v) {
		__GL_SETUP_NOT_IN_BEGIN("glLightModelf(int, float)");

		// accept only enumerants that
		// correspond to single values

		switch (p) {
		case GL_LIGHT_MODEL_LOCAL_VIEWER:
		case GL_LIGHT_MODEL_TWO_SIDE:
			//case GL_LIGHT_MODEL_COLOR_CONTROL:
			glLightModelfv(p, new float[] { v });
			break;
		default:
			throw new GLInvalidEnumException("glLightModelf(int, float)");
		}
	}

	public final void glLightModelfv(final int p, final float[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glLightModelfv(int, float[])");

		// In RGBA mode, the lighted color of a vertices is the sum of the material emission intensity,
		// the product of the material ambient reflectance and the lighting model full-scene ambient
		// intensity, and the contribution of each enabled lighting source.
		// Each lighting source contributes the sum of three terms: ambient, diffuse, and specular.
		// The ambient lighting source contribution is the product of the material ambient reflectance
		// and the lighting's ambient intensity. The diffuse lighting source contribution is the product
		// of the material diffuse reflectance, the lighting's diffuse intensity, and the dot product
		// of the vertices's normal with the normalized vector from the vertices to the lighting source.
		// The specular lighting source contribution is the product of the material specular reflectance,
		// the lighting's specular intensity, and the dot product of the normalized vertices-to-eye and
		// vertices-to-lighting vectors, raised to the power of the shininess of the material.
		// All three lighting source contributions are attenuated equally based on the distance from
		// the vertices to the lighting source and on lighting source direction, spread exponent,
		// and spread cutoff angle. All dot products are replaced with zero if they evaluate to a
		// negative value.

		// The alpha component of the resulting lighted color is set to the alpha value of the material
		// diffuse reflectance.

		// In color-numberOfVertices mode, the value of the lighted numberOfVertices of a vertices ranges from the ambient to
		// the specular values passed to glMaterial using GL_COLOR_INDEXES.
		// Diffuse and specular coefficients, computed with a (.30, .59, .11) weighting of the lighting's
		// colors, the shininess of the material, and the same reflection and attenuation equations
		// as in the RGBA case, determine how much above ambient the resulting numberOfVertices is.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glLightModelfv(p, pv);
		else
			gc.state.lighting.model.setParameter(p, pv);
	}

	public final void glMaterialf(final int face, final int p, final float pv) {
		try {
			glMaterialfv(face, p, new float[] { pv });
		} catch (GLException e) {
			throw new GLException("glMaterialf(int, int , float) - " + e.toString());
		}
	}

	public final void glMaterialfv(final int face, final int p, final float[] pv) {
		// The glMaterial function assigns values to material parameters. There are two matched sets of
		// material parameters. One, the front-facing set, is used to shade points, lines, bitmaps,
		// and all polygons (when two-sided lighting is disabled), or just front-facing polygons
		// (when two-sided lighting is enabled). The other set, back-facing, is used to shade
		// back-facing polygons only when two-sided lighting is enabled. Refer to glLightModel
		// for details concerning one- and two-sided lighting calculations.

		// The glMaterial function takes three arguments. The first, face, specifies whether the
		// GL_FRONT materials, the GL_BACK materials, or both GL_FRONT_AND_BACK materials will be
		// modified. The second, pname, specifies which of several parameters in one or both sets
		// will be modified. The third, params, specifies what value or values will be assigned
		// to the specified parameter.

		// Material parameters are used in the lighting equation that is optionally applied to each vertices.
		// The equation is discussed in glLightModel.

		// The material parameters can be updated at any time. In particular,
		// glMaterial can be called between a call to glBegin and the corresponding
		// call to glEnd. If only a single material parameter is to be changed per vertices,
		// however, glColorMaterial is preferred over glMaterial.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glMaterialfv(face, p, pv);
		else {
			switch (face) {
			case GL_FRONT:
			case GL_BACK:
			case GL_FRONT_AND_BACK:
				break;
			default:
				throw new GLInvalidEnumException("glMaterialfv(int, int , float[])");
			}

			switch (p) {
			case GL_COLOR_INDEXES:
			case GL_EMISSION:
			case GL_SPECULAR:
			case GL_AMBIENT:
			case GL_DIFFUSE:
			case GL_AMBIENT_AND_DIFFUSE:

				break;

			case GL_SHININESS:

				if (pv[0] < 0 || pv[0] > 128)
					throw new GLInvalidValueException("glMaterialfv(int, int , float[])");

				break;

			default:

				throw new GLInvalidEnumException("glMaterialfv(int, int , float[])");
			}

			gc.state.lighting.setMaterial(face, p, pv);
		}
	}

	/////////////////////// Pixels ///////////////////////

	public final void glPixelStorei(final int mode, final boolean value) {
		__GL_SETUP_NOT_IN_BEGIN("glPixelStorei(int, boolean)");

		glPixelStorei(mode, value ? 1 : 0);
	}

	public final void glPixelStorei(final int mode, final int value) {
		__GL_SETUP_NOT_IN_BEGIN("glPixelStorei(int, int)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPixelStorei(mode, value);
		else {
			final GLClientPixelState ps = gc.clientState.clientPixel;

			switch (mode) {
			case GL_PACK_ROW_LENGTH:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.packing.rowLength = value;
				break;

			case GL_PACK_IMAGE_HEIGHT:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.packing.imageHeight = value;
				break;

			case GL_PACK_SKIP_ROWS:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.packing.skipRows = value;
				break;

			case GL_PACK_SKIP_PIXELS:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.packing.skipPixels = value;
				break;

			case GL_PACK_SKIP_IMAGES:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.packing.skipImages = value;
				break;

			case GL_PACK_ALIGNMENT:

				switch (value) {
				case 1:
				case 2:
				case 4:
				case 8:
					ps.packing.alignment = value;
					break;
				default:
					throw new GLInvalidValueException("glPixelStorei(int, int)");
				}

				break;

			case GL_PACK_SWAP_BYTES:

				ps.packing.swapEndian = (value != 0);
				break;

			case GL_PACK_LSB_FIRST:

				ps.packing.lsbFirst = (value != 0);
				break;

			case GL_UNPACK_ROW_LENGTH:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.unpacking.rowLength = value;
				break;

			case GL_UNPACK_IMAGE_HEIGHT:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.unpacking.imageHeight = value;
				break;

			case GL_UNPACK_SKIP_ROWS:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.unpacking.skipRows = value;
				break;

			case GL_UNPACK_SKIP_PIXELS:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.unpacking.skipPixels = value;
				break;

			case GL_UNPACK_SKIP_IMAGES:

				if (value < 0)
					throw new GLInvalidValueException("glPixelStorei(int, int)");

				ps.unpacking.skipImages = value;
				break;

			case GL_UNPACK_ALIGNMENT:

				switch (value) {
				case 1:
				case 2:
				case 4:
				case 8:
					ps.unpacking.alignment = value;
					break;
				default:
					throw new GLInvalidValueException("glPixelStorei(int, int)");
				}

				break;

			case GL_UNPACK_SWAP_BYTES:

				ps.unpacking.swapEndian = (value != 0);
				break;

			case GL_UNPACK_LSB_FIRST:

				ps.unpacking.lsbFirst = (value != 0);
				break;

			default:

				throw new GLInvalidEnumException("glPixelStorei(int, int)");
			}
		}
	}

	public final void glPixelStoref(final int mode, final float value) {
		throw new GLException("glPixelStoref(int, float) - Not Implemented !");
	}

	public final void glPixelTransferi(final int pname, final int param) {
		__GL_SETUP_NOT_IN_BEGIN("glPixelTransferi(int, int)");

		glPixelTransferf(pname, (float) param);
	}

	public final void glPixelTransferf(final int pname, final float param) {
		__GL_SETUP_NOT_IN_BEGIN("glPixelTransferf(int, float)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPixelTransferf(pname, param);
		else {
			switch (pname) {
			case GL_MAP_COLOR:
				gc.state.pixel.transferMode.mapColor = param != 0.0f ? GL_TRUE : GL_FALSE;
				break;
			case GL_MAP_STENCIL:
				gc.state.pixel.transferMode.mapStencil = param != 0.0f ? GL_TRUE : GL_FALSE;
				break;
			case GL_INDEX_SHIFT:
				gc.state.pixel.transferMode.indexShift = (int) param;
				break;
			case GL_INDEX_OFFSET:
				gc.state.pixel.transferMode.indexOffset = (int) param;
				break;
			case GL_RED_SCALE:
				gc.state.pixel.transferMode.redScale = param;
				break;
			case GL_RED_BIAS:
				gc.state.pixel.transferMode.redBias = param;
				break;
			case GL_GREEN_SCALE:
				gc.state.pixel.transferMode.greenScale = param;
				break;
			case GL_GREEN_BIAS:
				gc.state.pixel.transferMode.greenBias = param;
				break;
			case GL_BLUE_SCALE:
				gc.state.pixel.transferMode.blueScale = param;
				break;
			case GL_BLUE_BIAS:
				gc.state.pixel.transferMode.blueBias = param;
				break;
			case GL_ALPHA_SCALE:
				gc.state.pixel.transferMode.alphaScale = param;
				break;
			case GL_ALPHA_BIAS:
				gc.state.pixel.transferMode.alphaBias = param;
				break;
			case GL_DEPTH_SCALE:
				gc.state.pixel.transferMode.depthScale = param;
				break;
			case GL_DEPTH_BIAS:
				gc.state.pixel.transferMode.depthBias = param;
				break;
			case GL_POST_COLOR_MATRIX_RED_SCALE:
				gc.state.pixel.transferMode.postColorMatrixRedScale = param;
				break;
			case GL_POST_COLOR_MATRIX_RED_BIAS:
				gc.state.pixel.transferMode.postColorMatrixRedBias = param;
				break;
			case GL_POST_COLOR_MATRIX_GREEN_SCALE:
				gc.state.pixel.transferMode.postColorMatrixGreenScale = param;
				break;
			case GL_POST_COLOR_MATRIX_GREEN_BIAS:
				gc.state.pixel.transferMode.postColorMatrixGreenBias = param;
				break;
			case GL_POST_COLOR_MATRIX_BLUE_SCALE:
				gc.state.pixel.transferMode.postColorMatrixBlueScale = param;
				break;
			case GL_POST_COLOR_MATRIX_BLUE_BIAS:
				gc.state.pixel.transferMode.postColorMatrixBlueBias = param;
				break;
			case GL_POST_COLOR_MATRIX_ALPHA_SCALE:
				gc.state.pixel.transferMode.postColorMatrixAlphaScale = param;
				break;
			case GL_POST_COLOR_MATRIX_ALPHA_BIAS:
				gc.state.pixel.transferMode.postColorMatrixAlphaBias = param;
				break;
			case GL_POST_CONVOLUTION_RED_SCALE:
				gc.state.pixel.transferMode.postConvolutionRedScale = param;
				break;
			case GL_POST_CONVOLUTION_RED_BIAS:
				gc.state.pixel.transferMode.postConvolutionRedBias = param;
				break;
			case GL_POST_CONVOLUTION_GREEN_SCALE:
				gc.state.pixel.transferMode.postConvolutionGreenScale = param;
				break;
			case GL_POST_CONVOLUTION_GREEN_BIAS:
				gc.state.pixel.transferMode.postConvolutionGreenBias = param;
				break;
			case GL_POST_CONVOLUTION_BLUE_SCALE:
				gc.state.pixel.transferMode.postConvolutionBlueScale = param;
				break;
			case GL_POST_CONVOLUTION_BLUE_BIAS:
				gc.state.pixel.transferMode.postConvolutionBlueBias = param;
				break;
			case GL_POST_CONVOLUTION_ALPHA_SCALE:
				gc.state.pixel.transferMode.postConvolutionAlphaScale = param;
				break;
			case GL_POST_CONVOLUTION_ALPHA_BIAS:
				gc.state.pixel.transferMode.postConvolutionAlphaBias = param;
				break;
			default:
				throw new GLInvalidEnumException("glPixelTransferf(int, float)");
			}
		}
	}

	public final void glReadPixels(final int x, final int y, final int width, final int height, final int format,
			final int type, final Object pixels) {
		__GL_SETUP_NOT_IN_BEGIN("glReadPixels(int, int, int, int, int, int, Object)");

		glFlush(); // TODO - why is this here ?

		if (width < 0 || height < 0)
			throw new GLInvalidValueException("glReadPixels(int, int, int, int, int, int, Object)");

		if (pixels == null)
			throw new GLInvalidValueException("glReadPixels(int, int, int, int, int, int, Object)");

		gc.pipeline.readPixels(x, y, width, height, format, type, pixels);
	}

	/////////////////////// Textures ///////////////////////

	public final void glGenTextures(final int n, final int[] textures) {
		__GL_SETUP_NOT_IN_BEGIN("glGenTextures(int, int[])");

		// n The number of texture names to be generated.
		// textures A pointer to the first element of an array in which the generated texture names are stored.

		// The glGenTextures function returns n texture names in the textures parameter.
		// The texture names are not necessarily a contiguous set of integers, however, none of the
		// returned names can have been in use immediately prior to calling the glGenTextures
		// function. The generated textures assume the dimensionality of the texture target to
		// which they are first bound with the glBindTexture function. Texture names returned by
		// glGenTextures are not returned by subsequent calls to glGenTextures unless they are first
		// deleted by calling glDeleteTextures.

		if (n < 0)
			throw new GLInvalidValueException("glGenTextures(int, int[])");
		else if (n == 0)
			return;

		// You cannot include glGenTextures in display lists.

		// TODO - should we do something ?
		// if (gc.pipeline.displayList.currentList != 0)

		if (textures == null)
			return;

		gc.pipeline.textureManager.glGenTextures(n, textures);
	}

	public final void glBindTexture(final int target, final int texture) {
		// Need to validate in case a new texture was popped into
		// the state immediately prior to this call

		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glBindTexture(int, int)");

		switch (target) {
		case GL_TEXTURE_1D:
		case GL_TEXTURE_2D:
		case GL_TEXTURE_3D:
			break;
		default:
			throw new GLInvalidEnumException("glBindTexture(int, int)");
		}

		gc.pipeline.textureManager.glBindTexture(target, texture);
	}

	public final void glTexImage1D(final int target, final int level, final int components, final int length,
			final int border, final int format, final int type, final Object pixels) {
		__GL_SETUP_NOT_IN_BEGIN("glTexImage1D(int, int, int, int, int, int, int, int, Object)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexImage1D(target, level, components, length, border, format, type, pixels);
		else {
			if (target != GL_TEXTURE_1D)
				throw new GLInvalidValueException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			if (level < 0 || level >= MAX_MIPMAP_LEVEL)
				throw new GLInvalidValueException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			if (components < 1 || components > 4)
				throw new GLInvalidValueException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			if (length < 2 * border || length > 2 + MAX_TEXTURE_SIZE)
				throw new GLInvalidValueException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			if (border != 0 && border != 1)
				throw new GLInvalidValueException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			final int r = length - border * 2;

			if ((r < 0) || (r & (r - 1)) != 0)
				throw new GLInvalidValueException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			switch (format) {
			case GL_COLOR_INDEX:
			case GL_RED:
			case GL_GREEN:
			case GL_BLUE:
			case GL_ALPHA:
			case GL_RGB:
			case GL_RGBA:
			case GL_LUMINANCE:
			case GL_LUMINANCE_ALPHA:
				break;
			default:
				throw new GLInvalidEnumException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			}

			final int size = sizeof(type);

			if (size < 0)
				throw new GLInvalidEnumException("glTexImage1D(int, int, int, int, int, int, int, Object)");

			try {
				gc.pipeline.textureManager.setTexture(target, level, border, new GLTextureBuffer(length, components,
						format, size, gc.clientState.clientPixel.unpacking, pixels));
			} catch (GLException e) {
				throw new GLException("glTexImage1D(int, int, int, int, int, int, int, Object) - " + e.toString());
			}
		}
	}

	public final void glTexImage2D(final int target, final int level, final int components, final int width,
			final int height, final int border, final int format, final int type, final Object pixels) {
		__GL_SETUP_NOT_IN_BEGIN("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList
					.glTexImage2D(target, level, components, width, height, border, format, type, pixels);
		else {
			if (target != GL_TEXTURE_2D)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			if (level < 0 || level >= MAX_MIPMAP_LEVEL)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			if (components < 1 || components > 4)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			if (width < 2 * border || width > 2 + MAX_TEXTURE_SIZE)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			if (height < 2 * border || height > 2 + MAX_TEXTURE_SIZE)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			if (border != 0 && border != 1)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			int r = width - border * 2;

			if ((r < 0) || (r & (r - 1)) != 0)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			r = height - border * 2;

			if ((r < 0) || (r & (r - 1)) != 0)
				throw new GLInvalidValueException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			switch (format) {
			case GL_COLOR_INDEX:
			case GL_RED:
			case GL_GREEN:
			case GL_BLUE:
			case GL_ALPHA:
			case GL_RGB:
			case GL_RGBA:
			case GL_LUMINANCE:
			case GL_LUMINANCE_ALPHA:
				break;
			default:
				throw new GLInvalidEnumException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");
			}

			final int size = sizeof(type);

			if (size < 0)
				throw new GLInvalidEnumException("glTexImage2D(int, int, int, int, int, int, int, int, Object)");

			try {
				gc.pipeline.textureManager.setTexture(target, level, border, new GLTextureBuffer(width, height,
						components, format, size, gc.clientState.clientPixel.unpacking, pixels));
			} catch (GLException e) {
				throw new GLException("glTexImage2D(int, int, int, int, int, int, int, int, Object) - " + e.toString());
			}
		}
	}

	public final void glTexParameteri(final int target, final int pname, final int i) {
		glTexParameterf(target, pname, i);
	}

	public final void glTexParameterf(final int target, final int pname, final float f) {
		__GL_SETUP_NOT_IN_BEGIN("glTexParameterf(int, int, float)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexParameterf(target, pname, f);
		else {
			// Accept only enumerants that correspond to single values

			switch (pname) {
			case GL_TEXTURE_WRAP_S:
			case GL_TEXTURE_WRAP_T:
			case GL_TEXTURE_WRAP_R:
			case GL_TEXTURE_MIN_FILTER:
			case GL_TEXTURE_MAG_FILTER:
				//            case GL_TEXTURE_PRIORITY:
				//            case GL_TEXTURE_MIN_LOD:
				//            case GL_TEXTURE_MAX_LOD:
				//            case GL_TEXTURE_BASE_LEVEL:
				//            case GL_TEXTURE_MAX_LEVEL:

				try {
					gc.pipeline.textureManager.setParameter(target, pname, f);
				} catch (GLException e) {
					throw new GLException("glTexParameterf(int, int, float) - " + e.toString());
				}

				break;

			default:
				throw new GLInvalidEnumException("glTexParameterf(int, int, float)");
			}
		}
	}

	public final void glTexParameterfv(final int target, final int pname, final float[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glTexParameterfv(int, int, float[])");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexParameterfv(target, pname, pv);
		else {
			try {
				gc.pipeline.textureManager.setParameter(target, pname, pv);
			} catch (GLException e) {
				throw new GLException("glTexParameterfv(int, int, float[]) - " + e.toString());
			}
		}
	}

	public final void glTexCoord1f(final float t) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexCoord1f(t);
		else
			gc.state.current.texture[0].set(t, 0.0f, 0.0f, 1.0f);
	}

	public final void glTexCoord1fv(final float[] t) {
		// The glTexCoord function sets the current texture coordinates that are part of the demos.data
		// associated with polygon vertices.

		// The glTexCoord function specifies texture coordinates in one, two, three, or four
		// dimensions. The glTexCoord1 function sets the current texture coordinates to (s, 0, 0, 1);
		// a call to glTexCoord2 sets them to (s, t, 0, 1). Similarly, glTexCoord3 specifies the
		// texture coordinates as (s, t, r, 1), and glTexCoord4 defines all four components explicitly
		// as (s, t, r, q).

		// The current texture coordinates can be updated at any time.
		// In particular, glTexCoord can be called between a call to glBegin and the corresponding
		// call to glEnd.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexCoord1fv(t);
		else
			gc.state.current.texture[0].set(t[0], 0.0f, 0.0f, 1.0f);
	}

	public final void glTexCoord2f(final float s, final float t) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexCoord2f(s, t);
		else
			gc.state.current.texture[0].set(s, t, 0.0f, 1.0f);
	}

	public final void glTexCoord2fv(final float[] v) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexCoord2fv(v);
		else
			gc.state.current.texture[0].set(v[0], v[1], 0.0f, 1.0f);
	}

	public final void glTexCoord3fv(final float[] v) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexCoord3fv(v);
		else
			gc.state.current.texture[0].set(v[0], v[1], v[2], 1.0f);
	}

	public final void glTexCoord4fv(final float[] v) {
		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glTexCoord4fv(v);
		else
			gc.state.current.texture[0].set(v[0], v[1], v[2], v[3]);
	}

	public final void glTexEnvfv(int target, final int pname, final float[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glTexEnvfv(int, int, float[])");

		if (target < GL_TEXTURE_ENV)
			throw new GLInvalidEnumException("glTexEnvfv(int, int, float[])");

		target -= GL_TEXTURE_ENV;

		if (target >= NUMBER_OF_TEXTURE_ENV_BINDINGS_PER_UNIT)
			throw new GLInvalidEnumException("glTexEnvfv(int, int, float[])");

		final GLTextureEnvState tes = gc.state.texture.currentUnit.env[target];

		switch (pname) {
		case GL_TEXTURE_ENV_MODE:

			switch ((int) pv[0]) {
			case GL_MODULATE:
			case GL_DECAL:
			case GL_BLEND:
			case GL_REPLACE:
			case GL_ADD:
				tes.mode = (int) pv[0];
				break;
			default:
				throw new GLInvalidEnumException("glTexEnvfv(int, int, float[])");
			}

			break;

		case GL_TEXTURE_ENV_COLOR:

			tes.color.set(pv);
			break;

		default:

			throw new GLInvalidEnumException("glTexEnvfv(int, int, float[])");
		}
	}

	public final void glTexEnvi(final int target, final int pname, final int i) {
		__GL_SETUP_NOT_IN_BEGIN("glTexEnvi(int, int, int)");

		// Accept only enumerants that correspond to single values

		switch (pname) {
		case GL_TEXTURE_ENV_MODE:
			// TODO - should we try / catch
			glTexEnvfv(target, pname, new float[] { i });
			break;
		default:
			throw new GLInvalidEnumException("glTexEnvi(int, int, int)");
		}
	}

	public final void glTexEnvf(final int target, final int pname, final float f) {
		__GL_SETUP_NOT_IN_BEGIN("glTexEnvf(int, int, float)");

		// Accept only enumerants that correspond to single values

		switch (pname) {
		case GL_TEXTURE_ENV_MODE:
			// TODO - should we try / catch
			glTexEnvfv(target, pname, new float[] { f });
			break;
		default:
			throw new GLInvalidEnumException("glTexEnvf(int, int, float)");
		}
	}

	public final void glTexGeni(final int coord, final int pname, final int p) {
		__GL_SETUP_NOT_IN_BEGIN("glTexGeni(int, int, int)");

		try {
			glTexGeniv(coord, pname, new int[] { p });
		} catch (GLException e) {
			throw new GLException("glTexGeni(int, int, int) - " + e.toString());
		}
	}

	public final void glTexGeniv(final int coord, final int pname, final int[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glTexGeniv(int, int, int[])");

		final GLTextureGenState tcs;

		switch (coord) {
		case GL_S:
			tcs = gc.state.texture.currentUnit.s;
			break;
		case GL_T:
			tcs = gc.state.texture.currentUnit.t;
			break;
		case GL_R:
			tcs = gc.state.texture.currentUnit.r;
			break;
		case GL_Q:
			tcs = gc.state.texture.currentUnit.q;
			break;
		default:
			throw new GLInvalidEnumException("glTexGeniv(int, int, int[])");
		}

		switch (pname) {
		case GL_TEXTURE_GEN_MODE:

			switch (pv[0]) {
			case GL_EYE_LINEAR:
			case GL_OBJECT_LINEAR:
				tcs.mode = pv[0];
				break;
			case GL_SPHERE_MAP:
				if (coord == GL_R || coord == GL_Q)
					throw new GLInvalidEnumException("glTexGeniv(int, int, int[])");
				tcs.mode = pv[0];
				break;
			default:
				throw new GLInvalidEnumException("glTexGeniv(int, int, int[])");
			}

			break;

		case GL_OBJECT_PLANE:

			tcs.objectPlaneEquation.set(pv[0], pv[1], pv[2], pv[3]);
			break;

		case GL_EYE_PLANE:

			final GLTransform tr = gc.transform.modelView;

			if (tr.updateInverseTranspose)
				tr.computeInverseTranspose(gc);

			tcs.eyePlaneEquation.set(pv[0], pv[1], pv[2], pv[3]);
			GLMatrix4f.transform(tcs.eyePlaneEquation, tr.inverseTranspose, tcs.eyePlaneEquation);

			break;

		default:

			throw new GLInvalidEnumException("glTexGeniv(int, int, int[])");
		}
	}

	public final void glTexGenf(final int coord, final int pname, final float p) {
		__GL_SETUP_NOT_IN_BEGIN("glTexGenf(int, int, float)");

		try {
			glTexGenfv(coord, pname, new float[] { p });
		} catch (GLException e) {
			throw new GLException("glTexGenf(int, int, float) - " + e.toString());
		}
	}

	public final void glTexGenfv(final int coord, final int pname, final float[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glTexGenfv(int, int, float[])");

		final GLTextureGenState tcs;

		switch (coord) {
		case GL_S:
			tcs = gc.state.texture.currentUnit.s;
			break;
		case GL_T:
			tcs = gc.state.texture.currentUnit.t;
			break;
		case GL_R:
			tcs = gc.state.texture.currentUnit.r;
			break;
		case GL_Q:
			tcs = gc.state.texture.currentUnit.q;
			break;
		default:
			throw new GLInvalidEnumException("glTexGenfv(int, int, float[])");
		}

		switch (pname) {
		case GL_TEXTURE_GEN_MODE:

			switch ((int) pv[0]) {
			case GL_EYE_LINEAR:
			case GL_OBJECT_LINEAR:

				tcs.mode = (int) pv[0];
				break;

			case GL_SPHERE_MAP:

				if ((coord == GL_R) || (coord == GL_Q))
					throw new GLInvalidEnumException("glTexGenfv(int, int, float[])");

				tcs.mode = (int) pv[0];

				break;

			default:

				throw new GLInvalidEnumException("glTexGenfv(int, int, float[])");
			}

			break;

		case GL_OBJECT_PLANE:

			tcs.objectPlaneEquation.set(pv[0], pv[1], pv[2], pv[3]);
			break;

		case GL_EYE_PLANE:

			final GLTransform tr = gc.transform.modelView;

			if (tr.updateInverseTranspose)
				tr.computeInverseTranspose(gc);

			tcs.eyePlaneEquation.set(pv[0], pv[1], pv[2], pv[3]);
			GLMatrix4f.transform(tcs.eyePlaneEquation, tr.inverseTranspose, tcs.eyePlaneEquation);

			break;

		default:

			throw new GLInvalidEnumException("glTexGenfv(int, int, float[])");
		}
	}

	/////////////////////// Viewport / Frustrum ///////////////////////

	public final void glViewport(final int x, final int y, int width, int height) {
		__GL_SETUP_NOT_IN_BEGIN("glViewport(int, int, int, int)");

		if (width < 0 || height < 0)
			throw new GLInvalidValueException("glViewport(int, int, int, int)");

		// The glViewport function specifies the affine transformation of x and y from normalized device
		// coordinates to window coordinates. Let (xnd, ynd) be normalized device coordinates.
		// The window coordinates (xw, yw) are then computed as follows:

		// Xw = (Xnd + 1)(width / 2) + x
		// Yw = (Ynd + 1)(height / 2) + y

		// Viewport width and height are silently clamped to a range that depends on the implementation.

		if (width < 1)
			width = 1;

		if (height < 1)
			height = 1;

		if (width > MAX_WINDOW_WIDTH)
			width = MAX_WINDOW_WIDTH;

		if (height > MAX_WINDOW_HEIGHT)
			height = MAX_WINDOW_HEIGHT;

		gc.state.viewport.setViewport(x, y, width, height);
		gc.resizeBuffers(width, height);
		//gc.getCurrentSurface().pixels = gc.frameBuffer.readBuffer.buffer;

		//surface = new Surface(gc.frameBuffer.drawBuffer.buffer, width, height);
	}

	public final void glOrtho(final float left, final float right, final float bottom, final float top,
			final float zNear, final float zFar) {
		__GL_SETUP_NOT_IN_BEGIN("glOrtho(float, float, float, float, float, float)");

		// The glOrtho function describes a perspective matrix that produces a parallel projection.
		// The (left, bottom, – near) and (right, top, – near) parameters specify the points on the
		// near clipping plane that are mapped to the lower-left and upper-right corners of the window,
		// respectively, assuming that the eye is located at (0, 0, 0).
		// The –far parameter specifies the location of the far clipping plane.
		// Both near and far can be either positive or negative.

		// The current matrix is multiplied by this matrix with the result replacing the current matrix.
		// That is, if M is the current matrix and O is the ortho matrix, then M is replaced with M • O.

		final float deltax = right - left;
		final float deltay = top - bottom;
		final float deltaz = zFar - zNear;

		if (deltax == 0.0f || deltay == 0.0f || deltaz == 0.0f)
			throw new GLInvalidValueException("glOrtho(float, float, float, float, float, float)");

		m.setIdentity();

		m.m[0] = 2.0f / deltax;
		m.m[12] = -(right + left) / deltax;
		m.m[5] = 2.0f / deltay;
		m.m[13] = -(top + bottom) / deltay;
		m.m[10] = -2.0f / deltaz;
		m.m[14] = -(zFar + zNear) / deltaz;

		glMultMatrixf(m.m);
	}

	public final void glFrustum(final float left, final float right, final float bottom, final float top,
			final float zNear, final float zFar) {
		__GL_SETUP_NOT_IN_BEGIN("glFrustrum(float, float, float, float, float, float)");

		// The glFrustum function describes a perspective matrix that produces a perspective projection.
		// The (left, bottom, znear) and (right, top, znear) parameters specify the points on the near
		// clipping plane that are mapped to the lower-left and upper-right corners of the window,
		// respectively, assuming that the eye is located at (0, 0, 0). The zfar parameter specifies the
		// location of the far clipping plane. Both znear and zfar must be positive.

		// The glFrustum function multiplies the current matrix by this matrix, with the result replacing
		// the current matrix. That is, if M is the current matrix and F is the frustum perspective matrix,
		// then glFrustum replaces M with M • F.

		// Depth-buffer precision is affected by the values specified for znear and zfar.
		// The greater the ratio of zfar to znear is, the less effective the depth buffer will be at
		// distinguishing between surfaces that are near each other. If

		// r = far / near

		// roughly log (2) r bits of depth buffer precision are lost. Because r approaches infinity as
		// znear approaches zero, you should never set znear to zero.

		final float deltaX = right - left;
		final float deltaY = top - bottom;
		final float deltaZ = zFar - zNear;

		if ((zNear <= 0.0f) || (zFar <= 0.0f) || (deltaX == 0.0f) || (deltaY == 0.0f) || (deltaZ == 0.0f))
			throw new GLInvalidValueException("glFrustrum(float, float, float, float, float, float)");

		m.setIdentity();

		m.m[0] = zNear * 2.0f / deltaX;
		m.m[5] = zNear * 2.0f / deltaY;
		m.m[8] = (right + left) / deltaX;
		m.m[9] = (top + bottom) / deltaY;
		m.m[10] = -(zFar + zNear) / deltaZ;
		m.m[11] = -1.0f;
		m.m[14] = -2.0f * zNear * zFar / deltaZ;
		m.m[15] = 0.0f;

		glMultMatrixf(m.m);
	}

	/////////////////////// glEnable / glDisable ///////////////////////

	public final void glEnable(int capability) {
		__GL_SETUP_NOT_IN_BEGIN("glEnable(int)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glEnable(capability);
		else {
			switch (capability) {
			case GL_ALPHA_TEST: // If enabled, do alpha testing. See glAlphaFunc.
				gc.state.enables.alphaTest = true;
				break;
			case GL_AUTO_NORMAL: // If enabled, compute surface normal vectors analytically when either
				// GL_MAP2_VERTEX_3 or GL_MAP2_VERTEX_4 has generated vertices.
				// See glMap2.
				gc.state.enables.autoNormal = true;
				break;
			case GL_BLEND: // If enabled, blend the incoming RGBA color values with the values in
				// the color buffers. See glBlendFunc.
				gc.state.enables.blend = true;
				break;
			case GL_COLOR_MATERIAL: // If enabled, have one or more material parameters track the
				// current color. See glColorMaterial.
				gc.state.enables.colorMaterial = true;
				//( * gc - > procs.pickColorMaterialProcs)(gc);
				//( * gc - > procs.setColor)(gc);
				break;

			case GL_CULL_FACE: // If enabled, cull polygons based on their winding in window coordinates.
				// See glCullFace.
				gc.state.enables.cullFace = true;
				break;

			case GL_DEPTH_TEST: // If enabled, do depth comparisons and update the depth buffer.
				// See glDepthFunc and glDepthRange.
				gc.state.enables.depthTest = true;
				break;
			case GL_DITHER: // If enabled, dither color components or indexes before they are written
				// to the color buffer.
				gc.state.enables.dither = true;
				break;
			case GL_FOG: // If enabled, blend a fog color into the post-texturing color. See glFog.
				gc.state.enables.fog = true;
				break;
			case GL_LIGHTING: // If enabled, use the current lighting parameters to compute the vertices
				// color or numberOfVertices. If disabled, associate the current color or numberOfVertices with
				// each vertices. See glMaterial, glLightModel, and glLight.
				gc.state.enables.lighting = true;
				//( * gc - > procs.pickColorMaterialProcs)(gc);
				//( * gc - > procs.setColor)(gc);

				break;

			case GL_LINE_SMOOTH: // If enabled, draw lines with correct filtering. If disabled, draw
				// aliased lines. See glLineWidth.
				gc.state.enables.lineSmooth = true;
				break;
			case GL_LINE_STIPPLE: // If enabled, use the current line stipple pattern when drawing lines.
				// See glLineStipple.
				gc.state.enables.lineStipple = true;
				return;
			case GL_INDEX_LOGIC_OP: // If enabled, apply the current logical operation to the incoming
				// numberOfVertices and color buffer indices. See glLogicOp.
				gc.state.enables.indexLogicOp = true;
				break;
			case GL_COLOR_LOGIC_OP: // If enabled, apply the current logical operation to the incoming
				// RGBA color and color buffer values. See glLogicOp.
				gc.state.enables.colorLogicOp = true;
				break;
			case GL_NORMALIZE: // If enabled, normal vectors specified with glNormal are scaled to unit
				// length after transformation. See glNormal.
				gc.state.enables.normalize = true;
				break;
			case GL_POINT_SMOOTH: // If enabled, draw points with proper filtering. If disabled, draw
				// aliased points. See glPointSize.
				gc.state.enables.pointSmooth = true;
				break;
			case GL_POLYGON_SMOOTH: // If enabled, draw polygons with proper filtering. If disabled,
				// draw aliased polygons. See glPolygonMode.
				gc.state.enables.polygonSmooth = true;
				break;
			case GL_POLYGON_STIPPLE: // If enabled, use the current polygon stipple pattern when
				// rendering polygons. See glPolygonStipple.
				gc.state.enables.polygonStipple = true;
				break;
			//                case GL_RESCALE_NORMAL:
			//                    state.enables.general |= __GL_RESCALE_NORMAL_ENABLE;
			//                    break;
			case GL_SCISSOR_TEST: // If enabled, discard fragments that are outside the scissor
				// rectangle. See glScissor.

				if (gc.state.enables.scissor)
					break;

				gc.state.enables.scissor = true;
				DELAY_VALIDATE(VALIDATE_BUFFER);

				break;

			case GL_STENCIL_TEST: // If enabled, do stencil testing and update the stencil buffer.
				// See glStencilFunc and glStencilOp.

				gc.state.enables.stencil = true;
				break;

			case GL_TEXTURE_1D: // If enabled, one-dimensional texturing is performed (unless
				// two-dimensional texturing is also enabled). See glTexImage1D.

				gc.state.enables.texture[gc.state.texture.currentUnitIndex] |= TEXTURE_1D_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_TEXTURE_2D: // If enabled, two-dimensional texturing is performed. See glTexImage2D.

				gc.state.enables.texture[gc.state.texture.currentUnitIndex] |= TEXTURE_2D_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_TEXTURE_3D: // If enabled, three-dimensional texturing is performed. See glTexImage3D.

				gc.state.enables.texture[gc.state.texture.currentUnitIndex] |= TEXTURE_3D_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_TEXTURE_GEN_S: // If enabled, the s textureManager coordinate is computed using the textureManager
				// generation function defined with glTexGen. If disabled, the current
				// s textureManager coordinate is used.
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] |= TEXTURE_GEN_S_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_TEXTURE_GEN_T: // If enabled, the t textureManager coordinate is computed using the textureManager
				// generation function defined with glTexGen. If disabled, the current
				// t textureManager coordinate is used.
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] |= TEXTURE_GEN_T_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_TEXTURE_GEN_R: // If enabled, the r textureManager coordinate is computed using the textureManager
				// generation function defined with glTexGen. If disabled, the current
				// r textureManager coordinate is used.
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] |= TEXTURE_GEN_R_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_TEXTURE_GEN_Q: // If enabled, the q textureManager coordinate is computed using the textureManager
				// generation function defined with glTexGen. If disabled, the current
				// q textureManager coordinate is used.
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] |= TEXTURE_GEN_Q_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_CLIP_PLANE0:
			case GL_CLIP_PLANE1:
			case GL_CLIP_PLANE2:
			case GL_CLIP_PLANE3:
			case GL_CLIP_PLANE4:
			case GL_CLIP_PLANE5:

				// If enabled, clip math against user-defined clipping plane i. See glClipPlane.

				capability -= GL_CLIP_PLANE0;
				gc.state.enables.clipPlanes |= (1 << capability);
				break;

			case GL_LIGHT0:
			case GL_LIGHT1:
			case GL_LIGHT2:
			case GL_LIGHT3:
			case GL_LIGHT4:
			case GL_LIGHT5:
			case GL_LIGHT6:
			case GL_LIGHT7:

				// If enabled, include lighting i in the evaluation of the lighting equation.
				// See glLightModel and glLight.

				capability -= GL_LIGHT0;
				gc.state.enables.lights |= (1 << capability);
				break;

			case GL_MAP1_COLOR_4:
				gc.state.enables.map1Color4 = true;
				break;
			case GL_MAP1_NORMAL:
				gc.state.enables.map1Normal = true;
				break;
			case GL_MAP1_INDEX:
				gc.state.enables.map1Index = true;
				break;
			case GL_MAP1_TEXTURE_COORD_1:
				gc.state.enables.map1TextureCoord1 = true;
				break;
			case GL_MAP1_TEXTURE_COORD_2:
				gc.state.enables.map1TextureCoord2 = true;
				break;
			case GL_MAP1_TEXTURE_COORD_3:
				gc.state.enables.map1TextureCoord3 = true;
				break;
			case GL_MAP1_TEXTURE_COORD_4:
				gc.state.enables.map1TextureCoord4 = true;
				break;
			case GL_MAP1_VERTEX_3:
				gc.state.enables.map1Vertex3 = true;
				break;
			case GL_MAP1_VERTEX_4:
				gc.state.enables.map1Vertex4 = true;
				break;
			case GL_MAP2_COLOR_4:
				gc.state.enables.map2Color4 = true;
				break;
			case GL_MAP2_NORMAL:
				gc.state.enables.map2Normal = true;
				break;
			case GL_MAP2_INDEX:
				gc.state.enables.map2Index = true;
				break;
			case GL_MAP2_TEXTURE_COORD_1:
				gc.state.enables.map2TextureCoord1 = true;
				break;
			case GL_MAP2_TEXTURE_COORD_2:
				gc.state.enables.map2TextureCoord2 = true;
				break;
			case GL_MAP2_TEXTURE_COORD_3:
				gc.state.enables.map2TextureCoord3 = true;
				break;
			case GL_MAP2_TEXTURE_COORD_4:
				gc.state.enables.map2TextureCoord4 = true;
				break;
			case GL_MAP2_VERTEX_3:
				gc.state.enables.map2Vertex3 = true;
				break;
			case GL_MAP2_VERTEX_4:
				gc.state.enables.map1Vertex4 = true;
				break;
			case GL_POLYGON_OFFSET_POINT: // If enabled, an offset is added to depth values of a polygon's
				// fragments before the depth comparison is performed, if the
				// polygon is rendered in GL_POINT mode. See glPolygonOffset.

				gc.state.enables.polygonOffsetPoint = true;
				break;

			case GL_POLYGON_OFFSET_LINE: // If enabled, and if the polygon is rendered in GL_LINE mode,
				// an offset is added to depth values of a polygon's fragments
				// before the depth comparison is performed. See glPolygonOffset.

				gc.state.enables.polygonOffsetLine = true;
				break;

			case GL_POLYGON_OFFSET_FILL: // If enabled, and if the polygon is rendered in GL_FILL mode,
				// an offset is added to depth values of a polygon's fragments
				// before the depth comparison is performed. See glPolygonOffset.

				gc.state.enables.polygonOffsetFill = true;
				break;

			default:

				throw new GLInvalidEnumException("glEnable(int)");
			}
		}
	}

	public final void glDisable(int capability) {
		__GL_SETUP_NOT_IN_BEGIN("glDisable(int)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glDisable(capability);
		else {
			switch (capability) {
			case GL_ALPHA_TEST:
				gc.state.enables.alphaTest = false;
				break;
			case GL_AUTO_NORMAL:
				gc.state.enables.autoNormal = false;
				break;
			case GL_BLEND:
				gc.state.enables.blend = false;
				break;
			case GL_COLOR_MATERIAL:
				gc.state.enables.colorMaterial = false;
				//(*gc->procs.pickColorMaterialProcs)(gc);
				break;
			case GL_CULL_FACE:
				gc.state.enables.cullFace = false;
				break;
			case GL_DEPTH_TEST:
				gc.state.enables.depthTest = false;
				break;
			case GL_DITHER:
				gc.state.enables.dither = false;
				break;
			case GL_FOG:
				gc.state.enables.fog = false;
				break;
			case GL_LIGHTING:
				gc.state.enables.lighting = false;
				//(*gc->procs.pickColorMaterialProcs)(gc);
				//(*gc->procs.setColor)(gc);
				break;
			case GL_LINE_SMOOTH:
				gc.state.enables.lineSmooth = false;
				break;
			case GL_LINE_STIPPLE:
				gc.state.enables.lineStipple = false;
				break;
			case GL_INDEX_LOGIC_OP:
				gc.state.enables.indexLogicOp = false;
				break;
			case GL_COLOR_LOGIC_OP:
				gc.state.enables.colorLogicOp = false;
				break;
			case GL_NORMALIZE:
				gc.state.enables.normalize = false;
				break;
			case GL_POINT_SMOOTH:
				gc.state.enables.pointSmooth = false;
				break;
			case GL_POLYGON_SMOOTH:
				gc.state.enables.pointSmooth = false;
				break;
			case GL_POLYGON_STIPPLE:
				gc.state.enables.polygonStipple = false;
				break;
			//      case GL_RESCALE_NORMAL:
			//	gc->state.enables.general &= ~__GL_RESCALE_NORMAL_ENABLE;
			//	break;
			case GL_SCISSOR_TEST:

				if (!gc.state.enables.scissor)
					break;

				gc.state.enables.scissor = false;
				DELAY_VALIDATE(VALIDATE_BUFFER);

				break;

			case GL_STENCIL_TEST:
				gc.state.enables.stencil = false;
				break;
			case GL_TEXTURE_1D:
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] &= ~TEXTURE_1D_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;
			case GL_TEXTURE_2D:
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] &= ~TEXTURE_2D_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;
			case GL_TEXTURE_3D:
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] &= ~TEXTURE_3D_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;
			case GL_TEXTURE_GEN_S:
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] &= ~TEXTURE_GEN_S_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;
			case GL_TEXTURE_GEN_T:
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] &= ~TEXTURE_GEN_T_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;
			case GL_TEXTURE_GEN_R:
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] &= ~TEXTURE_GEN_R_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;
			case GL_TEXTURE_GEN_Q:
				gc.state.enables.texture[gc.state.texture.currentUnitIndex] &= ~TEXTURE_GEN_Q_ENABLE;
				DELAY_VALIDATE(VALIDATE_TEXTURE);
				break;

			case GL_CLIP_PLANE0:
			case GL_CLIP_PLANE1:
			case GL_CLIP_PLANE2:
			case GL_CLIP_PLANE3:
			case GL_CLIP_PLANE4:
			case GL_CLIP_PLANE5:

				capability -= GL_CLIP_PLANE0;
				gc.state.enables.clipPlanes &= ~(1 << capability);
				break;

			case GL_LIGHT0:
			case GL_LIGHT1:
			case GL_LIGHT2:
			case GL_LIGHT3:
			case GL_LIGHT4:
			case GL_LIGHT5:
			case GL_LIGHT6:
			case GL_LIGHT7:

				capability -= GL_LIGHT0;
				gc.state.enables.lights &= ~(1 << capability);
				break;

			case GL_MAP1_COLOR_4:
				gc.state.enables.map1Color4 = false;
				break;
			case GL_MAP1_NORMAL:
				gc.state.enables.map1Normal = false;
				break;
			case GL_MAP1_INDEX:
				gc.state.enables.map1Index = false;
				break;
			case GL_MAP1_TEXTURE_COORD_1:
				gc.state.enables.map1TextureCoord1 = false;
				break;
			case GL_MAP1_TEXTURE_COORD_2:
				gc.state.enables.map1TextureCoord2 = false;
				break;
			case GL_MAP1_TEXTURE_COORD_3:
				gc.state.enables.map1TextureCoord3 = false;
				break;
			case GL_MAP1_TEXTURE_COORD_4:
				gc.state.enables.map1TextureCoord4 = false;
				break;
			case GL_MAP1_VERTEX_3:
				gc.state.enables.map1Vertex3 = false;
				break;
			case GL_MAP1_VERTEX_4:
				gc.state.enables.map1Vertex4 = false;
				break;
			case GL_MAP2_COLOR_4:
				gc.state.enables.map2Color4 = false;
				break;
			case GL_MAP2_NORMAL:
				gc.state.enables.map2Normal = false;
				break;
			case GL_MAP2_INDEX:
				gc.state.enables.map2Index = false;
				break;
			case GL_MAP2_TEXTURE_COORD_1:
				gc.state.enables.map2TextureCoord1 = false;
				break;
			case GL_MAP2_TEXTURE_COORD_2:
				gc.state.enables.map2TextureCoord2 = false;
				break;
			case GL_MAP2_TEXTURE_COORD_3:
				gc.state.enables.map2TextureCoord3 = false;
				break;
			case GL_MAP2_TEXTURE_COORD_4:
				gc.state.enables.map2TextureCoord4 = false;
				break;
			case GL_MAP2_VERTEX_3:
				gc.state.enables.map2Vertex3 = false;
				break;
			case GL_MAP2_VERTEX_4:
				gc.state.enables.map2Vertex4 = false;
				break;
			case GL_POLYGON_OFFSET_POINT:
				gc.state.enables.polygonOffsetPoint = false;
				break;
			case GL_POLYGON_OFFSET_LINE:
				gc.state.enables.polygonOffsetLine = false;
				break;
			case GL_POLYGON_OFFSET_FILL:
				gc.state.enables.polygonOffsetFill = false;
				break;
			default:
				throw new GLInvalidEnumException("glDisable(int)");
			}
		}
	}

	public final boolean glIsEnabled(final int capability) {
		__GL_SETUP_NOT_IN_BEGIN("glIsEnabled(int)");

		switch (capability) {
		case GL_ALPHA_TEST:
			return gc.state.enables.alphaTest;
		case GL_AUTO_NORMAL:
			return gc.state.enables.autoNormal;
		case GL_BLEND:
			return gc.state.enables.blend;
		case GL_COLOR_MATERIAL:
			return gc.state.enables.colorMaterial;
		case GL_CULL_FACE:
			return gc.state.enables.cullFace;
		case GL_DEPTH_TEST:
			return gc.state.enables.depthTest;
		case GL_DITHER:
			return gc.state.enables.dither;
		case GL_FOG:
			return gc.state.enables.fog;
		case GL_LIGHTING:
			return gc.state.enables.lighting;
		case GL_LINE_SMOOTH:
			return gc.state.enables.lineSmooth;
		case GL_LINE_STIPPLE:
			return gc.state.enables.lineStipple;
		case GL_INDEX_LOGIC_OP:
			return gc.state.enables.indexLogicOp;
		case GL_COLOR_LOGIC_OP:
			return gc.state.enables.colorLogicOp;
		case GL_NORMALIZE:
			return gc.state.enables.normalize;
		case GL_POINT_SMOOTH:
			return gc.state.enables.pointSmooth;
		case GL_POLYGON_SMOOTH:
			return gc.state.enables.polygonSmooth;
		case GL_POLYGON_STIPPLE:
			return gc.state.enables.polygonStipple;
			//      case GL_RESCALE_NORMAL:
			//	bit = gc->state.enables.general & __GL_RESCALE_NORMAL_ENABLE;
			//	break;
		case GL_SCISSOR_TEST:
			return gc.state.enables.scissor;
		case GL_STENCIL_TEST:
			return gc.state.enables.stencil;
		case GL_TEXTURE_1D:
			return (gc.state.enables.texture[gc.state.texture.currentUnitIndex] & TEXTURE_1D_ENABLE) != 0;
		case GL_TEXTURE_2D:
			return (gc.state.enables.texture[gc.state.texture.currentUnitIndex] & TEXTURE_2D_ENABLE) != 0;
		case GL_TEXTURE_3D:
			return (gc.state.enables.texture[gc.state.texture.currentUnitIndex] & TEXTURE_3D_ENABLE) != 0;
		case GL_TEXTURE_GEN_S:
			return (gc.state.enables.texture[gc.state.texture.currentUnitIndex] & TEXTURE_GEN_S_ENABLE) != 0;
		case GL_TEXTURE_GEN_T:
			return (gc.state.enables.texture[gc.state.texture.currentUnitIndex] & TEXTURE_GEN_T_ENABLE) != 0;
		case GL_TEXTURE_GEN_R:
			return (gc.state.enables.texture[gc.state.texture.currentUnitIndex] & TEXTURE_GEN_R_ENABLE) != 0;
		case GL_TEXTURE_GEN_Q:
			return (gc.state.enables.texture[gc.state.texture.currentUnitIndex] & TEXTURE_GEN_Q_ENABLE) != 0;
		case GL_CLIP_PLANE0:
		case GL_CLIP_PLANE1:
		case GL_CLIP_PLANE2:
		case GL_CLIP_PLANE3:
		case GL_CLIP_PLANE4:
		case GL_CLIP_PLANE5:
			return (gc.state.enables.clipPlanes & (1 << (capability - GL_CLIP_PLANE0))) != 0;
		case GL_LIGHT0:
		case GL_LIGHT1:
		case GL_LIGHT2:
		case GL_LIGHT3:
		case GL_LIGHT4:
		case GL_LIGHT5:
		case GL_LIGHT6:
		case GL_LIGHT7:
			return (gc.state.enables.lights & (1 << (capability - GL_LIGHT0))) != 0;
		case GL_MAP1_COLOR_4:
			return gc.state.enables.map1Color4;
		case GL_MAP1_NORMAL:
			return gc.state.enables.map1Normal;
		case GL_MAP1_INDEX:
			return gc.state.enables.map1Index;
		case GL_MAP1_TEXTURE_COORD_1:
			return gc.state.enables.map1TextureCoord1;
		case GL_MAP1_TEXTURE_COORD_2:
			return gc.state.enables.map1TextureCoord2;
		case GL_MAP1_TEXTURE_COORD_3:
			return gc.state.enables.map1TextureCoord3;
		case GL_MAP1_TEXTURE_COORD_4:
			return gc.state.enables.map1TextureCoord4;
		case GL_MAP1_VERTEX_3:
			return gc.state.enables.map1Vertex3;
		case GL_MAP1_VERTEX_4:
			return gc.state.enables.map1Vertex4;
		case GL_MAP2_COLOR_4:
			return gc.state.enables.map2Color4;
		case GL_MAP2_NORMAL:
			return gc.state.enables.map2Normal;
		case GL_MAP2_INDEX:
			return gc.state.enables.map2Index;
		case GL_MAP2_TEXTURE_COORD_1:
			return gc.state.enables.map2TextureCoord1;
		case GL_MAP2_TEXTURE_COORD_2:
			return gc.state.enables.map2TextureCoord2;
		case GL_MAP2_TEXTURE_COORD_3:
			return gc.state.enables.map2TextureCoord3;
		case GL_MAP2_TEXTURE_COORD_4:
			return gc.state.enables.map2TextureCoord4;
		case GL_MAP2_VERTEX_3:
			return gc.state.enables.map2Vertex3;
		case GL_MAP2_VERTEX_4:
			return gc.state.enables.map2Vertex4;
		case GL_POLYGON_OFFSET_POINT:
			return gc.state.enables.polygonOffsetPoint;
		case GL_POLYGON_OFFSET_LINE:
			return gc.state.enables.polygonOffsetLine;
		case GL_POLYGON_OFFSET_FILL:
			return gc.state.enables.polygonOffsetFill;
		default:
			return false; // If an error is generated, glIsEnabled returns false.
		}
	}

	public final void glEnableClientState(final int capabilty) {
		__GL_SETUP_NOT_IN_BEGIN("glEnableClientState(int)");

		switch (capabilty) {
		case GL_VERTEX_ARRAY:
			interleavedArray.mask |= VERTARRAY_V_MASK;
			interleavedArray.index |= VERTARRAY_V_INDEX;
			break;
		case GL_NORMAL_ARRAY:
			interleavedArray.mask |= VERTARRAY_N_MASK;
			interleavedArray.index |= VERTARRAY_N_INDEX;
			break;
		case GL_COLOR_ARRAY:
			interleavedArray.mask |= VERTARRAY_C_MASK;
			interleavedArray.index |= VERTARRAY_C_INDEX;
			return;
		case GL_INDEX_ARRAY:
			interleavedArray.mask |= VERTARRAY_I_MASK;
			interleavedArray.index |= VERTARRAY_I_INDEX;
			break;
		//    case GL_TEXTURE_COORD_ARRAY:
		//        interleavedArray.textureEnables |= (1<<gc->clientTexture.activeTexture);
		//	interleavedArray.valueMask |= VERTARRAY_T_MASK;
		//	interleavedArray.index |= VERTARRAY_T_INDEX;
		//	break;
		case GL_EDGE_FLAG_ARRAY:
			interleavedArray.mask |= VERTARRAY_E_MASK;
			interleavedArray.index |= VERTARRAY_E_INDEX;
			break;
		default:
			throw new GLInvalidEnumException("glEnableClientState(int)");
		}
	}

	public final void glDisableClientState(final int capabilty) {
		__GL_SETUP_NOT_IN_BEGIN("glDisableClientState(int)");

		switch (capabilty) {
		case GL_VERTEX_ARRAY:
			interleavedArray.mask &= ~VERTARRAY_V_MASK;
			interleavedArray.index &= ~VERTARRAY_V_INDEX;
			break;
		case GL_NORMAL_ARRAY:
			interleavedArray.mask &= ~VERTARRAY_N_MASK;
			interleavedArray.index &= ~VERTARRAY_N_INDEX;
			break;
		case GL_COLOR_ARRAY:
			interleavedArray.mask &= ~VERTARRAY_C_MASK;
			interleavedArray.index &= ~VERTARRAY_C_INDEX;
			break;
		case GL_INDEX_ARRAY:
			interleavedArray.mask &= ~VERTARRAY_I_MASK;
			interleavedArray.index &= ~VERTARRAY_I_INDEX;
			break;
		//    case GL_TEXTURE_COORD_ARRAY:
		//        interleavedArray.textureEnables &= ~(1<<gc->clientTexture.activeTexture);
		//	if (interleavedArray.textureEnables == 0) {
		//	    interleavedArray.valueMask &= ~VERTARRAY_T_MASK;
		//	    interleavedArray.index &= ~VERTARRAY_T_INDEX;
		//	}
		//	return;
		case GL_EDGE_FLAG_ARRAY:
			interleavedArray.mask &= ~VERTARRAY_E_MASK;
			interleavedArray.index &= ~VERTARRAY_E_INDEX;
			break;
		default:
			throw new GLInvalidEnumException("glDisableClientState(int)");
		}
	}

	/////////////////////// Attributes ///////////////////////

	public final void glPushAttrib(final int mask) {
		__GL_SETUP_NOT_IN_BEGIN("glPushAttrib(int)");

		/*

		 A valueMask that indicates which attributes to save. The symbolic valueMask constants
		 and their associated OpenGL state are as follows
		 (the indented paragraphs list which attributes are saved):

		 GL_ACCUM_BUFFER_BIT

		 * Accumulation buffer clear value

		 GL_COLOR_BUFFER_BIT

		 * GL_ALPHA_TEST enable bit
		 * Alpha test function and reference value
		 * GL_BLEND enable bit
		 * Blending source and destination functions
		 * GL_DITHER enable bit
		 * GL_DRAW_BUFFER setting
		 * GL_LOGIC_OP enable bit
		 * Logic op function
		 * Color-mode and index-mode clear values
		 * Color-mode and index-mode writemasks

		 GL_CURRENT_BIT

		 * Current RGBA color
		 * Current color index
		 * Current normal vector
		 * Current texture coordinates
		 * Current raster position
		 * GL_CURRENT_RASTER_POSITION_VALID flag

		 - RGBA color associated with current raster position
		 - Color index associated with current raster position
		 - Texture coordinates associated with current raster position
		 - GL_EDGE_FLAG flag

		 GL_DEPTH_BUFFER_BIT

		 * GL_DEPTH_TEST enable bit
		 * Depth buffer test function
		 * Depth buffer clear value
		 * GL_DEPTH_WRITEMASK enable bit

		 GL_ENABLE_BIT

		 * GL_ALPHA_TEST flag
		 * GL_AUTO_NORMAL flag
		 * GL_BLEND flag
		 * Enable bits for the user-definable clipping planes
		 * GL_COLOR_MATERIAL
		 * GL_CULL_FACE flag
		 * GL_DEPTH_TEST flag
		 * GL_DITHER flag
		 * GL_FOG flag
		 * GL_LIGHTi where 0 <= i < GL_MAX_LIGHTS
		 * GL_LIGHTING flag
		 * GL_LINE_SMOOTH flag
		 * GL_LINE_STIPPLE flag
		 * GL_COLOR_LOGIC_OP flag
		 * GL_INDEX_LOGIC_OP flag
		 * GL_MAP1_x where x is a map type
		 * GL_MAP2_x where x is a map type
		 * GL_NORMALIZE flag
		 * GL_POINT_SMOOTH flag
		 * GL_POLYGON_OFFSET_LINE flag
		 * GL_POLYGON_OFFSET_FILL flag
		 * GL_POLYGON_OFFSET_POINT flag
		 * GL_POLYGON_SMOOTH flag
		 * GL_POLYGON_STIPPLE flag
		 * GL_SCISSOR_TEST flag
		 * GL_STENCIL_TEST flag

		 - GL_TEXTURE_1D flag
		 - GL_TEXTURE_2D flag
		 - Flags GL_TEXTURE_GEN_x where x is S, T, R, or Q

		 GL_EVAL_BIT

		 * GL_MAP1_x enable bits, where x is a map type
		 * GL_MAP2_x enable bits, where x is a map type
		 * 1-D grid endpoints and divisions
		 * 2-D grid endpoints and divisions
		 * GL_AUTO_NORMAL enable bit

		 GL_FOG_BIT

		 * GL_FOG enable flag
		 * Fog color
		 * Fog density
		 * Linear fog start
		 * Linear fog end
		 * Fog index
		 * GL_FOG_MODE value

		 GL_HINT_BIT

		 * GL_PERSPECTIVE_CORRECTION_HINT setting
		 * GL_POINT_SMOOTH_HINT setting
		 * GL_LINE_SMOOTH_HINT setting
		 * GL_POLYGON_SMOOTH_HINT setting
		 * GL_FOG_HINT setting

		 GL_LIGHTING_BIT

		 GL_COLOR_MATERIAL enable bit
		 GL_COLOR_MATERIAL_FACE value
		 Color material parameters that are tracking the current color
		 Ambient scene color
		 GL_LIGHT_MODEL_LOCAL_VIEWER value
		 GL_LIGHT_MODEL_TWO_SIDE setting
		 GL_LIGHTING enable bit
		 Enable bit for each light
		 Ambient, diffuse, and specular intensity for each light
		 Direction, position, exponent, and cutoff angle for each light
		 Constant, linear, and quadratic attenuation factors for each light
		 Ambient, diffuse, specular, and emissive color for each material
		 Ambient, diffuse, and specular color indexes for each material
		 Specular exponent for each material
		 GL_SHADE_MODEL setting

		 GL_LINE_BIT

		 * GL_LINE_SMOOTH flag
		 * GL_LINE_STIPPLE enable bit
		 * Line stipple pattern and repeat counter
		 * Line width

		 GL_LIST_BIT

		 - GL_LIST_BASE setting

		 GL_PIXEL_MODE_BIT

		 - GL_RED_BIAS and GL_RED_SCALE settings
		 - GL_GREEN_BIAS and GL_GREEN_SCALE values
		 - GL_BLUE_BIAS and GL_BLUE_SCALE
		 - GL_ALPHA_BIAS and GL_ALPHA_SCALE
		 - GL_DEPTH_BIAS and GL_DEPTH_SCALE
		 - GL_INDEX_OFFSET and GL_INDEX_SHIFT values
		 - GL_MAP_COLOR and GL_MAP_STENCIL flags
		 - GL_ZOOM_X and GL_ZOOM_Y factors
		 - GL_READ_BUFFER setting

		 GL_POINT_BIT

		 * GL_POINT_SMOOTH flag
		 * Point size

		 GL_POLYGON_BIT

		 * GL_CULL_FACE enable bit
		 * GL_CULL_FACE_MODE value
		 * GL_FRONT_FACE indicator

		 - GL_POLYGON_MODE setting
		 - GL_POLYGON_SMOOTH flag

		 * GL_POLYGON_STIPPLE enable bit

		 - GL_POLYGON_OFFSET_FILL flag
		 - GL_POLYGON_OFFSET_LINE flag
		 - GL_POLYGON_OFFSET_POINT flag

		 * GL_POLYGON_OFFSET_FACTOR
		 * GL_POLYGON_OFFSET_UNITS

		 GL_POLYGON_STIPPLE_BIT

		 * Polygon stipple image

		 GL_SCISSOR_BIT

		 * GL_SCISSOR_TEST flag

		 - Scissor box

		 GL_STENCIL_BUFFER_BIT

		 * GL_STENCIL_TEST enable bit
		 * Stencil function and reference value
		 * Stencil value valueMask
		 * Stencil fail, pass, and depth buffer pass actions
		 * Stencil buffer clear value
		 * Stencil buffer writemask

		 GL_TEXTURE_BIT

		 Enable bits for the four texture coordinates
		 Border color for each texture image
		 Minification function for each texture image
		 Magnification function for each texture image
		 Texture coordinates and wrap mode for each texture image
		 Color and mode for each texture environment
		 Enable bits GL_TEXTURE_GEN_x; x is S, T, R, and Q
		 GL_TEXTURE_GEN_MODE setting for S, T, R, and Q
		 glTexGen plane equations for S, T, R, and Q

		 GL_TRANSFORM_BIT

		 * Coefficients of the six clipping planes
		 * Enable bits for the user-definable clipping planes
		 * GL_MATRIX_MODE value
		 * GL_NORMALIZE flag

		 GL_VIEWPORT_BIT

		 * Depth range (near and far)
		 * Viewport origin and extent
		 */

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPushAttrib(mask);
		else {
			final GLAttribute attribute = new GLAttribute();
			gc.attributeStack.push(attribute);

			attribute.mask = mask;
			attribute.enables = gc.state.enables.get(); // enables are always pushed !!!

			if ((mask & GL_ACCUM_BUFFER_BIT) != 0)
				attribute.accum = gc.state.accum.get();

			if ((mask & GL_COLOR_BUFFER_BIT) != 0)
				attribute.colorBuffer = gc.state.colorBuffer.get();

			if ((mask & GL_CURRENT_BIT) != 0)
				attribute.current = gc.state.current.get();

			if ((mask & GL_DEPTH_BUFFER_BIT) != 0)
				attribute.depth = gc.state.depth.get();

			if ((mask & GL_EVAL_BIT) != 0)
				attribute.evaluator = gc.state.evaluator.get();

			if ((mask & GL_FOG_BIT) != 0)
				attribute.fog = gc.state.fog.get();

			if ((mask & GL_HINT_BIT) != 0)
				attribute.hints = gc.state.hints.get();

			if ((mask & GL_LIGHTING_BIT) != 0)
				attribute.lighting = gc.state.lighting.get();

			if ((mask & GL_LINE_BIT) != 0)
				attribute.line = gc.state.line.get();

			if ((mask & GL_LIST_BIT) != 0)
				attribute.list = gc.state.list.get();

			if ((mask & GL_PIXEL_MODE_BIT) != 0)
				attribute.pixel = gc.state.pixel.get();

			if ((mask & GL_POINT_BIT) != 0)
				attribute.point = gc.state.point.get();

			if ((mask & GL_POLYGON_BIT) != 0)
				attribute.polygon = gc.state.polygon.get();

			if ((mask & GL_POLYGON_STIPPLE_BIT) != 0)
				attribute.polygonStipple = gc.state.polygonStipple.get();

			if ((mask & GL_SCISSOR_BIT) != 0)
				attribute.scissor = gc.state.scissor.get();

			if ((mask & GL_STENCIL_BUFFER_BIT) != 0)
				attribute.stencil = gc.state.stencil.get();

			if ((mask & GL_TEXTURE_BIT) != 0)
				attribute.texture = gc.state.texture.get();

			if ((mask & GL_TRANSFORM_BIT) != 0)
				attribute.transform = gc.state.transform.get();

			if ((mask & GL_VIEWPORT_BIT) != 0)
				attribute.viewport = gc.state.viewport.get();
		}
	}

	public final void glPopAttrib() {
		__GL_SETUP_NOT_IN_BEGIN("glPopAttrib()");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPopAttrib();
		else {
			final GLAttribute attribute = (GLAttribute) gc.attributeStack.pop();
			final GLEnableState enables = attribute.enables;

			final int mask = attribute.mask;

			if ((mask & GL_ACCUM_BUFFER_BIT) != 0)
				gc.state.accum.set(attribute.accum);

			if ((mask & GL_COLOR_BUFFER_BIT) != 0) {
				gc.state.colorBuffer.set(attribute.colorBuffer);

				gc.state.enables.alphaTest = enables.alphaTest;
				gc.state.enables.blend = enables.blend;
				gc.state.enables.dither = enables.dither;
				gc.state.enables.indexLogicOp = enables.indexLogicOp;
				gc.state.enables.colorLogicOp = enables.colorLogicOp;
			}

			if ((mask & GL_CURRENT_BIT) != 0)
				gc.state.current.set(attribute.current);

			if ((mask & GL_DEPTH_BUFFER_BIT) != 0) {
				gc.state.depth.set(attribute.depth);
				gc.state.enables.depthTest = enables.depthTest;
			}

			if ((mask & GL_ENABLE_BIT) != 0)
				gc.state.enables.set(attribute.enables);

			if ((mask & GL_EVAL_BIT) != 0) {
				gc.state.evaluator.set(attribute.evaluator);

				gc.state.enables.autoNormal = enables.autoNormal;

				gc.state.enables.map1Color4 = enables.map1Color4;
				gc.state.enables.map1Index = enables.map1Index;
				gc.state.enables.map1Normal = enables.map1Normal;
				gc.state.enables.map1TextureCoord1 = enables.map1TextureCoord1;
				gc.state.enables.map1TextureCoord2 = enables.map1TextureCoord2;
				gc.state.enables.map1TextureCoord3 = enables.map1TextureCoord3;
				gc.state.enables.map1TextureCoord4 = enables.map1TextureCoord4;
				gc.state.enables.map1Vertex3 = enables.map1Vertex3;
				gc.state.enables.map1Vertex4 = enables.map1Vertex4;

				gc.state.enables.map2Color4 = enables.map2Color4;
				gc.state.enables.map2Index = enables.map2Index;
				gc.state.enables.map2Normal = enables.map2Normal;
				gc.state.enables.map2TextureCoord1 = enables.map2TextureCoord1;
				gc.state.enables.map2TextureCoord2 = enables.map2TextureCoord2;
				gc.state.enables.map2TextureCoord3 = enables.map2TextureCoord3;
				gc.state.enables.map2TextureCoord4 = enables.map2TextureCoord4;
				gc.state.enables.map2Vertex3 = enables.map2Vertex3;
				gc.state.enables.map2Vertex4 = enables.map2Vertex4;
			}

			if ((mask & GL_FOG_BIT) != 0) {
				gc.state.fog.set(attribute.fog);
				gc.state.enables.fog = enables.fog;
			}

			if ((mask & GL_HINT_BIT) != 0)
				gc.state.hints.set(attribute.hints);

			if ((mask & GL_LIGHTING_BIT) != 0) {
				gc.state.lighting.set(attribute.lighting);

				gc.state.enables.colorMaterial = enables.colorMaterial;
				gc.state.enables.lighting = enables.lighting;
				gc.state.enables.lights = enables.lights;
			}

			if ((mask & GL_LINE_BIT) != 0) {
				gc.state.line.set(attribute.line);

				gc.state.enables.lineStipple = enables.lineStipple;
				gc.state.enables.lineSmooth = enables.lineSmooth;
			}

			if ((mask & GL_LIST_BIT) != 0)
				gc.state.list.set(attribute.list);

			if ((mask & GL_PIXEL_MODE_BIT) != 0) {
				gc.state.pixel.set(attribute.pixel);

				// TODO - haven't done the following, including enable flags

				//                GLint i;
				//                gc - > gc.state.pixel.transferMode = stackIndex - > pixel.transferMode;
				//                gc - > gc.state.pixel.readBufferReturn = stackIndex - > pixel.readBufferReturn;
				//                gc - > gc.state.pixel.readBuffer = stackIndex - > pixel.readBuffer;
				//                gc - > gc.state.enables.pixelPath = stackIndex - > enables.pixelPath;
				//                for (i = 0; i < __GL_NUM_COLOR_TABLE_TARGETS; ++i)
				//                {
				//                    *gc - > gc.state.pixel.colorTable[i] = *stackIndex - > pixel.colorTable[i];
				//                    ( * gc - > imports.free)(gc, stackIndex - > pixel.colorTable[i]);
				//                    stackIndex - > pixel.colorTable[i] = NULL;
				//                }
				//                for (i = 0; i < __GL_NUM_CONVOLUTION_TARGETS; ++i)
				//                {
				//                    *gc - > gc.state.pixel.convolutionFilter[i] =
				//                    *stackIndex - > pixel.convolutionFilter[i];
				//                    ( * gc - > imports.free)(gc, stackIndex - > pixel.convolutionFilter[i]);
				//                    stackIndex - > pixel.convolutionFilter[i] = NULL;
				//                }
			}

			if ((mask & GL_POINT_BIT) != 0) {
				gc.state.point.set(attribute.point);
				gc.state.enables.pointSmooth = enables.pointSmooth;
			}

			if ((mask & GL_POLYGON_BIT) != 0) {
				gc.state.polygon.set(attribute.polygon);

				gc.state.enables.cullFace = enables.cullFace;
				gc.state.enables.polygonSmooth = enables.polygonSmooth;
				gc.state.enables.polygonStipple = enables.polygonStipple;
				gc.state.enables.polygonOffsetPoint = enables.polygonOffsetPoint;
				gc.state.enables.polygonOffsetLine = enables.polygonOffsetLine;
				gc.state.enables.polygonOffsetFill = enables.polygonOffsetFill;
			}

			if ((mask & GL_POLYGON_STIPPLE_BIT) != 0) {
				gc.state.polygonStipple.set(attribute.polygonStipple);
				gc.state.enables.polygonStipple = enables.polygonStipple;
			}

			if ((mask & GL_SCISSOR_BIT) != 0) {
				gc.state.scissor.set(attribute.scissor);
				gc.state.enables.scissor = enables.scissor;
			}

			if ((mask & GL_STENCIL_BUFFER_BIT) != 0) {
				gc.state.stencil.set(attribute.stencil);
				gc.state.enables.stencil = enables.stencil;
			}

			if ((mask & GL_TEXTURE_BIT) != 0) {
				gc.state.texture.set(attribute.texture);

				// TODO - haven't done the following, including enable flags

				//                GLint i, j;
				//
				//                /* active textureManager */
				//                gc - > gc.state.textureManager.activeTexture = stackIndex - > textureManager.activeTexture;
				//                gc - > gc.state.textureManager.active =
				//                &gc - > gc.state.textureManager.unit[gc - > gc.state.textureManager.activeTexture];
				//                gc - > textureManager.active =
				//                &gc - > textureManager.unit[gc - > gc.state.textureManager.activeTexture];
				//
				//                /* textureManager unit state */
				//                for (i = 0; i < __GL_NUM_TEXTURE_UNITS; i++)
				//                {
				//                    __GLtextureUnitState * tusSrc = &stackIndex - > textureManager.unit[i];
				//                    __GLtextureUnitState * tusDst = &gc - > gc.state.textureManager.unit[i];
				//
				//                    /* TexGen state */
				//                    tusDst - > s = tusSrc - > s;
				//                    tusDst - > t = tusSrc - > t;
				//                    tusDst - > r = tusSrc - > r;
				//                    tusDst - > q = tusSrc - > q;
				//
				//                    /*
				//                    ** Texture binding gc.state.
				//                    ** If the textureManager name is different, a new binding is
				//                    ** called for.	Deferring the binding is dangerous, because
				//                    ** the state before the pop has to be saved with the
				//                    ** textureManager that is being unbound.  If we defer the binding,
				//                    ** we need to watch out for cases like two pops in a row
				//                    ** or a pop followed by a bind.
				//                    */
				//                    for (j = 0; j < __GL_NUM_TEXTURE_BINDINGS; j++)
				//                    {
				//                        __GLtextureBindingState * tbsSrc = tusSrc - > textureManager[j];
				//                        __GLtextureBindingState * tbsDst = tusDst - > textureManager[j];
				//
				//                        if (tbsDst - > name != tbsSrc - > name)
				//                        {
				//                            __glBindTexture(gc, i, j, tbsSrc - > name);
				//                        }
				//                        tbsDst = tusDst - > textureManager[j];
				//                        assert(tbsDst - > name == tbsSrc - > name);
				//                        tbsDst - > params = tbsSrc - > params;
				//
				//                        ( * gc - > imports.free)(gc, tbsSrc);
				//                        tusSrc - > textureManager[j] = NULL;
				//                    }
				//
				//                    /* TexEnv state */
				//                    __GL_MEMCOPY(tusDst - > env, tusSrc - > env,
				//                                 __GL_NUM_TEXTURE_ENV_BINDINGS
				//                                 * sizeof(__GLtextureEnvState));
				//
				//                    /* textureManager enable state */
				//                    gc - > gc.state.enables.textureManager[i] = stackIndex - > enables.textureManager[i];
				//                }

				DELAY_VALIDATE(VALIDATE_TEXTURE);
			}

			if ((mask & GL_TRANSFORM_BIT) != 0) {
				gc.state.transform.set(attribute.transform);

				gc.state.enables.normalize = enables.normalize;
				gc.state.enables.rescaleNormals = enables.rescaleNormals;
				gc.state.enables.clipPlanes = enables.clipPlanes;
			}

			if ((mask & GL_VIEWPORT_BIT) != 0) {
				gc.state.viewport.set(attribute.viewport);

				final int width = gc.state.viewport.width;
				final int height = gc.state.viewport.height;

				gc.resizeBuffers(width, height);
			}
		}
	}

	public final void glPushClientAttrib(final int mask) {
		throw new GLException("GLSoftware.glPushClientAttrib(int) - Not as yet implemented!");
	}

	public final void glPopClientAttrib() {
		throw new GLException("GLSoftware.glPopClientAttrib(int) - Not as yet implemented!");
	}

	public final void glPointSize(final float size) {
		__GL_SETUP_NOT_IN_BEGIN("glPointSize(float)");

		// The default is 1.0.

		// The glPointSize function specifies the rasterized diameter of both aliased and antialiased
		// points. Using a point size other than 1.0 has different effects, depending on whether point
		// antialiasing is enabled. Point antialiasing is controlled by calling glEnable and glDisable
		// with argument GL_POINT_SMOOTH.

		// If point antialiasing is disabled, the actual size is determined by rounding the supplied
		// size to the nearest integer. (If the rounding results in the value 0, it is as if the point
		// size were 1.) If the rounded size is odd, then the center point (x, y) of the pixel fragment
		// that represents the point is computed as

		// ( |xw| + .5, |yw| + .5)

		// where w subscripts indicate window coordinates. All pixels that lie within the square grid
		// of the rounded size centered at (x, y) make up the fragment. If the size is even, the center
		// point is

		// ( |xw + .5|, |yw + .5| )

		// and the rasterized fragment's centers are the half-integer window coordinates within the square
		// of the rounded size centered at (x, y). All pixel fragments produced in rasterizing a
		// nonantialiased point are assigned the same associated demos.data; that of the vertices corresponding
		// to the point.

		// If antialiasing is enabled, then point rasterization produces a fragment for each pixel
		// square that intersects the region lying within the circle having diameter equal to the
		// current point size and centered at the points (xw, yw). The coverage value for each
		// fragment is the window coordinate area of the intersection of the circular region with
		// the corresponding pixel square. This value is saved and used in the final rasterization step.
		// The demos.data associated with each fragment is the demos.data associated with the point being rasterized.

		// Not all sizes are supported when point antialiasing is enabled. If an unsupported size is
		// requested, the nearest supported size is used. Only size 1.0 is guaranteed to be supported;
		// others depend on the implementation. The range of supported sizes and the size difference
		// between supported sizes within the range can be queried by calling glGet with arguments
		// GL_POINT_SIZE_RANGE and GL_POINT_SIZE_GRANULARITY.

		// The point size specified by glPointSize is always returned when GL_POINT_SIZE is queried.
		// Clamping and rounding for aliased and antialiased points have no effect on the specified value.

		// Non-antialiased point size may be clamped to an implementation-dependent maximum.
		// Although this maximum cannot be queried, it must be no less than the maximum value
		// for antialiased points, rounded to the nearest integer value.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPointSize(size);
		else {
			if (size <= 0.0f)
				throw new GLInvalidValueException("glPointSize(float)");

			gc.state.point.requestedSize = size;

			// round aliased size

			if (size <= MIN_ALIASED_POINT_SIZE)
				gc.state.point.aliasedSize = MIN_ALIASED_POINT_SIZE;
			else if (size >= MAX_ALIASED_POINT_SIZE)
				gc.state.point.aliasedSize = MAX_ALIASED_POINT_SIZE;
			else
				gc.state.point.aliasedSize = (int) (size + 0.5f);

			// clamp smooth size

			if (size <= MIN_POINT_SIZE)
				gc.state.point.smoothSize = MIN_POINT_SIZE;
			else if (size >= MAX_POINT_SIZE)
				gc.state.point.smoothSize = MAX_POINT_SIZE;
			else {
				// choose closest fence post
				final int i = (int) (((size - MIN_POINT_SIZE) / POINT_SIZE_GRANULARITY) + 0.5f);
				gc.state.point.smoothSize = MIN_POINT_SIZE + i * POINT_SIZE_GRANULARITY;
			}
		}
	}

	public final void glLineWidth(final float width) {
		__GL_SETUP_NOT_IN_BEGIN("glLineWidth(float)");

		// The default is 1.0.

		// The glLineWidth function specifies the rasterized width of both aliased and antialiased lines.
		// Using a line width other than 1.0 has different effects, depending on whether line
		// antialiasing is enabled. Line antialiasing is controlled by calling glEnable and glDisable
		// with argument GL_LINE_SMOOTH.

		// If line antialiasing is disabled, the actual width is determined by rounding the supplied
		// width to the nearest integer. (If the rounding results in the value 0, it is as if the
		// line width were 1.) If | ? x | ? | ? y |, i pixels are filled in each column that is
		// rasterized, where i is the rounded value of width. Otherwise, i pixels are filled in each
		// row that is rasterized.

		// If antialiasing is enabled, line rasterization produces a fragment for each pixel square that
		// intersects the region lying within the rectangle having width equal to the current line width,
		// length equal to the actual length of the line, and centered on the mathematical line segment.
		// The coverage value for each fragment is the window coordinate area of the intersection of
		// the rectangular region with the corresponding pixel square. This value is saved and used in
		// the final rasterization step.

		// Not all widths can be supported when line antialiasing is enabled. If an unsupported width
		// is requested, the nearest supported width is used. Only width 1.0 is guaranteed to be supported;
		// others depend on the implementation. The range of supported widths and the size difference
		// between supported widths within the range can be queried by calling glGet with arguments
		// GL_LINE_WIDTH_RANGE and GL_LINE_WIDTH_GRANULARITY.

		// The line width specified by glLineWidth is always returned when GL_LINE_WIDTH is queried.
		// Clamping and rounding for aliased and antialiased lines have no effect on the specified value.

		// Non-antialiased line width may be clamped to an implementation-dependent maximum.
		// Although this maximum cannot be queried, it must be no less than the maximum value for
		// antialiased lines, rounded to the nearest integer value.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glLineWidth(width);
		else {
			if (width <= 0.0f)
				throw new GLInvalidValueException("glLineWidth(float) - The supplied float <= 0.0f");

			//if(width == gc.state.line.width)
			//    return;

			gc.state.line.requestedWidth = width;

			// round aliased width

			if (width <= MIN_ALIASED_LINE_WIDTH)
				gc.state.line.aliasedWidth = MIN_ALIASED_LINE_WIDTH;
			else if (width >= MAX_ALIASED_LINE_WIDTH)
				gc.state.line.aliasedWidth = MAX_ALIASED_LINE_WIDTH;
			else
				gc.state.line.aliasedWidth = (int) (width + 0.5f);

			// clamp smooth width

			if (width <= MIN_LINE_WIDTH)
				gc.state.line.smoothWidth = MIN_LINE_WIDTH;
			else if (width >= MAX_LINE_WIDTH)
				gc.state.line.smoothWidth = MAX_LINE_WIDTH;
			else {
				// choose closest fence post
				final int i = (int) (((width - MIN_LINE_WIDTH) / LINE_WIDTH_GRANULARITY) + 0.5f);
				gc.state.line.smoothWidth = MIN_LINE_WIDTH + i * LINE_WIDTH_GRANULARITY;
			}
		}
	}

	public final void glPolygonMode(final int face, final int mode) {
		__GL_SETUP_NOT_IN_BEGIN("glPolygonMode(int, int)");

		// The glPolygonMode function controls the interpretation of polygons for rasterization.
		// The face parameter describes which polygons mode applies to: front-facing polygons
		// (GL_FRONT), back-facing polygons (GL_BACK), or both (GL_FRONT_AND_BACK).
		// The polygon mode affects only the final rasterization of polygons. In particular,
		// a polygon's vertices are lit and the polygon is clipped and possibly culled before
		// these modes are applied.

		// To draw a surface with filled back-facing polygons and outlined front-facing polygons, call
		// glPolygonMode(GL_FRONT, GL_LINE);

		// Vertices are marked as boundary or nonboundary with an edge flag. Edge flags are generated
		// internally by OpenGL when it decomposes polygons, and they can be set explicitly
		// using glEdgeFlag.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glPolygonMode(face, mode);
		else {
			switch (mode) {
			case GL_POINT: // Polygon vertices that are marked as the start of a boundary edge are drawn
				// as points. Point attributes such as GL_POINT_SIZE and GL_POINT_SMOOTH
				// control the rasterization of the points. Polygon rasterization attributes
				// other than GL_POLYGON_MODE have no effect.
				//DELAY_VALIDATE(__GL_DIRTY_POINT);
				break;
			case GL_LINE: // Boundary edges of the polygon are drawn as line segments. They are treated
				// as connected line segments for line stippling; the line stipple counter
				// and pattern are not reset between segments (see glLineStipple).
				// Line attributes such as GL_LINE_WIDTH and GL_LINE_SMOOTH control the
				// rasterization of the lines. Polygon rasterization attributes other than
				// GL_POLYGON_MODE have no effect.
				//DELAY_VALIDATE(__GL_DIRTY_LINE);
				break;
			case GL_FILL: // The interior of the polygon is filled. Polygon attributes such as
				// GL_POLYGON_STIPPLE and GL_POLYGON_SMOOTH control the rasterization of the
				// polygon.
				// yep, do nothing ! we call DELAY_VALIDATE(__GL_DIRTY_POLYGON) below
				break;
			default:
				throw new GLInvalidEnumException("glPolygonMode(int, int)");
			}

			switch (face) {
			case GL_FRONT:
				gc.state.polygon.frontMode = mode;
				break;
			case GL_BACK:
				gc.state.polygon.backMode = mode;
				break;
			case GL_FRONT_AND_BACK:
				gc.state.polygon.frontMode = mode;
				gc.state.polygon.backMode = mode;
				break;
			default:
				throw new GLInvalidEnumException("glPolygonMode(int, int)");
			}
		}
	}

	public final void glClearDepth(final float z) {
		__GL_SETUP_NOT_IN_BEGIN("glClearDepth(float)");

		// The glClearDepth function specifies the depth value used by glClear to clear the depth buffer.
		// Values specified by glClearDepth are clamped to the range [0,1].

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glClearDepth(z);
		else
			gc.state.depth.clear = GLUtils.clamp(z, 0.0f, 1.0f);
	}

	public final void glDepthFunc(final int zf) {
		__GL_SETUP_NOT_IN_BEGIN("glDepthFunc(int)");

		// The glDepthFunc function specifies the function used to compare each incoming pixel z value
		// with the z value present in the depth buffer. The comparison is performed only if depth
		// testing is enabled. (See glEnable with the argument GL_DEPTH_TEST.)

		// Initially, depth testing is disabled.

		// GL_NEVER     Never passes.
		// GL_LESS      Passes if the incoming z value is less than the stored z value. This is the default value.
		// GL_LEQUAL    Passes if the incoming z value is less than or equal to the stored z value.
		// GL_EQUAL     Passes if the incoming z value is equal to the stored z value.
		// GL_GREATER   Passes if the incoming z value is greater than the stored z value.
		// GL_NOTEQUAL  Passes if the incoming z value is not equal to the stored z value.
		// GL_GEQUAL    Passes if the incoming z value is greater than or equal to the stored z value.
		// GL_ALWAYS    Always passes.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glDepthFunc(zf);
		else {
			if (zf < GL_NEVER || zf > GL_ALWAYS)
				throw new GLInvalidEnumException("glDepthFunc(int)");

			gc.state.depth.testFunc = zf;
		}
	}

	/**
	 * Specifies whether the depth buffer is enabled for writing.
	 * If flag is true, depth-buffer writing is disabled. Otherwise, it is enabled.
	 * Initially, depth-buffer writing is enabled.
	 *
	 * @param enabled
	 */
	public final void glDepthMask(final boolean enabled) {
		__GL_SETUP_NOT_IN_BEGIN("glDepthMask(boolean)");

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glDepthMask(enabled);
		else
			gc.state.depth.writeEnable = enabled;
	}

	public final void glDepthRange(final float zNear, final float zFar) {
		__GL_SETUP_NOT_IN_BEGIN("glDepthRange(float, float)");

		// znear
		// The mapping of the near clipping plane to window coordinates. The default value is 0.

		// zfar
		// The mapping of the far clipping plane to window coordinates. The default value is 1.

		// After clipping and division by w, z-coordinates range from –1.0 to 1.0, corresponding to
		// the near and far clipping planes. The glDepthRange function specifies a linear mapping of
		// the normalized z-coordinates in this range to window z-coordinates. Regardless of the
		// actual depth buffer implementation, window coordinate depth values are treated as though
		// they range from 0.0 through 1.0 (like color components). Thus, the values accepted by
		// glDepthRange are both clamped to this range before they are accepted.

		// The default mapping of 0,1 maps the near plane to 0 and the far plane to 1.
		// With this mapping, the depth-buffer range is fully utilized.

		// It is not necessary that znear be less than zfar. Reverse mappings such as 1,0 are
		// acceptable.

		gc.state.viewport.setDepthRange(zNear, zFar);
	}

	/////////////////////// Clipping ///////////////////////

	public final void glClipPlane(int plane, final float[] equation) {
		__GL_SETUP_NOT_IN_BEGIN("glClipPlane(int, float[])");

		// Geometry is always clipped against the boundaries of a six-plane frustum in x, y, and z.
		// The glClipPlane function allows the specification of additional planes, not necessarily
		// perpendicular to the x- , y- , or z-axis, against which all math is clipped.
		// Up to GL_MAX_CLIP_PLANES planes can be specified, where GL_MAX_CLIP_PLANES is at least
		// six in all implementations. Because the resulting clipping region is the intersection of
		// the defined half-spaces, it is always convex.

		// The glClipPlane function specifies a half-space using a four-component plane equation.
		// When you call glClipPlane, equation is transformed by the inverse of the modelview matrix
		// and stored in the resulting eye coordinates. Subsequent changes to the modelview matrix
		// have no effect on the stored plane-equation components. If the dot product of the eye
		// coordinates of a vertices with the stored plane equation components is positive or zero,
		// the vertices is in with respect to that clipping plane. Otherwise, it is out.

		// Use the glEnable and glDisable functions to enable and disable clipping planes.
		// Call clipping planes with the argument GL_CLIP_PLANEi, where i is the plane number.

		// By default, all clipping planes are defined as (0,0,0,0) in eye coordinates and are disabled.

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glClipPlane(plane, equation);
		else {
			if (plane < GL_CLIP_PLANE0)
				throw new GLInvalidEnumException("glClipPlane(int, float[])");

			plane -= GL_CLIP_PLANE0; // It is always the case that GL_CLIP_PLANEi = GL_CLIP_PLANE0 + i.

			if (plane >= MAX_USER_CLIP_PLANES)
				throw new GLInvalidEnumException("glClipPlane(int, double[])");

			// Project user clip plane into eye space.

			final GLTransform tr = gc.transform.modelView;

			if (tr.updateInverseTranspose)
				tr.computeInverseTranspose(gc);

			vf.set(equation);
			GLMatrix4f.transform(vf, gc.transform.modelView.inverseTranspose, gc.state.transform.eyeClipPlanes[plane]);
		}
	}

	/////////////////////// Evaluators ///////////////////////

	public final void glMap2f(final int type, final float u1, final float u2, final int uStride, final int uOrder,
			final float v1, final float v2, final int vStride, final int vOrder, final float[][][] points) {
		__GL_SETUP_NOT_IN_BEGIN("glMap2f(int, float, float, int, int, float, float, int, int, float[][][])");

		final GLEvalMap2 ev = gc.pipeline.evaluator.setUpMap2(type, uOrder, vOrder, u1, u2, v1, v2);

		if (ev == null)
			return;

		if (uStride < ev.dimension)
			throw new GLInvalidValueException(
					"glMap2f(int, float, float, int, int, float, float, int, int, float[][][]");

		if (vStride < ev.dimension)
			throw new GLInvalidValueException(
					"glMap2f(int, float, float, int, int, float, float, int, int, float[][][]");

		ev.fillMap2f(uOrder, vOrder, uStride, vStride, points);
	}

	public final void glMapGrid2f(final int nu, final float u0, final float u1, final int nv, final float v0,
			final float v1) {
		__GL_SETUP_NOT_IN_BEGIN("glMapGrid2f(int, float, float, int, float, float)");

		if (nu < 1 || nv < 1)
			throw new GLInvalidValueException("glMapGrid2f(int, float, float, int, float, float)");

		gc.state.evaluator.u2.start = u0;
		gc.state.evaluator.u2.finish = u1;
		gc.state.evaluator.u2.n = nu;
		gc.state.evaluator.v2.start = v0;
		gc.state.evaluator.v2.finish = v1;
		gc.state.evaluator.v2.n = nv;
	}

	public final void glEvalCoord2f(final float u, final float v) {
		gc.pipeline.evaluator.doEvalCoord2(this, u, v, new float[4]);
	}

	public final void glEvalMesh2(final int mode, final int lowU, final int highU, final int lowV, final int highV) {
		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glEvalMesh2(int, int, int, int, int)");

		switch (mode) {
		case GL_FILL:
			gc.pipeline.evaluator.evalMesh2Fill(this, lowU, lowV, highU, highV);
			break;
		case GL_LINE:
			gc.pipeline.evaluator.evalMesh2Line(this, lowU, lowV, highU, highV);
			break;
		case GL_POINT:
			gc.pipeline.evaluator.evalMesh2Point(this, lowU, lowV, highU, highV);
			break;
		default:
			throw new GLInvalidEnumException("glEvalMesh2(int, int, int, int, int)");
		}
	}

	////////////////////////////// Picking ////////////////////////////////

	public final void glSelectBuffer(final int bufferLength, final int[] buffer) {
		__GL_SETUP_NOT_IN_BEGIN("glSelectBuffer(int, int[])");
		gc.pipeline.select.glSelectBuffer(bufferLength, buffer);
	}

	public final void glInitNames() {
		__GL_SETUP_NOT_IN_BEGIN("glInitNames()");
		gc.pipeline.select.glInitNames();
	}

	public final void glLoadName(final int name) {
		__GL_SETUP_NOT_IN_BEGIN("glLoadName(int)");
		gc.pipeline.select.glLoadName(name);
	}

	public final void glPopName() {
		__GL_SETUP_NOT_IN_BEGIN("glPopName()");
		gc.pipeline.select.glPopName();
	}

	public final void glPushName(final int name) {
		__GL_SETUP_NOT_IN_BEGIN("glPushName(int)");
		gc.pipeline.select.glPushName(name);
	}

	/////////////////////// Feedback ///////////////////////

	public final void glFeedbackBuffer(final int bufferLength, final int type, final float[] buffer) {
		__GL_SETUP_NOT_IN_BEGIN("glFeedbackBuffer(int, int, float[])");

		if ((type < GL_2D) || (type > GL_4D_COLOR_TEXTURE))
			throw new GLInvalidEnumException("glFeedbackBuffer(int, int, float[])");

		if (bufferLength < 0)
			throw new GLInvalidValueException("glFeedbackBuffer(int, int, float[])");

		if (gc.renderMode == GL_FEEDBACK)
			throw new GLInvalidOperationException("glFeedbackBuffer(int, int, float[])");

		gc.pipeline.feedback.setBuffer(bufferLength, type, buffer);
	}

	/////////////////////// Accumulation  ///////////////////////

	public final void glClearAccum(final float r, final float g, final float b, final float a) {
		__GL_SETUP_NOT_IN_BEGIN("glClearAccum(float, float, float, float)");

		// The glClearAccum function specifies the red, green, blue, and alpha values used by
		// glClear to clear the accum buffers. Values specified by glClearAccum are
		// clamped to the range [-1,1].

		if (gc.pipeline.displayList.currentList != 0)
			gc.pipeline.displayList.glClearAccum(r < -1.0f ? -1.0f : (r > 1.0f ? 1.0f : r), g < -1.0f ? -1.0f
					: (g > 1.0f ? 1.0f : g), b < -1.0f ? -1.0f : (b > 1.0f ? 1.0f : b), a < -1.0f ? -1.0f
					: (a > 1.0f ? 1.0f : a));
		else
			gc.state.accum.clear.set(r < -1.0f ? -1.0f : (r > 1.0f ? 1.0f : r), g < -1.0f ? -1.0f : (g > 1.0f ? 1.0f
					: g), b < -1.0f ? -1.0f : (b > 1.0f ? 1.0f : b), a < -1.0f ? -1.0f : (a > 1.0f ? 1.0f : a));
	}

	public final void glAccum(final int op, final float value) {
		// The accumulation buffer operation. The accepted symbolic constants are:

		// GL_ACCUM Obtains R, G, B, and A values from the buffer currently selected for
		// reading (see glReadBuffer). Each component value is divided by 2n– 1,
		// where n is the number of bits allocated to each color component in the currently
		// selected buffer. The result is a floating-point value in the range [0,1],
		// which is multiplied by value and added to the corresponding pixel component in
		// the accumulation buffer, thereby updating the accumulation buffer.

		// GL_LOAD  Similar to GL_ACCUM, except that the current value in the accumulation buffer
		// is not used in the calculation of the new value. That is, the R, G, B, and A values
		// from the currently selected buffer are divided by 2n– 1, multiplied by value,
		// and then stored in the corresponding accumulation buffer cell, overwriting the
		// current value.

		// GL_ADD  Adds value to each R, G, B, and A in the accumulation buffer.

		// GL_MULT  Multiplies each R, G, B, and A in the accumulation buffer by value
		// and returns the scaled component to its corresponding accumulation buffer location.

		// GL_RETURN  Transfers accumulation buffer values to the color buffer or buffers
		// currently selected for writing. Each R, G, B, and A component is multiplied by value,
		// then multiplied by 2n– 1, clamped to the range [0, 2n – 1 ], and stored in the
		// corresponding display buffer cell. The only fragment operations that are applied to
		// this transfer are pixel ownership, scissor, dithering, and color writemasks.

		// A floating-point value used in the accumulation buffer operation. The op parameter
		// determines how value is used.

		// The accumulation buffer is an extended-range color buffer. Images are not rendered
		// into it. Rather, images rendered into one of the color buffers are added to the
		// contents of the accumulation buffer after rendering. You can create effects such
		// as antialiasing (of points, lines, and polygons), motion blur, and depth of
		// field by accumulating images generated with different transformation matrices.

		// Each pixel in the accumulation buffer consists of red, green, blue, and alpha
		// values. The number of bits per component in the accumulation buffer depends
		// on the implementation. You can examine this number by calling glGetIntegerv
		// four times, with the arguments GL_ACCUM_RED_BITS, GL_ACCUM_GREEN_BITS,
		// GL_ACCUM_BLUE_BITS, and GL_ACCUM_ALPHA_BITS, respectively.
		// Regardless of the number of bits per component, however, the range of values
		// stored by each component is [ – 1, 1]. The accumulation buffer pixels are
		// mapped one-to-one with framebuffer pixels.

		// The glAccum function operates on the accumulation buffer. The first argument,
		// op, is a symbolic constant that selects an accumulation buffer operation.
		// The second argument, value, is a floating-point value to be used in that operation.
		// Five operations are specified: GL_ACCUM, GL_LOAD, GL_ADD, GL_MULT, and GL_RETURN.

		// All accumulation buffer operations are limited to the area of the current scissor
		// box and are applied identically to the red, green, blue, and alpha components of
		// each pixel. The contents of an accumulation buffer pixel component are undefined
		// if the glAccum operation results in a value outside the range [ – 1,1].

		// To clear the accumulation buffer, use the glClearAccum function to specify
		// R, G, B, and A values to set it to, and issue a glClear function with the
		// accumulation buffer enabled.

		// Only those pixels within the current scissor box are updated by any
		// glAccum operation.

		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glAccum(int, float)");

		if (!gc.haveAccumBuffer || !gc.rgb)
			throw new GLInvalidOperationException("glAccum(int, float)");

		if (gc.renderMode == GL_RENDER) {
			final GLAccumBuffer buffer = gc.frameBuffer.accumBuffer;

			switch (op) {
			case GL_ACCUM:
				buffer.doAccumulate(value);
				break;
			case GL_LOAD:
				buffer.doLoad(value);
				break;
			case GL_RETURN:
				buffer.doReturn(value);
				break;
			case GL_MULT:
				buffer.doMult(value);
				break;
			case GL_ADD:
				buffer.doAdd(value);
				break;
			default:
				throw new GLInvalidEnumException("glAccum(int, float)");
			}
		}
	}

	/////////////////////// Display Lists ///////////////////////

	public final int glGenLists(final int range) {
		__GL_SETUP_NOT_IN_BEGIN("glGenLists(int)");

		if (range < 0)
			throw new GLInvalidValueException("glGenLists(int)");

		if (range == 0)
			return 0;

		return gc.pipeline.displayList.glGenLists(range);
	}

	public final void glNewList(final int list, final int mode) {
		__GL_SETUP_NOT_IN_BEGIN("glNewList(int, int)");

		switch (mode) {
		case GL_COMPILE:
		case GL_COMPILE_AND_EXECUTE:
			break;
		default:
			throw new GLInvalidEnumException("glNewList(int, int)");
		}

		if (list == 0)
			throw new GLInvalidValueException("glNewList(int, int)");

		if (gc.pipeline.displayList.currentList != 0)
			throw new GLInvalidOperationException("glNewList(int, int)");

		gc.pipeline.displayList.glNewList(list, mode);
	}

	public final void glEndList() {
		__GL_SETUP_NOT_IN_BEGIN("glEndList()");

		if (gc.pipeline.displayList.currentList == 0)
			throw new GLInvalidOperationException("glEndList()");

		gc.pipeline.displayList.glEndList(this);
	}

	public final void glCallList(final int list) {
		// Invoking the glCallList function begins execution of the named display list.
		// The functions saved in the display list are executed in order, just as if you
		// called them without using a display list. If list has not been defined as a
		// display list, glCallList is ignored.

		// The glCallList function can appear inside a display list. To avoid the
		// possibility of infinite recursion resulting from display lists calling one
		// another, a limit is placed on the nesting level of display lists during
		// display-list execution. This limit is at least 64, however it depends on the
		// implementation.

		// The OpenGL state is not saved and restored across a call to glCallList.
		// Thus, changes made to the OpenGL state during the execution of a display
		// list remain after execution of the display list is completed.
		// To preserve the OpenGL state across glCallList calls, use glPushAttrib,
		// glPopAttrib, glPushMatrix, and glPopMatrix.

		// You can execute display lists between a call to glBegin and the corresponding
		// call to glEnd, as long as the display list includes only functions that are
		// allowed in this interval.

		if (list == 0)
			throw new GLInvalidValueException("glNewList(int, int)");

		gc.pipeline.displayList.glCallList(list, this);
	}

	public final void glDeleteLists(final int list, final int range) {
		__GL_SETUP_NOT_IN_BEGIN("glDeleteLists(int, int)");

		// list The integer name of the first display list to delete.
		// range The number of display lists to delete.

		// The glDeleteLists function causes a contiguous group of display lists to be
		// deleted. The list parameter is the name of the first display list to be deleted,
		// and range is the number of display lists to delete. All display lists d with
		// list ? d ? list + range – 1 are deleted.

		// All storage locations allocated to the specified display lists are freed, and
		// the names are available for reuse at a later time. Names within the range that
		// do not have an associated display list are ignored. If range is zero, nothing
		// happens.

		if (range < 0)
			throw new GLInvalidValueException("glDeleteLists(int, int)");

		if (range == 0)
			return;

		gc.pipeline.displayList.glDeleteLists(list, range);
	}

	/////////////////////// Vertex Arrays ///////////////////////

	public final void glVertexPointer(final int size, final int type, final int stride, final float[] pointer) {
		__GL_SETUP_NOT_IN_BEGIN("glVertexPointer(int, int, int, float[])");

		// TODO - what about inside a display list ???

		if (stride < 0 || size < 2 || size > 4)
			throw new GLInvalidValueException("glVertexPointer(int, int, int, float[])");

		if (type != GL_FLOAT)
			throw new GLInvalidEnumException("glVertexPointer(int, int, int, float[])");

		interleavedArray.glVertexPointer(size, type, stride, pointer);
	}

	public final void glNormalPointer(final int type, final int stride, final float[] pointer) {
		__GL_SETUP_NOT_IN_BEGIN("glNormalPointer(int, int, float[])");

		// TODO - what about inside a display list ???

		if (stride < 0)
			throw new GLInvalidValueException("glNormalPointer(int, int, float[])");

		if (type != GL_FLOAT)
			throw new GLInvalidEnumException("glNormalPointer(int, int, float[])");

		interleavedArray.glNormalPointer(type, stride, pointer);
	}

	public final void glColorPointer(final int size, final int type, final int stride, final float[] pointer) {
		__GL_SETUP_NOT_IN_BEGIN("glColorPointer(int, int, int, float[])");

		// TODO - what about inside a display list ???

		if (stride < 0 || size < 3 || size > 4)
			throw new GLInvalidValueException("glColorPointer(int, int, int, float[])");

		if (type != GL_FLOAT)
			throw new GLInvalidEnumException("glColorPointer(int, int, int, float[])");

		interleavedArray.glColorPointer(size, type, stride, pointer);
	}

	public final void glArrayElement(final int i) {
		// i - An index in the enabled arrays.

		// Use the glArrayElement function within glBegin and glEnd pairs to specify vertex and
		// attribute demos.data for point, line, and polygon primitives. The glArrayElement function
		// specifies the demos.data for a single vertex using vertex and attribute demos.data located at the
		// index of the enabled vertex arrays.

		// You can use glArrayElement to construct primitives by indexing vertex demos.data, rather than
		// by streaming through arrays of demos.data in first-to-lastTime order. Because glArrayElement
		// specifies a single vertex only, you can explicitly specify attributes for individual
		// primitives. For example, you can set a single normal for each individual triangle.

		// When you include calls to glArrayElement in display lists, the necessary array demos.data,
		// determined by the array pointers and enable values, is entered in the display list also.
		// Array pointer and enable values are determined when display lists are created, not when
		// display lists are executed.

		// You can read and cache static array demos.data at any time with glArrayElement.
		// When you modify the elements of a static array without specifying the array again,
		// the results of any subsequent calls to glArrayElement are undefined.

		// When you call glArrayElement without first calling glEnableClientState(GL_VERTEX_ARRAY),
		// no drawing occurs, but the attributes corresponding to enabled arrays are modified.
		// Although no error is generated when you specify an array within glBegin and glEnd pairs,
		// the results are undefined.

		if (i < 0)
			throw new GLInvalidValueException("glArrayElement(int)");

		// TODO - what about inside a display list ???

		interleavedArray.glArrayElement(i, this);
	}

	public final void glDrawArrays(final int mode, final int first, final int count) {
		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glDrawArrays(int, int, int)");

		// check that first and count are positive

		if (first < 0 || count < 0)
			throw new GLInvalidValueException("glDrawArrays(int, int, int)");

		if (mode < GL_POINTS || mode > GL_POLYGON)
			throw new GLInvalidEnumException("glDrawArrays(int, int, int)");

		if (count == 0)
			return;

		// TODO - what about inside a display list ???

		interleavedArray.glDrawArrays(mode, first, count, this);
	}

	public void glDrawElements(int mode, int count, int type, short[] indices) {
		interleavedArray.glDrawElements(mode, count, type, indices, this);
	}

	/////////////////////// Stenciling ///////////////////////

	public final void glClearStencil(final int s) {
		__GL_SETUP_NOT_IN_BEGIN("glClearStencil(int)");

		// The glClearStencil function specifies the index used by glClear to clear the stencil
		// buffer. The s parameter is masked with 2m – 1, where m is the number of bits in the
		// stencil buffer.

		final int __GL_MAX_STENCIL_VALUE = ((1 << gc.stencilSize) - 1);
		gc.state.stencil.clear = s & __GL_MAX_STENCIL_VALUE;
	}

	public final void glStencilFunc(final int func, int ref, final int mask) {
		__GL_SETUP_NOT_IN_BEGIN("glStencilFunc(int, int, int)");

		if (func < GL_NEVER || func > GL_ALWAYS)
			throw new GLInvalidEnumException("glStencilFunc(int, int, int)");

		final int __GL_MAX_STENCIL_VALUE = ((1 << gc.stencilSize) - 1);

		// The ref parameter is clamped to the range [0, 2n – 1], where n is the number of
		// bitplanes in the stencil buffer.

		if (ref < 0)
			ref = 0;

		if (ref > __GL_MAX_STENCIL_VALUE)
			ref = __GL_MAX_STENCIL_VALUE;

		// Stenciling, like z-buffering, enables and disables drawing on a per-pixel basis.
		// You draw into the stencil planes using OpenGL drawing primitives, then render
		// geometry and images, using the stencil planes to valueMask out portions of the screen.
		// Stenciling is typically used in multipass rendering algorithms to achieve special
		// effects, such as decals, outlining, and constructive solid geometry rendering.

		// The stencil test conditionally eliminates a pixel based on the outcome of a comparison
		// between the reference value and the value in the stencil buffer.
		// The test is enabled by glEnable and glDisable with argument GL_STENCIL.
		// Actions taken based on the outcome of the stencil test are specified with glStencilOp.

		// The func parameter is a symbolic constant that determines the stencil comparison function.
		// It accepts one of the eight values shown above. The ref parameter is an integer reference
		// value that is used in the stencil comparison. It is clamped to the range [0,2n – 1],
		// where n is the number of bitplanes in the stencil buffer.
		// The valueMask parameter is bitwise ANDed with both the reference value and the stored
		// stencil value, with the ANDed values participating in the comparison.

		// If stencil represents the value stored in the corresponding stencil buffer location,
		// the preceding list shows the effect of each comparison function that can be specified
		// by func. Only if the comparison succeeds is the pixel passed through to the next stage
		// in the rasterization process (see glStencilOp). All tests treat stencil values as
		// unsigned integers in the range [0,2n – 1], where n is the number of bitplanes in the
		// stencil buffer.

		// Initially, the stencil test is disabled. If there is no stencil buffer, no stencil
		// modification can occur and it is as if the stencil test always passes.

		gc.state.stencil.testFunc = func;
		gc.state.stencil.reference = ref;
		gc.state.stencil.valueMask = mask & __GL_MAX_STENCIL_VALUE;
	}

	public final void glStencilMask(final int sm) {
		__GL_SETUP_NOT_IN_BEGIN("glStencilMask(int)");

		// A bit valueMask to enable and disable writing of individual bits in the stencil planes.
		// Initially, the valueMask is all ones.

		final int __GL_MAX_STENCIL_VALUE = ((1 << gc.stencilSize) - 1);
		gc.state.stencil.writeMask = sm & __GL_MAX_STENCIL_VALUE;

		// The glStencilMask function controls the writing of individual bits in the stencil planes.
		// The least significant n bits of valueMask, where n is the number of bits in the stencil
		// buffer, specify a valueMask. Wherever a one appears in the valueMask,
		// the corresponding bit in the stencil buffer is made writable. Where a zero appears,
		// the bit is write-protected. Initially, all bits are enabled for writing.
	}

	public final void glStencilOp(final int fail, final int zfail, final int zpass) {
		__GL_SETUP_NOT_IN_BEGIN("glStencilOp(int, int, int)");

		// zfail - Stencil action when the stencil test passes, but the depth test fails.
		// Accepts the same symbolic constants as fail.

		// zpass - Stencil action when both the stencil test and the depth test pass, or
		// when the stencil test passes and either there is no depth buffer or depth testing
		// is not enabled. Accepts the same symbolic constants as fail.

		// Stenciling, like z-buffering, enables and disables drawing on a per-pixel basis.
		// You draw into the stencil planes using OpenGL drawing primitives, then render
		// geometry and images, using the stencil planes to valueMask out portions of the screen.
		// Stenciling is typically used in multipass rendering algorithms to achieve special
		// effects, such as decals, outlining, and constructive solid geometry rendering.

		// The stencil test conditionally eliminates a pixel based on the outcome of a comparison
		// between the value in the stencil buffer and a reference value.
		// The test is enabled with glEnable and glDisable calls with argument GL_STENCIL,
		// and controlled with glStencilFunc.

		// The glStencilOp function takes three arguments that indicate what happens to the
		// stored stencil value while stenciling is enabled. If the stencil test fails, no
		// change is made to the pixel's color or depth buffers, and fail specifies what happens
		// to the stencil buffer contents.

		// Stencil buffer values are treated as unsigned integers. When incremented and
		// decremented, values are clamped to 0 and 2n – 1, where n is the value returned by
		// querying GL_STENCIL_BITS.

		// The other two arguments to glStencilOp specify stencil buffer actions should subsequent
		// depth buffer tests succeed (zpass) or fail (zfail). (See glDepthFunc.)
		// They are specified using the same six symbolic constants as fail.
		// Note that zfail is ignored when there is no depth buffer, or when the depth buffer
		// is not enabled. In these cases, fail and zpass specify stencil action when the
		// stencil test fails and passes, respectively.

		// Initially the stencil test is disabled. If there is no stencil buffer, no stencil
		// modification can occur and it is as if the stencil tests always pass, regardless of
		// any call to glStencilOp.

		switch (fail) {
		case GL_KEEP:
		case GL_ZERO:
		case GL_REPLACE:
		case GL_INCR:
		case GL_DECR:
		case GL_INVERT:
			break;
		default:
			throw new GLInvalidEnumException("glStencilOp(int, int, int)");
		}

		switch (zfail) {
		case GL_KEEP:
		case GL_ZERO:
		case GL_REPLACE:
		case GL_INCR:
		case GL_DECR:
		case GL_INVERT:
			break;
		default:
			throw new GLInvalidEnumException("glStencilOp(int, int, int)");
		}

		switch (zpass) {
		case GL_KEEP:
		case GL_ZERO:
		case GL_REPLACE:
		case GL_INCR:
		case GL_DECR:
		case GL_INVERT:
			break;
		default:
			throw new GLInvalidEnumException("glStencilOp(int, int, int)");
		}

		gc.state.stencil.fail = fail;
		gc.state.stencil.depthFail = zfail;
		gc.state.stencil.depthPass = zpass;
	}

	/////////////////////// Blending ///////////////////////

	public final void glBlendFunc(final int sf, final int df) {
		__GL_SETUP_NOT_IN_BEGIN("glBlendFunc(int, int)");

		// In RGB mode, pixels can be drawn using a function that blends the incoming (source)
		// RGBA values with the RGBA values that are already in the framebuffer
		// (the destination values). By default, blending is disabled.
		// Use glEnable and glDisable with the GL_BLEND argument to enable and disable blending.

		// When enabled, glBlendFunc defines the operation of blending.
		// The sfactor parameter specifies which of nine methods is used to scale the
		// source color components. The dfactor parameter specifies which of eight methods
		// is used to scale the destination color components. The eleven possible methods
		// are described in the following table. Each method defines four scale factors,
		// one each for red, green, blue, and alpha.

		// In the table and in subsequent equations, source and destination color components
		// are referred to as (RS,GS,BS,AS) and (Rd,Gd,Bd,Ad). They are understood to have
		// integer values between zero and (kR,kG,kR,kA), where

		// kR = 2mR – 1
		// kG = 2mG – 1
		// kB = 2mB – 1
		// kA = 2mA – 1

		// and (mR ,mG ,mB ,mA) is the number of red, green, blue, and alpha bitplanes.

		// Source and destination scale factors are referred to as (sR ,sG ,sB ,sA)
		// and (dR ,dG ,dB ,dA). The scale factors described in the table, denoted
		// (fR ,fG ,fB ,fA), represent either source or destination factors.
		// All scale factors have range [0,1].

		// In the table,

		// i = min (AS,kA– AD) / /kA

		// To determine the blended RGBA values of a pixel when drawing in RGB mode,
		// the system uses the following equations:

		// R (d) = min(kR,RssR+RddR)
		// G (d) = min(kG,GssG+GddG)
		// B (d) = min(kB,BssB+BddB)
		// A (d) = min(kA,AssA+AddA)

		// Despite the apparent precision of the above equations, blending arithmetic is not
		// exactly specified, because blending operates with imprecise integer color values.
		// However, a blend factor that should be equal to one is guaranteed not to modify
		// its multiplicand, and a blend factor equal to zero reduces its multiplicand to zero.
		// Thus, for example, when sfactor is GL_SRC_ALPHA, dfactor is GL_ONE_MINUS_SRC_ALPHA,
		// and As is equal to kA, the equations reduce to simple replacement:

		// Rd= Rs
		// Gd= Gs
		// Bd= Bs
		// Ad= As

		// Transparency is best implemented using glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
		// with primitives sorted from farthest to nearest. Note that this transparency calculation
		// does not require the presence of alpha bitplanes in the framebuffer.

		// You can also use glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) for rendering
		// antialiased points and lines in arbitrary order.

		// To optimize polygon antialiasing, use glBlendFunc(GL_SRC_ALPHA_SATURATE, GL_ONE)
		// with polygons sorted from nearest to farthest. (See the GL_POLYGON_SMOOTH argument in
		// glEnable for information on polygon antialiasing.) Destination alpha bitplanes,
		// which must be present for this blend function to operate correctly, store the
		// accumulated coverage.

		// Incoming (source) alpha is a material opacity, ranging from 1.0 (KA), representing
		// complete opacity, to 0.0 (0), representing complete transparency.

		// When you enable more than one color buffer for drawing, each enabled buffer is blended
		// separately, and the contents of the buffer is used for destination color.
		// (See glDrawBuffer.)

		// Blending affects only RGB rendering. It is ignored by color-index renderers.

		switch (sf) {
		case GL_ZERO:
		case GL_ONE:
		case GL_DST_COLOR:
		case GL_ONE_MINUS_DST_COLOR:
		case GL_SRC_ALPHA:
		case GL_ONE_MINUS_SRC_ALPHA:
		case GL_DST_ALPHA:
		case GL_ONE_MINUS_DST_ALPHA:
		case GL_SRC_ALPHA_SATURATE:
		case GL_CONSTANT_COLOR:
		case GL_ONE_MINUS_CONSTANT_COLOR:
		case GL_CONSTANT_ALPHA:
		case GL_ONE_MINUS_CONSTANT_ALPHA:
			break;
		default:
			throw new GLInvalidEnumException("glBlendFunc(int, int)");
		}

		switch (df) {
		case GL_ZERO:
		case GL_ONE:
		case GL_SRC_COLOR:
		case GL_ONE_MINUS_SRC_COLOR:
		case GL_SRC_ALPHA:
		case GL_ONE_MINUS_SRC_ALPHA:
		case GL_DST_ALPHA:
		case GL_ONE_MINUS_DST_ALPHA:
		case GL_CONSTANT_COLOR:
		case GL_ONE_MINUS_CONSTANT_COLOR:
		case GL_CONSTANT_ALPHA:
		case GL_ONE_MINUS_CONSTANT_ALPHA:
			break;
		default:
			throw new GLInvalidEnumException("glBlendFunc(int, int)");
		}

		gc.state.colorBuffer.blendSrcRGB = gc.state.colorBuffer.blendSrcA = sf;
		gc.state.colorBuffer.blendDstRGB = gc.state.colorBuffer.blendDstA = df;
	}

	public final void glBlendFuncSeparateEXT(final int sfactorRGB, final int dfactorRGB, final int sfactorA,
			final int dfactorA) {
		__GL_SETUP_NOT_IN_BEGIN("glBlendFuncSeparateEXT(int, int, int, int)");

		switch (sfactorRGB) {
		case GL_SRC_COLOR:
		case GL_ONE_MINUS_SRC_COLOR:
		case GL_ZERO:
		case GL_ONE:
		case GL_DST_COLOR:
		case GL_ONE_MINUS_DST_COLOR:
		case GL_SRC_ALPHA:
		case GL_ONE_MINUS_SRC_ALPHA:
		case GL_DST_ALPHA:
		case GL_ONE_MINUS_DST_ALPHA:
		case GL_SRC_ALPHA_SATURATE:
		case GL_CONSTANT_COLOR:
		case GL_ONE_MINUS_CONSTANT_COLOR:
		case GL_CONSTANT_ALPHA:
		case GL_ONE_MINUS_CONSTANT_ALPHA:
			break;
		default:
			throw new GLInvalidEnumException("glBlendFuncSeparateEXT(int, int, int, int)");
		}

		switch (dfactorRGB) {
		case GL_DST_COLOR:
		case GL_ONE_MINUS_DST_COLOR:
		case GL_ZERO:
		case GL_ONE:
		case GL_SRC_COLOR:
		case GL_ONE_MINUS_SRC_COLOR:
		case GL_SRC_ALPHA:
		case GL_ONE_MINUS_SRC_ALPHA:
		case GL_DST_ALPHA:
		case GL_ONE_MINUS_DST_ALPHA:
		case GL_CONSTANT_COLOR:
		case GL_ONE_MINUS_CONSTANT_COLOR:
		case GL_CONSTANT_ALPHA:
		case GL_ONE_MINUS_CONSTANT_ALPHA:
			break;
		default:
			throw new GLInvalidEnumException("glBlendFuncSeparateEXT(int, int, int, int)");
		}

		switch (sfactorA) {
		case GL_SRC_COLOR:
		case GL_ONE_MINUS_SRC_COLOR:
		case GL_ZERO:
		case GL_ONE:
		case GL_DST_COLOR:
		case GL_ONE_MINUS_DST_COLOR:
		case GL_SRC_ALPHA:
		case GL_ONE_MINUS_SRC_ALPHA:
		case GL_DST_ALPHA:
		case GL_ONE_MINUS_DST_ALPHA:
		case GL_SRC_ALPHA_SATURATE:
		case GL_CONSTANT_COLOR:
		case GL_ONE_MINUS_CONSTANT_COLOR:
		case GL_CONSTANT_ALPHA:
		case GL_ONE_MINUS_CONSTANT_ALPHA:
			break;
		default:
			throw new GLInvalidEnumException("glBlendFuncSeparateEXT(int, int, int, int)");
		}

		switch (dfactorA) {
		case GL_DST_COLOR:
		case GL_ONE_MINUS_DST_COLOR:
		case GL_ZERO:
		case GL_ONE:
		case GL_SRC_COLOR:
		case GL_ONE_MINUS_SRC_COLOR:
		case GL_SRC_ALPHA:
		case GL_ONE_MINUS_SRC_ALPHA:
		case GL_DST_ALPHA:
		case GL_ONE_MINUS_DST_ALPHA:
		case GL_CONSTANT_COLOR:
		case GL_ONE_MINUS_CONSTANT_COLOR:
		case GL_CONSTANT_ALPHA:
		case GL_ONE_MINUS_CONSTANT_ALPHA:
			break;
		default:
			throw new GLInvalidEnumException("glBlendFuncSeparateEXT(int, int, int, int)");
		}

		gc.state.colorBuffer.blendSrcRGB = sfactorRGB;
		gc.state.colorBuffer.blendSrcA = sfactorA;
		gc.state.colorBuffer.blendDstRGB = dfactorRGB;
		gc.state.colorBuffer.blendDstA = dfactorA;
	}

	public final void glBlendEquation(final int mode) {
		__GL_SETUP_NOT_IN_BEGIN("glBlendEquation(int)");

		switch (mode) {
		case GL_FUNC_ADD_EXT:
		case GL_FUNC_SUBTRACT_EXT:
		case GL_FUNC_REVERSE_SUBTRACT_EXT:
			break;
		default:
			throw new GLInvalidEnumException("glBlendEquation(int)");
		}

		gc.state.colorBuffer.blendEquation = mode;
	}

	public final void glBlendColor(final float r, final float g, final float b, final float a) {
		__GL_SETUP_NOT_IN_BEGIN("glBlendColor(float, float, float, float)");

		gc.state.colorBuffer.blendColor.set(r < 0.0f ? 0.0f : (r > 1.0f ? 1.0f : r), g < 0.0f ? 0.0f : (g > 1.0f ? 1.0f
				: g), b < 0.0f ? 0.0f : (b > 1.0f ? 1.0f : b), a < 0.0f ? 0.0f : (a > 1.0f ? 1.0f : a));
	}

	/////////////////////// Fog ///////////////////////

	public final void glFogi(final int p, final int i) {
		// Accept only enumerants that correspond to single values

		switch (p) {
		case GL_FOG_DENSITY:
		case GL_FOG_END:
		case GL_FOG_START:
		case GL_FOG_INDEX:
		case GL_FOG_MODE:
			glFogfv(p, new float[] { i });
			break;
		default:
			throw new GLInvalidEnumException("glFogi(int, int)");
		}
	}

	public final void glFogf(final int p, final float f) {
		// Accept only enumerants that correspond to single values

		switch (p) {
		case GL_FOG_DENSITY:
		case GL_FOG_END:
		case GL_FOG_START:
		case GL_FOG_INDEX:
		case GL_FOG_MODE:
			glFogfv(p, new float[] { f });
			break;
		default:
			throw new GLInvalidEnumException("glFogf(int, float)");
		}
	}

	public final void glFogfv(final int p, final float[] pv) {
		__GL_SETUP_NOT_IN_BEGIN("glFogfv(int, float[])");

		// GL_FOG_MODE The params parameter is a single integer or floating-point value that
		// specifies the equation to be used to compute the fog blend factor, f.
		// Three symbolic constants are accepted: GL_LINEAR, GL_EXP, and GL_EXP2.
		// The equations corresponding to these symbolic constants are defined in the
		// following Remarks section. The default fog mode is GL_EXP.

		// GL_FOG_DENSITY The params parameter is a single integer or floating-point value
		// that specifies density, the fog density used in both exponential fog equations.
		// Only nonnegative densities are accepted. The default fog density is 1.0.

		// GL_FOG_START The params parameter is a single integer or floating-point value
		// that specifies start, the near distance used in the linear fog equation.
		// The default near distance is 0.0.

		// GL_FOG_END The params parameter is a single integer or floating-point value that
		// specifies end, the far distance used in the linear fog equation.
		// The default far distance is 1.0.

		// GL_FOG_INDEX The params parameter is a single integer or floating-point value
		// that specifies if, the fog color index. The default fog index is 0.0.

		// GL_FOG_COLOR The glFogfv and glFogiv functions also accept GL_FOG_COLOR:
		// The params parameter contains four integer or floating-point values that
		// specify Cf, the fog color. Integer values are mapped linearly such that
		// the most positive representable value maps to 1.0, and the most negative
		// representable value maps to –1.0. Floating-point values are mapped directly.
		// After conversion, all color components are clamped to the range [0,1].
		// The default fog color is (0,0,0,0).

		// In glFogf and glFogi, specifies the value that pname will be set to.
		// In glFogfv and glFogiv, specifies the value or values to be assigned to pname.
		// GL_FOG_COLOR requires an array of four values. All other parameters accept an
		// array containing only a single value.

		// You enable and disable fog with glEnable and glDisable, using the argument GL_FOG.
		// While enabled, fog affects rasterized geometry, bitmaps, and pixel blocks,
		// but not buffer-clear operations.

		// The glFog function assigns the value or values in params to the fog parameter
		// specified by pname.

		// Fog blends a fog color with each rasterized pixel fragment's posttexturing color
		// using a blending factor f. Factor f is computed in one of three ways,
		// depending on the fog mode. Let z be the distance in eye coordinates from the
		// origin to the fragment being fogged. The equation for GL_LINEAR fog is:
		// f = end - z / end - start

		// The equation for GL_EXP fog is:
		// f = e^(-density.z)

		// The equation for GL_EXP2 fog is:
		// f = e^(-density.z)^2

		// Regardless of the fog mode, f is clamped to the range [0,1] after it is computed.
		// Then, if OpenGL is in RGBA color mode, the fragment's color Cr is replaced by

		// Cr = fCr + (1 - f)Cf

		switch (p) {
		case GL_FOG_COLOR:

			final float r = pv[0];
			final float g = pv[1];
			final float b = pv[2];
			final float a = pv[3];

			gc.state.fog.color.set(r < 0.0f ? 0.0f : (r > 1.0f ? 1.0f : r), g < 0.0f ? 0.0f : (g > 1.0f ? 1.0f : g),
					b < 0.0f ? 0.0f : (b > 1.0f ? 1.0f : b), a < 0.0f ? 0.0f : (a > 1.0f ? 1.0f : a));
			break;
		case GL_FOG_DENSITY:

			if (pv[0] < 0.0f)
				throw new GLInvalidValueException("glFogfv(int, float[])");

			gc.state.fog.density = pv[0];

			break;

		case GL_FOG_END:

			gc.state.fog.end = pv[0];
			break;

		case GL_FOG_START:

			gc.state.fog.start = pv[0];
			break;

		case GL_FOG_INDEX:

			break;

		case GL_FOG_MODE:

			switch ((int) pv[0]) {
			case GL_EXP:
			case GL_EXP2:
			case GL_LINEAR:

				gc.state.fog.mode = (int) pv[0];
				break;

			default:

				throw new GLInvalidEnumException("glFogfv(int, float[])");
			}

			break;

		default:

			throw new GLInvalidEnumException("glFogfv(int, float[])");
		}

		// Recompute cached 1/(end - start) value for linear fogging.

		if (gc.state.fog.mode == GL_LINEAR) {
			if (gc.state.fog.start != gc.state.fog.end)
				gc.state.fog.oneOverEMinusS = 1.0f / (gc.state.fog.end - gc.state.fog.start);
			else
				// use zero as the undefined value
				gc.state.fog.oneOverEMinusS = 0.0f;
		}
	}

	/////////////////////// Raster / Bitmaps ///////////////////////

	public final void glRasterPos2f(final float x, final float y) {
		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glRasterPos2f(float, float)");

		// The current raster position consists of three window coordinates (x, y, z), a clip
		// coordinate w value, an eye coordinate distance, a valid bit, and associated color demos.data
		// and texture coordinates. The w coordinate is a clip coordinate, because w is not projected
		// to window coordinates. The glRasterPos4 function specifies object coordinates x, y, z, and w
		// explicitly. The glRasterPos3 function specifies object coordinates x, y, and z explicitly,
		// while w is implicitly set to one. The glRasterPos2 function uses the argument values for
		// x and y while implicitly setting z and w to zero and one.

		// The object coordinates presented by glRasterPos are treated just like those of a glVertex command.
		// They are transformed by the current modelview and projection matrices and passed to the clipping
		// stage. If the vertex is not culled, then it is projected and scaled to window coordinates, which
		// become the new current raster position, and the GL_CURRENT_RASTER_POSITION_VALID flag is set.
		// If the vertex is culled, then the valid bit is cleared and the current raster position and
		// associated color and texture coordinates are undefined.

		// The current raster position also includes some associated color demos.data and texture coordinates.
		// If lighting is enabled, then GL_CURRENT_RASTER_COLOR, in RGBA mode, or the GL_CURRENT_RASTER_INDEX,
		// in color-index mode, is set to the color produced by the lighting calculation (see glLight, glLightModel,
		// and glShadeModel). If lighting is disabled, current color (in RGBA mode, state variable GL_CURRENT_COLOR)
		// or color index (in color-index mode, state variable GL_CURRENT_INDEX) is used to update the current
		// raster color.

		// Likewise, GL_CURRENT_RASTER_TEXTURE_COORDS is updated as a function of GL_CURRENT_TEXTURE_COORDS,
		// based on the texture matrix and the texture generation functions (see glTexGen). Finally, the distance
		// from the origin of the eye coordinate system to the vertex, as transformed by only the modelview matrix,
		// replaces GL_CURRENT_RASTER_DISTANCE.

		// Initially, the current raster position is (0,0,0,1), the current raster distance is 0, the valid bit
		// is set, the associated RGBA color is (1,1,1,1), the associated color index is 1, and the associated
		// texture coordinates are (0, 0, 0, 1). In RGBA mode, GL_CURRENT_RASTER_INDEX is always 1; in color-index
		// mode, the current raster RGBA color always maintains its initial value.

		// Note - The raster position is modified both by glRasterPos and by glBitmap.

		// When the raster position coordinates are invalid, drawing commands that are based on the raster
		// position are ignored (that is, they do not result in changes to the OpenGL state).

		final GLVertex raster = gc.state.current.raster;
		raster.position.set(x, y, 0.0f, 1.0f);

		gc.pipeline.raster(raster);
	}

	public final void glRasterPos2fv(final float[] p) {
		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glRasterPos2fv(float[])");

		glRasterPos2f(p[0], p[1]);
	}

	public final void glBitmap(final int width, final int height, final float xOrig, final float yOrig,
			final float xMove, final float yMove, final byte[] bits) {
		__GL_SETUP_NOT_IN_BEGIN_VALIDATE("glBitmap(int, int, float, float, float, float, byte[])");

		if (width < 0 || height < 0)
			throw new GLInvalidValueException("glBitmap(int, int, float, float, float, float, byte[])");

		final GLBitmap bitmap = new GLBitmap();

		bitmap.width = width;
		bitmap.height = height;
		bitmap.xOrig = xOrig;
		bitmap.yOrig = yOrig;
		bitmap.xMove = xMove;
		bitmap.yMove = yMove;
		bitmap.bitmap = bits;

		// Could check the pixel transfer modes and see if we can maybe just
		// render oldbits directly rather than converting it first.

		if (width > 0 && height > 0) {
			//	GLubyte *newbits;
			//
			//        newbits = (GLubyte *) (*gc->imports.malloc)(gc, (size_t)
			//				__glImageSize(width, height, GL_COLOR_INDEX, GL_BITMAP));
			//
			//        __glFillImage(gc, width, height, GL_COLOR_INDEX, GL_BITMAP,
			//                      oldbits, newbits);

			gc.pipeline.bitmap(bitmap);
		} else {
			// Nothing to draw, but we still need to update the current
			// colorBuffer position, and we might be in selection or feedback mode.

			gc.pipeline.bitmap(bitmap);
		}
	}

	/////////////////////// Gets ////////////////////////

	public final void glGetBooleanv(final int pname, final boolean[] params) {
		__GL_SETUP_NOT_IN_BEGIN("glGetBooleanv(int, boolean[])");

		doGet(pname, params, "glGetBooleanv(int, boolean[])");
	}

	public final void glGetIntegerv(final int pname, final int[] params) {
		__GL_SETUP_NOT_IN_BEGIN("glGetIntegerv(int, int[])");

		doGet(pname, params, "glGetIntegerv(int, int[])");
	}

	public final void glGetFloatv(final int pname, final float[] params) {
		__GL_SETUP_NOT_IN_BEGIN("glGetFloatv(int, float[])");

		doGet(pname, params, "glGetFloatv(int, float[])");
	}

	public final String glGetString(final int pname) {
		switch (pname) {
		case GL_VENDOR:
			return "The Lovin'";
		case GL_RENDERER:
			return "OGLJava";
		default:
			throw new GLInvalidEnumException("glGetString(int)");
		}
	}

	/////////////////////// GLX ///////////////////////

	public final boolean glXMakeCurrent(GLSoftwareContext gc, GLBackend b) {
		if (gc == null)
			throw new GLException("GLSoftware.glXMakeCurrent(GLContext, Surface) - The supplied GLContext is null.");

		if (b == null)
			throw new GLException("GLSoftware.glXMakeCurrent(GLContext, Surface) - The supplied GLBackend is null.");

		this.gc = (GLSoftwareContext) gc;
		this.gc.setBackend(b);

		return GL_TRUE;
	}

	public final void glXSwapBuffers() {
		//        if(source != null)
		//            source.newPixels(0, 0, gc.state.viewport.width, gc.state.viewport.height);

		if (gc.benchmark.frameCount++ == 0) {
			gc.benchmark.lastTime = System.currentTimeMillis();

			gc.benchmark.clearTime = 0;

			gc.benchmark.lightingTime = 0;
			gc.benchmark.textureTime = 0;
			gc.benchmark.blendTime = 0;
			gc.benchmark.fogTime = 0;

			gc.benchmark.stencilTest = 0;
			gc.benchmark.depthTest = 0;
		} else if (gc.benchmark.frameCount == 32) {
			final long totalTime = System.currentTimeMillis() - gc.benchmark.lastTime;
			final float percent = 100.0f / totalTime;

			// the 10000000 (7 zeros) is 10-9 (nanoseconds) / 100 (cos of the percent calc above)
			// the 10 is 1000 / 100 (cos of the percent calc above)
			gc.benchmark.fps = percent * 32 * 10;

			gc.benchmark.clearPercent = gc.benchmark.clearTime * percent;

			gc.benchmark.lightingPercent = gc.benchmark.lightingTime * percent;
			gc.benchmark.texturePercent = gc.benchmark.textureTime * percent;
			gc.benchmark.blendPercent = gc.benchmark.blendTime * percent;
			gc.benchmark.fogPercent = gc.benchmark.fogTime * percent;

			gc.benchmark.stencilPercent = gc.benchmark.stencilTest * percent;
			gc.benchmark.depthPercent = gc.benchmark.depthTest * percent;

			gc.benchmark.frameCount = 0;
		}

		final StringBuffer buffer = new StringBuffer();

		buffer.append((int) gc.benchmark.fps);
		buffer.append(" fps (C ");
		buffer.append((int) gc.benchmark.clearPercent);
		buffer.append("%, L ");
		buffer.append((int) gc.benchmark.lightingPercent);
		buffer.append("%, T ");
		buffer.append((int) gc.benchmark.texturePercent);
		buffer.append("%, B ");
		buffer.append((int) gc.benchmark.blendPercent);
		buffer.append("%, F ");
		buffer.append((int) gc.benchmark.fogPercent);
		buffer.append("%)");

		final GLBackend backend = gc.getBackend();

		if (backend != null) {

			//backend.setPixels(currentSurface, 0, 0, 0, 0, currentSurface.width, currentSurface.height);
			backend.updatePixels(gc.state.viewport.x, gc.state.viewport.y, gc.state.viewport.width,
					gc.state.viewport.height, gc.frameBuffer.readBuffer.buffer, 0, gc.state.viewport.width);

			// x11.XPutImage(display, drawable, null, ximage, 0, 0, 0, 0, ximage.width, ximage.height);

			//            x11.XLoadFont(display, null); // TODO - a bit of a hack
			//            final XFontStruct fontStruct = x11.XQueryFont(display, drawable, 0);
			//
			//            String string = buffer.toString();
			//            x11.XDrawString(display, drawable, null, 5, ximage.height - (fontStruct.ascent << 1), string, string.length());
			//
			//            buffer.setLength(0);
			//            buffer.append("tests (ST ");
			//            buffer.append((int) gc.benchmark.stencilPercent);
			//            buffer.append("%, DT ");
			//            buffer.append((int) gc.benchmark.depthPercent);
			//            buffer.append("%)");
			//
			//            string = buffer.toString();
			//            x11.XDrawString(display, drawable, null, 5, ximage.height - fontStruct.ascent, string, string.length());
		}
	}

	//public final void glXSetColorModel(final ColorModel colorModel)
	//{
	//    this.colorModel = colorModel;
	//}

	private void __GL_SETUP_IN_BEGIN(final String s) {
		if (gc.beginMode == NEED_VALIDATE || gc.beginMode == NOT_IN_BEGIN)
			throw new GLInvalidOperationException(s);
	}

	private void __GL_SETUP_NOT_IN_BEGIN(final String s) {
		if (gc.beginMode != NEED_VALIDATE && gc.beginMode != NOT_IN_BEGIN)
			throw new GLInvalidOperationException(s);
	}

	private void __GL_SETUP_NOT_IN_BEGIN_VALIDATE(final String s) {
		if (gc.beginMode != NOT_IN_BEGIN) {
			if (gc.beginMode == NEED_VALIDATE) {
				gc.validate();
				gc.beginMode = NOT_IN_BEGIN;
			} else
				throw new GLInvalidOperationException(s);
		}
	}

	private int sizeof(final int type) {
		switch (type) {
		case GL_BITMAP:
			return 0;
		case GL_UNSIGNED_BYTE:
		case GL_BYTE:
			return 8;
		case GL_UNSIGNED_SHORT:
		case GL_SHORT:
			return 16;
		case GL_UNSIGNED_INT:
		case GL_INT:
			return 32;
		case GL_FLOAT:
			// float has the same bit-number as int, set 64 to distinguish
			return 64;
		default:
			return -1;
		}
	}

	/**
	 * Fetch the demos.data for a query in its internal type, then convert it to the
	 * type that the user asked for.
	 */
	private void doGet(final int pname, final Object result, final String message) {
		float[] f = null;
		int[] i = null;
		boolean[] z = null;

		switch (pname) {
		case GL_ALPHA_TEST:
		case GL_BLEND:
		case GL_COLOR_MATERIAL:
		case GL_CULL_FACE:
		case GL_DEPTH_TEST:
		case GL_DITHER:
		case GL_FOG:
		case GL_LIGHTING:
		case GL_LINE_SMOOTH:
		case GL_LINE_STIPPLE:
		case GL_INDEX_LOGIC_OP:
		case GL_COLOR_LOGIC_OP:
		case GL_NORMALIZE:
		case GL_POINT_SMOOTH:
		case GL_POLYGON_SMOOTH:
		case GL_POLYGON_STIPPLE:
			//case GL_RESCALE_NORMAL:
		case GL_SCISSOR_TEST:
		case GL_STENCIL_TEST:
		case GL_TEXTURE_1D:
		case GL_TEXTURE_2D:
		case GL_TEXTURE_3D:
		case GL_AUTO_NORMAL:
		case GL_TEXTURE_GEN_S:
		case GL_TEXTURE_GEN_T:
		case GL_TEXTURE_GEN_R:
		case GL_TEXTURE_GEN_Q:
		case GL_CLIP_PLANE0:
		case GL_CLIP_PLANE1:
		case GL_CLIP_PLANE2:
		case GL_CLIP_PLANE3:
		case GL_CLIP_PLANE4:
		case GL_CLIP_PLANE5:
		case GL_LIGHT0:
		case GL_LIGHT1:
		case GL_LIGHT2:
		case GL_LIGHT3:
		case GL_LIGHT4:
		case GL_LIGHT5:
		case GL_LIGHT6:
		case GL_LIGHT7:
		case GL_MAP1_COLOR_4:
		case GL_MAP1_NORMAL:
		case GL_MAP1_INDEX:
		case GL_MAP1_TEXTURE_COORD_1:
		case GL_MAP1_TEXTURE_COORD_2:
		case GL_MAP1_TEXTURE_COORD_3:
		case GL_MAP1_TEXTURE_COORD_4:
		case GL_MAP1_VERTEX_3:
		case GL_MAP1_VERTEX_4:
		case GL_MAP2_COLOR_4:
		case GL_MAP2_NORMAL:
		case GL_MAP2_INDEX:
		case GL_MAP2_TEXTURE_COORD_1:
		case GL_MAP2_TEXTURE_COORD_2:
		case GL_MAP2_TEXTURE_COORD_3:
		case GL_MAP2_TEXTURE_COORD_4:
		case GL_MAP2_VERTEX_3:
		case GL_MAP2_VERTEX_4:
		case GL_POLYGON_OFFSET_POINT:
		case GL_POLYGON_OFFSET_LINE:
		case GL_POLYGON_OFFSET_FILL:
		case GL_VERTEX_ARRAY:
		case GL_NORMAL_ARRAY:
		case GL_COLOR_ARRAY:
		case GL_INDEX_ARRAY:
		case GL_TEXTURE_COORD_ARRAY:
		case GL_EDGE_FLAG_ARRAY:
			//            case GL_COLOR_TABLE:
			//            case GL_CONVOLUTION_1D:
			//            case GL_CONVOLUTION_2D:
			//            case GL_SEPARABLE_2D:
			//            case GL_POST_CONVOLUTION_COLOR_TABLE:
			//            case GL_POST_COLOR_MATRIX_COLOR_TABLE:
			//            case GL_HISTOGRAM:
			//            case GL_MINMAX:
			z = new boolean[] { glIsEnabled(pname) };
			break;
		case GL_MAX_TEXTURE_SIZE:
			i = new int[] { MAX_TEXTURE_SIZE };
			break;
		//            case GL_MAX_3D_TEXTURE_SIZE:
		//                *ip++ = gc - > constants.maxTexture3DSize;
		//                break;
		//            case GL_MAX_TEXTURE_UNITS_ARB:
		//                *ip++ = gc - > constants.maxTextureUnits;
		//                break;
		//            case GL_ACTIVE_TEXTURE_ARB:
		//                *ip++ = gc - > state.texture.activeTexture + GL_TEXTURE0_ARB;
		//                break;
		//            case GL_CLIENT_ACTIVE_TEXTURE_ARB:
		//                *ip++ = gc - > clientTexture.activeTexture + GL_TEXTURE0_ARB;
		//                break;
		//            case GL_SUBPIXEL_BITS:
		//                *ip++ = gc - > constants.subpixelBits;
		//                break;
		//            case GL_MAX_LIST_NESTING:
		//                *ip++ = __GL_MAX_LIST_NESTING;
		//                break;
		case GL_CURRENT_COLOR:
			f = new float[] { gc.state.current.color.r, gc.state.current.color.g, gc.state.current.color.b,
					gc.state.current.color.a };
			break;
		//            case GL_CURRENT_INDEX:
		//                *fp++ = gc - > state.current.userColorIndex;
		//                break;
		//            case GL_CURRENT_NORMAL:
		//                *cp++ = gc - > state.current.normal.x;
		//                *cp++ = gc - > state.current.normal.y;
		//                *cp++ = gc - > state.current.normal.z;
		//                break;
		//            case GL_CURRENT_TEXTURE_COORDS:
		//                *fp++ = gc - > state.current.texture[gc - > state.texture.activeTexture].x;
		//                *fp++ = gc - > state.current.texture[gc - > state.texture.activeTexture].y;
		//                *fp++ = gc - > state.current.texture[gc - > state.texture.activeTexture].z;
		//                *fp++ = gc - > state.current.texture[gc - > state.texture.activeTexture].w;
		//                break;
		//            case GL_CURRENT_RASTER_INDEX:
		//                if (gc - > modes.rgb)
		//                {
		//                    /* Always return 1 */
		//                    *fp++ = 1.0;
		//                }
		//                else
		//                {
		//                    *fp++ = gc - > state.current.rasterPos.colors[__GL_FRONTFACE].r;
		//                }
		//                break;
		//            case GL_CURRENT_RASTER_COLOR:
		//                if (gc - > modes.colorIndexMode)
		//                {
		//                    /* Always return 1,1,1,1 */
		//                    *fp++ = 1.0;
		//                    *fp++ = 1.0;
		//                    *fp++ = 1.0;
		//                    *fp++ = 1.0;
		//                }
		//                else
		//                {
		//                    *scp++ = gc - > state.current.rasterPos.colors[__GL_FRONTFACE].r;
		//                    *scp++ = gc - > state.current.rasterPos.colors[__GL_FRONTFACE].g;
		//                    *scp++ = gc - > state.current.rasterPos.colors[__GL_FRONTFACE].b;
		//                    *scp++ = gc - > state.current.rasterPos.colors[__GL_FRONTFACE].a;
		//                }
		//                break;
		//            case GL_CURRENT_RASTER_TEXTURE_COORDS:
		//                *fp++ = gc - > state.current.rasterPos.texture[gc - > state.texture.activeTexture].x;
		//                *fp++ = gc - > state.current.rasterPos.texture[gc - > state.texture.activeTexture].y;
		//                *fp++ = gc - > state.current.rasterPos.texture[gc - > state.texture.activeTexture].z;
		//                *fp++ = gc - > state.current.rasterPos.texture[gc - > state.texture.activeTexture].w;
		//                break;
		//            case GL_CURRENT_RASTER_POSITION:
		//                *fp++ = __glReturnWindowX(gc, gc - > state.current.rasterPos.window.x);
		//                *fp++ = __glReturnWindowY(gc, gc - > state.current.rasterPos.window.y);
		//                *fp++ = gc - > state.current.rasterPos.window.z
		//                        * gc - > constants.oneOverDepthScale;
		//                *fp++ = gc - > state.current.rasterPos.clip.w;
		//                break;
		//            case GL_CURRENT_RASTER_POSITION_VALID:
		//                *lp++ = gc - > state.current.validRasterPos;
		//                break;
		//            case GL_CURRENT_RASTER_DISTANCE:
		//                *fp++ = gc - > state.current.rasterPos.eye.z;
		//                break;
		//            case GL_POINT_SIZE:
		//                *fp++ = gc - > state.point.requestedSize;
		//                break;
		//            case GL_POINT_SIZE_RANGE:
		//                /* case GL_SMOOTH_POINT_SIZE_RANGE: */ /* Alias for POINT_SIZE_RANGE */
		//                *fp++ = gc - > constants.pointSizeMinimum;
		//                *fp++ = gc - > constants.pointSizeMaximum;
		//                break;
		//            case GL_POINT_SIZE_GRANULARITY:
		//                /* case GL_SMOOTH_POINT_SIZE_GRANULARITY: */ /* Alias for POINT_SIZE_GRANULARITY */
		//                *fp++ = gc - > constants.pointSizeGranularity;
		//                break;
		//            case GL_ALIASED_POINT_SIZE_RANGE:
		//                *fp++ = gc - > constants.aliasedPointSizeMinimum;
		//                *fp++ = gc - > constants.aliasedPointSizeMaximum;
		//                break;
		//            case GL_LINE_WIDTH:
		//                *fp++ = gc - > state.line.requestedWidth;
		//                break;
		//            case GL_LINE_WIDTH_RANGE:
		//                /* case GL_SMOOTH_LINE_WIDTH_RANGE: */ /* Alias for LINE_WIDTH_RANGE */
		//                *fp++ = gc - > constants.lineWidthMinimum;
		//                *fp++ = gc - > constants.lineWidthMaximum;
		//                break;
		//            case GL_LINE_WIDTH_GRANULARITY:
		//                /* case GL_SMOOTH_LINE_WIDTH_GRANULARITY: */ /* Alias for LINE_WIDTH_GRANULARITY */
		//                *fp++ = gc - > constants.lineWidthGranularity;
		//                break;
		//            case GL_ALIASED_LINE_WIDTH_RANGE:
		//                *fp++ = gc - > constants.aliasedLineWidthMinimum;
		//                *fp++ = gc - > constants.aliasedLineWidthMaximum;
		//                break;
		//            case GL_LINE_STIPPLE_PATTERN:
		//                *ip++ = gc - > state.line.stipple;
		//                break;
		//            case GL_LINE_STIPPLE_REPEAT:
		//                *ip++ = gc - > state.line.stippleRepeat;
		//                break;
		//            case GL_POLYGON_MODE:
		//                *ip++ = gc - > state.polygon.frontMode;
		//                *ip++ = gc - > state.polygon.backMode;
		//                break;
		//            case GL_EDGE_FLAG:
		//                *lp++ = (gc - > state.current.edgeTag & __GL_VERTEX_EDGE_FLAG) ? GL_TRUE
		//                        : GL_FALSE;
		//                break;
		//            case GL_CULL_FACE_MODE:
		//                *ip++ = gc - > state.polygon.cull;
		//                break;
		//            case GL_FRONT_FACE:
		//                *ip++ = gc - > state.polygon.frontFaceDirection;
		//                break;
		//            case GL_LIGHT_MODEL_LOCAL_VIEWER:
		//                *lp++ = gc - > state.lighting.model.localViewer;
		//                break;
		//            case GL_LIGHT_MODEL_TWO_SIDE:
		//                *lp++ = gc - > state.lighting.model.twoSided;
		//                break;
		//            case GL_LIGHT_MODEL_AMBIENT:
		//                __glUnScaleColorf(gc, cp,  & gc - > state.lighting.model.ambient);
		//                cp += 4;
		//                break;
		//            case GL_LIGHT_MODEL_COLOR_CONTROL:
		//                *ip++ = gc - > state.lighting.model.colorControl;
		//                break;
		//            case GL_COLOR_MATERIAL_FACE:
		//                *ip++ = gc - > state.lighting.colorMaterialFace;
		//                break;
		//            case GL_COLOR_MATERIAL_PARAMETER:
		//                *ip++ = gc - > state.lighting.colorMaterialParam;
		//                break;
		//            case GL_SHADE_MODEL:
		//                *ip++ = gc - > state.lighting.shadingModel;
		//                break;
		//            case GL_FOG_INDEX:
		//                *fp++ = gc - > state.fog.index;
		//                break;
		//            case GL_FOG_DENSITY:
		//                *fp++ = gc - > state.fog.density;
		//                break;
		//            case GL_FOG_START:
		//                *fp++ = gc - > state.fog.start;
		//                break;
		//            case GL_FOG_END:
		//                *fp++ = gc - > state.fog.end;
		//                break;
		//            case GL_FOG_MODE:
		//                *ip++ = gc - > state.fog.mode;
		//                break;
		//            case GL_FOG_COLOR:
		//                *scp++ = gc - > state.fog.color.r;
		//                *scp++ = gc - > state.fog.color.g;
		//                *scp++ = gc - > state.fog.color.b;
		//                *scp++ = gc - > state.fog.color.a;
		//                break;
		//            case GL_DEPTH_RANGE:
		//                /* These get scaled like colors, to [0, 2^31-1] */
		//                *cp++ = gc - > state.viewport.zNear;
		//                *cp++ = gc - > state.viewport.zFar;
		//                break;
		//            case GL_DEPTH_WRITEMASK:
		//                *lp++ = gc - > state.depth.writeEnable;
		//                break;
		//            case GL_DEPTH_CLEAR_VALUE:
		//                /* This gets scaled like colors, to [0, 2^31-1] */
		//                *cp++ = gc - > state.depth.clear;
		//                break;
		//            case GL_DEPTH_FUNC:
		//                *ip++ = gc - > state.depth.testFunc;
		//                break;
		//            case GL_ACCUM_CLEAR_VALUE:
		//                *cp++ = gc - > state.accum.clear.r;
		//                *cp++ = gc - > state.accum.clear.g;
		//                *cp++ = gc - > state.accum.clear.b;
		//                *cp++ = gc - > state.accum.clear.a;
		//                break;
		//            case GL_STENCIL_CLEAR_VALUE:
		//                *ip++ = gc - > state.stencil.clear;
		//                break;
		//            case GL_STENCIL_FUNC:
		//                *ip++ = gc - > state.stencil.testFunc;
		//                break;
		//            case GL_STENCIL_VALUE_MASK:
		//                *ip++ = gc - > state.stencil.valueMask;
		//                break;
		//            case GL_STENCIL_FAIL:
		//                *ip++ = gc - > state.stencil.fail;
		//                break;
		//            case GL_STENCIL_PASS_DEPTH_FAIL:
		//                *ip++ = gc - > state.stencil.depthFail;
		//                break;
		//            case GL_STENCIL_PASS_DEPTH_PASS:
		//                *ip++ = gc - > state.stencil.depthPass;
		//                break;
		//            case GL_STENCIL_REF:
		//                *ip++ = gc - > state.stencil.reference;
		//                break;
		//            case GL_STENCIL_WRITEMASK:
		//                *ip++ = gc - > state.stencil.writeMask;
		//                break;
		//            case GL_MATRIX_MODE:
		//                *ip++ = gc - > state.transform.matrixMode;
		//                break;
		case GL_VIEWPORT:
			i = new int[] { gc.state.viewport.x, gc.state.viewport.y, gc.state.viewport.width, gc.state.viewport.height };
			break;
		//            case GL_ATTRIB_STACK_DEPTH:
		//                *ip++ = gc - > attributes.stackPointer - gc - > attributes.stack;
		//                break;
		//            case GL_CLIENT_ATTRIB_STACK_DEPTH:
		//                *ip++ = gc - > attributes.clientStackPointer - gc - > attributes.clientStack;
		//                break;
		//            case GL_MODELVIEW_STACK_DEPTH:
		//                *ip++ = 1 + (gc - > transform.modelView - gc - > transform.modelViewStack);
		//                break;
		//            case GL_PROJECTION_STACK_DEPTH:
		//                *ip++ = 1 + (gc - > transform.projection - gc - > transform.projectionStack);
		//                break;
		//            case GL_TEXTURE_STACK_DEPTH:
		//                *ip++ = 1 + (gc - > transform.texture[gc - > state.texture.activeTexture] -
		//                        gc - > transform.textureStack[gc - > state.texture.activeTexture]);
		//                break;
		//            case GL_COLOR_MATRIX_STACK_DEPTH:
		//                *ip++ = 1 + (gc - > transform.color - gc - > transform.colorStack);
		//                break;
		case GL_MODELVIEW_MATRIX:
			f = new float[16];
			gc.transform.modelView.matrix.get(f);
			break;
		case GL_PROJECTION_MATRIX:
			f = new float[16];
			gc.transform.projection.matrix.get(f);
			break;
		//            case GL_TEXTURE_MATRIX:
		//                mp = &gc - > transform.texture[gc - > state.texture.activeTexture] - > matrix.matrix[0][0];
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                break;
		//            case GL_COLOR_MATRIX:
		//                mp = &gc - > transform.color - > matrix.matrix[0][0];
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++; *fp++ = *mp++;
		//                break;
		//            case GL_ALPHA_TEST_FUNC:
		//                *ip++ = gc - > state.colorBuffer.alphaFunction;
		//                break;
		//            case GL_ALPHA_TEST_REF:
		//                *fp++ = gc - > state.colorBuffer.alphaReference;
		//                break;
		//            case GL_BLEND_DST:
		//                *ip++ = gc - > state.colorBuffer.blendDst;
		//                break;
		//            case GL_BLEND_SRC:
		//                *ip++ = gc - > state.colorBuffer.blendSrc;
		//                break;
		//            case GL_BLEND_COLOR:
		//                *fp++ = gc - > state.colorBuffer.blendColor.r * gc - > constants.oneOverRedScale;
		//                *fp++ = gc - > state.colorBuffer.blendColor.g * gc - > constants.oneOverGreenScale;
		//                *fp++ = gc - > state.colorBuffer.blendColor.b * gc - > constants.oneOverBlueScale;
		//                *fp++ = gc - > state.colorBuffer.blendColor.a * gc - > constants.oneOverAlphaScale;
		//                break;
		//            case GL_BLEND_EQUATION:
		//                *ip++ = gc - > state.colorBuffer.blendEquation;
		//                break;
		//            case GL_LOGIC_OP_MODE:
		//                *ip++ = gc - > state.colorBuffer.logicOp;
		//                break;
		//            case GL_DRAW_BUFFER:
		//                *ip++ = gc - > state.colorBuffer.drawBufferReturn;
		//                break;
		//            case GL_READ_BUFFER:
		//                *ip++ = gc - > state.pixel.readBufferReturn;
		//                break;
		case GL_SCISSOR_BOX:
			i = new int[] { gc.state.scissor.x, gc.state.scissor.y, gc.state.scissor.width, gc.state.scissor.height };
			break;
		//                break;
		//            case GL_INDEX_CLEAR_VALUE:
		//                *fp++ = gc - > state.colorBuffer.clearIndex;
		//                break;
		//            case GL_INDEX_MODE:
		//                *lp++ = gc - > modes.colorIndexMode ? GL_TRUE : GL_FALSE;
		//                break;
		//            case GL_INDEX_WRITEMASK:
		//                *ip++ = gc - > state.colorBuffer.writeMask;
		//                break;
		//            case GL_COLOR_CLEAR_VALUE:
		//                *cp++ = gc - > state.colorBuffer.clear.r;
		//                *cp++ = gc - > state.colorBuffer.clear.g;
		//                *cp++ = gc - > state.colorBuffer.clear.b;
		//                *cp++ = gc - > state.colorBuffer.clear.a;
		//                break;
		//            case GL_RGBA_MODE:
		//                *lp++ = gc - > modes.rgb ? GL_TRUE : GL_FALSE;
		//                break;
		//            case GL_COLOR_WRITEMASK:
		//                *lp++ = gc - > state.colorBuffer.rMask;
		//                *lp++ = gc - > state.colorBuffer.gMask;
		//                *lp++ = gc - > state.colorBuffer.bMask;
		//                *lp++ = gc - > state.colorBuffer.aMask;
		//                break;
		case GL_RENDER_MODE:
			i = new int[] { gc.renderMode };
			break;
		//            case GL_PERSPECTIVE_CORRECTION_HINT:
		//                *ip++ = gc - > state.hints.perspectiveCorrection;
		//                break;
		//            case GL_POINT_SMOOTH_HINT:
		//                *ip++ = gc - > state.hints.pointSmooth;
		//                break;
		//            case GL_LINE_SMOOTH_HINT:
		//                *ip++ = gc - > state.hints.lineSmooth;
		//                break;
		//            case GL_POLYGON_SMOOTH_HINT:
		//                *ip++ = gc - > state.hints.polygonSmooth;
		//                break;
		//            case GL_FOG_HINT:
		//                *ip++ = gc - > state.hints.fog;
		//                break;
		//            case GL_LIST_BASE:
		//                *ip++ = gc - > state.list.listBase;
		//                break;
		//            case GL_LIST_INDEX:
		//                *ip++ = gc - > dlist.currentList;
		//                break;
		//            case GL_LIST_MODE:
		//                *ip++ = gc - > dlist.mode;
		//                break;
		case GL_PACK_SWAP_BYTES:
			z = new boolean[] { gc.clientState.clientPixel.packing.swapEndian };
			break;
		case GL_PACK_LSB_FIRST:
			z = new boolean[] { gc.clientState.clientPixel.packing.lsbFirst };
			break;
		case GL_PACK_ROW_LENGTH:
			i = new int[] { gc.clientState.clientPixel.packing.rowLength };
			break;
		case GL_PACK_IMAGE_HEIGHT:
			i = new int[] { gc.clientState.clientPixel.packing.imageHeight };
			break;
		case GL_PACK_SKIP_ROWS:
			i = new int[] { gc.clientState.clientPixel.packing.skipRows };
			break;
		case GL_PACK_SKIP_PIXELS:
			i = new int[] { gc.clientState.clientPixel.packing.skipPixels };
			break;
		case GL_PACK_SKIP_IMAGES:
			i = new int[] { gc.clientState.clientPixel.packing.skipImages };
			break;
		case GL_PACK_ALIGNMENT:
			i = new int[] { gc.clientState.clientPixel.packing.alignment };
			break;
		case GL_UNPACK_SWAP_BYTES:
			z = new boolean[] { gc.clientState.clientPixel.unpacking.swapEndian };
			break;
		case GL_UNPACK_LSB_FIRST:
			z = new boolean[] { gc.clientState.clientPixel.unpacking.lsbFirst };
			break;
		case GL_UNPACK_ROW_LENGTH:
			i = new int[] { gc.clientState.clientPixel.unpacking.rowLength };
			break;
		case GL_UNPACK_IMAGE_HEIGHT:
			i = new int[] { gc.clientState.clientPixel.unpacking.imageHeight };
			break;
		case GL_UNPACK_SKIP_ROWS:
			i = new int[] { gc.clientState.clientPixel.unpacking.skipRows };
			break;
		case GL_UNPACK_SKIP_PIXELS:
			i = new int[] { gc.clientState.clientPixel.unpacking.skipPixels };
			break;
		case GL_UNPACK_SKIP_IMAGES:
			i = new int[] { gc.clientState.clientPixel.unpacking.skipImages };
			break;
		case GL_UNPACK_ALIGNMENT:
			i = new int[] { gc.clientState.clientPixel.unpacking.alignment };
			break;
		//            case GL_MAP_COLOR:
		//                *lp++ = gc - > state.pixel.transferMode.mapColor;
		//                break;
		//            case GL_MAP_STENCIL:
		//                *lp++ = gc - > state.pixel.transferMode.mapStencil;
		//                break;
		//            case GL_INDEX_SHIFT:
		//                *ip++ = gc - > state.pixel.transferMode.indexShift;
		//                break;
		//            case GL_INDEX_OFFSET:
		//                *ip++ = gc - > state.pixel.transferMode.indexOffset;
		//                break;
		//            case GL_RED_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.scale.r;
		//                break;
		//            case GL_GREEN_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.scale.g;
		//                break;
		//            case GL_BLUE_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.scale.b;
		//                break;
		//            case GL_ALPHA_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.scale.a;
		//                break;
		//            case GL_RED_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.bias.r;
		//                break;
		//            case GL_GREEN_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.bias.g;
		//                break;
		//            case GL_BLUE_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.bias.b;
		//                break;
		//            case GL_ALPHA_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.bias.a;
		//                break;
		//            case GL_DEPTH_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.depthScale;
		//                break;
		//            case GL_DEPTH_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.depthBias;
		//                break;
		//            case GL_POST_CONVOLUTION_RED_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionScale.r;
		//                break;
		//            case GL_POST_CONVOLUTION_GREEN_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionScale.g;
		//                break;
		//            case GL_POST_CONVOLUTION_BLUE_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionScale.b;
		//                break;
		//            case GL_POST_CONVOLUTION_ALPHA_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionScale.a;
		//                break;
		//            case GL_POST_CONVOLUTION_RED_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionBias.r;
		//                break;
		//            case GL_POST_CONVOLUTION_GREEN_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionBias.g;
		//                break;
		//            case GL_POST_CONVOLUTION_BLUE_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionBias.b;
		//                break;
		//            case GL_POST_CONVOLUTION_ALPHA_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postConvolutionBias.a;
		//                break;
		//            case GL_POST_COLOR_MATRIX_RED_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixScale.r;
		//                break;
		//            case GL_POST_COLOR_MATRIX_GREEN_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixScale.g;
		//                break;
		//            case GL_POST_COLOR_MATRIX_BLUE_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixScale.b;
		//                break;
		//            case GL_POST_COLOR_MATRIX_ALPHA_SCALE:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixScale.a;
		//                break;
		//            case GL_POST_COLOR_MATRIX_RED_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixBias.r;
		//                break;
		//            case GL_POST_COLOR_MATRIX_GREEN_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixBias.g;
		//                break;
		//            case GL_POST_COLOR_MATRIX_BLUE_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixBias.b;
		//                break;
		//            case GL_POST_COLOR_MATRIX_ALPHA_BIAS:
		//                *fp++ = gc - > state.pixel.transferMode.postColorMatrixBias.a;
		//                break;
		//            case GL_ZOOM_X:
		//                *fp++ = gc - > state.pixel.transferMode.zoomX;
		//                break;
		//            case GL_ZOOM_Y:
		//                *fp++ = gc - > state.pixel.transferMode.zoomY;
		//                break;
		//            case GL_PIXEL_MAP_I_TO_I_SIZE:
		//            case GL_PIXEL_MAP_S_TO_S_SIZE:
		//            case GL_PIXEL_MAP_I_TO_R_SIZE:
		//            case GL_PIXEL_MAP_I_TO_G_SIZE:
		//            case GL_PIXEL_MAP_I_TO_B_SIZE:
		//            case GL_PIXEL_MAP_I_TO_A_SIZE:
		//            case GL_PIXEL_MAP_R_TO_R_SIZE:
		//            case GL_PIXEL_MAP_G_TO_G_SIZE:
		//            case GL_PIXEL_MAP_B_TO_B_SIZE:
		//            case GL_PIXEL_MAP_A_TO_A_SIZE:
		//                index = sq - GL_PIXEL_MAP_I_TO_I_SIZE;
		//                *ip++ = gc - > pixel.pixelMap[index].size;
		//                break;
		//            case GL_MAX_EVAL_ORDER:
		//                *ip++ = gc - > constants.maxEvalOrder;
		//                break;
		//            case GL_MAX_LIGHTS:
		//                *ip++ = gc - > constants.numberOfLights;
		//                break;
		//            case GL_MAX_CLIP_PLANES:
		//                *ip++ = gc - > constants.numberOfClipPlanes;
		//                break;
		//            case GL_MAX_PIXEL_MAP_TABLE:
		//                *ip++ = gc - > constants.maxPixelMapTable;
		//                break;
		//            case GL_MAX_ATTRIB_STACK_DEPTH:
		//                *ip++ = gc - > constants.maxAttribStackDepth;
		//                break;
		//            case GL_MAX_CLIENT_ATTRIB_STACK_DEPTH:
		//                *ip++ = gc - > constants.maxClientAttribStackDepth;
		//                break;
		//            case GL_MAX_MODELVIEW_STACK_DEPTH:
		//                *ip++ = gc - > constants.maxModelViewStackDepth;
		//                break;
		//            case GL_MAX_NAME_STACK_DEPTH:
		//                *ip++ = gc - > constants.maxNameStackDepth;
		//                break;
		//            case GL_MAX_PROJECTION_STACK_DEPTH:
		//                *ip++ = gc - > constants.maxProjectionStackDepth;
		//                break;
		//            case GL_MAX_TEXTURE_STACK_DEPTH:
		//                *ip++ = gc - > constants.maxTextureStackDepth;
		//                break;
		//            case GL_MAX_COLOR_MATRIX_STACK_DEPTH:
		//                *ip++ = gc - > constants.maxColorStackDepth;
		//                break;
		//            case GL_INDEX_BITS:
		//                i = new int[]{gc.indexBits};
		//                break;
		case GL_RED_BITS:
			i = new int[] { gc.redSize };
			break;
		case GL_GREEN_BITS:
			i = new int[] { gc.greenSize };
			break;
		case GL_BLUE_BITS:
			i = new int[] { gc.blueSize };
			break;
		case GL_ALPHA_BITS:
			i = new int[] { gc.alphaSize };
			break;
		case GL_DEPTH_BITS:
			i = new int[] { gc.depthSize };
			break;
		case GL_STENCIL_BITS:
			i = new int[] { gc.stencilSize };
			break;
		case GL_ACCUM_RED_BITS:
			i = new int[] { gc.accumRedSize };
			break;
		case GL_ACCUM_GREEN_BITS:
			i = new int[] { gc.accumGreenSize };
			break;
		case GL_ACCUM_BLUE_BITS:
			i = new int[] { gc.accumBlueSize };
			break;
		case GL_ACCUM_ALPHA_BITS:
			i = new int[] { gc.accumAlphaSize };
			break;
		//            case GL_MAP1_GRID_DOMAIN:
		//                *fp++ = gc - > state.evaluator.u1.start;
		//                *fp++ = gc - > state.evaluator.u1.finish;
		//                break;
		//            case GL_MAP1_GRID_SEGMENTS:
		//                *ip++ = gc - > state.evaluator.u1.n;
		//                break;
		//            case GL_MAP2_GRID_DOMAIN:
		//                *fp++ = gc - > state.evaluator.u2.start;
		//                *fp++ = gc - > state.evaluator.u2.finish;
		//                *fp++ = gc - > state.evaluator.v2.start;
		//                *fp++ = gc - > state.evaluator.v2.finish;
		//                break;
		//            case GL_MAP2_GRID_SEGMENTS:
		//                *ip++ = gc - > state.evaluator.u2.n;
		//                *ip++ = gc - > state.evaluator.v2.n;
		//                break;
		//            case GL_NAME_STACK_DEPTH:
		//                *ip++ = gc - > select.sp - gc - > select.stack;
		//                break;
		//            case GL_MAX_VIEWPORT_DIMS:
		//                *ip++ = gc - > constants.maxViewportWidth;
		//                *ip++ = gc - > constants.maxViewportHeight;
		//                break;
		case GL_DOUBLEBUFFER:
			z = new boolean[] { gc.doubleBuffer };
			break;
		case GL_AUX_BUFFERS:
			i = new int[] { gc.auxBuffers };
			break;
		//            case GL_STEREO:
		//                *lp++ = GL_FALSE;
		//                break;
		//            case GL_POLYGON_OFFSET_FACTOR:
		//                *fp++ = gc - > state.polygon.factor;
		//                break;
		//            case GL_POLYGON_OFFSET_UNITS:
		//                *fp++ = gc - > state.polygon.units;
		//                break;
		//            case GL_TEXTURE_BINDING_1D:
		//                {
		//                    __GLtexture * tex =
		//                            __glLookupActiveTexture(gc, GL_TEXTURE_1D);
		//                    *ip++ = tex - > state.name;
		//                }
		//                break;
		//            case GL_TEXTURE_BINDING_2D:
		//                {
		//                    __GLtexture * tex =
		//                            __glLookupActiveTexture(gc, GL_TEXTURE_2D);
		//                    *ip++ = tex - > state.name;
		//                }
		//                break;
		//            case GL_TEXTURE_BINDING_3D:
		//                {
		//                    __GLtexture * tex =
		//                            __glLookupActiveTexture(gc, GL_TEXTURE_3D);
		//                    *ip++ = tex - > state.name;
		//                }
		//                break;
		//            case GL_VERTEX_ARRAY_SIZE:
		//                *ip++ = gc - > vertexArray.vp.size;
		//                break;
		//            case GL_VERTEX_ARRAY_TYPE:
		//                *ip++ = gc - > vertexArray.vp.type;
		//                break;
		//            case GL_VERTEX_ARRAY_STRIDE:
		//                *ip++ = gc - > vertexArray.vp.userStride;
		//                break;
		//                #if
		//                0
		//            case GL_VERTEX_ARRAY_COUNT_EXT:
		//                *ip++ = gc - > vertexArray.vp.frameCount;
		//                break;
		//                #endif
		//            case GL_NORMAL_ARRAY_TYPE:
		//                *ip++ = gc - > vertexArray.np.type;
		//                break;
		//            case GL_NORMAL_ARRAY_STRIDE:
		//                *ip++ = gc - > vertexArray.np.userStride;
		//                break;
		//                #if
		//                0
		//            case GL_NORMAL_ARRAY_COUNT_EXT:
		//                *ip++ = gc - > vertexArray.np.frameCount;
		//                break;
		//                #endif
		//            case GL_COLOR_ARRAY_SIZE:
		//                *ip++ = gc - > vertexArray.cp.size;
		//                break;
		//            case GL_COLOR_ARRAY_TYPE:
		//                *ip++ = gc - > vertexArray.cp.type;
		//                break;
		//            case GL_COLOR_ARRAY_STRIDE:
		//                *ip++ = gc - > vertexArray.cp.userStride;
		//                break;
		//                #if
		//                0
		//            case GL_COLOR_ARRAY_COUNT_EXT:
		//                *ip++ = gc - > vertexArray.cp.frameCount;
		//                break;
		//                #endif
		//            case GL_INDEX_ARRAY_TYPE:
		//                *ip++ = gc - > vertexArray.ip.type;
		//                break;
		//            case GL_INDEX_ARRAY_STRIDE:
		//                *ip++ = gc - > vertexArray.ip.userStride;
		//                break;
		//                #if
		//                0
		//            case GL_INDEX_ARRAY_COUNT_EXT:
		//                *ip++ = gc - > vertexArray.ip.frameCount;
		//                break;
		//                #endif
		//            case GL_TEXTURE_COORD_ARRAY_SIZE:
		//                *ip++ = gc - > vertexArray.tp[gc - > clientTexture.activeTexture].size;
		//                break;
		//            case GL_TEXTURE_COORD_ARRAY_TYPE:
		//                *ip++ = gc - > vertexArray.tp[gc - > clientTexture.activeTexture].type;
		//                break;
		//            case GL_TEXTURE_COORD_ARRAY_STRIDE:
		//                *ip++ = gc - > vertexArray.tp[gc - > clientTexture.activeTexture].userStride;
		//                break;
		//                #if
		//                0
		//            case GL_TEXTURE_COORD_ARRAY_COUNT_EXT:
		//                *ip++ = gc - > vertexArray.tp[gc - > clientTexture.activeTexture].frameCount;
		//                break;
		//                #endif
		//            case GL_EDGE_FLAG_ARRAY_STRIDE:
		//                *ip++ = gc - > vertexArray.ep.userStride;
		//                break;
		//                #if
		//                0
		//            case GL_EDGE_FLAG_ARRAY_COUNT_EXT:
		//                *ip++ = gc - > vertexArray.ep.frameCount;
		//                break;
		//                #endif
		//            case GL_MAX_ELEMENTS_VERTICES:
		//                *ip++ = gc - > vertexArray.maxElementsVertices;
		//                break;
		//            case GL_MAX_ELEMENTS_INDICES:
		//                *ip++ = gc - > vertexArray.maxElementsIndices;
		//                break;
		//            case GL_FEEDBACK_BUFFER_SIZE:
		//                *ip++ = gc - > feedback.resultLength;
		//                break;
		//            case GL_FEEDBACK_BUFFER_TYPE:
		//                *ip++ = gc - > feedback.type;
		//                break;
		//            case GL_SELECTION_BUFFER_SIZE:
		//                *ip++ = gc - > select.resultLength;
		//                break;
		//                #ifdef XXX_IS_CULLVERTEX_SUPPORTED
		//            case GL_ARRAY_ELEMENT_LOCK_FIRST_EXT:
		//                *ip++ = gc - > vertexArray.start;
		//                break;
		//            case GL_ARRAY_ELEMENT_LOCK_COUNT_EXT:
		//                *ip++ = gc - > vertexArray.frameCount;
		//                break;
		//            case GL_CULL_VERTEX_EYE_POSITION_EXT:
		//                *fp++ = gc - > state.transform.eyePos.x;
		//                *fp++ = gc - > state.transform.eyePos.y;
		//                *fp++ = gc - > state.transform.eyePos.z;
		//                *fp++ = gc - > state.transform.eyePos.w;
		//                break;
		//            case GL_CULL_VERTEX_OBJECT_POSITION_EXT:
		//                *fp++ = gc - > state.transform.eyePosObj.x;
		//                *fp++ = gc - > state.transform.eyePosObj.y;
		//                *fp++ = gc - > state.transform.eyePosObj.z;
		//                *fp++ = gc - > state.transform.eyePosObj.w;
		//                break;
		//                #endif /*XXX_IS_CULLVERTEX_SUPPORTED*/
		default:
			throw new GLInvalidEnumException(message);
		}

		if (f != null)
			System.arraycopy(f, 0, result, 0, f.length);
		else if (i != null)
			System.arraycopy(i, 0, result, 0, i.length);
		else if (z != null)
			System.arraycopy(z, 0, result, 0, z.length);
		else
			throw new GLInvalidEnumException(message);
	}

	private void DELAY_VALIDATE(final int mask) {
		__GL_SETUP_NOT_IN_BEGIN("DELAY_VALIDATE(int)");

		gc.beginMode = NEED_VALIDATE;
		gc.validateMask |= mask;
	}

	public void throwGLError(int error, String s) {
		if (DEBUG) {
			System.out.print("GL Error (");
			switch (error) {
			case GL.GL_NO_ERROR:
				System.out.print("GL_NO_ERROR");
				break;
			case GL.GL_INVALID_VALUE:
				System.out.print("GL_INVALID_VALUE");
				break;
			case GL.GL_INVALID_ENUM:
				System.out.print("GL_INVALID_ENUM");
				break;
			case GL.GL_INVALID_OPERATION:
				System.out.print("GL_INVALID_OPERATION");
				break;
			case GL.GL_STACK_OVERFLOW:
				System.out.print("GL_STACK_OVERFLOW");
				break;
			case GL.GL_STACK_UNDERFLOW:
				System.out.print("GL_STACK_UNDERFLOW");
				break;
			case GL.GL_OUT_OF_MEMORY:
				System.out.print("GL_OUT_OF_MEMORY");
				break;
			default:
				System.out.print("unknown");
				break;
			}
			System.out.println("): " + s);
		}
		
		lastError = error;
		
	}
	
	public int glGetError() {
		int error = lastError;
		lastError = GL_NO_ERROR;
		return error;
	}
	
	
}
