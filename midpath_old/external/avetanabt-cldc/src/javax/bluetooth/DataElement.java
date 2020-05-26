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
*
* This class was written by Christian Lorenz and modified by Julien Campana
*
* @author Christian Lorenz
* @author Julien Campana
*/
package javax.bluetooth;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Vector;

import de.avetana.bluetooth.util.Debug;
/**
 * The <code>DataElement</code> class defines the various data types that a Bluetooth service attribute value may have.
 * The following table describes the data types and valid values that a <code>DataElement</code> object can store.
 * <TABLE BORDER> <TR><TH>Data Type</TH><TH>Valid Values</TH></TR> <TR><TD><code>NULL</code></TD><TD>represents a
 * <code>null</code> value </TD></TR> <TR><TD><code>U_INT_1</code></TD><TD><code> long </code> value range [0, 255]</TD></TR>
 * <TR><TD><code>U_INT_2</code></TD><TD><code>long</code> value range [0, 2<sup>16</sup>-1]</TD></TR>
 * <TR><TD><code>U_INT_4</code></TD> <TD><code>long</code> value range [0, 2<sup>32</sup>-1]</TD></TR>
 * <TR><TD><code>U_INT_8</code></TD> <TD><code>byte[]</code> value range [0, 2<sup>64</sup>-1]</TD></TR>
 * <TR><TD><code>U_INT_16</code></TD> <TD><code>byte[]</code> value range [0, 2<sup>128</sup>-1]</TD></TR>
 * <TR><TD><code>INT_1</code></TD><TD><code>long</code> value range [-128, 127]</TD></TR>
 * <TR><TD><code>INT_2</code></TD><TD><code>long</code> value range [-2<sup>15</sup>, 2<sup>15</sup>-1]</TD></TR>
 * <TR><TD><code>INT_4</code></TD><TD><code>long</code> value range [-2<sup>31</sup>, 2<sup>31</sup>-1]</TD></TR>
 * <TR><TD><code>INT_8</code></TD><TD><code>long</code> value range [-2<sup>63</sup>, 2<sup>63</sup>-1]</TD></TR>
 * <TR><TD><code>INT_16</code></TD><TD><code>byte[]</code> value range [-2<sup>127</sup>, 2<sup>127</sup>-1]</TD></TR>
 * <TR><TD><code>URL</code></TD> <TD><code>java.lang.String</code></TD></TR> <TR><TD><code>UUID</code></TD>
 * <TD><code>javax.bluetooth.UUID</code></TD></TR> <TR><TD><code>BOOL</code></TD><TD><code>boolean</code></TD></TR>
 * <TR><TD><code>STRING</code></TD> <TD><code>java.lang.String</code></TD></TR> <TR><TD><code>DATSEQ</code></TD>
 * <TD><code>java.util.Enumeration</code></TD></TR> <TR><TD><code>DATALT</code></TD>
 * <TD><code>java.util.Enumeration</code></TD></TR> </TABLE>
 * @author Christian Lorenz
 */
public class DataElement {
    private int valueType;
    private byte[] dataBytes;
    private long headerByteSize = 1;
    private Vector vector;

    /*
    * The following section defines public, static and instance
    * member variables used in the implementation of the methods.
    */

    /**
     * Defines data of type NULL. The value for data type <code>DataElement.NULL</code> is
     * implicit, i.e., there is no representation of it. Accordingly there is no method to retrieve
     * it, and attempts to retrieve the value will throw an exception. <P> The value of <code>NULL</code> is 0x00 (0).
     */
    public static final int NULL = 0x0000;

    /** Defines an unsigned integer of size one byte. <P> The value of the constant <code>U_INT_1</code> is 0x08 (8). */
    public static final int U_INT_1 = 0x0008;

    /** Defines an unsigned integer of size two bytes. <P> The value of the constant <code>U_INT_2</code> is 0x09 (9). */
    public static final int U_INT_2 = 0x0009;

    /** Defines an unsigned integer of size four bytes. <P> The value of the constant <code>U_INT_4</code> is 0x0A (10). */
    public static final int U_INT_4 = 0x000A;

    /** Defines an unsigned integer of size eight bytes. <P> The value of the constant <code>U_INT_8</code> is 0x0B (11). */
    public static final int U_INT_8 = 0x000B;

