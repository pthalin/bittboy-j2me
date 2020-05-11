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

public class MathTest implements Testlet {

    protected static TestHarness harness;

    public void test_Basics() {
        harness.check(!(Math.E != 2.7182818284590452354), "Math: Basics - 1");
        harness.check(!(Math.PI != 3.14159265358979323846), "Math: Basics - 2");
    }

    public void test_sincostan() {
        harness.check(!(!(new Double(Math.sin(Double.NaN))).isNaN()), "Math: sincostan - 1");
        harness.check(!(!(new Double(Math.sin(Double.POSITIVE_INFINITY))).isNaN()), "Math: sincostan - 2");
        harness.check(!(!(new Double(Math.sin(Double.NEGATIVE_INFINITY))).isNaN()), "Math: sincostan - 3");
        harness.check(!(Math.sin(-0.0) != -0.0), "Math: sincostan - 4");
        harness.check(!(Math.sin(0.0) != 0.0), "Math: sincostan - 5");

        harness.check(!(!(new Double(Math.cos(Double.NaN))).isNaN()), "Math: sincostan - 6");
        harness.check(!(!(new Double(Math.cos(Double.POSITIVE_INFINITY))).isNaN()), "Math: sincostan - 7");
        harness.check(!(!(new Double(Math.cos(Double.NEGATIVE_INFINITY))).isNaN()), "Math: sincostan - 8");

        harness.check(!(!(new Double(Math.tan(Double.NaN))).isNaN()), "Math: sincostan - 9");
        harness.check(!(!(new Double(Math.tan(Double.POSITIVE_INFINITY))).isNaN()), "Math: sincostan - 10");
        harness.check(!(!(new Double(Math.tan(Double.NEGATIVE_INFINITY))).isNaN()), "Math: sincostan - 11");
        harness.check(!(Math.tan(-0.0) != -0.0), "Math: sincostan - 12");
        harness.check(!(Math.tan(0.0) != 0.0), "Math: sincostan - 13");

        harness.check(!(Math.sin(Math.PI / 2.0 + Math.PI / 6.0) <= 0.0), "Math: sincostan - 14");
        harness.check(!(Math.cos(Math.PI / 2.0 + Math.PI / 6.0) >= 0.0), "Math: sincostan - 14");
        harness.check(!(Math.tan(Math.PI / 2.0 + Math.PI / 6.0) >= 0.0), "Math: sincostan - 14");
    }

    // public void test_asinacosatan() {
    // harness.check(!(!(new Double(Math.asin(Double.NaN))).isNaN()),
    // "test_asinacosatan - 1");
    // harness.check(!(Math.asin(-0.0) != -0.0), "test_asinacosatan - 2");
    // harness.check(!(Math.asin(0.0) != 0.0), "test_asinacosatan - 3");
    //
    // harness.check(!(!(new Double(Math.asin(10.0))).isNaN()),
    // "test_asinacosatan - 4");
    //
    // harness.check(!(!(new Double(Math.acos(Double.NaN))).isNaN()),
    // "test_asinacosatan - 5");
    // harness.check(!(!(new Double(Math.acos(10.0))).isNaN()),
    // "test_asinacosatan - 6");
    //
    // harness.check(!(!(new Double(Math.atan(Double.NaN))).isNaN()),
    // "test_asinacosatan - 7");
    // harness.check(!(Math.atan(-0.0) != -0.0), "test_asinacosatan - 8");
    // harness.check(!(Math.atan(0.0) != 0.0), "test_asinacosatan - 9");
    //
    // }

    // public void test_atan2() {
    // harness.check(!(!(new Double(Math.atan2(1.0, Double.NaN))).isNaN()),
    // "test_atan2 - 1");
    // harness.check(!(!(new Double(Math.atan2(Double.NaN, 1.0))).isNaN()),
    // "test_atan2 - 2");
    //
    // harness.check(!((Math.atan2(0.0, 10.0) != -0.0) || (Math.atan2(2.0,
    // Double.POSITIVE_INFINITY) != -0.0)), "test_atan2 - 3");
    //
    // harness.check(!((Math.atan2(-0.0, 10.0) != -0.0) || (Math.atan2(-2.0,
    // Double.POSITIVE_INFINITY) != -0.0)), "test_atan2 - 4");
    //
    // harness.check(!((Math.atan2(0.0, -10.0) != Math.PI) || (Math.atan2(2.0,
    // Double.NEGATIVE_INFINITY) != Math.PI)), "test_atan2 - 4");
    //
    // harness.check(!((Math.atan2(-0.0, -10.0) != -Math.PI) ||
    // (Math.atan2(-2.0, Double.NEGATIVE_INFINITY) != -Math.PI)),
    // "test_atan2 - 5");
    //
    // harness.check(!((Math.atan2(10.0, 0.0) != Math.PI / 2.0) ||
    // (Math.atan2(Double.POSITIVE_INFINITY, 3.0) != Math.PI / 2.0)),
    // "test_atan2 - 6");
    //
    // harness.check(!((Math.atan2(-10.0, 0.0) != -Math.PI / 2.0) ||
    // (Math.atan2(Double.NEGATIVE_INFINITY, 3.0) != -Math.PI / 2.0)),
    // "test_atan2 - 7");
    //
    // harness.check(!((Math.atan2(Double.POSITIVE_INFINITY,
    // Double.POSITIVE_INFINITY) != Math.PI / 4.0)), "test_atan2 - 8");
    //
    // harness.check(!((Math.atan2(Double.POSITIVE_INFINITY,
    // Double.NEGATIVE_INFINITY) != Math.PI * 3.0 / 4.0)), "test_atan2 - 9");
    //
    // harness.check(!((Math.atan2(Double.NEGATIVE_INFINITY,
    // Double.POSITIVE_INFINITY) != -Math.PI / 4.0)), "test_atan2 - 10");
    //
    // harness.check(!((Math.atan2(Double.NEGATIVE_INFINITY,
    // Double.NEGATIVE_INFINITY) != -Math.PI * 3.0 / 4.0)), "test_atan2 - 11");
    // }

