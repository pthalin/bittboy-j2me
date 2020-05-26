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
 * The Location class represents the standard set of basic location information. This
 * includes the timestamped coordinates, accuracy, speed, course, and information about
 * the positioning method used for the location, plus an optional textual address.
 * <p>
 * The location method is indicated using a bit field. The individual bits are defined
 * using constants in this class. This bit field is a bitwise combination of the location
 * method technology bits (MTE_*), method type (MTY_*) and method assistance information
 * (MTA_*). All other bits in the 32 bit integer than those that have defined constants in
 * this class are reserved and MUST not be set by implementations (i.e. these bits must be
 * 0).
 * <p>
 * A Location object may be either 'valid' or 'invalid'. The validity can be queried using
 * the isValid method. A valid Location object represents a location with valid
 * coordinates and the getQualifiedCoordinates method must return there coordinates. An
 * invalid Location object doesn't have valid coordinates, but the extra info that is
 * obtained from the getExtraInfo method can provide information about the reason why it
 * was not possible to provide a valid Location. For an invalid Location object, the
 * getQualifiedCoordinates method may return either null or some coordinates where the
 * information is not necessarily fully correct. The periodic location updates to the
 * LocationListener may return invalid Location objects if it isn't possible to determine
 * the location.
 * <p>
 * This class is only a container for the information. When the platform implementation
 * returns Location objects, it MUST ensure that it only returns objects where the
 * parameters have values set as described for their semantics in this class.
 */
public class Location {

	/**
	 * Location method is assisted by the other party (Terminal assisted for Network
	 * based, Network assisted for terminal based). MTA_ASSISTED = 0x00040000
	 */
	public static final int MTA_ASSISTED = 0x00040000;

	/**
	 * Location method is unassisted. This bit and MTA_ASSISTED bit MUST NOT both be set.
	 * Only one of these bits may be set or neither to indicate that the assistance
	 * information is not known. MTA_UNASSISTED = 0x00080000
	 */
	public static final int MTA_UNASSISTED = 0x00080000;

	/**
	 * Location method Angle of Arrival for cellular / terrestrial RF system.
	 * MTE_ANGLEOFARRIVAL = 0x00000020
	 */
	public static final int MTE_ANGLEOFARRIVAL = 0x00000020;

	/**
	 * Location method Cell-ID for cellular (in GSM, this is the same as CGI, Cell Global
	 * Identity). MTE_CELLID = 0x00000008
	 */
	public static final int MTE_CELLID = 0x00000008;

	/**
	 * Location method using satellites (for example, Global Positioning System (GPS)).
	 * MTE_SATELLITE = 0x00000001
	 */
	public static final int MTE_SATELLITE = 0x00000001;

	/**
	 * Location method Short-range positioning system (for example, Bluetooth LP).
	 * MTE_SHORTRANGE = 0x00000010
	 */
	public static final int MTE_SHORTRANGE = 0x00000010;

	/**
	 * Location method Time Difference for cellular / terrestrial RF system (for example,
	 * Enhanced Observed Time Difference (E-OTD) for GSM). MTE_TIMEDIFFERENCE = 0x00000002
	 */
	public static final int MTE_TIMEDIFFERENCE = 0x00000002;

	/**
	 * Location method Time of Arrival (TOA) for cellular / terrestrial RF system.
	 * MTE_TIMEOFARRIVAL = 0x00000004
	 */
	public static final int MTE_TIMEOFARRIVAL = 0x00000004;

	/**
	 * Location method is of type network based. This means that the final location result
	 * is calculated in the network. This bit and MTY_TERMINALBASED bit MUST NOT both be
	 * set. Only one of these bits may be set or neither to indicate that it is not known
	 * where the result is calculated. MTY_NETWORKBASED = 0x00020000
	 */
	public static final int MTY_NETWORKBASED = 0x00020000;

	/**
	 * Location method is of type terminal based. This means that the final location
	 * result is calculated in the terminal. MTY_TERMINALBASED = 0x00010000
	 */
	public static final int MTY_TERMINALBASED = 0x00010000;

	final com.openlapi.Location wrapped;

	/**
	 * A protected constructor for the Location to allow implementations of
	 * LocationProviders to construct the Location instances. This method is not intended
	 * to be used by applications.
	 * <p>
	 * This constructor sets the fields to implementation specific default values.
	 * Location providers are expected to set the fields to the correct values after
	 * constructing the object instance.
	 */
	protected Location() {
		throw new RuntimeException("Sorry, I can't let you do that Dave...");
	}

