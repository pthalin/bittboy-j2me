/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcMessageType.java,v 1.1.1.1 2003/08/13 12:03:41 haraldalbrecht Exp $
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
 * A collection of constants used for ONC/RPC messages to identify the
 * type of message. Currently, ONC/RPC messages can be either calls or
 * replies. Calls are sent by ONC/RPC clients to servers to call a remote
 * procedure (for you "ohohpies" that can be translated into the buzzword
 * "method"). A server then will answer with a corresponding reply message
 * (but not in the case of batched calls).
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:41 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcMessageType {

    /**
     * Identifies an ONC/RPC call. By a "call" a client request that a server
     * carries out a particular remote procedure.
     */
    public static final int ONCRPC_CALL = 0;

    /**
     * Identifies an ONC/RPC reply. A server responds with a "reply" after
     * a client has sent a "call" for a particular remote procedure, sending
     * back the results of calling that procedure.
     */
    public static final int ONCRPC_REPLY = 1;

}

// End of OncRpcMessageType.java
