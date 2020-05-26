/*
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

package de.avetana.bluetooth.stack;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import de.avetana.bluetooth.connection.BTConnection;
import de.avetana.bluetooth.connection.ConnectionFactory;
import de.avetana.bluetooth.connection.ConnectionNotifier;
import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.l2cap.L2CAPConnParam;
import de.avetana.bluetooth.l2cap.L2CAPConnectionNotifierImpl;
import de.avetana.bluetooth.sdp.LocalServiceRecord;
import de.avetana.bluetooth.util.BTAddress;
import de.avetana.bluetooth.util.Debug;
import edu.oswego.cs.dl.util.concurrent.PooledExecutor;

/**
 * This class provides the methods to access the underlying BlueZ functions.
 * All native methods defined in this class are implemented via the
 * Java Native Interface (JNI) in C/C++. See the bluez.cpp file and associated
 * comments for full details.
 *
 * All methods are static. This way of defining a common access to the
 * stack avoids the user and the developper to repeatedly create a new instance of the
 * BlueZ class.
 *
 * @author Edward Kay, ed.kay@appliancestudio.com / Julien Campana, Christiano di Flora diflora@unina.it
 * @version 1.1
 * 
 * @todo Stop BTThread on MacOS X in a shutdown hook.
 */

public class BlueZ {

	public static ConnectionFactory myFactory = new ConnectionFactory();

	public static int m_transactionId = 0;
	public static final PooledExecutor executor = new PooledExecutor();
	private static Vector mutexes = new Vector();

	private static class Mutex {
		int fid;

		public Mutex(int fid) {
			this.fid = fid;
		}

		public boolean equals(Object m2) {
			return (m2 instanceof Mutex && ((Mutex) m2).fid == fid);
		}

	}

	/**
	 * 
	 * Loads the library containing the native code implementation.
	 * It is usually called "libavetanaBT.so" under UNIX/Linux, but is loaded
	 * with the "avetanaBT" string, since this is how JNI implements platform
	 * independence.
	 */

	static {
		try {
			String ka = System.getProperty("de.avetana.bluetooth.ThreadPool.keepAliveTime");
			long keepalive = 0;
			if (ka != null)
				keepalive = Long.parseLong(ka);
			executor.setKeepAliveTime(keepalive);
		} catch (Exception e) {
		}
		//de.avetana.bluetooth.util.Version.readVersion();
	}

	/* HCI Get Link Quality*/
	/**
	 * Retrieves the current quality of the physical bluetooth connection
	 * to the specified device.
	 *
	 * See HCI_Link_Quality in the Bluetooth Specification for further
	 * details of the returned values.
	 *
	 *
	 * @param bdaddr Bluetooth address String in the form
	 *     <code>"00:12:34:56:78:9A"</code>.
	 *
	 * @return An estimation of the link quality.
	 */
	public static native int getLinkQuality(String bdaddr) throws BlueZException;

	/**
	 * Opens the HCI device.
	 *
	 * @param hciDevID The local HCI device ID (see the hciconfig tool provided
	 *     by BlueZ for further information).
	 * @exception BlueZException Unable to open the HCI device.
	 * @return A device descriptor (often named <code>dd</code>) for the HCI
	 *     device.
	 */
	public synchronized static native int hciOpenDevice(int hciDevID, BlueZ ref) throws BlueZException;

	/* HCI Close Device */
	/**
	 * Close the HCI device.
	 *
	 * @param dd The HCI device descriptor (as returned from
	 *     <code>hciOpenDevice</code>)
	 */
	protected synchronized static native void hciCloseDevice(int dd);

	public static native void cancelInquiry();

	/* HCI Inquiry */
	/**
	 * Performs an HCI inquiry to discover remote Bluetooth devices.
	 *
	 * See HCI_Inquiry in the Bluetooth Specification for further details of
	 * the various arguments.
	 *
	 * @exception BlueZException If the inquiry failed.
	 * @param hciDevID The local HCI device ID (see the hciconfig tool provided
	 *     by BlueZ for further information).
	 * @param len Maximum amount of time before inquiry is halted. Time = len
	 *      x 1.28 secs.
	 * @param max_num_rsp Maximum number of responses allowed before inquiry
	 *     is halted. For an unlimited number, set to 0.
	 * @param flags Additional flags. See BlueZ documentation and source code
	 *      for details.
	 * @return An InquiryInfo object containing the results of the inquiry.
	 * @see #hciInquiry(int hciDevID)
	 */
	public static native boolean hciInquiry(int hciDevID, int len, int max_num_rsp, long flags, DiscoveryAgent agent)
			throws BlueZException;

