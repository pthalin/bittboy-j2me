/*
 * Created on May 31, 2005
 */
package com.lti.civil.impl.jni;

import com.lti.civil.VideoFormat;
import com.lti.civil.impl.common.ImageImpl;

/**
 * 
 * @author Ken Larson
 */
public class NativeImage extends ImageImpl {
	// TODO: use this constructor.
	public NativeImage(VideoFormat format, byte[] bytes, long timestamp) {
		super(format, bytes, timestamp);
	}

	public NativeImage(VideoFormat format, byte[] bytes) {
		super(format, bytes);

	}

}
