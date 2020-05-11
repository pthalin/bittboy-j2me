package de.avetana.bluetooth.stack;

import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;

import com.sun.cldchi.jvm.JVM;

import de.avetana.bluetooth.connection.BTConnection;
import de.avetana.bluetooth.connection.ConnectionFactory;
import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.sdp.RemoteServiceRecord;
import de.avetana.bluetooth.util.BTAddress;

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
 * This class implements the de.avetana.bluetooth.stack.BluetoothStack class. <br>
 * Please refer to this abstract class for the documentation.
 *
 * @see de.avetana.bluetooth.stack.BluetoothStack
 * @author Julien Campana
 */
public class AvetanaBTStack extends BluetoothStack {

	private int m_bd; // bluetooth descriptor
	private int devID = -1; // bluetooth adapter
	private static RemoteServiceRecord myRecord;
	private static boolean fini = false;
	private static boolean initialized = false;
	private static final Object mutex = new Object();

	static {
		JVM.loadLibrary("libavetanabtcldc.so");
		//System.loadLibrary("avetanabtcldc");
	}

	public AvetanaBTStack() throws Exception {
		this(0);
	}

	public AvetanaBTStack(int devID) throws Exception {
		synchronized (mutex) {
			if (initialized)
				return;
			//LibLoader.loadBTLib();
			m_bd = BlueZ.hciOpenDevice(devID, new BlueZ());
			this.devID = devID;
			initialized = true;
		}
	}

	public void setDeviceID(int dev) throws Exception {
		if (BlueZ.myFactory.getConnections().size() != 0)
			throw new Exception("You must close before all connections");
		if (BlueZ.myFactory.getNotifiers().size() != 0)
			throw new Exception("You must close before all connection notifiers");
		BlueZ.myFactory = new ConnectionFactory();
		if (devID > -1) {
			try {
				BlueZ.hciCloseDevice(devID);
			} catch (Exception ex) {
			}
		}
		BlueZ.hciOpenDevice(dev, new BlueZ());
	}

	public String getRemoteName(String bd_addr) throws Exception {
		try {
			String addr = "";
			if (bd_addr.length() == 12) {
				addr = BTAddress.transform(bd_addr);
			} else
				addr = bd_addr;
			return BlueZ.hciRemoteName(m_bd, addr);
		} catch (Exception ex) {
			return "null";
		}
	}

	public int authenticate(RemoteDevice dev) throws Exception {
		BTConnection bs = isConnected(dev);
		//    if(bs==null || bs.getConnectionID() == -1) throw new Exception("This remote device is not connected!");
		return BlueZ.authenticate(bs == null ? -1 : bs.getConnectionID(), dev.getBTAddress().toString(), null);
	}

	public boolean getConnectionFlag(RemoteDevice dev, int pos) throws Exception {
		boolean b[] = getConnectionOptions(dev);
		if (b != null)
			return b[pos];
		return false;
	}

	private boolean[] getConnectionOptions(RemoteDevice dev) throws Exception {
		BTConnection bs = isConnected(dev);
		if (bs == null || bs.getConnectionID() == -1)
			throw new Exception("This remote device is not connected!");
		return BlueZ.connectionOptions(bs.getConnectionID(), dev.getBTAddress().toString());
	}

	public int encrypt(Connection conn, RemoteDevice dev, boolean encrypt) throws Exception {
		if (conn == null || ((BTConnection) conn).getConnectionID() == -1)
			throw new Exception("This remote device is not connected!");
		return BlueZ.encrypt(((BTConnection) conn).getConnectionID(), dev.getBTAddress().toString(), encrypt);
	}

	public BTConnection isConnected(RemoteDevice dev) {
		return BlueZ.myFactory.isConnected(dev);
	}

	public String getLocalDeviceAddress() throws java.lang.Exception {
		return BlueZ.hciDevBTAddress(devID).toStringSep(false);
	}

	public String getLocalDeviceName() throws java.lang.Exception {
		return BlueZ.hciLocalName(m_bd);
	}

	private synchronized int searchServices(int[] attrSet, byte[][] uuidSet, RemoteDevice btDev,
			DiscoveryListener myListener) {
		String addr = btDev.getBluetoothAddress();
		try {
			addr = BTAddress.transform(addr);
		} catch (Exception ex) {
		}
		return BlueZ.searchServices(addr, uuidSet, attrSet, myListener);

	}

	public int searchServices(final int[] attrSet, UUID[] uuidSet, RemoteDevice btDev,
			final DiscoveryListener myListener) {
		byte[][] uuidSetB;
		if (uuidSet == null || uuidSet.length == 0) {
			uuidSetB = new byte[0][0];
		} else {
			uuidSetB = new byte[uuidSet.length][];
			for (int i = 0; i < uuidSetB.length; i++) {
				try {
					uuidSetB[i] = uuidSet[i].toByteArray128();
				} catch (Exception ex) {
					throw new IllegalArgumentException("UUID must be 16 bits length!!!!!");
				}
			}
		}
		return searchServices(attrSet, uuidSetB, btDev, myListener);
	}

	public int getClassOfDevice() throws java.lang.Exception {
		return BlueZ.getDeviceClass(m_bd);
	}

	public void closeDevice() throws Exception {
		BlueZ.hciCloseDevice(m_bd);
	}

	public int getDiscoverableMode() throws Exception {
		return BlueZ.getAccessMode(m_bd);
	}

	public int setDiscoverableMode(int mode) throws Exception {
		return BlueZ.setAccessMode(m_bd, mode);
	}

	public Connection openRFCommConnection(JSR82URL url, int timeout) throws Exception {
		return de.avetana.bluetooth.rfcomm.RFCommConnectionImpl.createRFCommConnection(url, timeout);
	}

	public Connection openL2CAPConnection(JSR82URL url, int timeout) throws Exception {
		return de.avetana.bluetooth.l2cap.L2CAPConnectionImpl.createL2CAPConnection(url, timeout);
	}

	public boolean cancelServiceSearch(int transID) {
		return BlueZ.cancelServiceSearch(transID);
	}

	public int updateService(ServiceRecord rec, long recordHandle) throws Exception {
		return BlueZ.updateService(rec, recordHandle);
	}

}
