/*
 * Created on May 25, 2005
 */
package com.lti.civil.impl.jni;

import java.util.Vector;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;

/**
 * 
 * @author Ken Larson
 */
public class NativeCaptureSystem extends Peered implements CaptureSystem
{
	public NativeCaptureSystem(long ptr)
	{
		super(ptr);
		
	}
	public native void dispose() throws CaptureException;
	public native Vector getCaptureDeviceInfoList() throws CaptureException;
	public native void init() throws CaptureException;
	public native CaptureStream openCaptureDeviceStream(String deviceId) throws CaptureException;

}
