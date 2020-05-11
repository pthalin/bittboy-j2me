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

//#include <midpError.h>
//#include <midpEventUtil.h>

#include <jni.h>

#include "gx_graphics.h"
#include "gxapi_constants.h"
#include "gxapi_intern_graphics.h"
#include "gxj_putpixel.h"
#include "gxj_util.h"

#include "org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJFont.h"
#include "org_thenesis_microbackend_ui_graphics_toolkit_gxj_ImageData.h"

#ifdef UNDER_CE
#include "gxj_intern_graphics.h"
#include "gxj_intern_putpixel.h"
#include "gxj_intern_image.h"
extern void fast_rect_8x8(void*first_pixel, int ypitch, int pixel);
#endif

/**
 * @file
 * Implementation of Java native methods for the <tt>Graphics</tt> class.
 */

static jfieldID maxWidth_Id;
static jfieldID maxHeight_Id;
static jfieldID transX_Id;
static jfieldID transY_Id;
static jfieldID clipX1_Id;
static jfieldID clipY1_Id;
static jfieldID clipX2_Id;
static jfieldID clipY2_Id;
static jfieldID pixel_Id;
static jfieldID style_Id;
static jfieldID img_Id;
static jfieldID imageData_Id;
static jfieldID curentFont_Id;

JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_initFieldIDs(JNIEnv *env, jclass cls) {
    maxWidth_Id = (*env)->GetFieldID(env, cls, "maxWidth", "S");
    maxHeight_Id = (*env)->GetFieldID(env, cls, "maxHeight", "S");
    transX_Id = (*env)->GetFieldID(env, cls, "transX", "I");
    transY_Id = (*env)->GetFieldID(env, cls, "transY", "I");
    clipX1_Id = (*env)->GetFieldID(env, cls, "clipX1", "S");
    clipY1_Id = (*env)->GetFieldID(env, cls, "clipY1", "S");
    clipX2_Id = (*env)->GetFieldID(env, cls, "clipX2", "S");
    clipY2_Id = (*env)->GetFieldID(env, cls, "clipY2", "S");
    pixel_Id = (*env)->GetFieldID(env, cls, "pixel", "I");
    style_Id = (*env)->GetFieldID(env, cls, "style", "I");
    img_Id = (*env)->GetFieldID(env, cls, "img", "Lorg/thenesis/microbackend/ui/graphics/toolkit/gxj/GXJImage;");
    jclass imageClass = (*env)->FindClass(env, "org/thenesis/microbackend/ui/graphics/toolkit/gxj/GXJImage");
    imageData_Id = (*env)->GetFieldID(env, imageClass, "imageData", "Lorg/thenesis/microbackend/ui/graphics/toolkit/gxj/ImageData;");
    curentFont_Id = (*env)->GetFieldID(env, cls, "currentFont", "Lorg/thenesis/microbackend/ui/graphics/toolkit/gxj/GXJFont;");
}

static inline void translate(JNIEnv *env, jobject graphics, jint *x, jint *y) {
    x[0] = x[0] + (*env)->GetIntField(env, graphics, transX_Id);
    y[0] = y[0] + (*env)->GetIntField(env, graphics, transY_Id);
}

static inline void getClip(JNIEnv *env, jobject graphics, jshort *clip) {
    clip[0] = (*env)->GetIntField(env, graphics, clipX1_Id);
    clip[1] = (*env)->GetIntField(env, graphics, clipY1_Id);
    clip[2] = (*env)->GetIntField(env, graphics, clipX2_Id);
    clip[3] = (*env)->GetIntField(env, graphics, clipY2_Id);
}

static inline int getPixel(JNIEnv *env, jobject graphics) {
    return (*env)->GetIntField(env, graphics, pixel_Id);
}

static inline int getLineStyle(JNIEnv *env, jobject graphics) {
    return (*env)->GetIntField(env, graphics, style_Id);
}

static inline gxj_screen_buffer* getScreenBufferFromImage(JNIEnv *env, jobject img, gxj_screen_buffer *sbuf) {
    jobject imageData = (*env)->GetObjectField(env, img, imageData_Id);
    return getScreenBufferFromImageData(env, imageData, sbuf);
}

static inline gxj_screen_buffer* getScreenBufferFromGraphics(JNIEnv *env, jobject graphics, gxj_screen_buffer *sbuf) {
    jobject img = (*env)->GetObjectField(env, graphics, img_Id);
    if (img == NULL) {
        return NULL;
    }
    return getScreenBufferFromImage(env, img, sbuf);
}

