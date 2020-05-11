/*
 * Created on 20.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test;

import javax.microedition.io.*;
import javax.bluetooth.*;

import de.avetana.bluetooth.sdp.SDPConstants;
import java.io.*;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultiAOTest extends Thread{

	int id;
	public MultiAOTest(int id) throws Exception {
		this.id = id;
	}
	
	public void run() {
		try {
			StreamConnectionNotifier scnot = (StreamConnectionNotifier)Connector.open ("btspp://localhost:"+ id + "0112233445566778899aabbccddeeff;name=test" + id);
			System.out.println ("Before acceptAndOpen " + id);
			StreamConnection scon = scnot.acceptAndOpen();
			System.out.println ("acceptAndOpen done " + id);
			InputStream is = scon.openInputStream();
			OutputStream os = scon.openOutputStream();
			System.out.println ("opening of streams done " + id);
			is.read();
			System.out.println ("Stream " + id + " has received data");
			os.write(new byte[100]);
			is.close();
			os.close();
			scon.close();
			System.out.println ("Stream " + id + " closed");
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
		new MultiAOTest(1).start();
		br.readLine();
		new MultiAOTest(2).start();
	}
}
