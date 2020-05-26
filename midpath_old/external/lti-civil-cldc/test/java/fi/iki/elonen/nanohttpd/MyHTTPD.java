package fi.iki.elonen.nanohttpd;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * 
 * @author Ken Larson
 *
 */
public class MyHTTPD extends NanoHTTPD
{
	public MyHTTPD(int port) throws IOException
	{
		super(port);
	}

	/**
	 * Starts as a standalone file server and waits for Enter.
	 */
	public static void main( String[] args )
	{

		// Show licence if requested
		//int lopt = -1;
	

		// Change port if requested
		int port = 2050;


		MyHTTPD nh = null;
		try
		{
			nh = new MyHTTPD( port );
		}
		catch( IOException ioe )
		{
			System.err.println( "Couldn't start server:\n" + ioe );
			System.exit( -1 );
		}
		nh.myFileDir = new File("");

		System.out.println( "Now serving files in port " + port + " from \"" +
							new File("").getAbsolutePath() + "\"" );
		System.out.println( "Hit Enter to stop.\n" );

		try { System.in.read(); } catch( Throwable t ) {};
	}

	public Response serve(String uri, String method, Properties header, Properties parms)
	{
		System.out.println( method + " '" + uri + "' " );

		Enumeration e = header.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			System.out.println( "  HDR: '" + value + "' = '" +
								header.getProperty( value ) + "'" );
		}
		e = parms.propertyNames();
		while ( e.hasMoreElements())
		{
			String value = (String)e.nextElement();
			System.out.println( "  PRM: '" + value + "' = '" +
								parms.getProperty( value ) + "'" );
		}
		
		String msg = "<html><head></head>";
		msg += "<body>";
		msg += "<p>Hello</p>";
		msg += "</body></html>";
		
		return new Response( HTTP_OK, MIME_HTML, msg );

	}
}