    public void test_sqrt() {
        harness.check(!(!(new Double(Math.sqrt(Double.NaN))).isNaN() || !(new Double(Math.sqrt(-10.0))).isNaN()), "Math.sqrt: - 1");
        harness.check(!(!(new Double(Math.sqrt(Double.NaN))).isNaN() || !(new Double(Math.sqrt(-10.0))).isNaN()), "Math.sqrt: - 2");
        harness.check(!(!(new Double(Math.sqrt(Double.POSITIVE_INFINITY))).isInfinite()), "Math.sqrt: - 3");
        harness.check(!(Math.sqrt(-0.0) != -0.0 || Math.sqrt(0.0) != 0.0), "Math.sqrt: - 4");
        harness.check(!(Math.sqrt(-0.0) != -0.0 || Math.sqrt(0.0) != 0.0), "Math.sqrt: - 5");

        double sq = Math.sqrt(4.0);
        harness.check(!(!(sq >= 1.9999 && sq <= 2.111)), "Math.sqrt: - 6");
    }

    public void test_ceil() {
        harness.check(!(Math.ceil(5.0) != 5.0), "Math.ceil: - 1");
        harness.check(!(Math.ceil(0.0) != 0.0 || Math.ceil(-0.0) != -0.0), "Math.ceil: - 2");
        harness.check(!(!(new Double(Math.ceil(Double.POSITIVE_INFINITY))).isInfinite() || !(new Double(Math.ceil(Double.NaN))).isNaN()),
                "Math.ceil: - 3");
        harness.check(!(Math.ceil(-0.5) != -0.0), "Math.ceil: - 4");
        harness.check(!(Math.ceil(2.5) != 3.0), "Math.ceil: - 5");

    }

    public void test_floor() {
        harness.check(!(Math.floor(5.0) != 5.0), "Math.floor: - 1");
        harness.check(!(Math.floor(2.5) != 2.0), "Math.floor: - 2");
        harness.check(!(!(new Double(Math.floor(Double.POSITIVE_INFINITY))).isInfinite() || !(new Double(Math.floor(Double.NaN))).isNaN()),
                "Math.floor: - 3");
        harness.check(!(Math.floor(0.0) != 0.0 || Math.floor(-0.0) != -0.0), "Math.floor: - 4");

    }

    public void test_abs() {
        harness.check(!(Math.abs(10) != 10), "Math.abs: - 1");
        harness.check(!(Math.abs(-23) != 23), "Math.abs: - 2");
        harness.check(!(Math.abs(Integer.MIN_VALUE) != Integer.MIN_VALUE), "Math.abs: - 3");
        harness.check(!(Math.abs(-0) != 0), "Math.abs: - 4");
        harness.check(!(Math.abs(1000L) != 1000), "Math.abs: - 5");
        harness.check(!(Math.abs(-2334242L) != 2334242), "Math.abs: - 6");
        harness.check(!(Math.abs(Long.MIN_VALUE) != Long.MIN_VALUE), "Math.abs: - 7");
        harness.check(!(Math.abs(0.0f) != 0.0f || Math.abs(-0.0f) != 0.0f), "Math.abs: - 8");
        harness.check(!(!(new Float(Math.abs(Float.POSITIVE_INFINITY))).isInfinite()), "Math.abs: - 9");
        harness.check(!(!(new Float(Math.abs(Float.NaN))).isNaN()), "Math.abs: - 10");
        harness.check(!(Math.abs(23.34f) != 23.34f), "Math.abs: - 11");
        harness.check(!(Math.abs(0.0) != 0.0 || Math.abs(-0.0) != 0.0), "Math.abs: - 12");
        harness.check(!(!(new Double(Math.abs(Double.POSITIVE_INFINITY))).isInfinite()), "Math.abs: - 13");
        harness.check(!(!(new Double(Math.abs(Double.NaN))).isNaN()), "Math.abs: - 14");
        harness.check(!(Math.abs(23.34) != 23.34), "Math.abs: - 15");
    }

