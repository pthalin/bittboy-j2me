package de.avetana.bluetooth.stack;

import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;

import de.avetana.bluetooth.connection.BTConnection;
import de.avetana.bluetooth.connection.JSR82URL;

/**
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
* <dd>This abstract class defines the methods used by the implementation of javax.bluetooth.* to access
* the native and system dependant methods. The class BluezStack provides an example of how to implement this
* class. This strategy may look strange but the aim is to define a general framework independant of the
* stack implementation. <br>
* That way, you can define your own stack implementation, which will extend (for example) the functionnalities
* of the class BluezStack.
*
* @see de.avetana.bluetooth.stack.BluezStack
* @see de.avetana.bluetooth.stack.BlueZ
* @author Julien Campana
*/


public abstract class BluetoothStack {

  private static BluetoothStack m_stack;

  /**
   * This constant defines the array-position of the property <i>Accept</i>.
   * Indeed, the method getConnectionOptions() returns an array of boolean describing the properties set
   * for a specified connection and its related remote device.
   *
   */
  public final static int ACCEPT_POS=0;

  /**
   * This constant defines the array-position of the property <i>Master</i>.
   * Indeed, the method getConnectionOptions() returns an array of boolean describing the properties set
   * for a specified connection and its related remote device.
   *
   */
  public final static int CON_MASTER_POS=1;

  /**
   * This constant defines the array-position of the property <i>Authenticate</i>.
   * Indeed, the method getConnectionOptions() returns an array of boolean describing the properties set
   * for a specified connection and its related remote device.
   */
  public final static int CON_AUTH_POS=2;

  /**
   * This constant defines the array-position of the property <i>Encrypt</i>.
   * Indeed, the method getConnectionOptions() returns an array of boolean describing the properties set
   * for a specified connection and its related remote device.
   *
   */
  public final static int CON_ENCRYPT_POS=3;

  /**
   * This constant defines the array-position of the property <i>Trusted</i>.
   * Indeed, the method getConnectionOptions() returns an array of boolean describing the properties set
   * for a specified connection and its related remote device.
   */
  public final static int CON_TRUSTED_POS=4;

  /**
   * Returns the instance of BluetoothStack currently used. The BluetoothStack object must
   * be intiliazed with the help of the method init().
   * @return The current BluetoothStack
   * @throws Exception If the BluetoothStack was not initialized
   */
  public static BluetoothStack getBluetoothStack() throws Exception {
    if (m_stack == null) m_stack = new AvetanaBTStack();
    return m_stack;
  }

  /**
   * Returns the name of the local device.
   * @return The name of the local device
   * @throws Exception
   * @see javax.bluetooth.LocalDevice
   */
  public abstract String getLocalDeviceName() throws Exception;

  /**
   * Returns the class of the local device.
   * @return The class of the local device
   * @throws Exception
   * @see javax.bluetooth.LocalDevice
   */
  public abstract int getClassOfDevice() throws Exception;

  /**
   * Returns the local device address.
   * @return The local device address
   * @throws Exception
   * @see javax.bluetooth.LocalDevice
   */
  public abstract String getLocalDeviceAddress() throws Exception;

  /**
   * Returns the name of the remote device.
   * @param bd_addr The BT address of the remote device
   * @return The name of the remote device if available
   * @throws Exception
   */
  public abstract String getRemoteName(String bd_addr) throws Exception;

  /**
   * Opens a new RFCOMM client connnection.
   * @param url The connection URL
   * @return A StreamConnection object.
   * @throws Exception
   */
  public abstract Connection openRFCommConnection(JSR82URL url, int timeout) throws Exception;

  /**
   * Opens a new L2CAP client connection.
   * @param url The connection URL
   * @return A L2CAPConnection object
   * @throws Exception
   */
  public abstract Connection openL2CAPConnection(JSR82URL url, int timeout) throws Exception;

  /**
   * Searches for desired services.
   * @param attrSet The list of attributes to be considered for each found service.
   * @param uuidSet The list of UUIDs a service MUST contain.
   * @param btDev The remote device
   * @param myListener The discovery listener for callback methods.
   */
  public abstract int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener myListener);

  /**
   * Stops a service search.
   * @param transID The SDP transaction ID.
   */
// TransactionID - changed to boolean
  public abstract boolean cancelServiceSearch(int transID);

  /**
   * Updates a Service record stored in the local BCC.
   * @param b The new service description represented as a byte array
   * @param recordHandle The record handle of the old service
   * @return a positive integer if the update succeeds
   * @throws Exception
   * @see javax.bluetooth.LocalDevice
   */
  public abstract int updateService(ServiceRecord rec, long recordHandle) throws Exception;

  /**
   * Returns the discoverable mode of the local device.
   * @return The discoverable mode of the local device
   * @throws Exception
   * @see javax.bluetooth.LocalDevice
   */
  public abstract int getDiscoverableMode() throws Exception;

  /**
   * Sets the new discoverable mode for this local device.
   * @param mode a value in the range 0x9E8B00 to 0x9E8B3F
   * @return a positive integer if the operation succeeds.
   * @throws Exception
   * @see javax.bluetooth.LocalDevice
   */
  public abstract int setDiscoverableMode(int mode) throws Exception;

  /**
   * Returns <code>true</code> if the remote device is currently connected with the local device
   * (whatever the type of connection: server an client connections are here concerned)
   * @param dev The remote device
   * @return The active Connection object
   */
  public abstract BTConnection isConnected(RemoteDevice dev);

  /**
   * Asks the remote device to perform an authentication. The remote device MUST be connected with the local
   * device
   * @param dev The remote device
   * @return <code> 1 </code> If the authentication did succeed.<br>
   *         A negative integer otherwise.
   * @throws Exception
   * @see javax.bluetooth.RemoteDevice
   */
  public abstract int authenticate(RemoteDevice dev) throws Exception;

  /**
   * Turns on/off the encryption of an ACL link
  *  @param conn The connection object representing the connection beteen the local and remote device
   * @param dev The remote device for this connection
   * @param encrypt If <code>true</code>, turn on the encryption of the ACL link. Otherwise, turn it off
   * @return <code> 1 </code> If the operation did succeed.<br>
   *         A negative integer otherwise.
   * @throws Exception
   * @see javax.bluetooth.RemoteDevice
   */
  public abstract int encrypt(Connection conn, RemoteDevice dev, boolean encrypt) throws Exception;

  /**
   * Gets the property for a connection.
   * @param dev The remote device
   * @param pos The position of the desired flag in the array of properties
   * @return <code>true</code> If the property is true.<br>
   *         <code>false</code> Otherwise.
   * @throws Exception
   * @see javax.bluetooth.RemoteDevice
   * @see de.avetana.bluetooth.stack.BluetoothStack#ACCEPT_POS
   * @see de.avetana.bluetooth.stack.BluetoothStack#CON_AUTH_POS
   * @see de.avetana.bluetooth.stack.BluetoothStack#CON_ENCRYPT_POS
   * @see de.avetana.bluetooth.stack.BluetoothStack#CON_MASTER_POS
   * @see de.avetana.bluetooth.stack.BluetoothStack#CON_TRUSTED_POS
   */
  public abstract boolean getConnectionFlag(RemoteDevice dev, int pos) throws Exception;

  /**
   * This method is deprecated
   * @param stack
   * @throws Exception
   * at deprecated
   */
  public static void init (BluetoothStack stack) {//throws Exception {
    m_stack = stack;
//    System.err.println ("No need to call BluetoothStack.init() anymore");
//    new Throwable().printStackTrace();
  }
}
