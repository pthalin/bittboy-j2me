package de.avetana.bluetooth.test;

import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import de.avetana.bluetooth.sdp.SDPConstants;

public class MainReceiveTest extends Thread {

	private short service;
	private StreamConnectionNotifier scnot = null;
	
	public MainReceiveTest(short uuidDialupNetworking) {
		this.service = uuidDialupNetworking;
//		Thread t = new Thread() {
//			public void run() {
//				if (scnot != null)
//					try {
//						scnot.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//			}
//		};
//		Runtime.getRuntime().addShutdownHook(t);
	}
	
	public void run() {
		try {
			scnot = (StreamConnectionNotifier) Connector.open ("btspp://localhost:" + new UUID ( service));
			final ServiceRecord rec = LocalDevice.getLocalDevice().getRecord(scnot);
			System.out.println("Record " + rec.getConnectionURL(0, false));
			while (true) {
				System.out.println("Now waiting");
				final StreamConnection c = scnot.acceptAndOpen();
				Runnable r = new Runnable() {

					public void run() {
						try {
							Thread.currentThread().sleep (1000);
//							System.out.println("Connected and closed " + rec.getConnectionURL(0, false));
//							c.close();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				};
				new Thread (r).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 * @throws BluetoothStateException 
	 */
	public static void main(String[] args) throws BluetoothStateException {
		LocalDevice.getLocalDevice();
		new MainReceiveTest (SDPConstants.UUID_SERIAL_PORT).start();
//		new MainReceiveTest (SDPConstants.UUID_DIALUP_NETWORKING).start();
	}

}
