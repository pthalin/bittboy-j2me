package com.lti.civil.impl.jni;

import com.lti.civil.VideoFormat;
import com.lti.civil.impl.common.VideoFormatImpl;

/**
 * 
 * @author Ken Larson
 *
 */
public class NativeVideoFormat extends VideoFormatImpl implements VideoFormat {
	// opaque value is only used so that native implementations can store extra info like
	// a pointer or id, so they don't have to re-look up the format type/width/height in a table or list.
	private final long opaque;

	public NativeVideoFormat(long opaque, int formatType, int width, int height, float fps) {
		super(formatType, width, height, fps);
		this.opaque = opaque;
	}

	public long getOpaque() {
		return opaque;
	}

}
