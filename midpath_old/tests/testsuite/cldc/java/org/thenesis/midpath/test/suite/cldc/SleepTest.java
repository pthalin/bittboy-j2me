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

public class SleepTest implements Testlet, Runnable {
    private TestHarness harness;

    private Thread thread;
    private boolean helper_started;
    private boolean helper_done;

    private final static long SLEEP_TIME = 5 * 1000; // 5 seconds

    // Time the helper thread should sleep before interrupting the main
    // thread Or zero for immediate interruption (won't use
    // synchronization either in that case).
    private long helper_sleep = 0;

    // Helper method that runs from another thread.
    // Sleeps a bit and then interrupts the main thread.
    public void run() {
        try {
            if (helper_sleep == 0) {
                thread.interrupt();
                helper_done = true;
                return;
            }

            // Make sure main thread know we are about to sleep.
            // (It should also go to sleep)
            synchronized (this) {
                helper_started = true;
                this.notify();
            }

            Thread.sleep(helper_sleep);
            thread.interrupt();

            // Main thread should still have the lock on this
            synchronized (this) {
                helper_done = true;
            }
        } catch (InterruptedException ie) {
            harness.debug("Interrupted in helper thread");
            harness.check(false);
        }
    }

    public void test(TestHarness h) {
        harness = h;
        Thread helper = new Thread(this);

        harness.checkPoint("Thread.sleep: Interrupted sleep");

        // Get a lock on this to coordinate with the runner thread.
        // We should not loose it while sleeping.
        synchronized (this) {
            helper_done = false;
            helper_sleep = SLEEP_TIME / 2;
            thread = Thread.currentThread();
            long past = System.currentTimeMillis();

            helper.start();

            // Wait for the helper to start (and sleep immediately).
            try {
                while (!helper_started)
                    this.wait();
            } catch (InterruptedException ie) {
                harness.debug("Interrupted during helper start");
                harness.check(false);
            }

            // Go to sleep.
            // Helper thread sleeps less time and should interrupt us.
            boolean interrupted_exception = false;
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ie) {
                interrupted_exception = true;
            }
            harness.check(interrupted_exception);

            // About half the time should have been spent sleeping.
            long present = System.currentTimeMillis();
            long diff = present - past;
            harness.debug("diff: " + diff + " (tolerance: 50ms)");
            harness.check((diff + 50) >= SLEEP_TIME / 2);
            harness.check(diff < SLEEP_TIME);

            // We are still holding the lock so the helper_thread
            // cannot be done yet.
            harness.check(!helper_done);
        }

        // Now wait for the helper thead to finish
        try {
            helper.join();
        } catch (InterruptedException ie) {
            harness.debug("Interruped during joining the helper thread");
            harness.check(false);
        }
        harness.check(helper_done);

        // Invalid argument checks.
        harness.checkPoint("Thread.sleep: Invalid argument");
        invalid(Long.MIN_VALUE);
        invalid(-1);

        // (Large) valid argument checks
        valid(Integer.MAX_VALUE);
        valid(Long.MAX_VALUE);

        // A thread in interrupted state that goes to sleep gets
        // InterruptedException.
        harness.checkPoint("Thread.sleep: Interrupted state sleep");
        long past = System.currentTimeMillis();
        interruptedSleep(0);
        interruptedSleep(1);
        interruptedSleep(5000);

        // The thread should not actually have slept (much) since it was always
        // immediately waken up by the InterrupedException.
        long present = System.currentTimeMillis();
        harness.check(present - past < 5000);
    }

    private void invalid(long milli) {
        boolean illegal_argument = false;
        try {
            Thread.sleep(milli);
        } catch (IllegalArgumentException iae) {
            illegal_argument = true;
        } catch (InterruptedException ie) {
            harness.debug("InterruptedException in invalid(" + milli + ")");
            harness.check(false);
        }
        harness.check(illegal_argument);
    }

    private void valid(long milli) {
        harness.checkPoint("Thread.sleep: valid long " + milli);
        Thread helper = new Thread(this);
        helper_started = false;
        helper_done = false;
        helper_sleep = 1000;
        thread = Thread.currentThread();

        // Wait for the helper to start (and sleep immediately).
        helper.start();
        synchronized (this) {
            try {
                while (!helper_started)
                    this.wait();
            } catch (InterruptedException ie) {
                harness.debug("Interrupted during helper start");
                harness.check(false);
            }
        }

        boolean interrupted_exception = false;
        try {
            Thread.sleep(milli);
        } catch (InterruptedException ie) {
            interrupted_exception = true;
        } catch (Exception x) {
            harness.debug(x);
            try {
                // wait for the interrupt from the helper
                Thread.sleep(1000);
            } catch (InterruptedException _) {
            }
        }
        harness.check(interrupted_exception);

        try {
            helper.join();
        } catch (InterruptedException ie) {
            harness.debug("Interrupted during joining the helper thread");
            harness.check(false);
        }
        harness.check(helper_done);
    }

    private void nearZero(long milli) {
        try {
            Thread.sleep(milli);
            harness.check(true);
        } catch (InterruptedException ie) {
            harness.debug("InterruptedException in nearZero(" + milli + ")");
            harness.check(false);
        }
    }

    private void interruptedSleep(long milli) {
        boolean interrupted_exception = false;
        Thread.currentThread().interrupt();
        try {
            Thread.sleep(milli);
        } catch (InterruptedException ie) {
            interrupted_exception = true;
        }
        harness.check(interrupted_exception);
    }

}
