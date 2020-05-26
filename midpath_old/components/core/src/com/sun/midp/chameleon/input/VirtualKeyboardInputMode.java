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
package com.sun.midp.chameleon.input;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.TextField;

import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.graphics.VirtualKeyboard;

import com.sun.midp.chameleon.skins.TextFieldSkin;
import com.sun.midp.lcdui.Text;
import com.sun.midp.lcdui.TextCursor;
import com.sun.midp.lcdui.TextInfo;

/**
 * An InputMode instance which allows to write text from a virtual keyboard.
 */
public class VirtualKeyboardInputMode implements InputMode {

    /** The InputModeMediator for the current input session */
    protected InputModeMediator mediator;

    /** Symbol table */
    protected final VirtualKeyboardForm keyboardForm = new VirtualKeyboardForm("Keyboard");

    private int constraints;
    
    /**
     * This method is called to determine if this InputMode supports the given
     * text input constraints. The semantics of the constraints value are
     * defined in the javax.microedition.lcdui.TextField API. If this InputMode
     * returns false, this InputMode must not be used to process key input for
     * the selected text component.
     * 
     * @param constraints
     *            text input constraints. The semantics of the constraints value
     *            are defined in the TextField API.
     * 
     * @return true if this InputMode supports the given text component
     *         constraints, as defined in the MIDP TextField API
     */
    public boolean supportsConstraints(int constraints) {
//        switch (constraints & TextField.CONSTRAINT_MASK) {
//        case TextField.NUMERIC:
//        case TextField.DECIMAL:
//        case TextField.PHONENUMBER:
//            return false;
//        default:
//            return true;
//        }
        
        return true;
    }

    /**
     * Returns the display name which will represent this InputMode to the user,
     * such as in a selection list or the softbutton bar.
     * 
     * @return the locale-appropriate name to represent this InputMode to the
     *         user
     */
    public String getName() {
        return "Keyboard";
    }

    /**
     * Returns the command name which will represent this InputMode to the user
     * 
     * @return the locale-appropriate command name to represent this InputMode
     *         to the user
     */
    public String getCommandName() {
        return getName();
    }

    /**
     * This method will be called before any input keys are passed to this
     * InputMode to allow the InputMode to perform any needed initialization. A
     * reference to the InputModeMediator which is currently managing the
     * relationship between this InputMode and the input session is passed in.
     * This reference can be used by this InputMode to commit text input as well
     * as end the input session with this InputMode. The reference is only valid
     * until this InputMode's endInput() method is called.
     * 
     * @param constraints
     *            text input constraints. The semantics of the constraints value
     *            are defined in the TextField API.
     * 
     * @param mediator
     *            the InputModeMediator which is negotiating the relationship
     *            between this InputMode and the input session
     * 
     * @param inputSubset
     *            current input subset
     */
    public void beginInput(InputModeMediator mediator, String inputSubset, int constraints) {
        validateState(false);
        this.mediator = mediator;
        this.constraints = constraints;
        keyboardForm.getTextfield().setText(mediator.getInitialText());
    }

    /**
     * Symbol Input mode is represented by Symbol table implemented as canvas
     * 
     * @return SymbolTable
     */
    public Displayable getDisplayable() {
        return keyboardForm;
    }

    /**
     * Process the given key code as input.
     * 
     * This method will return true if the key was processed successfully, false
     * otherwise.
     * 
     * @param keyCode
     *            the keycode of the key which was input
     * @param longPress
     *            return true if it's long key press otherwise false
     * @return true if the key was processed by this InputMode, false otherwise.
     */
    public int processKey(int keyCode, boolean longPress) {
        return KEYCODE_NONE;
    }

    /**
     * return the pending char used to bypass the asynchronous commit mechanism
     * e.g. to immediately commit a char before moving the cursor
     * 
     * @return return the pending char
     */
    public char getPendingChar() {
        return 0;
    }

    /**
     * Return the next possible match for the key input processed thus far by
     * this InputMode. A call to this method should be preceeded by a check of
     * hasMoreMatches(). If the InputMode has more available matches for the
     * given input, this method will return them one by one.
     * 
     * @return a String representing the next available match to the key input
     *         thus far
     */
    public String getNextMatch() {
        return null;
    }

    /**
     * True, if after processing a key, there is more than one possible match to
     * the input. If this method returns true, the getNextMatch() method can be
     * called to return the value.
     * 
     * @return true if after processing a key, there is more than the one
     *         possible match to the given input
     */
    public boolean hasMoreMatches() {
        return false;
    }

