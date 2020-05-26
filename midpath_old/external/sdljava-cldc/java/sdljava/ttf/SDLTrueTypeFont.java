package sdljava.ttf;

/**
 *  sdljava - a java binding to the SDL API
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
import sdljava.video.SDLColor;
import sdljava.video.SDLSurface;
import sdljava.x.swig.SDL_Surface;
import sdljava.x.swig.SWIGTYPE_p__TTF_Font;
import sdljava.x.swig.SWIG_SDLTTF;
import sdljava.x.swig.SWIG_SDLTTFConstants;

/**
 * An instance of TTF_Font.  All functions which operate on a TTF_Font structure from
 * SDL_ttf may be found in this class.
 * <P>
 * <I>Note:  glyphMetrics() method currently is not working.</I>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLTrueTypeFont.java,v 1.7 2005/09/03 23:09:52 ivan_ganza Exp $
 * @see SDLTTF
 */
public class SDLTrueTypeFont {

	public static final int TTF_STYLE_NORMAL = SWIG_SDLTTFConstants.TTF_STYLE_NORMAL;
	public static final int TTF_STYLE_BOLD = SWIG_SDLTTFConstants.TTF_STYLE_BOLD;
	public static final int TTF_STYLE_ITALIC = SWIG_SDLTTFConstants.TTF_STYLE_ITALIC;
	public static final int TTF_STYLE_UNDERLINE = SWIG_SDLTTFConstants.TTF_STYLE_UNDERLINE;

	SWIGTYPE_p__TTF_Font swigTTFFont;
	int ptsize;

	/**
	 * Creates a new <code>SDLTrueTypeFont</code> instance.
	 *
	 * @param swigTTFFont a <code>SWIGTYPE_p__TTF_Font</code> value
	 */
	protected SDLTrueTypeFont(SWIGTYPE_p__TTF_Font swigTTFFont, int ptsize) {
		this.swigTTFFont = swigTTFFont;
		this.ptsize = ptsize;
	}

	/**
	 * Gets the value of swigTTFFont
	 *
	 * @return the value of swigTTFFont
	 */
	public SWIGTYPE_p__TTF_Font getSwigTTFFont() {
		return this.swigTTFFont;
	}

	/**
	 * Sets the value of swigTTFFont
	 *
	 * @param argSwigTTFFont Value to assign to this.swigTTFFont
	 */
	public void setSwigTTFFont(SWIGTYPE_p__TTF_Font argSwigTTFFont) {
		this.swigTTFFont = argSwigTTFFont;
	}

	/**
	 * Get the pt size
	 *
	 * @return the pt size
	 */
	public int getPTSize() {
		return this.ptsize;
	}

	/**
	 *  Create an 8-bit palettized surface and render the given text
	 *  at fast quality with the given font and color.  The 0 pixel is
	 *  the colorkey, giving a transparent background, and the 1 pixel
	 *  is set to the text color.  
	 *
	 * @param text The text to render
	 * @param fg    The foreground color
	 * @return a <code>SDLSurface</code> with the rendered text
	 * @exception SDLException if an error occurs
	 */
	public SDLSurface renderTextSolid(String text, SDLColor fg) throws SDLException {
		SDL_Surface surface = SWIG_SDLTTF.TTF_RenderText_Solid_FAST(swigTTFFont, text, (short) fg.getRed(), (short) fg
				.getGreen(), (short) fg.getBlue());
		return new SDLSurface(surface);
	}

	/**
	 *  Create an 8-bit palettized surface and render the given text
	 *  at fast quality with the given font and color.  The 0 pixel is
	 *  the colorkey, giving a transparent background, and the 1 pixel
	 *  is set to the text color.  
	 *
	 * @param text a <code>String</code> value
	 * @param fg a <code>SDLColor</code> value
	 * @param bg a <code>SDLColor</code> value
	 * @return a <code>SDLSurface</code> value
	 * @exception SDLException if an error occurs
	 */
	public SDLSurface renderTextShaded(String text, SDLColor fg, SDLColor bg) throws SDLException {
		SDL_Surface surface = SWIG_SDLTTF.TTF_RenderText_Shaded_FAST(swigTTFFont, text, (short) fg.getRed(), (short) fg
				.getGreen(), (short) fg.getBlue(), (short) bg.getRed(), (short) bg.getGreen(), (short) bg.getBlue());
		return new SDLSurface(surface);
	}

	/**
	 *  Create a 32-bit ARGB surface and render the given glyph at
	 *  high quality, using alpha blending to dither the font with the
	 *  given color.  The glyph is rendered without any padding or
	 *  centering in the X direction, and aligned normally in the Y
	 *  direction.  
	 *
	 * @param text The text to render
	 * @param fg   The foreground color
	 * @return a <code>SDLSurface</code> with the rendered text
	 * @exception SDLException if an error occurs
	 */
	public SDLSurface renderTextBlended(String text, SDLColor fg) throws SDLException {
		SDL_Surface surface = SWIG_SDLTTF.TTF_RenderText_Blended_FAST(swigTTFFont, text, (short) fg.getRed(),
				(short) fg.getGreen(), (short) fg.getBlue());
		return new SDLSurface(surface);
	}

