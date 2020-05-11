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
#include <stddef.h>

#include "org_thenesis_midpath_test_suite_jni_JNITests.h"

JNIEXPORT jint JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callNativeMethod(JNIEnv *env, jclass klass, jbyte b, jint i, jlong l, jfloat f, jdouble d, jboolean bool) {
    return (b + i + l + f + d + ((bool == JNI_TRUE) ? 1 : 0));
}

JNIEXPORT jstring JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callStringMethods(JNIEnv *env, jobject obj, jstring str) {
    /* Assume the input string is "Hello" */
    jchar outbuf[5];
    int len = (*env)->GetStringLength(env, str);

    // GetStringChars
    jchar *c = (jchar*)(*env)->GetStringChars(env, str, NULL);
    if (c[0] != 'H') {
        fprintf (stderr, "[ERROR] %s[%d]: Can't retrieve string content\n", __FILE__, __LINE__);
        return JNI_FALSE;
    }
    (*env)->ReleaseStringChars(env, str, c);

    // GetStringRegion
    (*env)->GetStringRegion(env, str, 0, len, outbuf);
    outbuf[1] = 'a';

    // NewString
    return (*env)->NewString(env, outbuf, 5);
}

JNIEXPORT jstring JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callStringUTFMethods(JNIEnv *env, jobject obj, jstring str) {
    /* Assume the input string is "Hello" */
    char outbuf[5];
    int len = (*env)->GetStringUTFLength(env, str);

    // GetStringUTFChars
    const char *s = (char*)(*env)->GetStringUTFChars(env, str, NULL);
    if (s[0] != 'H') {
        fprintf (stderr, "[ERROR] %s[%d]: Can't retrieve string content\n", __FILE__, __LINE__);
        return JNI_FALSE;
    }
    (*env)->ReleaseStringUTFChars(env, str, s);

    // GetStringUTFRegion
    (*env)->GetStringUTFRegion(env, str, 0, len, outbuf);
    outbuf[1] = 'a';

    // NewStringUTF
    return (*env)->NewStringUTF(env, outbuf);
}

JNIEXPORT jint JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callJavaMethodFromNative(JNIEnv *env, jobject _this) {
    jmethodID method;
    jclass klass, super;

    jbyte b;
    jshort s;
    jchar c;
    jint i;
    jlong l;
    jfloat f;
    jdouble d;

    jvalue val;

    jint fails = 0;

    klass = (*env)->GetObjectClass (env, _this);
    super = (*env)->GetSuperclass (env, klass);

    method = (*env)->GetMethodID (env, klass, "callByteMethod", "()B");
    b = (*env)->CallByteMethod (env, _this, method);
    if (b != 23)
    ++fails;

    method = (*env)->GetMethodID (env, klass, "callCharMethod", "(I)C");
    val.i = 10;
    c = (*env)->CallCharMethodA (env, _this, method, &val);
    if (c != ('a' + 10))
    ++fails;

    method = (*env)->GetMethodID (env, super, "callIntMethod", "()I");
    i = (*env)->CallNonvirtualIntMethod (env, _this, super, method);
    if (i != 27)
    ++fails;

    i = (*env)->CallIntMethod (env, _this, method);
    if (i != 1023)
    ++fails;

    method = (*env)->GetStaticMethodID (env, klass, "callLongMethod", "(J)J");
    l = (*env)->CallStaticLongMethod (env, klass, method, (jlong) 10);
    if (l != 2033)
    ++fails;

    method = (*env)->GetStaticMethodID (env, klass, "callLongMethod2", "(BJBJBJ)J");
    l = (*env)->CallStaticLongMethod (env, klass, method, (jbyte) 13, (jlong) 3,
            (jbyte) 13, (jlong) 3, (jbyte) 13, (jlong) 4);
    if (l != 3033)
    ++fails;

    method = (*env)->GetMethodID (env, klass, "callVoidMethod", "()V");
    (*env)->CallVoidMethod (env, _this, method);

    method = (*env)->GetStaticMethodID (env, klass, "callShortMethod", "()S");
    s = (*env)->CallStaticShortMethod (env, klass, method);
    if (s != 2)
    ++fails;

    method = (*env)->GetMethodID (env, klass, "callDoubleMethod", "()D");
    d = (*env)->CallDoubleMethod (env, _this, method);
    if (d != -1.0)
    ++fails;

    method = (*env)->GetMethodID (env, klass, "callFloatMethod", "()F");
    f = (*env)->CallFloatMethod (env, _this, method);
    if (f != 1.0)
    ++fails;

    return fails;
}

