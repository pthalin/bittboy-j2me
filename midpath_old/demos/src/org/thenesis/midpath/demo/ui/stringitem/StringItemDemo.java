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
package org.thenesis.midpath.demo.ui.stringitem;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;


/**
 * This is the main class for the UI MIDP demo. This demo of the components
 * available in the UI is designed for simplicity. Each UI element is
 * demonstrated within a class of its own derived from either BaseDemo
 * or BaseListDemo.
 *
 * @version 2.0
 */
public class StringItemDemo extends MIDlet implements CommandListener, ItemCommandListener {
    private static final Command CMD_GO = new Command("Go", Command.ITEM, 1);
    private static final Command CMD_PRESS = new Command("Press", Command.ITEM, 1);
    private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
    private Display display;
    private Form mainForm;

    /**
     * Signals the MIDlet to start and enter the Active state.
     */
    protected void startApp() {
        display = Display.getDisplay(this);

        mainForm = new Form("String Item Demo");
        mainForm.append("This is a simple label");

        StringItem item =
            new StringItem("This is a StringItem label: ", "This is the StringItems text");
        mainForm.append(item);
        item = new StringItem("Short label: ", "text");
        mainForm.append(item);
        item = new StringItem("Hyper-Link ", "hyperlink", Item.HYPERLINK);
        item.setDefaultCommand(CMD_GO);
        item.setItemCommandListener(this);
        mainForm.append(item);
        item = new StringItem("Button ", "Button", Item.BUTTON);
        item.setDefaultCommand(CMD_PRESS);
        item.setItemCommandListener(this);
        mainForm.append(item);
        mainForm.addCommand(CMD_EXIT);
        mainForm.setCommandListener(this);
        display.setCurrent(mainForm);
    }

    public void commandAction(Command c, Item item) {
        if (c == CMD_GO) {
            String text = "Go to the URL...";
            Alert a = new Alert("URL", text, null, AlertType.INFO);
            display.setCurrent(a);
        } else if (c == CMD_PRESS) {
            String text = "Do an action...";
            Alert a = new Alert("Action", text, null, AlertType.INFO);
            display.setCurrent(a);
        }
    }

    public void commandAction(Command c, Displayable d) {
        destroyApp(false);
        notifyDestroyed();
    }

    /**
     * Signals the MIDlet to terminate and enter the Destroyed state.
     */
    protected void destroyApp(boolean unconditional) {
    }

    /**
     * Signals the MIDlet to stop and enter the Paused state.
     */
    protected void pauseApp() {
    }
}
