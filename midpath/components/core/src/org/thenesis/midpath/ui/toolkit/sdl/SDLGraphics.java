/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
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
package org.thenesis.midpath.ui.toolkit.sdl;

import javax.microedition.lcdui.FontPeer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.thenesis.midpath.ui.toolkit.virtual.Rectangle;

import sdljava.SDLException;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljavax.gfx.SDLGfx;

import com.sun.midp.log.Logging;

public class SDLGraphics extends Graphics {

	public static final int MAX_CIRCLE_RADIUS = 16384;
	public static final int MAX_ELLIPSE_RADIUS = 16384;
	public static final int REPLACE = 0, BLEND = 1, LOGIC = 2;
	
	private SDLSurface surface;
	private int blendMode = REPLACE;

	long sdlGfxColor;
	long sdlColor;
	
	Rectangle clipRectangle = new Rectangle();

	SDLGraphics(SDLSurface surface) {
		this.surface = surface;
	}

	public SDLSurface getSurface() {
		return surface;
	}

	public synchronized void setColor(int red, int green, int blue) {
		super.setColor(red, green, blue);
		setInternalColor(red, green, blue);
	}

	public synchronized void setColor(int RGB) {

		super.setColor(RGB);

		int red = (RGB >> 16) & 0xff;
		int green = (RGB >> 8) & 0xff;
		int blue = RGB & 0xff;

		setInternalColor(red, green, blue);

	}

	public synchronized void setGrayScale(int value) {
		super.setGrayScale(value);
		setInternalColor(value, value, value);
	}

	private void setInternalColor(int red, int green, int blue) {
		// RRGGBBAA format needed by SDL_gfx
		sdlGfxColor = ((red << 24) | (green << 16) | (blue << 8) | 0xFF) & 0x00000000FFFFFFFFL;

		try {
			sdlColor = surface.mapRGB(red, green, blue);
		} catch (SDLException e) {
		}
	}

	/**
	 * Get a specific pixel value
	 *
	 * @param rgb
	 * @param gray
	 * @param isGray
	 * @return int
	 */
	protected synchronized int getPixel(int rgb, int gray, boolean isGray) {
		// TODO 
		//System.out.println("[DEBUG]SDLGraphics.getPixel : " + Integer.toHexString(rgb));
		if (isGray) {
			return (gray << 16) | (gray << 8) | gray;
		} else {
			return rgb;
		}
	}
	
	public synchronized void setClip(int x, int y, int width, int height) {
		super.setClip(x, y, width, height);
		clipRectangle.set(clipX1, clipY1, clipX2, clipY2);
	}

	public synchronized void clipRect(int x, int y, int width, int height) {
		super.clipRect(x, y, width, height);
		clipRectangle.set(clipX1, clipY1, clipX2, clipY2);
	}

	public void drawRect(int x, int y, int width, int height) {
		x += transX;
		y += transY;
		SDLGfx.rectangleColor(surface, x, y, x + width, y + height, sdlGfxColor);
	}

