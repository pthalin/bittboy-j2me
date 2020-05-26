/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 1999  Hewlett-Packard Company
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

public class BooleanTest implements Testlet {
	public void test_Basics(TestHarness harness) {
		harness.checkPoint("Boolean: Basics");

		harness.check(Boolean.TRUE.equals(new Boolean(true)) && Boolean.FALSE.equals(new Boolean(false)));

		Boolean b1 = new Boolean(true);
		Boolean b2 = new Boolean(false);

		harness.check(b1.booleanValue() == true && b2.booleanValue() == false);
	}

	public void test_equals(TestHarness harness) {
		harness.checkPoint("Boolean.equals");

		Boolean b1 = new Boolean(true);
		Boolean b2 = new Boolean(false);

		harness.check(!b1.equals(new Integer(4)));

		harness.check(!b1.equals(null));

		harness.check(!b1.equals(b2));

		harness.check(b1.equals(new Boolean(true)));
	}

	public void test_hashCode(TestHarness harness) {
		harness.checkPoint("Boolean.hashCode");

		Boolean b1 = new Boolean(true);
		Boolean b2 = new Boolean(false);

		harness.check(b1.hashCode() == 1231 && b2.hashCode() == 1237);
	}

	public void test_booleanValue(TestHarness harness) {
		harness.checkPoint("Boolean.booleanValue");

		Boolean b1 = new Boolean(true);
		Boolean b2 = new Boolean(false);

		harness.check(b1.booleanValue() == true && b2.booleanValue() == false);
	}

	public void test_valueOf(TestHarness harness) {
		harness.checkPoint("Boolean.valueOf");
	}

	public void test(TestHarness harness) {
		test_Basics(harness);
		test_equals(harness);
		test_hashCode(harness);
		test_booleanValue(harness);
		test_valueOf(harness);
	}
}
