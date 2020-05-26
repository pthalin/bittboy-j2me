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

#ifndef __org_thenesis_midpath_io_backend_cldc_RandomAccessFile__
#define __org_thenesis_midpath_io_backend_cldc_RandomAccessFile__

#include "jni.h"

#ifdef __cplusplus
extern "C"
{
#endif

extern jint Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_open (JNIEnv *env, jobject, jstring, jboolean);
extern void Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_closeInternal (JNIEnv *env, jobject, jint);
extern jlong Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_getFilePointerInternal (JNIEnv *env, jobject, jint);
extern jlong Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_lengthInternal (JNIEnv *env, jobject, jint);
extern void Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_seekInternal (JNIEnv *env, jobject, jint, jlong);
extern void Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_setLengthInternal (JNIEnv *env, jobject, jint, jlong);
extern jint Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_readInternal (JNIEnv *env, jobject, jint, jbyteArray, jint, jint);
extern jint Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_skipInternal (JNIEnv *env, jobject, jint, jint);
extern void Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_writeInternal (JNIEnv *env, jobject, jint, jbyteArray, jint, jint);

#ifdef __cplusplus
}
#endif

#endif /* __org_thenesis_midpath_io_backend_cldc_RandomAccessFile__ */
