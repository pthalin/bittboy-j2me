package com.lti.civil.test.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;
import com.lti.civil.test.awt.AWTImageConverter;

/**
 * Simple GUI to display captured video.
 * @author Ken Larson
 *
 */
public class CaptureFrame extends ImageFrame
{

	public static void main(String[] args) throws CaptureException
	{
		new CaptureFrame(DefaultCaptureSystemFactorySingleton.instance()).run();
		
	}
	
	private CaptureSystem system;
	private CaptureStream captureStream;
	private final CaptureSystemFactory factory;
	private volatile boolean disposing = false;
	
	
	public CaptureFrame(CaptureSystemFactory factory)
	{	
		super("LTI-CIVIL");
		this.factory = factory;
	
	}
	
	public void run() throws CaptureException
	{
		
		initCapture();
		
		
		setSize(captureStream.getVideoFormat().getWidth(), captureStream.getVideoFormat().getHeight());
		setLocation(200, 200);
		
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				try
				{
					disposeCapture();
				} catch (CaptureException e1)
				{
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});

		

		setVisible(true);
		pack();
		
		startCapture();
	}
	

	
	public void initCapture() throws CaptureException
	{
		
		system = factory.createCaptureSystem();
		system.init();
		List list = system.getCaptureDeviceInfoList();
		for (int i = 0; i < list.size(); ++i)
		{
			CaptureDeviceInfo info = (CaptureDeviceInfo) list.get(i);
			
			System.out.println("Device ID " + i + ": " + info.getDeviceID());
			System.out.println("Description " + i + ": " + info.getDescription());
			captureStream = system.openCaptureDeviceStream(info.getDeviceID());
			captureStream.setObserver(new MyCaptureObserver());
			
			break;
		}
		
	}
	
	public void startCapture() throws CaptureException
	{
		captureStream.start();
	}

	public void disposeCapture() throws CaptureException
	{
		disposing = true;
		
		if (captureStream != null)
		{	System.out.println("disposeCapture: stopping capture stream...");
			captureStream.stop();
			System.out.println("disposeCapture: stopped capture stream.");
			captureStream.dispose();
			captureStream = null;
		}
		
		if (system != null)
			system.dispose();
		System.out.println("disposeCapture done.");
	}

	
	class MyCaptureObserver implements CaptureObserver
	{

		public void onError(CaptureStream sender, CaptureException e)
		{	
			e.printStackTrace();
		}


		public void onNewImage(CaptureStream sender, com.lti.civil.Image image)
		{	
			if (disposing)
				return;
			try
			{
				setImage(AWTImageConverter.toBufferedImage(image));
			}
			catch (Throwable t)
			{	t.printStackTrace();
			}
		}
		
	}
}
