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
 
package javax.wireless.messaging;

/**
 * This class is defined by the JSR-205 specification
 * <em>Wireless Messaging API (WMA)
 * Version 2.0.</em>
 */
// JAVADOC COMMENT ELIDED
public interface MultipartMessage extends Message {
    
    // JAVADOC COMMENT ELIDED
    boolean addAddress(java.lang.String type, java.lang.String address);
    
    // JAVADOC COMMENT ELIDED
    void addMessagePart(MessagePart part) throws SizeExceededException;
    
    // JAVADOC COMMENT ELIDED
    java.lang.String getAddress();
    
    // JAVADOC COMMENT ELIDED
    java.lang.String[] getAddresses(java.lang.String type);
    
    // JAVADOC COMMENT ELIDED
    java.lang.String getHeader(java.lang.String headerField);
    
    // JAVADOC COMMENT ELIDED
    javax.wireless.messaging.MessagePart getMessagePart(
        java.lang.String contentID);
    
    // JAVADOC COMMENT ELIDED
    javax.wireless.messaging.MessagePart[] getMessageParts();
    
    // JAVADOC COMMENT ELIDED
    java.lang.String getStartContentId();
    
    // JAVADOC COMMENT ELIDED
    java.lang.String getSubject();
    
    // JAVADOC COMMENT ELIDED
    boolean removeAddress(java.lang.String type, java.lang.String address);
    
    // JAVADOC COMMENT ELIDED
    void removeAddresses();
    
    // JAVADOC COMMENT ELIDED
    void removeAddresses(java.lang.String type);
    
    // JAVADOC COMMENT ELIDED
    boolean removeMessagePart(MessagePart part);    
    
    // JAVADOC COMMENT ELIDED
    boolean removeMessagePartId(java.lang.String contentID);
    
    // JAVADOC COMMENT ELIDED
    boolean removeMessagePartLocation(java.lang.String contentLocation);
    
    // JAVADOC COMMENT ELIDED
    void setAddress(java.lang.String addr);
    
    // JAVADOC COMMENT ELIDED
    void setHeader(java.lang.String headerField, java.lang.String headerValue);
    
    // JAVADOC COMMENT ELIDED
    void setStartContentId(java.lang.String contentId);
    
    // JAVADOC COMMENT ELIDED
    void setSubject(java.lang.String subject);
}
