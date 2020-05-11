/*
 *   
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */
package com.sun.midp.io.j2me.btspp;

import java.io.IOException;

import javax.bluetooth.BluetoothConnectionException;
import javax.microedition.io.Connection;

import com.sun.cldc.io.BluetoothProtocol;
import com.sun.cldc.io.BluetoothUrl;
import com.sun.midp.security.Permissions;
import com.sun.midp.security.SecurityToken;

import de.avetana.bluetooth.connection.Connector;


/**
 * Provides 'btspp' protocol support.
 */
public class Protocol extends BluetoothProtocol {
    /**
     * Constructs an instance.
     */
    public Protocol() {
        super(BluetoothUrl.RFCOMM);
    }
    
    /** 
     * Ensures URL parameters have valid values.
     * @param url URL to check
     * @exception IllegalArgumentException if invalid url parameters found
     */
    protected void checkUrl(BluetoothUrl url) 
            throws IllegalArgumentException, BluetoothConnectionException {
        super.checkUrl(url);
        
        if (!url.isServer && (url.port < 1 || url.port > 30)) {
            throw new IllegalArgumentException("Invalid channel: " + url.port);
        }
    }
    
    /**
     * Ensures that permissions are proper and creates client side connection.
     * @param token security token if passed by caller, or <code>null</code>
     * @param mode       I/O access mode
     * @return proper <code>BTSPPConnectionImpl</code> instance
     * @exception IOException if openning connection fails.
     */
    protected Connection clientConnection(SecurityToken token, int mode)
            throws IOException {
        checkForPermission(token, Permissions.BLUETOOTH_CLIENT);
        return Connector.open(url.toString(), mode);
    }
    
    /**
     * Ensures that permissions are proper and creates required notifier at 
     * server side.
     * @param token security token if passed by caller, or <code>null</code>
     * @param mode       I/O access mode
     * @return proper <code>BTSPPNotifierImpl</code> instance
     * @exception IOException if openning connection fails
     */
    protected Connection serverConnection(SecurityToken token, int mode)
            throws IOException {
        checkForPermission(token, Permissions.BLUETOOTH_SERVER);
        return Connector.open(url.toString(), mode);
    }
}
