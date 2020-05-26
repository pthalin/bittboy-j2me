package de.avetana.bluetooth.test;

import javax.bluetooth.LocalDevice;

import de.avetana.bluetooth.stack.BlueZ;

public class HCITest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		LocalDevice.getLocalDevice();
		
		int code = Integer.parseInt(args[0], 16);
		int[] params = new int[args.length - 1];
		for (int i = 1;i < args.length;i++) {
			params[i - 1] = Integer.parseInt(args[i], 16);
		}
		
		int ret[] = BlueZ.sendHCI(code, params);
		
		for (int i = 0;i < ret.length;i++) {
			System.out.println (ret[i]);
		}
		

	}

}