    /** Defines an unsigned integer of size sixteen bytes. <P> The value of the constant <code>U_INT_16</code> is 0x0C (12). */
    public static final int U_INT_16 = 0x000C;

    /** Defines a signed integer of size one byte. <P> The value of the constant <code>INT_1</code> is 0x10 (16). */
    public static final int INT_1 = 0x0010;

    /** Defines a signed integer of size two bytes. <P> The value of the constant <code>INT_2</code> is 0x11 (17). */
    public static final int INT_2 = 0x0011;

    /** Defines a signed integer of size four bytes. <P> The value of the constant <code>INT_4</code> is 0x12 (18). */
    public static final int INT_4 = 0x0012;

    /** Defines a signed integer of size eight bytes. <P> The value of the constant <code>INT_8</code> is 0x13 (19). */
    public static final int INT_8 = 0x0013;

    /** Defines a signed integer of size sixteen bytes. <P> The value of the constant <code>INT_16</code> is 0x14 (20). */
    public static final int INT_16 = 0x0014;

    /** Defines data of type URL. <P> The value of the constant <code>URL</code> is 0x40 (64). */
    public static final int URL = 0x0040;

    /** Defines data of type UUID. <P> The value of the constant <code>UUID</code> is 0x18 (24). */
    public static final int UUID = 0x0018;

    /** Defines data of type BOOL. <P> The value of the constant <code>BOOL</code> is 0x28 (40). */
    public static final int BOOL = 0x0028;

    /** Defines data of type STRING. <P> The value of the constant <code>STRING</code> is 0x20 (32). */
    public static final int STRING = 0x0020;

    /**
     * Defines data of type DATSEQ.  The service attribute value whose
     * data has this type must consider all the elements of the list,
     * i.e. the value is the whole set and not a subset. The elements
     * of the set can be of any type defined in this class, including DATSEQ. <P>
     * The value of the constant <code>DATSEQ</code> is 0x30 (48).
     */
    public static final int DATSEQ = 0x0030;

    /**
     * Defines data of type DATALT.  The service attribute value whose
     * data has this type must consider only one of the elements of the
     * set, i.e., the value is the not the whole set but only one
     * element of the set. The user is free to choose any one element.
     * The elements of the set can be of any type defined in this class, including DATALT. <P>
     * The value of the constant <code>DATALT</code> is 0x38 (56).
     */
    public static final int DATALT = 0x0038;

    /*
    * The following section defines public, static, and instance
    * member methods used in the class. It also defines the
    * constructors.
    */

    /** Christian Lorenz: Added this Constructor to parse <code>byte[]</code> into <code>Data</code>. */
    public DataElement(byte[] bytes, int offset) {
        long dataByteSize        = 0;
        byte dataElementType     = (byte)(bytes[offset] >> 3);
        byte dataElementSizeDesc = (byte)(bytes[offset] & 0x07);

        //parse the Element Byte Size
        switch (dataElementSizeDesc) {
            case 0:
                if (valueType == NULL) dataByteSize = 1;
                else dataByteSize = 2;
                break;
            case 1:
                dataByteSize = 2;
                break;
            case 2:
                dataByteSize = 4;
                break;
            case 3:
                dataByteSize = 8;
                break;
            case 4:
                dataByteSize = 16;
                break;
            case 5:
                headerByteSize = 2;
                dataByteSize = (long)bytes[offset + 1] & 0xff;
                break;
            case 6:
                headerByteSize = 3;
                dataByteSize = (long)(((long)bytes[offset + 1] & 0xff) << 8 | ((long)bytes[offset + 2] & 0xff));;
                break;
            case 7:
                headerByteSize = 5;
                dataByteSize = (long)(((long)bytes[offset + 1] & 0xff) << 24 | ((long)bytes[offset + 2] & 0xff) << 16 |
                    ((long)bytes[offset + 3] & 0xff) << 8 | ((long)bytes[offset + 4] & 0xff));
                break;
        }
        //for getByteSize()
        dataBytes = new byte[(int)(dataByteSize + headerByteSize)];
        System.arraycopy(bytes, offset, dataBytes, 0, dataBytes.length);
        //Debug.println(7,"new Data Element:",dataBytes);
        switch (dataElementType) {
            case 0:
                valueType = NULL;
                break;
            case 1:
                switch (dataElementSizeDesc) {
                    case 0:
                        valueType = U_INT_1;
                        break;
                    case 1:
                        valueType = U_INT_2;
                        break;
                    case 2:
                        valueType = U_INT_4;
                        break;
                    case 3:
                        valueType = U_INT_8;
                        break;
                    case 4:
                        valueType = U_INT_16;
                        break;
                }
                break;
            case 2:
                switch (dataElementSizeDesc) {
                    case 0:
                        valueType = INT_1;
                        break;
                    case 1:
                        valueType = INT_2;
                        break;
                    case 2:
                        valueType = INT_4;
                        break;
                    case 3:
                        valueType = INT_8;
                        break;
                    case 4:
                        valueType = INT_16;
                        break;
                }
                break;
            case 3:
                valueType = UUID;
                break;
            case 4:
                valueType = STRING;
                break;
            case 5:
                valueType = BOOL;
                break;
            case 6:
                valueType = DATSEQ;
                break;
            case 7:
                valueType = DATALT;
                break;
            case 8:
                valueType = URL;
                break;
            default:
                throw new IllegalArgumentException("Invalid DataElement Type.");
        }
    }

