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
import java.net.InetAddress;

import org.thenesis.nfs4j.oncrpc.OncRpcClient;
import org.thenesis.nfs4j.oncrpc.OncRpcClientStub;
import org.thenesis.nfs4j.oncrpc.OncRpcException;
import org.thenesis.nfs4j.oncrpc.XdrInt;
import org.thenesis.nfs4j.oncrpc.XdrVoid;

/**
 * The class <code>NFSProxyStub</code> implements the client stub proxy
 * for the NFS_PROGRAM remote program. It provides method stubs
 * which, when called, in turn call the appropriate remote method (procedure).
 */
public class NFSProxyStub extends OncRpcClientStub {

    /**
     * Constructs a <code>NFSProxyStub</code> client stub proxy object
     * from which the NFS_PROGRAM remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public NFSProxyStub(InetAddress host, int protocol)
           throws OncRpcException, IOException {
        super(host, nfs_prot.NFS_PROGRAM, 2, 0, protocol);
    }

    /**
     * Constructs a <code>NFSProxyStub</code> client stub proxy object
     * from which the NFS_PROGRAM remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public NFSProxyStub(InetAddress host, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, nfs_prot.NFS_PROGRAM, 2, port, protocol);
    }

    /**
     * Constructs a <code>NFSProxyStub</code> client stub proxy object
     * from which the NFS_PROGRAM remote program can be accessed.
     * @param client ONC/RPC client connection object implementing a particular
     *   protocol.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public NFSProxyStub(OncRpcClient client)
           throws OncRpcException, IOException {
        super(client);
    }

    /**
     * Constructs a <code>NFSProxyStub</code> client stub proxy object
     * from which the NFS_PROGRAM remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public NFSProxyStub(InetAddress host, int program, int version, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, 0, protocol);
    }

    /**
     * Constructs a <code>NFSProxyStub</code> client stub proxy object
     * from which the NFS_PROGRAM remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public NFSProxyStub(InetAddress host, int program, int version, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, port, protocol);
    }

    /**
     * Call remote procedure NFSPROC_NULL_2.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void NFSPROC_NULL_2()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(nfs_prot.NFSPROC_NULL_2, nfs_prot.NFS_VERSION, args$, result$);
    }

    /**
     * Call remote procedure NFSPROC_GETATTR_2.
     * @param arg1 parameter (of type nfs_fh) to the remote procedure call.
     * @return Result from remote procedure call (of type attrstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public attrstat NFSPROC_GETATTR_2(nfs_fh arg1)
           throws OncRpcException, IOException {
        attrstat result$ = new attrstat();
        client.call(nfs_prot.NFSPROC_GETATTR_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_SETATTR_2.
     * @param arg1 parameter (of type sattrargs) to the remote procedure call.
     * @return Result from remote procedure call (of type attrstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public attrstat NFSPROC_SETATTR_2(sattrargs arg1)
           throws OncRpcException, IOException {
        attrstat result$ = new attrstat();
        client.call(nfs_prot.NFSPROC_SETATTR_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_ROOT_2.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void NFSPROC_ROOT_2()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(nfs_prot.NFSPROC_ROOT_2, nfs_prot.NFS_VERSION, args$, result$);
    }

    /**
     * Call remote procedure NFSPROC_LOOKUP_2.
     * @param arg1 parameter (of type diropargs) to the remote procedure call.
     * @return Result from remote procedure call (of type diropres).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public diropres NFSPROC_LOOKUP_2(diropargs arg1)
           throws OncRpcException, IOException {
        diropres result$ = new diropres();
        client.call(nfs_prot.NFSPROC_LOOKUP_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_READLINK_2.
     * @param arg1 parameter (of type nfs_fh) to the remote procedure call.
     * @return Result from remote procedure call (of type readlinkres).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public readlinkres NFSPROC_READLINK_2(nfs_fh arg1)
           throws OncRpcException, IOException {
        readlinkres result$ = new readlinkres();
        client.call(nfs_prot.NFSPROC_READLINK_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_READ_2.
     * @param arg1 parameter (of type readargs) to the remote procedure call.
     * @return Result from remote procedure call (of type readres).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public readres NFSPROC_READ_2(readargs arg1)
           throws OncRpcException, IOException {
        readres result$ = new readres();
        client.call(nfs_prot.NFSPROC_READ_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_WRITECACHE_2.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void NFSPROC_WRITECACHE_2()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(nfs_prot.NFSPROC_WRITECACHE_2, nfs_prot.NFS_VERSION, args$, result$);
    }

    /**
     * Call remote procedure NFSPROC_WRITE_2.
     * @param arg1 parameter (of type writeargs) to the remote procedure call.
     * @return Result from remote procedure call (of type attrstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public attrstat NFSPROC_WRITE_2(writeargs arg1)
           throws OncRpcException, IOException {
        attrstat result$ = new attrstat();
        client.call(nfs_prot.NFSPROC_WRITE_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_CREATE_2.
     * @param arg1 parameter (of type createargs) to the remote procedure call.
     * @return Result from remote procedure call (of type diropres).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public diropres NFSPROC_CREATE_2(createargs arg1)
           throws OncRpcException, IOException {
        diropres result$ = new diropres();
        client.call(nfs_prot.NFSPROC_CREATE_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_REMOVE_2.
     * @param arg1 parameter (of type diropargs) to the remote procedure call.
     * @return Result from remote procedure call (of type nfsstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public int NFSPROC_REMOVE_2(diropargs arg1)
           throws OncRpcException, IOException {
        XdrInt result$ = new XdrInt();
        client.call(nfs_prot.NFSPROC_REMOVE_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$.intValue();
    }

    /**
     * Call remote procedure NFSPROC_RENAME_2.
     * @param arg1 parameter (of type renameargs) to the remote procedure call.
     * @return Result from remote procedure call (of type nfsstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public int NFSPROC_RENAME_2(renameargs arg1)
           throws OncRpcException, IOException {
        XdrInt result$ = new XdrInt();
        client.call(nfs_prot.NFSPROC_RENAME_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$.intValue();
    }

    /**
     * Call remote procedure NFSPROC_LINK_2.
     * @param arg1 parameter (of type linkargs) to the remote procedure call.
     * @return Result from remote procedure call (of type nfsstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public int NFSPROC_LINK_2(linkargs arg1)
           throws OncRpcException, IOException {
        XdrInt result$ = new XdrInt();
        client.call(nfs_prot.NFSPROC_LINK_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$.intValue();
    }

    /**
     * Call remote procedure NFSPROC_SYMLINK_2.
     * @param arg1 parameter (of type symlinkargs) to the remote procedure call.
     * @return Result from remote procedure call (of type nfsstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public int NFSPROC_SYMLINK_2(symlinkargs arg1)
           throws OncRpcException, IOException {
        XdrInt result$ = new XdrInt();
        client.call(nfs_prot.NFSPROC_SYMLINK_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$.intValue();
    }

    /**
     * Call remote procedure NFSPROC_MKDIR_2.
     * @param arg1 parameter (of type createargs) to the remote procedure call.
     * @return Result from remote procedure call (of type diropres).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public diropres NFSPROC_MKDIR_2(createargs arg1)
           throws OncRpcException, IOException {
        diropres result$ = new diropres();
        client.call(nfs_prot.NFSPROC_MKDIR_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_RMDIR_2.
     * @param arg1 parameter (of type diropargs) to the remote procedure call.
     * @return Result from remote procedure call (of type nfsstat).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public int NFSPROC_RMDIR_2(diropargs arg1)
           throws OncRpcException, IOException {
        XdrInt result$ = new XdrInt();
        client.call(nfs_prot.NFSPROC_RMDIR_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$.intValue();
    }

    /**
     * Call remote procedure NFSPROC_READDIR_2.
     * @param arg1 parameter (of type readdirargs) to the remote procedure call.
     * @return Result from remote procedure call (of type readdirres).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public readdirres NFSPROC_READDIR_2(readdirargs arg1)
           throws OncRpcException, IOException {
        readdirres result$ = new readdirres();
        client.call(nfs_prot.NFSPROC_READDIR_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure NFSPROC_STATFS_2.
     * @param arg1 parameter (of type nfs_fh) to the remote procedure call.
     * @return Result from remote procedure call (of type statfsres).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public statfsres NFSPROC_STATFS_2(nfs_fh arg1)
           throws OncRpcException, IOException {
        statfsres result$ = new statfsres();
        client.call(nfs_prot.NFSPROC_STATFS_2, nfs_prot.NFS_VERSION, arg1, result$);
        return result$;
    }

}
// End of NFSProxyStub.java