JNIEXPORT jobjectArray JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callJavaFieldFromNative (JNIEnv *env, jobject this) {
    jclass cls;
    jfieldID fid;
    jobjectArray obj;

    cls = (*env)->GetObjectClass (env, this);
    if (! cls) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't get object class\n", __FILE__, __LINE__);
        return NULL;
    }

    // boolean field
    fid = (*env)->GetFieldID(env, cls, "booleanField", "Z");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read boolean field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetBooleanField(env, this, fid, JNI_TRUE);
    jboolean jbooleanValue = (*env)->GetBooleanField(env, this, fid);
    if (jbooleanValue != JNI_TRUE) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad boolean value\n", __FILE__, __LINE__);
        return NULL;
    }

    // byte field
    fid = (*env)->GetFieldID(env, cls, "byteField", "B");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read byte field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetByteField(env, this, fid, (jbyte)67);
    jbyte jbyteValue = (*env)->GetByteField(env, this, fid);
    if (jbyteValue != 67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad byte value\n", __FILE__, __LINE__);
        return NULL;
    }

    // int field
    fid = (*env)->GetFieldID(env, cls, "intField", "I");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read int field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetIntField(env, this, fid, (jint)67);
    jint jintValue = (*env)->GetIntField(env, this, fid);
    if (jintValue != 67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad int value\n", __FILE__, __LINE__);
        return NULL;
    }

    // long field
    fid = (*env)->GetFieldID(env, cls, "longField", "J");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read long field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetLongField(env, this, fid, (jlong)67);
    jlong jlongValue = (*env)->GetLongField(env, this, fid);
    if (jlongValue != 67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad long value\n", __FILE__, __LINE__);
        return NULL;
    }

    // float field
    fid = (*env)->GetFieldID(env, cls, "floatField", "F");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read float field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetFloatField(env, this, fid, (jfloat)67.67);
    jfloat jfloatValue = (*env)->GetFloatField(env, this, fid);
    if (jfloatValue != (jfloat)67.67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad float value\n", __FILE__, __LINE__);
        return NULL;
    }

    // double field
    fid = (*env)->GetFieldID(env, cls, "doubleField", "D");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read double field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetDoubleField(env, this, fid, (jdouble)67.67);
    jdouble jdoubleValue = (*env)->GetDoubleField(env, this, fid);
    if (jdoubleValue != (jdouble)67.67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad float value\n", __FILE__, __LINE__);
        return NULL;
    }

    // Object field
    fid = (*env)->GetFieldID (env, cls, "objectField", "[Ljava/lang/Object;");
    if (! fid)
    return 0;

    obj = (*env)->GetObjectField (env, this, fid);

    return obj;
}

JNIEXPORT jobjectArray JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callJavaStaticFieldFromNative (JNIEnv *env, jobject this) {
    jclass cls;
    jfieldID fid;
    jobjectArray obj;

    cls = (*env)->GetObjectClass (env, this);
    if (! cls) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't get object class\n", __FILE__, __LINE__);
        return NULL;
    }

    // boolean field
    fid = (*env)->GetStaticFieldID(env, cls, "staticBooleanField", "Z");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read static boolean field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetStaticBooleanField(env, this, fid, JNI_TRUE);
    jboolean jbooleanValue = (*env)->GetStaticBooleanField(env, this, fid);
    if (jbooleanValue != JNI_TRUE) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad static boolean value\n", __FILE__, __LINE__);
        return NULL;
    }

    // byte field
    fid = (*env)->GetStaticFieldID(env, cls, "staticByteField", "B");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read static byte field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetStaticByteField(env, this, fid, (jbyte)67);
    jbyte jbyteValue = (*env)->GetStaticByteField(env, this, fid);
    if (jbyteValue != 67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad static byte value\n", __FILE__, __LINE__);
        return NULL;
    }

    // int field
    fid = (*env)->GetStaticFieldID(env, cls, "staticIntField", "I");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read static int field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetStaticIntField(env, this, fid, (jint)67);
    jint jintValue = (*env)->GetStaticIntField(env, this, fid);
    if (jintValue != 67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad static int value\n", __FILE__, __LINE__);
        return NULL;
    }

    // long field
    fid = (*env)->GetStaticFieldID(env, cls, "staticLongField", "J");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read static long field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetStaticLongField(env, this, fid, (jlong)67);
    jlong jlongValue = (*env)->GetStaticLongField(env, this, fid);
    if (jlongValue != 67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad static long value\n", __FILE__, __LINE__);
        return NULL;
    }

    // float field
    fid = (*env)->GetStaticFieldID(env, cls, "staticFloatField", "F");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read static float field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetStaticFloatField(env, this, fid, (jfloat)67.67);
    jfloat jfloatValue = (*env)->GetStaticFloatField(env, this, fid);
    if (jfloatValue != (jfloat)67.67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad static float value\n", __FILE__, __LINE__);
        return NULL;
    }

    // double field
    fid = (*env)->GetStaticFieldID(env, cls, "staticDoubleField", "D");
    if (fid == 0) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't read static double field\n", __FILE__, __LINE__);
        return NULL;
    }
    (*env)->SetStaticDoubleField(env, this, fid, (jdouble)67.67);
    jdouble jdoubleValue = (*env)->GetStaticDoubleField(env, this, fid);
    if (jdoubleValue != (jdouble)67.67) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad static float value\n", __FILE__, __LINE__);
        return NULL;
    }

    // Object field
    fid = (*env)->GetStaticFieldID (env, cls, "staticObjectField", "[Ljava/lang/Object;");
    if (! fid)
    return 0;

    obj = (*env)->GetStaticObjectField (env, this, fid);

    return obj;
}

