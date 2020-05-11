/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 1998, 2003 Red Hat, Inc.
 * Copyright (C) 2004 Mark Wielaard (mark@klomp.org)
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

public class ArrayCopyTest implements Testlet {
	public void fill(int[] a) {
		for (int i = 0; i < a.length; ++i)
			a[i] = i;
	}

	public void check(TestHarness harness, int[] expect, int[] result) {
		boolean ok = expect.length == result.length;
		for (int i = 0; ok && i < expect.length; ++i)
			if (expect[i] != result[i])
				ok = false;
		harness.check(ok);
	}

	public Object copy(Object from, int a, Object to, int b, int c) {
		try {
			System.arraycopy(from, a, to, b, c);
		} catch (ArrayStoreException xa) {
			return "caught ArrayStoreException";
		} catch (IndexOutOfBoundsException xb) {
			return "caught IndexOutOfBoundsException";
		} catch (NullPointerException xc) {
			return "caught NullPointerException";
		} catch (Throwable xd) {
			return "caught unexpected exception";
		}

		return null;
	}

	public void test(TestHarness harness) {
		int[] x, y;

		x = new int[5];
		y = new int[5];
		fill(x);

		harness.checkPoint("System.arraycopy: Copying integer array");
		harness.check(copy(x, 0, y, 0, x.length), null);
		int[] one = { 0, 1, 2, 3, 4 };
		check(harness, y, one);

		harness.check(copy(x, 1, y, 0, x.length - 1), null);
		harness.check(copy(x, 0, y, x.length - 1, 1), null);
		int[] two = { 1, 2, 3, 4, 0 };
		check(harness, y, two);

		harness.checkPoint("System.arraycopy: Incompatible arrays");

		Object[] z = new Object[5];
		harness.check(copy(x, 0, z, 0, x.length), "caught ArrayStoreException");

		harness.checkPoint("System.arraycopy: negative length");

		harness.check(copy(x, 0, y, 0, -23), "caught IndexOutOfBoundsException");

		harness.checkPoint("System.arraycopy: null arrays");

		harness.check(copy(null, 0, y, 0, -23), "caught NullPointerException");

		harness.check(copy(x, 0, null, 0, -23), "caught NullPointerException");

		harness.checkPoint("System.arraycopy: Non arrays");

		String q = "metonymy";
		harness.check(copy(q, 0, y, 0, 19), "caught ArrayStoreException");

		harness.check(copy(x, 0, q, 0, 19), "caught ArrayStoreException");

		harness.checkPoint("System.arraycopy: Incompatible arrays 2");

		double[] v = new double[5];
		harness.check(copy(x, 0, v, 0, 5), "caught ArrayStoreException");

		harness.checkPoint("System.arraycopy: Bad offset");

		harness.check(copy(x, -1, y, 0, 1), "caught IndexOutOfBoundsException");

		harness.checkPoint("System.arraycopy: Incompatible arrays 3");

		harness.check(copy(x, 0, z, 0, x.length), "caught ArrayStoreException");

		harness.checkPoint("System.arraycopy: Bad offset 2");

		harness.check(copy(x, 0, y, -1, 1), "caught IndexOutOfBoundsException");

		harness.check(copy(x, 3, y, 0, 5), "caught IndexOutOfBoundsException");

		harness.check(copy(x, 0, y, 3, 5), "caught IndexOutOfBoundsException");

		// Regression test for missing check in libgcj.
		harness.check(copy(x, 4, y, 4, Integer.MAX_VALUE), "caught IndexOutOfBoundsException");

		harness.checkPoint("System.arraycopy: Object casting");

		Object[] w = new Object[5];
		String[] ss = new String[5];
		for (int i = 0; i < 5; ++i) {
			w[i] = i + "";
			ss[i] = (i + 23) + "";
		}
		w[3] = new Integer(23);

		harness.check(copy(w, 0, ss, 0, 5), "caught ArrayStoreException");
		harness.check(ss[0], "0");
		harness.check(ss[1], "1");
		harness.check(ss[2], "2");
		harness.check(ss[3], "26");
		harness.check(ss[4], "27");

		harness.checkPoint("System.arraycopy: Different dimensions");
		harness.check(copy(new Object[1][1], 0, new Object[1], 0, 1), null);
		harness.check(copy(new int[1][1], 0, new Object[1], 0, 1), null);
		Object[] objs = new Object[1];
		objs[0] = new int[1];
		harness.check(copy(objs, 0, new int[1][1], 0, 1), null);
		harness.check(copy(new String[1][1], 0, new Object[1], 0, 1), null);
		harness.check(copy(new int[1][1], 0, new int[1], 0, 1), "caught ArrayStoreException");
		harness.check(copy(new int[1], 0, new int[1][1], 0, 1), "caught ArrayStoreException");
	}
}
