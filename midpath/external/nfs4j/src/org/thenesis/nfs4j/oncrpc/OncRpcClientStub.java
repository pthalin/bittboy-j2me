/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcClientStub.java,v 1.1.1.1 2003/08/13 12:03:44 haraldalbrecht Exp $
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
import java.net.InetAddress;

/**
 * The abstract <code>OncRpcClientStub</code> class is the base class to
 * build ONC/RPC-program specific clients upon. This class is typically
 * only used by jrpcgen generated clients, which provide a particular
 * set of remote procedures as defined in a x-file.
 *
 * <p>When you do not need the client proxy object any longer, you should
 * return the resources it occupies to the system. Use the {@link #close}
 * method for this.
 *
 * <pre>
 * client.close();
 * client = null; // Hint to the garbage (wo)man
 * </pre>
 *
 * @see OncRpcTcpClient
 * @see OncRpcUdpClient
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:44 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class OncRpcClientStub {

    /**
     * Construct a new <code>OncRpcClientStub</code> for communication with
     * a remote ONC/RPC server.
     *
     * @param host Host address where the desired ONC/RPC server resides.
     * @param program Program number of the desired ONC/RPC server.
     * @param version Version number of the desired ONC/RPC server.
     * @param protocol {@link OncRpcProtocols Protocol} to be used for
     *   ONC/RPC calls. This information is necessary, so port lookups through
     *   the portmapper can be done.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcClientStub(InetAddress host, int program, int version,
                            int port, int protocol)
           throws OncRpcException, IOException {
        client = OncRpcClient.newOncRpcClient(host,
                                              program, version, port,
                                              protocol);
    }

    /**
     * Construct a new <code>OncRpcClientStub</code> which uses the given
     * client proxy object for communication with a remote ONC/RPC server.
     *
     * @param client ONC/RPC client proxy object implementing a particular
     *   IP protocol.
     */
    public OncRpcClientStub(OncRpcClient client)
           throws OncRpcException, IOException {
        this.client = client;
    }

    /**
     * Close the connection to an ONC/RPC server and free all network-related
     * resources. Well -- at least hope, that the Java VM will sometimes free
     * some resources. Sigh.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     */
    public void close()
           throws OncRpcException {
        if ( client != null ) {
            try {
                client.close();
            } finally {
                client = null;
            }
        }
    }

    /**
     * Returns ONC/RPC client proxy object used for communication with a
     * remote ONC/RPC server.
     *
     * @return ONC/RPC client proxy.
     */
    public OncRpcClient getClient() {
        return client;
    }

    /**
     * The real ONC/RPC client which is responsible for handling a particular
     * IP protocol.
     */
    protected OncRpcClient client;

}
// End of OncRpcClientStub.java
