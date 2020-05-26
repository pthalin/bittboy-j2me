/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * Copyright (C) Henning Heinold
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

#ifndef _org_thenesis_microbackend_ui_qt_QTBackend
#define _org_thenesis_microbackend_ui_qt_QTBackend


#ifdef QT3_BACKEND 

#include <qpixmap.h>
#include <qimage.h>
#include <qcanvas.h>
#include <qapplication.h>
#include <qpainter.h>
#include <qmainwindow.h>

#else

#ifdef QTOPIA4_BACKEND
#include <QtopiaApplication>
#else
#include <QApplication>
#endif

#include <QCloseEvent>
#include <QImage>
#include <QKeyEvent>
#include <QMouseEvent>
#include <QPainter>
#include <QPaintEvent>
#include <QWidget>

#endif

/**
* CustomCanvas
**/
class CustomCanvas : public QWidget {
       public:
#ifdef QT3_BACKEND
               CustomCanvas (QWidget *parent, const char *name = 0, int w = 0, int h = 0);
#else
			   CustomCanvas (QWidget *parent, int w = 0, int h = 0);
#endif
               QImage *qimage;

       protected:
               /**
               * Repaint the window using an off-screen buffer (a QPixmap).
               **/
               void paintEvent (QPaintEvent *);
               void mousePressEvent(QMouseEvent *);
               void mouseReleaseEvent(QMouseEvent *);
               void mouseMoveEvent(QMouseEvent *);
               void keyPressEvent(QKeyEvent *);
               void keyReleaseEvent(QKeyEvent *);
               void closeEvent(QCloseEvent *);

};

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>

/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    initialize
 * Signature: (II)I
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_initialize(JNIEnv * env, jobject obj, jint width, jint height);

/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    writeARGB
 * Signature: ([IIIII)V
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_writeARGB(JNIEnv * env, jobject obj, jintArray intBuffer, jint x_src, jint y_src, jint width, jint height);
/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    startMainLoop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_startMainLoop(JNIEnv * env, jobject obj);

/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    destroy
 * Signature: ()I
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_quit(JNIEnv * env, jobject obj);

#ifdef __cplusplus
}
#endif
#endif
