/*
 * Created on 25.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.obex;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.obex.Authenticator;
import javax.obex.HeaderSet;
import javax.obex.PasswordAuthentication;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HeaderSetImpl implements HeaderSet {


    public static final int CONNECTION_ID = 0xCB;
    public static final int AUTH_CHALLENGE = 0x4D;
    public static final int AUTH_RESPONSE = 0x4E;

	private Hashtable headers;
	private int lastResponse = -1;
	
	public HeaderSetImpl() {
		headers = new Hashtable();
	}
	
	/* (non-Javadoc)
	 * @see javax.obex.HeaderSet#setHeader(int, java.lang.Object)
	 */
	public void setHeader(int headerID, Object headerValue) {
		if (headerValue == null) headers.remove(new Integer (headerID));
		else headers.put( new Integer (headerID), headerValue);
	}

	/* (non-Javadoc)
	 * @see javax.obex.HeaderSet#getHeader(int)
	 */
	public Object getHeader(int headerID) {
		return headers.get( new Integer (headerID));
	}

	/* (non-Javadoc)
	 * @see javax.obex.HeaderSet#getHeaderList()
	 */
	public int[] getHeaderList() {
		int v[] = new int[headers.size()];
		Enumeration en = headers.keys();
		boolean has48 = headers.containsKey(new Integer (0x48));
		int j = 0;
		for (int i = 0;i < v.length;i++) { 
			int vi = ((Integer)en.nextElement()).intValue(); 
			if (vi != 0x48) v[j++] = vi;
		}
		if (has48) v[j++] = 0x48;
		return v;
	}

	/* (non-Javadoc)
	 * @see javax.obex.HeaderSet#createAuthenticationChallenge(java.lang.String, boolean, boolean)
	 */
	public void createAuthenticationChallenge(String realm, boolean userID,
			boolean access) {
		
		String nonce = "" + System.currentTimeMillis();
		
		byte option = (byte)((userID ? 1 : 0) | (access ? 2 : 0));
		byte[] b = new byte[18 + 3 + 3 + realm.getBytes().length];
		
		MD5 md5 = new MD5();
		md5.update (nonce.toCharArray(), (long)nonce.length());
		md5.md5final();
		byte[] rbb = md5.toByteArray();
		
		b[0] = 0x0;
		b[1] = 0x10;
		System.arraycopy(rbb, 0, b, 2, 16);
		
		b[18] = 0x01;
		b[19] = 0x01;
		b[20] = option;
		
		b[21] = 0x02;
		try {
			byte[] realmb = realm.getBytes("iso-8859-1");
			b[22] = (byte)(realmb.length + 1);
			b[23] = (byte)1;
			System.arraycopy(realmb, 0, b, 24, realmb.length);
		} catch (Exception e) { e.printStackTrace();}
		
		setHeader (HeaderSetImpl.AUTH_CHALLENGE, b);
	}

	/**
	 * @param hs
	 * @return
	 */
	public static byte[] createAuthResponse (byte[] challenge, Authenticator auth) {

		String nonce = "";
		try {
			nonce = new String (challenge, 2, 16, "iso-8859-1");
		} catch (Exception e) {}
		
		boolean userReq = false, fullAccess = false;
		if ((challenge[20] & 1) == 1) userReq = true;
		if ((challenge[20] & 2) == 2) fullAccess = true;
		
		String desc = "";
		if (challenge.length > 21 && challenge[21] == 0x02) {
			int len = (int)(challenge[22] & 0xff);
			String enc = "ascii";
		
			switch (challenge[23]) {
				case 0: enc = "ascii"; break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9: enc = "iso-8859-" + challenge[23]; break;
				case (byte)0xff: 	enc = "UTF-8"; break;
				default: enc = "iso-8859-1";
			}
			try {
				desc = new String (challenge, 24, len - 1, enc);
			} catch (Exception e) {}
		}
		
		PasswordAuthentication passwd = auth.onAuthenticationChallenge(desc, userReq, fullAccess);
			
		byte resp[] = new byte[18 + (userReq ? passwd.getUserName().length + 2 : 0) + 18];
			
		MD5 md5 = new MD5();
		String requestDigest= nonce + ":" + new String (passwd.getPassword());
		md5.update(requestDigest.toCharArray(), requestDigest.length());
		md5.md5final();

		int off = 0;
		resp[off++] = 0x00;
		resp[off++] = 0x10;
		System.arraycopy(md5.toByteArray(), 0, resp, off, 16); 
		off += 16;
		
		if (userReq) {
			resp[off++] = 0x01;
			resp[off++] = (byte)passwd.getUserName().length;
			System.arraycopy(passwd.getUserName(), 0, resp, off, (int)(resp[off - 1] & 0xff)); 
			off += (int)(resp[off - 1] & 0xff);
		}
		
		resp[off++] = 0x02;
		resp[off++] = 0x10;
		try {
			System.arraycopy(nonce.getBytes("iso-8859-1"), 0, resp, off, 16);
		} catch (Exception e) {}
		
		return resp;
	}

	/* (non-Javadoc)
	 * @see javax.obex.HeaderSet#getResponseCode()
	 */
	public int getResponseCode() throws IOException {
		if (lastResponse == -1) throw new IOException ("No Response code for this HeaderSet");
		return lastResponse;
	}
	
	protected void setRespCode (byte code) {
		lastResponse = (int)(code & 0xff);
	}

}
