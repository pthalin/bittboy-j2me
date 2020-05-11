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

public class writeargs implements XdrAble {
    public nfs_fh file;
    public int beginoffset;
    public int offset;
    public int totalcount;
    public byte [] data;

    public writeargs() {
    }

    public writeargs(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        file.xdrEncode(xdr);
        xdr.xdrEncodeInt(beginoffset);
        xdr.xdrEncodeInt(offset);
        xdr.xdrEncodeInt(totalcount);
        xdr.xdrEncodeDynamicOpaque(data);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        file = new nfs_fh(xdr);
        beginoffset = xdr.xdrDecodeInt();
        offset = xdr.xdrDecodeInt();
        totalcount = xdr.xdrDecodeInt();
        data = xdr.xdrDecodeDynamicOpaque();
    }

}
// End of writeargs.java
