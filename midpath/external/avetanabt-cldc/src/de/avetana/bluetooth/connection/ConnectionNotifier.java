package de.avetana.bluetooth.connection;

import java.io.IOException;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.microedition.io.Connection;

import de.avetana.bluetooth.sdp.LocalServiceRecord;
import de.avetana.bluetooth.sdp.RecordOwner;
import de.avetana.bluetooth.stack.BlueZ;
import de.avetana.bluetooth.util.Debug;


/**
 * The top-level connection notifier class.
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
 * The top-level class for connection notifiers.<br>
 * Depending on the protocol, connection notifiers can differently implement the method <i>AcceptAndOpen()</i> but
 * if only some method implementations differ, the principles of a JSR82 connection notifier remain for each protocol:
 * a service record, a connection ID, a connection URL and a remote device. This class encapsulates the common properties
 * of JSR82 notifiers and is therefore the class used in the ConnectionFactory.
 *
 * @author Julien Campana
 */

public abstract class ConnectionNotifier implements Connection, RecordOwner {

  /**
   * When registering of a connection fails, the reason is given here
   */
  
  protected IOException failEx = null;

  /**
   * The service record handle
   */
  protected long m_serviceHandle=-1;

  /**
   * The local service record
   */
  protected ServiceRecord myRecord;

  /**
   * Is the connection with the remote device closed?
   */
  protected boolean isClosed=false;

  /**
   * The connection URL used to create the local service
   */
  protected JSR82URL parsedURL;

  /**
   * The remote device
   */
  protected RemoteDevice m_remote;
  
  /**
   * Used to transport the connectionID from BlueZ Notification to acceptAndOpenI;
   */
  private int m_fid;

  /**
   * Sets the connection ID
   * @param fid The connection ID
   */
  public synchronized void setConnectionID(int fid) {
    this.m_fid = fid;
    notifyAll();
  }
  
  public void setFailure (IOException e) {
  	this.failEx = e;
  }

  /**
   * Sets the remote device
   * @param addr The BT address of the remote device
   */
  public void setRemoteDevice(String addr) {
    m_remote=new RemoteDevice(addr);
  }

  /**
   * Returns the service record handle
   * @return The service record handle
   */
  public long getServiceHandle() {
      return this.m_serviceHandle;
  }

  /**
   * Returns the connection URL
   * @return The connection URL
   */
  public JSR82URL getConnectionURL() {
    return parsedURL;
  }
  
  /**
   * Return the remote connections end
   * @return
   */
  public RemoteDevice getRemoteDevice() {
  	return m_remote;
  }

  /**
   * Returns the local service record
   * @return The local service record
   */
  public ServiceRecord getServiceRecord() {
      return myRecord;
  }

  /**
   * Gets the registration state of the local service
   * @return <code>true</code> - If the service is registered<br>
   *         <code>false</code> - Otherwise
   */
  public boolean isServiceRegistered() {
      return (m_serviceHandle!=-1);
  }

  /**
   * Gets the state of the notifier
   * @return <code>true</code> - If the notifier is closed
   *         <code>false</code> - Otherwise.
   */
  public boolean isNotifierClosed() {
      return isClosed;
  }

  /**
   * Sets the local service record.<br>
   * BEWARE: this method does not update the service reocrd in the BCC
   * @param a_record The new loca lservice record
   * @throws Exception ClassCastException - If the new local service record is not an instance of LocalServiceRecord
   */
  public void setServiceRecord(ServiceRecord a_record) throws Exception {
    if(!(a_record instanceof LocalServiceRecord)) throw new ClassCastException("This implementation only uses LocalServiceRecord objects!");
    this.myRecord=a_record;
  }

  /**
   * Remove the notifier and the SDP Record
   */
  
  public void removeNotifier() {
  	//System.out.println ("Removing notifier " + isClosed + " " + m_serviceHandle);
  	if (isClosed) return;
    try {
      BlueZ.myFactory.removeNotifier(this);
      if (m_serviceHandle != 0) BlueZ.disposeLocalRecord(m_serviceHandle);
      m_serviceHandle = 0;
    } catch (Exception e) {}
  }
  /**
   * Closes the connection NOTIFIER (and not the connection itself) .
   */
  
  
  public synchronized void close() {
  	if (isClosed) return;
  	removeNotifier();
  	isClosed=true;
  	m_fid = 0;
  	notifyAll();
  }
  

  /**
   * Waits for a client to connect to this service. This method is called from the subClasses acceptAndOpen method
   */
  
  protected synchronized int acceptAndOpenI() throws IOException, ServiceRegistrationException {
 
	if (isClosed) throw new IOException ("Notifier has been closed.");
    m_fid = -2;
  	failEx = null;
  	isClosed = false;
    try {
      BlueZ.registerNotifier(this);
    }catch(Exception ex) {
    	  m_fid = 0;
      throw new IOException("ERROR - Unable to register the local Service Record!");
    }

    Debug.println(1, "ConnectionNofifier::Starting to wait");
     while(m_fid==-2) {
        try {
        	wait(1000); 
        	Debug.println(1, "ConnectionNofifier::In Wait loop " + m_fid + " " + (System.currentTimeMillis() % 10000));
        }catch(Exception ex) { ex.printStackTrace(); }
     }
     Debug.println(1, "ConnectionNofifier::Connection established " + m_fid);
     
	 BlueZ.removeNotifier(this);

	 Debug.println(1, "ConnectionNofifier::Notifier removed");

     if (m_fid <= 0) {
 		close();
 		if (failEx != null) throw failEx;
 		throw new IOException ("Service Revoked");
     }
     
     return m_fid;
  	}
}