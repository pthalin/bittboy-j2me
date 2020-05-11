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

import java.util.Enumeration;
import java.util.Vector;

import de.avetana.bluetooth.sdp.RemoteServiceRecord;
import de.avetana.bluetooth.stack.BlueZ;
import de.avetana.bluetooth.stack.BluetoothStack;

/**
 * The <code>DiscoveryAgent</code> class provides methods to perform
 * device and service discovery.  A local device must have only one
 * <code>DiscoveryAgent</code> object.  This object must be retrieved by a call to <code>getDiscoveryAgent()</code> on the
 * <code>LocalDevice</code> object. <H3>Device Discovery</H3> There are two ways to discover devices.  First, an application
 * may use <code>startInquiry()</code> to start an inquiry to find devices
 * in proximity to the local device. Discovered devices are returned
 * via the <code>deviceDiscovered()</code> method of the interface <code>DiscoveryListener</code>.  The second way to
 * discover devices is via the <code>retrieveDevices()</code> method.
 * This method will return devices that have been discovered via a
 * previous inquiry or devices that are classified as pre-known. (Pre-known devices are those devices that are defined in the
 * Bluetooth Control Center as devices this device frequently contacts.)
 * The <code>retrieveDevices()</code> method does not perform an
 * inquiry, but provides a quick way to get a list of devices that may be in the area. <H3>Service Discovery</H3>
 * The <code>DiscoveryAgent</code> class also encapsulates the
 * functionality provided by the service discovery application profile.
 * The class provides an interface for an application to search and
 * retrieve attributes for a particular service.  There are two ways to
 * search for services.  To search for a service on a single device,
 * the <code>searchServices()</code> method should be used.  On the
 * other hand, if you don't care which device a service is on, the
 * <code>selectService()</code> method does a service search on a set of remote devices.
 * @author Julien Campana/Christian Lorenz
 */
public class DiscoveryAgent {

	private Vector m_remoteServ = null;
	private SelectListener m_listener = null;
	private final int m_timeout = 10000; //timeout from 10 seconds.

	static {
		try {
			BluetoothStack.getBluetoothStack();
		} catch (Exception e) {
		}
	}

	/** Takes the device out of discoverable mode. <P> The value of <code>NOT_DISCOVERABLE</code> is 0x00 (0). */
	public static final int NOT_DISCOVERABLE = 0;

	/**
	 * The inquiry access code for General/Unlimited Inquiry Access Code
	 * (GIAC). This is used to specify the type of inquiry to complete or respond to. <P>
	 * The value of <code>GIAC</code> is 0x9E8B33 (10390323). This value
	 * is defined in the Bluetooth Assigned Numbers document.
	 */
	public static final int GIAC = 0x9E8B33;

	/**
	 * The inquiry access code for Limited Dedicated Inquiry Access Code
	 * (LIAC). This is used to specify the type of inquiry to complete or respond to. <P>
	 * The value of <code>LIAC</code> is 0x9E8B00 (10390272). This value
	 * is defined in the Bluetooth Assigned Numbers document.
	 */
	public static final int LIAC = 0x9E8B00;

	/**
	 * Used with the <code>retrieveDevices()</code> method to return
	 * those devices that were found via a previous inquiry.  If no
	 * inquiries have been started, this will cause the method to return <code>null</code>. <P>
	 * The value of <code>CACHED</code> is 0x00 (0).
	 * @see #retrieveDevices
	 */
	public static final int CACHED = 0x00;

	/**
	 * Used with the <code>retrieveDevices()</code> method to return
	 * those devices that are defined to be pre-known devices.  Pre-known
	 * devices are specified in the BCC.  These are devices that are
	 * specified by the user as devices with which the local device will frequently communicate. <P>
	 * The value of <code>PREKNOWN</code> is 0x01 (1).
	 * @see #retrieveDevices
	 */
	public static final int PREKNOWN = 0x01;
	private BluetoothStack bluetoothStack;
	private Vector listeners;
	private Vector cachedRemoteDevices;
	private boolean isInquiring;
	private int transactionID = 0;

