/* 
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.thenesis.midpath.test.suite.AbstractTestSuite;
import org.thenesis.midpath.test.suite.cldc.sub.AttributeTester;

public class CLDCTestSuite extends AbstractTestSuite {

	public CLDCTestSuite() {
		super("CLDCTestSuite");
	}
	
	/* 
	 * Attributes tests
	 */
	private void testAttributeAccess() {
	    
	    AttributeTester tester = new AttributeTester();
	    checkPoint("Public attribute access");
	    check(tester.publicNumber, 32);
	    check(tester.publicMethod(), 32);
	    
	    AttributeTesterChild childTester = new AttributeTesterChild();
	    checkPoint("Protected attribute access");
	    check(childTester.checkProtectedField());
	    check(childTester.checkProtectedMethod());
        
    }
    

	/*
	 * Number tests
	 */
	public void testBoolean() {
		checkPoint("Boolean");
		new BooleanTest().test(this);
	}
	public void testByte() {
        checkPoint("Byte");
        new ByteTest().test(this);
    }
	public void testInteger() {
		checkPoint("Integer");
		new IntegerTest().test(this);
	}
	public void testLong() {
        checkPoint("Long");
        new LongTest().test(this);
    }
	public void testFloat() {
		checkPoint("Float");
		new FloatTest().test(this);
	}
	public void testDouble() {
		checkPoint("Double");
		new DoubleTest().test(this);
	}
	
	/*
	 * Class tests
	 */

	public void testForName() {

		Class c = null;

		try {
			c = Class.forName("org.thenesis.midpath.test.suite.cldc.TestClass");
		} catch (ClassNotFoundException e) {
			fail("Can't get a Class from a class name");
			debug(e);
		}

		checkPoint("Class.forName");
		check(c != null);
		//check(c.isAssignableFrom(TestClass.class));

	}

	public void testNewInstance() {

		Object instance = null;

		try {
			instance = TestClass.class.newInstance();
		} catch (Exception e) {
			fail("Can't instantiate a class");
			debug(e);
		} 

		checkPoint("Class.newInstance");
		check(instance != null);
		check(((TestClass) instance).fieldValue, 15);

	}

	public void testIsInstance() {

		Object instance = new TestClass();

		checkPoint("Class.isInstance");
		check(TestClass.class.isInstance(instance));
		check(!TestClass.class.isInstance(new Integer(1)));

	}

	public void testIsAssignable() {
		checkPoint("Class.isAssignable");
		check(TestClass.class.isAssignableFrom(TestClass.class));
		check(AttributeTester.class.isAssignableFrom(AttributeTesterChild.class));
		check(!String.class.isAssignableFrom(TestClass.class));
	}

	public void testIsInterface() {
		checkPoint("Class.isInterface");
		check(TestInterface.class.isInterface());
	}

	public void testIsArray() {

		int[] array = new int[] { 1, 2 };

		checkPoint("Class.isArray");
		check(((Object) array).getClass().isArray());
	}

	public void testGetName() {
		checkPoint("Class.getName");
		check(TestClass.class.getName(), "org.thenesis.midpath.test.suite.cldc.TestClass");
	}

	public void testGetResourceAsStream() {

		checkPoint("Class.getResourceAsStream");
		
		// Test 1
		InputStream is = CLDCTestSuite.class.getResourceAsStream("/org/thenesis/midpath/test/suite/cldc/file.txt");

		int val = -1;
		try {
			val = is.read();
		} catch (IOException e) {
			fail("Can't get resource as a stream");
			
		}
		check((char) val, '2');

		// Test 2
		is = CLDCTestSuite.class.getResourceAsStream("file.txt");

		try {
			val = is.read();
		} catch (IOException e) {
			fail("Can't get resource as a stream");
			debug(e);
		}
		check((char) val, '2');

	}
	
	
	/*
	 * Thread tests
	 */
	
	public void testInterrupt() {
		checkPoint("Thread.interrupt");
		new InterruptTest().test(this);
	}
	
	public void testIsAlive() {
		checkPoint("Thread.isAlive");
		new IsAliveTest().test(this);
	}
	
	public void testJoin() {
		checkPoint("Thread.join");
		new JoinTest().test(this);
	}
	
	public void testTheadName() {
		checkPoint("Thread.getName");
		new ThreadNameTest().test(this);
	}
	
	public void testPriority() {
		checkPoint("Thread.setPriority");
		new PriorityTest().test(this);
	}
	
	public void testSleep() {
		checkPoint("Thread.sleep");
		new SleepTest().test(this);
	}
	
	public void testActiveCount() {
	    checkPoint("Thread.activeCount");
	    check(Thread.activeCount() == 1);
	    
	    long waitTime = 500;
        TestClass thread = new TestClass(waitTime);
        thread.start();
        check(Thread.activeCount() == 2);
        try {
            Thread.sleep(waitTime + 100);
        } catch (InterruptedException e) {
        }
	}
	
	/* Object tests */
	
	public void testObject() {
		checkPoint("Object.wait");
		new ObjectTest().test(this);
	}

	public void testWait() {
		checkPoint("Object.wait");
		new WaitTest().test(this);
	}
	
	public void testNotify() {
		
		checkPoint("notify");
		
		TestClass thread = new TestClass(2000);
		
		//debug(thread.toString());

		try {
			thread.start();
			check(thread.isAlive());
			Thread.sleep(500);
			thread.release();
			Thread.sleep(500);
			check(!thread.isAlive());
		} catch (InterruptedException e) {
		}

		check(thread.fieldValue, 16);

	}
	
	/*
     * Math tests
     */

	public void testMath() {
        checkPoint("Math");
        new MathTest().test(this);
    }
	
	/*
	 * Runtime tests
	 */
	public void testFreeMemory() {
		checkPoint("Runtime.freeMemory");
		check(Runtime.getRuntime().freeMemory() > 0);
	}

	public void testTotalMemory() {
		checkPoint("Runtime.totalMemory");
		check(Runtime.getRuntime().totalMemory() > 0);
	}

	public void testGC() {
		checkPoint("Runtime.GC");
		Runtime.getRuntime().gc();
		//FIXME
		check(true);
	}

	/*
	 * String tests
	 */
	
	public void testString() {
        checkPoint("String");
        new StringTest().test(this);
    }
	
	/*
	 * System tests
	 */
	public void testCurrentTimeMillis() {
		
		long time = System.currentTimeMillis();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		long time2 = System.currentTimeMillis();
		long delta = time2 - time;
		
		checkPoint("System.currentTimeMillis");
		check((time > 0) && (time2 > 0) && (delta > 0) && (delta < 1000));
		
	}
	
	public void testArrayCopy() {
		checkPoint("System.arraycopy");
		new ArrayCopyTest().test(this);
	}
	
	public void testIdentityHashCode() {
		checkPoint("System.identityHashCode");
		new IdentityHashCodeTest().test(this);
	}
	
	
	
	public void testSocketConnection() {
		
		checkPoint("javax.microedition.io.Connection");
		
//		String hostname = "www.yahoo.com";
//		int port = 80;
		String hostname = "192.168.1.1";
		int port = 777;
		StreamConnection client;
		
		try {
			client = (StreamConnection) Connector.open("socket://" + hostname + ":" + port);
			
			OutputStream os = client.openOutputStream();
			PrintStream pos = new PrintStream(os);
			InputStream is = client.openInputStream();
			InputStreamReader reader = new InputStreamReader(is);
			
			String request = "GET http://" + hostname + "\r\n";

			//pos.print(request);
			//pos.flush();
			
			byte[] bArray = request.getBytes();
			for (int i = 0; i < bArray.length; i++) {
				os.write(bArray[i]);
			}
			
			// Block until some data are available
			while(is.available() <= 0) {
			}
			debug("available data: " + is.available());
			check(is.available() > 0);
			
			int length = 0;
			char[] buffer = new char[1000];
			StringBuffer response = new StringBuffer();
			
			// Test read(byte[] buf, int off, int len)
			while ((length = reader.read(buffer)) != -1) {
				response.append(buffer);
				System.out.print(buffer);
			}
			
//			// Test read()
//			int val = 0;
//			while ((val = reader.read()) != -1) {
//				response.append((char)val);
//				System.out.print((char)val);
//			}
			
			check(response.toString().indexOf("html") != -1);
			client.close();
		} catch (IOException e) {
			fail("Can't open socket");
			debug(e);
		} 
		
	}


	private void testObjectCreation() {
		checkPoint("Small objects creation");
		verbose("Before: free memory: " + Runtime.getRuntime().freeMemory());
		for (int i = 0; i < 100000; i++) {
			SmallObject object = new SmallObject();
			object.val = i;
		}
		verbose("After: free memory: " + Runtime.getRuntime().freeMemory());
		check(true);
		
		checkPoint("Big objects creation");
		verbose("Before: free memory: " + Runtime.getRuntime().freeMemory());
		for (int i = 0; i < 10000; i++) {
			BigObject object = new BigObject();
			object.val = i;
		}
		verbose("After: free memory: " + Runtime.getRuntime().freeMemory());
		check(true);
		
		/* Now keep the objects in the memory */ 
		
		checkPoint("Small objects creation 2");
		verbose("Before: free memory: " + Runtime.getRuntime().freeMemory());
		Vector list = new Vector(100000);
		for (int i = 0; i < 100000; i++) {
			SmallObject object = new SmallObject();
			object.val = i;
			list.addElement(object);
		}
		verbose("After: free memory: " + Runtime.getRuntime().freeMemory());
		list.removeAllElements();
		System.gc();
		verbose("After GC: free memory: " + Runtime.getRuntime().freeMemory());
		check(true);
		
		checkPoint("Big objects creation 2");
		verbose("Before: free memory: " + Runtime.getRuntime().freeMemory());
		list = new Vector(40);
		for (int i = 0; i < 40; i++) {
			BigObject object = new BigObject();
			object.val = i;
			list.addElement(object);
		}
		verbose("After: free memory: " + Runtime.getRuntime().freeMemory());
		list.removeAllElements();
		System.gc();
		verbose("After GC: free memory: " + Runtime.getRuntime().freeMemory());
		check(true);	
		
		
	}
	
	private void testArrayCreation() {
		checkPoint("Small objects creation");
		
		for (int i = 1; i < 100000; i += 100) {
			int[] array = new int[i];
			array[0] = i;
		}
		
		check(true);	
	}
	
	private void testThreadCreation() {
		
		checkPoint("Thread creation");
		
		
		Vector list = new Vector(40);
		
		for (int i = 1; i < 100; i += 100) {
			TestClass thread = new TestClass(500);
			list.addElement(thread);
			thread.start();
		}
		
		list.removeAllElements();
		System.gc();
		
		check(true);
		
	}

	
	public void testAll() {
	    
	    // Class attributes access
	    testAttributeAccess();
		
		// System
		testCurrentTimeMillis();
		testArrayCopy();
		testIdentityHashCode();
		
		// Numbers
		testBoolean();
		testByte();
		testInteger();
		testLong();
		testFloat();
		testDouble();
		
		// String
		testString();
		
		// Class
		testForName();
		testNewInstance();
		testIsInstance();
		testIsAssignable();
		testIsInterface();
		testIsArray();
		testGetName();
		testGetResourceAsStream();
		
		// Object
		testObject();
		testWait();

		// Thread
		testInterrupt();
		testIsAlive();
		testJoin();
		testNotify();
		testTheadName();
		testPriority();
		testSleep();
		testActiveCount();
		
		// Maths
		testMath();

		// Runtime
		testFreeMemory();
		testTotalMemory();
		testGC();
		
		// Socket
		//testSocketConnection();
		
		// Stress tests
//		testObjectCreation();
//		testArrayCreation();
//		testThreadCreation();
		
	}

    /**
	 * @param args
	 */
	public static void main(String[] args) {
		CLDCTestSuite suite = new CLDCTestSuite();
		suite.testAll();
	}

}

class AttributeTesterChild extends AttributeTester {
    
    public AttributeTesterChild() {
        super(1);
    }
    
    public boolean checkProtectedField() {
        return (protectedNumber == 86);
    }
    
    public boolean checkProtectedMethod() {
        return (protectedMethod() == 86);
    }
    
}

interface TestInterface {
	public static final int STATIC_VALUE = 8;
}

class SmallObject {
	
	int[] buf = new int[10];
	int val;
	
}

class BigObject {
	
	int[] buf = new int[100000];
	int val;
}



class TestClass extends Thread implements TestInterface {

	private long waitTime = 0;
	public int fieldValue = 15;

	public TestClass(long waitTime) {
		this.waitTime = waitTime;
	}

	public TestClass() {
	}

	public synchronized void release() {
		notify();
	}

	public void run() {
		
		synchronized (this) {
			try {
				//wait();
				wait(waitTime);
				fieldValue = 16;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