/**
 * Draws a straight line between the given coordinates using the
 * current color, stroke style, and clipping data.
 * <p>
 * Java declaration:
 * <pre>
 *     drawLine(IIII)V
 * </pre>
 *
 * @param x1 The x coordinate of the start of the line
 * @param y1 The y coordinate of the start of the line
 * @param x2 The x coordinate of the end of the line
 * @param y2 The y coordinate of the end of the line
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_drawLine
  (JNIEnv *env, jobject obj, jint x1, jint y1, jint x2, jint y2) {

        jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

        translate(env, obj, &x1, &y1);
        translate(env, obj, &x2, &y2);

        getClip(env, obj, &clip[0]);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, obj, &screen_buffer);

        gx_draw_line(getPixel(env, obj),
                     clip,
                     sbuf,
                     getLineStyle(env, obj), x1, y1, x2, y2);

        releaseScreenBufferPrimitiveArrays(env, sbuf);
}

/**
 * Draws the outline of the specified rectangle using the current
 * color and stroke style. According to the MIDP specification,
 * if either the <tt>width</tt> or <tt>height</tt> are negative,
 * no rectangle is drawn.
 * <p>
 * Java declaration:
 * <pre>
 *     drawRect(IIII)V
 * </pre>
 *
 * @param x The x coordinate of the rectangle to be drawn
 * @param y The y coordinate of the rectangle to be drawn
 * @param width The width of the rectangle to be drawn
 * @param height The height of the rectangle to be drawn
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_drawRect
  (JNIEnv *env, jobject graphics, jint x, jint y, jint w, jint h) {

    /*
     * @note { Spec verify step: "If either width or height
     * is less than zero, nothing is drawn." }
     */
    if ((w >= 0) && (h >= 0)) {
            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

            translate(env, graphics, &x, &y);

            getClip(env, graphics, clip);

            gxj_screen_buffer screen_buffer;
            gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

            gx_draw_rect(getPixel(env, graphics),
                         clip,
                         sbuf,
                         getLineStyle(env, graphics),
                         x, y, w, h);

            releaseScreenBufferPrimitiveArrays(env, sbuf);
    }

}

/**
 * Fills the specified rectangle using the current color and
 * stroke style. According to the MIDP specification, if either
 * the <tt>width</tt> or <tt>height</tt> are negative, no
 * rectangle is drawn.
 * <p>
 * Java declaration:
 * <pre>
 *     fillRect(IIII)V
 * </pre>
 *
 * @param x The x coordinate of the rectangle to be drawn
 * @param y The y coordinate of the rectangle to be drawn
 * @param width The width of the rectangle to be drawn
 * @param height The height of the rectangle to be drawn
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_fillRect
  (JNIEnv *env, jobject graphics, jint x, jint y, jint w, jint h) {

    /*
     * @note { Spec verify step: "If either width or height
     * is zero or less, nothing is drawn." }
     */
    if ((w >= 0) && (h >= 0)) {
        jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

        translate(env, graphics, &x, &y);

        getClip(env, graphics, clip);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

        gx_fill_rect(getPixel(env, graphics),
                                 clip,
                                 sbuf,
                                 getLineStyle(env, graphics),
                                 x, y, w, h);

        releaseScreenBufferPrimitiveArrays(env, sbuf);
    }
}

/**
 * Draws the outline of the specified rectangle, with rounded corners,
 * using the current color and stroke style. According to the MIDP
 * specification, if either the <tt>width</tt> or <tt>height</tt>
 * are negative, no rectangle is drawn.
 * <p>
 * Java declaration:
 * <pre>
 *     drawRoundRect(IIIIII)V
 * </pre>
 *
 * @param x The x coordinate of the rectangle to be drawn
 * @param y The y coordinate of the rectangle to be drawn
 * @param width The width of the rectangle to be drawn
 * @param height The height of the rectangle to be drawn
 * @param arcWidth The horizontal diameter of the arc at the four corners
 * @param arcHeight The vertical diameter of the arc at the four corners
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_drawRoundRect
  (JNIEnv *env, jobject graphics, jint x, jint y, jint w, jint h, jint arcWidth, jint arcHeight) {

    /*
     * @note { Spec verify step: "If either width or height
     * is less than zero, nothing is drawn." }
     */
    if ((w >= 0) && (h >= 0)) {
        jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

        translate(env, graphics, &x, &y);

        getClip(env, graphics, clip);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

        gx_draw_roundrect(getPixel(env, graphics), clip, sbuf, getLineStyle(env, graphics), x, y, w, h, arcWidth, arcHeight);

        releaseScreenBufferPrimitiveArrays(env, sbuf);
    }
}

