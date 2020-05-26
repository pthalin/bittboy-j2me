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
package org.thenesis.microbackend.ui.x11;

import gnu.x11.Connection;
import gnu.x11.Display;
import gnu.x11.Option;
import gnu.x11.Window;
import gnu.x11.event.ButtonPress;
import gnu.x11.event.ButtonRelease;
import gnu.x11.event.ClientMessage;
import gnu.x11.event.Event;
import gnu.x11.event.Expose;
import gnu.x11.event.KeyPress;
import gnu.x11.event.KeyRelease;
import gnu.x11.event.MotionNotify;
import gnu.x11.image.ZPixmap;
import gnu.x11.keysym.Misc;

import java.io.IOException;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;
import org.thenesis.microbackend.ui.KeyConstants;
import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.NullBackendEventListener;
import org.thenesis.microbackend.ui.UIBackend;

public class X11Backend implements UIBackend {

    private X11Application x11App;

    private int canvasWidth;
    private int canvasHeight;
    private String displayAddress;

    private BackendEventListener listener = new NullBackendEventListener();

    public X11Backend(int w, int h, String display) {
        canvasWidth = w;
        canvasHeight = h;
        this.displayAddress = display;
    }

    public X11Backend() {
    }

    /* UIBackend interface */

    public void configure(Configuration conf, int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
        displayAddress = conf.getParameterDefault("org.thenesis.microbackend.ui.x11.Display", ":0.0");
    }

    public void initialize(Configuration conf, Object container) {
        // Do nothing       
    }

    public void setBackendEventListener(BackendEventListener listener) {
        this.listener = listener;
    }

    public void open() throws IOException {
        x11App = new X11Application(new String[] { "" }, canvasWidth, canvasHeight);
        x11App.start();
    }

    public void updateARGBPixels(int[] argbPixels, int x, int y, int width, int height) {
        x11App.setPixels(x, y, (int) width, (int) height, argbPixels, 0, canvasWidth);
        x11App.paint();
    }

    public int getWidth() {
        return canvasWidth;
    }

    public int getHeight() {
        return canvasHeight;
    }

    public void close() {
        x11App.stop();
    }

    /* Internals */

    private class X11Application implements Runnable {

        private Thread thread;
        private volatile boolean running = true;

        public Event event;
        public boolean leave_display_open;
        public Window window;
        public Display display;
        public ZPixmap zpixmap;
        private boolean exposed = false;
        protected Option option;
        private String[] args;
        private int width;
        private int height;
        private boolean dragEnabled;

        public void start() {

            option = new Option(args);
            Display.Name display_name = option.display_name("displayAddress", "X server to connect to", new Display.Name(displayAddress));

            int send_mode = option.enum("send-mode", "request sending mode", Connection.SEND_MODE_STRINGS, Connection.ASYNCHRONOUS);

            display = new Display(display_name);
            display.connection.send_mode = send_mode;
            leave_display_open = false;
            zpixmap = new ZPixmap(display, width, height, display.default_pixmap_format);

            Window.Attributes win_attr = new Window.Attributes();
            win_attr.set_background(display.default_white);
            win_attr.set_border(display.default_black);
            win_attr.set_event_mask(Event.BUTTON_PRESS_MASK | Event.BUTTON_RELEASE_MASK | Event.EXPOSURE_MASK | Event.KEY_PRESS_MASK
                    | Event.KEY_RELEASE_MASK | Event.POINTER_MOTION_MASK);
            window = new Window(display.default_root, 10, 10, width, height, 5, win_attr);

            window.set_wm(this, "main");
            window.set_wm_delete_window();
            window.map();

            // Wait while the window is not shown
            while (!exposed) {
                dispatch_event();
            }

            // Start event thread
            thread = new Thread(this);
            thread.start();
        }

        public void stop() {
            running = false;
        }

        public X11Application(String[] args, int width, int height) {
            this.args = args;
            this.width = width;
            this.height = height;
        }

        public void setPixels(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize) {
            for (int y = startX; y < startY + h; y++) {
                for (int x = startX; x < startX + w; x++) {
                    zpixmap.set(x, y, rgbArray[offset + y * scansize + x]);
                }
            }
        }

        public void paint() {
            window.put_image(display.default_gc, zpixmap, 0, 0);
            display.flush();
        }

        public void run() {

            while (running)
                dispatch_event();

            if (!leave_display_open)
                display.close();
        }

