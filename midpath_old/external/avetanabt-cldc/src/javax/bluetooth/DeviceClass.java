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

/**
 * The <code>DeviceClass</code> class represents the class of device (CoD)
 * record as defined by the Bluetooth specification.  This record is defined in the Bluetooth Assigned Numbers document
 * and contains information on the type of the device and the type of services available on the device. <P>
 * The Bluetooth Assigned Numbers document (<A HREF="http://www.bluetooth.org/assigned-numbers/baseband.htm">
 * http://www.bluetooth.org/assigned-numbers/baseband.htm</A>) defines the service class, major device class, and minor device
 * class.  The table below provides some examples of possible return values and their meaning: <TABLE>
 * <TR><TH>Method</TH><TH>Return Value</TH><TH>Class of Device</TH></TR> <TR><TD><code>getServiceClasses()</code></TD>
 * <TD>0x22000</TD> <TD>Networking and Limited Discoverable Major Service Classes</TD></TR>
 * <TR><TD><code>getServiceClasses()</code></TD> <TD>0x100000</TD> <TD>Object Transfer Major Service Class</TD></TR>
 * <TR><TD><code>getMajorDeviceClass()</code></TD> <TD>0x00</TD> <TD>Miscellaneous Major Device Class</TD></TR>
 * <TR><TD><code>getMajorDeviceClass()</code></TD> <TD>0x200</TD> <TD>Phone Major Device Class</TD></TR>
 * <TR><TD><code>getMinorDeviceClass()</code></TD> <TD>0x0C</TD><TD>With a Computer Major Device Class,
 * Laptop Minor Device Class</TD></TR> <TR><TD><code>getMinorDeviceClass()</code></TD>
 * <TD>0x04</TD><TD>With a Phone Major Device Class, Cellular Minor Device Class</TD></TR> </TABLE>
 * @author Julien Campana
 */
public class DeviceClass {
    public int record;

    /**
     * Creates a <code>DeviceClass</code> from the class of device record
     * provided.  <code>record</code> must follow the format of the class of device record in the Bluetooth specification.
     * @param record describes the classes of a device
     * @exception IllegalArgumentException if <code>record</code> has any bits between 24 and 31 set
     */
    public DeviceClass(int record) {
        if (record >= 0x1000000) throw new IllegalArgumentException("DeviceClass has bits between 24 and 31 set.");
        this.record = record;
    }

    /**
     * Retrieves the major service classes.  A device may have multiple major
     * service classes.  When this occurs, the major service classes are bitwise OR'ed together.
     * @return the major service classes
     */
    public int getServiceClasses() { return (int)((record & 0xffe000)); }

    /**
     * Retrieves the major device class.  A device may have only a single major device class.
     * @return the major device class
     */
    public int getMajorDeviceClass() { return (int)((record & 0x001f00)); }

    /**
     * Retrieves the minor device class.
     * @return the minor device class
     */
    public int getMinorDeviceClass() { return (int)(record & 0x0000fc); }
}

