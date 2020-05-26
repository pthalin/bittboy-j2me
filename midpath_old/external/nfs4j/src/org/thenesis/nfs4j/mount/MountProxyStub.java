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
package org.thenesis.nfs4j.mount;
import java.io.IOException;
import java.net.InetAddress;

import org.thenesis.nfs4j.oncrpc.OncRpcClient;
import org.thenesis.nfs4j.oncrpc.OncRpcClientStub;
import org.thenesis.nfs4j.oncrpc.OncRpcException;
import org.thenesis.nfs4j.oncrpc.XdrVoid;

/**
 * The class <code>MountProxyStub</code> implements the client stub proxy
 * for the MOUNTPROG remote program. It provides method stubs
 * which, when called, in turn call the appropriate remote method (procedure).
 */
public class MountProxyStub extends OncRpcClientStub {

    /**
     * Constructs a <code>MountProxyStub</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public MountProxyStub(InetAddress host, int protocol)
           throws OncRpcException, IOException {
        super(host, mount.MOUNTPROG, 1, 0, protocol);
    }

    /**
     * Constructs a <code>MountProxyStub</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public MountProxyStub(InetAddress host, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, mount.MOUNTPROG, 1, port, protocol);
    }

    /**
     * Constructs a <code>MountProxyStub</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param client ONC/RPC client connection object implementing a particular
     *   protocol.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public MountProxyStub(OncRpcClient client)
           throws OncRpcException, IOException {
        super(client);
    }

    /**
     * Constructs a <code>MountProxyStub</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public MountProxyStub(InetAddress host, int program, int version, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, 0, protocol);
    }

    /**
     * Constructs a <code>MountProxyStub</code> client stub proxy object
     * from which the MOUNTPROG remote program can be accessed.
     * @param host Internet address of host where to contact the remote program.
     * @param program Remote program number.
     * @param version Remote program version number.
     * @param port Port number at host where the remote program can be reached.
     * @param protocol {@link org.thenesis.nfs4j.oncrpc.OncRpcProtocols Protocol} to be
     *   used for ONC/RPC calls.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public MountProxyStub(InetAddress host, int program, int version, int port, int protocol)
           throws OncRpcException, IOException {
        super(host, program, version, port, protocol);
    }

    /**
     * Call remote procedure MOUNTPROC_NULL_1.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void MOUNTPROC_NULL_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(mount.MOUNTPROC_NULL_1, mount.MOUNTVERS, args$, result$);
    }

    /**
     * Call remote procedure MOUNTPROC_MNT_1.
     * @param arg1 parameter (of type dirpath) to the remote procedure call.
     * @return Result from remote procedure call (of type fhstatus).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public fhstatus MOUNTPROC_MNT_1(dirpath arg1)
           throws OncRpcException, IOException {
        fhstatus result$ = new fhstatus();
        client.call(mount.MOUNTPROC_MNT_1, mount.MOUNTVERS, arg1, result$);
        return result$;
    }

    /**
     * Call remote procedure MOUNTPROC_DUMP_1.
     * @return Result from remote procedure call (of type mountlist).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public mountlist MOUNTPROC_DUMP_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        mountlist result$ = new mountlist();
        client.call(mount.MOUNTPROC_DUMP_1, mount.MOUNTVERS, args$, result$);
        return result$;
    }

    /**
     * Call remote procedure MOUNTPROC_UMNT_1.
     * @param arg1 parameter (of type dirpath) to the remote procedure call.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void MOUNTPROC_UMNT_1(dirpath arg1)
           throws OncRpcException, IOException {
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(mount.MOUNTPROC_UMNT_1, mount.MOUNTVERS, arg1, result$);
    }

    /**
     * Call remote procedure MOUNTPROC_UMNTALL_1.
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public void MOUNTPROC_UMNTALL_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        XdrVoid result$ = XdrVoid.XDR_VOID;
        client.call(mount.MOUNTPROC_UMNTALL_1, mount.MOUNTVERS, args$, result$);
    }

    /**
     * Call remote procedure MOUNTPROC_EXPORT_1.
     * @return Result from remote procedure call (of type exports).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public exports MOUNTPROC_EXPORT_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        exports result$ = new exports();
        client.call(mount.MOUNTPROC_EXPORT_1, mount.MOUNTVERS, args$, result$);
        return result$;
    }

    /**
     * Call remote procedure MOUNTPROC_EXPORTALL_1.
     * @return Result from remote procedure call (of type exports).
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public exports MOUNTPROC_EXPORTALL_1()
           throws OncRpcException, IOException {
        XdrVoid args$ = XdrVoid.XDR_VOID;
        exports result$ = new exports();
        client.call(mount.MOUNTPROC_EXPORTALL_1, mount.MOUNTVERS, args$, result$);
        return result$;
    }

}
// End of MountProxyStub.java
