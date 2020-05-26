package de.avetana.bluetooth.l2cap;


import java.io.IOException;
import java.io.InterruptedIOException;

import javax.bluetooth.L2CAPConnection;

import de.avetana.bluetooth.connection.BTConnection;
import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.stack.BlueZ;

/**
 * The implementation of the javax.bluetooth.L2CAPConnection class.
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
 * <b>Description: </b><br>
 * This class is an implementation of the javax.bluetooth.L2CAPConnection class and
 * opens a new L2CAP connection with a remote BT device
 * @see javax.bluetooth.L2CAPConnection
 */

public class L2CAPConnectionImpl extends BTConnection implements L2CAPConnection{

  protected int m_transmitMTU=672;
  protected int m_receiveMTU=672;
  private JSR82URL m_connectionURL;

  /**
   * Creates a new instance of L2CAPConnectionImpl and set the connection ID
   * @param fid the connection ID
   */
  public L2CAPConnectionImpl(int fid) {
    super(fid);
  }

  /**
   * Creates a new instance of L2CAPConnectionImpl. Set the connection ID and the
   * remote device
   * @param fid The connection ID
   * @param addr The address of the remote device
   */
  public L2CAPConnectionImpl(int fid, String addr) {
    super(fid,addr);
  }

  /**
   * Sets the connection URL.
   * @param a_url The new connection URL
   */
  public void setConnectionURL(JSR82URL a_url) {
    m_connectionURL=a_url;
  }

  /**
   * Creates a new instance of L2CAPConnectionImpl.
   * Initializes the data buffer, the different MTU values and the connection properties (Remote device, connection ID)
   * @param fid The connection ID
   * @param addr The address of the remote device
   * @param transmit The desired value of transmitMTU
   * @param receive The desire value of receiveMTU
   */
  protected L2CAPConnectionImpl(int fid, String addr, int transmit, int receive) {
    super(fid, addr);
    this.m_transmitMTU=transmit;
    this.m_receiveMTU=receive;
  }

  /**
   * Creates a new L2CAP connection with a remote BT device. All connection parameters (receiveMTU, transmitMTU,..etc..)
   * are encapsulated in the connection URL.
   * @param url The connection URL
   * @return An instance of L2CAPConnection if the establishment of the connection did succeed.
   * @throws Exception
   */
  public static L2CAPConnection createL2CAPConnection (JSR82URL url, int timeout) throws Exception{
    String addrB=null;
    L2CAPConnParam param = null;

    param=BlueZ.openL2CAP(url, timeout);
    if(param==null || param.m_fid==-1) throw new Exception("Connection could not be established!");

    int fid=param.m_fid;
    int psm=url.getAttrNumber();
    L2CAPConnectionImpl conn =  null;
    try {
      conn=new L2CAPConnectionImpl (fid,
                                    url.getBTAddress().toString(),
                                    param.m_transmitMTU,
                                    param.m_receiveMTU);
    }
    catch(Exception ex) {
       throw ex;
    }
    return conn;
  }

  /**
   * Returns the MTU that the remote device supports. This value
   * is obtained after the connection has been configured. If the
   * application had specified TransmitMTU in the <code>Connector.open()</code>
   * string then this value should be equal to that. If the application did
   * not specify any TransmitMTU, then this value should be  less than or
   * equal to the ReceiveMTU the remote device advertised during channel configuration.
   * @return the maximum number of bytes that can be sent in a single call to <code>send()</code> without losing any data
   * @exception IOException if the connection is closed
     */
  public int getTransmitMTU() throws IOException{
    return this.m_transmitMTU;
  }

  /**
   * Returns the ReceiveMTU that the connection supports. If the
   * connection string did not specify a ReceiveMTU, the value returned will be
   * less than or equal to the <code>DEFAULT_MTU</code>. Also, if the connection
   * string did specify an MTU, this value will be less than or equal to the value specified in the connection string.
   * @return the maximum number of bytes that can be read in a single call to <code>receive()</code>
   * @exception IOException if the connection is closed
     */
  public int getReceiveMTU() throws IOException {
    return this.m_receiveMTU;
  }

  /**
   * Reads a packet of data. The amount of data received in this operation is related to the value of ReceiveMTU.  If
   * the size of <code>inBuf</code> is greater than or equal to ReceiveMTU, then
   * no data will be lost. Unlike  <code>read()</code> on an
   * <code>java.io.InputStream</code>, if the size of <code>inBuf</code> is
   * smaller than ReceiveMTU, then the portion of the L2CAP payload that will
   * fit into <code>inBuf</code> will be placed in <code>inBuf</code>, the
   * rest will be discarded. If the application is aware of the number of
   * bytes (less than ReceiveMTU) it will receive in any transaction, then
   * the size of <code>inBuf</code> can be less than ReceiveMTU and no data
   * will be lost.  If <code>inBuf</code> is of length 0, all data sent in
   * one packet is lost unless the length of the packet is 0.
   * @param inBuf byte array to store the received data
   * @return the actual number of bytes read; 0 if a zero length packet is received; 0 if <code>inBuf</code> length is zero
   * @exception IOException if an I/O error occurs or the connection has been closed
   * @exception InterruptedIOException if the request timed out
   * @exception NullPointerException if <code>inBuf</code> is <code>null</code>
   */
  public int receive(byte[] inBuf) throws IOException, NullPointerException {
	  	byte[] b = read(0);
	  	
	    int rlen = b.length;
	    if (rlen > inBuf.length) rlen = inBuf.length;
	    System.arraycopy(b, 0, inBuf, 0, rlen);
	    return rlen;
  }

  /**
   * Determines if there is a packet that can be read via a call to <code>receive()</code>.  If <code>true</code>, a call to
   * <code>receive()</code> will not block the application.
   * @see #receive
   * @return <code>true</code> if there is data to read; <code>false</code> if there is no data to read
   * @exception IOException if the connection is closed
   */
  public synchronized boolean ready() throws IOException {
		return available() > 0;
  }

  /**
   * Requests that data be sent to the remote device. The TransmitMTU
   * determines the amount of data that can be successfully sent in
   * a single send operation. If the size of <code>data</code> is
   * greater than the TransmitMTU, then only the first TransmitMTU bytes
   * of the packet are sent, and the rest will be discarded.  If
   * <code>data</code> is of length 0, an empty L2CAP packet will be sent.
   * @param data data to be sent
   * @exception IOException if <code>data</code> cannot be sent successfully or if the connection is closed
   * @exception NullPointerException if the <code>data</code> is <code>null</code>
   */
  public void send(byte[] data) throws IOException {
    if(data==null) throw new NullPointerException("The buffer is null!");
    else if (data.length == 0) throw new IOException ("Sending of 0-length packet not supported");
    if(closed) throw new IOException("Connection closed");
    BlueZ.writeBytesS(fid, data, 0, Math.min (m_transmitMTU, data.length));
  }
}
