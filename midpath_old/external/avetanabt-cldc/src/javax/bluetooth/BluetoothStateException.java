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
 * The <code>BluetoothStateException</code> is thrown when
 * a request is made to the Bluetooth system that
 * the system cannot support in its present state.  If, however, the
 * Bluetooth system was not in this state, it could support this operation.
 * For example, some Bluetooth systems do not allow the device to go into
 * inquiry mode if a connection is established.  This exception would be
 * thrown if <code>startInquiry()</code> were called.
 *
 * @version 1.0 February 11, 2002
 */
public class BluetoothStateException extends IOException {

    /**
     * Creates a new <code>BluetoothStateException</code> without a detail
     * message.
     */
    public BluetoothStateException() {
      super();
    }

    /**
     * Creates a <code>BluetoothStateException</code> with the specified
     * detail message.
     *
     * @param msg the reason for the exception
     */
    public BluetoothStateException(String msg) {
      super(msg);
    }
}
