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

package com.sun.midp.io.j2me.mms;

import com.sun.midp.io.j2me.ProtocolBase;

import com.sun.midp.io.j2me.sms.MessageObject;

import com.sun.midp.security.Permissions;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Connection;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;

// Exceptions
import java.io.IOException;
import java.io.InterruptedIOException;

/**
 * MMS message connection implementation.
 *
 * <code>Protocol</code> itself is not instantiated. Instead, the application
 * calls <code>Connector.open</code> with an MMS URL string and obtains a
 * {@link javax.wireless.messaging.MessageConnection MessageConnection}
 * object. It is an instance of <code>MessageConnection</code> that is
 * instantiated. The Generic Connection Framework mechanism in CLDC will return
 * a <code>Protocol</code> object, which is the implementation of
 * <code>MessageConnection</code>. The <code>Protocol</code> object represents a
 * connection to a low-level transport mechanism.
 * <p>
 * Optional packages, such as <code>Protocol</code>, cannot reside in small
 * devices. The Generic Connection Framework allows an application to reach the
 * optional packages and classes indirectly. For example, an application can be
 * written with a string that is used to open a connection. Inside the
 * implementation of <code>Connector</code>, the string is mapped to a
 *  particular implementation: <code>Protocol</code>, in this case. This allows
 * the implementation to be optional even though the interface,
 * <code>MessageConnection</code>, is required.
 * <p>
 * Closing the connection frees an instance of <code>MessageConnection</code>.
 * <p>
 * The <code>Protocol</code> class contains methods to open and close the
 * connection to the low-level transport mechanism. The messages passed on the
 * transport mechanism are defined by the {@link MessageObject MessageObject}
 * class. Connections can be made in either client mode or server mode.
 * <ul>
 * <li>Client mode connections are for sending messages only. They are created
 * by passing a string identifying a destination address to the
 * <code>Connector.open()</code> method.</li>
 * <li>Server mode connections are for receiving and sending messages. They are
 * created by passing a string that identifies a port, or equivalent, on the
 * local host to the <code>Connector.open()</code> method.</li>
 * </ul>
 * The class also contains methods to send, receive, and construct
 * <code>Message</code> objects.
 * <p>
 * <p>
 * This class declares that it implements <code>StreamConnection</code> so it
 * can intercept calls to <code>Connector.open*Stream()</code> to throw an
 * <code>IllegalArgumentException</code>.
 * </p>
 */
public class Protocol extends ProtocolBase {

    /** Creates a message connection protocol handler. */
    public Protocol() {
        super();
        ADDRESS_PREFIX = "mms://";
    }

    /**
     * Gets the connection parameter in string mode.
     * @return string that contains a parameter 
     */
    protected String getAppID() {
        return appID;
    }

    /**
     * Sets the connection parameter in string mode.
     * @param newValue new value of connection parameter 
     */
    protected void setAppID(String newValue) {
        appID = newValue;
    }

    /**
     * The internal representation of the MMS message.
     */
    private class MMSPacket {
        /** The sender's address. */
        public byte[] fromAddress;
        /** The application ID associated with the message. */
        public byte[] appID;
        /** The application ID to which replies will be sent. */
        public byte[] replyToAppID;
        /** Entire message contents. */
        public byte[] message;
    };


    /*
     * Native function prototypes
     */

    /**
     * Native function to open a MMS connection.
     *
     * @param host The name of the host for this connection. Can be
     *     <code>null</code>.
     * @param appID The application ID associated with this connection.
     * Can be <code>null</code> for unblock sending and receiving messages.
     * @param msid The MIDlet suite ID.
     *
     * @return  returns handle to the open MMS connection.
     */
    private native int open0(String host, String appID, int msid)
         throws IOException;

    /**
     * Unblock the receive thread.
     *
     * @param msid The MIDlet suite ID.
     *
     * @return  returns handle to the connection.
     */
    protected int unblockReceiveThread(int msid)
        throws IOException {
        return open0(null, null, msid);
    }

