/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcProtocols.java,v 1.1.1.1 2003/08/13 12:03:41 haraldalbrecht Exp $
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
 * A collection of protocol constants used by the ONC/RPC package. Each
 * constant defines one of the possible transport protocols, which can be
 * used for communication between ONC/RPC clients and servers.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:41 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcProtocols {

    /**
     * Use the UDP protocol of the IP (Internet Protocol) suite as the
     * network communication protocol for doing remote procedure calls.
     * This is the same as the IPPROTO_UDP definition from the famous
     * BSD socket API.
     */
    public static final int ONCRPC_UDP = 17;

    /**
     * Use the TCP protocol of the IP (Internet Protocol) suite as the
     * network communication protocol for doing remote procedure calls.
     * This is the same as the IPPROTO_TCP definition from the famous
     * BSD socket API.
     */
    public static final int ONCRPC_TCP = 6;

    /**
     * Use the HTTP application protocol for tunneling ONC remote procedure
     * calls. This is definetely not similiar to any definition in the
     * famous BSD socket API.
     */
    public static final int ONCRPC_HTTP = -42;

}

// End of OncRpcProtocols.java
