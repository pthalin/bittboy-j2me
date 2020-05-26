/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 1999, 2002 Hewlett-Packard Company
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

public class DoubleTest implements Testlet {

	protected static TestHarness harness;

	public void test_Basics() {
		double min1 = 5e-324;
		double min2 = Double.MIN_VALUE;
		double max1 = 1.7976931348623157e+308;
		double max2 = Double.MAX_VALUE;
		double ninf1 = -1.0 / 0.0;
		double ninf2 = Double.NEGATIVE_INFINITY;
		double pinf1 = 1.0 / 0.0;
		double pinf2 = Double.POSITIVE_INFINITY;
		Double nan1 = new Double(0.0 / 0.0);
		Double nan2 = new Double(Double.NaN);

		if (min1 != min2) {
			harness.fail("Double: test_Basics - 1a");
			System.out.println("Double: Expected: " + min1);
			System.out.println("Double: Got: " + min2);
		}
		if (max1 != max2) {
			harness.fail("Double: test_Basics - 1b");
			System.out.println("Double: Expected: " + max1);
			System.out.println("Double: Got: " + max2);
		}
		if (ninf1 != ninf2) {
			harness.fail("Double: test_Basics - 1c");
			System.out.println("Double: Expected: " + ninf1);
			System.out.println("Double: Got: " + ninf2);
		}
		if (pinf1 != pinf2) {
			harness.fail("Double: test_Basics - 1d");
			System.out.println("Double: Expected: " + pinf1);
			System.out.println("Double: Got: " + pinf2);
		}
		if (!nan2.equals(nan1)) {
			harness.fail("Double: test_Basics CYGNUS: NaN.equals - 1e");
			System.out.println("Double: Expected: " + nan1);
			System.out.println("Double: Got: " + nan2);
		}

		Double i1 = new Double(100.5);

		harness.check(!(i1.doubleValue() != 100.5), "Double: test_Basics - 2");

		harness.check(!((new Double(3.4)).doubleValue() != 3.4), "Double: test_Basics - 6");

		Double nan = new Double(Double.NaN);
		harness.check(!(!nan.isNaN()), "Double: test_Basics - 7");

		harness.check(!((new Double(10.0f)).isNaN()), "Double: test_Basics - 8");

		harness.check(!(!Double.isNaN(Double.NaN)), "Double: test_Basics - 9");

		harness.check(!(!(new Double(Double.POSITIVE_INFINITY)).isInfinite()), "Double: test_Basics - 10");

		harness.check(!(!(new Double(Double.NEGATIVE_INFINITY)).isInfinite()), "Double: test_Basics - 11");
		harness.check(!(!(Double.isInfinite(Double.NEGATIVE_INFINITY))), "Double: test_Basics - 12");
		harness.check(!(!(Double.isInfinite(Double.POSITIVE_INFINITY))), "Double: test_Basics - 13");
		harness.check(!(0.0 - 0.0 != 0.0), "Double: test_Basics - 14");
		harness.check(!(0.0 + 0.0 != 0.0), "Double: test_Basics - 15");
		harness.check(!(0.0 + -0.0 != 0.0), "Double: test_Basics - 16");
		harness.check(!(0.0 - -0.0 != 0.0), "Double: test_Basics - 17");
		harness.check(!(-0.0 - 0.0 != -0.0), "Double: test_Basics - 18");
		harness.check(!(-0.0 + 0.0 != 0.0), "Double: test_Basics - 19");
		harness.check(!(-0.0 + -0.0 != -0.0), "Double: test_Basics - 20");
		harness.check(!(-0.0 - -0.0 != 0.0), "Double: test_Basics - 21");

		harness.check(!(!"0.0".equals(0.0 - 0.0 + "")), "Double: test_Basics - 22");

	}