    /** Christian Lorenz: Added this Constructor to parse <code>byte[]</code> into <code>Data</code>. */
    public DataElement(byte[] bytes) { this(bytes, 0);}

    /**
     * Christian Lorenz: Added this to parse <code>DataElement</code> into <code>byte[]</code>.
     * @return
     */
    public byte[] toByteArray() {
        if (vector != null) {
            int byteCount = 0;
            Enumeration elements = vector.elements();
            while (elements.hasMoreElements()) { byteCount += ((DataElement)elements.nextElement()).getByteSize(); }
            //TODO add variable size headers to support larger lists
            byte[] bytes = new byte[byteCount + 2];
            bytes[0] = dataBytes[0];
            bytes[1] = (byte)byteCount;
            byteCount = 2;
            elements = vector.elements();
            while (elements.hasMoreElements()) {
                byte[] elemBytes = ((DataElement)elements.nextElement()).toByteArray();
                if (elemBytes == null) continue;
                System.arraycopy(elemBytes, 0, bytes, byteCount, elemBytes.length);
                byteCount += elemBytes.length;
            }
            dataBytes = bytes;
            vector = null;
        }
        return dataBytes;
    }

    /**
     * Christian Lorenz: Added this for the byte parsing process.
     * @return The number of bytes this element will use in a byte represenation.
     */
    public int getByteSize() {
        if (vector != null) { toByteArray(); }
        if (dataBytes == null) return 0;
        else return dataBytes.length;
    }

    /**
     * Creates a <code>DataElement</code> of type <code>NULL</code>, <code>DATALT</code>, or <code>DATSEQ</code>.
     * @see #NULL
     * @see #DATALT
     * @see #DATSEQ
     * @param  valueType the type of DataElement to create: <code>NULL</code>, <code>DATALT</code>, or <code>DATSEQ</code>
     * @exception IllegalArgumentException if <code>valueType</code>
     * is not <code>NULL</code>, <code>DATALT</code>, or <code>DATSEQ</code>
     */
    public DataElement(int valueType) {
        this.valueType = valueType;
        switch (valueType) {
            case NULL: { //TODO parse null
                    break;
                }
            case DATALT:
            case DATSEQ: {
                    headerByteSize = 2;
                    byte[] bytes = { 0x35, 0x00 };
                    dataBytes = bytes;
                    break;
                }
            default:
                throw new IllegalArgumentException();
        }
    }

    /*  End of the constructor  */

    /**
     * Creates a <code>DataElement</code> whose data type is <code>BOOL</code> and whose value is equal to <code>bool</code>
     * @see #BOOL
     * @param bool the value of the <code>DataElement</code> of type BOOL.
     */
    public DataElement(boolean bool) {
        this.valueType = BOOL;
        this.headerByteSize = 1;
        this.dataBytes = new byte[] { BOOL, (byte) (bool ? 1 : 0) };
    }

    /*  End of the constructor  */