	/**
	 * Performs an HCI inquiry to discover remote Bluetooth devices. This is the
	 * same as <code>hciInquiry(int hciDevID, int len, int max_num_rsp, long
	 * flags)</code>, except that the <code>len</code>, <code>max_num_rsp</code>
	 * and <code>flags</code> fields are preset to 'default' values. These
	 * values are 8, 10 and 0, respectively.
	 *
	 * @exception BlueZException If the inquiry failed.
	 * @param hciDevID The local HCI device ID (see the hciconfig tool provided
	 *     by BlueZ for further information)
	 * @return An InquiryInfo object containing the results of the inquiry.
	 */
	public static boolean hciInquiry(int hciDevID, DiscoveryAgent agent) throws BlueZException {
		return hciInquiry(hciDevID, 8, 10, 0, agent);
	}

	/* HCI Device Bluetooth Address */
	/**
	 * Gets the Bluetooth device address for a specified local HCI device.
	 *
	 * @exception BlueZException If unable to get the Bluetooth device address.
	 * @param hciDevID The local HCI device ID (see the hciconfig tool provided
	 *     by BlueZ for further information)
	 * @return A BTAddress object representing the Bluetooth device address.
	 */
	public static native BTAddress hciDevBTAddress(int hciDevID) throws BlueZException;

	/* HCI Device ID */
	/**
	 * Gets the device ID for a specified local HCI device.
	 *
	 * @exception BlueZException If unable to get the device ID.
	 * @param bdaddr Bluetooth address String in the form
	 *     <code>"00:12:34:56:78:9A"</code>.
	 * @return The device ID for the local device.
	 */
	public static native int hciDeviceID(String bdaddr) throws BlueZException;

	/**
	 * Gets the device ID for a specified local HCI device.
	 *
	 * @exception BlueZException If unable to get the device ID.
	 * @param bdaddr Bluetooth address as a BTAddress object.
	 * @return The device ID for the local device.
	 */
	public static int hciDeviceID(BTAddress bdaddr) throws BlueZException {
		return hciDeviceID(bdaddr.toString());
	}

	/* HCI Local Name */
	/**
	 * Gets the name of a local device. The device must be opened using
	 * <code>hciOpenDevice</code> before calling this method.
	 *
	 * @exception BlueZException If unable to get the local device name.
	 * @param dd HCI device descriptor.
	 * @param timeOut Timeout, in milliseconds.
	 * @return A String containing the name of the specified local device.
	 */
	public static native String hciLocalName(int dd, int timeOut) throws BlueZException;

	/**
	 * Gets the name of a local device. The device must be opened using
	 * <code>hciOpenDevice</code> before calling this method. This is the same
	 * as <code>hciLocalName(int dd, int timeOut)</code> with the
	 * <code>timeOut</code> argument set to 10000 (i.e. 10 seconds).
	 *
	 * @param dd HCI device descriptor.
	 * @exception BlueZException If unable to get the local device name.
	 * @return A String containing the name of the specified local device.
	 */
	public static String hciLocalName(int dd) throws BlueZException {
		return hciLocalName(dd, 10000);
	}

	/* HCI Remote Name */
	/**
	 * Gets the name of a remote device, as specified by its Bluetooth device
	 * address. The local device must be opened using <code>hciOpenDevice</code>
	 * before calling this method.
	 *
	 * @exception BlueZException If unable to get the remote device name.
	 * @param dd HCI device descriptor.
	 * @param bdaddr Bluetooth address String in the form
	 *     <code>"00:12:34:56:78:9A"</code>.
	 * @param timeOut Timeout, in milliseconds.
	 * @return A String containing the name of the specified remote device.
	 */
	public static native String hciRemoteName(int dd, String bdaddr, int timeOut) throws BlueZException;

	/**
	 * Gets the name of a remote device, as specified by its Bluetooth device
	 * address. The local device must be opened using <code>hciOpenDevice</code>
	 * before calling this method. This is the same as
	 * <code>hciRemoteName(int dd, String bdaddr, int timeOut)</code> with the
	 * <code>timeOut</code> argument set to 10000 (i.e. 10 seconds).
	 *
	 * @param dd HCI device descriptor.
	 * @param bdaddr Bluetooth address String in the form
	 *     <code>"00:12:34:56:78:9A"</code>.
	 * @exception BlueZException If unable to get the remote device name.
	 * @return A String containing the name of the specified remote device.
	 */
	public static String hciRemoteName(int dd, String bdaddr) throws BlueZException {
		return hciRemoteName(dd, bdaddr, 10000);
	}

