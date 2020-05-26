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

import org.thenesis.pjogles.GLSoftwareContext;

/**
 * An OpenGL color.
 *
 * @author tdinneen
 */
public final class GLColor {
	private static final float scalef = 1.0f / 255.0f;

	public float r, g, b, a;

	public GLColor() {
		set(0.0f, 0.0f, 0.0f, 1.0f);
	}

	public GLColor(final GLColor c) {
		set(c);
	}

	public GLColor(final float r, final float g, final float b, final float a) {
		set(r, g, b, a);
	}

	public final void set(final int c) {
		a = ((c >> 24) & 0xFF) * scalef;
		r = ((c >> 16) & 0xFF) * scalef;
		g = ((c >> 8) & 0xFF) * scalef;
		b = (c & 0xFF) * scalef;
	}

	public final void set(final int r, final int g, final int b, final int a) {
		set(r * scalef, g * scalef, b * scalef, a * scalef);
	}

	public final void set(final GLColor c) {
		r = c.r;
		g = c.g;
		b = c.b;
		a = c.a;
	}

	public final void set(final float r, final float g, final float b, final float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public final void set(final float[] c) {
		r = c[0];
		g = c[1];
		b = c[2];
		a = c[3];
	}

	public void setIndexi(final int i) {
		r = (float) i;
	}

	public int getIndexi() {
		return (int) r;
	}

	public final int getRGBAi() {
		return ((((int) (a * 255.0f)) << 24) & 0xFF000000) | ((((int) (r * 255.0f)) << 16) & 0x00FF0000)
				| ((((int) (g * 255.0f)) << 8) & 0x0000FF00) | (((int) (b * 255.0f)) & 0x000000FF);
	}

	public final int getRGBAi(final GLSoftwareContext gc, final int c) {
		final int ir, ig, ib, ia;

		if (gc.state.colorBuffer.aMask) {
			ia = (((int) (a * 255.0f)) << 24) & 0xFF000000;
		} else {
			ia = c & 0xFF000000;
		}

		if (gc.state.colorBuffer.rMask) {
			ir = (((int) (r * 255.0f)) << 16) & 0x00FF0000;
		} else {
			ir = c & 0x00FF0000;
		}

		if (gc.state.colorBuffer.gMask) {
			ig = (((int) (g * 255.0f)) << 8) & 0x0000FF00;
		} else {
			ig = c & 0x0000FF00;
		}

		if (gc.state.colorBuffer.bMask) {
			ib = ((int) (b * 255.0f)) & 0x000000FF;
		} else {
			ib = c & 0x000000FF;
		}

		return ia | ir | ig | ib;
	}

	/*
	 public final void scale(final float s)
	 {
	 r *= s;
	 g *= s;
	 b *= s;
	 a *= s;
	 }

	 public final void add(final int c)
	 {
	 a += ((c >> 24) & 0xFF) * scalef;
	 r += ((c >> 16) & 0xFF) * scalef;
	 g += ((c >> 8) & 0xFF) * scalef;
	 b += (c & 0xFF) * scalef;
	 }

	 public final void add(final GLColor c)
	 {
	 final GLColor c4f = (GLColor)c;

	 r += c4f.r;
	 g += c4f.g;
	 b += c4f.b;
	 a += c4f.a;
	 }

	 public final void sub(final GLColor c)
	 {
	 final GLColor c4f = (GLColor)c;

	 r -= c4f.r;
	 g -= c4f.g;
	 b -= c4f.b;
	 a -= c4f.a;
	 }

	 public final void multiply(final GLColor c)
	 {
	 final GLColor c4f = (GLColor)c;

	 a *= c4f.a;
	 r *= c4f.r;
	 g *= c4f.g;
	 b *= c4f.b;
	 }

	 public final void multiply(final int c)
	 {
	 a *= ((c >> 24) & 0xFF) * scalef;
	 r *= ((c >> 16) & 0xFF) * scalef;
	 g *= ((c >> 8) & 0xFF) * scalef;
	 b *= (c & 0xFF) * scalef;
	 }
	 */

	//    public void clamp(float min, float max)
	//    {
	//        if (min > max)
	//        {
	//            float t = min;
	//            min = max;
	//            max = t;
	//        }
	//
	//        if (r < min)
	//            r = min;
	//        else if (r > max)
	//            r = max;
	//
	//        if (g < min)
	//            g = min;
	//        else if (g > max)
	//            g = max;
	//
	//        if (b < min)
	//            b = min;
	//        else if (b > max)
	//            b = max;
	//
	//        if (a < min)
	//            a = min;
	//        else if (a > max)
	//            a = max;
	//    }
	//    public int calcRGBA()
	//    {
	//        return ((((int) (a * 255.0f)) << 24) & 0xFF000000) |
	//                ((((int) (r * 255.0f)) << 16) & 0x00FF0000) |
	//                ((((int) (g * 255.0f)) << 8) & 0x0000FF00) |
	//                (((int) (b * 255.0f)) & 0x000000FF);
	//    }
	//
	//    public int calcRGBAWithMask(GLSoftwareContext gc, int c)
	//    {
	//        int ir, ig, ib, ia;
	//
	//        if(gc.state.colorBuffer.aMask)
	//            ia = (((int) (a * 255.0f)) << 24) & 0xFF000000;
	//        else
	//            ia = c & 0xFF000000;
	//
	//        if(gc.state.colorBuffer.rMask)
	//            ir = (((int) (r * 255.0f)) << 16) & 0x00FF0000;
	//        else
	//            ir = c & 0x00FF0000;
	//
	//        if(gc.state.colorBuffer.gMask)
	//            ig = (((int) (g * 255.0f)) << 8) & 0x0000FF00;
	//        else
	//            ig = c & 0x0000FF00;
	//
	//        if(gc.state.colorBuffer.bMask)
	//            ib = ((int) (b * 255.0f)) & 0x000000FF;
	//        else
	//            ib = c & 0x000000FF;
	//
	//        return ia | ir | ig | ib;
	//    }
	//
	//    public int calcColorIndex()
	//    {
	//        return (int)r;
	//    }
	//
	//    public void scale(float a)
	//    {
	//        this.r *= a;
	//        this.g *= a;
	//        this.b *= a;
	//        this.a *= a;
	//    }
	//
	//    public void add(GLColor c)
	//    {
	//        r += c.r;
	//        g += c.g;
	//        b += c.b;
	//        a += c.a;
	//    }
	//
	//    public void mul(GLColor c)
	//    {
	//        r *= c.r;
	//        g *= c.g;
	//        b *= c.b;
	//        a *= c.a;
	//    }
}
