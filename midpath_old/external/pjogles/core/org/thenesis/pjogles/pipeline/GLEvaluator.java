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

import org.thenesis.pjogles.GL11;
import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLInvalidValueException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.evaluators.GLEvalGrid2;
import org.thenesis.pjogles.evaluators.GLEvalMap2;
import org.thenesis.pjogles.math.GLVector4f;

/**
 * @author tdinneen
 */
public final class GLEvaluator implements GLConstants {
	private final GLSoftwareContext gc;

	private final GLEvalMap2[] eval2 = new GLEvalMap2[MAP_RANGE_COUNT];

	public GLEvaluator(final GLSoftwareContext gc) {
		this.gc = gc;

		eval2[C4] = new GLEvalMap2(4);
		eval2[I] = new GLEvalMap2(1);
		eval2[N3] = new GLEvalMap2(3);
		eval2[T1] = new GLEvalMap2(1);
		eval2[T2] = new GLEvalMap2(2);
		eval2[T3] = new GLEvalMap2(3);
		eval2[T4] = new GLEvalMap2(4);
		eval2[V3] = new GLEvalMap2(3);
		eval2[V4] = new GLEvalMap2(4);
	}

	public final GLEvalMap2 setUpMap2(final int type, final int majorOrder, final int minorOrder, final float u1,
			final float u2, final float v1, final float v2) {
		final GLEvalMap2 ev;

		switch (type) {
		case GL_MAP2_COLOR_4:
		case GL_MAP2_INDEX:
		case GL_MAP2_NORMAL:
		case GL_MAP2_TEXTURE_COORD_1:
		case GL_MAP2_TEXTURE_COORD_2:
		case GL_MAP2_TEXTURE_COORD_3:
		case GL_MAP2_TEXTURE_COORD_4:
		case GL_MAP2_VERTEX_3:
		case GL_MAP2_VERTEX_4:
			ev = eval2[__GL_EVAL2D_INDEX(type)];
			break;
		default:
			throw new GLInvalidEnumException(
					"GLEvaluator.setUpMap2(int type, int majorOrder, int minorOrder, float u1, float u2, float v1, float v2)");
		}

		if (minorOrder < 1 || minorOrder > MAX_EVAL_ORDER || majorOrder < 1 || majorOrder > MAX_EVAL_ORDER || u1 == u2
				|| v1 == v2)
			throw new GLInvalidValueException(
					"int type, int majorOrder, int minorOrder, float u1, float u2, float v1, float v2");

		ev.majorOrder = majorOrder;
		ev.minorOrder = minorOrder;
		ev.u1 = u1;
		ev.u2 = u2;
		ev.v1 = v1;
		ev.v2 = v2;

		return ev;
	}

	private static int __GL_EVAL2D_INDEX(final int type) {
		return type - GL_MAP2_COLOR_4;
	}

	public final void evalMesh2Point(final GL11 gl, final int lowU, final int lowV, final int highU, final int highV) {
		final GLEvalGrid2 u = gc.state.evaluator.u2;
		final GLEvalGrid2 v = gc.state.evaluator.v2;

		if (u.n == 0 || v.n == 0)
			return;

		final float du = (u.finish - u.start) / (float) u.n;
		final float dv = (v.finish - v.start) / (float) v.n;

		// TOMD - i've commented below out !!!!!!!!!! sort out !!!!!!!!!

		// GLColor currentColor = new GLColor(gc.state.current.color);
		final GLVector4f currentNormal = new GLVector4f(gc.state.current.normal);
		final GLVector4f currentTexture = new GLVector4f(gc.state.current.texture[0]);

		final float[] r = new float[4];

		gl.glBegin(GL_POINTS);

		for (int i = lowU; i <= highU; ++i) {
			final float u1 = (i == u.n) ? u.finish : (u.start + i * du);

			for (int j = lowV; j <= highV; ++j) {
				final float v1 = (j == v.n) ? v.finish : (v.start + j * dv);
				doEvalCoord2(gl, u1, v1, r);
			}
		}

		gl.glEnd();

		// gc.state.current.color.set(currentColor);
		gc.state.current.normal.set(currentNormal);
		gc.state.current.texture[0].set(currentTexture);
	}

