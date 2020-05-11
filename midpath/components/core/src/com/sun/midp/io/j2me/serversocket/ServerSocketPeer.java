package com.sun.midp.io.j2me.serversocket;

import java.io.IOException;

public interface ServerSocketPeer {

	/**
	 * Opens a server socket connection on the given port.  If successful, 
	 * stores a handle directly into the nativeHandle field.  If unsuccessful, 
	 * throws an exception.
	 *
	 * @param port       TCP port to listen for connections on
	 * @param suiteID    ID of current midlet suite, or null if there
	 *                   is no current suite
	 * 
	 * @exception IOException  if some other kind of I/O error occurs
	 * or if reserved by another suite
	 */
	public void open(int port, byte[] suiteID) throws IOException;

	/**
	 * Closes the connection.
	 *
	 * @exception  IOException  if an I/O error occurs when closing the
	 *                          connection
	 */
	public void close() throws IOException;

	/**
	 * Waits for an incoming TCP connection on server socket. The method
	 * blocks current thread till a connection is made.
	 * <p>
	 * The 'con' parameter must be a freshly created client socket connection
	 * object.  When an incoming connection is accepted, the socket handle for
	 * the newly accepted connection is stored into this object directly from 
	 * native code. This technique ensures that the acceptance of a new 
	 * connection and the storing of the native handle are performed 
	 * atomically.
	 * 
	 * @param con the client socket connection object
	 *
	 * @exception IOException if an I/O error has occurred
	 */
	public void accept(com.sun.midp.io.j2me.socket.Protocol con) throws IOException;

	/**
	 * Gets the local IP number.
	 *
	 * @return     the IP address as a dotted-quad <tt>String</tt>
	 */
	public String getLocalAddress();

	/**
	 * Gets the local port to which this socket connection is bound.
	 *
	 * @return the local port number for this socket connection
	 */
	public int getLocalPort();

}