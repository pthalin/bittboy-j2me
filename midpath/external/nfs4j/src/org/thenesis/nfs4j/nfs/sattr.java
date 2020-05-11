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

public class sattr implements XdrAble {
    public int mode;
    public int uid;
    public int gid;
    public int size;
    public nfstime atime;
    public nfstime mtime;

    public sattr() {
    }

    public sattr(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(mode);
        xdr.xdrEncodeInt(uid);
        xdr.xdrEncodeInt(gid);
        xdr.xdrEncodeInt(size);
        atime.xdrEncode(xdr);
        mtime.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        mode = xdr.xdrDecodeInt();
        uid = xdr.xdrDecodeInt();
        gid = xdr.xdrDecodeInt();
        size = xdr.xdrDecodeInt();
        atime = new nfstime(xdr);
        mtime = new nfstime(xdr);
    }

}
// End of sattr.java
