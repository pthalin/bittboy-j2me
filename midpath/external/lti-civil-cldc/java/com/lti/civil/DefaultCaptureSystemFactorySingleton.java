/*
 * Created on Jun 1, 2005
 */
package com.lti.civil;

import com.lti.civil.impl.jni.NativeCaptureSystemFactory;

/**
 * 
 * @author Ken Larson
 */
public class DefaultCaptureSystemFactorySingleton
{
	private static CaptureSystemFactory instance;
	public static CaptureSystemFactory instance()
	{
		if (instance != null)
			return instance;
		//if (OSUtils.isLinux())
			instance = new NativeCaptureSystemFactory();
		//else if (OSUtils.isWindows())
		//	instance = new NativeCaptureSystemFactory();
		
//		else
//			instance = new JMFCaptureSystemFactory();
		return instance;
	}
}