    public void test_min() {
        harness.check(!(Math.min(100, 12) != 12), "Math.min: - 1");
        harness.check(!(Math.min(Integer.MIN_VALUE, Integer.MIN_VALUE + 1) != Integer.MIN_VALUE), "Math.min: - 2");
        harness.check(!(Math.min(Integer.MAX_VALUE, Integer.MAX_VALUE - 1) != Integer.MAX_VALUE - 1), "Math.min: - 3");
        harness.check(!(Math.min(10, 10) != 10), "Math.min: - 4");
        harness.check(!(Math.min(0, -0) != -0), "Math.min: - 5");
        harness.check(!(Math.min(100L, 12L) != 12L), "Math.min: - 6");
        harness.check(!(Math.min(Long.MIN_VALUE, Long.MIN_VALUE + 1) != Long.MIN_VALUE), "Math.min: - 7");
        harness.check(!(Math.min(Long.MAX_VALUE, Long.MAX_VALUE - 1) != Long.MAX_VALUE - 1), "Math.min: - 8");
        harness.check(!(Math.min(10L, 10L) != 10L), "Math.min: - 9");
        harness.check(!(Math.min(0L, -0L) != -0L), "Math.min: - 10");
        harness.check(!(Math.min(23.4f, 12.3f) != 12.3f), "Math.min: - 11");
        harness.check(!(!(new Float(Math.min(Float.NaN, 1.0f))).isNaN()), "Math.min: - 12");
        harness.check(!(Math.min(10.0f, 10.0f) != 10.0f), "Math.min: - 13");
        harness.check(!(Math.min(0.0f, -0.0f) != -0.0f), "Math.min: - 14");
        harness.check(!(Math.min(23.4, 12.3) != 12.3), "Math.min: - 15");
        harness.check(!(!(new Double(Math.min(Double.NaN, 1.0))).isNaN()), "Math.min: - 16");
        harness.check(!(Math.min(10.0, 10.0) != 10.0), "Math.min: - 17");
        harness.check(!(Math.min(0.0, -0.0) != -0.0), "Math.min: - 18");
    }

    public void test_max() {
        harness.check(!(Math.max(100, 12) != 100), "Math.max: - 1");
        harness.check(!(Math.max(Integer.MAX_VALUE, Integer.MAX_VALUE - 1) != Integer.MAX_VALUE), "Math.max: - 2");
        harness.check(!(Math.max(Integer.MIN_VALUE, Integer.MIN_VALUE + 1) != Integer.MIN_VALUE + 1), "Math.max: - 3");
        harness.check(!(Math.max(10, 10) != 10), "Math.max: - 4");
        harness.check(!(Math.max(0, -0) != 0), "Math.max: - 5");
        harness.check(!(Math.max(100L, 12L) != 100L), "Math.max: - 6");
        harness.check(!(Math.max(Long.MAX_VALUE, Long.MAX_VALUE - 1) != Long.MAX_VALUE), "Math.max: - 7");
        harness.check(!(Math.max(Long.MIN_VALUE, Long.MIN_VALUE + 1) != Long.MIN_VALUE + 1), "Math.max: - 8");
        harness.check(!(Math.max(10L, 10L) != 10L), "Math.max: - 9");
        harness.check(!(Math.max(0L, -0L) != 0L), "Math.max: - 10");
        harness.check(!(Math.max(23.4f, 12.3f) != 23.4f), "Math.max: - 11");
        harness.check(!(!(new Float(Math.max(Float.NaN, 1.0f))).isNaN()), "Math.max: - 12");
        harness.check(!(Math.max(10.0f, 10.0f) != 10.0f), "Math.max: - 13");
        harness.check(!(Math.max(0.0f, -0.0f) != 0.0f), "Math.max: - 14");
        harness.check(!(Math.max(23.4, 12.3) != 23.4), "Math.max: - 15");
        harness.check(!(!(new Double(Math.max(Double.NaN, 1.0))).isNaN()), "Math.max: - 16");
        harness.check(!(Math.max(10.0, 10.0) != 10.0), "Math.max: - 17");
        harness.check(!(Math.max(0.0, -0.0) != 0.0), "Math.max: - 18");
    }

    public void testall() {
        test_Basics();
        test_sincostan();
        // test_asinacosatan();
        // test_atan2();
        test_sqrt();
        test_ceil();
        test_floor();
        test_abs();
        test_min();
        test_max();
    }

    public void test(TestHarness the_harness) {
        harness = the_harness;
        testall();
    }

}
