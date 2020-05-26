package com.sun.midp.io.j2me.push;


public class PushRegistryImpl  {

	public static void registerConnection(String connection, String midlet, String filter) {
		// TODO
	}

	public static boolean unregisterConnection(String connection) {
		// TODO 
		return true;
	}

	public static String[] listConnections(boolean available) {
		// TODO 
		return null;
	}

	public static String getMIDlet(String connection) {
		// TODO 
		return null;
	}

	public static long registerAlarm(String midlet, long time) {
		//TODO
		return 0;
	}

	public static String getFilter(String connection) {
		// TODO
		return null;
	}

}
