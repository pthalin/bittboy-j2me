/*
 * Created on May 25, 2005
 */
package com.lti.civil;


/**
 * 
 * @author Ken Larson
 */
public interface Image
{
	public byte[] getBytes();
	
	public VideoFormat getFormat();
	/** milliseconds, like System.currentTimeMillis. */
	public long getTimestamp();	
}
