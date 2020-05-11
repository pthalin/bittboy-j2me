/*
 * Created on 04.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test;

import java.io.*;

import javax.bluetooth.UUID;
import javax.microedition.io.OutputConnection;
import javax.microedition.io.StreamConnection;

import de.avetana.bluetooth.connection.Connector;
import de.avetana.bluetooth.rfcomm.RFCommConnectionImpl;
import de.avetana.bluetooth.rfcomm.RFCommConnectionNotifierImpl;

/**
 * @author gmelin
 *
 * 
 */
public class SendReceive {

	public void beClient(String dest) throws Exception {
		RFCommConnectionImpl bs = (RFCommConnectionImpl)Connector.open ("btspp://" + dest);
		communicate (bs);
	}
	
	public void beServer() throws Exception {
		
		UUID myUUID = new UUID("50b2f09b9b7545dca54b15f15e3b4ce9", false);
		RFCommConnectionNotifierImpl localConnectionNotifier = (RFCommConnectionNotifierImpl) Connector.open ("btspp://localhost:" + myUUID + ";name=DaumeN", Connector.READ_WRITE, true);

		RFCommConnectionImpl bluetoothStream = (RFCommConnectionImpl) localConnectionNotifier.acceptAndOpen();

		communicate (bluetoothStream);

	}
	
	public void communicate (RFCommConnectionImpl con) throws IOException {
		PrintStream o = System.out;

		OutputStream os = con.openOutputStream();
		InputStream is = con.openInputStream();

		o.println("?");

		int a, b, c;

		byte send = 0;

		  for (a = 0;; a ++) {

		    for (b = 0; b < 1024; b++) {

		      for (c = 0; c < 512; c++) {

		      os.write(send);
		      o.print(a+"-"+b+"-"+c+" av: "+is.available()+ " wrote: "+send);
		      o.println(" received: "+(byte)is.read());
		      send++;

		    }

 	      os.write(send);
 	      o.print(a+"-"+b+"-"+c+" av: "+is.available()+ " wrote: "+send);
	      send++;

		  }

		}


	}
	
	public static void main(String[] args) throws Exception {
		if (args[0].equals("server")) new SendReceive().beServer();
		else new SendReceive().beClient(args[0]);
	}
}
