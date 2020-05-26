/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrTcpEncodingStream.java,v 1.2 2003/08/14 11:07:39 haraldalbrecht Exp $
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
 * The <code>XdrTcpEncodingStream</code> class provides the necessary
 * functionality to {@link XdrEncodingStream} to send XDR records to the
 * network using the stream-oriented TCP/IP.
 *
 * @version $Revision: 1.2 $ $Date: 2003/08/14 11:07:39 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class XdrTcpEncodingStream extends XdrEncodingStream {

    /**
     * Construct a new <code>XdrTcpEncodingStream</code> object and associate
     * it with the given <code>streamingSocket</code> for TCP/IP-based
     * communication.
     *
     * @param streamingSocket Socket to which XDR data is sent.
     * @param bufferSize Size of packet buffer for temporarily storing
     *   outgoing XDR data.
     */
    public XdrTcpEncodingStream(Socket streamingSocket,
                                int bufferSize)
           throws IOException {
        socket = streamingSocket;
        stream = socket.getOutputStream();
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
        //
        // Set up the buffer and the buffer pointers (no, this is still
        // Java).
        //
        buffer = new byte[bufferSize];
        bufferFragmentHeaderIndex = 0;
        bufferIndex = 4;
        bufferHighmark = bufferSize - 4;
    }

    /**
     * Returns the Internet address of the sender of the current XDR data.
     * This method should only be called after {@link #beginEncoding},
     * otherwise it might return stale information.
     *
     * @return InetAddress of the sender of the current XDR data.
     */
    public InetAddress getSenderAddress() {
        return socket.getInetAddress();
    }

    /**
     * Returns the port number of the sender of the current XDR data.
     * This method should only be called after {@link #beginEncoding},
     * otherwise it might return stale information.
     *
     * @return Port number of the sender of the current XDR data.
     */
    public int getSenderPort() {
        return socket.getPort();
    }

    /**
     * Begins encoding a new XDR record. This typically involves resetting this
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
        //
        // Begin encoding with the four byte word after the fragment header,
        // which also four bytes wide. We have to remember where we can find
        // the fragment header as we support batching/pipelining calls, so
        // several requests (each in its own fragment) can be simultaneously
        // in the write buffer.
        //
        //bufferFragmentHeaderIndex = bufferIndex;
        //bufferIndex += 4;
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
        flush(true, false);
    }

    /**
     * Ends the current record fort this encoding XDR stream. If the parameter
     * <code>flush</code> is <code>true</code> any buffered output bytes are
     * immediately written to their intended destination. If <code>flush</code>
     * is <code>false</code>, then more than one record can be pipelined, for
     * instance, to batch several ONC/RPC calls. In this case the ONC/RPC
     * server <b>must not</b> send a reply (with the exception for the last
     * call in a batch, which might be trigger a reply). Otherwise, you will
     * most probably cause an interaction deadlock between client and server.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void endEncoding(boolean flush)
           throws OncRpcException, IOException {
        flush(true, !flush);
    }

    /**
     * Flushes the current contents of the buffer as one fragment to the
     * network.
     *
     * @param lastFragment <code>true</code> if this is the last fragment of
     *   the current XDR record.
     * @param batch if last fragment and <code>batch</code> is
     *   <code>true</code>, then the buffer is not flushed to the network
     *   but instead we wait for more records to be encoded.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    private void flush(boolean lastFragment, boolean batch)
           throws OncRpcException, IOException {
        //
        // Encode the fragment header. We have to take batching/pipelining
        // into account, so multiple complete fragments may be waiting in
        // the same write buffer. The variable bufferFragmentHeaderIndex
        // points to the place where we should store this fragment's header.
        //
        int fragmentLength = bufferIndex - bufferFragmentHeaderIndex - 4;
        if ( lastFragment ) {
            fragmentLength |= 0x80000000;
        }
        buffer[bufferFragmentHeaderIndex] = (byte)(fragmentLength >>> 24);
        buffer[bufferFragmentHeaderIndex + 1] = (byte)(fragmentLength >>> 16);
        buffer[bufferFragmentHeaderIndex + 2] = (byte)(fragmentLength >>>  8);
        buffer[bufferFragmentHeaderIndex + 3] = (byte) fragmentLength;
        if ( !lastFragment // buffer is full, so we have to flush
             || !batch     // buffer not full, but last fragment and not in batch
             || (bufferIndex >= bufferHighmark) // not enough space for next
                                                // fragment header and one int
           ) {
            //
            // Finally write the buffer's contents into the vastness of
            // network space. This has to be done when we do not need to
            // pipeline calls and if there is still enough space left in
            // the buffer for the fragment header and at least a single
            // int.
            //
            stream.write(buffer, 0, bufferIndex);
            stream.flush();
            //
            // Reset write pointer after the fragment header int within
            // buffer, so the next bunch of data can be encoded.
            //
            bufferFragmentHeaderIndex = 0;
            bufferIndex = 4;
       } else {
            //
            // Batch/pipeline several consecuting XDR records. So do not
            // flush the buffer yet to the network but instead wait for more
            // data.
            //
            bufferFragmentHeaderIndex = bufferIndex;
            bufferIndex += 4;
        }
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
        if ( bufferIndex > bufferHighmark ) {
            flush(false, false);
        }
        //
        // There's enough space in the buffer, so encode this int as
        // four bytes (french octets) in big endian order (that is, the
        // most significant byte comes first.
        //
        buffer[bufferIndex++] = (byte)(value >>> 24);
        buffer[bufferIndex++] = (byte)(value >>> 16);
        buffer[bufferIndex++] = (byte)(value >>>  8);
        buffer[bufferIndex++] = (byte) value;
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
        int padding = (4 - (length & 3)) & 3;
        int toCopy;

        while ( length > 0 ) {
            toCopy = bufferHighmark - bufferIndex + 4;
            if ( toCopy >= length ) {
                //
                // The buffer has more free space than we need. So copy the
                // bytes and leave the stage.
                //
                System.arraycopy(value, offset, buffer, bufferIndex, length);
                bufferIndex += length;
                // No need to adjust "offset", because this is the last round.
                break;
            } else {
                //
                // We need to copy more data than currently available from our
                // buffer, so we copy all we can get our hands on, then fill
                // the buffer again and repeat this until we got all we want.
                //
                System.arraycopy(value, offset, buffer, bufferIndex, toCopy);
                bufferIndex += toCopy;
                offset += toCopy;
                length -= toCopy;
                flush(false, false);
            }
        }
        System.arraycopy(paddingZeros, 0, buffer, bufferIndex, padding);
        bufferIndex += padding;
    }

    /**
     * The streaming socket to be used when receiving this XDR stream's
     * buffer contents.
     */
    private Socket socket;

    /**
     * The output stream used to get rid of bytes going off to the network.
     */
    OutputStream stream;

    /**
     * The buffer which will be filled from the datagram socket and then
     * be used to supply the information when decoding data.
     */
    private byte [] buffer;

    /**
     * The write pointer is an index into the <code>buffer</code>.
     */
    private int bufferIndex;

    /**
     * Index of the last four byte word in the <code>buffer</code>.
     */
    private int bufferHighmark;

    /**
     * Index of fragment header within <code>buffer</code>.
     */
    private int bufferFragmentHeaderIndex;

    /**
     * Some zeros, only needed for padding -- like in real life.
     */
    private static final byte [] paddingZeros = { 0, 0, 0, 0 };

}

// End of XdrTcpEncodingStream.java
