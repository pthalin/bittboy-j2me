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
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.midp.io.j2me.serversocket.ServerSocketPeer;

public class ServerSocketPeerImpl implements ServerSocketPeer {
	
	ServerSocket serverSocket;
	
	/* (non-Javadoc)
	 * @see org.thenesis.midp2.io.ServerSocketPeer#open(int, byte[])
	 */
	public void open(int port, byte[] suiteID) throws IOException {
		// FIXME what to do with suiteID ?
		serverSocket = new ServerSocket(port);
	}

	/* (non-Javadoc)
	 * @see org.thenesis.midp2.io.ServerSocketPeer#close()
	 */
	public void close() throws IOException {
		serverSocket.close();
	}

	/* (non-Javadoc)
	 * @see org.thenesis.midp2.io.ServerSocketPeer#accept(com.sun.midp.io.j2me.socket.Protocol)
	 */
	public void accept(com.sun.midp.io.j2me.socket.Protocol con) throws IOException {
		Socket socket = serverSocket.accept();
		SocketPeerImpl socketPeer = new SocketPeerImpl(socket);
		con.setSocketPeer(socketPeer);
	}

	/* (non-Javadoc)
	 * @see org.thenesis.midp2.io.ServerSocketPeer#getLocalAddress()
	 */
	public String getLocalAddress() {
		return serverSocket.getInetAddress().getHostAddress();
	}

	/* (non-Javadoc)
	 * @see org.thenesis.midp2.io.ServerSocketPeer#getLocalPort()
	 */
	public int getLocalPort() {
		return serverSocket.getLocalPort();
	}
	

}
