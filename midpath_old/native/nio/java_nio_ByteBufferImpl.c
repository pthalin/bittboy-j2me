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
 
#include <stdlib.h>
#include <string.h>

#include "java_nio_ByteBufferImpl.h"

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _allocNative
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_java_nio_ByteBufferImpl__1allocNative(JNIEnv * env, jclass c, jint capacity) {
	//printf("allocNative\n");
	return (jint)malloc(capacity);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _copyBytes
 * Signature: (III)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1copyBytes(JNIEnv * env, jclass c, jint srcAddress, jint dstAddress, jint nbytes) {
	memcpy((void *) dstAddress, (void *)srcAddress, nbytes);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getByte
 * Signature: (I)B
 */
JNIEXPORT jbyte JNICALL Java_java_nio_ByteBufferImpl__1getByte(JNIEnv * env, jclass c, jint address) {
	return *((jbyte *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getBytes
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getBytes(JNIEnv * env, jclass c, jint address, jbyteArray dst, jint offset, jint length) {
	(*env)->SetByteArrayRegion(env, dst, offset, length, (jbyte *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putByte
 * Signature: (IB)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putByte(JNIEnv * env, jclass c, jint address, jbyte value) {
	 *((jbyte *) address) = value;
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putBytes
 * Signature: (I[BII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putBytes(JNIEnv * env, jclass c, jint address, jbyteArray dst, jint offset, jint length) {
	(*env)->GetByteArrayRegion(env, dst, offset, length, (jbyte *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getShort
 * Signature: (I)S
 */
JNIEXPORT jshort JNICALL Java_java_nio_ByteBufferImpl__1getShort(JNIEnv * env, jclass c, jint address) {
	return *((jshort *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getShorts
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getShorts(JNIEnv * env, jclass c, jint address, jshortArray dst, jint offset, jint length) {
	(*env)->SetShortArrayRegion(env, dst, offset, length, (jshort *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putShort
 * Signature: (IS)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putShort(JNIEnv * env, jclass c, jint address, jshort value) {
	*((jshort *) address) = value;
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putShorts
 * Signature: (I[SII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putShorts(JNIEnv * env, jclass c, jint address, jshortArray dst, jint offset, jint length) {
	(*env)->GetShortArrayRegion(env, dst, offset, length, (jshort *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getInt
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_java_nio_ByteBufferImpl__1getInt(JNIEnv * env, jclass c, jint address) {
	return *((jint *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getInts
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getInts(JNIEnv * env, jclass c, jint address, jintArray dst, jint offset, jint length) {
	(*env)->SetIntArrayRegion(env, dst, offset, length, (jint *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putInt
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putInt(JNIEnv * env, jclass c, jint address, jint value) {
	*((jint *) address) = value;
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putInts
 * Signature: (I[III)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putInts(JNIEnv * env, jclass c, jint address, jintArray dst, jint offset, jint length) {
	(*env)->GetIntArrayRegion(env, dst, offset, length, (jint *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getFloat
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_java_nio_ByteBufferImpl__1getFloat(JNIEnv * env, jclass c, jint address) {
	return *((jfloat *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _getFloats
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1getFloats(JNIEnv * env, jclass c, jint address, jfloatArray dst, jint offset, jint length) {
	(*env)->SetFloatArrayRegion(env, dst, offset, length, (jfloat *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putFloat
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putFloat(JNIEnv * env, jclass c, jint address, jfloat value) {
	*((jfloat *) address) = value;
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    _putFloats
 * Signature: (I[FII)V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl__1putFloats(JNIEnv * env, jclass c, jint address, jfloatArray dst, jint offset, jint length) {
	(*env)->GetFloatArrayRegion(env, dst, offset, length, (jfloat *) address);
}

/*
 * Class:     java_nio_ByteBufferImpl
 * Method:    finalize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_java_nio_ByteBufferImpl_finalize(JNIEnv * env, jobject jobj) {

	jfieldID fid;
	jclass cls;
	void* native_buffer_address;
	//jobject directParentBuffer;
	jboolean hasDirectParentBuffer;
	jmethodID mid;
	
	cls = (*env)->GetObjectClass(env, jobj);
	
	/* Get the address from the arrayOffset field */
	fid = (*env)->GetFieldID(env, cls, "arrayOffset",  "I");
    if (fid == 0) {
    	fprintf (stderr, "[ERROR] %s[%d]: Can't find field arrayOffset\n", __FILE__, __LINE__);
        return;
    }
    native_buffer_address = (void *)(*env)->GetIntField(env, jobj, fid);
    
    mid = (*env)->GetMethodID(env, cls, "hasDirectParent", "()Z");
    hasDirectParentBuffer = (*env)->CallBooleanMethod(env, jobj, mid);
    if (mid == 0) {
   		fprintf (stderr, "[ERROR] %s[%d]: Can't find method hasDirectParent\n", __FILE__, __LINE__);
    	return;
    }

// 	  Getting a field object doesn't work on cacao-cldc... Used a method instead    
//    /* Check if the directParent Object is null */
//    fid = (*env)->GetFieldID(env, cls, "directParent", "Ljava/lang/Buffer;");
//    if (fid == 0) {
//    	fprintf (stderr, "[ERROR] %s[%d]: Can't find field directParent\n", __FILE__, __LINE__);
//        return;
//    }
//    directParentBuffer = (*env)->GetObjectField(env, jobj, fid);
    
    /* Free the buffer if there is no parent buffer */ 
    if (native_buffer_address && !hasDirectParentBuffer) {        
        free(native_buffer_address);
        //fprintf(stdout, "[NATIVE] %s[%d]: Buffer is free\n", __FILE__, __LINE__);
    }
    
}
