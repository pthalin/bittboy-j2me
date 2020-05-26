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
package org.thenesis.midpath.demo.game.pushpuzzle;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.ToneControl;


/**
 * PushPuzzleCanvas displays the game board and handles key events.
 * The PushPuzzle game logic and algorithms are separated into Board.java.
 * PushPuzzleCanvas does not setup or use any Commands.  Commands for each
 * screen and listeners should be setup outside this class.
 * PushPuzzleCanvas generates a SELECT_COMMAND when the current level
 * is solved. Sequencing through screens is done in the PushPuzzle MIDlet.
 * <p/>
 * PushPuzzleCanvas handles the reading, initialization, and sequencing
 * of individual puzzle screens.
 * <p/>
 * PushPuzzleCanvas uses the Score class to restore and save game levels
 * and scores for each level. To display the scores use getScoreScreen.
 * It will be initialized with the current scores.
 * To select a new level use the getLevelScreen and gotoLevel
 * methods.
 * <p/>
 * PushPuzzleCanvas handled key events for LEFT, RIGHT, UP, and DOWN to
 * move the pusher in the game board.  Pointer pressed events
 * are used to move the pusher to the target location (if possible).
 * <p/>
 */
class PushPuzzleCanvas extends GameCanvas implements Runnable {

    /** Pan Rate; number of milliseconds between screen updates */
    private static final int PanRate = 50;
    private static final int GroundColor0 = 0xffffff;
    private static final int PacketColor0 = 0xff6d00;
    private static final int StoreColor0 = 0xb60055;
    private static final int WallColor0 = 0x006D55;
    private static final int PusherColor0 = 0x6d6dff;

    /** The current level */
    private int level = 1;

    /** The current theme index */
    private int theme;

    /** True if the level has been solved */
    private boolean solved;

    /** number of pixels per cell (updated by readscreen) */
    private int cell = 1;

    /** The width of the canvas */
    private int width;

    /** The height of the canvas */
    private int height;

    /** The width of the board */
    private int bwidth;

    /** The height of the board */
    private int bheight;

    /** The board containing the location of each packet, ground, walls, etc */
    private final Board board;

    /** The score object */
    private Score score;

    /** The listener used to report solved events */
    private CommandListener listener;

    /** The TextBox to input new level numbers */
    private TextBox levelText; // for input of new level

    /** The index in the image of the Ground */
    public final int TILE_GROUND = 1;

    /** The index in the image of the Packet */
    public final int TILE_PACKET = 2;

    /** The index in the image of the Store */
    public final int TILE_STORE = 3;

    /** The index in the image of the Wall */
    public final int TILE_WALL = 4;

    /** The index in the image of the Pusher */
    public final int TILE_PUSHER = 5;

    /** Background image */
    private Image themeImage;

    /** Tiles forming the background */
    private TiledLayer tiles;

    /** The Sprite that is the pusher */
    private Sprite sprite;

    /** Layer manager */
    private LayerManager layers;

    /** Thread used for key handling and animation */
    private Thread thread;

    /** The target cell for runTo */
    private int targetx;

    /** The target cell for runTo */
    private int targety;

    /** The Tone player */
    private Player tonePlayer;

    /** The ToneController */
    private ToneControl toneControl;

    /** Tune to play when puzzle level is solved. */
    private byte[] solvedTune = {ToneControl.VERSION, 1, 74, 8, // 1/8 note
        75, 8, 73, 8};

    /** Tune to play when a packet enters a store */
    private byte[] storedTune = {ToneControl.VERSION, 1, 50, 8, // 1/8 note
        60, 8, 70, 8};

    /**
     * Construct a new canvas
     *
     * @param pushpuzzle the main MIDlet
     * @param s the score object
     */
    public PushPuzzleCanvas(PushPuzzle pushpuzzle, Score s) {
        super(false); // Don't suppress key events
        score = s;
        board = new Board();
        layers = new LayerManager();

        setupTheme();

        targetx = targety = -1;

        height = getHeight();
        width = getWidth();
    }

