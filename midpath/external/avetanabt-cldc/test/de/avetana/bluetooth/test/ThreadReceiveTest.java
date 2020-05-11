package de.avetana.bluetooth.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class ThreadReceiveTest extends Thread {

	private StreamConnection sc;
	private int tn = 0;

	public ThreadReceiveTest (StreamConnection sc, int tn) throws Exception {
		this.sc = sc;
		this.tn = tn;
	}
	
	public void run() {
		try {
			byte[] b = new byte[100];
			InputStream is = sc.openInputStream();
			OutputStream os = sc.openOutputStream();
			int i = 0;
			while (i++ < 3) {
				is.read (b);
				System.out.println ("Thread " + tn + " received data");
			}
			is.close();
			os.close();
			sc.close();
		} catch (Exception e) {
			try {
				sc.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		System.out.println ("Thread " + tn + " closed");

	}
	
	public static void main(String args[]) throws Exception {
		StreamConnectionNotifier scNot = (StreamConnectionNotifier)Connector.open ("btspp://localhost:00112233445566778899aabbccddeeff;name=test");
		
		int tn = 0;
		while (tn < 5) {
			tn++;
			System.out.println ("Waiting for connection " + tn);
			StreamConnection sc = scNot.acceptAndOpen();
			System.out.println ("Connnected " + tn);
			new ThreadReceiveTest (sc, tn).start();
		}
		scNot.close();
		
	}
}
