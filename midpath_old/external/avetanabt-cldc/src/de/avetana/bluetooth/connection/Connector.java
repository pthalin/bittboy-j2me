/*
 * Created on 09.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.connection;
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import de.avetana.bluetooth.l2cap.L2CAPConnectionNotifierImpl;
import de.avetana.bluetooth.obex.OBEXConnection;
import de.avetana.bluetooth.obex.SessionNotifierImpl;
import de.avetana.bluetooth.rfcomm.RFCommConnectionImpl;
import de.avetana.bluetooth.rfcomm.RFCommConnectionNotifierImpl;
import de.avetana.bluetooth.stack.BluetoothStack;

/**
 * This class the RFCOMM, OBEX and L2CAP Protocol.
 *
 * Remote (btspp://010203040506:1;master=false)
 * or
 * local (btspp://localhost:3B9FA89520078C303355AAA694238F07:1;name=Avetana Service;) URLs
 * are supported. The class JSR82URL verifies that the URL is a correct Bluetooth
 * connection URL, which matches the RFC 1808 specification.
 * (see http://www.w3.org/Addressing/rfc1808.txt for more information).
 *
 *
 * @todo Move auth / enc / authorize test to native code
 */
public class Connector {
    public static final int READ       = 0;
    public static final int WRITE      = 0;
    public static final int READ_WRITE = 0;
    private static BluetoothStack stack = null;

    public static Connection open(String url) throws IOException {
    	return openWithTimeout(url, 60000);
    }
    
    public static Connection openWithTimeout(String url, int timeout) throws IOException {
    		if (stack == null) {
			try {
				stack = BluetoothStack.getBluetoothStack();	
			} catch (Exception e) { throw new IOException (e.getMessage()); }
		}
   
            try {
          JSR82URL myURL=new JSR82URL(url);
          if(myURL.getBTAddress()==null) {
            if(myURL.getProtocol()==JSR82URL.PROTOCOL_RFCOMM) return new RFCommConnectionNotifierImpl(myURL);
            else if(myURL.getProtocol()==JSR82URL.PROTOCOL_L2CAP) return new L2CAPConnectionNotifierImpl(myURL);
            else if(myURL.getProtocol() == JSR82URL.PROTOCOL_OBEX) 
            return new SessionNotifierImpl (new RFCommConnectionNotifierImpl (myURL));
          }
          else {
            if(myURL.getProtocol() == JSR82URL.PROTOCOL_RFCOMM)
              return stack.openRFCommConnection(myURL, timeout);
            else if(myURL.getProtocol() == JSR82URL.PROTOCOL_L2CAP)
              return stack.openL2CAPConnection(myURL, timeout);
            if(myURL.getProtocol() == JSR82URL.PROTOCOL_OBEX)
                return new OBEXConnection ((RFCommConnectionImpl)stack.openRFCommConnection(myURL, timeout));
          }
        }
        catch (BluetoothStateException e) { throw new IOException("" + e); }
        catch (Exception e) { throw new IOException("" + e); }
        throw new IllegalArgumentException(url+" is not a valid Bluetooth connection URL");
    }

    /**
     * Create and open a Connection.
     * @param url The URL for the connection.
     * @param mode The access mode
     * @return A new Connection object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * This call is equivilant to Connection.open(url).
     */
    public static Connection open(String url, int mode) throws IOException {
    		return open (url);
    }

    /**
     * Create and open a Connection
     * @param url The URL for the connection.
     * @param mode The access mode
     * @param timeouts A flag to indicate that the called wants timeout exceptions
     * @return A new Connection object.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     * This call is equivilant to Connection.open(url).
     */
    public static Connection open(String url, int mode, boolean timeouts) throws IOException {
    		 return open (url);
    }

    /**
     * Create and open a connection dataInputStream. 
     * @param url The URL for the connection.
     * @return A DataInputStream.
     * @throws IOException
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     */
    public static DataInputStream openDataInputStream(String url) throws Exception {
        return new DataInputStream (openInputStream (url));
     }

    /**
     * Create and open a connection dataOutput stream. 
     * @param url The URL for the connection.
     * @return A DataOutputStream.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     */
    public static DataOutputStream openDataOutputStream(String url) throws Exception {
       return new DataOutputStream (openOutputStream (url));
    }

    /**
     * Create and open a connection input stream. 
     * @param url The URL for the connection.
     * @return A InputStream.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     */
    public static InputStream openInputStream(String url) throws Exception {
    		JSR82URL jurl = new JSR82URL (url);
    		if (jurl.getProtocol() != JSR82URL.PROTOCOL_RFCOMM) throw new IOException ("Only RFComm connection provide an InputStream");
    		Connection con = open (url);
    		if (con instanceof StreamConnection) return ((StreamConnection)con).openInputStream();
    		else if (con instanceof StreamConnectionNotifier) return ((StreamConnectionNotifier)con).acceptAndOpen().openInputStream();
    		else throw new IOException ("Could not get Stream from connection");
 
    }

    /**
     * Create and open a connection output stream. 
     * @param url The URL for the connection.
     * @return A DataOutputStream.
     * @throws IllegalArgumentException If a parameter is invalid.
     * @throws ConnectionNotFoundException If the connection cannot be found.
     * @throws IOException If some other kind of I/O error occurs.
     */
    public static OutputStream openOutputStream(String url) throws Exception { 
   		JSR82URL jurl = new JSR82URL (url);
		if (jurl.getProtocol() != JSR82URL.PROTOCOL_RFCOMM) throw new IOException ("Only RFComm connection provide an OutputStream");
		Connection con = open (url);
		if (con instanceof StreamConnection) return ((StreamConnection)con).openOutputStream();
		else if (con instanceof StreamConnectionNotifier) return ((StreamConnectionNotifier)con).acceptAndOpen().openOutputStream();
		else throw new IOException ("Could not get Stream from connection");
   }
    
 }

