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
 * All of the {@link LocationProvider} implementations in OpenLAPI share common code, here
 * is the place where we store it. This also makes creating new implementations somewhat
 * easier as there is not so much boilerplate to worry about.
 * <p>
 * This class should not be used directly by applications.
 * 
 * @author Samuel Halliday, ThinkTank Mathematics Limited
 */
public abstract class LocationProviderSimplified extends LocationProvider
		implements LocationBackendListener {

	// this is a count of the number of Threads calling the getLocation method
	// equivalent to the count within a semaphore
	private int countGetLocation = 0;

	private volatile boolean interrupt;

	private LocationListener listener = null;

	private volatile LocationDaemon locDaemon = null;

	private volatile int state = TEMPORARILY_UNAVAILABLE;

	public final Location getLocation(int timeout) throws LocationException,
			InterruptedException, SecurityException, IllegalArgumentException {
		interrupt = false;
		OpenLAPICommon.testPermission("javax.microedition.location.Location");
		if (timeout <= 0)
			throw new IllegalArgumentException();
		if (state == OUT_OF_SERVICE)
			throw new LocationException("Out of service");

		/*
		 * Get the last known location and if it is no older than timeout, return it
		 * immediately. Note that this is OpenLAPI being liberal with the definition in
		 * JSR-179, which does not explicitly state that the timestamp must be newer than
		 * when this method is called.
		 */
		long time = System.currentTimeMillis();
		Location location = getLastKnownLocation();
		long last = location.getTimestamp();
		if ((last + 1000L * timeout) >= time)
			return location;

		try {
			synchronized (this) {
				countGetLocation++;
				// start up the backend if it's not already running
				startBackend();
			}
			// set the time when we should timeout
			long timeoutMillis = 1000L * timeout + time;
			// set the period between checks to allow for 10 checks in total
			// note that Thread.sleep takes milliseconds, but timeout is in seconds
			long period = 100L * timeout;
			// loop until we timeout or we get a valid fix on our location
			while (System.currentTimeMillis() <= timeoutMillis) {
				if (interrupt)
					throw new InterruptedException(
						"getLocation() was interrupted by reset()");

				// now periodically poll the last known location,
				// which will be set by the background worker thread
				location = getLastKnownLocation();

				// ensure Location is newer than what was known before calling this method
				if (!location.isValid() || (location.getTimestamp() <= last)) {
					Thread.sleep(period);
					continue;
				}
				return location;
			}
			// fail
			throw new LocationException("Timed out");
		} finally {
			synchronized (this) {
				countGetLocation--;
				maybeStopBackend();
			}
		}
	}

	public final int getState() {
		return state;
	}

	public final synchronized void reset() {
		interrupt = true;
		stopBackend();
	}

	// synchronised to avoid all kinds of nasty race conditions with starting and
	// stopping various daemons
	public final synchronized void setLocationListener(
			LocationListener listener, int interval, int timeout, int maxAge) {
		this.listener = listener;
		if (listener != null)
			try {
				startBackend();
			} catch (LocationException e) {
				// provider temporarily failed to start
			}
		else
			maybeStopBackend();

		alertProximityListeners((listener == null) ? false : true);

		if (locDaemon != null) {
			// tell the current listen thread to change
			locDaemon.update(listener, interval, timeout, maxAge);
			return;
		} else if (listener != null) {
			// first listener registered, start a new thread
			locDaemon =
					new LocationDaemon(listener, interval, timeout, maxAge,
						this);
			new Thread(locDaemon).start();
		}
	}

	public final void updateLocation(Location newLocation) {
		setLastKnownLocation(newLocation);
	}

	public final synchronized void updateState(int newState) {
		if (state == newState)
			return;
		state = newState;
		if (listener != null)
			listener.providerStateChanged(this, newState);
	}

	/**
	 * Stop the backend if there are no threads calling {@link #getLocation(int)} and
	 * there are no {@link LocationListener} instances registered with this provider. This
	 * method must be called within a block synchronized on this instance, as should all
	 * methods dealing with the state of the backend.
	 */
	private void maybeStopBackend() {
		if ((listener != null) || (countGetLocation > 0))
			return;
		stopBackend();
	}

	/**
	 * Start a new backend in a background thread. Ignore if a backend is currently
	 * running. Always called from a block that is synchronized on this.
	 * 
	 * @throws LocationException
	 *             if the backend could not be started
	 */
	abstract protected void startBackend() throws LocationException;

	/**
	 * Stop the backend that is currently running. Ignore if no backend is currently
	 * running. Always called from a block that is synchronized on this.
	 * 
	 * @throws LocationException
	 *             if the backend could not be stopped
	 */
	protected abstract void stopBackend();

}
