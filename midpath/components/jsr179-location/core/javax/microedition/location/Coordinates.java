/*
 * Copyright ThinkTank Mathematics Limited 2006, 2007
 *
 * This file is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version. ThinkTank Mathematics
 * Limited designates this particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this file.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * You should have received a copy of the "Classpath" exception along with this file. If
 * not, see <http://www.gnu.org/software/classpath/license.html>.
 */
package javax.microedition.location;

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
	 *            a double reprentation of a coordinate
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
		return com.openlapi.Coordinates.convert(coordinate, outputType);
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
		return com.openlapi.Coordinates.convert(coordinate);
	}

	final com.openlapi.Coordinates wrapped;

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
		wrapped = new com.openlapi.Coordinates(latitude, longitude, altitude);
	}

	Coordinates(com.openlapi.Coordinates wrapped) {
		this.wrapped = wrapped;
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
		return wrapped.azimuthTo(to.wrapped);
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
		return wrapped.distance(to.wrapped);
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
		return wrapped.getAltitude();
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
		return wrapped.getLatitude();
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
		return wrapped.getLongitude();
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
		wrapped.setAltitude(altitude);
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
		wrapped.setLatitude(latitude);
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
		wrapped.setLongitude(longitude);
	}

}