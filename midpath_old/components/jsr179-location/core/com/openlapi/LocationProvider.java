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
import java.util.Enumeration;
import java.util.Vector;

import thinktank.j2me.TTUtils;

import com.sun.midp.main.Configuration;

/**
 * This is the starting point for applications using this API and represents a source of
 * the location information. A LocationProvider represents a location-providing module,
 * generating Locations.
 * <p>
 * Applications obtain LocationProvider instances (classes implementing the actual
 * functionality by extending this abstract class) by calling the factory method. It is
 * the responsibility of the implementation to return the correct LocationProvider-derived
 * object.
 * <p>
 * Applications that need to specify criteria for the location provider selection, must
 * first create a Criteria object, and pass it to the factory method. The methods that
 * access the location related information shall throw SecurityException if the
 * application does not have the relevant permission to access the location information.
 */
public abstract class LocationProvider {

	/**
	 * Availability status code: the location provider is available.
	 */
	public static final int AVAILABLE = 1;

	/**
	 * Availability status code: the location provider is out of service. Being out of
	 * service means that the method is unavailable and the implementation is not able to
	 * expect that this situation would change in the near future. An example is when
	 * using a location method implemented in an external device and the external device
	 * is detached.
	 */
	public static final int OUT_OF_SERVICE = 3;

	/**
	 * Availability status code: the location provider is temporarily unavailable.
	 * Temporary unavailability means that the method is unavailable due to reasons that
	 * can be expected to possibly change in the future and the provider to become
	 * available. An example is not being able to receive the signal because the signal
	 * used by the location method is currently being obstructed, e.g. when deep inside a
	 * building for satellite based methods. However, a very short transient obstruction
	 * of the signal should not cause the provider to toggle quickly between
	 * TEMPORARILY_UNAVAILABLE and AVAILABLE.
	 */
	public static final int TEMPORARILY_UNAVAILABLE = 2;

	// hold the latest valid location here so that it may be requested without expense.
	private static volatile Location lastKnownLocation = Location.getInvalid();

	/**
	 * This is where we store the proximity listeners. The elements are 3 element Object []
	 * arrays:-
	 * <ul>
	 * <li>[0] are the ProximityListeners</li>
	 * <li>[1] are the Coordinates</li>
	 * <li>[2] is the proximityRadius as a Float</li>
	 * </ul>
	 */
	private static final Vector listeners = new Vector();
	
	private static String mode;
	private static String resourceURI;
	private static int pollInterval;

	/**
	 * Adds a ProximityListener for updates when proximity to the specified coordinates is
	 * detected. If this method is called with a ProximityListener that is already
	 * registered, the registration to the specified coordinates is added in addition to
	 * the set of coordinates it has been previously registered for. A single listener can
	 * handle events for multiple sets of coordinates.
	 * <p>
	 * If the current location is known to be within the proximity radius of the specified
	 * coordinates, the listener shall be called immediately.
	 * <p>
	 * Detecting the proximity to the defined coordinates is done on a best effort basis
	 * by the implementation. Due to the limitations of the methods used to implement
	 * this, there are no guarantees that the proximity is always detected; especially in
	 * situations where the terminal briefly enters the proximity area and exits it
	 * shortly afterwards, it is possible that the implementation misses this. It is
	 * optional to provide this feature as it may not be reasonably implementable with all
	 * methods used to implement this API.
	 * <p>
	 * If the implementation is capable of supporting the proximity monitoring and has
	 * resources to add the new listener and coordinates to be monitored but the
	 * monitoring can't be currently done due to the current state of the method used to
	 * implement it, this method shall succeed and the monitoringStateChanged method of
	 * the listener shall be immediately called to notify that the monitoring is not
	 * active currently.
	 * 
	 * @param listener
	 *            the listener to be registered
	 * @param coordinates
	 *            the coordinates to be registered
	 * @param proximityRadius
	 *            the radius in meters that is considered to be the threshold for being in
	 *            the proximity of the specified coordinates
	 * @throws LocationException
	 *             if the platform does not have resources to add a new listener and
	 *             coordinates to be monitored or does not support proximity monitoring at
	 *             all
	 * @throws IllegalArgumentException
	 *             if the proximity radius is 0 or negative* or Float.NaN
	 * @throws NullPointerException
	 *             if the listener or coordinates parameter is null
	 * @throws SecurityException
	 *             if the application does not have the permission to register a proximity
	 *             listener
	 */
	public static void addProximityListener(ProximityListener listener,
			Coordinates coordinates, float proximityRadius)
			throws LocationException, IllegalArgumentException,
			NullPointerException, SecurityException {
		// test Security permissions
		OpenLAPICommon.testPermission("javax.microedition.location.ProximityListener");

		if ((listener == null) || (coordinates == null))
			throw new NullPointerException();

		if ((proximityRadius <= 0.0) || (Float.isNaN(proximityRadius)))
			throw new IllegalArgumentException();

		if (listeners == null)
			// impossible, but keeps Eclipse quiet about never thrown exceptions
			throw new LocationException();
		
		Float radius = new Float(proximityRadius);
		Object[] listenerArray = new Object[3];
		listenerArray[0] = listener;
		listenerArray[1] = coordinates;
		listenerArray[2] = radius;

		listeners.addElement(listenerArray);
	}

