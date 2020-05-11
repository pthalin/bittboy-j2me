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

package org.thenesis.pjogles.glu;

import org.thenesis.pjogles.GL11;
import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.math.GLMatrix4f;
import org.thenesis.pjogles.math.GLVector4f;

/**
 * The GL Utilities class.
 *
 * @author tdinneen
 */
public final class GLU implements GLConstants, GLUConstants {
	private final GL11 gl;

	private final GLMatrix4f m1 = new GLMatrix4f();
	private final GLMatrix4f m2 = new GLMatrix4f();

	private final GLVector4f v1 = new GLVector4f();
	private final GLVector4f v2 = new GLVector4f();
	private final GLVector4f v3 = new GLVector4f();

	public GLU(final GL11 gl) {
		this.gl = gl;
	}

	public final void gluOrtho2D(final float left, final float right, final float bottom, final float top) {
		gl.glOrtho(left, right, bottom, top, -1.0f, 1.0f);
	}

	public final void gluPerspective(final float fovy, final float aspect, final float zNear, final float zFar) {
		final float ymax = zNear * (float) Math.tan(fovy * Math.PI / 360.0f);
		final float ymin = -ymax;

		final float xmin = ymin * aspect;
		final float xmax = ymax * aspect;

		gl.glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	public final void gluLookAt(final float eyex, final float eyey, final float eyez, final float centerx,
			final float centery, final float centerz, final float upx, final float upy, final float upz) {
		final GLVector4f x = v1;
		final GLVector4f y = v2;
		final GLVector4f z = v3;

		// Make rotation matrix

		// Z vector
		z.x = eyex - centerx;
		z.y = eyey - centery;
		z.z = eyez - centerz;

		z.normalize();

		// Y vector
		y.x = upx;
		y.y = upy;
		y.z = upz;

		// X vector = Y cross Z
		x.x = y.y * z.z - y.z * z.y;
		x.y = -y.x * z.z + y.z * z.x;
		x.z = y.x * z.y - y.y * z.x;

		// Recompute Y = Z cross X
		y.x = z.y * x.z - z.z * x.y;
		y.y = -z.x * x.z + z.z * x.x;
		y.z = z.x * x.y - z.y * x.x;

		x.normalize();
		y.normalize();

		m1.set(x, y, z);
		gl.glMultMatrixf(m1.m);

		// Translate Eye to Origin
		gl.glTranslatef(-eyex, -eyey, -eyez);
	}

	public final void gluPickMatrix(final float x, final float y, final float width, final float height,
			final int viewport[]) {
		final float sx = (float) viewport[2] / width;
		final float sy = (float) viewport[3] / height;
		final float tx = ((float) viewport[2] + 2.0f * ((float) viewport[0] - x)) / width;
		final float ty = ((float) viewport[3] + 2.0f * ((float) viewport[1] - y)) / height;

		m1.setIdentity();

		m1.m[0] = sx;
		m1.m[5] = sy;
		m1.m[12] = tx;
		m1.m[13] = ty;

		gl.glMultMatrixf(m1.m);
	}

	public final boolean gluProject(final float objx, final float objy, final float objz, final float[] modelMatrix,
			final float[] projMatrix, final int[] viewport, final float[] winx, final float[] winy, final float[] winz) {
		final GLVector4f in = v1;
		in.set(objx, objy, objz, 1.0f);

		final GLVector4f out = v2;

		GLMatrix4f.transform(in, modelMatrix, out);
		GLMatrix4f.transform(out, projMatrix, in);

		if (in.w == 0.0f)
			return GL_FALSE;

		in.x /= in.w;
		in.y /= in.w;
		in.z /= in.w;

		// Map x, y and z to range 0-1
		in.x = in.x * 0.5f + 0.5f;
		in.y = in.y * 0.5f + 0.5f;
		in.z = in.z * 0.5f + 0.5f;

		// map x,y to viewport
		in.x = in.x * viewport[2] + viewport[0];
		in.y = in.y * viewport[3] + viewport[1];

		winx[0] = in.x;
		winy[0] = in.y;
		winz[0] = in.z;

		return GL_TRUE;
	}

	public final boolean gluUnProject(final float winx, final float winy, final float winz, final float[] modelMatrix,
			final float[] projMatrix, final int[] viewport, final float[] objx, final float[] objy, final float[] objz) {
		GLMatrix4f.multiply(modelMatrix, projMatrix, m1);

		if (!m1.invert(m2))
			return GL_FALSE;

		final GLVector4f in = v1;
		in.set(winx, winy, winz, 1.0f);

		// map x and y from window coordinates
		in.x = (in.x - viewport[0]) / viewport[2];
		in.x = (in.x - viewport[1]) / viewport[3];

		// map to range -1 to 1
		in.x = in.x * 2 - 1;
		in.y = in.y * 2 - 1;
		in.z = in.z * 2 - 1;

		final GLVector4f out = v2;
		GLMatrix4f.transform(in, m2, out);

		if (out.w == 0.0)
			return GL_FALSE;

		out.x /= out.w;
		out.y /= out.w;
		out.z /= out.w;

		objx[0] = out.x;
		objy[0] = out.y;
		objz[0] = out.z;

		return GL_TRUE;
	}

	public final GLUQuadric gluNewQuadric() {
		final GLUQuadric q = new GLUQuadric();

		q.drawStyle = GLU_FILL;
		q.orientation = GLU_OUTSIDE;
		q.textureCoordinates = GL_FALSE;
		q.normals = GLU_SMOOTH;

		return q;
	}

	public final void gluQuadricDrawStyle(final GLUQuadric quadric, final int drawStyle) {
		if (quadric == null)
			throw new IllegalArgumentException(
					"gluQuadricDrawStyle(GLUQuadric, int) - The supplied GLUQuadric is null.");

		switch (drawStyle) {
		case GLU_FILL:
		case GLU_LINE:
		case GLU_SILHOUETTE:
		case GLU_POINT:
			quadric.drawStyle = drawStyle;
			break;
		default:
			throw new GLUInvalidEnumException("gluQuadricDrawStyle(GLUQuadric, int)");
		}
	}

	public final void gluQuadricNormals(final GLUQuadric quadric, final int normals) {
		if (quadric == null)
			throw new IllegalArgumentException("gluQuadricNormals(GLUQuadric, int) - The supplied GLUQuadric is null.");

		switch (normals) {
		case GLU_NONE:
		case GLU_FLAT:
		case GLU_SMOOTH:
			quadric.normals = normals;
			break;
		default:
			throw new GLUInvalidEnumException("gluQuadricNormals(GLUQuadric, int)");
		}
	}

	public final void gluQuadricOrientation(final GLUQuadric quadric, final int orientation) {
		if (quadric == null)
			throw new IllegalArgumentException(
					"gluQuadricOrientation(GLUQuadric, int) - The supplied GLUQuadric is null.");

		switch (orientation) {
		case GLU_OUTSIDE:
		case GLU_INSIDE:
			quadric.orientation = orientation;
			break;
		default:
			throw new GLUInvalidEnumException("gluQuadricOrientation(GLUQuadric, int)");
		}
	}

	public final void gluQuadricTexture(final GLUQuadric quadric, final boolean textureCoordinates) {
		if (quadric == null)
			throw new IllegalArgumentException(
					"gluQuadricTexture(GLUQuadric, boolean) - The supplied GLUQuadric is null.");

		quadric.textureCoordinates = textureCoordinates;
	}

	public final void gluCylinder(final GLUQuadric quadric, final float baseRadius, final float topRadius,
			final float height, final int slices, final int stacks) {
		if (quadric == null)
			throw new IllegalArgumentException(
					"gluCylinder(GLUQuadric, float, float, int, int) - The supplied GLUQuadric is null.");

		final boolean normals;

		if (quadric.normals == GLU_NONE)
			normals = false;
		else
			normals = true;

		final float nsign;

		if (quadric.orientation == GLU_INSIDE)
			nsign = -1.0f;
		else
			nsign = 1.0f;

		final float da = 2.0f * (float) Math.PI / slices;
		final float dr = (topRadius - baseRadius) / stacks;
		final float dz = height / stacks;

		// Z component of normal vectors
		final float nz = (baseRadius - topRadius) / height;

		float x, y, z, r;

		if (quadric.drawStyle == GLU_POINT) {
			gl.glBegin(GL_POINTS);

			for (int i = 0; i < slices; i++) {
				x = (float) (Math.cos((double) i * da));
				y = (float) (Math.sin((double) i * da));

				if (normals)
					gl.glNormal3f(x * nsign, y * nsign, nz * nsign);

				z = 0.0f;
				r = baseRadius;

				for (int j = 0; j <= stacks; j++) {
					gl.glVertex3f(x * r, y * r, z);

					z += dz;
					r += dr;
				}
			}

			gl.glEnd();
		} else if (quadric.drawStyle == GLU_LINE || quadric.drawStyle == GLU_SILHOUETTE) {
			// draw rings

			if (quadric.drawStyle == GLU_LINE) {
				z = 0.0f;
				r = baseRadius;

				for (int j = 0; j <= stacks; j++) {
					gl.glBegin(GL_LINE_LOOP);

					for (int i = 0; i < slices; i++) {
						x = (float) (Math.cos((double) i * da));
						y = (float) (Math.sin((double) i * da));

						if (normals)
							gl.glNormal3f(x * nsign, y * nsign, nz * nsign);

						gl.glVertex3f(x * r, y * r, z);
					}

					gl.glEnd();

					z += dz;
					r += dr;
				}
			} else {
				// draw one ring at each end

				if (baseRadius != 0.0) {
					gl.glBegin(GL_LINE_LOOP);

					for (int i = 0; i < slices; i++) {
						x = (float) (Math.cos((double) i * da));
						y = (float) (Math.sin((double) i * da));

						if (normals)
							gl.glNormal3f(x * nsign, y * nsign, nz * nsign);

						gl.glVertex3f(x * baseRadius, y * baseRadius, 0.0f);
					}

					gl.glEnd();
					gl.glBegin(GL_LINE_LOOP);

					for (int i = 0; i < slices; i++) {
						x = (float) (Math.cos((double) i * da));
						y = (float) (Math.sin((double) i * da));

						if (normals)
							gl.glNormal3f(x * nsign, y * nsign, nz * nsign);

						gl.glVertex3f(x * topRadius, y * topRadius, height);
					}

					gl.glEnd();
				}
			}

			gl.glBegin(GL_LINES); // draw length lines

			for (int i = 0; i < slices; ++i) {
				x = (float) (Math.cos((double) i * da));
				y = (float) (Math.sin((double) i * da));

				if (normals)
					gl.glNormal3f(x * nsign, y * nsign, nz * nsign);

				gl.glVertex3f(x * baseRadius, y * baseRadius, 0.0f);
				gl.glVertex3f(x * topRadius, y * topRadius, height);
			}

			gl.glEnd();
		} else if (quadric.drawStyle == GLU_FILL) {
			float x1, y1, x2, y2;

			for (int i = 0; i < slices; ++i) {
				x1 = (float) (Math.cos((double) i * da));
				y1 = (float) (Math.sin((double) i * da));
				x2 = (float) (Math.cos((double) (i + 1) * da));
				y2 = (float) (Math.sin((double) (i + 1) * da));

				z = 0.0f;
				r = baseRadius;

				gl.glBegin(GL_QUAD_STRIP);

				for (int j = 0; j <= stacks; ++j) {
					if (nsign == 1.0) {
						if (normals)
							gl.glNormal3f(x1 * nsign, y1 * nsign, nz * nsign);

						gl.glVertex3f(x1 * r, y1 * r, z);

						if (normals)
							gl.glNormal3f(x2 * nsign, y2 * nsign, nz * nsign);

						gl.glVertex3f(x2 * r, y2 * r, z);
					} else {
						if (normals)
							gl.glNormal3f(x2 * nsign, y2 * nsign, nz * nsign);

						gl.glVertex3f(x2 * r, y2 * r, z);

						if (normals)
							gl.glNormal3f(x1 * nsign, y1 * nsign, nz * nsign);

						gl.glVertex3f(x1 * r, y1 * r, z);
					}

					z += dz;
					r += dr;
				}

				gl.glEnd();
			}
		}
	}

	public final void gluSphere(final GLUQuadric quadric, final float radius, final int slices, final int stacks) {
		if (quadric == null)
			throw new IllegalArgumentException(
					"gluSphere(GLUQuadric, float, int, int) - The supplied GLUQuadric is null.");

		/*
		 float rho, theta;
		 float x, y, z;
		 double sin;
		 int i, j;
		 final boolean normals;
		 final float nsign;

		 if (quadric.normals == GLU_NONE)
		 normals = false;
		 else
		 normals = true;

		 if (quadric.orientation == GLU_INSIDE)
		 nsign = -1.0f;
		 else
		 nsign = 1.0f;

		 final float drho = (float) Math.PI / (float) stacks;
		 final float dtheta = (float) 2.0 * (float) Math.PI / (float) slices;

		 if (quadric.drawStyle == GLU_FILL)
		 {
		 // draw +Z end as a triangle fan
		 gl.glBegin(GL_TRIANGLE_FAN);

		 if (normals)
		 gl.glNormal3f(0.0f, 0.0f, 1.0f);

		 gl.glVertex3f(0.0f, 0.0f, nsign * radius);

		 for (j = 0; j <= slices; j++)
		 {
		 if (j == slices)
		 theta = 0.0f;
		 else
		 theta = (float) j * dtheta;

		 sin = Math.sin(drho);

		 x = (float) (Math.cos(theta) * sin);
		 y = (float) (Math.sin(theta) * sin);
		 z = nsign * (float) Math.cos(drho);

		 if (normals)
		 gl.glNormal3f(x * nsign, y * nsign, z * nsign);

		 gl.glVertex3f(x * radius, y * radius, z * radius);
		 }

		 gl.glEnd();

		 // draw intermediate stacks as quad strips
		 for (i = 1; i < stacks - 1; i++)
		 {
		 rho = i * drho;
		 gl.glBegin(GL_QUAD_STRIP);

		 for (j = 0; j <= slices; j++)
		 {
		 if (j == slices)
		 theta = 0.0f;
		 else
		 theta = (float) j * dtheta;

		 sin = Math.sin(rho);

		 x = (float) (Math.cos(theta) * sin);
		 y = (float) (Math.sin(theta) * sin);
		 z = nsign * (float) Math.cos(rho);

		 if (normals)
		 gl.glNormal3f(x * nsign, y * nsign, z * nsign);

		 gl.glVertex3f(x * radius, y * radius, z * radius);

		 sin = Math.sin(rho + drho);

		 x = (float) (Math.cos(theta) * sin);
		 y = (float) (Math.sin(theta) * sin);
		 z = nsign * (float) Math.cos(rho + drho);

		 if (normals)
		 gl.glNormal3f(x * nsign, y * nsign, z * nsign);

		 gl.glVertex3f(x * radius, y * radius, z * radius);
		 }

		 gl.glEnd();
		 }

		 // draw -Z end as a triangle fan
		 gl.glBegin(GL_TRIANGLE_FAN);

		 if (normals)
		 gl.glNormal3f(0.0f, 0.0f, -1.0f);

		 gl.glVertex3f(0.0f, 0.0f, -radius * nsign);

		 rho = (float) Math.PI - drho;

		 for (j = slices; j >= 0; j--)
		 {
		 if (j == slices)
		 theta = (float) 0.0;
		 else
		 theta = (float) j * dtheta;

		 sin = Math.sin(rho);

		 x = (float) (Math.cos(theta) * sin);
		 y = (float) (Math.sin(theta) * sin);
		 z = nsign * (float) Math.cos(rho);

		 if (normals)
		 gl.glNormal3f(x * nsign, y * nsign, z * nsign);

		 gl.glVertex3f(x * radius, y * radius, z * radius);
		 }

		 gl.glEnd();
		 }
		 else if (quadric.drawStyle == GLU_LINE || quadric.drawStyle == GLU_SILHOUETTE)
		 {
		 // draw stack lines
		 for (i = 1; i < stacks - 1; i++)
		 {
		 rho = (float) i * drho;
		 gl.glBegin(GL_LINE_LOOP);

		 for (j = 0; j < slices; j++)
		 {
		 theta = (float) j * dtheta;
		 sin = Math.sin(rho);

		 x = (float) (Math.cos(theta) * sin);
		 y = (float) (Math.sin(theta) * sin);
		 z = (float) Math.cos(rho);

		 if (normals)
		 gl.glNormal3f(x * nsign, y * nsign, z * nsign);

		 gl.glVertex3f(x * radius, y * radius, z * radius);
		 }

		 gl.glEnd();
		 }

		 // draw slice lines
		 for (j = 0; j < slices; j++)
		 {
		 theta = (float) j * dtheta;
		 gl.glBegin(GL_LINE_STRIP);

		 for (i = 0; i <= stacks; i++)
		 {
		 rho = (float) i * drho;
		 sin = Math.sin(rho);

		 x = (float) (Math.cos(theta) * sin);
		 y = (float) (Math.sin(theta) * sin);
		 z = (float) Math.cos(rho);

		 if (normals)
		 gl.glNormal3f(x * nsign, y * nsign, z * nsign);

		 gl.glVertex3f(x * radius, y * radius, z * radius);
		 }

		 gl.glEnd();
		 }
		 }
		 else if (quadric.drawStyle == GLU_POINT)
		 {
		 // top and bottom-most points
		 gl.glBegin(GL_POINTS);

		 if (normals)
		 gl.glNormal3f(0.0f, 0.0f, nsign);

		 gl.glVertex3f(0.0f, 0.0f, radius);

		 if (normals)
		 gl.glNormal3f(0.0f, 0.0f, -nsign);

		 gl.glVertex3f(0.0f, 0.0f, radius);

		 // loop over stacks
		 for (i = 0; i < stacks; i++)
		 {
		 rho = (float) i * drho;

		 for (j = 0; j < slices; j++)
		 {
		 theta = (float) j * dtheta;
		 sin = Math.sin(rho);

		 x = (float) (Math.cos(theta) * sin);
		 y = (float) (Math.sin(theta) * sin);
		 z = (float) Math.cos(rho);

		 if (normals)
		 gl.glNormal3f(x * nsign, y * nsign, z * nsign);

		 gl.glVertex3f(x * radius, y * radius, z * radius);
		 }
		 }

		 gl.glEnd();
		 }
		 */

		float rho;
		final float drho;
		float theta;
		final float dtheta;
		float x, y, z;
		float s;
		float t;
		final float ds;
		final float dt;
		int i;
		int j;
		final int imin;
		final int imax;
		final boolean normals;
		final float nsign;

		if (quadric.normals == GLU_NONE)
			normals = GL_FALSE;
		else
			normals = GL_TRUE;

		if (quadric.orientation == GLU_INSIDE)
			nsign = -1.0f;
		else
			nsign = 1.0f;

		drho = (float) Math.PI / stacks;
		dtheta = 2.0f * (float) Math.PI / slices;

		// texturing: s goes from 0.0/0.25/0.5/0.75/1.0 at +y/+x/-y/-x/+y axis
		// t goes from -1.0/+1.0 at z = -radius/+radius (linear along longitudes)
		// cannot use triangle fan on texturing (s coord. at top/bottom tip varies)

		if (quadric.drawStyle == GLU_FILL) {
			if (!quadric.textureCoordinates) {
				// draw +Z end as a triangle fan

				gl.glBegin(GL_TRIANGLE_FAN);
				gl.glNormal3f(0.0f, 0.0f, 1.0f);
				gl.glVertex3f(0.0f, 0.0f, nsign * radius);

				for (j = 0; j <= slices; j++) {
					theta = (j == slices) ? 0.0f : j * dtheta;
					x = -(float) Math.sin(theta) * (float) Math.sin(drho);
					y = (float) Math.cos(theta) * (float) Math.sin(drho);
					z = nsign * (float) Math.cos(drho);

					if (normals)
						gl.glNormal3f(x * nsign, y * nsign, z * nsign);

					gl.glVertex3f(x * radius, y * radius, z * radius);
				}

				gl.glEnd();
			}

			ds = 1.0f / slices;
			dt = 1.0f / stacks;
			t = 1.0f; // because loop now runs from 0

			if (quadric.textureCoordinates) {
				imin = 0;
				imax = stacks;
			} else {
				imin = 1;
				imax = stacks - 1;
			}

			// draw intermediate stacks as quad strips

			for (i = imin; i < imax; i++) {
				rho = i * drho;
				gl.glBegin(GL_QUAD_STRIP);
				s = 0.0f;

				for (j = 0; j <= slices; j++) {
					theta = (j == slices) ? 0.0f : j * dtheta;
					x = -(float) Math.sin(theta) * (float) Math.sin(rho);
					y = (float) Math.cos(theta) * (float) Math.sin(rho);
					z = nsign * (float) Math.cos(rho);

					if (normals)
						gl.glNormal3f(x * nsign, y * nsign, z * nsign);

					if (quadric.textureCoordinates)
						gl.glTexCoord2f(s, t);

					gl.glVertex3f(x * radius, y * radius, z * radius);

					x = -(float) Math.sin(theta) * (float) Math.sin(rho + drho);
					y = (float) Math.cos(theta) * (float) Math.sin(rho + drho);
					z = nsign * (float) Math.cos(rho + drho);

					if (normals)
						gl.glNormal3f(x * nsign, y * nsign, z * nsign);

					if (quadric.textureCoordinates)
						gl.glTexCoord2f(s, t - dt);

					s += ds;
					gl.glVertex3f(x * radius, y * radius, z * radius);
				}

				gl.glEnd();
				t -= dt;
			}

			if (!quadric.textureCoordinates) {
				// draw -Z end as a triangle fan

				gl.glBegin(GL_TRIANGLE_FAN);
				gl.glNormal3f(0.0f, 0.0f, -1.0f);
				gl.glVertex3f(0.0f, 0.0f, -radius * nsign);

				rho = (float) Math.PI - drho;
				s = 1.0f;
				t = dt;

				for (j = slices; j >= 0; j--) {
					theta = (j == slices) ? 0.0f : j * dtheta;
					x = -(float) Math.sin(theta) * (float) Math.sin(rho);
					y = (float) Math.cos(theta) * (float) Math.sin(rho);
					z = nsign * (float) Math.cos(rho);

					if (normals)
						gl.glNormal3f(x * nsign, y * nsign, z * nsign);

					s -= ds;
					gl.glVertex3f(x * radius, y * radius, z * radius);
				}

				gl.glEnd();
			}
		} else if (quadric.drawStyle == GLU_LINE || quadric.drawStyle == GLU_SILHOUETTE) {
			// draw stack lines

			for (i = 1; i < stacks; i++) {
				// stack line at i==stacks-1 was missing here

				rho = i * drho;
				gl.glBegin(GL_LINE_LOOP);

				for (j = 0; j < slices; j++) {
					theta = j * dtheta;
					x = (float) Math.cos(theta) * (float) Math.sin(rho);
					y = (float) Math.sin(theta) * (float) Math.sin(rho);
					z = (float) Math.cos(rho);

					if (normals)
						gl.glNormal3f(x * nsign, y * nsign, z * nsign);

					gl.glVertex3f(x * radius, y * radius, z * radius);
				}

				gl.glEnd();
			}

			// draw slice lines

			for (j = 0; j < slices; j++) {
				theta = j * dtheta;
				gl.glBegin(GL_LINE_STRIP);

				for (i = 0; i <= stacks; i++) {
					rho = i * drho;
					x = (float) Math.cos(theta) * (float) Math.sin(rho);
					y = (float) Math.sin(theta) * (float) Math.sin(rho);
					z = (float) Math.cos(rho);

					if (normals)
						gl.glNormal3f(x * nsign, y * nsign, z * nsign);

					gl.glVertex3f(x * radius, y * radius, z * radius);
				}
				gl.glEnd();
			}
		} else if (quadric.drawStyle == GLU_POINT) {
			// top and bottom-most points

			gl.glBegin(GL_POINTS);

			if (normals)
				gl.glNormal3f(0.0f, 0.0f, nsign);

			gl.glVertex3f(0.0f, 0.0f, radius);

			if (normals)
				gl.glNormal3f(0.0f, 0.0f, -nsign);

			gl.glVertex3f(0.0f, 0.0f, -radius);

			// loop over stacks

			for (i = 1; i < stacks - 1; i++) {
				rho = i * drho;

				for (j = 0; j < slices; j++) {
					theta = j * dtheta;
					x = (float) Math.cos(theta) * (float) Math.sin(rho);
					y = (float) Math.sin(theta) * (float) Math.sin(rho);
					z = (float) Math.cos(rho);

					if (normals)
						gl.glNormal3f(x * nsign, y * nsign, z * nsign);

					gl.glVertex3f(x * radius, y * radius, z * radius);
				}
			}

			gl.glEnd();
		}
	}

	/**
	 * Find the value nearest to n which is also a power of two.
	 */
	private static int round2(final int n) {
		int m;

		for (m = 1; m < n; m <<= 1)
			;

		// m >= n
		if (m - n <= (n - m) >> 1)
			return m;
		else
			return m >> 1;
	}

	/**
	 * Given an pixel format and datatype, return the number of bytes to
	 * store one pixel.
	 */
	private static int bytes_per_pixel(final int format, final int type) {
		final int n, m;

		switch (format) {
		case GL_COLOR_INDEX:
		case GL_STENCIL_INDEX:
		case GL_DEPTH_COMPONENT:
		case GL_RED:
		case GL_GREEN:
		case GL_BLUE:
		case GL_ALPHA:
		case GL_LUMINANCE:
			n = 1;
			break;
		case GL_LUMINANCE_ALPHA:
			n = 2;
			break;
		case GL_RGB:
			n = 3;
			break;
		case GL_RGBA:
			n = 4;
			break;
		default:
			n = 0;
		}

		switch (type) {
		case GL_UNSIGNED_BYTE:
			m = 1;
			break;
		case GL_BYTE:
			m = 1;
			break;
		case GL_BITMAP:
			m = 1;
			break;
		case GL_UNSIGNED_SHORT:
			m = 2;
			break;
		case GL_SHORT:
			m = 2;
			break;
		case GL_UNSIGNED_INT:
			m = 4;
			break;
		case GL_INT:
			m = 4;
			break;
		case GL_FLOAT:
			m = 4;
			break;
		default:
			m = 0;
		}

		return n * m;
	}

	private static int CEILING(final int a, final int b) {
		return a % b == 0 ? a / b : a / b + 1;
	}

	public final int gluScaleImage(final int format, final int widthin, final int heightin, final int typein,
			final Object datain, final int widthout, final int heightout, final int typeout, final Object dataout) {
		final int components, sizein, sizeout;

		// Determine number of components per pixel

		switch (format) {
		case GL_COLOR_INDEX:
		case GL_STENCIL_INDEX:
		case GL_DEPTH_COMPONENT:
		case GL_RED:
		case GL_GREEN:
		case GL_BLUE:
		case GL_ALPHA:
		case GL_LUMINANCE:
			components = 1;
			break;
		case GL_LUMINANCE_ALPHA:
			components = 2;
			break;
		case GL_RGB:
			components = 3;
			break;
		case GL_RGBA:
			components = 4;
			break;
		default:
			throw new GLUInvalidEnumException("gluScaleImage(int, int, int, int, Object, int, int, int, Object)");
		}

		// Determine bytes per input datum

		switch (typein) {
		case GL_UNSIGNED_BYTE:
			sizein = 1;
			break;
		case GL_BYTE:
			sizein = 1;
			break;
		case GL_UNSIGNED_SHORT:
			sizein = 2;
			break;
		case GL_SHORT:
			sizein = 2;
			break;
		case GL_UNSIGNED_INT:
			sizein = 4;
			break;
		case GL_INT:
			sizein = 4;
			break;
		case GL_FLOAT:
			sizein = 4;
			break;
		case GL_BITMAP:
			// not implemented yet
		default:
			throw new GLUInvalidEnumException("gluScaleImage(int, int, int, int, Object, int, int, int, Object)");
		}

		// Determine bytes per output datum

		switch (typeout) {
		case GL_UNSIGNED_BYTE:
			sizeout = 1;
			break;
		case GL_BYTE:
			sizeout = 1;
			break;
		case GL_UNSIGNED_SHORT:
			sizeout = 2;
			break;
		case GL_SHORT:
			sizeout = 2;
			break;
		case GL_UNSIGNED_INT:
			sizeout = 4;
			break;
		case GL_INT:
			sizeout = 4;
			break;
		case GL_FLOAT:
			sizeout = 4;
			break;
		case GL_BITMAP:
			// not implemented yet
		default:
			throw new GLUInvalidEnumException("gluScaleImage(int, int, int, int, Object, int, int, int, Object)");
		}

		final int[] unpackrowlength = new int[1];
		final int[] unpackalignment = new int[1];
		final int[] unpackskiprows = new int[1];
		final int[] unpackskippixels = new int[1];
		final int[] packrowlength = new int[1];
		final int[] packalignment = new int[1];
		final int[] packskiprows = new int[1];
		final int[] packskippixels = new int[1];

		// Get glPixelStore state

		gl.glGetIntegerv(GL_UNPACK_ROW_LENGTH, unpackrowlength);
		gl.glGetIntegerv(GL_UNPACK_ALIGNMENT, unpackalignment);
		gl.glGetIntegerv(GL_UNPACK_SKIP_ROWS, unpackskiprows);
		gl.glGetIntegerv(GL_UNPACK_SKIP_PIXELS, unpackskippixels);
		gl.glGetIntegerv(GL_PACK_ROW_LENGTH, packrowlength);
		gl.glGetIntegerv(GL_PACK_ALIGNMENT, packalignment);
		gl.glGetIntegerv(GL_PACK_SKIP_ROWS, packskiprows);
		gl.glGetIntegerv(GL_PACK_SKIP_PIXELS, packskippixels);

		// Allocate storage for intermediate images

		final float[] tempin = new float[widthin * heightin * components];
		final float[] tempout = new float[widthout * heightout * components];

		// Unpack the pixel demos.data and convert to floating point

		int rowlen, rowstride;

		if (unpackrowlength[0] > 0)
			rowlen = unpackrowlength[0];
		else
			rowlen = widthin;

		if (sizein >= unpackalignment[0])
			rowstride = components * rowlen;
		else
			rowstride = unpackalignment[0] / sizein * CEILING(components * rowlen * sizein, unpackalignment[0]);

		int i, j, k;

		// TOMD - lots of unnecessary casts going on below, sort out !

		switch (typein) {
		case GL_UNSIGNED_BYTE:
		case GL_BYTE:

			k = 0;

			for (i = 0; i < heightin; i++) {
				int ptr = i * rowstride + unpackskiprows[0] * rowstride + unpackskippixels[0] * components;

				for (j = 0; j < widthin * components; j++)
					tempin[k++] = (float) ((byte[]) datain)[ptr++];
			}

			break;

		case GL_UNSIGNED_SHORT:
		case GL_SHORT:

			k = 0;

			for (i = 0; i < heightin; i++) {
				int ptr = i * rowstride + unpackskiprows[0] * rowstride + unpackskippixels[0] * components;

				for (j = 0; j < widthin * components; j++)
					tempin[k++] = (float) ((short[]) datain)[ptr++];
			}

			break;

		case GL_UNSIGNED_INT:
		case GL_INT:

			k = 0;

			for (i = 0; i < heightin; i++) {
				int ptr = i * rowstride + unpackskiprows[0] * rowstride + unpackskippixels[0] * components;

				for (j = 0; j < widthin * components; j++)
					tempin[k++] = (float) ((int[]) datain)[ptr++];
			}

			break;

		case GL_FLOAT:

			k = 0;

			for (i = 0; i < heightin; i++) {
				int ptr = i * rowstride + unpackskiprows[0] * rowstride + unpackskippixels[0] * components;

				for (j = 0; j < widthin * components; j++)
					tempin[k++] = ((float[]) datain)[ptr++];
			}

			break;

		default:
			throw new GLUInvalidEnumException("gluScaleImage(int, int, int, int, Object, int, int, int, Object)");
		}

		// Scale the image!

		final float sx;
		final float sy;

		if (widthout > 1)
			sx = (float) (widthin - 1) / (float) (widthout - 1);
		else
			sx = (float) (widthin - 1);

		if (heightout > 1)
			sy = (float) (heightin - 1) / (float) (heightout - 1);
		else
			sy = (float) (heightin - 1);

		// TOMD - using point sample below, it works but
		// there are better / more accurate ways of doing this

		for (i = 0; i < heightout; i++) {
			final int ii = (int) (i * sy);

			for (j = 0; j < widthout; j++) {
				final int jj = (int) (j * sx);

				int src = (ii * widthin + jj) * components;
				int dst = (i * widthout + j) * components;

				for (k = 0; k < components; k++)
					tempout[dst++] = tempin[src++];
			}
		}

		// Return output image

		if (packrowlength[0] > 0)
			rowlen = packrowlength[0];
		else
			rowlen = widthout;

		if (sizeout >= packalignment[0])
			rowstride = components * rowlen;
		else
			rowstride = packalignment[0] / sizeout * CEILING(components * rowlen * sizeout, packalignment[0]);

		switch (typeout) {
		case GL_UNSIGNED_BYTE:
		case GL_BYTE:

			k = 0;

			for (i = 0; i < heightout; i++) {
				int ptr = i * rowstride + packskiprows[0] * rowstride + packskippixels[0] * components;

				for (j = 0; j < widthout * components; j++)
					((byte[]) dataout)[ptr++] = (byte) tempout[k++];
			}

			break;

		case GL_UNSIGNED_SHORT:
		case GL_SHORT:

			k = 0;

			for (i = 0; i < heightout; i++) {
				int ptr = i * rowstride + packskiprows[0] * rowstride + packskippixels[0] * components;

				for (j = 0; j < widthout * components; j++)
					((short[]) dataout)[ptr++] = (short) tempout[k++];
			}

			break;

		case GL_UNSIGNED_INT:
		case GL_INT:

			k = 0;

			for (i = 0; i < heightout; i++) {
				int ptr = i * rowstride + packskiprows[0] * rowstride + packskippixels[0] * components;

				for (j = 0; j < widthout * components; j++)
					((int[]) dataout)[ptr++] = (int) tempout[k++];
			}

			break;

		case GL_FLOAT:

			k = 0;

			for (i = 0; i < heightout; i++) {
				int ptr = i * rowstride + packskiprows[0] * rowstride + packskippixels[0] * components;

				for (j = 0; j < widthout * components; j++)
					((float[]) dataout)[ptr++] = tempout[k++];
			}

			break;

		default:
			throw new GLUInvalidEnumException("gluScaleImage(int, int, int, int, Object, int, int, int, Object)");
		}

		return 0;
	}

	public final int gluBuild2DMipmaps(final int target, final int components, final int width, final int height,
			final int format, final int type, final Object data) {
		if (width < 1 || height < 1)
			throw new GLUInvalidValueException("gluBuild2DMipmaps(int, int, int, int, int, int, Object)");

		final int[] maxsizev = new int[1];
		gl.glGetIntegerv(GL_MAX_TEXTURE_SIZE, maxsizev);
		final int maxsize = maxsizev[0];

		int w = round2(width);

		if (w > maxsize)
			w = maxsize;

		int h = round2(height);

		if (h > maxsize)
			h = maxsize;

		final int bpp = bytes_per_pixel(format, type);

		if (bpp == 0) // probably a bad format or type enum */
			throw new GLUInvalidEnumException("gluBuild2DMipmaps(int, int, int, int, int, int, Object)");

		final int[] unpackrowlength = new int[1];
		final int[] unpackalignment = new int[1];
		final int[] unpackskiprows = new int[1];
		final int[] unpackskippixels = new int[1];
		final int[] packrowlength = new int[1];
		final int[] packalignment = new int[1];
		final int[] packskiprows = new int[1];
		final int[] packskippixels = new int[1];

		// get current glPixelStore values

		gl.glGetIntegerv(GL_UNPACK_ROW_LENGTH, unpackrowlength);
		gl.glGetIntegerv(GL_UNPACK_ALIGNMENT, unpackalignment);
		gl.glGetIntegerv(GL_UNPACK_SKIP_ROWS, unpackskiprows);
		gl.glGetIntegerv(GL_UNPACK_SKIP_PIXELS, unpackskippixels);
		gl.glGetIntegerv(GL_PACK_ROW_LENGTH, packrowlength);
		gl.glGetIntegerv(GL_PACK_ALIGNMENT, packalignment);
		gl.glGetIntegerv(GL_PACK_SKIP_ROWS, packskiprows);
		gl.glGetIntegerv(GL_PACK_SKIP_PIXELS, packskippixels);

		// set pixel packing

		gl.glPixelStorei(GL_PACK_ROW_LENGTH, 0);
		gl.glPixelStorei(GL_PACK_ALIGNMENT, 1);
		gl.glPixelStorei(GL_PACK_SKIP_ROWS, 0);
		gl.glPixelStorei(GL_PACK_SKIP_PIXELS, 0);

		boolean done = GL_FALSE;
		int retval = 0;

		Object image;

		int error;

		if (w != width || h != height) {
			// must rescale image to get "top" mipmap texture image

			image = new byte[((w + 4) * h * bpp)];
			error = gluScaleImage(format, width, height, type, data, w, h, GL_BYTE, image);

			if (error != 0) {
				retval = error;
				done = GL_TRUE;
			}
		} else
			image = data;

		int level = 0;
		int neww, newh;
		Object newimage;

		while (!done) {
			if (image != data) {
				// set pixel unpacking

				gl.glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
				gl.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
				gl.glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
				gl.glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
			}

			gl.glTexImage2D(target, level, components, w, h, 0, format, type, image);

			if (w == 1 && h == 1)
				break;

			neww = (w < 2) ? 1 : w >> 1;
			newh = (h < 2) ? 1 : h >> 1;
			newimage = new byte[(neww + 4) * newh * bpp];

			error = gluScaleImage(format, w, h, type, image, neww, newh, GL_BYTE, newimage);

			if (error != 0) {
				retval = error;
				done = GL_TRUE;
			}

			image = newimage;

			w = neww;
			h = newh;

			++level;
		}

		// restore original glPixelStore state

		gl.glPixelStorei(GL_UNPACK_ROW_LENGTH, unpackrowlength[0]);
		gl.glPixelStorei(GL_UNPACK_ALIGNMENT, unpackalignment[0]);
		gl.glPixelStorei(GL_UNPACK_SKIP_ROWS, unpackskiprows[0]);
		gl.glPixelStorei(GL_UNPACK_SKIP_PIXELS, unpackskippixels[0]);
		gl.glPixelStorei(GL_PACK_ROW_LENGTH, packrowlength[0]);
		gl.glPixelStorei(GL_PACK_ALIGNMENT, packalignment[0]);
		gl.glPixelStorei(GL_PACK_SKIP_ROWS, packskiprows[0]);
		gl.glPixelStorei(GL_PACK_SKIP_PIXELS, packskippixels[0]);

		return retval;
	}
}
