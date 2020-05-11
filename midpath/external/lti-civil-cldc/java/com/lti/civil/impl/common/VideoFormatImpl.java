package com.lti.civil.impl.common;

import com.lti.civil.VideoFormat;

/**
 * 
 * @author Ken Larson
 *
 */
public class VideoFormatImpl implements VideoFormat 
{
	private final int formatType;
	private final int width;
	private final int height;
	private final float fps;
	
	public VideoFormatImpl(final int formatType, final int width, final int height, final float fps) 
	{
		super();
		this.formatType = formatType;
		this.width = width;
		this.height = height;
		this.fps = fps;
	}

	public int getFormatType() 
	{
		return formatType;
	}

	public int getWidth() 
	{
		return width;
	}
	
	public int getHeight() 
	{
		return height;
	}

	public float getFPS()
	{	return fps;
	}

}
