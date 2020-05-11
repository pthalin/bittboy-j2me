/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * GNU Classpath - Copyright (C) 1998 Free Software Foundation, Inc.
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

#ifndef __JCL_H__
#define __JCL_H__

#include <stddef.h>
#include "jni.h"
/*#include <config.h>*/

#if SIZEOF_VOID_P == 4
typedef jint jpointer;
#elif SIZEOF_VOID_P == 8
typedef jlong jpointer;
#endif

/* Helper macros for going between pointers and jlongs.  */
#define JLONG_TO_PTR(T,P) ((T *)(long)P)
#define PTR_TO_JLONG(P) ((jlong)(long)P)

JNIEXPORT jclass JNICALL JCL_FindClass (JNIEnv * env, const char *className);
JNIEXPORT void JNICALL JCL_ThrowException (JNIEnv * env,
					   const char *className,
					   const char *errMsg);
JNIEXPORT void *JNICALL JCL_malloc (JNIEnv * env, size_t size);
JNIEXPORT void *JNICALL JCL_realloc (JNIEnv * env, void *ptr, size_t size);
JNIEXPORT void JNICALL JCL_free (JNIEnv * env, void *p);
JNIEXPORT const char *JNICALL JCL_jstring_to_cstring (JNIEnv * env,
						      jstring s);
JNIEXPORT void JNICALL JCL_free_cstring (JNIEnv * env, jstring s,
					 const char *cstr);
JNIEXPORT jint JNICALL JCL_MonitorEnter (JNIEnv * env, jobject o);
JNIEXPORT jint JNICALL JCL_MonitorExit (JNIEnv * env, jobject o);

JNIEXPORT jobject JNICALL JCL_NewRawDataObject (JNIEnv * env, void *data);
JNIEXPORT void * JNICALL JCL_GetRawData (JNIEnv * env, jobject rawdata);

#define JCL_RETHROW_EXCEPTION(env) if((*(env))->ExceptionOccurred((env)) != NULL) return NULL;

/* Simple debug macro */
#ifdef DEBUG
#define DBG(x) fprintf(stderr, "%s", (x));
#else
#define DBG(x)
#endif

/* Some O/S's don't declare 'environ' */
#if HAVE_CRT_EXTERNS_H
/* Darwin does not have a variable named environ
   but has a function which you can get the environ
   variable with.  */
#include <crt_externs.h>
#define environ (*_NSGetEnviron())
#else
extern char **environ;
#endif /* HAVE_CRT_EXTERNS_H */

#endif

