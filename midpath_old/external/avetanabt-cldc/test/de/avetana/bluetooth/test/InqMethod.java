package de.avetana.bluetooth.test;

import java.util.*;
import javax.bluetooth.*;

public class InqMethod {

	public synchronized Vector inquireDevices() throws Exception {
		final Vector v = new Vector();
		
		LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				v.addElement(btDevice);
				
			}

			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				// TODO Auto-generated method stub
				
			}

			public void serviceSearchCompleted(int transID, int respCode) {
				// TODO Auto-generated method stub
				
			}

			public void inquiryCompleted(int discType) {
				synchronized (InqMethod.this) { InqMethod.this.notify(); }
			}
			
		});
		
		synchronized (InqMethod.this) { wait(); }
		
		return v;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		InqMethod inqM = new InqMethod();
		Vector v = inqM.inquireDevices();
		
		for (int i = 0;i < v.size();i++) {
			System.out.println ("Devices found " + ((RemoteDevice)v.elementAt(i)).getFriendlyName(false));
		}
		

	}

}
