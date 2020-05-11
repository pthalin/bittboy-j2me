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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

/**
 * All the classes and methods required to implement a display list.
 * <p>
 * Currently it's a naive record and replay implementation, in that
 * we record (add to a list) all the calls made during a display list
 * and replay them back (iterate over the list) one by one. No optimisation
 * techniques are performed.
 * <p>
 * This is really a subproject in it's own right, providing
 * a plug and play framework for different display list optimisation techniques.
 *
 * @author tdinneen
 */
public final class GLDisplayList implements GLConstants {
	public interface DListNode {
		public abstract void execute(GL11 gl);
	}

	private abstract class DListNodeI implements DListNode {
		protected final int i;

		public DListNodeI(final int i) {
			this.i = i;
		}
	}

	private abstract class DListNodeF implements DListNode {
		protected final float f;

		public DListNodeF(final float f) {
			this.f = f;
		}
	}

	private abstract class DListNodeZ implements DListNode {
		protected final boolean z;

		public DListNodeZ(final boolean z) {
			this.z = z;
		}
	}

	private abstract class DListNodeIV implements DListNode {
		protected final int i1;
		protected final int i2;
		protected final int i3;
		protected final int i4;

		public DListNodeIV(final int i1, final int i2) {
			this.i1 = i1;
			this.i2 = i2;
			this.i3 = 0;
			this.i4 = 0;
		}

		public DListNodeIV(final int i1, final int i2, final int i3) {
			this.i1 = i1;
			this.i2 = i2;
			this.i3 = i3;
			this.i4 = 0;
		}

		public DListNodeIV(final int i1, final int i2, final int i3, final int i4) {
			this.i1 = i1;
			this.i2 = i2;
			this.i3 = i3;
			this.i4 = i4;
		}
	}

	private abstract class DListNodeFV implements DListNode {
		protected final float f1;
		protected final float f2;
		protected final float f3;
		protected final float f4;

		public DListNodeFV(final float f1, final float f2) {
			this.f1 = f1;
			this.f2 = f2;
			this.f3 = 0.0f;
			this.f4 = 0.0f;
		}

		public DListNodeFV(final float f1, final float f2, final float f3) {
			this.f1 = f1;
			this.f2 = f2;
			this.f3 = f3;
			this.f4 = 0.0f;
		}

		public DListNodeFV(final float f1, final float f2, final float f3, final float f4) {
			this.f1 = f1;
			this.f2 = f2;
			this.f3 = f3;
			this.f4 = f4;
		}
	}

	private abstract class DListNodeIF implements DListNode {
		protected final int i;
		protected final float f;

		public DListNodeIF(final int i, final float f) {
			this.i = i;
			this.f = f;
		}
	}

	private abstract class DListNodeBV implements DListNode {
		protected final byte[] b;

		public DListNodeBV(final byte[] b) {
			final int length = b.length;
			this.b = new byte[length];

			System.arraycopy(b, 0, this.b, 0, length);
		}
	}

	private abstract class DListNodeIIV implements DListNode {
		protected final int i1;
		protected final int[] iv;

		public DListNodeIIV(final int i, final int[] iv) {
			this.i1 = i;
			this.iv = new int[iv.length];

			System.arraycopy(iv, 0, this.iv, 0, iv.length);
		}
	}

	private abstract class DListNodeIFV implements DListNode {
		protected final int i1;
		protected final int i2;

		protected final float[] f;

		public DListNodeIFV(final int i, final float[] f) {
			this.i1 = i;
			this.i2 = 0;
			this.f = new float[f.length];

			System.arraycopy(f, 0, this.f, 0, f.length);
		}

		public DListNodeIFV(final int i1, final int i2, final float[] f) {
			this.i1 = i1;
			this.i2 = i2;
			this.f = new float[f.length];

			System.arraycopy(f, 0, this.f, 0, f.length);
		}
	}

