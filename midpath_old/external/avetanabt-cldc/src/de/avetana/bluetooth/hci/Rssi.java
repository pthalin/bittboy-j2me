/*
 * Created on 26.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.hci;

import de.avetana.bluetooth.stack.BlueZ;
import de.avetana.bluetooth.stack.BlueZException;
import de.avetana.bluetooth.util.BTAddress;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Rssi {

	public static final int NOT_CONNECTED = 0x100;
	public static final int NOT_IMPLEMENTED = 0x101;
		/**
		 * Return the signal strength of a device currently connected.
		 * 
		 * @param adr
		 * @return -13 < n <  13 for the connection quality ( 0 is best), NOT_CONNECTED if the device is not connected or NOT_IMPLEMENTED if no rssi value is available.  
		 */
	public static int getRssi (BTAddress adr) {
		try {
			return BlueZ.getRssi (adr.toString());
		} catch (BlueZException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return NOT_CONNECTED;
	}
}
