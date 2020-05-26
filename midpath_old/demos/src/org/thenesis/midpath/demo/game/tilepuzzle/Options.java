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
package org.thenesis.midpath.demo.game.tilepuzzle;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;


public class Options extends Form implements CommandListener {
    boolean reversed;
    boolean funny;
    boolean hard;
    Command ok;
    Command cancel;
    Display dpy;
    Displayable prev;
    ChoiceGroup cg1;
    ChoiceGroup cg2;
    boolean[] scratch;

    Options(Display dpy_, Displayable prev_) {
        super("Options");

        dpy = dpy_;
        prev = prev_;

        scratch = new boolean[2];

        // set up default values
        reversed = false;
        funny = false;
        hard = true;

        cg1 = new ChoiceGroup(null, Choice.MULTIPLE);
        cg1.append("reverse arrows", null);
        cg1.append("funny shuffle", null);
        append(cg1);

        // use a label here
        append("level:");

        cg2 = new ChoiceGroup(null, Choice.EXCLUSIVE);
        cg2.append("easy", null);
        cg2.append("hard", null);
        append(cg2);

        loadUI();

        ok = new Command("OK", Command.OK, 0);
        cancel = new Command("Cancel", Command.CANCEL, 1);

        addCommand(ok);
        addCommand(cancel);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == ok) {
            readUI();
        } else if (c == cancel) {
            loadUI();
        }

        dpy.setCurrent(prev);
    }

    void loadUI() {
        cg1.setSelectedIndex(0, reversed);
        cg1.setSelectedIndex(1, funny);

        cg2.setSelectedIndex((hard ? 1 : 0), true);
    }

    void readUI() {
        reversed = cg1.isSelected(0);
        funny = cg1.isSelected(1);

        hard = cg2.isSelected(1);
    }
}
