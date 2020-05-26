/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcDumpResult.java,v 1.1.1.1 2003/08/13 12:03:41 haraldalbrecht Exp $
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
import java.util.Vector;

/**
 * Objects of class <code>OncRpcDumpResult</code> represent the outcome of
 * the PMAP_DUMP operation on a portmapper. <code>OncRpcDumpResult</code>s are
 * (de-)serializeable, so they can be flushed down XDR streams.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:41 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcDumpResult implements XdrAble {

    /**
     * Vector of server ident objects describing the currently registered
     * ONC/RPC servers (also known as "programmes").
     */
    public Vector servers;

    /**
     * Initialize an <code>OncRpcServerIdent</code> object. Afterwards, the
     * <code>servers</code> field is initialized to contain no elements.
     */
    public OncRpcDumpResult() {
        servers = new Vector();
    }

    /**
     * Encodes -- that is: serializes -- the result of a PMAP_DUMP operationg
     * into a XDR stream.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException
    {
        if ( servers == null ) {
            xdr.xdrEncodeBoolean(false);
        } else {
            //
            // Now encode all server ident objects into the xdr stream. Each
            // object is preceeded by a boolan, which indicates to the receiver
            // whether an object follows. After the last object has been
            // encoded the receiver will find a boolean false in the stream.
            //
            int count = servers.size();
            int index = 0;
            while ( count > 0 ) {
                xdr.xdrEncodeBoolean(true);
                ((XdrAble) servers.elementAt(index)).xdrEncode(xdr);
                index++;
                count--;
            }
            xdr.xdrEncodeBoolean(false);
        }
    }

    /**
     * Decodes -- that is: deserializes -- the result from a PMAP_DUMP remote
     * procedure call from a XDR stream.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException
    {
        //
        // Calling removeAllElements() instead of clear() preserves
        // pre-JDK2 compatibility.
        //
        servers.removeAllElements();
        //
        // Pull the server ident object off the xdr stream. Each object is
        // preceeded by a boolean value indicating whether there is still an
        // object in the pipe.
        //
        while ( xdr.xdrDecodeBoolean() ) {
            servers.addElement(new OncRpcServerIdent(xdr));
        }
    }

}

// End of OncRpcDumpResult.java
