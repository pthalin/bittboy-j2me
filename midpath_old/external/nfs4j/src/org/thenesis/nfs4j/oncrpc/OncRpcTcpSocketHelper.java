/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcTcpSocketHelper.java,v 1.2 2003/08/14 11:26:50 haraldalbrecht Exp $
 *
 * NFS4J - Copyright (C) 2007 Guillaume Legris, Mathieu Legris
 *
 * Copyright (c) 2001
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
import java.net.*;
import java.lang.reflect.*;

/**
 * Wraps JRE-specific networking code for TCP/IP-based client sockets.
 * So much for compile once, make it unuseable everywhere. Why could our
 * great Sun simply not get their socket class straight from the beginning?
 * The BSD socket API has been around since long enough and that real
 * network applications need to control certain aspects of the transport
 * layer's behaviour was also well known at this time -- looks like the
 * one exceptions was -- and still is -- Sun, and the second one is MS.
 *
 * <p>Sun always toutes Java as the perfect network "whatever" (replace with
 * buzzword-of-the-day, like "programming language", "operating system",...)
 * and especially their support for the Web. Sweet irony that it took them
 * until JRE&nbsp;1.3 to realize that half-closed connections are the way
 * to do HTTP/1.0 non-persistent connections. And even more irony that
 * they are now beginning to understand about polled network i/o.
 *
 * <p>The following JRE-dependent methods are wrapped and will just do
 * nothing or return fake information on old JRE plattforms. The number
 * after each method wrapper indicates the first JRE version supporting
 * a particular feature:
 * <li>
 *   <li>setSendBufferSize() -- 1.2
 *   <li>setReceiveBufferSize() -- 1.2
 * </li>
 *
 * <p>The following methods have been around since JDK&nbsp;1.1, so we
 * do not need to wrap them as we will never support JDK&nbsp;1.0 -- let
 * it rest in piece(s):
 * <ul>
 *   <li>getTcpNoDelay() / setTcpNoDelay()
 *   <li>getSoTimeout() / setSoTimeout()
 * </ul>
 *
 * <p>In order to support connect() timeouts before JDK&nbsp;1.4, there's
 * now a <code>connect()</code> method available. It is more than just a
 * simple wrapper for pre JDK&nbsp;1.4.
 *
 * @version $Revision: 1.2 $ $Date: 2003/08/14 11:26:50 $ $State: Exp $ $Locker:  $
 * @author Harald Albrecht
 */
public class OncRpcTcpSocketHelper {

    /**
     * Creates a stream socket helper and associates it with the given
     * stream-based socket.
     *
     * @param socket The socket associated with this helper.
     */
    public OncRpcTcpSocketHelper(Socket socket) {
        this();
        this.socket = socket;
    }

    /**
     * Creates a stream socket helper but does not associates it with
     * a real stream socket object. You need to call {@link #connect}
     * lateron for a timeout-controlled connect.
     */
    public OncRpcTcpSocketHelper() {
        inspectSocketClassMethods();
    }

