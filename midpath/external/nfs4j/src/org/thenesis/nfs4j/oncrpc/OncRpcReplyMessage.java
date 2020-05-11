/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcReplyMessage.java,v 1.2 2003/08/14 07:56:59 haraldalbrecht Exp $
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
 * @version $Revision: 1.2 $ $Date: 2003/08/14 07:56:59 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class OncRpcReplyMessage extends OncRpcMessage {

    /**
     * The reply status of the reply message. This can be either
     * {@link OncRpcReplyStatus#ONCRPC_MSG_ACCEPTED} or
     * {@link OncRpcReplyStatus#ONCRPC_MSG_DENIED}. Depending on the value
     * of this field, other fields of an instance of
     * <code>OncRpcReplyMessage</code> become important.
     *
     * <p>The decision to define only one single class for the accepted and
     * rejected replies was driven by the motivation not to use polymorphism
     * and thus have to upcast and downcast references all the time.
     */
    public int replyStatus;

    /**
     * Acceptance status in case this reply was sent in response to an
     * accepted call ({@link OncRpcReplyStatus#ONCRPC_MSG_ACCEPTED}). This
     * field can take any of the values defined in the
     * {@link OncRpcAcceptStatus} interface.
     *
     * <p>Note that even for accepted calls that only in the case of
     * {@link OncRpcAcceptStatus#ONCRPC_SUCCESS} result data will follow
     * the reply message header.
     */
    public int acceptStatus;

    /**
     * Rejectance status in case this reply sent in response to a
     * rejected call ({@link OncRpcReplyStatus#ONCRPC_MSG_DENIED}). This
     * field can take any of the values defined in the
     * {@link OncRpcRejectStatus} interface.
     */
    public int rejectStatus;

    /**
     * Lowest supported version in case of
     * {@link OncRpcRejectStatus#ONCRPC_RPC_MISMATCH} and
     * {@link OncRpcAcceptStatus#ONCRPC_PROG_MISMATCH}.
     */
    public int lowVersion;
    /**
     * Highest supported version in case of
     * {@link OncRpcRejectStatus#ONCRPC_RPC_MISMATCH} and
     * {@link OncRpcAcceptStatus#ONCRPC_PROG_MISMATCH}.
     */
    public int highVersion;

    /**
     * Contains the reason for authentification failure in the case
     * of {@link OncRpcRejectStatus#ONCRPC_AUTH_ERROR}.
     */
    public int authStatus;

    /**
     * Initializes a new <code>OncRpcReplyMessage</code> object to represent
     * an invalid state. This default constructor should only be used if in the
     * next step the real state of the reply message is immediately decoded
     * from a XDR stream.
     */
    public OncRpcReplyMessage() {
        super(0);
        messageType  = OncRpcMessageType.ONCRPC_REPLY;
        replyStatus  = OncRpcReplyStatus.ONCRPC_MSG_ACCEPTED;
        acceptStatus = OncRpcAcceptStatus.ONCRPC_SYSTEM_ERR;
        rejectStatus = UNUSED_PARAMETER;
        lowVersion   = 0;
        highVersion  = 0;
        authStatus   = UNUSED_PARAMETER;
    }

    /**
     * Initializes a new <code>OncRpcReplyMessage</code> object and initializes
     * its complete state from the given parameters.
     *
     * <p>Note that depending on the reply, acceptance and rejectance status
     * some parameters are unused and can be specified as
     * <code>UNUSED_PARAMETER</code>.
     *
     * @param call The ONC/RPC call this reply message corresponds to.
     * @param replyStatus The reply status (see {@link OncRpcReplyStatus}).
     * @param acceptStatus The acceptance state (see {@link OncRpcAcceptStatus}).
     * @param rejectStatus The rejectance state (see {@link OncRpcRejectStatus}).
     * @param lowVersion lowest supported version.
     * @param highVersion highest supported version.
     * @param authStatus The autentication state (see {@link OncRpcAuthStatus}).
     */
    public OncRpcReplyMessage(OncRpcCallMessage call,
                              int replyStatus,
                              int acceptStatus, int rejectStatus,
                              int lowVersion, int highVersion,
                              int authStatus) {
        super(call.messageId);
        messageType       = OncRpcMessageType.ONCRPC_REPLY;
        this.replyStatus  = replyStatus;
        this.acceptStatus = acceptStatus;
        this.rejectStatus = rejectStatus;
        this.lowVersion   = lowVersion;
        this.highVersion  = highVersion;
        this.authStatus   = authStatus;
    }

    /**
     * Dummy, which can be used to identify unused parameters when constructing
     * <code>OncRpcReplyMessage</code> objects.
     */
    public static final int UNUSED_PARAMETER = 0;

}

// End of OncRpcReplyMessage.java
