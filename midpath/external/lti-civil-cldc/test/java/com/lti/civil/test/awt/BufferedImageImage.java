/*
 * Created on Jun 1, 2005
 */
package com.lti.civil.test.awt;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import com.lti.civil.Image;
import com.lti.civil.VideoFormat;
import com.lti.civil.impl.common.VideoFormatImpl;

/**
 * Adapter from BufferedImage to Image.
 * @author Ken Larson
 */
public class BufferedImageImage implements Image
{
	private final BufferedImage bufferedImage;
	private final VideoFormat format;
	private final long timestamp;
	private byte[] bytes;	// once set, we won't create again.
	
	public BufferedImageImage(BufferedImage image, long timestamp)
	{
		super();
		
		this.bufferedImage = image;
		this.timestamp = timestamp;
		
		// if the image is already in the correct 24-bit or 32-bit format, we can use the data directly.
		// if we can, set bytes to point to the correct array.  if bytes is not set, we will convert 
		// later
		final DataBuffer dataBuffer = image.getRaster().getDataBuffer();
		if (dataBuffer instanceof DataBufferByte)
		{
			// we are basically checking for the format used by QTCaptureStream.
			if (image.getColorModel() instanceof ComponentColorModel && image.getSampleModel() instanceof ComponentSampleModel)
			{
				final ComponentColorModel componentColorModel = (ComponentColorModel) image.getColorModel();
				final ComponentSampleModel componentSampleModel = (ComponentSampleModel) image.getSampleModel();
				final int[] offsets = componentSampleModel.getBandOffsets();
				
				if (offsets.length == 4 && offsets[1] == 2 && offsets[2] == 1 && offsets[3] == 0)
				{
					// TODO: check anything else?  see QTCaptureStream.initBufferedImage
					
					final DataBufferByte dbb = (DataBufferByte) dataBuffer;
					bytes = dbb.getBankData()[0];
					
					this.format = new VideoFormatImpl(VideoFormat.RGB32, bufferedImage.getWidth(), bufferedImage.getHeight(), VideoFormat.FPS_UNKNOWN);
					
					return;
				}
				else if (offsets.length == 3 && offsets[0] == 2 && offsets[1] == 1 && offsets[2] == 0)
				{
					// TODO: test.
					// TODO: check anything else?  
					
					final DataBufferByte dbb = (DataBufferByte) dataBuffer;
					bytes = dbb.getBankData()[0];
					this.format = new VideoFormatImpl(VideoFormat.RGB24, bufferedImage.getWidth(), bufferedImage.getHeight(), VideoFormat.FPS_UNKNOWN);
					
					return;
				}
			}
		}
		
		// format used if getBytes has to explicitly create the data.
		
		this.format = new VideoFormatImpl(VideoFormat.RGB24, bufferedImage.getWidth(), bufferedImage.getHeight(), VideoFormat.FPS_UNKNOWN);
		
	}
	


	public byte[] getBytes()
	{
		if (bytes != null)
			return bytes;
		// adapted from sample code at http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet156.java?rev=HEAD
		final int w = bufferedImage.getWidth();
		final int h = bufferedImage.getHeight();
		bytes = new byte[w * h * 3];
		//DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
		//PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
		WritableRaster raster = bufferedImage.getRaster();
		int[] pixelArray = new int[3];
		int off = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				raster.getPixel(x, y, pixelArray);
				bytes[off++] = (byte) pixelArray[0];
				bytes[off++] = (byte) pixelArray[1];
				bytes[off++] = (byte) pixelArray[2];
			}
		}		
		return bytes;
		
	}

	public VideoFormat getFormat() 
	{
		return format;
	}

	public long getTimestamp() 
	{
		return timestamp;
	}
}
