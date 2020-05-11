/*
 * Created on May 25, 2005
 */
package com.lti.civil;

import java.util.Vector;

/**
 * 
 * @author Ken Larson
 */
public interface CaptureSystem
{
	public void init() throws CaptureException;
	public void dispose() throws CaptureException;
	/** @return List of {@link CaptureDeviceInfo} */
	public Vector getCaptureDeviceInfoList() throws CaptureException;
	public CaptureStream openCaptureDeviceStream(String deviceId) throws CaptureException;
}
