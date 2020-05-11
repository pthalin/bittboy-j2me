/*
 * Created on 25.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.obex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;
import javax.obex.Authenticator;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OBEXConnection implements ClientSession, CommandHandler {

	protected static final int CONNECT = 0x80;
	protected static final int DISCONNECT = 0x81;
	protected static final int CLOSE = 0x82;
	protected static final int PUT = 0x02;
	protected static final int GET = 0x03;
	protected static final int SETPATH = 0x85;
	protected static final int SESSION = 0x87;
	protected static final int ABORT = 0xFF;
	protected int mtu = 0x2000;
	
	private StreamConnection con;
	private long conID = -1;
	private InputStream is;
	private OutputStream os;
	private Authenticator auth = null;
	private byte[] nonce = null;
	
	public OBEXConnection (StreamConnection con)  throws IOException {
		this.con = con;
		os = con.openOutputStream();
		is = con.openInputStream();
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.io.Connection#close()
	 */
	public void close() throws IOException  {
		is.close();
		os.close();
		con.close();
	}
	
	public RemoteDevice getRemoteDevice() throws IOException {
		return RemoteDevice.getRemoteDevice(con);
	}
	
	public HeaderSet createHeaderSet() {
		return new HeaderSetImpl();
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#setAuthenticator(javax.obex.Authenticator)
	 */
	public void setAuthenticator(Authenticator auth) {
		this.auth = auth;
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#setConnectionID(long)
	 */
	public void setConnectionID(long id) {
		this.conID = id;
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#getConnectionID()
	 */
	public long getConnectionID() {
		return conID;
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#connect(javax.obex.HeaderSet)
	 */
	public HeaderSet connect(HeaderSet headers) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(0x10);
		bos.write (0);
		bos.write (new byte[] { 0x20, 0x00 });
		bos.write (hsToByteArray (headers));
		sendCommand (CONNECT, bos.toByteArray());
		byte[] b = receiveCommand();
		mtu = 0xffff & ((0xff & b[5]) << 8 | (0xff & b[6]));
		//System.out.println ("Returning from connect");
		HeaderSet hs =  parseHeaders (b, 7);

		//handler an authenticationResponse in the response to connect
		byte[] authResp = (byte[])hs.getHeader(HeaderSetImpl.AUTH_RESPONSE);
		if (authResp != null && auth != null) {
			handleAuthResponse(authResp, auth);
		}

		//Handle a authentication challenge in the response to connect
		byte authChallenge[] = (byte[])hs.getHeader(HeaderSetImpl.AUTH_CHALLENGE);
		
		if (authChallenge != null && auth != null) {
			
			byte[] resp = HeaderSetImpl.createAuthResponse(authChallenge, auth);

			bos = new ByteArrayOutputStream();
			bos.write(0x10);
			bos.write (0);
			bos.write (new byte[] { 0x20, 0x00 });
			headers.setHeader(HeaderSetImpl.AUTH_RESPONSE, resp);
			bos.write (hsToByteArray (headers));
			sendCommand (CONNECT, bos.toByteArray());
			b = receiveCommand();
			if (b.length > 7) {
				mtu = 0xffff & ((0xff & b[5]) << 8 | (0xff & b[6]));
			//System.out.println ("Returning from connect");
				hs =  parseHeaders (b, 7);
			}
		}
		
		
		if (b[0] != (byte)0xa0) throw new IOException ("Connection not accepted " + Integer.toHexString((int)(b[0] & 0xff)));
		if (hs.getHeader(HeaderSetImpl.CONNECTION_ID) != null) conID = ((Long)hs.getHeader(HeaderSetImpl.CONNECTION_ID)).longValue();
		((HeaderSetImpl)hs).setRespCode (b[0]);
		return hs;
	}

	/**
	 * @param authResp
	 * @param authenticator
	 * @throws IOException
	 */
	protected void handleAuthResponse(byte[] authResp, Authenticator authenticator) throws IOException {

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
			if (!check.equals(digestS)) throw new IOException ("Authentication failure");
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
				
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#disconnect(javax.obex.HeaderSet)
	 */
	public HeaderSet disconnect(HeaderSet headers) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write (hsToByteArray (headers));
		sendCommand (DISCONNECT, bos.toByteArray());
		byte[] b = receiveCommand();
		if (b[0] != (byte)0xa0) throw new IOException ("Disconnection error");
		HeaderSet hs = parseHeaders (b, 3);
		((HeaderSetImpl)hs).setRespCode (b[0]);
		return hs;

	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#setPath(javax.obex.HeaderSet, boolean, boolean)
	 */
	public HeaderSet setPath(HeaderSet headers, boolean backup, boolean create) throws IOException {
			byte b1[] = OBEXConnection.hsToByteArray(headers);
			byte b2[] = new byte[b1.length + 2];
			b2[0] = b2[1] = 0;
			if (backup) b2[0] |= 1;
			if (!create) b2[0] |= 2;
			System.arraycopy(b1, 0, b2, 2, b1.length);
			sendCommand (OBEXConnection.SETPATH, b2);
			byte[] resp = receiveCommand();
			HeaderSet hs = OBEXConnection.parseHeaders(resp, 3);
			((HeaderSetImpl)hs).setRespCode (resp[0]);
			return hs;
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#delete(javax.obex.HeaderSet)
	 */
	public HeaderSet delete(HeaderSet headers) throws IOException {
		byte b1[] = OBEXConnection.hsToByteArray(headers);
		sendCommand (OBEXConnection.PUT, b1);
		byte[] resp = receiveCommand();
		HeaderSet hs = OBEXConnection.parseHeaders(resp, 3);
		((HeaderSetImpl)hs).setRespCode (b1[0]);
		return hs;
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#get(javax.obex.HeaderSet)
	 */
	public Operation get(HeaderSet headers) throws IOException {
		return new OperationImpl (this, headers, OBEXConnection.GET);
	}

	/* (non-Javadoc)
	 * @see javax.obex.ClientSession#put(javax.obex.HeaderSet)
	 */
	public Operation put(HeaderSet headers) throws IOException {
		return new OperationImpl (this, headers, OBEXConnection.PUT);
	}
		
	public void sendCommand (int commId, byte[] data) throws IOException {
		//System.err.println ("Sending command " + Integer.toHexString(commId) + " conID "+ conID);
		//new Throwable().printStackTrace();
		int len = 3 + data.length;
		if (conID != -1) len += 5;
		byte d2[] = new byte[len];
		d2[0] = (byte)commId;
		d2[1] =  (byte)((len >> 8) & 0xff);
		d2[2] = (byte)(len & 0xff);
		if (conID != -1) {
			d2[3] = (byte)0xcb;
			d2[4] = (byte)(0xff & (conID >> 24)); 
			d2[5] = (byte)(0xff & (conID >> 16)); 
			d2[6] = (byte)(0xff & (conID >> 8)); 
			d2[7] = (byte)(0xff & (conID >> 0)); 
		}
		System.arraycopy (data, 0, d2, len - data.length, data.length);
		
		//Special case for command SetPath that has two option bytes after the length field and before any other headers or the sessionID
		if (commId == OBEXConnection.SETPATH) {
			byte b1 = data[0];
			byte b2 = data[1];
			System.arraycopy(d2, 3, d2, 5, d2.length - data.length - 3);
			d2[3] = b1;
			d2[4] = b2;
		}
		
		os.write (d2);
		/*System.out.print ("Sending command ");
		 System.out.print (" " + Integer.toHexString(commId & 0xff));	
		 System.out.print (" " + Integer.toHexString((byte)((len >> 8) & 0xff)));	
		 System.out.print (" " + Integer.toHexString( (byte)(len & 0xff)));	
		for (int i = 0;i < data.length ;i++) System.out.print (" " + Integer.toHexString(data[i] & 0xff));
		System.out.println();*/
		os.flush();
	}
	
	public byte[] receiveCommand () throws IOException {
		byte start[] = new byte[3];
		int read = 0;
		while (read < 3) {
//			System.out.println ("Waiting for data to be returned");
			read += Math.max(0, is.read(start, read, 3 - read));
//			System.out.println ("read bytes "+ read);
		}
		int toRead = 0xffff & (((start[1] & 0xff) << 8) | (start[2] & 0xff));
		byte[] data = new byte[toRead];
		System.arraycopy (start, 0, data, 0, 3);
		while (read < toRead) {
//			System.out.println ("Waiting for data to be returned2");
			read += Math.max(0, is.read (data, read, toRead - read));
//			System.out.println ("Received " + read);
		}
/*		for (int i = 0;i < data.length;i++)
			System.out.print (" " + Integer.toHexString(0xff & data[i] ));
		System.out.println(); */
		return data;
	}
	
	static private void writeLen (OutputStream os, long v)  {
		byte[] b = new byte[4];
		b[0] = (byte) ((v >> 24) & 0xff);
		b[1] = (byte) ((v >> 16) & 0xff);
		b[2] = (byte) ((v >> 8) & 0xff);
		b[3] = (byte) (v & 0xff);
		try { os.write (b); } catch (Exception e) { e.printStackTrace();  }
	}
	
	static private long parseLong (byte data[], int offset) {
		long v = 0;
		for (int i = 0;i < 4;i++) {
			v = v << 8;
			v |= (int)(data[offset++] & 0xff);
		}
		return v;
	}
	
	static protected byte[] hsToByteArray (HeaderSet hs) throws IOException {
		if (hs == null) return new byte[0];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int[] hids = new int[0];
		try {
			hids = hs.getHeaderList();
		} catch (IOException e1) {
		}
		int has48header = -1;
		
		for (int i = 0;i < hids.length + (has48header != -1 ? 1 : 0);i++) {
			Object header = null;
			if (i == hids.length) { header = hs.getHeader (hids[has48header]); i = has48header; }
			else if (hids[i] == 0x48 || hids[i] == 0x49) { has48header = i; continue; }
			else header = hs.getHeader(hids[i]);
			try {
				bos.write (hids[i]);
				switch (hids[i]) {
				case HeaderSetImpl.COUNT:
				case HeaderSetImpl.LENGTH:	
					writeLen (bos, ((Long)header).longValue());
					break;
				case HeaderSetImpl.NAME:
				case HeaderSetImpl.DESCRIPTION:
					String nameObj = (String)header;

					//To handle 0-lengh names in some DIR-Listing cases.
					if (nameObj.equals("")) {
						writeShortLen (bos, 3);
						break;
					}
					
					//UTF-8 -> UTF-16BE convertion some J2ME implementations do not handle
					//UTF-16BE encoding. 
					byte[] b = nameObj.getBytes("UTF-8");
					byte[] b2 = new byte[b.length * 2 + 2];
					for (int j = 0;j < b2.length;j++) b2[j] = 0;
					for (int j = 0;j < b.length;j++) b2[j * 2 + 1] = b[j];
					writeShortLen (bos, 3 + b2.length);
					if (b2.length > 0) bos.write (b2);
					break;
				case HeaderSetImpl.TYPE:
					String typeObj = (String)header;
					writeShortLen (bos, 3 + (typeObj.length() + 1));
					bos.write (typeObj.getBytes ("iso-8859-1"));
					bos.write (new byte[] { 0 });
					break;
				default:
					byte http[] = (byte[])header;
					writeShortLen (bos, 3 + http.length);
					bos.write (http);
					break;
				}
			} catch (IOException e) { e.printStackTrace(); }
			if (i == has48header) break;
		}
		return bos.toByteArray();
	}

	static protected HeaderSetImpl parseHeaders (byte[] data, int offset) {
		HeaderSetImpl hs = (HeaderSetImpl)new HeaderSetImpl();

		while (offset < data.length) {
			int id = (int)data[offset] & 0xff;
			int len = (int)(0xffff & (((0xff & data[offset + 1]) << 8) | (data[offset + 2] & 0xff)));
			switch (id) {
			case HeaderSetImpl.COUNT:
			case HeaderSetImpl.LENGTH:	
			case HeaderSetImpl.CONNECTION_ID:
				hs.setHeader(id, new Long(parseLong (data, offset + 1)));
				len = 5;
				break;
			case HeaderSetImpl.NAME:
			case HeaderSetImpl.DESCRIPTION:
				try {
					byte d2[] = new byte[(len - 2 - 3) / 2];
					for (int i = 0;i < d2.length;i++) d2[i] = data[offset + 3 + 1 + i * 2];
					String nameObj = new String (d2, "UTF-8");
					hs.setHeader(id, nameObj);
					break;
				} catch (Exception e) { System.err.println(len + " " + offset + " " + data.length); e.printStackTrace(); }
				break;
			case HeaderSetImpl.TYPE:
				try {
					String typeObj = null;
//					 Fix for implementations that do not end the TYPE Header with a 0-byte
					if (data[offset + len - 1] != 0) 
						typeObj = new String (data, offset + 3, len - 3,	"iso-8859-1");
					else
						typeObj = new String (data, offset + 3, len - 1 - 3, "iso-8859-1");
					hs.setHeader(id, typeObj);
					break;
				} catch (Exception e) { e.printStackTrace(); }
				break;
			default:
				if (len <= 3) break;
				try {
					byte http[] = new byte[len - 3];
					System.arraycopy (data, offset + 3, http, 0, len - 3);
					hs.setHeader (id, http);
				} catch (Exception e) {
					System.err.println ("Error in parsing " + Integer.toHexString(id));
					for (int i = offset;i < data.length;i++)
						System.err.print (Integer.toHexString((int)(data[i] & 0xff)) + " ");
					System.err.println();
				}
				break;
			}
			offset += len;
		} 
		return hs;
	}
	
	/**
	 * @param bos
	 * @param length
	 */
	static private void writeShortLen(ByteArrayOutputStream bos, int v) {
		byte[] b = new byte[2];
		b[0] = (byte) ((v >> 8) & 0xff);
		b[1] = (byte) (v & 0xff);
		try { bos.write (b); } catch (Exception e) { e.printStackTrace();  }		
	}
	
	public int getMTU() {
		return mtu;
	}
	
	public Authenticator getAuthenticator() {
		return auth;
	}

}