	/**
	 * This factory method is used to get an actual LocationProvider implementation based
	 * on the defined criteria. The implementation chooses the LocationProvider so that it
	 * best fits the defined criteria, taking into account also possible implementation
	 * dependent preferences of the end user. If no concrete LocationProvider could be
	 * created that typically can match the defined criteria but there are other location
	 * providers not meeting the criteria that could be returned for a more relaxed
	 * criteria, null is returned to indicate this. The LocationException is thrown, if
	 * all supported location providers are out of service. A LocationProvider instance is
	 * returned if there is a location provider meeting the criteria in either the
	 * available or temporarily unavailable state. Implementations should try to select
	 * providers in the available state before providers in temporarily unavailable state,
	 * but this can't be always guaranteed because the implementation may not always know
	 * the state correctly at this point in time. If a LocationProvider meeting the
	 * criteria can be supported but is currently out of service, it shall not be
	 * returned.
	 * <p>
	 * When this method is called with a Criteria that has all fields set to the default
	 * values (i.e. the least restrictive criteria possible), the implementation shall
	 * return a LocationProvider if there is any provider that isn't in the out of service
	 * state. Passing null as the parameter is equal to passing a Criteria that has all
	 * fields set to the default values, i.e. the least restrictive set of criteria.
	 * <p>
	 * This method only makes the selection of the provider based on the criteria and is
	 * intended to return it quickly to the application. Any possible initialization of
	 * the provider is done at an implementation dependent time and MUST NOT block the
	 * call to this method.
	 * <p>
	 * This method may, depending on the implementation, return the same LocationProvider
	 * instance as has been returned previously from this method to the calling
	 * application, if the same instance can be used to fulfil both defined criteria. Note
	 * that there can be only one LocationListener associated with a LocationProvider
	 * instance.
	 * 
	 * @param criteria
	 *            the criteria for provider selection or null to indicate the least
	 *            restrictive criteria with default values
	 * @return a LocationProvider meeting the defined criteria or null if a
	 *         LocationProvider that meets the defined criteria can't be returned but
	 *         there are other supported available or temporarily unavailable providers
	 *         that do not meet the criteria.
	 * @throws LocationException
	 *             if all LocationProviders are currently out of service
	 */
	public static LocationProvider getInstance(Criteria criteria)
			throws LocationException {
		
		loadConfiguration();

		if (mode == null || "GPS".equals(mode)) {
			// GPS over Bluetooth
			return new LocationProviderBTGPS(criteria);
		} else if ("KML".equals(mode)) {
			// Google Earth file
			if ((resourceURI == null) || resourceURI.equals("")) {
				TTUtils.logWarning("Location resource is not configured. Embedded default.kml file will be used");
				resourceURI = "/com/openlapi/default.kml";
			}
			return new LocationProviderKML(criteria, resourceURI, pollInterval);
		} else if ("LMS".equals(mode)) {
			// random entry from the LandmarkStore
			return new LocationProviderLMS(criteria, null, pollInterval);
		} else if ("NMEA".equals(mode)) {
			// random entry from the LandmarkStore
			if ((resourceURI == null) || resourceURI.equals("")) {
				TTUtils.logWarning("Location resource is not configured. Embedded default.nmea file will be used");
				resourceURI = "/com/openlapi/default.nmea";
			}
			return new LocationProviderNMEA(criteria, resourceURI);
		}
		throw new LocationException();
	}

	/**
	 * Returns the last known location that the implementation has. This is the best
	 * estimate that the implementation has for the previously known location.
	 * Applications can use this method to obtain the last known location and check the
	 * timestamp and other fields to determine if this is recent enough and good enough
	 * for the application to use without needing to make a new request for the current
	 * location.
	 * 
	 * @return a location object. null is returned if the implementation doesn't have any
	 *         previous location information.
	 * @throws SecurityException
	 *             if the calling application does not have a permission to query the
	 *             location information
	 */
	public static Location getLastKnownLocation() throws SecurityException {
		// test Security permissions
		OpenLAPICommon.testPermission("javax.microedition.location.Location");

		return lastKnownLocation;
	}

