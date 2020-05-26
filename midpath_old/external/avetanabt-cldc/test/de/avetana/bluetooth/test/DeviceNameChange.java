package de.avetana.bluetooth.test;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

import de.avetana.bluetooth.stack.BlueZ;

public class DeviceNameChange {

	/**
	 * @param args
	 * @throws BluetoothStateException 
	 */
	public static void main(String[] args) throws BluetoothStateException {
		LocalDevice.getLocalDevice();
		boolean ret = BlueZ.setDeviceName(args[0]);
		System.out.println ("Name changed to " + args[0] + " -> " + ret);
		
	}

}
