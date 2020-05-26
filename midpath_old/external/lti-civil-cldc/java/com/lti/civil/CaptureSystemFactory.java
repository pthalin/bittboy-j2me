/*
 * Created on May 25, 2005
 */
package com.lti.civil;

/**
 * 
 * @author Ken Larson
 */
public interface CaptureSystemFactory
{
	public CaptureSystem createCaptureSystem() throws CaptureException;
}
