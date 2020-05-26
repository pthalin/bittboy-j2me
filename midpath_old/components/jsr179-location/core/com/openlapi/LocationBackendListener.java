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
 * Used by backend daemons to provide instant feedback on the availability of location
 * information. Closely resembles {@link LocationListener} but not for public use, as
 * there are some subtle differences.
 * 
 * @author Samuel Halliday, ThinkTank Mathematics Limited
 */
interface LocationBackendListener {
	/**
	 * We extracted a {@link Location} from the backend. No defined interval.
	 * 
	 * @param newLocation
	 *            the latest {@link Location} that we extracted. May be invalid, but never
	 *            null.
	 */
	public void updateLocation(Location newLocation);

	/**
	 * We believe we are in a particular state. This does not imply that the state has
	 * changed, just updates our latest belief.
	 * 
	 * @param newState
	 *            one of {@link LocationProvider#OUT_OF_SERVICE},
	 *            {@link LocationProvider#TEMPORARILY_UNAVAILABLE} or
	 *            {@link LocationProvider#AVAILABLE} which reflects the state of this
	 *            daemon. If {@link LocationProvider#OUT_OF_SERVICE} is received, it may
	 *            be possible to recover by discarding the daemon and starting a new one.
	 */
	public void updateState(int newState);
}