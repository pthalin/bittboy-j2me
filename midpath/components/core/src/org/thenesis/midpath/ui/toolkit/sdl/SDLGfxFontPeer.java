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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.FontPeer;
import javax.microedition.lcdui.Graphics;

import sdljavax.gfx.SDLGfx;

import com.sun.midp.log.Logging;

public class SDLGfxFontPeer implements FontPeer {

	// SDLGfx font: 8x8 pixels
	private int FONT_SIZE = 8;
	private int inset = 0;
	private int size;

	SDLGfxFontPeer(int face, int style, int size) {

		this.size = size;
		
		switch (size) {
		case Font.SIZE_LARGE:
			inset = 12;
		case Font.SIZE_MEDIUM:
			inset = 8;
		case Font.SIZE_SMALL:
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
		return Font.FACE_MONOSPACE;
	}

	public int getHeight() {
		return FONT_SIZE + inset;
	}

	public int getSize() {
		return size;
	}

	public int getStyle() {
		return Font.STYLE_PLAIN;
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

	public void render(Graphics g, String str, int x, int y, int anchor) {
		x += g.getTranslateX();
		y += g.getTranslateY();

		if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			y -= getHeight() - 1;
		}

		if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
			x -= stringWidth(str) - 1;
		} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
			x -= stringWidth(str) / 2 - 1;
		}
		
		y += inset / 2;

		SDLGraphics sdlg = ((SDLGraphics) g);
		SDLGfx.stringColor(sdlg.getSurface(), x, y, str, sdlg.sdlGfxColor); //0xffff00ffL

		if (Logging.TRACE_ENABLED)
			System.out.println("SDLGraphics.drawString(): " + str + " x=" + x + " y=" + y + " color="
				+ Integer.toHexString((int)sdlg.sdlColor));
		
	}

}
