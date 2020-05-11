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
import org.thenesis.microbackend.ui.graphics.VirtualFont;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;

import sdljava.video.SDLSurface;
import sdljavax.gfx.SDLGfx;


public class SDLGfxFont implements VirtualFont {

	// SDLGfx font: 8x8 pixels
	private int FONT_SIZE = 8;
	private int inset = 0;
	private int size;

	SDLGfxFont(int face, int style, int size) {

		this.size = size;
		
		switch (size) {
		case VirtualFont.SIZE_LARGE:
			inset = 12;
		case VirtualFont.SIZE_MEDIUM:
			inset = 8;
		case VirtualFont.SIZE_SMALL:
			inset = 6;
		}
	}

	public int charWidth(char ch) {
		return FONT_SIZE;
	}

	public int charsWidth(char[] ch, int offset, int length) {
		return length * FONT_SIZE;
	}

	public int getBaselinePosition() {
		return FONT_SIZE + inset / 2;
	}

	public int getFace() {
		return VirtualFont.FACE_MONOSPACE;
	}

	public int getHeight() {
		return FONT_SIZE + inset;
	}

	public int getSize() {
		return size;
	}

	public int getStyle() {
		return VirtualFont.STYLE_PLAIN;
	}

	public boolean isBold() {
		return false;
	}

	public boolean isItalic() {
		return false;
	}

	public boolean isPlain() {
		return true;
	}

	public boolean isUnderlined() {
		return false;
	}

	public int stringWidth(String str) {
		return str.length() * 8;
	}

	public int substringWidth(String str, int offset, int len) {
		return len * FONT_SIZE;
	}

	public void render(VirtualGraphics g, String str, int x, int y, int anchor) {
		x += g.getTranslateX();
		y += g.getTranslateY();

		if ((anchor & VirtualGraphics.BOTTOM) == VirtualGraphics.BOTTOM) {
			y -= getHeight() - 1;
		}

		if ((anchor & VirtualGraphics.RIGHT) == VirtualGraphics.RIGHT) {
			x -= stringWidth(str) - 1;
		} else if ((anchor & VirtualGraphics.HCENTER) == VirtualGraphics.HCENTER) {
			x -= stringWidth(str) / 2 - 1;
		}
		
		y += inset / 2;

		SDLGraphics sdlGraphics = ((SDLGraphics) g);
		SDLSurface sdlSurface = ((SDLImage)sdlGraphics.getImage()).sdlSurface;
		SDLGfx.stringColor(sdlSurface, x, y, str, sdlGraphics.sdlGfxColor); //0xffff00ffL

		if (Logging.TRACE_ENABLED)
			System.out.println("SDLGraphics.drawString(): " + str + " x=" + x + " y=" + y + " color="
				+ Integer.toHexString((int)sdlGraphics.sdlColor));
		
	}

}
