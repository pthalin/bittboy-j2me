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
package org.thenesis.microbackend.ui.sdl;

import java.io.IOException;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;
import org.thenesis.microbackend.ui.KeyConstants;
import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.NullBackendEventListener;
import org.thenesis.microbackend.ui.UIBackend;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.event.SDLEvent;
import sdljava.event.SDLKey;
import sdljava.event.SDLKeyboardEvent;
import sdljava.event.SDLMouseButtonEvent;
import sdljava.event.SDLMouseMotionEvent;
import sdljava.event.SDLQuitEvent;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;
import sdljava.x.swig.SDLPressedState;

public class SDLBackend implements UIBackend {

    public static final String VIDEO_MODE_SOFTWARE = "SW";
    public static final String VIDEO_MODE_HARDWARE = "HW";

    public static final int BITS_PER_PIXEL_32 = 32;
    public static final int BITS_PER_PIXEL_16 = 16;

    private SDLSurface screenSurface;
    private SDLSurface rootARGBSurface;
    private SDLEventThread eventThread;

    private int canvasWidth;
    private int canvasHeight;
    private int bitsPerPixel;
    private String videoMode;

    private BackendEventListener listener = new NullBackendEventListener();

    public SDLBackend(int w, int h, int bitsPerPixel, String videoMode) {
        canvasWidth = w;
        canvasHeight = h;
        this.bitsPerPixel = bitsPerPixel;
        this.videoMode = videoMode;
    }

    public SDLBackend() {

    }

    /* UIBackend interface */

    public void configure(Configuration conf, int width, int height) {
        canvasWidth = width;
        canvasHeight = height;

        bitsPerPixel = conf.getIntParameter("org.thenesis.microbackend.ui.sdl.bitsPerPixel", 32);
        videoMode = conf.getParameterDefault("org.thenesis.microbackend.ui.sdl.videoMode", "SW");

    }

    public void setBackendEventListener(BackendEventListener listener) {
        this.listener = listener;
    }

    public void updateARGBPixels(int[] argbPixels, int x, int y, int widht, int heigth) {

        // Draw rgb field on the surface
        rootARGBSurface.setPixelData32(argbPixels);

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] Toolkit.refresh(): x=" + x + " y=" + y + " widht=" + widht + " heigth=" + heigth);