	/**
	 * Removes a ProximityListener from the list of recipients for updates. If the
	 * specified listener is not registered or if the parameter is null, this method
	 * silently returns with no action.
	 * <p>
	 * <i>NOTE: the specification conflicts with itself on how to handle null parameter.
	 * We throw the exception.</i>
	 * 
	 * @param listener
	 *            the listener to remove
	 * @throws NullPointerException
	 *             if the parameter is null
	 */
	public static void removeProximityListener(ProximityListener listener)
			throws NullPointerException {
		if (listener == null)
			throw new NullPointerException();

		// synchronise because this is temporarily breaking the Enumeration
		synchronized (listeners) {
			// can't use Enumeration because we are removing elements
			Object[] list = new Object[listeners.size()];
			Enumeration en = listeners.elements();
			for (int i = 0; en.hasMoreElements(); i++) {
				list[i] = en.nextElement();
			}
			for (int i = 0; i < list.length; i++) {
				Object[] listenerArray = (Object[]) list[i];
				// remove every registration of this listener
				if (listenerArray[0].equals(listener)) {
					listeners.removeElement(listenerArray);
				}
			}
		}
	}

	/**
	 * Check if the ProximityListeners and alert them if the given Location meets their
	 * criteria.
	 * <p>
	 * Note that this involves calculating the distance between the current location and
	 * the location the listener is watching. The Coordinates.distance() method is a very
	 * expensive operation, so we check bounding boxes on the latitude and longitude to
	 * see if the location is in the general region of the listener. This effectively
	 * enables the use of many listeners with relatively low numerical cost.
	 * 
	 * @param location
	 */
	private static void alertProximityListeners(Location location) {
		// Syncronised incase we receive another update while calling this which
		// may result in the Enumeration breaking
		synchronized (listeners) {
			// alert proximity listeners
			QualifiedCoordinates current = location.getQualifiedCoordinates();
			Enumeration en = listeners.elements();
			for (; en.hasMoreElements();) {
				Object[] listenerArray = (Object[]) en.nextElement();
				Coordinates watching = (Coordinates) listenerArray[1];
				float radius = ((Float) listenerArray[2]).floatValue();

				float accuracy = current.getHorizontalAccuracy();
				if (!Float.isNaN(accuracy)) {
					// if there was an accuracy associated to our current
					// Location, consider it in the radius
					radius += accuracy;
				}

				// simple bounding on latitude is much less costly than distance
				// we use semi-minor circumference to get max bounding box
				double radiusAsAngle =
						radius * OpenLAPICommon.INV_MINOR_CIRCUMFERENCE;
				double latDiff =
						Math.abs(watching.getLatitude() - current.getLatitude());
				if (latDiff > radiusAsAngle) {
					// outside bounding box
					continue;
				}
				// The equivalent for longitude is more complicated
				double longDiff = longMinInMeters(watching, current);
				if (longDiff > radius) {
					// outside bounding box
					continue;
				}

				// calculating actual distance is very costly
				float distance = watching.distance(current);
				if (distance <= radius) {
					// within the tolerance
					// remove the listener from the list
					listeners.removeElement(listenerArray);
					// alert the listener
					ProximityListener listener =
							(ProximityListener) listenerArray[0];
					listener.proximityEvent(watching, location);
				}
			}
		}
	}

	/**
	 * Mechanism for setting the emulator mode by setting a property in an application
	 * resource file.
	 * <p>
	 * Because the J2ME specs made the user properties part of the MIDlet class, and not
	 * static, we have to look in the manifest file directly. gotta love those J2ME
	 * designers...
	 * 
	 * @return
	 * @throws IOException
	 */
	private static void loadConfiguration() {
		mode = Configuration.getProperty("org.thenesis.midpath.location.mode");
		resourceURI = Configuration.getProperty("org.thenesis.midpath.location.resource.uri");
		pollInterval = Configuration.getNonNegativeIntProperty("org.thenesis.midpath.location.resource.pollInterval", 5000);
	}

