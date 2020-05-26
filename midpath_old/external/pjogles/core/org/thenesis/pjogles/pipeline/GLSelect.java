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

import org.thenesis.pjogles.GLInvalidOperationException;
import org.thenesis.pjogles.GLInvalidValueException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.GLStackOverflowException;
import org.thenesis.pjogles.GLStackUnderflowException;
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLBitmap;
import org.thenesis.pjogles.primitives.GLPolygon;
import org.thenesis.pjogles.render.GLRenderException;

/**
 * An OpenGL select renderer.
 *
 * @see org.thenesis.pjogles.GL#glRenderMode glRenderMode
 *
 * @author tdinneen
 */
public final class GLSelect implements GLRenderer {
	private final GLSoftwareContext gc;

	/**
	 * This is true when the last primitive to execute hit (intersected)
	 * the selection box.  Whenever the name stack is manipulated this
	 * bit is cleared.
	 */
	private boolean hit;

	/**
	 * Name stack.
	 */
	private final int[] stack = new int[MAX_NAME_STACK_DEPTH];
	private int stackIndex = 0;

	/**
	 * The user specified result array overflows, this bit is set.
	 */
	public boolean overFlowed;

	/**
	 * User specified result array.  As primitives are processed names
	 * will be entered into this array.
	 */
	public int[] resultBase;

	/**
	 * The number of GLint's that the array can hold.
	 */
	private int resultLength;

	/**
	 * Current pointer into the result array.
	 */
	private int resultIndex = 0;

	/**
	 * Number of hits
	 */
	public int hits;

	/**
	 * Pointer to z values for last hit.
	 */
	private int minz;
	private int maxz;