        public void dispatch_event() {
            event = display.next_event();

            if (Logging.TRACE_ENABLED)
                System.out.println("[DEBUG] X11Backend.dispatch_event(): " + event);

            switch (event.code()) {
            case ClientMessage.CODE:
                if (((ClientMessage) event).delete_window()) {
                    fireCloseEvent();
                }
                break;
            case Expose.CODE:
                exposed = true;
                paint();
                break;
            case KeyPress.CODE: {
                KeyPress e = (KeyPress) event;
                int keycode = e.detail();
                int keystate = e.state();
                int keysym = display.input.keycode_to_keysym(keycode, keystate, false); // FIXME problem with uppercase
                fireKeyEvent(keycode, keystate, (char) keysym, true);
            }
                break;
            case KeyRelease.CODE: {
                KeyRelease e = (KeyRelease) event;
                int keycode = e.detail();
                int keystate = e.state();
                int keysym = display.input.keycode_to_keysym(keycode, keystate, false); // FIXME problem with uppercase
                fireKeyEvent(keycode, keystate, (char) keysym, false);
            }
                break;
            case ButtonPress.CODE: {
                dragEnabled = true;
                ButtonPress e = (ButtonPress) event;
                firePointerPressureEvent(e.event_x(), e.event_y(), true);
            }
                break;
            case ButtonRelease.CODE: {
                dragEnabled = false;
                ButtonRelease e = (ButtonRelease) event;
                firePointerPressureEvent(e.event_x(), e.event_y(), false);
            }
                break;
            case MotionNotify.CODE: {
                MotionNotify e = (MotionNotify) event;
                fireMotionEvent(e.event_x(), e.event_y());
            }
                break;
            }

        }

        public void fireKeyEvent(int keycode, int keystate, char c, boolean pressed) {
            //System.out.println("[DEBUG] X11Backend.fireKeyEvent(): " + c + " => " + convertKeyCode(c));
            if (pressed) {
                listener.keyPressed(convertKeyCode(c), c, 0);
            } else {
                listener.keyReleased(convertKeyCode(c), c, 0);
            }
        }

        private void firePointerPressureEvent(int x, int y, boolean pressed) {
            //System.out.println("[DEBUG] X11Backend.processPointerPressedEvent()");
            if (pressed) {
                listener.mousePressed(x, y, 0);
            } else {
                listener.mouseReleased(x, y, 0);
            }
        }

        private void fireMotionEvent(int x, int y) {
            //System.out.println("[DEBUG] SWTBackend.processPointerDraggedEvent()");
            listener.mouseMoved(x, y, 0);
        }

        private void fireCloseEvent() {
            //System.out.println("[DEBUG] SWTBackend.processPointerDraggedEvent()");
            listener.windowClosed();
        }

    }

