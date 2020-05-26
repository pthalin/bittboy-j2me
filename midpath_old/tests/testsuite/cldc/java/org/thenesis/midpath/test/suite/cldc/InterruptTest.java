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

public class InterruptTest implements Testlet {

	public void test(TestHarness harness) {
		Thread current = Thread.currentThread();

		// Set interrupt flag again for wait test
		current.interrupt();
		boolean interrupted_exception = false;
		try {
			Object o = new Object();
			synchronized (o) {
				o.wait(50);
			}
		} catch (InterruptedException ie) {
			interrupted_exception = true;
		}
		harness.check(interrupted_exception, "Thread.interrupt: wait with interrupt flag throws InterruptedException");

		// Set interrupt flag again for sleep test
		current.interrupt();
		interrupted_exception = false;
		try {
			Thread.sleep(50);
		} catch (InterruptedException ie) {
			interrupted_exception = true;
		}
		harness.check(interrupted_exception, "Thread.interrupt: sleep with interrupt flag throws InterruptedException");


	}
}