    /**
     * Native function to close mms connection
     *
     * @param appID  The application ID associated with this connection.
     * @param handle  The MMS handle created when the connection was opened.
     * @param deRegister Deregistration appID when parameter is 1.
     *
     * @return  <code>0</code> if successful; <code>-1</code> for failure.
     */
    private native int close0(String appID, int handle, int deRegister);

    /**
     * Close connection.
     *
     * @param connHandle handle returned by open0
     * @param deRegister Deregistration appID when parameter is 1.
     * @return    0 on success, -1 on failure
     */
    protected int closeConnection(int connHandle, int deRegister) {
        return close0(appID, connHandle, deRegister);
    }

    /**
     * Native function to get the device phone number
     *
     * @return  the phone number of device.
     */
    private native String getPhoneNumber0();

    /**
     * Sends an MMS message.
     *
     * @param handle The handle to the open MMS connection.
     * @param toAddress The recipient's MMS address.
     * @param fromAddress The sender's MMS address.
     * @param appID The application ID to be matched against incoming messages.
     * @param replyToAppID The ID of the application that processes replies.
     * @param msgHeader The message header context.
     * @param msgBody The message body context.
     *
     * @return the status of <code>0</code>, when bytes were sent;
     *	<code>&lt;0</code> when there is an error (This is accompanied by
     *	an exception.).
     */
    private native int send0(int handle,
                             String toAddress,
                             String fromAddress,
                             String appID,
                             String replyToAppID,
                             byte[] msgHeader,
                             byte[] msgBody) throws IOException;

    /**
     * Receives a MMS message.
     *
     * @param handle The handle to the the MMS connection.
     * @param appID The application ID to be matched against incoming messages.
     * @param msid The MIDlet suite ID.
     * @param packet The received message.
     *
     * @return    The number of bytes received.
     *
     * @exception IOException  if an I/O error occurs
     */
    private native int receive0(int handle, String appID, int msid,
                                MMSPacket packet) throws IOException;

    /**
     * Waits until message available
     *
     * @param appID  The application ID associated with this connection.
     * @param handle  The handle to the MMS connection.
     *
     * @return  <code>0</code> on success, <code>-1</code> on failure
     *
     * @exception IOException  if an I/O error occurs
     */
    private native int waitUntilMessageAvailable0(String appID, int handle)
                               throws IOException;

    /**
     * Waits until message available
     *
     * @param handle handle to connection
     * @return 0 on success, -1 on failure
     * @exception IOException  if an I/O error occurs
     */
    protected int waitUntilMessageAvailable(int handle) 
        throws IOException {
        return waitUntilMessageAvailable0(appID, handle);
    }

    /**
     * Computes the number of transport-layer segments that would be required to
     * send the given message.
     *
     * @param msgBuffer The message to be sent.
     * @param msgLen The length of the message.
     * @param msgType The message type: binary or text.
     * @param hasPort Indicates if the message includes a source or destination
     *     port number.
     *
     * @return The number of transport-layer segments required to send the
     *     message.
     */
    private native int numberOfSegments0(byte msgBuffer[], int msgLen,
        int msgType, boolean hasPort);

    /*
     * Helper methods
     */

    /**
     * Checks the internal setting of the receive permission. Called from
     * the <code>receive</code> and <code>setMessageListener</code> methods.
     *
     * @exception InterruptedIOException if permission dialog was pre-empted.
     */
    protected void checkReceivePermission() throws InterruptedIOException {

        // Check for permission to receive.
        if (readPermission == false) {
            try {
                midletSuite.checkForPermission(Permissions.MMS_RECEIVE,
                                               "mms:receive");
                readPermission = true;
            } catch (InterruptedException ie) {
                throw new InterruptedIOException("Interrupted while trying " +
                                               "to ask the user permission.");
            }
        }
    }

    /*
     * MessageConnection Interface
     */

