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
import java.util.Vector;

import sdljava.x.swig.SDL_Color;
import sdljava.x.swig.SDL_PixelFormat;
import sdljava.x.swig.SWIG_SDLVideo;

/**
 * Stores surface format information
 * <P>
 * Also see the documentation here:
 *    <a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_5fPixelFormat">SDL_PixelFormat</a>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLPixelFormat.java,v 1.12 2005/01/19 03:09:14 ivan_ganza Exp $
 * @todo Finish SWIG integration
 */
public class SDLPixelFormat {

	SDL_PixelFormat swigPixelFormat;

	/**
	 * Creates a new <code>SDLPixelFormat</code> instance.
	 *
	 */
	protected SDLPixelFormat(SDL_PixelFormat f) {
		this.swigPixelFormat = f;

	}

	protected SDL_PixelFormat getSwigPixelFormat() {
		return swigPixelFormat;
	}

	/**
	 * Gets the value of palette
	 *
	 * @return the value of palette
	 */
	public Vector getPalette() {
		if (swigPixelFormat.getPalette() != null) {
			SDL_Color[] colors = new SDL_Color[256];
			for (int i = 0; i < colors.length; i++) {
				colors[i] = new SDL_Color();
			}
			SWIG_SDLVideo.SWIG_GetPaletteColors(swigPixelFormat.getPalette(), colors);

			Vector l = new Vector();
			for (int i = 0; i < colors.length; i++) {
				l.addElement(new SDLColor(colors[i].getR(), colors[i].getG(), colors[i].getB()));
			}

			return l;
		}
		return null;
	}

	/**
	 * Gets the value of bitsPerPixel
	 *
	 * @return the value of bitsPerPixel
	 */
	public short getBitsPerPixel() {
		return swigPixelFormat.getBitsPerPixel();
	}

	/**
	 * Sets the value of bitsPerPixel
	 *
	 * @param argBitsPerPixel Value to assign to this.bitsPerPixel
	 */
	public void setBitsPerPixel(short argBitsPerPixel) {
		swigPixelFormat.setBitsPerPixel(argBitsPerPixel);
	}

	/**
	 * Gets the value of bytesPerPixel
	 *
	 * @return the value of bytesPerPixel
	 */
	public short getBytesPerPixel() {
		return swigPixelFormat.getBytesPerPixel();
	}

	/**
	 * Sets the value of bytesPerPixel
	 *
	 * @param argBytesPerPixel Value to assign to this.bytesPerPixel
	 */
	public void setBytesPerPixel(short argBytesPerPixel) {
		swigPixelFormat.setBytesPerPixel(argBytesPerPixel);
	}

	/**
	 * Gets the value of rLoss
	 *
	 * @return the value of rLoss
	 */
	public short getRLoss() {
		return swigPixelFormat.getRloss();
	}

	/**
	 * Sets the value of rLoss
	 *
	 * @param argRLoss Value to assign to this.rLoss
	 */
	public void setRLoss(short argRLoss) {
		swigPixelFormat.setRloss(argRLoss);
	}

	/**
	 * Gets the value of gLoss
	 *
	 * @return the value of gLoss
	 */
	public short getGLoss() {
		return swigPixelFormat.getGloss();
	}

	/**
	 * Sets the value of gLoss
	 *
	 * @param argGLoss Value to assign to this.gLoss
	 */
	public void setGLoss(short argGLoss) {
		swigPixelFormat.setGloss(argGLoss);
	}

	/**
	 * Gets the value of aLoss
	 *
	 * @return the value of aLoss
	 */
	public short getALoss() {
		return swigPixelFormat.getAloss();
	}

	/**
	 * Sets the value of aLoss
	 *
	 * @param argALoss Value to assign to this.aLoss
	 */
	public void setALoss(short argALoss) {
		swigPixelFormat.setAloss(argALoss);
	}

	/**
	 * Gets the value of rShift
	 *
	 * @return the value of rShift
	 */
	public int getRShift() {
		return swigPixelFormat.getRshift();
	}

	/**
	 * Sets the value of rShift
	 *
	 * @param argRShift Value to assign to this.rShift
	 */
	public void setRShift(short argRShift) {
		swigPixelFormat.setRshift(argRShift);
	}

	/**
	 * Gets the value of gShift
	 *
	 * @return the value of gShift
	 */
	public short getGShift() {
		return swigPixelFormat.getGshift();
	}

	/**
	 * Sets the value of gShift
	 *
	 * @param argGShift Value to assign to this.gShift
	 */
	public void setGShift(short argGShift) {
		swigPixelFormat.setGshift(argGShift);
	}

