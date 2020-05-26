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
import java.io.IOException;
import java.io.InputStream;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.SDLVersion;
import sdljava.util.BufferUtil;
import sdljava.x.swig.SDL_version;
import sdljava.x.swig.SWIGTYPE_p__TTF_Font;
import sdljava.x.swig.SWIG_SDLTTF;

/**
 * SDL_ttf is a TrueType font rendering library that is used with the
 * SDL library, and almost as portable. It depends on freetype2 to
 * handle the TrueType font data. It allows a programmer to use
 * multiple TrueType fonts without having to code a font rendering
 * routine themselves. With the power of outline fonts and
 * antialiasing, high quality text output can be obtained without much
 * effort.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLTTF.java,v 1.11 2005/02/19 02:26:02 ivan_ganza Exp $ 
 */
public class SDLTTF {

	/**
	 * Initialize the truetype font API. This must be called before
	 * using other functions in this library.
	 *
	 * @exception SDLException if an error occurs
	 */
	public static void init() throws SDLException {
		int result = SWIG_SDLTTF.TTF_Init();
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Shutdown and cleanup the truetype font API. After calling this
	 * the SDL_ttf functions should not be used.
	 *
	 * @exception SDLException if an error occurs
	 */
	public static void quit() throws SDLException {
		SWIG_SDLTTF.TTF_Quit();
	}
	

	/**
	 * Load font from a buffer, at ptsize size. This is actually
	 * TTF_OpenFontIndexRW(SDL_RWops *src, int freesrc, int ptsize, long index). 
	 * This can load TTF and FON files.
	 *
	 * @param data    data buffer
	 * @param ptsize  Point size (based on 72DPI) to load font as. This basically translates to pixel height.
	 * @param index   The index to load at
	 * @return a <code>TTFFont</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLTrueTypeFont openFontIndex(byte[] data, int ptsize, int index) throws SDLException {
		SWIGTYPE_p__TTF_Font swigFont = SWIG_SDLTTF.TTF_OpenFontIndexFromBuffer(data, data.length, ptsize, index);
		if (swigFont == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLTrueTypeFont(swigFont, ptsize);
	}
	
	/**
	 * Load font from an input stream, at ptsize size. This is actually
	 * TTF_OpenFontIndexRW(SDL_RWops *src, int freesrc, int ptsize, long index). 
	 * This can load TTF and FON files.
	 *
	 * @param data    The data input stream 
	 * @param ptsize  Point size (based on 72DPI) to load font as. This basically translates to pixel height.
	 * @param index   The index to load at
	 * @return a <code>TTFFont</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLTrueTypeFont openFontIndex(InputStream is, int ptsize, int index) throws SDLException {
		byte[] data;
		try {
			data = BufferUtil.readInputStream(is);
			return openFontIndex(data, ptsize, index);
		} catch (IOException e) {
			throw new SDLException(e.getMessage());
		}
	}


	/**
	 * Load file for use as a font, at ptsize size. This is actually
	 * TTF_OpenFontIndex(file, ptsize, 0). This can load TTF and FON
	 * files.
	 *
	 * @param path Path to font file
	 * @param ptsize  Point size (based on 72DPI) to load font as. This basically translates to pixel height.
	 * @return a <code>TTFFont</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLTrueTypeFont openFont(String path, int ptsize) throws SDLException {
		return openFontIndex(path, ptsize, 0);
	}

	/**
	 * Load file for use as a font, at ptsize size. This is actually
	 * TTF_OpenFontIndex(file, ptsize, 0). This can load TTF and FON
	 * files.
	 *
	 * @param path    Path to font file
	 * @param ptsize  Point size (based on 72DPI) to load font as. This basically translates to pixel height.
	 * @param index   The index to load at
	 * @return a <code>TTFFont</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLTrueTypeFont openFontIndex(String path, int ptsize, int index) throws SDLException {
		SWIGTYPE_p__TTF_Font swigFont = SWIG_SDLTTF.TTF_OpenFontIndex(path, ptsize, index);
		if (swigFont == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLTrueTypeFont(swigFont, ptsize);
	}

	/**
	 * get a version structure with the compile-time version of the SDL_ttf library.
	 *
	 *
	 * @return the compile-time version of the SDL_ttf library.
	 */
	public static SDLVersion getTTFVersion() {
		SDL_version v = SWIG_SDLTTF.SWIG_TTF_VERSION();
		return new SDLVersion(v);
	}

} // class SDLTTF