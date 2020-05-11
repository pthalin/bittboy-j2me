/*
 *
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
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
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

/**
 * @file
 *
 * Implementation of Java native methods for the <tt>ImageData</tt> class.
 */


#include <stdlib.h>
#include <string.h>
#include <jni.h>
//#include <sni.h>
//#include <midpError.h>

#include "gxj_util.h"
#include "imgapi_image.h"
#include "img_errorcodes.h"
#include "imgj_rgb.h"

#include "org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData.h"

static jfieldID imageData_width_Id;
static jfieldID imageData_height_Id;
static jfieldID imageData_pixelData_Id;
static jfieldID imageData_alphaData_Id;
static jfieldID imageData_nativePixelData_Id;
static jfieldID imageData_nativeAlphaData_Id;

inline gxj_screen_buffer* getScreenBufferFromImageData(JNIEnv *env, jobject imageData, gxj_screen_buffer *sbuf) {
    sbuf->width = (*env)->GetIntField(env, imageData, imageData_width_Id);
    sbuf->height = (*env)->GetIntField(env, imageData, imageData_height_Id);
    sbuf->pixelByteArray = (*env)->GetObjectField(env, imageData, imageData_pixelData_Id);
    sbuf->alphaByteArray = (*env)->GetObjectField(env, imageData, imageData_alphaData_Id);

    /* Only use nativePixelData and nativeAlphaData if
     * pixelData is null */
    if (sbuf->pixelByteArray != NULL) {
        sbuf->pixelData = (gxj_pixel_type *) (*env)->GetByteArrayElements(env, sbuf->pixelByteArray, NULL);
        sbuf->alphaData = (sbuf->alphaByteArray != NULL) ?
                (gxj_alpha_type *) (*env)->GetByteArrayElements(env, sbuf->alphaByteArray, NULL) : NULL;
    } else {
        sbuf->pixelData = (gxj_pixel_type *) (*env)->GetIntField(env, imageData, imageData_nativePixelData_Id);
        sbuf->alphaData = (gxj_alpha_type *) (*env)->GetIntField(env, imageData, imageData_nativeAlphaData_Id);
    }

    return sbuf;
}

inline void releaseScreenBufferPrimitiveArrays(JNIEnv *env, gxj_screen_buffer *sbuf) {
    if (sbuf->pixelByteArray != NULL) {
        (*env)->ReleaseByteArrayElements(env, sbuf->pixelByteArray, (jbyte *)sbuf->pixelData, 0);
    }
    if (sbuf->alphaByteArray != NULL) {
        (*env)->ReleaseByteArrayElements(env, sbuf->alphaByteArray, (jbyte *)sbuf->alphaData, 0);
    }
}

JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData_initFieldIDs(JNIEnv *env, jclass cls) {
    imageData_width_Id = (*env)->GetFieldID(env, cls, "width", "I");
    imageData_height_Id = (*env)->GetFieldID(env, cls, "height", "I");
    imageData_pixelData_Id = (*env)->GetFieldID(env, cls, "pixelData", "[B");
    imageData_alphaData_Id = (*env)->GetFieldID(env, cls, "alphaData", "[B");
    imageData_nativePixelData_Id = (*env)->GetFieldID(env, cls, "nativePixelData", "I");
    imageData_nativeAlphaData_Id = (*env)->GetFieldID(env, cls, "nativeAlphaData", "I");
}


JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData_allocateNativeData
  (JNIEnv *env, jobject imageData, jint width, jint height, jboolean allocateAlpha) {

	jint length = width * height * 2;
	jbyte *addr = GXJ_malloc(length);
	(*env)->SetIntField(env, imageData, imageData_nativePixelData_Id, (int)addr);

	if (allocateAlpha == JNI_TRUE) {
		length = width * height;
		addr = GXJ_malloc(length);
		(*env)->SetIntField(env, imageData, imageData_nativeAlphaData_Id, (int)addr);
	}
}

JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData_freeNativeData
  (JNIEnv *env, jobject imageData) {

	jbyte *pixelData = (jbyte *) (*env)->GetIntField(env, imageData, imageData_nativePixelData_Id);
	jbyte *alphaData = (jbyte *) (*env)->GetIntField(env, imageData, imageData_nativeAlphaData_Id);

	if (pixelData != NULL) {
		GXJ_free(pixelData);
	}
	if (alphaData != NULL) {
		GXJ_free(alphaData);
	}
}

JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData_fillNativePixelData
  (JNIEnv *env, jobject dstImageData, jobject srcImageData) {

	jint width = (*env)->GetIntField(env, dstImageData, imageData_width_Id);
	jint height = (*env)->GetIntField(env, dstImageData, imageData_height_Id);
	int length = width * height * 2;

	jbyte *dstData = (jbyte *) (*env)->GetIntField(env, dstImageData, imageData_nativePixelData_Id);
	jbyte *srcData = (jbyte *) (*env)->GetIntField(env, srcImageData, imageData_nativePixelData_Id);
	memcpy(dstData, srcData, length);
}

JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData_clearNativePixelData
  (JNIEnv *env, jobject imageData) {

	jint width = (*env)->GetIntField(env, imageData, imageData_width_Id);
	jint height = (*env)->GetIntField(env, imageData, imageData_height_Id);
	int length = width * height * 2;

	jbyte *data = (jbyte *) (*env)->GetIntField(env, imageData, imageData_nativePixelData_Id);
	memset(data, 0xFF, length);
}

/**
 * Gets an ARGB integer array from this <tt>ImmutableImage</tt>. The
 * array consists of values in the form of 0xAARRGGBB.
 * <p>
 * Java declaration:
 * <pre>
 *     getRGB([IIIIIII)V
 * </pre>
 *
 * @param rgbData The target integer array for the ARGB data
 * @param offset Zero-based index of first ARGB pixel to be saved
 * @param scanlen Number of intervening pixels between pixels in
 *                the same column but in adjacent rows
 * @param x The x coordinate of the upper left corner of the
 *          selected region
 * @param y The y coordinate of the upper left corner of the
 *          selected region
 * @param width The width of the selected region
 * @param height The height of the selected region
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData_getRGB
  (JNIEnv *env, jobject imageData, jintArray argbIntArray, jint offset, jint scanlen, jint x, jint y, jint width, jint height) {
    img_native_error_codes error = IMG_NATIVE_IMAGE_NO_ERROR;

    gxj_screen_buffer screen_buffer;
    gxj_screen_buffer *sbuf = getScreenBufferFromImageData(env, imageData, &screen_buffer);

    jint *rgbBuffer = (*env)->GetIntArrayElements(env, argbIntArray, NULL);

    imgj_get_argb(sbuf, rgbBuffer, offset, scanlen, x, y, width, height, &error);

    (*env)->ReleaseIntArrayElements(env, argbIntArray, rgbBuffer, 0);
    releaseScreenBufferPrimitiveArrays(env, sbuf);

    if (error != IMG_NATIVE_IMAGE_NO_ERROR) {
        GXJ_ThrowException(env, "java/lang/OutOfMemoryError", "");
    }
}
