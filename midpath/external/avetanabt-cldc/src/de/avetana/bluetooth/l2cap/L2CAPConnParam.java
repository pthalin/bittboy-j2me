package de.avetana.bluetooth.l2cap;

/**
 * The class used to manage L2CAP connection parameters.
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
 * A utility class for the C-code. <br>
 * This class encapsulates three variables set by the bluez stack for a connection.
 * <ul><li>The connection ID</li><li>The value of receiveMTU</li><li>The value of transmitMTU</li></ul>
 * The native method openL2CAPNative returns a L2CAPConnParam object.
 */
public class L2CAPConnParam {

  /**
   * The connection ID
   */
  public int m_fid=-1;

  /**
   * The value of receiveMTU
   */
  public int m_receiveMTU;

  /**
   * The value of transmitMTU
   */
  public int m_transmitMTU;

  /**
   * Creates a new instance of L2CAPConnParam and sets the connection parameters
   * @param fid The connection ID
   * @param receiveMTU The value of receiveMTU
   * @param transmitMTU The value of transmitMTU
   */
  public L2CAPConnParam(int fid, int receiveMTU, int transmitMTU) {
    m_fid=fid;
    m_receiveMTU=receiveMTU;
    m_transmitMTU=transmitMTU;
  }
}