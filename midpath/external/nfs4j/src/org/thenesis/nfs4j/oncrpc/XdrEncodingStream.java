/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrEncodingStream.java,v 1.2 2003/08/14 13:48:33 haraldalbrecht Exp $
 *
 * NFS4J - Copyright (C) 2007 Guillaume Legris, Mathieu Legris
 *
 * Copyright (c) 1999, 2000
 * Lehrstuhl fuer Prozessleittechnik (PLT), RWTH Aachen
 * D-52064 Aachen, Germany.
 * All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package org.thenesis.nfs4j.oncrpc;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Defines the abstract base class for all encoding XDR streams. An encoding
 * XDR stream receives data in the form of Java data types and writes it to
 * a data sink (for instance, network or memory buffer) in the
 * platform-independent XDR format.
 *
 * <p>Derived classes need to implement the {@link #xdrEncodeInt(int)},
 * {@link #xdrEncodeOpaque(byte[])} and
 * {@link #xdrEncodeOpaque(byte[], int, int)} methods to make this complete
 * mess workable.
 *
 * @version $Revision: 1.2 $ $Date: 2003/08/14 13:48:33 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class XdrEncodingStream {

    /**
     * Begins encoding a new XDR record. This typically involves resetting this
     * encoding XDR stream back into a known state.
     *
     * @param receiverAddress Indicates the receiver of the XDR data. This can
     *   be <code>null</code> for XDR streams connected permanently to a
     *   receiver (like in case of TCP/IP based XDR streams).
     * @param receiverPort Port number of the receiver.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void beginEncoding(InetAddress receiverAddress, int receiverPort)
           throws OncRpcException, IOException {
    }

    /**
     * Flushes this encoding XDR stream and forces any buffered output bytes
     * to be written out. The general contract of <code>endEncoding</code> is that
     * calling it is an indication that the current record is finished and any
     * bytes previously encoded should immediately be written to their intended
     * destination.
     *
     * <p>The <code>endEncoding</code> method of <code>XdrEncodingStream</code>
     * does nothing.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void endEncoding()
           throws OncRpcException, IOException {
    }

    /**
     * Closes this encoding XDR stream and releases any system resources
     * associated with this stream. The general contract of <code>close</code>
     * is that it closes the encoding XDR stream. A closed XDR stream cannot
     * perform encoding operations and cannot be reopened.
     *
     * <p>The <code>close</code> method of <code>XdrEncodingStream</code>
     * does nothing.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void close()
           throws OncRpcException, IOException {
    }

    /**
     * Encodes (aka "serializes") a "XDR int" value and writes it down a
     * XDR stream. A XDR int is 32 bits wide -- the same width Java's "int"
     * data type has. This method is one of the basic methods all other
     * methods can rely on. Because it's so basic, derived classes have to
     * implement it.
     *
     * @param value The int value to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrEncodeInt(int value)
        throws OncRpcException, IOException;

    /**
     * Encodes (aka "serializes") a XDR opaque value, which is represented
     * by a vector of byte values, and starts at <code>offset</code> with a
     * length of <code>length</code>. Only the opaque value is encoded, but
     * no length indication is preceeding the opaque value, so the receiver
     * has to know how long the opaque value will be. The encoded data is
     * always padded to be a multiple of four. If the given length is not a
     * multiple of four, zero bytes will be used for padding.
     *
     * <p>Derived classes must ensure that the proper semantic is maintained.
     *
     * @param value The opaque value to be encoded in the form of a series of
     *   bytes.
     * @param offset Start offset in the data.
     * @param length the number of bytes to encode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrEncodeOpaque(byte [] value, int offset, int length)
           throws OncRpcException, IOException;

    /**
     * Encodes (aka "serializes") a XDR opaque value, which is represented
     * by a vector of byte values. The length of the opaque value is written
     * to the XDR stream, so the receiver does not need to know
     * the exact length in advance. The encoded data is always padded to be
     * a multiple of four to maintain XDR alignment.
     *
     * @param value The opaque value to be encoded in the form of a series of
     *   bytes.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeDynamicOpaque(byte [] value)
           throws OncRpcException, IOException {
        xdrEncodeInt(value.length);
        xdrEncodeOpaque(value);
    }

    /**
     * Encodes (aka "serializes") a XDR opaque value, which is represented
     * by a vector of byte values. Only the opaque value is encoded, but
     * no length indication is preceeding the opaque value, so the receiver
     * has to know how long the opaque value will be. The encoded data is
     * always padded to be a multiple of four. If the length of the given byte
     * vector is not a multiple of four, zero bytes will be used for padding.
     *
     * @param value The opaque value to be encoded in the form of a series of
     *   bytes.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeOpaque(byte [] value)
           throws OncRpcException, IOException {
        xdrEncodeOpaque(value, 0, value.length);
    }

    /**
     * Encodes (aka "serializes") a XDR opaque value, which is represented
     * by a vector of byte values. Only the opaque value is encoded, but
     * no length indication is preceeding the opaque value, so the receiver
     * has to know how long the opaque value will be. The encoded data is
     * always padded to be a multiple of four. If the length of the given byte
     * vector is not a multiple of four, zero bytes will be used for padding.
     *
     * @param value The opaque value to be encoded in the form of a series of
     *   bytes.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeOpaque(byte [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        xdrEncodeOpaque(value, 0, value.length);
    }

    /**
     * Encodes (aka "serializes") a vector of bytes, which is nothing more
     * than a series of octets (or 8 bits wide bytes), each packed into its
     * very own 4 bytes (XDR int). Byte vectors are encoded together with a
     * preceeding length value. This way the receiver doesn't need to know
     * the length of the vector in advance.
     *
     * @param value Byte vector to encode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeByteVector(byte [] value)
           throws OncRpcException, IOException {
        int length = value.length; // well, silly optimizations appear here...
        xdrEncodeInt(length);
        if ( length != 0 ) {
            //
            // For speed reasons, we do sign extension here, but the higher bits
            // will be removed again when deserializing.
            //
            for ( int i = 0; i < length; ++i ) {
                xdrEncodeInt((int) value[i]);
            }
        }
    }

    /**
     * Encodes (aka "serializes") a vector of bytes, which is nothing more
     * than a series of octets (or 8 bits wide bytes), each packed into its
     * very own 4 bytes (XDR int).
     *
     * @param value Byte vector to encode.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeByteFixedVector(byte [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        if ( length != 0 ) {
            //
            // For speed reasons, we do sign extension here, but the higher bits
            // will be removed again when deserializing.
            //
            for ( int i = 0; i < length; ++i ) {
                xdrEncodeInt((int) value[i]);
            }
        }
    }

    /**
     * Encodes (aka "serializes") a byte and write it down this XDR stream.
     *
     * @param value Byte value to encode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeByte(byte value)
           throws OncRpcException, IOException {
        //
        // For speed reasons, we do sign extension here, but the higher bits
        // will be removed again when deserializing.
        //
        xdrEncodeInt((int) value);
    }

    /**
     * Encodes (aka "serializes") a short (which is a 16 bits wide quantity)
     * and write it down this XDR stream.
     *
     * @param value Short value to encode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeShort(short value)
           throws OncRpcException, IOException {
        xdrEncodeInt((int) value);
    }

    /**
     * Encodes (aka "serializes") a long (which is called a "hyper" in XDR
     * babble and is 64&nbsp;bits wide) and write it down this XDR stream.
     *
     * @param value Long value to encode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeLong(long value)
           throws OncRpcException, IOException {
        //
        // Just encode the long (which is called a "hyper" in XDR babble) as
        // two ints in network order, that is: big endian with the high int
        // comming first.
        //
        xdrEncodeInt((int)(value >>> 32));
        xdrEncodeInt((int)(value & 0xFFFFFFFF));
    }

    /**
     * Encodes (aka "serializes") a float (which is a 32 bits wide floating
     * point quantity) and write it down this XDR stream.
     *
     * @param value Float value to encode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeFloat(float value)
           throws OncRpcException, IOException {
        xdrEncodeInt(Float.floatToIntBits(value));
    }

    /**
     * Encodes (aka "serializes") a double (which is a 64 bits wide floating
     * point quantity) and write it down this XDR stream.
     *
     * @param value Double value to encode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeDouble(double value)
           throws OncRpcException, IOException {
        xdrEncodeLong(Double.doubleToLongBits(value));
    }

    /**
     * Encodes (aka "serializes") a boolean and writes it down this XDR stream.
     *
     * @param value Boolean value to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeBoolean(boolean value)
           throws OncRpcException, IOException {
        xdrEncodeInt(value ? 1 : 0);
    }

    /**
     * Encodes (aka "serializes") a string and writes it down this XDR stream.
     *
     * @param value String value to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeString(String value)
           throws OncRpcException, IOException {
        if ( characterEncoding != null ) {
			xdrEncodeDynamicOpaque(value.getBytes(characterEncoding));
        } else { 
			xdrEncodeDynamicOpaque(value.getBytes());
        }
    }

    /**
     * Encodes (aka "serializes") a vector of short integers and writes it down
     * this XDR stream.
     *
     * @param value short vector to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeShortVector(short [] value)
           throws OncRpcException, IOException {
        int size = value.length;
        xdrEncodeInt(size);
        for ( int i = 0; i < size; i++ ) {
            xdrEncodeShort(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of short integers and writes it down
     * this XDR stream.
     *
     * @param value short vector to be encoded.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeShortFixedVector(short [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        for ( int i = 0; i < length; i++ ) {
            xdrEncodeShort(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of ints and writes it down
     * this XDR stream.
     *
     * @param value int vector to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeIntVector(int [] value)
           throws OncRpcException, IOException {
        int size = value.length;
        xdrEncodeInt(size);
        for ( int i = 0; i < size; i++ ) {
            xdrEncodeInt(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of ints and writes it down
     * this XDR stream.
     *
     * @param value int vector to be encoded.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeIntFixedVector(int [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        for ( int i = 0; i < length; i++ ) {
            xdrEncodeInt(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of long integers and writes it down
     * this XDR stream.
     *
     * @param value long vector to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeLongVector(long [] value)
           throws OncRpcException, IOException {
        int size = value.length;
        xdrEncodeInt(size);
        for ( int i = 0; i < size; i++ ) {
            xdrEncodeLong(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of long integers and writes it down
     * this XDR stream.
     *
     * @param value long vector to be encoded.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeLongFixedVector(long [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        for ( int i = 0; i < length; i++ ) {
            xdrEncodeLong(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of floats and writes it down
     * this XDR stream.
     *
     * @param value float vector to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeFloatVector(float [] value)
           throws OncRpcException, IOException {
        int size = value.length;
        xdrEncodeInt(size);
        for ( int i = 0; i < size; i++ ) {
            xdrEncodeFloat(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of floats and writes it down
     * this XDR stream.
     *
     * @param value float vector to be encoded.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeFloatFixedVector(float [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        for ( int i = 0; i < length; i++ ) {
            xdrEncodeFloat(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of doubles and writes it down
     * this XDR stream.
     *
     * @param value double vector to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeDoubleVector(double [] value)
           throws OncRpcException, IOException {
        int size = value.length;
        xdrEncodeInt(size);
        for ( int i = 0; i < size; i++ ) {
            xdrEncodeDouble(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of doubles and writes it down
     * this XDR stream.
     *
     * @param value double vector to be encoded.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeDoubleFixedVector(double [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        for ( int i = 0; i < length; i++ ) {
            xdrEncodeDouble(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of booleans and writes it down
     * this XDR stream.
     *
     * @param value long vector to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeBooleanVector(boolean [] value)
           throws OncRpcException, IOException {
        int size = value.length;
        xdrEncodeInt(size);
        for ( int i = 0; i < size; i++ ) {
            xdrEncodeBoolean(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of booleans and writes it down
     * this XDR stream.
     *
     * @param value long vector to be encoded.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeBooleanFixedVector(boolean [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        for ( int i = 0; i < length; i++ ) {
            xdrEncodeBoolean(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of strings and writes it down
     * this XDR stream.
     *
     * @param value String vector to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrEncodeStringVector(String [] value)
           throws OncRpcException, IOException {
        int size = value.length;
        xdrEncodeInt(size);
        for ( int i = 0; i < size; i++ ) {
            xdrEncodeString(value[i]);
        }
    }

    /**
     * Encodes (aka "serializes") a vector of strings and writes it down
     * this XDR stream.
     *
     * @param value String vector to be encoded.
     * @param length of vector to write. This parameter is used as a sanity
     *   check.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IllegalArgumentException if the length of the vector does not
     *   match the specified length.
     */
    public final void xdrEncodeStringFixedVector(String [] value, int length)
           throws OncRpcException, IOException {
        if ( value.length != length ) {
            throw(new IllegalArgumentException("array size does not match protocol specification"));
        }
        for ( int i = 0; i < length; i++ ) {
            xdrEncodeString(value[i]);
        }
    }

	/**
	 * Set the character encoding for serializing strings.
	 *
	 * @param characterEncoding the encoding to use for serializing strings.
	 *   If <code>null</code>, the system's default encoding is to be used.
	 */
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	/**
	 * Get the character encoding for serializing strings.
	 *
	 * @return the encoding currently used for serializing strings.
	 *   If <code>null</code>, then the system's default encoding is used.
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/**
	 * Encoding to use when serializing strings or <code>null</code> if
	 * the system's default encoding should be used.
	 */
	private String characterEncoding = null;

}

// End of XdrEncodingStream.java
