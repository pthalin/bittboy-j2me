package de.avetana.bluetooth.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class ServiceFindTest implements DiscoveryListener {

	private boolean inqFinished = false;

	public ServiceFindTest(String uuidS) throws Exception {

		DiscoveryAgent da = LocalDevice.getLocalDevice().getDiscoveryAgent();

		da.startInquiry(DiscoveryAgent.GIAC, this);

		while (!inqFinished) {
			synchronized (this) {
				wait(1000);
			}
		}

		System.out.println("Starting service select");

		UUID uuid = null;
		if (uuidS == null)
			uuid = new UUID(0x1101);
		else
			uuid = new UUID(uuidS, false);
		String url = da.selectService(uuid, ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);

		System.out.println(url);
		
//		StreamConnection scon = (StreamConnection) Connector.open (url);
//		System.out.println("Connection open");
//		DataOutputStream os  = scon.openDataOutputStream();
//		DataInputStream is = scon.openDataInputStream();
//		
//		os.write("a".getBytes());
//		os.flush();
//		
//		int val = 0;
//		while((val = is.read()) > 0)
//			System.out.print((char)val);
		

	}

	public static void main(String[] args) throws Exception {
		new ServiceFindTest(args.length > 0 ? args[0] : null);
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#deviceDiscovered(javax.bluetooth.RemoteDevice, javax.bluetooth.DeviceClass)
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		try {
			System.out.println("deviceDiscovered : " + btDevice.getFriendlyName(true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int, javax.bluetooth.ServiceRecord[])
	 */
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		System.out.println("servicesDiscovered");
		for (int i = 0; i < servRecord.length; i++) {
			System.out.println("service " + i + " "+ servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
		}
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
	 */
	public void serviceSearchCompleted(int transID, int respCode) {
		System.out.println("serviceSearchCompleted");
	}

	/* (non-Javadoc)
	 * @see javax.bluetooth.DiscoveryListener#inquiryCompleted(int)
	 */
	public synchronized void inquiryCompleted(int discType) {
		inqFinished = true;
		notifyAll();
	}
}
