/*
 * Copyright ThinkTank Mathematics Limited 2006 - 2008
 *
 * This file is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */
package thinktank.j2me;

import java.io.IOException;
import java.util.Hashtable;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 * Wrapper that turns the asynchronous Bluetooth Discovery process into a synchronous one.
 * <p>
 * Note that many devices will hang indefinitely if you attempt to connect to a bluetooth
 * device before the discovery process finishes... even if the client attempts to finish
 * the discovery earlier. This class has been tested on many devices and caters for the
 * lowest common denominator, at the cost of penalising the devices that do allow a clean
 * early shutdown of the discovery.
 * 
 * @author Samuel Halliday, ThinkTank Mathematics Limited
 */
public class BluetoothDeviceDiscover {
	/**
	 * The asynchronous listener.
	 */
	private final class BluetoothHelper implements DiscoveryListener {
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
			String name;
			try {
				name = btDevice.getFriendlyName(false);
			} catch (IOException e) {
				name = btDevice.getBluetoothAddress();
			}
			devices.put(name, btDevice);
		}

		public void inquiryCompleted(int discType) {
			finish();
		}

		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			// not looking for services. Dumb interface! grr...
		}

		public void serviceSearchCompleted(int transID, int respCode) {
			// not looking for services. Dumb interface! grr...
		}
	}

	private final Hashtable devices = new Hashtable();

	private boolean finished = false;

	/** Can only run once per instance */
	private volatile boolean ran = false;

	private final Runnable runner = new Runnable() {
		public void run() {
			ran = true;
			LocalDevice localDevice;
			try {
				localDevice = LocalDevice.getLocalDevice();
			} catch (BluetoothStateException e) {
				finish();
				return;
			}
			DiscoveryAgent discoveryAgent = localDevice.getDiscoveryAgent();
			int accessCode = DiscoveryAgent.GIAC;
			try {
				discoveryAgent.startInquiry(accessCode, new BluetoothHelper());
			} catch (BluetoothStateException e) {
				finish();
				// maybe a partial success
			}
		}
	};

	private final Object waiter = new Object();

	/**
	 * @return a map of Strings (of friendly names) to the respective RemoteDevices
	 */
	public Hashtable discover() {
		/*
		 * TODO: timeout by making use of the DiscoveryAgent.stopInquiry method. Defaults
		 * are usually 10 seconds, we may wish to have a 2 second blast. This may be
		 * implemented by devices that it is known to work for.
		 */
		if (ran)
			throw new RuntimeException(
				"Invalid state... already started lookup.");
		Thread thread = new Thread(runner);
		thread.start();
		synchronized (waiter) {
			while (!finished) {
				try {
					waiter.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		return devices;
	}

	private void finish() {
		synchronized (waiter) {
			finished = true;
			waiter.notify();
		}
	}
}