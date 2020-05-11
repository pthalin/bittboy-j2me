package com.lti.civil.test.webcam;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import fi.iki.elonen.nanohttpd.NanoHTTPD;

/**
 * 
 * @author Ken Larson
 *
 */
public class CivilJPEG_HTTPD extends NanoHTTPD
{
	
	public static StoreMostRecent_CaptureObserver storeMostRecent_CaptureObserver;

 	public CivilJPEG_HTTPD(int port) throws IOException
 	{
 		super(port);
 	}


 	public Response serve(String uri, String method, Properties header, Properties parms)
 	{

// 		logger.debug( method + " '" + uri + "' " );
//
// 		Enumeration e = header.propertyNames();
// 		while ( e.hasMoreElements())
// 		{
// 			String value = (String)e.nextElement();
// 			logger.debug( "  HDR: '" + value + "' = '" +
// 								header.getProperty( value ) + "'" );
// 		}
// 		e = parms.propertyNames();
// 		while ( e.hasMoreElements())
// 		{
// 			String value = (String)e.nextElement();
// 			logger.debug( "  PRM: '" + value + "' = '" +
// 								parms.getProperty( value ) + "'" );
// 		}
 		
		{
			final byte[] bytes = storeMostRecent_CaptureObserver.getBytes();
			if (bytes == null)
				throw new NullPointerException("No image");

			final ByteArrayInputStream is = new ByteArrayInputStream(bytes);

			return new Response( HTTP_OK, "image/jpeg", is );
		} 
        
 		
 		

 

 	}
 }