    /**
     * Read the previous level number from the score file.
     * Read in the level data.
     */
    public void init() {
        // Read the last level; if it can't be found, revert to level 0
        theme = score.getTheme();
        setupTheme();
        level = score.getLevel();

        if (!readScreen(level)) {
            level = 0;
            readScreen(level);
        }
    }

    /** Cleanup and destroy. */
    public void destroy() {
        hideNotify();
    }

    /**
     * Change themes.
     * Cycle to the next index and try it
     */
    public void changeTheme() {
        theme++;
        setupTheme();
        score.setLevel(level, theme); // save the level and theme
        setupTiles();
        updateSprite(0);
    }

    /**
     * Undo the last move if possible. Redraw the cell
     * the pusher occupies after the undone move and the cells
     * in the direction of the original move.
     * Here so undo can be triggered by a command.
     */
    public void undoMove() {
        int pos = board.getPusherLocation();
        int dir = board.undoMove();

        if (dir >= 0) {
            updateTilesNear(pos, dir);
            updateSprite(dir);
        }

        solved = board.solved();
    }

    /** Restart the current level. */
    public void restartLevel() {
        readScreen(level);
        solved = false;
    }

    /**
     * Start the next level.
     *
     * @param offset of the next level
     * @return true if the new level was loaded
     */
    public boolean nextLevel(int offset) {
        updateScores(); // save best scores 

        if (((level + offset) >= 0) && readScreen(level + offset)) {
            level += offset;
            score.setLevel(level, theme);
            solved = false;

            return true;
        }

        return false;
    }

    /**
     * Get the current level.
     *
     * @return the current level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get a screen to let the user change the level.
     * A simple numeric TextBox will do.
     *
     * @return the textbox used to change the level number
     */
    public Screen getLevelScreen() {
        if (levelText == null) {
            levelText =
                new TextBox("Enter Level", Integer.toString(level), // default
                    4, TextField.NUMERIC);
        } else {
            levelText.setString(Integer.toString(level));
        }

        return levelText;
    }

    /**
     * Go to the chosen Level.
     *
     * @return true if the new level was loaded.
     */
    public boolean gotoLevel() {
        if (levelText != null) {
            String s = levelText.getString();
            int l;

            try {
                l = Integer.parseInt(s);
            } catch (java.lang.NumberFormatException e) {
                return false;
            }

            updateScores();

            if ((l >= 0) && readScreen(l)) {
                level = l;
                score.setLevel(level, theme);
                solved = false;

                return true;
            }
        }

        return false;
    }

    /**
     * Read and setup the next level.
     * Opens the resource file with the name "/Screen.<lev>"
     * and tells the board to read from the stream.
     * <STRONG>Must be called only with the board locked.</STRONG>
     *
     * @param lev the level number to read.
     * @return true if the reading of the level worked, false otherwise.
     */
    private boolean readScreen(int lev) {
        if (lev <= 0) {
            board.screen0(); // Initialize the default zero screen.
        } else {
            InputStream is;

            try {
                is = getClass().getResourceAsStream("res/screen." + lev);

                if (is != null) {
                    board.read(is, lev);
                    is.close();
                } else {
                    System.out.println("Could not find the game board for level " + lev);

                    return false;
                }
            } catch (java.io.IOException ex) {
                return false;
            }
        }

        bwidth = board.getWidth();
        bheight = board.getHeight();

        setupTiles();
        updateSprite(0);

        return true;
    }

    /** Create the Tiled layer to represent the current board. */
    private void setupTiles() {
        if (tiles != null) {
            layers.remove(tiles);
        }

        // Figure out how many cells are needed to cover canvas.
        int w = ((width + cell) - 1) / cell;
        int h = ((height + cell) - 1) / cell;
        tiles = new TiledLayer((w > bwidth) ? w : bwidth, (h > bheight) ? h : bheight, themeImage,
            cell, cell);

        /** Fill it all with background */
        tiles.fillCells(0, 0, w, h, TILE_GROUND);

        // Initialize the background tileset
        for (int y = 0; y < bheight; y++) {
            for (int x = 0; x < bwidth; x++) {
                updateTile(x, y);
            }
        }

        layers.append(tiles);
    }

