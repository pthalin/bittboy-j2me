package com.sun.midp.io.j2me.socket;

import java.io.IOException;

public interface SocketPeer {
	
	/**
	 * Opens a TCP connection to a server.
	 *
	 * @param host hostname
	 * @param port TCP port at host
	 *
	 * @exception  IOException  if an I/O error occurs.
	 * 
	 * TODO: use open(byte[] szIpBytes, int port) instead
	 */
	public void open(String host, int port) throws IOException;

//	/**
//	 * Opens a TCP connection to a server.
//	 *
//	 * @param szIpBytes  raw IPv4 address of host
//	 * @param port       TCP port at host
//	 *
//	 * @exception  IOException  if an I/O error occurs.
//	 */
//	public void open(byte[] szIpBytes, int port) throws IOException;

	/**
	 * Reads from the open socket connection.
	 *
	 * @param      b      the buffer into which the data is read.
	 * @param      off    the start offset in array <code>b</code>
	 *                    at which the data is written.
	 * @param      len    the maximum number of bytes to read.
	 * @return     the total number of bytes read into the buffer, or
	 *             <code>-1</code> if there is no more data because the end of
	 *             the stream has been reached.
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int read(byte b[], int off, int len) throws IOException;

	/**
	 * Writes to the open socket connection.
	 *
	 * @param      b      the buffer of the data to write
	 * @param      off    the start offset in array <code>b</code>
	 *                    at which the data is written.
	 * @param      len    the number of bytes to write.
	 * @return     the total number of bytes written
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int write(byte b[], int off, int len) throws IOException;

	/**
	 * Gets the number of bytes that can be read without blocking.
	 *
	 * @return     number of bytes that can be read without blocking
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int available() throws IOException;

	/**
	 * Closes the socket connection.
	 *
	 * @exception  IOException  if an I/O error occurs when closing the
	 *                          connection.
	 */
	public void close() throws IOException;

	/**
	 * Gets the requested IP number.
	 *
	 * @param      local   <tt>true</tt> to get the local host IP address, or
	 *                     <tt>false</tt> to get the remote host IP address
	 * @return     the IP address as a dotted-quad <tt>String</tt>
	 * @exception  IOException  if an I/O error occurs.
	 */
	public String getHost(boolean local) throws IOException;

	/**
	 * Gets the requested port number.
	 *
	 * @param      local   <tt>true</tt> to get the local port number, or
	 *                     <tt>false</tt> to get the remote port number
	 * @return     the port number
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int getPort(boolean local) throws IOException;

	/**
	 * Gets the requested socket option.
	 *
	 * @param      option  socket option to retrieve
	 * @return     value of the socket option
	 * @exception  IOException  if an I/O error occurs.
	 */
	public int getSockOpt(int option) throws IOException;

	/**
	 * Sets the requested socket option.
	 *
	 * @param      option  socket option to set
	 * @param      value   the value to set <tt>option</tt> to
	 * @exception  IOException  if an I/O error occurs.
	 */
	public void setSockOpt(int option, int value) throws IOException;

	/**
	 * Shuts down the output side of the connection.  Any error that might
	 * result from this operation is ignored.
	 */
	public void shutdownOutput();
	
//	/**
//	 * Gets a byte array that represents an IPv4 or IPv6 address 
//	 *
//	 * @param      szHost  the hostname to lookup as a 'C' string
//	 * @param      ipBytes_out  Output array that receives the
//	 *             bytes of IP address
//	 * @return     number of bytes copied to ipBytes_out or -1 for an error
//	 */
//	public int getIpNumber(String host, byte[] ipBytes_out);

}