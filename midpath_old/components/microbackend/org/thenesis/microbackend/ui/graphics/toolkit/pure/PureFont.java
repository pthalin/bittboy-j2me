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
package org.thenesis.microbackend.ui.graphics.toolkit.pure;

import java.io.IOException;
import java.io.InputStream;

import org.thenesis.microbackend.ui.graphics.VirtualFont;


public abstract class PureFont implements VirtualFont {

    public static final int TYPE_BW = 0, TYPE_A = 1, TYPE_LA = 2, TYPE_RGBA = 3;

    // Global informations.
    public int type;
    public int ascent, descent, leading, height;
    public int maxAscent, maxDescent, maxAdvance;
    public int maxWidth, maxHeight;
    public int nbCharacters;

    // Character informations.
    public int charCodePoint[];
    public short charWidth[], charHeight[]; // Dimensions.
    public short charX[], charY[]; // Reference point.
    public short charAdvance[];
    public int charOffset[]; // Offset in data

    // Character data.
    public byte pixels[];
    
    protected int size;

    public int findChar(int codePoint) {
        // Binary search.
        if (nbCharacters <= 0)
            return -1;
        int l = 0, r = nbCharacters - 1;
        while (l <= r) {
            int m = (l + r) >> 1;
            int me = charCodePoint[m];
            if (me == codePoint)
                return m;
            if (me > codePoint)
                r = m - 1;
            else
                l = m + 1;
        }
        return -1;
    }

    public void load(InputStream is) throws IOException {
        StreamReader isr = new StreamReader(is);

        type = isr.readInt();
        ascent = isr.readInt();
        descent = isr.readInt();
        leading = isr.readInt();
        height = isr.readInt();
        maxAscent = isr.readInt();
        maxDescent = isr.readInt();
        maxAdvance = isr.readInt();
        maxWidth = isr.readInt();
        maxHeight = isr.readInt();
        nbCharacters = isr.readInt();

        charCodePoint = new int[nbCharacters];
        isr.readInt(charCodePoint, 0, nbCharacters);
        charWidth = new short[nbCharacters];
        isr.readShort(charWidth, 0, nbCharacters);
        charHeight = new short[nbCharacters];
        isr.readShort(charHeight, 0, nbCharacters);
        charX = new short[nbCharacters];
        isr.readShort(charX, 0, nbCharacters);
        charY = new short[nbCharacters];
        isr.readShort(charY, 0, nbCharacters);
        charAdvance = new short[nbCharacters];
        isr.readShort(charAdvance, 0, nbCharacters);
        charOffset = new int[nbCharacters];
        isr.readInt(charOffset, 0, nbCharacters);

        int pixelsLength = isr.readInt();
        pixels = new byte[pixelsLength];
        isr.readByte(pixels, 0, pixelsLength);
    }
    
	public int charWidth(char ch) {
		return maxWidth;
//		return charWidth[ch];
	}

	public int charsWidth(char[] ch, int offset, int length) {
	    
	    return maxWidth * length;
	    
//	    int width = 0;
//	    for (int i = 0; i < length; i++) {
//	        width += charWidth[ch[offset + i]];
//	    }
//	    return width;
	}

	public int getBaselinePosition() {
		//return height;
	    return ascent;
	}

	public abstract int getFace();

	public int getHeight() {
		return maxHeight;
	}

	public int getSize() {
		return size;
	}

	public abstract int getStyle();

	public boolean isBold() {
		return false;
	}

	public boolean isItalic() {
		return false;
	}

	public boolean isPlain() {
		return true;
	}

	public boolean isUnderlined() {
		return false;
	}

	public int stringWidth(String str) {
		return substringWidth(str, 0, str.length());
	}

	public int substringWidth(String str, int offset, int len) {
		char[] chars = str.toCharArray();
		return charsWidth(chars, offset, len);
	}

    private class StreamReader {
        private InputStream is;
        private byte buffer[] = new byte[4];

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public byte readByte() throws IOException {
            readByte(buffer, 0, 1);
            return buffer[0];
        }

        public void readByte(byte data[], int offset, int length) throws IOException {
            while (length > 0) {
                int lengthRead = is.read(data, offset, length);
                if (length < 0)
                    throw new IOException();
                offset += lengthRead;
                length -= lengthRead;
            }
        }

        public short readShort() throws IOException {
            readByte(buffer, 0, 2);
            int v = 0;
            for (int i = 0; i < 2; i++)
                v = (v << 8) | (buffer[i] & 0xff);
            return (short) v;
        }

        public void readShort(short data[], int offset, int length) throws IOException {
            for (int i = 0; i < length; i++)
                data[offset + i] = readShort();
        }

        public char readChar() throws IOException {
            readByte(buffer, 0, 2);
            int v = 0;
            for (int i = 0; i < 2; i++)
                v = (v << 8) | (buffer[i] & 0xff);
            return (char) v;
        }

        public void readChar(char data[], int offset, int length) throws IOException {
            for (int i = 0; i < length; i++)
                data[offset + i] = readChar();
        }

        public int readInt() throws IOException {
            readByte(buffer, 0, 4);
            int v = 0;
            for (int i = 0; i < 4; i++)
                v = (v << 8) | (buffer[i] & 0xff);
            return v;
        }

        public void readInt(int data[], int offset, int length) throws IOException {
            for (int i = 0; i < length; i++)
                data[offset + i] = readInt();
        }

    }

	

}