/**
 * Fills the specified rectangle, with rounded corners, using
 * the current color and stroke style. According to the MIDP
 * specification, if either the <tt>width</tt> or <tt>height</tt>
 * are negative, no rectangle is drawn.
 * <p>
 * Java declaration:
 * <pre>
 *     fillRoundRect(IIIIII)V
 * </pre>
 *
 * @param x The x coordinate of the rectangle to be drawn
 * @param y The y coordinate of the rectangle to be drawn
 * @param width The width of the rectangle to be drawn
 * @param height The height of the rectangle to be drawn
 * @param arcWidth The horizontal diameter of the arc at the four corners
 * @param arcHeight The vertical diameter of the arc at the four corners
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_fillRoundRect
(JNIEnv *env, jobject graphics, jint x, jint y, jint w, jint h, jint arcWidth, jint arcHeight) {

    /*
     * @note { Spec verify step: "If either width or height
     * is zero or less, nothing is drawn." }
     */
    if ((w >= 0) && (h >= 0)) {
        jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

        translate(env, graphics, &x, &y);

        getClip(env, graphics, clip);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

        gx_fill_roundrect(getPixel(env, graphics), clip, sbuf, getLineStyle(env, graphics), x, y, w, h, arcWidth, arcHeight);

        releaseScreenBufferPrimitiveArrays(env, sbuf);
    }
}

/**
 * Draws the outline of the specified circular or elliptical arc
 * segment using the current color and stroke style. According
 * to the MIDP specification, if either the <tt>width</tt> or
 * <tt>height</tt> are negative, no arc is drawn.
 * <p>
 * Java declaration:
 * <pre>
 *     drawArc(IIIIII)V
 * </pre>
 *
 * @param x The x coordinate of the upper-left corner of the arc
 *          to be drawn
 * @param y The y coordinate of the upper-left corner of the arc
 *          to be drawn
 * @param width The width of the arc to be drawn
 * @param height The height of the arc to be drawn
 * @param startAngle The beginning angle
 * @param arcAngle The angular extent of the arc, relative to
 *                 <tt>startAngle</tt>
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_drawArc
  (JNIEnv *env, jobject graphics, jint x, jint y, jint w, jint h, jint startAngle, jint arcAngle) {
    /*
     * @note { Spec verify step: "If either width or height
     * is less than zero, nothing is drawn." }
     */
    if ((w >= 0) && (h >= 0)) {

            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

            translate(env, graphics, &x, &y);

#ifdef PLATFORM_SUPPORT_CCW_ARC_ONLY
            /* this block transfer any negative number of
             * start angle or arc angle to positive and
             * always counter-clockwise.
             *
             * Optimization: This whole block can skip if
             * native platform support negative arc input.
             */
            if (arcAngle < 0) {
                startAngle += arcAngle;
                arcAngle = -arcAngle;
            }
            startAngle = (startAngle + 360) % 360;
#endif

        getClip(env, graphics, clip);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

        gx_draw_arc(getPixel(env, graphics), clip, sbuf, getLineStyle(env, graphics), x, y, w, h, startAngle, arcAngle);

        releaseScreenBufferPrimitiveArrays(env, sbuf);
    }
}

/**
 * Fills the specified circular or elliptical arc segment using the
 * current color and stroke style. According to the MIDP specification,
 * if either the <tt>width</tt> or <tt>height</tt> are negative, no
 * arc is drawn.
 * <p>
 * Java declaration:
 * <pre>
 *     fillArc(IIIIII)V
 * </pre>
 *
 * @param x The x coordinate of the upper-left corner of the arc
 *          to be drawn
 * @param y The y coordinate of the upper-left corner of the arc
 *          to be drawn
 * @param width The width of the arc to be drawn
 * @param height The height of the arc to be drawn
 * @param startAngle The beginning angle
 * @param arcAngle The angular extent of the arc, relative to
 *                 <tt>startAngle</tt>
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_fillArc
  (JNIEnv *env, jobject graphics, jint x, jint y, jint w, jint h, jint startAngle, jint arcAngle) {
    /*
     * @note { Spec verify step: "If either width or height
     * is less than zero, nothing is drawn." }
     */
    if ((w >= 0) && (h >= 0)) {

            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

            translate(env, graphics, &x, &y);

#ifdef PLATFORM_SUPPORT_CCW_ARC_ONLY
            /* this block transfer any negative number of
             * start angle or arc angle to positive and
             * always counter-clockwise.
             *
             * Optimization: This whole block can skip if
             * native platform support negative arc input.
             */
            if (arcAngle < 0) {
                startAngle += arcAngle;
                arcAngle = -arcAngle;
            }
            startAngle = (startAngle + 360) % 360;
#endif

        getClip(env, graphics, clip);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

        gx_fill_arc(getPixel(env, graphics), clip, sbuf, getLineStyle(env, graphics), x, y, w, h, startAngle, arcAngle);

        releaseScreenBufferPrimitiveArrays(env, sbuf);
    }
}

