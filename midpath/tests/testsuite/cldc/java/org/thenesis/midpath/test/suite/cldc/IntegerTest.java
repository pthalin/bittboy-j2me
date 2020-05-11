/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 1999, 2001 Hewlett-Packard Company
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

public class IntegerTest implements Testlet {

	protected static TestHarness harness;

	public void test_Basics() {
		harness.check(!(Integer.MIN_VALUE != 0x80000000 || Integer.MAX_VALUE != 0x7fffffff), "Integer: test_Basics - 1");
	}

	public void test_toString() {
		harness.check(!(!(new Integer(123)).toString().equals("123")), "Integer.toString: - 1");
		harness.check(!(!(new Integer(-44)).toString().equals("-44")), "Integer.toString: - 2");

		harness.check(!(!Integer.toString(234).equals("234")), "Integer.toString: - 3");
		harness.check(!(!Integer.toString(-34).equals("-34")), "Integer.toString: - 4");
		harness.check(!(!Integer.toString(-34).equals("-34")), "Integer.toString: - 5");

		harness.check(!(!Integer.toString(99, 1).equals("99")), "Integer.toString: - 6");
		harness.check(!(!Integer.toString(99, 37).equals("99")), "Integer.toString: - 7");

		harness.check(!(!Integer.toString(15, 2).equals("1111")), "Integer.toString: - 8");
		harness.check(!(!Integer.toString(37, 36).equals("11")), "Integer.toString: - 9");
		harness.check(!(!Integer.toString(31, 16).equals("1f")), "Integer.toString: - 10");

		harness.check(!(!Integer.toString(-99, 1).equals("-99")), "Integer.toString: - 11");
		harness.check(!(!Integer.toString(-99, 37).equals("-99")), "Integer.toString: - 12");

		harness.check(!(!Integer.toString(-15, 2).equals("-1111")), "Integer.toString: - 13");
		harness.check(!(!Integer.toString(-37, 36).equals("-11")), "Integer.toString: - 14");
		harness.check(!(!Integer.toString(-31, 16).equals("-1f")), "Integer.toString: - 15");
	}

	public void test_equals() {
		Integer i1 = new Integer(23);
		Integer i2 = new Integer(-23);

		harness.check(!(!i1.equals(new Integer(23))), "Integer.equals: - 1");
		harness.check(!(!i2.equals(new Integer(-23))), "Integer.equals: - 2");

		harness.check(!(i1.equals(i2)), "Integer.equals: - 3");

		harness.check(!(i1.equals(null)), "Integer.equals: - 4");
	}

	public void test_hashCode() {
		Integer b1 = new Integer(3439);
		Integer b2 = new Integer(-3439);

		harness.check(!(b1.hashCode() != 3439 || b2.hashCode() != -3439), "Integer.hashCode");
	}

	public void test_intValue() {
		Integer b1 = new Integer(32767);
		Integer b2 = new Integer(-32767);

		harness.check(!(b1.intValue() != 32767), "Integer.intValue: - 1");

		harness.check(!(b2.intValue() != -32767), "Integer.intValue: - 2");
	}

	public void test_longValue() {
		Integer b1 = new Integer(3767);
		Integer b2 = new Integer(-3767);

		harness.check(!(b1.longValue() != (long) 3767), "Integer.longValue: - 1");

		harness.check(!(b2.longValue() != -3767), "Integer.longValue: - 2");
	}

	public void test_floatValue() {
		Integer b1 = new Integer(3276);
		Integer b2 = new Integer(-3276);

		harness.check(!(b1.floatValue() != 3276.0f), "Integer.floatValue: - 1");

		harness.check(!(b2.floatValue() != -3276.0f), "Integer.floatValue: - 2");
	}

	public void test_doubleValue() {
		Integer b1 = new Integer(0);
		Integer b2 = new Integer(30);

		harness.check(!(b1.doubleValue() != 0.0), "Integer.doubleValue: - 1");

		harness.check(!(b2.doubleValue() != 30.0), "Integer.doubleValue: - 2");
	}

