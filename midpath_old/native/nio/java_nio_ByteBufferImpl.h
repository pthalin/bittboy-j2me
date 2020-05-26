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
/* Header for class java_nio_ByteBufferImpl */

#ifndef _Included_java_nio_ByteBufferImpl
#define _Included_java_nio_ByteBufferImpl
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _allocNative
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_java_nio_ByteBufferImpl__1allocNative
  (JNIEnv *, jclass, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _copyBytes
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1copyBytes
  (JNIEnv *, jclass, jint, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getByte
 * Signature: (I)B
 */
JNIEXPORT jbyte JNICALL Java_java_nio_ByteBufferImpl__1getByte
  (JNIEnv *, jclass, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getBytes
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getBytes
  (JNIEnv *, jclass, jint, jbyteArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putByte
 * Signature: (IB)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putByte
  (JNIEnv *, jclass, jint, jbyte);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putBytes
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putBytes
  (JNIEnv *, jclass, jint, jbyteArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getShort
 * Signature: (I)S
 */
JNIEXPORT jshort JNICALL Java_java_nio_ByteBufferImpl__1getShort
  (JNIEnv *, jclass, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getShorts
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getShorts
  (JNIEnv *, jclass, jint, jshortArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putShort
 * Signature: (IS)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putShort
  (JNIEnv *, jclass, jint, jshort);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putShorts
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putShorts
  (JNIEnv *, jclass, jint, jshortArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getInt
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_java_nio_ByteBufferImpl__1getInt
  (JNIEnv *, jclass, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getInts
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getInts
  (JNIEnv *, jclass, jint, jintArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putInt
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putInt
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putInts
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putInts
  (JNIEnv *, jclass, jint, jintArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getFloat
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_java_nio_ByteBufferImpl__1getFloat
  (JNIEnv *, jclass, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getFloats
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getFloats
  (JNIEnv *, jclass, jint, jfloatArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putFloat
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putFloat
  (JNIEnv *, jclass, jint, jfloat);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putFloats
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putFloats
  (JNIEnv *, jclass, jint, jfloatArray, jint, jint);

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    finalize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl_finalize
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
