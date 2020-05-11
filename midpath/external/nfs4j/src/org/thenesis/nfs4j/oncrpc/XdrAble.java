/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrAble.java,v 1.1.1.1 2003/08/13 12:03:43 haraldalbrecht Exp $
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
 * Defines the interface for all classes that should be able to be
 * serialized into XDR streams, and deserialized or constructed from
 * XDR streams.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:43 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public interface XdrAble {

    /**
     * Encodes -- that is: serializes -- an object into a XDR stream in
     * compliance to RFC 1832.
     *
     * @param xdr XDR stream to which information is sent for encoding.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException;

    /**
     * Decodes -- that is: deserializes -- an object from a XDR stream in
     * compliance to RFC 1832.
     *
     * @param xdr XDR stream from which decoded information is retrieved.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException;

}

// End of XdrAble.java
