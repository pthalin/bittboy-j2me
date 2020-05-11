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
 * The <code>ServiceRecord</code> interface describes characteristics of a
 * Bluetooth service.  A <code>ServiceRecord</code> contains a
 * set of service attributes, where each service attribute is an (ID,
 * value) pair. A Bluetooth attribute ID is a 16-bit unsigned integer, and an attribute value is a <code>DataElement</code>.
 * <p> The structure and use of service records is specified by the
 * Bluetooth specification in the Service Discovery Protocol (SDP)
 * document.  Most of the Bluetooth Profile specifications also
 * describe the structure of the service records used by the Bluetooth services that conform to the profile. <p>
 * An SDP Server maintains a Service Discovery Database (SDDB) of
 * service records that describe the services on the local device. Remote SDP clients can use the SDP to query an SDP server
 * for any service records of interest.  A service record provides
 * sufficient information to allow an SDP client to connect to the Bluetooth service on the SDP server's device. <p>
 * <code>ServiceRecord</code>s are made available to a client application via an
 * argument of the <code>servicesDiscovered</code> method of the
 * <code>DiscoveryListener</code> interface. <code>ServiceRecord</code>s are available to server applications via the method
 * <code>getRecord()</code> on <code>LocalDevice</code>. <p> There might be many service attributes in a service record, and
 * the SDP protocol makes it possible to specify the subset of the
 * service attributes that an SDP client wants to retrieve from a
 * remote service record.  The <code>ServiceRecord</code> interface treats
 * certain service attribute IDs as default IDs, and, if present, these service attributes are automatically retrieved during
 * service searches. <p> The Bluetooth Assigned Numbers document (<A HREF="http://www.bluetooth.org/assigned-numbers/sdp.htm">
 * http://www.bluetooth.org/assigned-numbers/sdp.htm</A>) defines a large number of service attribute IDs.  Here is a subset of
 * the most common service attribute IDs and their types. <TABLE BORDER=1> <TR><TH>Attribute Name</TH><TH>Attribute ID</TH>
 * <TH>Attribute Value Type</TH></TR> <TR><TD>ServiceRecordHandle</TD><TD>0x0000</TD> <TD>32-bit unsigned integer</TD></TR>
 * <TR><TD>ServiceClassIDList</TD><TD>0x0001</TD> <TD>DATSEQ of UUIDs</TD></TR> <TR><TD>ServiceRecordState</TD><TD>0x0002</TD>
 * <TD>32-bit unsigned integer</TD></TR> <TR><TD>ServiceID</TD><TD>0x0003</TD> <TD>UUID</TD></TR>
 * <TR><TD>ProtocolDescriptorList</TD><TD>0x0004</TD> <TD>DATSEQ of DATSEQ of UUID and optional parameters</TD></TR>
 * <TR><TD>BrowseGroupList</TD><TD>0x0005</TD> <TD>DATSEQ of UUIDs</TD></TR>
 * <TR><TD>LanguageBasedAttributeIDList</TD><TD>0x0006</TD> <TD>DATSEQ of DATSEQ triples</TD></TR>
 * <TR><TD>ServiceInfoTimeToLive</TD><TD>0x0007</TD> <TD>32-bit unsigned integer</TD></TR>
 * <TR><TD>ServiceAvailability</TD><TD>0x0008</TD> <TD>8-bit unsigned integer</TD></TR>
 * <TR><TD>BluetoothProfileDescriptorList</TD><TD>0x0009</TD> <TD>DATSEQ of DATSEQ pairs</TD></TR>
 * <TR><TD>DocumentationURL</TD><TD>0x000A</TD> <TD>URL</TD></TR>
 * <TR><TD>ClientExecutableURL</TD><TD>0x000B</TD><TD>URL</TD></TR> <TR><TD>IconURL</TD><TD>0x000C</TD><TD>URL</TD></TR>
 * <TR><TD>VersionNumberList</TD><TD>0x0200</TD> <TD><code>DATSEQ</code> of 16-bit unsigned integers</TD></TR>
 * <TR><TD>ServiceDatabaseState</TD><TD>0x0201</TD> <TD>32-bit unsigned integer</TD></TR> </TABLE> <P>
 * The following table lists the common string-valued attribute ID offsets
 * used in a <code>ServiceRecord</code>.  These offsets must be added to a
 * base value to obtain the actual service ID.  (For more information, see
 * the Service Discovery Protocol Specification located in the Bluetooth
 * Core Specification at <A HREF="http://www.bluetooth.com/dev/specifications.asp">
 * http://www.bluetooth.com/dev/specifications.asp</A>). <TABLE BORDER=1>
 * <TR><TH>Attribute Name</TH><TH>Attribute ID Offset</TH> <TH>Attribute Value Type</TH></TR>
 * <TR><TD>ServiceName</TD><TD>0x0000</TD><TD>String</TD></TR>
 * <TR><TD>ServiceDescription</TD><TD>0x0001</TD><TD>String</TD></TR>
 * <TR><TD>ProviderName</TD><TD>0x0002</TD><TD>String</TD></TR> </TABLE>
 */
public interface ServiceRecord {
    /*
    * The following section defines public, static and instance member.
    * variables used in the implementation of the methods.
    */

    /**
     * Authentication and encryption are not needed on a connection to this service.  Used with
     * <code>getConnectionURL()</code> method. <P> <code>NOAUTHENTICATE_NOENCRYPT</code> is set to the
     * constant value 0x00 (0).
     * @see #getConnectionURL
     */
    public static final int NOAUTHENTICATE_NOENCRYPT = 0;

    /**
     * Authentication is required for connections to this service, but not
     * encryption. It is OK for encryption to be either on or off for the connection.  Used
     * with <code>getConnectionURL()</code> method. <P>
     * <code>AUTHENTICATE_NOENCRYPT</code> is set to the constant value 0x01 (1).
     * @see #getConnectionURL
     */
    public static final int AUTHENTICATE_NOENCRYPT = 0x01;

    /**
     * Authentication and encryption are required for connections to this service.  Used  with
     * <code>getConnectionURL()</code> method. <P> <code>AUTHENTICATE_ENCRYPT</code> is set to the constant value 0x02 (2).
     * @see #getConnectionURL
     */
    public static final int AUTHENTICATE_ENCRYPT = 0x02;

    /*
    * The following section defines public, static and instance member.
    * member methods used in the class. It also defines the
    * constructors.
    */

    /**
     * Returns the value of the service attribute ID provided it is
     * present in the service record, otherwise this method returns <code>null</code>.
     * @param attrID the attribute whose value is to be returned
     * @return the value of the attribute ID if present in the service record, otherwise <code>null</code>
     * @exception IllegalArgumentException if <code>attrID</code> is negative or greater than or equal to 2<sup>16</sup>
     */
    public DataElement getAttributeValue(int attrID);

    /**
     * Returns the remote Bluetooth device that populated the service
     * record with attribute values. It is important to note that the
     * Bluetooth device that provided the value might not be reachable
     * anymore, since it can move, turn off, or change its security mode denying all further transactions.
     * @return the remote Bluetooth device that populated the service
     * record, or <code>null</code> if the local device populated this <code>ServiceRecord</code>
     */
    public RemoteDevice getHostDevice();

    /**
     * Returns the service attribute IDs whose value could be retrieved by a call to
     * <code>getAttributeValue()</code>. The list of attributes being returned is not sorted and includes default attributes.
     * @see #getAttributeValue
     * @return an array of service attribute IDs that are in this
     * object and have values for them; if there are no attribute IDs
     * that have values, this method will return an array of length zero.
     */
    public int[] getAttributeIDs();

    /**
     * Retrieves the values by contacting the remote Bluetooth device
     * for a set of service attribute IDs of a service that is available
     * on a Bluetooth device.  (This involves going over the air and
     * contacting the remote device for the attribute values.)  The system
     * might impose a limit on the number of service attribute ID
     * values one can request at a time.  Applications can obtain the value of this limit as a String by calling
     * <code>LocalDevice.getProperty("bluetooth.sd.attr.retrievable.max")</code>.
     * The method is blocking and will return when the results of the request are available.  Attribute IDs whose
     * values could be obtained are added to this service record. If
     * there exist attribute IDs for which values are retrieved this
     * will cause the old values to be overwritten. If the remote
     * device cannot be reached, an <code>IOException</code> will be thrown.
     * @param attrIDs the list of service attributes IDs whose value
     * are to be retrieved; the number of attributes cannot exceed the
     * property <code>bluetooth.sd.attr.retrievable.max</code>; the
     * attributes in the request must be legal, i.e. their values are
     * in the range of [0, 2<sup>16</sup>-1]. The input attribute IDs
     * can include attribute IDs from the default attribute set too.
     * @return <code>true</code> if the request was successful in retrieving values for some or all of the attribute IDs;
     * <code>false</code> if it was unsuccessful in retrieving any values
     * @exception IOException if the local device is unable to connect
     * to the remote Bluetooth device that was the source of this <code>ServiceRecord</code>; if this
     * <code>ServiceRecord</code> was deleted from the SDDB of the remote device
     * @exception IllegalArgumentException if the size of <code>attrIDs</code> exceeds the system specified limit as
     * defined by <code>bluetooth.sd.attr.retrievable.max</code>; if the
     * <code>attrIDs</code> array length is zero; if any of their values are not in the range of [0, 2<sup>16</sup>-1]; if
     * <code>attrIDs</code> has duplicate values
     * @exception NullPointerException if <code>attrIDs</code> is <code>null</code>
     * @exception RuntimeException if this <code>ServiceRecord</code>
     * describes a service on the local device rather than a service on a remote device
     */
    public boolean populateRecord(int[] attrIDs) throws IOException;

    /**
     * Returns a String including optional parameters that can be used by a client to connect to the service described by this
     * <code>ServiceRecord</code>.  The return value can be used as the
     * first argument to <code>Connector.open()</code>. In the case of a
     * Serial Port service record, this string might look like
     * "btspp://0050CD00321B:3;authenticate=true;encrypt=false;master=true", where "0050CD00321B" is the Bluetooth
     * address of the device that provided this <code>ServiceRecord</code>, "3" is the RFCOMM
     * server channel mentioned in this <code>ServiceRecord</code>, and
     * there are three optional parameters related to security and master/slave roles. <P>
     * If this method is called on a <code>ServiceRecord</code> returned
     * from <code>LocalDevice.getRecord()</code>, it will return the
     * connection string that a remote device will use to connect to this service.
     * @see #NOAUTHENTICATE_NOENCRYPT
     * @see #AUTHENTICATE_NOENCRYPT
     * @see #AUTHENTICATE_ENCRYPT
     * @param requiredSecurity determines whether authentication or encryption are required for a connection
     * @param mustBeMaster <code>true</code> indicates that this device
     * must play the role of master in connections to this service;
     * <code>false</code> indicates that the local device is willing to be either the master or the slave
     * @return a string that can be used to connect to the service or <code>null</code> if the ProtocolDescriptorList in this
     * ServiceRecord is not formatted according to the Bluetooth specification
     * @exception IllegalArgumentException if <code>requiredSecurity</code> is not one of the constants
     * <code>NOAUTHENTICATE_NOENCRYPT</code>, <code>AUTHENTICATE_NOENCRYPT</code>, or <code>AUTHENTICATE_ENCRYPT</code>
     */
    public String getConnectionURL(int requiredSecurity, boolean mustBeMaster);

    /**
     * Used by a server application to indicate the major service class bits that should be activated in the server's
     * <code>DeviceClass</code> when this <code>ServiceRecord</code> is added to the SDDB.  When client devices do device
     * discovery, the server's <code>DeviceClass</code> is provided
     * as one of the arguments of the <code>deviceDiscovered</code>
     * method of the <code>DiscoveryListener</code> interface. Client
     * devices can consult the <code>DeviceClass</code> of the server
     * device to get a general idea of the kind of device this is (e.g., phone, PDA, or PC) and the major service classes it
     * offers (e.g., rendering, telephony, or information).  A server application should use the
     * <code>setDeviceServiceClasses</code> method to describe its service in terms of the major service classes.  This allows
     * clients to obtain a <code>DeviceClass</code> for the server
     * that accurately describes all of the services being offered.
     * <p> When <code>acceptAndOpen()</code> is invoked for the first time on the notifier associated with this
     * <code>ServiceRecord</code>, the <code>classes</code> argument
     * from the <code>setDeviceServiceClasses</code> method is OR'ed
     * with the current setting of the major service class bits of the local device.  The OR operation potentially activates
     * additional bits. These bits may be retrieved by calling <code>getDeviceClass()</code> on the <code>LocalDevice</code>
     * object. Likewise, a call to <code>LocalDevice.updateRecord()</code> will cause the major service
     * class bits to be OR'ed with the current settings and updated. <p> The documentation for <code>DeviceClass</code> gives
     * examples of the integers that describe each of the major service classes and provides a URL for the complete list.
     * These integers can be used individually or OR'ed together to describe the appropriate value for <code>classes</code>.
     * <p> Later, when this <code>ServiceRecord</code> is removed from the SDDB, the implementation will automatically
     * deactivate the device bits that were activated as a result of
     * the call to <code>setDeviceServiceClasses</code>.  The only exception to this occurs if there is another
     * <code>ServiceRecord</code> that is in the SDDB and <code>setDeviceServiceClasses</code> has been sent to that other
     * <code>ServiceRecord</code> to request that some of the same bits be activated.
     * @param classes an integer whose binary representation indicates the major service class bits that should be activated
     * @exception IllegalArgumentException if <code>classes</code> is
     * not an OR of one or more of the major service class integers in the Bluetooth Assigned Numbers document.  While Limited
     * Discoverable Mode is included in this list of major service
     * classes, its bit is activated by placing the device in Limited
     * Discoverable Mode (see the GAP specification), so if bit 13 is set this exception will be thrown.
     * @exception RuntimeException if the <code>ServiceRecord</code> receiving the message was obtained from a remote device
     */
    public void setDeviceServiceClasses(int classes);

    /**
     * Modifies this <code>ServiceRecord</code> to contain the service attribute defined by the attribute-value pair
     * <code>(attrID, attrValue)</code>.  If the <code>attrID</code> does not exist in the <code>ServiceRecord</code>, this
     * attribute-value pair is added to this <code>ServiceRecord</code> object.  If the <code>attrID</code> is already in this
     * <code>ServiceRecord</code>, the value of the attribute is changed to <code>attrValue</code>.
     * If <code>attrValue</code> is <code>null</code>, the attribute
     * with the attribute ID of <code>attrID</code> is removed from this <code>ServiceRecord</code> object.  If
     * <code>attrValue</code> is <code>null</code> and <code>attrID</code> does not exist in this object,
     * this method will return <code>false</code>. <P> This method makes no modifications to a service record in the
     * SDDB.  In order for any changes made by this method to be reflected in the SDDB, a call must be made to the
     * <code>acceptAndOpen()</code> method of the associated notifier
     * to add this <code>ServiceRecord</code> to the SDDB for the first time, or a call must be made to the
     * <code>updateRecord()</code> method of <code>LocalDevice</code> to
     * modify the version of this <code>ServiceRecord</code> that is already in the SDDB. <P>
     * This method prevents the ServiceRecordHandle from being modified by throwing an <code>IllegalArgumentException</code>.
     * @param attrID  the service attribute ID
     * @param attrValue the <code>DataElement</code> which is the value of the service attribute
     * @return <code>true</code> if the service attribute was successfully added, removed, or modified; <code>false</code> if
     * <code>attrValue</code> is <code>null</code> and <code>attrID</code> is not in this object
     * @exception IllegalArgumentException if  <code>attrID</code> does
     * not represent a 16-bit unsigned integer; if <code>attrID</code> is the value of ServiceRecordHandle (0x0000)
     * @exception RuntimeException if this method is called on a <code>ServiceRecord</code> that was created by a call to
     * <code>DiscoveryAgent.searchServices()</code>
     */
    public boolean setAttributeValue(int attrID, DataElement attrValue);
}

/*  End of the interface ServiceRecord  */

