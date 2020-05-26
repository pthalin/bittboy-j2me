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

import java.util.Random;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.midlet.MIDlet;


public class Board extends Canvas implements CommandListener {
    // commands
    static final int CMD_EXIT = 1;
    static final int CMD_OPTIONS = 2;
    static final int CMD_RESET = 3;
    static final int CMD_START = 4;
    static final int CMD_UNLOCK = 5;
    static final int CMD_ZLAST = 6; // must be ze last, of course

    // state variables
    static final int INITIALIZED = 0;
    static final int PLAYING = 1;
    static final int WON = 2;
    MIDlet midlet;
    Display dpy;
    Options options;

    // this string must be exactly 15 characters long
    String letters = "RATEYOURMINDPAL";
    Font font;
    Piece blankp;
    Piece[] all;
    Piece[][] grid;
    Random rand;

    // grid origin in pixels
    int gridx;
    int gridy;

    // grid width and height, in cells
    int gridw;
    int gridh;

    // cell geometry in pixels
    int cellw;
    int cellh;
    int cellyoff;
    int cellxoff;
    Command[] cmd;
    int gameState;
    boolean cheated;

    public Board(MIDlet midlet_) {
        int i;

        // "global" variables
        midlet = midlet_;
        dpy = Display.getDisplay(midlet);
        gridw = 4;
        gridh = 4;

        font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

        // update when font metrics info gets implemented
        cellw = font.charWidth('M') + 7;
        cellh = font.getHeight() + 1;
        cellxoff = 3;
        cellyoff = 0;

        gridx = (getWidth() - (gridw * cellw) + 1) / 2;
        gridy = 10;

        cheated = false;
        rand = new Random();

        // create the grid arrays
        grid = new Piece[gridw][];

        for (i = 0; i < gridw; i++) {
            grid[i] = new Piece[gridh];
        }

        all = new Piece[gridw * gridh];

        for (i = 0; i < ((gridw * gridh) - 1); i++) {
            int x = i % gridw;
            int y = i / gridw;
            String s = letters.substring(i, i + 1);
            grid[x][y] = all[i] = new Piece(s, i, x, y, i < ((gridw * gridh) / 2));
        }

        // make the special blank piece
        blankp = new Piece(null, (gridw * gridh) - 1, gridw - 1, gridh - 1, false);
        grid[gridw - 1][gridh - 1] = blankp;
        all[(gridw * gridh) - 1] = blankp;

        // set up commands
        cmd = new Command[CMD_ZLAST];

        cmd[CMD_EXIT] = new BoardCommand("Exit", Command.EXIT, 6, CMD_EXIT);

        cmd[CMD_OPTIONS] = new BoardCommand("Options", Command.SCREEN, 3, CMD_OPTIONS);

        cmd[CMD_RESET] = new BoardCommand("Reset", Command.SCREEN, 1, CMD_RESET);

        cmd[CMD_START] = new BoardCommand("Start", Command.SCREEN, 1, CMD_START);

        cmd[CMD_UNLOCK] = new BoardCommand("Unlock", Command.SCREEN, 4, CMD_UNLOCK);

        // set up the listener
        setCommandListener(this);

        // set up options screen
        options = new Options(dpy, this);

        // set up initial state
        setState(INITIALIZED);
    }

    void D(String s) {
        System.out.println(s);
    }

    void setGrid(Piece p, int x, int y) {
        grid[x][y] = p;
        p.setLocation(x, y);
    }

    // swap the piece at sx, sy with the blank piece
    // assumes that this is a legal move
    void moveBlank(int swapx, int swapy) {
        setGrid(grid[swapx][swapy], blankp.x, blankp.y);
        setGrid(blankp, swapx, swapy);
    }

    // swaps the pieces at (x1, y1) and (x2, y2)
    // no parity checking is done!    
    void swap(int x1, int y1, int x2, int y2) {
        Piece t = grid[x1][y1];
        setGrid(grid[x2][y2], x1, y1);
        setGrid(t, x2, y2);
    }

    boolean isSolved() {
        for (int i = 0; i < gridh; i++) {
            for (int j = 0; j < gridw; j++) {
                if (!grid[j][i].isHome()) {
                    return false;
                }
            }
        }

        return true;
    }

    // return a random integer in the range [0..n)
    int randRange(int n) {
        int r = rand.nextInt() % n;

        if (r < 0) {
            r += n;
        }

        return r;
    }

