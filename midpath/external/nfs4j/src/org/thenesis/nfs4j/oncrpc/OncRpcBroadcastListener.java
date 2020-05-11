/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcBroadcastListener.java,v 1.1.1.1 2003/08/13 12:03:40 haraldalbrecht Exp $
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
 * The listener class for {@link OncRpcBroadcastListener receiving}
 * {@link OncRpcBroadcastEvent ONC/RPC broadcast reply events}.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:40 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcBroadcastListener {

    /**
     * Invoked when a reply to an ONC/RPC broadcast call is received.
     *
     * <p>Please note that you should not spend too much time when handling
     * broadcast events, otherwise you'll probably miss some of the incomming
     * replies. Because most operating systems will not buffer large amount
     * of incomming UDP/IP datagramms for a given socket, you will experience
     * packet drops when you stay too long in the processing stage.
     *
     * @see OncRpcBroadcastEvent
     */
    public void replyReceived(OncRpcBroadcastEvent evt);

}

// End of OncRpcBroadcastListener.java

