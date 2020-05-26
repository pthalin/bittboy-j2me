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

// Interfaces
import javax.wireless.messaging.MessageConnection;

// Classes
import com.sun.midp.io.j2me.sms.MessageObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;
import java.util.Vector;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;

// Exceptions
import java.io.IOException;
import javax.wireless.messaging.SizeExceededException;


/**
 * Implements an MMS message for the MMS message connection.
 */
public class MultipartObject extends MessageObject 
    implements MultipartMessage {

    /** The array of "to" addresses. */
    Vector to;
    /** The array of "cc" addresses. */
    Vector cc;
    /** The array of "bcc" addresses. */
    Vector bcc;
    /** The array of message parts. */
    Vector parts;
    /** The Content-ID of the part that starts the message. */
    String startContentID;
    /** The Subject field of the message. */
    String subject;
    /** The array of message headers. */
    String[] headerValues;
    /** The Application Identifier of the agent to process the message. */
    String applicationID;
    /** 
     * The Application Identifier of the return agent to 
     * process the message.
     */
    String replyToApplicationID;

    /** 
     * Construct a multipart message and initialize the  
     * target address. 
     * @param toAddress the address of the recipient. May be null. 
     */         
    public MultipartObject(String toAddress) {
        super(MessageConnection.MULTIPART_MESSAGE, null);
        to = new Vector();
        cc = new Vector();
        bcc = new Vector();
        parts = new Vector();
        startContentID = null;
        subject = null;
        applicationID = null;
        replyToApplicationID = null;
        setupHeaderFields();
        if (toAddress != null) {
            addAddress("to", toAddress);
        }
    }

    /**
     * Gets the "reply-to" application identifier.
     * @return the return address application identifier, or null if none is set
     * @see #setReplyToApplicationID
     */
    String getReplyToApplicationID() {
        return replyToApplicationID;
    }
    
    /**
     * Sets the "reply-to" application identifier.
     * @param appID the return address application identifier. May be null.
     * @see #getReplyToApplicationID
     */
    public void setReplyToApplicationID(String appID) {
        replyToApplicationID = appID;
    }

    /**
     * Returns the destination application identifier.
     * @return the destination application identifier, or null if none is set
     */
    public String getApplicationID() {
        return applicationID;
    }
    
    /**
     * Sets the "from" address.
     * @param fromAddress the return address, which may be null
     */
    public void setFromAddress(String fromAddress) {
        super.setAddress(fromAddress);
    }   
    
    /**
     * Prepares a received message to be sent right back to the sender.
     * Removes this device's address from the "to" and "cc"
     * address lists and sets the sender's address as the first "to" address.
     * @param senderAddress the sender's address. Should not include the 
     *      <code>"mms://"</code> prefix, and may contain the 
     *      <code>":appID"</code> suffix. May be null.
     * @param myAddress this device's address. Must not be null. 
     */
    public void fixupReceivedMessageAddresses(String senderAddress,
					      String myAddress) {
        String regularAddress = myAddress;
        String plusAddress = myAddress;
        if (regularAddress.charAt(0) == '+') {
            regularAddress = regularAddress.substring(1);
        } else if (plusAddress.charAt(0) != '+') {
            plusAddress = "+" + plusAddress;
        }                              
        // remove ourselves from "to" and "cc" list
        Vector addresses = to;
        for (int i = 0; i < 2; ++i) {                
            int numAdds = addresses.size();
            for (int index = 0; index < numAdds; ++index) {
                String thisAddress = (String)addresses.elementAt(index);
                MMSAddress parsedAddress = 
                    MMSAddress.getParsedMMSAddress(thisAddress);
                if (parsedAddress != null && 
                    (regularAddress.equals(parsedAddress.address) ||
                     plusAddress.equals(parsedAddress.address))) {
                        --numAdds;
                        addresses.removeElementAt(index);
                        --index;
                }
            }
            addresses = cc;
        }
        // set the first "to" address to be the sender's address
        if (senderAddress != null) {
            String formalAddress = senderAddress;
            to.insertElementAt(formalAddress, 0);
            MMSAddress parsedAddress = 
                MMSAddress.getParsedMMSAddress(formalAddress);
            applicationID = parsedAddress.appId;
        } else {
            applicationID = null;
        }
    }
    
    /**
     * Gets the requested address list.
     *
     * @param type the address list to be returned, either "to", "cc" or "bcc" 
     * @exception IllegalArgumentException if some other 
     * address list type is requested
     * @return a list of addresses
     */
    Vector getAddressList(String type) {
        String lower = type.toLowerCase();
        if (lower.equals("to")) {
            return to;
        } else if (lower.equals("cc")) {
            return cc;
        } else if (lower.equals("bcc")) {
            return bcc;
        }
        throw new IllegalArgumentException(
            "Address type is not 'to', 'cc', or 'bcc'");
    }     
    /**
     * Checks if the string is a valid MMS address according to the grammar
     * in Appendix D of the spec.
     * @param addr the address to check
     * @return MMSAddress representing the valid address, otherwise null.
     */
    MMSAddress checkValidAddress(String addr) throws IllegalArgumentException {
        MMSAddress parsedAddress = MMSAddress.getParsedMMSAddress(addr);
        // check to make sure there's a device address
        if (parsedAddress == null || 
            parsedAddress.type == MMSAddress.INVALID_ADDRESS ||
            parsedAddress.type == MMSAddress.APP_ID) {
            throw new IllegalArgumentException("Invalid destination address: "
                + addr);
        }
        return parsedAddress;
    }

    /**
     * Checks an application ID to see if it can be legally added to this
     * message. The spec requires that only a single applicationID can be
     * specified for any <code>MultipartMessage</code>.
     * @param newAppID the candidate applicationID to check. May be 
     *      <code>null</code>
     * @throws IllegalArgumentException if newAppID conflicts with an
     *      applicationID already specified for this message.
     */
    void checkApplicationID(String newAppID) throws IllegalArgumentException {
        if (applicationID != null) {
            if (!applicationID.equals(newAppID)) {
                throw new IllegalArgumentException(
                   "Only one Application-ID can be specified per message");
            }
        } else {
            applicationID = newAppID;
        }   
    }
     
    /** Array of allowed MMS header fields. */
    static final String[] ALLOWED_HEADER_FIELDS = {
        "X-Mms-Delivery-Time",
        "X-Mms-Priority"
    };
    
    /** Array of default header values. */
    static final String[] DEFAULT_HEADER_VALUES = {
        null,
        "Normal"
    };
    
    /** Array of known header fields. */
    static final String[] KNOWN_HEADER_FIELDS = {
        // mandatory fields
        "X-Mms-Message-Type",
        "X-Mms-Transaction-ID",
        "X-Mms-MMS-Version",
        "X-Mms-Content-Type",
        // optional fields supported by this implementation
        "X-Mms-Subject",
        "X-Mms-From",
        "X-Mms-To",
        "X-Mms-CC",
        "X-Mms-BCC"
    };
    
    /**
     * Checks if header field is known.
     *
     * @param headerField the header field key to check
     * @return <code>true</code> if the header is known
     */
    static boolean isKnownHeaderField(String headerField) {
        String lowerFieldName = headerField.toLowerCase();
        for (int i = 0; i < KNOWN_HEADER_FIELDS.length; ++i) {
            if (lowerFieldName.equals(KNOWN_HEADER_FIELDS[i].toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sets default values for all allowed header fields.
     */
    void setupHeaderFields() {
        headerValues = new String[ALLOWED_HEADER_FIELDS.length];
        for (int i = 0; i < DEFAULT_HEADER_VALUES.length; ++i) {
            headerValues[i] = DEFAULT_HEADER_VALUES[i];
        }
    }

    /**
     * Gets the location of the requested header from the
     * list of allowed header fields.
     *
     * @param headerField the header field key to be checked
     * @return the index of the requested header field, or 
     * -1 if the header is not supported
     */
    static int getHeaderFieldIndex(String headerField) {
        String lowerFieldName = headerField.toLowerCase();
        for (int i = 0; i < ALLOWED_HEADER_FIELDS.length; ++i) {
            if (lowerFieldName.equals(ALLOWED_HEADER_FIELDS[i].toLowerCase())) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Checks if allowed to access the requested header field.
     *
     * @param field the header field key to check
     * @return <code>true</code> if the header exists
     */
    boolean isAllowedToAccessHeaderField(String field) {
        return (getHeaderFieldIndex(field) != -1);
    }
    
    /**
     * Checks the header field value.
     * @param headerIndex the index of the header field to check
     * @param value the value to be checked
     * @exception Error if an invalid header index is requested
     * @exception IllegalArgumentException if the value is not
     * a valid delivery time or priority
     */
    static void checkHeaderValue(int headerIndex, String value) {
        switch (headerIndex) {
            case 0: // X-Mms-Delivery-Time
                try {
                    Long.parseLong(value);
                    return;
                } catch (NumberFormatException nfe) {
                    // do nothing... we'll report the error in a second
                }
                break;
            case 1: // X-Mms-Priority
            {
                String lower = value.toLowerCase();
                if (lower.equals("normal") || lower.equals("high") ||
                    lower.equals("low")) {
                    return;
                }
                // we'll report the error in a second
                break;
            }
            default:
                throw new Error("Unknown headerIndex: " + headerIndex);
        }
        // report the error
        throw new IllegalArgumentException("Illegal value for header " +
            ALLOWED_HEADER_FIELDS[headerIndex] + ": " + value);                
    }
    
    /**
     * Adds an address to the multipart message.
     * @param type the address type ("to", "cc" or "bcc") as a 
     *      <code>String</code>. Each message can have none or multiple "to",
     *      "cc" and "bcc" addresses. Each address is added separately. The
     *      type is not case-sensitive.
     * @param address the address as a <code>String</code>
     * @return <code>true</code> if it was possible to add the address, else
     *      <code>false</code>
     * @exception java.lang.IllegalArgumentException if type is none of 
     *      "to", "cc", or "bcc" or if <code>address</code> is not valid.
     * @see #setAddress(String)
     */
    public boolean addAddress(java.lang.String type, java.lang.String address) 
	throws IllegalArgumentException {

        MMSAddress parsedAddress = checkValidAddress(address);
        String appID = parsedAddress.appId;
        if (appID != null) {
            checkApplicationID(appID);
        }
        
        Vector which = getAddressList(type);
        if (!which.contains(address)) {
            which.addElement(address);
            return true;
        }
        return false;
    }
    
    /**
     * Maximum size of MMS message.
     * Default value is 30720 (30K)
     */
    static final int MAX_TOTAL_SIZE = 30 * 1024;
    
    /**
     * Attaches a <code>MessagePart</code> to the multipart message.
     * @param part <code>MessagePart</code> to add
     * @exception java.lang.IllegalArgumentException if the Content-ID of the
     *      <code>MessagePart</code> conflicts with a Content-ID of a
     *      <code>MessagePart</code> already contained in this 
     *      <code>MultiPartMessage</code>. The Content-IDs must be unique
     *      within a MultipartMessage.
     * @exception NullPointerException if the parameter is <code>null</code>
     * @exception SizeExceededException if it's not possible to attach the 
     *      <code>MessagePart</code>.
     */
    public void addMessagePart(MessagePart part) throws SizeExceededException {
        String thisContentID = part.getContentID();
        boolean duplicateContentID = false;
        int totalSizeSoFar = 0;
        int numPartsSoFar = parts.size();
        for (int i = 0; i < numPartsSoFar; ++i) {
            MessagePart onePart = (MessagePart)parts.elementAt(i);
            if (thisContentID.equals(onePart.getContentID())) {
                throw new IllegalArgumentException(
                    "Cannot add duplicate content-id: " + thisContentID);
            }
            totalSizeSoFar += onePart.getLength();
        }
        if (totalSizeSoFar + part.getLength() > MAX_TOTAL_SIZE) {
            throw new SizeExceededException(
                "Adding this MessagePart would exceed max size of " + 
                MAX_TOTAL_SIZE + " bytes");
        }
        parts.addElement(part);
    }

    /**
     * Returns the "from" address associated with this message, e.g. address of
     * the sender. If the message is a newly created message, e.g. not a 
     * received one, then the first "to" address is returned.
     * Returns <code>null</code> if the "from" or "to" address for the
     * message, dependent on the case, are not set.
     * Note: This design allows sending responses to a received message easily 
     * by reusing the same <code>Message</code> object and just replacing the
     * payload. The address field can normally be kept untouched (unless the
     * used messaging protocol requires some special handling of the address).
     * @return the "from" or "to" address of this message, or <code>null</code>
     *      if the address that is expected as a result of this method is not
     *      set
     * @see #setAddress(String)
     */
    public java.lang.String getAddress() {
        String returnMe = null;
        Date tStamp = getTimestamp();
        if (tStamp == null || tStamp.getTime() == 0L) {            
            // not a received message - use the first "to" address
            if (to.size() > 0) {
                returnMe = (String)to.elementAt(0);
            }
        } else {            
            // received - use the "from" address
            returnMe = super.getAddress();
        }       
        return returnMe;
    }
        
    /**
     * Gets the addresses of the multipart message of the specified type (e.g.
     * "to", "cc", "bcc" or "from") as <code>String</code>. The method is not
     * case sensitive.
     * @param type the type of addresses to return
     * @return the addresses as a <code>String</code> array or <code>null</code>
     *      if this value is not present.
     */
    public java.lang.String[] getAddresses(java.lang.String type) {
        if (type.toLowerCase().equals("from")) {
            String address = super.getAddress();
            if (address == null) {
                return null;
            }
            return new String[] { address };
        }
        Vector which = getAddressList(type);
        int num = which.size();
        if (num == 0) {
            return null;
        }
        String[] addresses = new String[num];
        which.copyInto(addresses);
        return addresses;
    }
    
    /**
     * Gets the content of the specific header field of the multipart message.
     * <p>
     * See Appendix D for known header fields.
     *
     * @see #setHeader
     *
     * @param headerField the name of the header field as a <code>String</code>
     *
     * @return the content of the specified header field as a 
     *      <code>String</code> or <code>null</code> of the specified header
     *      field is not present.
     * @exception SecurityException if the access to specified header field is 
     *      restricted
     * @exception IllegalArgumentException if <code>headerField</code> 
     *      is unknown
     */      
    public java.lang.String getHeader(java.lang.String headerField) {
        if (headerField == null) {
            throw new IllegalArgumentException(
                "headerField must not be null");
        }
        if (isAllowedToAccessHeaderField(headerField)) {                
            int index = getHeaderFieldIndex(headerField);
            if (index != -1) {
                return headerValues[index];
            }
            throw new Error("Allowed to access field but it has no index");
        }
        if (isKnownHeaderField(headerField)) {
            throw new SecurityException(
                "Cannot access restricted header field: " + headerField);
        } else {
            throw new IllegalArgumentException("Unknown header field: " + 
                headerField);
        }
    }
    
    /**
     * This method returns a <code>MessagePart</code> from the message that
     * matches the content-id passed as a parameter.
     * @param contentID the content-id for the <code>MessagePart</code> to be
     *      returned
     * @return <code>MessagePart</code> that matches the provided content-id or
     *      <code>null</code> if there is no <code>MessagePart</code> in this
     *      message with the provided content-id
     * @exception NullPointerException if the parameter is <code>null</code>
     */
    public javax.wireless.messaging.MessagePart getMessagePart(
        java.lang.String contentID) {
        if (contentID == null) {
            throw new NullPointerException("contentID must not be null");
        }
        int numParts = parts.size();
        for (int i = 0; i < numParts; ++i) {
            MessagePart onePart = (MessagePart)parts.elementAt(i);
            if (contentID.equals(onePart.getContentID())) {
                return onePart;
            }
        }
        return null;
    }
    
    /**
     * Returns an array of all <code>MessagePart</code>s of this message.
     * @return array of <code>MessagePart</code>s, or <code>null</code> if no
     *      <code>MessagePart</code>s are available
     */
    public javax.wireless.messaging.MessagePart[] getMessageParts() {
        int num = parts.size();
        if (num == 0) {
            return null;
        }
        MessagePart[] msgParts = new MessagePart[num];
        parts.copyInto(msgParts);
        return msgParts;
    }
    
    /**
     * Returns the <code>contentId</code> of the start <code>MessagePart</code>.
     * The start <code>MessagePart</code> is set in 
     * <code>setStartContentId(String)</code>
     * @return the content-id of the start <code>MessagePart</code> or
     *      <code>null</code> if the start <code>MessagePart</code> is not set.
     * @see #setStartContentId(String)
     */
    public java.lang.String getStartContentId() {
        return startContentID;
    }
    
    /**
     * Gets the subject of the multipart message.
     * @return the message subject as a <code>String</code> or <code>null</code>
     *      if this value is not present.
     * @see #setSubject
     */
    public java.lang.String getSubject() {
        return subject;
    }

    /**
     * Cleans application Id value.
     */
    private void cleanupAppID() throws IllegalStateException {
        Vector addresses = to;
        boolean checkedTo = false;
        boolean checkedCC = false;
        int currIndex = 0;
        boolean matchedAppID = false; 
        while (true) {
            if (currIndex >= addresses.size()) {
                if (!checkedTo) {
                    checkedTo = true;
                    addresses = cc;
                    currIndex = 0;
                    continue;
                } else if (!checkedCC) {
                    checkedCC = true;
                    addresses = bcc;
                    currIndex = 0;
                    continue;
                } else {
                    break;
                }
                    
            }
            String addr = (String)addresses.elementAt(currIndex++); 
            MMSAddress parsedAddress = MMSAddress.getParsedMMSAddress(addr);
            if (parsedAddress == null || 
                parsedAddress.type == MMSAddress.INVALID_ADDRESS ||
                parsedAddress.type == MMSAddress.APP_ID) {
                throw new IllegalStateException(
                    "Invalid MMS address: " + addr);
            }
            String thisAppID = parsedAddress.appId;
            if (thisAppID != null && thisAppID.equals(applicationID)) {
                matchedAppID = true;
            }
        }
        if (!matchedAppID) {
            applicationID = null;
        }
    }

    /**
     * Removes an address from the multipart message.
     * @param type the address type ("to", "cc", or "bcc") as a
     *      <code>String</code>
     * @param address the address as a <code>String</code>
     * @return <code>true</code> if it was possible to delete the address, else
     *      <code>false</code>
     * @throws NullPointerException is type is <code>null</code>
     * @throws java.lang.IllegalArgumentException if type is none of "to", "cc",
     *      or "bcc"
     */
    public boolean removeAddress(java.lang.String type,
        java.lang.String address) {
        Vector which = getAddressList(type);
        boolean result = which.removeElement(address);
        cleanupAppID();
        return result;
    }
    
    /**
     * Removes all addresses of types "to", "cc", "bcc" from the
     * multipart message.
     * @see #setAddress(String)
     * @see #addAddress(String, String)
     */
    public void removeAddresses() {
        to.removeAllElements();
        cc.removeAllElements();
        bcc.removeAllElements();
        applicationID = null;
    }
    
    /**
     * Removes all addresses of the specified type from the multipart message.
     * @param type the address type ("to", "cc", or "bcc") as a 
     *      <code>String</code>
     * @throws NullPointerException if type is <code>null</code>
     * @throws java.lang.IllegalArgumentException if type is none of "to", "cc",
     *      or "bcc"
     */
    public void removeAddresses(java.lang.String type) {
        Vector which = getAddressList(type);
        which.removeAllElements();
        cleanupAppID();
    }        
    
    /**
     * Removes a <code>MessagePart</code> from the multipart message.
     * @param part <code>MessagePart</code> to delete
     * @return <code>true</code> if it was possible to remove the
     *      <code>MessagePart</code>, else <code>false</code>
     * @exception NullPointerException id the parameter is <code>null</code>
     */
    public boolean removeMessagePart(MessagePart part) {
        if (part == null) {
            throw new NullPointerException("part must not be null");
        }
        if (part.getContentID().equals(startContentID)) {
            startContentID = null;
        }
        return parts.removeElement(part);
    }
    
    /**
     * Removes a <code>MessagePart</code> with the specific 
     * <code>contentID</code> from the multipart message.
     * @param contentID identifies which <code>MessagePart</code> must be
     *      deleted.
     * @return <code>true</code> if it was possible to remove the
     *      <code>MessagePart</code>, else <code>false</code>
     * @exception NullPointerException if the parameter is <code>null</code>
     */
    public boolean removeMessagePartId(java.lang.String contentID) {
        if (contentID == null) {
            throw new NullPointerException("contentID must not be null");
        }
        int numParts = parts.size();
        for (int i = 0; i < numParts; ++i) {
            MessagePart onePart = (MessagePart)parts.elementAt(i);
            if (contentID.equals(onePart.getContentID())) {
                if (contentID.equals(startContentID)) {
                    startContentID = null;
                }
                parts.removeElementAt(i);
                return true;
            }
        }
        return false;
    }         
    
    /**
     * Removes <code>MessagePart</code>s with the specific content location
     * from the multipart message. All <code>MessagePart</code>s with the
     * specified <code>contentLocation</code> are removed.
     * @param contentLocation content location (file name) of the 
     *      <code>MessagePart</code>
     * @return <code>true</code> if it was possible to remove the
     *      <code>MessagePart</code>, else <code>false</code>
     * @exception NullPointerException if the parameter is <code>null</code>
     */
    public boolean removeMessagePartLocation(java.lang.String contentLocation) {
        if (contentLocation == null) {
            throw new NullPointerException("contentLocation must not be null");
        }
        int numParts = parts.size();
        boolean found = false;
        for (int i = 0; i < numParts; ++i) {
            MessagePart onePart = (MessagePart)parts.elementAt(i);
            if (contentLocation.equals(onePart.getContentLocation())) {
                if (onePart.getContentID().equals(startContentID)) {
                    startContentID = null;
                }
                parts.removeElementAt(i);
                --numParts;
                --i;
                found = true;
            }
        }
        return found;
    }

    /**
     * Sets the "to" address associated with this message. It works the same way
     * as <code>addAddress("to", addr)</code>. The address may be set to
     * <code>null</code>.
     * @param address address for the message
     * @see #getAddress()
     * @see #addAddress(String, String)
     */
    public void setAddress(java.lang.String address) {
        if (address != null) {
            addAddress("to", address);
        }
        // otherwise it's a no-op.
    }
    
    /**
     * Sets the specified header of the multipart message. The header value can
     * be <code>null</code>.
     * <p>
     * See Appendix D for known header fields.
     *
     * @see #getHeader(String)
     *
     * @param headerField the name of the header field as a <code>String</code>
     * @param headerValue the value of the header as a <code>String</code>
     *
     * @exception java.lang.IllegalArgumentException if 
     *      <code>headerField</code> is unknown, or if 
     *      <code>headerValue</code> is not correct (depends on
     *      <code>headerField</code>!)
     * @exception NullPointerException if <code>headerField</code> is 
     *      <code>null</code>
     * @exception SecurityException if the access to specified header field is
     *      restricted
     */
    public void setHeader(java.lang.String headerField,
        java.lang.String headerValue) {
        if (isAllowedToAccessHeaderField(headerField)) {
            int index = getHeaderFieldIndex(headerField);
            if (index != -1) {
                if (headerValue != null) {
                    checkHeaderValue(index, headerValue);
                }
                headerValues[index] = headerValue;
                return;
            }
            throw new Error("Allowed to access field but it has no index");
        }
        if (isKnownHeaderField(headerField)) {
            throw new SecurityException(
                "Cannot access restricted header field: " + headerField);
        } else {
            throw new IllegalArgumentException("Unknown header field: " + 
                headerField);
        }
    }
    
    /**
     * Sets the <code>Content-ID</code> of the start <code>MessagePart</code> of
     * a multipart related message. The <code>Content-ID</code> may be set to
     * <code>null</code>. The <code>StartContentId</code> is set for the
     * MessagePart that is used to reference the other MessageParts of the
     * MultipartMessage for presentation or processing purposes.
     * @param contentId as a <code>String</code>
     * @exception java.lang.IllegalArgumentException if 
     *      <code>contentId</code> is none of the added 
     *      <code>MessagePart</code> objects matches the
     *      <code>contentId</code>
     * @see #getStartContentId()
     */
    public void setStartContentId(java.lang.String contentId) {
        if (contentId != null) {
            if (getMessagePart(contentId) == null) {
                throw new IllegalArgumentException("Unknown contentId: "
                    + contentId);
            }
        }
        startContentID = contentId;
    }
    
    /**
     * Sets the Subject of the multipart message. This value can be
     * <code>null</code>.
     * @param subject the message subject as a <code>String</code>
     * @see #getSubject()
     */
    public void setSubject(java.lang.String subject) {
        if (subject != null && subject.length() > 40) { // MMS Conformance limit
            throw new IllegalArgumentException("Subject exceeds 40 chars");
        }
        this.subject = subject;
    }
    
    /**
     * Returns only the device part of the MMS Address.
     * @return the device portion of the MMS Address
     * @param address the MMS address
     * @throws IllegalArgumentException if the MMS Address has no
     *  device portion.
     */
    static String getDevicePortionOfAddress(String address) 
        throws IllegalArgumentException {
        MMSAddress parsedAddress = MMSAddress.getParsedMMSAddress(address);
        if (parsedAddress == null || parsedAddress.address == null) {
            throw new IllegalArgumentException("MMS Address " 
					       +"has no device portion");
        }
        return parsedAddress.address;
    }
         
    /**
     * Writes a vector to an output stream. If the contents are MMS addresses,
     * as indicated by the <code>isAddress</code> parameter, then
     * only the device
     * part of the address is placed into the vector, not the
     * application-id, if any.
     * @param dos the data output stream for writing
     * @param v the array to be written
     * @param isAddress is the contents of the vector an MMS address.
     * @exception IOException if any I/O errors occur
     */
    static void writeVector(DataOutputStream dos, Vector v, boolean isAddress) 
        throws IOException {
        StringBuffer buff = new StringBuffer(); 
        int len = v.size();
        String appendMe = null; 
        if (len > 0) {
            appendMe = (String)v.elementAt(0);
            if (isAddress) {
                appendMe = getDevicePortionOfAddress(appendMe);
            }
            buff.append(appendMe);
        }
        for (int i = 1; i < len; ++i) {
            buff.append("; ");
            appendMe = (String)v.elementAt(i);
            if (isAddress) {
                appendMe = getDevicePortionOfAddress(appendMe);
            }
            buff.append(appendMe);
        }
        dos.writeUTF(buff.toString());
    }
    
    /**
     * Reads a vector from an input stream. If the content is an MMS Address,
     * as indicated by the <code>isAddress</code> parameter, then the prefix
     * <code>"mms://"</code> is added to each address.
     * @param dis the data input stream for reading
     * @param v the array to be returned
     * @param isAddress the contents are MMS Addresses
     * @exception IOException if any I/O errors occur
     */
    static void readVector(DataInputStream dis, Vector v, boolean isAddress)
        throws IOException {
        String inputStr = dis.readUTF();
        int prevDelim = -2;
        String prefix = "";
        if (isAddress) {
            prefix = "mms://";
        }
        while (prevDelim != -1) {
            int nextDelim = inputStr.indexOf("; ", prevDelim + 2);
            String addStr = null;
            if (nextDelim == -1) {
                addStr = prefix + inputStr.substring(prevDelim + 2);
            } else {
                addStr = prefix + inputStr.substring(prevDelim + 2, nextDelim);
            }
            v.addElement(addStr);
            prevDelim = nextDelim;
        }
    }

    /**
     * Writes a message part to the output stream
     * @param dos the data output stream for writing
     * @param p the message part to be written
     * @exception IOException if any I/O errors occur
     */    
    static void writeMessagePart(DataOutputStream dos, MessagePart p)
        throws IOException {
        dos.writeUTF("Content-Type");
        StringBuffer contentType = new StringBuffer(p.getMIMEType());
        String loc = p.getContentLocation(); 
        if (loc != null) {
            contentType.append("; name=\"");
            contentType.append(loc);
            contentType.append("\"");
        }
        dos.writeUTF(contentType.toString());
        String id = p.getContentID();
        if (id != null) {
            dos.writeUTF("Content-ID");
            dos.writeUTF(id);
        }
        String enc = p.getEncoding();
        if (enc != null) {
            dos.writeUTF("Encoding");
            dos.writeUTF(enc);
        }
        // the payload
        dos.writeUTF("Content-Length");
        dos.writeInt(p.getLength());
        dos.writeUTF("Content");
        dos.write(p.getContent());
    }

    /**
     * Create a new message part from the input stream
     * @param dis the data input stream for reading
     * @exception IOException if any I/O errors occur
     * @return the message object instance
     */
    static MessagePart createMessagePart(DataInputStream dis)
	throws IOException {
        String nextField = dis.readUTF(); // eats "Content-Type" header
        String contentType = dis.readUTF();
        nextField = dis.readUTF();
        String contentID = null;
        if (nextField.equals("Content-ID")) {
            contentID = dis.readUTF();
            nextField = dis.readUTF();
        }
        String encoding = null;
        if (nextField.equals("Encoding")) {
            encoding = dis.readUTF();
            nextField = dis.readUTF();
        }
        // "Content-Length" was just eaten
        int length = dis.readInt();
        byte[] contents = new byte[length];
        nextField = dis.readUTF(); // eats the "Content" header
        dis.readFully(contents);
        // now separate the content location and mime type
        String mimeType = contentType;
        String contentLocation = null;
        int sepPos = contentType.indexOf(';');
        if (sepPos != -1 && contentType.substring(sepPos).
            startsWith("; name=\"")) {
            contentLocation = contentType.substring(sepPos+8, // ; name="
						    contentType.length()-1);
            mimeType = contentType.substring(0, sepPos);
        }
        return new MessagePart(contents, mimeType, contentID, contentLocation,
			       encoding);
    }

    /** The content type for the MMS message. */
    static final String STREAM_SIGNATURE = "application/vnd.wap.mms-message";

    /**
     * Gets the message object as a byte array.
     *
     * @exception IOException if any I/O errors occur
     * @return the serialized byte array of the message object
     */
    public byte[] getAsByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(STREAM_SIGNATURE);
        
        dos.writeUTF("X-Mms-Message-Type");
        dos.writeUTF("m-send-req");
        dos.writeUTF("X-Mms-Transaction-ID");
        dos.writeUTF(String.valueOf(System.currentTimeMillis()));      
        dos.writeUTF("X-Mms-Version");
        dos.writeUTF("1.0");
        for (int i = 0; i < ALLOWED_HEADER_FIELDS.length; ++i) {
            String headerValue = headerValues[i];
            if (headerValue != null) {
                dos.writeUTF(ALLOWED_HEADER_FIELDS[i]);
                dos.writeUTF(headerValue);
            }
        }
        String fromAddress = super.getAddress();
        if (fromAddress != null) {
            dos.writeUTF("From");
            dos.writeUTF(getDevicePortionOfAddress(fromAddress));
        }
        if (to.size() != 0) {
            dos.writeUTF("To");
            writeVector(dos, to, true);
        }
        if (cc.size() != 0) {
            dos.writeUTF("Cc");
            writeVector(dos, cc, true);
        }
        if (bcc.size() != 0) {
            dos.writeUTF("Bcc");
            writeVector(dos, bcc, true);
        }
        long date = 0L;
        Date tStamp = getTimestamp();
        if (tStamp != null && (date = tStamp.getTime()) != 0L) {
            dos.writeUTF("Date");
            dos.writeUTF(String.valueOf(date));
        }
        if (subject != null) {
            dos.writeUTF("Subject");
            dos.writeUTF(subject);
        }
        dos.writeUTF("Content-Type");
        Vector contentTypeElements = new Vector();
        if (startContentID != null) {
            contentTypeElements.addElement(
                "application/vnd.wap.multipart.related");
        } else {
            contentTypeElements.addElement(
                "application/vnd.wap.multipart.mixed");
        }
        if (startContentID != null) {
            contentTypeElements.addElement("start = <" + startContentID + ">");
            contentTypeElements.addElement("type = " + 
                getMessagePart(startContentID).getMIMEType());
        }
        if (applicationID != null) {
            contentTypeElements.addElement("Application-ID = " + applicationID);
        }
        if (replyToApplicationID != null) {
            contentTypeElements.addElement("Reply-To-Application-ID = " +
                replyToApplicationID);
        }
        writeVector(dos, contentTypeElements, false);
        dos.writeUTF("nEntries");
        int numParts = parts.size();
        dos.writeUTF(String.valueOf(numParts));
        for (int i = 0; i < numParts; ++i) {
            MessagePart p = (MessagePart)parts.elementAt(i);
            writeMessagePart(dos, p);
        }
        dos.close();
        byte[] returnMe = baos.toByteArray();
        baos.close();
        return returnMe;
    }

    /**
     * Create a message object from a serialized byte array.
     *
     * @param data a serialized byte array of a message object
     * @return the multipart message object
     * @exception IOException if any I/O errors occur
     */    
    public static MultipartObject createFromByteArray(byte[] data) 
        throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        
        String signature = dis.readUTF();
        if (!signature.equals(STREAM_SIGNATURE)) {
            throw new IOException("invalid data format");
        }
        // eat the first 6 entries: "X-Mms-Message-Type", "m-send-req", 
        // "X-Mms-Transaction-ID", <transactionID>, "X-Mms-Version", "1.0"
        for (int i = 0; i < 6; ++i) {
            dis.readUTF();
        }
        
        String[] headerValues = new String[ALLOWED_HEADER_FIELDS.length];
        String nextField = dis.readUTF();
        int headerIndex;
        while ((headerIndex = getHeaderFieldIndex(nextField)) != -1) { 
            headerValues[headerIndex] = dis.readUTF();
            nextField = dis.readUTF();   
        }
        String fromAddress = null;
        if (nextField.equals("From")) {
            fromAddress = "mms://" + dis.readUTF();
            nextField = dis.readUTF();
        }
        Vector to = new Vector();
        if (nextField.equals("To")) {
            readVector(dis, to, true);
            nextField = dis.readUTF();
        }
        Vector cc = new Vector();
        if (nextField.equals("Cc")) {
            readVector(dis, cc, true);
            nextField = dis.readUTF();
        }
        Vector bcc = new Vector();
        if (nextField.equals("Bcc")) {
            readVector(dis, bcc, true);
            nextField = dis.readUTF();            
        }
        long date = 0L;
        if (nextField.equals("Date")) {
            String dateStr = dis.readUTF();
            try {
                date = Long.parseLong(dateStr);
            } catch (NumberFormatException nfe) {
                date = 0L;
            }
            nextField = dis.readUTF();
        }
        String subject = null;
        if (nextField.equals("Subject")) {
            subject = dis.readUTF();
            nextField = dis.readUTF();
        }
        // nextField is "Content-Type"
        String startContentID = null;
        String applicationID = null;
        String replyToApplicationID = null;
        Vector contentTypeElements = new Vector();
        readVector(dis, contentTypeElements, false);
        int numContentTypeElements = contentTypeElements.size();
        for (int i = 0; i < numContentTypeElements; ++i) {
            String element = (String)contentTypeElements.elementAt(i);
            if (element.startsWith("start = <")) {
                startContentID = element.substring(9);
                startContentID = startContentID.substring(0, 
                    startContentID.length()-1);
            } else if (element.startsWith("Application-ID = ")) {
                applicationID = element.substring(17);
            } else if (element.startsWith("Reply-To-Application-ID = ")) {
                replyToApplicationID = element.substring(26);
            }
        }        
        nextField = dis.readUTF();
        // nextField is "nEntries"
        int numParts = 0;
        String numPartsStr = dis.readUTF();
        try {
            numParts = Integer.parseInt(numPartsStr);
        } catch (NumberFormatException nfe) {
            numParts = 0;
        }
        Vector parts = new Vector();
        for (int i = 0; i < numParts; ++i) {
            parts.addElement(createMessagePart(dis));
        }
        dis.close();
        bais.close();

        MultipartObject mpo = new MultipartObject(fromAddress);
        mpo.setTimeStamp(date);
        mpo.headerValues = headerValues;
        mpo.subject = subject;
        mpo.startContentID = startContentID;
        mpo.to = to;
        mpo.cc = cc;
        /* 
         * Uncomment this if you want the "bcc"s to be visible to the recipients
        mpo.bcc = bcc;
         */
        mpo.parts = parts;
        mpo.applicationID = applicationID;
        mpo.replyToApplicationID = replyToApplicationID;
        return mpo;
    }

    /**
     * Gets the message object header as a byte array. The header is composed of
     * a number of fields and is exclusive of the <code>MessagePart</code>
     * contents.
     *
     * @throws IOException if any I/O errors occur.
     * @return the serialized byte array of the message object
     */
    public byte[] getHeaderAsByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF(STREAM_SIGNATURE);
        
        // Write headers that 
        dos.writeUTF("X-Mms-Message-Type");
        dos.writeUTF("m-send-req");
        dos.writeUTF("X-Mms-Transaction-ID");
        dos.writeUTF(String.valueOf(System.currentTimeMillis()));      
        dos.writeUTF("X-Mms-Version");
        dos.writeUTF("1.0");

        for (int i = 0; i < ALLOWED_HEADER_FIELDS.length; ++i) {
            String headerValue = headerValues[i];
            if (headerValue != null) {
                dos.writeUTF(ALLOWED_HEADER_FIELDS[i]);
                dos.writeUTF(headerValue);
            }
        }
        String fromAddress = super.getAddress();
        if (fromAddress != null) {
            dos.writeUTF("From");
            dos.writeUTF(getDevicePortionOfAddress(fromAddress));
        }
        if (to.size() != 0) {
            dos.writeUTF("To");
            writeVector(dos, to, true);
        }
        if (cc.size() != 0) {
            dos.writeUTF("Cc");
            writeVector(dos, cc, true);
        }
        if (bcc.size() != 0) {
            dos.writeUTF("Bcc");
            writeVector(dos, bcc, true);
        }
        long date = 0L;
        Date tStamp = getTimestamp();
        if (tStamp != null && (date = tStamp.getTime()) != 0L) {
            dos.writeUTF("Date");
            dos.writeUTF(String.valueOf(date));
        }
        if (subject != null) {
            dos.writeUTF("Subject");
            dos.writeUTF(subject);
        }
        dos.writeUTF("Content-Type");
        Vector contentTypeElements = new Vector();
        if (startContentID != null) {
            contentTypeElements.addElement(
                "application/vnd.wap.multipart.related");
        } else {
            contentTypeElements.addElement(
                "application/vnd.wap.multipart.mixed");
        }
        if (startContentID != null) {
            contentTypeElements.addElement("start = <" + startContentID + ">");
            contentTypeElements.addElement("type = " + 
                getMessagePart(startContentID).getMIMEType());
        }
        if (applicationID != null) {
            contentTypeElements.addElement("Application-ID = " + applicationID);
        }
        if (replyToApplicationID != null) {
            contentTypeElements.addElement("Reply-To-Application-ID = " +
                replyToApplicationID);
        }
        writeVector(dos, contentTypeElements, false);
        dos.close();
        byte[] returnMe = baos.toByteArray();
        baos.close();
        return returnMe;
    }

    /**
     * Gets the message object body as a byte array. The body is composed of a
     * single header that states the number of entries, followed by a serialized
     * array of <code>MessagePart</code> objects.
     *
     * @throws IOException if any I/O errors occur.
     * @return the serialized byte array of the message body.
     */
    public byte[] getBodyAsByteArray() throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeUTF("nEntries");
        int numParts = parts.size();
        dos.writeUTF(String.valueOf(numParts));
        for (int i = 0; i < numParts; ++i) {
            MessagePart p = (MessagePart)parts.elementAt(i);
            writeMessagePart(dos, p);
        }

        dos.close();
        byte[] returnMe = baos.toByteArray();
        baos.close();
        return returnMe;
    }

}