	/**
	 * Gets the name of a remote device, as specified by its Bluetooth device
	 * address. The local device must be opened using <code>hciOpenDevice</code>
	 * before calling this method.
	 *
	 * @param dd HCI device descriptor.
	 * @param bdaddr Bluetooth address as a BTAddress object.
	 * @param timeOut Timeout, in milliseconds.
	 * @exception BlueZException If unable to get the remote device name.
	 * @return A String containing the name of the specified remote device.
	 */
	public static String hciRemoteName(int dd, BTAddress bdaddr, int timeOut) throws BlueZException {
		return hciRemoteName(dd, bdaddr.toString(), timeOut);
	}

	/**
	 * Gets the name of a remote device, as specified by its Bluetooth device
	 * address. The local device must be opened using <code>hciOpenDevice</code>
	 * before calling this method. This is the same as
	 * <code>hciRemoteName(int dd, BTAddress bdaddr, int timeOut)</code> with
	 * the <code>timeOut</code> argument set to 10000 (i.e. 10 seconds).
	 *
	 * @param dd HCI device descriptor.
	 * @param bdaddr Bluetooth address as a BTAddress object.
	 * @exception BlueZException If unable to get the remote device name.
	 * @return A String containing the name of the specified remote device.
	 */
	public static String hciRemoteName(int dd, BTAddress bdaddr) throws BlueZException {
		return hciRemoteName(dd, bdaddr.toString(), 25000);
	}

	// Call of the native method openRFCommNative
	private static native int openRFCommNative(String addr, int channel, boolean master, boolean auth, boolean encrypt,
			int timeout);

	// Call of the native method openL2CAPNative
	private static native L2CAPConnParam openL2CAPNative(String addr, int psm, boolean master, boolean auth,
			boolean encrypt, int receiveMTU, int transmitMTU, int timeout);

	/**
	 * Opens an L2CAP connection with a remote BT device.
	 * @param url The JSR82URL object describing the remote BT device (BT address and PSM),
	 * the desired security options (master, authenticate, encrypt) and the desired connection options (imtu, omtu)
	 * @return An L2CAPConnParam object encapsulating the integer, which uniquely identifies the connection.
	 * @throws BlueZException If an error occured in the C part of the Code
	 * @throws Exception If the URL is not a valid L2CAP URL.
	 */
	public static L2CAPConnParam openL2CAP(JSR82URL url, int timeout) throws BlueZException, Exception {
		if (url.getBTAddress() == null)
			throw new Exception("This is not a valid remote L2CAP connection url!");
		int psm = url.getAttrNumber();
		int receiveMTU = 672, transmitMTU = 672;
		try {
			receiveMTU = Integer.parseInt((String) url.getParameter("receivemtu"));
		} catch (Exception ex) {
		}
		try {
			transmitMTU = Integer.parseInt((String) url.getParameter("transmitmtu"));
		} catch (Exception ex) {
		}
		return openL2CAPNative(url.getBTAddress().toString(), psm, url.isLocalMaster(), url.isAuthenticated(), url
				.isEncrypted(), receiveMTU, transmitMTU, timeout);
	}

	/**
	 * Opens an RFCOMM connection with a remote BT device.
	 * @param url The JSR82URL object describing the remote BT device (BT address and channel number),
	 * the desired security options (master, authenticate, encrypt).
	 * @return The integer, which uniquely identifies the connection.
	 * @throws BlueZException If an error occured in the C part of the Code
	 * @throws Exception If the URL is not a valid L2CAP URL.
	 */
	public static int openRFComm(JSR82URL url, int timeout) throws BlueZException, Exception {
		if (url.getBTAddress() == null)
			throw new Exception("This is not a valid remote RFComm connection url!");
		int channel = url.getAttrNumber();
		return openRFCommNative(url.getBTAddress().toString(), channel, url.isLocalMaster(), url.isAuthenticated(), url
				.isEncrypted(), timeout);
	}

	/**
	 * Closes an existing connection.
	 * @param fid The integer, which uniquely identifies the connection (the file descriptor under linux).
	 */

	public static void closeConnectionS(int fid) {
		//LibLoader.DebugT ("BlueZ::CloseConnection from java side " + fid);
		synchronized (getMutex(fid)) {
			closeConnection(fid);
			mutexes.removeElement(new Mutex(fid));
		}
	}

