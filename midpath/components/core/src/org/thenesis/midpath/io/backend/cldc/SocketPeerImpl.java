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
package org.thenesis.midpath.io.backend.cldc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.sun.midp.io.j2me.socket.SocketPeer;

/**
 * Socket peer based on the implementation provided by CLDC. 
 */
public class SocketPeerImpl implements SocketPeer {
	
	private InputStream dis;
	private OutputStream dos;
	StreamConnection connection;
	private String host;
	private int port;

	
	public int available() throws IOException {
		return dis.available();
	}

	public void close() throws IOException {
		connection.close();
	}

	public String getHost(boolean local) throws IOException {
		if (local) {
			return "localhost";
		} else {
			return host;
		}
	}

//	public int getIpNumber(String host, byte[] ipBytes_out) {
//		// Fake DNS ok
//		return 4;
//	}

	public int getPort(boolean local) throws IOException {
		return port;
	}

	public int getSockOpt(int option) throws IOException {
		return -1;
	}

//	public void open(byte[] szIpBytes, int port) throws IOException {
//		
//	}
	
	public void open(String hostname, int port) throws IOException {
		this.host = hostname;
		this.port = port;
		
		com.sun.cldc.io.j2me.socket.Protocol protocol = new com.sun.cldc.io.j2me.socket.Protocol();
		connection = (StreamConnection)protocol.openPrim("//" + hostname + ":" + port, Connector.READ_WRITE, true);
		dis = connection.openInputStream();
		dos = connection.openOutputStream();
	}

	
	public int read(byte[] b, int off, int len) throws IOException {
		return dis.read(b, off, len);
	}

	public void setSockOpt(int option, int value) throws IOException {
		// Do nothing
	}

	public void shutdownOutput() {
		try {
			dos.close();
		} catch (IOException e) {
			// Ignore
		}

	}

	public int write(byte[] b, int off, int len) throws IOException {
		dos.write(b, off, len);
		return len;
	}

}
