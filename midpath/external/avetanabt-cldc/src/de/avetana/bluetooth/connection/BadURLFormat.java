package de.avetana.bluetooth.connection;

/**
 * The exception which identifies non-correct URLs.
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
 * This exception occurs when the programmer or the end-user are using a bad connection URL. They are
 * numerous reasons that can lead to this exception. For example:
 * <ul>
 * <li>The connection URL does not represent a supported protocol</li>
 * <li>Options in the URL are incompatible (for example encrypt=true and authenticate=false is a nonsense)</li>
 * <li>The remote BT address is not correct</li>
 * <li>...</li>
 * </ul>
 *
 * @author Julien Campana
 */
public class BadURLFormat extends Exception {

  /**
   * Creates a new BadURLFormat object.<br>
   * The exception message of a default BadURLFormat Exception is: <br>
   * <i>Bad URL Format: URL must begin with protocol://localhost:UUID or protocoll://BTAdress</i>
   */
  public BadURLFormat() {
    super("Bad URL Format: URL must begin with protocol://localhost:UUID or protocoll://BTAdress");
  }

  /**
   * Creates a new BadURLFormat object and set the exception message.
   * @param message The exception message
   */
  public BadURLFormat(String message) {
    super(message);
  }
}