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

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.math.GLMatrix4f;
import org.thenesis.pjogles.math.GLVector4f;
import org.thenesis.pjogles.math.GLVertex;
import org.thenesis.pjogles.primitives.GLSpan;
import org.thenesis.pjogles.texture.GLTextureBuffer;
import org.thenesis.pjogles.texture.GLTextureEnvState;
import org.thenesis.pjogles.texture.GLTextureGenState;
import org.thenesis.pjogles.texture.GLTextureImage;
import org.thenesis.pjogles.texture.GLTextureObject;
import org.thenesis.pjogles.texture.GLTextureUnitState;

/**
 * A grouping of all the texturing operations.
 *
 * @author tdinneen
 */
public final class GLTextureOperations implements GLConstants {
	private final GLSoftwareContext gc;

	private final GLVector4f u = new GLVector4f();

	protected final GLColor[][] unitTexels = new GLColor[NUMBER_OF_TEXTURE_UNITS][];

	private final int[] i0i1 = new int[2];
	private final int[] j0j1 = new int[2];

	private final GLColor t00 = new GLColor();
	private final GLColor t01 = new GLColor();
	private final GLColor t10 = new GLColor();
	private final GLColor t11 = new GLColor();

	/**
	 * Bitflags for texture border color sampling.
	 */
	private static final int I0BIT = 1;
	private static final int I1BIT = 2;
	private static final int J0BIT = 4;
	private static final int J1BIT = 8;
	private static final int K0BIT = 16;
	private static final int K1BIT = 32;