/**
 * Fills the specified triangle using the current color and stroke
 * style.
 * <p>
 * Java declaration:
 * <pre>
 *     fillTriangle(IIIIII)V
 * </pre>
 *
 * @param x1 The x coordinate of the first vertices
 * @param y1 The y coordinate of the first vertices
 * @param x2 The x coordinate of the second vertices
 * @param y2 The y coordinate of the second vertices
 * @param x3 The x coordinate of the third vertices
 * @param y3 The y coordinate of the third vertices
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_fillTriangle
(JNIEnv *env, jobject graphics, jint x1, jint y1, jint x2, jint y2, jint x3, jint y3) {

    /*
     * @note { Spec verify step: "If either width or height
     * is zero or less, nothing is drawn." }
     */
        jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

        translate(env, graphics, &x1, &y1);
        translate(env, graphics, &x2, &y2);
        translate(env, graphics, &x3, &y3);

        getClip(env, graphics, clip);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

        gx_fill_triangle(getPixel(env, graphics), clip, sbuf, getLineStyle(env, graphics), x1, y1, x2, y2, x3, y3);

        releaseScreenBufferPrimitiveArrays(env, sbuf);

}

///**
// * Draws the specified <tt>String</tt> using the current font and color.
// * <p>
// * Java declaration:
// * <pre>
// *     drawString(Ljava/lang/String;III)V
// * </pre>
// *
// * @param str The <tt>String</tt> to be drawn
// * @param x The x coordinate of the anchor point
// * @param y The y coordinate of the anchor point
// * @param anchor The anchor point for positioning the text
// */
//KNIEXPORT KNI_RETURNTYPE_VOID
//KNIDECL(javax_microedition_lcdui_Graphics_drawString) {
//    int anchor = KNI_GetParameterAsInt(4);
//    int      y = KNI_GetParameterAsInt(3);
//    int      x = KNI_GetParameterAsInt(2);
//    int strLen;
//
//    KNI_StartHandles(3);
//
//    KNI_DeclareHandle(str);
//    KNI_DeclareHandle(thisObject);
//    KNI_DeclareHandle(font);
//
//    KNI_GetParameterAsObject(1, str);
//    KNI_GetThisPointer(thisObject);
//
//    if (GRAPHICS_OP_IS_ALLOWED(thisObject)) {
//        strLen = KNI_GetStringLength(str);
//        if (strLen < 0) {
//            KNI_ThrowNew(midpNullPointerException, NULL);
//        } else if (!check_anchor(anchor, VCENTER)) {
//            KNI_ThrowNew(midpIllegalArgumentException, NULL);
//        } else {
//            int      face, style, size;
//            _JavaString *jstr;
//            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */
//
//            GET_FONT(thisObject, font);
//
//            DECLARE_FONT_PARAMS(font);
//
//            TRANSLATE(thisObject, x, y);
//
//            jstr = GET_STRING_PTR(str);
//
//            GET_CLIP(thisObject, clip);
//
//            gx_draw_chars(GET_PIXEL(thisObject),
//                          clip,
//                          GET_IMAGEDATA_PTR_FROM_GRAPHICS(thisObject),
//                          GET_LINESTYLE(thisObject),
//                          face, style, size, x, y, anchor,
//                          jstr->value->elements + jstr->offset,
//                          strLen);
//        }
//    }
//
//    KNI_EndHandles();
//    KNI_ReturnVoid();
//}
//
///**
// * Draws a portion of the specified <tt>String</tt> using the
// * current font and color.
// * <p>
// * Java declaration:
// * <pre>
// *     drawSubstring(Ljava/lang/String;III)V
// * </pre>
// *
// * @param str The <tt>String</tt> to be drawn
// * @param offset Zero-based index of first character in the substring
// * @param length Length of the substring
// * @param x The x coordinate of the anchor point
// * @param y The y coordinate of the anchor point
// * @param anchor The anchor point for positioning the text
// */
//KNIEXPORT KNI_RETURNTYPE_VOID
//KNIDECL(javax_microedition_lcdui_Graphics_drawSubstring) {
//    int anchor = KNI_GetParameterAsInt(6);
//    int      y = KNI_GetParameterAsInt(5);
//    int      x = KNI_GetParameterAsInt(4);
//    int length = KNI_GetParameterAsInt(3);
//    int offset = KNI_GetParameterAsInt(2);
//    int strLen;
//
//    KNI_StartHandles(3);
//
//    KNI_DeclareHandle(str);
//    KNI_DeclareHandle(thisObject);
//    KNI_DeclareHandle(font);
//
//    KNI_GetParameterAsObject(1, str);
//    KNI_GetThisPointer(thisObject);
//
//    if (GRAPHICS_OP_IS_ALLOWED(thisObject)) {
//        strLen = KNI_GetStringLength(str);
//        if (strLen < 0) {
//            KNI_ThrowNew(midpNullPointerException, NULL);
//        } else if (   (offset < 0)
//                      || (offset > strLen)
//                      || (length < 0)
//                      || (length > strLen)
//                      || ((offset + length) < 0)
//                      || ((offset + length) > strLen)) {
//            KNI_ThrowNew(midpStringIndexOutOfBoundsException, NULL);
//        } else if (!check_anchor(anchor, VCENTER)) {
//            KNI_ThrowNew(midpIllegalArgumentException, NULL);
//        } else if (length != 0) {
//            int      face, style, size;
//            _JavaString *jstr;
//
//            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */
//
//            GET_FONT(thisObject, font);
//
//            DECLARE_FONT_PARAMS(font);
//
//            TRANSLATE(thisObject, x, y);
//
//            jstr = GET_STRING_PTR(str);
//
//            GET_CLIP(thisObject, clip);
//
//            gx_draw_chars(GET_PIXEL(thisObject),
//                          clip,
//                          GET_IMAGEDATA_PTR_FROM_GRAPHICS(thisObject),
//                          GET_LINESTYLE(thisObject),
//                          face, style, size, x, y, anchor,
//                          jstr->value->elements + (jstr->offset + offset),
//                          length);
//        }
//    }
//
//    KNI_EndHandles();
//    KNI_ReturnVoid();
//}
//
///**
// * Draws the specified character using the current font and color.
// * <p>
// * Java declaration:
// * <pre>
// *     drawChar(CIII)V
// * </pre>
// *
// * @param ch The character to be drawn
// * @param x The x coordinate of the anchor point
// * @param y The y coordinate of the anchor point
// * @param anchor The anchor point for positioning the text
// */
//KNIEXPORT KNI_RETURNTYPE_VOID
//KNIDECL(javax_microedition_lcdui_Graphics_drawChar) {
//    int anchor = KNI_GetParameterAsInt(4);
//    int      y = KNI_GetParameterAsInt(3);
//    int      x = KNI_GetParameterAsInt(2);
//    jchar  c = KNI_GetParameterAsShort(1);
//
//    KNI_StartHandles(2);
//    KNI_DeclareHandle(thisObject);
//    KNI_DeclareHandle(font);
//
//    KNI_GetThisPointer(thisObject);
//
//    if (GRAPHICS_OP_IS_ALLOWED(thisObject)) {
//        if (!check_anchor(anchor, VCENTER)) {
//            KNI_ThrowNew(midpIllegalArgumentException, NULL);
//        } else {
//            int      face, style, size;
//            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */
//
//            GET_FONT(thisObject, font);
//
//            DECLARE_FONT_PARAMS(font);
//
//            TRANSLATE(thisObject, x, y);
//
//            GET_CLIP(thisObject, clip);
//
//            gx_draw_chars(GET_PIXEL(thisObject),
//                          clip,
//                          GET_IMAGEDATA_PTR_FROM_GRAPHICS(thisObject),
//                          GET_LINESTYLE(thisObject),
//                          face, style, size, x, y, anchor, &c, 1);
//        }
//    }
//
//    KNI_EndHandles();
//    KNI_ReturnVoid();
//}

