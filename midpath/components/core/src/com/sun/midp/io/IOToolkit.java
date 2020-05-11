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
package com.sun.midp.io;

import org.thenesis.midpath.io.MemoryFileHandler;

import com.sun.midp.io.j2me.file.BaseFileHandler;
import com.sun.midp.io.j2me.serversocket.ServerSocketPeer;
import com.sun.midp.io.j2me.socket.SocketPeer;
import com.sun.midp.main.Configuration;

public class IOToolkit {

	private static IOToolkit toolkit = new IOToolkit();
	private String backendName;

	private IOToolkit() {
		backendName = Configuration.getPropertyDefault("com.sun.midp.io.backend", "J2SE");
	}

	public BaseFileHandler createBaseFileHandler() {
		if (backendName.equalsIgnoreCase("J2SE")) {
			return new org.thenesis.midpath.io.backend.j2se.FileHandlerImpl();
		} else if (backendName.equalsIgnoreCase("CLDC")) {
			return new org.thenesis.midpath.io.backend.cldc.FileHandlerImpl();
		} else {
			return new MemoryFileHandler();
		}
	}

	public SocketPeer getSocketPeer() {
		if (backendName.equalsIgnoreCase("J2SE")) {
			return new org.thenesis.midpath.io.backend.j2se.SocketPeerImpl();
		} else if (backendName.equalsIgnoreCase("CLDC")) {
			return new org.thenesis.midpath.io.backend.cldc.SocketPeerImpl();
		} else {
			return null;
		}
	}

	public ServerSocketPeer getServerSocketPeer() {
		if (backendName.equalsIgnoreCase("J2SE")) {
			return new org.thenesis.midpath.io.backend.j2se.ServerSocketPeerImpl();
		} else if (backendName.equalsIgnoreCase("CLDC")) {
			return null;
		} else {
			return null;
		}
	}

	public static IOToolkit getToolkit() {
		return toolkit;
	}

}
