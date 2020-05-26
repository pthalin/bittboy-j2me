package com.lti.civil.test.webcam.staticimage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import fi.iki.elonen.nanohttpd.NanoHTTPD;

/**
 * 
 * @author Ken Larson
 *
 */
public class StaticJPEG_HTTPD extends NanoHTTPD
{
	private static final Logger logger = Logger.global;

 	public StaticJPEG_HTTPD(int port) throws IOException
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
 		
		try
		{
			final FileInputStream is = new FileInputStream("image.jpeg");
			return new Response( HTTP_OK, "image/jpeg", is );
		} catch (FileNotFoundException e)
		{
			logger.log(Level.WARNING, "" + e, e);
			return null;	// TODO: error response
		}
        
 		
 		

 

 	}
 }