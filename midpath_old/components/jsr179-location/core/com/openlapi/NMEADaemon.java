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

import thinktank.j2me.TTUtils;

/**
 * Generic NMEA-reading {@link Runnable} which is intended to run in a background
 * {@link Thread}. Takes a {@link LocationBackendListener} during construction and will
 * update it with {@link Location} objects as they become available. May be stopped by
 * calling {@link #end()}, which will also close the input stream. Cannot be restarted
 * once stopped.
 * 
 * @see http://en.wikipedia.org/wiki/NMEA_0183
 */
final class NMEADaemon implements Runnable {

	private volatile boolean end = false;

	private final InputStream input;

	private final LocationBackendListener listener;

	private volatile boolean running = false;

	private volatile boolean seenGPGGA = false;

	/**
	 * @param listener
	 * @param input
	 * @throws NullPointerException
	 *             if either parameter (except output) is null.
	 */
	public NMEADaemon(LocationBackendListener listener, InputStream input) {
		if ((listener == null) || (input == null))
			throw new NullPointerException();

		this.listener = listener;
		this.input = input;
	}

	/**
	 * Signal the daemon to stop at the next appropriate moment.
	 */
	public void end() {
		synchronized (this) {
			if (end)
				// already ending in another thread
				return;
			// stop the loop in run
			end = true;
		}
		// close the input
		try {
			input.close();
		} catch (IOException e) {
		}
		// let the listener know that we are out of action.
		listener.updateState(LocationProvider.OUT_OF_SERVICE);
	}

	/**
	 * @return true if this is running.
	 */
	public boolean isRunning() {
		return running;
	}

	public void run() {
		// only one thread permitted
		synchronized (this) {
			if (running)
				return;
			// we are now running
			running = true;
		}

		String line;
		while (!end) {
			try {
				line = TTUtils.readLine(input);
				if (line == null) {
					end();
					break;
				}
			} catch (IOException e) {
				TTUtils.logWarning("IOException reading NMEA " + e.getMessage());
				end();
				break;
			}
			Location location = parseNMEA(line);
			if (location != null) {
				// avoid a minor race condition with end() saying we are OUT_OF_SERVICE
				if (!end)
					listener.updateState(LocationProvider.AVAILABLE);
				listener.updateLocation(location);
			}
		}
	}

	/**
	 * Calculates the checksum of a NMEA sentence and compares it to the one in the
	 * sentence.
	 * <p>
	 * The checksum is the 8-bit exclusive OR (no start or stop bits) of all characters in
	 * the sentence, including the "," delimiters, between (but not including) the "$" and
	 * "*" delimiters. *
	 * 
	 * @param sentence
	 *            NMEA sentence, may contain starting '$' or ending newlines.
	 * @return true if the checksum passes, otherwise false
	 * @see http://www.garmin.com/support/faqs/faq.jsp?faq=40
	 */
	private boolean isNMEAValid(String sentence) {
		int sum = 0;
		String recvSum = null;
		for (int i = 0; i < sentence.length(); i++) {
			char c = sentence.charAt(i);
			if (c == '$') {
				continue;
			}
			if (c == '*') {
				try {
					recvSum = sentence.substring(i + 1, i + 3);
				} catch (IndexOutOfBoundsException e) {
					return false;
				}
				break;
			}
			sum = sum ^ c;
		}
		if (recvSum == null)
			return false;
		// specification is vague on case of the checksum string
		recvSum = recvSum.toUpperCase();
		String calcSum = Integer.toHexString(sum).toUpperCase();
		if (calcSum.equals(recvSum))
			return true;
		return false;
	}

	/**
	 * Ignore the date string in the sentence as it will not be synchronised to our clock.
	 * 
	 * @param sentence
	 *            an NMEA sentence
	 * @return a {@link Location} (which may be invalid, as defined in the sentence).
	 *         Malformed (or unrecognised) sentences will return null.
	 */
	private Location parseNMEA(String sentence) {
		// TTUtils.log(sentence);
		if (!isNMEAValid(sentence))
			return null;

		double latitude;
		double longitude;
		float altitude = Float.NaN;
		boolean valid = false;

		if (sentence.startsWith("$GPGGA")) {
			if (!seenGPGGA)
				seenGPGGA = true;

			// GGA - essential fix data which provide 3D location and accuracy
			// $GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47
			String[] fields = TTUtils.stringSplit(sentence, ',');
			// check validity. 1 or 2 for valid, 0 for invalid
			if (!"0".equals(fields[6])) {
				valid = true;
			}
			latitude = readNMEACoordinate(fields[2], fields[3]);
			longitude = readNMEACoordinate(fields[4], fields[5]);
			// if the height of geoid is missing, the altitude is suspect
			if (!"".equals(fields[11])) {
				altitude = Float.valueOf(fields[9]).floatValue();
			}
		} else if (!seenGPGGA && sentence.startsWith("$GPRMC")) {
			// ignore GPRMC strings if we have observed GPGGA ones
			// as they come at the same time and GPGGA contains more info
			// The Recommended Minimum, which will look similar to:
			// $GPRMC,123519,A,4807.038,N,01131.000,E,022.4,084.4,230394,003.1,W*6A
			String[] fields = TTUtils.stringSplit(sentence, ',');
			// check validity. A for Active, V for inValid
			if ("A".equals(fields[2])) {
				valid = true;
			}
			latitude = readNMEACoordinate(fields[3], fields[4]);
			longitude = readNMEACoordinate(fields[5], fields[6]);
		} else
			// no GGA or RMC to parse
			return null;
		// construct the "heavier" (for J2ME) objects only after confirming we have values
		QualifiedCoordinates qc =
				new QualifiedCoordinates(latitude, longitude, altitude,
					Float.NaN, Float.NaN);
		Location location = new Location();
		location.valid = valid;
		location.extraInfo_NMEA = sentence;
		location.timestamp = System.currentTimeMillis();
		location.locationMethod = Location.MTE_SATELLITE;
		location.qualifiedCoordinates = qc;
		// TTUtils.log("Seen a " + (valid ? "" : "non-") + "valid NMEA: " + location.extraInfo_NMEA);
		return location;
	}

	/**
	 * Reads an NMEA coordinate pair of strings, such as 4807.038,N or 01131.000,E and
	 * returns the double point representation.
	 * 
	 * @param decimal
	 *            e.g. 4807.038 or 01131.000
	 * @param bearing
	 *            e.g. N or W
	 * @return
	 */
	private double readNMEACoordinate(String decimal, String bearing) {
		// first determine if this is a longitude or a latitude
		String coordString = null;
		if ("N".equals(bearing) || "S".equals(bearing)) {
			coordString = decimal.substring(0, 2) + ":" + decimal.substring(2);
		} else {
			coordString = decimal.substring(0, 3) + ":" + decimal.substring(3);
		}
		double coord = Coordinates.convert(coordString);
		// if it's opposite bearing, reverse the sign
		if ("S".equals(bearing) || "W".equals(bearing)) {
			coord = 0.0 - coord;
		}
		return coord;
	}

}