	private abstract class DListNodeIVFV implements DListNode {
		protected final int[] i;
		protected final float[] f;

		public DListNodeIVFV(final int[] i, final float[] f) {
			this.i = new int[i.length];
			this.f = new float[f.length];

			System.arraycopy(i, 0, this.i, 0, i.length);
			System.arraycopy(f, 0, this.f, 0, f.length);
		}
	}

	private final GLSoftwareContext gc;
	private List currentDisplayList;

	public int currentList;
	private int mode;

	public GLDisplayList(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final int glGenLists(final int range) {
		final Hashtable displayLists = gc.state.shared.displayLists;
		int i = 1, j = 0, firstempty = 0;

		while (i < displayLists.size() && j < range) {
			if (displayLists.get(new Integer(i)) == null) {
				if (j == 0)
					firstempty = i;

				++i;
				++j;
			} else {
				++i;
				j = 0;
			}
		}

		if (j == range)
			return firstempty;
		else
			return i;
	}

	public final void glNewList(final int list, final int mode) {
		currentList = list;
		this.mode = mode;

		currentDisplayList = new LinkedList();
	}

	public final void glEndList(final GL11 gl) {
		compileCurrentList(mode == GL_COMPILE_AND_EXECUTE, gl);
		currentList = 0;
	}

	public final void glCallList(final int list, final GL11 gl) {
		final List displayList = (List) gc.state.shared.displayLists.get(new Integer(list));

		if (displayList != null) {
			final Iterator i = displayList.iterator();

			while (i.hasNext())
				((DListNode) i.next()).execute(gl);
		}

		// TOMD - should we do something? display list does not exist
	}

	public final void glDeleteLists(final int list, final int range) {
		final Hashtable displayLists = gc.state.shared.displayLists;

		// All storage locations allocated to the specified display lists are freed,
		// and the names are available for reuse at a later time. Names within the range
		// that do not have an associated display list are ignored.

		for (int i = list; i < range; ++i)
			displayLists.put(new Integer(i), null);
	}

	private void compileCurrentList(final boolean execute, final GL11 gl) {
		gc.state.shared.displayLists.put(new Integer(currentList), currentDisplayList);

		if (execute)
			glCallList(currentList, gl);
	}

	public final void glBegin(final int mode) {
		currentDisplayList.add(new DListNodeI(mode) {
			public void execute(final GL11 gl) {
				gl.glBegin(i);
			}
		});
	}

	public final void glEnd() {
		currentDisplayList.add(new DListNode() {
			public void execute(final GL11 gl) {
				gl.glEnd();
			}
		});
	}

	public final void glVertex4f(final float x, final float y, final float z, final float w) {
		currentDisplayList.add(new DListNodeFV(x, y, z, w) {
			public void execute(final GL11 gl) {
				gl.glVertex4f(f1, f2, f3, f4);
			}
		});
	}

	public final void glColor4f(final float r, final float g, final float b, final float a) {
		currentDisplayList.add(new DListNodeFV(r, g, b, a) {
			public void execute(final GL11 gl) {
				gl.glColor4f(f1, f2, f3, f4);
			}
		});
	}

	public final void glNormal3f(final float x, final float y, final float z) {
		currentDisplayList.add(new DListNodeFV(x, y, z) {
			public void execute(final GL11 gl) {
				gl.glNormal3f(f1, f2, f3);
			}
		});
	}

	public final void glTranslatef(final float x, final float y, final float z) {
		currentDisplayList.add(new DListNodeFV(x, y, z) {
			public void execute(final GL11 gl) {
				gl.glTranslatef(f1, f2, f3);
			}
		});
	}

	public final void glRotatef(final float angle, final float x, final float y, final float z) {
		currentDisplayList.add(new DListNodeFV(angle, x, y, z) {
			public void execute(final GL11 gl) {
				gl.glRotatef(f1, f2, f3, f4);
			}
		});
	}

	public final void glScalef(final float x, final float y, final float z) {
		currentDisplayList.add(new DListNodeFV(x, y, z) {
			public void execute(final GL11 gl) {
				gl.glScalef(f1, f2, f3);
			}
		});
	}

	public final void glClearColor(final float r, final float g, final float b, final float a) {
		currentDisplayList.add(new DListNodeFV(r, g, b, a) {
			public void execute(final GL11 gl) {
				gl.glClearColor(f1, f2, f3, f4);
			}
		});
	}

	public final void glClearAccum(final float r, final float g, final float b, final float a) {
		currentDisplayList.add(new DListNodeFV(r, g, b, a) {
			public void execute(final GL11 gl) {
				gl.glClearAccum(f1, f2, f3, f4);
			}
		});
	}

	public final void glClear(final int bitfield) {
		currentDisplayList.add(new DListNodeI(bitfield) {
			public void execute(final GL11 gl) {
				gl.glClear(i);
			}
		});
	}

	public final void glMatrixMode(final int mode) {
		currentDisplayList.add(new DListNodeI(mode) {
			public void execute(final GL11 gl) {
				gl.glMatrixMode(i);
			}
		});
	}

	public final void glLoadIdentity() {
		currentDisplayList.add(new DListNode() {
			public void execute(final GL11 gl) {
				gl.glLoadIdentity();
			}
		});
	}

	public final void glPushMatrix() {
		currentDisplayList.add(new DListNode() {
			public void execute(final GL11 gl) {
				gl.glPushMatrix();
			}
		});
	}

	public final void glPopMatrix() {
		currentDisplayList.add(new DListNode() {
			public void execute(final GL11 gl) {
				gl.glPopMatrix();
			}
		});
	}

	public final void glFrontFace(final int direction) {
		currentDisplayList.add(new DListNodeI(direction) {
			public void execute(final GL11 gl) {
				gl.glFrontFace(i);
			}
		});
	}

	public final void glCullFace(final int cull) {
		currentDisplayList.add(new DListNodeI(cull) {
			public void execute(final GL11 gl) {
				gl.glCullFace(i);
			}
		});
	}

	public final void glMaterialfv(final int face, final int p, final float[] pv) {
		currentDisplayList.add(new DListNodeIFV(face, p, pv) {
			public void execute(final GL11 gl) {
				gl.glMaterialfv(i1, i2, f);
			}
		});
	}

	public final void glIndexf(final float i) {
		currentDisplayList.add(new DListNodeF(i) {
			public void execute(final GL11 gl) {
				gl.glIndexf(f);
			}
		});
	}

	public final void glLineStipple(final int factor, final short stipple) {
		currentDisplayList.add(new DListNodeIV(factor, stipple) {
			public void execute(final GL11 gl) {
				gl.glLineStipple(i1, (short) i2);
			}
		});
	}

	public final void glPolygonStipple(final byte[] stipple) {
		currentDisplayList.add(new DListNodeBV(stipple) {
			public void execute(final GL11 gl) {
				gl.glPolygonStipple(b);
			}
		});
	}

	public final void glRectf(final float x0, final float y0, final float x1, final float y1) {
		currentDisplayList.add(new DListNodeFV(x0, y0, x1, y1) {
			public void execute(final GL11 gl) {
				gl.glRectf(f1, f2, f3, f4);
			}
		});
	}

	public final void glShadeModel(final int shadeModel) {
		currentDisplayList.add(new DListNodeI(shadeModel) {
			public void execute(final GL11 gl) {
				gl.glShadeModel(i);
			}
		});
	}

	public final void glLightfv(final int light, final int p, final float[] pv) {
		currentDisplayList.add(new DListNodeIFV(light, p, pv) {
			public void execute(final GL11 gl) {
				gl.glLightfv(i1, i2, f);
			}
		});
	}

	public final void glLightModeliv(final int p, final int[] pv) {
		currentDisplayList.add(new DListNodeIIV(p, pv) {
			public void execute(final GL11 gl) {
				gl.glLightModeliv(i1, iv);
			}
		});
	}

	public final void glLightModelfv(final int p, final float[] pv) {
		currentDisplayList.add(new DListNodeIFV(p, pv) {
			public void execute(final GL11 gl) {
				gl.glLightModelfv(i1, f);
			}
		});
	}

	public void glPixelTransferf(final int pname, final float param) {
		currentDisplayList.add(new DListNodeIF(pname, param) {
			public void execute(final GL11 gl) {
				gl.glPixelTransferf(i, f);
			}
		});
	}

	public final void glPixelStorei(final int mode, final int value) {
		currentDisplayList.add(new DListNodeIV(mode, value) {
			public void execute(final GL11 gl) {
				gl.glPixelStorei(i1, i2);
			}
		});
	}

	public final void glTexImage1D(final int target, final int level, final int components, final int length,
			final int border, final int format, final int type, final Object pixels) {
	}

	public final void glTexImage2D(final int target, final int level, final int components, final int width,
			final int height, final int border, final int format, final int type, final Object pixels) {
	}

	public final void glTexParameterf(final int target, final int pname, final float f) {
	}

	public final void glTexParameterfv(final int target, final int pname, final float[] pv) {
	}

	public final void glTexCoord1f(final float t) {
	}

	public final void glTexCoord1fv(final float[] t) {
	}

	public final void glTexCoord2f(final float x, final float y) {
	}

	public final void glTexCoord2fv(final float[] t) {
	}

	public final void glTexCoord3fv(final float[] t) {
	}

	public final void glTexCoord4fv(final float[] t) {
	}

	public final void glEnable(final int capability) {
		currentDisplayList.add(new DListNodeI(capability) {
			public void execute(final GL11 gl) {
				gl.glEnable(i);
			}
		});
	}

	public final void glDisable(final int capability) {
		currentDisplayList.add(new DListNodeI(capability) {
			public void execute(final GL11 gl) {
				gl.glDisable(i);
			}
		});
	}

	public final void glPushAttrib(final int mask) {
		currentDisplayList.add(new DListNodeI(mask) {
			public void execute(final GL11 gl) {
				gl.glPushAttrib(i);
			}
		});
	}

	public final void glPopAttrib() {
		currentDisplayList.add(new DListNode() {
			public void execute(final GL11 gl) {
				gl.glPopAttrib();
			}
		});
	}

	public final void glPointSize(final float size) {
		currentDisplayList.add(new DListNodeF(size) {
			public void execute(final GL11 gl) {
				gl.glPointSize(f);
			}
		});
	}

	public final void glLineWidth(final float width) {
		currentDisplayList.add(new DListNodeF(width) {
			public void execute(final GL11 gl) {
				gl.glLineWidth(f);
			}
		});
	}

	public final void glPolygonMode(final int face, final int mode) {
		currentDisplayList.add(new DListNodeIV(face, mode) {
			public void execute(final GL11 gl) {
				gl.glPolygonMode(i1, i2);
			}
		});
	}

	public final void glClearDepth(final float z) {
		currentDisplayList.add(new DListNodeF(z) {
			public void execute(final GL11 gl) {
				gl.glClearDepth(f);
			}
		});
	}

	public final void glDepthFunc(final int zf) {
		currentDisplayList.add(new DListNodeI(zf) {
			public void execute(final GL11 gl) {
				gl.glClearDepth(i);
			}
		});
	}

	public final void glDepthMask(final boolean enabled) {
		currentDisplayList.add(new DListNodeZ(enabled) {
			public void execute(final GL11 gl) {
				gl.glDepthMask(z);
			}
		});
	}

	public final void glClipPlane(final int plane, final float[] equation) {
		currentDisplayList.add(new DListNodeIFV(plane, equation) {
			public void execute(final GL11 gl) {
				gl.glClipPlane(i1, f);
			}
		});
	}
}
