package de.avetana.bluetooth.test;

import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;

import de.avetana.bluetooth.stack.BlueZ;
import de.avetana.bluetooth.stack.BlueZException;

public class GetAddressWithoutLicense {

	/**
	 * @param args
	 * @throws BlueZException 
	 */
	public static void main(String[] args) throws BlueZException {
		try {
			LocalDevice.getLocalDevice();
			System.out.println(LocalDevice.getLocalDevice().getBluetoothAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Address of device " + BlueZ.hciDevBTAddress(0));
		// TODO Auto-generated method stub

	}

}