	/**
	 * Fills the specified rectangle.
	 */
	public synchronized void fillRect(int x, int y, int width, int height) {

		x += transX;
		y += transY;

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG]SDLGraphics.fillRect(): x=" + x + " y1=" + y + " width=" + width + " height= "
					+ height + " color=" + Integer.toHexString((int)sdlColor));

		//System.out.println("[DEBUG]SDLGraphics.fillRect(): " + Long.toHexString(sdlColor));

		// SDLGfx.boxColor(surface, x, y, x + width, x + height, sdlCurrentColor); // doesn't work correctly. Why ?
		try {
			surface.fillRect(new SDLRect(x, y, width, height), sdlColor); //sdlColor); 0x0000FF00L
		} catch (SDLException e) {
			e.printStackTrace();
		}

	}

	public synchronized void drawString(String str, int x, int y, int anchor) {

		FontPeer fontPeer = getFont().getFontPeer();

		if (fontPeer != null) {
			fontPeer.render(this, str, x, y, anchor);
		}

		//		x += transX;
		//		y += transY;
		//
		//		Font font = getFont(); // Font.getDefaultFont();
		//
		//		if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
		//			System.out.println("SDLGraphics.drawString(): BOTTOM");
		//			y -= font.getHeight() - 1;
		//		}
		//
		//		if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
		//			System.out.println("SDLGraphics.drawString(): RIGHT");
		//			x -= font.stringWidth(str) - 1;
		//		} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
		//			x -= font.stringWidth(str) / 2 - 1;
		//		}
		//
		//		// TODO 
		//		System.out.println("SDLGraphics.drawString(): " + str + " x=" + x + " y=" + y + " color="
		//				+ Long.toHexString(sdlColor));
		//		SDLGfx.stringColor(surface, x, y, str, sdlGfxColor); //0xffff00ffL
	}

	//	public void drawSubstring(String str, int offset, int len,
	//            int x, int y, int anchor) {
	//		
	//		// Reject bad arguments
	//		if (str == null) {
	//			throw new NullPointerException();
	//		}
	//		
	//		int length = str.length();
	//		if ((offset >= length) || ((offset + len) >= length)) {
	//			throw new StringIndexOutOfBoundsException();
	//		}
	//	
	//		
	//		
	//		
	//		/*StringIndexOutOfBoundsException if <code>offset</code>
	//     * and <code>length</code> do not specify
	//     * a valid range within the <code>String</code> <code>str</code>
	//     * @throws IllegalArgumentException if <code>anchor</code>
	//     * is not a legal value
	//     * @throws NullPointerException if <code>str</code> is <code>null</code>*/
	//	}

	/**
	 * Draws a line from point (x1, y1) to point (x2, y2).
	 */
	public synchronized void drawLine(int x1, int y1, int x2, int y2) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] SDLGraphics.drawLine() : x1=" + x1 + " y1=" + y1 + " x2=" + x2 + " y2= " + y2
					+ " color=" + Integer.toHexString((int)sdlGfxColor));

		x1 += transX;
		y1 += transY;
		x2 += transX;
		y2 += transY;

		if (y1 == y2) {
			SDLGfx.hlineColor(surface, x1, x2, y1, sdlGfxColor);
		} else if (x1 == x2) {
			SDLGfx.vlineColor(surface, x1, y1, y2, sdlGfxColor);
		} else {
			SDLGfx.lineColor(surface, x1, y1, x2, y2, sdlGfxColor);
		}

	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height,
			boolean processAlpha) {

		// Create a new Image
		int[] buf = new int[width * height];
		for (int b = 0; b < height; b++) {
			for (int a = 0; a < width; a++) {
				buf[a + b * width] = rgbData[offset + a + b * scanlength];
			}
		}
		Image image = Image.createRGBImage(buf, width, height, processAlpha);
		
		// Draw the created image
		drawImage(image, x, y, Graphics.TOP | Graphics.LEFT);

		if (Logging.TRACE_ENABLED)
			System.out.println("SDLGraphics.drawRGB()");
	}

	protected void doCopyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {

		x_src += transX;
		y_src += transY;
		x_dest += transX;
		y_dest += transY;

		//SDLImage transformedImage = new SDLImage(surface, x_src,y_src, width, height, Sprite.TRANS_NONE);

		if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			y_dest -= height - 1;
		} else if ((anchor & Graphics.VCENTER) == Graphics.VCENTER) {
			y_dest -= height / 2 - 1;
		}

		if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
			x_dest -= width - 1;
		} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
			x_dest -= width / 2 - 1;
		}

		try {
			// Copy the source area in an offscreen surface
			SDLSurface dstSurface = ((SDLToolkit)SDLToolkit.getToolkit()).createSDLSurface(width, height);
			SDLRect srcRect = new SDLRect(x_src, y_src, width, height);
			SDLRect dstRect = new SDLRect(0, 0, width, height);
			surface.blitSurface(srcRect, dstSurface, dstRect);

			// Blit the offscreen surface on the current surface
			dstSurface.blitSurface(surface, new SDLRect(x_dest, y_dest, width, height));

		} catch (SDLException e) {
			e.printStackTrace();
		}

	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {

		x1 += transX;
		y1 += transY;
		x2 += transX;
		y2 += transY;
		x3 += transX;
		y3 += transY;

		SDLGfx.trigonColor(surface, x1, y1, x2, y2, x3, y3, sdlGfxColor);
	}
	
	
	
	//--------------------------------------------------------------------------------
	// Span.
	//--------------------------------------------------------------------------------	
	private void drawSpan(int x, int y, int w) {

		//if (Logging.TRACE_ENABLED)
		//	System.out.println("[DEBUG]VirtualGraphics.drawSpan : surface data size= " + surface.data.length + " dstPosition=" + dstPosition + " w=" + w);

		switch (blendMode) {
		case REPLACE:
			drawLine(x, y, x + w, y);
			
//			for (int x = dstPosition; x < (dstPosition + w); x++) {
//				dst[x] = rgbColor;
//			}
			break;
		}

	}

	//	--------------------------------------------------------------------------------
	// Circle.
	//--------------------------------------------------------------------------------
	private void drawQuadrantPoints(int x, int y, int xp, int yp) {
		Rectangle r = clipRectangle;
		int x0 = x - xp, x1 = x + xp, y0 = y - yp, y1 = y + yp;
		boolean left = (x0 >= r.xmin) && (x0 <= r.xmax), right = (x1 >= r.xmin) && (x1 <= r.xmax) && (xp != 0);
		if ((y0 >= r.ymin) && (y0 <= r.ymax)) {
			if (left)
				drawSpan(x0, y0, 1);
			if (right)
				drawSpan(x1, y0, 1);
		}
		if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0)) {
			if (left)
				drawSpan(x0, y1, 1);
			if (right)
				drawSpan(x1, y1, 1);
		}
	}

	private void drawQuadrantSpans(int x, int y, int xp, int yp) {
		Rectangle r = clipRectangle;
		int x0 = x - xp, x1 = x + xp, y0 = y - yp, y1 = y + yp;
		if (x0 < r.xmin)
			x0 = r.xmin;
		if (x1 > r.xmax)
			x1 = r.xmax;
		int w = x1 - x0 + 1;
		if (w > 0) {
			if ((y0 >= r.ymin) && (y0 <= r.ymax))
				drawSpan(x0, y0, w);
			if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0))
				drawSpan(x0, y1, w);
		}
	}

	// Horn's algorithm.
	public void drawCircle(int x, int y, int radius) {
		if ((radius < 0) || (radius > MAX_CIRCLE_RADIUS))
			return;
		x += transX;
		y += transY;

		Rectangle r = clipRectangle;
		int xmin = x - radius, xmax = x + radius, ymin = y - radius, ymax = y + radius;
		if ((xmin > r.xmax) || (xmax < r.xmin) || (ymin > r.ymax) || (ymax < r.ymin))
			return;

		int xp = 0, yp = radius, d = 1 - radius;
		do {
			drawQuadrantPoints(x, y, xp, yp);
			if (yp > xp)
				drawQuadrantPoints(x, y, yp, xp);

			// Update.
			if (d < 0) {
				d += (xp << 1) + 3;
			} else {
				d += ((xp - yp) << 1) + 5;
				yp--;
			}
			xp++;
		} while (yp >= xp);
	}

	// Horn's algorithm.
	public void fillCircle(int x, int y, int radius) {
		if ((radius < 0) || (radius > MAX_CIRCLE_RADIUS))
			return;
		x += transX;
		y += transY;

		Rectangle r = clipRectangle;
		int xmin = x - radius, xmax = x + radius, ymin = y - radius, ymax = y + radius;
		if ((xmin > r.xmax) || (xmax < r.xmin) || (ymin > r.ymax) || (ymax < r.ymin))
			return;

		int xp = 0, yp = radius, d = 1 - radius;
		do {
			if (yp > xp)
				drawQuadrantSpans(x, y, yp, xp);

			// Update.
			if (d < 0) {
				d += (xp << 1) + 3;
			} else {
				drawQuadrantSpans(x, y, xp, yp);
				d += ((xp - yp) << 1) + 5;
				yp--;
			}
			xp++;
		} while (yp >= xp);
	}

	//	--------------------------------------------------------------------------------
	// Ellipse.
	//--------------------------------------------------------------------------------
	// Cf: Graphics Programming Methods; 2.3 - A fast all-integer ellipse discretization algorithm; page 121.  
	public void drawEllipse(int x, int y, int rx, int ry) {
		if ((rx < 0) || (rx > MAX_ELLIPSE_RADIUS) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
			return;

		if (ry == 0) {
			//drawHLine(x - rx, x + rx, y);
			drawLine(x - rx, y, x + rx, y);
			return;
		}
		if (rx == 0) {
			//drawVLine(y - ry, y + ry, x);
			drawLine(x, y - ry, x, y + ry);
			return;
		}
		if (rx == ry) {
			drawCircle(x, y, rx);
			return;
		}

		x += transX;
		y += transY;

		int xp, yp;
		long rx2, ry2;
		long d, u, v, s, t, du, dv;

		rx2 = (long) rx * (long) rx;
		ry2 = (long) ry * (long) ry;

		s = 0;
		t = rx2 * ry;
		d = ry2 - t;
		du = ry2 << 1;
		dv = -rx2 << 1;
		u = du + ry2;
		v = dv * (ry - 1);
		xp = 0;
		yp = ry;

		while (s < t) {
			drawQuadrantPoints(x, y, xp, yp);
			if (d >= 0) {
				d += v;
				v -= dv;
				yp--;
				t -= rx2;
			}
			d += u;
			u += du;
			xp++;
			s += ry2;
		}

		s = 0;
		t = ry2 * rx;
		d = rx2 - t;
		du = rx2 << 1;
		dv = -ry2 << 1;
		u = du + rx2;
		v = dv * (rx - 1);
		xp = rx;
		yp = 0;

		while (s <= t) {
			drawQuadrantPoints(x, y, xp, yp);
			if (d >= 0) {
				d += v;
				v -= dv;
				xp--;
				t -= ry2;
			}
			d += u;
			u += du;
			yp++;
			s += rx2;
		}
	}

	public void fillEllipse(int x, int y, int rx, int ry) {
		if ((rx < 0) || (rx > 16384) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
			return;

		if (ry == 0) {
			//drawHLine(x - rx, x + rx, y);
			drawLine(x - rx, y, x + rx, y);
			return;
		}
		if (rx == 0) {
			//drawVLine(y - ry, y + ry, x);
			drawLine(x, y - ry, x, y + ry);
			return;
		}
		if (rx == ry) {
			fillCircle(x, y, rx);
			return;
		}

		x += transX;
		y += transY;

		int xp, yp;
		long rx2 = (long) rx * (long) rx, ry2 = (long) ry * (long) ry;
		long rxy2, t0, t1, d;
		if (rx > ry) {
			xp = rx;
			yp = 0;
			rxy2 = (long) rx * ry2;
		} else {
			xp = 0;
			yp = ry;
			rxy2 = (long) ry * rx2;
		}
		t0 = rxy2 << 3;
		t1 = 0;
		d = rx2 + ry2 - (rxy2 << 2);
		rx2 <<= 3;
		ry2 <<= 3;

		boolean drawFlag = rx > ry;
		for (int i = rx + ry; i >= 0; i--) {
			if (drawFlag)
				drawQuadrantSpans(x, y, xp, yp);

			// Update.
			if (rx > ry) {
				if (d < 0) {
					yp++;
					t1 += rx2;
					d += t1;
					drawFlag = true;
				} else {
					xp--;
					t0 -= ry2;
					d -= t0;
					drawFlag = false;
				}
			} else {
				if (d < 0) {
					xp++;
					t1 += ry2;
					d += t1;
					drawFlag = false;
				} else {
					yp--;
					t0 -= rx2;
					d -= t0;
					drawFlag = true;
				}
			}
		}
	}

	//	--------------------------------------------------------------------------------
	// Arc. 
	//--------------------------------------------------------------------------------
	// Cf: Graphics Programming Methods; 2.3 - A fast all-integer ellipse discretization algorithm; page 121.

	private void drawArcPoints(int x, int y, int xp, int yp, int xs, int ys, int xe, int ye, boolean reflexAngle) {
		Rectangle r = clipRectangle;
		int x0 = x - xp, x1 = x + xp, y0 = y - yp, y1 = y + yp;
		int xpys = xp * ys, ypxs = yp * xs, xpye = xp * ye, ypxe = yp * xe;
		boolean left = (x0 >= r.xmin) && (x0 <= r.xmax), right = (x1 >= r.xmin) && (x1 <= r.xmax) && (xp != 0), insideFlag;
		if ((y0 >= r.ymin) && (y0 <= r.ymax)) {
			if (left) {
				insideFlag = reflexAngle ? (-xpys - ypxs < 0 || -xpye - ypxe > 0)
						: (-xpys - ypxs < 0 && -xpye - ypxe > 0);
				if (insideFlag)
					drawSpan(x0, y0, 1); // Top left. Second quadrant.
					//drawSpan(surface.data, y0 * surface.width + x0, 1); // Top left. Second quadrant.
			}
			if (right) {
				insideFlag = reflexAngle ? (xpys - ypxs < 0 || xpye - ypxe > 0) : (xpys - ypxs < 0 && xpye - ypxe > 0);
				if (insideFlag)
					drawSpan(x1, y0, 1); // Top right. First quadrant.
					//drawSpan(surface.data, y0 * surface.width + x1, 1); // Top right. First quadrant.
			}
		}
		if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0)) {
			if (left) {
				insideFlag = reflexAngle ? (-xpys + ypxs < 0 || -xpye + ypxe > 0)
						: (-xpys + ypxs < 0 && -xpye + ypxe > 0);
				if (insideFlag)
					drawSpan(x0, y1, 1); // Bottom left. Third quadrant.
					//drawSpan(surface.data, y1 * surface.width + x0, 1); // Bottom left. Third quadrant.
			}
			if (right) {
				insideFlag = reflexAngle ? (xpys + ypxs < 0 || xpye + ypxe > 0) : (xpys + ypxs < 0 && xpye + ypxe > 0);
				if (insideFlag)
					drawSpan(x1, y1, 1); // Bottom right. Fourth quadrant.
					//drawSpan(surface.data, y1 * surface.width + x1, 1); // Bottom right. Fourth quadrant.
			}
		}
	}

	// Check whether an angle is comprised between a start and an end angle. 
	private boolean checkAngle(int start, int end, int angle) {
		if (start < end)
			return angle > start && angle < end;
		else
			return angle > start || angle < end;
	}

	public void drawArc(int x, int y, int rx, int ry, int startAngle, int arcAngle) {
		if ((rx < 0) || (rx > MAX_ELLIPSE_RADIUS) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
			return;
		if (arcAngle == 0)
			return;

		int endAngle;
		startAngle %= 360;
		if (startAngle < 0)
			startAngle += 360;
		endAngle = (startAngle + arcAngle) % 360;
		if (endAngle < 0)
			endAngle += 360;
		if (arcAngle < 0) {
			int swap = startAngle;
			startAngle = endAngle;
			endAngle = swap;
			arcAngle = -arcAngle;
		}
		if (arcAngle >= 360) {
			drawEllipse(x, y, rx, ry);
			return;
		}

		boolean reflexAngle = arcAngle >= 180;
		double start = startAngle / 180.0 * Math.PI, end = endAngle / 180.0 * Math.PI;
		int xs = (int) (Math.cos(start) * MAX_ELLIPSE_RADIUS), ys = (int) (Math.sin(start) * MAX_ELLIPSE_RADIUS);
		int xe = (int) (Math.cos(end) * MAX_ELLIPSE_RADIUS), ye = (int) (Math.sin(end) * MAX_ELLIPSE_RADIUS);

		if (ry == 0) {
			int rx0, rx1;
			rx0 = checkAngle(startAngle, endAngle, 180) ? -rx : 0;
			rx1 = checkAngle(startAngle, endAngle, 0) ? rx : 0;
			//drawHLine(x + rx0, x + rx1, y);
			drawLine(x + rx0, y, x + rx1, y);
			return;
		}

		if (rx == 0) {
			int ry0, ry1;
			ry0 = checkAngle(startAngle, endAngle, 90) ? -ry : 0;
			ry1 = checkAngle(startAngle, endAngle, 270) ? ry : 0;
			//drawVLine(y + ry0, y + ry1, x);
			drawLine(x, y + ry0, x, y + ry1);
			return;
		}

		x += transX;
		y += transY;

		int xp, yp;
		long rx2, ry2;
		long d, u, v, s, t, du, dv;

		rx2 = (long) rx * (long) rx;
		ry2 = (long) ry * (long) ry;

		s = 0;
		t = rx2 * ry;
		d = ry2 - t;
		du = ry2 << 1;
		dv = -rx2 << 1;
		u = du + ry2;
		v = dv * (ry - 1);
		xp = 0;
		yp = ry;

		while (s < t) {
			drawArcPoints(x, y, xp, yp, xs, ys, xe, ye, reflexAngle);
			if (d >= 0) {
				d += v;
				v -= dv;
				yp--;
				t -= rx2;
			}
			d += u;
			u += du;
			xp++;
			s += ry2;
		}

		s = 0;
		t = ry2 * rx;
		d = rx2 - t;
		du = rx2 << 1;
		dv = -ry2 << 1;
		u = du + rx2;
		v = dv * (rx - 1);
		xp = rx;
		yp = 0;

		while (s <= t) {
			drawArcPoints(x, y, xp, yp, xs, ys, xe, ye, reflexAngle);
			if (d >= 0) {
				d += v;
				v -= dv;
				xp--;
				t -= ry2;
			}
			d += u;
			u += du;
			yp++;
			s += rx2;
		}
	}

	private void fillArcSpan(int x, int y, int xp, int yp, int xs, int ys, int xe, int ye) {
		Rectangle r = clipRectangle;
		int y0 = y - yp;
		if ((y0 >= r.ymin) && (y0 <= r.ymax)) {
			int xs0, xe0, xi0, xi1, x0, x1, w;
			boolean invertFlag;

			xs0 = ys != 0 ? ((xs * yp << 1) + ys) / (ys << 1) : (xs > 0 ? MAX_ELLIPSE_RADIUS : -MAX_ELLIPSE_RADIUS);
			xe0 = ye != 0 ? ((xe * yp << 1) + ye) / (ye << 1) : (xe > 0 ? MAX_ELLIPSE_RADIUS : -MAX_ELLIPSE_RADIUS);

			if (yp >= 0) {
				if (ys >= 0) {
					if (ye >= 0) {
						if (xs >= xe) {
							xi0 = xe0;
							xi1 = xs0;
							invertFlag = false;
						} else {
							xi0 = xs0;
							xi1 = xe0;
							invertFlag = true;
						}
					} else {
						xi0 = -MAX_ELLIPSE_RADIUS;
						xi1 = xs0;
						invertFlag = false;
					}
				} else {
					if (ye >= 0) {
						xi0 = xe0;
						xi1 = MAX_ELLIPSE_RADIUS;
						invertFlag = false;
					} else {
						if (xs >= xe) {
							xi0 = -MAX_ELLIPSE_RADIUS;
							xi1 = MAX_ELLIPSE_RADIUS;
							invertFlag = false;
						} else
							return;
					}
				}
			} else {
				if (ys < 0) {
					if (ye < 0) {
						if (xs <= xe) {
							xi0 = xs0;
							xi1 = xe0;
							invertFlag = false;
						} else {
							xi0 = xe0;
							xi1 = xs0;
							invertFlag = true;
						}
					} else {
						xi0 = xs0;
						xi1 = MAX_ELLIPSE_RADIUS;
						invertFlag = false;
					}
				} else {
					if (ye < 0) {
						xi0 = -MAX_ELLIPSE_RADIUS;
						xi1 = xe0;
						invertFlag = false;
					} else {
						if (xs <= xe) {
							xi0 = -MAX_ELLIPSE_RADIUS;
							xi1 = MAX_ELLIPSE_RADIUS;
							invertFlag = false;
						} else
							return;
					}
				}
			}

			if (!invertFlag) {
				x0 = -xp;
				x1 = xp;
				if (x0 < xi0)
					x0 = xi0;
				if (x1 > xi1)
					x1 = xi1;
				x0 += x;
				x1 += x;
				if (x0 < r.xmin)
					x0 = r.xmin;
				if (x1 > r.xmax)
					x1 = r.xmax;
				w = x1 - x0 + 1;
				if (w > 0)
					drawSpan(x0, y0, w);
					//drawSpan(surface.data, y0 * surface.width + x0, w);
			} else {
				x0 = -xp;
				x1 = xp;
				if (x1 > xi0)
					x1 = xi0;
				x0 += x;
				x1 += x;
				if (x0 < r.xmin)
					x0 = r.xmin;
				if (x1 > r.xmax)
					x1 = r.xmax;
				w = x1 - x0 + 1;
				if (w > 0)
					drawSpan(x0, y0, w);
					//drawSpan(surface.data, y0 * surface.width + x0, w);

				x0 = -xp;
				x1 = xp;
				if (x0 < xi1)
					x0 = xi1;
				x0 += x;
				x1 += x;
				if (x0 < r.xmin)
					x0 = r.xmin;
				if (x1 > r.xmax)
					x1 = r.xmax;
				w = x1 - x0 + 1;
				if (w > 0)
					drawSpan(x0, y0, w);
					//drawSpan(surface.data, y0 * surface.width + x0, w);
			}
		}
	}

	public void fillArc(int x, int y, int rx, int ry, int startAngle, int arcAngle) {

		if ((rx < 0) || (rx > MAX_ELLIPSE_RADIUS) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
			return;
		if (arcAngle == 0)
			return;

		int endAngle;
		startAngle %= 360;
		if (startAngle < 0)
			startAngle += 360;
		endAngle = (startAngle + arcAngle) % 360;
		if (endAngle < 0)
			endAngle += 360;
		if (arcAngle < 0) {
			int swap = startAngle;
			startAngle = endAngle;
			endAngle = swap;
			arcAngle = -arcAngle;
		}
		if (arcAngle >= 360) {
			fillEllipse(x, y, rx, ry);
			return;
		}

		double start = startAngle / 180.0 * Math.PI, end = endAngle / 180.0 * Math.PI;
		int xs = (int) (Math.cos(start) * MAX_ELLIPSE_RADIUS), ys = (int) (Math.sin(start) * MAX_ELLIPSE_RADIUS);
		int xe = (int) (Math.cos(end) * MAX_ELLIPSE_RADIUS), ye = (int) (Math.sin(end) * MAX_ELLIPSE_RADIUS);

		x += transX;
		y += transY;

		int xp, yp;
		long rx2 = (long) rx * (long) rx, ry2 = (long) ry * (long) ry;
		long rxy2, t0, t1, d;
		if (rx > ry) {
			xp = rx;
			yp = 0;
			rxy2 = (long) rx * ry2;
		} else {
			xp = 0;
			yp = ry;
			rxy2 = (long) ry * rx2;
		}
		t0 = rxy2 << 3;
		t1 = 0;
		d = rx2 + ry2 - (rxy2 << 2);
		rx2 <<= 3;
		ry2 <<= 3;

		boolean drawFlag = rx > ry;
		for (int i = rx + ry; i >= 0; i--) {
			if (drawFlag) {
				fillArcSpan(x, y, xp, yp, xs, ys, xe, ye);
				if (yp != 0)
					fillArcSpan(x, y, xp, -yp, xs, ys, xe, ye);
			}

			// Update.
			if (rx > ry) {
				if (d < 0) {
					yp++;
					t1 += rx2;
					d += t1;
					drawFlag = true;
				} else {
					xp--;
					t0 -= ry2;
					d -= t0;
					drawFlag = false;
				}
			} else {
				if (d < 0) {
					xp++;
					t1 += ry2;
					d += t1;
					drawFlag = false;
				} else {
					yp--;
					t0 -= rx2;
					d -= t0;
					drawFlag = true;
				}
			}
		}
	}

	public void drawRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {

		x += transX;
		y += transY;

		//Vertical lines
		drawLine(x, y + arcHeight, x, y + h - arcHeight);
		drawLine(x + w + 1, y + arcHeight, x + w + 1, y + h - arcHeight);

		// Horizontal lines
		drawLine(x + arcWidth, y, x + w - arcWidth, y);
		drawLine(x + arcWidth, y + h + 1, x + w - arcWidth, y + h + 1);

		// Rounded corners
		drawArc(x + arcWidth, y + arcHeight, arcWidth, arcHeight, 90, 90); // top left
		drawArc(x + w - arcWidth, y + arcHeight, arcWidth, arcHeight, 0, 90); // top right
		drawArc(x + arcWidth, y + h - arcHeight, arcWidth, arcHeight, 180, 90); // bottom left
		drawArc(x + w - arcWidth, y + h - arcHeight, arcWidth, arcHeight, 270, 90); // bottom right
	}
	
	public void fillRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {

		x += transX;
		y += transY;
		
		fillRect(x, y + arcHeight + 1, w + 1,  h - 2 * arcHeight); // center
		fillRect(x + arcWidth + 1, y, w - 2 * arcWidth, arcHeight + 1); // top middle
		fillRect(x + arcWidth + 1, y + h - arcHeight, w - 2 * arcWidth, arcHeight); // bottom middle

		// Rounded corners
		fillArc(x + arcWidth, y + arcHeight, arcWidth, arcHeight, 90, 90); // top left
		fillArc(x + w - arcWidth - 1, y + arcHeight, arcWidth, arcHeight, 0, 90); // top right
		fillArc(x + arcWidth, y + h - arcHeight - 1, arcWidth, arcHeight, 180, 90); // bottom left
		fillArc(x + w - arcWidth - 1, y + h - arcHeight - 1, arcWidth, arcHeight, 270, 90); // bottom right
	}

}