    /**
     * Creates a <code>DataElement</code> that encapsulates an integer
     * value of size <code>U_INT_1</code>, <code>U_INT_2</code>, <code>U_INT_4</code>, <code>INT_1</code>, <code>INT_2</code>,
     * <code>INT_4</code>, and <code>INT_8</code>. The legal values for the <code>valueType</code> and the corresponding
     * attribute values are: <TABLE> <TR><TH>Value Type</TH><TH>Value Range</TH></TR> <TR><TD><code>U_INT_1</code></TD>
     * <TD>[0, 2<sup>8</sup>-1]</TD></TR> <TR><TD><code>U_INT_2</code></TD> <TD>[0, 2<sup>16</sup>-1]</TD></TR>
     * <TR><TD><code>U_INT_4</code></TD> <TD>[0, 2<sup>32</sup>-1]</TD></TR> <TR><TD><code>INT_1</code></TD>
     * <TD>[-2<sup>7</sup>, 2<sup>7</sup>-1]</TD></TR> <TR><TD><code>INT_2</code></TD>
     * <TD>[-2<sup>15</sup>, 2<sup>15</sup>-1]</TD></TR> <TR><TD><code>INT_4</code></TD>
     * <TD>[-2<sup>31</sup>, 2<sup>31</sup>-1]</TD></TR> <TR><TD><code>INT_8</code></TD>
     * <TD>[-2<sup>63</sup>, 2<sup>63</sup>-1]</TD></TR> </TABLE> All other pairings are illegal and will cause an
     * <code>IllegalArgumentException</code> to be thrown.
     * @see #U_INT_1
     * @see #U_INT_2
     * @see #U_INT_4
     * @see #INT_1
     * @see #INT_2
     * @see #INT_4
     * @see #INT_8
     * @param valueType the data type of the object that is being created; must be one of the following: <code>U_INT_1</code>,
     * <code>U_INT_2</code>, <code>U_INT_4</code>, <code>INT_1</code>, <code>INT_2</code>,
     * <code>INT_4</code>, or <code>INT_8</code>
     * @param value the value of the object being created; must be in the range specified for the given <code>valueType</code>
     * @exception IllegalArgumentException if the <code>valueType</code>
     * is not valid or the <code>value</code> for the given legal <code>valueType</code> is outside the valid range
     */
    public DataElement(int valueType, long value) {
        this.valueType = valueType;
        headerByteSize = 1;
        int blen = 0;
        switch (valueType) {
            case U_INT_1: 
            case INT_1: 
					blen = 1;
            			break;
            case U_INT_2: 
            case INT_2: 
					blen = 2;
            			break;
            case U_INT_4: 
            case INT_4: 
					blen = 4;
            			break;
            case INT_8: 
				blen = 8;
        			break;
             default:
                throw new IllegalArgumentException();
        }
        dataBytes = new byte[blen + 1];
        dataBytes[0] = (byte)valueType;
        for (int i = 0;i < blen;i++) {
        		dataBytes[i + 1] = (byte)(0xff & (value >> (8 * (blen - i - 1))));
        }
        //TODO parse long
    }

    /*  End of the constructor  */