/**
 * Draws the specified characters using the current font and color.
 * <p>
 * Java declaration:
 * <pre>
 *     drawChars([CIIIII)V
 * </pre>
 *
 * @param data The array of characters to be drawn
 * @param offset Zero-based index of first character to be drawn
 * @param length Number of characters to be drawn
 * @param x The x coordinate of the anchor point
 * @param y The y coordinate of the anchor point
 * @param anchor The anchor point for positioning the text
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_drawChars
  (JNIEnv *env, jobject graphics, jcharArray ch, jint offset, jint length, jint x, jint y, jint anchor) {
    int chLen = (*env)->GetArrayLength(env, ch);
    if (chLen < 0) {
        GXJ_ThrowException(env, "java/lang/NullPointerException", "Input char array is null");
    } else if ((offset < 0) || (offset > chLen) || (length < 0) || (length > chLen) || ((offset + length) < 0) || ((offset + length)
            > chLen)) {
        GXJ_ThrowException(env, "java/lang/IndexOutOfBoundsException", "");
    } else if (!check_anchor(anchor, VCENTER)) {
        GXJ_ThrowException(env, "java/lang/IllegalArgumentException", "");
    } else if (length != 0) {
        int face, style, size;
        jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

        jobject font = (*env)->GetObjectField(env, graphics, curentFont_Id);
        GXJFont_getFontType(env, font, &face, &style, &size);

        translate(env, graphics, &x, &y);

        getClip(env, graphics, clip);

        gxj_screen_buffer screen_buffer;
        gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);
        jchar *charBuffer = (*env)->GetCharArrayElements(env, ch, NULL);

        gx_draw_chars(getPixel(env, graphics), clip, sbuf, getLineStyle(env, graphics), face, style, size, x, y, anchor, &charBuffer[offset], length);

        (*env)->ReleaseCharArrayElements(env, ch, charBuffer, 0);
        releaseScreenBufferPrimitiveArrays(env, sbuf);
    }

}

/**
 * Draws the specified pixels from the given data array. The array
 * consists of values in the form of 0xAARRGGBB.
 * <p>
 * Java declaration:
 * <pre>
 *     drawRGB([IIIIIIIZ)V
 * </pre>
 *
 * @param rgbData The array of argb pixels to draw
 * @param offset Zero-based index of first argb pixel to be drawn
 * @param scanlen Number of intervening pixels between pixels in
 *                the same column but in adjacent rows
 * @param x The x coordinate of the upper left corner of the
 *          region to draw
 * @param y The y coordinate of the upper left corner of the
 *          region to draw
 * @param width The width of the target region
 * @param height The height of the target region
 * @param processAlpha If <tt>true</tt>, alpha channel bytes will
 *                     be used, otherwise, alpha channel bytes will
 *                     be ignored
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_drawRGB
  (JNIEnv *env, jobject graphics, jintArray argbIntArray, jint offset, jint scanlen, jint x, jint y, jint width, jint height, jboolean processAlpha) {

    if (argbIntArray == NULL) {
        GXJ_ThrowException(env, "java/lang/NullPointerException", "Input int array is null");
    } else {
    	long min, max, l_scanlen, l_height, l_tmpexp;
        int buflen = (*env)->GetArrayLength(env, argbIntArray);

        /* According to the spec., this function can be
         * defined as operation P(a,b) = rgbData[ offset +
         * (a-x) + (b-y)* scanlength] where x <= a < x + width
         * AND y <= b < y + height.
         *
         * We do not need to check every index value and its
         * corresponding array access violation. We only need
         * to check for the min/max case. Detail explanation
         * can be found in the design doc.
         *
         * - To translate "<" to "<=", we minus one from height
         * and width (the ceiling operation), for all cases
         * except when height or width is zero.
         * - To avoid overflow (or underflow), we cast the
         * variables scanlen and height to long first */

        l_scanlen = (long) scanlen;
        l_height = (long) height - 1;
        l_tmpexp = (height == 0) ? 0 : l_height * l_scanlen;

        /* Find the max/min of the index for rgbData array */
        max = offset + ((width == 0) ? 0 : (width - 1)) + ((scanlen < 0) ? 0 : l_tmpexp);
        min = offset + ((scanlen < 0) ? l_tmpexp : 0);

        if ((max >= buflen) || (min < 0) || (max < 0) || (min >= buflen)) {
            GXJ_ThrowException(env, "java/lang/IndexOutOfBoundsException", "");
        } else {
            if ((0 == scanlen || 0 == width || 0 == height)) {
                /* Valid values, but nothing to render. */
            } else {
                jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

                translate(env, graphics, &x, &y);

                getClip(env, graphics, clip);

                gxj_screen_buffer screen_buffer;
                gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);
                jint *rgbBuffer = (*env)->GetIntArrayElements(env, argbIntArray, NULL);

                //getPixel(env, obj), clip, sbuf, getLineStyle(env, obj), face, style, size, x, y, anchor, &charBuffer[offset], length
                gx_draw_rgb(clip, sbuf, rgbBuffer, offset, scanlen, x, y, width, height, processAlpha);

                (*env)->ReleaseIntArrayElements(env, argbIntArray, rgbBuffer, 0);
                releaseScreenBufferPrimitiveArrays(env, sbuf);
            }
        }
    }

}

