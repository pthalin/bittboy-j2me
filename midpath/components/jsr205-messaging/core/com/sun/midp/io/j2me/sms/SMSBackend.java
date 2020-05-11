package com.sun.midp.io.j2me.sms;

import java.io.IOException;

import com.sun.midp.io.j2me.sms.Protocol.SMSPacket;

public interface SMSBackend {

	/**
	 * Native function to open sms connection
	 *
	 * @param host The name of the host for this connection. Can be
	 *	   <code>null</code>.
	 * @param msid Midlet suite ID, Cannot be <code>null</code>.
	 * @param port port to open
	 * @return    returns handle to SMS connection.
	 */
	public int open(String host, int msid, int port) throws IOException;

	/**
	 * Receives SMS message
	 *
	 * @param port incoming port
	 * @param msid Midlet suite ID
	 * @param handle handle to open SMS connection
	 * @param smsPacket received packet
	 * @return number of bytes received
	 * @exception IOException  if an I/O error occurs
	 */
	public int receive(int port, int msid, int handle, SMSPacket smsPacket) throws IOException;

	/**
	 * Native function to close sms connection
	 *
	 * @param port port number to close
	 * @param handle sms handle returned by open0
	 * @param deRegister Deregistration appID when parameter is 1.
	 * @return    0 on success, -1 on failure
	 */
	public int close(int port, int handle, int deRegister);

	/**
	 * Sends SMS message
	 *
	 * @param handle handle to SMS connection.
	 * @param type message type, binary or text.
	 * @param host URL of host sending message
	 * @param destPort destination port
	 * @param sourcePort source port
	 * @param message message buffer
	 * @return number of bytes sent
	 * @exception IOException  if an I/O error occurs
	 */
	public int send(int handle, int type, String host, int destPort, int sourcePort, byte[] message) throws IOException;

	/**
	 * Waits until message available
	 *
	 * @param port incoming port
	 * @param handle handle to SMS connection
	 * @return 0 on success, -1 on failure
	 * @exception IOException  if an I/O error occurs
	 */
	public int waitUntilMessageAvailable(int port, int handle) throws IOException;

	/**
	 * Unblock the receive thread.
	 *
	 * @return  returns handle to the connection.
	 */
	public int unblockReceiveThread() throws IOException;
	
	//	/**
	//	 * Computes the number of transport-layer segments that would be required to
	//	 * send the given message.
	//	 *
	//	 * @param msgBuffer The message to be sent.
	//	 * @param msgLen The length of the message.
	//	 * @param msgType The message type: binary or text.
	//	 * @param hasPort Indicates if the message includes a source or destination
	//	 *	   port number.
	//	 *
	//	 * @return The number of transport-layer segments required to send the
	//	 *	   message.
	//	 */
	//	public int numberOfSegments(byte msgBuffer[], int msgLen, int msgType, boolean hasPort);

}