	Location(com.openlapi.Location wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * Returns the AddressInfo associated with this Location object. If no address is
	 * available, null is returned.
	 *
	 * @return an AddressInfo associated with this Location object
	 */
	public AddressInfo getAddressInfo() {
		return new AddressInfo(wrapped.getAddressInfo());
	}

	/**
	 * Returns the terminal's course made good in degrees relative to true north. The
	 * value is always in the range [0.0,360.0) degrees.
	 *
	 * @return the terminal's course made good in degrees relative to true north or
	 *         Float.NaN if the course is not known
	 */
	public float getCourse() {
		return wrapped.getCourse();
	}

	/**
	 * Returns extra information about the location. This method is intended to provide
	 * location method specific extra information that applications that are aware of the
	 * used location method and information format are able to use. A MIME type is used to
	 * identify the type of the extra information when requesting it. If the
	 * implementation supports this type, it returns the extra information as a String
	 * encoded according to format identified by the MIME type. If the implementation does
	 * not support this type, the method returns null.
	 * <p>
	 * This specification does not require implementations to support any extra
	 * information type.
	 * <p>
	 * The following MIME types are defined here together with their definitions in order
	 * to ensure interoperability of implementations wishing to use these types. The
	 * definition of these types here is not an indication that these formats are
	 * preferred over any other format not defined here.
	 * <p>
	 * When the MIME type is "application/X-jsr179-location-nmea", the returned string
	 * shall be a valid sequence of NMEA sentences formatted according to the syntax
	 * specified in the NMEA 0183 v3.1 specification. These sentences shall represent the
	 * set of NMEA sentences that are related to this location at the time this location
	 * was created.
	 * <p>
	 * When the MIME type is "application/X-jsr179-location-lif", the returned string
	 * shall contain an XML formatted document containing the "pd" element defined in the
	 * LIF Mobile Location Protocol TS 101 v3.0.0 as the root element of the document.
	 * <p>
	 * When the MIME type is "text/plain", the returned string shall contain textual extra
	 * information that can be displayed to the end user.
	 *
	 * @param mimetype
	 *            the MIME type of the requested extra information
	 * @return string encoded according to the format identified by the MIME type defined
	 *         in the parameter. null if the information for the requested MIME type is
	 *         not available or not supported by this implementation.
	 */
	public String getExtraInfo(String mimetype) {
		return wrapped.getExtraInfo(mimetype);
	}

	/**
	 * Returns information about the location method used. The returned value is a bitwise
	 * combination (OR) of the method technology, method type and assistance information.
	 * The method technology values are defined as constant values named MTE_* in this
	 * class, the method type values are named MTY_* and assistance information values are
	 * named MTA_*. For example, if the location method used is terminal based, network
	 * assisted E-OTD, the value 0x00050002 ( = MTY_TERMINALBASED | MTA_ASSISTED |
	 * MTE_TIMEDIFFERENCE) would be returned.
	 * <p>
	 * If the location is determined by combining several location technologies, the
	 * returned value may have several MTE_* bits set.
	 * <p>
	 * If the used location method is unknown, the returned value must have all the bits
	 * set to zero.
	 * <p>
	 * Only bits that have defined constants within this class are allowed to be used.
	 * Other bits are reserved and must be set to 0.
	 *
	 * @return a bitfield identifying the used location method
	 */
	public int getLocationMethod() {
		return wrapped.getLocationMethod();
	}

	/**
	 * Returns the coordinates of this location and their accuracy.
	 *
	 * @return a QualifiedCoordinates object. If the coordinates are not known, returns
	 *         null.
	 */
	public QualifiedCoordinates getQualifiedCoordinates() {
		return new QualifiedCoordinates(wrapped.getQualifiedCoordinates());
	}

	/**
	 * Returns the terminal's current ground speed in meters per second (m/s) at the time
	 * of measurement. The speed is always a non-negative value. Note that unlike the
	 * coordinates, speed does not have an associated accuracy because the methods used to
	 * determine the speed typically are not able to indicate the accuracy.
	 *
	 * @return the current ground speed in m/s for the terminal or Float.NaN if the speed
	 *         is not known
	 */
	public float getSpeed() {
		return wrapped.getSpeed();
	}

	/**
	 * Returns the time stamp at which the data was collected. This timestamp should
	 * represent the point in time when the measurements were made. Implementations make
	 * best effort to set the timestamp as close to this point in time as possible. The
	 * time returned is the time of the local clock in the terminal in milliseconds using
	 * the same clock and same time representation as System.currentTimeMillis().
	 *
	 * @return a timestamp representing the time
	 */
	public long getTimestamp() {
		return wrapped.getTimestamp();
	}

	/**
	 * Returns whether this Location instance represents a valid location with coordinates
	 * or an invalid one where all the data, especially the latitude and longitude
	 * coordinates, may not be present. A valid Location object contains valid coordinates
	 * whereas an invalid Location object may not contain valid coordinates but may
	 * contain other information via the getExtraInfo() method to provide information on
	 * why it was not possible to provide a valid Location object.
	 *
	 * @return a boolean value with true indicating that this Location instance is valid
	 *         and false indicating an invalid Location instance
	 * @see #getExtraInfo(String)
	 */
	public boolean isValid() {
		return wrapped.isValid();
	}

	/**
	 * Sets the terminal's course, ensuring that the value is always in the range
	 * [0.0,360.0) degrees.
	 *
	 * @param course
	 *            the course to set.
	 * @throws IllegalArgumentException
	 *             if the course is outside the range.
	 */
	protected void setCourse(float course) throws IllegalArgumentException {
		throw new RuntimeException("Sorry, I can't let you do that Dave...");
	}

	/**
	 * Set the current ground speed of the location object in m/s.
	 *
	 * @param speed
	 *            must be non-negative or Float.NaN if unknown.
	 * @throws IllegalArgumentException
	 *             if the speed is negative
	 */
	protected void setSpeed(float speed) throws IllegalArgumentException {
		throw new RuntimeException("Sorry, I can't let you do that Dave...");
	}
}
