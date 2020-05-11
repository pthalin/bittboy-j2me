package de.avetana.bluetooth.l2cap;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;

import de.avetana.bluetooth.connection.BadURLFormat;
import de.avetana.bluetooth.connection.ConnectionNotifier;
import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.sdp.LocalServiceRecord;
import de.avetana.bluetooth.stack.BlueZ;

/**
 * The implementation of the javax.bluetooth.L2CAPConnectionNotifier class.
 *
 * <br><br>
 * <b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
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
 * The <code>L2CAPConnectionNotifierImpl</code> class provides an implementation of the javax.bluetooth.L2CAPConnectionNotifier
 * interface. <P>
 * To create a server connection, the protocol must be <code>btl2cap</code>. The target contains "localhost:" and the UUID of
 * the service. The parameters are ReceiveMTU and  TransmitMTU, the same parameters
 * used to define a client connection. Here is an example of a valid server connection string:<BR>
 * <code>btl2cap://localhost:3B9FA89520078C303355AAA694238F07;ReceiveMTU=512;TransmitMTU=512</code><BR> <P>
 * A call to Connector.open() with this string will return a <code>javax.bluetooth.L2CAPConnectionNotifier</code> instance. An
 * <code>L2CAPConnection</code> object is obtained from the <code>L2CAPConnectionNotifier</code> by calling the method
 * <code>acceptAndOpen()</code>.
 */
public class L2CAPConnectionNotifierImpl extends ConnectionNotifier implements L2CAPConnectionNotifier{

	private int m_transMTU = 672;
	private int m_recMTU = 672;
	
  /**
   * Creates a new instance of L2CAPConnectionNotifierImpl. The connection URL be of the form: btl2cap://localhost:UUID
   * @param url The connection URL
   * @throws BadURLFormat If the connection URL does not describe a well-formed and correct L2CAP server URL.
   * @throws Exception
   */
  public L2CAPConnectionNotifierImpl(JSR82URL url) throws BadURLFormat, Exception {
    parsedURL=url;
    if(parsedURL.getBTAddress()!=null) throw new BadURLFormat("This is not an sdp server URL!");
    String m_serviceName=(String)parsedURL.getParameter("name");
    m_serviceName=(m_serviceName==null?"Avetana Service":m_serviceName);
    myRecord=LocalServiceRecord.createSerialSvcRecord(new UUID(url.getLocalServiceUUID(), false), m_serviceName,
        parsedURL.getAttrNumber(), parsedURL.getProtocol());
    if(myRecord==null) throw new Exception("Not a valid Service Record!!!!!");

    ((LocalServiceRecord)myRecord).setRecordOwner(this);
    
    try {
        m_serviceHandle=BlueZ.createService((LocalServiceRecord)myRecord);
        if(m_serviceHandle < 0) throw new Exception();
        myRecord.setAttributeValue(0x0, new DataElement(DataElement.U_INT_4, new Long(m_serviceHandle).longValue()));
        int psm = (int)((LocalServiceRecord)myRecord).getChannelNumberElement().getLong();
        this.parsedURL.setAttrNumber(psm);
      }catch(Exception ex) {
        throw new ServiceRegistrationException("ERROR - The service record could not be added in the local SDB!");
      }

  }

  /**
   * Waits for a client to connect to this L2CAP service. Upon connection returns an <code>L2CAPConnection</code>
   * that can be used to communicate with this client. <P> A service record associated with this connection will be
   * added to the SDDB associated with this <code>L2CAPConnectionNotifier</code> object if one does not
   * exist in the SDDB.  This method will put the local device in connectable mode so that it may respond to
   * connection attempts by clients. <P> The following checks are done to verify that any
   * modifications made by the application to the service record
   * after it was created by <code>Connector.open()</code> have not
   * created an invalid service record.  If any of these checks
   * fail, then a <code>ServiceRegistrationException</code> is thrown. <UL>
   * <LI>ServiceClassIDList and ProtocolDescriptorList, the mandatory
   * service attributes for a <code>btl2cap</code> service record, must be present in the service record.
   * <LI>L2CAP must be in the ProtocolDescriptorList. <LI>The PSM value must not have changed in the service record. </UL>
   * <P> This method will not ensure that the service record created
   * is a completely valid service record. It is the responsibility
   * of the application to ensure that the service record follows
   * all of the applicable syntactic and semantic rules for service record correctness.
   * @return a connection to communicate with the client
   * @exception IOException if the notifier is closed before <code>acceptAndOpen()</code> is called
   * @exception ServiceRegistrationException if the structure of the
   * associated service record is invalid or if the service record could not be added successfully to the local SDDB.  The
   * structure of service record is invalid if the service record is missing any mandatory service attributes, or has
   * changed any of the values described above which are fixed and
   * cannot be changed. Failures to add the record to the SDDB could be due to insufficient disk space, database locks, etc.
   * @exception BluetoothStateException if the server device could
   * not be placed in connectable mode because the device user has configured the device to be non-connectable.
   */
  public synchronized L2CAPConnection acceptAndOpen() throws IOException, ServiceRegistrationException {
	int m_fid = super.acceptAndOpenI();

      L2CAPConnectionImpl myConnection=new L2CAPConnectionImpl(m_fid);
      ((L2CAPConnectionImpl)myConnection).m_receiveMTU = m_recMTU;
      ((L2CAPConnectionImpl)myConnection).m_transmitMTU = m_transMTU;
      myConnection.setRemoteDevice(m_remote);
      ((L2CAPConnectionImpl)myConnection).setConnectionURL(parsedURL);
      m_fid = 0;
      return (L2CAPConnectionImpl)myConnection;

  }
  
  /**
   * This is used from JBlueZ to set the mtu on incoming connections
   */
  
  public void setMTUs (int transMTU, int recMTU) {
  	  m_transMTU = transMTU;
  	  m_recMTU = recMTU;
  }

}