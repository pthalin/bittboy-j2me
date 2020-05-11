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
package org.thenesis.midpath.demo.ui.ticker;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.MIDlet;


/**
 * The Ticker Demo simply sets a rather long ticker to the screen.
 *
 * @version 2.0
 */
public class TickerDemo extends MIDlet implements CommandListener {
    /**
     * This is the text displayed in the ticker.
     */
    private static final String TICKER_TEXT =
        "This is a ticker see it " + "scroll along the way... On and on it goes without " +
        "stopping even for a second!";
    private boolean firstTime;
    private Form mainForm;
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private Display myDisplay;
    private Ticker t;

    public TickerDemo() {
        myDisplay = Display.getDisplay(this);
        firstTime = true;
        mainForm = new Form("Ticker");
        mainForm.setCommandListener(this);
    }

    protected void startApp() {
        if (firstTime) {
            t = new Ticker(TICKER_TEXT);
            mainForm.setTicker(t);
            firstTime = false;
        }

        mainForm.addCommand(exitCommand);
        myDisplay.setCurrent(mainForm);
    }

    protected void destroyApp(boolean unconditional) {
        myDisplay.setCurrent((Displayable)null);
        notifyDestroyed();
    }

    protected void pauseApp() {
    }

    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
            destroyApp(true);
        }
    }
}
