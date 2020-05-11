package de.avetana.bluetooth.sdp;

/**
 * A special Exception used by the DiscoveryListener.
 *
 * <br><br><b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
 *
 * This file is part of the Avetana bluetooth API for Linux.<br><br>
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version. <br><br>
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.<br><br><br><br>
 *
 *
 * <b>Description:</b><br>
 * The Exception thrown if more than one service is discovered. See therefore the method
 * javax.bluetooth.DiscoveryAgent.selectService(...)
 *
 *
 * @author Julien Campana
 */

public class ServiceFoundException extends Exception {

	private int m_serv = 0;

	/**
	 * Creates a new ServiceFound exception
	 * @param serv The number of services found
	 */
	public ServiceFoundException(int serv) {
		m_serv = serv;
	}

	/**
	 * Creates a new ServiceFound exception
	 * @param s The exception message
	 * @param serv The number of services found
	 */
	public ServiceFoundException(String s, int serv) {
		super(s);
		m_serv = serv;
	}

	/**
	 * Returns the number of services found.
	 * @return The number of services found.
	 */
	public int getNumberofServicesFound() {
		return m_serv;
	}

}