    protected static int convertKeyCode(int keyCode) {

        switch (keyCode) {
        /* TTY Functions*/
        case Misc.BACKSPACE:
            return KeyConstants.VK_BACK_SPACE; /* back space, back char */
        case Misc.TAB:
            return KeyConstants.VK_TAB;
        case Misc.LINEFEED:
            return KeyConstants.VK_UNDEFINED;
        case Misc.CLEAR:
            return KeyConstants.VK_CLEAR;
        case Misc.RETURN:
            return KeyConstants.VK_ENTER;
        case Misc.PAUSE:
            return KeyConstants.VK_PAUSE;
        case Misc.SCROLL_LOCK:
            return KeyConstants.VK_SCROLL_LOCK;
        case Misc.SYS_REQ:
            return KeyConstants.VK_UNDEFINED;
        case Misc.ESCAPE:
            return KeyConstants.VK_ESCAPE;
        case Misc.DELETE:
            return KeyConstants.VK_DELETE;
            /** Cursor control & motion. */
        case Misc.HOME:
            return KeyConstants.VK_HOME;
        case Misc.LEFT:
            return KeyConstants.VK_LEFT;
        case Misc.UP:
            return KeyConstants.VK_UP;
        case Misc.RIGHT:
            return KeyConstants.VK_RIGHT;
        case Misc.DOWN:
            return KeyConstants.VK_DOWN;
        case Misc.PAGE_UP:
            return KeyConstants.VK_PAGE_UP;
        case Misc.PAGE_DOWN:
            return KeyConstants.VK_PAGE_DOWN;
        case Misc.END:
            return KeyConstants.VK_END;
        case Misc.BEGIN:
            return KeyConstants.VK_BEGIN;
            /* Misc Functions. */
        case Misc.SELECT:
            return KeyConstants.VK_UNDEFINED;
        case Misc.PRINT:
            return KeyConstants.VK_PRINTSCREEN;
        case Misc.EXECUTE:
            return KeyConstants.VK_UNDEFINED;
        case Misc.INSERT:
            return KeyConstants.VK_INSERT;
        case Misc.UNDO:
            return KeyConstants.VK_UNDO;
        case Misc.REDO:
            return KeyConstants.VK_UNDEFINED;
        case Misc.MENU:
            return KeyConstants.VK_UNDEFINED;
        case Misc.FIND:
            return KeyConstants.VK_FIND;
        case Misc.CANCEL:
            return KeyConstants.VK_CANCEL;
        case Misc.HELP:
            return KeyConstants.VK_HELP;
        case Misc.BREAK:
            return KeyConstants.VK_UNDEFINED;
        case Misc.MODE_SWITCH:
            return KeyConstants.VK_MODECHANGE;
        case Misc.NUM_LOCK:
            return KeyConstants.VK_NUM_LOCK;
            /* Keypad Functions, keypad numbers cleverly chosen to map to ascii. */
        case Misc.KP_SPACE:
            return KeyConstants.VK_SPACE; /* space */
        case Misc.KP_TAB:
            return KeyConstants.VK_TAB;
        case Misc.KP_ENTER:
            return KeyConstants.VK_ENTER;
        case Misc.KP_F1:
            return KeyConstants.VK_F1;
        case Misc.KP_F2:
            return KeyConstants.VK_F2;
        case Misc.KP_F3:
            return KeyConstants.VK_F3;
        case Misc.KP_F4:
            return KeyConstants.VK_F4;
        case Misc.KP_HOME:
            return KeyConstants.VK_HOME;
        case Misc.KP_LEFT:
            return KeyConstants.VK_KP_LEFT;
        case Misc.KP_UP:
            return KeyConstants.VK_KP_UP;
        case Misc.KP_RIGHT:
            return KeyConstants.VK_KP_RIGHT;
        case Misc.KP_DOWN:
            return KeyConstants.VK_KP_DOWN;
        case Misc.KP_PAGE_UP:
            return KeyConstants.VK_PAGE_UP;
        case Misc.KP_PAGE_DOWN:
            return KeyConstants.VK_PAGE_DOWN;
        case Misc.KP_END:
            return KeyConstants.VK_END;
        case Misc.KP_BEGIN:
            return KeyConstants.VK_BEGIN;
        case Misc.KP_INSERT:
            return KeyConstants.VK_INSERT;
        case Misc.KP_DELETE:
            return KeyConstants.VK_DELETE;
        case Misc.KP_EQUAL:
            return KeyConstants.VK_EQUALS;
        case Misc.KP_MULTIPLY:
            return KeyConstants.VK_MULTIPLY;
        case Misc.KP_ADD:
            return KeyConstants.VK_ADD;
        case Misc.KP_SEPARATOR:
            return KeyConstants.VK_SEPARATOR;
        case Misc.KP_SUBTRACT:
            return KeyConstants.VK_SUBTRACT;
        case Misc.KP_DECIMAL:
            return KeyConstants.VK_DECIMAL;
        case Misc.KP_DIVIDE:
            return KeyConstants.VK_DIVIDE;

        case Misc.KP_0:
            return KeyConstants.VK_0;
        case Misc.KP_1:
            return KeyConstants.VK_1;
        case Misc.KP_2:
            return KeyConstants.VK_2;
        case Misc.KP_3:
            return KeyConstants.VK_3;
        case Misc.KP_4:
            return KeyConstants.VK_4;
        case Misc.KP_5:
            return KeyConstants.VK_5;
        case Misc.KP_6:
            return KeyConstants.VK_6;
        case Misc.KP_7:
            return KeyConstants.VK_7;
        case Misc.KP_8:
            return KeyConstants.VK_8;
        case Misc.KP_9:
            return KeyConstants.VK_9;

            /* Auxilliary Functions;*/

        case Misc.F1:
            return KeyConstants.VK_F1;
        case Misc.F2:
            return KeyConstants.VK_F12;
        case Misc.F3:
            return KeyConstants.VK_F13;
        case Misc.F4:
            return KeyConstants.VK_F14;
        case Misc.F5:
            return KeyConstants.VK_F15;
        case Misc.F6:
            return KeyConstants.VK_F16;
        case Misc.F7:
            return KeyConstants.VK_F17;
        case Misc.F8:
            return KeyConstants.VK_F18;
        case Misc.F9:
            return KeyConstants.VK_F19;
        case Misc.F10:
            return KeyConstants.VK_F10;
        case Misc.F11:
            return KeyConstants.VK_F11;
        case Misc.F12:
            return KeyConstants.VK_F12;
        case Misc.F13:
            return KeyConstants.VK_F13;
        case Misc.F14:
            return KeyConstants.VK_F14;
        case Misc.F15:
            return KeyConstants.VK_F15;
        case Misc.F16:
            return KeyConstants.VK_F16;
        case Misc.F17:
            return KeyConstants.VK_F17;
        case Misc.F18:
            return KeyConstants.VK_F18;
        case Misc.F19:
            return KeyConstants.VK_F19;
        case Misc.F20:
            return KeyConstants.VK_F20;
        case Misc.F21:
            return KeyConstants.VK_F21;
        case Misc.F22:
            return KeyConstants.VK_F22;
        case Misc.F23:
            return KeyConstants.VK_F23;
        case Misc.F24:
            return KeyConstants.VK_F24;

            /* Modifiers. */

        case Misc.SHIFT_L:
        case Misc.SHIFT_R:
            return KeyConstants.VK_SHIFT;
        case Misc.CONTROL_L:
        case Misc.CONTROL_R:
            return KeyConstants.VK_CONTROL;
        case Misc.CAPS_LOCK:
        case Misc.SHIFT_LOCK:
            return KeyConstants.VK_CAPS_LOCK;
        case Misc.META_L:
        case Misc.META_R:
            return KeyConstants.VK_META;
        case Misc.ALT_L:
        case Misc.ALT_R:
            return KeyConstants.VK_ALT;
        case Misc.SUPER_L:
        case Misc.SUPER_R:
            return KeyConstants.VK_WINDOWS;
        case Misc.HYPER_L:
        case Misc.HYPER_R:
            return KeyConstants.VK_UNDEFINED;

        default:
            return KeyConstants.VK_UNDEFINED;

        }

    }

}