/**
 * Copies the specified region of the current <tt>Graphics</tt> object
 * to the specified destination within the same <tt>Graphics</tt> object.
 * <p>
 * Java declaration:
 * <pre>
 *     doCopyArea(IIIIII)V
 * </pre>
 *
 * @param x_src The x coordinate of the upper-left corner of the
 *              source region
 * @param y_src The y coordinate of the upper-left corner of the
 *              source region
 * @param width The width of the source region
 * @param height The height of the source region
 * @param x_dest The x coordinate of the upper-left corner of the
 *               destination region
 * @param y_dest The y coordinate of the upper-left corner of the
 *               destination region
 * @param anchor The anchor point for positioning the copied region
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_doCopyArea
  (JNIEnv *env, jobject graphics, jint x_src, jint y_src, jint width, jint height, jint x_dest, jint y_dest, jint anchor) {
    jshort gfx_width = 0;
    jshort gfx_height = 0;

    gfx_width = (*env)->GetShortField(env, graphics, maxWidth_Id);
    gfx_height = (*env)->GetShortField(env, graphics, maxHeight_Id);

    translate(env, graphics, &x_src, &y_src);

    if ((height < 0) || (width < 0) || (x_src < 0) || (y_src < 0) || ((x_src + width) > gfx_width) || ((y_src + height) > gfx_height)) {
        GXJ_ThrowException(env, "java/lang/IllegalArgumentException", "");
    } else {
        translate(env, graphics, &x_dest, &y_dest);

        if (normalize_anchor(&x_dest, &y_dest, width, height, anchor)) {
            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */
            getClip(env, graphics, clip);
            gxj_screen_buffer screen_buffer;
            gxj_screen_buffer *sbuf = getScreenBufferFromGraphics(env, graphics, &screen_buffer);

            gx_copy_area(clip, sbuf, x_src, y_src, width, height, x_dest, y_dest);

            releaseScreenBufferPrimitiveArrays(env, sbuf);
        }
    }

}

