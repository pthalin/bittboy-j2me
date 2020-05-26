/*
 * MIDPath - Copyright (C) 2006-2007 Guillaume Legris, Mathieu Legris
 * 
 * GNU Classpath - Copyright (C) 1998, 1999, 2001, 2005  Free Software Foundation, Inc.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.thenesis.microbackend.ui.image.png;

public class Util {
	
	/**
	   * Constants for scaling and comparing doubles by powers of 2. The compiler
	   * must automatically inline constructs like (1/TWO_54), so we don't list
	   * negative powers of two here.
	   */
	  private static final double
	    TWO_31 = 0x80000000L, // Long bits 0x41e0000000000000L.
	    TWO_54 = 0x40000000000000L, // Long bits 0x4350000000000000L.
	    TWO_64 = 1.8446744073709552e19; // Long bits 0x43f0000000000000L.

	  /**
	   * Natural log and square root constants, for calculation of
	   * {@link #exp(double)}, {@link #log(double)} and
	   * {@link #pow(double, double)}. CP is 2/(3*ln(2)).
	   */
	  private static final double
	    SQRT_1_5 = 1.224744871391589, // Long bits 0x3ff3988e1409212eL.
	    SQRT_3 = 1.7320508075688772, // Long bits 0x3ffbb67ae8584caaL.
	    CP = 0.9617966939259756, // Long bits 0x3feec709dc3a03fdL.
	    CP_H = 0.9617967009544373, // Long bits 0x3feec709e0000000L.
	    CP_L = -7.028461650952758e-9, // Long bits 0xbe3e2fe0145b01f5L.
	    LN2 = 0.6931471805599453, // Long bits 0x3fe62e42fefa39efL.
	    LN2_H = 0.6931471803691238, // Long bits 0x3fe62e42fee00000L.
	    LN2_L = 1.9082149292705877e-10, // Long bits 0x3dea39ef35793c76L.
	    INV_LN2 = 1.4426950408889634, // Long bits 0x3ff71547652b82feL.
	    INV_LN2_H = 1.4426950216293335, // Long bits 0x3ff7154760000000L.
	    INV_LN2_L = 1.9259629911266175e-8; // Long bits 0x3e54ae0bf85ddf44L.

	  /**
	   * Constants for computing {@link #pow(double, double)}. L and P are
	   * coefficients for series; OVT is -(1024-log2(ovfl+.5ulp)); and DP is ???.
	   * The P coefficients also calculate {@link #exp(double)}.
	   */
	  private static final double
	    L1 = 0.5999999999999946, // Long bits 0x3fe3333333333303L.
	    L2 = 0.4285714285785502, // Long bits 0x3fdb6db6db6fabffL.
	    L3 = 0.33333332981837743, // Long bits 0x3fd55555518f264dL.
	    L4 = 0.272728123808534, // Long bits 0x3fd17460a91d4101L.
	    L5 = 0.23066074577556175, // Long bits 0x3fcd864a93c9db65L.
	    L6 = 0.20697501780033842, // Long bits 0x3fca7e284a454eefL.
	    P1 = 0.16666666666666602, // Long bits 0x3fc555555555553eL.
	    P2 = -2.7777777777015593e-3, // Long bits 0xbf66c16c16bebd93L.
	    P3 = 6.613756321437934e-5, // Long bits 0x3f11566aaf25de2cL.
	    P4 = -1.6533902205465252e-6, // Long bits 0xbebbbd41c5d26bf1L.
	    P5 = 4.1381367970572385e-8, // Long bits 0x3e66376972bea4d0L.
	    DP_H = 0.5849624872207642, // Long bits 0x3fe2b80340000000L.
	    DP_L = 1.350039202129749e-8, // Long bits 0x3e4cfdeb43cfd006L.
	    OVT = 8.008566259537294e-17; // Long bits 0x3c971547652b82feL.

	
	/**
	   * Raise a number to a power. Special cases:<ul>
	   * <li>If the second argument is positive or negative zero, then the result
	   * is 1.0.</li>
	   * <li>If the second argument is 1.0, then the result is the same as the
	   * first argument.</li>
	   * <li>If the second argument is NaN, then the result is NaN.</li>
	   * <li>If the first argument is NaN and the second argument is nonzero,
	   * then the result is NaN.</li>
	   * <li>If the absolute value of the first argument is greater than 1 and
	   * the second argument is positive infinity, or the absolute value of the
	   * first argument is less than 1 and the second argument is negative
	   * infinity, then the result is positive infinity.</li>
	   * <li>If the absolute value of the first argument is greater than 1 and
	   * the second argument is negative infinity, or the absolute value of the
	   * first argument is less than 1 and the second argument is positive
	   * infinity, then the result is positive zero.</li>
	   * <li>If the absolute value of the first argument equals 1 and the second
	   * argument is infinite, then the result is NaN.</li>
	   * <li>If the first argument is positive zero and the second argument is
	   * greater than zero, or the first argument is positive infinity and the
	   * second argument is less than zero, then the result is positive zero.</li>
	   * <li>If the first argument is positive zero and the second argument is
	   * less than zero, or the first argument is positive infinity and the
	   * second argument is greater than zero, then the result is positive
	   * infinity.</li>
	   * <li>If the first argument is negative zero and the second argument is
	   * greater than zero but not a finite odd integer, or the first argument is
	   * negative infinity and the second argument is less than zero but not a
	   * finite odd integer, then the result is positive zero.</li>
	   * <li>If the first argument is negative zero and the second argument is a
	   * positive finite odd integer, or the first argument is negative infinity
	   * and the second argument is a negative finite odd integer, then the result
	   * is negative zero.</li>
	   * <li>If the first argument is negative zero and the second argument is
	   * less than zero but not a finite odd integer, or the first argument is
	   * negative infinity and the second argument is greater than zero but not a
	   * finite odd integer, then the result is positive infinity.</li>
	   * <li>If the first argument is negative zero and the second argument is a
	   * negative finite odd integer, or the first argument is negative infinity
	   * and the second argument is a positive finite odd integer, then the result
	   * is negative infinity.</li>
	   * <li>If the first argument is less than zero and the second argument is a
	   * finite even integer, then the result is equal to the result of raising
	   * the absolute value of the first argument to the power of the second
	   * argument.</li>
	   * <li>If the first argument is less than zero and the second argument is a
	   * finite odd integer, then the result is equal to the negative of the
	   * result of raising the absolute value of the first argument to the power
	   * of the second argument.</li>
	   * <li>If the first argument is finite and less than zero and the second
	   * argument is finite and not an integer, then the result is NaN.</li>
	   * <li>If both arguments are integers, then the result is exactly equal to
	   * the mathematical result of raising the first argument to the power of
	   * the second argument if that result can in fact be represented exactly as
	   * a double value.</li>
	   *
	   * </ul><p>(In the foregoing descriptions, a floating-point value is
	   * considered to be an integer if and only if it is a fixed point of the
	   * method {@link #ceil(double)} or, equivalently, a fixed point of the
	   * method {@link #floor(double)}. A value is a fixed point of a one-argument
	   * method if and only if the result of applying the method to the value is
	   * equal to the value.)
	   *
	   * @param x the number to raise
	   * @param y the power to raise it to
	   * @return x<sup>y</sup>
	   */
	  public static double pow(double x, double y)
	  {
	    // Special cases first.
	    if (y == 0)
	      return 1;
	    if (y == 1)
	      return x;
	    if (y == -1)
	      return 1 / x;
	    if (x != x || y != y)
	      return Double.NaN;

	    // When x < 0, yisint tells if y is not an integer (0), even(1),
	    // or odd (2).
	    int yisint = 0;
	    if (x < 0 && Math.floor(y) == y)
	      yisint = (y % 2 == 0) ? 2 : 1;
	    double ax = Math.abs(x);
	    double ay = Math.abs(y);

	    // More special cases, of y.
	    if (ay == Double.POSITIVE_INFINITY)
	      {
	        if (ax == 1)
	          return Double.NaN;
	        if (ax > 1)
	          return y > 0 ? y : 0;
	        return y < 0 ? -y : 0;
	      }
	    if (y == 2)
	      return x * x;
	    if (y == 0.5)
	      return Math.sqrt(x);

	    // More special cases, of x.
	    if (x == 0 || ax == Double.POSITIVE_INFINITY || ax == 1)
	      {
	        if (y < 0)
	          ax = 1 / ax;
	        if (x < 0)
	          {
	            if (x == -1 && yisint == 0)
	              ax = Double.NaN;
	            else if (yisint == 1)
	              ax = -ax;
	          }
	        return ax;
	      }
	    if (x < 0 && yisint == 0)
	      return Double.NaN;

	    // Now we can start!
	    double t;
	    double t1;
	    double t2;
	    double u;
	    double v;
	    double w;
	    if (ay > TWO_31)
	      {
	        if (ay > TWO_64) // Automatic over/underflow.
	          return ((ax < 1) ? y < 0 : y > 0) ? Double.POSITIVE_INFINITY : 0;
	        // Over/underflow if x is not close to one.
	        if (ax < 0.9999995231628418)
	          return y < 0 ? Double.POSITIVE_INFINITY : 0;
	        if (ax >= 1.0000009536743164)
	          return y > 0 ? Double.POSITIVE_INFINITY : 0;
	        // Now |1-x| is <= 2**-20, sufficient to compute
	        // log(x) by x-x^2/2+x^3/3-x^4/4.
	        t = x - 1;
	        w = t * t * (0.5 - t * (1 / 3.0 - t * 0.25));
	        u = INV_LN2_H * t;
	        v = t * INV_LN2_L - w * INV_LN2;
	        t1 = (float) (u + v);
	        t2 = v - (t1 - u);
	      }
	    else
	    {
	      long bits = Double.doubleToLongBits(ax);
	      int exp = (int) (bits >> 52);
	      if (exp == 0) // Subnormal x.
	        {
	          ax *= TWO_54;
	          bits = Double.doubleToLongBits(ax);
	          exp = (int) (bits >> 52) - 54;
	        }
	      exp -= 1023; // Unbias exponent.
	      ax = Double.longBitsToDouble((bits & 0x000fffffffffffffL)
	                                   | 0x3ff0000000000000L);
	      boolean k;
	      if (ax < SQRT_1_5)  // |x|<sqrt(3/2).
	        k = false;
	      else if (ax < SQRT_3) // |x|<sqrt(3).
	        k = true;
	      else
	        {
	          k = false;
	          ax *= 0.5;
	          exp++;
	        }

	      // Compute s = s_h+s_l = (x-1)/(x+1) or (x-1.5)/(x+1.5).
	      u = ax - (k ? 1.5 : 1);
	      v = 1 / (ax + (k ? 1.5 : 1));
	      double s = u * v;
	      double s_h = (float) s;
	      double t_h = (float) (ax + (k ? 1.5 : 1));
	      double t_l = ax - (t_h - (k ? 1.5 : 1));
	      double s_l = v * ((u - s_h * t_h) - s_h * t_l);
	      // Compute log(ax).
	      double s2 = s * s;
	      double r = s_l * (s_h + s) + s2 * s2
	        * (L1 + s2 * (L2 + s2 * (L3 + s2 * (L4 + s2 * (L5 + s2 * L6)))));
	      s2 = s_h * s_h;
	      t_h = (float) (3.0 + s2 + r);
	      t_l = r - (t_h - 3.0 - s2);
	      // u+v = s*(1+...).
	      u = s_h * t_h;
	      v = s_l * t_h + t_l * s;
	      // 2/(3log2)*(s+...).
	      double p_h = (float) (u + v);
	      double p_l = v - (p_h - u);
	      double z_h = CP_H * p_h;
	      double z_l = CP_L * p_h + p_l * CP + (k ? DP_L : 0);
	      // log2(ax) = (s+..)*2/(3*log2) = exp + dp_h + z_h + z_l.
	      t = exp;
	      t1 = (float) (z_h + z_l + (k ? DP_H : 0) + t);
	      t2 = z_l - (t1 - t - (k ? DP_H : 0) - z_h);
	    }

	    // Split up y into y1+y2 and compute (y1+y2)*(t1+t2).
	    boolean negative = x < 0 && yisint == 1;
	    double y1 = (float) y;
	    double p_l = (y - y1) * t1 + y * t2;
	    double p_h = y1 * t1;
	    double z = p_l + p_h;
	    if (z >= 1024) // Detect overflow.
	      {
	        if (z > 1024 || p_l + OVT > z - p_h)
	          return negative ? Double.NEGATIVE_INFINITY
	            : Double.POSITIVE_INFINITY;
	      }
	    else if (z <= -1075) // Detect underflow.
	      {
	        if (z < -1075 || p_l <= z - p_h)
	          return negative ? -0.0 : 0;
	      }

	    // Compute 2**(p_h+p_l).
	    //int n = round((float) z);
	    int n = (int)Math.floor((float)z + 0.5d);
	    p_h -= n;
	    t = (float) (p_l + p_h);
	    u = t * LN2_H;
	    v = (p_l - (t - p_h)) * LN2 + t * LN2_L;
	    z = u + v;
	    w = v - (z - u);
	    t = z * z;
	    t1 = z - t * (P1 + t * (P2 + t * (P3 + t * (P4 + t * P5))));
	    double r = (z * t1) / (t1 - 2) - (w + z * w);
	    z = scale(1 - (r - z), n);
	    return negative ? -z : z;
	  }
	  
	  /**
	   * Helper method for scaling a double by a power of 2.
	   *
	   * @param x the double
	   * @param n the scale; |n| < 2048
	   * @return x * 2**n
	   */
	  private static double scale(double x, int n)
	  {
	    //if (Configuration.DEBUG && abs(n) >= 2048)
	    //  throw new InternalError("Assertion failure");
	    if (x == 0 || x == Double.NEGATIVE_INFINITY
	        || ! (x < Double.POSITIVE_INFINITY) || n == 0)
	      return x;
	    long bits = Double.doubleToLongBits(x);
	    int exp = (int) (bits >> 52) & 0x7ff;
	    if (exp == 0) // Subnormal x.
	      {
	        x *= TWO_54;
	        exp = ((int) (Double.doubleToLongBits(x) >> 52) & 0x7ff) - 54;
	      }
	    exp += n;
	    if (exp > 0x7fe) // Overflow.
	      return Double.POSITIVE_INFINITY * x;
	    if (exp > 0) // Normal.
	      return Double.longBitsToDouble((bits & 0x800fffffffffffffL)
	                                     | ((long) exp << 52));
	    if (exp <= -54)
	      return 0 * x; // Underflow.
	    exp += 54; // Subnormal result.
	    x = Double.longBitsToDouble((bits & 0x800fffffffffffffL)
	                                | ((long) exp << 52));
	    return x * (1 / TWO_54);
	  }


}
