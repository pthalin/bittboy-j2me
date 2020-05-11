/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcClient.java,v 1.3 2003/08/14 13:48:33 haraldalbrecht Exp $
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
 * The abstract <code>OncRpcClient</code> class is the foundation for
 * protcol-specific ONC/RPC clients. It encapsulates protocol-independent
 * functionality, like port resolving, if no port was specified for the
 * ONC/RPC server to contact. This class also provides the method skeleton,
 * for instance for executing procedure calls.
 *
 * <p>In order to communicate with an ONC/RPC server, you need to create an
 * ONC/RPC client, represented by classes derived from <code>OncRpcClient</code>.
 * The most generic way to generate an ONC/RPC client is as follows: use
 * {@link #newOncRpcClient(InetAddress, int, int, int) newOncRpcClient(...)}
 * and specify:
 *
 * <ul>
 * <li>the host (of class InetAddress) where the ONC/RPC server resides,
 * <li>the ONC/RPC program number of the server to contact,
 * <li>the program's version number,
 * <li>and finally the IP protocol to use when talking to the server. This can be
 *   either {@link OncRpcProtocols#ONCRPC_UDP} or
 *   {@link OncRpcProtocols#ONCRPC_TCP}.
 * </ul>
 *
 * <p>The next code snippet shows how to create an ONC/RPC client, which can
 * communicate over UDP/IP with the ONC/RPC server for program number
 * <code>0x49678</code> on the same host (by coincidence, this is the program
 * number of the <a href="http://www.acplt.org/ks">ACPLT/KS</a> protocol).
 *
 * <pre>
 * OncRpcClient client;
 * try {
 *     client = OncRpcClient.newOncRpcClient(
 *         InetAddress.getByName("localhost"),
 *         0x49678, 1,
 *         OncRpcProtocols.ONCRPC_UDP);
 * } catch ( OncRpcProgramNotRegisteredException e ) {
 *     System.out.println("ONC/RPC program server not found");
 *     System.exit(0);
 * } catch ( OncRpcException e ) {
 *     System.out.println("Could not contact portmapper:");
 *     e.printStackTrace(System.out);
 *     System.exit(0);
 * } catch ( IOException e ) {
 *     System.out.println("Could not contact portmapper:");
 *     e.printStackTrace(System.out);
 *     System.exit(0);
 * }
 * </pre>
 *
 * <p>This code snippet also shows exception handling. The most common error
 * you'll see is probably an {@link OncRpcProgramNotRegisteredException}, in
 * case no such program number is currently registered at the specified host.
 * An {@link OncRpcProgramNotRegisteredException} is a subclass of
 * {@link OncRpcException} with a detail of
 * {@link OncRpcException#RPC_PROGNOTREGISTERED}.
 * In case no ONC/RPC portmapper is available at the specified host, you'll
 * get an {@link OncRpcTimeoutException} instead (which is again a subclass of
 * <code>OncRpcException</code> with a detail of
 * {@link OncRpcException#RPC_TIMEDOUT}).
 * You might also get an IOException when using TCP/IP and the server
 * can not be contacted because it does not accept new connections.
 *
 * <p>Instead of calling
 * {@link #newOncRpcClient(InetAddress, int, int, int) OncRpcClient.newOncRpcClient(...)}
 * you can also directly create objects of classes
 * {@link OncRpcTcpClient} and {@link OncRpcUdpClient} if you know at compile
 * time which kind of IP protocol you will use.
 *
 * <p>With a client proxy in your hands, you can now issue ONC/RPC calls. As
 * a really, really simple example -- did I say "simple example"? -- we start
 * with the famous ONC/RPC ping call. This call sends no parameters and expects
 * no return from an ONC/RPC server. It is just used to check whether a server
 * is still responsive.
 *
 * <pre>
 * System.out.print("pinging server: ");
 * try {
 *     client.call(0, XdrVoid.XDR_VOID, XdrVoid.XDR_VOID);
 * } catch ( OncRpcException e ) {
 *     System.out.println("method call failed unexpectedly:");
 *     e.printStackTrace(System.out);
 *     System.exit(1);
 * }
 * System.out.println("server is alive.");
 * </pre>
 *
 * <p>By definition, the ONC/RPC ping call has program number 0 and expects
 * no parameters and replies with no result. Thus we just specify an empty
 * parameter and result in the form of the static {@link XdrVoid#XDR_VOID}
 * object, when calling the ping procedure in the server using the
 * {@link #call(int, XdrAble, XdrAble) call(...)} method.
 *
 * <p>For more complex and sometimes more useful ONC/RPC calls, you will need
 * to write appropriate ONC/RPC parameter and reply classes. Unfortunately,
 * at this time there's no compiler available to compile <code>.x</code> files
 * into appropriate Java classes. Well -- surely a lot of people will now associate
 * completely wrong things with "x files", but in our case there's no new age
 * or whatever mumbo jumbo involved, but definitions of XDR data structures
 * instead.
 *
 * For the next example, let's pretend our server provides the answer to all
 * questions when called with procedure number 42. Let's also pretend that
 * this ONC/RPC call expects a question in form of a string and returns the
 * answer as an integer. So we need to define two classes, one for the call's
 * parameters and one for the reply. But let us first examine the class
 * containing a call's parameters:
 *
 * <pre>
 * class Parameters implements XdrAble {
 *     public String question;
 *
 *     public void xdrEncode(XdrEncodingStream xdr)
 *         throws OncRpcException, IOException {
 *         xdr.xdrEncodeString(question);
 *     }
 *     public void xdrDecode(XdrDecodingStream xdr)
 *         throws OncRpcException, IOException {
 *         question = xdr.xdrDecodeString();
 *     }
 * }
 * </pre>
 *
 * <p>The <code>Parameters</code> class implements {@link XdrAble}, so instances
 * of it can be sent and received over the network using Sun's XDR protocol.
 * What exactly is sent over the wire is up to the two methods
 * {@link XdrAble#xdrEncode xdrEncode(...)} and
 * {@link XdrAble#xdrDecode xdrDecode(...)}. The <code>xdrEncode</code> method
 * is responsible to "encode" the data to be sent over the network. On the
 * other side, <code>xdrDecode</code> is responsible to restore an object's
 * state from the data received over the network. In our example, these methods
 * either send or receive a string.
 *
 * <p>The class defining the reply of our the-answer-to-all-questions ONC/RPC
 * call is now straightforward:
 *
 * <pre>
 * class Answer implements XdrAble {
 *     public int definitiveAnswer;
 *
 *     public void xdrEncode(XdrEncodingStream xdr)
 *         throws OncRpcException, IOException {
 *         xdr.xdrEncodeInt(definitiveAnswer);
 *     }
 *     public void xdrDecode(XdrDecodingStream xdr)
 *         throws OncRpcException, IOException {
 *         definitiveAnswer = xdr.xdrDecodeInt();
 *     }
 * }
 * </pre>
 *
 * <p>Finally, to ask a question, you need to create the parameter object and
 * fill it with the parameters to be sent. Then create the object later receiving
 * the reply. Finally issue the ONC/RPC call:
 *
 * <pre>
 * Parameters parameters = new Parameters();
 * parameters.question = "What is the final answer to all our questions?";
 *
 * Answer answer = new Answer();
 *
 * try {
 *     client.call(42, parameters, answer);
 * } catch ( OncRpcException e ) {
 * } catch ( IOException e ) {
 * }
 * System.out.println(parameters.question);
 * System.out.println("And the answer is: " + answer.definitiveAnswer);
 * </pre>
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
 * <p>{@link OncRpcClientAuth Authentication} can be done as follows: just
 * create an authentication object and hand it over to the ONC/RPC client
 * object.
 *
 * <pre>
 * OncRpcClientAuth auth = new OncRpcClientAuthUnix(
 *                                 "marvin@ford.prefect",
 *                                 42, 1001, new int[0]);
 * client.setAuth(auth);
 * </pre>
 *
 * The {@link OncRpcClientAuthUnix authentication <code>AUTH_UNIX</code>} will
 * handle shorthand credentials (of type <code>AUTH_SHORT</code>) transparently.
 * If you do not set any authentication object after creating an ONC/RPC client
 * object, <code>AUTH_NONE</code> is used automatically.
 *
 * <p>TCP-based ONC/RPC clients also support call batching (exception handling
 * ommited for clarity):
 *
 * <pre>
 * OncRpcTcpClient client = new OncRpcTcpClient(
 *     InetAddress.getByName("localhost"),
 *     myprogramnumber, myprogramversion,
 *     OncRpcProtocols.ONCRPC_TCP);
 * client.callBatch(42, myparams, false);
 * client.callBatch(42, myotherparams, false);
 * client.callBatch(42, myfinalparams, true);
 * </pre>
 *
 * In the example above, three calls are batched in a row and only be sent
 * all together with the third call. Note that batched calls must not expect
 * replies, with the only exception being the last call in a batch:
 *
 * <pre>
 * client.callBatch(42, myparams, false);
 * client.callBatch(42, myotherparams, false);
 * client.call(43, myfinalparams, myfinalresult);
 * </pre>
 *
 * @see OncRpcPortmapClient
 * @see OncRpcTcpClient
 * @see OncRpcUdpClient
 * @see OncRpcClientAuth
 *
 * @version $Revision: 1.3 $ $Date: 2003/08/14 13:48:33 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public abstract class OncRpcClient {

    /**
     * Constructs an <code>OncRpcClient</code> object (the generic part). If
     * no port number is given (that is, <code>port</code> is <code>0</code>),
     * then a port lookup using the portmapper at <code>host</code> is done.
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
    protected OncRpcClient(InetAddress host,
                           int program, int version,
                           int port,
                           int protocol)
              throws OncRpcException, IOException {
        //
        // Set up the basics...
        //
        this.host =  host;
        this.program = program;
        this.version = version;
        //
        // Initialize the message identifier with some more-or-less random
        // value.
        //
        long seed = System.currentTimeMillis();
        xid = ((int) seed) ^ ((int) (seed >>> 32));
        //
        // If the port number of the ONC/RPC server to contact is not yet
        // known, try to find it out. For this we need to contact the portmap
        // process at the given host and ask it for the desired program.
        //
        // In case of tunneling through the HTTP protocol, we accept a port
        // number of zero and do not resolve it. This task is left up to
        // the other end of the HTTP tunnel (at the web server).
        //
        if ( (port == 0) && (protocol != OncRpcProtocols.ONCRPC_HTTP) ) {
            OncRpcPortmapClient portmap = new OncRpcPortmapClient(host);
            try {
                port = portmap.getPort(program, version, protocol);
            } finally {
                portmap.close();
            }
        }
        this.port = port;
    }

    /**
     * Creates a new ONC/RPC client object, which can handle the requested
     * <code>protocol</code>.
     *
     * @param host Host address where the desired ONC/RPC server resides.
     * @param program Program number of the desired ONC/RPC server.
     * @param version Version number of the desired ONC/RPC server.
     * @param protocol {@link OncRpcProtocols Protocol} to be used for
     *   ONC/RPC calls.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public static OncRpcClient newOncRpcClient(InetAddress host,
                                               int program, int version,
                                               int protocol)
        throws OncRpcException, IOException {
        return newOncRpcClient(host, program, version, 0, protocol);
    }

    /**
     * Creates a new ONC/RPC client object, which can handle the requested
     * <code>protocol</code>.
     *
     * @param host Host address where the desired ONC/RPC server resides.
     * @param program Program number of the desired ONC/RPC server.
     * @param version Version number of the desired ONC/RPC server.
     * @param port Port number of the ONC/RPC server. Specifiy <code>0</code>
     *   if this is not known and the portmap process located at host should
     *   be contacted to find out the port.
     * @param protocol {@link OncRpcProtocols Protocol} to be used for
     *   ONC/RPC calls.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     * @throws IOException if an I/O error occurs.
     */
    public static OncRpcClient newOncRpcClient(InetAddress host,
                                               int program, int version,
                                               int port,
                                               int protocol)
        throws OncRpcException, IOException {
        //
        // Now we need to create a protocol client object, which will know
        // how to create the network connection and how to send and receive
        // data to and from it.
        //
        switch ( protocol ) {
        case OncRpcProtocols.ONCRPC_UDP:
            return new OncRpcUdpClient(host, program, version, port);
        case OncRpcProtocols.ONCRPC_TCP:
            return new OncRpcTcpClient(host, program, version, port);
        default:
            throw(new OncRpcException(OncRpcException.RPC_UNKNOWNPROTO));
        }
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
    }

    /**
     * Calls a remote procedure on an ONC/RPC server.
     *
     * <p>The <code>OncRpcUdpClient</code> uses a similar timeout scheme as
     * the genuine Sun C implementation of ONC/RPC: it starts with a timeout
     * of one second when waiting for a reply. If no reply is received within
     * this time frame, the client doubles the timeout, sends a new request
     * and then waits again for a reply. In every case the client will wait
     * no longer than the total timeout set through the
     * {@link #setTimeout(int)} method.
     *
     * @param procedureNumber Procedure number of the procedure to call.
     * @param params The parameters of the procedure to call, contained
     *   in an object which implements the {@link XdrAble} interface.
     * @param result The object receiving the result of the procedure call.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     */
    public synchronized void call(int procedureNumber,
                                  XdrAble params, XdrAble result)
        throws OncRpcException {
        //
        // Use the default version number as specified for this client.
        //
        call(procedureNumber, version, params, result);
    }

    /**
     * Calls a remote procedure on an ONC/RPC server.
     *
     * @param procedureNumber Procedure number of the procedure to call.
     * @param versionNumber Protocol version number.
     * @param parameters The parameters of the procedure to call, contained
     *   in an object which implements the {@link XdrAble} interface.
     * @param result The object receiving the result of the procedure call.
     *
     * @throws OncRpcException if an ONC/RPC error occurs.
     */
    public abstract void call(int procedureNumber, int versionNumber,
                              XdrAble parameters, XdrAble result)
           throws OncRpcException;

    /**
     * Set the timout for remote procedure calls to wait for an answer from
     * the ONC/RPC server. If the timeout expires,
     * {@link #call(int, XdrAble, XdrAble)} will raise a
     * {@link java.io.InterruptedIOException}. The default timeout value is
     * 30 seconds (30,000 milliseconds). The timeout must be > 0.
     * A timeout of zero indicated batched calls, for which no reply message
     * is expected.
     *
     * @param milliseconds Timeout in milliseconds. A timeout of zero indicates
     *   batched calls.
     */
    public void setTimeout(int milliseconds) {
        if ( milliseconds < 0 ) {
            throw(new IllegalArgumentException("timeouts can not be negative."));
        }
        timeout = milliseconds;
    }

    /**
     * Retrieve the current timeout set for remote procedure calls. A timeout
     * of zero indicates batching calls (no reply message is expected).
     *
     * @return Current timeout.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Returns the program number specified when creating this client.
     *
     * @return ONC/RPC program number.
     */
    public int getProgram() {
        return program;
    }

    /**
     * Returns the version number specified when creating this client.
     *
     * @return ONC/RPC version number of ONC/RPC program.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns the IP address of the server's host this client is connected to.
     *
     * @return IP address of host.
     */
    public InetAddress getHost() {
        return host;
    }

    /**
     * Returns port number of the server this client is connected to.
     *
     * @return port number of ONC/RPC server.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the authentication to be used when making ONC/RPC calls.
     *
     * @param auth Authentication protocol handling object encapsulating
     *   authentication information.
     */
    public void setAuth(OncRpcClientAuth auth) {
        this.auth = auth;
    }

    /**
     * Returns the current authentication.
     *
     * @return Authentication protocol handling object encapsulating
     *   authentication information.
     */
    public OncRpcClientAuth getAuth() {
        return auth;
    }

	/**
	 * Set the character encoding for (de-)serializing strings.
	 *
	 * @param characterEncoding the encoding to use for (de-)serializing strings.
	 *   If <code>null</code>, the system's default encoding is to be used.
	 */
	public abstract void setCharacterEncoding(String characterEncoding);

	/**
	 * Get the character encoding for (de-)serializing strings.
	 *
	 * @return the encoding currently used for (de-)serializing strings.
	 *   If <code>null</code>, then the system's default encoding is used.
	 */
	public abstract String getCharacterEncoding();

    /**
     * Create next message identifier. Message identifiers are used to match
     * corresponding ONC/RPC call and reply messages.
     */
    protected void nextXid() {
        xid++;
    }

    /**
     * Internet address of the host where the ONC/RPC server we want to
     * communicate with is located at.
     */
    protected InetAddress host;

    /**
     * Timeout (in milliseconds) for communication with an ONC/RPC server.
     * ONC/RPC calls through the {@link #call(int, XdrAble, XdrAble)} method
     * will throw an exception if no answer from the ONC/RPC server is
     * received within the timeout time span.
     */
    protected int timeout = 30000;

    /**
     * Program number of the ONC/RPC server to communicate with.
     */
    protected int program;

    /**
     * Version number of the ONC/RPC server to communicate with.
     */
    protected int version;

    /**
     * Port number at which the ONC/RPC server can be contacted.
     */
    protected int port;

    /**
     * The message id (also sometimes known as "transaction id") used for
     * the next call message.
     */
    protected int xid;

    /**
     * Authentication protocol object to be used when issuing ONC/RPC calls.
     */
    protected OncRpcClientAuth auth;

}

// End of OncRpcClient.java