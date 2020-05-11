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
package org.thenesis.midpath.demo.ui.gauge;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;


/**
 * This MIDlet demonstrates the different types of gauges supported by MIDP-2.0:
 *      Interactive -
 *      Non Interactive -
 *      Interactive -
 *      Indefinite Incremental
 *
 * @version 2.0
 */
public class GaugeDemo extends MIDlet implements CommandListener {
    private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
    private Display display;
    private NonInteractiveGaugeRunnable nonInteractive;
    private IncrementalIndefiniteGaugeRunnable indefinite;
    private Form mainForm;

    public GaugeDemo() {
        display = Display.getDisplay(this);

        mainForm = new Form("Gauge Demo");
        //
        mainForm.append(new Gauge("Interactive", true, 10, 0));
        //
        nonInteractive = new NonInteractiveGaugeRunnable("Non Interactive", 10, 0);
        new Thread(nonInteractive).start();
        mainForm.append(nonInteractive);
        //
        mainForm.append(new Gauge("Indefinite - Running", false, Gauge.INDEFINITE,
                Gauge.CONTINUOUS_RUNNING));

        indefinite = new IncrementalIndefiniteGaugeRunnable("Indefinite - Incremental");
        new Thread(indefinite).start();
        mainForm.append(indefinite);
        mainForm.addCommand(CMD_EXIT);
        mainForm.setCommandListener(this);
    }

    /**
     * Signals the MIDlet to start and enter the Active state.
     */
    protected void startApp() {
        display.setCurrent(mainForm);
    }

    /**
     * Signals the MIDlet to terminate and enter the Destroyed state.
     */
    protected void destroyApp(boolean unconditional) {
        nonInteractive.setDone();

        indefinite.setDone();
    }

    /**
     * Signals the MIDlet to stop and enter the Paused state.
     */
    protected void pauseApp() {
    }

    public void commandAction(Command c, Displayable d) {
        destroyApp(false);
        notifyDestroyed();
    }
}
