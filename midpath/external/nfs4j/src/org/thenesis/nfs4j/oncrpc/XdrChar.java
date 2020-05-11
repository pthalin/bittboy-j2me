/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrChar.java,v 1.1.1.1 2003/08/13 12:03:44 haraldalbrecht Exp $
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
 * Instances of the class <code>XdrChar</code> represent (de-)serializeable
 * chars, which are especially useful in cases where a result with only a
 * single char is expected from a remote function call or only a single
 * char parameter needs to be supplied.
 *
 * <p>Please note that this class is somewhat modelled after Java's primitive
 * data type wrappers. As for these classes, the XDR data type wrapper classes
 * follow the concept of values with no identity, so you are not allowed to
 * change the value after you've created a value object.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:44 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class XdrChar implements XdrAble {

    /**
     * Constructs and initializes a new <code>XdrChar</code> object.
     *
     * @param value Char value.
     */
    public XdrChar(char value) {
        this.value = value;
    }

    /**
     * Constructs and initializes a new <code>XdrChar</code> object.
     */
    public XdrChar() {
        this.value = 0;
    }

    /**
     * Returns the value of this <code>XdrChar</code> object as a char
     * primitive.
     *
     * @return  The primitive <code>char</code> value of this object.
     */
    public char charValue() {
        return this.value;
    }

    /**
     * Encodes -- that is: serializes -- a XDR char into a XDR stream in
     * compliance to RFC 1832.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException
    {
        xdr.xdrEncodeByte((byte)value);
    }

    /**
     * Decodes -- that is: deserializes -- a XDR char from a XDR stream in
     * compliance to RFC 1832.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException
    {
        value = (char) xdr.xdrDecodeByte();
    }

    /**
     * The encapsulated char value itself.
     */
    private char value;

}

// End of XdrChar.java
