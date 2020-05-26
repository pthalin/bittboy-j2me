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

package com.sun.cldc.io;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.bluetooth.BluetoothConnectionException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

import com.sun.midp.midlet.MIDletStateHandler;
import com.sun.midp.midlet.MIDletSuite;
import com.sun.midp.security.SecurityToken;

/**
 * Provides abstract base for bluetooth protocols.
 */
public abstract class BluetoothProtocol implements ConnectionBaseInterface {
    /** Particular protocol type. */
    private int protocol;

    /** Keeps set of fields specified by URL. */
    protected BluetoothUrl url = null;

    /**
     * Constructs an instance.
     * @param protocol specifies particular protocol, must be one of <code>
     * BluetoothUrl.L2CAP, BluetoothUrl.RFCOMM, BluetoothUrl.OBEX </code>
     */
    protected BluetoothProtocol(int protocol) {
        this.protocol = protocol;
    }

    /**
     * Implements the <code>openPrim()</code> of
     * <code>ConnectionBaseInerface</code> and allows to get
     * connection by means of <code>Connector.open()</code>
     * call.
     *
     * @param name       the target for the connection
     * @param mode       I/O access mode
     * @param timeouts   ignored
     *
     * @return L2CAP connection open.
     * @exception IOException if opening connection fails.
     */
    public Connection openPrim(String name, int mode, boolean timeouts)
            throws IOException {
        return openPrim(null, name, mode, timeouts);
    }

    /**
     * Checks permissions and opens requested connection.
     *
     * @param token security token of the calling class
     * @param name the URL without protocol name and colon.
     * @param mode connector.READ_WRITE or connector.READ or connector.WRITE
     * @param timeouts ignored.
     *
     * @return a notifier in case of server connection string, open connection
     * in case of client one.
     *
     * @exception IOException if opening connection fails.
     */
    public Connection openPrim(Object token, String name, int mode,
        boolean timeouts) throws IOException {

        return openPrimImpl(token,
            new BluetoothUrl(protocol, name), mode);
    }

    /**
     * Checks permissions and opens requested connection.
     *
     * @param token security token passed by calling class
     * @param url <code>BluetoothUrl</code> instance that defines required
     *        connection stringname the URL without protocol name and colon
     * @param mode connector.READ_WRITE or connector.READ or connector.WRITE
     *
     * @return a notifier in case of server connection string, open connection
     * in case of client one.
     *
     * @exception IOException if opening connection fails.
     */
    protected Connection openPrimImpl(Object token, BluetoothUrl url, int mode)
                throws IOException {
        checkOpenMode(mode);
        checkUrl(url);
        this.url = url;

        return url.isServer?
            serverConnection((SecurityToken)token, mode):
            clientConnection((SecurityToken)token, mode);
    }

    /**
     * Ensures open mode requested is READ_WRITE or READ or WRITE
     *
     * @param mode open mode to be checked
     * @exception IllegalArgumentException if mode given is invalid
     *
     * IMPL_NOTE check if other modes are needed
     */
    private void checkOpenMode(int mode)  throws IllegalArgumentException {
        if (mode != Connector.READ_WRITE &&
            mode != Connector.READ &&
            mode != Connector.WRITE) {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }

    /**
     * Ensures URL parameters have valid values. This implementation contains
     * common checks and is called from subclasses before making protocol
     * specific ones.
     *
     * @param url URL to check
     * @exception IllegalArgumentException if invalid url parameters found
     * @exception BluetoothConnectionException if url parameters are not
     *            acceptable due to Bluetooth stack limitations
     */
    protected void checkUrl(BluetoothUrl url)
            throws IllegalArgumentException, BluetoothConnectionException {

        /*
         * IMPL_NOTE: revisit this code if TCK changes.
         * IllegalArgumentException seems to be right one here, not
         * BluetoothConnectionException. However TCK expects the latter
         * in several cases. Once IllegalArgumentException becomes
         * preferable this check can be placed to BluetoothUrl.
         * Questionable TCK tests:
         * bluetooth.Connector.Security.openClientTests,
         * bluetooth.Connector.Security.openServerTests
         */
        if ((url.encrypt || url.authorize) && !url.authenticate) {
            throw new BluetoothConnectionException(
                BluetoothConnectionException.UNACCEPTABLE_PARAMS,
                "Invalid Authenticate parameter");
        }
    }

    /**
     * Ensures that permissions are proper and creates client side connection.
     * @param token security token if passed by caller, or <code>null</code>
     * client side connection.
     * @param mode       I/O access mode
     * @return connection created, defined in subclasses
     * @exception IOException if opening connection fails.
     */
    protected abstract Connection clientConnection(SecurityToken token,
                                                   int mode)
            throws IOException;

    /**
     * Ensures that permissions are proper and creates required notifier at
     * server side.
     * @param token security token if passed by caller, or <code>null</code>
     * @param mode       I/O access mode
     * @return server notifier, defined in subclasses
     * @exception IOException if opening connection fails.
     */
    protected abstract Connection serverConnection(SecurityToken token,
                                                   int mode)
            throws IOException;

    /**
     * Makes sure caller has the com.sun.midp permission set to "allowed".
     *
     * @param token security token of the calling class, may be null
     * @param permission requested permission ID
     *
     * @exception IOInterruptedException if another thread interrupts the
     *        calling thread while this method is waiting to preempt the
     *        display.
     */
    protected void checkForPermission(SecurityToken token, int permission)
            throws InterruptedIOException {

        if (token != null) {
            token.checkIfPermissionAllowed(permission);
        } else {

            MIDletSuite midletSuite =
                MIDletStateHandler.getMidletStateHandler().getMIDletSuite();

            if (midletSuite != null) {
                try {
                    midletSuite.checkForPermission(permission,
                                                   url.getResourceName());
                } catch (InterruptedException ie) {
                    throw new InterruptedIOException(
                        "Interrupted while trying to ask the user permission");
                }
            }
        }
    }
}

