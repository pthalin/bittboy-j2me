/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcCallMessage.java,v 1.2 2003/08/14 07:55:07 haraldalbrecht Exp $
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
 * The <code>OncRpcCallMessage</code> class represents a remote procedure call
 * message as defined by ONC/RPC in RFC 1831. Such messages are sent by ONC/RPC
 * clients to servers in order to request a remote procedure call.
 *
 * <p>Note that this is an abstract class. Because call message objects also
 * need to deal with authentication protocol issues, they need help of so-called
 * authentication protocol handling objects. These objects are of different
 * classes, depending on where they are used (either within the server or
 * the client).
 *
 * <p>Please also note that this class implements no encoding or decoding
 * functionality: it doesn't need them. Only derived classes will be able
 * to be encoded on the side of the client and decoded at the end of the
 * server.
 *
 * @version $Revision: 1.2 $ $Date: 2003/08/14 07:55:07 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class OncRpcCallMessage extends OncRpcMessage {

    /**
     * Protocol version used by this ONC/RPC Java implementation. The protocol
     * version 2 is defined in RFC 1831.
     */
    public final static int ONCRPC_VERSION = 2;

    /**
     * Protocol version used by this ONC/RPC call message.
     */
    public int oncRpcVersion;

    /**
     * Program number of this particular remote procedure call message.
     */
    public int program;

    /**
     * Program version number of this particular remote procedure call message.
     */
    public int version;

    /**
     * Number (identifier) of remote procedure to call.
     */
    public int procedure;

    /**
     * Constructs and initialises a new ONC/RPC call message header.
     *
     * @param messageId An identifier choosen by an ONC/RPC client to uniquely
     *   identify matching call and reply messages.
     * @param program Program number of the remote procedure to call.
     * @param version Program version number of the remote procedure to call.
     * @param procedure Procedure number (identifier) of the procedure to call.
     */
    public OncRpcCallMessage(int messageId, int program,
                             int version, int procedure) {
        super(messageId);
        messageType    = OncRpcMessageType.ONCRPC_CALL;
        oncRpcVersion  = ONCRPC_VERSION;
        this.program   = program;
        this.version   = version;
        this.procedure = procedure;
    }

    /**
     * Constructs a new (incompletely initialized) ONC/RPC call message header.
     * The <code>messageType</code> is set to
     * {@link OncRpcMessageType#ONCRPC_CALL} and the <code>oncRpcVersion</code>
     * is set to {@link #ONCRPC_VERSION}.
     */
    public OncRpcCallMessage() {
        super(0);
        messageType    = OncRpcMessageType.ONCRPC_CALL;
        oncRpcVersion  = ONCRPC_VERSION;
        this.program   = 0;
        this.version   = 0;
        this.procedure = 0;
    }

}

// End of OncRpcCallMessage.java