    /**
     * Creates a <code>DataElement</code> whose data type is given by
     * <code>valueType</code> and whose value is specified by the argument
     * <code>value</code>. The legal values for the <code>valueType</code> and the corresponding attribute values are: <TABLE>
     * <TR><TH>Value Type</TH><TH>Java Type / Value Range</TH></TR>
     * <TR><TD><code>URL</code></TD><TD><code>java.lang.String</code> </TD></TR> <TR><TD><code>UUID</code></TD>
     * <TD><code>javax.bluetooth.UUID</code></TD></TR> <TR><TD><code>STRING</code></TD>
     * <TD><code>java.lang.String</code></TD></TR> <TR><TD><code>INT_16</code></TD>
     * <TD>[-2<sup>127</sup>, 2<sup>127</sup>-1] as a byte array whose length must be 16</TD></TR>
     * <TR><TD><code>U_INT_8</code></TD> <TD>[0, 2<sup>64</sup>-1] as a byte array whose length must be 8</TD></TR>
     * <TR><TD><code>U_INT_16</code></TD> <TD>[0, 2<sup>128</sup>-1] as a byte array whose length must be 16</TD></TR>
     * </TABLE> All other pairings are illegal and would cause an <code>IllegalArgumentException</code> exception.
     * @see #URL
     * @see #UUID
     * @see #STRING
     * @see #U_INT_8
     * @see #INT_16
     * @see #U_INT_16
     * @param valueType the data type of the object that is being created; must be one of the following: <code>URL</code>,
     * <code>UUID</code>, <code>STRING</code>, <code>INT_16</code>, <code>U_INT_8</code>, or <code>U_INT_16</code>
     * @param value the value for the <code>DataElement</code> being created of type <code>valueType</code>
     * @exception IllegalArgumentException if the <code>value</code>
     * is not of the <code>valueType</code> type or is not in the range specified or is <code>null</code>
     */
    public DataElement(int valueType, Object value) {
        this.valueType = valueType;
        switch (valueType) {
            case UUID: {
                    UUID uuid = (UUID)value;
                    headerByteSize = 1;
                    byte[] uuidBytes = uuid.toByteArray();
                    byte[] bytes = null;
                    if (uuidBytes.length == 2) bytes=new byte[]{0x19, uuidBytes[0], uuidBytes[1]};
                    else if (uuidBytes.length == 4) bytes=new byte[]{0x1a, uuidBytes[0], uuidBytes[1], uuidBytes[2], uuidBytes[3] };
                    else if (uuidBytes.length == 16) {
                      bytes=new byte[17];
                      bytes[0] = 0x1c;
                      for (int i = 0;i < 16;i++) bytes[1 + i] =  uuidBytes[i];
                    }
                    dataBytes = bytes;
                    break;
                }
            case STRING: 
            case URL: {
                    headerByteSize = 2;
                    String string      = (String)value;
                    byte[] stringBytes;
                    try {
						stringBytes = string.getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						stringBytes = string.getBytes();
					}
					
                    int strtype = 0x25;
                    if (stringBytes.length > 0xff) { strtype = 0x26; headerByteSize = 3; }
                    else if (stringBytes.length > 0xffff) { strtype = 0x27; headerByteSize = 5; }

                    byte[] bytes  = new byte[stringBytes.length + (int)headerByteSize];
                    
                    bytes[0] = (byte)(valueType == URL ? URL : strtype);
                    if (headerByteSize == 2) bytes[1] = (byte)stringBytes.length;
                    else if (headerByteSize == 3) {
                    		bytes[1] = (byte)((stringBytes.length >> 8) & 0xff);
                    		bytes[2] = (byte)(stringBytes.length & 0xff);
                    } else if (headerByteSize == 5) {
                			bytes[1] = (byte)((stringBytes.length >> 24) & 0xff);
                			bytes[2] = (byte)((stringBytes.length >> 16) & 0xff);
                			bytes[3] = (byte)((stringBytes.length >> 8) & 0xff);
                			bytes[4] = (byte)(stringBytes.length & 0xff);
                    }
                    System.arraycopy(stringBytes, 0, bytes, (int)headerByteSize, stringBytes.length);
                    dataBytes = bytes;
                    break;
                }
            case U_INT_8: 
            case U_INT_16:
            case INT_8:
            case INT_16:
            		headerByteSize = 1;
            		dataBytes = new byte[(valueType == U_INT_8 || valueType == INT_8) ? 9 : 17];
            		dataBytes[0] = (byte)valueType;
            		System.arraycopy((byte[])value, 0, dataBytes, 1, dataBytes.length - 1);
            		break;
            default:
                throw new IllegalArgumentException();
        }
        //TODO parse object
    }

    /*  End of the constructor  */

    /**
     * Adds a <code>DataElement</code> to this <code>DATALT</code> or <code>DATSEQ</code> <code>DataElement</code> object.
     * The <code>elem</code> will be added at the end of the list. The <code>elem</code> can be of any
     * <code>DataElement</code> type, i.e., <code>URL</code>, <code>NULL</code>, <code>BOOL</code>, <code>UUID</code>,
     * <code>STRING</code>, <code>DATSEQ</code>, <code>DATALT</code>, and the various signed and unsigned integer types.
     * The same object may be added twice. If the object is
     * successfully added the size of the <code>DataElement</code> is increased by one.
     * @param elem the <code>DataElement</code> object to add
     * @exception ClassCastException if the method is invoked on a
     * <code>DataElement</code> whose type is not <code>DATALT</code> or <code>DATSEQ</code>
     * @exception NullPointerException if <code>elem</code> is <code>null</code>
     */
    public void addElement(DataElement elem) {
        if ((valueType != DATALT) && (valueType != DATSEQ)) throw new ClassCastException();
        if (vector == null) { vector = this.getVector(); }
        vector.addElement(elem);
    }

    /*  End of the method addElement    */