/**
 * Gets a specific pixel value.
 * <p>
 * Java declaration:
 * <pre>
 *     getPixel(IIZ)I
 * </pre>
 *
 * @param rgb compact rgb representation
 * @param gray gray scale
 * @param isGray use gray scale
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_getPixel
  (JNIEnv *env, jobject graphics, jint rgb, jint gray, jboolean isGray) {
    return gx_get_pixel(rgb, gray, isGray);
}

/**
 * Maps the specified RGB value to the actual RGB value displayed
 * on device.
 * <p>
 * Java declaration:
 * <pre>
 *     getDisplayColor(I)I
 * </pre>
 *
 * @param color The RGB value to get the display mapping
 * @return The RGB value used to display this color on device
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_getDisplayColor
  (JNIEnv *env, jobject graphics, jint color) {
    return gx_get_displaycolor(color);
}

/**
 * Draws the specified image by using the anchor point.
 * The image can be drawn in different positions relative to
 * the anchor point by passing the appropriate position constants.
 * See <a href="#anchor">anchor points</a>.
 *
 * <p>If the source image contains transparent pixels, the corresponding
 * pixels in the destination image must be left untouched.  If the source
 * image contains partially transparent pixels, a compositing operation
 * must be performed with the destination pixels, leaving all pixels of
 * the destination image fully opaque.</p>
 *
 * <p>If <code>img</code> is the same as the destination of the Graphics
 * object, the result is undefined.  For copying areas within an
 * <code>Image</code>, {@link #copyArea copyArea} should be used instead.
 * </p>
 *
 * @param img the specified Image to be drawn
 * @param x the x coordinate of the anchor point
 * @param y the y coordinate of the anchor point
 * @param anchor the anchor point for positioning the image
 * @throws IllegalArgumentException if <code>anchor</code>
 * is not a legal value
 * @throws NullPointerException if <code>g</code> is <code>null</code>
 * @see Image
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_render
  (JNIEnv *env, jobject graphics, jobject img, jint x, jint y, jint anchor) {
    jboolean success = JNI_TRUE;

    /* null checking is handled by the Java layer, but test just in case */
    if (img == NULL) {
        success = JNI_FALSE; //KNI_ThrowNew(midpNullPointerException, NULL);
    } else {
        gxj_screen_buffer srcScreenBuffer;
        gxj_screen_buffer *srcBuffer = getScreenBufferFromImage(env, img, &srcScreenBuffer);

        jobject gImg = (*env)->GetObjectField(env, graphics, img_Id);
        if ((gImg == img) || !check_anchor(anchor, 0)) {
            success = JNI_FALSE; //KNI_ThrowNew(midpIllegalArgumentException, NULL);
        } else if (!normalize_anchor(&x, &y, srcBuffer->width, srcBuffer->height, anchor)) {
            success = JNI_FALSE;//KNI_ThrowNew(midpIllegalArgumentException, NULL);
        } else {
            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

            gxj_screen_buffer dstScreenBuffer;
            gxj_screen_buffer *dstBuffer = getScreenBufferFromGraphics(env, graphics, &dstScreenBuffer);

            translate(env, graphics, &x, &y);
            getClip(env, graphics, clip);

            gx_render_image(srcBuffer, dstBuffer, clip, x, y);

            releaseScreenBufferPrimitiveArrays(env, dstBuffer);
        }

        releaseScreenBufferPrimitiveArrays(env, srcBuffer);
    }

    return success;
}