        try {
            rootARGBSurface.blitSurface(screenSurface);
            screenSurface.updateRect(x, y, widht, heigth);
        } catch (SDLException e) {
            e.printStackTrace();
        }

    }

    public int getWidth() {
        return canvasWidth;
    }

    public int getHeight() {
        return canvasHeight;
    }

    public void close() {
        eventThread.stop();
        SDLMain.quitSubSystem(SDLMain.SDL_INIT_VIDEO);
    }

    public void open() throws IOException {

        long flags = videoMode.equalsIgnoreCase("HW") ? SDLVideo.SDL_HWSURFACE : SDLVideo.SDL_SWSURFACE;

        try {
            SDLMain.init(SDLMain.SDL_INIT_VIDEO);
            screenSurface = SDLVideo.setVideoMode(canvasWidth, canvasHeight, bitsPerPixel, flags);
            rootARGBSurface = SDLVideo.createRGBSurface(SDLVideo.SDL_SWSURFACE, canvasWidth, canvasHeight, 32, 0x00ff0000L, 0x0000ff00L,
                0x000000ffL, 0xff000000L);
            if (Logging.TRACE_ENABLED)
                System.out.println("[DEBUG] Toolkit.initialize(): VideoSurface: " + rootARGBSurface);
            eventThread = new SDLEventThread();
            eventThread.start();

        } catch (SDLException e) {
            e.printStackTrace();
        }
    }

    /* Internals */

    public class SDLEventThread implements Runnable {

        private volatile Thread thread;

        public void start() {
            thread = new Thread(this);
            thread.start();
        }

        public void stop() {
            thread = null;
        }

        /**
         * The main event pump loop. Events are pumped into the system event
         * queue.
         */
        public void run() {

            try {
                SDLEvent.enableUNICODE(1);
                while (Thread.currentThread() == thread) {
                    processEvent(SDLEvent.waitEvent(true));
                }
            } catch (Throwable t) {
                if (Logging.TRACE_ENABLED) {
                    System.err.println("Exception during event dispatch");
                    t.printStackTrace();
                }
            }
        }

        public void processEvent(SDLEvent event) {

            if (Logging.TRACE_ENABLED)
                System.out.println("[DEBUG] SDLEventThread.processEvent()");

            if (event instanceof SDLMouseButtonEvent)
                processEvent((SDLMouseButtonEvent) event);
            else if (event instanceof SDLMouseMotionEvent)
                processEvent((SDLMouseMotionEvent) event);
            else if (event instanceof SDLQuitEvent)
                processEvent((SDLQuitEvent) event);
            //else if (event instanceof SDLExposeEvent)
            //	processEvent((SDLExposeEvent) event);
            else if (event instanceof SDLKeyboardEvent)
                processEvent((SDLKeyboardEvent) event);
        }

        public void processEvent(SDLMouseButtonEvent event) {

            //int sdlButton = event.getButton();

            if (event.getState() == SDLPressedState.PRESSED) {
                if (Logging.TRACE_ENABLED)
                    System.out.println("[DEBUG] SDLEventThread.processEvent(): MOUSE_PRESSED");
                listener.mousePressed(event.getX(), event.getY(), 0);
            } else {
                if (Logging.TRACE_ENABLED)
                    System.out.println("[DEBUG] SDLEventThread.processEvent(): MOUSE_RELEASED");
                listener.mouseReleased(event.getX(), event.getY(), 0);
            }

        }

        public void processEvent(SDLMouseMotionEvent event) {
            listener.mouseMoved(event.getX(), event.getY(), 0);
        }

        public void processEvent(SDLKeyboardEvent event) {

            int unicode = event.getUnicode();
            int keyCode = event.getSym();

            if (event.getState() == SDLPressedState.PRESSED) {
                if (Logging.TRACE_ENABLED)
                    System.out.println("[DEBUG] SDLEventThread.processEvent(): keyCode: " + keyCode + " char: " + (char) unicode);
                listener.keyPressed(convertKeyCode(keyCode), (char) unicode, 0);
            } else if (event.getState() == SDLPressedState.RELEASED) {
                listener.keyReleased(convertKeyCode(keyCode), (char) unicode, 0);
            }

        }

        public void processEvent(SDLQuitEvent event) {
            listener.windowClosed();
        }

    }

    public static int convertKeyCode(int keyCode) {

        switch (keyCode) {

        case SDLKey.SDLK_UNKNOWN:
            return KeyConstants.VK_UNDEFINED;
        case SDLKey.SDLK_BACKSPACE:
            return KeyConstants.VK_BACK_SPACE;
        case SDLKey.SDLK_TAB:
            return KeyConstants.VK_TAB;
        case SDLKey.SDLK_CLEAR:
            return KeyConstants.VK_CLEAR;
        case SDLKey.SDLK_RETURN:
            return KeyConstants.VK_ENTER;
        case SDLKey.SDLK_PAUSE:
            return KeyConstants.VK_PAUSE;
        case SDLKey.SDLK_ESCAPE:
            return KeyConstants.VK_ESCAPE;
        case SDLKey.SDLK_SPACE:
            return KeyConstants.VK_SPACE;
        case SDLKey.SDLK_EXCLAIM:
            return KeyConstants.VK_EXCLAMATION_MARK;
        case SDLKey.SDLK_QUOTEDBL:
            return KeyConstants.VK_QUOTEDBL;
        case SDLKey.SDLK_HASH:
            return KeyConstants.VK_UNDEFINED; // FIXME
        case SDLKey.SDLK_DOLLAR:
            return KeyConstants.VK_DOLLAR;
        case SDLKey.SDLK_AMPERSAND:
            return KeyConstants.VK_AMPERSAND;
        case SDLKey.SDLK_QUOTE:
            return KeyConstants.VK_QUOTE;
        case SDLKey.SDLK_LEFTPAREN:
            return KeyConstants.VK_LEFT_PARENTHESIS;
        case SDLKey.SDLK_RIGHTPAREN:
            return KeyConstants.VK_RIGHT_PARENTHESIS;
        case SDLKey.SDLK_ASTERISK:
            return KeyConstants.VK_ASTERISK;
        case SDLKey.SDLK_PLUS:
            return KeyConstants.VK_PLUS;
        case SDLKey.SDLK_COMMA:
            return KeyConstants.VK_COMMA;
        case SDLKey.SDLK_MINUS:
            return KeyConstants.VK_MINUS;
        case SDLKey.SDLK_PERIOD:
            return KeyConstants.VK_PERIOD;
        case SDLKey.SDLK_SLASH:
            return KeyConstants.VK_SLASH;
        case SDLKey.SDLK_0:
            return KeyConstants.VK_0;
        case SDLKey.SDLK_1:
            return KeyConstants.VK_1;
        case SDLKey.SDLK_2:
            return KeyConstants.VK_2;
        case SDLKey.SDLK_3:
            return KeyConstants.VK_3;
        case SDLKey.SDLK_4:
            return KeyConstants.VK_4;
        case SDLKey.SDLK_5:
            return KeyConstants.VK_5;
        case SDLKey.SDLK_6:
            return KeyConstants.VK_6;
        case SDLKey.SDLK_7:
            return KeyConstants.VK_7;
        case SDLKey.SDLK_8:
            return KeyConstants.VK_8;
        case SDLKey.SDLK_9:
            return KeyConstants.VK_9;
        case SDLKey.SDLK_COLON:
            return KeyConstants.VK_COLON;
        case SDLKey.SDLK_SEMICOLON:
            return KeyConstants.VK_SEMICOLON;
        case SDLKey.SDLK_LESS:
            return KeyConstants.VK_LESS;
        case SDLKey.SDLK_EQUALS:
            return KeyConstants.VK_EQUALS;
        case SDLKey.SDLK_GREATER:
            return KeyConstants.VK_GREATER;
        case SDLKey.SDLK_QUESTION:
            return KeyConstants.VK_UNDEFINED;
        case SDLKey.SDLK_AT:
            return KeyConstants.VK_AT;
            /* 
             Skip uppercase letters
             */
        case SDLKey.SDLK_LEFTBRACKET:
            return KeyConstants.VK_OPEN_BRACKET;
        case SDLKey.SDLK_BACKSLASH:
            return KeyConstants.VK_BACK_SLASH;
        case SDLKey.SDLK_RIGHTBRACKET:
            return KeyConstants.VK_CLOSE_BRACKET;
        case SDLKey.SDLK_CARET:
            return KeyConstants.VK_UNDEFINED;
        case SDLKey.SDLK_UNDERSCORE:
            return KeyConstants.VK_UNDERSCORE;
        case SDLKey.SDLK_BACKQUOTE:
            return KeyConstants.VK_BACK_QUOTE;
        case SDLKey.SDLK_a:
            return KeyConstants.VK_A;
        case SDLKey.SDLK_b:
            return KeyConstants.VK_B;
        case SDLKey.SDLK_c:
            return KeyConstants.VK_C;
        case SDLKey.SDLK_d:
            return KeyConstants.VK_D;
        case SDLKey.SDLK_e:
            return KeyConstants.VK_E;
        case SDLKey.SDLK_f:
            return KeyConstants.VK_F;
        case SDLKey.SDLK_g:
            return KeyConstants.VK_G;
        case SDLKey.SDLK_h:
            return KeyConstants.VK_H;
        case SDLKey.SDLK_i:
            return KeyConstants.VK_I;
        case SDLKey.SDLK_j:
            return KeyConstants.VK_J;
        case SDLKey.SDLK_k:
            return KeyConstants.VK_K;
        case SDLKey.SDLK_l:
            return KeyConstants.VK_L;
        case SDLKey.SDLK_m:
            return KeyConstants.VK_M;
        case SDLKey.SDLK_n:
            return KeyConstants.VK_N;
        case SDLKey.SDLK_o:
            return KeyConstants.VK_O;
        case SDLKey.SDLK_p:
            return KeyConstants.VK_P;
        case SDLKey.SDLK_q:
            return KeyConstants.VK_Q;
        case SDLKey.SDLK_r:
            return KeyConstants.VK_R;
        case SDLKey.SDLK_s:
            return KeyConstants.VK_S;
        case SDLKey.SDLK_t:
            return KeyConstants.VK_T;
        case SDLKey.SDLK_u:
            return KeyConstants.VK_U;
        case SDLKey.SDLK_v:
            return KeyConstants.VK_V;
        case SDLKey.SDLK_w:
            return KeyConstants.VK_W;
        case SDLKey.SDLK_x:
            return KeyConstants.VK_X;
        case SDLKey.SDLK_y:
            return KeyConstants.VK_Y;
        case SDLKey.SDLK_z:
            return KeyConstants.VK_Z;
        case SDLKey.SDLK_DELETE:
            return KeyConstants.VK_DELETE;
            /* End of ASCII mapped keysyms */

            /* Numeric keypad */
        case SDLKey.SDLK_KP0:
            return KeyConstants.VK_NUMPAD0;
        case SDLKey.SDLK_KP1:
            return KeyConstants.VK_NUMPAD1;
        case SDLKey.SDLK_KP2:
            return KeyConstants.VK_NUMPAD2;
        case SDLKey.SDLK_KP3:
            return KeyConstants.VK_NUMPAD3;
        case SDLKey.SDLK_KP4:
            return KeyConstants.VK_NUMPAD4;
        case SDLKey.SDLK_KP5:
            return KeyConstants.VK_NUMPAD5;
        case SDLKey.SDLK_KP6:
            return KeyConstants.VK_NUMPAD6;
        case SDLKey.SDLK_KP7:
            return KeyConstants.VK_NUMPAD7;
        case SDLKey.SDLK_KP8:
            return KeyConstants.VK_NUMPAD8;
        case SDLKey.SDLK_KP9:
            return KeyConstants.VK_NUMPAD9;
        case SDLKey.SDLK_KP_PERIOD:
            return KeyConstants.VK_PERIOD;
        case SDLKey.SDLK_KP_DIVIDE:
            return KeyConstants.VK_DIVIDE;
        case SDLKey.SDLK_KP_MULTIPLY:
            return KeyConstants.VK_MULTIPLY;
        case SDLKey.SDLK_KP_MINUS:
            return KeyConstants.VK_MINUS;
        case SDLKey.SDLK_KP_PLUS:
            return KeyConstants.VK_PLUS;
        case SDLKey.SDLK_KP_ENTER:
            return KeyConstants.VK_ENTER;
        case SDLKey.SDLK_KP_EQUALS:
            return KeyConstants.VK_EQUALS;

            /* Arrows + Home/End pad */
        case SDLKey.SDLK_UP:
            return KeyConstants.VK_UP;
        case SDLKey.SDLK_DOWN:
            return KeyConstants.VK_DOWN;
        case SDLKey.SDLK_RIGHT:
            return KeyConstants.VK_RIGHT;
        case SDLKey.SDLK_LEFT:
            return KeyConstants.VK_LEFT;
        case SDLKey.SDLK_INSERT:
            return KeyConstants.VK_INSERT;
        case SDLKey.SDLK_HOME:
            return KeyConstants.VK_HOME;
        case SDLKey.SDLK_END:
            return KeyConstants.VK_END;
        case SDLKey.SDLK_PAGEUP:
            return KeyConstants.VK_PAGE_UP;
        case SDLKey.SDLK_PAGEDOWN:
            return KeyConstants.VK_PAGE_DOWN;

            /* Function keys */
        case SDLKey.SDLK_F1:
            return KeyConstants.VK_F1;
        case SDLKey.SDLK_F2:
            return KeyConstants.VK_F2;
        case SDLKey.SDLK_F3:
            return KeyConstants.VK_F3;
        case SDLKey.SDLK_F4:
            return KeyConstants.VK_F4;
        case SDLKey.SDLK_F5:
            return KeyConstants.VK_F5;
        case SDLKey.SDLK_F6:
            return KeyConstants.VK_F6;
        case SDLKey.SDLK_F7:
            return KeyConstants.VK_F7;
        case SDLKey.SDLK_F8:
            return KeyConstants.VK_F8;
        case SDLKey.SDLK_F9:
            return KeyConstants.VK_F9;
        case SDLKey.SDLK_F10:
            return KeyConstants.VK_F10;
        case SDLKey.SDLK_F11:
            return KeyConstants.VK_F11;
        case SDLKey.SDLK_F12:
            return KeyConstants.VK_F12;
        case SDLKey.SDLK_F13:
            return KeyConstants.VK_F13;
        case SDLKey.SDLK_F14:
            return KeyConstants.VK_F14;
        case SDLKey.SDLK_F15:
            return KeyConstants.VK_F15;

            /* Key state modifier keys */
        case SDLKey.SDLK_NUMLOCK:
            return KeyConstants.VK_NUM_LOCK;
        case SDLKey.SDLK_CAPSLOCK:
            return KeyConstants.VK_CAPS_LOCK;
        case SDLKey.SDLK_SCROLLOCK:
            return KeyConstants.VK_SCROLL_LOCK;
        case SDLKey.SDLK_RSHIFT:
        case SDLKey.SDLK_LSHIFT:
            return KeyConstants.VK_SHIFT;
        case SDLKey.SDLK_RCTRL:
        case SDLKey.SDLK_LCTRL:
            return KeyConstants.VK_CONTROL;
        case SDLKey.SDLK_RALT:
        case SDLKey.SDLK_LALT:
            return KeyConstants.VK_ALT;
        case SDLKey.SDLK_RMETA:
        case SDLKey.SDLK_LMETA:
            return KeyConstants.VK_META;
        case SDLKey.SDLK_LSUPER:
        case SDLKey.SDLK_RSUPER:
            return KeyConstants.VK_WINDOWS;
        case SDLKey.SDLK_MODE:
            return KeyConstants.VK_ALT_GRAPH;
        case SDLKey.SDLK_COMPOSE:
            return KeyConstants.VK_COMPOSE;

            /* Miscellaneous function keys */
        case SDLKey.SDLK_HELP:
            return KeyConstants.VK_HELP;
        case SDLKey.SDLK_PRINT:
            return KeyConstants.VK_PRINTSCREEN;
        case SDLKey.SDLK_SYSREQ:
            return KeyConstants.VK_UNDEFINED;
        case SDLKey.SDLK_BREAK:
            return KeyConstants.VK_UNDEFINED;
        case SDLKey.SDLK_MENU:
            return KeyConstants.VK_UNDEFINED;
        case SDLKey.SDLK_POWER:
            return KeyConstants.VK_UNDEFINED; /* Power Macintosh power key */
        case SDLKey.SDLK_EURO:
            return KeyConstants.VK_EURO_SIGN; /* Some european keyboards */

        default:
            return KeyConstants.VK_UNDEFINED;

        }
    }

}
