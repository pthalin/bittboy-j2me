/**
*  (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED.
*
* This file is part of the Avetana bluetooth API for Linux.
*
* The Avetana bluetooth API for Linux is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2 of
* the License, or (at your option) any later version.
*
* The Avetana bluetooth API is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* The development of the Avetana bluetooth API is based on the work of
* Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
* on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
* on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
* Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
* are explicitly mentioned.
*
* @author Julien Campana
*/

package javax.bluetooth;

import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnectionNotifier;

import de.avetana.bluetooth.connection.BTConnection;
import de.avetana.bluetooth.l2cap.L2CAPConnectionNotifierImpl;
import de.avetana.bluetooth.obex.OBEXConnection;
import de.avetana.bluetooth.obex.SessionHandler;
import de.avetana.bluetooth.obex.SessionNotifierImpl;
import de.avetana.bluetooth.rfcomm.RFCommConnectionNotifierImpl;
import de.avetana.bluetooth.stack.BluetoothStack;
import de.avetana.bluetooth.util.BTAddress;

public class RemoteDevice {
    private String bdAddrString;
    private long bdAddrLong;
    private byte pageScanRepMode;
    private byte pageScanPeriodMode;
    private byte pageScanMode;
    private DeviceClass deviceClass;
    private short clockOffset;
    private String friendlyName;
    private Hashtable serviceRecords;
    private boolean isAuth=false, isEncrypt;

    /**
     * Notifies the method to call <code>wait()</code> for a response from the
     * native code.  Used by the <code>securityResult</code> variable in the
     * <code>authenticate()</code>, <code>encrypt()</code>, and <code>authorize()</code> methods. <P>
     * The value of  <code>WAIT</code> is 0x00 (0).
     */
    private static final int WAIT = 0;

    /**
     * Notifies the method that the request succeeded.  Used by the <code>securityResult</code> variable in the
     * <code>authenticate()</code>, <code>encrypt()</code>, and <code>authorize()</code> methods. <P>
     * The value of <code>SUCCEEDED</code> is 0x01 (1).
     */
    private static final int SUCCEEDED = 1;


    public RemoteDevice(String bdAddrString, byte repMode, byte periodMode, byte scanMode, short clockOffset) throws NullPointerException, IllegalArgumentException {
      this(bdAddrString.toUpperCase());
      this.pageScanRepMode=repMode;
      this.pageScanPeriodMode=periodMode;
      this.pageScanMode=scanMode;
      this.clockOffset=clockOffset;
    }

    public RemoteDevice(String bdAddrString, byte repMode, byte periodMode, byte scanMode, short clockOffset, int deviceClass) throws NullPointerException, IllegalArgumentException {
      this (bdAddrString, repMode, periodMode, scanMode, clockOffset);
      this.deviceClass = new DeviceClass (deviceClass);
    }

    public RemoteDevice(String bdAddrString, byte repMode, byte periodMode, byte scanMode, short clockOffset, int deviceClass, String name) throws NullPointerException, IllegalArgumentException {
    		this (bdAddrString, repMode, periodMode, scanMode, clockOffset, deviceClass);
    		this.friendlyName = name;
    }


    /**
     * Creates a Bluetooth device based upon its address.  The Bluetooth
     * address must be 12 hex characters long.  Valid characters are 0-9, a-f,
     * and A-F.  There is no preceding "0x" in the string.  For example, valid
     * Bluetooth addresses include but are not limited to:<BR> <code>008037144297</code><BR> <code>00af8300cd0b</code><BR>
     * <code>014bd91DA8FC</code>
     * @param address  the address of the Bluetooth device as a 12 character hex string
     * @exception NullPointerException if <code>address</code> is <code>null</code>
     * @exception IllegalArgumentException if <code>address</code> is the
     * address of the local device or is not a valid Bluetooth address
     */
    public RemoteDevice(String address) throws NullPointerException, IllegalArgumentException{
      if(address==null) throw new NullPointerException("Bluetooth Address is null!");
      try {
        String tmp="";
        address = address.replace('-', ':').toUpperCase();
        if(address.length()>12) {
        		tmp = "" + address.substring(0, 2) + address.substring(3, 5) + address.substring(6,8) + address.substring(9, 11) + address.substring(12, 14) + address.substring(15);
        } else tmp=address;
        //System.out.println("address="+tmp);
        this.bdAddrString=tmp;
        this.bdAddrLong=Long.parseLong(tmp, 16);
      }
      catch(Exception ex) {throw new IllegalArgumentException("Not a valid bluetooth address");}
    }

