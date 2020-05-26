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

package org.thenesis.pjogles.texture;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLException;
import org.thenesis.pjogles.GLInvalidEnumException;
import org.thenesis.pjogles.lighting.GLColor;
import org.thenesis.pjogles.pixel.GLPixelPacking;

/**
 * @author tdinneen
 */
public final class GLTextureBuffer implements GLConstants {
	public int width = 0;
	public int height = 0;
	public int depth = 0;

	public final int format;
	public final int[] data;

	public GLTextureBuffer(final int length, final int components, final int format, final int size,
			final GLPixelPacking packing, final Object data) {
		int i, si;
		float r = 0.0f;
		float g = 0.0f;
		float b = 0.0f;
		float a = 1.0f;

		this.width = length - packing.skipPixels;
		this.height = 1;
		this.depth = 1;
		this.data = new int[this.width * this.height * 4];
		this.format = format;

		final GLColor color = new GLColor();

		if (size == 0)
			; // demos.data is GL_BITMAP
		else {
			si = packing.skipPixels;

			for (i = 0; i < this.width; ++i) {
				switch (format) {
				case GL_COLOR_INDEX:
					break;
				case GL_RGB:
					r = getData(si, 0, size, data);
					g = getData(si, 1, size, data);
					b = getData(si, 2, size, data);
					a = (byte) 255;
					break;
				case GL_RGBA:
					r = getData(si, 0, size, data);
					g = getData(si, 1, size, data);
					b = getData(si, 2, size, data);
					a = getData(si, 3, size, data);
					break;
				case GL_RED:
					r = getData(si, 0, size, data);
					g = (byte) 0;
					b = (byte) 0;
					a = (byte) 255;
					break;
				case GL_GREEN:
					r = (byte) 0;
					g = getData(si, 0, size, data);
					b = (byte) 0;
					a = (byte) 255;
					break;
				case GL_BLUE:
					r = (byte) 0;
					g = (byte) 0;
					b = getData(si, 0, size, data);
					a = (byte) 255;
					break;
				case GL_ALPHA:
					r = (byte) 0;
					g = (byte) 0;
					b = (byte) 0;
					a = getData(si, 0, size, data);
					break;
				case GL_LUMINANCE:
					r = getData(si, 0, size, data);
					g = r;
					b = r;
					a = (byte) 255;
					break;
				case GL_LUMINANCE_ALPHA:
					r = getData(si, 0, size, data);
					g = r;
					b = r;
					a = getData(si, 1, size, data);
					break;
				}
				///*
				//		    if (need_scale) {
				//		    	r = p.Red.apply_bias_scale(r);
				//		    	g = p.Green.apply_bias_scale(g);
				//		    	b = p.Blue.apply_bias_scale(b);
				//		    	a = p.Alpha.apply_bias_scale(a);
				//		    }
				//*/            /*
				switch (components) {
				case 1:
					color.set(r, 0, 0, 255);
					break;
				case 2:
					color.set(r, 0, 0, a);
					break;
				case 3:
					color.set(r, g, b, 255);
					break;
				case 4:
					color.set(r, g, b, a);
					break;
				default:
					throw new GLInvalidEnumException("GLTextureBuffer(int, int, int, int, GLPixelPacking, Object)");
				}

				this.data[i] = color.getRGBAi();

				++si;
			}
		}
	}

