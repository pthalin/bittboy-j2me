/*
 * Created on May 27, 2005
 */
package com.lti.civil.test.dummy;

import java.util.Vector;

import com.lti.civil.CaptureException;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.impl.common.CaptureDeviceInfoImpl;

/**
 * 
 * @author Ken Larson
 */
public class DummyCaptureSystem implements CaptureSystem {
	public DummyCaptureSystem() {

	}

	public void init() throws CaptureException {
	}

	public void dispose() throws CaptureException {
	}

	public Vector getCaptureDeviceInfoList() throws CaptureException {
		final Vector result = new Vector();
		result.add(new CaptureDeviceInfoImpl("Dummy", "Dummy"));
		return result;
	}

	public CaptureStream openCaptureDeviceStream(final String deviceId) throws CaptureException {

		return new DummyCaptureStream();
	}

}
