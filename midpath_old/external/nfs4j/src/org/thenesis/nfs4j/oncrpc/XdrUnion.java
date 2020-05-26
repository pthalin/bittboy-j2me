/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/XdrUnion.java,v 1.1.1.1 2003/08/13 12:03:41 haraldalbrecht Exp $
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
import java.io.InvalidClassException;

/**
 * The abstract base class <code>XdrUnion</code> helps (de-)serializing
 * polymorphic classes. This class should not be confused with C unions in
 * general. Instead <code>XdrUnion</code> is an object-oriented construct
 * which helps in deploying polymorphism. For examples on how to use this,
 * please take a look at the "ACPLTea Java Library" package, which is also
 * available from <code>www.acplt.org/ks</code>. As a sidenote, the
 * serialization scheme implemented by <code>XdrUnion</code> is only a question
 * of getting used to it: after serializing the type code of the polymorphic
 * class, the variant part is serialized first before the common part. This
 * behaviour stems from the ACPLT C++ Communication Library and has been
 * retained for compatibility reasons. As it doesn't hurt, you won't mind
 * anyway.
 *
 * <p>To use polymorphism with XDR streams, you'll have to derive your own base
 * class (let's call it <code>foo</code> from <code>XdrUnion</code>
 * and implement the two methods
 * {@link #xdrEncodeCommon(XdrEncodingStream)} and
 * {@link #xdrDecodeCommon(XdrDecodingStream)}. Do not overwrite
 * the methods xdrEncode and xdrDecode!
 *
 * <p>Then, in your <code>foo</code>-derived classes, like <code>bar</code>
 * and <code>baz</code>, implement the other two methods
 * {@link #xdrEncodeVariant(XdrEncodingStream)} and
 * {@link #xdrDecodeVariant(XdrDecodingStream)}. In addition, implement
 * {@link #getXdrTypeCode} to return an int, uniquely identifying your
 * class. Note that this identifier only needs to be unique within the scope
 * of your <code>foo</code> class.
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:41 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class XdrUnion implements XdrAble {

    /**
     * Returns the so-called type code which identifies a derived class when
     * encoded or decoded. Note that the type code is not globally unique, but
     * rather it is only unique within the derived classes of a direct descend
     * of XdrUnion. If <code>foo</code> is derived from <code>XdrUnion</code>
     * and <code>foo</code> is the base class for <code>bar</code> and
     * <code>baz</code>, then the type code needs only be unique between
     * <code>bar</code> and <code>baz</code>.
     *
     * @return Type code identifying an object's class when encoding or
     *   decoding the object into or from a XDR stream.
     */
    public abstract int getXdrTypeCode();

    /**
     * Encodes -- that is: serializes -- an object into a XDR stream in
     * compliance to RFC 1832.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        //
        // For historial reasons (read: "for dumb and pure idiotic reasons")
        // and compatibility with the ACPLT/KS C++ Communication Library we
        // encode/decode the variant part *first* before encoding/decoding
        // the common part.
        //
        xdr.xdrEncodeInt(getXdrTypeCode());
        xdrEncodeVariant(xdr);
        xdrEncodeCommon(xdr);
    }

    /**
     * Decodes -- that is: deserializes -- an object from a XDR stream in
     * compliance to RFC 1832.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        //
        // Make sure that when deserializing this object's state that
        // the stream provides state information indeed intended for this
        // particular class.
        //
        int xdrTypeCode = xdr.xdrDecodeInt();
        if ( xdrTypeCode != getXdrTypeCode() ) {
            throw(new InvalidClassException(this.getClass().getName(),
                                            "non-matching XDR type code received."));
        }
        //
        // For historial reasons (read: "for dumb and pure idiotic reasons")
        // and compatibility with the ACPLT/KS C++ Communication Library we
        // encode/decode the variant part *first* before encoding/decoding
        // the common part.
        //
        xdrDecodeVariant(xdr);
        xdrDecodeCommon(xdr);
    }

    /**
     * Encodes -- that is: serializes -- the common part of an object into
     * a XDR stream in compliance to RFC 1832. Note that the common part is
     * deserialized after the variant part.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrEncodeCommon(XdrEncodingStream xdr)
           throws OncRpcException, IOException;

    /**
     * Decodes -- that is: deserializes -- the common part of an object from
     * a XDR stream in compliance to RFC 1832. Note that the common part is
     * deserialized after the variant part.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrDecodeCommon(XdrDecodingStream xdr)
           throws OncRpcException, IOException;

    /**
     * Encodes -- that is: serializes -- the variant part of an object into
     * a XDR stream in compliance to RFC 1832. Note that the variant part is
     * deserialized before the common part.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrEncodeVariant(XdrEncodingStream xdr)
           throws OncRpcException, IOException;

    /**
     * Decodes -- that is: deserializes -- the variant part of an object from
     * a XDR stream in compliance to RFC 1832. Note that the variant part is
     * deserialized before the common part.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public abstract void xdrDecodeVariant(XdrDecodingStream xdr)
           throws OncRpcException, IOException;

}

// End of XdrUnion.java
