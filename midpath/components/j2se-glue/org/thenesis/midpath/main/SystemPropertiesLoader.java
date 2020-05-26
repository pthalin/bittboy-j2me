package org.thenesis.midpath.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class SystemPropertiesLoader {

	public SystemPropertiesLoader() throws IOException {

		/* Load system properties required by MIDP2 and JSR specs */
		
		// Load property file
		Properties properties = new Properties();
		InputStream is = MIDletLauncherSE.class.getResourceAsStream("/com/sun/midp/configuration/system_properties");
		if (is == null) {
			throw new IOException("Can't find system_properties file");
		}
		properties.load(is);
		
		// Set system properties
		Enumeration e = properties.keys();
		while(e.hasMoreElements()) {
			String key = (String)e.nextElement();
			String value = properties.getProperty(key);
			
			// Fix for the Sun JDK. FIXME: why ?
			String javaVendor = System.getProperty("java.vendor");
			if ((javaVendor != null) && (javaVendor.indexOf("Sun") != -1)) {
				if (key.equals("microedition.locale")) {
					continue;
				}
			}
			
			System.setProperty(key, value);
		}
		
		//System.getProperties().list(System.out);

	}

}
