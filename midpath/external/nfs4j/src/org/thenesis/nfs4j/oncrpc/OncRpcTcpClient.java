/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcTcpClient.java,v 1.5 2005/11/11 21:04:30 haraldalbrecht Exp $
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
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * ONC/RPC client which communicates with ONC/RPC servers over the network
 * using the stream-oriented protocol TCP/IP.
 *
 * @version $Revision: 1.5 $ $Date: 2005/11/11 21:04:30 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcTcpClient extends OncRpcClient {

    /**
     * Constructs a new <code>OncRpcTcpClient</code> object, which connects
     * to the ONC/RPC server at <code>host</code> for calling remote procedures
     * of the given { program, version }.
     *
     * <p>Note that the construction of an <code>OncRpcTcpClient</code>
     * object will result in communication with the portmap process at
     * <code>host</code>.
     *
     * @param host The host where the ONC/RPC server resides.
     * @param program Program number of the ONC/RPC server to call.
     * @param version Program version number.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcTcpClient(InetAddress host,
                           int program, int version)
           throws OncRpcException, IOException {
        this(host, program, version, 0, 0);
    }

    /**
     * Constructs a new <code>OncRpcTcpClient</code> object, which connects
     * to the ONC/RPC server at <code>host</code> for calling remote procedures
     * of the given { program, version }.
     *
     * <p>Note that the construction of an <code>OncRpcTcpClient</code>
     * object will result in communication with the portmap process at
     * <code>host</code> if <code>port</code> is <code>0</code>.
     *
     * @param host The host where the ONC/RPC server resides.
     * @param program Program number of the ONC/RPC server to call.
     * @param version Program version number.
     * @param port The port number where the ONC/RPC server can be contacted.
     *   If <code>0</code>, then the <code>OncRpcUdpClient</code> object will
     *   ask the portmapper at <code>host</code> for the port number.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcTcpClient(InetAddress host,
                           int program, int version,
                           int port)
           throws OncRpcException, IOException {
        this(host, program, version, port, 0);
    }

        /**
     * Constructs a new <code>OncRpcTcpClient</code> object, which connects
     * to the ONC/RPC server at <code>host</code> for calling remote procedures
     * of the given { program, version }.
     *
     * <p>Note that the construction of an <code>OncRpcTcpClient</code>
     * object will result in communication with the portmap process at
     * <code>host</code> if <code>port</code> is <code>0</code>.
     *
     * @param host The host where the ONC/RPC server resides.
     * @param program Program number of the ONC/RPC server to call.
     * @param version Program version number.
     * @param port The port number where the ONC/RPC server can be contacted.
     *   If <code>0</code>, then the <code>OncRpcUdpClient</code> object will
     *   ask the portmapper at <code>host</code> for the port number.
     * @param bufferSize Size of receive and send buffers. In contrast to
     *   UDP-based ONC/RPC clients, messages larger than the specified
     *   buffer size can still be sent and received. The buffer is only
     *   necessary to handle the messages and the underlaying streams will
     *   break up long messages automatically into suitable pieces.
     *   Specifying zero will select the default buffer size (currently
     *   8192 bytes).
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcTcpClient(InetAddress host,
                           int program, int version,
                           int port,
                           int bufferSize)
           throws OncRpcException, IOException {
        this(host, program, version, port, bufferSize, -1);
    }

    /**
     * Constructs a new <code>OncRpcTcpClient</code> object, which connects
     * to the ONC/RPC server at <code>host</code> for calling remote procedures
     * of the given { program, version }.
     *
     * <p>Note that the construction of an <code>OncRpcTcpClient</code>
     * object will result in communication with the portmap process at
     * <code>host</code> if <code>port</code> is <code>0</code>.
     *
     * @param host The host where the ONC/RPC server resides.
     * @param program Program number of the ONC/RPC server to call.
     * @param version Program version number.
     * @param port The port number where the ONC/RPC server can be contacted.
     *   If <code>0</code>, then the <code>OncRpcUdpClient</code> object will
     *   ask the portmapper at <code>host</code> for the port number.
     * @param bufferSize Size of receive and send buffers. In contrast to
     *   UDP-based ONC/RPC clients, messages larger than the specified
     *   buffer size can still be sent and received. The buffer is only
     *   necessary to handle the messages and the underlaying streams will
     *   break up long messages automatically into suitable pieces.
     *   Specifying zero will select the default buffer size (currently
     *   8192 bytes).
     * @param timeout Maximum timeout in milliseconds when connecting to
     *   the ONC/RPC server. If negative, a default implementation-specific
     *   timeout setting will apply. <i>Note that this timeout only applies
     *   to the connection phase, but <b>not</b> to later communication.</i>
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcTcpClient(InetAddress host,
                           int program, int version,
                           int port,
                           int bufferSize,
                           int timeout)
           throws OncRpcException, IOException {
        //
        // Construct the inherited part of our object. This will also try to
        // lookup the port of the desired ONC/RPC server, if no port number
        // was specified (port = 0).
        //
        super(host, program, version, port, OncRpcProtocols.ONCRPC_TCP);
        //
        // Let the host operating system choose which port (and network
        // interface) to use. Then set the buffer sizes for sending and
        // receiving UDP datagrams. Finally set the destination of packets.
        //
        if ( bufferSize == 0 ) {
            bufferSize = 8192; // default setting
        }
        if ( bufferSize < 1024 ) {
            bufferSize = 1024;
        }
        //
        // Note that we use this.port at this time, because the superclass
        // might have resolved the port number in case the caller specified
        // simply 0 as the port number.
        //
        socketHelper = new OncRpcTcpSocketHelper();
        socket = socketHelper.connect(host, this.port, timeout);

        socket.setTcpNoDelay(true);
        if ( socketHelper.getSendBufferSize() < bufferSize ) {
            socketHelper.setSendBufferSize(bufferSize);
        }
        if ( socketHelper.getReceiveBufferSize() < bufferSize ) {
            socketHelper.setReceiveBufferSize(bufferSize);
        }
        //
        // Create the necessary encoding and decoding streams, so we can
        // communicate at all.
        //
        sendingXdr = new XdrTcpEncodingStream(socket, bufferSize);
        receivingXdr = new XdrTcpDecodingStream(socket, bufferSize);
    }

    /**
     * Close the connection to an ONC/RPC server and free all network-related
     * resources. Well -- at least hope, that the Java VM will sometimes free
     * some resources. Sigh.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     */
    public void close()
           throws OncRpcException {
        if ( socket != null ) {
            try {
                socket.close();
            } catch ( IOException e ) {
            }
            socket = null;
        }
        if ( sendingXdr != null ) {
            try {
                sendingXdr.close();
            } catch ( IOException e ) {
            }
            sendingXdr = null;
        }
        if ( receivingXdr != null ) {
            try {
                receivingXdr.close();
            } catch ( IOException e ) {
            }
            receivingXdr = null;
        }
    }

    /**
     * Calls a remote procedure on an ONC/RPC server.
     *
     * <p>Please note that while this method supports call batching by
     * setting the communication timeout to zero
     * (<code>setTimeout(0)</code>) you should better use
     * {@link #batchCall} as it provides better control over when the
     * batch should be flushed to the server.
     *
     * @param procedureNumber Procedure number of the procedure to call.
     * @param versionNumber Protocol version number.
     * @param params The parameters of the procedure to call, contained
     *   in an object which implements the {@link XdrAble} interface.
     * @param result The object receiving the result of the procedure call.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     */
    public synchronized void call(int procedureNumber, int versionNumber,
                                  XdrAble params, XdrAble result)
        throws OncRpcException {
    Refresh:
        for ( int refreshesLeft = 1; refreshesLeft >= 0;
              --refreshesLeft ) {
            //
            // First, build the ONC/RPC call header. Then put the sending
            // stream into a known state and encode the parameters to be
            // sent. Finally tell the encoding stream to send all its data
            // to the server. Then wait for an answer, receive it and decode
            // it. So that's the bottom line of what we do right here.
            //
            nextXid();

            OncRpcClientCallMessage callHeader =
                new OncRpcClientCallMessage(xid,
                                            program,
                                            versionNumber, procedureNumber,
                                            auth);
            OncRpcClientReplyMessage replyHeader =
                new OncRpcClientReplyMessage(auth);

            //
            // Send call message to server. If we receive an IOException,
            // then we'll throw the appropriate ONC/RPC (client) exception.
            // Note that we use a connected stream, so we don't need to
            // specify a destination when beginning serialization.
            //
            try {
                socket.setSoTimeout(transmissionTimeout);
                sendingXdr.beginEncoding(null, 0);
                callHeader.xdrEncode(sendingXdr);
                params.xdrEncode(sendingXdr);
                if ( timeout != 0 ) {
                    sendingXdr.endEncoding();
                } else {
                    sendingXdr.endEncoding(false);
                }
            } catch ( IOException e ) {
                throw(new OncRpcException(OncRpcException.RPC_CANTSEND,
                                          e.getLocalizedMessage()));
            }

            //
            // Receive reply message from server -- at least try to do so...
            // In case of batched calls we don't need no stinkin' answer, so
            // we can do other, more interesting things.
            //
            if ( timeout == 0 ) {
                return;
            }

            try {
                //
                // Keep receiving until we get the matching reply.
                //
                while ( true ) {
                    socket.setSoTimeout(timeout);
                    receivingXdr.beginDecoding();
                    socket.setSoTimeout(transmissionTimeout);
                    //
                    // First, pull off the reply message header of the
                    // XDR stream. In case we also received a verifier
                    // from the server and this verifier was invalid, broken
                    // or tampered with, we will get an
                    // OncRpcAuthenticationException right here, which will
                    // propagate up to the caller. If the server reported
                    // an authentication problem itself, then this will
                    // be handled as any other rejected ONC/RPC call.
                    //
                    try {
                    	replyHeader.xdrDecode(receivingXdr);
                    } catch ( OncRpcException e ) {
                    	//
                    	// ** SF bug #1262106 **
                    	//
                    	// We ran into some sort of trouble. Usually this will have
                    	// been a buffer underflow. Whatever, end the decoding process
                    	// and ensure this way that the next call has a chance to start
                    	// from a clean state.
                    	//
                    	receivingXdr.endDecoding();
                    	throw(e);
                    }
                    //
                    // Only deserialize the result, if the reply matches the
                    // call. Otherwise skip this record.
                    //
                    if ( replyHeader.messageId == callHeader.messageId ) {
                        break;
                    }
                    receivingXdr.endDecoding();
                }
                //
                // Make sure that the call was accepted. In case of unsuccessful
                // calls, throw an exception, if it's not an authentication
                // exception. In that case try to refresh the credential first.
                //
                if ( !replyHeader.successfullyAccepted() ) {
                    receivingXdr.endDecoding();
                    //
                    // Check whether there was an authentication
                    // problem. In this case first try to refresh the
                    // credentials.
                    //
                    if ( (refreshesLeft > 0)
                         && (replyHeader.replyStatus
                             == OncRpcReplyStatus.ONCRPC_MSG_DENIED)
                         && (replyHeader.rejectStatus
                             == OncRpcRejectStatus.ONCRPC_AUTH_ERROR)
                         && (auth != null)
                         && auth.canRefreshCred() ) {
                        continue Refresh;
                    }
                    //
                    // Nope. No chance. This gets tough.
                    //
                    throw(replyHeader.newException());
                }
                try {
                	result.xdrDecode(receivingXdr);
                } catch ( OncRpcException e ) {
                	//
                	// ** SF bug #1262106 **
                	//
                	// We ran into some sort of trouble. Usually this will have
                	// been a buffer underflow. Whatever, end the decoding process
                	// and ensure this way that the next call has a chance to start
                	// from a clean state.
                	//
                	receivingXdr.endDecoding();
                	throw(e);
                }
                //
                // Free pending resources of buffer and exit the call loop,
                // returning the reply to the caller through the result
                // object.
                //
                receivingXdr.endDecoding();
                return;
            } catch ( InterruptedIOException e ) {
                //
                // In case our time run out, we throw an exception.
                //
                throw(new OncRpcTimeoutException());
            } catch ( IOException e ) {
                //
                // Argh. Trouble with the transport. Seems like we can't
                // receive data. Gosh. Go away!
                //
                throw(new OncRpcException(OncRpcException.RPC_CANTRECV,
                                          e.getLocalizedMessage()));
            }
        } // for ( refreshesLeft )
    }

    /**
     * Issues a batched call for a remote procedure to an ONC/RPC server.
     * Below is a small example (exception handling ommited for clarity):
     *
     * <pre>
     * OncRpcTcpClient client = new OncRpcTcpClient(
     *     InetAddress.getByName("localhost"),
     *     myprogramnumber, myprogramversion,
     *     OncRpcProtocols.ONCRPC_TCP);
     * client.callBatch(42, myparams, false);
     * client.callBatch(42, myotherparams, false);
     * client.callBatch(42, myfinalparams, true);
     * </pre>
     *
     * In the example above, three calls are batched in a row and only be sent
     * all together with the third call. Note that batched calls must not expect
     * replies, with the only exception being the last call in a batch:
     *
     * <pre>
     * client.callBatch(42, myparams, false);
     * client.callBatch(42, myotherparams, false);
     * client.call(43, myfinalparams, myfinalresult);
     * </pre>
     *
     * @param procedureNumber Procedure number of the procedure to call.
     * @param params The parameters of the procedure to call, contained
     *   in an object which implements the {@link XdrAble} interface.
     * @param flush Make sure that all pending batched calls are sent to
     *   the server.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     */
    public synchronized void batchCall(int procedureNumber,
                                       XdrAble params,
                                       boolean flush)
        throws OncRpcException {
        //
        // First, build the ONC/RPC call header. Then put the sending
        // stream into a known state and encode the parameters to be
        // sent. Finally tell the encoding stream to send all its data
        // to the server. We don't then need to wait for an answer. And
        // we don't need to take care of credential refreshes either.
        //
        nextXid();

        OncRpcClientCallMessage callHeader =
            new OncRpcClientCallMessage(xid,
                                        program, version, procedureNumber,
                                        auth);

        //
        // Send call message to server. If we receive an IOException,
        // then we'll throw the appropriate ONC/RPC (client) exception.
        // Note that we use a connected stream, so we don't need to
        // specify a destination when beginning serialization.
        //
        try {
            socket.setSoTimeout(transmissionTimeout);
            sendingXdr.beginEncoding(null, 0);
            callHeader.xdrEncode(sendingXdr);
            params.xdrEncode(sendingXdr);
            sendingXdr.endEncoding(flush);
        } catch ( IOException e ) {
            throw(new OncRpcException(OncRpcException.RPC_CANTSEND,
                                      e.getLocalizedMessage()));
        }
    }

    /**
     * Set the timout for remote procedure calls to wait for an answer from
     * the ONC/RPC server. If the timeout expires,
     * {@link #call(int, XdrAble, XdrAble)} will raise a
     * {@link java.io.InterruptedIOException}. The default timeout value is
     * 30 seconds (30,000 milliseconds). The timeout must be > 0.
     * A timeout of zero indicates a batched call, for which no reply message
     * is expected.
     *
     * @param milliseconds Timeout in milliseconds. A timeout of zero indicates
     *   batched calls.
     */
    public void setTimeout(int milliseconds) {
        super.setTimeout(milliseconds);
    }

    /**
     * Set the timeout used during transmission of data. If the flow of data
     * when sending calls or receiving replies blocks longer than the given
     * timeout, an exception is thrown. The timeout must be > 0.
     *
     * @param milliseconds Transmission timeout in milliseconds.
     */
    public void setTransmissionTimeout(int milliseconds) {
         if ( milliseconds <= 0 ) {
            throw(new IllegalArgumentException("transmission timeout must be > 0"));
        }
       transmissionTimeout = milliseconds;
    }

    /**
     * Retrieve the current timeout used during transmission phases (call and
     * reply phases).
     *
     * @return Current transmission timeout.
     */
    public int getTransmissionTimeout() {
        return transmissionTimeout;
    }

	/**
	 * Set the character encoding for (de-)serializing strings.
	 *
	 * @param characterEncoding the encoding to use for (de-)serializing strings.
	 *   If <code>null</code>, the system's default encoding is to be used.
	 */
	public void setCharacterEncoding(String characterEncoding) {
		receivingXdr.setCharacterEncoding(characterEncoding);
		sendingXdr.setCharacterEncoding(characterEncoding);
	}

	/**
	 * Get the character encoding for (de-)serializing strings.
	 *
	 * @return the encoding currently used for (de-)serializing strings.
	 *   If <code>null</code>, then the system's default encoding is used.
	 */
	public String getCharacterEncoding() {
		return receivingXdr.getCharacterEncoding();
	}

    /**
     * TCP socket used for stream-oriented communication with an ONC/RPC
     * server.
     */
    private Socket socket;

    /**
     * Socket helper object supplying missing methods for JDK&nbsp;1.1
     * backwards compatibility. So much for compile once, does not run
     * everywhere.
     */
    private OncRpcTcpSocketHelper socketHelper;

    /**
     * XDR encoding stream used for sending requests via TCP/IP to an ONC/RPC
     * server.
     */
    protected XdrTcpEncodingStream sendingXdr;

    /**
     * XDR decoding stream used when receiving replies via TCP/IP from an
     * ONC/RPC server.
     */
    protected XdrTcpDecodingStream receivingXdr;

    /**
     * Timeout during the phase where data is sent within calls, or data is
     * received within replies.
     */
    protected int transmissionTimeout = 30000;

}

// End of OncRpcTcpClient.java