/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.ui.textbox;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.MIDlet;


/**
 * The textbox demo displays a list of all the text box types and allows the
 * user to select a specific type of text box to try.
 *
 * @version 2.0
 */
public class TextBoxDemo extends MIDlet implements CommandListener {
    private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
    private static final Command CMD_BACK = new Command("Back", Command.BACK, 1);
    private static final Command CMD_SHOW = new Command("Show", Command.SCREEN, 1);

    /**
     * The labels for the supported textboxs.
     */
    static final String[] textBoxLabels =
        { "Any Character", "E-Mail", "Number", "Decimal", "Phone", "Url" };

    /**
     * The supported textbox types.
     */
    static final int[] textBoxTypes =
        {
            TextField.ANY, TextField.EMAILADDR, TextField.NUMERIC, TextField.DECIMAL,
            TextField.PHONENUMBER, TextField.URL
        };
    private Display display;
    private ChoiceGroup types;
    private ChoiceGroup options;
    private Form mainForm;
    private boolean firstTime;

    public TextBoxDemo() {
        display = Display.getDisplay(this);
        firstTime = true;
    }

    protected void startApp() {
        if (firstTime) {
            mainForm = new Form("Select a Text Box Type");
            mainForm.append("Select a text box type");

            // the string elements will have no images
            Image[] imageArray = null;

            types = new ChoiceGroup("Choose type", Choice.EXCLUSIVE, textBoxLabels, imageArray);
            mainForm.append(types);

            // advanced options
            String[] optionStrings = { "As Password", "Show Ticker" };
            options = new ChoiceGroup("Options", Choice.MULTIPLE, optionStrings, null);
            mainForm.append(options);
            mainForm.addCommand(CMD_SHOW);
            mainForm.addCommand(CMD_EXIT);
            mainForm.setCommandListener(this);
            firstTime = false;
        }

        display.setCurrent(mainForm);
    }

    protected void destroyApp(boolean unconditional) {
    }

    protected void pauseApp() {
    }

    public void commandAction(Command c, Displayable d) {
        if (c == CMD_EXIT) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == CMD_SHOW) {
            // these are the images and strings for the choices.
            Image[] imageArray = null;
            int index = types.getSelectedIndex();
            String title = textBoxLabels[index];
            int choiceType = textBoxTypes[index];
            boolean[] flags = new boolean[2];
            options.getSelectedFlags(flags);

            if (flags[0]) {
                choiceType |= TextField.PASSWORD;
            }

            TextBox textBox = new TextBox(title, "", 50, choiceType);

            if (flags[1]) {
                textBox.setTicker(new Ticker("TextBox: " + title));
            }

            textBox.addCommand(CMD_BACK);
            textBox.setCommandListener(this);
            display.setCurrent(textBox);
        } else if (c == CMD_BACK) {
            display.setCurrent(mainForm);
        }
    }
}