	/**
	 * Set the rendering style of the loaded font.
	 * <P>
	 * NOTE: This will flush the internal cache of previously rendered
	 * glyphs, even if there is no change in style, so it may be best
	 * to check the current style using TTF_GetFontStyle first.
	 * <P>
	 * NOTE: I've seen that combining TTF_STYLE_UNDERLINE with anything can
	 * cause a segfault, other combinations may also do this. Some brave soul
	 * may find the cause of this and fix it...
	 * <P>
	 * NOTE: Rendered text formatted with TTF_STYLE_BOLD is hollow on the
	 * inside (wireframe like) for some reason when you use
	 * TTF_?RenderSolid. It displayed ok if you use TTF_?RenderBlended.
	 *
	 * @param style  A bitmask of the desired style composed from the TTF_STYLE_* defined values.
	 * @exception SDLException if an error occurs
	 */
	public void setFontStyle(int style) throws SDLException {
		SWIG_SDLTTF.TTF_SetFontStyle(swigTTFFont, style);
	}

	/**
	 * Get the rendering style of the loaded font.
	 *
	 * @return The style as a bitmask composed of the TTF_* defines
	 * @exception SDLException if an error occurs
	 */
	public int getFontStyle() {
		return SWIG_SDLTTF.TTF_GetFontStyle(swigTTFFont);
	}

	/**
	 * Get the maximum pixel height of all glyphs of the loaded
	 * font. You may use this height for rendering text as close
	 * together vertically as possible, though adding at least one
	 * pixel height to it will space it so they can't touch. Remember
	 * that SDL_ttf doesn't handle multiline printing, so you are
	 * responsible for line spacing, see the TTF_FontLineSkip as well.
	 *
	 * @return The maximum pixel height of all glyphs in the font.
	 */
	public int fontHeight() {
		return SWIG_SDLTTF.TTF_FontHeight(swigTTFFont);
	}

	/**
	 * Get the maximum pixel ascent of all glyphs of the loaded
	 * font. This can also be interpreted as the distance from the top
	 * of the font to the baseline. It could be used when drawing an
	 * individual glyph relative to a top point, by combining it with
	 * the glyph's maxy metric to resolve the top of the rectangle
	 * used when blitting the glyph on the screen.
	 * <P>
	 * rect.y = top + TTF_FontAscent(font) - glyph_metric.maxy;
	 *
	 * @return The maximum pixel ascent of all glyphs in the font.
	 */
	public int fontAscent() {
		return SWIG_SDLTTF.TTF_FontAscent(swigTTFFont);
	}

	/**
	 * Get the maximum pixel descent of all glyphs of the loaded
	 * font. This can also be interpreted as the distance from the
	 * baseline to the bottom of the font. It could be used when
	 * drawing an individual glyph relative to a bottom point, by
	 * combining it with the glyph's maxy metric to resolve the top of
	 * the rectangle used when blitting the glyph on the screen.
	 * <P>
	 * rect.y = bottom - TTF_FontDescent(font) - glyph_metric.maxy; 
	 *
	 * @return The maximum pixel height of all glyphs in the font.
	 */
	public int fontDescent() {
		return SWIG_SDLTTF.TTF_FontDescent(swigTTFFont);
	}

	/**
	 * et the reccomended pixel height of a rendered line of text of
	 * the loaded font. This is usually larger than the TTF_FontHeight
	 * of the font.
	 *
	 * @return The maximum pixel height of all glyphs in the font.
	 */
	public int fontLineSkip() {
		return SWIG_SDLTTF.TTF_FontLineSkip(swigTTFFont);
	}

	/**
	 * Gets the glyph metrics from the font file.
	 *
	 * @param c a <code>char</code> value
	 * @return  the glyph metrics from the font file as a <code>GlyphMetrics</code> value
	 * @exception SDLException if an error occurs
	 */
	public GlyphMetrics glyphMetrics(char c) throws SDLException {
		int minx[] = { 0 };
		int maxx[] = { 0 };
		int miny[] = { 0 };
		int maxy[] = { 0 };
		int advance[] = { 0 };

		int result = SWIG_SDLTTF.TTF_GlyphMetrics(swigTTFFont, (int) c, minx, maxx, miny, maxy, advance);
		if (result == 0) {
			throw new SDLException("Could not find character: " + c);
		}
		return new GlyphMetrics(minx[0], maxx[0], miny[0], maxy[0], advance[0]);
	}

	/**
	 * Compute the size (width and height) required to render the given String
	 *
	 * @param text a <code>String</code> value
	 * @return a <code>TextSize</code> value
	 */
	public TextSize sizeText(String text) {
		return sizeText(text, null);
	}

	/**
	 * Compute the size (width and height) required to render the given String.
	 * Reuse the given TextSize object.
	 *
	 * @param text a <code>String</code> value
	 * @param size a <code>TextSize</code> value
	 * @return a <code>TextSize</code> value
	 */
	public TextSize sizeText(String text, TextSize size) {
		if (size == null)
			size = new TextSize();

		int width[] = { 0 };
		int height[] = { 0 };

		int result = SWIG_SDLTTF.TTF_SizeText(swigTTFFont, text, width, height);

		size.setWidth(width[0]);
		size.setHeight(height[0]);

		return size;
	}

	/**
	 * Free the memory used by font, and free font itself as well. Do
	 * not use font after this without loading a new font to it.
	 *
	 * @param font The font to free
	 * @exception SDLException if an error occurs
	 */
	public void closeFont() throws SDLException {
		SWIG_SDLTTF.TTF_CloseFont(swigTTFFont);
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLTrueTypeFont[").append("ptsize=").append(getPTSize())
				.append(", style=").append(getFontStyle()).append(", height=").append(fontHeight()).append(", ascent=")
				.append(fontAscent()).append(", descent=").append(fontDescent()).append(", lineSkip=").append(
						fontLineSkip()).append("]");

		return buf.toString();
	}

	protected void finalize() {
		try {
			closeFont();
		} catch (SDLException e) {
			e.printStackTrace();
		} // try-catch
	}
}