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
package org.thenesis.midpath.svg.awt;

import java.awt.image.BufferedImage;

import com.sun.perseus.model.RasterImage;

/**
 * Class for 2D Raster Images.
 *
 * @version $Id: RasterImage.java,v 1.4 2006/04/21 06:34:58 st125089 Exp $
 */
public class RasterImageImpl implements RasterImage {

	BufferedImage image;

	/**
	 * The cached pixel array.
	 */
	int[] argb;

	/**
	 */
	RasterImageImpl(BufferedImage img) {
		if (img == null) {
			throw new NullPointerException();
		}

		image = img;
	}

	/**
	 * @return the image width.
	 */
	public int getWidth() {
		return image.getHeight();
	}

	/**
	 * @return the image height.
	 */
	public int getHeight() {
		return image.getHeight();
	}

	/**
	 * @return a pixel array where the image data is stored in 
	 *         single pixel packed format 0xaarrggbb, with a 
	 *         scanline stride equal to the image width and a
	 *         zero offset in the returned array. The returned
	 *         array is of size width * height.
	 */
	public int[] getRGB() {

		if (argb != null) {
			return argb;
		}

		int w = image.getWidth();
		int h = image.getHeight();

		//	AWT: int[] getRGB(int startX,
		//            int startY,
		//            int w,
		//            int h,
		//            int[] rgbArray,
		//            int offset,
		//            int scansize)

		//	MIDP: getRGB(int[] rgbData,
		//            int offset,
		//            int scanlength,
		//            int x,
		//            int y,
		//            int width,
		//            int height)

		argb = new int[w * h];
		BufferedImage bImage = (BufferedImage) image;
		bImage.getRGB(0, 0, w, h, argb, 0, w);

		//        image.getRGB(argb,
		//		     0, w,
		//		     0, 0,
		//		     w, h);

		return argb;

	}

}
