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

/**
 * The criteria used for the selection of the location provider is defined by the values
 * in this class. It is up to the implementation to provide a The criteria used for the
 * selection of the location provider is defined by the values in this class. It is up to
 * the implementation to provide a <code>LocationProvider</code> that can obtain
 * locations constrained by these values.
 * <p>
 * Instances of <code>Criteria</code> are used by the application to indicate criteria
 * for choosing the location provider in the <code>LocationProvider.getInstance</code>
 * method call. The implementation considers the different criteria fields to choose the
 * location provider that best fits the defined criteria. The different criteria fields do
 * not have any defined priority order but the implementation uses some implementation
 * specific logic to choose the location provider that can typically best meet the defined
 * criteria.
 * <p>
 * However, the cost criteria field is treated differently from others. If the application
 * has set the cost field to indicate that the returned location provider is not allowed
 * to incur financial cost to the end user, the implementation MUST guarantee that the
 * returned location provider does not incur cost.
 * <p>
 * If there is no available location provider that is able to meet all the specified
 * criteria, the implementation is allowed to make its own best effort selection of a
 * location provider that is closest to the defined criteria (provided that the cost
 * criteria is met). However, an implementation is not required to return a location
 * provider if it does not have any available provider that is able to meet these criteria
 * or be sufficiently close to meeting them, where the judgement of sufficiently close is
 * an implementation dependent best effort choice. It is left up to the implementation to
 * consider what is close enough to the specified requirements that it is worth providing
 * the location provider to the application.
 * <p>
 * The default values for the criteria fields are specified below in the table. The
 * default values are always the least restrictive option that will match all location
 * providers. Default values:
 * <p>
 * <table border=1>
 * <tr>
 * <th>Criteria field</th>
 * <th>Default value</th>
 * </tr>
 * <tr>
 * <td>Horizontal accuracy</td>
 * <td>NO_REQUIREMENT</td>
 * </tr>
 * <tr>
 * <td>Vertical accuracy</td>
 * <td>NO_REQUIREMENT</td>
 * </tr>
 * <tr>
 * <td>Preferred response time</td>
 * <td>NO_REQUIREMENT</td>
 * </tr>
 * <tr>
 * <td>Power consumption</td>
 * <td>NO_REQUIREMENT</td>
 * </tr>
 * <tr>
 * <td>Cost allowed</td>
 * <td>true (allowed to cost)</td>
 * </tr>
 * <tr>
 * <td>Speed and course required</td>
 * <td>false (not required)</td>
 * </tr>
 * <tr>
 * <td>Altitude required</td>
 * <td>false (not required)</td>
 * </tr>
 * <tr>
 * <td>Address info required</td>
 * <td>false (not required)</td>
 * </tr>
 * </table>
 * <p>
 * The implementation of this class only retains the values that are passed in using the
 * set* methods. It does not try to validate the values of the parameters in any way.
 * Applications may set any values it likes, even negative values, but the consequence may
 * be that no matching <code>LocationProvider</code> can be created. that can obtain
 * locations constrained by these values.
 */
public class Criteria {

	/**
	 * Constant indicating no requirements for the parameter.
	 * <p>
	 * Note that because JSR-179 defines this as integer value 0, it means that it is
	 * impossible to tell the difference between values of zero, and those with no
	 * requirement. For example, there is no way to differentiate between "no error" and
	 * "no requirement on error".
	 */
	public static final int NO_REQUIREMENT = 0;

	/**
	 * Level indicating high power consumption allowed.
	 */
	public static final int POWER_USAGE_HIGH = 3;

	/**
	 * Level indicating only low power consumption allowed.
	 */
	public static final int POWER_USAGE_LOW = 1;

	/**
	 * Level indicating average power consumption allowed.
	 */
	public static final int POWER_USAGE_MEDIUM = 2;

	/**
	 * @see #setAddressInfoRequired(boolean)
	 * @see #isAddressInfoRequired()
	 */
	private boolean addressInfoRequired = false;

	/**
	 * @see #setAltitudeRequired(boolean)
	 * @see #isAltitudeRequired()
	 */
	private boolean altitudeRequired = false;

	/**
	 * @see #setCostAllowed(boolean)
	 * @see #isAllowedToCost()
	 */
	private boolean costAllowed = true;

