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

public class ByteTest implements Testlet {
    protected static TestHarness harness;

    public void test_Basics() {
        harness.check(!(Byte.MIN_VALUE != -128), "Byte: Basics - 1");
        harness.check(!(Byte.MAX_VALUE != 127), "Byte: Basics - 2");

        Byte ch = new Byte((byte) 'b');
        harness.check(!(ch.byteValue() != (byte) 'b'), "Byte: Basics - 3");
    }

    public void test_toString() {
        Byte ch = new Byte((byte) 'a');
        String str = ch.toString();
        harness.check(!(str.length() != 2 || !str.equals("97")), "Byte.toString");
    }

    public void test_equals() {
        Byte ch1 = new Byte((byte) '+');
        Byte ch2 = new Byte((byte) '+');
        Byte ch3 = new Byte((byte) '-');

        harness.check(!(!ch1.equals(ch2) || ch1.equals(ch3) || ch1.equals(null)), "Byte.equals - 1");
    }

    public void test_hashCode() {
        Byte ch1 = new Byte((byte) 'a');

        harness.check(!(ch1.hashCode() != (int) 'a'), "Byte.hashCode");
    }


    public void test_values() {
        Byte b = new Byte((byte) 100);
        Byte b1 = new Byte((byte) -123);
        harness.check(!(b.byteValue() != 100), "Byte: values - 11");
        harness.check(!(b1.byteValue() != -123), "Byte: values - 12");
    }

    public void testall() {
        test_Basics();
        test_equals();
        test_toString();
        test_hashCode();
        test_values();
    }

    public void test(TestHarness the_harness) {
        harness = the_harness;
        testall();
    }

}
