/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrUdpEncodingStream.java,v 1.2 2003/08/14 11:07:39 haraldalbrecht Exp $
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
 * functionality to {@link XdrDecodingStream} to send XDR packets over the
 * network using the datagram-oriented UDP/IP.
 *
 * @version $Revision: 1.2 $ $Date: 2003/08/14 11:07:39 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class XdrUdpEncodingStream extends XdrEncodingStream {

    /**
     * Creates a new UDP-based encoding XDR stream, associated with the
     * given datagram socket.
     *
     * @param datagramSocket Datagram-based socket to use to get rid of
     *   encoded data.
     * @param bufferSize Size of buffer to store encoded data before it
     *   is sent as one datagram.
     */
    public XdrUdpEncodingStream(DatagramSocket datagramSocket,
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
        bufferHighmark = bufferSize - 4;
    }

    /**
     * Begins encoding a new XDR record. This involves resetting this
     * encoding XDR stream back into a known state.
     *
     * @param receiverAddress Indicates the receiver of the XDR data. This can be
     *   <code>null</code> for XDR streams connected permanently to a
     *   receiver (like in case of TCP/IP based XDR streams). 
     * @param receiverPort Port number of the receiver.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void beginEncoding(InetAddress receiverAddress, int receiverPort)
           throws OncRpcException, IOException {
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        bufferIndex = 0;
    }

    /**
     * Flushes this encoding XDR stream and forces any buffered output bytes
     * to be written out. The general contract of <code>endEncoding</code> is that
     * calling it is an indication that the current record is finished and any
     * bytes previously encoded should immediately be written to their intended
     * destination.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void endEncoding()
           throws OncRpcException, IOException {
        DatagramPacket packet = new DatagramPacket(buffer, bufferIndex,
                                                   receiverAddress,
                                                   receiverPort);
        socket.send(packet);
    }

    /**
     * Closes this encoding XDR stream and releases any system resources
     * associated with this stream. The general contract of <code>close</code>
     * is that it closes the encoding XDR stream. A closed XDR stream cannot
     * perform encoding operations and cannot be reopened.
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
     * Encodes (aka "serializes") a "XDR int" value and writes it down a
     * XDR stream. A XDR int is 32 bits wide -- the same width Java's "int"
     * data type has. This method is one of the basic methods all other
     * methods can rely on.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrEncodeInt(int value)
           throws OncRpcException, IOException {
        if ( bufferIndex <= bufferHighmark ) {
            //
            // There's enough space in the buffer, so encode this int as
            // four bytes (french octets) in big endian order (that is, the
            // most significant byte comes first.
            //
            buffer[bufferIndex++] = (byte)(value >>> 24);
            buffer[bufferIndex++] = (byte)(value >>> 16);
            buffer[bufferIndex++] = (byte)(value >>>  8);
            buffer[bufferIndex++] = (byte) value;
        } else {
            throw(new OncRpcException(OncRpcException.RPC_BUFFEROVERFLOW));
        }
    }

    /**
     * Encodes (aka "serializes") a XDR opaque value, which is represented
     * by a vector of byte values, and starts at <code>offset</code> with a
     * length of <code>length</code>. Only the opaque value is encoded, but
     * no length indication is preceeding the opaque value, so the receiver
     * has to know how long the opaque value will be. The encoded data is
     * always padded to be a multiple of four. If the given length is not a
     * multiple of four, zero bytes will be used for padding.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrEncodeOpaque(byte [] value, int offset, int length)
           throws OncRpcException, IOException {
        //
        // First calculate the number of bytes needed for padding.
        //
        int padding = (4 - (length & 3)) & 3;
        if ( bufferIndex <= bufferHighmark - (length + padding) ) {
            System.arraycopy(value, offset, buffer, bufferIndex, length);
            bufferIndex += length;
            if ( padding != 0 ) {
                //
                // If the length of the opaque data was not a multiple, then
                // pad with zeros, so the write pointer (argh! how comes Java
                // has a pointer...?!) points to a byte, which has an index
                // of a multiple of four.
                //
                System.arraycopy(paddingZeros, 0, buffer, bufferIndex, padding);
                bufferIndex += padding;
            }
        } else {
            throw(new OncRpcException(OncRpcException.RPC_BUFFEROVERFLOW));
        }
    }

    /**
     * The datagram socket to be used when sending this XDR stream's
     * buffer contents.
     */
    private DatagramSocket socket;
    /**
     * Receiver address of current buffer contents when flushed.
     */
    private InetAddress receiverAddress = null;
    /**
     * The receiver's port.
     */
    private int receiverPort = 0;
    /**
     * The buffer which will receive the encoded information, before it
     * is sent via a datagram socket.
     */
    private byte [] buffer;
    /**
     * The write pointer is an index into the <code>buffer</code>.
     */
    private int bufferIndex;
    /**
     * Index of the last four byte word in the buffer.
     */
    private int bufferHighmark;
    /**
     * Some zeros, only needed for padding -- like in real life.
     */
    private static final byte [] paddingZeros = { 0, 0, 0, 0 };

}

// End of XdrUdpEncodingStream
