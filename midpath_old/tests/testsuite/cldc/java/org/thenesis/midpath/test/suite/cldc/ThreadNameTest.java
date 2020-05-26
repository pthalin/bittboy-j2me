/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 2002 Free Software Foundation, Inc.
 * Written by Mark Wielaard (mark@klomp.org)
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

public class ThreadNameTest implements Testlet {

	public void test(TestHarness harness) {
		Thread current = Thread.currentThread();

		harness.check(current.getName() != null, "Thread.getName: Every Thread has a non-null name");

		Thread t = new Thread("Test-Thread");
		harness.check(t.getName().equals("Test-Thread"), "Thread.getName: Create thread with name");

		boolean null_exception = false;
		try {
			new Thread((String) null);
		} catch (NullPointerException npe) {
			null_exception = true;
		}
		harness.check(null_exception, "Thread.getName: Cannot create Thread with null name");

		t = new Thread();
		String name = t.getName();
		harness.check(name != null, "Thread.getName: New Thread has non-null name");

		harness.check(t.getName().equals(name), "Thread.getName: Setting Thread name to null doesn't change name");
	}
}
