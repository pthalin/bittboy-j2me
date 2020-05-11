package de.avetana.bluetooth.test;

import java.io.IOException;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ConnectionTester {

	public ConnectionTester(String string) throws Exception{
		if (string == null)
			startServer();
		else 
			startClient (string);
	}

	
	private void startClient(String string) throws IOException, InterruptedException {
		while (true) {
			StreamConnection scon = (StreamConnection) Connector.open (string);
			System.out.println("Connection open");
			int len = 0;
			while (len < 1000) 
				len += scon.openInputStream().read (new byte[1000]);
			System.out.println("Client read bytes " + len);
			scon.openOutputStream().write (new byte[1000]);
			scon.openOutputStream().write (new byte[1000]);
			scon.openOutputStream().write (new byte[1000]);
			scon.openOutputStream().write (new byte[1000]);
			scon.openOutputStream().write (new byte[1000]);
			Thread.currentThread().sleep (2000);
			scon.close();
		}
	}


	private void startServer() throws Exception {
		
		final StreamConnectionNotifier sconNot =  (StreamConnectionNotifier) Connector.open ("btspp://localhost:00112233445566778899aabbccddeeff;name=test");
		
//		Runtime.getRuntime().addShutdownHook(new Thread () {
//			public  void run() {
//				try {
//					sconNot.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
		
		
		while (true) {
			System.out.println("Server reacheable at " + LocalDevice.getLocalDevice().getRecord(sconNot).getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
			final StreamConnection scon = sconNot.acceptAndOpen();
			
			new Thread() {
				public void run() {

					try {
						System.out.println("Connection open");
//						Thread.currentThread().sleep (500);
						scon.openOutputStream().write (new byte[1000]);
						int len = 0;
						while (len < 5000) {
							len += scon.openInputStream().read (new byte[1000]);
						}
						System.out.println("Server Read bytes " + len);
//						Thread.currentThread().sleep (1000);
						scon.close();
						System.out.println("Connection closed");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args[0].equalsIgnoreCase("Server"))
			new ConnectionTester (null);
		else 
			new ConnectionTester (args[0]);
	}

}
