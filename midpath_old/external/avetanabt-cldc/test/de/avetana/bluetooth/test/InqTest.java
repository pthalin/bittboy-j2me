/*
 * Created on 28.10.2004
 *
 * if the program is called without any parameters, it does an inquiry. 
 * If it is calles with 1 parameter it does a service serach on the device specified (e.g. 000d9305170e). 
 * If it is called with 2 parameters, the second parameter is considered a UUID on which the service search is supposed to be restricted on.
 */
package de.avetana.bluetooth.test;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InqTest implements DiscoveryListener {

	private static final boolean doException = false;
	private boolean searchCompleted = false;
	private Vector devices;
	
	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		devices.addElement(btDevice);
		try {
			System.out.println ("device discovered " + btDevice + " name " + btDevice.getFriendlyName(true) + " majc 0x" + Integer.toHexString(cod.getMajorDeviceClass()) + " minc 0x" + Integer.toHexString(cod.getMinorDeviceClass()) + " sc 0x" + Integer.toHexString(cod.getServiceClasses()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doException) {
			byte[] b = new byte[0];
			b[1] = 0;
		}
	      
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
	 */
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		// TODO Auto-generated method stub
		System.out.println (servRecord.length + " services discovered " + transID);
		
		if (doException) {
			byte[] b = new byte[0];
			b[1] = 0;
		}

		for (int i = 0;i < servRecord.length;i++) {
			try {
				System.out.println ("Record " + (i + 1));
				System.out.println ("" + servRecord[i]);
				} catch (Exception e) { e.printStackTrace(); }
				/*if (servRecord[0] instanceof RemoteServiceRecord) {
					RemoteServiceRecord rsr = (RemoteServiceRecord)servRecord[0];
					if (rsr.raw.length == 0) return;
					for (int j = 0; j < rsr.raw.length;j++) {
						System.out.print (" " + Integer.toHexString(rsr.raw[j] & 0xff));
					}
					System.out.println("\n----------------");
					
					ServiceRecord rsr2;
					try {
						rsr2 = RemoteServiceRecord.createServiceRecord("000000000000", new byte[0][0], new int[] { 0,1,2,3,4,5,6,7,8,9,10, 256 }, rsr.raw);
						byte[] raw2 = MSServiceRecord.getByteArray (rsr2);
						for (int j = 0; j < raw2.length;j++) {
							System.out.print (" " + Integer.toHexString(raw2[j] & 0xff));
						}
						System.out.println();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}*/
 		}
		
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
	 */
	public synchronized void serviceSearchCompleted(int transID, int respCode) {
		// TODO Auto-generated method stub
		System.out.println ("Service search completed " + transID + " / " + respCode);
		searchCompleted = true;
		notifyAll();
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
	 */
	public synchronized void inquiryCompleted(int discType) {
		System.out.println ("Inquiry completed " + discType);
		searchCompleted = true;	
		notifyAll();
		/*try {
			LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, InqTest.this);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}

	public InqTest (String addr, String uuid) throws Exception {
		devices = new Vector();
		System.out.println ("Getting discoveryAgent");
		DiscoveryAgent da = LocalDevice.getLocalDevice().getDiscoveryAgent();
		
		if (addr == null) {
			da.startInquiry(DiscoveryAgent.GIAC, this);
		} else {
			UUID uuids[];
			System.out.println ("Setting up uuids");
			if (uuid == null) uuids = new UUID[] { };
			else if (uuid.length() == 32) uuids = new UUID[] { new UUID (uuid, false) };
			else uuids = new UUID[] { new UUID (uuid, true) };
			System.out.println ("Starting search");
			int transID = da.searchServices(new int[] { 0x05, 0x06, 0x07, 0x08, 0x09, 0x100, 0x303 }, uuids, new RemoteDevice (addr), this);
			System.out.println ("Started " + transID);
			//BufferedReader br = new BufferedReader (new InputStreamReader (System.in));
			//br.readLine();
			//if (!searchCompleted) da.cancelServiceSearch(transID);
			//System.out.println ("Canceled");

/*			Thread.currentThread().sleep(100);
			
			System.out.println ("Interrupting service search " + transID);
			da.cancelServiceSearch(transID);
			Thread.currentThread().sleep(1500);
			System.out.println ("Restarting service search");
			searchCompleted = false;
			transID = da.searchServices(new int[] { 0x05, 0x06, 0x07, 0x08, 0x09, 0x100, 0x303 }, uuids, new RemoteDevice ("000E0799107C"), this);
			*/
		}

			while (!searchCompleted) {
				synchronized (this) { wait(100); }
			}
//			BlueZ.authenticate (((RemoteDevice)devices.get(0)).getBluetoothAddress(), "6624");
		
			System.exit(0);
			
	}
	
	public InqTest(String addr) throws BluetoothStateException, InterruptedException {
		devices = new Vector();
		int loop = 0;
		DiscoveryAgent da = LocalDevice.getLocalDevice().getDiscoveryAgent();
		while (true) {
			System.out.println ("Performing loop " + loop++);
			searchCompleted = false;
			devices.removeAllElements();
			if (addr == null || addr.equals("inq")) {
				da.startInquiry(DiscoveryAgent.GIAC, this);
				synchronized (this) { wait(60000); }
				if (!searchCompleted) {
					System.out.println ("Cancelling inquiry");
					da.cancelInquiry(this);
				}
				if (("" + addr).equals("inq"))
					continue;
			} else
				devices.addElement(new RemoteDevice (addr));
			for (int i = 0;i < devices.size();i++) {
				RemoteDevice rc = (RemoteDevice) devices.elementAt(i);
				searchCompleted = false;
				System.out.println ("Searching services on " + rc.getBluetoothAddress());
				int transID = da.searchServices(new int[] { 0x05, 0x06, 0x07, 0x08, 0x09, 0x100, 0x303 }, new UUID[0], rc, this);
				synchronized (this) { wait(60000); }
				if (!searchCompleted) {
					System.out.println ("Cancelling service search");
					da.cancelServiceSearch(transID);
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
//		while (true) {
//			System.out.println ("Starting inquiry");
			if (args.length == 1 && args[0].equals("loop")) {
				new InqTest(null);
			} else if (args.length == 2 && args[0].equals("loop")) {
				new InqTest(args[1]);
			}
			else
				new InqTest(args.length == 0 ? null : args[0], args.length <= 1 ? null : args[1]);
//		}
	}
}