	public void test_toString() {
		harness.check(!(!(new Double(123.0)).toString().equals("123.0")), "Double.toString: - 1");
		harness.check(!(!(new Double(-44.5343)).toString().equals("-44.5343")), "Double.toString - 2");

		harness.check(!(!Double.toString(23.04).equals("23.04")), "Double.toString: - 3");

		harness.check(!(!Double.toString(Double.NaN).equals("NaN")), "Double.toString: - 4");

		harness.check(!(!Double.toString(Double.POSITIVE_INFINITY).equals("Infinity")), "Double.toString: - 5");
		harness.check(!(!Double.toString(Double.NEGATIVE_INFINITY).equals("-Infinity")), "Double.toString: - 6");

		harness.check(!(!Double.toString(0.0).equals("0.0")), "Double.toString: - 7");

		String str;

		str = Double.toString(-0.0);
		harness.check(!(!str.equals("-0.0")), "Double.toString: - 8");

		str = Double.toString(-9412128.34);
		harness.check(!(!str.equals("-9412128.34")), "Double.toString: - 9");

		// The following case fails for some Sun JDKs (e.g. 1.3.1
		// and 1.4.0) where toString(0.001) returns "0.0010".  This
		// is contrary to the JDK 1.4 javadoc.  This particular
		// case has been noted as a comment to Sun Java bug #4642835
		//		str = Double.toString(0.001);
		//		if (!Double.toString(0.001).equals("0.001")) {
		//			harness.fail("Double.toString: - 10");
		//			System.out.println("Expected: " + "0.001");
		//			System.out.println("Got: " + Double.toString(0.001));
		//		}

		str = Double.toString(1e4d);
		if (!Double.toString(1e4d).equals("10000.0")) {
			harness.fail("Double.toString: - 11");
			System.out.println("Expected: " + "10000.0");
			System.out.println("Got: " + Double.toString(1e4d));
		}

		/*
		 str = Double.toString(-23.43E33);
		 if ( !(new Double( str)).equals(new Double(-23.43E33)))
		 harness.fail("test_toString - 16" );
		 */

	}

	public void test_equals() {
		Double i1 = new Double(2334.34E4);
		Double i2 = new Double(-2334.34E4);

		harness.check(!(!i1.equals(new Double(2334.34E4))), "Double.equals: - 1");
		harness.check(!(!i2.equals(new Double(-2334.34E4))), "Double.equals: - 2");

		harness.check(!(i1.equals(i2)), "Double.equals: - 3");

		harness.check(!(i1.equals(null)), "Double.equals: - 4");

		double n1 = Double.NaN;
		double n2 = Double.NaN;
		harness.check(!(n1 == n2), "Double.equals: - 5");

		Double flt1 = new Double(Double.NaN);
		Double flt2 = new Double(Double.NaN);
		harness.check(!(!flt1.equals(flt2)), "Double.equals: CYGNUS: NaN.equals - 6");

		harness.check(!(0.0 != -0.0), "Double.equals: - 7");

		Double pzero = new Double(0.0);
		Double nzero = new Double(-0.0);

		harness.check(!(pzero.equals(nzero)), "Double.equals: CYGNUS: Double.equals - 8");

	}

	public void test_hashCode() {
		Double flt1 = new Double(3.4028235e+38);
		long lng1 = Double.doubleToLongBits(3.4028235e+38);

		harness.check(!(flt1.hashCode() != (int) (lng1 ^ (lng1 >>> 32))), "Double.hashCode: - 1");

		Double flt2 = new Double(-2343323354.0);
		long lng2 = Double.doubleToLongBits(-2343323354.0);

		harness.check(!(flt2.hashCode() != (int) (lng2 ^ (lng2 >>> 32))), "Double.hashCode: - 2");
	}