    /**
     * Connects to specified TCP port at given host address, but aborts
     * after timeout milliseconds if the connection could not be established
     * within this time frame.
     *
     * <p>On pre-JRE&nbsp;1.4 systems, this method will create a new thread
     * to handle connection establishment. This should be no problem, as in
     * general you might not want to connect to many ONC/RPC servers at the
     * same time; but surely someone will soon pop up with a perfect
     * reason just to do so...
     *
     * @param timeout Timeout in milliseconds for connection operation.
     *   A negative timeout leaves the exact timeout up to the particular
     *   JVM and java.net implementation.
     *
     * @throws IOException with the message "connect interrupted" in case the
     *   timeout was reached before the connection could be established.
     */
    public Socket connect(InetAddress address, int port, int timeout)
           throws IOException {
        if ( timeout < 0 ) {
            //
            // Leave the timeout up to the JVM and java.net implementation.
            //
            socket = new Socket(address, port);
            return socket;
        }
        //
        // Otherwise we have to implement a user-controlled timeout...
        //
        if ( methodConnect != null ) {
            //
            // Easy going. This JRE can do timeout-controlled connects
            // itself. Fine. We just need to invoke the proper connect method.
            // First, we call the Socket constructor without any parameters
            // to create an unconnected socket (new in JRE 1.4), then we
            // connect it to the destination -- well, at least we try to do so.
            //
            try {
                socket = (Socket) ctor.newInstance(new Object [] {});
                methodConnect.invoke(socket, new Object [] {
                                                 address,
                                                 new Integer(timeout)
                                             });
            } catch ( InvocationTargetException e ) {
                Throwable t = e.getTargetException();
                if ( t instanceof SocketException ) {
                    throw((SocketException) t);
                } else if ( t instanceof IllegalArgumentException ) {
                    throw((IllegalArgumentException) t);
                }
            } catch ( IllegalAccessException e ) {
                throw(new SocketException(e.getMessage()));
            } catch ( InstantiationException e ) {
                // This should never happen, but who knows...
                throw(new SocketException(e.getMessage()));
            }
        } else {
            //
            // Sigh. We have to emulate timeout-controlled connects ourself.
            // What a lousy JRE.
            //
            // Solution: we create a thread which will try to connect. It
            // will signal us when it succeeded or some exception was thrown.
            // We then wait for the signal for the given amount of time. In
            // case our wait times out, we let the thread go...
            //
            Connectiator connectiator = new Connectiator(address, port);
            connectiator.start();
            synchronized ( connectiator ) {
                try {
                    connectiator.wait(timeout);
                } catch ( InterruptedException ie ) {
                    //
                    // Please note that we already reacquired the lock on the
                    // connectiator again, so we can signal it that we are
                    // not interested any more. However, this will not directly
                    // terminate it, as this is not possible.
                    //
                    connectiator.notRequiredAnyMore();
                    //
                    // Now inform the caller that something went awry.
                    //
                    throw(new IOException("connect interrupted"));
                }
                //
                // Now find out whether the connection could be made within
                // the time limit or not. We still need to hold the lock so
                // we avoid race conditions when the connection thread just
                // connects while we timed out.
                //
                // We either could connect successfully, or the connect operation
                // threw an exception before we timed out. In case we could not
                // connect because the socket constructor failed, we simply
                // rethrow the exception.
                //
                IOException ie = connectiator.getIOException();
                if ( ie != null ) {
                    throw(ie);
                }
                socket = connectiator.getSocket();
                if ( socket == null ) {
                    //
                    // Same behaviour as constructor immediately trying to
                    // establish a connection.
                    //
                    throw(new NoRouteToHostException("Operation timed out: connect"));
                }
                // Well done. So to speak.
            }
            connectiator = null; // just to made the point...
        }
        return socket;
    }

    /**
     * The class <code>Connectiator</code> has a short and sometimes sad
     * life, as its only purpose is trying to connect to a TCP port at
     * another host machine.
     */
    private class Connectiator extends Thread {

