/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrUdpDecodingStream.java,v 1.2 2005/11/11 21:07:40 haraldalbrecht Exp $
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

import java.io.*;
import java.net.*;

/**
 * The <code>XdrUdpDecodingStream</code> class provides the necessary
 * functionality to {@link XdrDecodingStream} to receive XDR packets from the
 * network using the datagram-oriented UDP/IP.
 *
 * @version $Revision: 1.2 $ $Date: 2005/11/11 21:07:40 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class XdrUdpDecodingStream extends XdrDecodingStream {

    /**
     * Construct a new <code>XdrUdpDecodingStream</code> object and associate
     * it with the given <code>datagramSocket</code> for UDP/IP-based
     * communication. This constructor is typically used when communicating
     * with servers over UDP/IP using a "connected" datagram socket.
     *
     * @param datagramSocket Datagram socket from which XDR data is received.
     * @param bufferSize Size of packet buffer for storing received XDR
     *   datagrams.
     */
    public XdrUdpDecodingStream(DatagramSocket datagramSocket,
                                int bufferSize) {
        socket = datagramSocket;
        //
        // If the given buffer size is too small, start with a more sensible
        // size. Next, if bufferSize is not a multiple of four, round it up to
        // the next multiple of four.
        //
        if ( bufferSize < 1024 ) {
            bufferSize = 1024;
        }
        if ( (bufferSize & 3) != 0 ) {
            bufferSize = (bufferSize + 4) & ~3;
        }
        buffer = new byte[bufferSize];
        bufferIndex = 0;
        bufferHighmark = -4;
    }

    /**
     * Returns the Internet address of the sender of the current XDR data.
     * This method should only be called after {@link #beginDecoding},
     * otherwise it might return stale information.
     *
     * @return InetAddress of the sender of the current XDR data.
     */
    public InetAddress getSenderAddress() {
        return senderAddress;
    }

    /**
     * Returns the port number of the sender of the current XDR data.
     * This method should only be called after {@link #beginDecoding},
     * otherwise it might return stale information.
     *
     * @return Port number of the sender of the current XDR data.
     */
    public int getSenderPort() {
        return senderPort;
    }

    /**
     * Initiates decoding of the next XDR record. For UDP-based XDR decoding
     * streams this reads in the next datagram from the network socket.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void beginDecoding()
           throws OncRpcException, IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        senderAddress = packet.getAddress();
        senderPort = packet.getPort();
        bufferIndex = 0;
        bufferHighmark = packet.getLength() - 4;
    }

    /**
     * End decoding of the current XDR record. The general contract of
     * <code>endDecoding</code> is that calling it is an indication that
     * the current record is no more interesting to the caller and any
     * allocated data for this record can be freed.
     *
     * <p>This method overrides {@link XdrDecodingStream#endDecoding}. It does nothing
     * more than resetting the buffer pointer (eeek! a pointer in Java!!!) back
     * to the begin of an empty buffer, so attempts to decode data will fail
     * until the buffer is filled again.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void endDecoding()
           throws OncRpcException, IOException {
        bufferIndex = 0;
        bufferHighmark = -4;
    }

    /**
     * Closes this decoding XDR stream and releases any system resources
     * associated with this stream. A closed XDR stream cannot perform decoding
     * operations and cannot be reopened.
     *
     * <p>This implementation frees the allocated buffer but does not close
     * the associated datagram socket. It only throws away the reference to
     * this socket.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void close()
           throws OncRpcException, IOException {
        buffer = null;
        socket = null;
    }

    /**
     * Decodes (aka "deserializes") a "XDR int" value received from a
     * XDR stream. A XDR int is 32 bits wide -- the same width Java's "int"
     * data type has.
     *
     * @return The decoded int value.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public int xdrDecodeInt()
           throws OncRpcException, IOException {
        if ( bufferIndex <= bufferHighmark ) {
            //
            // There's enough space in the buffer to hold at least one
            // XDR int. So let's retrieve it now.
            // Note: buffer[...] gives a byte, which is signed. So if we
            // add it to the value (which is int), it has to be widened
            // to 32 bit, so its sign is propagated. To avoid this sign
            // madness, we have to "and" it with 0xFF, so all unwanted
            // bits are cut off after sign extension. Sigh.
            //
            int value = buffer[bufferIndex++];
            value = (value << 8) + (buffer[bufferIndex++] & 0xFF);
            value = (value << 8) + (buffer[bufferIndex++] & 0xFF);
            value = (value << 8) + (buffer[bufferIndex++] & 0xFF);
            return value;
        } else {
            throw(new OncRpcException(OncRpcException.RPC_BUFFERUNDERFLOW));
        }
    }

    /**
     * Decodes (aka "deserializes") an opaque value, which is nothing more
     * than a series of octets (or 8 bits wide bytes). Because the length
     * of the opaque value is given, we don't need to retrieve it from the
     * XDR stream. This is different from
     * {@link #xdrDecodeOpaque(byte[], int, int)} where
     * first the length of the opaque value is retrieved from the XDR stream.
     *
     * @param length Length of opaque data to decode.
     *
     * @return Opaque data as a byte vector.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public byte [] xdrDecodeOpaque(int length)
           throws OncRpcException, IOException {
        //
        // First make sure that the length is always a multiple of four.
        //
        int alignedLength = length;
        if ( (alignedLength & 3) != 0 ) {
            alignedLength = (alignedLength & ~3) + 4;
        }
        //
        // Now allocate enough memory to hold the data to be retrieved.
        //
        byte [] bytes = new byte[length];
        if ( length > 0 ) {
            if ( bufferIndex <= bufferHighmark - alignedLength + 4 ) {
                System.arraycopy(buffer, bufferIndex, bytes, 0, length);
            } else {
                 throw(new OncRpcException(OncRpcException.RPC_BUFFERUNDERFLOW));
            }
        }
        bufferIndex += alignedLength;
        return bytes;
    }

    /**
     * Decodes (aka "deserializes") a XDR opaque value, which is represented
     * by a vector of byte values, and starts at <code>offset</code> with a
     * length of <code>length</code>. Only the opaque value is decoded, so the
     * caller has to know how long the opaque value will be. The decoded data
     * is always padded to be a multiple of four (because that's what the
     * sender does).
     *
     * @param opaque Byte vector which will receive the decoded opaque value.
     * @param offset Start offset in the byte vector.
     * @param length the number of bytes to decode.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrDecodeOpaque(byte [] opaque, int offset, int length)
           throws OncRpcException, IOException {
        //
        // First make sure that the length is always a multiple of four.
        //
        int alignedLength = length;
        if ( (alignedLength & 3) != 0 ) {
            alignedLength = (alignedLength & ~3) + 4;
        }
        //
        // Now allocate enough memory to hold the data to be retrieved.
        //
        if ( length > 0 ) {
            if ( bufferIndex <= bufferHighmark - alignedLength + 4 ) {
                System.arraycopy(buffer, bufferIndex, opaque, offset, length);
            } else {
                 throw(new OncRpcException(OncRpcException.RPC_BUFFERUNDERFLOW));
            }
        }
        bufferIndex += alignedLength;
    }

    /**
     * The datagram socket to be used when receiving this XDR stream's
     * buffer contents.
     */
    private DatagramSocket socket;
    /**
     * Sender's address of current buffer contents.
     */
    private InetAddress senderAddress = null;
    /**
     * The senders's port.
     */
    private int senderPort = 0;
    /**
     * The buffer which will be filled from the datagram socket and then
     * be used to supply the information when decoding data.
     */
    private byte [] buffer;
    /**
     * The read pointer is an index into the <code>buffer</code>.
     */
    private int bufferIndex;
    /**
     * Index of the last four byte word in the buffer, which has been read
     * in from the datagram socket.
     */
    private int bufferHighmark;

}

// End of XdrUdpDecodingStream
