/*
 * NFS4J - Copyright (C) 2007 Guillaume Legris, Mathieu Legris
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.thenesis.nfs4j.nfs;
import java.io.IOException;

import org.thenesis.nfs4j.oncrpc.OncRpcException;
import org.thenesis.nfs4j.oncrpc.XdrAble;
import org.thenesis.nfs4j.oncrpc.XdrDecodingStream;
import org.thenesis.nfs4j.oncrpc.XdrEncodingStream;

public class entry implements XdrAble {
    public int fileid;
    public filename name;
    public nfscookie cookie;
    public entry nextentry;

    public entry() {
    }

    public entry(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        entry $this = this;
        do {
            xdr.xdrEncodeInt($this.fileid);
            $this.name.xdrEncode(xdr);
            $this.cookie.xdrEncode(xdr);
            $this = $this.nextentry;
            xdr.xdrEncodeBoolean($this != null);
        } while ( $this != null );
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        entry $this = this;
        entry $next;
        do {
            $this.fileid = xdr.xdrDecodeInt();
            $this.name = new filename(xdr);
            $this.cookie = new nfscookie(xdr);
            $next = xdr.xdrDecodeBoolean() ? new entry() : null;
            $this.nextentry = $next;
            $this = $next;
        } while ( $this != null );
    }

}
// End of entry.java