    /**
     * Inserts a <code>DataElement</code> at the specified location.
     * This method can be invoked only on a <code>DATALT</code> or <code>DATSEQ</code> <code>DataElement</code>.
     * <code>elem</code> can be of any <code>DataElement</code> type, i.e., <code>URL</code>,  <code>NULL</code>,
     * <code>BOOL</code>, <code>UUID</code>, <code>STRING</code>, <code>DATSEQ</code>,
     * <code>DATALT</code>, and the various signed and unsigned integers. The same object may be added twice. If the object is
     * successfully added the size will be increased by one. Each element with an index greater than or equal to the specified
     * index is shifted upward to have an index one greater than the value it had previously. <P>
     * The <code>index</code> must be greater than or equal to 0 and less than or equal to the current size.  Therefore,
     * <code>DATALT</code> and <code>DATSEQ</code> are zero-based objects.
     * @param elem the <code>DataElement</code> object to add
     * @param index the location at which to add the <code>DataElement</code>
     * @throws ClassCastException if the method is invoked on an instance of <code>DataElement</code> whose type is not
     * <code>DATALT</code> or <code>DATSEQ</code>
     * @throws IndexOutOfBoundsException if <code>index</code> is negative or greater than
     * the size of the <code>DATALT</code> or <code>DATSEQ</code>
     * @throws NullPointerException if <code>elem</code> is <code>null</code>
     */
    public void insertElementAt(DataElement elem, int index) {
        if ((valueType != DATALT) && (valueType != DATSEQ)) throw new ClassCastException();
        if (vector == null) { vector = (Vector)this.getVector(); }
        vector.insertElementAt(elem, index);
    }

    /*  End of the method insertElementAt   */

    /**
     * Returns the number of <code>DataElements</code> that are present
     * in this <code>DATALT</code> or <code>DATSEQ</code> object. It is possible that the number of elements is equal to zero.
     * @return the number of elements in this <code>DATALT</code> or <code>DATSEQ</code>
     * @throws ClassCastException if this object is not of type <code>DATALT</code> or <code>DATSEQ</code>
     */
    public int getSize() {
        if ((valueType != DATALT) && (valueType != DATSEQ)) throw new ClassCastException();
        if (vector == null) { vector = this.getVector(); }
        return vector.size();
    }

    /*  End of the method getSize   */

    /**
     * Removes the first occurrence of the <code>DataElement</code>
     * from this object.  <code>elem</code> may be of any type, i.e., <code>URL</code>,  <code>NULL</code>, <code>BOOL</code>,
     * <code>UUID</code>,  <code>STRING</code>, <code>DATSEQ</code>,
     * <code>DATALT</code>, or the variously sized signed and unsigned integers.
     * Only the first object in the list that is equal to <code>elem</code> will be removed. Other objects, if present,
     * are not removed.  Since this class doesn?t override the <code>equals()</code> method of the <code>Object</code> class,
     * the remove method compares only the references of objects. If <code>elem</code> is
     * successfully removed the size of this <code>DataElement</code>
     * is decreased by one.  Each <code>DataElement</code> in the
     * <code>DATALT</code> or <code>DATSEQ</code> with an index greater
     * than the index of <code>elem</code> is shifted downward to have an index one smaller than the value it had previously.
     * @param elem the <code>DataElement</code> to be removed
     * @return <code>true</code> if the input value was found and removed; else <code>false</code>
     * @throws ClassCastException if this object is not of type <code>DATALT</code> or <code>DATSEQ</code>
     * @throws NullPointerException if <code>elem</code> is <code>null</code>
     */
    public boolean removeElement(DataElement elem) {
        if ((valueType != DATALT) && (valueType != DATSEQ)) throw new ClassCastException();
        if (vector == null) { vector = (Vector)this.getVector(); }
        return vector.removeElement(elem);
    }

    /*  End of the method removeElement */

    /**
     * Returns the data type of the object this <code>DataElement</code> represents.
     * @return the data type of this <code>DataElement<code> object; the legal return values are: <code>URL</code>,
     * <code>NULL</code>, <code>BOOL</code>, <code>UUID</code>, <code>STRING</code>, <code>DATSEQ</code>, <code>DATALT</code>,
     * <code>U_INT_1</code>, <code>U_INT_2</code>, <code>U_INT_4</code>, <code>U_INT_8</code>, <code>U_INT_16</code>,
     * <code>INT_1</code>, <code>INT_2</code>, <code>INT_4</code>, <code>INT_8</code>, or <code>INT_16</code>
     */
    public int getDataType() { return valueType; }

