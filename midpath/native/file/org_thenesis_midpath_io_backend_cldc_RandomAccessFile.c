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
 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>

#include <jni.h>
#include "jcl.h"
#include "org_thenesis_midpath_io_backend_cldc_RandomAccessFile.h"

#include "javaio.h"

/*************************************************************************/

/*
 * Returns the length of the file being read.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    lengthInternal
 * Signature: (I)J
 */

JNIEXPORT jlong JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_lengthInternal(JNIEnv *env, jobject obj, jint fd)
{
  return(_javaio_get_file_length(env, fd));
}

/*************************************************************************/

/*
 * Method to skip bytes in a file.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    skipInternal
 * Signature: (II)I
 */

JNIEXPORT jint JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_skipInternal(JNIEnv *env, jobject obj, jint fd,
                                           jint num_bytes)
{
  return(_javaio_skip_bytes(env, fd, num_bytes));
}

/*************************************************************************/

/*
 * Opens the file for reading.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    open
 * Signature: (Ljava/lang/String;Z)I
 */

JNIEXPORT jint JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_open(JNIEnv *env, jobject obj, jstring name,
                                   jboolean read_only)
{
  if (read_only)
    return(_javaio_open(env, name, O_RDONLY));
  else
    return(_javaio_open(env, name, O_RDWR|O_CREAT));
}

/*************************************************************************/

/*
 * Closes the file.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    closeInternal
 * Signature: (I)V
 */

JNIEXPORT void JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_closeInternal(JNIEnv *env, jobject obj, jint fd)
{
  _javaio_close(env, fd);
}

/*************************************************************************/

/*
 * Reads bytes from the file.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    readInternal
 * Signature: (I[BII)I
 */ 

JNIEXPORT jint JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_readInternal(JNIEnv *env, jobject obj, jint fd,
                                           jarray buf, jint offset, jint len)
{
  return(_javaio_read(env, obj, fd, buf, offset, len));
}

/*************************************************************************/

/*
 * Write bytes to the file.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    writeInternal
 * Signature: (I[BII)V
 */ 

JNIEXPORT void JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_writeInternal(JNIEnv *env, jobject obj, jint fd,
                                            jarray buf, jint offset, jint len)
{
  _javaio_write(env, obj, fd, buf, offset, len);
}

/*************************************************************************/

/*
 * This method returns the current position in the file.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    getFilePointerInternal
 * Signature: (I)J
 */

JNIEXPORT jlong JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_getFilePointerInternal(JNIEnv *env, jobject obj,
                                                     jint fd)
{
  int rc = lseek(fd, 0, SEEK_CUR);
  if (rc == -1)
    JCL_ThrowException(env, "java/io/IOException", strerror(errno));

  return(rc);
}

/*************************************************************************/

/*
 * This method seeks to the specified position from the beginning of the file.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    seekInternal
 * Signature: (IJ)V
 */

JNIEXPORT void JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_seekInternal(JNIEnv *env, jobject obj,
                                           jint fd, jlong pos)
{
  int rc = lseek(fd, pos, SEEK_SET);
  if (rc == -1)
    JCL_ThrowException(env, "java/io/IOException", strerror(errno));
}

/*************************************************************************/

/* 
 * This method sets the length of the file.  Hmm.  Do all platforms have
 * ftruncate?  Probably not so we migth have to do some non-atomic stuff
 * on those.
 *
 * Class:     org_thenesis_midpath_io_backend_cldc_RandomAccessFile
 * Method:    setLengthInternal
 * Signature: (IJ)V
 */

JNIEXPORT void JNICALL
Java_org_thenesis_midpath_io_backend_cldc_RandomAccessFile_setLengthInternal(JNIEnv *env, jobject obj,
                                                jint fd, jlong len)
{
  int rc;

  jlong cur_len = _javaio_get_file_length(env, fd);
  if (cur_len == -1)
    return;

  if (cur_len > len)
    rc = ftruncate(fd, len);
  else if (cur_len < len)
    rc = lseek(fd, len - cur_len, SEEK_CUR);
  else
    return;

  if (rc == -1)
    JCL_ThrowException(env, "java/io/IOException", strerror(errno));
}

