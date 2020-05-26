package sdljavax.gfx;

/**
 *  sdljava - a java binding to the SDL API
 *
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 */
import sdljava.SDLException;
import sdljava.util.Dimension;
import sdljava.video.SDLSurface;
import sdljava.x.swig.SDL_Surface;
import sdljava.x.swig.SWIG_SDLGfx;

//import java.nio.ShortBuffer;

/**
 * Binding to the SDL_gfx library
 * <P>
 * <B><I>Note: all ___Color routines expect the color to be in format 0xRRGGBBAA </B></I>
 * <P>
 * Also note in the functions which take a list of points (ShortBuffer), the
 * length is inferred from the size of the buffer, you do not have to specify it to the
 * function, SDLGfx will take care of this for you.  <I>No checking is done to ensure
 * vx.capacity() = vy[].capacity() so make sure they are the same length or the results could
 * will most likely be a segfault.</I>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLGfx.java,v 1.10 2005/02/19 02:02:06 ivan_ganza Exp $ 
 */
public class SDLGfx {
	/**
	 *  <code>pixelColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int pixelColor(SDLSurface dst, int x, int y, long color) {
		return SWIG_SDLGfx.pixelColor(dst.getSwigSurface(), (short) x, (short) y, color);
	}

	/**
	 *  <code>pixelRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>short</code> value
	 * @param y a <code>short</code> value
	 * @param r a <code>short</code> value
	 * @param g a <code>short</code> value
	 * @param b a <code>short</code> value
	 * @param a a <code>short</code> value
	 * @return an <code>int</code> value
	 */
	public static int pixelRGBA(SDLSurface dst, int x, int y, int r, int g, int b, int a) {
		return SWIG_SDLGfx.pixelRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) r, (short) g, (short) b,
				(short) a);
	}

	/**
	 *  <code>hlineColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x1 a <code>int</code> value
	 * @param x2 a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int hlineColor(SDLSurface dst, int x1, int x2, int y, long color) {
		return SWIG_SDLGfx.hlineColor(dst.getSwigSurface(), (short) x1, (short) x2, (short) y, color);
	}

	/**
	 *  <code>hlineRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x1 a <code>int</code> value
	 * @param x2 a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int hlineRGBA(SDLSurface dst, int x1, int x2, int y, int r, int g, int b, int a) {
		return SWIG_SDLGfx.hlineRGBA(dst.getSwigSurface(), (short) x1, (short) x2, (short) y, (short) r, (short) g,
				(short) b, (short) a);
	}

	/**
	 *  <code>vlineColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y1 a <code>int</code> value
	 * @param y2 a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int vlineColor(SDLSurface dst, int x, int y1, int y2, long color) {
		return SWIG_SDLGfx.vlineColor(dst.getSwigSurface(), (short) x, (short) y1, (short) y2, color);
	}

	/**
	 *  <code>vlineRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y1 a <code>int</code> value
	 * @param y2 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int vlineRGBA(SDLSurface dst, int x, int y1, int y2, int r, int g, int b, int a) {
		return SWIG_SDLGfx.vlineRGBA(dst.getSwigSurface(), (short) x, (short) y1, (short) y2, (short) r, (short) g,
				(short) b, (short) a);
	}

	/**
	 *  <code>rectangleColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x1 a <code>int</code> value
	 * @param y1 a <code>int</code> value
	 * @param x2 a <code>int</code> value
	 * @param y2 a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int rectangleColor(SDLSurface dst, int x1, int y1, int x2, int y2, long color) {
		return SWIG_SDLGfx.rectangleColor(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, color);
	}

	/**
	 *  <code>rectangleRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x1 a <code>int</code> value
	 * @param y1 a <code>int</code> value
	 * @param x2 a <code>int</code> value
	 * @param y2 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int rectangleRGBA(SDLSurface dst, int x1, int y1, int x2, int y2, int r, int g, int b, int a) {
		return SWIG_SDLGfx.rectangleRGBA(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2,
				(short) r, (short) g, (short) b, (short) a);
	}

	/**
	 *  <code>boxColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x1 a <code>int</code> value
	 * @param y1 a <code>int</code> value
	 * @param x2 a <code>int</code> value
	 * @param y2 a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int boxColor(SDLSurface dst, int x1, int y1, int x2, int y2, long color) {
		return SWIG_SDLGfx.boxColor(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, color);
	}

	/**
	 *  <code>boxRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x1 a <code>int</code> value
	 * @param y1 a <code>int</code> value
	 * @param x2 a <code>int</code> value
	 * @param y2 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int boxRGBA(SDLSurface dst, int x1, int y1, int x2, int y2, int r, int g, int b, int a) {
		return SWIG_SDLGfx.boxRGBA(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, (short) r,
				(short) g, (short) b, (short) a);
	}

	/**
	 *  <code>lineColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x1 a <code>int</code> value
	 * @param y1 a <code>int</code> value
	 * @param x2 a <code>int</code> value
	 * @param y2 a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int lineColor(SDLSurface dst, int x1, int y1, int x2, int y2, long color) {
		return SWIG_SDLGfx.lineColor(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, color);
	}

	/**
	 *  <code>lineRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int lineRGBA(SDLSurface dst, int x1, int y1, int x2, int y2, int r, int g, int b, int a) {
		return SWIG_SDLGfx.lineRGBA(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, (short) r,
				(short) g, (short) b, (short) a);
	}

	/**
	 *  <code>aalineColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int aalineColor(SDLSurface dst, int x1, int y1, int x2, int y2, long color) {
		return SWIG_SDLGfx.aalineColor(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, color);
	}

	/**
	 *  <code>aalineRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int aalineRGBA(SDLSurface dst, int x1, int y1, int x2, int y2, int r, int g, int b, int a) {
		return SWIG_SDLGfx.aalineRGBA(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, (short) r,
				(short) g, (short) b, (short) a);
	}

	/**
	 *  <code>circleColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int circleColor(SDLSurface dst, int x, int y, int r, long color) {
		return SWIG_SDLGfx.circleColor(dst.getSwigSurface(), (short) x, (short) y, (short) r, color);
	}

	/**
	 *  <code>circleRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rad a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int circleRGBA(SDLSurface dst, int x, int y, int rad, int r, int g, int b, int a) {
		return SWIG_SDLGfx.circleRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) rad, (short) r, (short) g,
				(short) b, (short) a);
	}

	/**
	 *  <code>aacircleColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int aacircleColor(SDLSurface dst, int x, int y, int r, long color) {
		return SWIG_SDLGfx.aacircleColor(dst.getSwigSurface(), (short) x, (short) y, (short) r, color);
	}

	/**
	 *  <code>aacircleRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rad a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int aacircleRGBA(SDLSurface dst, int x, int y, int rad, int r, int g, int b, int a) {
		return SWIG_SDLGfx.aacircleRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) rad, (short) r, (short) g,
				(short) b, (short) a);
	}

	/**
	 *  <code>filledCircleColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledCircleColor(SDLSurface dst, int x, int y, int r, long color) {
		return SWIG_SDLGfx.filledCircleColor(dst.getSwigSurface(), (short) x, (short) y, (short) r, color);
	}

	/**
	 *  <code>filledCircleRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rad a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledCircleRGBA(SDLSurface dst, int x, int y, int rad, int r, int g, int b, int a) {
		return SWIG_SDLGfx.filledCircleRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) rad, (short) r,
				(short) g, (short) b, (short) a);
	}

	/**
	 *  <code>ellipseColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rx a <code>int</code> value
	 * @param ry a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int ellipseColor(SDLSurface dst, int x, int y, int rx, int ry, long color) {
		return SWIG_SDLGfx.ellipseColor(dst.getSwigSurface(), (short) x, (short) y, (short) rx, (short) ry, color);
	}

	/**
	 *  <code>ellipseRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rx a <code>int</code> value
	 * @param ry a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int ellipseRGBA(SDLSurface dst, int x, int y, int rx, int ry, int r, int g, int b, int a) {
		return SWIG_SDLGfx.ellipseRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) rx, (short) ry, (short) r,
				(short) g, (short) b, (short) a);
	}

	/**
	 *  <code>aaellipseColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param xc a <code>int</code> value
	 * @param yc a <code>int</code> value
	 * @param rx a <code>int</code> value
	 * @param ry a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int aaellipseColor(SDLSurface dst, int xc, int yc, int rx, int ry, long color) {
		return SWIG_SDLGfx.aaellipseColor(dst.getSwigSurface(), (short) xc, (short) yc, (short) rx, (short) ry, color);
	}

	/**
	 *  <code>aaellipseRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rx a <code>int</code> value
	 * @param ry a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int aaellipseRGBA(SDLSurface dst, int x, int y, int rx, int ry, int r, int g, int b, int a) {
		return SWIG_SDLGfx.aaellipseRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) rx, (short) ry, (short) r,
				(short) g, (short) b, (short) a);
	}

	/**
	 *  <code>filledEllipseColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rx a <code>int</code> value
	 * @param ry a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledEllipseColor(SDLSurface dst, int x, int y, int rx, int ry, long color) {
		return SWIG_SDLGfx
				.filledEllipseColor(dst.getSwigSurface(), (short) x, (short) y, (short) rx, (short) ry, color);
	}

	/**
	 *  <code>filledEllipseRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rx a <code>int</code> value
	 * @param ry a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledEllipseRGBA(SDLSurface dst, int x, int y, int rx, int ry, int r, int g, int b, int a) {
		return SWIG_SDLGfx.filledEllipseRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) rx, (short) ry,
				(short) r, (short) g, (short) b, (short) a);
	}

	/**
	 *  <code>filledpieColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rad a <code>int</code> value
	 * @param start a <code>int</code> value
	 * @param end a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledpieColor(SDLSurface dst, int x, int y, int rad, int start, int end, long color) {
		return SWIG_SDLGfx.filledPieColor(dst.getSwigSurface(), (short) x, (short) y, (short) rad, (short) start,
				(short) end, (short) color);
	}

	/**
	 *  <code>filledpieRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param rad a <code>int</code> value
	 * @param start a <code>int</code> value
	 * @param end a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledpieRGBA(SDLSurface dst, int x, int y, int rad, int start, int end, int r, int g, int b,
			int a) {
		return SWIG_SDLGfx.filledPieRGBA(dst.getSwigSurface(), (short) x, (short) y, (short) rad, (short) start,
				(short) end, (short) r, (short) g, (short) b, (short) a);
	}

	/**
	 *  <code>trigonColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param x3 a <code>int</code> value
	 * @param y3 a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int trigonColor(SDLSurface dst, int x1, int y1, int x2, int y2, int x3, int y3, long color) {
		return SWIG_SDLGfx.trigonColor(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2,
				(short) x3, (short) y3, color);
	}

	/**
	 *  <code>trigonRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param x3 a <code>int</code> value
	 * @param y3 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int trigonRGBA(SDLSurface dst, int x1, int y1, int x2, int y2, int x3, int y3, int r, int g, int b,
			int a) {
		return SWIG_SDLGfx.trigonRGBA(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2, (short) x3,
				(short) y3, (short) r, (short) g, (short) b, (short) a);
	}

	/**
	 *  <code>aatrigonColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param x3 a <code>int</code> value
	 * @param y3 a <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int aatrigonColor(SDLSurface dst, int x1, int y1, int x2, int y2, int x3, int y3, long color) {
		return SWIG_SDLGfx.aatrigonColor(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2,
				(short) x3, (short) y3, color);
	}

	/**
	 *  <code>aatrigonRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param x3 a <code>int</code> value
	 * @param y3 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int aatrigonRGBA(SDLSurface dst, int x1, int y1, int x2, int y2, int x3, int y3, int r, int g, int b,
			int a) {
		return SWIG_SDLGfx.aatrigonRGBA(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2,
				(short) x3, (short) y3, (short) r, (short) g, (short) b, (short) a);
	}

	/**
	 *  <code>filledTrigonColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param x3 a <code>int</code> value
	 * @param y3 a <code>int</code> value
	 * @param color an <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledTrigonColor(SDLSurface dst, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
		return SWIG_SDLGfx.filledTrigonColor(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2,
				(short) x3, (short) y3, color);
	}

	/**
	 *  <code>filledTrigonRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param (short)x1 a <code>int</code> value
	 * @param (short)y1 a <code>int</code> value
	 * @param (short)x2 a <code>int</code> value
	 * @param (short)y2 a <code>int</code> value
	 * @param x3 a <code>int</code> value
	 * @param y3 a <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int filledTrigonRGBA(SDLSurface dst, int x1, int y1, int x2, int y2, int x3, int y3, int r, int g,
			int b, int a) {
		return SWIG_SDLGfx.filledTrigonRGBA(dst.getSwigSurface(), (short) x1, (short) y1, (short) x2, (short) y2,
				(short) x3, (short) y3, (short) r, (short) g, (short) b, (short) a);
	}

	/**
	 * <code>polygonColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>ShortBuffer</code> value
	 * @param vy a <code>ShortBuffer</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	//    public static int polygonColor(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, long color) {
	//	return SWIG_SDLGfx.SWIG_polygonColor(dst.getSwigSurface(), vx, vy, vx.capacity(), color);
	//    }
	/**
	 * <code>polygonRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>ShortBuffer</code> value
	 * @param vy a <code>ShortBuffer</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	//    public static int polygonRGBA(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, short r, short g, short b, short a) {
	//	return SWIG_SDLGfx.SWIG_polygonRGBA(dst.getSwigSurface(), vx, vy, vx.capacity(), (short)r, (short)g, (short)b, a);
	//    }
	/**
	 *  <code>aapolygonColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>int[]</code> value
	 * @param vy a <code>int[]</code> value
	 * @param n an <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	//    public static int aapolygonColor(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, long color) {
	//	return SWIG_SDLGfx.SWIG_aapolygonColor(dst.getSwigSurface(), vx, vy, vx.capacity(), color);
	//    }
	/**
	 * <code>aapolygonRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>ShortBuffer</code> value
	 * @param vy a <code>ShortBuffer</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	//    public static int aapolygonRGBA(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, short r, short g, short b, short a) {
	//	return SWIG_SDLGfx.SWIG_aapolygonRGBA(dst.getSwigSurface(), vx, vy, vx.capacity(), (short)r, (short)g, (short)b, (short)a);
	//    }
	/**
	 * <code>filledPolygonColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>ShortBuffer</code> value
	 * @param vy a <code>ShortBuffer</code> value
	 * @param color an <code>int</code> value
	 * @return an <code>int</code> value
	 */
	//    public static int filledPolygonColor(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, short color) {
	//	return SWIG_SDLGfx.SWIG_filledPolygonColor(dst.getSwigSurface(), vx, vy, vx.capacity(), color);
	//    }
	/**
	 * <code>filledPolygonRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>ShortBuffer</code> value
	 * @param vy a <code>ShortBuffer</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	//    public static int filledPolygonRGBA(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, short r, short g, short b, short a) {
	//	return SWIG_SDLGfx.SWIG_filledPolygonRGBA(dst.getSwigSurface(), vx, vy, vx.capacity(), (short)r, (short)g, (short)b, (short)a);
	//    }
	/**
	 * <code>bezierColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>ShortBuffer</code> value
	 * @param vy a <code>ShortBuffer</code> value
	 * @param s an <code>int</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	//    public static int bezierColor(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, short s, long color)
	//        throws SDLException
	//    {
	//	return SWIG_SDLGfx.SWIG_bezierColor(dst.getSwigSurface(), vx, vy, vx.capacity(), s, color);
	//    }

	/**
	 * <code>bezierRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param vx a <code>ShortBuffer</code> value
	 * @param vy a <code>ShortBuffer</code> value
	 * @param s an <code>int</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	//    public static int bezierRGBA(SDLSurface dst, ShortBuffer vx, ShortBuffer vy, short s, short r, short g, short b, short a) {
	//	return SWIG_SDLGfx.SWIG_bezierRGBA(dst.getSwigSurface(), vx, vy, vx.capacity(), (short)s, (short)r, (short)g, (short)b, (short)a);
	//    }
	/**
	 *  <code>characterColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param c a <code>char</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int characterColor(SDLSurface dst, int x, int y, char c, long color) {
		return SWIG_SDLGfx.characterColor(dst.getSwigSurface(), (short) x, (short) y, c, color);
	}

	/**
	 *  <code>characterRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param c a <code>char</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int characterRGBA(SDLSurface dst, int x, int y, char c, int r, int g, int b, int a) {
		return SWIG_SDLGfx.characterRGBA(dst.getSwigSurface(), (short) x, (short) y, c, (short) r, (short) g,
				(short) b, (short) a);
	}

	/**
	 *  <code>stringColor</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param c a <code>String</code> value
	 * @param color a <code>long</code> value
	 * @return an <code>int</code> value
	 */
	public static int stringColor(SDLSurface dst, int x, int y, String c, long color) {
		return SWIG_SDLGfx.stringColor(dst.getSwigSurface(), (short) x, (short) y, c, color);
	}

	/**
	 *  <code>stringRGBA</code> 
	 *
	 * @param dst a <code>SDLSurface</code> value
	 * @param x a <code>int</code> value
	 * @param y a <code>int</code> value
	 * @param c a <code>String</code> value
	 * @param r a <code>int</code> value
	 * @param g a <code>int</code> value
	 * @param b a <code>int</code> value
	 * @param a a <code>int</code> value
	 * @return an <code>int</code> value
	 */
	public static int stringRGBA(SDLSurface dst, int x, int y, String c, int r, int g, int b, int a) {
		return SWIG_SDLGfx.stringRGBA(dst.getSwigSurface(), (short) x, (short) y, c, (short) r, (short) g, (short) b,
				(short) a);
	}

	/**
	 *  Rotates and zoomes a 32bit or 8bit 'src' surface to newly
	 *  created 'dst' surface.  'angle' is the rotation in
	 *  degrees. 'zoom' a scaling factor. If 'smooth' is true then the
	 *  destination 32bit surface is anti-aliased. If the surface is
	 *  not 8bit or 32bit RGBA/ABGR it will be converted into a 32bit
	 *  RGBA format on the fly.
	 *
	 * @param src a <code>SDLSurface</code> value
	 * @param angle a <code>double</code> value
	 * @param zoom a <code>double</code> value
	 * @param smooth an <code>int</code> value
	 * @return a <code>SDLSurface</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLSurface rotozoomSurface(SDLSurface src, double angle, double zoom, boolean smooth)
			throws SDLException {
		SDL_Surface swigSurface = SWIG_SDLGfx
				.rotozoomSurface(src.getSwigSurface(), angle, zoom, smooth == true ? 1 : 0);
		if (swigSurface == null) {
			throw new SDLException("rotozoomSurface operation failed!");
		}
		return new SDLSurface(swigSurface);
	}

	/**
	 * Compute the size that <I>would</I> be required to hold the surface after the zoom
	 *
	 * @param width an <code>int</code> value
	 * @param height an <code>int</code> value
	 * @param angle a <code>double</code> value
	 * @param zoom a <code>double</code> value
	 * @return a <code>Dimension</code> value
	 * @exception SDLException if an error occurs
	 */
	public static Dimension rotozoomSurfaceSize(int width, int height, double angle, double zoom) throws SDLException {

		int dstWidth[] = { 0 };
		int dstHeight[] = { 0 };

		SWIG_SDLGfx.rotozoomSurfaceSize(width, height, angle, zoom, dstWidth, dstHeight);
		return new Dimension(dstWidth[0], dstHeight[0]);
	}

	/**
	 * Zoomes a 32bit or 8bit 'src' surface to newly created 'dst'
	 * surface.  'zoomx' and 'zoomy' are scaling factors for width and
	 * height. If 'smooth' is true then the destination 32bit surface is
	 * anti-aliased. If the surface is not 8bit or 32bit RGBA/ABGR it
	 * will be converted into a 32bit RGBA format on the fly.
	 *
	 * @param src a <code>SDLSurface</code> value
	 * @param zoomx a <code>double</code> value
	 * @param zoomy a <code>double</code> value
	 * @param smooth an <code>int</code> value
	 * @return a <code>SDLSurface</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLSurface zoomSurface(SDLSurface src, double zoomx, double zoomy, boolean smooth)
			throws SDLException {
		SDL_Surface swigSurface = SWIG_SDLGfx.zoomSurface(src.getSwigSurface(), zoomx, zoomy, smooth == true ? 1 : 0);
		if (swigSurface == null) {
			throw new SDLException("zoomSurface operation failed!");
		}
		return new SDLSurface(swigSurface);
	}

	/**
	 * Compute the size that <I>would</I> be required to hold the surface after the zoom
	 *
	 * @param width an <code>int</code> value
	 * @param height an <code>int</code> value
	 * @param zoomx a <code>double</code> value
	 * @param zoomy a <code>double</code> value
	 * @return
	 * @exception SDLException if an error occurs
	 */
	public static Dimension zoomSurfaceSize(int width, int height, double zoomx, double zoomy) throws SDLException {
		int dstWidth[] = { 0 };
		int dstHeight[] = { 0 };

		SWIG_SDLGfx.zoomSurfaceSize(width, height, zoomx, zoomy, dstWidth, dstHeight);
		return new Dimension(dstWidth[0], dstHeight[0]);
	}
}