    /**
     * Gets the possible string matches
     * 
     * @return returns the set of options.
     */
    public String[] getMatchList() {
        return new String[0];
    }

    /**
     * Mark the end of this InputMode's processing. The only possible call to
     * this InputMode after a call to endInput() is a call to beginInput() to
     * begin a new input session.
     */
    public void endInput() {
        validateState(true);
        mediator = null;
    }

    /**
     * Returns true if input mode is using its own displayable, false ifinput
     * mode does not require the speial displayable for its representation. For
     * Symbol mode is represented by Symbol table canvas, so it returns true
     * 
     * @return true if input mode is using its own displayable, otherwise false
     */
    public boolean hasDisplayable() {
        return true;
    }

    /**
     * This method will validate the state of this InputMode. If this is a check
     * for an "active" operation, the TextInputMediator must be non-null or else
     * this method will throw an IllegalStateException. If this is a check for
     * an "inactive" operation, then the TextInputMediator should be null.
     * 
     * @param activeOperation
     *            true if any operation is active otherwise false.
     */
    protected void validateState(boolean activeOperation) {
        if (activeOperation && mediator == null) {
            throw new IllegalStateException("Illegal operation on an input session already in progress");
        } else if (!activeOperation && mediator != null) {
            throw new IllegalStateException("Illegal operation on an input session which is not in progress");
        }
    }

    /**
     * Complete current input mode
     * 
     * @param commit
     *            true if the symbol has to be committed otherwise false
     */
    protected void completeInputMode(boolean commit) {
        if (commit) {
            mediator.commit(keyboardForm.getTextfield().getString(), true);
        }
        mediator.inputModeCompleted();
    }

    /** input subset x constraint map */
    //private static final boolean[][] isMap = new boolean[TextInputSession.INPUT_SUBSETS.length][TextInputSession.MAX_CONSTRAINTS];
    private static final boolean[][] isMap = {
        // |ANY|EMAILADDR|NUMERIC|PHONENUMBER|URL|DECIMAL
        { false, false, false, false, false, false }, // IS_FULLWIDTH_DIGITS
        { false, false, false, false, false, false }, // IS_FULLWIDTH_LATIN  
        { true,  true,  false, false, true,  false }, // IS_HALFWIDTH_KATAKANA
        { true,  true,  false, false, true,  false }, // IS_HANJA          
        { true,  true,  false, false, true,  false }, // IS_KANJI           
        { false, false, false, false, false, false }, // IS_LATIN           
        { false, false, false, false, false, false }, // IS_LATIN_DIGITS    
        { true,  true,  false, false, true,  false }, // IS_SIMPLIFIED_HANZI
        { true,  true,  false, false, true,  false }, // IS_TRADITIONAL_HANZI
        { false, false, false, false, false, false }, // MIDP_UPPERCASE_LATIN
        { false, false, false, false, false, false }, // MIDP_LOWERCASE_LATIN
        { true,  true,  false, false, true,  false }  // NULL
    };
    
    
    /**
     * Returns the map specifying this input mode is proper one for the
     * particular pair of input subset and constraint. The form of the map is
     * 
     * |ANY|EMAILADDR|NUMERIC|PHONENUMBER|URL|DECIMAL|
     * ---------------------------------------------------------------------
     * IS_FULLWIDTH_DIGITS |t|f| t|f | t|f | t|f |t|f| t|f | IS_FULLWIDTH_LATIN
     * |t|f| t|f | t|f | t|f |t|f| t|f | IS_HALFWIDTH_KATAKANA |t|f| t|f | t|f |
     * t|f |t|f| t|f | IS_HANJA |t|f| t|f | t|f | t|f |t|f| t|f | IS_KANJI |t|f|
     * t|f | t|f | t|f |t|f| t|f | IS_LATIN |t|f| t|f | t|f | t|f |t|f| t|f |
     * IS_LATIN_DIGITS |t|f| t|f | t|f | t|f |t|f| t|f | IS_SIMPLIFIED_HANZI
     * |t|f| t|f | t|f | t|f |t|f| t|f | IS_TRADITIONAL_HANZI |t|f| t|f | t|f |
     * t|f |t|f| t|f | MIDP_UPPERCASE_LATIN |t|f| t|f | t|f | t|f |t|f| t|f |
     * MIDP_LOWERCASE_LATIN |t|f| t|f | t|f | t|f |t|f| t|f | NULL |t|f| t|f |
     * t|f | t|f |t|f| t|f |
     * 
     * @return input subset x constraint map
     */
    public boolean[][] getIsConstraintsMap() {
        return isMap;
    }

