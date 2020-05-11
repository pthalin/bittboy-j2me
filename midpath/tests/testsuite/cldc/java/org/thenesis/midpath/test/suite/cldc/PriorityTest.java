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

public class PriorityTest implements Testlet, Runnable {
	private static TestHarness harness;

	private void test_set_prio(Thread t, String test_name) {
		int prio = t.getPriority();
		harness.check(prio >= Thread.MIN_PRIORITY, test_name + " has at least MIN_PRIORITY");
		harness.check(prio <= Thread.MAX_PRIORITY, test_name + " has at at most MAX_PRIORITY");

		boolean illegal_exception = false;
		try {
			t.setPriority(Thread.MIN_PRIORITY - 1);
		} catch (IllegalArgumentException iae) {
			illegal_exception = true;
		}
		harness.check(illegal_exception, test_name + " cannot set prio to less then MIN_PRIORITY");
		harness.check(t.getPriority() == Thread.NORM_PRIORITY, test_name
				+ " prio doesn't change when set to illegal min");

		illegal_exception = false;
		try {
			t.setPriority(Thread.MAX_PRIORITY + 1);
		} catch (IllegalArgumentException iae) {
			illegal_exception = true;
		}
		harness.check(illegal_exception, test_name + " cannot set prio to more then MAX_PRIORITY");
		harness.check(t.getPriority() == Thread.NORM_PRIORITY, test_name
				+ " prio doesn't change when set to illegal max");
	}

	public void test(TestHarness h) {
		harness = h;

		harness.check(10, Thread.MAX_PRIORITY);
		harness.check(1, Thread.MIN_PRIORITY);
		harness.check(5, Thread.NORM_PRIORITY);

		Thread current = Thread.currentThread();
		test_set_prio(current, "Thread priority: Every Thread");

		int prio = current.getPriority();
		Thread t = new Thread(p);
		harness.check(t.getPriority() == prio, "Thread priority: New Thread inherits priority");
		test_set_prio(t, "Thread priority: New Thread");

		prio = t.getPriority();
		t.start();
		harness.check(t.getPriority() == prio, "Thread priority: Started Thread does not change priority");
		test_set_prio(t, "Thread priority: Started Thread");

		synchronized (p) {
			p.please_stop = true;
			p.notify();
		}

		try {
			t.join();
		} catch (InterruptedException ie) {
		}
		harness.check(t.getPriority() == prio, "Thread priority: Stopped Thread does not change priority");

		// What is the expected behavior of setPriority on a stopped Thread?
	}

	static PriorityTest p = new PriorityTest();
	boolean please_stop = false;

	public void run() {
		synchronized (p) {
			while (!please_stop)
				try {
					p.wait();
				} catch (InterruptedException ie) {
				}
		}
	}
}
