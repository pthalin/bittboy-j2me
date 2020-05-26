package com.sun.perseus.model;

public interface RasterImage {

	/**
	 * @return the image width.
	 */
	public int getWidth();

	/**
	 * @return the image height.
	 */
	public int getHeight();

	/**
	 * @return a pixel array where the image data is stored in 
	 *         single pixel packed format 0xaarrggbb, with a 
	 *         scanline stride equal to the image width and a
	 *         zero offset in the returned array. The returned
	 *         array is of size width * height.
	 */
	public int[] getRGB();

}