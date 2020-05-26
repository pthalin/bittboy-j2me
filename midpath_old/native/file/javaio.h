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
 
#ifndef JAVAIO_H_INCLUDED
#define JAVAIO_H_INCLUDED

#include <stddef.h>

/*
 * Function Prototypes
 */

extern jlong _javaio_get_file_length(JNIEnv *, jint);
extern jlong _javaio_skip_bytes(JNIEnv *, jint, jlong);
extern jint _javaio_open(JNIEnv *, jstring, int);
extern void _javaio_close(JNIEnv *, jint fd);
extern jint _javaio_read(JNIEnv *, jobject obj, jint, jarray, jint, jint);
extern jint _javaio_write(JNIEnv *, jobject obj, jint, jarray, jint, jint);

#endif /* JAVAIO_H_INCLUDED */

