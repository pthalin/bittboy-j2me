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

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;


/**
 * Keep track of the last level played.
 * For each level keep track of the number of moves.
 * <p>
 * The scores are kept in a RecordStore named PushPuzzleScores.
 * There are two types of records:
 * - Last level
 * - Level history
 *   Name of level (either resource name "/..." or...)
 */
class Score {
    private static final int LEVEL_LEN = 9;
    private static final byte LEVEL_TAG = 1;
    private static final int SCORE_LEN = 13;
    private static final byte SCORE_TAG = 2;

    // Current level record = {byte LEVEL_TAG; int level;}
    private int levelId; // The record Id of the level record
    private byte[] levelRec; // Byte array holding the level

    // Score for level = {byte SCORE_TAG; int level, int pushes; int moves;}
    private int scoreId; // The record Id of the current level
    private byte[] scoreRec; // scores for the current level.
    private RecordStore store; // Record store, null if not open

    /*
     * Construct a new Score handler.
     */
    Score() {
        store = null;

        levelId = 0;
        levelRec = new byte[LEVEL_LEN];
        levelRec[0] = LEVEL_TAG;
        putInt(levelRec, 1, 0);
        putInt(levelRec, 5, 0);

        scoreId = 0;
        scoreRec = new byte[SCORE_LEN];
        scoreRec[0] = SCORE_TAG;
        putInt(scoreRec, 1, 0);
    }

    /**
     * Open the record store and locate
     * the record with the level number in it.
     */
    boolean open() {
        try {
            store = RecordStore.openRecordStore("PushPuzzleScores", true);
        } catch (RecordStoreException ex) {
        }

        if (store == null) {
            return false;
        }

        try {
            /*
             * Locate the record containing the level.
             */
            levelId = 0;

            RecordEnumeration enm = store.enumerateRecords(null, null, false);

            while (enm.hasNextElement()) {
                int ndx = enm.nextRecordId();

                if (store.getRecordSize(ndx) == LEVEL_LEN) {
                    int l = store.getRecord(ndx, levelRec, 0);

                    if ((l == LEVEL_LEN) && (levelRec[0] == LEVEL_TAG)) {
                        levelId = ndx;

                        break;
                    }
                }
            }
        } catch (RecordStoreException ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Get the current Theme number.
     */
    int getTheme() {
        return getInt(levelRec, 5);
    }

    /**
     * Retrieve the level from the level record.  It should
     * have already been read from the store or created.
     * The first byte is a tag, the second byte the level.
     */
    int getLevel() {
        return getInt(levelRec, 1);
    }

    /**
     * Set the level and theme into the RecordStore.
     * @param level the current level
     * @param theme the current theme
     */
    boolean setLevel(int level, int theme) {
        putInt(levelRec, 1, level);
        putInt(levelRec, 5, theme);
        putInt(scoreRec, 1, level);

        if (store == null) {
            return false;
        }

        try {
            if (levelId == 0) {
                levelId = store.addRecord(levelRec, 0, levelRec.length);
            } else {
                store.setRecord(levelId, levelRec, 0, levelRec.length);
            }
        } catch (RecordStoreException ex) {
            System.out.println("RecordStoreException");
            ex.printStackTrace();

            return false;
        }

        readScore(level); // get the score for the level

        return true;
    }

    /**
     * Get the number of pushes on the current level.
     */
    int getPushes() {
        return getInt(scoreRec, 5);
    }

    /**
     * Get the number of moved on the current level.
     */
    int getMoves() {
        return getInt(scoreRec, 9);
    }

    /**
     * Read the score for the current level.
     * Read through the records looking for the one for this level.
     */
    boolean readScore(int level) {
        try {
            scoreId = 0;

            // Locate the matching record
            RecordEnumeration enm = store.enumerateRecords(null, null, false);

            while (enm.hasNextElement()) {
                int ndx = enm.nextRecordId();

                if (store.getRecordSize(ndx) == SCORE_LEN) {
                    int l = store.getRecord(ndx, scoreRec, 0);

                    if ((l == SCORE_LEN) && (scoreRec[0] == SCORE_TAG) &&
                            (getInt(scoreRec, 1) == level)) {
                        scoreId = ndx;

                        return true;
                    }
                }
            }
        } catch (RecordStoreException ex) {
            ex.printStackTrace();

            return false;
        }

        // No record found, start fresh
        scoreRec[0] = SCORE_TAG;
        putInt(scoreRec, 1, level);
        putInt(scoreRec, 5, 0);
        putInt(scoreRec, 9, 0);

        return true;
    }

    /**
     * Set the updated score to the RecordStore.
     */
    boolean setLevelScore(int pushes, int moves) {
        // Update the scores in the buffer.
        putInt(scoreRec, 5, pushes);
        putInt(scoreRec, 9, moves);

        try {
            // Write/Add the record to the  store
            if (scoreId == 0) {
                scoreId = store.addRecord(scoreRec, 0, scoreRec.length);
            } else {
                store.setRecord(scoreId, scoreRec, 0, scoreRec.length);
            }
        } catch (RecordStoreException ex) {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * Get an integer from an array.
     */
    private int getInt(byte[] buf, int offset) {
        return ((buf[offset + 0] & 0xff) << 24) | ((buf[offset + 1] & 0xff) << 16) |
        ((buf[offset + 2] & 0xff) << 8) | (buf[offset + 3] & 0xff);
    }

    /**
     * Put an integer to an array
     */
    private void putInt(byte[] buf, int offset, int value) {
        buf[offset + 0] = (byte)((value >> 24) & 0xff);
        buf[offset + 1] = (byte)((value >> 16) & 0xff);
        buf[offset + 2] = (byte)((value >> 8) & 0xff);
        buf[offset + 3] = (byte)((value >> 0) & 0xff);
    }

    /**
     * Close the store.
     */
    void close() {
        try {
            if (store != null) {
                store.closeRecordStore();
            }
        } catch (RecordStoreException ex) {
            ex.printStackTrace();
        }
    }
}