	public GLTextureOperations(final GLSoftwareContext gc) {
		this.gc = gc;

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			unitTexels[i] = new GLColor[MAX_WINDOW_WIDTH];

			for (int j = MAX_WINDOW_WIDTH; --j >= 0;)
				unitTexels[i][j] = new GLColor();
		}
	}

	public final void apply(final GLSpan span) {
		final long start = System.currentTimeMillis();

		final GLTextureUnitState[] units = gc.state.texture.unit;
		GLTextureUnitState unit;

		final int enabledUnits = gc.state.texture.enabledUnits;

		// Must do all texture sampling before combining in order to
		// accomodate GL_ARB_texture_env_crossbar.

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			unit = units[i];

			// TOMD - need to apply lambda adjust and clamping !!!

			if ((enabledUnits & (1 << i)) != 0)
				sample(span, unit, unitTexels[i]);
		}

		// OK, now apply the texture (aka texture combine/blend).
		// We modify the span->color.rgba values.

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			unit = units[i];

			if ((enabledUnits & (1 << i)) != 0)
				combine(span, unit, unitTexels[i]);
		}

		gc.benchmark.textureTime += System.currentTimeMillis() - start;
	}

	public final void processCoordinates(final GLVertex[] vertices, final int numberOfVertices) {
		if (gc.state.texture.textureGenEnabledUnits != 0) {
			for (int i = numberOfVertices; --i >= 0;)
				generateCoordinates(vertices[i]);
		}

		GLVector4f t;

		// a texture coordinate is treated internally by OpenGL as a four
		// dimensional homogenous coordinate (s, t, r, q) in much the same way as vertex
		// positions are.

		for (int i = numberOfVertices; --i >= 0;) {
			t = vertices[i].texture[0];
			GLMatrix4f.transform(t, gc.transform.texture.matrix, t);

			// TOMD - !!!!! should be projecting homogenous coord to
			// three dimensional carthesian by diving by q
		}
	}

	private void generateCoordinates(final GLVertex v) {
		final int active = gc.state.texture.currentUnitIndex;

		final GLTextureUnitState texture = gc.state.texture.currentUnit;
		final GLVector4f coordinate = v.texture[active];

		final int enable = gc.state.enables.texture[active];

		if ((enable & TEXTURE_GEN_S_ENABLE) != 0)
			coordinate.x = generateCoordinate(texture.s, v);

		if ((enable & TEXTURE_GEN_T_ENABLE) != 0)
			coordinate.y = generateCoordinate(texture.t, v);

		if ((enable & TEXTURE_GEN_R_ENABLE) != 0)
			coordinate.z = generateCoordinate(texture.r, v);

		if ((enable & TEXTURE_GEN_Q_ENABLE) != 0)
			coordinate.w = generateCoordinate(texture.q, v);
	}

	private float generateCoordinate(final GLTextureGenState s, final GLVertex v) {
		switch (s.mode) {
		case GL_EYE_LINEAR:

			return v.eye.dotProduct(s.eyePlaneEquation);

		case GL_OBJECT_LINEAR:

			return v.position.dotProduct(s.objectPlaneEquation);

		case GL_SPHERE_MAP:

			u.set(v.eye);
			u.normalize();

			final GLVector4f normal = v.normal;

			final float nu = 2.0f * normal.dotProduct(u);
			final float fx = u.x - normal.x * nu;
			final float fy = u.y - normal.y * nu;
			final float fz = u.z - normal.z * nu + 1.0f;
			final float m = 2.0f * (float) Math.sqrt(fx * fx + fy * fy + fz * fz);

			if (m == 0.0f)
				return 0.5f;
			else
				return (fx / m + 0.5F);

		default:

			throw new GLInvalidEnumException("GLTextureOperations.generateCoordinate(GLTextureGenState, GLVertex)");
		}
	}

	private void sample(final GLSpan span, final GLTextureUnitState unit, final GLColor[] texels) {
		final GLTextureObject textureObject = unit.currentTexture;

		if (textureObject.params.magFilter == GL_NEAREST)
			nearest(span, textureObject, texels);
		else if (textureObject.params.magFilter == GL_LINEAR)
			linear(span, textureObject, texels);
		else
			throw new GLInvalidEnumException("GLTextureUnitMachine.calcTexture(GLFragment)");
	}

	private final void combine(final GLSpan span, final GLTextureUnitState unit, final GLColor[] texels) {
		final GLTextureObject textureObject = unit.currentTexture;
		final int format = textureObject.level[0].baseFormat;

		final GLTextureEnvState env = unit.env[0];

		final int length = span.length;

		final GLColor[] localColors = (GLColor[]) span.color;
		final GLColor[] localTexels = (GLColor[]) texels;

		GLColor localColor;
		GLColor localTexel;

		float Lt, It;
		float s, t;

		int i;

		switch (env.mode) {
		case GL_REPLACE:

			switch (format) {
			case GL_ALPHA:

				for (i = length; --i >= 0;) {
					// Cv = Cf
					// Av = At
					localColors[i].a = localTexels[i].a;
				}

				break;

			case GL_LUMINANCE:

				for (i = length; --i >= 0;) {
					// Cv = Lt
					Lt = localTexels[i].r;
					localColor = localColors[i];
					localColor.r = localColor.g = localColor.b = Lt;
					// Av = Af
				}

				break;

			case GL_LUMINANCE_ALPHA:

				for (i = length; --i >= 0;) {
					// Cv = Lt
					Lt = localTexels[i].r;
					localColor = localColors[i];
					localColor.r = localColor.g = localColor.b = Lt;
					// Av = At
					localColor.a = Lt;
				}

				break;

			case GL_INTENSITY:

				for (i = length; --i >= 0;) {
					// Cv = It
					It = localTexels[i].r;
					localColor = localColors[i];
					localColor.r = localColor.g = localColor.b = It;
					// Av = It
					localColor.a = It;
				}

				break;

			case GL_RGB:

				for (i = length; --i >= 0;) {
					// Cv = Ct
					localColor = localColors[i];
					localTexel = localTexels[i];

					localColor.r = localTexel.r;
					localColor.g = localTexel.g;
					localColor.b = localTexel.b;
					// Av = Af
				}

				break;

			case GL_RGBA:

				for (i = length; --i >= 0;) {
					// Cv = Ct
					// Av = At
					localColors[i].set(localTexels[i]);
				}

				break;

			default:

				throw new GLInvalidEnumException("GLTextureOperations.combine(GLSpan, GLTextureUnitState, GLColor[])");
			}

			break;

		case GL_MODULATE:

			switch (format) {
			case GL_ALPHA:

				for (i = length; --i >= 0;) {
					// Cv = Cf
					// Av = AfAt
					localColors[i].a *= localTexels[i].a;
				}

				break;

			case GL_LUMINANCE:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];

					// Cv = LtCf
					Lt = localTexels[i].r;

					localColor.r *= Lt;
					localColor.g *= Lt;
					localColor.b *= Lt;
					// Av = Af
				}

				break;

			case GL_LUMINANCE_ALPHA:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];

					// Cv = CfLt
					// Av = AfAt
					Lt = localTexels[i].r;

					localColor.r *= Lt;
					localColor.g *= Lt;
					localColor.b *= Lt;
					localColor.a *= Lt;
				}

				break;

			case GL_INTENSITY:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];

					// Cv = CfIt
					// Av = AfIt
					It = localTexels[i].r;

					localColor.r *= It;
					localColor.g *= It;
					localColor.b *= It;
					localColor.a *= It;
				}

				break;

			case GL_RGB:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];
					localTexel = localTexels[i];

					// Cv = CfCt
					localColor.r *= localTexel.r;
					localColor.g *= localTexel.g;
					localColor.b *= localTexel.b;
					// Av = Af
				}

				break;

			case GL_RGBA:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];
					localTexel = localTexels[i];

					// Cv = CfCt
					// Av = AfAt

					localColor.r *= localTexel.r;
					localColor.g *= localTexel.g;
					localColor.b *= localTexel.b;
					localColor.a *= localTexel.a;
				}

				break;

			default:
				throw new GLInvalidEnumException("GLTextureOperations.combine(GLSpan, GLTextureUnitState, GLColor[])");
			}

			break;

		case GL_DECAL:

			switch (format) {
			case GL_ALPHA:
			case GL_LUMINANCE:
			case GL_LUMINANCE_ALPHA:
			case GL_INTENSITY:

				// undefined
				break;

			case GL_RGB:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];
					localTexel = localTexels[i];

					// Cv = Ct
					localColor.r = localTexel.r;
					localColor.g = localTexel.g;
					localColor.b = localTexel.b;
					// Av = Af
				}

				break;

			case GL_RGBA:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];
					localTexel = localTexels[i];

					// Cv = Cf(1-At) + CtAt
					t = localTexels[i].a;
					s = 1.0f - t;

					localColor.r *= s;
					localColor.g *= s;
					localColor.b *= s;

					localColor.r += localTexel.r * t;
					localColor.g += localTexel.g * t;
					localColor.b += localTexel.b * t;
					// Av = Af
				}

				break;

			default:
				throw new GLInvalidEnumException("GLTextureOperations.combine(GLSpan, GLTextureUnitState, GLColor[])");
			}

			break;

		case GL_BLEND:

			final GLColor envColor = (GLColor) env.color;

			final float Rc = envColor.r;
			final float Gc = envColor.g;
			final float Bc = envColor.b;
			final float Ac = envColor.a;

			switch (format) {
			case GL_ALPHA:

				for (i = length; --i >= 0;) {
					// Cv = Cf
					// Av = AfAt
					localColors[i].a *= localTexels[i].a;
				}

				break;

			case GL_LUMINANCE:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];

					// Cv = Cf(1-Lt) + CcLt
					Lt = localTexels[i].r;
					s = 1.0f - Lt;

					localColor.r *= s;
					localColor.g *= s;
					localColor.b *= s;

					localColor.r += Rc * Lt;
					localColor.g += Gc * Lt;
					localColor.b += Bc * Lt;
					// Av = Af
				}

				break;

			case GL_LUMINANCE_ALPHA:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];

					// Cv = Cf(1-Lt) + CcLt
					Lt = localTexels[i].r;
					s = 1.0f - Lt;

					localColor.r *= s;
					localColor.g *= s;
					localColor.b *= s;

					localColor.r += Rc * Lt;
					localColor.g += Gc * Lt;
					localColor.b += Bc * Lt;

					// Av = AfAt
					localColor.a *= localTexels[i].a;
				}

				break;

			case GL_INTENSITY:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];

					// Cv = Cf(1-It) + CcLt
					It = localTexels[i].r;
					s = 1.0f - It;

					localColor.r *= s;
					localColor.g *= s;
					localColor.b *= s;

					localColor.r += Rc * It;
					localColor.g += Gc * It;
					localColor.b += Bc * It;

					// Av = Af(1-It) + Ac*It
					localColor.a *= s;
					localColor.a += Ac * It;
				}

				break;

			case GL_RGB:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];
					localTexel = localTexels[i];

					// Cv = Cf(1-Ct) + CcCt
					It = localTexel.r;

					localColor.r *= 1.0f - It;
					localColor.r += Rc * It;

					It = localTexel.g;

					localColor.g *= 1.0f - It;
					localColor.g += Gc * It;

					It = localTexel.b;

					localColor.b *= 1.0f - It;
					localColor.b += Bc * It;
					// Av = Af
				}

				break;

			case GL_RGBA:

				for (i = length; --i >= 0;) {
					localColor = localColors[i];
					localTexel = localTexels[i];

					// Cv = Cf(1-Ct) + CcCt
					It = localTexel.r;

					localColor.r *= 1.0f - It;
					localColor.r += Rc * It;

					It = localTexel.g;

					localColor.g *= 1.0f - It;
					localColor.g += Gc * It;

					It = localTexel.b;

					localColor.b *= 1.0f - It;
					localColor.b += Bc * It;

					// Av = AfAt
					localColor.a *= localTexels[i].a;
				}

				break;

			default:
				throw new GLInvalidEnumException("GLTextureOperations.combine(GLSpan, GLTextureUnitState, GLColor[])");
			}
			break;

		default:
			throw new GLInvalidEnumException("GLTextureOperations.combine(GLSpan, GLTextureUnitState, GLColor[])");
		}
	}

	private void nearest(final GLSpan span, final GLTextureObject textureObject, final GLColor[] unitColor) {
		final GLTextureImage image = textureObject.level[0];
		final GLTextureBuffer buffer = image.buffer;

		final int border = image.border;
		final int width = image.width;
		final int height = image.height;
		final int[] data = buffer.data;

		final GLVector4f[] texture = span.textureCoordinates[0].coordinates;

		final int length = span.length;

		final GLColor borderColor = textureObject.params.borderColor;
		final boolean[] mask = span.mask;

		final int sWrapMode = textureObject.params.sWrapMode;
		final int tWrapMode = textureObject.params.tWrapMode;

		int i, j;

		for (int k = length; --k >= 0;) {
			if (mask[k]) {
				i = COMPUTE_NEAREST_TEXEL_LOCATION(sWrapMode, texture[k].x, width);
				j = COMPUTE_NEAREST_TEXEL_LOCATION(tWrapMode, texture[k].y, width);

				i += border;
				j += border;

				if (i < 0 || i >= width || j < 0 || j >= height)
					unitColor[k].set(borderColor);
				else
					unitColor[k].set(data[i + (j * width)]);
			}
		}
	}

	private final void linear(final GLSpan span, final GLTextureObject textureObject, final GLColor[] unitColor) {
		final GLTextureImage image = textureObject.level[0];
		final GLTextureBuffer buffer = image.buffer;

		final int border = image.border;
		final int width = image.width;
		final int height = image.height;
		final int[] data = buffer.data;

		final GLVector4f[] localCoordinates = span.textureCoordinates[0].coordinates;
		GLVector4f localCoordinate;

		final int length = span.length;

		final GLColor[] localUnitColors = (GLColor[]) unitColor;
		GLColor localColor;

		final GLColor borderColor = textureObject.params.borderColor;
		final boolean[] mask = span.mask;

		final int sWrapMode = textureObject.params.sWrapMode;
		final int tWrapMode = textureObject.params.tWrapMode;

		float u, v;
		int i0, i1, j0, j1;
		float w00, w01, w10, w11;

		int useBorderColor;

		for (int k = length; --k >= 0;) {
			if (mask[k]) {
				localCoordinate = localCoordinates[k];

				u = COMPUTE_LINEAR_TEXEL_LOCATIONS(sWrapMode, localCoordinate.x, width, i0i1); // u = FRAC(sv[k] * width - 0.5f)
				v = COMPUTE_LINEAR_TEXEL_LOCATIONS(tWrapMode, localCoordinate.y, width, j0j1); // v = FRAC(tv[k] * width - 0.5f)

				i0 = i0i1[0];
				i1 = i0i1[1];
				j0 = j0j1[0];
				j1 = j0j1[1];

				useBorderColor = 0;

				if (border != 0) {
					i0 += border;
					i1 += border;
					j0 += border;
					j1 += border;
				}

				if (i0 < 0 || i0 >= width)
					useBorderColor |= I0BIT;

				if (i1 < 0 || i1 >= width)
					useBorderColor |= I1BIT;

				if (j0 < 0 || j0 >= height)
					useBorderColor |= J0BIT;

				if (j1 < 0 || j1 >= height)
					useBorderColor |= J1BIT;

				w00 = (1.0f - u) * (1.0f - v);
				w10 = u * (1.0f - v);
				w01 = (1.0f - u) * v;
				w11 = u * v;

				j0 *= width;

				if ((useBorderColor & (I0BIT | J0BIT)) != 0)
					t00.set(borderColor);
				else
					t00.set(data[i0 + j0]);

				if ((useBorderColor & (I1BIT | J0BIT)) != 0)
					t10.set(borderColor);
				else
					t10.set(data[i1 + j0]);

				j1 *= width;

				if ((useBorderColor & (I0BIT | J1BIT)) != 0)
					t01.set(borderColor);
				else
					t01.set(data[i0 + j1]);

				if ((useBorderColor & (I1BIT | J1BIT)) != 0)
					t11.set(borderColor);
				else
					t11.set(data[i1 + j1]);

				// weight the texels

				t00.r *= w00;
				t00.g *= w00;
				t00.b *= w00;
				t00.a *= w00;

				t10.r *= w10;
				t10.g *= w10;
				t10.b *= w10;
				t10.a *= w10;

				t01.r *= w01;
				t01.g *= w01;
				t01.b *= w01;
				t01.a *= w01;

				t11.r *= w11;
				t11.g *= w11;
				t11.b *= w11;
				t11.a *= w11;

				// add them all together

				localColor = localUnitColors[k];

				localColor.r = t00.r + t10.r + t01.r + t11.r;
				localColor.g = t00.g + t10.g + t01.g + t11.g;
				localColor.b = t00.b + t10.b + t01.b + t11.b;
				localColor.a = t00.a + t10.a + t01.a + t11.a;
			}
		}
	}

	private final int COMPUTE_NEAREST_TEXEL_LOCATION(final int wrapMode, final float s, final int size) {
		int i;
		final float f;

		if (wrapMode == GL_REPEAT) {
			f = s * size; // i = IFLOOR(s * size)
			i = (int) (f > 0.0f ? f + 0.5f : f - 0.5f);

			if (i > f)
				--i;

			i &= (size - 1);
		} else if (wrapMode == GL_CLAMP) {
			if (s <= 0.0f)
				i = 0;
			else if (s >= 1.0f)
				i = size - 1;
			else {
				f = s * size; // i = IFLOOR(s * size)
				i = (int) (f > 0.0f ? f + 0.5f : f - 0.5f);

				if (i > f)
					--i;
			}
		} else {
			throw new GLInvalidEnumException("GLTextureUnitState.COMPUTE_NEAREST_TEXEL_LOCATION(int, float, int)");
		}

		return i;
	}

	protected final float COMPUTE_LINEAR_TEXEL_LOCATIONS(final int wrapMode, final float s, final int size,
			final int[] ij) {
		final float f;
		int i;

		if (wrapMode == GL_REPEAT) {
			f = s * size - 0.5f; // i = IFLOOR(s * size - 0.5f)
			i = (int) (f > 0.0f ? f + 0.5f : f - 0.5f);

			if (i > f)
				--i;

			ij[0] = i & (size - 1);
			ij[1] = (ij[0] + 1) & (size - 1);

			return f - i; // FRAC(s * size - 0.5f);
		} else if (wrapMode == GL_CLAMP) {
			if (s <= 0.0f)
				f = -0.5f;
			else if (s >= 1.0f)
				f = (float) size - 0.5f;
			else
				f = (s * size) - 0.5f;

			i = (int) (f > 0.0f ? f + 0.5f : f - 0.5f);

			if (i > f)
				--i;

			ij[0] = i;
			ij[1] = i + 1;

			return f - i;
		} else
			throw new GLInvalidEnumException("GLTextureUnitState.COMPUTE_NEAREST_TEXEL_LOCATION(int, float, int)");
	}
}