	private static native void closeConnection(int fid);

	/**
	 * Sends a native HCI command to the BT-Dongle. THis works with the Widcomm Stack right now.
	 * 
	 * @param command
	 * @param params
	 * @return
	 * @throws BlueZException
	 */

	public static native int[] sendHCI(int command, int[] params) throws BlueZException;

	/**
	 * Writes byte to an existing connection
	 * @param fid The integer, which uniquely identifies the connection.
	 * @param b The byte array storing the bytes to be written
	 * @param len The length of b
	 * @param off The offset into b at which to start
	 */

	public static void writeBytesS(int fid, byte b[], int off, int len) throws IOException {
		if (fid <= 0)
			throw new IOException("Connection closed");
		synchronized (getMutex(fid)) {
			if (writeBytes(fid, b, off, len) == 0)
				throw new IOException("Connection closed");
		}

	}

	private static native int writeBytes(int fid, byte b[], int off, int len);

	private static Object getMutex(int num) {
		synchronized (mutexes) {
			Mutex m = new Mutex(num);
			int i = mutexes.indexOf(m);
			if (i != -1)
				return mutexes.elementAt(i);
			else {
				mutexes.addElement(m);
				return m;
			}
		}
	}

	/**
	 * Lists all SDP services, which match a desired list of UUIDs. Only the attributes contained in attrIds will
	 * populate the returned service records.
	 * @param bdaddr_jstr The address of the remote BT device
	 * @param uuid The list of UUIDs the service record must contain.
	 * @param attrIds The list of Attributes which will populate the Service record.
	 * @throws BlueZException
	 */
	public static native void listService(String bdaddr_jstr, byte[][] uuid, int[] attrIds, int transID)
			throws BlueZException;

	/**
	 * Stores a new Service record in the BCC.
	 * @param service The service record
	 * @return a positive integer is the process succeeds.
	 * @throws BlueZException
	 */
	public static synchronized native int createService(LocalServiceRecord service) throws BlueZException;

	/**
	 * Updates an existing service record (the old <service record must be already stored in the BCC.)
	 * @param newService The byte representation of this new service record
	 * @param length The length of this byte representation
	 * @param recordHandle The record handle of the old service record
	 * @return a positive integer is the process succeeds.
	 * @throws BlueZException
	 */
	public static native int updateService(ServiceRecord service, long recordHandle) throws BlueZException;

	/**
	 * Registers the service record identified by the variable "serviceHandle" and listens for an incoming RFCOMM Connection
	 * @param serviceHandle The integer, which uniquely identifies the service record
	 * @param channel The channel number (Incoming connections linked with a channel number
	 * @param master Is the local device master for this connection?
	 * @param auth Must the remote device be authenticated during the establishment of the connection
	 * @param encrypt Must the ACL linkbe encrpyted during the establishment of the connection
	 * @return a positive integer is the process succeeds.
	 * @throws BlueZException
	 */
	public static native int registerService(int serviceHandle, int channel, boolean master, boolean auth,
			boolean encrypt) throws BlueZException;

	/**
	 * Registers the service record identified by the variable "serviceHandle" and listens for an incoming L2CAP Connection
	 * @param serviceHandle The integer, which uniquely identifies the service record
	 * @param channel The channel number (Incoming connections linked with a channel number
	 * @param master Is the local device master for this connection?
	 * @param auth Must the remote device be authenticated during the establishment of the connection
	 * @param encrypt Must the ACL linkbe encrpyted during the establishment of the connection
	 * @param omtu Set the size of the Output MTU
	 * @param imtu Set the size of the Input MTU
	 * @return a positive integer is the process succeeds.
	 * @throws BlueZException
	 */
	public static native int registerL2CAPService(int serviceHandle, int channel, boolean master, boolean auth,
			boolean encrypt, int omtu, int imtu) throws BlueZException;

	/**
	 * Deletes the service record identified by the record handle given as parameter.
	 * @param recordHandle
	 * @throws BlueZException
	 */
	public static native void disposeLocalRecord(long recordHandle) throws BlueZException;

	/**
	 * Gets the access mode of the local device
	 * @param device The integer which identifies the local device
	 * @return The access mode if this device
	 * @throws BlueZException
	 */
	public static native int getAccessMode(int device) throws BlueZException;

