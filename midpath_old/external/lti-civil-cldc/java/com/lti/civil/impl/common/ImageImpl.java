/*
 * Created on May 31, 2005
 */
package com.lti.civil.impl.common;

import com.lti.civil.Image;
import com.lti.civil.VideoFormat;

/**
 * 
 * @author Ken Larson
 */
public class ImageImpl implements Image
{
	private final VideoFormat format;
	private final byte[] bytes;
	private final long timestamp;
	
	public ImageImpl(VideoFormat format, byte[] bytes, long timestamp)
	{
		super();
		
		this.format = format;
		this.bytes = bytes;
		this.timestamp = timestamp;
	}
	public ImageImpl(VideoFormat format, byte[] bytes)
	{
		this(format, bytes, System.currentTimeMillis());
	}
	
	
	public byte[] getBytes()
	{
		return bytes;
	}


	public VideoFormat getFormat() 
	{
		return format;
	}


	public long getTimestamp() {
		return timestamp;
	}


	

	
}
