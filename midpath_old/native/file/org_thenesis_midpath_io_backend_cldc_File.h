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

#ifndef __java_io_File__
#define __java_io_File__

#include "jni.h"

#ifdef __cplusplus
extern "C"
{
#endif

extern jboolean Java_java_io_File_createInternal (JNIEnv *env, jclass, jstring);
extern jboolean Java_java_io_File_canReadInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_canWriteInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_setReadOnlyInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_existsInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_isFileInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_isDirectoryInternal (JNIEnv *env, jobject, jstring);
extern jlong Java_java_io_File_lengthInternal (JNIEnv *env, jobject, jstring);
extern jlong Java_java_io_File_lastModifiedInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_setLastModifiedInternal (JNIEnv *env, jobject, jstring, jlong);
extern jboolean Java_java_io_File_deleteInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_mkdirInternal (JNIEnv *env, jobject, jstring);
extern jboolean Java_java_io_File_renameToInternal (JNIEnv *env, jobject, jstring, jstring);
extern jobjectArray Java_java_io_File_listInternal (JNIEnv *env, jobject, jstring);

#ifdef __cplusplus
}
#endif

#endif /* __java_io_File__ */
