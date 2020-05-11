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



/**
 * The class Board knows how the pieces move, handles undo, and
 * handles reading of screens.
 */
public class Board {
    // Move directions
    public static final int LEFT = 0;
    public static final int RIGHT = 3;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int MOVEPACKET = 4;

    // Bit definitions for pieces of each board position
    public static final byte GROUND = 0; // If nothing there
    public static final byte STORE = 1; // If it is a storage place
    public static final byte PACKET = 2; // If it has a packet
    public static final byte WALL = 4; // If it is a wall
    public static final byte PUSHER = 8; // If the pusher is there
    private int level;
    private byte[] array;
    private byte[] pathmap; // used for runTo to find shortest path
    private int width;
    private int height;
    private int pusher; // position of the pusher at index into array
    private int packets = 0; // total number of packets
    private int stored = 0; // number of packets in Stores
    private byte[] moves; // recorded moves
    private int nmoves; // number of moves executed
    private int npushes; // number of pushes executed

    /**
     * Creates new Board initialized to a simple puzzle.
     */
    public Board() {
        moves = new byte[200];
        screen0();
    }

    /**
     * Create the hard coded simple game board.
     */
    public void screen0() {
        width = 9;
        height = 7;
        array = new byte[width * height];
        level = 0;
        nmoves = 0;
        npushes = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                byte t =
                    ((x == 0) || (y == 0) || (x == (width - 1)) || (y == (height - 1))) ? WALL
                                                                                        : GROUND;
                set(x, y, t);
            }
        }

        packets = 2;
        stored = 0;
        set(2, 2, PACKET);
        set(4, 4, PACKET);
        set(4, 2, STORE);
        set(6, 4, STORE);
        pusher = index(1, 1);
    }

    /**
     * Move the pusher in the direction indicated.
     * If there is a wall, don't move.
     * if there is a packet in that direction, try to move it.
     * @param move the direction; one of LEFT, RIGHT, UP, DOWN
     * @return the direction actually moved, -1 if not moved
     */
    public int move(int move) {
        int obj = pusher + indexOffset(move);

        // Handle the simple cases
        if ((array[obj] & WALL) != 0) {
            return -1;
        }

        int m = movePacket(obj, move);

        if (m < 0) {
            return -1; // If can't move packet, done!
        }

        pusher = obj; // move the pusher to the new spot
        saveMove(m);

        return m;
    }

    /**
     * Move the packet in the direction indicated relative to
     * the pusher.  If it fits into a store position remember.
     * @return -1 if can't be moved or the updated move
     * including the packet flag if there was a packet to move.
     */
    private int movePacket(int index, int move) {
        if ((array[index] & PACKET) == 0) {
            return move; // no packet to move
        }

        int dest = index + indexOffset(move);

        if (array[dest] > STORE) {
            return -1; // can't move packet into next spot.
        }

        // Remove packet from current location
        array[index] &= ~PACKET;

        if ((array[index] & STORE) != 0) {
            stored--;
        }

        // Insert packet into new location
        array[dest] |= PACKET;

        if ((array[dest] & STORE) != 0) {
            stored++;
        }

        npushes++; // count pushes done

        return move + MOVEPACKET;
    }

    /*
     * Save a move, extending the array if necessary.
     */
    private void saveMove(int move) {
        if (nmoves >= moves.length) {
            byte[] n = new byte[moves.length + 50];
            System.arraycopy(moves, 0, n, 0, moves.length);
            moves = n;
        }

        moves[nmoves++] = (byte)move;
    }

    /**
     * Undo the most recent move
     * @return the move undone;  if none -1 is returned;
     *  See LEFT, RIGHT, UP, DOWN, and MOVEPACKET
     */
    public int undoMove() {
        if (nmoves <= 0) {
            return -1;
        }

        int move = moves[--nmoves];
        int rev = (move & 3) ^ 3; // reverse the direction
        int back = pusher + indexOffset(rev);

        if ((move & MOVEPACKET) != 0) {
            npushes--; // "unpush"
            movePacket(pusher + indexOffset(move), rev); // move packet
        }

        pusher = back;

        return move;
    }

    /**
     * Determine if the screen has been solved.
     */
    public boolean solved() {
        return packets == stored;
    }

    /**
     * Move the player to the position (x,y), if possible. Return
     * the direction it moved if successful, otherwise -1.
     * The position (x,y) must be empty.
     * @param x window coordinate
     * @param y window coordinate
     * @return the direction that it moved, -1 is not moved;
     *  See LEFT, RIGHT, UP, DOWN, MOVEPACKET
     */
    public int runTo(int x, int y, int max) {
        int target = index(x, y);

        if ((target < 0) || (target >= array.length)) {
            return -1;
        }

        if (target == pusher) {
            return -1; // already there
        }

        /* Fill the path map */
        if ((pathmap == null) || (pathmap.length != array.length)) {
            pathmap = new byte[array.length];
        }

        // Fill with unset value.
        for (int i = 0; i < pathmap.length; i++)
            pathmap[i] = 127;

        // flood fill search to find a shortest path to the push point.
        findTarget(target, (byte)0);

        /*
         * if we didn't make it back to the players position,
         * there is no valid path to that place.
         */
        if (pathmap[pusher] == 127) {
            return -1;
        } else {
            // We made it back, so let's walk the path we just built up
            // Save the final move to return
            int pathlen = pathmap[pusher];
            int pathmin = pathlen - max;
            int dir = -1;

            for (pathlen--; pathlen >= pathmin; pathlen--) {
                if (pathmap[pusher - 1] == pathlen) {
                    dir = LEFT;
                    saveMove(dir);
                    pusher--;
                } else if (pathmap[pusher + 1] == pathlen) {
                    dir = RIGHT;
                    saveMove(dir);
                    pusher++;
                } else if (pathmap[pusher - width] == pathlen) {
                    dir = UP;
                    saveMove(dir);
                    pusher -= width;
                } else if (pathmap[pusher + width] == pathlen) {
                    dir = DOWN;
                    saveMove(dir);
                    pusher += width;
                } else {
                    /*
                     * if we get here, something is SERIOUSLY wrong,
                     * so we should abort
                     */
                    throw new RuntimeException("runTo abort");
                }
            }

            return dir;
        }
    }

    /**
     * Find the shortest path to the pusher via a fill search algorithm
     */
    private void findTarget(int t, byte pathlen) {
        if (array[t] > STORE) {
            return; // Either a wall or looped back to player
        }

        // Already tried here and this way is longer
        if (pathmap[t] <= pathlen) {
            return;
        }

        pathmap[t] = pathlen++; // set path length to this location

        if (t == pusher) {
            return;
        }

        // avoiding ArrayIndexOutOfBoundException
        if ((t - 1) >= 0) {
            findTarget(t - 1, pathlen); // to previous cell
        }

        if ((t + 1) < array.length) {
            findTarget(t + 1, pathlen); // to next cell
        }

        if ((t - width) >= 0) {
            findTarget(t - width, pathlen); // to previous row
        }

        if ((t + width) < array.length) {
            findTarget(t + width, pathlen); // to next row
        }
    }

    /**
     * Return the pieces at the location.
     * @param x location in the board.
     * @param y location in the board.
     * @return flags indicating what pieces are in this board location.
     * Bit flags; combinations of WALL, PUSHER, STORE, PACKET.
     */
    public byte get(int x, int y) {
        int offset = index(x, y);

        if (offset == pusher) {
            return (byte)(array[offset] | PUSHER);
        } else {
            return array[offset];
        }
    }

    /**
     * Set the value of the location.
     */
    private void set(int x, int y, byte value) {
        array[index(x, y)] = value;
    }

    /**
     * Compute the index in the array of the x, y location.
     */
    private int index(int x, int y) {
        if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
            return -1;
        }

        return (y * width) + x;
    }

    /**
     * Get the location of the pusher.
     * It is returned as an int with the lower 16 bits being
     * the x index and the upper 16 bits being the y index.
     * @return the encoded location of the pusher.
     */
    public int getPusherLocation() {
        int x = pusher % width;
        int y = pusher / width;

        return (y << 16) + x;
    }

    /**
     * Compute the offset in the array of the cell relative
     * to the current pusher location in the direction of the move.
     * Note: the walls around the edge always make a +/- guard band.
     * Also, the order of evaluation should never try to get to +/- 2.
     */
    private int indexOffset(int move) {
        switch (move & 3) {
        case LEFT:
            return -1;

        case RIGHT:
            return +1;

        case UP:
            return -width;

        case DOWN:
            return +width;
        }

        return 0;
    }

    /**
     * Read a board from a stream.
     * Read it into a fixed size array and then shrink to fit.
     */
    public void read(java.io.InputStream is, int l) {
        final int W = 20;
        final int H = 20;
        byte[] b = new byte[W * H];

        // Add resize code later.
        int c;

        // Add resize code later.
        int w = 0;
        int x = 0;
        int y = 0;
        int xn = 0;
        int yn = 0;
        int npackets = 0;

        try {
            while ((c = is.read()) != -1) {
                switch (c) {
                case '\n':

                    if (x > w) {
                        w = x;
                    }

                    y++;
                    x = 0;

                    break;

                case '$':
                    b[(y * W) + x++] = PACKET;
                    npackets++;

                    break;

                case '#':
                    b[(y * W) + x++] = WALL;

                    break;

                case ' ':
                    b[(y * W) + x++] = GROUND;

                    break;

                case '.':
                    b[(y * W) + x++] = STORE;

                    break;

                case '*':
                    b[(y * W) + x] = PACKET;
                    b[(y * W) + x++] |= STORE;
                    npackets++;
                    stored++;

                    break;

                case '+': // player and store in same place
                    b[(y * W) + x++] = STORE;

                case '@':
                    xn = x;
                    yn = y;
                    x++;

                    break;
                }
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }

        if (y > 0) {
            array = new byte[w * y];

            if (y > w) { // Switch x for y while copying
                width = y;
                height = w;

                for (y = 0; y < width; y++) {
                    for (x = 0; x < w; x++) {
                        array[index(y, x)] = b[(y * W) + x];
                    }
                }

                pusher = index(yn, xn);
            } else {
                width = w;
                height = y;
                array = new byte[width * height];

                for (y = 0; y < height; y++) {
                    for (x = 0; x < width; x++) {
                        array[index(x, y)] = b[(y * W) + x];
                    }
                }

                pusher = index(xn, yn);
            }

            stored = 0;
            packets = npackets;
            level = l;
            nmoves = 0;
            npushes = 0;
        }
    }

    /**
     * Get the width of the game board.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the board.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the number of moves to get this far.
     */
    public int getMoves() {
        return nmoves;
    }

    /**
     * Get the number of packets pushed around.
     */
    public int getPushes() {
        return npushes;
    }

    /**
     * Get the number of packets stored.
     */
    public int getStored() {
        return stored;
    }

    /**
     * Convert a left/right direction into an offset.
     */
    private int dx(int dir) {
        if (dir == LEFT) {
            return -1;
        }

        if (dir == RIGHT) {
            return +1;
        }

        return 0;
    }

    /**
     * Convert a up/down direction into an offset.
     */
    private int dy(int dir) {
        if (dir == UP) {
            return -1;
        }

        if (dir == DOWN) {
            return +1;
        }

        return 0;
    }
}
