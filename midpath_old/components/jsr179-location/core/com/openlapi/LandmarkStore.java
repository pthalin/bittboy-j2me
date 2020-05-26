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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;


/*
 * TODO: memoryless Enumeration of Landmark objects Currently, all Enumerations are
 * Vectors.elements(), but it would be much more scalable if the Enumeration accessed each
 * Landmark as the next element was requested, although this does make the store less
 * stable if there are more than one thread accessing the store.
 */

/**
 * The <code>LandmarkStore</code> class provides methods to store, delete and retrieve
 * landmarks from a persistent landmark store. There is one default landmark store and
 * there may be multiple other named landmark stores. The implementation may support
 * creating and deleting landmark stores by the application. All landmark stores MUST be
 * shared between all J2ME applications and MAY be shared with native applications in the
 * terminal. Named landmark stores have unique names in this API. If the underlying
 * implementation allows multiple landmark stores with the same name, it must present them
 * with unique names in the API e.g. by adding some postfix to those names that have
 * multiple instances in order to differentiate them.
 * <p>
 * Because native landmark stores may be stored as files in a file system and file systems
 * have sometimes limitations for the allowed characters in file names, the
 * implementations MUST support all other Unicode characters in landmark store names
 * except the following list:
 * <ul>
 * <li>0x0000...0x001F control characters</li>
 * <li>0x005C <code>'\'</code></li>
 * <li>0x002F <code>'/'</code></li>
 * <li>0x003A <code>':'</code></li>
 * <li>0x002A <code>'*'</code></li>
 * <li>0x003F <code>'?'</code></li>
 * <li>0x0022 <code>'"'</code></li>
 * <li>0x003C <code>'<'</code></li>
 * <li>0x003E <code>'>'</code></li>
 * <li>0x007C <code>'|'</code></li>
 * <li>0x007F...0x009F control characters</li>
 * <li>0xFEFF Byte-order-mark</li>
 * <li>0xFFF0...0xFFFF</li>
 * </ul>
 * Support for the listed characters is not required and therefore applications are
 * strongly encouraged not to use the characters listed above in landmark store names in
 * order to ensure interoperability of the application on different platform
 * implementations.
 * <p>
 * The <code>Landmark</code>s have a name and may be placed in a category or several
 * categories. The category is intended to group landmarks that are of similar type to the
 * end user, e.g. restaurants, museums, etc. The landmark names are strings that identify
 * the landmark to the end user. The category names describe the category to the end user.
 * The language used in the names may be any and depends on the preferences of the end
 * user. The names of the categories are unique within a <code>LandmarkStore</code>.
 * However, the names of the landmarks are not guaranteed to be unique.
 * <code>Landmark</code>s with the same name can appear in multiple categories or even
 * several <code>Landmark</code>s with the same name in the same category.
 * <p>
 * The <code>Landmark</code> objects returned from the <code>getLandmarks</code>
 * methods in this class shall guarantee that the application can read a consistent set of
 * the landmark data valid at the time of obtaining the object instance, even if the
 * landmark information in the store is modified subsequently by this or some other
 * application.
 * <p>
 * The <code>Landmark</code> object instances can be in two states: <br>
 * <ul>
 * <li>initially constructed by an application </li>
 * <li>belongs to a <code>LandmarkStore</code> </li>
 * </ul>
 * A <code>Landmark</code> object belongs to a <code>LandmarkStore</code> if it has
 * been obtained from the <code>LandmarkStore</code> using <code>getLandmarks</code>
 * or if it has been added to the <code>LandmarkStore</code> using
 * <code>addLandmark</code>. A <code>Landmark</code> object is initially constructed
 * by an application when it has been constructed using the constructor but has not been
 * added to a <code>LandmarkStore</code> using <code>addLandmark</code>.
 * <p>
 * Note that the term "belongs to a <code>LandmarkStore</code>" is defined above in a
 * way that "belong to a <code>LandmarkStore</code>" has an different meaning than the
 * landmark "being currently stored in a <code>LandmarkStore</code>". According to the
 * above definition, a <code>Landmark</code> object instance may be in a state where it
 * is considered to "belong to a <code>LandmarkStore</code>" even when it is not stored
 * in that <code>LandmarkStore</code> (e.g. if the landmark is deleted from the
 * <code>LandmarkStore</code> using <code>deleteLandmark</code> method, the
 * <code>Landmark</code> object instance still is considered to "belong to this
 * <code>LandmarkStore</code>").
 * <p>
 * The landmark stores created by an application and landmarks added in landmark stores
 * persist even if the application itself is deleted from the terminal.
 * <p>
 * Accessing the landmark store may cause a <code>SecurityException</code>, if the
 * calling application does not have the required permissions. The permissions to read and
 * write (including add and delete) landmarks are distinct. An application having e.g. a
 * permission to read landmarks wouldn't necessarily have the permission to delete them.
 * The permissions (names etc.) for the MIDP 2.0 security framework are defined elsewhere
 * in this specification.
 */
public class LandmarkStore {
	/*
	 * Overview of implementation (for JSR-179 hackers interested in the guts) The
	 * Landmark store is implemented as a RecordStore (part of the MIDP specification).
	 * The LandmarkStore name is prefixed by RECORD_STORE_PREFIX to create the RecordStore
	 * name on disc. This is so that we don't clash with other stores using the same name
	 * in completely unrelated stores for other applications. We never save permanent
	 * temporary local versions of the Landmarks as we don't want to have to store them
	 * all in memory. WeakReferences are used to keep track of instances. This may have
	 * the strange side effect that Landmarks may be returned that have been edited by the
	 * same program but not saved to disc. e.g. An application gets all Landmarks from the
	 * store and edits the Landmark for "Edinburgh Castle" and changes it's name to
	 * "Scottish Castle". The same application then emmediately requests all landmarks in
	 * Edinburgh. The returned Landmark will include the edited one, not the one on disc.
	 * It will not be saved to disc until .updateLandmark() is called on it.
	 * Permission-wise, the default store is editable by all but new stores are only
	 * writable by the app that created them in the first place. LandmarkStores may be
	 * read by other apps. Note that the spec does not require additional stores to be
	 * supported by the implementation and there is no standard policy regarding the
	 * permission of such stores. This policy was chosen as it seems the most sensible.
	 * Due to the lack of Serialization support in J2ME, this contains private static
	 * methods to serialise and deserialise the Landmark objects. Included in the
	 * serialisation is an unspecified number of category Strings. We define a class named
	 * CategorisedLandmark which is simply a container class for a Landmark and an array
	 * of String categories, it is these that are actually serialised. Every LandmarkStore
	 * has a record that holds the valid categories. Unfortunately the RecordStore spec
	 * does not allow writing to an arbitrary record ID if it doesn't exist (MPowerPlayer
	 * certainly does not allow this), nor is there a standard on the ID of the first
	 * record (various implementations use 0 or 1). It gets worse... the RecordEnumeration
	 * does not quarantee that its first entry will be the first record, so each new
	 * instance of LandmarkStore must first find out which record holds the category
	 * information. The only way to do this is to attempt to read all the records until
	 * one is only Strings, then record this ID in a local variable. This ID will never
	 * change unless something evil accesses the RecordStore directly or the
	 * implementation is completely moronic and changes record IDs on the fly. The first
	 * (ID unspecified) record in the store will always be a simple array of valid
	 * category Strings. By storing this information in the RecordStore and not locally in
	 * the instance of the LandmarkStore, it helps to keep the store syncronised across
	 * different applications using the same store at the same time, although due to the
	 * nature of the RecordStore, syncronisation cannot be guaranteed and there will
	 * always be classic database race conditions. Future work may involve making the get*
	 * methods memoryless. The spec requires that they return Enumerations over Landmark
	 * objects. Currently we load all the Landmark objects and return them as a live
	 * Enumeration, however a more scalable solution would involve only reading the record
	 * from the disc when requested. In terms of compliance with the spec... this does a
	 * good job, but because of the lack of SecurityException throws in RecordStore for
	 * read operations, most security-related denials of service will be reported as
	 * IOExceptions.
	 */