	/**
	 * @see #setHorizontalAccuracy(int)
	 * @see #getHorizontalAccuracy()
	 */
	private int horizontalAccuracy = NO_REQUIREMENT;

	/**
	 * @see #setPreferredPowerConsumption(int)
	 * @see #getPreferredPowerConsumption()
	 */
	private int powerConsumption = NO_REQUIREMENT;

	/**
	 * @see #setPreferredResponseTime(int)
	 * @see #getPreferredResponseTime()
	 */
	private int responseTime = NO_REQUIREMENT;

	/**
	 * @see #setSpeedAndCourseRequired(boolean)
	 * @see #isSpeedAndCourseRequired()
	 */
	private boolean speedAndCourseRequired = false;

	/**
	 * @see #setVerticalAccuracy(int)
	 * @see #getVerticalAccuracy()
	 */
	private int verticalAccuracy = NO_REQUIREMENT;

	/**
	 * Constructs a Criteria object. All the fields are set to the default values that are
	 * specified in the specification of the set methods for the parameters.
	 */
	public Criteria() {
	}

	/**
	 * Returns the horizontal accuracy value set in this Criteria.
	 *
	 * @return the horizontal accuracy in meters
	 * @see #setHorizontalAccuracy(int)
	 */
	public int getHorizontalAccuracy() {
		return horizontalAccuracy;
	}

	/**
	 * Returns the preferred power consumption.
	 *
	 * @return the power consumption level, should be one of NO_REQUIREMENT,
	 *         POWER_USAGE_LOW, POWER_USAGE_MEDIUM, POWER_USAGE_HIGH.
	 * @see {@link #setPreferredPowerConsumption(int)}
	 */
	public int getPreferredPowerConsumption() {
		return powerConsumption;
	}

	/**
	 * Returns the preferred maximum response time.
	 *
	 * @return the maximum response time in milliseconds
	 * @see #setPreferredResponseTime(int)
	 */
	public int getPreferredResponseTime() {
		return responseTime;
	}

	/**
	 * Returns the vertical accuracy value set in this Criteria.
	 *
	 * @return the accuracy in meters See Also: setVerticalAccuracy(int)
	 */
	public int getVerticalAccuracy() {
		return verticalAccuracy;
	}

	/**
	 * Returns whether the location provider should be able to determine textual address
	 * information.
	 *
	 * @return whether the location provider should be able to normally provide textual
	 *         address information. true means that it should be able, false means that
	 *         this is not required.
	 * @see #setAddressInfoRequired(boolean)
	 */
	public boolean isAddressInfoRequired() {
		return addressInfoRequired;
	}

	/**
	 * Returns the preferred cost setting.
	 *
	 * @return the preferred cost setting. true if allowed to cost, false if it must be
	 *         free of charge.
	 * @see {@link #setCostAllowed(boolean)}
	 */
	public boolean isAllowedToCost() {
		return costAllowed;
	}

	/**
	 * Returns whether the location provider should be able to determine altitude.
	 *
	 * @return whether the location provider should be able to determine altitude. true
	 *         means that it should be able, false means that this is not required.
	 * @see #setAltitudeRequired(boolean)
	 */
	public boolean isAltitudeRequired() {
		return altitudeRequired;
	}

	/**
	 * Returns whether the location provider should be able to determine speed and course.
	 *
	 * @return whether the location provider should be able to determine speed and course.
	 *         true means that it should be able, false means that this is not required.
	 * @see #setSpeedAndCourseRequired(boolean)
	 */
	public boolean isSpeedAndCourseRequired() {
		return speedAndCourseRequired;
	}

	/**
	 * Sets whether the location provider should be able to determine textual address
	 * information. Setting this criteria to true implies that a location provider should
	 * be selected that is capable of providing the textual address information. This does
	 * not mean that every returned location instance necessarily will have all the
	 * address information filled in, though. Default is false.
	 *
	 * @param addressInfoRequired
	 *            if set to true, the LocationProvider is required to be able to normally
	 *            determine the textual address information. if set the false, the textual
	 *            address information is not required.
	 * @see #isAddressInfoRequired()
	 */
	public void setAddressInfoRequired(boolean addressInfoRequired) {
		this.addressInfoRequired = addressInfoRequired;
	}