    /**
     * Update the tile at the location.
     *
     * @param x the offset of the tile to update
     * @param y the offset of the tile to update
     */
    private void updateTile(int x, int y) {
        int tile;

        byte v = board.get(x, y);

        switch (v & ~Board.PUSHER) {
        case Board.WALL:
            tile = TILE_WALL;

            break;

        case Board.PACKET:
        case Board.PACKET | Board.STORE:
            tile = TILE_PACKET;

            break;

        case Board.STORE:
            tile = TILE_STORE;

            break;

        case Board.GROUND:
        default:
            tile = TILE_GROUND;
        }

        tiles.setCell(x, y, tile);
    }

    /**
     * Setup Theme-0 generated to match screen
     * size and board size.
     */
    private void setupTheme0() {
        int bwidth = board.getWidth();
        int bheight = board.getHeight();
        int w = getWidth();
        int h = getHeight(); // height of Canvas
        cell = (((h - 14) / bheight) < (w / bwidth)) ? ((h - 14) / bheight) : (w / bwidth);

        // Create a mutable image and initialize
        themeImage = Image.createImage(cell * 5, cell);

        Graphics g = themeImage.getGraphics();

        g.setColor(GroundColor0);
        g.fillRect((TILE_GROUND - 1) * cell, 0, cell * TILE_PUSHER, cell);
        g.setColor(PacketColor0);
        g.fillRect(((TILE_PACKET - 1) * cell) + 1, 1, cell - 2, cell - 2);
        g.setColor(StoreColor0);
        g.drawRect(((TILE_STORE - 1) * cell) + 1, 1, cell - 2, cell - 2);
        g.setColor(WallColor0);
        g.fillRect((TILE_WALL - 1) * cell, 0, cell, cell);
        g.setColor(PusherColor0);
        g.fillArc((TILE_PUSHER - 1) * cell, 0, cell, cell, 0, 360);
    }

    /**
     * Setup the theme by reading the images and setting up
     * the sprite and picking the tile size.
     * Uses the current theme index.
     * If the image with the current index can't be found
     * retry with theme = 0.
     *
     * @param image containing all the frames used for the board.
     */
    private void setupTheme() {
        if (sprite != null) {
            layers.remove(sprite);
            sprite = null;
        }

        if (theme > 0) {
            try {
                StringBuffer name = new StringBuffer("res/Theme-");
                name.append(theme);
                name.append(".png");
                themeImage = Image.createImage(name.toString());

                // Cells are square using the minimum of the width and height
                int h = themeImage.getHeight();
                int w = themeImage.getWidth();
                cell = (w < h) ? w : h;
            } catch (IOException e) {
                theme = 0;
                setupTheme0();
            }
        } else {
            setupTheme0();
        }

        sprite = new Sprite(themeImage, cell, cell);
        sprite.defineReferencePixel(cell / 2, cell / 2);

        int[] seq = new int[]{TILE_PUSHER - 1};
        sprite.setFrameSequence(seq);
        layers.insert(sprite, 0);
    }

    /**
     * Return the Screen to display scores.
     * It returns a screen with the current scores.
     *
     * @return a screen initialized with the current score information.
     */
    public Screen getScoreScreen() {

        int currPushes = board.getPushes();
        int bestPushes = score.getPushes();
        int currMoves = board.getMoves();
        int bestMoves = score.getMoves();
        boolean newbest = solved && ((bestPushes == 0) || (currPushes < bestPushes));

        // Temp until form can do setItem
        Form scoreForm = new Form(null);

        scoreForm.append(new StringItem(newbest ? "New Best:\n" : "Current:\n",
            currPushes + " pushes\n" + currMoves + " moves"));

        scoreForm.append(new StringItem(newbest ? "Old Best:\n" : "Best:\n",
            bestPushes + " pushes\n" + bestMoves + " moves"));

        String title = "Scores";

        if (newbest) {
            title = "Congratulations";
        }

        scoreForm.setTitle(title);

        return scoreForm;
    }

