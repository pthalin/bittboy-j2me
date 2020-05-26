/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcGetPortResult.java,v 1.1.1.1 2003/08/13 12:03:41 haraldalbrecht Exp $
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
 * The <code>OncRpcGetPortResult</code> class represents the result from
 * a PMAP_GETPORT remote procedure call to the ONC/RPC portmapper.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:41 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcGetPortResult implements XdrAble {

    /**
     * The port number of the ONC/RPC in question. This is the only interesting
     * piece of information in this class. Go live with it, you don't have
     * alternatives.
     */
    public int port;

    /**
     * Default constructor for initializing an <code>OncRpcGetPortParams</code>
     * result object. It sets the <code>port</code> member to a useless value.
     */
    public OncRpcGetPortResult() {
        port = 0;
    }

    /**
     * Encodes -- that is: serializes -- an <code>OncRpcGetPortParams</code>
     * object into a XDR stream.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(port);
    }

    /**
     * Decodes -- that is: deserializes -- an <code>OncRpcGetPortParams</code>
     * object from a XDR stream.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        port = xdr.xdrDecodeInt();
    }

}

// End of OncRpcGetPortResult.java

