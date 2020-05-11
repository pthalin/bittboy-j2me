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
 * This interface represents a listener to events associated with detecting proximity to
 * some registered coordinates. Applications implement this interface and register it with
 * a static method in LocationProvider to obtain notfications when proximity to registered
 * coordinates is detected.
 * <p>
 * This listener is called when the terminal enters the proximity of the registered
 * coordinates. The proximity is defined as the proximity radius around the coordinates
 * combined with the horizontal accuracy of the current sampled location.
 * <p>
 * The listener is called only once when the terminal enters the proximity of the
 * registered coordinates. The registration with these coordinates is cancelled when the
 * listener is called. If the application wants to be notified again about these
 * coordinates, it must re-register the coordinates and the listener.
 */
public interface ProximityListener {

	/**
	 * Called to notify that the state of the proximity monitoring has changed. These
	 * state changes are delivered to the application as soon as possible after the state
	 * of the monitoring changes.
	 * <p>
	 * Regardless of the state, the ProximityListener remains registered until the
	 * application explicitly removes it with LocationProvider.removeProximityListener or
	 * the application exits.
	 * <p>
	 * These state changes may be related to state changes of some location providers, but
	 * this is implementation dependent as implementations can freely choose the method
	 * used to implement this proximity monitoring.
	 *
	 * @param isMonitoringActive
	 *            a boolean indicating the new state of the proximity monitoring. true
	 *            indicates that the proximity monitoring is active and false indicates
	 *            that the proximity monitoring can't be done currently.
	 */
	public void monitoringStateChanged(boolean isMonitoringActive);

	/**
	 * After registering this listener with the LocationProvider, this method will be
	 * called by the platform when the implementation detects that the current location of
	 * the terminal is within the defined proximity radius of the registered coordinates.
	 *
	 * @param coordinates
	 *            the registered coordinates to which proximity has been detected
	 * @param location
	 *            the current location of the terminal monitoringStateChanged
	 */
	public void proximityEvent(Coordinates coordinates, Location location);

}
