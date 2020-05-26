/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA  
 */
package org.thenesis.microbackend.ui;

public class Logging {

    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARNING = 3;
    public static final int ERROR = 4;

    private static String STRING_DEBUG = "[DEBUG]";
    private static String STRING_INFO = "[INFO]";
    private static String STRING_WARNING = "[WARNING]";
    private static String STRING_ERROR = "[ERROR]";

    public static final boolean TRACE_ENABLED = false;

    public static int currentLevel = 4;

    public static int getLevel() {
        return currentLevel;
    }

    public static void log(String message, int level) {
        if (currentLevel >= level) {
            String levelString = "";
            switch (level) {
            case DEBUG:
                levelString = STRING_DEBUG;
                break;
            case INFO:
                levelString = STRING_INFO;
                break;
            case WARNING:
                levelString = STRING_WARNING;
                break;
            case ERROR:
                levelString = STRING_ERROR;
                break;
            }

            System.out.println(levelString + " " + message);
        }
    }

}