    /**
     * Construct a new message object of a text, binary or multipart message
     * type and specify a destination address. When a
     * <code>MULTIPART_MESSAGE</code> constant is passed in, the created object
     * implements the <code>MultipartMessage</code> interface.
     * <p>
     * If this method is called in a sending mode, a new <code>Message</code>
     * object is requested from the connection. For example:
     * <p>
     * <code>Message msg = conn.newMessage(MULTIPART_MESSAGE);</code>
     * <p>
     * The <code>Message</code> does not have the destination address set. It
     * must be set by the application before the message is sent.
     * <p>
     * If this method is called in receiving mode, the <code>Message</code>
     * object does have its address set. The application can act on the object
     * to extract the address and message data.
     * <p>
     * <!-- The <code>type</code> parameter indicates the number of bytes that
     * should be allocated for the message. No restrictions are placed on the
     * application for the value of <code>size</code>. A value of
     * <code>null</code> is permitted and creates a <code>Message</code> object
     * with a 0-length message. -->
     *
     * @param type <code>MULTIPART_MESSAGE</code> is the only type permitted.
     *
     * @return A new MMS <code>Message</code> object.
     */
    public Message newMessage(String type) {

        String address = null;

        // Provide the default address from the original open.
        if (host != null) {
            address = ADDRESS_PREFIX + host;
            if (appID != null) {
                address = address + ":" + appID;
            }
        }

        return newMessage(type, address);
    }

    /**
     * Construct a new <code>MULTIPART_MESSAGE</code> message object and specify
     * a destination address. The <code>MULTIPART_MESSAGE</code> constant must
     * be passed in. The created object implements the
     * <code>MultipartMessage</code> interface.
     * <p>
     * The destination address <code>addr</code> has the following format:
     * <p>
     * <code>mms://<em>phone_number</em>:<em>application_id</em></code>
     *
     * @param type <code>MULTIPART_MESSAGE</code> is the only type permitted.
     * @param addr  The destination address of the message.
     *
     * @return A new MMS <code>Message</code> object.
     */
    public Message newMessage(String type, String addr) {

        if (!type.equals(MessageConnection.MULTIPART_MESSAGE)) {
            throw new IllegalArgumentException("Message type not supported.");
        }

        return new MultipartObject(addr);
    }