	/**
	 * When given two Coordinate objects, this method will first calculate the difference
	 * in longitude between them. It will then calculate and return the minimal
	 * longitudinal distance between them in meters, which would be the distance if the
	 * most equatorial Coordinate were moved to be on the same latitude as the other.
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	private static double longMinInMeters(Coordinates c1, Coordinates c2) {
		// calculate difference in longitude, which may be via the back
		double front = Math.abs(c1.getLongitude() - c2.getLongitude());
		double back =
				Math.abs(360 - Math.abs(c1.getLongitude())
						- Math.abs(c2.getLongitude()));
		double diffLong = (front < back) ? front : back;

		// calculate the latitude closer to a pole
		double lat1 = Math.abs(c1.getLatitude());
		double lat2 = Math.abs(c2.getLatitude());
		double lat = (lat1 > lat2) ? lat1 : lat2;

		// calculate the cross sectional radius of the earth for each latitude
		double radius =
				Math.abs(OpenLAPICommon.SEMI_MAJOR
						* Math.cos(Math.toRadians(lat)));

		// calculate the distance in meters at each latitude
		double distance = 2 * Math.PI * radius * (diffLong / 360d);
		return distance;
	}

	/**
	 * Convenience method that tells all the proximity listeners that we are periodically
	 * reporting the location or not.
	 * 
	 * @param status
	 */
	static protected void alertProximityListeners(boolean status) {
		Enumeration en = listeners.elements();
		for (; en.hasMoreElements();) {
			Object[] listenerArray = (Object[]) en.nextElement();
			ProximityListener listener = (ProximityListener) listenerArray[0];
			listener.monitoringStateChanged(status);
		}
	}

	/**
	 * This is an implementation specific method to alert the LocationProvider of the
	 * latest location. This also ensures that all ProximityListeners are alerted if they
	 * are in range.
	 * <p>
	 * Will check if the given Location has a more recent timestamp before committing to
	 * the update.
	 * 
	 * @param location
	 */
	protected static void setLastKnownLocation(Location location) {
		if (location == null || !location.isValid())
			return;

		// only update if the given Location is actually newer! remember, some
		// devices provide locations with a time delay.
		if (location.getTimestamp() <= lastKnownLocation.getTimestamp())
			return;

		lastKnownLocation = location;

		// alert ProximityListeners
		alertProximityListeners(location);
	}

	/**
	 * Empty constructor to help implementations and extensions. This is not intended to
	 * be used by applications. Applications should not make subclasses of this class and
	 * invoke this constructor from the subclass.
	 */
	protected LocationProvider() {
	}

	/**
	 * Retrieves a Location with the constraints given by the Criteria associated with
	 * this class. If no result could be retrieved, a LocationException is thrown. If the
	 * location can't be determined within the timeout period specified in the parameter,
	 * the method shall throw a LocationException. If the provider is temporarily
	 * unavailable, the implementation shall wait and try to obtain the location until the
	 * timeout expires. If the provider is out of service, then the LocationException is
	 * thrown immediately.
	 * <p>
	 * Note that the individual Location returned might not fulfil exactly the criteria
	 * used for selecting this LocationProvider. The Criteria is used to select a location
	 * provider that typically is able to meet the defined criteria, but not necessarily
	 * for every individual location measurement.
	 * 
	 * @param timeout
	 *            a timeout value in seconds. -1 is used to indicate that the
	 *            implementation shall use its default timeout value for this provider.
	 * @return
	 * @throws LocationException
	 *             if the location couldn't be retrieved or if the timeout period expired
	 * @throws InterruptedException
	 *             if the operation is interrupted by calling reset() from another thread
	 * @throws SecurityException
	 *             if the calling application does not have a permission to query the
	 *             location information
	 * @throws IllegalArgumentException
	 *             if the timeout = 0 or timeout < -1
	 */
	public abstract Location getLocation(int timeout) throws LocationException,
			InterruptedException, SecurityException, IllegalArgumentException;

	/**
	 * Returns the current state of this LocationProvider. The return value shall be one
	 * of the availability status code constants defined in this class.
	 * 
	 * @return the availability state of this LocationProvider
	 */
	public abstract int getState();

	/**
	 * Resets the LocationProvider. All pending synchronous location requests will be
	 * aborted and any blocked getLocation method calls will terminate with
	 * InterruptedException.
	 * <p>
	 * Applications can use this method e.g. when exiting to have its threads freed from
	 * blocking synchronous operations.
	 */
	public abstract void reset();

