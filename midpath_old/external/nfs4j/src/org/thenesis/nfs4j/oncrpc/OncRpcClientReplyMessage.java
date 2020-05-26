/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcClientReplyMessage.java,v 1.1.1.1 2003/08/13 12:03:40 haraldalbrecht Exp $
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

/**
 * The <code>OncRpcReplyMessage</code> class represents an ONC/RPC reply
 * message as defined by ONC/RPC in RFC 1831. Such messages are sent back by
 * ONC/RPC to servers to clients and contain (in case of real success) the
 * result of a remote procedure call.
 *
 * <p>The decision to define only one single class for the accepted and
 * rejected replies was driven by the motivation not to use polymorphism
 * and thus have to upcast and downcast references all the time.
 *
 * <p>The derived classes are only provided for convinience on the server
 * side.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:40 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcClientReplyMessage extends OncRpcReplyMessage {

    /**
     * Initializes a new <code>OncRpcReplyMessage</code> object to represent
     * an invalid state. This default constructor should only be used if in the
     * next step the real state of the reply message is immediately decoded
     * from a XDR stream.
     *
     * @param auth Client-side authentication protocol handling object which
     *   is to be used when decoding the verifier data contained in the reply.
     */
    public OncRpcClientReplyMessage(OncRpcClientAuth auth) {
        super();
        this.auth = auth;
    }

    /**
     * Check whether this <code>OncRpcReplyMessage</code> represents an
     * accepted and successfully executed remote procedure call.
     *
     * @return <code>true</code> if remote procedure call was accepted and
     *   successfully executed.
     */
    public boolean successfullyAccepted() {
        return (replyStatus == OncRpcReplyStatus.ONCRPC_MSG_ACCEPTED)
               && (acceptStatus == OncRpcAcceptStatus.ONCRPC_SUCCESS);
    }

    /**
     * Return an appropriate exception object according to the state this
     * reply message header object is in. The exception object then can be
     * thrown.
     *
     * @return Exception object of class {@link OncRpcException} or a subclass
     *    thereof.
     */
    public OncRpcException newException() {
        switch ( replyStatus ) {
        case OncRpcReplyStatus.ONCRPC_MSG_ACCEPTED:
            switch ( acceptStatus ) {
            case OncRpcAcceptStatus.ONCRPC_SUCCESS:
                return new OncRpcException(OncRpcException.RPC_SUCCESS);
            case OncRpcAcceptStatus.ONCRPC_PROC_UNAVAIL:
                return new OncRpcException(OncRpcException.RPC_PROCUNAVAIL);
            case OncRpcAcceptStatus.ONCRPC_PROG_MISMATCH:
                return new OncRpcException(OncRpcException.RPC_PROGVERSMISMATCH);
            case OncRpcAcceptStatus.ONCRPC_PROG_UNAVAIL:
                return new OncRpcException(OncRpcException.RPC_PROGUNAVAIL);
            case OncRpcAcceptStatus.ONCRPC_GARBAGE_ARGS:
                return new OncRpcException(OncRpcException.RPC_CANTDECODEARGS);
            case OncRpcAcceptStatus.ONCRPC_SYSTEM_ERR:
                return new OncRpcException(OncRpcException.RPC_SYSTEMERROR);
            }
            break;

        case OncRpcReplyStatus.ONCRPC_MSG_DENIED:
            switch ( rejectStatus ) {
            case OncRpcRejectStatus.ONCRPC_AUTH_ERROR:
                return new OncRpcAuthenticationException(authStatus);
            case OncRpcRejectStatus.ONCRPC_RPC_MISMATCH:
                return new OncRpcException(OncRpcException.RPC_FAILED);
            }
            break;
        }
        return new OncRpcException();
    }

    /**
     * Decodes -- that is: deserializes -- a ONC/RPC message header object
     * from a XDR stream.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        messageId = xdr.xdrDecodeInt();
        //
        // Make sure that we are really decoding an ONC/RPC message call
        // header. Otherwise, throw the appropriate OncRpcException exception.
        //
        messageType = xdr.xdrDecodeInt();
        if ( messageType != OncRpcMessageType.ONCRPC_REPLY ) {
            throw(new OncRpcException(OncRpcException.RPC_WRONGMESSAGE));
        }
        replyStatus = xdr.xdrDecodeInt();
        switch ( replyStatus ) {
        case OncRpcReplyStatus.ONCRPC_MSG_ACCEPTED:
            //
            // Decode the information returned for accepted message calls.
            // If we have an associated client-side authentication protocol
            // object, we use that. Otherwise we fall back to the default
            // handling of only the AUTH_NONE authentication.
            //
            if ( auth != null ) {
                auth.xdrDecodeVerf(xdr);
            } else {
                //
                // If we don't have a protocol handler and the server sent its
                // reply using another authentication scheme than AUTH_NONE, we
                // will throw an exception. Also we check that no-one is
                // actually sending opaque information within AUTH_NONE.
                //
                if ( xdr.xdrDecodeInt() != OncRpcAuthType.ONCRPC_AUTH_NONE ) {
                    throw(new OncRpcAuthenticationException(
                        OncRpcAuthStatus.ONCRPC_AUTH_FAILED));
                }
                if ( xdr.xdrDecodeInt() != 0 ) {
                    throw(new OncRpcAuthenticationException(
                        OncRpcAuthStatus.ONCRPC_AUTH_FAILED));
                }
            }
            //
            // Even if the call was accepted by the server, it can still
            // indicate an error. Depending on the status of the accepted
            // call we will receive an indication about the range of
            // versions a particular program (server) supports.
            //
            acceptStatus = xdr.xdrDecodeInt();
            switch ( acceptStatus ) {
            case OncRpcAcceptStatus.ONCRPC_PROG_MISMATCH:
                lowVersion = xdr.xdrDecodeInt();
                highVersion = xdr.xdrDecodeInt();
                break;
            default:
                //
                // Otherwise "open ended set of problem", like the author
                // of Sun's ONC/RPC source once wrote...
                //
                break;
            }
            break;

        case OncRpcReplyStatus.ONCRPC_MSG_DENIED:
            //
            // Encode the information returned for denied message calls.
            //
            rejectStatus = xdr.xdrDecodeInt();
            switch ( rejectStatus ) {
            case OncRpcRejectStatus.ONCRPC_RPC_MISMATCH:
                lowVersion = xdr.xdrDecodeInt();
                highVersion = xdr.xdrDecodeInt();
                break;
            case OncRpcRejectStatus.ONCRPC_AUTH_ERROR:
                authStatus = xdr.xdrDecodeInt();
                break;
            default:
            }
            break;
        }
    }

    /**
     * Client-side authentication protocol handling object to use when
     * decoding the reply message.
     */
    protected OncRpcClientAuth auth;

}

// End of OncRpcClientReplyMessage.java
