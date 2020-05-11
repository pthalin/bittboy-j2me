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

public class IsAliveTest extends Thread implements Testlet {

	boolean started = false;
	boolean please_stop = false;

	public void run() {
		synchronized (this) {
			started = true;
			notify();
			while (!please_stop)
				try {
					this.wait();
				} catch (InterruptedException ignore) {
				}
		}
	}

	public void test(TestHarness harness) {
		Thread current = Thread.currentThread();

		boolean alive = current.isAlive();
		harness.check(alive, "Thread.isAlive: current running thread is always alive");

		IsAliveTest t = new IsAliveTest();
		harness.check(!t.isAlive(), "Thread.isAlive: newly created threads are not alive");

		t.start();
		synchronized (t) {
			while (!t.started)
				try {
					t.wait();
				} catch (InterruptedException ignore) {
				}

			harness.check(t.isAlive(), "Thread.isAlive: running threads are alive");

			t.please_stop = true;
			t.notify();
		}
		try {
			t.join();
		} catch (InterruptedException ignore) {
		}

		harness.check(!t.isAlive(), "Thread.isAlive: stopped threads are not alive");
	}
}
