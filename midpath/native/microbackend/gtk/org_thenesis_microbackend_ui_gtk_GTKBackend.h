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
 
#include <jni.h>
/* Header for class org_thenesis_microbackend_ui_gtk_GTKBackend */

#ifndef _org_thenesis_microbackend_ui_gtk_GTKBackend
#define _org_thenesis_microbackend_ui_gtk_GTKBackend

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    initialize
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_initialize(JNIEnv * env, jobject obj, jint width, jint height);

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    gtkMain
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_gtkMainIterationDo(JNIEnv * env, jobject obj);

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    destroy
 * Signature: ()I
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_destroy(JNIEnv * env, jobject obj);

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    writeARGB
 * Signature: ([IIIII)V
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_writeARGB(JNIEnv * env, jobject obj, jintArray intBuffer, jint x_src, jint y_src, jint width, jint height);

#ifdef __cplusplus
}
#endif
#endif
