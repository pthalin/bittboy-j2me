/*
*  (c) Copyright 2003 Christian Lorenz  ALL RIGHTS RESERVED.
*
* This file is part of the JavaBluetooth Stack.
*
* The JavaBluetooth Stack is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2 of
* the License, or (at your option) any later version.
*
* The JavaBluetooth Stack is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* Created on May 22, 2003
* by Christian Lorenz
*
*/

package de.avetana.bluetooth.util;

/**
 * This class contains methods for printing Debug messages. Which messages are printed may be changed by adjusting the
 * DEBUGLEVELMIN and DEBUGLEVELMAX variables. Care should be taken when prining large byte arrays, as this
 * will ususally kill the TINI.
 * @author Christian Lorenz
 */
public class Debug {
    public static boolean debugMessages   = true;
    public static final int DEBUGLEVELMIN = 1;
    public static final int DEBUGLEVELMAX = 10;

    public static void println(int level, String label, byte[] packet) {
        if ((level >= DEBUGLEVELMIN) && (level <= DEBUGLEVELMAX)) {
            for (int i = 0; i < packet.length; i++)
                label += " " + Integer.toString((packet[i] & 0xff) + 0x100, 16).substring(1);
            System.err.println(label);
        }
    }

    public static void println(int level, String debugMessage) {
        if ((level >= DEBUGLEVELMIN) && (level <= DEBUGLEVELMAX)) { System.err.println(debugMessage); }
    }

    public static String printByteArray(byte[] packetData) {
        String output = "";
        for (int i = 0; i < packetData.length; i++)
            output += " " + Integer.toString((packetData[i] & 0xff) + 0x100, 16).substring(1);
        return output;
    }
}

