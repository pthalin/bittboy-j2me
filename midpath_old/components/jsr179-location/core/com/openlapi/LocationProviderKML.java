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
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import thinktank.j2me.TTUtils;

/**
 * A LocationProvider using a Google Earth KML 2.0 file.
 * <p>
 * Locations are saved in the KML file as Placemark elements, and many levels of Folders
 * are allowed (the metadata associated to the Folder elements is simply ignored). If the
 * Placemark has many coordinates, only the first one is used. If the Placemark has a
 * LookAt element, it is used to calculate the course. Locations are discovered using a
 * first element search of the folder structure.
 * <p>
 * When there are multiple Locations in the file, the LocationProvider will return them
 * based on the time the Location is requested... this emulates a moving device. The time
 * interval of the change is set in TIME_INTERVAL.
 * <p>
 * NOTE: KML 2.2 will be supported in a future release and the way that trails are defined
 * may change.
 * 
 * @see http://en.wikipedia.org/wiki/Keyhole_Markup_Language
 */
class LocationProviderKML extends LocationProviderSimplified {

	/**
	 * Given a list of locations, and an interval between them, this class will create
	 * periodic location update events to emulate movement through all the locations in
	 * order (and back again) indefinitely.
	 * 
	 * @author Samuel Halliday, ThinkTank Mathematics Limited
	 */
	class TrailDaemon implements Runnable {
		private volatile boolean end = false;
		private final long interval;
		private final Vector locations;
		private final long startup = System.currentTimeMillis();

		/**
		 * @param locations
		 * @param interval
		 *            in milliseconds
		 */
		TrailDaemon(Vector locations, long interval) {
			this.locations = locations;
			this.interval = interval;
			if (locations.size() == 0)
				updateState(OUT_OF_SERVICE);
			else
				updateState(AVAILABLE);
		}

		public void end() {
			end = true;
			updateState(OUT_OF_SERVICE);
		}

		public void run() {
			while (!end) {
				updateLocation(location());
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
				}
			}
		}

