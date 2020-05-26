/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * Tritonus - Copyright (C) 1999 - 2002 by Matthias Pfisterer <Matthias.Pfisterer@gmx.de>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */ 

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <esd.h>


#ifndef _Included_org_thenesis_midpath_sound_backend_esd_EsdSink
#define _Included_org_thenesis_midpath_sound_backend_esd_EsdSink
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_thenesis_midpath_sound_backend_esd_EsdSink
 * Method:    open
 * Signature: (II)J
 */
JNIEXPORT jlong JNICALL Java_org_thenesis_midpath_sound_backend_esd_EsdSink_open0(JNIEnv * env, jobject obj, jint format, jint sampleRate) {

	int	fd = -1;
	char name[20];
	
	sprintf(name, "midpath%d", (int) obj);	// DANGEROUS!!
	fd = esd_play_stream/*_fallback*/(format, sampleRate, NULL, name);
	
	return (jlong) fd;

}

/*
 * Class:     org_thenesis_midpath_sound_backend_esd_EsdSink
 * Method:    write
 * Signature: (I[BII)I
 */
JNIEXPORT int JNICALL Java_org_thenesis_midpath_sound_backend_esd_EsdSink_write0(JNIEnv * env, jobject obj, jlong lfd, jbyteArray byteBuffer, jint offset, jint length) {
	
	int	 fd = (int) lfd;
	signed char* data = NULL;
	int	nWritten = -1;

	data = (*env)->GetByteArrayElements(env, byteBuffer, NULL);
	nWritten = write(fd, data + offset, length);
	(*env)->ReleaseByteArrayElements(env, byteBuffer, data, JNI_ABORT);
	
	return nWritten;

}

/*
 * Class:     org_thenesis_midpath_sound_backend_esd_EsdSink
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_thenesis_midpath_sound_backend_esd_EsdSink_close0(JNIEnv * env, jobject obj, jlong lfd) {
	int	fd = (int) lfd;
	close(fd);
}

#ifdef __cplusplus
}
#endif
#endif