    /**
     * Send an MMS message over the connection. The data are extracted from the
     * <code>Message</code> object payload for use by the underlying transport
     * layer.
     *
     * @param msg  A <code>Message</code> object.
     *
     * @exception java.io.IOException if the message could not be sent
     *            or because of network failure.
     * @exception java.lang.IllegalArgumentException if the message is
     *            incomplete or contains invalid information. This exception
     *            is also thrown if the payload of the message exceeds
     *            the maximum length for the given messaging protocol.
     * @exception java.io.InterruptedIOException if a timeout occurs while
     *            either trying to send the message or if this
     *            <code>Connection</code> object is closed during this
     *            <code>send</code> operation.
     * @exception java.lang.NullPointerException if the parameter is null.
     * @exception java.lang.SecurityException if the application does not
     *            have permission to send the message.
     */
    public void send(Message msg) throws IOException {

        if (msg == null) {
            throw new NullPointerException("Null message");
        }

        // If this isn't a multipart message, bail out.
        if (!(msg instanceof MultipartObject)) {
            throw new IllegalArgumentException("Unsupported message type");
        }

        // Make sure the connection is still open.
        ensureOpen();

        // Create the multi-part object that will be used below.
        MultipartObject mpo = (MultipartObject)msg;

        /*
         * Check for valid MMS URL connection format. Note that the addresses in
         * the lists are not used. This is simply a check to make sure that the
         * addresses can be placed into the multipart object's header when the
         * header and message are bundled within MultipartObject.
         *
         * Process each MMS address in the to:, cc: and bcc: address lists. An
         * MMS address assumes this form: address:appID
         *
         * Each MMS address is parsed to extract the address and application ID
         * data. Those parts are then checked for validity.
         *
         * The loop starts by processing all addresses in the to: field (if
         * any), followed by the addresses in the cc: list, then the bcc: list.
         *
         */
        Vector allAddresses = new Vector();
        String[] addresses = mpo.getAddresses("to");
        int currIndex = 0;
        boolean checkedTo = false;
        boolean checkedCC = false;

        // The application ID extracted from an address in the address list.
        String parsedAppID = null;
        while (true) {

            /*
             * If no addresses were in the to: field, or if all addresses have
             * been extracted and checked from the current address list
             * (Initially, the to: list), then continue to process the cc: list
             * (if any), next, followed by the bcc: list.
             */
            if (addresses == null || currIndex >= addresses.length) {

                if (!checkedTo) {

                    // The to: list has been processed. Process cc: list, next.
                    checkedTo = true;
                    addresses = mpo.getAddresses("cc");
                    currIndex = 0;
                    continue;

                } else if (!checkedCC) {

                    // The cc: list has been processed. Process bcc: list, next.
                    checkedCC = true;
                    addresses = mpo.getAddresses("bcc");
                    currIndex = 0;
                    continue;
                } else {

                    /*
                     * The to:, cc: and bcc: lists have now been checked, so
                     * bail out of the while() loop.
                     */
                    break;
                }
            }

            /*
             * Pick up the next address and add it to the list. Then, parse it
             * to extract the address and application ID parts.
             */
            String addr = addresses[currIndex++];
            allAddresses.addElement(addr);

            MMSAddress parsedAddress = MMSAddress.getParsedMMSAddress(addr);

            if (parsedAddress == null ||
                parsedAddress.type == MMSAddress.INVALID_ADDRESS ||
                parsedAddress.type == MMSAddress.APP_ID) {
                throw new IllegalArgumentException(
                    "Invalid MMS address: " + addr);
            }

            if (parsedAppID == null) {
                parsedAppID = parsedAddress.appId;
            } else if (parsedAddress.appId != null &&
                !parsedAppID.equals(parsedAddress.appId)) {
                throw new IllegalArgumentException("Only one Application-ID "
                    + "can be specified per message");
            }

        } // while

        if (allAddresses.size() == 0) {
            throw new IllegalArgumentException("No to, cc, or bcc addresses.");
        }

        /*
         * Since JTWI requires the destination phone number (not port)
         * and the number of messages to be displayed in the permission
         * dialog to the user, the permission check must happen after the
         * address check and message disassembly.
         */
        try {
            /*
             * IMPL_NOTE: (Original comment for MMS): Add the display of the addresses
             * in a nice way, and the size of all attachments and total size, as
             * per page 66 (Appendix E) of the specification.
             *
             * Nicer display can happen by creating an MMSAddressList class that
             * extends Vector and overrides toString().
             *
             */
            midletSuite.checkForPermission(Permissions.MMS_SEND,
					   allAddresses.toString(),
                              Integer.toString(numberOfSegments(msg)));
        } catch (InterruptedException ie) {
            throw new InterruptedIOException(
                 "Interrupted while trying to ask the user permission");
        }

        // Construct the target address protocol string.
        String messageAppID = mpo.getApplicationID();
        String toAddress = "mms://:";
        if (messageAppID != null) {
            toAddress = toAddress + messageAppID;
        }

        /*
         * If no application ID was supplied, use the ID that was used to open
         * the connection as the default ID.
         */
        String replyToAppID = null;
        if (messageAppID != null && host == null) {
            replyToAppID = appID;
            mpo.setReplyToApplicationID(replyToAppID);
        }

        // Retain the original "from:" address.
        String oldFromAddress = ((MessageObject)mpo).getAddress();

        // Create the "reply-to" information.
        String phoneNumber = getPhoneNumber0();
        String fromAddress = "mms://" + phoneNumber;
        if (replyToAppID != null) {
            fromAddress = fromAddress + ":" + replyToAppID;
        }
        mpo.setFromAddress(fromAddress);

        // Send the message and reply information.
        byte[] header = mpo.getHeaderAsByteArray();
        byte[] body = mpo.getBodyAsByteArray();
        try {
            send0(connHandle, toAddress, fromAddress, messageAppID, replyToAppID,
                  header, body);
        } catch (IOException ex) {
            io2InterruptedIOExc(ex, "sending");
        }

        // Reset the from: address to its original form.
        mpo.setFromAddress(oldFromAddress);
    }