	/**
	 * Sets the access mode of the local device
	 * @param device The integer which identifies the local device
	 * @param mode The new access mode
	 * @return
	 * @throws BlueZException
	 */
	public static native int setAccessMode(int device, int mode) throws BlueZException;

	/**
	 * Gets the device class of the local device number "dev_id"
	 * @param dev_id The number which identifies the local device
	 * @return The current device class
	 * @throws BlueZException
	 */
	public static native int getDeviceClass(int dev_id) throws BlueZException;

	/**
	 * sets the device class to "cls" of the local device number "dev_id"
	 * @see <A href="https://www.bluetooth.org/foundry/assignnumb/document/baseband">Assigned Numbers - Bluetooth Baseband</A>  for the correct format of cls
	 * @param dev_id The number which identifies the local device
	 * @param cls The new device class of the device
	 * @return true/false
	 * @throws BlueZException
	 */
	public static native boolean setDeviceClass(int dev_id, int cls) throws BlueZException;

	/**
	 * Is master/slave switch allowed?
	 * @return <code>true</code> If master/slave switch is allowed<br>
	 *         <code>false</code> Otherwise
	 * @throws BlueZException
	 */
	public static native boolean isMasterSwitchAllowed() throws BlueZException;

	/**
	 * Returns the maximum number of connected devices allowed by the stack
	 * This number may be greater than 7 if the implementation handles parked connections.
	 *  The string will be in Base 10 digits.
	 * @return The maximum number of connected devices allowed by the stack
	 * @throws BlueZException
	 */
	public static native int getMaxConnectedDevices() throws BlueZException;

	/**
	 * Inquiry scanning allowed during connection?
	 * @return <code>true</code> If the inquiry scanning is allowed during a connection<br>
	 *         <code>false</code> Otherwise
	 * @throws BlueZException
	 */
	public static native boolean inquiryScanAndConAllowed() throws BlueZException;

	/**
	 * Is Inquiry allowed during a connection?
	 * @return <code>true</code> If the inquiry is allowed during a connection<br>
	 *         <code>false</code> Otherwise
	 * @throws BlueZException
	 */
	public static native boolean inquiryAndConAllowed() throws BlueZException;

	/**
	 * Page scanning allowed during connection?
	 * @return <code>true</code> If page scanning during a connection is allowed <br>
	 *         <code>false</code> Otherwise
	 * @throws BlueZException
	 */
	public static native boolean pageScanAndConAllowed() throws BlueZException;

	/**
	 * Is paging allowed during a connection?
	 * In other words, can a connection be established to one device if
	 * it is already connected to another device.
	 * @return <code>true</code> If paging during a connection is allowed <br>
	 *         <code>false</code> Otherwise
	 * @throws BlueZException
	 */
	public static native boolean pageAndConnAllowed() throws BlueZException;

	/**
	 * Change the local device name
	 * @param name
	 * @return true if successful
	 */
	public static native boolean setDeviceName(String name);

	/**
	 * Authenticates the remote device
	 * @param hci number (0)
	 * @param deviceAddr The BT address of the remote device (00-0d-93-05-17-0e)
	 * @param pin in Windows, one can specify a given PIN number to use
	 * @return 1 if successful 0 if not
	 * @throws BlueZException
	 */
	public static native int authenticate(int handle, String deviceAddr, String pin) throws BlueZException;

	/**
	 * Authenticates the remote device
	 * @param deviceAddr The BT address of the remote device
	 * @param pin in Windows, one can specify a given PIN number to use
	 * @return 1 if successful 0 if not
	 * @throws BlueZException
	 */
	public static int authenticate(String deviceAddr, String pin) throws BlueZException {
		if (deviceAddr.length() == 12) {
			deviceAddr = deviceAddr.substring(0, 2) + "-" + deviceAddr.substring(2, 4) + "-"
					+ deviceAddr.substring(4, 6) + "-" + deviceAddr.substring(6, 8) + "-" + deviceAddr.substring(8, 10)
					+ "-" + deviceAddr.substring(10, 12);
		} else if (deviceAddr.length() != 17)
			throw new BlueZException("Wrong address length " + deviceAddr.length());
		return authenticate(0, deviceAddr, pin);
	}

	public static int unPair(String deviceAddr) throws BlueZException {
		if (deviceAddr.length() == 12) {
			deviceAddr = deviceAddr.substring(0, 2) + "-" + deviceAddr.substring(2, 4) + "-"
					+ deviceAddr.substring(4, 6) + "-" + deviceAddr.substring(6, 8) + "-" + deviceAddr.substring(8, 10)
					+ "-" + deviceAddr.substring(10, 12);
		} else if (deviceAddr.length() != 17)
			throw new BlueZException("Wrong address length " + deviceAddr.length());
		return unPairN(deviceAddr);

	}