	/**
	 * Sets whether the location provider should be able to determine altitude. Default is
	 * false.
	 *
	 * @param altitudeRequired
	 *            if set to true, the LocationProvider is required to be able to normally
	 *            determine the altitude if set the false, the altitude is not required.
	 * @see #isAltitudeRequired()
	 */
	public void setAltitudeRequired(boolean altitudeRequired) {
		this.altitudeRequired = altitudeRequired;
	}

	/**
	 * Sets the preferred cost setting. Sets whether the requests for location
	 * determination is allowed to incur any financial cost to the user of the terminal.
	 * <p>
	 * The default is true, i.e. the method is allowed to cost.
	 * <p>
	 * Note that the platform implementation may not always be able to know if a location
	 * method implies cost to the end user or not. If the implementation doesn't know, it
	 * MUST assume that it may cost. When this criteria is set to false, the
	 * implementation may only return a LocationProvider of which it is certain that using
	 * it for determining the location does not cause a per usage cost to the end user.
	 *
	 * @param costAllowed
	 *            false if location determination is not allowed to cost, true if it is
	 *            allowed to cost
	 * @see #isAllowedToCost()
	 */
	public void setCostAllowed(boolean costAllowed) {
		this.costAllowed = costAllowed;
	}

	/**
	 * Sets the desired horizontal accuracy preference. Accuracy is measured in meters.
	 * The preference indicates maximum allowed typical 1-sigma standard deviation for the
	 * location method. Default is NO_REQUIREMENT, meaning no preference on horizontal
	 * accuracy.
	 *
	 * @param accuracy
	 *            the preferred horizontal accuracy in meters
	 * @see #getHorizontalAccuracy()
	 */
	public void setHorizontalAccuracy(int accuracy) {
		if (accuracy < 0) {
			horizontalAccuracy = NO_REQUIREMENT;
		}
		horizontalAccuracy = accuracy;
	}

	/**
	 * Sets the preferred maximum level of power consumption. These levels are inherently
	 * indeterminable and depend on many factors. It is the judgement of the
	 * implementation that defines a positioning method as consuming low power or high
	 * power. Default is NO_REQUIREMENT, meaning power consumption is not a quality
	 * parameter.
	 *
	 * @param level
	 *            the preferred maximum level of power consumption. Should be one of
	 *            NO_REQUIREMENT, POWER_USAGE_LOW, POWER_USAGE_MEDIUM, POWER_USAGE_HIGH.
	 * @see #getPreferredPowerConsumption()
	 */
	public void setPreferredPowerConsumption(int level) {
		if ((level == POWER_USAGE_LOW) || (level == POWER_USAGE_MEDIUM)
				|| (level == POWER_USAGE_HIGH)) {
			powerConsumption = level;
		} else {
			powerConsumption = NO_REQUIREMENT;
		}
	}

	/**
	 * Sets the desired maximum response time preference. This value is typically used by
	 * the implementation to determine a location method that typically is able to produce
	 * the location information within the defined time. Default is NO_REQUIREMENT,
	 * meaning no response time constraint.
	 *
	 * @param time
	 *            the preferred time constraint and timeout value in milliseconds
	 * @see #getPreferredResponseTime()
	 */
	public void setPreferredResponseTime(int time) {
		if (time < 0) {
			responseTime = NO_REQUIREMENT;
		}
		responseTime = time;
	}

	/**
	 * Sets whether the location provider should be able to determine speed and course.
	 * Default is false.
	 *
	 * @param speedAndCourseRequired
	 *            if set to true, the LocationProvider is required to be able to normally
	 *            determine the speed and course. if set the false, the speed and course
	 *            are not required.
	 * @see #isSpeedAndCourseRequired()
	 */
	public void setSpeedAndCourseRequired(boolean speedAndCourseRequired) {
		this.speedAndCourseRequired = speedAndCourseRequired;
	}

	/**
	 * Sets the desired vertical accuracy preference. Accuracy is measured in meters. The
	 * preference indicates maximum allowed typical 1-sigma standard deviation for the
	 * location method. Default is NO_REQUIREMENT, meaning no preference on vertical
	 * accuracy.
	 *
	 * @param accuracy
	 *            the preferred vertical accuracy in meters
	 * @see #getVerticalAccuracy()
	 */
	public void setVerticalAccuracy(int accuracy) {
		if (accuracy < 0) {
			verticalAccuracy = NO_REQUIREMENT;
		}
		verticalAccuracy = accuracy;
	}

}