    /*  End of the method getDataType   */

    /**
     * Returns the value of the <code>DataElement</code> if it can be
     * represented as a <code>long</code>. The data type of the object must be <code>U_INT_1</code>, <code>U_INT_2</code>,
     * <code>U_INT_4</code>, <code>INT_1</code>, <code>INT_2</code>, <code>INT_4</code>, or <code>INT_8</code>.
     * @return the value of the <code>DataElement</code> as a <code>long</code>
     * @throws ClassCastException if the data type of the object is not <code>U_INT_1</code>, <code>U_INT_2</code>,
     * <code>U_INT_4</code>, <code>INT_1</code>, <code>INT_2</code>, <code>INT_4</code>, or <code>INT_8</code>
     */
    public long getLong() {
        int blen = 0;
        switch (valueType) {
            case U_INT_1: 
            case INT_1: 
					blen = 1;
            			break;
            case U_INT_2: 
            case INT_2: 
					blen = 2;
            			break;
            case U_INT_4: 
            case INT_4: 
					blen = 4;
            			break;
            case U_INT_8: 
            case INT_8: 
					blen = 8;
            			break;
            case U_INT_16: 
            case INT_16: 
					blen = 16;
            			break;
            	default:
            		throw new IllegalArgumentException();
        }
        long v = (valueType >= 0x10 && ((dataBytes[blen] & (byte)0x80) == (byte)0x80)) ? -1 : 0;
        for (int i = 0;i < blen;i++) {
        		v = (long)(v << 8);
        		v |= (long)(0xff & dataBytes[i + 1]);
        }
        return v;
    }

    /*  End of the method getLong   */

    /**
     * Returns the value of the <code>DataElement</code> if it is represented as a <code>boolean</code>.
     * @return the <code>boolean</code> value of this <code>DataElement</code> object
     * @throws ClassCastException if the data type of this object is not of type <code>BOOL</code>
     */
    public boolean getBoolean() {
        if (valueType != BOOL) throw new ClassCastException();
        return (dataBytes[1] != 0);
    }

    /*  End of the method getBoolean    */

