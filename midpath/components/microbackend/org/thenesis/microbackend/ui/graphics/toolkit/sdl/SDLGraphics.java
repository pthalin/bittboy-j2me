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
package org.thenesis.microbackend.ui.graphics.toolkit.sdl;

import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.graphics.BaseGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualFont;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;
import org.thenesis.microbackend.ui.sdl.SDLBackend;

import sdljava.SDLException;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljavax.gfx.SDLGfx;


public class SDLGraphics extends BaseGraphics {

	public static final int MAX_CIRCLE_RADIUS = 16384;
	public static final int MAX_ELLIPSE_RADIUS = 16384;
	public static final int REPLACE = 0, BLEND = 1, LOGIC = 2;
	
	private SDLSurface surface;
	private int blendMode = REPLACE;

	long sdlGfxColor;
	long sdlColor;
	SDLRect rect = new SDLRect();
	

	SDLGraphics(VirtualImage image) {
		super(image);
	}
	
	SDLImage getSDLImage() {
	    return (SDLImage)getImage();
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

	public synchronized void setClip(int x, int y, int width, int height) {
		super.setClip(x, y, width, height);
		updateSDLClipping();
	}

	public synchronized void clipRect(int x, int y, int width, int height) {
		super.clipRect(x, y, width, height);
		updateSDLClipping();
	}
	
	private void updateSDLClipping() {
	    rect.setLocation(getClipX(), getClipX());
	    rect.setSize(getClipWidth(), getClipHeight());
	    try {
            surface.setClipRect(rect);
        } catch (SDLException e) {
            e.printStackTrace();
        }
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
	    SDLGfxFont font = (SDLGfxFont)getFont();
		if (font != null) {
			font.render(this, str, x, y, anchor);
		}
	}

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
		SDLImage image = new SDLImage(buf, width, height, processAlpha);
		
		// Draw the created image
		drawImage(image, x, y, VirtualGraphics.TOP | VirtualGraphics.LEFT);

		if (Logging.TRACE_ENABLED)
			System.out.println("SDLGraphics.drawRGB()");
	}

        public void drawImage(VirtualImage img, int x, int y, int anchor) {
                super.drawImage(img, x, y, anchor);
        }

	public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {

		x_src += transX;
		y_src += transY;
		x_dest += transX;
		y_dest += transY;

		//SDLImage transformedImage = new SDLImage(surface, x_src,y_src, width, height, Sprite.TRANS_NONE);

		if ((anchor & VirtualGraphics.BOTTOM) == VirtualGraphics.BOTTOM) {
			y_dest -= height - 1;
		} else if ((anchor & VirtualGraphics.VCENTER) == VirtualGraphics.VCENTER) {
			y_dest -= height / 2 - 1;
		}

		if ((anchor & VirtualGraphics.RIGHT) == VirtualGraphics.RIGHT) {
			x_dest -= width - 1;
		} else if ((anchor & VirtualGraphics.HCENTER) == VirtualGraphics.HCENTER) {
			x_dest -= width / 2 - 1;
		}

		try {
			// Copy the source area in an offscreen surface
			SDLSurface dstSurface = SDLBackend.createRGBSurface(width, height);
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
	public void drawSpan(int x, int y, int w) {

		//if (Logging.TRACE_ENABLED)
		//	System.out.println("[DEBUG]VirtualGraphics.drawSpan : surface data size= " + surface.data.length + " dstPosition=" + dstPosition + " w=" + w);

		switch (blendMode) {
		case REPLACE:
			drawLine(x, y, x + w, y);
			break;
		}

	}

    public void resetFont() {
        currentFont = new SDLGfxFont(VirtualFont.FACE_MONOSPACE, VirtualFont.STYLE_PLAIN, VirtualFont.SIZE_LARGE);
    }

}