	/**
	 * Creates a <code>DiscoveryAgent</code> object.
	 *@param bluetoothStack The Instance of the <code>BluetoothStack</code> associated with this <code>DiscoveryAgent</code>.
	 * 
	 */
	protected DiscoveryAgent(BluetoothStack bluetoothStack) {
		this.bluetoothStack = bluetoothStack;
		this.listeners = new Vector();
		this.cachedRemoteDevices = new Vector();
		this.isInquiring = false;
	}

	/*  End of the constructor method   */

	/**
	 * Returns an array of Bluetooth devices that have either been found
	 * by the local device during previous inquiry requests or been
	 * specified as a pre-known device depending on the argument. The list
	 * of previously found devices is maintained by the implementation of
	 * this API. (In other words, maintenance of the list of previously
	 * found devices is an implementation detail.) A device can be set as a pre-known device in the Bluetooth Control Center.
	 * @param option <code>CACHED</code> if previously found devices
	 * should be returned; <code>PREKNOWN</code> if pre-known devices should be returned
	 * @return an array containing the Bluetooth devices that were
	 * previously found if <code>option</code> is <code>CACHED</code>; an array of devices that are pre-known devices if
	 * <code>option</code> is <code>PREKNOWN</code>; <code>null</code> if no devices meet the criteria
	 * Christian Lorenz: This implemenation does not support <code>PREKNOWN</code> devices and will always return
	 * <code>null</code>. Use <code>CACHED</code> instead.
	 * @exception IllegalArgumentException if <code>option</code> is not <code>CACHED</code> or <code>PREKNOWN</code>
	 */
	public RemoteDevice[] retrieveDevices(int option) {
		if (option == PREKNOWN)
			return null;
		else if (option == CACHED) {
			RemoteDevice[] devices = null;
			synchronized (cachedRemoteDevices) {
				int deviceCount = cachedRemoteDevices.size();
				if (deviceCount > 0) {
					devices = new RemoteDevice[deviceCount];
					cachedRemoteDevices.copyInto(devices);
				}
			}
			return devices;
		} else
			throw new IllegalArgumentException(
					"DiscoveryAgent.retrieveDevices: Only CACHED and PREKNOWN are vaild values. ");
	}

	/**
	 * @deprecated
	 */
	public boolean isInquiring() {
		return isInquiring;
	}

