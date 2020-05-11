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
package org.thenesis.midpath.test.suite;

public interface TestHarness {

	/**
	 * Records the result of a boolean check.
	 *
	 * @param result  the result.
	 */
	public void check(boolean result);

	/**
	 * Checks the two objects for equality and records the result of
	 * the check.
	 *
	 * @param result  the actual result.
	 * @param expected  the expected result.
	 */
	public void check(Object result, Object expected);

	/**
	 * Checks two booleans for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(boolean result, boolean expected);

	/**
	 * Checks two ints for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(int result, int expected);

	/**
	 * Checks two longs for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(long result, long expected);

	/**
	 * Checks two doubles for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(double result, double expected);

	// These methods are like the above, but checkpoint first.
	public void check(boolean result, String name);

	public void check(Object result, Object expected, String name);

	public void check(boolean result, boolean expected, String name);

	public void check(int result, int expected, String name);

	public void check(long result, long expected, String name);

	public void check(double result, double expected, String name);

	/**
	 * A convenience method that sets a checkpoint with the specified name
	 * then records a failed check.
	 *
	 * @param name  the checkpoint name.
	 */
	public void fail(String name);

	/**
	 * Records a check point.  This can be used to mark a known place in a 
	 * testlet.  It is useful if you have a large number of tests -- it makes 
	 * it easier to find a failing test in the source code. 
	 *
	 * @param name  the check point name.
	 */
	public void checkPoint(String name);

	/**
	 * This will print a message when in verbose mode.
	 *
	 * @param message  the message.
	 */
	public void verbose(String message);

	/**
	 * Writes a message to the debug log.
	 *
	 * @param message  the message.
	 */
	public void debug(String message);

	/**
	 * Writes a message to the debug log with or without a newline.
	 *
	 * @param message  the message.
	 * @param newline  a flag to control whether or not a newline is added.
	 */
	public void debug(String message, boolean newline);

	/**
	 * Writes a stack trace for the specified exception to a log.
	 *
	 * @param ex  the exception.
	 */
	public void debug(Throwable ex);

	/**
	 * Writes the contents of an array to the log.
	 *
	 * @param o  the array of objects.
	 * @param desc  the description.
	 */
	public void debug(Object[] o, String desc);

}