    /**
     * Receives the bytes that have been sent over the connection, constructs a
     * <code>MultipartMessage</code> object, and returns the message.
     * <p>
     * If there are no <code>MultipartMessage</code>s waiting on the connection,
     * this method will block until a message is received, or the
     * <code>MessageConnection</code> is closed.
     *
     * @return  A <code>MultipartMessage</code> object.
     *
     * @exception java.io.IOException  if an error occurs while receiving a
     *            message.
     * @exception java.io.InterruptedIOException if this
     *            <code>MessageConnection</code> object is closed during this
     *            receive method call.
     * @exception java.lang.SecurityException if the application does not have
     *            permission to receive messages using the given application ID.
     */
    public synchronized Message receive() throws IOException {

        if (host != null) {
            throw new IOException(
                "Can not receive from client only connection: " + host);
        }

        // Check for permission to receive.
        checkReceivePermission();

        // Make sure the connection is still open.
        ensureOpen();

        // The connection must be read-only with no host address.
        if (((m_mode & Connector.READ) == 0) || (host != null)) {
            throw new IOException("Invalid connection mode.");
        }

        // No message received yet.
        Message msg = null;
        int length = 0;

        try {

            MMSPacket mmsPacket = new MMSPacket();

            /*
             * Packet has been received and deleted from inbox.
             * Time to wake up receive thread.
             */
            // Pick up the message from the message pool.
            length = receive0(connHandle, appID, midletSuite.getUniqueID(),
                              mmsPacket);

            if (length == 0) {
                throw new InterruptedIOException("No message received.");
            }

            if (length > 0) {

                /*
                 * Convert the message data into a multipart message. The
                 * message packet contains the appID that goes with the message
                 * as well as the entire message content in a big byte[] lump.
                 */
                msg = MultipartObject.createFromByteArray(mmsPacket.message);

                // IMPL_NOTE: KEEP ?
                // IMPL_NOTE: msg.setTimeStamp(tm.getTimeStamp());

                String fromAddress = null;
                if (mmsPacket.fromAddress != null) {
                    fromAddress = new String(mmsPacket.fromAddress);
                }

                String replyToAppID;
                if (mmsPacket.replyToAppID == null) {
                    replyToAppID = null;
                } else {
                    replyToAppID = new String(mmsPacket.replyToAppID);
                }

                ((MultipartObject)msg).setFromAddress(fromAddress);
                String phoneNumber = getPhoneNumber0();
                ((MultipartObject)msg).fixupReceivedMessageAddresses(
                    fromAddress, phoneNumber);
            }

        } catch (InterruptedIOException ex) {
            length = 0;  // Avoid the "finally" exception, below.
            throw new InterruptedIOException("MMS connection closed.");
        } catch (IOException ex) {
            io2InterruptedIOExc(ex, "receiving");
	} finally {
            if (length < 0) {
		throw new InterruptedIOException("Connection closed.");
            }
	}

        return msg;
    }

    /**
     * Returns the number of segments required to send the given
     * <code>Message</code>.
     * <p>
     * Note: The message is not actually sent. This method will compute the
     * number of segments needed when this message is split into the protocol
     * segments using the appropriate features of the underlying protocol. This
     * method does not take the possible limitations of the implementation into
     * account, which may limit the number of segments that can be sent, using
     * this feature. These limitations are protocol-specific and are documented
     * with the adapter definition for that protocol.
     * </p>
     * @param msg The <code>MultipartObject</code>message to be used for
     *     the computation.
     *
     * @return  The number of protocol segments needed for sending the message.
     *     Returns <code>0</code> if the <code>Message</code> object cannot be
     *     sent using the underlying protocol.
     */
    public int numberOfSegments(Message msg) {

        if (!(msg instanceof MultipartObject)) {
            return 0;
        }

        /** Number of segments that need to be sent. */
        int segments = 0;

        byte[] msgBuffer = null;
        try {
            msgBuffer = ((MultipartObject)msg).getAsByteArray();
        } catch (IOException ioe) {
            // ignore this.
        }

        // Pick up the message length.
        if (msgBuffer != null) {

            // There is always a "port" (Application ID).
            boolean hasPort = true;

            /* Compute the total number of transport-layer segments. */
            segments = numberOfSegments0(msgBuffer, msgBuffer.length,
                0, hasPort);
        }

        return  segments;
    }

