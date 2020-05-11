/*
 * Created on Jun 1, 2005
 */
package com.lti.civil.test.awt;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import com.lti.civil.VideoFormat;

/**
 * 
 * @author Ken Larson
 */
public class AWTImageConverter
{
	
	// adapted from FMJ's BufferToImage class.
	
	public static BufferedImage toBufferedImage(final com.lti.civil.Image image)
	{
		// TODO: if it is a BufferedImageImage, we should just cast and return the wrapped image.
		final VideoFormat format = image.getFormat();
		final int w = format.getWidth();
		final int h = format.getHeight();
		
		byte[] bytes = image.getBytes();
		if (format.getFormatType() == com.lti.civil.VideoFormat.RGB24)
		{
			// this is much faster than iterating through the pixels.
			// if we create a writable raster and then construct a buffered image,
			// no new array is created and no data is copied.
			// TODO: optimize other cases.
			final DataBufferByte db = new DataBufferByte(new byte[][] {bytes}, bytes.length);
			final ComponentSampleModel sm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, w, h, 3, w * 3, new int[] {2, 1, 0});
			final WritableRaster r = Raster.createWritableRaster(sm, db, new Point(0, 0));
			// construction borrowed from BufferedImage constructor, for BufferedImage.TYPE_3BYTE_BGR
            final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            int[] nBits = {8, 8, 8};
            //int[] bOffs = {2, 1, 0};
            final ColorModel colorModel = new ComponentColorModel(cs, nBits, false, false,
                                                 Transparency.OPAQUE,
                                                 DataBuffer.TYPE_BYTE);
            final BufferedImage bi = new BufferedImage(colorModel, r, false, null);
			return bi;
		}
		else if (format.getFormatType() == com.lti.civil.VideoFormat.RGB32)
		{
			final DataBufferByte db = new DataBufferByte(new byte[][] {bytes}, bytes.length);
			final ComponentSampleModel sm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, w, h, 4, w * 4, new int[] {2, 1, 0, 3});	
			final WritableRaster r = Raster.createWritableRaster(sm, db, new Point(0, 0));
			// construction borrowed from BufferedImage constructor, for BufferedImage.TYPE_4BYTE_ABGR
            final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            int[] nBits = {8, 8, 8, 8};
            //int[] bOffs = {3, 2, 1, 0};
            final ColorModel colorModel = new ComponentColorModel(cs, nBits, true, false,
                                                 Transparency.TRANSLUCENT,
                                                 DataBuffer.TYPE_BYTE);
            final BufferedImage bi = new BufferedImage(colorModel, r, false, null);
			return bi;
		}
		else
		{	throw new IllegalArgumentException();
		}
	}

}
