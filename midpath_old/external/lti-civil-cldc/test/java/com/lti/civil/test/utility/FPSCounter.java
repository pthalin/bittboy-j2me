package com.lti.civil.test.utility;

/**
 * Utility for measuring FPS, used for benchmarking and optimization. 
 * @author Ken Larson
 *
 */
public class FPSCounter 
{
	private int frames;
	private long start;

	public void reset()
	{
		start = 0;
		frames = 0;
	}
	
	public void nextFrame()
	{	
		if (start == 0)
			start = System.currentTimeMillis();
		
		++frames;
	}
	
	public int getNumFrames()
	{	return frames;
	}
	
	public double getFPS()
	{
		long now = System.currentTimeMillis();
		return 1000.0 * frames / (now - start);
	}
	
	public String toString()
	{
		return "FPS: " + getFPS();
	}
}