	public final void evalMesh2Line(final GL11 gl, final int lowU, final int lowV, final int highU, final int highV) {
		final GLEvalGrid2 u = gc.state.evaluator.u2;
		final GLEvalGrid2 v = gc.state.evaluator.v2;

		if (u.n == 0 || v.n == 0)
			return;

		final float du = (u.finish - u.start) / (float) u.n;
		final float dv = (v.finish - v.start) / (float) v.n;

		// TOMD - i've commented below out !!!!!!!!!! sort out !!!!!!!!!

		// GLColor currentColor = new GLColor(gc.state.current.color);
		final GLVector4f currentNormal = new GLVector4f(gc.state.current.normal);
		final GLVector4f currentTexture = new GLVector4f(gc.state.current.texture[0]);

		final float[] r = new float[4];

		for (int j = lowV; j <= highV; ++j) {
			gl.glBegin(GL_LINE_STRIP);

			final float v1 = (j == v.n) ? v.finish : (v.start + j * dv);

			for (int i = lowU; i <= highU; ++i) {
				final float u1 = (i == u.n) ? u.finish : (u.start + i * du);
				doEvalCoord2(gl, u1, v1, r);
			}

			gl.glEnd();
		}

		for (int i = lowU; i <= highU; ++i) {
			gl.glBegin(GL_LINE_STRIP);

			final float u1 = (i == u.n) ? u.finish : (u.start + i * du);

			for (int j = lowV; j <= highV; ++j) {
				final float v1 = (j == v.n) ? v.finish : (v.start + j * dv);
				doEvalCoord2(gl, u1, v1, r);
			}

			gl.glEnd();
		}

		// gc.state.current.color.set(currentColor);
		gc.state.current.normal.set(currentNormal);
		gc.state.current.texture[0].set(currentTexture);
	}

	public final void evalMesh2Fill(final GL11 gl, final int lowU, final int lowV, final int highU, final int highV) {
		final GLEvalGrid2 u = gc.state.evaluator.u2;
		final GLEvalGrid2 v = gc.state.evaluator.v2;

		if (u.n == 0 || v.n == 0)
			return;

		final float du = (u.finish - u.start) / (float) u.n;
		final float dv = (v.finish - v.start) / (float) v.n;

		// TOMD - i've commented below out !!!!!!!!!! sort out !!!!!!!!!

		// GLColor currentColor = new GLColor(gc.state.current.color);
		final GLVector4f currentNormal = new GLVector4f(gc.state.current.normal);
		final GLVector4f currentTexture = new GLVector4f(gc.state.current.texture[0]);

		final float[] r = new float[4];

		for (int j = lowV; j < highV; ++j) {
			gl.glBegin(GL_TRIANGLE_STRIP);

			final float v1 = (j == v.n) ? v.finish : (v.start + j * dv);
			final float v11 = ((j + 1) == v.n) ? v.finish : (v.start + ((j + 1) * dv));

			for (int i = lowU; i <= highU; ++i) {
				final float u1 = (i == u.n) ? u.finish : (u.start + i * du);

				doEvalCoord2(gl, u1, v1, r);
				doEvalCoord2(gl, u1, v11, r);
			}

			gl.glEnd();
		}

		// gc.state.current.color.set(currentColor);
		gc.state.current.normal.set(currentNormal);
		gc.state.current.texture[0].set(currentTexture);
	}

