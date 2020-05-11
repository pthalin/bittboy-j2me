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

import java.util.Enumeration;
import java.util.Vector;

/**
 * The Coordinates class represents coordinates as latitude-longitude-altitude values. The
 * latitude and longitude values are expressed in degrees using floating point values. The
 * degrees are in decimal values (rather than minutes/seconds). The coordinates are given
 * using the WGS84 datum.
 * <p>
 * This class also provides convenience methods for converting between a string coordinate
 * representation and the double representation used in this class.
 */
public class Coordinates {
	/**
	 * Identifier for string coordinate representation Degrees, Minutes, decimal fractions
	 * of a minute
	 */
	public static final int DD_MM = 2;

	/**
	 * Identifier for string coordinate representation Degrees, Minutes, Seconds and
	 * decimal fractions of a second
	 */
	public static final int DD_MM_SS = 1;

	/**
	 * Converts a double representation of a coordinate with decimal degrees into a string
	 * representation. There are string syntaxes supported are the same as for the
	 * #convert(String) method. The implementation shall provide as many significant
	 * digits for the decimal fractions as are allowed by the string syntax definition.
	 *
	 * @param coordinate
	 *            a double representation of a coordinate
	 * @param outputType
	 *            identifier of the type of the string representation wanted for output
	 *            The constant {@link #DD_MM_SS} identifies the syntax 1 and the constant
	 *            {@link #DD_MM} identifies the syntax 2.
	 * @throws IllegalArgumentException
	 *             if the outputType is not one of the two costant values defined in this
	 *             class or if the coordinate value is not within the range [-180.0,
	 *             180.0) or is Double.NaN
	 * @return a string representation of the coordinate in a representation indicated by
	 *         the parameter
	 * @see #convert(String)
	 */
	public static String convert(double coordinate, int outputType)
			throws IllegalArgumentException {
		if ((coordinate < -180.0) || (coordinate > 180.0))
			throw new IllegalArgumentException();

		// treat negative values correctly
		int degrees;
		if (coordinate >= 0.0) {
			degrees = (int) Math.floor(coordinate);
		} else {
			degrees = (int) Math.ceil(coordinate);
		}

		// The decimal string
		String DD = Integer.toString(degrees);

		// The minute string
		double decimalFracDegrees = Math.abs((coordinate - degrees) * 100.0);
		int minutes = (int) (Math.floor(decimalFracDegrees * 0.6));
		String MM = Integer.toString(minutes);

		if (outputType == DD_MM_SS) {
			// The seconds string
			double decimalFracMin = (decimalFracDegrees * 0.6 - minutes) * 100.0;
			// Math.round(x) does not exist in CLDC/MIDP but it is equivalent to
			// Math.floor(x + 0.5d)
			int ss = (int) Math.floor(decimalFracMin * 0.6 + 0.5d);
			String SS = Integer.toString(ss);
			if (SS.length() == 1)
				SS = "0" + SS;
			// The decimal fraction part of seconds, up to 3 significant digits
			int decimalFracSec = (int) Math
					.floor((decimalFracMin * 0.6 - ss) * 1000.0 + 0.5d);
			String SS_d = dropTrailingZeros(decimalFracSec);
			String out = DD + ":" + MM;
			// output only significant figures
			if (SS_d != null) {
				out = out + ":" + SS + "." + SS_d;
			} else if (!SS.equals("00")) {
				out = out + ":" + SS;
			}
			return out;
		} else if (outputType == DD_MM) {
			// The decimal fraction part of minutes, up to 5 significant digits
			double decimalFracMin = (decimalFracDegrees * 0.6 - minutes) * 100000.0;
			int ss = (int) Math.floor(decimalFracMin + 0.5d);
			String MM_d = dropTrailingZeros(ss);
			String out = DD + ":" + MM;
			// output only significant figures
			if (MM_d != null) {
				out = out + "." + MM_d;
			}
			return out;
		} else
			throw new IllegalArgumentException();
	}

