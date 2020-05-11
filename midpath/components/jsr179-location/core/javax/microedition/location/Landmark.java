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
 * The Landmark class represents a landmark, i.e. a known location with a name. A landmark
 * has a name by which it is known to the end user, a textual description,
 * QualifiedCoordinates and optionally AddressInfo.
 * <p>
 * This class is only a container for the information. The constructor does not validate
 * the parameters passed in but just stores the values, except the name field is never
 * allowed to be null. The get* methods return the values passed in the constructor or if
 * the values are later modified by calling the set* methods, the get* methods return the
 * modified values. The QualifiedCoordinates object inside the landmark is a mutable
 * object and the Landmark object holds only a reference to it. Therefore, it is possible
 * to modify the QualifiedCoordinates object inside the Landmark object by calling the
 * set* methods in the QualifiedCoordinates object. However, any such dynamic
 * modifications affect only the Landmark object instance, but MUST not automatically
 * update the persistent landmark information in the landmark store. The
 * LandmarkStore.updateLandmark method is the only way to commit the modifications to the
 * persistent landmark store.
 * <p>
 * When the platform implementation returns Landmark objects, it MUST ensure that it only
 * returns objects where the parameters have values set as described for their semantics
 * in this class.
 */
public class Landmark {

	final com.openlapi.Landmark wrapped;

	/**
	 * Constructs a new Landmark object with the values specified.
	 *
	 * @param name
	 *            the name of the landmark
	 * @param description
	 *            description of the landmark. May be null if not available.
	 * @param coordinates
	 *            the Coordinates of the landmark. May be null if not known.
	 * @param addressInfo
	 *            the textual address information of the landmark. May be null if not
	 *            known.
	 * @throws NullPointerException
	 *             if the name is null
	 */
	public Landmark(String name, String description,
			QualifiedCoordinates coordinates, AddressInfo addressInfo)
			throws NullPointerException {
		wrapped = new com.openlapi.Landmark(name, description,
				coordinates.wrapped, addressInfo.wrapped);
	}

	Landmark(com.openlapi.Landmark wrapped) {
		this.wrapped = wrapped;
	}

	/**
	 * Gets the AddressInfo of the landmark.
	 *
	 * @return the AddressInfo of the landmark.
	 * @see #setAddressInfo(AddressInfo)
	 */
	public AddressInfo getAddressInfo() {
		return new AddressInfo(wrapped.getAddressInfo());
	}

	/**
	 * Gets the landmark description.
	 *
	 * @return the description of the landmark, null if not available.
	 * @see #setDescription(String)
	 */
	public String getDescription() {
		return wrapped.getDescription();
	}

	/**
	 * Gets the landmark name.
	 *
	 * @return the name of the landmark.
	 * @see #setName(String)
	 */
	public String getName() {
		return wrapped.getName();
	}

	/**
	 * Gets the QualifiedCoordinates of the landmark.
	 *
	 * @return the QualifiedCoordinates of the landmark. null if not available.
	 * @see #setQualifiedCoordinates(QualifiedCoordinates)
	 */
	public QualifiedCoordinates getQualifiedCoordinates() {
		return new QualifiedCoordinates(wrapped.getQualifiedCoordinates());
	}

	/**
	 * Sets the AddressInfo of the landmark.
	 *
	 * @param addressInfo
	 *            the AddressInfo of the landmark
	 * @see #getAddressInfo()
	 */
	public void setAddressInfo(AddressInfo addressInfo) {
		wrapped.setAddressInfo(addressInfo.wrapped);
	}

	/**
	 * Sets the description of the landmark.
	 *
	 * @param description
	 *            description for the landmark, null may be passed in to indicate that
	 *            description is not available.
	 * @see #getDescription()
	 */
	public void setDescription(String description) {
		wrapped.setDescription(description);
	}

	/**
	 * Sets the name of the landmark.
	 *
	 * @param name
	 *            name for the landmark
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @see #getName()
	 */
	public void setName(String name) throws NullPointerException {
		wrapped.setName(name);
	}

	/**
	 * Sets the QualifiedCoordinates of the landmark.
	 *
	 * @param coordinates
	 *            the qualified coordinates of the landmark
	 * @see #getQualifiedCoordinates()
	 */
	public void setQualifiedCoordinates(QualifiedCoordinates coordinates) {
		wrapped.setQualifiedCoordinates(coordinates.wrapped);
	}
}