/**
 * Renders the given region of the given  <tt>Image</tt> onto the
 * this <tt>Graphics</tt> object.
 * <p>
 * Java declaration:
 * <pre>
 *     renderRegion(Ljavax/microedition/lcdui/Image;IIIIIIII)V
 * </pre>
 *
 * @param img The <tt>Image</tt> object to be drawn
 * @param x_src The x coordinate of the upper-left corner of the
 *              source region
 * @param y_src The y coordinate of the upper-left corner of the
 *              source region
 * @param width The width of the source region
 * @param height The height of the source region
 * @param transform The transform to apply to the selected region.
 * @param x_dest The x coordinate of the destination anchor point
 * @param y_dest The y coordinate of the destination anchor point
 * @param anchor The anchor point for positioning the destination
 *               <tt>Image</tt>
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_microbackend_ui_graphics_toolkit_gxj_GXJGraphics_renderRegion
  (JNIEnv *env, jobject graphics, jobject img, jint x_src, jint y_src, jint width, jint height, jint transform, jint x_dest, jint y_dest, jint anchor) {
    jboolean success = JNI_TRUE;

     if (img == NULL) {
        /* null checking is performed in the Java code, but check just in case */
        success = JNI_FALSE; //KNI_ThrowNew(midpNullPointerException, NULL);
    } else if ((transform < 0) || (transform > 7)) {
        success = JNI_FALSE; //KNI_ThrowNew(midpIllegalArgumentException, NULL);
    } else if (!normalize_anchor(&x_dest, &y_dest, width, height, anchor)) {
        success = JNI_FALSE; //KNI_ThrowNew(midpIllegalArgumentException, NULL);
    } else {
        gxj_screen_buffer srcScreenBuffer;
        gxj_screen_buffer *srcBuffer = getScreenBufferFromImage(env, img, &srcScreenBuffer);
        jint img_width = srcBuffer->width;
        jint img_height = srcBuffer->height;

        jobject gImg = (*env)->GetObjectField(env, graphics, img_Id);
        if ((gImg == img) || (height < 0) || (width < 0) || (x_src < 0) || (y_src < 0) || ((x_src + width) > img_width)
                || ((y_src + height) > img_height)) {
            success = JNI_FALSE; //KNI_ThrowNew(midpIllegalArgumentException, NULL);
        } else {
            jshort clip[4]; /* Defined in Graphics.java as 4 shorts */

            gxj_screen_buffer dstScreenBuffer;
            gxj_screen_buffer *dstBuffer = getScreenBufferFromGraphics(env, graphics, &dstScreenBuffer);

            translate(env, graphics, &x_dest, &y_dest);
            getClip(env, graphics, clip);

            gx_render_imageregion(srcBuffer, dstBuffer, clip, x_src, y_src, width, height, x_dest, y_dest, transform);

            releaseScreenBufferPrimitiveArrays(env, dstBuffer);
        }
        releaseScreenBufferPrimitiveArrays(env, srcBuffer);
    }

    return success;
}