    /**
     * A form which contains a virtual keyboard and a text pad.
     */
    class VirtualKeyboardForm extends Form implements CommandListener {

        private InternalTextField textField;
        private VirtualKeyboardItem keyboardItem;
        private Command cancelCommand = new Command("Cancel", Command.CANCEL, 1);
        private Command okCommand = new Command("Ok", Command.OK, 1);

        public VirtualKeyboardForm(String title) {
            super(title);
            textField = new InternalTextField("Pad");
            append(textField);
            textField.setLayout(Item.LAYOUT_CENTER);
            keyboardItem = new VirtualKeyboardItem("Keyboard");
            keyboardItem.setLayout(Item.LAYOUT_CENTER);
            append(keyboardItem);

            addCommand(cancelCommand);
            addCommand(okCommand);
            setCommandListener(VirtualKeyboardForm.this);
        }

        public void commandAction(Command c, Displayable s) {
            if (c == cancelCommand) {
                completeInputMode(false);
            } else if (c == okCommand) {
                completeInputMode(true);
            }
        }

        public Item getFirstFocusableItem() {
            return keyboardItem;
        }

        public void handleKey(int key) {
            if (key >= 0) {
                //System.out.println("key " + key);
                switch (key) {
//                case 10: // Enter
//                    completeInputMode(true);
//                    break;
//                case 24: // Cancel
//                    completeInputMode(false);
//                    break;
                case 8: // Backspace
                    textField.delete(1);
                    break;
                case VirtualKeyboard.KEY_UP:
                    textField.moveCursorUp();
                    break;
                case VirtualKeyboard.KEY_DOWN:
                    textField.moveCursorDown();
                    break;
                case VirtualKeyboard.KEY_LEFT:
                    textField.moveCursorLeft();
                    break;
                case VirtualKeyboard.KEY_RIGHT:
                    textField.moveCursorRight();
                    break;
                default:
                    textField.insert(String.valueOf((char) key));
                }
            }
        }

        public void reset() {
            textField.buffer.setLength(0);
        }

        public InternalTextField getTextfield() {
            return textField;
        }

        class InternalTextField extends CustomItem {

            private static final int INSET = 1;
            private int minWidth;
            private int minHeight;
            private StringBuffer buffer;
            private Font font;
            private TextInfo info = new TextInfo(4);
            private TextCursor cursor;

            protected InternalTextField(String label) {
                super(label);
                font = Font.getDefaultFont();
                minWidth = VirtualKeyboardForm.this.getWidth() * 9 / 10; //  160; //font.charWidth('A') * 20;
                minHeight = (VirtualKeyboardForm.this.getHeight() - VirtualKeyboard.HEIGHT) / 2; //font.getHeight() + INSET;
                buffer = new StringBuffer(20);
                cursor = new TextCursor(buffer.length());
            }

            protected int getMinContentHeight() {
                return minHeight;
            }

            protected int getMinContentWidth() {
                return minWidth;
            }

            protected int getPrefContentHeight(int width) {
                return getMinContentHeight();
            }

            protected int getPrefContentWidth(int height) {
                return getMinContentWidth();
            }

            protected void paint(Graphics g, int w, int h) {

                g.setColor(0xFF000000);
                g.drawRect(0, 0, w, h);
                g.drawLine(INSET, INSET, w - INSET, INSET);
                g.drawLine(INSET, INSET, INSET, h - INSET);

                int offset = 0; // Buggy ?
                int offsetXY = 4;
                int options = Text.NORMAL;
                
                String str;
                if ((constraints & TextField.PASSWORD) == TextField.PASSWORD) {
                    int length = buffer.length();
                    StringBuffer sb = new StringBuffer(length);
                    for (int i = 0; i < length; i++) {
                        sb.append('*');
                    }
                    str = sb.toString();
                } else {
                    str = buffer.toString();
                }
                
                info.isModified = true;
                //cursor.index = str.length();
                Text.updateTextInfo(str, font, w - offsetXY, h - offsetXY, offset, options, cursor, info);

                int fgColor = TextFieldSkin.COLOR_FG;

                g.translate(offsetXY, offsetXY);
                //Text.paint(g, str, font, fgColor, 0xffffff - fgColor, w, h, offset, options, cursor);
                Text.paintText(info, g, str, font, fgColor, 0xffffff - fgColor, w - offsetXY, h - offsetXY, offset, options, cursor);
                g.translate(-offsetXY, -offsetXY);

            }

            /* TextField-like API */
            
            public void insert(String s) {
                insert(s, cursor.index);
            }

            public void insert(String s, int pos) {
                buffer.insert(pos, s);
                cursor.index += s.length();
                repaint();
            }

