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
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <alsa/asoundlib.h>

#ifndef _Included_org_thenesis_midpath_sound_backend_alsa_AlsaSink
#define _Included_org_thenesis_midpath_sound_backend_alsa_AlsaSink
#ifdef __cplusplus
extern "C" {
#endif

 /* Handle for the PCM device */ 
snd_pcm_t *pcm_handle;          

/* This structure contains information about    */
/* the hardware and can be used to specify the  */      
/* configuration to be used for the PCM stream. */ 
snd_pcm_hw_params_t *hwparams;

/*
 * Class:     org_thenesis_midpath_sound_backend_alsa_AlsaSink
 * Method:    open
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_midpath_sound_backend_alsa_AlsaSink_open0(JNIEnv * env, jobject obj, jstring deviceName, jint bufferSize, jint sampleRate) {

	/* Name of the PCM device, like plughw:0,0          */
	/* The first number is the number of the soundcard, */
	/* the second number is the number of the device.   */
	char *pcm_name;

	char *utfDeviceName;
    utfDeviceName = (char *)(*env)->GetStringUTFChars(env, deviceName, 0);
    pcm_name = strdup(utfDeviceName);
    //pcm_name = strdup("plughw:0,0");
	(*env)->ReleaseStringUTFChars(env, deviceName, utfDeviceName);
	
	 /* Playback stream */
    snd_pcm_stream_t stream = SND_PCM_STREAM_PLAYBACK;
    
    /* Allocate the snd_pcm_hw_params_t structure on the stack. */
    snd_pcm_hw_params_alloca(&hwparams);
    
    /* Open PCM. The last parameter of this function is the mode. */
    /* If this is set to 0, the standard mode is used. Possible   */
    /* other values are SND_PCM_NONBLOCK and SND_PCM_ASYNC.       */ 
    /* If SND_PCM_NONBLOCK is used, read / write access to the    */
    /* PCM device will return immediately. If SND_PCM_ASYNC is    */
    /* specified, SIGIO will be emitted whenever a period has     */
    /* been completely processed by the soundcard.                */
    if (snd_pcm_open(&pcm_handle, pcm_name, stream, 0) < 0) {
      fprintf(stderr, "Error opening PCM device %s\n", pcm_name);
      return(-1);
    }
    
    /* Init hwparams with full configuration space */
    if (snd_pcm_hw_params_any(pcm_handle, hwparams) < 0) {
      fprintf(stderr, "Can not configure this PCM device.\n");
      return(-1);
    }
    
    int rate = 44100; /* Sample rate */
    int exact_rate;   /* Sample rate returned by */
                      /* snd_pcm_hw_params_set_rate_near() */ 
    snd_pcm_uframes_t best_buffer_size; /* Best buffer size returned by snd_pcm_hw_params_set_buffer_size_near()*/
    int dir = 0;          /* exact_rate == rate --> dir = 0 */
                      /* exact_rate < rate  --> dir = -1 */
                      /* exact_rate > rate  --> dir = 1 */
    int periods = 2;       /* Number of periods */
    snd_pcm_uframes_t periodsize = 8192; /* Periodsize (bytes) */
    
    
      
    /* Set access type. This can be either    */
    /* SND_PCM_ACCESS_RW_INTERLEAVED or       */
    /* SND_PCM_ACCESS_RW_NONINTERLEAVED.      */
    /* There are also access types for MMAPed */
    /* access, but this is beyond the scope   */
    /* of this introduction.                  */
    if (snd_pcm_hw_params_set_access(pcm_handle, hwparams, SND_PCM_ACCESS_RW_INTERLEAVED) < 0) {
      fprintf(stderr, "Error setting access.\n");
      return(-1);
    }
  
    /* Set sample format */
    if (snd_pcm_hw_params_set_format(pcm_handle, hwparams, SND_PCM_FORMAT_S16_LE) < 0) {
      fprintf(stderr, "Error setting format.\n");
      return(-1);
    }

    /* Set sample rate. If the exact rate is not supported */
    /* by the hardware, use nearest possible rate.         */ 
    exact_rate = rate;
    if (snd_pcm_hw_params_set_rate_near(pcm_handle, hwparams, &exact_rate, 0) < 0) {
      fprintf(stderr, "Error setting rate.\n");
      return(-1);
    }
    if (rate != exact_rate) {
      fprintf(stderr, "The rate %d Hz is not supported by your hardware.\n ==> Using %d Hz instead.\n", rate, exact_rate);
    }

    /* Set number of channels */
    if (snd_pcm_hw_params_set_channels(pcm_handle, hwparams, 2) < 0) {
      fprintf(stderr, "Error setting channels.\n");
      return(-1);
    }

    /* Set number of periods. Periods used to be called fragments. */ 
    if (snd_pcm_hw_params_set_periods(pcm_handle, hwparams, periods, 0) < 0) {
      fprintf(stderr, "Error setting periods.\n");
      return(-1);
    }
//
//    /* Set buffer size (in frames). The resulting latency is given by */
//    /* latency = periodsize * periods / (rate * bytes_per_frame)     */
//    if (snd_pcm_hw_params_set_buffer_size(pcm_handle, hwparams, (periodsize * periods)>>2) < 0) {
//    //if (snd_pcm_hw_params_set_buffer_size(pcm_handle, hwparams, 2000) < 0) {
//      fprintf(stderr, "Error setting buffersize.\n");
//      return(-1);
//    }
    
    best_buffer_size = (periodsize * periods) >> 2;
    if (snd_pcm_hw_params_set_buffer_size_near(pcm_handle, hwparams, &best_buffer_size) < 0) {
      fprintf(stderr, "Error setting buffer size.\n");
      return(-1);
    }
//    printf("Best buffer size: %i\n", best_buffer_size);
//


	int val = 500000;
//	snd_pcm_hw_params_set_buffer_time_max(pcm_handle, hwparams, &val, 0);
//	printf("Best buffer size: %i\n", val);

  
    /* Apply HW parameter settings to */
    /* PCM device and prepare device  */
    if (snd_pcm_hw_params(pcm_handle, hwparams) < 0) {
      fprintf(stderr, "Error setting HW params.\n");
      return(-1);
    }
    
    snd_pcm_hw_params_get_buffer_time(hwparams, &val, 0);
 	//printf("Best buffer time: %i\n", val);
    snd_pcm_hw_params_get_buffer_size(hwparams, &best_buffer_size);
 	//printf("Best buffer size: %lo\n", best_buffer_size);
   
    /* Write num_frames frames from buffer data to    */ 
    /* the PCM device pointed to by pcm_handle.       */
    /* Returns the number of frames actually written. */
    //snd_pcm_sframes_t snd_pcm_writei(pcm_handle, data, num_frames);

    /* Write num_frames frames from buffer data to    */ 
    /* the PCM device pointed to by pcm_handle.       */ 
    /* Returns the number of frames actually written. */
    //snd_pcm_sframes_t snd_pcm_writen(pcm_handle, data, num_frames);
    
     /* Stop PCM device and drop pending frames */
    //snd_pcm_drop(pcm_handle);

    /* Stop PCM device after pending frames have been played */ 
    //snd_pcm_drain(pcm_handle);
  
	//free(data);
	free(pcm_name);
	
	return 1;

}