    // randomize by making random moves
    void randomize_by_moving() {
        int dx;
        int dy;
        int v;

        for (int i = 0; i < 100; i++) {
            dx = dy = 0;
            v = (rand.nextInt() & 2) - 1; // 1 or -1

            if ((rand.nextInt() & 1) == 0) {
                dx = v;
            } else {
                dy = v;
            }

            if ((blankp.x + dx) < 0) {
                dx = 1;
            }

            if ((blankp.x + dx) == gridw) {
                dx = -1;
            }

            if ((blankp.y + dy) < 0) {
                dy = 1;
            }

            if ((blankp.y + dy) == gridh) {
                dy = -1;
            }

            moveBlank(blankp.x + dx, blankp.y + dy);
        }

        // now move the blank tile to the lower right corner
        while (blankp.x != (gridw - 1))
            moveBlank(blankp.x + 1, blankp.y);

        while (blankp.y != (gridh - 1))
            moveBlank(blankp.x, blankp.y + 1);
    }

    // shuffle the tiles randomly and place the blank at the bottom right
    void shuffle() {
        int limit = (gridw * gridh) - 1;
        Piece[] ta = new Piece[limit];
        Piece temp;

        System.arraycopy(all, 0, ta, 0, limit);

        for (int i = 0; i < limit; i++) {
            int j = randRange(limit);
            temp = ta[j];
            ta[j] = ta[i];
            ta[i] = temp;
        }

        for (int i = 0; i < limit; i++) {
            setGrid(ta[i], i / gridw, i % gridw);
        }

        setGrid(blankp, gridw - 1, gridh - 1);
    }

    void randomize(boolean hard) {
        shuffle();

        int ra;
        int rb;
        int x;
        int y;

        if (hard) {
            ra = 7;
            rb = 0;
        } else {
            ra = 0;
            rb = 7;
        }

        x = rand.nextInt() & 1;
        y = rand.nextInt() & 1;

        if ((x == 1) && (y == 1)) {
            x = 2;
            y = 0;
        }

        swap(x, y, all[ra].x, all[ra].y);
        swap((rand.nextInt() & 1) + 1, 3, all[rb].x, all[rb].y);

        if ((displacement() & 1) == 1) {
            swap(1, 3, 2, 3);
        }
    }

    // Compute and return the displacement, that is, the number of
    // pairs of tiles that are out of order.  The blank tile *must*
    // be in the lower right corner.
    int displacement() {
        boolean[] temp = new boolean[(gridw * gridh) - 1]; // all false
        int n = 0;

        for (int i = 0; i < gridh; i++) {
            for (int j = 0; j < gridw; j++) {
                Piece p = grid[j][i];

                if (p == blankp) {
                    continue;
                }

                temp[p.serial] = true;

                for (int k = 0; k < p.serial; k++) {
                    if (!temp[k]) {
                        n++;
                    }
                }
            }
        }

        return n;
    }

    void resetGrid() {
        Piece[] temp = new Piece[gridw * gridh];
        int k = 0;

        for (int i = 0; i < gridw; i++) {
            for (int j = 0; j < gridh; j++) {
                temp[k++] = grid[i][j];
            }
        }

        for (k = 0; k < temp.length; k++) {
            temp[k].goHome();
        }
    }

    void rearrangeFunnily(boolean hard) {
        resetGrid();

        if (hard) {
            // RATE YOUR MIDP LAN
            swap(0, 0, 3, 1);
            swap(2, 2, 3, 2);
            swap(3, 2, 0, 3);
            swap(0, 3, 2, 3);
        } else {
            // RATE YOUR MIDP NAL
            swap(2, 2, 3, 2);
            swap(3, 2, 0, 3);
        }
    }

    void setState(int ns) {
        gameState = ns;

        switch (gameState) {
        case INITIALIZED:
            removeCommand(cmd[CMD_RESET]);
            addCommand(cmd[CMD_START]);
            addCommand(cmd[CMD_UNLOCK]);
            addCommand(cmd[CMD_EXIT]);
            addCommand(cmd[CMD_OPTIONS]);

            break;

        case PLAYING:
            addCommand(cmd[CMD_RESET]);
            removeCommand(cmd[CMD_START]);
            removeCommand(cmd[CMD_UNLOCK]);
            addCommand(cmd[CMD_EXIT]);
            addCommand(cmd[CMD_OPTIONS]);

            break;

        case WON:
            removeCommand(cmd[CMD_RESET]);
            addCommand(cmd[CMD_START]);
            addCommand(cmd[CMD_UNLOCK]);
            addCommand(cmd[CMD_EXIT]);
            addCommand(cmd[CMD_OPTIONS]);

            break;
        }
    }

