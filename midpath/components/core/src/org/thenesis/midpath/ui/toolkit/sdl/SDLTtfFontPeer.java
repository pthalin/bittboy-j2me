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

import com.sun.midp.log.Logging;

import sdljava.SDLException;
import sdljava.ttf.SDLTTF;
import sdljava.ttf.SDLTrueTypeFont;
import sdljava.video.SDLColor;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

public class SDLTtfFontPeer implements FontPeer {

	private int face;
	private int style;
	private int size;
	private SDLTrueTypeFont ttfFont;
	private int ascent;
	private int descent;
	private int maxAdvance;
	private int leading;

	/**
	 * The widths of the characters.
	 */
	private int[] charWidths;

	static {
		try {
			SDLTTF.init();
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construct a new Font object
	 *
	 * @param face The face to use to construct the Font
	 * @param style The style to use to construct the Font
	 * @param size The point size to use to construct the Font
	 */
	SDLTtfFontPeer(int face, int style, int size) {
		this.face = face;
		this.style = style;
		this.size = size;

		try {

			int ttfSize = 0;
			switch (size) {
			case Font.SIZE_LARGE:
				ttfSize = 24;
			case Font.SIZE_MEDIUM:
				ttfSize = 16;
			case Font.SIZE_SMALL:
				ttfSize = 12;
			}

			// FIXME Do not hardcode font file path
			switch (face) {
			case Font.FACE_MONOSPACE:
			case Font.FACE_PROPORTIONAL:
			case Font.FACE_SYSTEM:
				ttfFont = SDLTTF.openFont("resources/VeraMono.ttf", ttfSize);
			}

			switch (style) {
			case Font.STYLE_BOLD:
				ttfFont.setFontStyle(SDLTrueTypeFont.TTF_STYLE_BOLD);
				break;
			case Font.STYLE_ITALIC:
				ttfFont.setFontStyle(SDLTrueTypeFont.TTF_STYLE_ITALIC);
				break;
			case Font.STYLE_PLAIN:
				ttfFont.setFontStyle(SDLTrueTypeFont.TTF_STYLE_NORMAL);
				break;
			case Font.STYLE_UNDERLINED:
				ttfFont.setFontStyle(SDLTrueTypeFont.TTF_STYLE_UNDERLINE);
				break;
			}

			ascent = ttfFont.fontAscent();
			descent = ttfFont.fontDescent();
			leading = 0; // TODO: Not provided by SDL. Possible not needed.

			//readCharWidths();

		} catch (SDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	/**
//	 * Reads the character widths.
//	 */
//	private void readCharWidths() {
//
//		// FIXME Calculate the real char width
//		charWidths = new int[255];
//
//		for (int i = 0; i < charWidths.length; i++) {
//			try {
//				charWidths[i] = ttfFont.glyphMetrics('a').getAdvance();
//			} catch (SDLException e) {
//				charWidths[i] = maxAdvance;
//			}
//
//			if (charWidths[i] > maxAdvance) {
//				maxAdvance = charWidths[i];
//			}
//		}
//
//	}

	public int charWidth(char ch) {
		return getStringWidth(String.valueOf(ch));
	}

	public int charsWidth(char[] ch, int offset, int length) {


		return getStringWidth(new String(ch, offset, length));
		
//		int maxOffset = offset + length;
//		int stringLength = 0;
//		for (int i = offset; i < maxOffset; i++) {
//			stringLength += charWidths[ch[i]];
//		}
//		return stringLength;
	}

	public int getBaselinePosition() {
		return ascent;
	}

	public int getFace() {
		return face;
	}

	public int getHeight() {
		return ttfFont.fontLineSkip();
	}

	public int getSize() {
		return size;
	}

	public int getStyle() {
		return style;
	}

	public boolean isBold() {
		return (style == Font.STYLE_BOLD);
	}

	public boolean isItalic() {
		return (style == Font.STYLE_ITALIC);
	}

	public boolean isPlain() {
		return (style == Font.STYLE_PLAIN);
	}

	public boolean isUnderlined() {
		return (style == Font.STYLE_UNDERLINED);
	}

	public int stringWidth(String str) {
		return getStringWidth(str);
		//return charsWidth(str.toCharArray(), 0, str.length());
	}

	public int substringWidth(String str, int offset, int len) {
		return getStringWidth(str.substring(offset, offset + len));
		//return charsWidth(str.toCharArray(), offset, len);
	}

	public void render(Graphics g, String str, int x, int y, int anchor) {
		x += g.getTranslateX();
		y += g.getTranslateY();

		if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			System.out.println("SDLGraphics.drawString(): BOTTOM");
			y -= getHeight() - 1;
		}

		if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
			System.out.println("SDLGraphics.drawString(): RIGHT");
			x -= stringWidth(str) - 1;
		} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
			x -= stringWidth(str) / 2 - 1;
		}

		// Render font now
		SDLGraphics sdlg = ((SDLGraphics)g);
		
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] SDLTtfFontPeer.render(): " + str + " x=" + x + " y=" + y + " color="
				+ Integer.toHexString((int)sdlg.sdlColor));
		try {
			SDLColor color = SDLVideo.getRGBA((int)sdlg.sdlColor, sdlg.getSurface().getFormat());
			
			// FIXME Doesn't work with renderTextSolid... Why ?
			SDLSurface ttfSurface = ttfFont.renderTextBlended(str, color);
		
			ttfSurface.blitSurface(sdlg.getSurface(), new SDLRect(x, y, 0, 0));
		} catch (SDLException e) {
			e.printStackTrace();
		}

	}
	
	private int getStringWidth(String s) {
		return ttfFont.sizeText(s).getWidth();
		//return 8 * s.length();
	}

}