    /**
     * Handle a repeated arrow keys as though it were another press.
     *
     * @param keyCode the key pressed.
     */
    protected void keyRepeated(int keyCode) {
        int action = getGameAction(keyCode);
        switch (action) {
        case Canvas.LEFT:
        case Canvas.RIGHT:
        case Canvas.UP:
        case Canvas.DOWN:
            keyPressed(keyCode);
            break;
        default:
            break;
        }
    }

    /**
     * Handle a single key event.
     * The LEFT, RIGHT, UP, and DOWN keys are used to
     * move the pusher within the Board.
     * Other keys are ignored and have no effect.
     * Repaint the screen on every action key.
     */
    protected void keyPressed(int keyCode) {

        // Protect the data from changing during painting.
        synchronized (board) {
            cancelTo();

            int action = getGameAction(keyCode);
            int move;

            switch (action) {
            case Canvas.LEFT:
                move = Board.LEFT;

                break;

            case Canvas.RIGHT:
                move = Board.RIGHT;

                break;

            case Canvas.DOWN:
                move = Board.DOWN;

                break;

            case Canvas.UP:
                move = Board.UP;

                break;

                // case 0: // Ignore keycode that don't map to actions.
            default:
                return;
            }

            // Tell the board to move the piece
            int stored = board.getStored();
            int dir = board.move(move);

            if (stored < board.getStored()) {
                // Play a note if a packet hit the spot.
                play(storedTune);
            }

            int pos = board.getPusherLocation();
            updateTilesNear(pos, dir);
            updateSprite(dir);
        } // End of synchronization on the Board.
    }

    /**
     * Update the scores for the current level if it has
     * been solved and the scores are better than before.
     */
    private void updateScores() {
        if (!solved) {
            return;
        }

        int sp = score.getPushes();
        int bp = board.getPushes();
        int bm = board.getMoves();

        /*
         * Update the scores.  If the score for this level is lower
         * than the last recorded score save the lower scores.
         */
        if ((sp == 0) || (bp < sp)) {
            score.setLevelScore(bp, bm);
        }
    }

    /** Cancel the animation. */
    private void cancelTo() {
        targetx = -1;
        targety = -1;
    }

    /**
     * Called when the pointer is pressed.
     * Record the target for the pusher.
     *
     * @param x location in the Canvas
     * @param y location in the Canvas
     */
    protected void pointerPressed(int x, int y) {
        targetx = (x - tiles.getX()) / cell;
        targety = (y - tiles.getY()) / cell;
    }

    /**
     * Add a listener to notify when the level is solved.
     * The listener is send a List.SELECT_COMMAND when the
     * level is solved.
     *
     * @param l the object implementing interface CommandListener
     */
    public void setCommandListener(CommandListener l) {
        super.setCommandListener(l);
        listener = l;
    }

    /**
     * Update the Sprite location from the board supplied position
     *
     * @param dir the sprite is moving
     */
    private void updateSprite(int dir) {
        int loc = board.getPusherLocation();
        int x = (loc & 0x7fff) * cell;
        int y = ((loc >> 16) & 0x7fff) * cell;
        // Update sprite location
        sprite.setPosition(tiles.getX() + x, tiles.getY() + y);
        dir = Board.RIGHT; // Graphics.drawRegion doesn't do xofrm

        switch (dir & 0x03) {
        case Board.LEFT:
            sprite.setTransform(Sprite.TRANS_ROT180);

            break;

        case Board.UP:
            sprite.setTransform(Sprite.TRANS_ROT90);

            break;

        case Board.DOWN:
            sprite.setTransform(Sprite.TRANS_ROT270);

            break;

        default:
            sprite.setTransform(Sprite.TRANS_NONE);

            break;
        }
    }

