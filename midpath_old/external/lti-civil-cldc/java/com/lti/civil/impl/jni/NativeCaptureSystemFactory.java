/*
 * Created on May 25, 2005
 */
package com.lti.civil.impl.jni;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.sun.cldchi.jvm.JVM;

/**
 * 
 * @author Ken Larson
 */
public class NativeCaptureSystemFactory implements CaptureSystemFactory {

	public CaptureSystem createCaptureSystem() throws CaptureException {
		try {
			JVM.loadLibrary("libmidpathcivil.so");
			//System.loadLibrary("midpathcivil");
		} catch (Throwable t) {
			throw new CaptureException(t, CaptureException.GENERIC_ERROR);
		}

		return newCaptureSystemObj();
	}

	private static native CaptureSystem newCaptureSystemObj();

}