/*
 * Class:     org_thenesis_midpath_sound_backend_alsa_AlsaSink
 * Method:    available
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_midpath_sound_backend_alsa_AlsaSink_available0(JNIEnv * env, jobject obj) {
	
	snd_pcm_sframes_t frames_to_deliver;
	
	if ((frames_to_deliver = snd_pcm_avail_update (pcm_handle)) < 0) {
				if (frames_to_deliver == -EPIPE) {
					fprintf (stderr, "an xrun occured\n");
					//break;
				} else {
					fprintf (stderr, "unknown ALSA avail update return value (%d)\n", 
						 frames_to_deliver);
					//break;
				}
			}
	
	return frames_to_deliver;
	
}

/*
 * Class:     org_thenesis_midpath_sound_backend_alsa_AlsaSink
 * Method:    write
 * Signature: ([BII)I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_midpath_sound_backend_alsa_AlsaSink_write0(JNIEnv * env, jobject obj, jbyteArray byteBuffer, jint offset, jint length) {
	
	// Lock the array (http://www.iam.ubc.ca/guides/javatut99/native1.1/implementing/array.html)
  	jsize arraySize = (*env)->GetArrayLength(env, byteBuffer);
  	jbyte *jarr = (*env)->GetByteArrayElements(env, byteBuffer, 0);
  	char * srcBuffer = (char*)jarr;
  	snd_pcm_uframes_t frames = length;

 	int pcmreturn;
 	while ((pcmreturn = snd_pcm_writei(pcm_handle, srcBuffer, frames)) < 0) {
        snd_pcm_prepare(pcm_handle);
        fprintf(stderr, "<<<<<<<<<<<<<<< Buffer Underrun >>>>>>>>>>>>>>>\n");
    }

  	// Release the array and clean context
  	(*env)->ReleaseByteArrayElements(env, byteBuffer, jarr, 0);
  
 	return pcmreturn;

}

/*
 * Class:     org_thenesis_midpath_sound_backend_alsa_AlsaSink
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_thenesis_midpath_sound_backend_alsa_AlsaSink_close0(JNIEnv * env, jobject obj) {
	snd_pcm_drain(pcm_handle);
}

#ifdef __cplusplus
}
#endif
#endif


