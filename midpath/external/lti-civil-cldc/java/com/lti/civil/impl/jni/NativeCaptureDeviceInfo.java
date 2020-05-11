/*
 * Created on May 25, 2005
 */
package com.lti.civil.impl.jni;

import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.impl.common.CaptureDeviceInfoImpl;

/**
 * 
 * @author Ken Larson
 */
public class NativeCaptureDeviceInfo extends CaptureDeviceInfoImpl implements CaptureDeviceInfo
{

	public NativeCaptureDeviceInfo(String deviceID, String description) 
	{
		super(deviceID, description);

	}

	
	
}
