/*
 * Created on May 25, 2005
 */
package com.lti.civil;

/**
 * 
 * @author Ken Larson
 */
public interface CaptureObserver
{
	/** Implementations should catch all exceptions (Throwable), as this is often called from a native thread, which 
	 * does not have very sophisticated exception handling.
	 * TODO: we need to clarify how long the image is valid to use by the callee.
	 * For now, the implementation is such that each image sent to onNewImage uses 
	 * a new byte array, so the receiver does not have to process them immediately.
	 * It would be good to have a clear lifecycle for the Image, so that the callee
	 * can inform the caller that it is done with an image, to prevent excessive
	 * allocation and deallocation.
	 */
	public void onNewImage(CaptureStream sender, Image image);
	public void onError(CaptureStream sender, CaptureException e);
}
