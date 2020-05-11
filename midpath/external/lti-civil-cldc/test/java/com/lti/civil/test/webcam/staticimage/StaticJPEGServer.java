package com.lti.civil.test.webcam.staticimage;

/**
 * 
 * @author Ken Larson
 *
 */
public class StaticJPEGServer
{

	public static void main(String[] list) throws Exception
	{
		new StaticJPEG_HTTPD(8090);

		while (true)
		{
			Thread.sleep(10000);
		}
	}
}