	public GLTextureBuffer(final int width, final int height, final int components, final int format, final int size,
			final GLPixelPacking packing, final Object data) {
		int i, j, si, sj, index;
		int r, g, b, a;

		this.width = width - packing.skipPixels;
		this.height = height - packing.skipRows;
		this.data = new int[this.width * this.height];
		this.depth = 1;
		this.format = format;

		final GLColor color = new GLColor();

		if (size == 0)
			; // demos.data is GL_BITMAP
		else {
			si = packing.skipPixels;

			for (i = 0; i < this.width; ++i) {
				sj = packing.skipRows;

				for (j = 0; j < this.height; ++j) {
					switch (format) {
					case GL_COLOR_INDEX:
						r = getData(si, sj, 0, 1, size, data);
						g = 0;
						b = 0;
						a = 255;
						break;
					case GL_RGB:
						r = getData(si, sj, 0, 3, size, data);
						g = getData(si, sj, 1, 3, size, data);
						b = getData(si, sj, 2, 3, size, data);
						a = 255;
						break;
					case GL_RGBA:
						r = getData(si, sj, 0, 4, size, data);
						g = getData(si, sj, 1, 4, size, data);
						b = getData(si, sj, 2, 4, size, data);
						a = getData(si, sj, 3, 4, size, data);
						break;
					case GL_RED:
						r = getData(si, sj, 0, 1, size, data);
						g = 0;
						b = 0;
						a = 255;
						break;
					case GL_GREEN:
						r = 0;
						g = getData(si, sj, 0, 1, size, data);
						b = 0;
						a = 255;
						break;
					case GL_BLUE:
						r = 0;
						g = 0;
						b = getData(si, sj, 0, 1, size, data);
						a = 255;
						break;
					case GL_ALPHA:
						r = 0;
						g = 0;
						b = 0;
						a = getData(si, sj, 0, 1, size, data);
						break;
					case GL_LUMINANCE:
						r = getData(si, sj, 0, 1, size, data);
						g = r;
						b = r;
						a = 255;
						break;
					case GL_LUMINANCE_ALPHA:
						r = getData(si, sj, 0, 2, size, data);
						g = r;
						b = r;
						a = getData(si, sj, 1, 2, size, data);
						break;
					default:
						throw new GLInvalidEnumException(
								"GLTextureBuffer(int, int, int, int, int, GLPixelPacking, Object)");
					}

					switch (components) {
					case 1:
						color.set(r, 0, 0, 255);
						break;
					case 2:
						color.set(r, 0, 0, a);
						break;
					case 3:
						color.set(r, g, b, 255);
						break;
					case 4:
						color.set(r, g, b, a);
						break;
					default:
						throw new GLInvalidEnumException(
								"GLTextureBuffer(int, int, int, int, int, GLPixelPacking, Object)");
					}

					this.data[i + (j * width)] = color.getRGBAi();

					++sj;
				}

				++si;
			}
		}
	}

	// 1D

	private int getData(final int x, final int i, final byte[] data) {
		return data[x + i] & 0xFF;
	}

	private int getData(final int x, final int i, final short[] data) {
		return data[x + i] & 0xFFFF;
	}

	private int getData(final int x, final int i, final int[] data) {
		return data[x + i] & 0xFFFFFFFF;
	}

	private int getData(final int x, final int i, final float[] data) {
		return (int) (data[x + i] * 255.0f);
	}

	private int getData(final int x, final int i, final int s, final Object data) {
		if (s == 8)
			return getData(x, i, (byte[]) data);
		else if (s == 16)
			return getData(x, i, (short[]) data);
		else if (s == 32)
			return getData(x, i, (int[]) data);
		else if (s == 64)
			return getData(x, i, (float[]) data);
		else
			throw new GLInvalidEnumException("GLTextureBuffer.getData(int, int, int, Object)");
	}

	// 2D

	private int getData(final int x, final int y, final int i, final int c, final byte[] data) {
		return (data[((x + (y * width)) * c) + i]) & 0xFF;
	}

	private int getData(final int x, final int y, final int i, final int c, final byte[][][] data) {
		return data[x][y][i] & 0xFF;
	}

	private int getData(final int x, final int y, final int i, final int c, final short[] data) {
		return (data[((x + (y * width)) * c) + i]) & 0xFFFF;
	}

	private int getData(final int x, final int y, final int i, final int c, final int[] data) {
		return (data[((x + (y * width)) * c) + i]) & 0xFFFFFFFF;
	}

	private int getData(final int x, final int y, final int i, final int c, final int s, final Object data) {
		if (s == 8) {
			if (data instanceof byte[])
				return getData(x, y, i, c, (byte[]) data);
			else if (data instanceof byte[][][])
				return getData(x, y, i, c, (byte[][][]) data);
			else
				throw new GLException(
						"GLTextureBuffer.getData(int, int, int, int, Object) - The supplied Object is not an instanceof byte[] or byte[][][].");
		} else if (s == 16)
			return getData(x, y, i, c, (short[]) data);
		else if (s == 32)
			return getData(x, y, i, c, (int[]) data);
		else
			throw new GLInvalidEnumException("GLTextureBuffer.getData(int, int, int, int, int, Object)");
	}
}