    /**
     * Creates a Bluetooth device based upon its address.  For example, valid Bluetooth addresses include but are not limited
     * to:<BR> <code>008037144297</code><BR> <code>00af8300cd0b</code><BR> <code>014bd91DA8FC</code>
     * @param address  the address of the Bluetooth device as a long
     * @exception IllegalArgumentException if <code>address</code> is the
     * address of the local device or is not a valid Bluetooth address
     */
    public RemoteDevice(long address) {
        this.bdAddrLong = address;
        bdAddrString = Long.toString(address, 16).toUpperCase();
        while (bdAddrString.length() < 12) { bdAddrString = "0" + bdAddrString; }
        this.serviceRecords = new Hashtable();
    }

    /**
     * Determines if this is a trusted device according to the BCC.
     * @return <code>true</code> if the device is a trusted device, otherwise <code>false</code>
     */
    public boolean isTrustedDevice() {
      BluetoothStack bluetoothStack=null;
      try {
        bluetoothStack = BluetoothStack.getBluetoothStack();
        return bluetoothStack.getConnectionFlag(this,BluetoothStack.CON_TRUSTED_POS);
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      return false;
    }

    /**
     * Returns the name of this device. The Bluetooth specification calls this name the "Bluetooth device name" or the
     * "user-friendly name".  This method will only contact the remote device if the name is not known or
     * <code>alwaysAsk</code> is <code>true</code>.
     * @param alwaysAsk  if <code>true</code> then the device will be
     * contacted for its name, otherwise, if there exists a known name for this device, the name will be returned without
     * contacting the remote device
     * @return the name of the device, or <code>null</code> if the
     * Bluetooth system does not support this feature; if the local device
     * is able to contact the remote device, the result will never be
     * <code>null</code>; if the remote device does not have a name then an empty string will be returned
     * @exception IOException if the remote device can not be contacted or the remote device could not provide its name
     */
    public synchronized String getFriendlyName(boolean alwaysAsk) throws IOException {
        if ((friendlyName == null) || alwaysAsk) {
            try {
                BluetoothStack bluetoothStack = BluetoothStack.getBluetoothStack();
                String buffer = bluetoothStack.getRemoteName(bdAddrString);
                if (buffer != null) friendlyName = buffer;
            }
            catch (Exception e) { throw new IOException("Exception: RemoteDevice.getFriendlyName(): " + e); }
        }
        return friendlyName;
    }

    /**
     * Retrieves the Bluetooth address of this device.  The Bluetooth address
     * will be 12 characters long.  Valid characters are 0-9 and A-F.  This method will never return <code>null</code>.
     * @return the Bluetooth address of the remote device
     */
    public final String getBluetoothAddress() {
        if (bdAddrString == null) {
            bdAddrString = Long.toString(bdAddrLong, 16).toUpperCase();
            while (bdAddrString.length() < 12) { bdAddrString = "0" + bdAddrString; }
        }
        return bdAddrString;
    }

    public String toString() {
      try {
        return getBluetoothAddress();
      }catch(Exception ex) {ex.printStackTrace();}
      return "000000000000";
    }

    public String toStringWithName() {
      try {
        return getBluetoothAddress() +
            ((friendlyName!=null && !friendlyName.trim().equals(""))?(" - "+friendlyName):"");
      }catch(Exception ex) {ex.printStackTrace();}
      return "000000000000";
    }

    /**
     * BlueZ and Stollmann use Bluetooth Address, which are 17 characters long.
     * This method constructs and returns a BTAddress object, which easily converts
     * a 12 characters long BT address into a 17 characters long BT address.
     * This method is not part of the JSR82 Specification.
     * @return The BTAddress object constructed from the 12-characters BT address of a RemoteDevice object.
     * @throws Exception if the BTAddress could not be correctly constructed.
     */
    public BTAddress getBTAddress() throws Exception{
      return BTAddress.parseString(getBluetoothAddress());
    }

    /**
     * Computes the hash code for this object.  This method will return the
     * same value when it is called multiple times on the same object.
     * @return the hash code for this object
     */
    public int hashCode() { return (new Long(bdAddrLong)).hashCode(); }

    /**
     * Determines if two <code>RemoteDevice</code>s are equal.  Two devices are
     * equal if they have the same Bluetooth device address.
     * @param obj the object to compare to
     * @return <code>true</code> if both devices have the same Bluetooth
     * address; <code>false</code> if both devices do not have the same address;
     * <code>false</code> if <code>obj</code> is <code>null</code>; <code>false</code> if <code>obj</code> is not a
     * <code>RemoteDevice</code>
     */
    public boolean equals(Object obj) {
        try {
            RemoteDevice remote = (RemoteDevice)obj;
            return (this.getBluetoothAddress().equals(remote.getBluetoothAddress()));
        }
        catch (Exception e) { return false; }
    }

    /**
     * Retrieves the Bluetooth device that is at the other end of the Bluetooth
     * Serial Port Profile connection, L2CAP connection, or OBEX over RFCOMM
     * connection provided.  This method will never return <code>null</code>.
     * @param conn the Bluetooth Serial Port connection, L2CAP connection,
     * or OBEX over RFCOMM connection whose remote Bluetooth device is needed
     * @return the remote device involved in the connection
     * @exception IllegalArgumentException if <code>conn</code> is not a
     * Bluetooth Serial Port Profile connection, L2CAP connection, or OBEX over RFCOMM connection; if <code>conn</code> is a
     * <code>L2CAPConnectionNotifier</code>, <code>StreamConnectionNotifier</code>, or <code>SessionNotifier</code>
     * @exception IOException if the connection is closed
     * @exception NullPointerException if <code>conn</code> is <code>null</code>
     */
    public static RemoteDevice getRemoteDevice(Connection conn) throws IOException {
    	        if (conn == null) throw new NullPointerException("Connection is null.");
    	        RemoteDevice dev=null;
    			if (conn instanceof BTConnection) {
    				if (((BTConnection)conn).isClosed())
    					throw new IOException("Connection Closed");
    				dev = ((BTConnection)conn).getRemoteDevice();
    			} else if (conn instanceof SessionHandler && ((SessionHandler)conn).getStreamConnection() != null && ((SessionHandler)conn).getStreamConnection() instanceof BTConnection) dev = ((BTConnection)((SessionHandler)conn).getStreamConnection()).getRemoteDevice();
    			else if (conn instanceof OBEXConnection) dev = ((OBEXConnection)conn).getRemoteDevice();
    			else if (conn instanceof RFCommConnectionNotifierImpl || conn instanceof L2CAPConnectionNotifierImpl || conn instanceof SessionNotifierImpl) {
    					throw new IllegalArgumentException ();
    			}
    			else throw new ClassCastException ("Connection type not supported");
    	        if(dev==null) throw new IOException("The remote device could not be determined!");
    	        return dev;
    }

    /**
     * Attempts to authenticate this <code>RemoteDevice</code>.
     * Authentication is a means of verifying the identity of a remote
     * device. Authentication involves a device-to-device challenge and
     * response scheme that requires a 128-bit common secret link key
     * derived from a PIN code shared by both devices. If either side's
     * PIN code does not match, the authentication process fails and the
     * method returns <code>false</code>.  The method will also return
     * <code>false</code> if authentication is incompatible with the
     * current security settings of the local device established by the BCC, if the stack does not
     * support authentication at all, or if the stack does not support authentication subsequent to connection establishment.
     * <p> If this <code>RemoteDevice</code> has previously been authenticated, then this method returns <code>true</code>
     * without attempting to re-authenticate this <code>RemoteDevice</code>.
     * @return <code>true</code> if authentication is successful; otherwise <code>false</code>
     * @exception IOException if there are no open connections between the local device and this <code>RemoteDevice</code>
     */
    public boolean authenticate() throws IOException {
      BluetoothStack bluetoothStack=null;
      try {
       bluetoothStack = BluetoothStack.getBluetoothStack();
      }
      catch(Exception ex) {
         throw new IOException("The Bluetooth stack could not be initialized!!!");
      }
      if(this.isAuth) return true; //Was the authentication already done?
      int result=0;
      try {
        if((result=bluetoothStack.authenticate(this)) > 0) return true;
      }catch(Exception ex) {throw new IOException(ex.getMessage());}
      return false;
    }

    /**
     * Determines if this <code>RemoteDevice</code> should be allowed to continue to access the local service provided by the
     * <code>Connection</code>.  In Bluetooth, authorization is defined as the process of deciding if device X is allowed to
     * access service Y.  The implementation of the <code>authorize(Connection conn)</code> method asks the
     * Bluetooth Control Center (BCC) to decide if it is acceptable
     * for <code>RemoteDevice</code> to continue to access a local
     * service over the connection <code>conn</code>.  In devices with
     * a user interface, the BCC is expected to consult with the user to obtain approval.
     * <p> Some Bluetooth systems may allow the user to permanently
     * authorize a remote device for all local services. When a device
     * is authorized in this way, it is known as a "trusted device" -- see {@link #isTrustedDevice() isTrustedDevice()}.
     * <p> The <code>authorize()</code> method will also check that the
     * identity of the <code>RemoteDevice</code> can be verified through authentication.
     * If this <code>RemoteDevice</code> has been authorized for <code>conn</code> previously, then this method returns
     * <code>true</code> without attempting to re-authorize this <code>RemoteDevice</code>.
     * @see #isTrustedDevice
     * @param conn the connection that this <code>RemoteDevice</code> is using to access a local service
     * @return <code>true</code> if this <code>RemoteDevice</code> is successfully authenticated and authorized, otherwise
     * <code>false</code> if authentication or authorization fails
     * @exception IllegalArgumentException if <code>conn</code> is not
     * a connection to this <code>RemoteDevice</code>, or if the local
     * device initiated the connection, i.e., the local device is the
     * client rather than the server.  This exception is also thrown if
     * <code>conn</code> was created by <code>RemoteDevice</code> using a scheme other than <code>btspp</code>,
     * <code>btl2cap</code>, or <code>btgoep</code>. This exception
     * is thrown if <code>conn</code> is a notifier used by a server
     * to wait for a client connection, since the notifier is not a connection to this <code>RemoteDevice</code>.
     * @exception IOException if <code>conn</code> is closed
     * Real Authentication and Authorization will be added in a
     * future version. For now this call always returns <code>true</code>.
     */
    public boolean authorize(Connection conn) throws IOException {
      throw new RuntimeException("Will be soon implemented");
    }

    /**
     * Attempts to turn encryption on or off for an existing connection.  In the case where the parameter <code>on</code> is
     * <code>true</code>, this method will first authenticate this <code>RemoteDevice</code> if it has not already been
     * authenticated.  Then it will attempt to turn on encryption.  If the connection is already encrypted then this method
     * returns <code>true</code>.  Otherwise, when the parameter <code>on</code>
     * is <code>true</code>, either: <UL> <LI> the method succeeds in turning on encryption for the connection and returns
     * <code>true</code>, or <LI> the method was unsuccessful in turning on encryption and returns <code>false</code>.  This
     * could happen because the stack does not support encryption or
     * because encryption conflicts with the user's security settings for the device.  </UL>
     * <p> In the case where the parameter <code>on</code> is <code>false</code>, there are again two possible outcomes: <UL>
     * <LI> encryption is turned off on the connection and <code>true</code> is returned, or <LI> encryption is left on
     * for the connection and <code>false</code> is returned.  </UL> Encryption may be left on following <code>encrypt(conn,
     * false)</code> for a variety of reasons.  The user's current
     * security settings for the device may require encryption or the
     * stack may not have a mechanism to turn off encryption.  Also,
     * the BCC may have determined that encryption will be kept on for
     * the physical link to this <code>RemoteDevice</code>.  The details of the BCC are implementation dependent, but encryption
     * might be left on because other connections to the same device need encryption.  (All of the connections over the same
     * physical link must be encrypted if any of them are encrypted.)
     * <p> While attempting to turn encryption off may not succeed
     * immediately because other connections need encryption on, there
     * may be a delayed effect.  At some point, all of the connections
     * over this physical link needing encryption could be closed or
     * also have had the method <code>encrypt(conn, false)</code> invoked for them.  In this case, the BCC may turn off
     * encryption for all connections over this physical link.  (The
     * policy used by the BCC is implementation dependent.)  It is recommended that applications do <code>encrypt(conn,
     * false)</code> once they no longer need encryption to allow the
     * BCC to determine if it can reduce the overhead on connections to this <code>RemoteDevice</code>.
     * <p> The fact that <code>encrypt(conn, false)</code> may not
     * succeed in turning off encryption has very few consequences for
     * applications.  The stack handles encryption and decryption, so the application does not have to do anything different
     * depending on whether the connection is still encrypted or not.
     * @param conn the connection whose need for encryption has changed
     * @param on  <code>true</code> attempts to turn on encryption; <code>false</code> attempts to turn off encryption
     * @return <code>true</code> if the change succeeded, otherwise <code>false</code> if it failed
     * @exception IOException if <code>conn</code> is closed
     * @exception IllegalArgumentException if <code>conn</code> is not
     * a connection to this <code>RemoteDevice</code>; if <code>conn</code> was created by the
     * client side of the connection using a scheme other than <code>btspp</code>, <code>btl2cap</code>, or
     * <code>btgoep</code> (for example, this exception will be thrown if <code>conn</code> was created using the
     * <code>file</code> or <code>http</code> schemes.); if <code>conn</code> is a notifier used by a server
     * to wait for a client connection, since the notifier is not a connection to this <code>RemoteDevice</code>
     */
    public boolean encrypt(Connection conn, boolean on) throws IOException {
      if(conn instanceof StreamConnectionNotifier) throw new IllegalArgumentException("Argument must be a connection and not a notifier");
      if(!(conn instanceof BTConnection)) throw new IllegalArgumentException("Implementation does not support this type of connection!");
      if(((BTConnection)conn).isClosed()) throw new IOException("Connection is closed!");
      BluetoothStack bluetoothStack=null;
      try {
        bluetoothStack = BluetoothStack.getBluetoothStack();
      }
      catch(Exception ex) {
        throw new IOException("The Bluetooth stack could not be initialized!!!");
      }
      try {
        if(bluetoothStack.encrypt(conn, this, on)>0) {
          this.isEncrypt=on;
          return true;
        }
      }catch(Exception ex) {throw new IOException(ex.getMessage());}
      return false;

    }

    /**
     * Determines if this <code>RemoteDevice</code> has been authenticated. <P>
     * A device may have been authenticated by this application or another application.
     * Authentication applies to an ACL link
     * between devices and not on a specific L2CAP, RFCOMM, or OBEX connection.
     * Therefore, if <code>authenticate()</code> is performed when an L2CAP
     * connection is made to device A, then <code>isAuthenticated()</code> may
     * return <code>true</code> when tested as part of making an RFCOMM connection to device A.
     * @return <code>true</code> if this <code>RemoteDevice</code> has
     * previously been authenticated; <code>false</code> if it has not
     * been authenticated or there are no open connections between the local device and this <code>RemoteDevice</code>
     */
    public boolean isAuthenticated() {
      BluetoothStack bluetoothStack=null;
      try {
        bluetoothStack = BluetoothStack.getBluetoothStack();
        return bluetoothStack.getConnectionFlag(this,BluetoothStack.CON_AUTH_POS);
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      return false;
    }

    /**
     * Determines if this <code>RemoteDevice</code> has been authorized previously by the BCC of the local device to
     * exchange data related to the service associated with the connection. Both clients and servers can call this method.
     * However, for clients this method returns <code>false</code> for all legal values of the <code>conn</code> argument.
     * @param conn a connection that this <code>RemoteDevice</code> is using to access a service or provide a service
     * @return <code>true</code> if <code>conn</code> is a server-side connection and this <code>RemoteDevice</code> has been
     * authorized; <code>false</code> if <code>conn</code> is a
     * client-side connection, or a server-side connection that has not been authorized
     * @exception IllegalArgumentException if <code>conn</code> is not a connection to this <code>RemoteDevice</code>; if
     * <code>conn</code> was not created using one of the schemes <code>btspp</code>, <code>btl2cap</code>, or
     * <code>btgoep</code>; or if <code>conn</code> is a notifier used by a server to wait for a client connection, since the
     * notifier is not a connection to this <code>RemoteDevice</code>.
     * @exception IOException if <code>conn</code> is closed
     * and authorized. Propper authorization will be added in future version.
     * For now this call always returns <code>true</code>.
     */
    public boolean isAuthorized(Connection conn) throws IOException { throw new RuntimeException("Will be soon implemented"); }

    /**
     * Determines if data exchanges with this <code>RemoteDevice</code> are currently being encrypted. <P>
     * Encryption may have been previously turned on by this or another application.  Encryption applies to an ACL link
     * between devices and not on a specific L2CAP, RFCOMM, or OBEX connection.
     * Therefore, if <code>encrypt()</code> is performed with the
     * <code>on</code> parameter set to <code>true</code> when an L2CAP
     * connection is made to device A, then <code>isEncrypted()</code> may
     * return <code>true</code> when tested as part of making an RFCOMM connection to device A.
     * @return <code>true</code> if data exchanges with this <code>RemoteDevice</code> are being encrypted; <code>false</code>
     * if they are not being encrypted, or there are no open connections
     * between the local device and this <code>RemoteDevice</code>
     */
    public boolean isEncrypted() {
      BluetoothStack bluetoothStack=null;
      try {
        bluetoothStack = BluetoothStack.getBluetoothStack();
        isEncrypt=bluetoothStack.getConnectionFlag(this,BluetoothStack.CON_ENCRYPT_POS);
        return isEncrypt;
      }
      catch(Exception ex) {
        ex.printStackTrace();
      }
      return false;
    }
    
    /**
     * This function returns the deviceClass as it has been discovered.
     * The function does not appear in JSR82
     * @return
     */
    public DeviceClass getDeviceClass() {
    		return this.deviceClass;
    }

	/**
	 * @return the clockOffset
	 */
	public short getClockOffset() {
		return clockOffset;
	}
    
}

