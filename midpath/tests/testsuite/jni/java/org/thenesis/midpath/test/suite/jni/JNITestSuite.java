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
package org.thenesis.midpath.test.suite.jni;

import org.thenesis.midpath.test.suite.AbstractTestSuite;
import org.thenesis.midpath.test.suite.TestHarness;
import org.thenesis.midpath.test.suite.Testlet;

import com.sun.cldchi.jvm.JVM;

public class JNITestSuite extends AbstractTestSuite {

    public JNITestSuite() {
        super("JNITestSuite");
    }

    public void testAll() {
        new JNITests().test(this);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        JNITestSuite suite = new JNITestSuite();
        suite.testAll();
    }

}

interface TestInterface {
    public static final int STATIC_VALUE = 8;
}

class JNITestParent {
    public int callIntMethod() {
        return 27;
    }
    
    public static class InnerClass {
    }

}

class JNITests extends JNITestParent implements TestInterface, Testlet {
    
    protected static TestHarness harness;
    
    // Fields read/written from native side 
    public boolean booleanField = false;
    public byte byteField = (byte)1;
    public int intField = 1;
    public long longField = 1;
    public float floatField = (float)1.1;
    public double doubleField = 1.1;
    public Object[] objectField = new Object[7];

    public static boolean staticBooleanField = false;
    public static byte staticByteField = (byte)1;
    public static int staticIntField = 1;
    public static long staticLongField = 1;
    public static float staticFloatField = (float)1.1;
    public static double staticDoubleField = 1.1;
    public static Object[] staticObjectField = new Object[7];
    
    static {
        //System.loadLibrary("midpath-jnitest");
        JVM.loadLibrary("libmidpath-jnitest.so");
    }

    public JNITests(int value) {
        this.intField = value;
    }

    public JNITests() {
    }
    
    public void test(TestHarness the_harness) {
        harness = the_harness;
        testNativeMethodCall();
        testStringMethods();
        testMethodCallsFromNative();
        testFields();
        testFindClass();
        testNewObject();
        testArrayMethods();
        testArrayRegionMethods();
        testException();
    }
    
    /* Call native methods */
    
    public void testNativeMethodCall() {
        int value = callNativeMethod((byte)34, 14, (long)25, (float)12, 56, true);
        harness.check(value == (34 + 14 + 25 + 12 + 56 + 1), "Call native methods from Java");
    }
    
    public native int callNativeMethod(byte b, int i, long l, float f, double d, boolean bool);
    
    /* Call native String methods */
    
    public void testStringMethods() {
        String value = callStringMethods("Hello");
        harness.check(value, "Hallo", "Call string methods from native side");
        value = callStringUTFMethods("Hello");
        harness.check(value, "Hallo", "Call UTF string methods from native side");
    }
    
    public native String callStringMethods(String prompt);
    public native String callStringUTFMethods(String prompt);
    
    /* Call Java Methods from native side */
    
    public void testMethodCallsFromNative() {
        int failures = callJavaMethodFromNative();
        harness.check(failures == 0, "Call Java methods from native side");
    }
    
    public native int callJavaMethodFromNative();

    public byte callByteMethod() {
        return 23;
    }

    public char callCharMethod(int z) {
        return (char) ('a' + z);
    }

    public int callIntMethod() {
        return 1023;
    }

    public static long callLongMethod(long q) {
        return q + 2023;
    }

    public static long callLongMethod2(byte b1, long q1, byte b2, long q2, byte b3, long q3) {
        return q1 + q2 + q3 + 3023;
    }

    public void callVoidMethod() {
        harness.verbose("void method called");
    }

    public static short callShortMethod() {
        return 2;
    }

    public double callDoubleMethod() {
        return -1.0;
    }

    public float callFloatMethod() {
        return (float) 1.0;
    }
    
    /* Play with Java fields from native side */
    
    public void testFields() {
        Object object = callJavaFieldFromNative();
        harness.check(object == objectField, "Call Java field from native side");
        object = callJavaStaticFieldFromNative();
        harness.check(object == staticObjectField, "Call static Java field from native side");
    }
    
    public native Object callJavaFieldFromNative();
    public native Object callJavaStaticFieldFromNative();
    
    /* Find classes from native side */
    
    public void testFindClass() {
        Class clazz = callFindClass("java/lang/String");
        harness.check(clazz == String.class, "Find class from native side");
        clazz = callFindClass("org/thenesis/midpath/test/suite/jni/JNITestParent$InnerClass");
        harness.check(clazz == InnerClass.class, "Find inner class from native side");
    }
    
    public static native Class callFindClass(String name);
    
    /* Create object from native side */
    
    public void testNewObject() {
        String s = (String)callNewObject();
        harness.check(s, "Hello", "Create new object from native side");
    }
    
    public static native Object callNewObject();
    
    /* Throw exception from native side */
    
    public void testException() {
        try {
            boolean success = callExceptionMethod();
            harness.fail("Can't throw Exception");
        } catch (Exception e) {
            harness.check(e.getMessage(), "Oops", "Exception thrown from native side");
        }
    }
    public static native boolean callExceptionMethod() throws Exception;
    
    
    /* Read/Write Java arrays from native side */
    
    public void testArrayMethods() {
        byte[] bytes = new byte[4];
        bytes[0] = 0;
        bytes[1] = 1;
        bytes[2] = 2;
        bytes[3] = 3;
        boolean success = callArrayMethods(bytes, 4);
        harness.check(success, "Read Java arrays from native side");
        success = (bytes[0] == 3) && (bytes[1] == 2) && (bytes[2] == 1) && (bytes[3] == 0);
        harness.check(success, "Write Java arrays from native side");
    }
    
    public native boolean callArrayMethods(byte[] bytes, int arrayLength);
    
    public void testArrayRegionMethods() {
        byte[] bytes = new byte[4];
        bytes[0] = 0;
        bytes[1] = 1;
        bytes[2] = 2;
        bytes[3] = 3;
        boolean success = callArrayRegionMethods(bytes);
        harness.check(success, "Read Java array regions from native side");
        success = (bytes[0] == 3) && (bytes[1] == 2) && (bytes[2] == 1) && (bytes[3] == 0);
        harness.check(success, "Write Java arrays regions from native side");
    }
    
    public native boolean callArrayRegionMethods(byte[] bytes);


}