	/**
	 * Places the device into inquiry mode.  The length of the inquiry is
	 * implementation dependent. This method will search for devices with the
	 * specified inquiry access code. Devices that responded to the inquiry are returned to the application via the method
	 * <code>deviceDiscovered()</code> of the interface <code>DiscoveryListener</code>. The <code>cancelInquiry()</code>
	 * method is called to stop the inquiry.
	 * @see #cancelInquiry
	 * @see #GIAC
	 * @see #LIAC
	 * @param accessCode  the type of inquiry to complete
	 * @param listener the event listener that will receive device discovery events
	 * @return <code>true</code> if the inquiry was started; <code>false</code> if the inquiry was not started because the
	 * <code>accessCode</code> is not supported
	 * @exception IllegalArgumentException if the access code provided
	 * is not <code>LIAC</code>, <code>GIAC</code>, or in the range 0x9E8B00 to 0x9E8B3F
	 * @exception NullPointerException if <code>listener</code> is <code>null</code>
	 * @exception BluetoothStateException if the Bluetooth device does
	 * not allow an inquiry to be started due to other operations that are being performed by the device
	 */
	public synchronized boolean startInquiry(final int accessCode, DiscoveryListener listener)
			throws BluetoothStateException {
		if (!isInquiring)
			listeners = new Vector();

		if (listener != null) {
			if (!listeners.contains(listener))
				listeners.addElement(listener);
		} else
			throw new NullPointerException("Listener must not be null.");

		if (isInquiring) {
			Enumeration remoteDevices = cachedRemoteDevices.elements();
			while (remoteDevices.hasMoreElements()) {
				RemoteDevice remoteDevice = (RemoteDevice) remoteDevices.nextElement();
				if ((remoteDevice != null) && (listener != null))
					listener.deviceDiscovered(remoteDevice, remoteDevice.getDeviceClass());
			}
			return true;
		} else {
			isInquiring = true;
			Runnable r = new Runnable() {
				public void run() {
					//LibLoader.cremeInit(this);

					cachedRemoteDevices = new Vector();
					//bluetoothStack.registerDiscoveryAgent(DiscoveryAgent.this);
					try {
						boolean ret = BlueZ.hciInquiry(0, 8, 10, accessCode, DiscoveryAgent.this);
						isInquiring = false;
						for (int u = 0; u < listeners.size(); u++) {
							try {
								((DiscoveryListener) listeners.elementAt(u))
										.inquiryCompleted(ret == true ? DiscoveryListener.INQUIRY_COMPLETED
												: DiscoveryListener.INQUIRY_ERROR);
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						for (int u = 0; u < listeners.size(); u++) {
							try {
								((DiscoveryListener) listeners.elementAt(u))
										.inquiryCompleted(DiscoveryListener.INQUIRY_ERROR);
							} catch (Throwable e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					} finally {
						isInquiring = false;
						//LibLoader.cremeOut(this);
					}
				}
			};
			try {
				BlueZ.executor.execute(r);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				new Thread(r).start();
			}

			return true;
		}
	}

	/*  End of the method  startInquiry     */

	/**
	 * Removes the device from inquiry mode. <P> An <code>inquiryCompleted()</code> event will occur with a type of
	 * <code>INQUIRY_TERMINATED</code> as a result of calling this method.  After receiving this
	 * event, no further <code>deviceDiscovered()</code> events will occur as a result of this inquiry. <P>
	 * This method will only cancel the inquiry if the <code>listener</code> provided is the listener that started
	 * the inquiry. Christian Lorenz: Acctually this implementation will not remove the device
	 * from inquiry mode, or cancel any running inquiry. It will only prevent the <code>listener</code> from receiving any
	 * further <code>deviceDiscovered()</code> events. This change in behavior is due to the distributed enviroment  which
	 * allows multiple <code>DiscoveryAgent</code> to be running on different hosts.
	 * @param listener the listener that is receiving inquiry events
	 * @return <code>true</code> if the inquiry was canceled; otherwise
	 * <code>false</code> if the inquiry was not canceled or if the inquiry was not started using <code>listener</code>
	 * @exception NullPointerException if <code>listener</code> is <code>null</code>
	 */
	public boolean cancelInquiry(DiscoveryListener listener) {
		if (listener == null)
			throw new NullPointerException("DiscoveryAgent.cancelInquiry: DiscoveryListener is null.");
		BlueZ.cancelInquiry();
		listeners.removeElement(listener);
		try {
			listener.inquiryCompleted(DiscoveryListener.INQUIRY_TERMINATED);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * The following method is called by some Stacks to notify of RemoteDevices that have been found.
	 *
	 */

	public synchronized void deviceDiscovered(RemoteDevice d) {
		cachedRemoteDevices.addElement(d);
		for (int i = 0; i < listeners.size(); i++) {
			((DiscoveryListener) listeners.elementAt(i)).deviceDiscovered(d, d.getDeviceClass());
		}
	}

	/**
	 * Searches for services on a remote Bluetooth device that have all the
	 * UUIDs specified in <code>uuidSet</code>.  Once the service is found,
	 * the attributes specified in <code>attrSet</code> and the default attributes are retrieved.  The default attributes are
	 * ServiceRecordHandle (0x0000), ServiceClassIDList (0x0001), ServiceRecordState (0x0002), ServiceID (0x0003), and
	 * ProtocolDescriptorList (0x0004).If <code>attrSet</code> is
	 * <code>null</code> then only the default attributes will be retrieved.
	 * <code>attrSet</code> does not have to be sorted in increasing order,
	 * but must only contain values in the range [0 - (2<sup>16</sup>-1)].
	 * @see DiscoveryListener
	 * @param attrSet indicates the attributes whose values will be
	 * retrieved on services which have the UUIDs specified in <code>uuidSet</code>
	 * @param uuidSet the set of UUIDs that are being searched for;  all
	 * services returned will contain all the UUIDs specified here
	 * @param btDev the remote Bluetooth device to search for services on
	 * @param discListener the object that will receive events when services are discovered
	 * @return the transaction ID of the service search; this number must be positive
	 * @exception BluetoothStateException if the number of concurrent
	 * service search transactions exceeds the limit specified by the
	 * <code>bluetooth.sd.trans.max</code> property obtained from the
	 * class <code>LocalDevice</code> or the system is unable to start one due to current conditions
	 * @exception IllegalArgumentException if <code>attrSet</code> has an illegal service attribute ID or exceeds the property
	 * <code>bluetooth.sd.attr.retrievable.max</code> defined in the class <code>LocalDevice</code>; if <code>attrSet</code>
	 * or <code>uuidSet</code> is of length 0; if <code>attrSet</code> or <code>uuidSet</code> contains duplicates
	 * @exception NullPointerException if <code>uuidSet</code>, <code>btDev</code>, or <code>discListener</code> is
	 * <code>null</code>; if an element in  <code>uuidSet</code> array is <code>null</code>
	 */
	public int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener myListener)
			throws BluetoothStateException, IllegalArgumentException {
		if (myListener == null)
			throw new NullPointerException("DiscoveryAgent.searchServices: DiscoveryListener is null.");
		if (attrSet == null || attrSet.length == 0) {
			attrSet = new int[] { 0x0000, 0x0001, 0x0002, 0x0003, 0x0004 };
		} else {
			int addatts = 0;
			for (int i = 0; i < attrSet.length; i++) {
				if (attrSet[i] > 4)
					addatts++;
			}
			int[] attrSet2 = new int[5 + addatts];
			for (int i = 0; i < 5; i++)
				attrSet2[i] = i;
			addatts = 0;
			for (int i = 0; i < attrSet.length; i++) {
				if (attrSet[i] > 4)
					attrSet2[5 + addatts++] = attrSet[i];
			}
			attrSet = attrSet2;

		}
		if (bluetoothStack == null)
			throw new IllegalArgumentException("Stack is not defined!!");
		return bluetoothStack.searchServices(attrSet, uuidSet, btDev, myListener);

	}

	/*  End of the method searchServices    */

	/**
	 * Cancels the service search transaction that has the specified
	 * transaction ID. The ID was assigned to the transaction by the method <code>searchServices()</code>. A
	 * <code>serviceSearchCompleted()</code> event with a discovery type
	 * of <code>SERVICE_SEARCH_TERMINATED</code> will occur when this method is called. After receiving this event, no further
	 * <code>servicesDiscovered()</code> events will occur as a result of this search.
	 * @param transID the ID of the service search transaction to cancel; returned by <code>searchServices()</code>
	 * @return <code>true</code> if the service search transaction is
	 * terminated, else <code>false</code>  if the <code>transID</code>
	 * does not represent an active service search transaction
	 * Christian Lorenz: This method has no effect. ServiceSearches
	 * will continue until they are completed. This method always returns true.
	 */
	public boolean cancelServiceSearch(int transID) {
		return bluetoothStack.cancelServiceSearch(transID);
	}

	/*  End of the method cancelServiceSearch   */

	/**
	 * Attempts to locate a service that contains <code>uuid</code> in the ServiceClassIDList of its service record.  This
	 * method will return a string that may be used in <code>Connector.open()</code> to establish a connection to the
	 * service.  How the service is selected if there are multiple services with <code>uuid</code> and which devices to
	 * search is implementation dependent.
	 * @see ServiceRecord#NOAUTHENTICATE_NOENCRYPT
	 * @see ServiceRecord#AUTHENTICATE_NOENCRYPT
	 * @see ServiceRecord#AUTHENTICATE_ENCRYPT
	 * @param uuid the UUID to search for in the ServiceClassIDList
	 * @param security specifies the security requirements for a connection to this service; must be one of
	 * <code>ServiceRecord.NOAUTHENTICATE_NOENCRYPT</code>, <code>ServiceRecord.AUTHENTICATE_NOENCRYPT</code>, or
	 * <code>ServiceRecord.AUTHENTICATE_ENCRYPT</code>
	 * @param master determines if this client must be the master of the
	 * connection; <code>true</code> if the client must be the master;
	 * <code>false</code> if the client can be the master or the slave
	 * @return the connection string used to connect to the service
	 * with a UUID of <code>uuid</code>; or <code>null</code> if no
	 * service could be found with a UUID of <code>uuid</code> in the ServiceClassIDList
	 * @exception BluetoothStateException if the Bluetooth system cannot
	 * start the request due to the current state of the Bluetooth system
	 * @exception NullPointerException if <code>uuid</code> is <code>null</code>
	 * @exception IllegalArgumentException if <code>security</code> is
	 * not <code>ServiceRecord.NOAUTHENTICATE_NOENCRYPT</code>, <code>ServiceRecord.AUTHENTICATE_NOENCRYPT</code>, or
	 * <code>ServiceRecord.AUTHENTICATE_ENCRYPT</code>
	 * The ServiceFoundException class encapsulates the number of services found.
	 * @return <code>The connection URL</code> if the service was found.<br>
	 *         <code>null</code> if the selected service does not have a valid connection URL.
	 * @author Julien Campana
	 */
	public synchronized String selectService(final UUID uuid, int security, boolean master)
			throws BluetoothStateException {
		if (uuid == null)
			throw new NullPointerException("UUID given in argument is null!!");
		if (security != ServiceRecord.AUTHENTICATE_ENCRYPT && security != ServiceRecord.AUTHENTICATE_NOENCRYPT
				&& security != ServiceRecord.NOAUTHENTICATE_NOENCRYPT)
			throw new IllegalArgumentException("Bad security argument");
		m_listener = new SelectListener();
		m_remoteServ = new Vector();
		//16 bits uuid????
		Runnable r = new Runnable() {
			public void run() {
				//LibLoader.cremeInit(this);

				if (cachedRemoteDevices.size() == 0)
					m_listener.setResponse(0);
				for (int i = 0; i < cachedRemoteDevices.size(); i++) {
					RemoteDevice d = (RemoteDevice) cachedRemoteDevices.elementAt(i);
					try {
						m_listener.searchFinished = false;
						searchServices(null, new UUID[] { uuid }, d, m_listener);
						while (!m_listener.searchFinished) {
							synchronized (this) {
								wait(100);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						m_listener.setResponse(0);
					}
				}
				m_listener.setResponse(1);
				//LibLoader.cremeOut(this);

			}
		};
		new Thread(r).start();
		while (m_listener.resp == -1) {
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
				return null;
			}
		}

		String back = null;
		try {
			if (m_remoteServ.size() > 0) {
				RemoteServiceRecord serv = (RemoteServiceRecord) m_remoteServ.elementAt(0);
				back = serv.getConnectionURL(security, master);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return back;
	}

	private class SelectListener implements DiscoveryListener {
		protected boolean searchFinished;
		private int resp = -1;

		public SelectListener() {
		}

		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		}

		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			for (int i = 0; i < servRecord.length; i++) {
				RemoteServiceRecord myRec = (RemoteServiceRecord) servRecord[i];
				m_remoteServ.addElement(myRec);
			}
		}

		public void serviceSearchCompleted(int transID, int respCode) {
			searchFinished = true;
		}

		public void setResponse(int i) {
			resp = i;
		}

		public void inquiryCompleted(int discType) {
		}

	}
}

/*  End of the definition of class DiscoveryAgent */

