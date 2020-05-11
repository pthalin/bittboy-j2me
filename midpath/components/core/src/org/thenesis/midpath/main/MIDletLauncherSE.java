/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.main;

import com.sun.midp.log.LogChannels;
import com.sun.midp.log.Logging;

public class MIDletLauncherSE {

	public static void main(String[] args) {
		
		// Load system properties required by MIDP2 and JSR specs
		MIDletLauncherSE.callSystemPropertiesLoader();
		
		// Launch the MIDlet
		MIDletLauncher.main(args);

	}
	
	static void callSystemPropertiesLoader()  {
		
		try {
			Class clazz = Class.forName("org.thenesis.midpath.main.SystemPropertiesLoader");
			clazz.newInstance();
		} catch (Exception e) {
			if (Logging.REPORT_LEVEL <= Logging.WARNING) {
				Logging.report(Logging.WARNING, LogChannels.LC_CORE, "MIDletLauncherSE: System properties can't be loaded");
			}
			e.printStackTrace();
		} 
		
	}

}
