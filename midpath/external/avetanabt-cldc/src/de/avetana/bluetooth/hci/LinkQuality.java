/*
 *
 * $Id: LinkQuality.java,v 1.1 2005/06/30 14:23:33 markinho Exp $
 *
 * Created on 29.06.2005
 *
 */
package de.avetana.bluetooth.hci;

import de.avetana.bluetooth.stack.BlueZ;
import de.avetana.bluetooth.stack.BlueZException;
import de.avetana.bluetooth.util.BTAddress;

/**
 *
 * The class LinkQuality is used to get the quality of a connection
 * to a currently connected device.
 *
 * <p> 
 * <b>Last CVS update:</b> $Date: 2005/06/30 14:23:33 $
 * 
 * @author $Author: markinho $
 * @author markinho ( 1st Version )
 * @version $Revision: 1.1 $
 * 
 *
 */
public class LinkQuality {

	public static final int NOT_CONNECTED = 0x100;
	public static final int NOT_IMPLEMENTED = 0x101;
	/**
	* Return the connection quality of a device currently connected
	*
	* @param adr
	* @return 0 < n <  255 for the connection quality ( 255 is best) 
	*         NOT_CONNECTED if the device is not connected or 
	*         NOT_IMPLEMENTED if no linkquality value is available.
	*/
	public static int getLinkQuality (BTAddress adr) {
		try {
			return BlueZ.getLinkQuality (adr.toString());
		} catch (BlueZException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return NOT_CONNECTED;
	}
}

