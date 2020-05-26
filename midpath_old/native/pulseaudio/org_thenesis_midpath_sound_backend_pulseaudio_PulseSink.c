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
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */ 

#include <jni.h>
#include <stdio.h>
#include <errno.h>

#include <pulse/simple.h>
#include <pulse/error.h>

#ifndef _Included_org_thenesis_midpath_sound_backend_pulseaudio_PulseSink
#define _Included_org_thenesis_midpath_sound_backend_pulseaudio_PulseSink
#ifdef __cplusplus
extern "C" {
#endif

static pa_simple *s;

/*
 * Class:     org_thenesis_midpath_sound_backend_pulseaudio_PulseSink
 * Method:    open0
 * Signature: (III)Z
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_midpath_sound_backend_pulseaudio_PulseSink_open0(JNIEnv * env, jobject obj, jint format, jint channels, jint sampleRate) {

	int error;
	pa_sample_spec ss;

	ss.format = format;
 	ss.channels = channels;
 	ss.rate = sampleRate;
	
	s = pa_simple_new(NULL,             /* Use the default server. */
                   "MIDPath",           /* Our application's name. */
                   PA_STREAM_PLAYBACK,
                   NULL,               /* Use the default device. */
                   "Line",            /* Description of our stream. */
                   &ss,                /* Our sample format. */
                   NULL,               /* Use default channel map */
                   NULL,               /* Use default buffering attributes. */
                   &error               /* Ignore error code. */
                   );
	
	if (!s) {
		fprintf(stderr, __FILE__": pa_simple_new() failed: %s\n", pa_strerror(error));
		return JNI_FALSE;
	}
	
	return JNI_TRUE;

}

/*
 * Class:     org_thenesis_midpath_sound_backend_pulseaudio_PulseSink
 * Method:    write0
 * Signature: ([BII)I
 */
JNIEXPORT int JNICALL Java_org_thenesis_midpath_sound_backend_pulseaudio_PulseSink_write0(JNIEnv * env, jobject obj, jbyteArray byteBuffer, jint offset, jint length) {
	
	int error;
	signed char* data = NULL;
	int	nWritten = -1;

	data = (*env)->GetByteArrayElements(env, byteBuffer, NULL);
	
	if (pa_simple_write(s, data + offset, length, &error) < 0) {
    	fprintf(stderr, __FILE__": pa_simple_write() failed: %s\n", pa_strerror(error));
        nWritten = -1;
    } else {
		nWritten = length;
	}
	
	(*env)->ReleaseByteArrayElements(env, byteBuffer, data, JNI_ABORT);
	
	return nWritten;

}

/*
 * Class:     org_thenesis_midpath_sound_backend_pulseaudio_PulseSink
 * Method:    close0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_thenesis_midpath_sound_backend_pulseaudio_PulseSink_close0(JNIEnv * env, jobject obj) {
	 if (s)
		pa_simple_free(s);
}

#ifdef __cplusplus
}
#endif
#endif


