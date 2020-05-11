/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.io.backend.j2se;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.microedition.io.SocketConnection;

import com.sun.midp.io.j2me.socket.SocketPeer;

public class SocketPeerImpl implements SocketPeer {

	private Socket socket;
	private InputStream is;
	private OutputStream os;

	public SocketPeerImpl() {}
	
	SocketPeerImpl(Socket socket) throws IOException {
		this.socket = socket;
		is = socket.getInputStream();
		os = socket.getOutputStream();
	}
	
	/**
	 * Opens a TCP connection to a server.
	 *
	 * @param host hostname
	 * @param port TCP port at host
	 *
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void open(String host, int port) throws IOException {
		socket = new Socket(host, port);
		//System.out.println("[DEBUG] SocketPeerImpl.open(): " + socket.getInetAddress());
		is = socket.getInputStream();
		os = socket.getOutputStream();
	}

//	/**
//	 * Opens a TCP connection to a server.
//	 *
//	 * @param szIpBytes  raw IPv4 address of host
//	 * @param port       TCP port at host
//	 *
//	 * @exception  IOException  if an I/O error occurs.
//	 */
//	public void open(byte[] szIpBytes, int port) throws IOException {
//		InetAddress ia = InetAddress.getByAddress(szIpBytes);
//		socket = new Socket(ia, port);
//		//System.out.println("[DEBUG] SocketPeerImpl.open(): " + socket.getInetAddress());
//		is = socket.getInputStream();
//		os = socket.getOutputStream();
//	}

	/**
	 * Reads from the open socket connection.
	 *
	 * @param      b      the buffer into which the data is read.
	 * @param      off    the start offset in array <code>b</code>
	 *                    at which the data is written.
	 * @param      len    the maximum number of bytes to read.
	 * @return     the total number of bytes read into the buffer, or
	 *             <code>-1</code> if there is no more data because the end of
	 *             the stream has been reached.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int read(byte b[], int off, int len) throws IOException {
		//System.out.println("[DEBUG] SocketPeerImpl.read(): ");
		return is.read(b, off, len);
	}

	/**
	 * Writes to the open socket connection.
	 *
	 * @param      b      the buffer of the data to write
	 * @param      off    the start offset in array <code>b</code>
	 *                    at which the data is written.
	 * @param      len    the number of bytes to write.
	 * @return     the total number of bytes written
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int write(byte b[], int off, int len) throws IOException {
		os.write(b, off, len);
		return len;
	}

	/**
	 * Gets the number of bytes that can be read without blocking.
	 *
	 * @return     number of bytes that can be read without blocking
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int available() throws IOException {
		return is.available();
	}

	/**
	 * Closes the socket connection.
	 *
	 * @exception  IOException  if an I/O error occurs when closing the
	 *                          connection.
	 */
	public void close() throws IOException {
		socket.close();
		is.close();
		os.close();
	}

//	/**
//	 * Gets a byte array that represents an IPv4 or IPv6 address 
//	 *
//	 * @param      szHost  the hostname to lookup as a 'C' string
//	 * @param      ipBytes_out  Output array that receives the
//	 *             bytes of IP address
//	 * @return     number of bytes copied to ipBytes_out or -1 for an error
//	 */
//	public int getIpNumber(String host, byte[] ipBytes_out) {
//
//		try {
//			InetAddress ia = InetAddress.getByName(host);
//			byte[] address = ia.getAddress();
//			for (int i = 0; i < ipBytes_out.length; i++){
//				ipBytes_out[i] = address[i];
//			}
//			return ipBytes_out.length;
//		} catch (UnknownHostException e) {
//			//e.printStackTrace();
//			return -1;
//		}
//		
//		//System.out.println("[DEBUG] SocketPeerImpl.getIpNumber(): not implemented yet: " + new String(host));
//		
//	}

	/**
	 * Gets the requested IP number.
	 *
	 * @param      local   <tt>true</tt> to get the local host IP address, or
	 *                     <tt>false</tt> to get the remote host IP address
	 * @return     the IP address as a dotted-quad <tt>String</tt>
	 * @exception  IOException  if an I/O error occurs.
	 */
	public String getHost(boolean local) throws IOException {
		if (local) {
			return InetAddress.getLocalHost().getHostAddress();
		} else {
			return socket.getInetAddress().getHostAddress();
		}
	}

	/**
	 * Gets the requested port number.
	 *
	 * @param      local   <tt>true</tt> to get the local port number, or
	 *                     <tt>false</tt> to get the remote port number
	 * @return     the port number
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int getPort(boolean local) throws IOException {
		if (local) {
			return socket.getLocalPort();
		} else {
			return socket.getPort();
		}
	}

	/**
	 * Gets the requested socket option.
	 *
	 * @param      option  socket option to retrieve
	 * @return     value of the socket option
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int getSockOpt(int option) throws IOException {

		switch (option) {
		case SocketConnection.DELAY:
			return socket.getTcpNoDelay() ? 1 : 0;
		case SocketConnection.KEEPALIVE:
			return socket.getKeepAlive() ? 1 : 0;
		case SocketConnection.LINGER:
			if (socket.getSoLinger() == -1) {
				return 0;
			}
			return socket.getSoLinger();
		case SocketConnection.RCVBUF:
			return socket.getReceiveBufferSize();
		case SocketConnection.SNDBUF:
			return socket.getSendBufferSize();
		}

		return -1;
	}

	/**
	 * Sets the requested socket option.
	 *
	 * @param      option  socket option to set
	 * @param      value   the value to set <tt>option</tt> to
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void setSockOpt(int option, int value) throws IOException {
		switch (option) {
		case SocketConnection.DELAY:
			socket.setTcpNoDelay((value != 0) ? true : false);
			break;
		case SocketConnection.KEEPALIVE:
			socket.setKeepAlive((value != 0) ? true : false);
			break;
		case SocketConnection.LINGER:
			socket.setSoLinger((value != 0) ? true : false, value);
			break;
		case SocketConnection.RCVBUF:
			socket.setReceiveBufferSize(value);
			break;
		case SocketConnection.SNDBUF:
			socket.setSendBufferSize(value);
			break;
		}
	}

	/**
	 * Shuts down the output side of the connection.  Any error that might
	 * result from this operation is ignored.
	 */
	public void shutdownOutput() {
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//	private void checkIfOpen() {
	//		//if 
	//	}

}