	/**
	 * For the purposes of serialisation of the AddressInfo object, this is shared between
	 * {@link #serialiseLandmark(Landmark)} and {@link #landmarkFromSerialised(byte[])}
	 */
	private static final int[] ADDRESS_INFO_ORDER = {
			AddressInfo.BUILDING_FLOOR, AddressInfo.BUILDING_NAME,
			AddressInfo.BUILDING_ROOM, AddressInfo.BUILDING_ZONE,
			AddressInfo.CITY, AddressInfo.COUNTRY, AddressInfo.COUNTRY_CODE,
			AddressInfo.COUNTY, AddressInfo.CROSSING1, AddressInfo.CROSSING2,
			AddressInfo.DISTRICT, AddressInfo.EXTENSION,
			AddressInfo.PHONE_NUMBER, AddressInfo.POSTAL_CODE,
			AddressInfo.STATE, AddressInfo.STREET, AddressInfo.URL };

	/**
	 * It is not possible to save a null string, so we use this as a placeholder. It is
	 * important that null strings be recovered otherwise pre and post stored objects will
	 * not have the same fields. Note that this means that the literal String "(null)"
	 * will be recovered as a null, but such a situation should be incredibly rare.
	 */
	private static final String NULL_STRING = "(null)";

	/**
	 * The LandmarkStore name is prefixed by this in order to get the name of the
	 * RecordStore we save on the device. #see {@link #recordStoreName(String)}
	 */
	private static final String RECORD_STORE_PREFIX = "jsr179_";

	/**
	 * The specification defines when to throw exceptions for filenames that are too long,
	 * but not a minimal length. However, the RecordStore imposes a 32 char maximum.
	 */
	private static final int STORE_NAME_MAX_CHARS = 32 - RECORD_STORE_PREFIX
			.length();

	/**
	 * Creates a new landmark store with a specified name. All LandmarkStores are shared
	 * between all J2ME applications and may be shared with native applications.
	 * Implementations may support creating landmark stores on a removable media. However,
	 * the Java application is not able to directly choose where the landmark store is
	 * stored, if the implementation supports several storage media. The implementation of
	 * this method may e.g. prompt the end user to make the choice if the implementation
	 * supports several storage media. If the landmark store is stored on a removable
	 * media, this media might be removed by the user possibly at any time causing it to
	 * become unavailable.
	 * <p>
	 * A newly created landmark store does not contain any landmarks.
	 * <p>
	 * Note that the landmark store name MAY be modified by the implementation when the
	 * store is created, e.g. by adding an implementation specific post-fix to
	 * differentiate stores on different storage drives as described in the class
	 * overview. Therefore, the application needs to use the listLandmarkStores method to
	 * discover the form the name was stored as. However, when creating stores to the
	 * default storage location, it is recommended that the implementation does not modify
	 * the store name but preserves it in the form it was passed to this method. It is
	 * strongly recommended that this method is implemented as character case preserving
	 * for the store name.
	 *
	 * @param storeName
	 *            the name of the landmark store to create
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws IllegalArgumentException
	 *             if the name is too long or if a landmark store with the specified name
	 *             already exists
	 * @throws IOException
	 *             if the landmark store couldn't be created due to an I/O error
	 * @throws SecurityException
	 *             if the application does not have permissions to create a new landmark
	 *             store
	 * @throws LandmarkException
	 *             if the implementation does not support creating new landmark stores
	 */
	public static void createLandmarkStore(String storeName)
			throws NullPointerException, IllegalArgumentException, IOException,
			LandmarkException, SecurityException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.management");