	/**
	 * Adds a LocationListener for updates at the defined interval. The listener will be
	 * called with updated location at the defined interval. The listener also gets
	 * updates when the availablilty state of the LocationProvider changes. Passing in -1
	 * as the interval selects the default interval which is dependent on the used
	 * location method. Passing in 0 as the interval registers the listener to only
	 * receive provider status updates and not location updates at all.
	 * <p>
	 * Only one listener can be registered with each LocationProvider instance. Setting
	 * the listener replaces any possibly previously set listener. Setting the listener to
	 * null cancels the registration of any previously set listener.
	 * <p>
	 * The implementation shall initiate obtaining the first location result immediately
	 * when the listener is registered and provide the location to the listener as soon as
	 * it is available. Subsequent location updates will happen at the defined interval
	 * after the first one. If the specified update interval is smaller than the time it
	 * takes to obtain the first result, the listener shall receive location updates with
	 * invalid Locations at the defined interval until the first location result is
	 * available. Note that prior to getting the first valid location result, the timeout
	 * parameter has no effect. When the first valid location result is obtained, the
	 * implementation may return it to the application immediately, i.e. before the next
	 * interval is due. This implies that in the beginning when starting to obtain
	 * location results, the listener may first get updates with invalid location results
	 * at the defined interval and when the first valid location result is obtained, it
	 * may be returned to the listener as soon as it is available before the next interval
	 * is due. After the first valid location result is delivered to the application the
	 * timeout parameter is used and the next update is delivered between the time defined
	 * by the interval and time interval+timeout after the previous update.
	 * <p>
	 * The timeout parameter determines a timeout that is used if it's not possible to
	 * obtain a new location result when the update is scheduled to be provided. This
	 * timeout value indicates how many seconds the update is allowed to be provided late
	 * compared to the defined interval. If it's not possible to get a new location result
	 * (interval + timeout) seconds after the previous update, the update will be made and
	 * an invalid Location instance is returned. This is also done if the reason for the
	 * inability to obtain a new location result is due to the provider being temporarily
	 * unavailable or out of service. For example, if the interval is 60 seconds and the
	 * timeout is 10 seconds, the update must be delivered at most 70 seconds after the
	 * previous update and if no new location result is available by that time the update
	 * will be made with an invalid Location instance.
	 * <p>
	 * The maxAge parameter defines how old the location result is allowed to be provided
	 * when the update is made. This allows the implementation to reuse location results
	 * if it has a recent location result when the update is due to be delivered. This
	 * parameter can only be used to indicate a larger value than the normal time of
	 * obtaining a location result by a location method. The normal time of obtaining the
	 * location result means the time it takes normally to obtain the result when a
	 * request is made. If the application specifies a time value that is less than what
	 * can be realized with the used location method, the implementation shall provide as
	 * recent location results as are possible with the used location method. For example,
	 * if the interval is 60 seconds, the maxAge is 20 seconds and normal time to obtain
	 * the result is 10 seconds, the implementation would normally start obtaining the
	 * result 50 seconds after the previous update. If there is a location result
	 * otherwise available that is more recent than 40 seconds after the previous update,
	 * then the maxAge setting to 20 seconds allows to return this result and not start
	 * obtaining a new one.
	 * <p>
	 * The requirements for the intervals hold while the application is executing. If the
	 * application environment or the device is suspended so that the application will not
	 * execute at all and then the environment is later resumed, the periodic updates MUST
	 * continue at the defined interval but there may be a shift after the suspension
	 * period compared to the original interval schedule.
	 * 
	 * @param listener
	 *            the listener to be registered. If set to null the registration of any
	 *            previously set listener is cancelled.
	 * @param interval
	 *            the interval in seconds. -1 is used for the default interval of this
	 *            provider. 0 is used to indicate that the application wants to receive
	 *            only provider status updates and not location updates at all.
	 * @param timeout
	 *            timeout value in seconds, must be greater than 0. if the value is -1,
	 *            the default timeout for this provider is used. Also, if the interval is
	 *            -1 to indicate the default, the value of this parameter has no effect
	 *            and the default timeout for this provider is used. If timeout == -1 and
	 *            interval > 0 and the default timeout of the provider is greater than the
	 *            specified interval, then the timeout parameter is handled as if the
	 *            application had passed the same value as timeout as the interval (i.e.
	 *            timeout is considered to be equal to the interval). If the interval is
	 *            0, this parameter has no effect.
	 * @param maxAge
	 *            maximum age of the returned location in seconds, must be greater than 0
	 *            or equal to -1 to indicate that the default maximum age for this
	 *            provider is used. Also, if the interval is -1 to indicate the default,
	 *            the value of this parameter has no effect and the default maximum age
	 *            for this provider is used.
	 */
	public abstract void setLocationListener(LocationListener listener,
			int interval, int timeout, int maxAge);

}