    /**
     * Closes the connection. Resets the connection <code>open</code> flag to
     * <code>false</code>. Subsequent operations on a closed connection should
     * throw an appropriate exception.
     *
     * @exception IOException  if an I/O error occurs
     */
    public void close() throws IOException {

        /*
         * Set appID to null, in order to quit out of the while loop
         * in the receiver thread.
         */
        String save_appID = null;
        if (appID != null) {
            save_appID = new String(appID);
        }

        appID = null;

        synchronized (closeLock) {
            if (open) {
                /*
                 * Reset open flag early to prevent receive0 executed by 
                 * concurrent thread to operate on partially closed 
                 * connection 
                 */
                open = false;        /* Close the connection and unregister the application ID. */
                close0(save_appID, connHandle, 1);

                setMessageListener(null);

                /*
                 * Reset handle and other params to default
                 * values. Multiple calls to close() are allowed
                 * by the spec and the resetting would prevent any
                 * strange behaviour.
                 */
                connHandle = 0;
                host = null;
                m_mode = 0;
            }
        }
    }

    /*
     * ConnectionBaseInterface Interface
     */

    /**
     * Opens a connection. This method is called from the
     * <code>Connector.open()</code> method to obtain the destination address
     * given in the <code>name</code> parameter.
     * <p>
     * The format for the <code>name</code> string for this method is:
     * <p>
     * <code>mms://[<em>phone_number</em>]:[<em>application_id</em>]</code>
     * <p>
     * where the <em>phone_number</em> is optional. If the <em>phone_number</em>
     * parameter is present, the connection is being opened in client mode. This
     * means that messages can be sent. If the parameter is absent, the
     * connection is being opened in server mode. This means that messages can
     * be sent and received.
     * <p>
     * The connection that is opened is to a low-level transport mechanism which
     * can be any of the following:
     * <ul>
     *   <li>A datagram Short Message Peer-to-Peer (SMPP) to a service center.
     *   <li>A <code>comm</code> connection to a phone device with AT-commands.
     *   <li>A native MMS stack.
     * </ul>
     * Currently, the <code>mode</code> and <code>timeouts</code> parameters are
     * ignored.
     *
     * @param name  The target of the connection.
     * @param mode  Indicates whether the caller intends to write to the
     *              connection. Currently, this parameter is ignored.
     * @param timeouts  Indicates whether the caller wants timeout exceptions.
     *              Currently, this parameter is ignored.
     *
     * @return  This connection.
     *
     * @exception IOException  if the connection is closed or unavailable.
     */
    public Connection openPrim(String name, int mode, boolean timeouts)
        throws IOException {

        return openPrimInternal(name, mode, timeouts);
    }


    /*
     * StreamConnection Interface
     */

    /**
     * Open and return an input stream for a connection. This method always
     * throws <code>IllegalArgumentException</code>.
     *
     * @return                              An input stream.
     * @exception IOException               if an I/O error occurs.
     * @exception IllegalArgumentException  is thrown for all requests.
     */
    public InputStream openInputStream() throws IOException {
        throw new IllegalArgumentException("Not supported.");
    }

    /**
     * Open and return a data input stream for a connection. This method always
     * throws <code>IllegalArgumentException</code>.
     *
     * @return                              An input stream.
     * @exception IOException               if an I/O error occurs.
     * @exception IllegalArgumentException  is thrown for all requests.
     */
    public DataInputStream openDataInputStream() throws IOException {
        throw new IllegalArgumentException("Not supported.");
    }

    /**
     * Open and return an output stream for a connection. This method always
     * throws <code>IllegalArgumentException</code>.
     *
     * @return                              An output stream.
     * @exception IOException               if an I/O error occurs.
     * @exception IllegalArgumentException  is thrown for all requests.
     */
    public OutputStream openOutputStream() throws IOException {
        throw new IllegalArgumentException("Not supported.");
    }

    /**
     * Open and return a data output stream for a connection. This method always
     * throws <code>IllegalArgumentException</code>.
     *
     * @return                              An output stream.
     * @exception IOException               if an I/O error occurs.
     * @exception IllegalArgumentException  is thrown for all requests.
     */
    public DataOutputStream openDataOutputStream() throws IOException {
        throw new IllegalArgumentException("Not supported.");
    }


