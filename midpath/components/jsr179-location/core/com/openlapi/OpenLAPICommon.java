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

import javax.microedition.io.Connector;

/**
 * Some constants and common methods that are used in various places of OpenLAPI.
 */
final class OpenLAPICommon {

	/**
	 * Earth equatorial radius
	 * 
	 * @see http://en.wikipedia.org/wiki/Earth_radius
	 */
	public final static double EARTH_RADIUS = 6378135.0;

	/**
	 * WGS flattening SetPrecision[1/298.257223563, 15]
	 * 
	 * @see http://en.wikipedia.org/wiki/Reference_ellipsoid
	 */
	public final static double FLATTENING = 0.00335281066474748;

	/**
	 * Inverse circumference of earth if the Earth had a radius equal to it's semi minor
	 * axis, times 360.
	 */
	public static final double INV_MINOR_CIRCUMFERENCE =
			360d / (2d * Math.PI * 6356752.3142);

	/**
	 * WGS semi major axis (equatorial axis).
	 * 
	 * @see http://en.wikipedia.org/wiki/Reference_ellipsoid
	 */
	public final static double SEMI_MAJOR = 6378137.0;

	/**
	 * WGS semi minor axis (polar axis)
	 * 
	 * @see http://en.wikipedia.org/wiki/Reference_ellipsoid
	 */
	public final static double SEMI_MINOR = 6356752.3142;

	/**
	 * Helper method for emulating checking Security permissions. The default
	 * implementation does nothing, but this may be edited to better emulate a target
	 * JSR-179 device.
	 * 
	 * @param permission
	 * @throws SecurityException
	 *             if the requested permission is not allowed.
	 */
	public static void testPermission(String permission)
			throws SecurityException {
		// TODO: implement Security here (Emulator specific)
	}

	/**
	 * Opens an input stream from a resource description. If this begins with a '/', it
	 * will be assumed to be a resource file in the MIDlet jar... otherwise we will
	 * attempt to open it using {@link Connector} (i.e. over the network).
	 * 
	 * @param source
	 * @return
	 * @throws LocationException
	 */
	public static InputStream getOpenLAPIResource(String source)
			throws LocationException {
		InputStream input = null;
		if ('/' == source.charAt(0)) {
			input = OpenLAPICommon.class.getResourceAsStream(source);
		} else {
			try {
				input = Connector.openInputStream(source);
			} catch (IOException e) {
				throw new LocationException(e.getMessage());
			}
		}
		if (input == null)
			throw new LocationException("null input stream");
		return input;
	}
}
