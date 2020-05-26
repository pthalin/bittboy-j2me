package de.avetana.bluetooth.test;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

public class InitTest {

	/**
	 * @param args
	 * @throws BluetoothStateException 
	 */
	public static void main(String[] args) throws BluetoothStateException {
		LocalDevice.getLocalDevice();
		System.exit(0);
	}

}
