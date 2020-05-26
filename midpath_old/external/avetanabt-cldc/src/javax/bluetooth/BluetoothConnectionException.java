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


package javax.bluetooth;

import java.io.IOException;

/**
 * This <code>BluetoothConnectionException</code> is thrown when a Bluetooth connection (L2CAP, RFCOMM, or OBEX over RFCOMM)
 * cannot be established successfully. The fields in this exception class indicate the cause of the exception.
 * For example, an L2CAP connection may fail due to a security problem.
 * This reason is passed on to the application through this class.
 * @author Julien Campana
 */
public class BluetoothConnectionException extends IOException {
    private int m_status;

    /**
     * Indicates the connection to the server failed because no service for the given PSM was registered. <P>
     * The value for <code>UNKNOWN_PSM</code> is 0x0001 (1).
     */
    public static final int UNKNOWN_PSM = 0x0001;

    /**
     * Indicates the connection failed because the security settings on the local device or the remote device were
     * incompatible with the request. <P> The value for <code>SECURITY_BLOCK</code> is 0x0002 (2).
     */
    public static final int SECURITY_BLOCK = 0x0002;

    /**
     * Indicates the connection failed due to a lack of resources either on the local device or on the remote device. <P>
     * The value for <code>NO_RESOURCES</code> is 0x0003 (3).
     */
    public static final int NO_RESOURCES = 0x0003;

    /**
     * Indicates the connection to the server failed due to unknown reasons. <P>
     * The value for <code>FAILED_NOINFO</code> is 0x0004 (4).
     */
    public static final int FAILED_NOINFO = 0x0004;

    /** Indicates the connection to the server failed due to a timeout. <P> The value for <code>TIMEOUT</code> is 0x0005 (5). */
    public static final int TIMEOUT = 0x0005;

    /**
     * Indicates the connection failed because the configuration parameters provided were not acceptable to either the remote
     * device or the local device. <P> The value for <code>UNACCEPTABLE_PARAMS</code> is 0x0006 (6).
     */
    public static final int UNACCEPTABLE_PARAMS = 0x0006;

    /*
    * The following section defines public, static and instance
    * member methods used in the implementation of the methods.
    */

    /**
     * Creates a new <code>BluetoothConnectionException</code> with the error indicator specified.
     * @param error indicates the exception condition; must be one of the constants described in this class
     * @exception IllegalArgumentException if the input value is not one of the constants in this class
     */
    public BluetoothConnectionException(int error) {
        super();
        m_status = error;
    }

    /*  End of the constructor  */

    /**
     * Creates a new <code>BluetoothConnectionException</code> with the error indicator and message specified.
     * @param error indicates the exception condition; must be one of the constants described in this class
     * @param msg a description of the exception; may by <code>null</code>
     * @exception IllegalArgumentException if the input value is not one of the constants in this class
     */
    public BluetoothConnectionException(int error, String msg) {
        super(msg);
        m_status = error;

    }

    /*  End of the constructor  */

    /**
     * Gets the status set in the constructor that will indicate the reason for the exception.
     * @return cause for the exception; will be one of the constants defined in this class
     */
    public int getStatus() { return m_status; }

    /*  End of the getStatus method */
}

/*  End of the BluetoothConnectionException     */

