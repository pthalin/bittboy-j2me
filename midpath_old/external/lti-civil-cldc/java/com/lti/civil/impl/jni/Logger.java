package com.lti.civil.impl.jni;

public class Logger {

	public static final boolean DEBUG = true;
	
	public static final Logger global = new Logger();
	public static final int WARNING = 0;
	public static final int SEVERE = 1;

	public void fine(String message) {
		if (DEBUG) {
			System.out.println("[DEBUG] " + message +" :" );
		}
	}

	public void log(int level, String message, Throwable t) {
		if (DEBUG) {
			String levelString = (level == WARNING ? "WARNING":"SEVERE");
			System.out.println("[" + levelString + "] " + message +" :" );
			t.printStackTrace();
		}
	}


}
