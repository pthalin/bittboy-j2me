package de.avetana.bluetooth.rfcomm;

import java.io.IOException;

import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import de.avetana.bluetooth.connection.BadURLFormat;
import de.avetana.bluetooth.connection.ConnectionNotifier;
import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.sdp.LocalServiceRecord;
import de.avetana.bluetooth.stack.BlueZ;

/**
 * The class used to manage RFCOMM or RFCOM-based server connection.
 *
 * <br><br><b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
 *
 * This file is part of the Avetana bluetooth API for Linux.<br><br>
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version. <br><br>
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.<br><br><br><br>
 *
 *
 * <b>Description:</b><br>
 * This class extends the class ConnectionNotifier and implements, as requested by the JSR82 specification
 * the interface StreamConnectionNotifier. Typically, this class will be used to manage RFCOMM or RFCOMM-based
 * server connections
 *
 * @see de.avetana.bluetooth.connection.ConnectionNotifier
 * @author Julien Campana
 */

public class RFCommConnectionNotifierImpl extends ConnectionNotifier implements StreamConnectionNotifier {

	/**
	 * Creates a new LocalConnectionNotifier object and therefore a new Local service record.
	 * @param url The server connection URL
	 * @throws BadURLFormat If the format of the URL is no correct
	 * @throws Exception If the service record is not valid.
	 */
	public RFCommConnectionNotifierImpl(JSR82URL url) throws BadURLFormat, Exception {
		parsedURL = url;
		if (parsedURL.getBTAddress() != null)
			throw new BadURLFormat("This is not an sdp server URL!");
		String m_serviceName = (String) parsedURL.getParameter("name");
		m_serviceName = (m_serviceName == null ? "Avetana Service" : m_serviceName);
		myRecord = LocalServiceRecord.createSerialSvcRecord(new UUID(url.getLocalServiceUUID(), false), m_serviceName,
				parsedURL.getAttrNumber(), parsedURL.getProtocol());
		if (myRecord == null)
			throw new Exception("Not a valid Service Record!!!!!");

		((LocalServiceRecord) myRecord).setRecordOwner(this);
		try {
			m_serviceHandle = BlueZ.createService((LocalServiceRecord) myRecord);
			if (m_serviceHandle < 0)
				throw new Exception();
			myRecord
					.setAttributeValue(0x0, new DataElement(DataElement.U_INT_4, new Long(m_serviceHandle).longValue()));
			int channel = (int) ((LocalServiceRecord) myRecord).getChannelNumberElement().getLong();
			this.parsedURL.setAttrNumber(channel);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new IOException("ERROR - The service record could not be added in the local SDB " + m_serviceHandle
					+ " " + ex.getMessage());
		}

	}

	/**
	 * Waits for a client to connect to this RFCOMM service. Upon connection returns an <code>StreamConnection</code>
	 * that can be used to communicate with this client. <P> A service record associated with this connection will be
	 * added to the SDDB associated with this <code>ConnectionNotifer</code> object if one does not
	 * exist in the SDDB.
	 * @return the opened stream connection
	 * @throws IOException
	 */
	public synchronized StreamConnection acceptAndOpen() throws IOException {
		int m_fid = super.acceptAndOpenI();

		RFCommConnectionImpl myConnection = new RFCommConnectionImpl(m_fid);
		myConnection.setRemoteDevice(m_remote);
		m_fid = 0;
		return (StreamConnection) myConnection;

	}
}