package com.lti.civil;

/**
 * 
 * @author Ken Larson
 *
 */
public interface VideoFormat
{
	// TODO: FPS
	
	public int getFormatType();

	// formats
	public static final int RGB24 = 1;
	public static final int RGB32 = 2;
	
	public int getWidth();
	public int getHeight();
	public float getFPS();
	
	public static final float FPS_UNKNOWN = -1.f;
}
