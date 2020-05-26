package de.avetana.bluetooth.test;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

import de.avetana.bluetooth.stack.BlueZ;
import de.avetana.bluetooth.stack.BlueZException;

public class AuthTest {

	/**
	 * @param args PIN adr1, adr2.......
	 * @throws BluetoothStateException 
	 * @throws BlueZException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws BluetoothStateException, BlueZException, InterruptedException {
		LocalDevice.getLocalDevice();
		for (int i = 1;i < args.length;i++) {
			BlueZ.unPair(args[i]);
			int ret = BlueZ.authenticate(args[i], args[0]);
			System.out.println ("Authenticated " + ret);
		}
	}

}
