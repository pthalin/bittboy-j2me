package de.avetana.bluetooth.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.obex.Authenticator;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;

import de.avetana.bluetooth.sdp.SDPConstants;

public class OBEXReceiveTest extends ServerRequestHandler {

	private static boolean diconnected = false;

	public OBEXReceiveTest() throws IOException {
				
	}
	
	public static void main (String args[]) throws IOException, InterruptedException{
		SessionNotifier notify = (SessionNotifier) Connector.open("btgoep://localhost:" + new UUID (SDPConstants.UUID_OBEX_OBJECT_PUSH) + ";name=OBEXTest;authenticate=false;master=false;encrypt=false");
         int secSet = ServiceRecord.NOAUTHENTICATE_NOENCRYPT;

         System.out.println ("ready " + LocalDevice.getLocalDevice().getRecord(notify).getConnectionURL(secSet, false));
		
         while (true) {
        	 	System.out.println ("Waiting for connection");
        	 	notify.acceptAndOpen(new OBEXReceiveTest());
        	 	System.out.println ("Accept and open returned");
         }
        	 	
//        	while (!diconnected) {
//        		Thread.currentThread().sleep(100);
//        	}
	}
	
	public int onConnect (HeaderSet request, HeaderSet response) {
		System.out.println ("RequestHandler got connect");
		
		//response.setHeader(HeaderSetImpl.CONNECTION_ID, new Long(12345));
		
		int retCode = ResponseCodes.OBEX_HTTP_OK;
		//If an authenticationResponseHeader is in the Request and authenticationFailed
		//has not been called by the SessionNofifier, you can assume that authentication was
		//successfull.
		//you could also (alternatively) test whether onAuthenticationResponse has been called
		//and not onAuthenticationFailure
/*
		if (request.getHeader(HeaderSetImpl.AUTH_RESPONSE) != null && authenticationFailed == false)
			retCode = ResponseCodes.OBEX_HTTP_OK;
		else {
			response.createAuthenticationChallenge("Server realm", true, true);
			retCode = ResponseCodes.OBEX_HTTP_UNAUTHORIZED;
		}
		*/
		System.out.println ("Returning ret code " + Integer.toHexString(retCode));
		
		return retCode;
	}
	
	public int onPut (Operation op) {

		try {
			java.io.InputStream is = op.openInputStream();
		System.out.println ("Got data bytes " + is.available() + " name " + op.getReceivedHeaders().getHeader(HeaderSet.NAME) + " type " + op.getType());
		File f = File.createTempFile("obex", ".tmp");
		FileOutputStream fos = new FileOutputStream (f);
		byte b[] = new byte[1000];
		int len;
		while (is.available() > 0 && (len = is.read(b)) > 0) {
			fos.write (b, 0, len);
		}
		fos.close();
		System.out.println ("Wrote data to " + f.getAbsolutePath());
		op.sendHeaders(null);
		} catch (Exception e) { e.printStackTrace(); }
		return 0xa0;
	}
	
	public void onDisconnect (HeaderSet req, HeaderSet resp) {
		System.out.println("Disconnected");
		diconnected = true;
	}
	
	public int onGet (Operation op) {
		
		
		try {
			HeaderSet hs = op.getReceivedHeaders();

			int[] hl = hs.getHeaderList();
			for (int i = 0;i < hl.length;i++) {
				System.out.println ("Received header " + hl[i] + " : " + hs.getHeader(hl[i]));
			}
		op.openOutputStream().write ("Test Message from avetanaBluetooth".getBytes());
		/*InputStream is = new FileInputStream ("/Users/gmelin/eclipse-workspace/avetanaBluetooth/avetanabt/de/avetana/bluetooth/JSRTest.java");
		byte b[] = new byte[1000];
		int len = 0;
		while ((len = is.read (b)) > 0) {
			op.openOutputStream().write(b, 0, len);
			System.out.println ("Added " + len + " bytes to OK");
		}*/
		HeaderSet set = op.getReceivedHeaders();
		set.setHeader(HeaderSet.TYPE, "text/plain");
		set.setHeader(HeaderSet.NAME, "msg.txt");
		op.sendHeaders (set);
		} catch (IOException e) {
			e.printStackTrace();
			return 0xc5;
		}
		return 0xa0;
	}
	
	public void onAuthenticationFailure (byte[] user) {
		System.out.println ("Authentication failed");
	}

}