	public void test_intValue() {
		Double b1 = new Double(3.4e+32);
		Double b2 = new Double(-23.45);

		int i1 = b1.intValue();
		int i2 = b2.intValue();

		harness.check(!(i1 != (int) 3.4e+32), "Double.intValue: CYGNUS: Float to int conversions - 1");

		harness.check(!(i2 != (int) -23.45), "Double.intValue: - 2");
		Double b3 = new Double(3000.54);
		harness.check(!(b3.intValue() != 3000), "Double.intValue: - 3");
		Double b4 = new Double(32735.3249);
		harness.check(!(b4.intValue() != 32735), "Double.intValue: - 4");
		Double b5 = new Double(-32735.3249);
		harness.check(!(b5.intValue() != -32735), "Double.intValue: - 5");
		Double b6 = new Double(-32735.3249);
		harness.check(!(b6.intValue() != -32735), "Double.intValue: - 6");
		Double b7 = new Double(0.0);
		harness.check(!(b7.intValue() != 0), "Double.intValue: - 7");
	}

	public void test_longValue() {
		Double b1 = new Double(3.4e+32);
		Double b2 = new Double(-23.45);

		harness.check(!(b1.longValue() != (long) 3.4e+32), "Double.longValue: CYGNUS: Float to int conversions - 1");

		harness.check(!(b2.longValue() != (long) -23.45), "Double.longValue: - 2");
	}

	public void test_doubleValue() {
		Double b1 = new Double(0.0);
		Double b2 = new Double(30.0);

		harness.check(!(b1.doubleValue() != 0.0), "Double.doubleValue: - 1");

		harness.check(!(b2.doubleValue() != 30.0), "Double.doubleValue: - 2");
	}

	public void test_floatValue() {
		Double b1 = new Double(0.0);
		Double b2 = new Double(30.0);

		harness.check(!(b1.floatValue() != 0.0f), "Double.floatValue: - 1");

		harness.check(!(b2.floatValue() != 30.0f), "Double.floatValue: - 2");
	}

	public void test_valueOf() {
		try {
			Double.valueOf(null);
			harness.fail("Double.valueOf - 1");
		} catch (NumberFormatException nfe) {
			harness.check(false, "Double.valueOf: null should throw NullPointerException");
		} catch (NullPointerException e) {
			harness.check(true, "Double.valueOf: null");
		}

		try {
			Double.valueOf("Kona");
			harness.fail("Double.valueOf: - 2");
		} catch (NumberFormatException e) {
		}

		harness.check(!(Double.valueOf("3.4e+32").doubleValue() != 3.4e+32), "Double.valueOf: - 3");

		harness.check(!(Double.valueOf(" -23.45    ").doubleValue() != -23.45), "Double.valueOf: - 4");
	}

	public void test_parseDouble() {
		try {
			Double.parseDouble(null);
			harness.fail("Double.parseDouble: - 1");
		} catch (NumberFormatException nfe) {
			harness.check(false, "Double.parseDouble: null should throw NullPointerException");
		} catch (NullPointerException e) {
			harness.check(true, "Double.parseDouble: null");
		}

		try {
			Double.parseDouble("Kona");
			harness.fail("Double.parseDouble: - 2");
		} catch (NumberFormatException e) {
		}

		harness.check(!(Double.parseDouble("3.4e+32") != 3.4e+32), "Double.parseDouble: - 3");

		harness.check(!(Double.parseDouble(" -23.45    ") != -23.45), "Double.parseDouble: - 4");
	}

