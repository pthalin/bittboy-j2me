package com.sun.cldchi.jvm;

public class JVM {

	/**
	 * Wrapper to System.loadLibrary(). It's needed to support JNI library 
	 * loading from CLDC code running on J2SE.
	 * @param name The name of the library to load
	 */
	public static void loadLibrary(String name) {
		
		if(name.startsWith("lib") && (name.length() > 3)) {
			name = name.substring(3, name.length());
		}
		
		if(name.endsWith(".so") && (name.length() > 3)) {
			name = name.substring(0, name.length() - 3);
		}
		
		System.loadLibrary(name);
	}
	
}
