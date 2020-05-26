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
import java.util.Enumeration;
import java.util.Random;

import thinktank.j2me.TTUtils;

/**
 * This implementation of the LocationProvider chooses a random entry from the
 * LandmarkStore every time it is called.
 */
class LocationProviderLMS extends LocationProviderSimplified {

	/**
	 * Given a list of locations, and an interval between them, this class will create
	 * periodic location update events to emulate movement through all the locations in
	 * order (and back again) indefinitely.
	 * 
	 * @author Samuel Halliday, ThinkTank Mathematics Limited
	 */
	class RandomLocationDaemon implements Runnable {
		private volatile boolean end = false;
		private final long interval;

		private final LandmarkStore lms;
		private final Random random = new Random();

		/**
		 * @param lms
		 *            not null
		 * @param interval
		 *            in milliseconds
		 */
		RandomLocationDaemon(LandmarkStore lms, long interval) {
			this.interval = interval;
			this.lms = lms;
		}

		public void end() {
			end = true;
			updateState(OUT_OF_SERVICE);
		}

		public void run() {
			while (!end) {
				updateLocation(getRandomEntry());
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
				}
			}
		}

		private synchronized Location getRandomEntry() {
			if (lms == null)
				return Location.getInvalid();
			try {
				// count how many entries there are
				int count = 0;
				Enumeration en = lms.getLandmarks();
				while (en.hasMoreElements()) {
					en.nextElement();
					count++;
				}
				// pick a random entry
				int entry = random.nextInt(count);
				en = lms.getLandmarks();
				for (int i = 0; en.hasMoreElements(); i++) {
					Landmark landmark = (Landmark) en.nextElement();
					if (i == entry)
						return landmarkToLocation(landmark);
				}
				return Location.getInvalid();
			} catch (IOException e) {
				return Location.getInvalid();
			}
		}
	}

	private volatile RandomLocationDaemon daemon;
	private final String storeName;
	private int pollInterval;

	/**
	 * Create a new LocationProvider that chooses random entries from the LandmarkStore.
	 * 
	 * @param criteria
	 * @param storeName
	 * @param pollInterval 
	 * @throws LocationException
	 */
	LocationProviderLMS(Criteria criteria, String storeName, int pollInterval)
			throws LocationException {
		TTUtils.logInfo("OpenLAPI LMS mode");
		this.storeName = storeName;
		this.pollInterval = pollInterval;
		startBackend();
	}

	private Location landmarkToLocation(Landmark landmark) {
		Location location = new Location();
		location.valid = true;
		location.extraInfo_Text = "OpenLAPI LMS mode: " + landmark.getName();
		location.addressInfo = landmark.getAddressInfo();
		location.qualifiedCoordinates = landmark.getQualifiedCoordinates();
		// set the timestamp to now
		location.timestamp = System.currentTimeMillis();
		return location;
	}

	protected void startBackend() throws LocationException {
		if (daemon != null)
			return;

		LandmarkStore lms = LandmarkStore.getInstance(storeName);
		if (lms == null)
			throw new LocationException("LandmarkStore " + lms + " not found.");

		daemon = new RandomLocationDaemon(lms, pollInterval);
		new Thread(daemon).start();
	}

	protected void stopBackend() {
		if (daemon == null)
			return;

		daemon.end();
		daemon = null;
	}
}