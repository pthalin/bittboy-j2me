/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrDecodingStream.java,v 1.3 2003/08/14 13:48:33 haraldalbrecht Exp $
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
 * Defines the abstract base class for all decoding XDR streams. A decoding
 * XDR stream returns data in the form of Java data types which it reads
 * from a data source (for instance, network or memory buffer) in the
 * platform-independent XDR format.
 *
 * <p>Derived classes need to implement the {@link #xdrDecodeInt()},
 * {@link #xdrDecodeOpaque(int)} and
 * {@link #xdrDecodeOpaque(byte[], int, int)} methods to make this complete
 * mess workable.
 *
 * @version $Revision: 1.3 $ $Date: 2003/08/14 13:48:33 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class XdrDecodingStream {

     /**
     * Returns the Internet address of the sender of the current XDR data.
     * This method should only be called after {@link #beginDecoding}, otherwise
     * it might return stale information.
     *
     * @return InetAddress of the sender of the current XDR data.
     */
    public abstract InetAddress getSenderAddress();

    /**
     * Returns the port number of the sender of the current XDR data.
     * This method should only be called after {@link #beginDecoding}, otherwise
     * it might return stale information.
     *
     * @return Port number of the sender of the current XDR data.
     */
    public abstract int getSenderPort();

   /**
     * Initiates decoding of the next XDR record. This typically involves
     * filling the internal buffer with the next datagram from the network, or
     * reading the next chunk of data from a stream-oriented connection. In
     * case of memory-based communication this might involve waiting for
     * some other process to fill the buffer and signal availability of new
     * XDR data.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void beginDecoding()
        throws OncRpcException, IOException;

    /**
     * End decoding of the current XDR record. The general contract of
     * <code>endDecoding</code> is that calling it is an indication that
     * the current record is no more interesting to the caller and any
     * allocated data for this record can be freed.
     *
     * <p>The <code>endDecoding</code> method of <code>XdrDecodingStream</code>
     * does nothing.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void endDecoding()
           throws OncRpcException, IOException {
    }

    /**
     * Closes this decoding XDR stream and releases any system resources
     * associated with this stream. The general contract of <code>close</code>
     * is that it closes the decoding XDR stream. A closed XDR stream cannot
     * perform decoding operations and cannot be reopened.
     *
     * <p>The <code>close</code> method of <code>XdrDecodingStream</code>
     * does nothing.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void close()
           throws OncRpcException, IOException {
    }

    /**
     * Decodes (aka "deserializes") a "XDR int" value received from a
     * XDR stream. A XDR int is 32 bits wide -- the same width Java's "int"
     * data type has. This method is one of the basic methods all other
     * methods can rely on. Because it's so basic, derived classes have to
     * implement it.
     *
     * @return The decoded int value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract int xdrDecodeInt()
           throws OncRpcException, IOException;

    /**
     * Decodes (aka "deserializes") an opaque value, which is nothing more
     * than a series of octets (or 8 bits wide bytes). Because the length
     * of the opaque value is given, we don't need to retrieve it from the
     * XDR stream.
     *
     * <p>Note that this is a basic abstract method, which needs to be
     * implemented in derived classes.
     *
     * @param length Length of opaque data to decode.
     *
     * @return Opaque data as a byte vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract byte [] xdrDecodeOpaque(int length)
        throws OncRpcException, IOException;

    /**
     * Decodes (aka "deserializes") a XDR opaque value, which is represented
     * by a vector of byte values, and starts at <code>offset</code> with a
     * length of <code>length</code>. Only the opaque value is decoded, so the
     * caller has to know how long the opaque value will be. The decoded data
     * is always padded to be a multiple of four (because that's what the
     * sender does).
     *
     * <p>Derived classes must ensure that the proper semantic is maintained.
     *
     * @param opaque Byte vector which will receive the decoded opaque value.
     * @param offset Start offset in the byte vector.
     * @param length the number of bytes to decode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     * @throws IndexOutOfBoundsException if the given <code>opaque</code>
     *   byte vector isn't large enough to receive the result.
     */
    public abstract void xdrDecodeOpaque(byte [] opaque, int offset, int length)
           throws OncRpcException, IOException;

    /**
     * Decodes (aka "deserializes") a XDR opaque value, which is represented
     * by a vector of byte values. Only the opaque value is decoded, so the
     * caller has to know how long the opaque value will be. The decoded data
     * is always padded to be a multiple of four (because that's what the
     * sender does).
     *
     * @param opaque Byte vector which will receive the decoded opaque value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final void xdrDecodeOpaque(byte [] opaque)
           throws OncRpcException, IOException {
        xdrDecodeOpaque(opaque, 0, opaque.length);
    }

    /**
     * Decodes (aka "deserializes") a XDR opaque value, which is represented
     * by a vector of byte values. The length of the opaque value to decode
     * is pulled off of the XDR stream, so the caller does not need to know
     * the exact length in advance. The decoded data is always padded to be
     * a multiple of four (because that's what the sender does).
     *
     * @return The byte vector containing the decoded data.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final byte [] xdrDecodeDynamicOpaque()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        byte [] opaque = new byte[length];
        if ( length != 0 ) {
            xdrDecodeOpaque(opaque);
        }
        return opaque;
    }

    /**
     * Decodes (aka "deserializes") a vector of bytes, which is nothing more
     * than a series of octets (or 8 bits wide bytes), each packed into its
     * very own 4 bytes (XDR int). Byte vectors are decoded together with a
     * preceeding length value. This way the receiver doesn't need to know
     * the length of the vector in advance.
     *
     * @return The byte vector containing the decoded data.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final byte [] xdrDecodeByteVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        if ( length > 0 ) {
            byte [] bytes = new byte[length];
            for ( int i = 0; i < length; ++i ) {
                bytes[i] = (byte) xdrDecodeInt();
            }
            return bytes;
        } else {
            return new byte[0];
        }
    }

    /**
     * Decodes (aka "deserializes") a vector of bytes, which is nothing more
     * than a series of octets (or 8 bits wide bytes), each packed into its
     * very own 4 bytes (XDR int).
     *
     * @param length of vector to read.
     *
     * @return The byte vector containing the decoded data.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final byte [] xdrDecodeByteFixedVector(int length)
           throws OncRpcException, IOException {
        if ( length > 0 ) {
            byte [] bytes = new byte[length];
            for ( int i = 0; i < length; ++i ) {
                bytes[i] = (byte) xdrDecodeInt();
            }
            return bytes;
        } else {
            return new byte[0];
        }
    }

    /**
     * Decodes (aka "deserializes") a byte read from this XDR stream.
     *
     * @return Decoded byte value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final byte xdrDecodeByte()
           throws OncRpcException, IOException {
        return (byte) xdrDecodeInt();
    }

    /**
     * Decodes (aka "deserializes") a short (which is a 16 bit quantity)
     * read from this XDR stream.
     *
     * @return Decoded short value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final short xdrDecodeShort()
           throws OncRpcException, IOException {
        return (short) xdrDecodeInt();
    }

    /**
     * Decodes (aka "deserializes") a long (which is called a "hyper" in XDR
     * babble and is 64&nbsp;bits wide) read from a XDR stream.
     *
     * @return Decoded long value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final long xdrDecodeLong()
           throws OncRpcException, IOException {
        //
        // Similiar to xdrEncodeLong: just read in two ints in network order.
        //
        return (((long) xdrDecodeInt()) << 32) +
                 (((long) xdrDecodeInt()) & 0x00000000FFFFFFFFl);
    }

    /**
     * Decodes (aka "deserializes") a float (which is a 32 bits wide floating
     * point entity) read from a XDR stream.
     *
     * @return Decoded float value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final float xdrDecodeFloat()
           throws OncRpcException, IOException {
        return Float.intBitsToFloat(xdrDecodeInt());
    }

    /**
     * Decodes (aka "deserializes") a double (which is a 64 bits wide floating
     * point entity) read from a XDR stream.
     *
     * @return Decoded double value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final double xdrDecodeDouble()
           throws OncRpcException, IOException {
        return Double.longBitsToDouble(xdrDecodeLong());
    }

    /**
     * Decodes (aka "deserializes") a boolean read from a XDR stream.
     *
     * @return Decoded boolean value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
     public final boolean xdrDecodeBoolean()
           throws OncRpcException, IOException {
        return xdrDecodeInt() != 0 ? true : false;
    }

    /**
     * Decodes (aka "deserializes") a string read from a XDR stream.
     * If a character encoding has been set for this stream, then this
     * will be used for conversion.
     *
     * @return Decoded String value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final String xdrDecodeString()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        if ( length > 0 ) {
            byte [] bytes = new byte[length];
            xdrDecodeOpaque(bytes, 0, length);
            return (characterEncoding != null) ?
            	     new String(bytes, characterEncoding) :
            	     new String(bytes);
        } else {
            return new String();
        }
    }

    /**
     * Decodes (aka "deserializes") a vector of short integers read from a
     * XDR stream.
     *
     * @return Decoded vector of short integers.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final short [] xdrDecodeShortVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        short [] value = new short[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeShort();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of short integers read from a
     * XDR stream.
     *
     * @param length of vector to read.
     *
     * @return Decoded vector of short integers.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final short [] xdrDecodeShortFixedVector(int length)
           throws OncRpcException, IOException {
        short [] value = new short[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeShort();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of ints read from a XDR stream.
     *
     * @return Decoded int vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final int [] xdrDecodeIntVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        int [] value = new int[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeInt();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of ints read from a XDR stream.
     *
     * @param length of vector to read.
     *
     * @return Decoded int vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final int [] xdrDecodeIntFixedVector(int length)
           throws OncRpcException, IOException {
        int [] value = new int[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeInt();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of longs read from a XDR stream.
     *
     * @return Decoded long vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final long [] xdrDecodeLongVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        long [] value = new long[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeLong();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of longs read from a XDR stream.
     *
     * @param length of vector to read.
     *
     * @return Decoded long vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final long [] xdrDecodeLongFixedVector(int length)
           throws OncRpcException, IOException {
        long [] value = new long[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeLong();
        }
        return value;
    }


    /**
     * Decodes (aka "deserializes") a vector of floats read from a XDR stream.
     *
     * @return Decoded float vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final float [] xdrDecodeFloatVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        float [] value = new float[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeFloat();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of floats read from a XDR stream.
     *
     * @param length of vector to read.
     *
     * @return Decoded float vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final float [] xdrDecodeFloatFixedVector(int length)
           throws OncRpcException, IOException {
        float [] value = new float[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeFloat();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of doubles read from a XDR stream.
     *
     * @return Decoded double vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final double [] xdrDecodeDoubleVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        double [] value = new double[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeDouble();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of doubles read from a XDR stream.
     *
     * @param length of vector to read.
     *
     * @return Decoded double vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final double [] xdrDecodeDoubleFixedVector(int length)
           throws OncRpcException, IOException {
        double [] value = new double[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeDouble();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of booleans read from a XDR stream.
     *
     * @return Decoded boolean vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final boolean [] xdrDecodeBooleanVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        boolean [] value = new boolean[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeBoolean();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of booleans read from a XDR stream.
     *
     * @param length of vector to read.
     *
     * @return Decoded boolean vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final boolean [] xdrDecodeBooleanFixedVector(int length)
           throws OncRpcException, IOException {
        boolean [] value = new boolean[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeBoolean();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of strings read from a XDR stream.
     *
     * @return Decoded String vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final String [] xdrDecodeStringVector()
           throws OncRpcException, IOException {
        int length = xdrDecodeInt();
        String [] value = new String[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeString();
        }
        return value;
    }

    /**
     * Decodes (aka "deserializes") a vector of strings read from a XDR stream.
     *
     * @param length of vector to read.
     *
     * @return Decoded String vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public final String [] xdrDecodeStringFixedVector(int length)
           throws OncRpcException, IOException {
        String [] value = new String[length];
        for ( int i = 0; i < length; ++i ) {
            value[i] = xdrDecodeString();
        }
        return value;
    }

	/**
	 * Set the character encoding for deserializing strings.
	 *
	 * @param characterEncoding the encoding to use for deserializing strings.
	 *   If <code>null</code>, the system's default encoding is to be used.
	 */
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	/**
	 * Get the character encoding for deserializing strings.
	 *
	 * @return the encoding currently used for deserializing strings.
	 *   If <code>null</code>, then the system's default encoding is used.
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/**
	 * Encoding to use when deserializing strings or <code>null</code> if
	 * the system's default encoding should be used.
	 */
	private String characterEncoding = null;

}

// End of XdrDecodingStream.java
