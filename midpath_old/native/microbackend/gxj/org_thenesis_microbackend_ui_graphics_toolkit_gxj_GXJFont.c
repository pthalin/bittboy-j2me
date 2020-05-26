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

#include <stdlib.h>

//#include <sni.h>
//#include <commonKNIMacros.h>
//#include <midpError.h>

#include <jni.h>

#include "gxj_util.h"
#include "gx_font.h"
#include "gxapi_constants.h"
#include "gxapi_intern_graphics.h"

#include "org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJFont.h"

/**
 * @file
 *
 * Implementation of Java native methods for the <tt>Font</tt> class.
 */

static jfieldID faceId;
static jfieldID styleId;
static jfieldID sizeId;

void GXJFont_getFontType(JNIEnv *env, jobject font, jint* face, jint* style, jint* size) {
    face[0] = (*env)->GetIntField(env, font, faceId);
    style[0] = (*env)->GetIntField(env, font, styleId);
    size[0] = (*env)->GetIntField(env, font, sizeId);
}

/**
 * Initializes the native peer of this <tt>Font</tt>.
 * <p>
 * Java declaration:
 * <pre>
 *     init(III)V
 * </pre>
 *
 * @param face The face of the font to initialize
 * @param style The style of the font to initialize
 * @param size The point size of the font to initialize
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJFont_init(JNIEnv *env, jobject obj, jint face, jint style, jint size) {
    int ascent, descent, leading;

    /* Get a reference to obj's class */
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID baseLineId = (*env)->GetFieldID(env, cls, "baseline", "I");
    jfieldID heightId = (*env)->GetFieldID(env, cls, "height", "I");
    faceId = (*env)->GetFieldID(env, cls, "face", "I");
    styleId = (*env)->GetFieldID(env, cls, "style", "I");
    sizeId = (*env)->GetFieldID(env, cls, "size", "I");

    gx_get_fontinfo(face, style, size, &ascent, &descent, &leading);

    (*env)->SetIntField(env, obj, baseLineId, (jint)ascent);
    (*env)->SetIntField(env, obj, heightId, (jint)(ascent + descent + leading));
}

/**
 * Gets the advance width of the specified character using this
 * <tt>Font</tt>.
 * <p>
 * Java declaration:
 * <pre>
 *     charWidth(C)I
 * </pre>
 *
 * @param ch the character to be measured
 *
 * @return the total advance width in pixels (a non-negative value)
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJFont_charWidth(JNIEnv *env, jobject font, jchar c) {
    jint face, style, size;
    GXJFont_getFontType(env, font, &face, &style, &size);
    return gx_get_charswidth(face, style, size, &c, 1);
}

/**
 * Gets the combined advance width of multiple characters, starting at
 * the specified offset and for the specified number of characters using
 * this <tt>Font</tt>.
 * <p>
 * Java declaration:
 * <pre>
 *     charsWidth([CII)I
 * </pre>
 *
 * @param ch the array of characters to be measured
 * @param offset the index of the first character to measure
 * @param length the number of characters to measure
 *
 * @return the total width of the character range in pixels
 */
//KNIEXPORT KNI_RETURNTYPE_INT
//KNIDECL(javax_microedition_lcdui_Font_charsWidth) {
JNIEXPORT jint JNICALL  Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJFont_charsWidth(JNIEnv *env, jobject font, jcharArray ch,
        jint offset, jint length) {

    int chLen;
    jint result = 0;

    if ((chLen = (*env)->GetArrayLength(env, ch)) == -1) {
        GXJ_ThrowException(env, "java/lang/NullPointerException", "Input char array is null");
    } else if ((offset < 0) || (offset > chLen) || (length < 0) || (length > chLen) || ((offset + length) < 0)
            || ((offset + length) > chLen)) {
        GXJ_ThrowException(env, "java/lang/IndexOutOfBoundsException", "");
    } else if (length != 0) {
        jint face, style, size;
        GXJFont_getFontType(env, font, &face, &style, &size);
        jchar *carr = (*env)->GetCharArrayElements(env, ch, NULL);
        result = gx_get_charswidth(face, style, size, &carr[offset], length);
    }

    return result;
}

///**
// * Gets the total advance width of the given <tt>String</tt> in this
// * <tt>Font</tt>.
// * <p>
// * Java declaration:
// * <pre>
// *     stringWidth(Ljava/lang/String;)I
// * </pre>
// *
// * @param str the <tt>String</tt> to be measured
// *
// * @return the total advance width of the <tt>String</tt> in pixels
// */
//KNIEXPORT KNI_RETURNTYPE_INT
//KNIDECL(javax_microedition_lcdui_Font_stringWidth) {
//    int strLen;
//    jint result = 0;
//
//    KNI_StartHandles(2);
//
//    KNI_DeclareHandle(str);
//    KNI_DeclareHandle(thisObject);
//
//    KNI_GetParameterAsObject(1, str);
//    KNI_GetParameterAsObject(0, thisObject);
//
//    if ((strLen = KNI_GetStringLength(str)) == -1) {
//        KNI_ThrowNew(midpNullPointerException, NULL);
//    } else {
//        int      face, style, size;
//        _JavaString *jstr;
//
//        DECLARE_FONT_PARAMS(thisObject);
//
//        SNI_BEGIN_RAW_POINTERS;
//
//        jstr = GET_STRING_PTR(str);
//
//        result = gx_get_charswidth(face, style, size,
//				   jstr->value->elements + jstr->offset,
//				   strLen);
//
//        SNI_END_RAW_POINTERS;
//    }
//
//    KNI_EndHandles();
//    KNI_ReturnInt(result);
//}

///**
// * Gets the total advance width of a portion of the given <tt>String<tt>
// * in this <tt>Font</tt>.
// * <p>
// * Java declaration:
// * <pre>
// *     substringWidth(Ljava/lang/String;II)I
// * </pre>
// *
// * @param str a <tt>String</tt> to be measured
// * @param offset the index of the first character of the substring to measure
// * @param length the number of characters in the substring to measure
// *
// * @return the total advance width of the substring in pixels
// */
//KNIEXPORT KNI_RETURNTYPE_INT
//KNIDECL(javax_microedition_lcdui_Font_substringWidth) {
//    int length = KNI_GetParameterAsInt(3);
//    int offset = KNI_GetParameterAsInt(2);
//    int strLen;
//    jint result = 0;
//
//    KNI_StartHandles(2);
//
//    KNI_DeclareHandle(str);
//    KNI_DeclareHandle(thisObject);
//
//    KNI_GetParameterAsObject(1, str);
//    KNI_GetParameterAsObject(0, thisObject);
//
//    if ((strLen = KNI_GetStringLength(str)) == -1) {
//        KNI_ThrowNew(midpNullPointerException, NULL);
//    } else if ( (offset < 0)
//		|| (offset > strLen)
//		|| (length < 0)
//		|| (length > strLen)
//		|| ((offset + length) < 0)
//		|| ((offset + length) > strLen)) {
//	KNI_ThrowNew(midpStringIndexOutOfBoundsException, NULL);
//    } else if (length != 0) {
//        int      face, style, size;
//        _JavaString *jstr;
//
//        DECLARE_FONT_PARAMS(thisObject);
//
//        SNI_BEGIN_RAW_POINTERS;
//
//        jstr = GET_STRING_PTR(str);
//
//        result = gx_get_charswidth(face, style, size,
//				   jstr->value->elements +
//				   (jstr->offset + offset),
//				   length);
//
//        SNI_END_RAW_POINTERS;
//    }
//
//    KNI_EndHandles();
//    KNI_ReturnInt(result);
//}
