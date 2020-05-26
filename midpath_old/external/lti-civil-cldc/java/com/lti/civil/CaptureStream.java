/*
 * Created on May 25, 2005
 */
package com.lti.civil;

import java.util.Vector;

/**
 * 
 * @author Ken Larson
 */
public interface CaptureStream
{
	public Vector enumVideoFormats() throws CaptureException;
	// TODO: clarify when video format may be set.  Must the stream be started?  must it be stopped?
	// for now, it may only be set before starting.
	/** Must be a video format returned from enumVideoFormats. */
	public void setVideoFormat(VideoFormat f) throws CaptureException;
	public VideoFormat getVideoFormat() throws CaptureException;
	public void start() throws CaptureException;
	public void stop() throws CaptureException;
	public void dispose() throws CaptureException;
	public void setObserver(CaptureObserver observer);
}