	private static native int unPairN(String deviceAddr);

	/**
	 * Turns on/off the encryption of an ACL link
	 * @param handle The number, which uniquely identifies the connection
	 * @param deviceAddr The BT address of the remote device
	 * @param enable If true, turn on the encryption of the ACL link
	 * @return
	 * @throws BlueZException
	 */
	public static native int encrypt(int handle, String deviceAddr, boolean enable) throws BlueZException;

	/**
	 * Retrieves the connection options
	 * @param handle The number, which uniquely identifies the connection
	 * @param deviceAddr The BT address of the remote device
	 * @return
	 * @throws BlueZException
	 */
	public static native boolean[] connectionOptions(int handle, String deviceAddr) throws BlueZException;

	// Debug method
	//        public synchronized static void debugPrintStr(String str)  {
	//        	LibLoader.Debug("Native::"+str);
	//        }

	/**
	 * Starts a service search.
	 * @param bdaddr_jstr The BT address of the remote device
	 * @param uuid The list of UUIDs the services must match
	 * @param attrIds The list of attributes the services should contain
	 * @param listener The discovery listener, which handles the callback methods.
	 * @throws BlueZException
	 */
	public synchronized static int searchServices(final String bdaddr_jstr, final byte[][] uuid, final int[] attrIds,
			final DiscoveryListener flistener) {
		m_transactionId++;
		final int myIntTransactionID = m_transactionId;

		DiscoveryListener listener = new DiscoveryListener() {

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			}

			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				try {
					flistener.servicesDiscovered(transID, servRecord);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			public void serviceSearchCompleted(int transID, int respCode) {
				try {
					flistener.serviceSearchCompleted(transID, respCode);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

			public void inquiryCompleted(int discType) {
			}

		};

		myFactory.addListener(myIntTransactionID, listener);
		Runnable r = new Runnable() {
			public void run() {
				//LibLoader.cremeInit(this);
				try {
					listService(bdaddr_jstr, uuid, attrIds, myIntTransactionID);
				} catch (Exception ex) {
					serviceSearchComplete(myIntTransactionID, DiscoveryListener.SERVICE_SEARCH_ERROR);
				}
				//LibLoader.cremeOut(this);
			}
		};
		try {
			executor.execute(r);
		} catch (Exception e) {
			serviceSearchComplete(myIntTransactionID, DiscoveryListener.SERVICE_SEARCH_ERROR);
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}

		return myIntTransactionID;
	}

	/**
	 * Registers a service and waits for incoming connections
	 * @param a_notifier The connection notifier created by the user.
	 * @throws Exception
	 */
	public static void registerNotifier(final ConnectionNotifier a_notifier) throws Exception {
		if (a_notifier.getConnectionURL() == null)
			throw new Exception("No connection URL previously defined!");
		myFactory.addNotifier(a_notifier);
		final short proto = (a_notifier.getConnectionURL() == null ? JSR82URL.PROTOCOL_RFCOMM : a_notifier
				.getConnectionURL().getProtocol());
		final int channel = a_notifier.getConnectionURL().getAttrNumber();
		Debug.println(1, "BlueZ::Registered notifier for channel " + channel);
		Runnable r = new Runnable() {
			public void run() {
				//LibLoader.cremeInit(this);
				int fid = 0;
				try {
					if (proto == JSR82URL.PROTOCOL_L2CAP) {
						fid = registerL2CAPService((int) a_notifier.getServiceHandle(), channel, a_notifier
								.getConnectionURL().isLocalMaster(), a_notifier.getConnectionURL().isAuthenticated(),
								a_notifier.getConnectionURL().isEncrypted(), -1, -1);
					} else {
						Debug.println(1, "BlueZ::Registering service");
						fid = registerService((int) a_notifier.getServiceHandle(), channel, a_notifier
								.getConnectionURL().isLocalMaster(), a_notifier.getConnectionURL().isAuthenticated(),
								a_notifier.getConnectionURL().isEncrypted());
						Debug.println(1, "BlueZ::Registered service " + fid);
					}
				} catch (BlueZException e) {
					a_notifier.setFailure(new IOException(e.getMessage()));
					a_notifier.setConnectionID(-1);
				}
				//LibLoader.cremeOut(this);
			}
		};
		executor.execute(r);
		Thread.currentThread().sleep(50); //This is done so we can be sure that the registering of the service on the native side has actually started before we continue
		//If I don't do this, then the Notifier could be closed before the registering starts which causes a crash in Widcomm.
	}

	/**
	 * Remove the Notifier when creating a service has failed
	 */

	public static void removeNotifier(ConnectionNotifier a_notifier) {
		myFactory.removeNotifier(a_notifier);
	}

	/**
	 * Method called by the C-Code in order to notify the establishment of a new connection.
	 * This method is always called after the call of registerNotifier.
	 * @param fid The number which uniquely identifies the connection
	 * @param channel The channel or PSM number
	 * @param protocol The protocol (see the class JSR82URL)
	 * @param jaddr The BT address of the remote device
	 * @return
	 */

	public static boolean connectionEstablished(int fid, int channel, int protocol, String jaddr) {
		return connectionEstablished(fid, channel, protocol, jaddr, 672, 672);
	}

	public static boolean connectionEstablished(int fid, int channel, int protocol, String jaddr, int recMTU,
			int transMTU) {
		Debug.println(1, "BlueZ::Connection established " + channel + " fid " + fid);
		for (int i = 0; i < myFactory.getNotifiers().size(); i++) {
			try {
				ConnectionNotifier not = (ConnectionNotifier) myFactory.getNotifiers().elementAt(i);
				// Nur eine Verbindung pro Kanal!
				short proto = (not.getConnectionURL() == null ? JSR82URL.PROTOCOL_RFCOMM : not.getConnectionURL()
						.getProtocol());
				if (proto == JSR82URL.PROTOCOL_OBEX)
					proto = JSR82URL.PROTOCOL_RFCOMM;
				int ch = not.getConnectionURL().getAttrNumber();
				if (ch == channel && protocol == proto) {
					if (fid == 0)
						return true; //Native wants to check if there is a notifier to be called
					not.setRemoteDevice(jaddr);
					if (not instanceof L2CAPConnectionNotifierImpl)
						((L2CAPConnectionNotifierImpl) not).setMTUs(transMTU, recMTU);
					not.setConnectionID(fid);
					//myFactory.removeNotifier(not);
					Debug.println(1, "BlueZ::Listener notified");
					return true;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Debug.println(5, "BlueZ::Notifier not found for " + fid + " " + channel + " in "
				+ myFactory.getNotifiers().size());
		return false;
	}

	public static boolean cancelServiceSearch(int transID) {
		DiscoveryListener dl = myFactory.getListener(transID);
		if (dl != null) {
			try {
				dl.serviceSearchCompleted(transID, DiscoveryListener.SERVICE_SEARCH_TERMINATED);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		myFactory.removeListener(transID);
		return true;
	}

	/**
	 * Callback method, which notifies the discovering a new service.
	 * @param transID SDP transaction ID
	 * @param rec The service record discovered
	 */
	public static void addService(int transID, ServiceRecord rec[]) {
		for (int i = 0; i < rec.length; i++) {
			addService(transID, rec[i]);
		}
	}

	public static void addService(int transID, ServiceRecord rec) {
		DiscoveryListener myListener = myFactory.getListener(transID);
		if (myListener == null) {
			//System.out.println("ERROR - Listener not defined. Unable to add service " + transID);
			return;
		}
		try {
			myListener.servicesDiscovered(transID, new ServiceRecord[] { rec });
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Callback method which notifies the end of a service search.
	 * @param transID SDP transaction code
	 * @param respCode The responde code of the C-implementation
	 * @param jBTAddr The BT address of the remote device
	 */
	public static void serviceSearchComplete(int transID, int respCode) {
		DiscoveryListener myListener = myFactory.getListener(transID);
		if (myListener == null) {
			//System.out.println("ERROR - Listener not defined. Unable to interpret service search completed code");
			return;
		}
		myListener.serviceSearchCompleted(transID, respCode);
		myFactory.removeListener(transID);

	}

	public Class createClass(String name) {
		try {
			int pos = -1;
			while ((pos = name.indexOf("/")) != -1 || ((pos = name.indexOf("\\")) != -1))
				name = name.substring(0, pos) + "." + name.substring(pos + 1);

			Class c = Class.forName(name);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method called from the Windows native library to reserve storage space.
	 * This is used because of some JNI troubles.
	 * 
	 * @param size
	 * @return
	 */

	public static byte[] newByteArray(int size) {
		try {
			return new byte[size];
		} catch (Throwable e) {
			System.err.println("Collection garbage");
			e.printStackTrace();
			System.gc();
			try {
				return new byte[size];
			} catch (Throwable e2) {
				e2.printStackTrace();
				return null;
			}

		}
	}

	/**
	 * Method called from the Windows native DLL when the stack has gone down. This is used
	 * to remove all Notifiers and close all connections. 
	 *
	 */

	public synchronized static void stackDown() {
		Vector v = myFactory.getNotifiers();
		for (int i = 0; i < v.size(); i++) {
			ConnectionNotifier conNot = (ConnectionNotifier) v.elementAt(i);
			conNot.setFailure(new IOException("Stack gone down"));
			conNot.setConnectionID(-1);
		}
		v = myFactory.getConnections();
		for (int i = 0; i < v.size(); i++) {
			BTConnection conNot = (BTConnection) v.elementAt(i);
			conNot.close();
		}
	}

	/**
	 * Provides an estimation of the strength of the signal received
	 * from another specified bluetooth-device.
	 *
	 * See HCI_Get_RSSI in the Bluetooth Specification for further
	 * details of the returned values.
	 *
	 *
	 * @param bdaddr Bluetooth address String in the form
	 *     <code>"00:12:34:56:78:9A"</code>.
	 *
	 * @return An estimation of the Received Signal Strength Indicator.
	 */
	public static native int getRssi(String adr) throws BlueZException;

	/**
	 * Method is called from native implementations when new Data has arrived for a connection listening for data.
	 * 
	 * @param data
	 * @param fid
	 * @return 0 if connection is not known on java side else 1
	 */

	public static int newData(byte[] data, int fid) {
		Debug.println(1, "BlueZ::new Data " + data.length);
		BTConnection con = myFactory.getConnectionForFID(fid);
		if (con == null)
			return 0;

		con.newData(data);

		return 1;
	}

	/**
	 * Used for Widcomm to wait for the RFCommBuffer to be drained
	 * @param fd
	 * @throws IOException
	 */

	public static native void flush(int fd) throws IOException;

	/**
	 * Called from the native side, when the connection is beeing closed
	 * 
	 * @param fid
	 */

	public static void connectionClosed(int fid) {
		Debug.println(1, "BlueZ::ConnectionClosed from native side " + fid);

		try {
			BTConnection con = myFactory.getConnectionForFID(fid);
			if (con != null && !con.isClosed())
				con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * For Windows and PocketPC, where reading should be started from the JavaVM. Also interrupts the timeout thread
	 * @param fid
	 * @param mtu
	 */

	public static void startReaderThread(final int fid, final int mtu) {
		if (fid == timeoutfid)
			timeoutfid = -1;

		Runnable r = new Runnable() {
			public void run() {
				Debug.println(1, "BlueZ::Starting Reader Thread");
				//LibLoader.cremeInit(this);
				readBytes(fid, mtu);
				//LibLoader.cremeOut(this);
			}
		};
		try {
			executor.execute(r);
		} catch (Exception e) {
			e.printStackTrace();
			new Thread(r).start();
		}
	}

	private static int timeoutfid = -1;

	public static void startTimeoutThread(final int fid, final int timeout) {
		timeoutfid = fid;
		Runnable r = new Runnable() {
			public void run() {
				long startt = System.currentTimeMillis();
				while (fid == timeoutfid && System.currentTimeMillis() - startt < (long) timeout) {
					try {
						Thread.currentThread().sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (fid == timeoutfid)
					closeSocket(fid);
				timeoutfid = -1;
			}
		};
		try {
			executor.execute(r);
		} catch (Exception e) {
			e.printStackTrace();
			new Thread(r).start();
		}
	}

	private static native void closeSocket(int fid);

	/**
	 * This function is used in Linux to endlessly read new Data from an opened connection
	 * @param fid
	 * @param mtu
	 */
	private static native void readBytes(int fid, int mtu);

	/**
	 * The following method is called by some Stacks to notify of RemoteDevices that have been found.
	 *
	 */

	public static synchronized void deviceDiscovered(RemoteDevice d) {
		try {
			LocalDevice.getLocalDevice().getDiscoveryAgent().deviceDiscovered(d);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * MacOS X only function to remove a service record by its ID.
	 * @param id 0x00010004 for PDA-SyncService
	 * @return 0 upon success
	 */
	public static native void removeServiceByID(long id);

}