	/**
	 * Gets the value of bShift
	 *
	 * @return the value of bShift
	 */
	public short getBShift() {
		return swigPixelFormat.getBshift();
	}

	/**
	 * Sets the value of bShift
	 *
	 * @param argBShift Value to assign to this.bShift
	 */
	public void setBShift(short argBShift) {
		swigPixelFormat.setBshift(argBShift);
	}

	/**
	 * Gets the value of aShift
	 *
	 * @return the value of aShift
	 */
	public short getAShift() {
		return swigPixelFormat.getAshift();
	}

	/**
	 * Sets the value of aShift
	 *
	 * @param argAShift Value to assign to this.aShift
	 */
	public void setAShift(short argAShift) {
		swigPixelFormat.setAshift(argAShift);
	}

	/**
	 * Gets the value of rMask
	 *
	 * @return the value of rMask
	 */
	public long getRMask() {
		return swigPixelFormat.getRmask();
	}

	/**
	 * Sets the value of rMask
	 *
	 * @param argRMask Value to assign to this.rMask
	 */
	public void setRMask(long argRMask) {
		swigPixelFormat.setRmask(argRMask);
	}

	/**
	 * Gets the value of gMask
	 *
	 * @return the value of gMask
	 */
	public long getGMask() {
		return swigPixelFormat.getGmask();
	}

	/**
	 * Sets the value of gMask
	 *
	 * @param argGMask Value to assign to this.gMask
	 */
	public void setGMask(long argGMask) {
		swigPixelFormat.setGmask(argGMask);
	}

	/**
	 * Gets the value of bMask
	 *
	 * @return the value of bMask
	 */
	public long getBMask() {
		return swigPixelFormat.getBmask();
	}

	/**
	 * Sets the value of bMask
	 *
	 * @param argBMask Value to assign to this.bMask
	 */
	public void setBMask(long argBMask) {
		swigPixelFormat.setBmask(argBMask);
	}

	/**
	 * Gets the value of aMask
	 *
	 * @return the value of aMask
	 */
	public long getAMask() {
		return swigPixelFormat.getAmask();
	}

	/**
	 * Sets the value of aMask
	 *
	 * @param argAMask Value to assign to this.aMask
	 */
	public void setAMask(long argAMask) {
		swigPixelFormat.setAmask(argAMask);
	}

	/**
	 * Gets the value of colorKey
	 *
	 * @return the value of colorKey
	 */
	public long getColorKey() {
		return swigPixelFormat.getColorkey();
	}

	/**
	 * Sets the value of colorKey
	 *
	 * @param argColorKey Value to assign to this.colorKey
	 */
	public void setColorKey(long argColorKey) {
		swigPixelFormat.setColorkey(argColorKey);
	}

	/**
	 * Gets the value of alpha
	 *
	 * @return the value of alpha
	 */
	public short getAlpha() {
		return swigPixelFormat.getAlpha();
	}

	/**
	 * Sets the value of alpha
	 *
	 * @param argAlpha Value to assign to this.alpha
	 */
	public void setAlpha(short argAlpha) {
		swigPixelFormat.setAlpha(argAlpha);
	}

	/**
	 * Gets the value of bLoss
	 *
	 * @return the value of bLoss
	 */
	public short getBLoss() {
		return swigPixelFormat.getBloss();
	}

	/**
	 * Sets the value of bLoss
	 *
	 * @param argBLoss Value to assign to this.bLoss
	 */
	public void setBLoss(short argBLoss) {
		swigPixelFormat.setBloss(argBLoss);
	}

	/**
	 * @return a String representation of myself
	 *
	 * 
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		Vector palette = getPalette();

		buf.append("SDLPixelFormat[").append("bitsPerPixel=").append(getBitsPerPixel()).append(", bytesPerPixel=")
				.append(getBytesPerPixel()).append(", palette=").append(palette == null ? "(NO PALETTE)" : "(PALETTE)")
				.append(", rLoss=").append(getRLoss()).append(", gLoss=").append(getGLoss()).append(", bLoss=").append(
						getBLoss()).append(", aLoss=").append(getALoss()).append(", rShift=").append(getRShift())
				.append(", gShift=").append(getGShift()).append(", bShift=").append(getBShift()).append(", aShift=")
				.append(getAShift()).append(", rMask=").append(getRMask()).append(", gMask=").append(getGMask())
				.append(", bMask=").append(getBMask()).append(", aMask=").append(getAMask()).append(", colorKey=")
				.append(getColorKey()).append(", alpha=").append(getAlpha()).append("]");

		return buf.toString();
	}

} // class SDLPixelFormat
