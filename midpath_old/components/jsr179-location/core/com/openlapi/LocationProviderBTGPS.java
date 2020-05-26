/*
 * Copyright ThinkTank Mathematics Limited 2006, 2007
 *
 * This file is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this file.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.openlapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;

import thinktank.j2me.BluetoothDeviceDiscover;
import thinktank.j2me.TTUtils;

/**
 * An implementation of the LocationProvider that accesses a Bluetooth GPS device. Device
 * discovery typically takes about 10 seconds.
 * <p>
 * Note that a connection to a running GPS device takes about 30 seconds before valid data
 * starts coming through.
 */
final class LocationProviderBTGPS extends LocationProviderSimplified {

	private volatile NMEADaemon daemon = null;
	private volatile boolean starting = false;

	/**
	 * @param criteria
	 * @param output
	 *            if NMEA logging is required
	 * @throws LocationException 
	 */
	LocationProviderBTGPS(Criteria criteria) throws LocationException {
		TTUtils.logInfo("OpenLAPI GPS mode");
		startBackend();
	}

	private void connectToDevice(RemoteDevice device) throws IOException {
		// make the connection and receive the InputStream
		String uri =
				"btspp://" + device.getBluetoothAddress()
						+ ":1;authenticate=false;master=false;encrypt=false";
		InputStream stream = Connector.openInputStream(uri);
		daemon = new NMEADaemon(this, stream);
		// no need to create a new thread, always called from background
		daemon.run();
		// Thread thread = new Thread(daemon);
		// thread.start();
	}

	protected void startBackend() throws LocationException {
		if (starting || ((daemon != null) && daemon.isRunning()))
			return;
		starting = true;
		// non-blocking, but can't report failures

		// TODO: more control over which devices to connect to.
		// i.e. possibly allow device address setting in config
		// or caching of device addresses, or user selection
		
		Runnable runnable = new Runnable() {
			public void run() {
				BluetoothDeviceDiscover discover =
						new BluetoothDeviceDiscover();
				// this will block for about 10 to 20 seconds
				// so we do this in a new thread to avoid blocking beyond timeouts
				// this has the disadvantage that we can't report failures
				Hashtable devices = discover.discover();
				Enumeration names = devices.keys();
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					TTUtils.logInfo("Seen " + name);
					if (name.indexOf("GPS") != -1)
						try {
							TTUtils.logInfo("connecting to " + name);
							connectToDevice((RemoteDevice) devices.get(name));
							starting = false;
							return;
						} catch (IOException e) {
							// if we fail to connect... maybe there is another device
							TTUtils.logInfo("IOException " + e.getMessage());
						}
				}
				starting = false;
				TTUtils.logInfo("Didn't find any GPS devices.");
			}
		};
		new Thread(runnable).start();
	}

	protected void stopBackend() {
		if (daemon == null || !daemon.isRunning())
			return;

		daemon.end();
		daemon = null;
	}

}