	public GLSelect(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void reset() {
		overFlowed = GL_FALSE;
		stackIndex = 0;
		hit = GL_FALSE;
		hits = 0;
		minz = 0;
		maxz = 0;
		resultIndex = 0;
	}

	public final void glSelectBuffer(final int bufferLength, final int[] buffer) {
		// The glSelectBuffer function has two parameters: buffer is a pointer to an array
		// of unsigned integers, and size indicates the size of the array. The buffer
		// parameter returns values from the name stack (see glInitNames, glLoadName,
		// glPushName) when the rendering mode is GL_SELECT (see glRenderMode).
		// The glSelectBuffer function must be issued before selection mode is enabled,
		// and it must not be issued while the rendering mode is GL_SELECT.

		// Selection is used by a programmer to determine which primitives are drawn into
		// some region of a window. The region is defined by the current modelview and
		// perspective matrices.

		// In selection mode, no pixel fragments are produced from rasterization. Instead,
		// if a primitive intersects the clip volume defined by the viewing frustum and the
		// user-defined clipping planes, this primitive causes a selection hit.
		// (With polygons, no hit occurs if the polygon is culled.) When a change is made
		// to the name stack, or when glRenderMode is called, a hit record is copied to
		// buffer if any hits have occurred since the last such event (either a name stack
		// change or a glRenderMode call). The hit record consists of the number of names
		// in the name stack at the time of the event; followed by the minimum and maximum
		// depth values of all vertices that hit since the previous event; followed by the
		// name stack contents, bottom name first.

		// Returned depth values are mapped such that the largest unsigned integer value
		// corresponds to window coordinate depth 1.0, and zero corresponds to window
		// coordinate depth 0.0.

		// An internal index into buffer is reset to zero whenever selection mode is entered.
		// Each time a hit record is copied into buffer, the index is incremented to point
		// to the cell just past the end of the block of names—that is, to the next
		// available cell. If the hit record is larger than the number of remaining
		// locations in buffer, as much demos.data as can fit is copied, and the overflow flag is
		// set. If the name stack is empty when a hit record is copied, that record consists
		// of zero followed by the minimum and maximum depth values.

		// Selection mode is exited by calling glRenderMode with an argument other than
		// GL_SELECT. Whenever glRenderMode is called while the render mode is GL_SELECT, it
		// returns the number of hit records copied to buffer, resets the overflow flag
		// and the selection buffer pointer, and initializes the name stack to be empty.
		// If the overflow bit was set when glRenderMode was called, a negative hit record
		// count is returned.

		if (bufferLength < 0)
			throw new GLInvalidValueException("glSelectBuffer(int, int[])");

		if (gc.renderMode == GL_SELECT)
			throw new GLInvalidOperationException("glSelectBuffer(int, int[])");

		resultBase = buffer;
		resultLength = bufferLength;

		overFlowed = GL_FALSE;
		resultIndex = 0;
	}

	public final void glInitNames() {
		// The glInitNames function causes the name stack to be initialized to its default
		// empty state. The name stack is used during selection mode to allow sets of
		// rendering commands to be uniquely identified. It consists of an ordered set of
		// unsigned integers.

		// The name stack is always empty while the render mode is not GL_SELECT.
		// Calls to glInitNames while the render mode is not GL_SELECT are ignored.

		if (gc.renderMode == GL_SELECT) {
			stackIndex = 0;
			hit = GL_FALSE;
		}
	}

	public final void glLoadName(final int name) {
		// The glLoadName function causes name to replace the value on the top of the name stack,
		// which is initially empty. The name stack is used during selection mode to allow sets of
		// rendering commands to be uniquely identified. It consists of an ordered set of unsigned
		// integers.

		// The name stack is always empty while the render mode is not GL_SELECT. Calls to
		// glLoadName while the render mode is not GL_SELECT are ignored.

		if (gc.renderMode == GL_SELECT) {
			if (stackIndex == 0)
				throw new GLInvalidOperationException("glLoadName(int)");

			stack[stackIndex - 1] = name;
			hit = GL_FALSE;
		}
	}

	public final void glPopName() {
		// The glPushName function causes name to be pushed onto the name stack, which is
		// initially empty. The glPopName function pops one name off the top of the stack.
		// The name stack is used during selection mode to allow sets of rendering commands to
		// be uniquely identified. It consists of an ordered set of unsigned integers.

		// The name stack is always empty while the render mode is not GL_SELECT.
		// Calls to glPushName or glPopName while the render mode is not GL_SELECT are ignored.

		// It is an error to push a name onto a full stack, or to pop a name off an empty stack.
		// It is also an error to manipulate the name stack between a call to glBegin and the
		// corresponding call to glEnd. In any of these cases, the error flag is set and no
		// other change is made to the OpenGL state.

		if (gc.renderMode == GL_SELECT) {
			if (stackIndex <= 0)
				throw new GLStackUnderflowException("glPopName()");

			--stackIndex;
			hit = GL_FALSE;
		}
	}

	public final void glPushName(final int name) {
		// The glPushName function causes name to be pushed onto the name stack, which is
		// initially empty. The glPopName function pops one name off the top of the stack.
		// The name stack is used during selection mode to allow sets of rendering commands to
		// be uniquely identified. It consists of an ordered set of unsigned integers.

		// The name stack is always empty while the render mode is not GL_SELECT.
		// Calls to glPushName or glPopName while the render mode is not GL_SELECT are ignored.

		// It is an error to push a name onto a full stack, or to pop a name off an empty stack.
		// It is also an error to manipulate the name stack between a call to glBegin and the
		// corresponding call to glEnd. In any of these cases, the error flag is set and no
		// other change is made to the OpenGL state.

		if (gc.renderMode == GL_SELECT) {
			if (stackIndex >= MAX_NAME_STACK_DEPTH)
				throw new GLStackOverflowException("glPushName(int)");

			stack[stackIndex++] = name;
			hit = GL_FALSE;
		}
	}

	public final void render(final GLVertex v) {
		selectHit(v.window.z);
	}

	public final void render(final GLVertex v1, final GLVertex v2) {
		// we only need to store the min and max z values

		selectHit(v1.window.z);
		selectHit(v2.window.z);
	}

	public final void render(final GLPolygon polygon) throws GLRenderException {
		final GLVertex[] vertices = polygon.vertices;

		final int numberOfVertices = polygon.numberOfVertices;

		if (numberOfVertices < 3)
			throw new GLRenderException("GLSelectRenderer.renderPolygon(GLPolygon) - The number of GLVertex's is < 3");

		// take into account culling

		if (gc.state.enables.cullFace) {
			final int cull = gc.state.polygon.cull;

			if (cull == GL_FRONT_AND_BACK)
				return;
			else {
				final GLVertex v0 = vertices[0];
				final GLVertex v1 = vertices[1];
				final GLVertex v2 = vertices[2];

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

				if (facing == cull)
					return;
			}
		}

		// we only need to store the min and max z values, so just
		// hit on each vertices z value

		for (int i = 0; i < polygon.numberOfVertices; ++i)
			selectHit(polygon.vertices[i].window.z);
	}

	public final void render(final GLBitmap bitmap) {
		// Check if current colorBuffer position is valid.  Do not render if invalid.
		// Also, if selection is in progress skip the rendering of the
		// bitmap. Bitmaps are invisible to selection and do not generate
		// selection hits.

		if (!gc.state.current.validRasterPosition)
			return;

		final GLVector4f v = gc.state.current.raster.window;

		//  Advance current colorBuffer position.

		v.x += bitmap.xMove;
		v.y -= bitmap.yMove;
	}

	private void selectHit(final float z) {
		if (overFlowed)
			return;

		final int iz = (int) ((float) Integer.MAX_VALUE * z);

		if (!hit) {
			hit = GL_TRUE;

			// Put number of elements in name stack out first

			if (resultIndex >= resultLength) {
				overFlowed = GL_TRUE;
				return;
			}

			resultBase[resultIndex++] = stackIndex;
			++hits;

			// Put out smallest z
			if (resultIndex >= resultLength) {
				overFlowed = GL_TRUE;
				return;
			}

			resultBase[minz = resultIndex++] = iz;

			// Put out largest z
			if (resultIndex >= resultLength) {
				overFlowed = GL_TRUE;
				return;
			}

			resultBase[maxz = resultIndex++] = iz;

			// Copy name stack into output buffer
			for (int i = 0; i < stackIndex; ++i) {
				if (resultIndex >= resultLength) {
					overFlowed = GL_TRUE;
					return;
				}

				resultBase[resultIndex++] = stack[i];
			}
		} else {
			// update range of Z values
			if (iz < resultBase[minz])
				resultBase[minz] = iz;

			if (iz > resultBase[maxz])
				resultBase[maxz] = iz;
		}
	}
}
