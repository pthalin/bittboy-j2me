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
package org.thenesis.microbackend.ui;

import org.thenesis.microbackend.ui.awt.AWTBackend;
import org.thenesis.microbackend.ui.awt.AWTWrapperBackend;
import org.thenesis.microbackend.ui.awtgrabber.AWTGrabberBackend;
import org.thenesis.microbackend.ui.fb.FBBackend;
import org.thenesis.microbackend.ui.gtk.GTKBackend;
import org.thenesis.microbackend.ui.qt.QTBackend;
import org.thenesis.microbackend.ui.sdl.SDLBackend;
import org.thenesis.microbackend.ui.swt.SWTBackend;
import org.thenesis.microbackend.ui.swt.SWTWrapperBackend;
import org.thenesis.microbackend.ui.x11.X11Backend;

public class UIBackendFactory {

    //public static final String BACKEND_PACKAGE_PREFIX = "org.thenesis.microbackend.ui.";

    public static final String BACKEND_SDL = "SDL";
    public static final String BACKEND_AWT = "AWT";
    public static final String BACKEND_AWTGRABBER = "AWTGRABBER";
    public static final String BACKEND_SWT = "SWT";
    public static final String BACKEND_X11 = "X11";
    public static final String BACKEND_GTK = "GTK";
    public static final String BACKEND_QT = "QT";
    public static final String BACKEND_FB = "FB";
    public static final String BACKEND_NULL = "NULL";

    public static UIBackend createBackend(String name) {

        UIBackend backend = null;

        if (name.equalsIgnoreCase(BACKEND_SDL)) {
            backend = new SDLBackend();
        } else if (name.equalsIgnoreCase(BACKEND_AWT)) {
            backend = new AWTBackend();
        } else if (name.equalsIgnoreCase(BACKEND_AWTGRABBER)) {
            backend = new AWTGrabberBackend();
        } else if (name.equalsIgnoreCase(BACKEND_SWT)) {
            backend = new SWTBackend();
        } else if (name.equalsIgnoreCase(BACKEND_X11)) {
            backend = new X11Backend();
        } else if (name.equalsIgnoreCase(BACKEND_GTK)) {
            backend = new GTKBackend();
        } else if (name.equalsIgnoreCase(BACKEND_QT)) {
            backend = new QTBackend();
        } else if (name.equalsIgnoreCase(BACKEND_FB)) {
            backend = new FBBackend();
        } else {
            return null;
        }

        return backend;

    }

    public static UIBackend createBackend(Object m) {
        
        if (m == null) {
            return null;
        }

        if (m instanceof UIBackend) {
            return (UIBackend) m;
        }

        Class containerClass = null;
        try {
            containerClass = Class.forName("java.awt.Container");
            if (containerClass.isAssignableFrom(m.getClass())) {
                return new AWTWrapperBackend(m);
            }
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        
        try {
            containerClass = Class.forName("org.eclipse.swt.widgets.Canvas");
            if (containerClass.isAssignableFrom(m.getClass())) {
                return new SWTWrapperBackend(m);
            }
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }

        return null;

    }
    
//    private static void setNativeLibraryLoader(String jvmType) {
//        boolean success = false;
//        try {
//            Class loaderClass = null;
//            if (jvmType.equalsIgnoreCase("J2SE")) {
//                loaderClass = Class.forName("org.thenesis.microbackend.ui.NativeLoaderSE");
//            } else if (jvmType.equalsIgnoreCase("J2ME")) {
//                loaderClass = Class.forName("org.thenesis.microbackend.ui.NativeLoaderME");
//            }
//
//            if (loaderClass != null) {
//                NativeLibraryLoader loader = (NativeLibraryLoader) loaderClass.newInstance();
//                NativeLibraryLoader.setDefaultLoader(loader);
//                success = true;
//            } 
//        } catch (Exception e) {
//        }
//
//        if (!success)
//            Logging.log("A native library loader can't be found", Logging.ERROR);
//    }

    public static UIBackend createBackend(Object m, Configuration backendConfig, BackendEventListener listener) {

        // Create the native library loader required by some backends
//        String jvmType = backendConfig.getParameterDefault("org.thenesis.microbackend.ui.jvm", "J2SE");
//        setNativeLibraryLoader(jvmType);

        // Try to create an object from the given object
        UIBackend backend = createBackend(m);

        // Try to create an object from the configuration file or system properties
        if (backend == null) {
            String backendName = backendConfig.getParameterDefault("org.thenesis.microbackend.ui.backend", "AWT");
            backend = createBackend(backendName);
        }

        // If we fail to find a backend, create a fake one
        if (backend == null) {
            Logging.log("No Backend was not found: NULL backend will be used", Logging.WARNING);
            backend = new NullBackend();
        }

        // Get requested screen size
        int w = backendConfig.getIntParameter("org.thenesis.lwuit.microbackend.screenWidth", 100);
        int h = backendConfig.getIntParameter("org.thenesis.lwuit.microbackend.screenHeight", 100);

        backend.configure(backendConfig, w, h);
        backend.setBackendEventListener(listener);

        return backend;
    }

}
