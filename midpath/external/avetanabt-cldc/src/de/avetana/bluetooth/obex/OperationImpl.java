/*
 * Created on 25.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.obex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.obex.HeaderSet;
import javax.obex.Operation;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OperationImpl implements Operation {

	private CommandHandler con;
	private HeaderSet hs;
	private HeaderSet recHeaders;
	private OutputStream oos;
	private InputStream ois;
	private int respCode = 0;
	private long len = -1;
	private String type = null;
	private boolean closed = false;
	private int opCode;
	private boolean opNeedsClosing = false;
	
	protected OperationImpl (CommandHandler con, HeaderSet hs, int opCode) throws IOException {
		this.con = con;
		this.hs = hs;
		if ((opCode & 0x7f) == OBEXConnection.PUT) oos = new OBEXPutOutputStream();
		else oos = new OBEXGetOutputStream();
		ois = new OBEXInputStream();
		
		this.opCode = opCode;
		if (hs.getHeader(0x49) != null && opCode == OBEXConnection.PUT) opCode = OBEXConnection.CLOSE;
		//Only Client connections initiate the Commands
		if (con instanceof OBEXConnection) {
			con.sendCommand(opCode == OBEXConnection.GET ? (opCode | 0x80) : opCode, OBEXConnection.hsToByteArray(hs));
			byte[] b = con.receiveCommand();
			
			HeaderSet rechs = OBEXConnection.parseHeaders(b, 3);

			//handler an authenticationResponse in the response to connect
			byte[] authResp = (byte[])rechs.getHeader(HeaderSetImpl.AUTH_RESPONSE);
			if (authResp != null && con.getAuthenticator() != null) {
				((OBEXConnection)con).handleAuthResponse(authResp, con.getAuthenticator());
			}

			//If an authentication challenge is in the response, get the authentication response from the authenticator and try again.
			byte authChallenge[] = (byte[])rechs.getHeader(HeaderSetImpl.AUTH_CHALLENGE);
			if (authChallenge != null && con.getAuthenticator() != null) {
				byte[] resp = HeaderSetImpl.createAuthResponse(authChallenge, con.getAuthenticator());

				hs.setHeader(HeaderSetImpl.AUTH_RESPONSE, resp);
				con.sendCommand(opCode == OBEXConnection.GET ? (opCode | 0x80) : opCode, OBEXConnection.hsToByteArray(hs));
				b = con.receiveCommand();
				//System.out.println ("Returning from connect");
				rechs =  OBEXConnection.parseHeaders (b, 3);
			}

			respCode = (int)b[0] & 0xff;
			if (b[0] != (byte)0x90 && b[0] != (byte)0xa0) throw new IOException ("Command not accepted " + Integer.toHexString(b[0] & 0xff) + "(" +  Integer.toHexString(opCode & 0xff) + ")");
			newData (rechs);

			if (opCode != OBEXConnection.CLOSE && opCode != OBEXConnection.GET) opNeedsClosing = true;
			
			//If the data was marked end of body or the return code was 0xa0 don't
			//need to start a receiver thread.
			
			if (opCode == OBEXConnection.GET && b[0] != (byte)0xa0) {
				receive();
			}
			this.hs = con.createHeaderSet();
		} else { // The command must come from the SessionNotifierImpl -> Save the headers received
			recHeaders = hs;
		}
	}
	
	private void receive() throws IOException {
		while (true) {
				con.sendCommand ((OBEXConnection.GET | 0x80), new byte[0]);
				byte b[] = con.receiveCommand();
				respCode = (int)(b[0] & 0xff);
				newData (OBEXConnection.parseHeaders(b, 3));
				if (b[0] == (byte)0xa0) break;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see javax.obex.Operation#abort()
	 */
	public void abort() throws IOException {
		try {
			con.sendCommand(OBEXConnection.ABORT, new byte[] { 0x49, 0x00, 0x03 });
			byte[] b = con.receiveCommand();
			respCode = (int)b[0] & 0xff;
		} catch (IOException e) {
			e.printStackTrace() ;
		}
	}

	/* (non-Javadoc)
	 * @see javax.obex.Operation#getReceivedHeaders()
	 */
	public HeaderSet getReceivedHeaders() throws IOException {
		if (recHeaders == null) recHeaders = con.createHeaderSet();
		return recHeaders;
	}

	/* (non-Javadoc)
	 * @see javax.obex.Operation#sendHeaders(javax.obex.HeaderSet)
	 */
	public void sendHeaders(HeaderSet headers) throws IOException {
		this.hs = headers;
	}

	protected HeaderSet getHeadersToSend() {
		if (hs == null) hs = con.createHeaderSet();
		return hs;
	}
	/* (non-Javadoc)
	 * @see javax.obex.Operation#getResponseCode()
	 */
	public int getResponseCode() throws IOException {
		return respCode;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.ContentConnection#getEncoding()
	 */
	public String getEncoding() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.ContentConnection#getLength()
	 */
	public long getLength() {
		return len;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.ContentConnection#getType()
	 */
	public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.InputConnection#openDataInputStream()
	 */
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream (ois);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.InputConnection#openInputStream()
	 */
	public InputStream openInputStream() throws IOException {
		return ois;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.OutputConnection#openDataOutputStream()
	 */
	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream (oos);
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.OutputConnection#openOutputStream()
	 */
	public OutputStream openOutputStream() throws IOException {
		return oos;
	}

	/* (non-Javadoc)
	 * @see javax.microedition.io.Connection#close()
	 */
	public void close() {
		if (!opNeedsClosing) return;
		try {
			//Only relevant for Client-GET Operations
			this.con.sendCommand(OBEXConnection.CLOSE, new byte[] { 0x49, 0x00, 0x03 });
			byte b[] = this.con.receiveCommand();
			respCode = (int)b[0] & 0xff;
		} catch (IOException e) {
			e.printStackTrace() ;
		}
	}
	
	class OBEXPutOutputStream extends OutputStream {

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			write (new byte[] { (byte)(b & 0xff) });
		}
		
		public  void write (byte b[]) throws IOException {
			write (b, 0, b.length);
		}
		
		public synchronized void write (byte b[], int off, int len) throws IOException {
			
			byte[] hsba = hs != null ? OBEXConnection.hsToByteArray(hs) : new byte[0];
			
			while (len + 6 + hsba.length > con.getMTU()) {
				write (b, off, con.getMTU() - 6 - hsba.length);
				off += con.getMTU() - 6 - hsba.length;
				len -= con.getMTU() - 6 - hsba.length;
			}
			
			byte[] d;
			
			if (OperationImpl.this.hs == null) hs = con.createHeaderSet();
			d = new byte[len];
			System.arraycopy(b, off, d, 0, len);
			hs.setHeader(0x48, d);
			byte[] b2 = OBEXConnection.hsToByteArray(hs);
			con.sendCommand (OBEXConnection.PUT, b2);
			hs = null;
			//System.out.print ("Send put " + b2.length + " " + con.getMTU());
			//for (int i = 0;i < b2.length && i < 30;i++) 
			//	System.out.print (" " + Integer.toHexString((int)(b2[i] & 0xff)));
			//System.out.println ();
			d = con.receiveCommand();
			//System.out.println ("Answer " + d);
			if (d[0] != (byte)0x90) throw new IOException ("Error while sending PUT command " + Integer.toHexString(d[0] & 0xff));
			respCode = (int)d[0] & 0xff;
		}
		
	}
	
	protected void newData (HeaderSet header) throws IOException {
		if (recHeaders == null) recHeaders = con.createHeaderSet();
		int[] hids = new int[0];
		try {
			hids = header.getHeaderList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0;i < hids.length;i++) {
			if (hids[i] == 0x48 || hids[i] == 0x49) { ((OBEXInputStream)ois).addData((byte[])header.getHeader(hids[i])); continue; }
			else if (hids[i] == HeaderSet.LENGTH) len = ((Long)header.getHeader(HeaderSet.LENGTH)).longValue();
			else if (hids[i] == HeaderSet.TYPE) type = (String)header.getHeader (HeaderSet.TYPE);
			recHeaders.setHeader (hids[i], header.getHeader(hids[i]));
			//System.out.println ("New data with header " + Integer.toHexString(hids[i]));
		}
	}

	 /**
	   * An own extension of the classical java InputStream class.
	   * @author Moritz Gmelin
	   */
	  protected class OBEXInputStream extends InputStream {

	    byte[] buffer = new byte[100];
	    int readPos = 0, writePos = 0;

	    public synchronized int available () {
	      return closed ? 0 : writePos - readPos;
	    }

	    public synchronized void addData(byte[] b) {
	      while (writePos + b.length > buffer.length) {
	        byte[] b2 = new byte[buffer.length * 2];
	        System.arraycopy(buffer, readPos, b2, 0, writePos - readPos);
	        buffer = b2;
	        writePos -= readPos;
	        readPos = 0;
	      }
	      System.arraycopy(b, 0, buffer, writePos, b.length);
	      writePos += b.length;
	      this.notify();
	    }

	    private synchronized void waitForData() throws IOException {
	      while (writePos <= readPos) {
	        if (closed == true) throw new IOException("Connection closed");
	        try {
	          this.wait(50);
	        }
	        catch (Exception e) {
	        }
	      }
	    }

	    public synchronized int read() throws IOException {
	      if (closed == true) throw new IOException("Connection closed");
	      if (opCode == OBEXConnection.GET && available() == 0) return -1;
	      if (opCode == OBEXConnection.PUT && available() == 0) return -1;
	      waitForData();
	      return (int)(buffer[readPos++] & 0xff);
	    }

	    public synchronized int read (byte[] b, int off, int len) throws IOException {
	      if (closed == true) throw new IOException("Connection closed");;
	      if (opCode == OBEXConnection.GET && available() == 0) return -1;
	      if (opCode == OBEXConnection.PUT && available() == 0) return -1;
	      waitForData();
	      int av = available();
	      int r = av > b.length - off ? b.length - off : av;
	      r = r > len ? len : r;
	      System.arraycopy(buffer, readPos, b, off, r);
	      readPos += r;
	      return r;
	    }

	    public void close() {
	      closed = true;
	    }

	    public void reset() {
	      readPos = writePos = 0;
	    }

	  }
	  
	  class OBEXGetOutputStream extends OutputStream {

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			((OBEXInputStream)ois).addData(new byte[] { (byte)b });
		}
		
		public void write (byte[] b, int offset, int len) throws IOException {
			byte[] b2 = new byte[len];
			System.arraycopy(b, offset, b2, 0, len);
			((OBEXInputStream)ois).addData(b2);
		}
		
		public void write (byte b2[]) {
			((OBEXInputStream)ois).addData(b2);		
		}
	  	
	  }

}