        /**
         * Construct a new <code>Connectiator</code> that can later be used
         * connect to the given TCP port at the host specified. Note that we
         * do not try to establish the connection yet; this has to be done
         * later using the {@link #run} method.
         */
        public Connectiator(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public void run() {
            //
            // We need temporary object references here, as we are not allowed
            // to assign to the object's member references "socket" and "ie"
            // before we acquired the lock.
            //
            Socket mysocket = null;
            IOException myie = null;
            try {
                mysocket = new Socket(address, port);
            } catch ( IOException ie ) {
                myie = ie;
            }
            //
            // Acquire lock, so we can permanently set the connect's outcome
            // and notify the waiting socket helper object that we did
            // our job -- either good or not so good.
            //
            synchronized ( this ) {
                socket = mysocket;
                ioexception = myie;
                this.notify();
                if ( hitTheBucket && (socket != null) ) {
                    try {
                        socket.close();
                    } catch ( IOException e ) {
                    }
                }
            }
        }

        /**
         * Return exception caused by connection operation, if any, or
         * <code>null</code> if no exception was thrown.
         *
         * <p>Note that we do not need to synchronize this method as the caller
         * calls us when it is already holding the lock on us.
         *
         * @return Connection operation exception or <code>null</code>.
         */
        public IOException getIOException() {
            return ioexception;
        }

        /**
         * Return socket created by connection establishment, or
         * <code>null</code> if the connection could not be established.
         *
         * <p>Note that we do not need to synchronize this method as the caller
         * calls us when it is already holding the lock on us.
         *
         * @return Socket or <code>null</code>.
         */
        public Socket getSocket() {
            return socket;
        }

        /**
         * Indicates that the caller initiating this Thread is not interested
         * in its results any more.
         *
         * <p>Note that we do not need to synchronize this method as the caller
         * calls us when it is already holding the lock on us.
         */
        public void notRequiredAnyMore() {
            hitTheBucket = true;
            try {
                this.interrupt();
            } catch ( SecurityException e ) {
            }
        }

        /**
         * Host to connect to.
         */
        private InetAddress address;

        /**
         * TCP port to connect to.
         */
        private int port;

        /**
         * <code>IOException</code> caused by connection attempt, if any,
         * or <code>null</code>.
         */
        private IOException ioexception;

        /**
         * Socket object, if the connection could be established, or
         * <code>null</code>.
         */
        private Socket socket;

        /**
         * Flag to indicate that the socket is not needed, as the caller
         * timed out.
         */
        private boolean hitTheBucket = false;
    }

    /**
     * Sets the socket's send buffer size as a hint to the underlying
     * transport layer to use appropriately sized I/O buffers. If the class
     * libraries of the underlying JRE do not support setting the send
     * buffer size, this is silently ignored.
     *
     * @param size The size to which to set the send buffer size. This value
     *   must be greater than 0.
     *
     * @throws SocketException if the socket's send buffer size could not
     *   be set, because the transport layer decided against accepting the
     *   new buffer size.
     * @throws IllegalArgumentException if <code>size</code> is 0 or negative.
     */
    public void setSendBufferSize(int size)
           throws SocketException {
        if ( methodSetSendBufferSize != null ) {
            try {
                methodSetSendBufferSize.invoke(socket, new Object [] {
                                                   new Integer(size)
                                               });
            } catch ( InvocationTargetException e ) {
                Throwable t = e.getTargetException();
                if ( t instanceof SocketException ) {
                    throw((SocketException) t);
                } else if ( t instanceof IllegalArgumentException ) {
                    throw((IllegalArgumentException) t);
                }

            } catch ( IllegalAccessException e ) {
                throw(new SocketException(e.getMessage()));
            }
        }
    }

    /**
     * Get size of send buffer for this socket.
     *
     * @return Size of send buffer.
     *
     * @throws SocketException If the transport layer could not be queried
     *   for the size of this socket's send buffer.
     */
    public int getSendBufferSize()
           throws SocketException {
        if ( methodGetSendBufferSize != null ) {
            try {
                Object result = methodGetSendBufferSize.invoke(socket, null);
                if ( result instanceof Integer ) {
                    return ((Integer) result).intValue();
                }
            } catch ( InvocationTargetException e ) {
                Throwable t = e.getTargetException();
                if ( t instanceof SocketException ) {
                    throw((SocketException) t);
                }
                throw(new SocketException(t.getMessage()));
            } catch ( IllegalAccessException e ) {
                throw(new SocketException(e.getMessage()));
            }
        }
        //
        // Without knowing better we can only return fake information.
        // For quite some solaris OS revisions, the buffer size returned
        // is beyond their typical configuration, with only 8k for fragment
        // reassemble set asside by the IP layer. What a NOS...
        //
        return 65536;
    }

