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

public class fattr implements XdrAble {
    public int type;
    public int mode;
    public int nlink;
    public int uid;
    public int gid;
    public int size;
    public int blocksize;
    public int rdev;
    public int blocks;
    public int fsid;
    public int fileid;
    public nfstime atime;
    public nfstime mtime;
    public nfstime ctime;

    public fattr() {
    }

    public fattr(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(type);
        xdr.xdrEncodeInt(mode);
        xdr.xdrEncodeInt(nlink);
        xdr.xdrEncodeInt(uid);
        xdr.xdrEncodeInt(gid);
        xdr.xdrEncodeInt(size);
        xdr.xdrEncodeInt(blocksize);
        xdr.xdrEncodeInt(rdev);
        xdr.xdrEncodeInt(blocks);
        xdr.xdrEncodeInt(fsid);
        xdr.xdrEncodeInt(fileid);
        atime.xdrEncode(xdr);
        mtime.xdrEncode(xdr);
        ctime.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        type = xdr.xdrDecodeInt();
        mode = xdr.xdrDecodeInt();
        nlink = xdr.xdrDecodeInt();
        uid = xdr.xdrDecodeInt();
        gid = xdr.xdrDecodeInt();
        size = xdr.xdrDecodeInt();
        blocksize = xdr.xdrDecodeInt();
        rdev = xdr.xdrDecodeInt();
        blocks = xdr.xdrDecodeInt();
        fsid = xdr.xdrDecodeInt();
        fileid = xdr.xdrDecodeInt();
        atime = new nfstime(xdr);
        mtime = new nfstime(xdr);
        ctime = new nfstime(xdr);
    }

}
// End of fattr.java
