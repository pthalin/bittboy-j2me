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
 * The QualifiedCoordinates class represents coordinates as latitude-longitude-altitude
 * values that are associated with an accuracy value.
 */
public class QualifiedCoordinates extends Coordinates {

	final com.openlapi.QualifiedCoordinates wrapped;

	/**
	 * Constructs a new QualifiedCoordinates object with the values specified. The
	 * latitude and longitude parameters are expressed in degrees using floating point
	 * values. The degrees are in decimal values (rather than minutes/seconds). The
	 * coordinate values always apply to the WGS84 datum.
	 * <p>
	 * The Float.NaN value can be used for altitude to indicate that altitude is not
	 * known.
	 *
	 * @param latitude
	 *            the latitude of the location. Valid range: [-90.0, 90.0]
	 * @param longitude
	 *            the longitude of the location. Valid range: [-180.0, 180.0)
	 * @param altitude
	 *            the altitude of the location in meters, defined as height above WGS84
	 *            ellipsoid. Float.NaN can be used to indicate that altitude is not known.
	 * @param horizontalAccuracy
	 *            the horizontal accuracy of this location result in meters. Float.NaN can
	 *            be used to indicate that the accuracy is not known. Must be greater or
	 *            equal to 0.
	 * @param verticalAccuracy
	 *            the vertical accuracy of this location result in meters. Float.NaN can
	 *            be used to indicate that the accuracy is not known. Must be greater or
	 *            equal to 0.
	 * @throws IllegalArgumentException
	 *             if an input parameter is out of the valid range
	 */
	public QualifiedCoordinates(double latitude, double longitude,
			float altitude, float horizontalAccuracy, float verticalAccuracy)
			throws IllegalArgumentException {
		this(new com.openlapi.QualifiedCoordinates(latitude, longitude,
				altitude, horizontalAccuracy, verticalAccuracy));
	}

	QualifiedCoordinates(com.openlapi.QualifiedCoordinates wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}

	/**
	 * Returns the horizontal accuracy of the location in meters (1-sigma standard
	 * deviation). A value of Float.NaN means the horizontal accuracy could not be
	 * determined. The horizontal accuracy is the RMS (root mean square) of east accuracy
	 * (latitudinal error in meters, 1-sigma standard deviation), north accuracy
	 * (longitudinal error in meters, 1-sigma).
	 *
	 * @return the horizontal accuracy in meters. Float.NaN if this is not known
	 */
	public float getHorizontalAccuracy() {
		return wrapped.getHorizontalAccuracy();
	}

	/**
	 * Returns the accuracy of the location in meters in vertical direction (orthogonal to
	 * ellipsoid surface, 1-sigma standard deviation). A value of Float.NaN means the
	 * vertical accuracy could not be determined.
	 *
	 * @return the vertical accuracy in meters. Float.NaN if this is not known.
	 */
	public float getVerticalAccuracy() {
		return wrapped.getVerticalAccuracy();
	}

	/**
	 * Sets the horizontal accuracy of the location in meters (1-sigma standard
	 * deviation). A value of Float.NaN means the horizontal accuracy could not be
	 * determined. The horizontal accuracy is the RMS (root mean square) of east accuracy
	 * (latitudinal error in meters, 1-sigma standard deviation), north accuracy
	 * (longitudinal error in meters, 1-sigma).
	 *
	 * @param horizontalAccuracy
	 *            the horizontal accuracy of this location result in meters. Float.NaN
	 *            means the horizontal accuracy could not be determined. Must be greater
	 *            or equal to 0.
	 * @throws IllegalArgumentException
	 *             if the parameter is less than 0
	 */
	public void setHorizontalAccuracy(float horizontalAccuracy)
			throws IllegalArgumentException {
		wrapped.setHorizontalAccuracy(horizontalAccuracy);
	}

	/**
	 * Sets the accuracy of the location in meters in vertical direction (orthogonal to
	 * ellipsoid surface, 1-sigma standard deviation). A value of Float.NaN means the
	 * vertical accuracy could not be determined.
	 *
	 * @param verticalAccuracy
	 *            the vertical accuracy of this location result in meters. Float.NaN means
	 *            the horizontal accuracy could not be determined. Must be greater or
	 *            equal to 0.
	 * @throws IllegalArgumentException
	 *             if the parameter is less than 0
	 */
	public void setVerticalAccuracy(float verticalAccuracy)
			throws IllegalArgumentException {
		wrapped.setVerticalAccuracy(verticalAccuracy);
	}
}
