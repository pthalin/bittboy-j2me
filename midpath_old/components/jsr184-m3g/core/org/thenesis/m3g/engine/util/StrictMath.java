/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * GNU Classpath - Copyright (C) 1998, 2001, 2002, 2003, 2006 
 * Free Software Foundation, Inc.
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
 * 02110-1301 USA   */

/*
 * Some of the algorithms in this class are in the public domain, as part
 * of fdlibm (freely-distributable math library), available at
 * http://www.netlib.org/fdlibm/, and carry the following copyright:
 * ====================================================
 * Copyright (C) 1993 by Sun Microsystems, Inc. All rights reserved.
 *
 * Developed at SunSoft, a Sun Microsystems, Inc. business.
 * Permission to use, copy, modify, and distribute this
 * software is freely granted, provided that this notice
 * is preserved.
 * ====================================================
 */

package org.thenesis.m3g.engine.util;


/**
 * Helper class containing useful mathematical functions and constants.
 * This class mirrors {@link Math}, but is 100% portable, because it uses
 * no native methods whatsoever.  Also, these algorithms are all accurate
 * to less than 1 ulp, and execute in <code>strictfp</code> mode, while
 * Math is allowed to vary in its results for some functions. Unfortunately,
 * this usually means StrictMath has less efficiency and speed, as Math can
 * use native methods.
 *
 * <p>The source of the various algorithms used is the fdlibm library, at:<br>
 * <a href="http://www.netlib.org/fdlibm/">http://www.netlib.org/fdlibm/</a>
 *
 * Note that angles are specified in radians.  Conversion functions are
 * provided for your convenience.
 *
 * @author Eric Blake (ebb9@email.byu.edu)
 * @since 1.3
 */
public final strictfp class StrictMath
{
  /**
   * StrictMath is non-instantiable.
   */
  private StrictMath()
  {
  }

  /**
   * The most accurate approximation to the mathematical constant <em>pi</em>:
   * <code>3.141592653589793</code>. This is the ratio of a circle's diameter
   * to its circumference.
   */
  public static final double PI
    = 3.141592653589793; // Long bits 0x400921fb54442d18L.

  /**
   * The trigonometric function <em>arccos</em>. The range of angles returned
   * is 0 to pi radians (0 to 180 degrees). If the argument is NaN or
   * its absolute value is beyond 1, the result is NaN.
   *
   * @param x the cos to turn back into an angle
   * @return arccos(x)
   */
  public static double acos(double x)
  {
    boolean negative = x < 0;
    if (negative)
      x = -x;
    if (! (x <= 1))
      return Double.NaN;
    if (x == 1)
      return negative ? PI : 0;
    if (x < 0.5)
      {
        if (x < 1 / TWO_57)
          return PI / 2;
        double z = x * x;
        double p = z * (PS0 + z * (PS1 + z * (PS2 + z * (PS3 + z
                                                         * (PS4 + z * PS5)))));
        double q = 1 + z * (QS1 + z * (QS2 + z * (QS3 + z * QS4)));
        double r = x - (PI_L / 2 - x * (p / q));
        return negative ? PI / 2 + r : PI / 2 - r;
      }
    if (negative) // x<=-0.5.
      {
        double z = (1 + x) * 0.5;
        double p = z * (PS0 + z * (PS1 + z * (PS2 + z * (PS3 + z
                                                         * (PS4 + z * PS5)))));
        double q = 1 + z * (QS1 + z * (QS2 + z * (QS3 + z * QS4)));
        double s = sqrt(z);
        double w = p / q * s - PI_L / 2;
        return PI - 2 * (s + w);
      }
    double z = (1 - x) * 0.5; // x>0.5.
    double s = sqrt(z);
    double df = (float) s;
    double c = (z - df * df) / (s + df);
    double p = z * (PS0 + z * (PS1 + z * (PS2 + z * (PS3 + z
                                                     * (PS4 + z * PS5)))));
    double q = 1 + z * (QS1 + z * (QS2 + z * (QS3 + z * QS4)));
    double w = p / q * s + c;
    return 2 * (df + w);
  }

  /**
   * Take a square root. If the argument is NaN or negative, the result is
   * NaN; if the argument is positive infinity, the result is positive
   * infinity; and if the result is either zero, the result is the same.
   *
   * <p>For other roots, use pow(x, 1/rootNumber).
   *
   * @param x the numeric argument
   * @return the square root of the argument
   * @see #pow(double, double)
   */
  public static double sqrt(double x)
  {
    if (x < 0)
      return Double.NaN;
    if (x == 0 || ! (x < Double.POSITIVE_INFINITY))
      return x;

    // Normalize x.
    long bits = Double.doubleToLongBits(x);
    int exp = (int) (bits >> 52);
    if (exp == 0) // Subnormal x.
      {
        x *= TWO_54;
        bits = Double.doubleToLongBits(x);
        exp = (int) (bits >> 52) - 54;
      }
    exp -= 1023; // Unbias exponent.
    bits = (bits & 0x000fffffffffffffL) | 0x0010000000000000L;
    if ((exp & 1) == 1) // Odd exp, double x to make it even.
      bits <<= 1;
    exp >>= 1;

    // Generate sqrt(x) bit by bit.
    bits <<= 1;
    long q = 0;
    long s = 0;
    long r = 0x0020000000000000L; // Move r right to left.
    while (r != 0)
      {
        long t = s + r;
        if (t <= bits)
          {
            s = t + r;
            bits -= t;
            q += r;
          }
        bits <<= 1;
        r >>= 1;
      }

    // Use floating add to round correctly.
    if (bits != 0)
      q += q & 1;
    return Double.longBitsToDouble((q >> 1) + ((exp + 1022L) << 52));
  }

  
  /**
   * Constants for scaling and comparing doubles by powers of 2. The compiler
   * must automatically inline constructs like (1/TWO_54), so we don't list
   * negative powers of two here.
   */
  private static final double
    TWO_54 = 0x40000000000000L, // Long bits 0x4350000000000000L.
    TWO_57 = 0x200000000000000L; // Long bits 0x4380000000000000L.

  /**
   * More constants related to pi, used in
   * {@link #remPiOver2(double, double[])} and elsewhere.
   */
  private static final double
    PI_L = 1.2246467991473532e-16; // Long bits 0x3ca1a62633145c07L.

  /**
   * Coefficients for computing {@link #asin(double)} and
   * {@link #acos(double)}.
   */
  private static final double
    PS0 = 0.16666666666666666, // Long bits 0x3fc5555555555555L.
    PS1 = -0.3255658186224009, // Long bits 0xbfd4d61203eb6f7dL.
    PS2 = 0.20121253213486293, // Long bits 0x3fc9c1550e884455L.
    PS3 = -0.04005553450067941, // Long bits 0xbfa48228b5688f3bL.
    PS4 = 7.915349942898145e-4, // Long bits 0x3f49efe07501b288L.
    PS5 = 3.479331075960212e-5, // Long bits 0x3f023de10dfdf709L.
    QS1 = -2.403394911734414, // Long bits 0xc0033a271c8a2d4bL.
    QS2 = 2.0209457602335057, // Long bits 0x40002ae59c598ac8L.
    QS3 = -0.6882839716054533, // Long bits 0xbfe6066c1b8d0159L.
    QS4 = 0.07703815055590194; // Long bits 0x3fb3b8c5b12e9282L.

}
