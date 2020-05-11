package de.avetana.bluetooth.obex;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.obex.Authenticator;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ServerRequestHandler;

import de.avetana.bluetooth.util.Debug;

public class SessionHandler implements Connection, CommandHandler, Runnable {


	private ServerRequestHandler myHandler;
	private StreamConnection streamCon;
	private InputStream is;
	private OutputStream os;
	private int mtu = 0x2000;
	private Operation m_putOperation = null;
	private Operation m_getOperation = null;
	private Authenticator auth;
	private boolean authorized = false;


	public SessionHandler(StreamConnection streamCon, Authenticator auth, ServerRequestHandler handler) {
		this.streamCon = streamCon;
		this.auth = auth;
		this.myHandler = handler;
	}

	public HeaderSet createHeaderSet() {
		if (myHandler != null) return myHandler.createHeaderSet();
		else return new HeaderSetImpl();
	}
	
	public synchronized void run () {
					try {
					is = streamCon.openInputStream();
					os = streamCon.openOutputStream();
					while (streamCon != null) {
						byte[] data = null;
						try {
							data = receiveCommand ();
						} catch (Exception e) {
							closeStreamCon();
							continue;
						}
						//System.out.println ("Received command ! " + Integer.toHexString((int)(data[0] & 0xff)));
						
						if (is == null || os == null || streamCon == null) {
							closeStreamCon();
							continue;
						}
						
						switch ((int)(data[0] & 0xff)) {
							case 0x80: {
								mtu = 0xffff & ((0xff & data[5]) << 8 | (0xff & data[6]));
								HeaderSet request = OBEXConnection.parseHeaders(data, 7);
								HeaderSet response = myHandler.createHeaderSet();
								boolean success = handleAuthResponse(request);
								handleAuthChallenge(request, response);
								int ret = myHandler.onConnect( request, response);
								byte[] rhead = OBEXConnection.hsToByteArray(response);
								byte retdata[] = new byte[7 + rhead.length];
								retdata[0] = (byte)ret;
								retdata[1] = (byte)((retdata.length >> 8) & 0xff);
								retdata[2] = (byte)((retdata.length >> 0) & 0xff);
								retdata[3] = 0x10;
								retdata[4] = 0x00;
								retdata[5] = 0x20;
								retdata[6] = 0x00;
								System.arraycopy(rhead, 0, retdata, 7, rhead.length);
								os.write(retdata);
								os.flush();
								break;
							}
							case 0x81: {
								HeaderSet request = OBEXConnection.parseHeaders(data, 3);
								HeaderSet response = myHandler.createHeaderSet();
								synchronized (this) {
									myHandler.onDisconnect( request, response);
									byte[] rhead = OBEXConnection.hsToByteArray(response);
									byte retdata[] = new byte[3 + rhead.length];
									retdata[0] = (byte)0xa0;
									retdata[1] = (byte)((retdata.length >> 8) & 0xff);
									retdata[2] = (byte)((retdata.length >> 0) & 0xff);
									System.arraycopy(rhead, 0, retdata, 3, rhead.length);
									os.write(retdata);
									os.flush();
									closeStreamCon();
								}
//								is.close(); is = null;
//								os.close(); os = null;
//								streamCon.close(); streamCon = null;
//								System.out.println ("OBEX Disconnected");
								break;
							}
							case 0x02:
							case 0x82: {
								HeaderSet request = OBEXConnection.parseHeaders(data, 3);
								HeaderSet response = myHandler.createHeaderSet();
								handleAuthResponse(request);
								handleAuthChallenge(request, response);
								if (m_putOperation == null) m_putOperation = new OperationImpl (this, request, OBEXConnection.PUT);
								((OperationImpl)m_putOperation).newData (request);
								int ret = 0x90;
								if (data[0] == (byte)0x82) {
									ret = myHandler.onPut(m_putOperation);
									response = ((OperationImpl)m_putOperation).getHeadersToSend();
								}
								response.setHeader(0x49, null);
								sendCommand (ret, OBEXConnection.hsToByteArray(response));
								//System.out.println ("OBEX PUT ret " + ret + " got " + (int)(data[0] & 0xff) + " len " + data.length);
								break;
							}
							case 0x83:	
							case 0x03:	
							{
								HeaderSet request = OBEXConnection.parseHeaders(data, 3);
								HeaderSet response = null;
								
								int ret = 0xa0;
								if (m_getOperation == null) {
									m_getOperation = new OperationImpl(this, request, OBEXConnection.GET);
									response = myHandler.createHeaderSet();

									((OperationImpl)m_getOperation).sendHeaders(response);
									handleAuthResponse(request);
									handleAuthChallenge(request, response);
									ret = myHandler.onGet(m_getOperation);
									if (((OperationImpl)m_getOperation).getHeadersToSend() != response) {
										Object auResp = response.getHeader(HeaderSetImpl.AUTH_RESPONSE);
										response = ((OperationImpl)m_getOperation).getHeadersToSend();
										if (auResp != null)
											response.setHeader(HeaderSetImpl.AUTH_RESPONSE, auResp);
									}
								} else 	response = ((OperationImpl)m_getOperation).getHeadersToSend();

								
								
								if (ret == 0x90 || ret == 0xa0) {
									response.setHeader(0x48, null);
									response.setHeader(0x49, null);
									InputStream is = m_getOperation.openInputStream();
									int respLen = OBEXConnection.hsToByteArray(response).length;
									byte d2[] = new byte[Math.min(is.available(), mtu - respLen - 3)];
									is.read(d2);
									if (is.available() == 0) {
										response.setHeader(0x49, d2);
										ret = 0xa0;
										m_getOperation = null;
									} else {
										response.setHeader(0x48, d2);
										ret = 0x90;
									}
									//System.out.println ("OBEX GET ret " + ret + " got " + (int)(data[0] & 0xff) + " len " + d2.length);
									sendCommand (ret, OBEXConnection.hsToByteArray(response));
								}
								break;
							}
							case 0x85: {
								HeaderSet request = OBEXConnection.parseHeaders(data, 5);
								HeaderSet response = myHandler.createHeaderSet();
								boolean success = handleAuthResponse(request);
								handleAuthChallenge(request, response);
								int ret = myHandler.onSetPath( request, response, (data[3] & 1) == 1, (data[3] & 2) == 2);
								byte[] rhead = OBEXConnection.hsToByteArray(response);
								byte retdata[] = new byte[3 + rhead.length];
								retdata[0] = (byte)ret;
								retdata[1] = (byte)((retdata.length >> 8) & 0xff);
								retdata[2] = (byte)((retdata.length >> 0) & 0xff);
								System.arraycopy(rhead, 0, retdata, 3, rhead.length);
								os.write(retdata);
								os.flush();
							}
							default:
								Debug.println(5, "Received unidentified command ! " + (int)(data[0] & 0xff));

						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					closeStreamCon();
				}
	}
	
	/**
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	protected void handleAuthChallenge(HeaderSet request, HeaderSet response) throws IOException {
		byte authChallenge[] = (byte[])request.getHeader(HeaderSetImpl.AUTH_CHALLENGE);
		if (authChallenge != null && auth != null) {
			byte[] resp = HeaderSetImpl.createAuthResponse(authChallenge, auth);
			response.setHeader(HeaderSetImpl.AUTH_RESPONSE, resp);
		}
	}

	/**
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 */
	protected boolean handleAuthResponse(HeaderSet request) throws IOException {
		byte authResp[] = (byte[])request.getHeader(HeaderSetImpl.AUTH_RESPONSE);
		
		if (auth == null) return true;
		else if (authResp == null) return false;

		byte[] nonce = null;
		
		int offset = 18;
		
		byte[] digest = new byte[16];
		System.arraycopy(authResp, 2, digest, 0, 16);
		
		byte[] user = null;
		if (authResp[18] == (byte)0x01) {
			user = new byte[(byte)authResp[19]];
			System.arraycopy (authResp, 20, user, 0, user.length);
			offset += 2 + user.length;
		}
		byte[] passwd = auth.onAuthenticationResponse(user);
		
		if (authResp.length > offset && authResp[offset] == (byte)0x02) {
			nonce = new byte[16];
			System.arraycopy(authResp, offset + 2, nonce, 0, 16);
		}
		
		String check = "", digestS ="";
		try {
			check = new String (nonce, 0, 16, "iso-8859-1") + ":" + new String (passwd, 0, passwd.length, "iso-8859-1");
			MD5 md5 = new MD5();
			md5.update(check.toCharArray(), check.length());
			md5.md5final();
			byte checkB[] = md5.toByteArray();
			digestS = new String (digest, "iso-8859-1");
			check = new String (checkB, "iso-8859-1");
			if (!check.equals(digestS)) this.myHandler.onAuthenticationFailure(user);
			else authorized = true;
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		return authorized;
	}

	/**
	 * closes only the currently open connection if there is one. Does not close the Connection notifier
	 * itself. So after closeStreamCon() is called, one can call acceptAndOpen() to offer a new service. 
	 *
	 */
	
	private synchronized void closeStreamCon() {
//		new Throwable().printStackTrace();
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {}
			is = null;
		}
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {}
			os = null;
		}
		if (streamCon != null) {
			try {
				streamCon.close();
			} catch (Exception e) {}
			streamCon = null;
		}

	}

	
	public void close() {
		closeStreamCon();
	}
	
	public byte[] receiveCommand () throws IOException {
		byte start[] = new byte[3];
		int read = 0;
		while (read < 3) {
			//System.out.println ("Waiting for command " + read);
			read += Math.max(0, is.read(start, read, 3 - read));
		}
		//System.out.println ("Got " + read + " bytes");
		int toRead = 0xffff & (((start[1] & 0xff) << 8) | (start[2] & 0xff));
		byte[] data = new byte[toRead];
		System.arraycopy (start, 0, data, 0, 3);
		while (read < toRead) 			read += Math.max(0, is.read (data, read, toRead - read));
		/*System.out.print ("Data received ");
		for (int i = 0;i < data.length;i++)
			System.out.print (" " + Integer.toHexString(0xff & data[i] ));
		System.out.println();*/
		return data;
	}

	public synchronized void sendCommand (int commId, byte[] data) throws IOException {
		if (os == null)
			throw new IOException ("Connection closed");
		
		int len = 3 + data.length;
		
		os.write (new byte[] { (byte)commId, (byte)((len >> 8) & 0xff), (byte)(len & 0xff) });
		os.write (data);
		//System.out.print ("Sending command ");
		 //System.out.print (" " + Integer.toHexString(commId & 0xff));	
		 //System.out.print (" " + Integer.toHexString((byte)((len >> 8) & 0xff)));	
		 //System.out.print (" " + Integer.toHexString( (byte)(len & 0xff)));	
		//for (int i = 0;i < data.length ;i++) System.out.print (" " + Integer.toHexString(data[i] & 0xff));
		//System.out.println();
		os.flush();
	}
	
	public int getMTU() {
		return mtu;
	}

	public StreamConnection getStreamConnection() {
		return streamCon;
	}

	public Authenticator getAuthenticator() {
		return auth;
	}
}