	public void test_doubleToLongBits() {
		harness.check(!(Double.doubleToLongBits(Double.POSITIVE_INFINITY) != 0x7ff0000000000000L),
				"Double.doubleToLongBits: - 1");
		harness.check(!(Double.doubleToLongBits(Double.NEGATIVE_INFINITY) != 0xfff0000000000000L),
				"Double.doubleToLongBits:- 2");

		long nanval = Double.doubleToLongBits(Double.NaN);
		harness.check(!(nanval != 0x7ff8000000000000L), "Double.doubleToLongBits: CYGNUS: NaN.doubleToLongBits");

		long i1 = Double.doubleToLongBits(3.4e+32f);
		long i2 = Double.doubleToLongBits(-34.56f);

		long sign1 = i1 & 0x8000000000000000L;
		long sign2 = i2 & 0x8000000000000000L;

		long exp1 = i1 & 0x7ff0000000000000L;
		long exp2 = i2 & 0x7ff0000000000000L;

		long man1 = i1 & 0x000fffffffffffffL;
		long man2 = i2 & 0x000fffffffffffffL;

		harness.check(!(sign1 != 0), "Double.doubleToLongBits: - 4");

		harness.check(!(sign2 != 0x8000000000000000L), "Double.doubleToLongBits: - 5");

		harness.check(!(exp1 != 5093571178556030976L), "Double.doubleToLongBits: - 6");

		harness.check(!(exp2 != 4629700416936869888L), "Double.doubleToLongBits: - 7");

		harness.check(!(man1 != 214848222789632L), "Double.doubleToLongBits: - 8");

		harness.check(!(man2 != 360288163463168L), "Double.doubleToLongBits: - 9");

	}

	public void test_longBitsToDouble() {
		harness.check(!(Double.longBitsToDouble(0x7ff0000000000000L) != Double.POSITIVE_INFINITY),
				"Double.longBitsToDouble: - 1");
		harness.check(!(Double.longBitsToDouble(0xfff0000000000000L) != Double.NEGATIVE_INFINITY),
				"Double.longBitsToDouble: - 2");

		harness.check(!(!Double.isNaN(Double.longBitsToDouble(0xfff8000000000000L))), "Double.longBitsToDouble: - 3");

		harness.check(!(!Double.isNaN(Double.longBitsToDouble(0x7ffffff000000000L))), "Double.longBitsToDouble: - 4");

		harness.check(!(!Double.isNaN(Double.longBitsToDouble(0xfff8000020000001L))), "Double.longBitsToDouble: - 5");

		harness.check(!(!Double.isNaN(Double.longBitsToDouble(0xfffffffffffffff1L))), "Double.longBitsToDouble: - 6");

		double fl1 = Double.longBitsToDouble(0x34343f33);

		if (Double.doubleToLongBits(fl1) != 0x34343f33) {
			harness.fail("Double.longBitsToDouble: - 7");
			System.out.println("Expected: " + 0x34343f33);
			System.out.println("Got: " + Double.doubleToLongBits(fl1));
		}

		harness.check(!(Double.doubleToLongBits(Double.longBitsToDouble(0x33439943)) != 0x33439943),
				"Double.longBitsToDouble: - 8");
	}

	public void check_remainder(double val, double val1, double ret, int errno) {
		double res = val % val1;
		harness.check(!(res < ret - 0.001 || res > ret + 0.001), "Double: remainder " + errno);
	}

	public void check_remainder_NaN(double val, double val1, int errno) {
		double res = val % val1;
		if (!Double.isNaN(res)) {
			harness.fail("Double: remainder " + errno);
		}
	}

