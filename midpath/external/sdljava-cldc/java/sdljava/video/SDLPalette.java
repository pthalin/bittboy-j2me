package sdljava.video;

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
import sdljava.x.swig.SDL_Palette;

/**
 * Color palette for 8-bit pixel formats
 * <P><B><I>NOTE: 8-bit pixel formats are not yet supported! </I></B>
 * <P>
 * Each pixel in an 8-bit surface is an index into the colors field of
 * the SDL_Palette structure store in SDL_PixelFormat. A SDL_Palette should
 * never need to be created manually. It is automatically created when SDL
 * allocates a SDL_PixelFormat for a surface. The colors values of a SDL_Surfaces
 * palette can be set with the SDL_SetColors.
 * <P>
 * Also see the documentation here:
 *     <a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_5fPalette>SDL_Palette</a>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLPalette.java,v 1.9 2004/12/24 17:32:17 ivan_ganza Exp $
 * @todo Finish SWIG integration
 */
public class SDLPalette {

	SDL_Palette swigPalette;

	/**
	 * Creates a new <code>SDLPalette</code> instance.
	 *
	 */
	protected SDLPalette(SDL_Palette swigPalette) {
		this.swigPalette = swigPalette;
	}

} // class SDLPalette