    public void commandAction(Command c, Displayable d) {
        switch (((BoardCommand)c).tag) {
        case CMD_EXIT:
            midlet.notifyDestroyed();

            break;

        case CMD_OPTIONS:
            dpy.setCurrent(options);

            break;

        case CMD_RESET:
            cheated = false;
            resetGrid();
            setState(INITIALIZED);
            repaint();

            break;

        case CMD_START:
            cheated = false;

            if (options.funny) {
                rearrangeFunnily(options.hard);
            } else {
                randomize(options.hard);
            }

            setState(PLAYING);
            repaint();

            break;

        case CMD_UNLOCK:
            cheated = true;
            setState(PLAYING);
            repaint();

            break;
        }
    }

    public void showNotify() {
        // System.out.println("Board: showNotify");
    }

    public void hideNotify() {
        // System.out.println("Board: hideNotify");
    }

    public void paint(Graphics g) {
        g.setColor(0xFFFFFF);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.translate(gridx, gridy);
        g.setColor(0);
        g.drawRect(-2, -2, (gridw * cellw) + 2, (gridh * cellh) + 2);

        for (int j = 0; j < gridw; j++) {
            for (int k = 0; k < gridh; k++) {
                grid[j][k].paint(g);
            }
        }

        if (gameState == WON) {
            g.translate(-g.getTranslateX(), -g.getTranslateY());
            g.setColor(0);
            g.setFont(Font.getDefaultFont());
            g.drawString((cheated ? "CHEATER!" : "YOU WIN!"), getWidth() / 2, getHeight() - 1,
                Graphics.BOTTOM | Graphics.HCENTER);
        }
    }

    public void keyPressed(int code) {
        if (gameState != PLAYING) {
            return;
        }

        int game = getGameAction(code);

        int swapx = blankp.x;
        int swapy = blankp.y;

        int direction = (options.reversed ? (-1) : 1);

        switch (game) {
        case Canvas.UP:
            swapy += direction;

            break;

        case Canvas.DOWN:
            swapy -= direction;

            break;

        case Canvas.LEFT:
            swapx += direction;

            break;

        case Canvas.RIGHT:
            swapx -= direction;

            break;

        default:
            return;
        }

        if ((swapx < 0) || (swapx >= gridw) || (swapy < 0) || (swapy >= gridh)) {
            return;
        }

        moveBlank(swapx, swapy);
        repaint();

        if (isSolved()) {
            setState(WON);
        }
    }

    class Piece {
        String label;
        boolean inv;
        int serial; // serial number for ordering
        int ix; // initial location in grid coordinates
        int iy; // initial location in grid coordinates
        int x; // current location in grid coordinates
        int y; // current location in grid coordinates

        Piece(String str, int ser, int nx, int ny, boolean v) {
            label = str;
            serial = ser;
            x = ix = nx;
            y = iy = ny;
            inv = v;
        }

        void setLocation(int nx, int ny) {
            x = nx;
            y = ny;
        }

        boolean isHome() {
            return (x == ix) && (y == iy);
        }

        void goHome() {
            setGrid(this, ix, iy);
        }

        // assumes background is white
        void paint(Graphics g) {
            int px = x * cellw;
            int py = y * cellh;

            if (label != null) {
                if (inv) {
                    // black outlined, white square with black writing
                    g.setColor(0);
                    g.setFont(font);
                    g.drawRect(px, py, cellw - 2, cellh - 2);
                    g.drawString(label, px + cellxoff, py + cellyoff, Graphics.TOP | Graphics.LEFT);
                } else {
                    // black square with white writing
                    g.setColor(0);
                    g.fillRect(px, py, cellw - 1, cellh - 1);
                    g.setColor(0xFFFFFF);
                    g.setFont(font);
                    g.drawString(label, px + cellxoff, py + cellyoff, Graphics.TOP | Graphics.LEFT);
                }
            }
        }
    }

    class BoardCommand extends Command {
        int tag;

        BoardCommand(String label, int type, int pri, int tag_) {
            super(label, type, pri);
            tag = tag_;
        }
    }
}
