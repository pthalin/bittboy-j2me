/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (c) 2005  Mark J. Wielaard  <mark@klomp.org>
 * Written by Tom Tromey <tromey@cygnus.com>
 * 
 * This file is part of Mauve.
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

/**
* This base class defines the API that test cases can report against.  This
* code has been lifted from the Mauve project (and reformatted and 
* commented).
*/
public abstract class AbstractTestSuite implements TestHarness {

	private int count;
	protected String className;
	private String last_check;
	
	public AbstractTestSuite(String className) {
		this.className = className;
	}

	/**
	 * Records the result of a boolean check.
	 *
	 * @param result  the result.
	 */
	public void check(boolean result) {
		String message = (result ? "PASS" : "FAIL") + ": " + className
				+ ((last_check == null) ? "" : (": " + last_check)) + " (number " + count++ + ")";
		System.out.println(message);
	}

	/**
	 * Checks the two objects for equality and records the result of
	 * the check.
	 *
	 * @param result  the actual result.
	 * @param expected  the expected result.
	 */
	public void check(Object result, Object expected) {
		boolean ok = (result == null ? expected == null : result.equals(expected));
		check(ok);
		// This debug message may be misleading, depending on whether
		// string conversion produces same results for unequal objects.
		if (!ok)
			debug("got " + result + " but expected " + expected);
	}

	/**
	 * Checks two booleans for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(boolean result, boolean expected) {
		boolean ok = (result == expected);
		check(ok);
		if (!ok)
			debug("got " + result + " but expected " + expected);
	}

	/**
	 * Checks two ints for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(int result, int expected) {
		boolean ok = (result == expected);
		check(ok);
		if (!ok)
			debug("got " + result + " but expected " + expected);
	}

	/**
	 * Checks two longs for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(long result, long expected) {
		boolean ok = (result == expected);
		check(ok);
		if (!ok)
			debug("got " + result + " but expected " + expected);
	}

	/**
	 * Checks two doubles for equality and records the result of the check.
	 * 
	 * @param result the actual result.
	 * @param expected the expected result.
	 */
	public void check(double result, double expected) {
		// This triple check overcomes the fact that == does not
		// compare NaNs, and cannot tell between 0.0 and -0.0;
		// and all without relying on java.lang.Double (which may
		// itself be buggy - else why would we be testing it? ;)
		// For 0, we switch to infinities, and for NaN, we rely
		// on the identity in JLS 15.21.1 that NaN != NaN is true.
		boolean ok = (result == expected ? (result != 0) || (1 / result == 1 / expected) : (result != result)
				&& (expected != expected));
		check(ok);
		if (!ok)
			// If Double.toString() is buggy, this debug statement may
			// accidentally show the same string for two different doubles!
			debug("got " + result + " but expected " + expected);
	}

	// These methods are like the above, but checkpoint first.
	public void check(boolean result, String name) {
		checkPoint(name);
		check(result);
	}

	public void check(Object result, Object expected, String name) {
		checkPoint(name);
		check(result, expected);
	}

	public void check(boolean result, boolean expected, String name) {
		checkPoint(name);
		check(result, expected);
	}

	public void check(int result, int expected, String name) {
		checkPoint(name);
		check(result, expected);
	}

	public void check(long result, long expected, String name) {
		checkPoint(name);
		check(result, expected);
	}

	public void check(double result, double expected, String name) {
		checkPoint(name);
		check(result, expected);
	}

	/**
	 * A convenience method that sets a checkpoint with the specified name
	 * then records a failed check.
	 *
	 * @param name  the checkpoint name.
	 */
	public void fail(String name) {
		checkPoint(name);
		check(false);
	}

	/**
	 * Records a check point.  This can be used to mark a known place in a 
	 * testlet.  It is useful if you have a large number of tests -- it makes 
	 * it easier to find a failing test in the source code. 
	 *
	 * @param name  the check point name.
	 */
	public void checkPoint(String name) {
		last_check = name;
		count = 0;
	}

	/**
	 * This will print a message when in verbose mode.
	 *
	 * @param message  the message.
	 */
	public void verbose(String message) {
		System.out.println(message);
	}

	/**
	 * Writes a message to the debug log.
	 *
	 * @param message  the message.
	 */
	public void debug(String message) {
		debug(message, true);
	}

	/**
	 * Writes a message to the debug log with or without a newline.
	 *
	 * @param message  the message.
	 * @param newline  a flag to control whether or not a newline is added.
	 */
	public void debug(String message, boolean newline) {
		if (newline)
			System.out.println(message);
		else
			System.out.print(message);
	}

	/**
	 * Writes a stack trace for the specified exception to a log.
	 *
	 * @param ex  the exception.
	 */
	public void debug(Throwable ex) {
		ex.printStackTrace();
	}

	/**
	 * Writes the contents of an array to the log.
	 *
	 * @param o  the array of objects.
	 * @param desc  the description.
	 */
	public void debug(Object[] o, String desc) {
		debug("Dumping Object Array: " + desc);
		if (o == null) {
			debug("null");
			return;
		}

		for (int i = 0; i < o.length; i++) {
			if (o[i] instanceof Object[])
				debug((Object[]) o[i], desc + " element " + i);
			else
				debug("  Element " + i + ": " + o[i]);
		}
	}

}

