package de.avetana.bluetooth.connection;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;

import de.avetana.bluetooth.stack.BlueZ;

/**
 * The top-level connection class.
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
 * The top-level connection class. Each connection (whatever the protocol)  can be described with at least three
 * variables:
 * <ul>
 * <li>A connection ID</li>
 * <li>A remote device</li>
 * <li>A boolean telling if the connection is closed or still opened</li>
 * </ul>
 * The advantage of such a top-level class is classical: a connection factory can work only with BTConnection instances
 * without special handling and casting for each protocol.<br>
 * That's why the L2CAPConnectionImpl as well as the RFCommConnection class extends BTConnection. If someone
 * wants to add a new protocol to this JSR82 implementation (like OBEX or SCO), I would suggest that every
 * class dealing with connection (or at least every Object returned by a call to the static method Connector.Open(...))
 * extends this BTConnection class
 *
 *
 * @author Julien Campana
 */
public class BTConnection {

  /**
   * If <code>true</code>, the connection was closed by the client or the server. Default value is <code>false</code>
   */
  protected boolean closed = false;

  /**
   * The connection ID
   */
  protected int fid;

  /**
   * The remote device the local BT device is connected to.
   */
  protected RemoteDevice m_remote;

  /**
   * This is where incoming data is buffered
   */
  
  protected Vector dataBuffer;

  private Object readLock;
  
  /**
   * Creates a new instance of BTConnection and set the value of the connection ID
   * @param a_fid The connection ID
   */
  protected BTConnection(int a_fid) {
	readLock = new Object();
    fid=a_fid;
    dataBuffer = new Vector();
    BlueZ.myFactory.addConnection(this);
  }

  /**
   * Creates a new instance of BTConnection. Set the value of the connection ID and the address
   * of the remote device
   * @param fid The connection ID
   * @param addr The address of the remote device
   */
  protected BTConnection(int fid, String addr) {
    this(fid);
    m_remote=new RemoteDevice(addr);
  }

  /**
   * Sets the remote device
   * @param a_device The new remote device
   */
  public void setRemoteDevice(RemoteDevice a_device) {m_remote=a_device;}

  /**
   * Returns the remote device
   * @return The remote device
   */
  public RemoteDevice getRemoteDevice() {return m_remote;}

  /**
   * Returns the state of the connection
   * @return <code>true</code> - If the connection is closed.<br>
   *         <code>false</code> - Otherwise
   */
  public boolean isClosed() {return closed;}

  /**
   * Returns the connection ID
   * @return The connection ID
   */
  public int getConnectionID() {return fid;}

  /**
   * Closes the current connection
   */
  public synchronized void close() {
    closed=true;
    if  (fid > 0) {
      BlueZ.closeConnectionS(fid);
      BlueZ.myFactory.removeConnection(this);
    }
    fid = 0;
    notifyAll();
  }
	
	/**
	 * 
	 * Called from BlueZ when new data has arrived for that connection
	 * @param data
	 */
	public void newData(byte[] data) {
		synchronized (readLock) {
			dataBuffer.addElement (data);
			readLock.notify();
		}
	}
	
	/**
	 * Read the most recent packet of data (L2CAP) or all data available < len for RFComm
	 * 
	 * @param len 0 for L2CAP reading of one packet or > 0 for RFComm to specify the maximum number of bytes requested
	 * @return array with data.
	 * @throws IOException if the connection is closed.
	 */
	
	protected byte[] read(int len) throws IOException {
		synchronized (readLock) {
		while (dataBuffer.size() == 0) {
			try { readLock.wait (100); } catch (Exception e) {}
			if (closed) throw new IOException ("Connection closed");
		}
		
		if (len == 0) { //L2CAP return uppermost packet
			byte[] b = (byte[])dataBuffer.elementAt(0);
			dataBuffer.removeElementAt(0);
			return b;
		}
		
		byte[] bret = new byte[Math.min (available(), len)];
		int pos = 0;
		
		while (pos < bret.length) {
			byte[] b = (byte[])dataBuffer.elementAt(0);
		
			if (b.length + pos > len) { // RFComm and the uppermost packet is longer than what is requested
				int remain = len - pos;
				byte b3[] = new byte[b.length - remain];
				System.arraycopy (b, 0, bret, pos, remain);
				System.arraycopy (b, remain, b3, 0, b3.length);
				dataBuffer.setElementAt(b3, 0);
				pos += remain;
			} else { // RFComm and len is longer than or equal the data available
				System.arraycopy(b, 0, bret, pos, b.length);
				dataBuffer.removeElementAt(0);
				pos += b.length;
			}
		}
		return bret;
		}
	}
	
	protected synchronized int available() throws IOException {
		if (closed && dataBuffer.size() == 0)  throw new IOException ("Connection closed");
		int size = 0;
		for (int i = 0;i < dataBuffer.size();i++)
			size += ((byte[])dataBuffer.elementAt(i)).length;
		return size;
	}
	
}