	public void test_remainder() {
		check_remainder(15.2, 1.0, 0.2, 1);
		check_remainder(2345.2432, 1.2, 0.44319999999997, 2);
		check_remainder(20.56, 1.87, 1.8600000000000, 3);
		check_remainder(0.0, 1.2, 0.00000000000000, 4);
		check_remainder(1000, 10, 0.00000000000000, 5);
		check_remainder(234.332, 134.34, 99.992000000000, 6);
		check_remainder(1.0, 1.0, 0.0, 7);
		check_remainder(45.0, 5.0, 0.0, 8);
		check_remainder(1.25, 0.50, 0.25, 9);
		check_remainder(12345.678, 1234.5678, 1234.5678000000, 10);


		check_remainder(0.0, 999.99, 0.00000000000000, 12);
		check_remainder(123.0, 25.0, 23.0, 13);
		check_remainder(15.0, 1.5, 0.00, 14);
		check_remainder_NaN(Double.NaN, 1.5, 15);
		check_remainder_NaN(1.5, Double.NaN, 16);
		check_remainder_NaN(Double.NaN, 0, 17);
		check_remainder_NaN(0, Double.NaN, 18);
		check_remainder_NaN(Double.POSITIVE_INFINITY, 1.5, 19);
		check_remainder_NaN(Double.NEGATIVE_INFINITY, 1.5, 20);
		check_remainder_NaN(1.5, 0, 21);
		check_remainder_NaN(Double.POSITIVE_INFINITY, 0, 22);
		check_remainder_NaN(Double.NEGATIVE_INFINITY, 0, 23);
		// EJWcr00505
		check_remainder(15.0, Double.POSITIVE_INFINITY, 15.0, 24);
		check_remainder(-15.0, Double.POSITIVE_INFINITY, -15.0, 25);
		check_remainder(0.0, Double.POSITIVE_INFINITY, 0.0, 26);
		check_remainder(-0.0, Double.POSITIVE_INFINITY, -0.0, 27);
		check_remainder(0.1, Double.POSITIVE_INFINITY, 0.1, 28);
		check_remainder(-0.1, Double.POSITIVE_INFINITY, -0.1, 29);

		check_remainder(15.0, Double.NEGATIVE_INFINITY, 15.0, 30);
		check_remainder(-15.0, Double.NEGATIVE_INFINITY, -15.0, 31);
		check_remainder(0.0, Double.NEGATIVE_INFINITY, 0.0, 32);
		check_remainder(-0.0, Double.NEGATIVE_INFINITY, -0.0, 33);
		check_remainder(0.1, Double.NEGATIVE_INFINITY, 0.1, 34);
		check_remainder(-0.1, Double.NEGATIVE_INFINITY, -0.1, 35);

	}

	public void test_shortbyteValue() {
		Double d1 = new Double(123.35);
		Double d2 = new Double(400.35);
		Double d3 = new Double(0.0);

		harness.check(!(d1.shortValue() != 123), "Double.shortValue: - 1");
		harness.check(!(d2.shortValue() != 400), "Double.shortValue: - 2");
		harness.check(!(d3.shortValue() != 0), "Double.shortValue: - 3");

		harness.check(!(d1.byteValue() != 123), "Double.shortValue: - 4");
		harness.check(!(d2.byteValue() != (byte) 400), "Double.shortValue: - 5");
		harness.check(!(d3.byteValue() != 0), "Double.shortValue: - 6");

	}

	public void test_neg() {
		double zero = 0.0;
		String zero1 = String.valueOf(zero);
		if (!zero1.equals("0.0")) {
			harness.fail("test_neg - 1");
		}

		zero = -zero;
		String zero2 = String.valueOf(zero);
		if (!zero2.equals("-0.0")) {
			harness.fail("test_neg - 2");
			System.out.println("Expected -0.0, got: " + zero2);
		}

		zero = -zero;
		String zero3 = String.valueOf(zero);
		if (!zero3.equals("0.0")) {
			harness.fail("test_neg - 3");
		}

		double nonzero = -21.23;
		String nonzero1 = String.valueOf(nonzero);
		if (!nonzero1.equals("-21.23")) {
			harness.fail("test_neg - 4");
		}

		nonzero = -nonzero;
		String nonzero2 = String.valueOf(nonzero);
		if (!nonzero2.equals("21.23")) {
			harness.fail("test_neg - 5");
		}

		nonzero = -nonzero;
		String nonzero3 = String.valueOf(nonzero);
		if (!nonzero3.equals("-21.23")) {
			harness.fail("test_neg - 6");
		}
	}

