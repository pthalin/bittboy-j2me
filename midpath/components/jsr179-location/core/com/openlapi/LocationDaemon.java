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


/**
 * Periodically polls the instantiating {@link LocationProvider#getLocation(int)} to allow
 * {@link LocationListener}s to receive updates.
 * <p>
 * Listening to LocationProvider state changes is not the responsibility of this class.
 */
class LocationDaemon implements Runnable {

	/**
	 * Runs until it obtains a valid {@link Location}, or the {@link LocationListener} is
	 * de-registered.
	 */
	private class FirstValidLocation implements Runnable {
		public void run() {
			while (!end) {
				try {
					Location location = provider.getLocation(Integer.MAX_VALUE);
					if (!location.isValid())
						continue;
					updateListener(location);
					break;
				} catch (Exception e) {
				}
			}
		}
	}

	// If -1 is passed as the interval, this is used.
	private static final int DEFAULT_LISTEN_INTERVAL = 60000;

	// If -1 is passed as the maxAge, this is used.
	private static final int DEFAULT_MAXAGE = 20000;

	// If -1 is passed as the timeout, this is used.
	private static final int DEFAULT_TIMEOUT = 10;

	// Flag to alert the listener if the parameters have been changed recently.
	private volatile boolean changed = false;

	// Set true when the thread is to finish
	private volatile boolean end = false;

	private int interval;

	private volatile LocationListener listener;

	private int maxAge;

	private final LocationProvider provider;

	private int timeout;

	/**
	 * @param listener
	 * @param interval
	 * @param timeout
	 * @param maxAge
	 * @param provider
	 */
	protected LocationDaemon(LocationListener listener, int interval,
			int timeout, int maxAge, LocationProvider provider) {
		this.provider = provider;
		update(listener, interval, timeout, maxAge);
		changed = false;
	}

	public void run() {
		if (listener == null)
			return;

		// only spawn FirstValidLocation if lastKnownLocation isn't valid
		Location firstLocation = getLocation(0);
		if (firstLocation.isValid()) {
			updateListener(firstLocation);
		} else {
			FirstValidLocation firstLocationThread = new FirstValidLocation();
			new Thread(firstLocationThread).start();
		}

		// thereafter loop the wait and capture process
		while (!end) {
			synchronized (this) {
				if (changed) {
					changed = false;
					continue;
				}
			}
			Location location = getLocation(timeout);
			if (end)
				break;
			updateListener(location);
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
			}
		}
	}

	// Helper method to return the Location, but will grab the last known location if it's
	// new enough. If there were any problems, an invalid Location object is returned.
	// synchronised to encourage contention here, increasing the likelihood of
	// getting a "free" recent lookup.
	private synchronized Location getLocation(int timeout) {
		Location location = LocationProvider.getLastKnownLocation();
		if (location.isValid()) {
			int age =
					(int) (System.currentTimeMillis() - location.getTimestamp());
			if (age <= maxAge)
				return location;
		}

		try {
			return provider.getLocation(timeout);
		} catch (Exception e) {
			return Location.getInvalid();
		}
	}

	// ensures that this operation is always atomic
	private synchronized void updateListener(Location location) {
		if (listener != null)
			listener.locationUpdated(provider, location);
	}

	/**
	 * End all running daemons cleanly.
	 */
	protected void end() {
		end = true;
	}

	/**
	 * Register a new LocationListener and parameters.
	 * 
	 * @param listener
	 * @param interval
	 * @param timeout
	 * @param maxAge
	 */
	protected synchronized void update(LocationListener listener, int interval,
			int timeout, int maxAge) {
		// convert to milliseconds
		maxAge = 1000 * maxAge;
		interval = 1000 * interval;
		if (interval == -1000) {
			interval = DEFAULT_LISTEN_INTERVAL;
		}
		if (maxAge == -1000) {
			maxAge = DEFAULT_MAXAGE;
		}
		if (timeout == -1) {
			// special case: timeout is shorter than interval
			if (DEFAULT_TIMEOUT > (interval / 1000)) {
				timeout = interval / 1000;
			} else {
				timeout = DEFAULT_TIMEOUT;
			}
		}

		this.listener = listener;
		this.interval = interval;
		this.timeout = timeout;
		this.maxAge = maxAge;

		// alert the loop of a change
		changed = true;
		if (listener == null)
			end();
	}
}
