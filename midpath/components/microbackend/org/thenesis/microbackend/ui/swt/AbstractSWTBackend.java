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
package org.thenesis.microbackend.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.KeyConstants;
import org.thenesis.microbackend.ui.NullBackendEventListener;
import org.thenesis.microbackend.ui.UIBackend;

/**
 * TODO: Handle modifiers and shift/alt/control key codes
 */
public abstract class AbstractSWTBackend implements UIBackend, KeyListener, MouseListener, MouseMoveListener, DisposeListener {

    protected ImageData imageData;
    protected Image image;
    protected GC gc;
    protected RedrawTask redrawTask;
    protected Display display;
    protected Canvas canvas;

    protected int canvasWidth;
    protected int canvasHeight;

    protected BackendEventListener listener = new NullBackendEventListener();
    private int keyCode;
    private int keyChar;

    public void setBackendEventListener(BackendEventListener listener) {
        this.listener = listener;
    }

    public void updateARGBPixels(int[] argbPixels, int x, int y, int width, int height) {

        //System.out.println("[DEBUG] SWTBackend.updateSurfacePixels(): " + x + " " + y + " " + width + " " + height);	

        //		for (int j = 0; j < canvasHeight; j++) {
        //			imageData.setPixels(0, j, canvasWidth, argbPixels, canvasWidth * j);
        //		}

        for (int j = 0; j < height; j++) {
            imageData.setPixels(x, y + j, width, argbPixels, canvasWidth * (y + j) + x);
        }

        if (display != null) {
            redrawTask.setRegion(x, y, width, height);
            display.syncExec(redrawTask);
        }

    }

    public int getWidth() {
        return canvasWidth;
    }

    public int getHeight() {
        return canvasHeight;
    }