JNIEXPORT jclass JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callFindClass(JNIEnv *env, jclass klass, jstring name) {
    const char *buf = (*env)->GetStringUTFChars (env, name, NULL);
    jclass k = (*env)->FindClass (env, buf);
    (*env)->ReleaseStringUTFChars (env, name, buf);
    return k;
}

JNIEXPORT jobject JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callNewObject(JNIEnv *env, jobject obj) {
    jclass stringClass;
    jclass objectClass;
    jmethodID cid;
    jstring result;

    stringClass = (*env)->FindClass(env, "java/lang/String");
    objectClass = (*env)->FindClass(env, "java/lang/Object");
    if ((stringClass == NULL) || (objectClass == NULL)) {
        fprintf (stderr, "[ERROR] %s[%d]: String class should be a subclass of the Object class\n", __FILE__, __LINE__);
        return NULL;
    }

    jboolean isAssignable = (*env)->IsAssignableFrom(env, stringClass, objectClass);
    if (isAssignable != JNI_TRUE) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't find String or Object class\n", __FILE__, __LINE__);
        return NULL;
    }

    /* Get the method ID for the String(String) constructor */
    cid = (*env)->GetMethodID(env, stringClass, "<init>", "(Ljava/lang/String;)V");
    if (cid == NULL) {
        return NULL;
    }

    /* Create a string parameter */
    char *chars = "Hello";
    jstring str = (*env)->NewStringUTF(env, chars);

    /* Construct a java.lang.String object */
    result = (*env)->NewObject(env, stringClass, cid, str);

    jboolean isString = (*env)->IsInstanceOf(env, result, stringClass);
    if (isString != JNI_TRUE) {
        fprintf (stderr, "[ERROR] %s[%d]: Created Object should be a String\n", __FILE__, __LINE__);
        return NULL;
    }

    /* Free local references */
    (*env)->DeleteLocalRef(env, stringClass);
    (*env)->DeleteLocalRef(env, objectClass);
    (*env)->DeleteLocalRef(env, str);
    return result;

}

JNIEXPORT jboolean JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callExceptionMethod(JNIEnv *env, jobject obj) {
    jclass exceptionClass = (*env)->FindClass(env, "java/lang/Exception");

    if (exceptionClass == NULL) {
        fprintf (stderr, "[ERROR] %s[%d]: Can't find java.lang.Exception class\n", __FILE__, __LINE__);
        return JNI_FALSE;
    }
    (*env)->ThrowNew(env, exceptionClass, "Oops");
    (*env)->DeleteLocalRef(env, exceptionClass);
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callArrayMethods(JNIEnv * env, jobject obj, jbyteArray byteBuffer, jint length) {

    jsize arraySize = (*env)->GetArrayLength(env, byteBuffer);
    if (arraySize != length) {
        fprintf (stderr, "[ERROR] %s[%d]: Array size doesn't match\n", __FILE__, __LINE__);
        return JNI_FALSE;
    }

    // Read array
    jbyte *jarr = (*env)->GetByteArrayElements(env, byteBuffer, 0);
    if ((jarr[0] != 0) || (jarr[1] != 1) || (jarr[2] != 2) || (jarr[3] != 3)) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad byte array content\n", __FILE__, __LINE__);
        return JNI_FALSE;
    }

    // Write in the array
    jarr[0] = 3;
    jarr[1] = 2;
    jarr[2] = 1;
    jarr[3] = 0;

    // Release the array and clean context
    (*env)->ReleaseByteArrayElements(env, byteBuffer, jarr, 0);

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callArrayRegionMethods(JNIEnv * env, jobject obj, jbyteArray byteArray) {

    jbyte byteBuffer[4];

    jsize arraySize = (*env)->GetArrayLength(env, byteArray);
    if (arraySize != 4) {
        fprintf (stderr, "[ERROR] %s[%d]: Array size doesn't match\n", __FILE__, __LINE__);
        return JNI_FALSE;
    }

    // Read array
    (*env)->GetByteArrayRegion(env, byteArray, 0, 4, byteBuffer);
    if ((byteBuffer[0] != 0) || (byteBuffer[1] != 1) || (byteBuffer[2] != 2) || (byteBuffer[3] != 3)) {
        fprintf (stderr, "[ERROR] %s[%d]: Bad byte array content\n", __FILE__, __LINE__);
        return JNI_FALSE;
    }

    // Write in the array
    byteBuffer[0] = 3;
    byteBuffer[1] = 2;
    byteBuffer[2] = 1;
    byteBuffer[3] = 0;
    (*env)->SetByteArrayRegion(env, byteArray, 0, 4, byteBuffer);

    return JNI_TRUE;
}