	/**
	 * Converts a String representation of a coordinate into the double representation as
	 * used in this API. There are two string syntaxes supported:
	 * <p>
	 * 1. Degrees, minutes, seconds and decimal fractions of seconds. This is expressed as
	 * a string complying with the following BNF definition where the degrees are within
	 * the range [-179, 179] and the minutes and seconds are within the range [0, 59], or
	 * the degrees is -180 and the minutes, seconds and decimal fractions are 0:
	 * <p>
	 * coordinate = degrees &quot;:&quot; minutes &quot;:&quot; seconds &quot;.&quot;
	 * decimalfrac | degrees &quot;:&quot; minutes &quot;:&quot; seconds | degrees
	 * &quot;:&quot; minutes<br />
	 * degrees = degreedigits | &quot;-&quot; degreedigits<br />
	 * degreedigits = digit | nonzerodigit digit | &quot;1&quot; digit digit<br />
	 * minutes = minsecfirstdigit digit<br />
	 * seconds = minsecfirstdigit digit<br />
	 * decimalfrac = 1*3digit <br />
	 * digit = &quot;0&quot; | &quot;1&quot; | &quot;2&quot; | &quot;3&quot; |
	 * &quot;4&quot; | &quot;5&quot; | &quot;6&quot; | &quot;7&quot; | &quot;8&quot; |
	 * &quot;9&quot;<br />
	 * nonzerodigit = &quot;1&quot; | &quot;2&quot; | &quot;3&quot; | &quot;4&quot; |
	 * &quot;5&quot; | &quot;6&quot; | &quot;7&quot; | &quot;8&quot; | &quot;9&quot;<br />
	 * minsecfirstdigit = &quot;0&quot; | &quot;1&quot; | &quot;2&quot; | &quot;3&quot; |
	 * &quot;4&quot; | &quot;5&quot;<br />
	 * <p>
	 * 2. Degrees, minutes and decimal fractions of minutes. This is expressed as a string
	 * complying with the following BNF definition where the degrees are within the range
	 * [-179, 179] and the minutes are within the range [0, 59], or the degrees is -180
	 * and the minutes and decimal fractions are 0:
	 * <p>
	 * coordinate = degrees &quot;:&quot; minutes &quot;.&quot; decimalfrac | degrees
	 * &quot;:&quot; minutes<br/> degrees = degreedigits | &quot;-&quot; degreedigits<br/>
	 * degreedigits = digit | nonzerodigit digit | &quot;1&quot; digit digit<br/> minutes =
	 * minsecfirstdigit digit<br/> decimalfrac = 1*5digit<br/> digit = &quot;0&quot; |
	 * &quot;1&quot; | &quot;2&quot; | &quot;3&quot; | &quot;4&quot; | &quot;5&quot; |
	 * &quot;6&quot; | &quot;7&quot; | &quot;8&quot; | &quot;9&quot;<br/> nonzerodigit =
	 * &quot;1&quot; | &quot;2&quot; | &quot;3&quot; | &quot;4&quot; | &quot;5&quot; |
	 * &quot;6&quot; | &quot;7&quot; | &quot;8&quot; | &quot;9&quot;<br/>
	 * minsecfirstdigit = &quot;0&quot; | &quot;1&quot; | &quot;2&quot; | &quot;3&quot; |
	 * &quot;4&quot; | &quot;5&quot;
	 * <p>
	 * For example, for the double value of the coordinate 61.51d, the corresponding
	 * syntax 1 string is "61:30:36" and the corresponding syntax 2 string is "61:30.6".
	 *
	 * @param coordinate
	 *            a String in either of the two representation specified above
	 * @return a double value with decimal degrees that matches the string representation
	 *         given as the parameter
	 * @throws IllegalArgumentException
	 *             if the coordinate input parameter does not comply with the defined
	 *             syntax for the specified types
	 * @throws NullPointerException
	 *             if the coordinate string is null convert
	 */
	public static double convert(String coordinate)
			throws IllegalArgumentException, NullPointerException {
		/*
		 * A much more academic way to do this would be to generate some tree-based parser
		 * code using the BNF definition, but that seems a little too heavyweight for such
		 * short strings.
		 */
		if (coordinate == null)
			throw new NullPointerException();

		/*
		 * We don't have Java 5 regex or split support in Java 1.3, making this task a bit
		 * of a pain to code.
		 */

		/*
		 * First we check that all the characters are valid, whilst also counting the
		 * number of colons and decimal points (we check that colons do not follow
		 * decimals). This allows us to know what type the string is.
		 */
		int length = coordinate.length();
		int colons = 0;
		int decimals = 0;
		for (int i = 0; i < length; i++) {
			char element = coordinate.charAt(i);
			if (!convertIsValidChar(element))
				throw new IllegalArgumentException();
			if (element == ':') {
				if (decimals > 0)
					throw new IllegalArgumentException();
				colons++;
			} else if (element == '.') {
				decimals++;
				if (decimals > 1)
					throw new IllegalArgumentException();
			}
		}

		/*
		 * Then we break the string into its components and parse the individual pieces
		 * (whilst also doing bounds checking). Code looks ugly because there is a lot of
		 * Exception throwing for bad syntax.
		 */
		String[] parts = convertSplit(coordinate);

		try {
			double out = 0.0;
			// the first 2 parts are the same, regardless of type
			int degrees = Integer.valueOf(parts[0]).intValue();
			if ((degrees < -180) || (degrees > 179))
				throw new IllegalArgumentException();
			boolean negative = false;
			if (degrees < 0) {
				negative = true;
				degrees = Math.abs(degrees);
			}

			out += degrees;

			int minutes = Integer.valueOf(parts[1]).intValue();
			if ((minutes < 0) || (minutes > 59))
				throw new IllegalArgumentException();
			out += minutes * 0.1 / 6;

			if (colons == 2) {
				// type 1
				int seconds = Integer.valueOf(parts[2]).intValue();
				if ((seconds < 0) || (seconds > 59))
					throw new IllegalArgumentException();
				// degrees:minutes:seconds
				out += seconds * 0.01 / 36;
				if (decimals == 1) {
					// degrees:minutes:seconds.decimalfrac
					double decimalfrac = Double.valueOf("0." + parts[3])
							.doubleValue();
					// note that spec says this should be 1*3digit, but we don't
					// restrict the digit count
					if ((decimalfrac < 0) || (decimalfrac >= 1))
						throw new IllegalArgumentException();
					out += decimalfrac * 0.01 / 36;
				}
			} else if ((colons == 1) && (decimals == 1)) {
				// type 2
				// degrees:minutes.decimalfrac
				double decimalfrac = Double.valueOf("0." + parts[2])
						.doubleValue();
				// note that spec says this should be 1*5digit, but we don't
				// restrict the digit count
				if ((decimalfrac < 0) || (decimalfrac >= 1))
					throw new IllegalArgumentException();
				out += decimalfrac * 0.1 / 6;
			} else
				throw new IllegalArgumentException();

			if (negative) {
				out = -out;
			}

			// do a final check on bounds
			if ((out < -180.0) || (out >= 180.0))
				throw new IllegalArgumentException();
			return out;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Helper method for {@link #convert(String)}
	 *
	 * @param element
	 * @return
	 */
	private static boolean convertIsValidChar(char element) {
		if ((element == '-') || (element == ':') || (element == '.')
				|| Character.isDigit(element))
			return true;
		return false;
	}

	/**
	 * Helper method for {@link #convert(String)}
	 *
	 * @param in
	 * @return
	 */
	private static String[] convertSplit(String in)
			throws IllegalArgumentException {
		Vector parts = new Vector(4);

		int start = 0;
		int length = in.length();
		for (int i = 0; i <= length; i++) {
			if ((i == length) || (in.charAt(i) == ':') || (in.charAt(i) == '.')) {
				// syntax checking
				if (start - i == 0)
					throw new IllegalArgumentException();
				String part = in.substring(start, i);
				parts.addElement(part);
				start = i + 1;
			}
		}

		// syntax checking
		if ((parts.size() < 2) || (parts.size() > 4))
			throw new IllegalArgumentException();
		// return an array
		String[] partsArray = new String[parts.size()];
		Enumeration en = parts.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			partsArray[i] = (String) en.nextElement();
		}
		return partsArray;
	}

	/**
	 * Takes an integer and removes trailing zeros.
	 *
	 * @param number
	 *            must be positive
	 * @return the number as a String, with trailing zeros removed. Returns null if the
	 *         number was zero or negative.
	 */
	private static String dropTrailingZeros(int number) {
		if (number <= 0)
			return null;
		while ((number % 10) == 0) {
			number = number / 10;
		}
		return Integer.toString(number);
	}

	/**
	 * @param fromLongitude
	 * @param toLongitude
	 * @return true if toLongitude is east of fromLongitude. If both have the same
	 *         longitude, or are 180 degrees away in this plane, report true.
	 */
	private static boolean isEast(double fromLongitude, double toLongitude) {
		double diff = toLongitude - fromLongitude;
		// if the same longitude, report east
		// if equally east/west, report east
		if (((diff >= 0.0) && (diff <= 180.0)) || (diff <= -180.0))
			return true;
		return false;
	}

	/**
	 * @param fromLatitude
	 * @param toLatitude
	 * @return true if toLatitude is north of fromLatitude. If both have the same latitude
	 *         report true.
	 */
	private static boolean isNorth(double fromLatitude, double toLatitude) {
		double diff = toLatitude - fromLatitude;
		// if the same longitiude, report north
		// if equally north/south, report north
		if ((diff >= 0.0) && (diff <= 90.0))
			return true;
		return false;
	}

	/**
	 * The altitude of the location in meters, defined as height above the WGS84
	 * ellipsoid. Float.NaN can be used to indicate that altitude is not known.
	 */
	private float altitude;

	/**
	 * The latitude of the location. Valid range: [-90.0, 90.0]. Positive values indicate
	 * northern latitude and negative values southern latitude.
	 */
	private double latitude;

	/**
	 * The longitude of the location. Valid range: [-180.0, 180.0). Positive values
	 * indicate eastern longitude and negative values western longitude.
	 */
	private double longitude;

	/**
	 * Constructs a new Coordinates object with the values specified. The latitude and
	 * longitude parameters are expressed in degrees using floating point values. The
	 * degrees are in decimal values (rather than minutes/seconds). The coordinate values
	 * always apply to the WGS84 datum.
	 * <p>
	 * The Float.NaN value can be used for altitude to indicate that altitude is not
	 * known.
	 *
	 * @param latitude
	 *            the latitude of the location. Valid range: [-90.0, 90.0]. Positive
	 *            values indicate northern latitude and negative values southern latitude.
	 * @param longitude
	 *            the longitude of the location. Valid range: [-180.0, 180.0). Positive
	 *            values indicate eastern longitude and negative values western longitude.
	 * @param altitude
	 *            the altitude of the location in meters, defined as height above the
	 *            WGS84 ellipsoid. Float.NaN can be used to indicate that altitude is not
	 *            known.
	 * @throws IllegalArgumentException
	 *             if an input parameter is out of the valid range
	 */
	public Coordinates(double latitude, double longitude, float altitude)
			throws IllegalArgumentException {
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
	}

	/**
	 * Calculates the azimuth between the two points according to the ellipsoid model of
	 * WGS84. The azimuth is relative to true north. The Coordinates object on which this
	 * method is called is considered the origin for the calculation and the Coordinates
	 * object passed as a parameter is the destination which the azimuth is calculated to.
	 * When the origin is the North pole and the destination is not the North pole, this
	 * method returns 180.0. When the origin is the South pole and the destination is not
	 * the South pole, this method returns 0.0. If the origin is equal to the destination,
	 * this method returns 0.0. The implementation shall calculate the result as exactly
	 * as it can. However, it is required that the result is within 1 degree of the
	 * correct result.
	 *
	 * @param to
	 *            the Coordinates of the destination
	 * @return the azimuth to the destination in degrees. Result is within the range [0.0,
	 *         360.0).
	 * @throws NullPointerException
	 *             if the parameter is null
	 */
	public float azimuthTo(Coordinates to) {
		return distance2(to, false);
	}

	/**
	 * Calculates the geodetic distance between the two points according to the ellipsoid
	 * model of WGS84. Altitude is neglected from calculations. The implementation shall
	 * calculate this as exactly as it can. However, it is required that the result is
	 * within 0.36% of the correct result.
	 *
	 * @param to
	 *            the Coordinates of the destination
	 * @return the distance to the destination in meters
	 * @throws NullPointerException
	 *             if the parameter is null
	 */
	public float distance(Coordinates to) throws NullPointerException {
		return distance2(to, true);
	}

	/**
	 * Returns the altitude component of this coordinate. Altitude is defined to mean
	 * height above the WGS84 reference ellipsoid. 0.0 means a location at the ellipsoid
	 * surface, negative values mean the location is below the ellipsoid surface,
	 * Float.NaN that the altitude is not available.
	 *
	 * @return the altitude in meters above the reference ellipsoid
	 * @see #setAltitude(float)
	 */
	public float getAltitude() {
		return altitude;
	}

	/**
	 * Returns the latitude component of this coordinate. Positive values indicate
	 * northern latitude and negative values southern latitude. The latitude is given in
	 * WGS84 datum.
	 *
	 * @return the latitude in degrees
	 * @see #setLatitude(double)
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Returns the longitude component of this coordinate. Positive values indicate
	 * eastern longitude and negative values western longitude. The longitude is given in
	 * WGS84 datum.
	 *
	 * @return the longitude in degrees
	 * @see #setLongitude(double)
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the geodetic altitude for this point.
	 *
	 * @param altitude
	 *            the altitude of the location in meters, defined as height above the
	 *            WGS84 ellipsoid. 0.0 means a location at the ellipsoid surface, negative
	 *            values mean the location is below the ellipsoid surface, Float.NaN that
	 *            the altitude is not available
	 * @see #getAltitude()
	 */
	public void setAltitude(float altitude) {
		this.altitude = altitude;
	}

	/**
	 * Sets the geodetic latitude for this point. Latitude is given as a double expressing
	 * the latitude in degrees in the WGS84 datum.
	 *
	 * @param latitude
	 *            the latitude component of this location in degrees. Valid range: [-90.0,
	 *            90.0]
	 * @throws IllegalArgumentException
	 *             if latitude is out of the valid range
	 * @see #getLatitude()
	 */
	public void setLatitude(double latitude) throws IllegalArgumentException {
		if ((latitude < -90.0) || (latitude > 90.0))
			throw new IllegalArgumentException();
		this.latitude = latitude;
	}

	/**
	 * Sets the geodetic longitude for this point. Longitude is given as a double
	 * expressing the longitude in degrees in the WGS84 datum.
	 *
	 * @param longitude
	 *            the longitude of the location in degrees. Valid range: [-180.0, 180.0)
	 * @see #getLongitude()
	 * @throws IllegalArgumentException
	 *             if longitude is out of the valid range
	 */
	public void setLongitude(double longitude) {
		if ((longitude < -180.0) || (longitude >= 180.0))
			throw new IllegalArgumentException();
		this.longitude = longitude;
	}

	/**
	 * CLDC/MIDP lacks Math.atan(). This implementation uses a numerical geometric mean.
	 *
	 * @param z
	 * @return
	 * @see http://mathworld.wolfram.com/InverseTangent.html
	 */
	private double arctan(double z) {
		// special cases
		if (Double.isNaN(z))
			return Double.NaN;
		if (z == 0.0)
			return 0.0;

		// set accuracy rate here, in terms of max iterations or early
		// convergence. This typically converges in under 20 iterations.
		double conv = 1.0E-10;
		int max = 20;

		// a_0 = 1/sqrt( 1 + x^2 )
		double a = 1 / Math.sqrt(1 + z * z);
		// b_0 = 1
		double b = 1.0;

		double diff = 1.0;
		int i;
		for (i = 0; (i < max) && (diff > conv); i++) {
			double oldA = a;
			// a_{i+1} = 1/2 (a_i + b_i)
			a = 0.5 * (a + b);
			diff = Math.abs(a - oldA);
			// b_{i+1} = sqrt( a_{i+1} * b_i )
			b = Math.sqrt(a * b);
		}

		// atan(x), lim n -> infty = x / (sqrt(1+x^2) * a_n)
		double result = z / (Math.sqrt(1 + z * z) * a);
		return result;
	}

	/**
	 * CLDC/MIDP lacks Math.atan2(). This is an implementation using
	 * {@link #arctan(double)} and a logic table for quadrant.
	 * <p>
	 * The point of atan2() is that the signs of both inputs are known to it, so it can
	 * compute the correct quadrant for the angle. For example, atan(1) and atan2(1, 1)
	 * are both pi/4, but atan2(-1, -1) is -3*pi/4.
	 *
	 * @param y
	 * @param x
	 * @return Return atan(y / x), in radians.
	 * @see http://en.wikipedia.org/wiki/Atan2
	 */
	private double arctan2(double y, double x) {
		// subset of the special cases in the Java5 spec
		if ((Double.isNaN(x) || Double.isNaN(y)))
			return Double.NaN;
		if (x == 0.0) {
			if (y == 0.0)
				return 0.0;
			if (y > 0.0)
				return Math.PI * 0.5;
			if (y < 0.0)
				return -Math.PI * 0.5;
		}
		if (y == 0.0) {
			if (x > 0.0)
				return 0.0;
			if (x < 0.0)
				return Math.PI;
		}

		// get the quadrant right
		double atan = arctan(y / x);
		if (x < 0) {
			if (y < 0) {
				atan -= Math.PI;
			} else {
				atan += Math.PI;
			}
		}

		return atan;
	}

	/**
	 * Although there is an analytical solution [Weisstein], this implementation uses the
	 * approximate and well-tested solution as given by Vincenty (1975).
	 *
	 * @param to
	 *            the Coordinates of the destination
	 * @param distance
	 *            boolean value to request the type of output
	 * @return either the distance to the destination in meters or the forward azimuth
	 *         (initial bearing), depending on the value of the the boolean parameter.
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @see Weisstein, Eric W., "Oblate Spheroid Geodesic"
	 *      http://mathworld.wolfram.com/OblateSpheroidGeodesic.html
	 * @see Vincenty, T., "Direct and inverse solutions of geodesics on the ellipsoid with
	 *      application of nested equations", Survey Review, 1975
	 */
	private float distance2(Coordinates to, boolean distance)
			throws NullPointerException {
		if (to == null)
			throw new NullPointerException();

		// at North Pole, all azimuths are 180.0 (unless to is same point)
		if (!distance && (latitude == 90.0) && (to.latitude != 90.0))
			return 180.0f;
		// at South Pole, all azimuths are 0.0 (unless to is same point)
		if (!distance && (latitude == -90.0) && (to.latitude != -90.0))
			return 0.0f;

		// calculate the Radian versions of the coordinates
		double phi1 = Math.toRadians(latitude);
		double lambda1 = Math.toRadians(longitude);
		double phi2 = Math.toRadians(to.latitude);
		double lambda2 = Math.toRadians(to.longitude);
		double L = Math.abs(lambda1 - lambda2);

		// some re-used terms (so we don't recalculate them in each iteration)
		double U1 = arctan((1 - OpenLAPICommon.FLATTENING) * Math.tan(phi1));
		double U2 = arctan((1 - OpenLAPICommon.FLATTENING) * Math.tan(phi2));
		double cosU1 = Math.cos(U1);
		double cosU2 = Math.cos(U2);
		double sinU1 = Math.sin(U1);
		double sinU2 = Math.sin(U2);
		double cosU1sinU2 = cosU1 * sinU2;
		double sinU1cosU2 = sinU1 * cosU2;
		double sinU1sinU2 = sinU1 * sinU2;
		double cosU1cosU2 = cosU1 * cosU2;
		double f_a = (1.0 + OpenLAPICommon.FLATTENING)
				* OpenLAPICommon.FLATTENING / 4.0;
		double f_b = OpenLAPICommon.FLATTENING * 0.1875
				* OpenLAPICommon.FLATTENING;

		// terms we intend to calculate by iteration and use afterward
		double sigma = Double.NaN;
		double cosSigma = Double.NaN;
		double sinSigma = Double.NaN;
		double sin2Sigma = Double.NaN;
		double cos2Alpha = Double.NaN;
		double sin2Alpha = Double.NaN;
		double cos2Sigma_m = Double.NaN;
		double sinLambda = Double.NaN;
		double cosLambda = Double.NaN;
		double twocos22Sigma_m = Double.NaN;

		// find lambda by iterating until it converges (λ_ stores last value)
		double lambda = L;
		double lambda_ = 2 * Math.PI;
		/*
		 * Set the converge value accordingly to the expected accuracy. 5.6e-5 is
		 * equivalent to about 1m accuracy, so we use 1e-9 to get
		 */
		double converge = 1e-9;
		int maxIterations = 10;

		// iterate until we either reach the max iteration count or converge
		int i;
		for (i = 0; (i < maxIterations)
				&& (Math.abs(lambda - lambda_) > converge); i++) {
			lambda_ = lambda;
			sinLambda = Math.sin(lambda);
			cosLambda = Math.cos(lambda);
			sin2Sigma = square(cosU2 * sinLambda)
					+ square(cosU1sinU2 - sinU1cosU2 * cosLambda);
			// here is where we can identify co-incident points
			if (sin2Sigma == 0.0)
				// answer is zero for both distance and azimuth
				return 0.0F;
			sinSigma = Math.sqrt(sin2Sigma);
			cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda;
			sigma = arctan2(sinSigma, cosSigma);
			sin2Alpha = square(cosU1cosU2 * sinLambda / sinSigma);
			cos2Alpha = 1.0 - sin2Alpha;
			cos2Sigma_m = cosSigma - 2.0 * sinU1sinU2 / cos2Alpha;
			// here is where we can identify equatorial lines
			if (Double.isNaN(cos2Sigma_m)) {
				// use a spherical model
				if (distance)
					return (float) (OpenLAPICommon.EARTH_RADIUS * lambda);
				if (isEast(lambda1, lambda2))
					return 90.0f;
				return 270.0f;
			}
			double C = cos2Alpha * (f_a - f_b * cos2Alpha);
			double sinAlpha = Math.sqrt(sin2Alpha);
			twocos22Sigma_m = 2.0 * square(cos2Sigma_m);
			double part1 = C * cosSigma * (twocos22Sigma_m - 1.0);
			double part2 = C * sinSigma * (cos2Sigma_m + part1);
			lambda = L + (1 - C) * OpenLAPICommon.FLATTENING * sinAlpha
					* (sigma + part2);
		}
		double a2 = square(OpenLAPICommon.SEMI_MAJOR);
		double b2 = square(OpenLAPICommon.SEMI_MINOR);
		double u2 = (cos2Alpha * (a2 - b2)) / b2;

		// this code uses u^8 terms, see commented code below for alternative
		double A = 1.0 + u2 / 16384.0
				* (4096.0 + u2 * (-768.0 + u2 * (320.0 - 175.0 * u2)));
		double B = u2 / 1024.0
				* (256.0 + u2 * (-128.0 + u2 * (74.0 - 47.0 * u2)));
		double deltaSigma = B
				* sinSigma
				* (cos2Sigma_m + B
						/ 4.0
						* (cosSigma * (-1.0 + twocos22Sigma_m) - B / 6.0
								* cos2Sigma_m * (-3.0 + 4.0 * sin2Sigma)
								* (-3.0 + 2.0 * twocos22Sigma_m)));

		if (distance) {
			double s = A * OpenLAPICommon.SEMI_MINOR * (sigma - deltaSigma);
			return (float) s;
		}

		double top = cosU2 * sinLambda;
		double bottom = cosU1sinU2 - sinU1cosU2 * cosLambda;
		double alpha1 = arctan2(top, bottom);
		double azimuth = Math.toDegrees(alpha1);
		// atan2 gives -180 to 180, we want 0 to 360
		if (azimuth < 0) {
			azimuth = 360 + azimuth;
		}

		/*
		 * Note that sometimes the atan2() method places the result in the wrong quadrant,
		 * so perform some sanity checks to correct. Note that this is *not* a failing of
		 * this implementation of atan2(), it happens with the Java 5 implementation as
		 * well and is more likely a subtelty of the algorithm.
		 */
		boolean azimuthNorth = false;
		if ((azimuth >= 270) || (azimuth <= 90)) {
			azimuthNorth = true;
		}
		boolean azimuthEast = false;
		if ((azimuth >= 0) || (azimuth <= 180)) {
			azimuthEast = true;
		}
		boolean east = isEast(lambda1, lambda2);
		boolean north = isNorth(phi1, phi2);

		// should be in a left quadrant, but is in a right one
		if (!east && azimuthEast)
			return (float) (360.0 - azimuth);
		// should be in a top quadrant, but is in a bottom one
		if (north && !azimuthNorth)
			return (float) (180.0 - azimuth);

		return (float) azimuth;
	}

	/**
	 * CLDC/MIDP lacks Math.pow(). This is a convenience method to calculate pow(x , 2) or
	 * x * x
	 *
	 * @param x
	 * @return
	 */
	private double square(double x) {
		return x * x;
	}

}