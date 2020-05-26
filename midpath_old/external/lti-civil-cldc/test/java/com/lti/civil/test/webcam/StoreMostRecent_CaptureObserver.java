package com.lti.civil.test.webcam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.Image;
import com.lti.civil.test.awt.AWTImageConverter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 
 * @author Ken Larson
 *
 */
public class StoreMostRecent_CaptureObserver implements CaptureObserver
{
	private static final Logger logger = Logger.global;

	private byte[] bytes;
	
	public void onNewImage(final CaptureStream sender, final Image image)
	{
		if (image == null)
		{	bytes = null;
			return;
		}
		try
		{
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder(os);
			jpeg.encode(AWTImageConverter.toBufferedImage(image));
			os.close();
			bytes = os.toByteArray();
		}
		catch (IOException e)
		{	logger.log(Level.WARNING, "" + e, e);
			bytes = null;
		}	
		catch (Throwable t)
		{	logger.log(Level.SEVERE, "" + t, t);
			bytes = null;
		}
	}
	public void onError(CaptureStream sender, CaptureException e)
	{
		logger.log(Level.WARNING, "" + e, e);
		bytes = null;
	}

	
	public byte[] getBytes()
	{	return bytes;
	}
}
