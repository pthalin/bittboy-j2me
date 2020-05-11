/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 1999 Hewlett-Packard Company
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
package org.thenesis.midpath.test.suite.cldc;

import org.thenesis.midpath.test.suite.TestHarness;
import org.thenesis.midpath.test.suite.Testlet;

public class ObjectTest implements Testlet {
	boolean finFlag = false;

	protected static TestHarness harness;

	public void test_getClass() {
		Integer i = new Integer(10);
		Class cls = i.getClass();
		if (cls == null)
			harness.fail("Object.getClass: Error: test_getClass returned null");

		ObjectTest obj = new ObjectTest();
		if (obj.getClass() != getClass())
			harness.fail("Object.getClass: Error: test_getClass returned wrong class");

	}

	public void test_toString() {
		if (toString() == null)
			harness.fail("Object.toString: Error: test_toString returned null string");
		if (!toString().equals(getClass().getName() + "@" + Integer.toHexString(hashCode())))
			harness.fail("Object.toString: Error: test_toString returned wrong string");

	}

	public void test_equals() {
		Object nu = this;

		// reflexive
		if (this != nu)
			harness.fail("Object.equals: Error: test_equals returned wrong results - 1");
		if (!this.equals(nu))
			harness.fail("Object.equals: Error: test_equals returned wrong results - 2");

		if (!nu.equals(nu))
			harness.fail("Object.equals: Error: test_equals returned wrong results - 3");

		// symmetric
		Object nu1 = nu;

		if (!(nu.equals(nu1) && nu1.equals(nu)))
			harness.fail("Object.equals: Error: test_equals returned wrong results - 4");

		// transitive
		if (!(nu.equals(nu1) && nu1.equals(this) && equals(nu)))
			harness.fail("Object.equals: Error: test_equals returned wrong results - 5");

		Object p = null;
		if (equals(p))
			harness.fail("Object.equals: Error: test_equals returned wrong results - 6");
	}

	public void test_hashCode() {
		Object s = this;
		if (s.hashCode() != hashCode())
			harness.fail("Object.hashCode: Error: test_hashCode returned wrong results - 1");

		int hash = s.hashCode();

		if (hash != s.hashCode())
			harness.fail("Object.hashCode: Error: test_hashCode returned wrong results - 2");
	}

	public void testall() {
		test_getClass();
		test_toString();
		test_equals();
		test_hashCode();
	}

	public void test(TestHarness the_harness) {
		harness = the_harness;
		testall();
	}

}
