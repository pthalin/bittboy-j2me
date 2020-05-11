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
 * SDLColor describes a color in a format independent way.
 * You can convert a SDLColor to a pixel value for a certain pixel format using Video.mapRGB
 * <P>
 * <I>Note:  On the java side alpha was added to this class so that the return value from
 *           SDL_MapRGBA can be easily accommodated.  Otherwise the value is meaningless
 *           and can safely be ignore
 * </I>
 * <P>
 * Also see the documentation here:
 *    <a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_5fColor">SDL_Color</a>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLColor.java,v 1.7 2004/12/24 17:32:17 ivan_ganza Exp $
 *
 */
public class SDLColor {
	int red;
	int green;
	int blue;
	int alpha;

	public SDLColor() {
	}

	public SDLColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = 0xFF; // opaque
	}

	public SDLColor(int red, int green, int blue, int alpha) {
		this(red, green, blue);
		this.alpha = alpha;
	}

	/**
	 * Gets the value of red
	 *
	 * @return the value of red
	 */
	public int getRed() {
		return this.red;
	}

	/**
	 * Sets the value of red
	 *
	 * @param argRed Value to assign to this.red
	 */
	public void setRed(int argRed) {
		this.red = argRed;
	}

	/**
	 * Gets the value of green
	 *
	 * @return the value of green
	 */
	public int getGreen() {
		return this.green;
	}

	/**
	 * Sets the value of green
	 *
	 * @param argGreen Value to assign to this.green
	 */
	public void setGreen(int argGreen) {
		this.green = argGreen;
	}

	/**
	 * Gets the value of blue
	 *
	 * @return the value of blue
	 */
	public int getBlue() {
		return this.blue;
	}

	/**
	 * Sets the value of blue
	 *
	 * @param argBlue Value to assign to this.blue
	 */
	public void setBlue(int argBlue) {
		this.blue = argBlue;
	}

	/**
	 * Gets the value of alpha
	 *
	 * @return the value of alpha
	 */
	public int getAlpha() {
		return this.alpha;
	}

	/**
	 * Sets the value of alpha
	 *
	 * @param argAlpha Value to assign to this.alpha
	 */
	public void setAlpha(int argAlpha) {
		this.alpha = argAlpha;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLColor[").append("red=").append(red).append(", green=").append(green).append(", blue=").append(
				blue).append(", alpha=").append(alpha).append("]");

		return buf.toString();
	}
};