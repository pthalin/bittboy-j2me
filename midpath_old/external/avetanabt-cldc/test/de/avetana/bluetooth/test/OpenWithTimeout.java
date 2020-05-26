package de.avetana.bluetooth.test;

import java.io.IOException;

import javax.microedition.io.Connection;

import de.avetana.bluetooth.connection.Connector;

public class OpenWithTimeout {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		Connection con = Connector.openWithTimeout(args[0], Integer.parseInt(args[1]));
		System.out.println("Opening successfull");
		con.close();
	}

}
