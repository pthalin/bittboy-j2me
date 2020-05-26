package de.avetana.bluetooth.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BTProxy {

	public class PipeStream extends Thread {

		private InputStream in;
		private OutputStream out;
		private String name;

		public PipeStream (InputStream in, OutputStream out, String string) {
			this.in = in;
			this.out = out;
			this.name = string;
		}
		
		public void run() {
			byte[] b = new byte[1000];
			while (true) {
				try {
					int r = in.read (b);
					if (r == -1) throw new Exception ("Connection ended");
					out.write (b, 0, r);
					System.out.println ("Stream " + name + " forwarded " + r + " bytes.");
				} catch (Exception e) {
					try { in.close();
					out.close(); } catch (Exception e2) { e2.printStackTrace(); }
					break;
				}
			}
		}
		
	}

	private StreamConnectionNotifier sconNot;
	private StreamConnection scon;

	public BTProxy(String string) throws Exception {
		sconNot = (StreamConnectionNotifier)Connector.open ("btspp://localhost:00112233445566778899aabbccddeeff;name=proxy");

		System.out.println ("Waiting for incoming connection on url...." + LocalDevice.getLocalDevice().getRecord(sconNot).getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
		scon = sconNot.acceptAndOpen();
		System.out.println ("connected!");

		InputStream inIn = scon.openInputStream();
		OutputStream inOut = scon.openOutputStream();

		try {
			
			System.out.println ("Opening outbound connection to " + string);
			StreamConnection sconout = (StreamConnection) Connector.open ("btspp://" + string);
			System.out.println ("Outbound open");
			
			InputStream outIn = sconout.openInputStream();
			OutputStream outOut = sconout.openOutputStream();

			
			try {

				Thread t1 = new PipeStream (inIn, outOut, "1");
				t1.start();
				Thread t2 = new PipeStream (outIn, inOut, "2");
				t2.start();
				
				t1.join();
				t2.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			outIn.close();
			outOut.close();
			sconout.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		scon.close();
		sconNot.close();

	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new BTProxy (args.length == 0 ? "00A0961DCA99:1" : args[0]);

	}

}