	public void test_shortbyteValue() {
		Integer b1 = new Integer(0);
		Integer b2 = new Integer(300);

		harness.check(!(b1.byteValue() != 0), "Integer.byteValue: - 1");

		harness.check(!(b2.byteValue() != (byte) 300), "Integer.byteValue: - 2");
		harness.check(!(b1.shortValue() != 0), "Integer.byteValue: - 3");

		harness.check(!(b2.shortValue() != (short) 300), "Integer.byteValue: - 4");
		
	}

	public void test_toHexString() {
		String str, str1;

		str = Integer.toHexString(8375);
		str1 = Integer.toHexString(-5361);

		harness.check("20b7".equals(str), "Integer.toHexString: - 1");

		harness.check("ffffeb0f".equals(str1), "Integer.toHexString: - 2");
	}

	public void test_toOctalString() {
		String str, str1;
		str = Integer.toOctalString(5847);
		str1 = Integer.toOctalString(-9863);

		harness.check(!(!str.equals("13327")), "Integer.toOctalString: - 1");

		harness.check(!(!str1.equals("37777754571")), "Integer.toOctalString: - 2");
	}

	public void test_toBinaryString() {
		harness.check(!(!Integer.toBinaryString(358).equals("101100110")), "Integer.toBinaryString: - 1");

		harness.check(!(!Integer.toBinaryString(-5478).equals("11111111111111111110101010011010")),
				"Integer.toBinaryString: - 2");
	}

	public void test_parseInt() {
		harness.check(!(Integer.parseInt("473") != Integer.parseInt("473", 10)), "Integer.parseInt: - 1");

		harness.check(!(Integer.parseInt("0", 10) != 0), "Integer.parseInt: - 2");

		harness.check(!(Integer.parseInt("473", 10) != 473), "Integer.parseInt: - 3");
		harness.check(!(Integer.parseInt("-0", 10) != 0), "Integer.parseInt: - 4");
		harness.check(!(Integer.parseInt("-FF", 16) != -255), "Integer.parseInt: - 5");
		harness.check(!(Integer.parseInt("1100110", 2) != 102), "Integer.parseInt: - 6");
		harness.check(!(Integer.parseInt("2147483647", 10) != 2147483647), "Integer.parseInt: - 7");
		harness.check(!(Integer.parseInt("-2147483647", 10) != -2147483647), "Integer.parseInt: - 8");
		try {
			Integer.parseInt("2147483648", 10);
			harness.fail("Integer.parseInt: - 9");
		} catch (NumberFormatException e) {
		}
		try {
			Integer.parseInt("99", 8);
			harness.fail("Integer.parseInt: - 10");
		} catch (NumberFormatException e) {
		}
		try {
			Integer.parseInt("kona", 10);
			harness.fail("Integer.parseInt: - 11");
		} catch (NumberFormatException e) {
		}
		harness.check(!(Integer.parseInt("Kona", 27) != 411787), "Integer.parseInt: - 12");
	}

	public void test_valueOf() {
		harness.check(!(Integer.valueOf("21234").intValue() != Integer.parseInt("21234")), "Integer.valueOf: - 1");
		harness.check(!(Integer.valueOf("Kona", 27).intValue() != Integer.parseInt("Kona", 27)),
						"Integer.valueOf: - 2");
	}

	public void testall() {
		test_Basics();
		test_toString();
		test_equals();
		test_hashCode();
		test_intValue();
		test_longValue();
		test_floatValue();
		test_doubleValue();
		test_shortbyteValue();
		test_toHexString();
		test_toOctalString();
		test_toBinaryString();
		test_parseInt();
		test_valueOf();
	}

	public void test(TestHarness the_harness) {
		harness = the_harness;
		testall();
	}

}
