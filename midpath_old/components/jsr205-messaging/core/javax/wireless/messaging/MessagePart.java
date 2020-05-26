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

import java.io.*;

/**
 * This class is defined by the JSR-205 specification
 * <em>Wireless Messaging API (WMA)
 * Version 2.0.</em>
 */
// JAVADOC COMMENT ELIDED
public class MessagePart {
 
    // IMPL_NOTE: allow maximum part size to be externally set
    /** Maximum size for message part. */
    static int MAX_PART_SIZE_BYTES = 30720; // 30K
    // JAVADOC COMMENT ELIDED
    void construct(byte[] contents, int offset, int length, 
        java.lang.String mimeType, java.lang.String contentId,
        java.lang.String contentLocation, java.lang.String enc) throws
        SizeExceededException {

        if (length > MAX_PART_SIZE_BYTES) {
            throw new SizeExceededException(
                "InputStream data exceeds " +
                "MessagePart size limit");            
        }

        if (mimeType == null) {
            throw new IllegalArgumentException("mimeType must be specified");
        }
        checkContentID(contentId);
        checkContentLocation(contentLocation);
        if (length < 0) {
            throw new IllegalArgumentException("length must be >= 0");
        }
        if (contents != null && offset + length > contents.length) {
            throw new IllegalArgumentException(
                "offset + length exceeds contents length");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
        checkEncodingScheme(enc);
    
        if (contents != null) {
            this.content = new byte[length]; 
            System.arraycopy(contents, offset, this.content, 0, length);
        }
        
        this.mimeType = mimeType;
        this.contentID = contentId;
        this.contentLocation = contentLocation;
        this.encoding = enc;
    }
 
    // JAVADOC COMMENT ELIDED
    public MessagePart(byte[] contents, int offset, int length, 
        java.lang.String mimeType, java.lang.String contentId,
        java.lang.String contentLocation, java.lang.String enc) throws
        SizeExceededException {
        construct(contents, offset, length, mimeType, contentId,
            contentLocation, enc);
    }
       
    // JAVADOC COMMENT ELIDED
    public MessagePart(byte[] contents, java.lang.String mimeType, 
        java.lang.String contentId, java.lang.String contentLocation,
        java.lang.String enc) throws SizeExceededException {
        construct(contents, 0, (contents == null ? 0 : contents.length),
            mimeType, contentId, contentLocation, enc);
    }
    /** Buffer size 2048. */    
    static final int BUFFER_SIZE = 2048;
    
    // JAVADOC COMMENT ELIDED
    public MessagePart(java.io.InputStream is, java.lang.String mimeType, 
        java.lang.String contentId, java.lang.String contentLocation,
        java.lang.String enc) throws IOException, SizeExceededException {
        byte[] bytes = {};
        if (is != null) {
            ByteArrayOutputStream accumulator = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int readBytes = 0;
            while ((readBytes = is.read(buffer)) != -1) {
                accumulator.write(buffer, 0, readBytes);
            }
            bytes = accumulator.toByteArray();
        }
        construct(bytes, 0, bytes.length, mimeType, contentId, 
            contentLocation, enc);
    }
    
    // JAVADOC COMMENT ELIDED
    public byte[] getContent() {
        if (content == null) {
            return null;
        }
        byte[] copyOfContent = new byte[content.length];
        System.arraycopy(content, 0, copyOfContent, 0, content.length);
        return copyOfContent;
    }
    
    // JAVADOC COMMENT ELIDED
    public java.io.InputStream getContentAsStream() {
        if (content == null) {
            return new ByteArrayInputStream(new byte[0]);
        } else {
            return new ByteArrayInputStream(content);
        }
    }
    
    // JAVADOC COMMENT ELIDED
    public java.lang.String getContentID() {
        return contentID;
    }
    
    // JAVADOC COMMENT ELIDED
    public java.lang.String getContentLocation() {
        return contentLocation;
    }
    
    // JAVADOC COMMENT ELIDED
    public java.lang.String getEncoding() {
        return encoding;
    }
    
    // JAVADOC COMMENT ELIDED
    public int getLength() {
        return content == null ? 0 : content.length; 
    }
    
    // JAVADOC COMMENT ELIDED
    public java.lang.String getMIMEType() {
        return mimeType;
    }
    
    /** Content byte array. */
    byte[] content;
    /** MIME Content ID. */
    String contentID;
    /** Content location. */
    String contentLocation;
    /** Content encoding. */
    String encoding;
    /** MIME type. */
    String mimeType;

    /**
     * Verifies the content identifier.
     * @param contentId content id to be checked
     * @exception IllegalArgumentException if content id is not valid
     */    
    static void checkContentID(String contentId) 
        throws IllegalArgumentException {
        if (contentId == null) {
            throw new IllegalArgumentException("contentId must be specified");
        }
        if (contentId.length() > 100) { // MMS Conformance limit
            throw new IllegalArgumentException(
                "contentId exceeds 100 char limit");
        }
        if (containsNonUSASCII(contentId)) {
            throw new IllegalArgumentException(
                "contentId must not contain non-US-ASCII characters");
        }
    }
 
    /**
     * Verifies the content location.
     * @param contentLoc content location to be checked.
     * @exception IllegalArgumentException if content location is not valid.
     */    
    static void checkContentLocation(String contentLoc)
        throws IllegalArgumentException {
        if (contentLoc != null) {
            if (containsNonUSASCII(contentLoc)) {
                throw new IllegalArgumentException(
                    "contentLocation must not contain non-US-ASCII characters");
            }
            if (contentLoc.length() > 100) { // MMS Conformance limit
                throw new IllegalArgumentException(
                    "contentLocation exceeds 100 char limit");
            }
        }
    }
    
    /**
     * Verifies the content encoding.
     * @param encoding The content encoding to be checked.
     * @exception IllegalArgumentException if content encoding is not valid.
     */    
    static void checkEncodingScheme(String encoding)
        throws IllegalArgumentException {
        // IMPL_NOTE: check for a valid encoding scheme        
    }

    /** Lowest valid ASCII character. */
    static final char US_ASCII_LOWEST_VALID_CHAR = 32;

    /** Mask for ASCII character checks. */
    static final char US_ASCII_VALID_BIT_MASK = 0x7F;
    
    /**
     * Checks if a string contains non-ASCII characters.
     * @param str Text to be checked.
     * @return <code>true</code> if non-ASCII characters are found.
     */    
    static boolean containsNonUSASCII(String str) {
        int numChars = str.length();
        for (int i = 0; i < numChars; ++i) {
            char thisChar = str.charAt(i);
            if (thisChar < US_ASCII_LOWEST_VALID_CHAR ||
                thisChar != (thisChar & US_ASCII_VALID_BIT_MASK))
                return true;
        }
        return false;
    }
}
