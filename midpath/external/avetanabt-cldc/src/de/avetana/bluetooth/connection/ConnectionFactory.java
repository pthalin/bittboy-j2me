package de.avetana.bluetooth.connection;

import java.util.Hashtable;
import java.util.Vector;

import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;

/**
 * The class used to manage connections at the application level.
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
 * One of the request of the JSR82 specification is to be able to test if a device is connected or not.
 * For example the method javax.bluetooth.RemoteDevice.authenticate() must throw an exception if it is called
 * for a remote device which is not connected with any local device. There is two ways of handling such a
 * request:
 * <ul>
 * <li>To let the stack make all requested tests</li>
 * <li>To introduce a new test possibility at the application level </li>
 * </ul>
 * The second solution was choosed because it is operating system independant and moreover, because it allows
 * to better manage the error cases.<br>
 * The task of the ConnectionFactory class is therefore to store all existing client and server connections but also
 * to be able to identify the DiscoveryListener associated with a specified device.
 *
 * @author Julien Campana
 */

public class ConnectionFactory {

  /**
   * Vector used to store all connection objects.
   */
  private Vector m_connections;

  /**
   * Vector used to store all connection notifiers
   */
  private Vector m_notifiers;

  /**
   * Hashtable used to store all DiscoveryListener. The key values of this hashtable are String objects representing
   * BT device address.
   */

  private Hashtable m_sdpListeners;

  /**
   * Creates a new ConnectionFactory object. Initialize all encapsulated variables.
   */
  public ConnectionFactory() {
    m_connections=new Vector();
    m_notifiers=new Vector();
    m_sdpListeners = new Hashtable();
  }

  /**
   * Adds a connection to the list of registered connections
   * @param desc The unregistered connection
   */
  public synchronized void addConnection(BTConnection desc) {
    m_connections.addElement(desc);
  }

  /**
   * Returns the list of all registered connections.
   * @return A vector storing all registered connections
   */
  public synchronized Vector getConnections() {return m_connections;}

  /**
   * Returns the connection for a given fid
   * 
   * @param fid
   * @return
   */
  public synchronized BTConnection getConnectionForFID(int fid) {
  	for (int i = 0;i < m_connections.size();i++) {
  		if (((BTConnection)m_connections.elementAt(i)).getConnectionID() == fid) return (BTConnection)m_connections.elementAt(i);
  		
  	}
  		
  	return null;
  }
  
  /**
   * Returns the list of all registered notifiers.
   * @return A vector storing all registered notifiers
   */
  public synchronized Vector getNotifiers() {return m_notifiers;}

  /**
   * Unregisters a Connection
   * @param desc The registered connection
   * @return <code>true</code> - if the connection was successfully unregistered<br>
   *         <code>false</code> - Otherwise.
   */
  public synchronized boolean removeConnection(BTConnection desc) {
    return m_connections.removeElement(desc);
  }

  /**
   * Registers a new connection notifier
   * @param desc A new and unregistered connection notifier
   */
  public synchronized void addNotifier(ConnectionNotifier desc) {
    m_notifiers.addElement(desc);
  }

  /**
   * Unregisters a Connection notifier
   * @param desc The registered connection notifier
   * @return <code>true</code> - if the connection notifier was successfully unregistered<br>
   *         <code>false</code> - Otherwise.
   */
  public synchronized boolean removeNotifier(ConnectionNotifier desc) {
	  return m_notifiers.removeElement(desc);
  }

  /**
   * Unregisters the Connection identified by its connection ID
   * @param fid The ID of the registered connection
   * @return <code>true</code> - if the connection was successfully unregistered<br>
   *         <code>false</code> - Otherwise.
   */
  public synchronized boolean removeConnection(int fid) {
    for(int i = 0 ; i < m_connections.size() ; i++) {
      BTConnection desc=(BTConnection)m_connections.elementAt(i);
      if(fid==desc.getConnectionID()) return removeConnection(desc);
    }
    return false;
  }

  /**
   * Registers a new DiscoveryListener.
   * @param jbdAddr The string representation of the BT address of the remote device
   * @param list The Discoverylistener associated with this remote device
   */

  public synchronized void addListener(int transactionID, DiscoveryListener list) {
    m_sdpListeners.put(new Integer(transactionID), list);
  }

  public synchronized boolean isListener(int transID) {
    return m_sdpListeners.contains(new Integer(transID));
  }

  public synchronized void removeListener(int transID) {
    m_sdpListeners.remove(new Integer(transID));
  }

  /**
   * Returns the DiscoveryListener associated with a remote device
   * @param jbdAddr The string representation of the BT address of the remote device
   * @return The DiscoveryListener associated with a remote device
   */
  public synchronized DiscoveryListener getListener(int id) {
    return (DiscoveryListener)m_sdpListeners.get(new Integer (id));
  }

  /**
   * Tells if a remote device is currently connected with a local device
   * @param dev The remote device
   * @return <code>The BTConnection object</code> - If the remote device is currently connected
   *         <code>null</code> - If the remote device is not connected.
   */
  public synchronized BTConnection isConnected(RemoteDevice dev) {
    for(int i = 0 ; i < m_connections.size() ; i++) {
      BTConnection desc=(BTConnection)m_connections.elementAt(i);
      if(dev.equals(desc.m_remote)) return desc;
    }
    return null;
  }

}