	/**
	 * Tests some regular values.
	 * 
	 * @param harness  the test harness.
	 */
	public void testRegular() {
		harness.checkPoint("Double.parseDouble: regular");
		harness.check(Double.parseDouble("1.0"), 1.0);
		harness.check(Double.parseDouble("+1.0"), 1.0);
		harness.check(Double.parseDouble("-1.0"), -1.0);
		harness.check(Double.parseDouble(" 1.0 "), 1.0);
		harness.check(Double.parseDouble(" -1.0 "), -1.0);

		harness.check(Double.parseDouble("2."), 2.0);
		harness.check(Double.parseDouble(".3"), 0.3);
		harness.check(Double.parseDouble("1e-9"), 1e-9);
		harness.check(Double.parseDouble("1e137"), 1e137);

		// test some bad formats
		try {
			/* double d = */Double.parseDouble("");
			harness.check(false);
		} catch (NumberFormatException e) {
			harness.check(true);
		}

		try {
			/* double d = */Double.parseDouble("X");
			harness.check(false);
		} catch (NumberFormatException e) {
			harness.check(true);
		}

		try {
			/* double d = */Double.parseDouble("e");
			harness.check(false);
		} catch (NumberFormatException e) {
			harness.check(true);
		}

		try {
			/* double d = */Double.parseDouble("+ 1.0");
			harness.check(false);
		} catch (NumberFormatException e) {
			harness.check(true);
		}

		try {
			/* double d = */Double.parseDouble("- 1.0");
			harness.check(false);
		} catch (NumberFormatException e) {
			harness.check(true);
		}

		// null argument should throw NullPointerException
		try {
			/* double d = */Double.parseDouble(null);
			harness.check(false);
		} catch (NullPointerException e) {
			harness.check(true);
		}
	}

	/**
	 * Some checks for values that should parse to Double.POSITIVE_INFINITY
	 * or Double.NEGATIVE_INFINITY.
	 * 
	 * @param harness  the test harness.
	 */
	public void testInfinities() {
		harness.checkPoint("Double.parseDouble: Infinities");
		try {
			harness.check(Double.parseDouble("Infinity"), Double.POSITIVE_INFINITY);
			harness.check(Double.parseDouble("+Infinity"), Double.POSITIVE_INFINITY);
			harness.check(Double.parseDouble("-Infinity"), Double.NEGATIVE_INFINITY);
			harness.check(Double.parseDouble(" +Infinity "), Double.POSITIVE_INFINITY);
			harness.check(Double.parseDouble(" -Infinity "), Double.NEGATIVE_INFINITY);
		} catch (Exception e) {
			harness.check(false);
			harness.debug(e);
		}
		harness.check(Double.parseDouble("1e1000"), Double.POSITIVE_INFINITY);
		harness.check(Double.parseDouble("-1e1000"), Double.NEGATIVE_INFINITY);
	}

	/**
	 * Some checks for 'NaN' values.
	 * 
	 * @param harness  the test harness.
	 */
	public void testNaN() {
		harness.checkPoint("Double.parseDouble: NaN");
		try {
			harness.check(Double.isNaN(Double.parseDouble("NaN")));
			harness.check(Double.isNaN(Double.parseDouble("+NaN")));
			harness.check(Double.isNaN(Double.parseDouble("-NaN")));
			harness.check(Double.isNaN(Double.parseDouble(" +NaN ")));
			harness.check(Double.isNaN(Double.parseDouble(" -NaN ")));
		} catch (Exception e) {
			harness.check(false);
			harness.debug(e);
		}
	}

	public void testall() {
		test_Basics();
		test_remainder();
		test_toString();
		test_equals();
		test_hashCode();
		test_intValue();
		test_longValue();
		test_doubleValue();
		test_floatValue();
		test_shortbyteValue();
		test_valueOf();
		test_parseDouble();
		test_doubleToLongBits();
		test_longBitsToDouble();
		test_neg();

		// Test parsing
		testRegular();
		testInfinities();
		testNaN();
		
		

	}

	public void test(TestHarness the_harness) {
		harness = the_harness;
		testall();
	}

}