            public void setText(String text) {
                buffer.setLength(0);
                buffer.append(text);
                cursor.index = text.length();
            }

            public int size() {
                return buffer.length();
            }
            
            public void delete(int length) {
                delete(cursor.index - 1, length);
                repaint();
            }

            public void delete(int offset, int length) {
                buffer.delete(offset, offset + length);
                int diff = cursor.index - offset;
                cursor.index -= (diff < length) ? diff : length;
                repaint();
            }
            
            public void moveCursorLeft() {
                cursor.index--;
                repaint();
            }

            public void moveCursorRight() {
                cursor.index++;
                repaint();
            }

            public void moveCursorDown() {
                int cursorPosInLine = cursor.index - info.lineStart[info.cursorLine];
                
                if (info.lineStart.length > info.cursorLine + 1) {
                    cursor.index = info.lineStart[info.cursorLine + 1] + cursorPosInLine;
                }
                
                if (cursor.index > buffer.length()) {
                    cursor.index = buffer.length();
                }
                //info.scroll(TextInfo.FORWARD);
                
                repaint();
            }

            public void moveCursorUp() {
                int cursorPosInLine = cursor.index - info.lineStart[info.cursorLine];
                
                if (info.cursorLine - 1 >= 0) {
                    cursor.index = info.lineStart[info.cursorLine - 1] + cursorPosInLine;
                }
                
                if (cursor.index < 0) {
                    cursor.index = 0;
                }
                //info.scroll(TextInfo.BACK);

                repaint();
            }

            public String getString() {
                return buffer.toString();
            }

        }

        class VirtualKeyboardItem extends CustomItem {

            private VirtualKeyboard keyboard = new VirtualKeyboard();
            private int[] buffer;

            protected VirtualKeyboardItem(String label) {
                super(label);
                buffer = new int[VirtualKeyboard.HEIGHT * VirtualKeyboard.WIDTH];
                keyboard.activateCursor(true);

            }

            protected int getMinContentHeight() {
                return VirtualKeyboard.HEIGHT;
            }

            protected int getMinContentWidth() {
                return VirtualKeyboard.WIDTH;
            }

            protected int getPrefContentHeight(int width) {
                return getMinContentHeight();
            }

            protected int getPrefContentWidth(int height) {
                return getMinContentWidth();
            }

            protected void paint(Graphics g, int w, int h) {
                if (keyboard.isDirty()) {
                    keyboard.draw(buffer);
                }
                int posX = (w - VirtualKeyboard.WIDTH) / 2;
                int posY = (h - VirtualKeyboard.HEIGHT) / 2;
                g.drawRGB(buffer, 0, VirtualKeyboard.WIDTH, posX, posY, VirtualKeyboard.WIDTH, VirtualKeyboard.HEIGHT, false);
            }

            public void keyPressed(int keyCode) {

                try {
                    int gameAction = getGameAction(keyCode);
                    switch (gameAction) {
                    case Canvas.UP:
                        keyboard.moveUp();
                        break;
                    case Canvas.DOWN:
                        keyboard.moveDown();
                        break;
                    case Canvas.LEFT:
                        keyboard.moveLeft();
                        break;
                    case Canvas.RIGHT:
                        keyboard.moveRight();
                        break;
                    case Canvas.FIRE:
                        int key = keyboard.pushKey();
                        handleKey(key);
                        break;
                    }

                    if (keyboard.isDirty())
                        repaint();

                } catch (IllegalArgumentException e) {
                    if (Logging.TRACE_ENABLED) {
                        System.out.println("[DEBUG] VirtualKeyboardInputMode.keyPressed: keyCode a valid key code");
                    }
                }

            }

            public void keyReleased(int keyCode) {
                keyboard.releaseKey();
                if (keyboard.isDirty())
                    repaint();
            }

            public void pointerPressed(int x, int y) {
                int key = keyboard.pushKey(x, y);
                handleKey(key);

                if (keyboard.isDirty())
                    repaint();
            }

            public void pointerReleased(int x, int y) {
                keyboard.releaseKey();
                if (keyboard.isDirty())
                    repaint();
            }

            protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {

                switch (dir) {
                case Canvas.UP:
                    keyboard.moveUp();
                    break;
                case Canvas.DOWN:
                    keyboard.moveDown();
                    break;
                case Canvas.LEFT:
                    keyboard.moveLeft();
                    break;
                case Canvas.RIGHT:
                    keyboard.moveRight();
                    break;
                }

                if (keyboard.isDirty())
                    repaint();

                return true;
            }

        }

    }

}
