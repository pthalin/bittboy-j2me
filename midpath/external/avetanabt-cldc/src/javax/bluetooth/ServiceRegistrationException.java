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
 * The <code>ServiceRegistrationException</code> is thrown when there is a failure to add
 * a service record to the local Service Discovery Database (SDDB) or to modify
 * an existing service record in the SDDB.  The failure could be because the
 * SDDB has no room for new records or because the modification being
 * attempted to a service record violated one of the rules about
 * service record updates.  This exception will also be thrown if it
 * was not possible to obtain an RFCOMM server channel needed for a <code>btspp</code> service record.
 */
public class ServiceRegistrationException extends IOException {
    /** Creates a <code>ServiceRegistrationException</code> without a detailed message. */
    public ServiceRegistrationException() { 
    		super();
    }

    /**
     * Creates a <code>ServiceRegistrationException</code> with a detailed message.
     * @param msg the reason for the exception
     */
    public ServiceRegistrationException(String msg) { 
    		super (msg);
    	}
}

