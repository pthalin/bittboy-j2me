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
 * <p>
 * The AddressInfo class holds textual address information about a location. Typically the
 * information is e.g. street address. The information is divided into fields (e.g.
 * street, postal code, city, etc.). Defined field constants can be used to retrieve field
 * data.
 * </p>
 * <p>
 * If the value of a field is not available, it is set to null.
 * </p>
 * <p>
 * The names of the fields use terms and definitions that are commonly used e.g. in the
 * United States. Addresses for other countries should map these to the closest
 * corresponding entities used in that country.
 * </p>
 * <p>
 * This class is only a container for the information. The getField method returns the
 * value set for the defined field using the setField method. When the platform
 * implementation returns AddressInfo objects, it MUST ensure that it only returns objects
 * where the parameters have values set as described for their semantics in this class.
 * </p>
 * <p>
 * Below are some typical examples of addresses in different countries and how they map to
 * the AddressInfo fields.
 * </p>
 * <table border="1" cellpadding="5">
 * <tr>
 * <td>AddressInfo Field</td>
 * <td>American Example</td>
 * <td>British Example</td>
 * </tr>
 * <tr>
 * <td>EXTENSION</td>
 * <td>Flat 5</td>
 * <td>The Oaks</td>
 * </tr>
 * <tr>
 * <td>STREET</td>
 * <td>10 Washington Street</td>
 * <td>20 Greenford Court</td>
 * </tr>
 * <tr>
 * <td>POSTAL_CODE</td>
 * <td>12345</td>
 * <td>AB1 9YZ</td>
 * </tr>
 * <tr>
 * <td>CITY</td>
 * <td>Palo Alto</td>
 * <td>Cambridge</td>
 * </tr>
 * <tr>
 * <td>COUNTY</td>
 * <td>Santa Clara County</td>
 * <td>Cambridgeshire</td>
 * </tr>
 * <tr>
 * <td>STATE</td>
 * <td>California</td>
 * <td>England</td>
 * </tr>
 * <tr>
 * <td>COUNTRY</td>
 * <td>United States of America</td>
 * <td>United Kingdom</td>
 * </tr>
 * <tr>
 * <td>COUNTRY_CODE</td>
 * <td>US</td>
 * <td>GB</td>
 * </tr>
 * <tr>
 * <td>DISTRICT</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>BUILDING_NAME</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>BUILDING_FLOOR</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>BUILDING_ROOM</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>BUILDING_ZONE</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>CROSSING1</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>CROSSING2</td>
 * <td></td>
 * <td></td>
 * </tr>
 * <tr>
 * <td>URL</td>
 * <td>http://www.americanurl.com</td>
 * <td>http://britishurl.co.uk</td>
 * </tr>
 * <tr>
 * <td>PHONE_NUMBER</td>
 * <td></td>
 * <td></td>
 * </tr>
 * </table>
 */
public class AddressInfo {

	/**
	 * Address field denoting a building floor.
	 */
	public static final int BUILDING_FLOOR = 11;

	/**
	 * Address field denoting a building name.
	 */
	public static final int BUILDING_NAME = 10;

	/**
	 * Address field denoting a building room.
	 */
	public static final int BUILDING_ROOM = 12;

	/**
	 * Address field denoting a building zone
	 */
	public static final int BUILDING_ZONE = 13;

	/**
	 * Address field denoting town or city name.
	 */
	public static final int CITY = 4;

	/**
	 * Address field denoting country.
	 */
	public static final int COUNTRY = 7;

	/**
	 * Address field denoting country as a two-letter ISO 3166-1 code.
	 */
	public static final int COUNTRY_CODE = 8;

	/**
	 * Address field denoting a county, which is an entity between a state and a city
	 */
	public static final int COUNTY = 5;

	/**
	 * Address field denoting a street in a crossing.
	 */
	public static final int CROSSING1 = 14;

	/**
	 * Address field denoting a street in a crossing.
	 */
	public static final int CROSSING2 = 15;

	/**
	 * Address field denoting a municipal district.
	 */
	public static final int DISTRICT = 9;

	/**
	 * Address field denoting address extension, e.g. flat number.
	 */
	public static final int EXTENSION = 1;

	/**
	 * Address field denoting a phone number for this place.
	 */
	public static final int PHONE_NUMBER = 17;

	/**
	 * Address field denoting zip or postal code.
	 */
	public static final int POSTAL_CODE = 3;

	/**
	 * Address field denoting state or province.
	 */
	public static final int STATE = 6;

	/**
	 * Address field denoting street name and number.
	 */
	public static final int STREET = 2;

	/**
	 * Address field denoting a URL for this place.
	 */
	public static final int URL = 16;

	final com.openlapi.AddressInfo wrapped;

	/**
	 * Constructs an AddressInfo object with all the values of the fields set to null.
	 */
	public AddressInfo() {
		wrapped = new com.openlapi.AddressInfo();
	}

	AddressInfo(com.openlapi.AddressInfo wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * Returns the value of an address field. If the field is not available null is
	 * returned.
	 * <p>
	 * Example: getField(AddressInfo.STREET) might return "113 Broadway" if the location
	 * is on Broadway, New York, or null if not available.
	 *
	 * @param field
	 *            the ID of the field to be retrieved
	 * @return the address field string. If the field is not set, returns null.
	 * @throws IllegalArgumentException
	 *             if the parameter field ID is not one of the constant values defined in
	 *             this class
	 * @see #setField(int, String)
	 */
	public String getField(int field) throws IllegalArgumentException {
		return wrapped.getField(field);
	}

	/**
	 * Sets the value of an address field.
	 *
	 * @param field
	 *            the ID of the field to be set
	 * @param value
	 *            the new value for the field. null is used to indicate that the field has
	 *            no content.
	 * @throws IllegalArgumentException
	 *             if the parameter field ID is not one of the constant values defined in
	 *             this class
	 * @see #getField(int)
	 */
	public void setField(int field, String value)
			throws IllegalArgumentException {
		wrapped.setField(field, value);
	}
}