    protected void configureCanvas(final Canvas canvas) {

        this.canvas = canvas;
        PaletteData palette = new PaletteData(0x00FF0000, 0x0000FF00, 0x000000FF);
        imageData = new ImageData(canvasWidth, canvasHeight, 32, palette);
        gc = new GC(canvas);

        canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                image = new Image(display, imageData);
                e.gc.drawImage(image, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
                image.dispose();
            }
        });

        redrawTask = new RedrawTask(canvas);

        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMoveListener(this);
        canvas.addDisposeListener(this);
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("[DEBUG] SWTBackend.keyPressed(): key code: " + e.keyCode + " char: " + (int) e.character);
        mapKey(e.keyCode, e.character);
        listener.keyPressed(keyCode, (char)keyChar, 0);
    }

    public void keyReleased(KeyEvent e) {
        //System.out.println("[DEBUG] SWTBackend.keyReleased(): key code: " + e.keyCode + " char: " + e.character);
        mapKey(e.keyCode, e.character);
        listener.keyReleased(keyCode, (char)keyChar, 0);
    }

    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public void mouseDoubleClick(MouseEvent arg0) {
        // Not used
    }

    public void mouseDown(MouseEvent e) {
        //System.out.println("[DEBUG] SWTBackend.mouseDown()");
        listener.pointerPressed(e.x, e.y, 0);
    }

    public void mouseUp(MouseEvent e) {
        //System.out.println("[DEBUG] SWTBackend.mouseUp()");
        listener.pointerReleased(e.x, e.y, 0);
    }

    public void mouseMove(MouseEvent e) {
        //System.out.println("[DEBUG] SWTBackend.mouseDragged(): " + dragEnabled);
        listener.pointerMoved(e.x, e.y, 0);
    }

    public void widgetDisposed(DisposeEvent e) {
        listener.windowClosed();
    }

    protected void mapKey(int inputKeyCode, char inputChar) {

        keyCode = KeyConstants.VK_UNDEFINED;

        //  First check if it's a SWT key code
        switch (inputKeyCode) {
        case SWT.ARROW_UP:
            keyCode = KeyConstants.VK_UP;
            break;
        case SWT.ARROW_DOWN:
            keyCode = KeyConstants.VK_DOWN;
            break;
        case SWT.ARROW_LEFT:
            keyCode = KeyConstants.VK_LEFT;
            break;
        case SWT.ARROW_RIGHT:
            keyCode = KeyConstants.VK_RIGHT;
            break;
        case SWT.PAGE_UP:
            keyCode = KeyConstants.VK_PAGE_UP;
            break;
        case SWT.PAGE_DOWN:
            keyCode = KeyConstants.VK_PAGE_DOWN;
            break;
        case SWT.HOME:
            keyCode = KeyConstants.VK_HOME;
            break;
        case SWT.END:
            keyCode = KeyConstants.VK_END;
            break;
        case SWT.INSERT:
            keyCode = KeyConstants.VK_INSERT;
            break;
        case SWT.F1:
            keyCode = KeyConstants.VK_F1;
            break;
        case SWT.F2:
            keyCode = KeyConstants.VK_F2;
            break;
        case SWT.F3:
            keyCode = KeyConstants.VK_F3;
            break;
        case SWT.F4:
            keyCode = KeyConstants.VK_F4;
            break;
        case SWT.F5:
            keyCode = KeyConstants.VK_F5;
            break;
        case SWT.F6:
            keyCode = KeyConstants.VK_F6;
            break;
        case SWT.F7:
            keyCode = KeyConstants.VK_F7;
            break;
        case SWT.F8:
            keyCode = KeyConstants.VK_F8;
            break;
        case SWT.F9:
            keyCode = KeyConstants.VK_F9;
            break;
        case SWT.F10:
            keyCode = KeyConstants.VK_F10;
            break;
        case SWT.F11:
            keyCode = KeyConstants.VK_F11;
            break;
        case SWT.F12:
            keyCode = KeyConstants.VK_F12;
            break;
        case SWT.F13:
            keyCode = KeyConstants.VK_F13;
            break;
        case SWT.F14:
            keyCode = KeyConstants.VK_F14;
            break;
        case SWT.F15:
            keyCode = KeyConstants.VK_F15;
            break;
        case SWT.KEYPAD_MULTIPLY:
            keyCode = KeyConstants.VK_MULTIPLY;
            break;
        case SWT.KEYPAD_ADD:
            keyCode = KeyConstants.VK_ADD;
            break;
        case SWT.KEYPAD_SUBTRACT:
            keyCode = KeyConstants.VK_SUBTRACT;
            break;
        case SWT.KEYPAD_DECIMAL:
            keyCode = KeyConstants.VK_DECIMAL;
            break;
        case SWT.KEYPAD_DIVIDE:
            keyCode = KeyConstants.VK_DIVIDE;
            break;
        case SWT.KEYPAD_0:
            keyCode = KeyConstants.VK_NUMPAD0;
            break;
        case SWT.KEYPAD_1:
            keyCode = KeyConstants.VK_NUMPAD1;
            break;
        case SWT.KEYPAD_2:
            keyCode = KeyConstants.VK_NUMPAD2;
            break;
        case SWT.KEYPAD_3:
            keyCode = KeyConstants.VK_NUMPAD3;
            break;
        case SWT.KEYPAD_4:
            keyCode = KeyConstants.VK_NUMPAD4;
            break;
        case SWT.KEYPAD_5:
            keyCode = KeyConstants.VK_NUMPAD5;
            break;
        case SWT.KEYPAD_6:
            keyCode = KeyConstants.VK_NUMPAD6;
            break;
        case SWT.KEYPAD_7:
            keyCode = KeyConstants.VK_NUMPAD7;
            break;
        case SWT.KEYPAD_8:
            keyCode = KeyConstants.VK_NUMPAD8;
            break;
        case SWT.KEYPAD_9:
            keyCode = KeyConstants.VK_NUMPAD9;
            break;
        case SWT.KEYPAD_EQUAL:
            keyCode = KeyConstants.VK_EQUALS;
            break;
        case SWT.KEYPAD_CR:
            keyCode = KeyConstants.VK_ENTER;
            break;
        case SWT.CR:
            keyCode = KeyConstants.VK_ENTER;
            break;
        case SWT.HELP:
            keyCode = KeyConstants.VK_HELP;
            break;
        case SWT.CAPS_LOCK:
            keyCode = KeyConstants.VK_CAPS_LOCK;
            break;
        case SWT.NUM_LOCK:
            keyCode = KeyConstants.VK_NUM_LOCK;
            break;
        case SWT.SCROLL_LOCK:
            keyCode = KeyConstants.VK_SCROLL_LOCK;
            break;
        case SWT.PAUSE:
            keyCode = KeyConstants.VK_PAUSE;
            break;
        case SWT.BREAK:
            keyCode = KeyConstants.VK_UNDEFINED;
            break;
        case SWT.PRINT_SCREEN:
            keyCode = KeyConstants.VK_PRINTSCREEN;
            break;
        default:
            keyCode = KeyConstants.VK_UNDEFINED;
        }

        // Map SWT char to AWT key codes and AWT chars
        keyChar = KeyConstants.CHAR_UNDEFINED;

        if (inputChar != 0) {

            keyChar = inputChar;

            // Convert letter and number key codes
            if (((inputChar >= '0') && (inputChar <= '9')) || ((inputChar >= 'A') && (inputChar <= 'Z'))) {
                keyCode = inputChar;
            }

            if (((inputChar >= 'a') && (inputChar <= 'z'))) {
                keyCode = inputChar - 0x20;
            }

            // Convert RETURN and ENTER keys
            if (inputKeyCode == SWT.CR) {
                keyChar = '\n';
            } else if (inputKeyCode == SWT.KEYPAD_CR) {
                keyChar = KeyConstants.CHAR_UNDEFINED;
            }

            // Try to get key code from key character
            if (keyCode == KeyConstants.VK_UNDEFINED) {
                switch (inputChar) {
                case '\b':
                    keyCode = KeyConstants.VK_BACK_SPACE;
                    break;
                case 127:
                    keyCode = KeyConstants.VK_DELETE;
                    break;
                case '@':
                    keyCode = KeyConstants.VK_AT;
                    break;
                case '`':
                    keyCode = KeyConstants.VK_BACK_QUOTE;
                    break;
                case '\\':
                    keyCode = KeyConstants.VK_BACK_SLASH;
                    break;
                case '^':
                    keyCode = KeyConstants.VK_CIRCUMFLEX;
                    break;
                case ']':
                    keyCode = KeyConstants.VK_CLOSE_BRACKET;
                    break;
                case ':':
                    keyCode = KeyConstants.VK_COLON;
                    break;
                case ',':
                    keyCode = KeyConstants.VK_COMMA;
                    break;
                case '$':
                    keyCode = KeyConstants.VK_DOLLAR;
                    break;
                case '=':
                    keyCode = KeyConstants.VK_EQUALS;
                    break;
                case '!':
                    keyCode = KeyConstants.VK_EXCLAMATION_MARK;
                    break;
                case '(':
                    keyCode = KeyConstants.VK_LEFT_PARENTHESIS;
                    break;
                case '-':
                    keyCode = KeyConstants.VK_MINUS;
                    break;
                case '*':
                    keyCode = KeyConstants.VK_MULTIPLY;
                    break;
                case '#':
                    keyCode = KeyConstants.VK_NUMBER_SIGN;
                    break;
                case '[':
                    keyCode = KeyConstants.VK_OPEN_BRACKET;
                    break;
                case '.':
                    keyCode = KeyConstants.VK_PERIOD;
                    break;
                case '+':
                    keyCode = KeyConstants.VK_PLUS;
                    break;
                case ')':
                    keyCode = KeyConstants.VK_RIGHT_PARENTHESIS;
                    break;
                case ';':
                    keyCode = KeyConstants.VK_SEMICOLON;
                    break;
                case '/':
                    keyCode = KeyConstants.VK_SLASH;
                    break;
                case '_':
                    keyCode = KeyConstants.VK_UNDERSCORE;
                    break;
                default:
                    keyCode = KeyConstants.VK_UNDEFINED;
                }
            }
        }

    }

    private class RedrawTask implements Runnable {

        private int x;
        private int y;
        private int width;
        private int height;
        private Canvas canvas;

        public RedrawTask(Canvas canvas) {
            this.canvas = canvas;
        }

        public void setRegion(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void run() {
            canvas.redraw(x, y, width, height, true);
            //canvas.update();
        }

    }

}