    /**
     * Queue a repaint for an area around the specified location.
     *
     * @param loc an encoded location from Board.getPusherLocation
     * @param dir that the pusher moved and flag if it pushed a packet
     */
    void updateTilesNear(int loc, int dir) {
        int x = loc & 0x7fff;
        int y = (loc >> 16) & 0x7fff;

        // Update cells if any were moved
        if ((dir >= 0) && ((dir & Board.MOVEPACKET) != 0)) {
            updateTile(x, y);
            updateTile(x + 1, y);
            updateTile(x - 1, y);
            updateTile(x, y + 1);
            updateTile(x, y - 1);
        }
    }

    /**
     * Paint the contents of the Canvas.
     * The clip rectangle of the canvas is retrieved and used
     * to determine which cells of the board should be repainted.
     *
     * @param g Graphics context to paint to.
     */
    public void paint(Graphics g) {
        flushGraphics();
    }

    /**
     * The canvas is being displayed.
     * Stop the event handling and animation thread.
     */
    protected void showNotify() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * The canvas is being removed from the screen.
     * Stop the event handling and animation thread.
     */
    protected void hideNotify() {
        thread = null;
    }

    /**
     * The main event processor. Events are polled and
     * actions taken based on the directional events.
     */
    public void run() {
        Graphics g = getGraphics(); // Of the buffered screen image
        Thread mythread = Thread.currentThread();

        // Loop handling events
        while (mythread == thread) {
            try { // Start of exception handler

                boolean newlySolved = false;

                if (!solved && board.solved()) {
                    newlySolved = solved = true;
                    play(solvedTune);
                }

                if (newlySolved && (listener != null)) {
                    listener.commandAction(List.SELECT_COMMAND, this);
                }

                if ((targetx >= 0) && (targety >= 0)) {
                    int dir = board.runTo(targetx, targety, 1);
                    int pos = board.getPusherLocation();

                    if (dir < 0) {
                        targetx = targety = -1; // Cancel target
                    } else {
                        updateTilesNear(pos, dir);
                        updateSprite(dir);
                    }
                }

                // Check that the pusher is not to close to the edge
                int loc = board.getPusherLocation();
                int x = (loc & 0x7fff) * cell;
                int y = ((loc >> 16) & 0x7fff) * cell;

                int lx = tiles.getX();
                int ly = tiles.getY();

                int panScale = cell / 4;

                if (panScale < 1) {
                    panScale = 1;
                }

                // If the sprite is too near the edge (or off) pan
                if ((lx + x) > (width - cell - cell)) {
                    tiles.move(-panScale, 0);
                    sprite.move(-panScale, 0);
                }

                if ((lx + x) < cell) {
                    tiles.move(panScale, 0);
                    sprite.move(panScale, 0);
                }

                if ((ly + y) > (height - cell - cell)) {
                    tiles.move(0, -panScale);
                    sprite.move(0, -panScale);
                }

                if ((ly + y) < cell) {
                    tiles.move(0, panScale);
                    sprite.move(0, panScale);
                }

                // Draw all the layers and flush
                layers.paint(g, 0, 0);

                if (mythread == thread) {
                    flushGraphics();
                }

                // g.drawString("PushPuzzle Level " + level, 0, height,
                //			     Graphics.BOTTOM|Graphics.LEFT);
                try {
                    mythread.sleep(PanRate);
                } catch (java.lang.InterruptedException e) {
                    // Ignore
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** Play the simple tune supplied. */
    void play(byte[] tune) {
        try {
            if (tonePlayer == null) {
                // First time open the tonePlayer
                tonePlayer = Manager.createPlayer(Manager.TONE_DEVICE_LOCATOR);
                tonePlayer.realize();
                toneControl = (ToneControl) tonePlayer.getControl(
                    "javax.microedition.media.control.ToneControl");
            }

            tonePlayer.deallocate();
            toneControl.setSequence(tune);
            tonePlayer.start();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /*
     * Close the tune player and release resources.
     */
    void closePlayer() {
        if (tonePlayer != null) {
            toneControl = null;
            tonePlayer.close();
            tonePlayer = null;
        }
    }
}