	/**
	 * Compute the color, texture, normal, vertices based upon u and v.
	 */
	public final void doEvalCoord2(final GL11 gl, final float u, final float v, final float[] r) {
		if (gc.state.enables.autoNormal) {
			if (gc.state.enables.map2Vertex4) {
				evalNormal2(eval2[V4], u, v, r);
				gl.glNormal3fv(r);

				evalCoord2(eval2[V4], u, v, r);
				gl.glVertex4fv(r);
			} else if (gc.state.enables.map2Vertex3) {
				evalNormal2(eval2[V3], u, v, r);
				gl.glNormal3fv(r);

				evalCoord2(eval2[V3], u, v, r);
				gl.glVertex3fv(r);
			}
		} else {
			if (gc.state.enables.map2Normal) {
				evalCoord2(eval2[N3], u, v, r);
				gl.glNormal3fv(r);
			}

			if (gc.state.enables.map2Vertex4) {
				evalCoord2(eval2[V4], u, v, r);
				gl.glVertex4fv(r);
			} else if (gc.state.enables.map2Vertex3) {
				evalCoord2(eval2[V3], u, v, r);
				gl.glVertex3fv(r);
			}
		}

		if (gc.state.enables.map2Color4) {
			evalCoord2(eval2[C4], u, v, r);
			gl.glColor4fv(r);
		}

		if (gc.state.enables.map2TextureCoord4) {
			evalCoord2(eval2[T4], u, v, r);
			gl.glTexCoord4fv(r);
		} else if (gc.state.enables.map2TextureCoord3) {
			evalCoord2(eval2[T3], u, v, r);
			gl.glTexCoord3fv(r);
		} else if (gc.state.enables.map2TextureCoord2) {
			evalCoord2(eval2[T2], u, v, r);
			gl.glTexCoord2fv(r);
		} else if (gc.state.enables.map2TextureCoord1) {
			evalCoord2(eval2[T1], u, v, r);
			gl.glTexCoord1fv(r);
		}
	}

	private void evalCoord2(final GLEvalMap2 evaluator, final float u, final float v, final float[] r) {
		final float uu = (u - evaluator.u1) / (evaluator.u2 - evaluator.u1);
		final float vv = (v - evaluator.v1) / (evaluator.v2 - evaluator.v1);

		bezierSurface(evaluator, uu, vv, r);
	}

	private void evalNormal2(final GLEvalMap2 evaluator, final float u, final float v, final float[] r) {
		final float uu = (u - evaluator.u1) / (evaluator.u2 - evaluator.u1);
		final float vv = (v - evaluator.v1) / (evaluator.v2 - evaluator.v1);

		bezierNormal(evaluator, uu, vv, r);
	}

	/**
	 *   Bezier curve as u varies from 0 to 1
	 *
	 *               n   n
	 *       C(u) =  E  B(u)*Pi
	 *              i=0  i
	 *
	 *   Bernstein polynomial of degree n (or order n+1)
	 *
	 *        n     (n)
	 *       B(u) = ( )*u^i*(1-u)^(n-i)
	 *        i     (i)
	 */
	private void bezierCurve(final float[][] p, final float u, final int dim, final int odr, final float[] c) {
		final float v; // v = 1 - u
		float uu; // uu = u^n (n = 1~(odr-1))
		int bc; // (n i) = n! / (n - i)! * i!
		int i, j;

		if (odr > 1) {
			bc = odr - 1; // degree = odr - 1
			v = (float) 1.0 - u;

			for (j = 0; j < dim; ++j)
				// for B0*P0+B1*P1 (will * v^n-1)
				c[j] = v * p[0][j] + bc * u * p[1][j];

			for (i = 2, uu = u * u; i < odr; i++, uu *= u) // for Pi
			{
				bc *= odr - i;
				bc /= i;

				for (j = 0; j < dim; ++j)
					c[j] = v * c[j] + bc * uu * p[i][j];
			}
		} else // odr == 1
		{
			for (j = 0; j < dim; ++j)
				c[j] = p[0][j];
		}
	}

	/*
	 *   Bezier surface patch as u and v varies from 0 to 1
	 *
	 *                 n   m   n    m
	 *       S(u,v) =  E   E  B(u)*B(v)*Pij
	 *                i=0 j=0  i    j
	 *
	 *   u and v are independent
	 */
	private void bezierSurface(final GLEvalMap2 evaluator, final float u, final float v, final float[] r) {
		final int vodr = evaluator.minorOrder;
		final int uodr = evaluator.majorOrder;
		final float[][][] points = evaluator.points;
		final int dimension = evaluator.dimension;

		final float[][] c;
		float w, uu;
		int i, j, k, bc;

		if (vodr > uodr) {
			if (uodr > 1) {
				c = new float[vodr][dimension];

				for (j = 0; j < vodr; ++j) {
					c[j] = new float[dimension];

					bc = uodr - 1; // degree = odr - 1
					w = (float) 1.0 - u;

					for (k = 0; k < dimension; ++k)
						c[j][k] = w * points[0][j][k] + bc * u * points[1][j][k];

					for (i = 2, uu = u * u; i < uodr; ++i, uu *= u) {
						bc *= uodr - i;
						bc /= i;

						for (k = 0; k < dimension; k++)
							c[j][k] = w * c[j][k] + bc * uu * points[i][j][k];
					}
				}
			} else
				// uodr == 1
				c = points[0];

			bezierCurve(c, v, dimension, vodr, r);
		} else {
			c = new float[uodr][dimension];

			for (j = 0; j < uodr; ++j)
				bezierCurve(points[j], v, dimension, vodr, c[j]);

			bezierCurve(c, u, dimension, uodr, r);
		}
	}

