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
#include <jni.h>
/* Header for class org_thenesis_midpath_test_suite_jni_JNITests */

#ifndef _Included_org_thenesis_midpath_test_suite_jni_JNITests
#define _Included_org_thenesis_midpath_test_suite_jni_JNITests
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callNativeMethod
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callNativeMethod(JNIEnv *, jobject, jbyte, jint, jlong, jfloat, jdouble, jboolean);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callStringMethods
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callStringMethods(JNIEnv *env, jobject obj, jstring prompt);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callStringUTFMethods
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callStringUTFMethods(JNIEnv *env, jobject obj, jstring prompt);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callJavaMethodFromNative
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callJavaMethodFromNative(JNIEnv *, jobject);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callJavaFieldFromNative
 * Signature: ()Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callJavaFieldFromNative(JNIEnv *, jobject);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callJavaStaticFieldFromNative
 * Signature: ()Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callJavaStaticFieldFromNative(JNIEnv *, jobject);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callFindClass
 * Signature: (Ljava/lang/String;)Ljava/lang/Class;
 */
JNIEXPORT jclass JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callFindClass(JNIEnv *, jclass, jstring);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callNewObject
 * Signature: ()V
 */
JNIEXPORT jobject JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callNewObject(JNIEnv *env, jobject obj);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callExceptionMethod
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callExceptionMethod(JNIEnv *env, jobject obj);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callArrayMethods
 * Signature: ([BI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callArrayMethods(JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     org_thenesis_midpath_test_suite_jni_JNITests
 * Method:    callArrayRegionMethods
 * Signature: ([BI)Z
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_midpath_test_suite_jni_JNITests_callArrayRegionMethods(JNIEnv *, jobject, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
