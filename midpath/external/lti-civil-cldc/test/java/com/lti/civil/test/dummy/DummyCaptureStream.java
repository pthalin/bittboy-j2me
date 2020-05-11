/*
 * Created on May 27, 2005
 */
package com.lti.civil.test.dummy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.VideoFormat;
import com.lti.civil.impl.common.VideoFormatImpl;
import com.lti.civil.impl.jni.Logger;
import com.lti.civil.test.awt.BufferedImageImage;
import com.lti.civil.test.utils.synchronization.CloseableThread;

// code adapted from http://www.uk-dave.com/bytes/java/jmf-framegrab.shtml
// TODO: bad flicker
// TODO: do we get the same frame twice?
/**
 * 
 * @author Ken Larson
 */
public class DummyCaptureStream  implements CaptureStream
{
	private static final int WIDTH = 320;
	private static final int HEIGHT = 200;
	
	public Vector enumVideoFormats() throws CaptureException 
	{
		final Vector result = new Vector();
		result.add(new VideoFormatImpl(VideoFormat.RGB24, WIDTH, HEIGHT, VideoFormat.FPS_UNKNOWN));
		return result;
	}
	
	public VideoFormat getVideoFormat() throws CaptureException
	{	return (VideoFormat)enumVideoFormats().get(0);
	}

	public void setVideoFormat(VideoFormat f) throws CaptureException 
	{	// TODO
	}

	private static final Logger logger = Logger.global;

	private GrabberThread thread;
	private CaptureObserver observer;
	
	public DummyCaptureStream()
	{	
	}

	public void setObserver(CaptureObserver observer)
	{	this.observer = observer;
	}
	
	public void start() throws CaptureException
	{
        if (thread == null)
        {
	        thread = new GrabberThread();
	        thread.start();
        }

		
	}
	
	int fileIndex;
	class GrabberThread extends CloseableThread
	{
		
		public GrabberThread()
		{
			super(Thread.currentThread().getThreadGroup(), "GrabberThread");
		}

		public void close()
		{
			setClosing();	// don't interrupt
		}

		public void run()
		{
			while (!isClosing())
			{
				
				final int width = WIDTH;
				final int height = HEIGHT;
		        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		        final Date d = new Date();
		        {
		        Graphics2D g = buffImg.createGraphics();
		        //g.drawImage(img, null, null);
		
		        // Overlay curent time on image
		        g.setColor(Color.RED);
		        g.setFont(new Font("Verdana", Font.BOLD, 16));
		        g.drawString((d).toString(), 10, 25);
		        }

		        if (observer != null)
		        	observer.onNewImage(DummyCaptureStream.this, new BufferedImageImage(buffImg, d.getTime()));
		        
				try
				{
					Thread.sleep(1000 / 15);
				} catch (InterruptedException e1)
				{
					break;
				}
		        
		        // Save image to disk as PNG
		        if (false)
		        {
			        try
					{
			        	String path = "webcam" + fileIndex++ + ".png";
						ImageIO.write(buffImg, "png", new File(path));
						logger.fine("Wrote " + path);
					} catch (IOException e)
					{
						throw new RuntimeException(e);
					}
		        }
			}
			
			
			setClosed();
		}

	}
	
	public void stop() throws CaptureException
	{
		// TODO: implement.

		
	}

	public void dispose() throws CaptureException
	{
		if (thread != null)
		{
//			try
//			{
				thread.close();
//				logger.fine("Waiting for GrabberThread to complete...");
//				thread.waitUntilClosed();
//			} catch (InterruptedException e)
//			{
//				logger.log(Level.WARNING, "" + e, e);
//				return;
//			}
		}
//		logger.fine("GrabberThread completed");
		
	}
}