    /**
     * Sets the socket's receive buffer size as a hint to the underlying
     * transport layer to use appropriately sized I/O buffers. If the class
     * libraries of the underlying JRE do not support setting the receive
     * buffer size, this is silently ignored.
     *
     * @param size The size to which to set the receive buffer size. This value
     *   must be greater than 0.
     *
     * @throws SocketException if the socket's receive buffer size could not
     *   be set, because the transport layer decided against accepting the
     *   new buffer size.
     * @throws IllegalArgumentException if <code>size</code> is 0 or negative.
     */
    public void setReceiveBufferSize(int size)
           throws SocketException {
        if ( methodSetSendBufferSize != null ) {
            try {
                methodSetReceiveBufferSize.invoke(socket, new Object [] {
                                                      new Integer(size)
                                                  });
            } catch ( InvocationTargetException e ) {
                Throwable t = e.getTargetException();
                if ( t instanceof SocketException ) {
                    throw((SocketException) t);
                } else if ( t instanceof IllegalArgumentException ) {
                    throw((IllegalArgumentException) t);
                }

            } catch ( IllegalAccessException e ) {
                throw(new SocketException(e.getMessage()));
            }
        }
    }

    /**
     * Get size of receive buffer for this socket.
     *
     * @return Size of receive buffer.
     *
     * @throws SocketException If the transport layer could not be queried
     *   for the size of this socket's receive buffer.
     */
    public int getReceiveBufferSize()
           throws SocketException {
        if ( methodGetReceiveBufferSize != null ) {
            try {
                Object result = methodGetReceiveBufferSize.invoke(socket, null);
                if ( result instanceof Integer ) {
                    return ((Integer) result).intValue();
                }
            } catch ( InvocationTargetException e ) {
                Throwable t = e.getTargetException();
                if ( t instanceof SocketException ) {
                    throw((SocketException) t);
                }
                throw(new SocketException(t.getMessage()));
            } catch ( IllegalAccessException e ) {
                throw(new SocketException(e.getMessage()));
            }
        }
        //
        // Without knowing better we can only return fake information.
        // For quite some solaris OS revisions, the buffer size returned
        // is beyond their typical configuration, with only 8k for fragment
        // reassemble set asside by the IP layer. What a NOS...
        //
        return 65536;
    }

    /**
     * Looks up methods of class Socket whether they are supported by the
     * class libraries of the JRE we are currently executing on.
     */
    protected void inspectSocketClassMethods() {
        Class socketClass = Socket.class;
        // JRE 1.2 specific stuff
        try {
            methodSetSendBufferSize = socketClass.getMethod("setSendBufferSize",
                                             new Class [] {int.class});
            methodGetSendBufferSize = socketClass.getMethod("getSendBufferSize", null);
        } catch ( Exception e ) { }
        try {
            methodSetReceiveBufferSize = socketClass.getMethod("setReceiveBufferSize",
                                             new Class [] {int.class});
            methodGetReceiveBufferSize = socketClass.getMethod("getReceiveBufferSize", null);
        } catch ( Exception e ) { }
        // JRE 1.4 specific stuff
        try {
            ctor = socketClass.getConstructor(new Class [] {});
            methodConnect = socketClass.getMethod("connect",
                                   new Class [] {InetAddress.class, int.class});
        } catch ( Exception e ) { }
    }

    /**
     * The socket for which we have to help out with some missing methods.
     */
    private Socket socket;

    /**
     * Method Socket.setSendBufferSize or <code>null</code> if not available
     * in the class library of a particular JRE.
     */
    private Method methodSetSendBufferSize;

    /**
     * Method Socket.setReceiverBufferSize or <code>null</code> if not available
     * in the class library of a particular JRE.
     */
    private Method methodSetReceiveBufferSize;

    /**
     * Method Socket.getSendBufferSize or <code>null</code> if not available
     * in the class library of a particular JRE.
     */
    private Method methodGetSendBufferSize;

    /**
     * Method Socket.getReceiveBufferSize or <code>null</code> if not available
     * in the class library of a particular JRE.
     */
    private Method methodGetReceiveBufferSize;

    /**
     * Method Socket.connect with timeout or <code>null</code> if not available
     * in the class library of a particular JRE.
     */
    private Method methodConnect;

    /**
     * Constructor Socket() without any parameters or <code>null</code> if not available
     * in the class library of a particular JRE.
     */
    private Constructor ctor;

}

// End of OncRpcTcpSocketHelper.java
