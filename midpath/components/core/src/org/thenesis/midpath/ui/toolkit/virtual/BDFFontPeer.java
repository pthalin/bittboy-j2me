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
package org.thenesis.midpath.ui.toolkit.virtual;

import java.io.InputStreamReader;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.FontPeer;
import javax.microedition.lcdui.Graphics;

import org.thenesis.midpath.font.bdf.BDFFontContainer;
import org.thenesis.midpath.font.bdf.BDFGlyph;
import org.thenesis.midpath.font.bdf.BDFMetrics;
import org.thenesis.midpath.font.bdf.BDFParser;

import com.sun.midp.log.Logging;
import com.sun.midp.main.Configuration;

/**
 * A BDF font renderer which supports 1bpp and 8bpp fonts.
 */
public class BDFFontPeer implements FontPeer {

	private static BDFFontContainer container;

	private int inset = 0;
	private int size;

	static {
		
		String fontFilename = Configuration.getPropertyDefault("org.thenesis.midpath.font.bdf.filename", "VeraMono-12-8.bdf");
		
		InputStreamReader reader = new InputStreamReader(BDFFontContainer.class
				.getResourceAsStream(fontFilename));
		
		try {
			container = new BDFParser(reader).createFont();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* 
	 * References:
	 * http://partners.adobe.com/asn/developer/PDFS/TN/5005.BDF_Spec.pdf
	 * http://jnode.svn.sourceforge.net/viewvc/jnode/trunk/gui/fonts/ 
	 * http://fontforge.sourceforge.net/BDFgrey.html
	 * http://cvsweb.xfree86.org/cvsweb/xc/fonts/bdf/#dirlist
	 * */

	BDFFontPeer(int face, int style, int size) {

		this.size = size;

		//		switch (size) {
		//		case Font.SIZE_LARGE:
		//			//inset = 4;
		//			font = FONT_8X16;
		//		case Font.SIZE_MEDIUM:
		//			//inset = 0;
		//			font = FONT_8X16;
		//		case Font.SIZE_SMALL:
		//			//inset = 0;
		//			font = FONT_8X16;
		//		}
		
	}

	public int charWidth(char ch) {
		return charsWidth(new char[] { ch }, 0, 1);
		//return FONTS_WIDTH[font];
	}

	public int charsWidth(char[] ch, int offset, int length) {

		int w = 0;
		for (int i = 0; i < length; i++) {
			w += container.getFontMetrics().charWidth(ch[offset + i]);
		}

		if (Logging.TRACE_ENABLED)
			System.out.println("BDFFontPeer.charsWidth(): " + new String(ch, offset, length) + " : " + w);

		return w;
		//return (container.getFontMetrics().charsWidth(ch, offset, offset + length));
		//return length * FONTS_WIDTH[font];
	}

	public int getBaselinePosition() {
		return container.getFontMetrics().getAscent();
		//return 16;
		//return FONTS_HEIGHT[font] + inset / 2;
	}

	public int getFace() {
		return Font.FACE_MONOSPACE;
	}

	public int getHeight() {
		return container.getFontMetrics().getHeight();
		//return FONTS_HEIGHT[font] + inset;
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
		return charsWidth(str.toCharArray(), 0, str.length());
		//return str.length() * FONTS_WIDTH[font];
	}

	public int substringWidth(String str, int offset, int len) {
		return charsWidth(str.toCharArray(), offset, len);
		//return len * FONTS_WIDTH[font];
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

		VirtualGraphics vg = ((VirtualGraphics) g);
		VirtualSurface surface = vg.getSurface();

		if (Logging.TRACE_ENABLED)
			System.out.println("SDLGraphics.drawString(): " + str + " x=" + x + " y=" + y + " color="
					+ Integer.toHexString(g.getColor()));

		int color = vg.getInternalColor();
		//int pw = surface.getWidth();
		Rectangle r = vg.clipRectangle;

		char[] chars = str.toCharArray();
		int charsCount = chars.length;

		if ((container != null) && (charsCount > 0)) {

			int offset = 0;
			BDFMetrics fm = (BDFMetrics) container.getFontMetrics();

			for (int i = 0; i < charsCount; i++) {

				BDFGlyph glyph = container.getGlyph(chars[i]);
				if (glyph == null) {
					continue;
				}

				int base = fm.getDescent();
				int charWidth = fm.charWidth(chars[i]);

				int fHeight = glyph.getBbx().height;
				int[] fData = glyph.getData();
				int scan = fData.length / fHeight;

				// FIXME Improve clipping
				int currentX = x + offset;
				if (currentX > r.xmax || (currentX + scan) > r.xmax || y < r.ymin || y > r.ymax
						|| (y + fHeight) > r.ymax)
					break;

				if (container.getDepth() == 8) {
					for (int k = 0; k < fHeight; k++) {
						for (int j = 0; j < scan; j++) {
							int fPixel = fData[(k * scan) + j];
							if (fPixel != 0) {

								int destPosition = (y + (container.getBoundingBox().height + base - fHeight) + k - glyph
										.getBbx().y)
										* surface.getWidth() + (x + offset + j);

								// Source.
								int sr = (color & 0x00FF0000) >> 16;
								int sg = (color & 0x0000FF00) >> 8;
								int sb = color & 0x000000FF;

								// Destination.
								int dr = (surface.data[destPosition] & 0x00FF0000) >> 16;
								int dg = (surface.data[destPosition] & 0x0000FF00) >> 8;
								int db = surface.data[destPosition] & 0x000000FF;

								// Alpha blending
								int a = fPixel;
								int factor = 0x00010000 / 255;
								dr = ((a * sr + (0xff - a) * dr) * factor) >> 16;
								dg = ((a * sg + (0xff - a) * dg) * factor) >> 16;
								db = ((a * sb + (0xff - a) * db) * factor) >> 16;

								surface.data[destPosition] = (((dr << 16) + (dg << 8) + db) | 0xFF000000);
							}
						}
					}
				} else {

					for (int k = 0; k < fHeight; k++) {
						for (int j = 0; j < scan; j++) {
							int fPixel = fData[(k * scan) + j];
							if (fPixel != 0) {

								int destPosition = (y + (container.getBoundingBox().height + base - fHeight) + k - glyph
										.getBbx().y)
										* surface.getWidth() + (x + offset + j);

								int red = (color & 0x00FF0000) >> 16;
								int green = (color & 0x0000FF00) >> 8;
								int blue = (color & 0x000000FF);

								red = ((red * fPixel) >> container.getDepth()) & 0xFF;
								green = ((green * fPixel) >> container.getDepth()) & 0xFF;
								blue = ((blue * fPixel) >> container.getDepth()) & 0xFF;

								surface.data[destPosition] = (((red << 16) + (green << 8) + blue) | 0xFF000000);
							}
						}
					}
				}

				offset += charWidth;

			}
		}

	}

}
