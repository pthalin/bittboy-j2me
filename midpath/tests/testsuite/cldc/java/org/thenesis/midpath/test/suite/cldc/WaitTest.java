/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 *
 * Copyright (C) 2001 Eric Blake
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


public final class WaitTest implements Testlet {
  
    private TestHarness harness;

    /**
     * Tests the trivial exceptions possible with wait, but
     * it does NOT test for InterruptedException, which is only possible
     * with threaded programming.
     */
    public void testArgs() {
     // wait must have reasonable args
        try {
            wait(-1);
            harness.fail("Object.wait: bad arg not detected");
        } catch (IllegalArgumentException iae) {
            harness.check(true, "Object.wait: bad arg detected");
        } catch (IllegalMonitorStateException imse) {
            harness.fail("Object.wait: bad arg not detected");
        } catch (InterruptedException ie) {
            harness.fail("Object.wait: bad arg not detected");
        }
        try {
            wait(0, -1);
            harness.fail("Object.wait: bad arg not detected");
        } catch (IllegalArgumentException iae) {
            harness.check(true, "Object.wait: bad arg detected");
        } catch (IllegalMonitorStateException imse) {
            harness.fail("Object.wait: bad arg not detected");
        } catch (InterruptedException ie) {
            harness.fail("Object.wait: bad arg not detected");
        }
        try {
            wait(0, 1000000);
            harness.fail("Object.wait: bad arg not detected");
        } catch (IllegalArgumentException iae) {
            harness.check(true, "Object.wait: bad arg detected");
        } catch (IllegalMonitorStateException imse) {
            harness.fail("Object.wait: bad arg not detected");
        } catch (InterruptedException ie) {
            harness.fail("Object.wait: bad arg not detected");
        }

        // wait and notify must be called in synchronized code
        try {
            wait();
            harness.fail("Object.wait: wait called outside synchronized block");
        } catch (IllegalMonitorStateException imse) {
            harness.check(true, "Object.wait: wait called outside synchronized block");
        } catch (InterruptedException ie) {
            harness.fail("Object.wait: wait called outside synchronized block");
        }
        try {
            wait(1);
            harness.fail("Object.wait: wait called outside synchronized block");
        } catch (IllegalMonitorStateException imse) {
            harness.check(true, "Object.wait: wait called outside synchronized block");
        } catch (InterruptedException ie) {
            harness.fail("Object.wait: wait called outside synchronized block");
        }
        try {
            wait(1, 0);
            harness.fail("Object.wait: wait called outside synchronized block");
        } catch (IllegalMonitorStateException imse) {
            harness.check(true, "Object.wait: wait called outside synchronized block");
        } catch (InterruptedException ie) {
            harness.fail("Object.wait: wait called outside synchronized block");
        }
        try {
            notify();
            harness.fail("Object.wait: notify called outside synchronized block");
        } catch (IllegalMonitorStateException imse) {
            harness.check(true, "Object.wait: notify called outside synchronized block");
        }
        try {
            notifyAll();
            harness.fail("Object.wait: notifyAll called outside synchronized block");
        } catch (IllegalMonitorStateException imse) {
            harness.check(true, "Object.wait: notifyAll called outside synchronized block");
        }
        
    }
    
    public void testBehavior() {
        long waitTime = 500;
        long startTime = System.currentTimeMillis();
        
        harness.checkPoint("Object.wait");
        try {
            synchronized(this) {
                wait(waitTime);
            }
        } catch (InterruptedException e) {
            harness.fail("Object.wait: Unexpected InterruptedException while waiting");
        }
        
        long delta = System.currentTimeMillis() - startTime;
        harness.check((delta >= 500) && (delta < 1000));
    }
    
	public void test(TestHarness harness) {
		this.harness = harness;
		testArgs();
		testBehavior();
	}
}
