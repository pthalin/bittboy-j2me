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

package org.thenesis.pjogles.lighting;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.GLSoftwareContext;
import org.thenesis.pjogles.primitives.GLSpan;

/**
 * Encapsulates all blending operations.
 *
 * @see org.thenesis.pjogles.GL#glBlendFunc glBlendFunc
 * @see org.thenesis.pjogles.pipeline.GLFragmentOperations GLFragmentOperations
 *
 * @author tdinneen
 */
public final class GLBlending implements GLConstants {
	protected final GLSoftwareContext gc;
	private final GLColor c = new GLColor();

	public GLBlending(final GLSoftwareContext gc) {
		this.gc = gc;
	}

	public final void apply(final GLSpan span, final int[] buffer) {
		final long start = System.currentTimeMillis();

		final GLColor[] localColor = span.color;
		final boolean[] mask = span.mask;

		final int offset = span.offset;

		float Rs, Gs, Bs, As;
		float Rd, Gd, Bd, Ad;
		float sR, sG, sB, sA;
		float dR, dG, dB, dA;

		final int blendEquation = gc.state.colorBuffer.blendEquation;
		final int blendSrcRGB = gc.state.colorBuffer.blendSrcRGB;
		final int blendSrcA = gc.state.colorBuffer.blendSrcA;
		final int blendDstRGB = gc.state.colorBuffer.blendDstRGB;
		final int blendDstA = gc.state.colorBuffer.blendDstA;

		GLColor src;
		final GLColor dst = c;
		final GLColor blendColor = (GLColor) gc.state.colorBuffer.blendColor;

		float r, g, b, a;

		for (int k = span.length; --k >= 0;) {
			if (mask[k]) {
				src = localColor[k];

				Rs = src.r;
				Gs = src.g;
				Bs = src.b;
				As = src.a;

				dst.set(buffer[offset + k]);

				Rd = dst.r;
				Gd = dst.g;
				Bd = dst.b;
				Ad = dst.a;

				switch (blendSrcRGB) {
				case GL_ZERO:
					sR = sG = sB = 0.0f;
					break;
				case GL_ONE:
					sR = sG = sB = 1.0f;
					break;
				case GL_DST_COLOR:
					sR = Rd;
					sG = Gd;
					sB = Bd;
					break;
				case GL_ONE_MINUS_DST_COLOR:
					sR = 1.0f - Rd;
					sG = 1.0f - Gd;
					sB = 1.0f - Bd;
					break;
				case GL_SRC_ALPHA:
					sR = sG = sB = As;
					break;
				case GL_ONE_MINUS_SRC_ALPHA:
					sR = sG = sB = 1.0f - As;
					break;
				case GL_DST_ALPHA:
					sR = sG = sB = Ad;
					break;
				case GL_ONE_MINUS_DST_ALPHA:
					sR = sG = sB = 1.0f - Ad;
					break;
				case GL_SRC_ALPHA_SATURATE: // TOMD - not 100% sure about below, check it out
					if (As < 1.0f - Ad)
						sR = sG = sB = As;
					else
						sR = sG = sB = 1.0f - Ad;
					break;
				case GL_CONSTANT_COLOR:
					sR = blendColor.r;
					sG = blendColor.g;
					sB = blendColor.b;
					break;
				case GL_ONE_MINUS_CONSTANT_COLOR:
					sR = 1.0f - blendColor.r;
					sG = 1.0f - blendColor.g;
					sB = 1.0f - blendColor.b;
					break;
				case GL_CONSTANT_ALPHA:
					sR = sG = sB = blendColor.a;
					break;
				case GL_ONE_MINUS_CONSTANT_ALPHA:
					sR = sG = sB = 1.0f - blendColor.a;
					break;
				case GL_SRC_COLOR: /* GL_NV_blend_square */
					sR = Rs;
					sG = Gs;
					sB = Bs;
					break;
				case GL_ONE_MINUS_SRC_COLOR: /* GL_NV_blend_square */
					sR = 1.0f - Rs;
					sG = 1.0f - Gs;
					sB = 1.0f - Bs;
					break;
				default:
					throw new GLInvalidEnumException("GLBlending.blend(GLSpan, int[])");
				}

				switch (blendSrcA) {
				case GL_ZERO:
					sA = 0.0f;
					break;
				case GL_ONE:
					sA = 1.0f;
					break;
				case GL_DST_COLOR:
					sA = Ad;
					break;
				case GL_ONE_MINUS_DST_COLOR:
					sA = 1.0f - Ad;
					break;
				case GL_SRC_ALPHA:
					sA = As;
					break;
				case GL_ONE_MINUS_SRC_ALPHA:
					sA = 1.0f - As;
					break;
				case GL_DST_ALPHA:
					sA = Ad;
					break;
				case GL_ONE_MINUS_DST_ALPHA:
					sA = 1.0f - Ad;
					break;
				case GL_SRC_ALPHA_SATURATE:
					sA = 1.0f;
					break;
				case GL_CONSTANT_COLOR:
					sA = blendColor.a;
					break;
				case GL_ONE_MINUS_CONSTANT_COLOR:
					sA = 1.0f - blendColor.a;
					break;
				case GL_CONSTANT_ALPHA:
					sA = blendColor.a;
					break;
				case GL_ONE_MINUS_CONSTANT_ALPHA:
					sA = 1.0f - blendColor.a;
					break;
				case GL_SRC_COLOR: // GL_NV_blend_square
					sA = As;
					break;
				case GL_ONE_MINUS_SRC_COLOR: // GL_NV_blend_square
					sA = 1.0f - As;
					break;
				default:
					throw new GLInvalidEnumException("GLBlending.blend(GLSpan, int[])");
				}

				switch (blendDstRGB) {
				case GL_ZERO:
					dR = dG = dB = 0.0f;
					break;
				case GL_ONE:
					dR = dG = dB = 1.0f;
					break;
				case GL_SRC_COLOR:
					dR = Rs;
					dG = Gs;
					dB = Bs;
					break;
				case GL_ONE_MINUS_SRC_COLOR:
					dR = 1.0f - Rs;
					dG = 1.0f - Gs;
					dB = 1.0f - Bs;
					break;
				case GL_SRC_ALPHA:
					dR = dG = dB = As;
					break;
				case GL_ONE_MINUS_SRC_ALPHA:
					dR = dG = dB = 1.0f - As;
					break;
				case GL_DST_ALPHA:
					dR = dG = dB = Ad;
					break;
				case GL_ONE_MINUS_DST_ALPHA:
					dR = dG = dB = 1.0f - Ad;
					break;
				case GL_CONSTANT_COLOR:
					dR = blendColor.r;
					dG = blendColor.g;
					dB = blendColor.b;
					break;
				case GL_ONE_MINUS_CONSTANT_COLOR:
					dR = 1.0f - blendColor.r;
					dG = 1.0f - blendColor.g;
					dB = 1.0f - blendColor.b;
					break;
				case GL_CONSTANT_ALPHA:
					dR = dG = dB = blendColor.a;
					break;
				case GL_ONE_MINUS_CONSTANT_ALPHA:
					dR = dG = dB = 1.0f - blendColor.a;
					break;
				case GL_DST_COLOR: /* GL_NV_blend_square */
					dR = Rd;
					dG = Gd;
					dB = Bd;
					break;
				case GL_ONE_MINUS_DST_COLOR: /* GL_NV_blend_square */
					dR = 1.0f - Rd;
					dG = 1.0f - Gd;
					dB = 1.0f - Bd;
					break;
				default:
					throw new GLInvalidEnumException("GLBlending.blend(GLSpan, int[])");
				}

				switch (blendDstA) {
				case GL_ZERO:
					dA = 0.0f;
					break;
				case GL_ONE:
					dA = 1.0f;
					break;
				case GL_SRC_COLOR:
					dA = As;
					break;
				case GL_ONE_MINUS_SRC_COLOR:
					dA = 1.0f - As;
					break;
				case GL_SRC_ALPHA:
					dA = As;
					break;
				case GL_ONE_MINUS_SRC_ALPHA:
					dA = 1.0f - As;
					break;
				case GL_DST_ALPHA:
					dA = Ad;
					break;
				case GL_ONE_MINUS_DST_ALPHA:
					dA = 1.0f - Ad;
					break;
				case GL_CONSTANT_COLOR:
					dA = blendColor.a;
					break;
				case GL_ONE_MINUS_CONSTANT_COLOR:
					dA = 1.0f - blendColor.a;
					break;
				case GL_CONSTANT_ALPHA:
					dA = blendColor.a;
					break;
				case GL_ONE_MINUS_CONSTANT_ALPHA:
					dA = 1.0f - blendColor.a;
					break;
				case GL_DST_COLOR: /* GL_NV_blend_square */
					dA = Ad;
					break;
				case GL_ONE_MINUS_DST_COLOR: /* GL_NV_blend_square */
					dA = 1.0F - Ad;
					break;
				default:
					throw new GLInvalidEnumException("GLBlending.blend(GLSpan, int[])");
				}

				// compute blended color

				if (blendEquation == GL_FUNC_ADD_EXT) {
					r = Rs * sR + Rd * dR;
					g = Gs * sG + Gd * dG;
					b = Bs * sB + Bd * dB;
					a = As * sA + Ad * dA;
				} else if (blendEquation == GL_FUNC_SUBTRACT_EXT) {
					r = Rs * sR - Rd * dR;
					g = Gs * sG - Gd * dG;
					b = Bs * sB - Bd * dB;
					a = As * sA - Ad * dA;
				} else if (blendEquation == GL_FUNC_REVERSE_SUBTRACT_EXT) {
					r = Rd * dR - Rs * sR;
					g = Gd * dG - Gs * sG;
					b = Bd * dB - Bs * sB;
					a = Ad * dA - As * sA;
				} else
					throw new GLInvalidEnumException("GLBlending.blend(GLSpan, int[])");

				// clamp

				src.set(r < 0.0f ? 0.0f : (r > 1.0f ? 1.0f : r), g < 0.0f ? 0.0f : (g > 1.0f ? 1.0f : g),
						b < 0.0f ? 0.0f : (b > 1.0f ? 1.0f : b), a < 0.0f ? 0.0f : (a > 1.0f ? 1.0f : a));
			}
		}

		gc.benchmark.blendTime += System.currentTimeMillis() - start;
	}
}
