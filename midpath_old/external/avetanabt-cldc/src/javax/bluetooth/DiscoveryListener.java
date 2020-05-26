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
 * The <code>DiscoveryListener</code> interface allows an application
 * to receive device discovery and service discovery events. This interface provides four methods, two for discovering
 * devices and two for discovering services.
 */
public interface DiscoveryListener {
    /**
     * Indicates the normal completion of device discovery. Used with the <code>inquiryCompleted()</code> method. <P>
     * The value of <code>INQUIRY_COMPLETED</code> is 0x00 (0).
     * @see #inquiryCompleted
     * @see DiscoveryAgent#startInquiry
     */
    public static final int INQUIRY_COMPLETED = 0x00;

    /**
     * Indicates device discovery has been canceled by the application and did not complete.
     * Used with the <code>inquiryCompleted()</code> method. <P> The value of <code>INQUIRY_TERMINATED</code> is 0x05 (5).
     * @see #inquiryCompleted
     * @see DiscoveryAgent#startInquiry
     * @see DiscoveryAgent#cancelInquiry
     */
    public static final int INQUIRY_TERMINATED = 0x05;

    /**
     * Indicates that the inquiry request failed to complete normally, but was not cancelled. <P>
     * The value of <code>INQUIRY_ERROR</code> is 0x07 (7).
     * @see #inquiryCompleted
     * @see DiscoveryAgent#startInquiry
     */
    public static final int INQUIRY_ERROR = 0x07;

    /**
     * Indicates the normal completion of service discovery. Used with the <code>serviceSearchCompleted()</code> method. <P>
     * The value of <code>SERVICE_SEARCH_COMPLETED</code> is 0x01 (1).
     * @see #serviceSearchCompleted
     * @see DiscoveryAgent#searchServices
     */
    public static final int SERVICE_SEARCH_COMPLETED = 0x01;

    /**
     * Indicates the service search has been canceled by the application and did not complete.  Used with the
     * <code>serviceSearchCompleted()</code> method. <P> The value of <code>SERVICE_SEARCH_TERMINATED</code> is 0x02 (2).
     * @see #serviceSearchCompleted
     * @see DiscoveryAgent#searchServices
     * @see DiscoveryAgent#cancelServiceSearch
     */
    public static final int SERVICE_SEARCH_TERMINATED = 0x02;

    /**
     * Indicates the service search terminated with an error. Used with the <code>serviceSearchCompleted()</code> method. <P>
     * The value of <code>SERVICE_SEARCH_ERROR</code> is 0x03 (3).
     * @see #serviceSearchCompleted
     * @see DiscoveryAgent#searchServices
     */
    public static final int SERVICE_SEARCH_ERROR = 0x03;

    /**
     * Indicates the service search has completed with no service records found on the device.
     * Used with the <code>serviceSearchCompleted()</code> method. <P>
     * The value of <code>SERVICE_SEARCH_NO_RECORDS</code> is 0x04 (4).
     * @see #serviceSearchCompleted
     * @see DiscoveryAgent#searchServices
     */
    public static final int SERVICE_SEARCH_NO_RECORDS = 0x04;

    /**
     * Indicates the service search could not be completed because the remote device provided to
     * <code>DiscoveryAgent.searchServices()</code> could not be reached.
     * Used with the <code>serviceSearchCompleted()</code> method. <P>
     * The value of <code>SERVICE_SEARCH_DEVICE_NOT_REACHABLE</code> is 0x06 (6).
     * @see #serviceSearchCompleted
     * @see DiscoveryAgent#searchServices
     */
    public static final int SERVICE_SEARCH_DEVICE_NOT_REACHABLE = 0x06;

    /**
     * Called when a device is found during an inquiry.  An inquiry
     * searches for devices that are discoverable.  The same device may be returned multiple times.
     * @see DiscoveryAgent#startInquiry
     * @param btDevice the device that was found during the inquiry
     * @param cod the service classes, major device class, and minor device class of the remote device
     */
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod);

    /**
     * Called when service(s) are found during a service search.
     * @param transID the transaction ID of the service search that is posting the result
     * @param service a list of services found during the search request
     * @see DiscoveryAgent#searchServices
     */
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord);

    /**
     * Called when a service search is completed or was terminated because of an error.  Legal status values in the
     * <code>respCode</code> argument include <code>SERVICE_SEARCH_COMPLETED</code>, <code>SERVICE_SEARCH_TERMINATED</code>,
     * <code>SERVICE_SEARCH_ERROR</code>, <code>SERVICE_SEARCH_NO_RECORDS</code> and
     * <code>SERVICE_SEARCH_DEVICE_NOT_REACHABLE</code>.  The following
     * table describes when each <code>respCode</code> will be used: <TABLE>
     * <TR><TH><code>respCode</code></TH><TH>Reason</TH></TR> <TR><TD><code>SERVICE_SEARCH_COMPLETED</code></TD>
     * <TD>if the service search completed normally</TD></TR> <TR><TD><code>SERVICE_SEARCH_TERMINATED</code></TD>
     * <TD>if the service search request was cancelled by a call to
     * <code>DiscoveryAgent.cancelServiceSearch()</code></TD></TR> <TR><TD><code>SERVICE_SEARCH_ERROR</code></TD>
     * <TD>if an error occurred while processing the request</TD></TR> <TR><TD><code>SERVICE_SEARCH_NO_RECORDS</code></TD>
     * <TD>if no records were found during the service search</TD></TR>
     * <TR><TD><code>SERVICE_SEARCH_DEVICE_NOT_REACHABLE</code></TD>
     * <TD>if the device specified in the search request could not be
     * reached or the local device could not establish a connection to the remote device</TD></TR> </TABLE>
     * @param transID the transaction ID identifying the request which initiated the service search
     * @param respCode the response code that indicates the status of the transaction
     */
    public void serviceSearchCompleted(int transID, int respCode);

    /**
     * Called when an inquiry is completed. The <code>discType</code> will be
     * <code>INQUIRY_COMPLETED</code> if the inquiry ended normally or
     * <code>INQUIRY_TERMINATED</code> if the inquiry was canceled by a call to
     * <code>DiscoveryAgent.cancelInquiry()</code>.  The <code>discType</code>
     * will be <code>INQUIRY_ERROR</code> if an error occurred while
     * processing the inquiry causing the inquiry to end abnormally.
     * @see #INQUIRY_COMPLETED
     * @see #INQUIRY_TERMINATED
     * @see #INQUIRY_ERROR
     * @param discType the type of request that was completed; either
     * <code>INQUIRY_COMPLETED</code>, <code>INQUIRY_TERMINATED</code>, or <code>INQUIRY_ERROR</code>
     */
    public void inquiryCompleted(int discType);
}

/*  End of the interface DiscoveryListener  */