		// not allowed to create default stores
		if (storeName == null)
			throw new NullPointerException();
		// outsource all the work
		createLandmarkStore2(storeName);
	}

	/**
	 * Delete a landmark store with a specified name. All the landmarks and categories
	 * defined in the named landmark store are irrevocably removed. If a landmark store
	 * with the specified name does not exist, this method returns silently without any
	 * error.
	 * <p>
	 * Note that the landmark store names MAY be handled as either case-sensitive or
	 * case-insensitive (e.g. Unicode collation algorithm level 2). Therefore, the
	 * implementation MUST accept the names in the form returned by listLandmarkStores and
	 * MAY accept the name in other variations of character case.
	 *
	 * @param storeName
	 *            the name of the landmark store to delete
	 * @throws NullPointerException
	 *             if the parameter is null (the default landmark store can't be deleted)
	 * @throws IOException
	 *             if the landmark store couldn't be deleted due to an I/O error
	 * @throws SecurityException
	 *             if the application does not have permissions to delete a landmark store
	 * @throws LandmarkException
	 *             if the implementation does not support deleting landmark stores
	 */
	public static void deleteLandmarkStore(String storeName)
			throws NullPointerException, IOException, SecurityException,
			LandmarkException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.management");

		if (storeName == null)
			throw new NullPointerException();

		String recordStoreName = recordStoreName(storeName);

		try {
			// check if we have write permissions first
			RecordStore store = RecordStore.openRecordStore(recordStoreName,
					false);
			// this may throw SecurityException
			store.addRecord(null, 0, 0);
			// delete
			RecordStore.deleteRecordStore(recordStoreName);
		} catch (RecordStoreNotFoundException e) {
			return;
		} catch (RecordStoreException e) {
			throw new IOException();
		}
	}

	/**
	 * Gets a LandmarkStore instance for storing, deleting and retrieving landmarks. There
	 * must be one default landmark store and there may be other landmark stores that can
	 * be accessed by name. Note that the landmark store names MAY be handled as either
	 * case-sensitive or case-insensitive (e.g. Unicode collation algorithm level 2).
	 * Therefore, the implementation MUST accept the names in the form returned by
	 * listLandmarkStores and MAY accept the name in other variations of character case.
	 *
	 * @param storeName
	 *            the name of the landmark store to open. if null, the default landmark
	 *            store will be returned
	 * @return the LandmarkStore object representing the specified landmark store or null
	 *         if a landmark store with the specified name does not exist.
	 * @throws SecurityException
	 *             if the application does not have a permission to read landmark stores
	 */
	public static LandmarkStore getInstance(String storeName)
			throws SecurityException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.read");

		/*
		 * Create a new LandmarkStore object and return it. If it has not been created
		 * return null, if the permissions are not correct throw a SecurityException, if
		 * there was an IO error return null.
		 */
		try {
			return new LandmarkStore(storeName);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Lists the names of all the available landmark stores. The default landmark store is
	 * obtained from getInstance by passing null as the parameter. The null name for the
	 * default landmark store is not included in the list returned by this method. If
	 * there are no named landmark stores, other than the default landmark store, this
	 * method returns null.
	 * <p>
	 * The store names must be returned in a form that is directly usable as input to
	 * getInstance and deleteLandmarkStore.
	 *
	 * @return an array of landmark store names
	 * @throws SecurityException
	 *             if the application does not have the permission to access landmark
	 *             stores
	 * @throws IOException
	 *             if an I/O error occurred when trying to access the landmark stores
	 */
	public static String[] listLandmarkStores() throws SecurityException,
			IOException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.read");

		// obtain a list of all RecordStores on the device.
		String[] allStores = RecordStore.listRecordStores();
		Vector vecLandmarkStores = new Vector();

		// if the string doesn't begin with #STORE_PREFIX, ignore it
		for (int i = 0; i < allStores.length; i++) {
			String storeName = allStores[i];
			if (storeName.startsWith(RECORD_STORE_PREFIX)) {
				// trim the prefix when reporting back
				vecLandmarkStores.addElement(storeName
						.substring(RECORD_STORE_PREFIX.length()));
			}
		}

		if (vecLandmarkStores.size() == 0)
			return null;

		// need to return Array, not Vector
		String[] list = new String[vecLandmarkStores.size()];
		Enumeration en = vecLandmarkStores.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			list[i] = (String) en.nextElement();
		}
		return list;
	}

	/**
	 * A private version of {@link #createLandmarkStore(String)} that can create the
	 * default store (null).
	 *
	 * @param storeName
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws LandmarkException
	 * @throws SecurityException
	 */
	private static void createLandmarkStore2(String storeName)
			throws IllegalArgumentException, IOException, LandmarkException,
			SecurityException {

		// check if it exists already, throw appropriate Exception if it does
		String recordStoreName = recordStoreName(storeName);
		String[] existAlready = RecordStore.listRecordStores();
		if (existAlready != null) {
			for (int i = 0; i < existAlready.length; i++) {
				if (existAlready[i].equals(recordStoreName))
					throw new IllegalArgumentException();
			}
		}

		// try this entire block, there are many places throwing the same
		// exceptions
		try {
			RecordStore store;
			if (storeName == null) {
				// we were asked to create the default store
				store = RecordStore.openRecordStore(recordStoreName(storeName),
						true);
				// setMode() may throw SecurityException so we don't have to
				// handle it.
				store.setMode(RecordStore.AUTHMODE_ANY, true);
			} else {
				// not the default store
				// enforce a name length restriction
				if (storeName.length() > STORE_NAME_MAX_CHARS)
					throw new IllegalArgumentException(
							"LocationStore name too long.");

				// check that the String is FS-safe
				for (int i = 0; i < storeName.length(); i++) {
					if (isUnsupportedUnicode(storeName.charAt(i)))
						throw new IllegalArgumentException(
								"LocationStore name does not support some unicode characters.");
				}

				// create the underlying RecordStore. Readable, but not writable
				// by any other MIDlets. setMode() may throw SecurityException
				// so we don't have to handle it.
				store = RecordStore.openRecordStore(recordStoreName(storeName),
						true);
				store.setMode(RecordStore.AUTHMODE_ANY, false);
			}
			// place a null marker string in the category record
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			out.writeUTF(NULL_STRING);
			byte[] b = baos.toByteArray();
			store.addRecord(b, 0, b.length);
		} catch (RecordStoreFullException e) {
			throw new LandmarkException(e.getMessage());
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}

		/*
		 * There is no need to create an actual LandmarkStore object, as calling
		 * getInstance() will return one from the RecordStore we just created on the
		 * device.
		 */
	}

	/**
	 * Helper method that takes raw byte data for a single Landmark object as input and
	 * returns a CategorisedLandmark object. This is sadly needed as neither Landmark nor
	 * its components implement Serializable in the specification. Not that it matters
	 * since ObjectStream doesn't exist in J2ME.
	 *
	 * @see #serialise(CategorisedLandmark)
	 * @return
	 * @throws IOException
	 */
	private static CategorisedLandmark deserialise(byte[] rawBytes)
			throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(rawBytes);
		DataInputStream in = new DataInputStream(bais);

		// read the data from the byte stream, in the same order as it was saved
		// replace empty strings with null strings
		String name = in.readUTF();
		if (name.equals(NULL_STRING))
			// this should *never* happen unless the file was edited directly or
			// the Landmark really had an empty (but not null) name.
			throw new IOException();
		String description = in.readUTF();
		if (description.equals(NULL_STRING)) {
			description = null;
		}

		String[] addressInfoParts = new String[ADDRESS_INFO_ORDER.length];
		for (int i = 0; i < ADDRESS_INFO_ORDER.length; i++) {
			addressInfoParts[i] = in.readUTF();
		}
		float altitude = in.readFloat();
		double latitude = in.readDouble();
		double longitude = in.readDouble();
		float horizontalAccuracy = in.readFloat();
		float verticalAccuracy = in.readFloat();

		// read the categories
		Vector vecCategories = new Vector();
		while (true) {
			String category;
			try {
				category = in.readUTF();
			} catch (EOFException e) {
				break;
			}
			// ignore null Strings, which indicate that there are no categories
			if (!category.equals(NULL_STRING)) {
				vecCategories.addElement(category);
			}
		}

		// construct a new QualifiedCoordinates object
		QualifiedCoordinates qualifiedCoordinates = new QualifiedCoordinates(
				latitude, longitude, altitude, horizontalAccuracy,
				verticalAccuracy);

		// construct a new AddressInfo object, ignoring empty strings
		AddressInfo addressInfo = new AddressInfo();
		for (int i = 0; i < ADDRESS_INFO_ORDER.length; i++) {
			String part = addressInfoParts[i];
			if (!part.equals(NULL_STRING)) {
				addressInfo.setField(ADDRESS_INFO_ORDER[i], part);
			}
		}

		// construct a new Landmark object
		Landmark landmark;
		landmark = new Landmark(name, description, qualifiedCoordinates,
				addressInfo);

		// construct the CategorisedLandmark object
		CategorisedLandmark catLandmark = new CategorisedLandmark(landmark);

		// add the categories
		Enumeration en = vecCategories.elements();
		for (; en.hasMoreElements();) {
			String category = (String) en.nextElement();
			catLandmark.addCategory(category);
		}

		return catLandmark;
	}

	/**
	 * Returns true if the char is one of
	 * <ul>
	 * <li>0x0000...0x001F control characters</li>
	 * <li>0x005C <code>'\'</code></li>
	 * <li>0x002F <code>'/'</code></li>
	 * <li>0x003A <code>':'</code></li>
	 * <li>0x002A <code>'*'</code></li>
	 * <li>0x003F <code>'?'</code></li>
	 * <li>0x0022 <code>'"'</code></li>
	 * <li>0x003C <code>'<'</code></li>
	 * <li>0x003E <code>'>'</code></li>
	 * <li>0x007C <code>'|'</code></li>
	 * <li>0x007F...0x009F control characters</li>
	 * <li>0xFEFF Byte-order-mark</li>
	 * <li>0xFFF0...0xFFFF</li>
	 * </ul>
	 *
	 * @param character
	 * @return true if the character is one of the unicode characters defined in the
	 *         specification where support is not required.
	 */
	private static boolean isUnsupportedUnicode(char character) {

		// the individually labelled unicode values of unsupported characters
		int[] individuals = { 0x005C, 0x002F, 0x003A, 0x002A, 0x003F, 0x0022,
				0x003C, 0x003E, 0x007C, 0xFEFF };
		for (int i = 0; i < individuals.length; i++) {
			if (character == individuals[i])
				return true;
		}

		// the block-defined unicode values of unsupported characters
		// be careful when using this. the even indexed entries are where a
		// block begins, ending with the proceeding odd indexed entry.
		int[] blocks = { 0x0000, 0x001F, 0x007F, 0x009F, 0xFFF0, 0xFFFF };
		for (int i = 0; i < blocks.length; i = i + 2) {
			if ((character >= blocks[i]) && (character <= blocks[i + 1]))
				return true;
		}

		return false;
	}

	/**
	 * Helper method that calculates the name of the RecordStore associated to a
	 * LandmarkStore name.
	 *
	 * @param storeName
	 * @return
	 */
	private static String recordStoreName(String storeName) {
		// this is the identifier we use for the default store. Using an empty
		// string is probably the safest option.
		if (storeName == null) {
			storeName = "";
		}
		return RECORD_STORE_PREFIX + storeName;
	}

	/**
	 * Helper method that takes a CategorisedLandmark object as input and returns a raw
	 * byte array which can be saved in the store. This is sadly needed as neither
	 * Landmark nor its components implement Serializable in the specification. Not that
	 * it matters since ObjectStream doesn't exist in J2ME.
	 *
	 * @see #unserialised(byte[])
	 * @return
	 * @throws IOException
	 */
	private static byte[] serialise(CategorisedLandmark catLandmark)
			throws IOException {
		Landmark landmark = catLandmark.getLandmark();
		String[] categories = catLandmark.getCategories();

		// get the top-level components
		String name = landmark.getName();
		String description = landmark.getDescription();
		AddressInfo addressInfo = landmark.getAddressInfo();
		QualifiedCoordinates qualifiedCoordinates = landmark
				.getQualifiedCoordinates();

		// get the AddressInfo components
		String[] addressInfoParts = new String[ADDRESS_INFO_ORDER.length];
		for (int i = 0; i < ADDRESS_INFO_ORDER.length; i++) {
			addressInfoParts[i] = addressInfo.getField(ADDRESS_INFO_ORDER[i]);
		}

		// get the QualifiedCoordinates components
		float altitude = qualifiedCoordinates.getAltitude();
		double latitude = qualifiedCoordinates.getLatitude();
		double longitude = qualifiedCoordinates.getLongitude();
		float horizontalAccuracy = qualifiedCoordinates.getHorizontalAccuracy();
		float verticalAccuracy = qualifiedCoordinates.getVerticalAccuracy();

		// now convert this data into a bytestream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);

		// note that writeUTF(null) may screw things up, so save a marker
		out.writeUTF(name == null ? NULL_STRING : name);
		out.writeUTF(description == null ? NULL_STRING : description);
		for (int i = 0; i < addressInfoParts.length; i++) {
			String part = addressInfoParts[i];
			out.writeUTF(part == null ? NULL_STRING : part);
		}
		out.writeFloat(altitude);
		out.writeDouble(latitude);
		out.writeDouble(longitude);
		out.writeFloat(horizontalAccuracy);
		out.writeFloat(verticalAccuracy);
		// place the categories at the end, as there are an unspecified amount
		if (categories == null) {
			// if there were none, store the null marker
			out.writeUTF(NULL_STRING);
		} else {
			for (int i = 0; i < categories.length; i++) {
				String category = categories[i];
				out.writeUTF(category == null ? NULL_STRING : category);
			}
		}

		// extract the byte array
		byte[] b = baos.toByteArray();
		return b;
	}

	/**
	 * The instance specific record ID of the category info.
	 */
	private int categoryID;

	/**
	 * In order to allow .updateLandmarks() to work correctly, we need to track which
	 * instances are associated to the entries in the store. We could simply keep a cache
	 * of every elemnt that has been requested but that would involve storing all elements
	 * in memory! Not really an option for even 100k stores on a mobile device. Instead we
	 * keep a hashTable of IDs to weakReferences.
	 * <p>
	 * Note that although a weakReference may exist here, do not forget that another
	 * instance of the LandmarkStore may have changed the data on disc, so existence here
	 * does not mean existence on disc.
	 * <p>
	 * Map is from Record ID (Integer) to Landmark (WeakReference)
	 */
	private final Hashtable instances = new Hashtable();

	/**
	 * This is where the data is persistently stored.
	 */
	private RecordStore store;

	/**
	 * Private constructor so that stores may only be created using the static methods of
	 * this class.
	 *
	 * @throws IOException
	 * @throws SecurityException
	 */
	private LandmarkStore(String storeName) throws IOException,
			SecurityException {

		String recordStoreName = recordStoreName(storeName);

		try {
			// only open, do not create if it doesn't exist
			store = RecordStore.openRecordStore(recordStoreName, false);
		} catch (RecordStoreException e) {
			// the Exception might be because we asked for the default store and
			// it was never created, in which case create it.
			if (storeName == null) {
				try {
					createLandmarkStore2(null);
					// don't forget to open the default store after we create it
					store = RecordStore.openRecordStore(recordStoreName, false);
				} catch (IllegalArgumentException e2) {
					throw new IOException(e2.getMessage());
				} catch (LandmarkException e2) {
					throw new IOException(e2.getMessage());
				} catch (RecordStoreException e2) {
					throw new IOException(e2.getMessage());
				}
			} else
				throw new IOException(e.getMessage());
		}

		// determine where the category info is stored
		determineCategoryID();
	}

	/**
	 * Adds a category to this LandmarkStore. All implementations must support names that
	 * have length up to and including 32 characters. If the provided name is longer it
	 * may be truncated by the implementation if necessary.
	 *
	 * @param categoryName
	 *            name for the category to be added
	 * @throws IllegalArgumentException
	 *             if a category with the specified name already exists
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws LandmarkException
	 *             if this LandmarkStore does not support adding new categories
	 * @throws IOException
	 *             if an I/O error occurs or there are no resources to add a new category
	 * @throws SecurityException
	 *             if the application does not have the permission to manage categories
	 */
	public void addCategory(String categoryName)
			throws IllegalArgumentException, NullPointerException,
			LandmarkException, IOException, SecurityException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.category");

		if (categoryName == null)
			throw new NullPointerException();

		// truncate if necessary
		if (categoryName.length() > 32) {
			categoryName = categoryName.substring(0, 32);
		}

		// get the latest category list
		Vector categories = getCategories1();
		// does it exist already
		if (categories.contains(categoryName))
			throw new IllegalArgumentException();
		// add to the category list
		categories.addElement(categoryName);
		// save to disc
		saveCategories(categories);
	}

	/**
	 * Adds a landmark to the specified group in the landmark store. If some textual
	 * String field inside the landmark object is set to a value that is too long to be
	 * stored, the implementation is allowed to automatically truncate fields that are too
	 * long.
	 * <p>
	 * However, the name field MUST NOT be truncated. Every implementation shall be able
	 * to support name fields that are 32 characters or shorter. Implementations may
	 * support longer names but are not required to. If an application tries to add a
	 * Landmark with a longer name field than the implementation can support,
	 * IllegalArgumentException is thrown.
	 * <p>
	 * When the landmark store is empty, every implementation is required to be able to
	 * store a landmark where each String field is set to a 30 character long string.
	 * <p>
	 * If the Landmark object that is passed as a parameter is an instance that belongs to
	 * this LandmarkStore, the same landmark instance will be added to the specified
	 * category in addition to the category/categories which it already belongs to. If the
	 * landmark already belongs to the specified category, this method returns with no
	 * effect. If the landmark has been deleted after obtaining it from getLandmarks, it
	 * will be added back when this method is called.
	 * <p>
	 * If the Landmark object that is passed as a parameter is an instance initially
	 * constructed by the application using the constructor or an instance that belongs to
	 * a different LandmarkStore, a new landmark will be created in this LandmarkStore and
	 * it will belong initially to only the category specified in the category parameter.
	 * After this method call, the Landmark object that is passed as a parameter belongs
	 * to this LandmarkStore.
	 *
	 * @param landmark
	 *            the landmark to be added
	 * @param category
	 *            where the landmark is added. null can be used to indicate that the
	 *            landmark does not belong to a category
	 * @throws SecurityException
	 *             if the application is not allowed to add landmarks
	 * @throws IllegalArgumentException
	 *             if the landmark has a longer name field than the implementation can
	 *             support or if the category is not null or one of the categories defined
	 *             in this LandmarkStore
	 * @throws IOException
	 *             if an I/O error happened when accessing the landmark store or if there
	 *             are no resources available to store this landmark
	 * @throws NullPointerException
	 *             if the landmark parameter is null
	 */
	public void addLandmark(Landmark landmark, String category)
			throws SecurityException, IllegalArgumentException, IOException,
			NullPointerException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.write");

		if (landmark == null)
			throw new NullPointerException();

		// check if the category is valid
		Vector validCategories = getCategories1();
		if ((category != null) && !validCategories.contains(category))
			throw new IllegalArgumentException();

		// check if an instance already exists
		int ID = getRecordIDOfInstance(landmark);
		if (ID != -1) {
			// already in the store, add category
			CategorisedLandmark clm = getCLAtID(ID);
			clm.addCategory(category);
			saveCLToID(clm, ID);
			return;
		}
		// no instance found in the store
		// create the CategorisedLandmark object
		CategorisedLandmark clm = new CategorisedLandmark(landmark);
		if (category != null) {
			clm.addCategory(category);
		}
		// save to the store
		saveNewCL(clm);
	}

	/**
	 * Removes a category from this LandmarkStore. The category will be removed from all
	 * landmarks that are in that category. However, this method will not remove any of
	 * the landmarks, only the associated category information from the landmarks. If a
	 * category with the supplied name does not exist in this LandmarkStore, the method
	 * returns silently with no error.
	 *
	 * @param categoryName
	 *            name for the category to be removed
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws LandmarkException
	 *             if this LandmarkStore does not support deleting categories
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws SecurityException
	 *             if the application does not have the permission to manage categories
	 */
	public void deleteCategory(String categoryName)
			throws NullPointerException, LandmarkException, IOException,
			SecurityException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.category");

		if (categoryName == null)
			throw new NullPointerException();

		// remove the category from the valid list
		Vector categories = getCategories1();
		if (!categories.contains(categoryName))
			// it wasn't a valid category anyway, return silently
			return;
		categories.removeElement(categoryName);
		saveCategories(categories);

		// remove Landmarks from this category
		int[] records = getRecordIDs();
		if (records == null)
			return;
		for (int i = 0; i < records.length; i++) {
			// get the ID
			int ID = records[i];
			// get the record
			byte[] record;
			try {
				record = store.getRecord(ID);
			} catch (RecordStoreException e) {
				throw new IOException(e.getMessage());
			}
			CategorisedLandmark clm = deserialise(record);
			// check if the Landmark is in the category
			if (clm.inCategory(categoryName)) {
				// it is!
				// remove the category from the landmark
				clm.removeCategory(categoryName);
				// save the new Categorised Landmark
				saveCLToID(clm, ID);
			}
		}
	}

	/**
	 * Deletes a landmark from this LandmarkStore. This method removes the specified
	 * landmark from all categories and deletes the information from this LandmarkStore.
	 * The Landmark instance passed in as the parameter must be an instance that belongs
	 * to this LandmarkStore.
	 * <p>
	 * If the Landmark belongs to this LandmarkStore but has already been deleted from
	 * this LandmarkStore, then the request is silently ignored and the method call
	 * returns with no error. Note that LandmarkException is thrown if the Landmark
	 * instance does not belong to this LandmarkStore, and this is different from not
	 * being stored currently in this LandmarkStore.
	 *
	 * @param lm
	 *            the landmark to be deleted
	 * @throws SecurityException
	 *             if the application is not allowed to delete the landmark
	 * @throws LandmarkException
	 *             if the landmark instance passed as the parameter does not belong to
	 *             this LandmarkStore
	 * @throws IOException
	 *             if an I/O error happened when accessing the landmark store
	 * @throws NullPointerException
	 *             if the parameter is null
	 */
	public void deleteLandmark(Landmark lm) throws SecurityException,
			LandmarkException, IOException, NullPointerException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.write");

		if (lm == null)
			throw new NullPointerException();

		// check if an instance actually exists
		int ID = getRecordIDOfInstance(lm);
		if (ID == -1)
			// instance not from this store
			throw new LandmarkException();

		// delete from disc
		try {
			store.deleteRecord(ID);
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
		// remove from our lookup table
		instances.remove(new Integer(ID));
	}

	/**
	 * Returns the category names that are defined in this LandmarkStore. The language and
	 * locale used for these names depends on the implementation and end user settings.
	 * The names shall be such that they can be displayed to the end user and have a
	 * meaning to the end user.
	 *
	 * @return an Enumeration containing Strings representing the category names. If there
	 *         are no categories defined in this LandmarkStore, an Enumeration with no
	 *         entries is returned.
	 */
	public Enumeration getCategories() {
		Vector categories;

		try {
			categories = getCategories1();
		} catch (IOException e) {
			// hmm... no way to report back if any error occured, so just return
			// null if anything bad happened
			return null;
		}

		// if there were no categories, return an enumeration with no entries
		if (categories == null) {
			Vector empty = new Vector(0);
			return empty.elements();
		}
		return categories.elements();
	}

	/**
	 * Lists all landmarks stored in the store.
	 *
	 * @return an Enumeration object containing Landmark objects representing all the
	 *         landmarks stored in this LandmarkStore or null if there are no landmarks in
	 *         the store
	 * @throws IOException
	 *             if an I/O error happened when accessing the landmark store
	 */
	public Enumeration getLandmarks() throws IOException {
		// we will create a Vector of Landmark objects
		Vector landmarks = new Vector();

		// hold a temporary list of instance lookups, incase something throws an
		// Exception before we return
		Hashtable lookup = new Hashtable();

		// cycle through all the records
		int[] records = getRecordIDs();
		if (records == null)
			return null;
		for (int i = 0; i < records.length; i++) {
			// get the ID
			int ID = records[i];
			// get the record
			byte[] record;
			try {
				record = store.getRecord(ID);
			} catch (RecordStoreException e) {
				throw new IOException(e.getMessage());
			}
			// is an instance already alive
			Landmark lm = getAliveLandmark(ID);
			if (lm != null) {
				// yes, an instance is already alive
				landmarks.addElement(lm);
				continue;
			}
			// no instance currently alive
			CategorisedLandmark clm = deserialise(record);
			// add the landmark, ignore the category info
			lm = clm.getLandmark();
			landmarks.addElement(lm);
			// record the ID and instance
			WeakReference instance = new WeakReference(lm);
			lookup.put(new Integer(ID), instance);
		}

		// don't forget to update the instance ID lookup
		instanceTableUpdate(lookup);
		return landmarks.elements();
	}

	/**
	 * Lists all the landmarks that are within an area defined by bounding minimum and
	 * maximum latitude and longitude and belong to the defined category, if specified.
	 * The bounds are considered to belong to the area. If minLongitude <= maxLongitude,
	 * this area covers the longitude range [minLongitude, maxLongitude]. If minLongitude >
	 * maxLongitude, this area covers the longitude range [-180.0, maxLongitude] and
	 * [minLongitude, 180.0).
	 * <p>
	 * For latitude, the area covers the latitude range [minLatitude, maxLatitude].
	 *
	 * @param category
	 *            the category of the landmark. null implies a wildcard that matches all
	 *            categories
	 * @param minLatitude
	 *            minimum latitude of the area. Must be within the range [-90.0, 90.0]
	 * @param maxLatitude
	 *            maximum latitude of the area. Must be within the range [minLatitude,
	 *            90.0]
	 * @param minLongitude
	 *            minimum longitude of the area. Must be within the range [-180.0, 180.0)
	 * @param maxLongitude
	 *            maximum longitude of the area. Must be within the range [-180.0, 180.0)
	 * @return an Enumeration containing all the matching Landmarks or null if no Landmark
	 *         matched the given parameters
	 * @throws IOException
	 *             if an I/O error happened when accessing the landmark store
	 * @throws IllegalArgumentException
	 *             if the minLongitude or maxLongitude is out of the range [-180.0,
	 *             180.0), or minLatitude or maxLatitude is out of the range [-90.0,90.0],
	 *             or if minLatitude > maxLatitude
	 */
	public Enumeration getLandmarks(String category, double minLatitude,
			double maxLatitude, double minLongitude, double maxLongitude)
			throws IOException, IllegalArgumentException {
		if ((minLatitude > maxLatitude) || (minLatitude < -90)
				|| (maxLatitude > 90) || (minLongitude < -180)
				|| (minLongitude >= 180) || (maxLongitude < -180)
				|| (maxLongitude >= 180))
			throw new IllegalArgumentException();

		// we will create a Vector of Landmark objects
		Vector landmarks = new Vector();

		// hold a temporary list of instance lookups, incase something throws an
		// Exception before we return
		Hashtable lookup = new Hashtable();

		// cycle through all the records
		int[] records = getRecordIDs();
		if (records == null)
			return null;
		for (int i = 0; i < records.length; i++) {
			// get the ID
			int ID = records[i];
			// get the record
			byte[] record;
			try {
				record = store.getRecord(ID);
			} catch (RecordStoreException e) {
				throw new IOException(e.getMessage());
			}
			// no point looking to see if it's alive yet, as we need the
			// category info
			CategorisedLandmark clm = deserialise(record);
			// is the Landmark categorised correctly
			if ((category == null) || clm.inCategory(category)) {
				// is the Landmark in the correct region
				Landmark lm = clm.getLandmark();
				QualifiedCoordinates qc = lm.getQualifiedCoordinates();
				double longitude = qc.getLongitude();
				double latitude = qc.getLatitude();
				// first test latitude
				if ((latitude < minLatitude) || (latitude > maxLatitude)) {
					continue;
				}
				// then the longitude, urgh
				if (((minLongitude <= maxLongitude)
						&& (longitude >= minLongitude) && (longitude <= maxLongitude))
						|| ((minLongitude > maxLongitude) && (((longitude >= -180) && (longitude < maxLongitude)) || ((longitude < 180) && (longitude > minLongitude))))) {
					// ok, this Landmark passes the tests
					// is an instance already alive
					Landmark aliveLm = getAliveLandmark(ID);
					if (aliveLm == null) {
						// no, not alive
						landmarks.addElement(lm);
						// record the instance
						WeakReference instance = new WeakReference(lm);
						lookup.put(new Integer(ID), instance);
					} else {
						// yes, an instance exists already
						landmarks.addElement(aliveLm);
					}
				}
			}
		}

		// don't forget to update the instance ID lookup
		instanceTableUpdate(lookup);
		return landmarks.elements();
	}

	/**
	 * Gets the Landmarks from the storage where the category and/or name matches the
	 * given parameters.
	 *
	 * @param category
	 *            the category of the landmark. null implies a wildcard that matches all
	 *            categories
	 * @param name
	 *            the name of the desired landmark. null implies a wildcard that matches
	 *            all the names within the category indicated by the category parameter
	 * @return an Enumeration containing all the matching Landmarks or null if no Landmark
	 *         matched the given parameters
	 * @throws IOException
	 *             if an I/O error happened when accessing the landmark store
	 */
	public Enumeration getLandmarks(String category, String name)
			throws IOException {
		// we will create a Vector of Landmark objects
		Vector landmarks = new Vector();

		// hold a temporary list of instance lookups, incase something throws an
		// Exception before we return
		Hashtable lookup = new Hashtable();

		// cycle through all the records
		int[] records = getRecordIDs();
		if (records == null)
			return null;
		for (int i = 0; i < records.length; i++) {
			// get the ID
			int ID = records[i];
			// get the record
			byte[] record;
			try {
				record = store.getRecord(ID);
			} catch (RecordStoreException e) {
				throw new IOException(e.getMessage());
			}
			// no point looking to see if it's alive yet, as we need the
			// category info
			CategorisedLandmark clm = deserialise(record);
			Landmark lm = clm.getLandmark();
			// is the Landmark name OK, and categorised correctly
			if (((name == null) || lm.getName().equals(name))
					&& ((category == null) || clm.inCategory(category))) {
				// ok, this Landmark passes the tests
				// is an instance already alive
				Landmark aliveLm = getAliveLandmark(ID);
				if (aliveLm == null) {
					// no, not alive
					landmarks.addElement(lm);
					// record the instance
					WeakReference instance = new WeakReference(lm);
					lookup.put(new Integer(ID), instance);
				} else {
					// yes, an instance exists already
					landmarks.addElement(aliveLm);
				}
			}
		}

		// don't forget to update the instance ID lookup
		instanceTableUpdate(lookup);
		return landmarks.elements();
	}

	/**
	 * Removes the named landmark from the specified category. The Landmark instance
	 * passed in as the parameter must be an instance that belongs to this LandmarkStore.
	 * <p>
	 * If the Landmark is not found in this LandmarkStore in the specified category or if
	 * the parameter is a Landmark instance that does not belong to this LandmarkStore,
	 * then the request is silently ignored and the method call returns with no error. The
	 * request is also silently ignored if the specified category does not exist in this
	 * LandmarkStore.
	 * <p>
	 * The landmark is only removed from the specified category but the landmark
	 * information is retained in the store. If the landmark no longer belongs to any
	 * category, it can still be obtained from the store by passing null as the category
	 * to getLandmarks.
	 *
	 * @param lm
	 *            the landmark to be removed
	 * @param category
	 *            the category from which it will be removed.
	 * @throws SecurityException
	 *             if the application is not allowed to delete the landmark
	 * @throws IOException
	 *             if an I/O error happened when accessing the landmark store
	 * @throws NullPointerException
	 *             if either parameter is null
	 */
	public void removeLandmarkFromCategory(Landmark lm, String category)
			throws SecurityException, IOException, NullPointerException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.write");

		if ((lm == null) || (category == null))
			throw new NullPointerException();

		Vector categories = getCategories1();
		if (!categories.contains(category))
			// it wasn't a valid category anyway, return silently
			return;

		// get the category info for the Landmark
		int id = getRecordIDOfInstance(lm);
		CategorisedLandmark clm = getCLAtID(id);
		// remove the landmark from the category
		clm.removeCategory(category);
		// save the new clasification info to disc
		saveCLToID(clm, id);
	}

	/**
	 * Updates the information about a landmark. This method only updates the information
	 * about a landmark and does not modify the categories the landmark belongs to. The
	 * Landmark instance passed in as the parameter must be an instance that belongs to
	 * this LandmarkStore.
	 * <p>
	 * This method can't be used to add a new landmark to the store.
	 *
	 * @param lm
	 *            the landmark to be updated
	 * @throws SecurityException
	 *             if the application is not allowed to update the landmark
	 * @throws LandmarkException
	 *             if the landmark instance passed as the parameter does not belong to
	 *             this LandmarkStore or does not exist in the store any more
	 * @throws IOException
	 *             if an I/O error happened when accessing the landmark store
	 * @throws NullPointerException
	 *             if the parameter is null
	 * @throws IllegalArgumentException
	 *             if the landmark has a longer name field than the implementation can
	 *             support
	 */
	public void updateLandmark(Landmark lm) throws SecurityException,
			LandmarkException, IOException, NullPointerException,
			IllegalArgumentException {
		// test Security permissions
		OpenLAPICommon
				.testPermission("javax.microedition.location.LandmarkStore.write");

		if (lm == null)
			throw new IllegalArgumentException();

		// first check if lm is an instance recently obtained from the store
		int ID = getRecordIDOfInstance(lm);
		if (ID == -1)
			throw new LandmarkException();

		// extract the category info
		CategorisedLandmark clm = getCLAtID(ID);
		CategorisedLandmark newClm = new CategorisedLandmark(lm);
		String[] categories = clm.getCategories();
		if (categories != null) {
			for (int i = 0; i < categories.length; i++) {
				newClm.addCategory(categories[i]);
			}
		}

		// save the updated Landmark to disc
		saveCLToID(newClm, ID);
	}

	/**
	 * Sets the local variable {@link #categoryID}. To be run when the LandmarkStore is
	 * first instantiated (but valid anytime, though it should never be needed after that,
	 * unless something other than this class edited the RecordStore directly).
	 *
	 * @throws IOException
	 */
	private void determineCategoryID() throws IOException {
		try {
			RecordEnumeration en = store.enumerateRecords(null, null, false);
			for (; en.hasNextElement();) {
				int ID = en.nextRecordId();
				byte[] record = store.getRecord(ID);
				if (isCategoryRecord(record)) {
					categoryID = ID;
					return;
				}
			}
			throw new IOException("No category info in RecordStore "
					+ store.getName());
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Convenience method for obtaining the instance associated to a record ID. Will
	 * return the instance if it exists, otherwise null.
	 *
	 * @param id
	 * @return
	 */
	private Landmark getAliveLandmark(int id) {
		Integer ID = new Integer(id);
		if (instances.contains(ID)) {
			WeakReference weakRef = (WeakReference) instances.get(ID);
			return (Landmark) weakRef.get();
		}
		return null;
	}

	/**
	 * Helper method that returns all the valid categories in this store. Reads from the
	 * category record in the store from disc on each call to avoid syncronisation issues.
	 *
	 * @return a String Vector of all categories. returns empty Vector if there were none.
	 * @throws RecordStoreException
	 * @throws RecordStoreNotOpenException
	 * @throws IOException
	 */
	private Vector getCategories1() throws IOException {
		byte[] raw;
		try {
			raw = store.getRecord(categoryID);
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(raw);
		DataInputStream in = new DataInputStream(bais);

		// read the categories
		Vector vecCategories = new Vector();
		while (true) {
			String category;
			try {
				category = in.readUTF();
			} catch (EOFException e) {
				break;
			}
			// ignore null Strings, which indicate that there are no categories
			if (!category.equals(NULL_STRING)) {
				vecCategories.addElement(category);
			}
		}

		// return the categories
		return vecCategories;
	}

	/**
	 * Convenience method for obtaining the CategorisedLandmark from a particular record
	 * in the store. Note that it is very inefficient to use this within an enumeration of
	 * the records, so only use it when you know the ID and are not enumerating. Yes, it
	 * would be wonderful if RecordStore returned a Collection of record IDs, but it
	 * doesn't.
	 *
	 * @see #saveCLToID(CategorisedLandmark, int)
	 * @param ID
	 * @return
	 * @throws IOException
	 */
	private CategorisedLandmark getCLAtID(int ID) throws IOException {
		byte[] record;
		try {
			record = store.getRecord(ID);
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
		CategorisedLandmark clm = deserialise(record);
		return clm;
	}

	/**
	 * @param lm
	 * @return the record ID of an instance of a Landmark object, -1 if the instance is
	 *         not recently from the store. (Note this is not to say that an exact
	 *         duplicate of the Landmark isn't in the store).
	 */
	private int getRecordIDOfInstance(Landmark lm) {
		for (Enumeration e = instances.keys(); e.hasMoreElements();) {
			Integer ID = (Integer) e.nextElement();
			WeakReference instance = (WeakReference) instances.get(ID);
			if (lm.equals(instance.get()))
				return ID.intValue();
		}

		return -1;
	}

	/**
	 * Retrieve an array containing all of the valid record IDs of the store, at the
	 * moment it was called. If an ID is changed or removed by another appliciton in
	 * between calling this method and accessing ing contents, expect IOException.
	 * <p>
	 * This method only returns IDs that contain suspected CategorisedLandmark objects
	 * (i.e. does not return category entries).
	 * <p>
	 * Returns null if there were no entries.
	 *
	 * @return
	 * @throws IOException
	 */
	private int[] getRecordIDs() throws IOException {
		try {
			// number of records is store size minus the category entry
			int size = store.getNumRecords() - 1;
			if (size == 0)
				return null;

			int[] recordIDs = new int[size];

			int i = 0;
			RecordEnumeration en = store.enumerateRecords(null, null, false);
			for (; en.hasNextElement();) {
				int recordID = en.nextRecordId();
				if (recordID == categoryID) {
					continue;
				}
				// add it to the list
				recordIDs[i] = recordID;
				i++;
			}
			return recordIDs;
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Helper method to merge a Hashtable of ID->hashCodes to the store's lookup table.
	 *
	 * @param table
	 */
	private void instanceTableUpdate(Hashtable table) {
		for (Enumeration e = table.keys(); e.hasMoreElements();) {
			Integer ID = (Integer) e.nextElement();
			WeakReference instance = (WeakReference) table.get(ID);
			// overwrite previous ID entries... assume that the caller knows
			// what they are doing and that the instance reference is now
			// garbage collected
			instances.put(ID, instance);
		}
	}

	/**
	 * When given a byte array, determine if it is or is not the category entry.
	 *
	 * @param rawBytes
	 * @return
	 */
	private boolean isCategoryRecord(byte[] rawBytes) {
		// rather than writing new code that looks for all Strings, check if it
		// is a CategorisedLandmark and if an Exception is thrown assume it is
		// the category info
		try {
			deserialise(rawBytes);
		} catch (IOException e) {
			// wasn't a CategorisedLandmark, must be categories
			return true;
		}
		// was a CategorisedLandmark
		return false;
	}

	/**
	 * Convenience method to save a Vector of String objects to the category entry of the
	 * record store.
	 *
	 * @param categories
	 * @throws IOException
	 */
	private void saveCategories(Vector categories) throws IOException {
		// create the record
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(baos);
		if (categories == null) {
			// if there were none, store the null marker
			out.writeUTF(NULL_STRING);
		} else {
			for (int i = 0; i < categories.size(); i++) {
				String category = (String) categories.elementAt(i);
				out.writeUTF(category == null ? NULL_STRING : category);
			}
		}
		// and save to disc
		byte[] b = baos.toByteArray();
		try {
			store.setRecord(categoryID, b, 0, b.length);
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Convenience method for saving a CategorisedLandmark to a particular record in the
	 * store.
	 *
	 * @see #getCLAtID(int)
	 * @param clm
	 * @param ID
	 * @throws IOException
	 */
	private void saveCLToID(CategorisedLandmark clm, int ID) throws IOException {
		// serialise the new Categorised Landmark
		byte[] record = serialise(clm);
		// save to the RecordStore
		try {
			store.setRecord(ID, record, 0, record.length);
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Convenience method for appending a new CategorisedLandmark object to the store.
	 *
	 * @param clm
	 * @throws IOException
	 */
	private void saveNewCL(CategorisedLandmark clm) throws IOException {
		// serialise the new record
		byte[] record = serialise(clm);
		Integer ID;
		try {
			// save to disc and get the ID
			ID = new Integer(store.addRecord(record, 0, record.length));
		} catch (RecordStoreException e) {
			throw new IOException(e.getMessage());
		}
		// get the instance
		WeakReference instance = new WeakReference(clm.getLandmark());
		// add to the instance lookup
		instances.put(ID, instance);
	}

}