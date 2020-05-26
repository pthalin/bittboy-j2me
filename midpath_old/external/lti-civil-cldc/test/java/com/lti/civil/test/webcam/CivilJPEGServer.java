package com.lti.civil.test.webcam;

import java.util.List;

import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;


/**
 * 
 * @author Ken Larson
 *
 */
public class CivilJPEGServer
{

	public static void main(String[] args) throws Exception
	{

		final CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton.instance();
		final CaptureSystem system = factory.createCaptureSystem();
		system.init();
		final List list = system.getCaptureDeviceInfoList();
		for (int i = 0; i < list.size(); ++i)
		{
			final CaptureDeviceInfo info = (CaptureDeviceInfo) list.get(i);

			System.out.println("Device ID " + i + ": " + info.getDeviceID());
			System.out.println("Description " + i + ": "
					+ info.getDescription());

			CaptureStream captureStream = system.openCaptureDeviceStream(info
					.getDeviceID());
			CivilJPEG_HTTPD.storeMostRecent_CaptureObserver = new StoreMostRecent_CaptureObserver();
			captureStream.setObserver(CivilJPEG_HTTPD.storeMostRecent_CaptureObserver);
			captureStream.start();
			
			break;
			// captureStream.stop();
			// captureStream.dispose();

		}
		// system.dispose();

		new CivilJPEG_HTTPD(8090);

		while (true)
		{
			Thread.sleep(10000);
		}
	}
}