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
/**
 * Class to represent a possible SDL Video Mode (not part of SDL)
 * 
 *
 * @author Ivan Z. Ganza
 * @author Chris Dennett
 * @version $Id: SDLVideoMode.java,v 1.4 2005/02/23 02:46:48 ivan_ganza Exp $
 */
public class SDLVideoMode {
	int width;
	int height;
	int bpp;
	int flags;

	/**
	 * Creates a new SDLVideoMode object.
	 * @param width The width of the screen.
	 * @param height The height of the screen
	 * @param bpp The bits per pixel.
	 * @param flags The flags of the video mode.
	 */
	public SDLVideoMode(int width, int height, int bpp, int flags) {
		this.width = width;
		this.height = height;
		this.bpp = bpp;
		this.flags = flags;
	}

	/**
	 * Creates a new SDLVideoMode object with no flags.
	 * @param width The width of the screen.
	 * @param height The height of the screen
	 * @param bpp The bits per pixel.
	 */
	public SDLVideoMode(int width, int height, int bpp) {
		this(width, height, bpp, 0);
	}

	/**
	 * Gets the value of width
	 *
	 * @return the value of width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Gets the value of height
	 *
	 * @return the value of height
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Gets the value of bpp
	 *
	 * @return the value of bpp
	 */
	public int getBpp() {
		return this.bpp;
	}

	/**
	 * Gets the value of flags
	 *
	 * @return the value of flags
	 */
	public int getFlags() {
		return this.flags;
	}
} // class VideoMode