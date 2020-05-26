package de.avetana.bluetooth.test;

import java.io.IOException;

import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.L2CAPConnectionNotifier;
import javax.microedition.io.Connector;

public class L2ThreadReceiveTest extends Thread {

	private L2CAPConnection l2ccon;
	private int tn = 0;

	public L2ThreadReceiveTest (L2CAPConnection sc, int tn) throws Exception {
		this.l2ccon = sc;
		this.tn = tn;
	}
	
	public void run() {
		try {
			byte[] b = new byte[100];
			int i = 0;
			while (i < 3) {
				if (l2ccon.ready()) {
					l2ccon.receive (b);
					System.out.println ("Thread " + tn + " received data");
					i++;
				}
				synchronized (this) { wait (100); }
			}
			l2ccon.close();
		} catch (Exception e) {
			try {
				l2ccon.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println ("Thread " + tn + " closed");

	}
	
	public static void main(String args[]) throws Exception {
		L2CAPConnectionNotifier scNot = (L2CAPConnectionNotifier)Connector.open ("btl2cap://localhost:00112233445566778899aabbccddeeff;name=test");
		
		int tn = 0;
		while (tn < 5) {
			tn++;
			System.out.println ("Waiting for connection " + tn);
			L2CAPConnection sc = scNot.acceptAndOpen();
			System.out.println ("Connnected " + tn);
			new L2ThreadReceiveTest (sc, tn).start();
		}
		scNot.close();
		
	}
}