	/**
	 *   Normal analysis of Bezier surface patch as u and v varies from 0 to 1
	 *
	 *            M
	 *       N = ---
	 *           |M|
	 *
	 *           dS   dS
	 *       M = -- X --
	 *           du   dv
	 *
	 *   since
	 *                 n  (n)
	 *       S(u,v) =  E  ( )*u^i*(1-u)^(n-i)* C(v)i
	 *                i=0 (i)
	 *   so
	 *       dS   n-1 (n-1)
	 *       -- =  E  (   )*u^i*(1-u)^(n-i-1)*(-C(v)i + C(v)i+1)
	 *       du   i=0 ( i )
	 *
	 *   then use Bezier curve function to solve this
	 *
	 *       n  <= n-1
	 *       Pi <= -C(v)i + C(v)i+1
	 */
	private void bezierNormal(final GLEvalMap2 evaluator, final float u, final float v, final float[] r) {
		final int vodr = evaluator.minorOrder;
		final int uodr = evaluator.majorOrder;
		final float[][][] points = evaluator.points;
		final int dimension = evaluator.dimension;

		float w; // w = 1 - u
		float uu; // uu = u^n (n = 1~(odr-1))
		int bc; // (n i) = n! / (n - i)! * i!
		int i, j, k;
		float c[][];

		// calculate dv first
		if (uodr > 1) {
			c = new float[vodr][dimension];

			for (j = 0; j < vodr; j++) {
				c[j] = new float[dimension];

				bc = uodr - 1; // degree = odr - 1
				w = (float) 1.0 - u;

				for (k = 0; k < dimension; k++)
					c[j][k] = w * points[0][j][k] + bc * u * points[1][j][k];

				for (i = 2, uu = u * u; i < uodr; i++, uu *= u) {
					bc *= uodr - i;
					bc /= i;

					for (k = 0; k < dimension; k++)
						c[j][k] = w * c[j][k] + bc * uu * points[i][j][k];
				}
			}
		} else
			// uodr == 1
			c = points[0];

		for (j = 0; j < vodr - 1; j++) {
			for (k = 0; k < dimension; k++)
				c[j][k] = c[j + 1][k] - c[j][k];
		}

		final float[] dv = new float[dimension];
		bezierCurve(c, v, dimension, vodr - 1, dv);

		// then calculate du
		c = new float[uodr][dimension];

		for (j = 0; j < uodr; ++j) {
			c[j] = new float[dimension];
			bezierCurve(points[j], v, dimension, vodr, c[j]);
		}

		for (j = 0; j < uodr - 1; ++j) {
			for (k = 0; k < dimension; ++k)
				c[j][k] = c[j + 1][k] - c[j][k];
		}

		final float[] du = new float[dimension];
		bezierCurve(c, u, dimension, uodr - 1, du);

		if (dimension == 4) {
			for (k = 0; k < 3; k++) {
				du[k] /= du[3];
				dv[k] /= dv[3];
			}
		}

		// float temp[];
		// temp = du;
		// du = dv;
		// dv = temp;

		// cross product
		r[0] = du[1] * dv[2] - dv[1] * du[2];
		r[1] = du[2] * dv[0] - dv[2] * du[0];
		r[2] = du[0] * dv[1] - dv[0] * du[1];

		// normalise
		final float length = (float) Math.sqrt(r[0] * r[0] + r[1] * r[1] + r[2] * r[2]);

		if (length != 0.0f) {
			final float scale = 1.0f / length;

			r[0] *= scale;
			r[1] *= scale;
			r[2] *= scale;
		}
	}
}
