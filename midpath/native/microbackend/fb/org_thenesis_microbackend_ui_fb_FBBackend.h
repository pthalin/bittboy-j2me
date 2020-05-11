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

#ifndef _Included_org_thenesis_microbackend_ui_fb_FBBackend
#define _Included_org_thenesis_microbackend_ui_fb_FBBackend

#include "jni.h"

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    getKeymap
 * Signature: (II)I
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_getKeymap(JNIEnv * env, jobject obj, jcharArray keymapBuffer);

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    initialize
 * Signature: (II)I
 */
extern JNIEXPORT jboolean JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_initialize(JNIEnv * env, jobject obj, jstring keyboardDeviceName, jstring mouseDeviceName, jstring touchscreenDeviceName, jstring fbDeviceName, jint width, jint height);

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    writeARGB
 * Signature: ([IIIII)V
 */
extern JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_drawARGB(JNIEnv * env, jobject obj, jintArray intBuffer, jint offset, jint scanlength, jint x, jint y, jint width, jint height);

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    readKeyCode
 * Signature: ()I
 */
extern JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_eventLoop(JNIEnv * env, jobject obj);

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    quit
 * Signature: ()V
 */
extern JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_quit(JNIEnv * env, jobject obj);

#ifdef __cplusplus
}
#endif
#endif




