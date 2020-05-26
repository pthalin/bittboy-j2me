/*
 * Created on May 25, 2005
 */
package com.lti.civil;

/**
 * 
 * @author Ken Larson
 */
public class CaptureException extends Exception {
	
	public static int GENERIC_ERROR = 0;
	private int errorCode;

	public CaptureException() {
		super();
	}

	public CaptureException(String message, final int errorCode) {
		super(message + ": " + errorCode);
		this.errorCode = errorCode;

	}
	
	public CaptureException(Throwable t, final int errorCode) {
		this(t.getMessage(), errorCode);
	}

	public int getErrorCode() {
		return errorCode;
	}

}
