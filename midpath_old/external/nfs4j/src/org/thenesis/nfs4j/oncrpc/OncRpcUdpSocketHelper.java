/*
 * $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcUdpSocketHelper.java,v 1.2 2003/08/14 07:58:42 haraldalbrecht Exp $
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

import java.net.*;
import java.lang.reflect.*;

/**
 * Wraps JRE-specific networking code for UDP/IP-based client sockets.
 * So much for compile once, make it unuseable everywhere.
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
 * @version $Header: /cvsroot/remotetea/remotetea/src/org/acplt/oncrpc/OncRpcUdpSocketHelper.java,v 1.2 2003/08/14 07:58:42 haraldalbrecht Exp $
 * @author Harald Albrecht
 */
public class OncRpcUdpSocketHelper {

    /**
     * Creates a datagram socket and binds it to an arbitrary available port
     * on the local host machine.
     */
    public OncRpcUdpSocketHelper(DatagramSocket socket) {
        this.socket = socket;
        queryMethods();
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
                throw(new SocketException(t.getMessage()));
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
        if ( methodSetReceiveBufferSize != null ) {
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
     * Looks up methods of class DatagramSocket whether they are supported
     * by the class libraries of the JRE we are currently executing on.
     */
    protected void queryMethods() {
        Class c = DatagramSocket.class;
        try {
            methodSetSendBufferSize = c.getMethod("setSendBufferSize",
                                             new Class [] {int.class});
            methodGetSendBufferSize = c.getMethod("getSendBufferSize", null);
        } catch ( Exception e ) { }
        try {
            methodSetReceiveBufferSize = c.getMethod("setReceiveBufferSize",
                                             new Class [] {int.class});
            methodGetReceiveBufferSize = c.getMethod("getReceiveBufferSize", null);
        } catch ( Exception e ) { }
    }

    /**
     * The datagram socket for which we have to help out with some missing
     * methods.
     */
    private DatagramSocket socket;

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

}

// End of OncRpcUdpSocketHelper.java