    /**
     * Returns the value of this <code>DataElement</code> as an <code>Object</code>. This method returns the appropriate Java
     * object for the following data types: <code>URL</code>, <code>UUID</code>, <code>STRING</code>, <code>DATSEQ</code>,
     * <code>DATALT</code>, <code>U_INT_8</code>, <code>U_INT_16</code>, and <code>INT_16</code>.
     * Modifying the returned <code>Object</code> will not change this <code>DataElement</code>.
     * The following are the legal pairs of data type and Java object type being returned. <TABLE>
     * <TR><TH><code>DataElement</code> Data Type</code></TH> <TH>Java Data Type</TH></TR>
     * <TR><TD><code>URL</code></TD><TD><code>java.lang.String</code> </TD></TR> <TR><TD><code>UUID</code></TD>
     * <TD><code>javax.bluetooth.UUID</code></TD></TR> <TR><TD><code>STRING</code></TD><TD><code>java.lang.String
     * </code></TD></TR> <TR><TD><code>DATSEQ</code></TD> <TD><code>java.util.Enumeration</code></TD></TR>
     * <TR><TD><code>DATALT</code></TD> <TD><code>java.util.Enumeration</code></TD></TR> <TR><TD><code>U_INT_8</code></TD>
     * <TD>byte[] of length 8</TD></TR> <TR><TD><code>U_INT_16</code></TD> <TD>byte[] of length 16</TD></TR>
     * <TR><TD><code>INT_16</code></TD> <TD>byte[] of length 16</TD></TR> </TABLE>
     * @return the value of this object
     * @throws ClassCastException if the object is not a <code>URL</code>, <code>UUID</code>,
     * <code>STRING</code>, <code>DATSEQ</code>, <code>DATALT</code>, <code>U_INT_8</code>,
     * <code>U_INT_16</code>, or <code>INT_16</code>
     */
    public Object getValue() {
        switch (valueType) {
        		/*case U_INT_1: 
        		case INT_1: 
        		case U_INT_2: 
        		case INT_2: 
        		case U_INT_4: 
        		case INT_4: 
        			return new Long (getLong());*/
        		case INT_8:
            case U_INT_8: {
                    byte[] valueObject = new byte[8];
                    System.arraycopy(dataBytes, (int)headerByteSize, valueObject, 0, 8);
                    return valueObject;
                }
            case INT_16:
            case U_INT_16: {
                    byte[] valueObject = new byte[16];
                    System.arraycopy(dataBytes, (int)headerByteSize, valueObject, 0, 16);
                    return valueObject;
                }
            case UUID: {
                    byte[] uuidBytes = new byte[(int)(dataBytes.length - headerByteSize)];
                    System.arraycopy(dataBytes, (int)(headerByteSize), uuidBytes, 0, uuidBytes.length);
                    UUID uuid = new UUID(uuidBytes);
                    return uuid;
                }
            case URL:
            case 0x20:
            case 0x25:
            case 0x26:
            case 0x27: {
            	// If the String is NULL-Terminated, then ignore the 0-Element
            	       if (dataBytes.length == 0) return "";
            		   int clen = (int)(dataBytes.length - headerByteSize) - (dataBytes[dataBytes.length - 1] == 0 ? 1 : 0);
            		   if (clen <= 0) return "";
            		   byte[] valueObject = new byte[clen];
                    System.arraycopy(dataBytes, (int)(headerByteSize), valueObject, 0, valueObject.length);
                    try {
						return new String(valueObject, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						return new String (valueObject);
					}
                }
            case DATSEQ:
            case DATALT: {
                    if (vector == null) {
                        vector = new Vector();
                        int byteCount = 0;
                        while (byteCount < dataBytes.length - headerByteSize) {
                            DataElement newElement = new DataElement(dataBytes, (int)headerByteSize + byteCount);
                            byteCount += newElement.getByteSize();
                            vector.addElement(newElement);
                        }
                    }
                    Enumeration elemList = vector.elements();
                    return elemList;
                }
            default:
                throw new ClassCastException("Unhandled type 0x" + Integer.toHexString(valueType));
        }
    }

    /*  End of the method getValue  */

    protected Vector getVector() {
        if ((valueType != DATALT) && (valueType != DATSEQ)) throw new ClassCastException();
        if (vector == null) {
            vector = new Vector();
            int byteCount = 0;
            while (byteCount < dataBytes.length - headerByteSize) {
                DataElement newElement = new DataElement(dataBytes, (int)headerByteSize + byteCount);
                byteCount += newElement.getByteSize();
                vector.addElement(newElement);
            }
        }
        return vector;
    }

    /** Christian Lorenz: added this... */
    public String toString() {
        switch (valueType) {
            case NULL:
                return "NULL";
            case U_INT_1:
                return "U_INT_1=" + getLong();
            case U_INT_2:
                return "U_INT_2=" + getLong();
            case U_INT_4:
                return "U_INT_4=" + getLong();
            case U_INT_8:
                byte[] uint8 = (byte[]) getValue();
                return "U_INT_8=" + Debug.printByteArray(uint8);
            case U_INT_16:
                byte[] unit16 = (byte[]) getValue();
                return "U_INT_16=" + Debug.printByteArray(unit16);
            case INT_1:
                return "INT_1=" + getLong();
            case INT_2:
                return "INT_2=" + getLong();
            case INT_4:
                return "INT_4=" + getLong();
            case INT_8:
                return "INT_8=" + getLong();
            case INT_16:
                byte[] int16 = (byte[]) getValue();
                return "INT_16=" + Debug.printByteArray(int16);
            case UUID:
                return "UUID=" + ((UUID)getValue());
            case STRING:
                return "STRING=" + ((String)getValue());
            case URL:
                return "URL=" + ((String)getValue());
            case BOOL:
                return "BOOL=" + getBoolean();
            case DATSEQ: {
                    String result = "DATSEQ={";
                    Enumeration elements = (Enumeration)getValue();
                    while (elements.hasMoreElements()) {
                        DataElement element = (DataElement)elements.nextElement();
                        result += " " + element.toString();
                    }
                    result += "}";
                    return result;
                }
            case DATALT: {
                    String result = " DATALT={";
                    Enumeration elements = (Enumeration)getValue();
                    while (elements.hasMoreElements()) {
                        DataElement element = (DataElement)elements.nextElement();
                        result += " " + element.toString();
                    }
                    result += "}";
                    return result;
                }
            default:
                return " UNKNOWN ELEMENT";
        }
    }

}
