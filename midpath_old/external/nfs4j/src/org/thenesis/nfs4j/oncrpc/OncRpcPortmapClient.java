/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcPortmapClient.java,v 1.1.1.1 2003/08/13 12:03:41 haraldalbrecht Exp $
 *
 * NFS4J - Copyright (C) 2007 Guillaume Legris, Mathieu Legris
 *
 * Copyright (c) 1999, 2000, 2001, 2002
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
 * The class <code>OncRpcPortmapClient</code> is a specialized ONC/RPC client,
 * which can talk to the portmapper on a given host using the famous
 * UDP/IP datagram-oriented internet protocol. In addition, it is also possible
 * to contact portmappers using TCP/IP. For this, the constructor of the
 * <code>OncRpcPortmapClient</code> class also accepts a protocol parameter
 * ({@link OncRpcPortmapClient#OncRpcPortmapClient(InetAddress, int)}).
 *
 * Technically spoken, instances of <code>OncRpcPortmapClient</code> are proxy objects.
 * <code>OncRpcPortmapClient</code> objects currently speak protocol version
 * 2. The newer transport-independent protocol versions 3 and 4 are
 * <b>not</b> supported as the transport-independent ONC/RPC implementation is not
 * that widely in use due to the brain-damaged design of XTI. If you should
 * ever have programmed using XTI (transport independent interface) then you'll
 * know what I mean and probably agree with me. Otherwise, in case you find XTI
 * the best thing since the Win32 API, please implement the rpcbind protocol
 * versions 3 and 4 and give it to the community -- thank you.
 *
 * <p>Here are some simple examples of how to use the portmapper proxy object.
 * We first start with one of the most interesting operations, which can be
 * performed on portmappers, querying the port of a local or remote ONC/RPC
 * server.
 *
 * <p>To query the port number of an ONC/RPC server, we need to contact the
 * portmapper at the host machine where the server is running. The following
 * code snippet just contacts the local portmapper. <code>try</code> blocks
 * are ommited for brevity -- but remember that you almost allways need to catch
 * {@link OncRpcException} as well as <code>IOException</code>.
 *
 * <pre>
 * OncRpcPortmapClient portmap =
 *     new OncRpcPortmapClient(InetAddress.getByName("localhost"));
 * </pre>
 *
 * <p>With the portmapper proxy object in our hands we can now ask for the port
 * number of a particular ONC/RPC server. In this (ficious) example we ask for
 * the ONC/RPC program (server) number <code>0x49678</code> (by coincidence this
 * happens to be the program number of the <a href="http://www.acplt.org/ks">ACPLT/KS</a>
 * protocol). To ask for the port number of a given program number, use the
 * {@link OncRpcPortmapClient#getPort(int, int, int) getPort(...)} method.
 *
 * <pre>
 * int port;
 * try {
 *     port = portmap.getPort(0x49678, 1, OncRpcProtocols.ONCRPC_UDP);
 * } catch ( OncRpcProgramNotRegisteredException e ) {
 *     System.out.println("ONC/RPC program server not found");
 *     System.exit(0);
 * } catch ( OncRpcException e ) {
 *     System.out.println("Could not contact portmapper:");
 *     e.printStackTrace(System.out);
 *     System.exit(0);
 * }
 * System.out.println("Program available at port " + port);
 * </pre>
 *
 * <p>In the call to {@link OncRpcPortmapClient#getPort(int, int, int) getPort(...)}, the
 * first parameter specifies the ONC/RPC program number, the secondm parameter
 * specifies the program's version number, and the third parameter specifies
 * the IP protocol to use when issueing ONC/RPC calls. Currently, only
 * {@link OncRpcProtocols#ONCRPC_UDP} and {@link OncRpcProtocols#ONCRPC_TCP} are
 * supported. But who needs other protocols anyway?!
 *
 * <p>In case {@link OncRpcPortmapClient#getPort(int, int, int) getPort(...)} succeeds, it
 * returns the number of the port where the appropriate ONC/RPC server waits
 * for incoming ONC/RPC calls. If the ONC/RPC program is not registered with
 * the particular ONC/RPC portmapper, an {@link OncRpcProgramNotRegisteredException}
 * is thrown (which is a subclass of {@link OncRpcException} with a detail
 * reason of {@link OncRpcException#RPC_PROGNOTREGISTERED}.
 *
 * <p>A second typical example of how to use the portmapper is retrieving a
 * list of the currently registered servers. We use the
 * {@link OncRpcPortmapClient#listServers} method for this purpose in the
 * following example, and print the list we got.
 *
 * <pre>
 * OncRpcServerIdent [] list = null;
 * try {
 *     list = portmap.listServers();
 * } catch ( OncRpcException e ) {
 *     e.printStackTrace(System.out);
 *     System.exit(20);
 * }
 * for ( int i = 0; i < list.length; ++i ) {
 *     System.out.println(list[i].program + " " + list[i].version + " "
 *                        + list[i].protocol + " " + list[i].port);
 * }
 * </pre>
 *
 * <p>When you do not need the client proxy object any longer, you should
 * return the resources it occupies to the system. Use the {@link #close}
 * method for this.
 *
 * <pre>
 * portmap.close();
 * portmap = null; // Hint to the garbage (wo)man
 * </pre>
 *
 * <p>For another code example, please consult
 * <code>src/tests/org/acplt/oncrpc/PortmapGetPortTest.java</code>.
 *
 * @see OncRpcClient
 *
 * @version $Revision: 1.1.1.1 $ $Date: 2003/08/13 12:03:41 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcPortmapClient {

    /**
     * Constructs and initializes an ONC/RPC client object, which can
     * communicate with the portmapper at the specified host using the
     * UDP/IP datagram-oriented internet protocol.
     *
     * @param host Host where to contact the portmapper.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcPortmapClient(InetAddress host)
        throws OncRpcException, IOException {
        this(host, OncRpcProtocols.ONCRPC_UDP, 0);
    }

    /**
     * Constructs and initializes an ONC/RPC client object, which can
     * communicate with the portmapper at the given host using the
     * speicified protocol.
     *
     * @param host Host where to contact the portmapper.
     * @param protocol Protocol to use for contacting the portmapper. This
     *        can be either <code>OncRpcProtocols.ONCRPC_UDP</code> or
     *        <code>OncRpcProtocols.ONCRPC_TCP</code> (HTTP is currently
     *        not supported).
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcPortmapClient(InetAddress host, int protocol)
        throws OncRpcException, IOException {
        this(host, protocol, -1);
    }

    /**
     * Constructs and initializes an ONC/RPC client object, which can
     * communicate with the portmapper at the given host using the
     * speicified protocol.
     *
     * @param host Host where to contact the portmapper.
     * @param protocol Protocol to use for contacting the portmapper. This
     *        can be either <code>OncRpcProtocols.ONCRPC_UDP</code> or
     *        <code>OncRpcProtocols.ONCRPC_TCP</code> (HTTP is currently
     *        not supported).
     * @param timeout Timeout in milliseconds for connection operation. This
     *        parameter applies only when using TCP/IP for talking to the
     *        portmapper. A negative timeout indicates that the
     *        implementation-specific timeout setting of the JVM and java.net
     *        implementation should be used instead.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public OncRpcPortmapClient(InetAddress host, int protocol, int timeout)
        throws OncRpcException, IOException {
        switch ( protocol ) {
        case OncRpcProtocols.ONCRPC_UDP:
            portmapClient = new OncRpcUdpClient(host, PMAP_PROGRAM,
                                                      PMAP_VERSION,
                                                      PMAP_PORT);
            break;
        case OncRpcProtocols.ONCRPC_TCP:
            portmapClient = new OncRpcTcpClient(host, PMAP_PROGRAM,
                                                      PMAP_VERSION,
                                                      PMAP_PORT,
                                                      0, // default buff size
                                                      timeout);
            break;
        default:
            throw(new OncRpcException(OncRpcException.RPC_UNKNOWNPROTO));
        }
    }

    /**
     * Closes the connection to the portmapper.
     *
     * @throws OncRpcException
     */
    public void close()
         throws OncRpcException {
        portmapClient.close();
    }

    /**
     * Returns the client proxy object used for communicating with the
     * portmapper.
     *
     * @return portmap client proxy object (subclass of <code>OncRpcClient</code>).
     */
    public OncRpcClient getOncRpcClient() {
        return portmapClient;
    }

    /**
     * Asks the portmapper this <code>OncRpcPortmapClient</code> object is
     * a proxy for, for the port number of a particular ONC/RPC server
     * identified by the information tuple {program number, program version,
     * protocol}.
     *
     * @param program Program number of the remote procedure call in question.
     * @param version Program version number.
     * @param protocol Protocol lateron used for communication with the
     *   ONC/RPC server in question. This can be one of the protocols constants
     *   defined in the {@link OncRpcProtocols} interface.
     *
     * @return port number of ONC/RPC server in question.
     *
     * @throws OncRpcException if the portmapper is not available (detail is
     *   {@link OncRpcException#RPC_PMAPFAILURE}).
     * @throws OncRpcProgramNotRegisteredException if the requested program
     *   is not available.
     */
    public int getPort(int program, int version, int protocol)
        throws OncRpcException {
        //
        // Fill in the request parameters. Note that params.port is
        // not used. BTW - it is automatically initialized as 0 by the
        // constructor of the OncRpcServerParams class.
        //
        OncRpcServerIdent params =
            new OncRpcServerIdent(program, version, protocol, 0);
        OncRpcGetPortResult result = new OncRpcGetPortResult();
        //
        // Try to contact the portmap process. If something goes "boing"
        // at this stage, then rethrow the exception as a generic portmap
        // failure exception. Otherwise, if the port number returned is
        // zero, then no appropriate server was found. In this case,
        // throw an exception, that the program requested could not be
        // found.
        //
        try {
            portmapClient.call(OncRpcPortmapServices.PMAP_GETPORT, params, result);
        } catch ( OncRpcException e ) {
            throw(new OncRpcException(OncRpcException.RPC_PMAPFAILURE));
        }
        //
        // In case the program is not registered, throw an exception too.
        //
        if ( result.port == 0 ) {
            throw(new OncRpcProgramNotRegisteredException());
        }
        return result.port;
    }

    /**
     * Register an ONC/RPC with the given program number, version and protocol
     * at the given port with the portmapper.
     *
     * @param program The number of the program to be registered.
     * @param version The version number of the program.
     * @param protocol The protocol spoken by the ONC/RPC server. Can be one
     *   of the {@link OncRpcProtocols} constants.
     * @param port The port number where the ONC/RPC server can be reached.
     *
     * @return Indicates whether registration succeeded (<code>true</code>) or
     *   was denied by the portmapper (<code>false</code>).
     *
     * @throws OncRpcException if the portmapper is not available (detail is
     *   {@link OncRpcException#RPC_PMAPFAILURE}).
     */
    public boolean setPort(int program, int version, int protocol, int port)
        throws OncRpcException {
        //
        // Fill in the request parameters.
        //
        OncRpcServerIdent params =
            new OncRpcServerIdent(program, version, protocol, port);
        XdrBoolean result = new XdrBoolean(false);
        //
        // Try to contact the portmap process. If something goes "boing"
        // at this stage, then rethrow the exception as a generic portmap
        // failure exception.
        //
        try {
            portmapClient.call(OncRpcPortmapServices.PMAP_SET, params, result);
        } catch ( OncRpcException e ) {
            throw(new OncRpcException(OncRpcException.RPC_PMAPFAILURE));
        }
        return result.booleanValue();
    }

    /**
     * Unregister an ONC/RPC with the given program number and version. The
     * portmapper will remove all entries with the same program number and
     * version, regardless of the protocol and port number.
     *
     * @param program The number of the program to be unregistered.
     * @param version The version number of the program.
     *
     * @return Indicates whether deregistration succeeded (<code>true</code>)
     *   or was denied by the portmapper (<code>false</code>).
     *
     * @throws OncRpcException if the portmapper is not available (detail is
     *   {@link OncRpcException#RPC_PMAPFAILURE}).
     */
    public boolean unsetPort(int program, int version)
        throws OncRpcException {
        //
        // Fill in the request parameters.
        //
        OncRpcServerIdent params =
            new OncRpcServerIdent(program, version, 0, 0);
        XdrBoolean result = new XdrBoolean(false);
        //
        // Try to contact the portmap process. If something goes "boing"
        // at this stage, then rethrow the exception as a generic portmap
        // failure exception.
        //
        try {
            portmapClient.call(OncRpcPortmapServices.PMAP_UNSET, params, result);
        } catch ( OncRpcException e ) {
            throw(new OncRpcException(OncRpcException.RPC_PMAPFAILURE));
        }
        return result.booleanValue();
    }

    /**
     * Retrieves a list of all registered ONC/RPC servers at the same host
     * as the contacted portmapper.
     *
     * @return vector of server descriptions (see
     *   class {@link OncRpcServerIdent}).
     *
     * @throws OncRpcException if the portmapper is not available (detail is
     *   {@link OncRpcException#RPC_PMAPFAILURE}).
     */
    public OncRpcServerIdent [] listServers()
           throws OncRpcException {
        //
        // Fill in the request parameters.
        //
        OncRpcDumpResult result = new OncRpcDumpResult();
        //
        // Try to contact the portmap process. If something goes "boing"
        // at this stage, then rethrow the exception as a generic portmap
        // failure exception.
        //
        try {
            portmapClient.call(OncRpcPortmapServices.PMAP_DUMP, XdrVoid.XDR_VOID, result);
        } catch ( OncRpcException e ) {
            throw(new OncRpcException(OncRpcException.RPC_PMAPFAILURE));
        }
        //
        // Copy the server ident object references from the Vector
        // into the vector (array).
        //
        OncRpcServerIdent [] info =
            new OncRpcServerIdent[result.servers.size()];
        result.servers.copyInto(info);
        return info;
    }

    /**
     * Ping the portmapper (try to call procedure 0).
     *
     * @throws OncRpcException if the portmapper is not available (detail is
     *   {@link OncRpcException#RPC_PMAPFAILURE}).
     */
    public void ping()
           throws OncRpcException {
        try {
            portmapClient.call(0, XdrVoid.XDR_VOID, XdrVoid.XDR_VOID);
        } catch ( OncRpcException e ) {
            throw(new OncRpcException(OncRpcException.RPC_PMAPFAILURE));
        }
    }

    /**
     * Well-known port where the portmap process can be found on Internet hosts.
     */
    public static final int PMAP_PORT    = 111;

    /**
     * Program number of the portmapper as defined in RFC 1832.
     */
    public static final int PMAP_PROGRAM = 100000;

    /**
     * Program version number of the portmapper as defined in RFC 1832.
     */
    public static final int PMAP_VERSION = 2;

    /**
     * The particular transport-specific ONC/RPC client object used for
     * talking to the portmapper.
     */
    protected OncRpcClient portmapClient;

} // public class OncRpcPortmapClient

// End of OncRpcPortmapClient.java
