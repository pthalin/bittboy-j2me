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

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.sun.midp.installer.ManifestProperties;
import com.sun.midp.midletsuite.MIDletInfo;

abstract class AbstractJarInspector  {

	protected ManifestProperties manifestProperties;
	
	public abstract InputStream getManifest() throws IOException;
	
	//http://developers.sun.com/techtopics/mobility/midp/ttips/getAppProperty/index.html
	//http://www.onjava.com/pub/a/onjava/2001/04/26/midlet.html
	public ManifestProperties getManifestProperties() throws IOException {
		if (manifestProperties == null) {
			InputStream is = getManifest();
			manifestProperties = new ManifestProperties();
			manifestProperties.load(is);
			is.close();
		}
		return manifestProperties;
	}
	
	public String getSuiteName() throws IOException {
		return getManifestProperties().getProperty(ManifestProperties.SUITE_NAME_PROP);
	}
	
	public MIDletInfo[] getMIDletInfo() throws IOException {

		String midlet = null;
		MIDletInfo midletInfo = null;
		Vector infoList = new Vector();

		for (int i = 1;; i++) {
			midlet = getManifestProperties().getProperty("MIDlet-" + i);
			if (midlet == null) {
				break;
			}

			/*
			 * Verify the MIDlet class is present in the JAR
			 * An exception thrown if not.
			 * Do the proper install notify on an exception
			 */

			midletInfo = new MIDletInfo(midlet);
			infoList.addElement(midletInfo);
			//verifyMIDlet(midletInfo.classname);

		}

		MIDletInfo[] infos = new MIDletInfo[infoList.size()];
		for (int j = 0; j < infoList.size(); j++) {
			infos[j] = (MIDletInfo) infoList.elementAt(j);
		}

		return infos;
	}
	
	
}