    /*
     * Protocol members
     */

    /**
     * Opens a connection. This method is called from the
     * <code>Connector.open()</code> method to obtain the destination address
     * given in the <code>name</code> parameter.
     * <p>
     * The format for the <code>name</code> string for this method is:
     * <p>
     * <code>mms://[<em>phone_number</em>]:[<em>application_id</em>]</code>
     * <p>
     * where the <em>phone_number</em> is optional. If the <em>phone_number</em>
     * parameter is present, the connection is being opened in client mode. This
     * means that messages can be sent. If the parameter is absent, the
     * connection is being opened in server mode. This means that messages can
     * be sent and received.
     * <p>
     * The connection that is opened is to a low-level transport mechanism which
     * can be any of the following:
     * <ul>
     *   <li>A datagram Short Message Peer-to-Peer (SMPP) to a service center.
     *   <li>A <code>comm</code> connection to a phone device with AT-commands.
     *   <li>A native MMS stack.
     * </ul>
     * Currently, the <code>mode</code> and <code>timeouts</code> parameters are
     * ignored.
     *
     * @param name  The target of the connection.
     * @param mode  Indicates whether the caller intends to write to the
     *              connection. Currently, this parameter is ignored.
     * @param timeouts  Indicates whether the caller wants timeout exceptions.
     *              Currently, this parameter is ignored.
     *
     * @return  This connection.
     *
     * @exception IOException  if the connection is closed or unavailable.
     * @exception IllegalArgumentException  if the parameters are invalid
     */
    public synchronized Connection openPrimInternal(String name,
                                                    int mode,
                                                    boolean timeouts)
                                       throws IOException {

        /*
         * The general form of a MMS address is <code>mms://host:port</code>.
         * The form at this point should now be <code>//host:port</code>
         */
        if ((name == null) || (name.length() <= 2) ||
            (name.charAt(0) != '/') || (name.charAt(1) != '/')) {

            throw new IllegalArgumentException("Missing protocol separator.");
        }

        String fullAddress = "mms:" + name;

        MMSAddress parsedAddress = MMSAddress.getParsedMMSAddress(fullAddress);
        if (parsedAddress == null) {
            throw new IllegalArgumentException("Invalid MMS connection URL");
        }

        host = null;
        if (parsedAddress.address != null) {
            host = new String(parsedAddress.address);
        }

        appID = null;
        if (parsedAddress.appId != null) {
            appID = new String(parsedAddress.appId);
        }

        // Make sure the I/O constraint is READ, WRITE or READ_WRITE, only.
        if ((mode != Connector.READ) && (mode != Connector.WRITE) &&
            (mode != Connector.READ_WRITE)) {
            throw new IllegalArgumentException("Invalid mode");
        }

        /*
         * If <code>host == null</code>, then this is a server endpoint at
         * the supplied <code>appId</code>.
         *
         * If <code>host != null</code>, then this is a client endpoint at an
         * application-id decided by the system, and the default address for
         * MMS messages to be sent is <code>mms://host:appId</code>.
         */
        if (mode == Connector.READ && host != null && host.length() > 0) {
            throw new IllegalArgumentException("Cannot read on " +
                                               "a client connection.");
        }

        /*
         * Perform a one-time check to see if the application has the permission
         * to use this connection type.
         */
        if (openPermission == false) {
            try {
                midletSuite.checkForPermission(Permissions.MMS_SERVER,
                                               "mms:open");
                openPermission = true;
            } catch (InterruptedException ie) {
                throw new InterruptedIOException("Interrupted while trying " +
                                                "to ask the user permission");
            }
        }

        try {
            connHandle = open0(host, appID, midletSuite.getUniqueID());
        } catch (IOException ioe) {
            m_mode = 0;
            throw new IOException("Unable to open MMS connection: " +
                                  ioe.getMessage());
        } catch (OutOfMemoryError oome) {
            m_mode = 0;
            throw new IOException("Unable to open MMS connection: " +
                                  oome.getMessage());
        }
        open = true;

        m_mode = mode;

        // Return this connection.
        return this;
    }

}