		private Location location() {
			long timestamp = System.currentTimeMillis();
			long time = timestamp - startup;

			// the number of times the location has changed
			int stepsIn = (int) (time / interval);

			// if we overflow, go backwards instead of starting from the beginning
			int total = locations.size();
			int step = stepsIn % (2 * total);
			if (step > total) {
				step = 2 * total - step;
			}
			Location location = (Location) locations.elementAt(step);
			Location newLoc = new Location();
			newLoc.addressInfo = location.addressInfo;
			newLoc.extraInfo_LIF = location.extraInfo_LIF;
			newLoc.extraInfo_NMEA = location.extraInfo_NMEA;
			newLoc.extraInfo_Text = location.extraInfo_Text;
			newLoc.locationMethod = location.locationMethod;
			newLoc.qualifiedCoordinates = location.qualifiedCoordinates;
			newLoc.valid = location.valid;
			newLoc.setCourse(location.getCourse());
			newLoc.setSpeed(location.getSpeed());
			// timestamp is different
			newLoc.timestamp = timestamp;
			return newLoc;
		}
	}

	private TrailDaemon daemon;
	private String source;
	private int pollInterval;

	/**
	 * @param criteria
	 * @param source
	 * @param pollInterval 
	 * @throws LocationException 
	 */
	LocationProviderKML(Criteria criteria, String source, int pollInterval) throws LocationException {
		TTUtils.logInfo("OpenLAPI KML mode");
		this.source = source;
		this.pollInterval = pollInterval;
		startBackend();
	}

	/**
	 * Recursively discover all folders, if a Placemark object is encountered, it is
	 * parsed into a Location abject and stored.
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private Vector findPlacemarks(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Vector locations = new Vector();
		// loop that increments elements in the XML stream
		int event = parser.next();
		for (; event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
			if (event != XmlPullParser.START_TAG) {
				// we're only interested in finding Placemark tags
				continue;
			}
			String name = parser.getName();
			if ("Placemark".equals(name)) {
				// if it's a Placemark parse it, even if it's in a Folder
				Location location = parsePlacemark(parser);
				if (location != null) {
					locations.addElement(location);
				}
			}
			// else ignore it
		}
		return locations;
	}

	/**
	 * Parses a Google Earth coordinates String and returns a Coordinates object. Defines
	 * the exact coordinates of the point location in longitude, latitude, and altitude—in
	 * that precise order. Values are separated by commas. The KML format allows for
	 * multiple coordinates to be set in this field, but we will ignore all after the
	 * first.
	 * 
	 * @param content
	 * @return
	 * @throws IllegalArgumentException
	 *             if there are any parsing errors or the coordinates are out of bounds
	 */
	private QualifiedCoordinates parseCoordinates(String content)
			throws IllegalArgumentException {
		int subStart = 0;
		int p = 0;
		int length = content.length();
		// EclipseME won't allow a double [] array
		Double[] parts = new Double[3];

		for (int i = 0; (i < length) && (p < 3); i++) {
			char character = content.charAt(i);
			if ((character == ',') || (character == ' ') || (i == length - 1)) {
				// end of segment reached
				int end = i;
				if (i == length - 1) {
					end++;
				}
				String part = content.substring(subStart, end);
				parts[p] = Double.valueOf(part);
				p++;
				subStart = i + 1;
			}
		}
		// didn't contain a long/lat/alt triplet
		if (p != 3)
			throw new IllegalArgumentException();

		// note that KML is long/lat/alt, whereas Coordinates is lat/long/alt
		// KML doesn't support horizontal/vertical accuracy
		QualifiedCoordinates qc =
				new QualifiedCoordinates(parts[1].doubleValue(),
					parts[0].doubleValue(), parts[2].floatValue(), Float.NaN,
					Float.NaN);
		return qc;
	}

	/**
	 * To be called immediately after the parser reaches the beginning of a LookAt tag,
	 * this will extract and return the heading after proceeding to the end of the LookAt
	 * element.
	 * 
	 * @return
	 * @throws XmlPullParserException
	 *             if there are any parse errors, including if the entry for heading is
	 *             not a valid floating point number.
	 * @throws IOException
	 */
	private float parseLookAt(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		// keep track of the tag we are in
		Vector tags = new Vector();
		tags.addElement("LookAt");

		float heading = Float.NaN;

		int event = parser.next();
		for (; event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
			if (event == XmlPullParser.START_TAG) {
				String name = parser.getName();
				// mark that we are in a tag
				tags.addElement(name);
				continue;
			} else if (event == XmlPullParser.END_TAG) {
				String name = parser.getName();
				// remove the tag from the list
				tags.removeElement(name);
				if ("LookAt".equals(name))
					// end of LookAt info, return
					return heading;
			}
			if (event == XmlPullParser.TEXT) {
				// we recorded where we are
				String name = (String) tags.lastElement();
				if ("heading".equals(name)) {
					String content = parser.getText();
					try {
						heading = Float.parseFloat(content);
					} catch (IllegalArgumentException e) {
						throw new XmlPullParserException(e.getMessage());
					}
				}
			}
		}
		// we get here if the tag never closed
		throw new XmlPullParserException("expected </LookAt>");
	}

	/**
	 * Parse a Placemark element into a Location. Moves the parser to the end of the
	 * Placemark element.
	 * 
	 * @param element
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private Location parsePlacemark(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Location location = new Location();
		location.extraInfo_Text = "OpenLAPI KML emulator mode";
		location.valid = true;

		// keep track of the tag we are in
		Vector tags = new Vector();
		tags.addElement("Placemark");

		int event = parser.next();
		for (; event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
			if (event == XmlPullParser.START_TAG) {
				String name = parser.getName();
				// mark that we are in a tag
				tags.addElement(name);
				continue;
			} else if (event == XmlPullParser.END_TAG) {
				String name = parser.getName();
				// remove the tag from the list
				tags.removeElement(name);
				if ("Placemark".equals(name))
					// end of Placemark info, return
					return location;
			}
			if (event == XmlPullParser.TEXT) {
				// we recorded where we are
				String name = (String) tags.lastElement();
				if ("name".equals(name)) {
					// name, append this info to the plain text extra info
					location.extraInfo_Text =
							location.extraInfo_Text + ": " + parser.getText();
				} else if ("description".equals(name)) {
					// description
				} else if ("LookAt".equals(name)) {
					// LookAt tag holds course info
					location.setCourse(parseLookAt(parser));
					tags.removeElement("LookAt");
				} else if ("Point".equals(name)) {
					// Point
					location.qualifiedCoordinates = parsePoint(parser);
					tags.removeElement("Point");
				}
			}
		}

		// if there were no coordinate, return null
		if (location.qualifiedCoordinates == null)
			return null;

		return location;
	}

	/**
	 * Parse a Point element, returning the QualifiedCoordinates contained within.
	 * 
	 * @param entry
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private QualifiedCoordinates parsePoint(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		QualifiedCoordinates qc = null;

		// keep track of the tag we are in
		Vector tags = new Vector();
		tags.addElement("Point");

		int event = parser.next();
		for (; event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
			if (event == XmlPullParser.START_TAG) {
				String name = parser.getName();
				// mark that we are in a tag
				tags.addElement(name);
				continue;
			} else if (event == XmlPullParser.END_TAG) {
				String name = parser.getName();
				// remove the tag from the list
				tags.removeElement(name);
				if ("Point".equals(name))
					// end of Point info, return
					return qc;
			}
			if (event == XmlPullParser.TEXT) {
				// we recorded where we are
				String name = (String) tags.lastElement();
				String content = parser.getText();
				if ("coordinates".equals(name)) {
					try {
						// unfortunately, the actual data is contained in a
						// coordinates element, so outsource again
						qc = parseCoordinates(content);
					} catch (IllegalArgumentException e) {
						throw new XmlPullParserException(e.getMessage());
					}
				}
			}
		}
		// we get here if the tag never closed
		throw new XmlPullParserException("expected </Point>");
	}

	protected void startBackend() throws LocationException {
		if (daemon != null)
			return;

		InputStream input = OpenLAPICommon.getOpenLAPIResource(source);

		try {
			XmlPullParser parser = new KXmlParser();
			parser.setInput(input, "UTF-8");
			parser.next();
			// confirm that this is a Google KML 2.0 file
			String namespace = parser.getAttributeValue(0);
			if (!"http://earth.google.com/kml/2.0".equals(namespace))
				throw new LocationException(source
						+ " not a valid KML 2.0 file.");

			Vector locations = findPlacemarks(parser);
			
			daemon = new TrailDaemon(locations, pollInterval);
			new Thread(daemon).start();
		} catch (XmlPullParserException e) {
			daemon = null;
			throw new LocationException(e.getMessage());
		} catch (IOException e) {
			daemon = null;
			throw new LocationException(e.getMessage());
		} finally {
			try {
				input.close();
			} catch (IOException e) {
			}
		}
	}

	protected void stopBackend() {
		if (daemon == null)
			return;

		daemon.end();
		daemon = null;
	}
}
