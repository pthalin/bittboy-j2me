/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcUdpRetransmissionMode.java,v 1.1.1.1 2003/08/13 12:03:43 haraldalbrecht Exp $
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
 * A collection of constants used to identify the retransmission schemes
 * when using {@link OncRpcUdpClient UDP/IP-based ONC/RPC clients}.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:43 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface OncRpcUdpRetransmissionMode {

    /**
     * In exponentional back-off retransmission mode, UDP/IP-based ONC/RPC
     * clients first wait a given retransmission timeout period before
     * sending the ONC/RPC call again. The retransmission timeout then is
     * doubled on each try.
     */
    public static final int EXPONENTIAL = 0;

    /**
     * In fixed retransmission mode, UDP/IP-based ONC/RPC clients wait a
     * given retransmission timeout period before send the ONC/RPC call again.
     * The retransmission timeout is not changed between consecutive tries
     * but is fixed instead.
     */
    public static final int FIXED = 1;

}
// End of OncRpcUdpRetransmissionMode.java

