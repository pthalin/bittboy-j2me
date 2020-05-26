/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcMessage.java,v 1.2 2003/08/14 07:56:37 haraldalbrecht Exp $
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
 * The <code>OncRpcMessage</code> class is an abstract superclass for all
 * the message types ONC/RPC defines (well, an overwhelming count of two).
 * The only things common to all ONC/RPC messages are a message identifier
 * and the message type. All other things do not come in until derived
 * classes are introduced.
 *
 * @version $Revision: 1.2 $ $Date: 2003/08/14 07:56:37 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class OncRpcMessage {

    /**
     * Constructs  a new <code>OncRpcMessage</code> object with default
     * values: a given message type and no particular message identifier.
     */
    public OncRpcMessage(int messageId) {
        this.messageId = messageId;
        messageType = -1;
    }

    /**
     * The message id is used to identify matching ONC/RPC calls and
     * replies. This is typically choosen by the communication partner
     * sending a request. The matching reply then must have the same
     * message identifier, so the receiver can match calls and replies.
     */
    public int messageId;
    /**
     * The kind of ONC/RPC message, which can be either a call or a
     * reply. Can be one of the constants defined in {@link OncRpcMessageType}.
     *
     * @see OncRpcMessageType
     */
    public int messageType;

}

// End of OncRpcMessage.java
