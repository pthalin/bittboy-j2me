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
package com.sun.midp.chameleon.input;

public class VirtualKeyboard {

	// Tile width and height. Internal only.
	private final static int TILE_WIDTH=16, TILE_HEIGHT=16;
	
	// Keyboard image width and height in tiles. Internal only.
	private final static int WIDTH_IN_TILES=11, HEIGHT_IN_TILES=5;
	
	/**
	 * Keyboard image width and height in pixels.
	 */
	public final static int WIDTH=WIDTH_IN_TILES*TILE_WIDTH, HEIGHT=HEIGHT_IN_TILES*TILE_HEIGHT; 

	/**
	 * Special keys.
	 * Keys which are not Unicode characters are allocated in the private use area of the Basic Multilingual Plane (BMP).
	 */
	public final static int
		KEY_BACKSPACE=			0x00000008,
		KEY_TAB=				0x00000009,
		KEY_ENTER=				0x0000000a,
		KEY_CANCEL=				0x00000018,
		KEY_ESCAPE=				0x0000001b,
		KEY_DELETE=				0x0000007f,
	
		KEY_SHIFT=				0x0000e000, // Internal only.
		KEY_CAPSLOCK=			0x0000e001, // Internal only.
		KEY_ALTERNATE=			0x0000e002,
		KEY_CONTROL=			0x0000e003,

		// Keys for changing the keyboard layout.
		KEY_ABC=				0x0000e100, // Internal only.
		KEY_ACCENTS=			0x0000e101, // Internal only.
		KEY_SYMBOLS=			0x0000e102, // Internal only.
		KEY_132=				0x0000e103, // Internal only.

		// Large keys. They are converted to normal keys internally. 
		KEY_ENTER_LARGE=		0x0000e200, // Internal only.
		KEY_CANCEL_LARGE=		0x0000e201, // Internal only.
		KEY_SHIFT_LARGE=		0x0000e202, // Internal only.
		KEY_CAPSLOCK_LARGE=		0x0000e203, // Internal only.
		KEY_SPACE_LARGE=		0x0000e204, // Internal only.
		KEY_BACKSPACE_LARGE=	0x0000e205, // Internal only.
		KEY_RETURN_LARGE=		0x0000e206, // Internal only.
		KEY_NUMLOCK_LARGE=		0x0000e207, // Internal only.
		KEY_HELP_LARGE=			0x0000e208, // Internal only.
		KEY_PAUSE_LARGE=		0x0000e209, // Internal only.
		KEY_INSERT_LARGE=		0x0000e20a, // Internal only.
		KEY_UNDO_LARGE=			0x0000e20b, // Internal only.
		KEY_PAGE_UP_LARGE=		0x0000e20c, // Internal only.
		KEY_PAGE_DOWN_LARGE=	0x0000e20d // Internal only.
		;

	/**
	 * Keyboard layouts.
	 */
	public final static int
		LAYOUT_QWERTY=		0,
		LAYOUT_AZERTY=		1;
	
	// User selectable maps.
	private final static int
		MAP_ABC=			0,
		MAP_ACCENTS=		1,
		MAP_SYMBOLS=		2;
	
	private final static int MAX_KEYS=WIDTH_IN_TILES*HEIGHT_IN_TILES;
	private int layout=LAYOUT_QWERTY;
	private int keyMap; // Current user key map.
	private int keyMapKeysNb; // Number of keys in the current key map.
	private int keyMapData[][]; // Data of the current key map.
	private final int keyImg[]=new int[MAX_KEYS]; // Index in the key image table for each key in the current key map.
	private final boolean keyPressed[]=new boolean[MAX_KEYS]; // Key status for each key in the current key map.
	private int lastKey=-1; // Last key pushed.
	private boolean capslockOn=false, shiftOn=false; // State of shift and capslock.
	private boolean cursorActivated=false;
	private int cursorX0, cursorY0, cursorX1, cursorY1, cursorX, cursorY; // Cursor position.
	private int cursorKey=-1;
	private boolean dirty=true; // Flag indicating whether the keyboard needs to be redrawn.
	
	/**
	 * 
	 */
	public VirtualKeyboard() {
		reset();
	}
	
	/**
	 * Sets the layout of the keyboard.
	 * @param layout
	 */
	public void setLayout(int l) {
		switch (l) {
		case LAYOUT_QWERTY:
		case LAYOUT_AZERTY: break;
		default: return;
		}
		layout=l;
		lastKey=-1;
		shiftOn=false;
		setMap(-1);
	}
	
	/**
	 * Resets the keyboard to the default state.
	 */
	public void reset() {
		lastKey=-1;
		capslockOn=false;
		shiftOn=false;
		setMap(MAP_ABC);
	}
	
	/**
	 * Activates or deactivates the cursor.
	 * @param flag the activation flag. If true the cursor is activated. Otherwise it is deactivated.
	 */
	public void activateCursor(boolean flag) {
		cursorActivated=flag;
	}
	
	/**
	 * Returns whether the cursor is activated or not.
	 * @return <code>true</code> if the cursor if activated, <code>false</code> otherwise.
	 */
	public boolean isCursorActivated() {
		return cursorActivated;
	}
	
	/**
	 * Move the cursor to the left.
	 */
	public void moveLeft() {
		int nearestLeftKey=-1, nearestLeftX=Integer.MAX_VALUE, nearestLeftY=Integer.MAX_VALUE;
		int farthestRightKey=-1, farthestRightX=Integer.MIN_VALUE, farthestRightY=Integer.MIN_VALUE;
		
		for (int i=0; i<keyMapKeysNb; i++) {
			if (i==cursorKey) continue;
			
			int tableData[]=KEY_IMAGE_TABLE[keyImg[i]];
			int mapData[]=keyMapData[i];
			int kx0=mapData[1], ky0=mapData[2], kx1=kx0+tableData[3]-1, ky1=ky0+tableData[4]-1;

			int dx0=cursorX0-kx1, dx1=kx1-cursorX1, dy=pointToLineDistance(cursorY, ky0, ky1);

			// Find the nearest key at the left of the cursor.
			if (dx0>0 && (dx0<nearestLeftX || (dx0==nearestLeftX && dy<nearestLeftY))) {
				nearestLeftKey=i; nearestLeftX=dx0; nearestLeftY=dy;
			}			
			// Find the farthest key at the right of the cursor.
			if (dx1>0 && (dx1>farthestRightX || (dx1==farthestRightX && dy<farthestRightY))) {
				farthestRightKey=i; farthestRightX=dx1; farthestRightY=dy;
			}
		}
		
		if (nearestLeftKey>=0) { setCursorKey(nearestLeftKey); dirty=true; }
		else if (farthestRightKey>=0) { setCursorKey(farthestRightKey); dirty=true; }
	}

	/**
	 * Move the cursor to the right.
	 */
	public void moveRight() {
		int nearestRightKey=-1, nearestRightX=Integer.MAX_VALUE, nearestRightY=Integer.MAX_VALUE;
		int farthestLeftKey=-1, farthestLeftX=Integer.MIN_VALUE, farthestLeftY=Integer.MIN_VALUE;
		
		for (int i=0; i<keyMapKeysNb; i++) {
			if (i==cursorKey) continue;
			
			int tableData[]=KEY_IMAGE_TABLE[keyImg[i]];
			int mapData[]=keyMapData[i];
			int kx0=mapData[1], ky0=mapData[2], kx1=kx0+tableData[3]-1, ky1=ky0+tableData[4]-1;
			
			int dx0=kx0-cursorX1, dx1=cursorX0-kx0, dy=pointToLineDistance(cursorY, ky0, ky1);
			
			// Find the nearest key at the right of the cursor.
			if (dx0>0 && (dx0<nearestRightX || (dx0==nearestRightX && dy<nearestRightY))) {
				nearestRightKey=i; nearestRightX=dx0; nearestRightY=dy;
			}
			// Find the farthest key at the left of the cursor.
			if (dx1>0 && (dx1>farthestLeftX || (dx1==farthestLeftX && dy<farthestLeftY))) {
				farthestLeftKey=i; farthestLeftX=dx1; farthestLeftY=dy;
			}
		}
		
		if (nearestRightKey>=0) { setCursorKey(nearestRightKey); dirty=true; }
		else if (farthestLeftKey>=0) { setCursorKey(farthestLeftKey); dirty=true; }		
	}

	/**
	 * Move the cursor up.
	 */
	public void moveUp() {
		int nearestUpKey=-1, nearestUpX=Integer.MAX_VALUE, nearestUpY=Integer.MAX_VALUE;
		int farthestDownKey=-1, farthestDownX=Integer.MIN_VALUE, farthestDownY=Integer.MIN_VALUE;
		
		for (int i=0; i<keyMapKeysNb; i++) {
			if (i==cursorKey) continue;
			
			int tableData[]=KEY_IMAGE_TABLE[keyImg[i]];
			int mapData[]=keyMapData[i];
			int kx0=mapData[1], ky0=mapData[2], kx1=kx0+tableData[3]-1, ky1=ky0+tableData[4]-1;

			int dx=pointToLineDistance(cursorX, kx0, kx1), dy0=cursorY0-ky1, dy1=ky1-cursorY1;
			
			// Find the nearest key at the top of the cursor.
			if (dy0>0 && (dy0<nearestUpY || (dy0==nearestUpY && dx<nearestUpX))) {
				nearestUpKey=i; nearestUpX=dx; nearestUpY=dy0;
			}
			// Find the farthest key at the bottom of the cursor.
			if (dy1>0 && (dy1>farthestDownY || (dy1==farthestDownY && dx<farthestDownX))) {
				farthestDownKey=i; farthestDownX=dx; farthestDownY=dy1;
			}
		}
		
		if (nearestUpKey>=0) { setCursorKey(nearestUpKey); dirty=true; }
		else if (farthestDownKey>=0) { setCursorKey(farthestDownKey); dirty=true; }				
	}
	
	/**
	 * Move the cursor down.
	 */
	public void moveDown() {
		int nearestDownKey=-1, nearestDownX=Integer.MAX_VALUE, nearestDownY=Integer.MAX_VALUE;
		int farthestUpKey=-1, farthestUpX=Integer.MIN_VALUE, farthestUpY=Integer.MIN_VALUE;
		
		for (int i=0; i<keyMapKeysNb; i++) {
			if (i==cursorKey) continue;
			
			int tableData[]=KEY_IMAGE_TABLE[keyImg[i]];
			int mapData[]=keyMapData[i];
			int kx0=mapData[1], ky0=mapData[2], kx1=kx0+tableData[3]-1, ky1=ky0+tableData[4]-1;

			int dx=pointToLineDistance(cursorX, kx0, kx1), dy0=ky0-cursorY1, dy1=cursorY0-ky0;
			
			// Find the nearest key at the bottom of the cursor.			
			if (dy0>0 && (dy0<nearestDownY || (dy0==nearestDownY && dx<nearestDownX))) {
				nearestDownKey=i; nearestDownX=dx; nearestDownY=dy0;
			}			
			// Find the farthest key at the top of the cursor.
			if (dy1>0 && (dy1>farthestUpY || (dy1==farthestUpY && dx<farthestUpX))) {
				farthestUpKey=i; farthestUpX=dx; farthestUpY=dy1;
			}
		}
		
		if (nearestDownKey>=0) { setCursorKey(nearestDownKey); dirty=true; }
		else if (farthestUpKey>=0) { setCursorKey(farthestUpKey); dirty=true; }		
	}

	private int pointToLineDistance(int x, int lineX0, int lineX1) {
		if (x<lineX0) return lineX0-x;
		if (x>lineX1) return x-lineX1;
		return 0;
	}
	
	// Update the cursor.
	private void updateCursor() {
		int bestMatchKey=-1;
		int bestMatchDistance=Integer.MAX_VALUE;
		for (int i=0; i<keyMapKeysNb; i++) {
			int tableData[]=KEY_IMAGE_TABLE[keyImg[i]];
			int mapData[]=keyMapData[i];
			int kx=mapData[1]+(tableData[3]>>1), ky=mapData[2]+(tableData[4]>>1);
			
			// Find the nearest key.
			int dx=cursorX-kx, dy=cursorY-ky, d=dx*dx+dy*dy;
			if (d<bestMatchDistance) { bestMatchKey=i; bestMatchDistance=d; }
		}
		setCursorKey(bestMatchKey);
	}
	
	// Set the cursor at a given key.
	private void setCursorKey(int k) {
		if (k<0 || k>=keyMapKeysNb) { cursorKey=-1; cursorX=0; cursorY=0; return; }
		
		// Set the cursor position at the center of the key.
		int tableData[]=KEY_IMAGE_TABLE[keyImg[k]];
		int mapData[]=keyMapData[k];
		cursorKey=k;
		cursorX0=mapData[1]; cursorY0=mapData[2]; cursorX1=cursorX0+tableData[3]-1; cursorY1=cursorY0+tableData[4]-1;
		cursorX=(cursorX0+cursorX1)>>1; cursorY=(cursorY0+cursorY1)>>1;
	}
	
	/**
	 * Push the key currently under the cursor.
	 * @return	a value <0 if this is a private key used internally (like shift or capslock).
	 * 			the Unicode value of the character if the key represents a valid Unicode character.
	 * 			a value inside the private area of the Basic Multilingual Plane (BMP) of Unicode if the key does not represent a valid Unicode character.
	 */
	public int pushKey() {
		return (cursorKey>=0) ? pushKey(cursorKey) : -1;
	}

	/**
	 * Pushes a key at the given pixel coordinate.
	 * @param x the x-coordinate in pixels where the key must be pressed
	 * @param y the y-coordinate in pixels where the key must be pressed
	 * @return	a value <0 if there is no key under the given position or if this is a private key used internally (like shift or capslock).
	 * 			the Unicode value of the character if the key represents a valid Unicode character.
	 * 			a value inside the private area of the Basic Multilingual Plane (BMP) of Unicode if the key does not represent a valid Unicode character.
	 */
	public int pushKey(int x, int y) {
		int key=-1;
		for (int i=0; i<keyMapKeysNb; i++) {
			int tableData[]=KEY_IMAGE_TABLE[keyImg[i]];
			int mapData[]=keyMapData[i];
			int kx=mapData[1], ky=mapData[2], kw=tableData[3], kh=tableData[4];
			if (x>=kx && x<(kx+kw) && y>=ky && y<(ky+kh)) { key=i; break; }
		}		
		return key<0 ? -1 : pushKey(key);

	}
	
	/**
	 * Pushes a given key.
	 * @param key
	 * @return	a value <0 if the key is invalid or if this is a private key used internally (like shift or capslock).
	 * 			the Unicode value of the character if the key represents a valid Unicode character.
	 * 			a value inside the private area of the Basic Multilingual Plane (BMP) of Unicode if the key does not represent a valid Unicode character.
	 */
	private int pushKey(int key) {
		if (key<0 || key>keyMapKeysNb) return -1;
		
		setCursorKey(key);
		
		boolean mapDirty=false;
		int newMap=-1;
		
		if (lastKey>=0) {
			shiftOn=false; mapDirty=true;
			keyPressed[lastKey]=false; lastKey=-1; dirty=true;
		}
				
		int inputCharacter=keyMapData[key][0], outputCharacter=-1;
		
		// Convert some special keys.
		switch (inputCharacter) {
		case KEY_ENTER_LARGE: inputCharacter=KEY_ENTER; break;
		case KEY_CANCEL_LARGE: inputCharacter=KEY_CANCEL; break;
		case KEY_SHIFT_LARGE: inputCharacter=KEY_SHIFT; break;
		case KEY_CAPSLOCK_LARGE: inputCharacter=KEY_CAPSLOCK; break;
		case KEY_SPACE_LARGE: inputCharacter=' '; break;
		case KEY_BACKSPACE_LARGE: inputCharacter=KEY_BACKSPACE; break;
//		case KEY_RETURN_LARGE: inputCharacter=KEY_RETURN; break;
//		case KEY_NUMLOCK_LARGE: inputCharacter=KEY_NUMLOCK; break;
//		case KEY_HELP_LARGE: inputCharacter=KEY_HELP; break;
//		case KEY_PAUSE_LARGE: inputCharacter=KEY_PAUSE; break;
//		case KEY_INSERT_LARGE: inputCharacter=KEY_INSERT; break;
//		case KEY_UNDO_LARGE: inputCharacter=KEY_UNDO; break;
//		case KEY_PAGE_UP_LARGE: inputCharacter=KEY_PAGE_UP; break;
//		case KEY_PAGE_DOWN_LARGE: inputCharacter=KEY_PAGE_DOWN; break;
		}

		// Process key.
		switch (inputCharacter) {
		default:
			outputCharacter=inputCharacter;
			lastKey=key;
			keyPressed[key]=true;
			dirty=true;
			break;
		case KEY_CAPSLOCK:
			shiftOn=false;
			capslockOn=!capslockOn;
			mapDirty=true;
			break;
		case KEY_SHIFT:
			shiftOn=!shiftOn;
			mapDirty=true;
			break;
		case KEY_ABC:
			shiftOn=false;
			mapDirty=true; newMap=MAP_ABC;
			break;
		case KEY_ACCENTS:
			shiftOn=false;
			mapDirty=true; newMap=MAP_ACCENTS;
			break;
		case KEY_SYMBOLS:
			shiftOn=false;
			mapDirty=true; newMap=MAP_SYMBOLS;
			break;
		}

		if (mapDirty) setMap(newMap);
		
		return outputCharacter;		
	}
	
	/**
	 * Releases the currently pushed key.
	 */
	public void releaseKey() {
		boolean mapDirty=false;

		if (lastKey>=0) {
			if (shiftOn) { shiftOn=false; mapDirty=true; }
			keyPressed[lastKey]=false; lastKey=-1; dirty=true;
		}
		
		if (mapDirty) setMap(-1);
	}
	
	/**
	 * Returns whether the keyboard image needs to be redrawn.  
	 * @return <code>true</code> if the keyboard image has changed since the last draw.
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * Draws the image of the keyboard.
	 * This method needs to be called only if the method isDirty() returns <code>true</code>. 
	 * @param image An array in which the image will be drawn. It must have a size of at least WIDTH*HEIGHT.
	 */
	public void draw(int image[]) {
		// First, we clear the whole image.
		{
			int n=WIDTH*HEIGHT;
			for (int i=0; i<n; i++) image[i]=0xffffffff;
		}
		
		// Draw each key.
		for (int i=0; i<keyMapKeysNb; i++) {
			int keyImgInfos[]=KEY_IMAGE_TABLE[keyImg[i]];
			int mapData[]=keyMapData[i];
			
			// Color for foreground and background pixels. They are different depending if the key is pressed or not.
			int backgroundColor, foregroundColor;
			if (cursorActivated && cursorKey==i) {
				if (keyPressed[i]) {
					backgroundColor=0xff600000; foregroundColor=0xffffffff;
				} else {
					backgroundColor=0xff00c000; foregroundColor=0xff000000;
				}
			} else {
				if (keyPressed[i]) {
					backgroundColor=0xff404040; foregroundColor=0xffffffff;
				} else {
					backgroundColor=0xffc0c0c0; foregroundColor=0xff000000;
				}
			}
			
			int sx0=keyImgInfos[1], sy0=keyImgInfos[2], w=keyImgInfos[3], h=keyImgInfos[4];
			int dx0=mapData[1], dy0=mapData[2];
			
			// Draw the key image.
			for (int y=1; y<h; y++) {
				int sk=(sy0+y)*KEY_IMAGE_WIDTH+sx0, dk=(dy0+y)*WIDTH+dx0;
				for (int x=1; x<w; x++) {
					int sk0=sk+x;
					int pixel=KEY_IMAGE[sk0>>5]&(1<<(31-(sk0&0x1f)));
					image[dk+x]=pixel!=0 ? foregroundColor : backgroundColor;
				}
			}
		}
		
		dirty=false;
	}
	
	/**
	 * Sets the current key map.
	 * @param map
	 */
	private void setMap(int map) {
		if (map<0) map=keyMap;
		boolean shiftFlag=capslockOn&&!shiftOn || !capslockOn&&shiftOn;
		
		switch (map) {
		default: return;
		case MAP_ABC:
			switch (layout) {
			default:
			case LAYOUT_QWERTY: keyMapData=shiftFlag ? KEY_MAP_QWERTY_UPPER : KEY_MAP_QWERTY_LOWER; break;
			case LAYOUT_AZERTY: keyMapData=shiftFlag ? KEY_MAP_AZERTY_UPPER : KEY_MAP_AZERTY_LOWER; break;
			}
			break;
		case MAP_ACCENTS: keyMapData=shiftFlag ? KEY_MAP_ACCENTS_UPPER : KEY_MAP_ACCENTS_LOWER; ; break;
		case MAP_SYMBOLS: keyMapData=KEY_MAP_SYMBOLS; break;
		}
		keyMap=map;
		keyMapKeysNb=keyMapData.length;
		lastKey=-1;
		dirty=true;

		// Build the tables.
		for (int i=0; i<keyMapKeysNb; i++) {
			int mapData[]=keyMapData[i];
			boolean flag;
			switch (mapData[0]) {
			default: flag=false; break;
			case KEY_CAPSLOCK: flag=capslockOn; break;
			case KEY_SHIFT: flag=shiftOn; break;
			case KEY_ABC: flag=map==MAP_ABC; break;
			case KEY_ACCENTS: flag=map==MAP_ACCENTS; break;
			case KEY_SYMBOLS: flag=map==MAP_SYMBOLS; break;
			}
			keyPressed[i]=flag;
			keyImg[i]=0;
			
			// Find the data corresponding to the key using binary search.
			int l=0, r=KEY_IMAGE_TABLE.length-1;
			while (l<=r) {
				int m=(l+r)>>1;
				int me[]=KEY_IMAGE_TABLE[m];
				if (mapData[0]==me[0]) { keyImg[i]=m; break; }
				if (mapData[0]<me[0]) r=m-1; else l=m+1;
			}
		}
		
		updateCursor();
	}
	
	//--------------------------------------------------------------------------------
	// .
	//--------------------------------------------------------------------------------
	// Map for QWERTY keyboard with lower case characters.
	private final static int KEY_MAP_QWERTY_LOWER[][]={
		{ KEY_ABC, 0, 0 }, { '1', 16, 0 }, { '2', 32, 0 }, { '3', 48, 0 }, { '4', 64, 0 }, { '5', 80, 0 }, { '6', 96, 0 }, { '7', 112, 0 }, { '8', 128, 0 }, { '9', 144, 0 }, { '0', 160, 0},
		{ KEY_ACCENTS, 0, 16 }, { 'q', 16, 16 }, { 'w', 32, 16 }, { 'e', 48, 16 }, { 'r', 64, 16 }, { 't', 80, 16 }, { 'y', 96, 16 }, { 'u', 112, 16 }, { 'i', 128, 16 }, { 'o', 144, 16 }, { 'p', 160, 16 },
		{ KEY_SYMBOLS, 0, 32 }, { 'a', 16, 32 }, { 's', 32, 32 }, { 'd', 48, 32 }, { 'f', 64, 32 }, { 'g', 80, 32 }, { 'h', 96, 32 }, { 'j', 112, 32 }, { 'k', 128, 32 }, { 'l', 144, 32 }, { '\'', 160, 32 },
		{ KEY_CAPSLOCK, 0, 48 }, { 'z', 16, 48 }, { 'x', 32, 48 }, { 'c', 48, 48 }, { 'v', 64, 48 }, { 'b', 80, 48 }, { 'n', 96, 48 }, { 'm', 112, 48 }, { '(', 128, 48 }, { ')', 144, 48 }, { KEY_CANCEL, 160, 48 },
		{ KEY_SHIFT, 0, 64 }, { KEY_BACKSPACE, 16, 64 }, { ' ', 32, 64 }, { ',', 48, 64 }, { ':', 64, 64 }, { ';', 80, 64 }, { '.', 96, 64 }, { '!', 112, 64 }, { '?', 128, 64 }, { '-', 144, 64 }, { KEY_ENTER, 160, 64 } 
	};
	
	// Map for QWERTY keyboard with upper case characters.
	private final static int KEY_MAP_QWERTY_UPPER[][]={
		{ KEY_ABC, 0, 0 }, { '1', 16, 0 }, { '2', 32, 0 }, { '3', 48, 0 }, { '4', 64, 0 }, { '5', 80, 0 }, { '6', 96, 0 }, { '7', 112, 0 }, { '8', 128, 0 }, { '9', 144, 0 }, { '0', 160, 0},
		{ KEY_ACCENTS, 0, 16 }, { 'Q', 16, 16 }, { 'W', 32, 16 }, { 'E', 48, 16 }, { 'R', 64, 16 }, { 'T', 80, 16 }, { 'Y', 96, 16 }, { 'U', 112, 16 }, { 'I', 128, 16 }, { 'O', 144, 16 }, { 'P', 160, 16 },
		{ KEY_SYMBOLS, 0, 32 }, { 'A', 16, 32 }, { 'S', 32, 32 }, { 'D', 48, 32 }, { 'F', 64, 32 }, { 'G', 80, 32 }, { 'H', 96, 32 }, { 'J', 112, 32 }, { 'K', 128, 32 }, { 'L', 144, 32 }, { '\'', 160, 32 },
		{ KEY_CAPSLOCK, 0, 48 }, { 'Z', 16, 48 }, { 'X', 32, 48 }, { 'C', 48, 48 }, { 'V', 64, 48 }, { 'B', 80, 48 }, { 'N', 96, 48 }, { 'M', 112, 48 }, { '(', 128, 48 }, { ')', 144, 48 }, { KEY_CANCEL, 160, 48 },
		{ KEY_SHIFT, 0, 64 }, { KEY_BACKSPACE, 16, 64 }, { ' ', 32, 64 }, { ',', 48, 64 }, { ':', 64, 64 }, { ';', 80, 64 }, { '.', 96, 64 }, { '!', 112, 64 }, { '?', 128, 64 }, { '-', 144, 64 }, { KEY_ENTER, 160, 64 } 
	};
	
	// Map for AZERTY keyboard with lower case characters.
	private final static int KEY_MAP_AZERTY_LOWER[][]={
		{ KEY_ABC, 0, 0 }, { '1', 16, 0 }, { '2', 32, 0 }, { '3', 48, 0 }, { '4', 64, 0 }, { '5', 80, 0 }, { '6', 96, 0 }, { '7', 112, 0 }, { '8', 128, 0 }, { '9', 144, 0 }, { '0', 160, 0},
		{ KEY_ACCENTS, 0, 16 }, { 'a', 16, 16 }, { 'z', 32, 16 }, { 'e', 48, 16 }, { 'r', 64, 16 }, { 't', 80, 16 }, { 'y', 96, 16 }, { 'u', 112, 16 }, { 'i', 128, 16 }, { 'o', 144, 16 }, { 'p', 160, 16 },
		{ KEY_SYMBOLS, 0, 32 }, { 'q', 16, 32 }, { 's', 32, 32 }, { 'd', 48, 32 }, { 'f', 64, 32 }, { 'g', 80, 32 }, { 'h', 96, 32 }, { 'j', 112, 32 }, { 'k', 128, 32 }, { 'l', 144, 32 }, { 'm', 160, 32 },
		{ KEY_CAPSLOCK, 0, 48 }, { 'w', 16, 48 }, { 'x', 32, 48 }, { 'c', 48, 48 }, { 'v', 64, 48 }, { 'b', 80, 48 }, { 'n', 96, 48 }, { '\'', 112, 48 }, { '(', 128, 48 }, { ')', 144, 48 }, { KEY_CANCEL, 160, 48 },
		{ KEY_SHIFT, 0, 64 }, { KEY_BACKSPACE, 16, 64 }, { ' ', 32, 64 }, { ',', 48, 64 }, { ':', 64, 64 }, { ';', 80, 64 }, { '.', 96, 64 }, { '!', 112, 64 }, { '?', 128, 64 }, { '-', 144, 64 }, { KEY_ENTER, 160, 64 } 
	};
	
	// Map for AZERTY keyboard with upper case characters.
	private final static int KEY_MAP_AZERTY_UPPER[][]={
		{ KEY_ABC, 0, 0 }, { '1', 16, 0 }, { '2', 32, 0 }, { '3', 48, 0 }, { '4', 64, 0 }, { '5', 80, 0 }, { '6', 96, 0 }, { '7', 112, 0 }, { '8', 128, 0 }, { '9', 144, 0 }, { '0', 160, 0},
		{ KEY_ACCENTS, 0, 16 }, { 'A', 16, 16 }, { 'Z', 32, 16 }, { 'E', 48, 16 }, { 'R', 64, 16 }, { 'T', 80, 16 }, { 'Y', 96, 16 }, { 'U', 112, 16 }, { 'I', 128, 16 }, { 'O', 144, 16 }, { 'P', 160, 16 },
		{ KEY_SYMBOLS, 0, 32 }, { 'Q', 16, 32 }, { 'S', 32, 32 }, { 'D', 48, 32 }, { 'F', 64, 32 }, { 'G', 80, 32 }, { 'H', 96, 32 }, { 'J', 112, 32 }, { 'K', 128, 32 }, { 'L', 144, 32 }, { 'M', 160, 32 },
		{ KEY_CAPSLOCK, 0, 48 }, { 'W', 16, 48 }, { 'X', 32, 48 }, { 'C', 48, 48 }, { 'V', 64, 48 }, { 'B', 80, 48 }, { 'N', 96, 48 }, { '\'', 112, 48 }, { '(', 128, 48 }, { ')', 144, 48 }, { KEY_CANCEL, 160, 48 },
		{ KEY_SHIFT, 0, 64 }, { KEY_BACKSPACE, 16, 64 }, { ' ', 32, 64 }, { ',', 48, 64 }, { ':', 64, 64 }, { ';', 80, 64 }, { '.', 96, 64 }, { '!', 112, 64 }, { '?', 128, 64 }, { '-', 144, 64 }, { KEY_ENTER, 160, 64 } 
	};
	
	// Map for accentuated lower case characters.
	private final static int KEY_MAP_ACCENTS_LOWER[][]={
		{ KEY_ABC, 0, 0 }, { '`', 16, 0 }, { 0xb4, 32, 0 }, { '^', 48, 0 }, { 0xa8, 64, 0 }, { '~', 80, 0 }, { 0xb0, 96, 0 }, { 0xaf, 112, 0 }, { 0xb8, 128, 0}, { 0xe0, 144, 0 }, { 0xe1, 160, 0 },
		{ KEY_ACCENTS, 0, 16 }, { 0xe2, 16, 16 }, { 0xe3, 32, 16 }, { 0xe4, 48, 16 }, { 0xe5, 64, 16 }, { 0xe6, 80, 16 }, { 0xe7, 96, 16 }, { 0xe8, 112, 16 }, { 0xe9, 128, 16 }, { 0xea, 144, 16 }, { 0xeb, 160, 16 },
		{ KEY_SYMBOLS, 0, 32 }, { 0xec, 16, 32 }, { 0xed, 32, 32 }, { 0xee, 48, 32 }, { 0xef, 64, 32 }, { 0xf0, 80, 32 }, { 0xf1, 96, 32 }, { 0xf2, 112, 32 }, { 0xf3, 128, 32 }, { 0xf4, 144, 32 }, { 0xf5, 160, 32 },
		{ KEY_CAPSLOCK, 0, 48 }, { 0xf6, 16, 48 }, { 0xf8, 32, 48 }, { 0xf9, 48, 48 }, { 0xfa, 64, 48 }, { 0xfb, 80, 48 },  { 0xfc, 96, 48 }, { 0xfd, 112, 48 }, { 0xfe, 128, 48 }, { 0xff, 144, 48 }, { KEY_CANCEL, 160, 48 },
		{ KEY_SHIFT, 0, 64 }, { KEY_BACKSPACE, 16, 64 }, { ' ', 32, 64 }, { ',', 48, 64 }, { ':', 64, 64 }, { ';', 80, 64 }, { '.', 96, 64 }, { '!', 112, 64 }, { '?', 128, 64 }, { '-', 144, 64 }, { KEY_ENTER, 160, 64 } 
	};
	
	// Map for accentuated upper case characters.
	private final static int KEY_MAP_ACCENTS_UPPER[][]={
		{ KEY_ABC, 0, 0 }, { '`', 16, 0 }, { 0xb4, 32, 0 }, { '^', 48, 0 }, { 0xa8, 64, 0 }, { '~', 80, 0 }, { 0xb0, 96, 0 }, { 0xaf, 112, 0 }, { 0xb8, 128, 0}, { 0xc0, 144, 0 }, { 0xc1, 160, 0 }, 
		{ KEY_ACCENTS, 0, 16 }, { 0xc2, 16, 16 }, { 0xc3, 32, 16 }, { 0xc4, 48, 16 }, { 0xc5, 64, 16 }, { 0xc6, 80, 16 }, { 0xc7, 96, 16 }, { 0xc8, 112, 16 }, { 0xc9, 128, 16 }, { 0xca, 144, 16 }, { 0xcb, 160, 16 }, 
		{ KEY_SYMBOLS, 0, 32 }, { 0xcc, 16, 32 }, { 0xcd, 32, 32 }, { 0xce, 48, 32 }, { 0xcf, 64, 32 }, { 0xd0, 80, 32 }, { 0xd1, 96, 32 }, { 0xd2, 112, 32 }, { 0xd3, 128, 32 },{ 0xd4, 144, 32 }, { 0xd5, 160, 32 }, 
		{ KEY_CAPSLOCK, 0, 48 }, { 0xd6, 16, 48 }, { 0xd8, 32, 48 }, { 0xd9, 48, 48 }, { 0xda, 64, 48 }, { 0xdb, 80, 48 },  { 0xdc, 96, 48 }, { 0xdd, 112, 48 }, { 0xde, 128, 48 }, { 0xdf, 144, 48 }, { KEY_CANCEL, 160, 48 },
		{ KEY_SHIFT, 0, 64 }, { KEY_BACKSPACE, 16, 64 }, { ' ', 32, 64 }, { ',', 48, 64 }, { ':', 64, 64 }, { ';', 80, 64 }, { '.', 96, 64 }, { '!', 112, 64 }, { '?', 128, 64 }, { '-', 144, 64 }, { KEY_ENTER, 160, 64 } 
	};
	
	// Map for symbol characters.
	private final static int KEY_MAP_SYMBOLS[][]={
		{ KEY_ABC, 0, 0 }, { '&', 16, 0 }, { '|', 32, 0 }, { '-', 48, 0 }, { '+', 64, 0 }, { '*', 80, 0 }, { '/', 96, 0 }, { '=', 112, 0 }, { '<', 128, 0 }, { '>', 144, 0 }, { '~', 160, 0},
		{ KEY_ACCENTS, 0, 16 }, { 0xac, 16, 16 }, { 0xa6, 32, 16 }, { '_', 48, 16 }, { 0xf7, 64, 16 }, { 0xd7, 80, 16 }, { '\\', 96, 16 }, { 0xb1, 112, 16 }, { 0xab, 128, 16 }, { 0xbb, 144, 16 }, { '"', 160, 16 },
		{ KEY_SYMBOLS, 0, 32 }, { 0xa4, 16, 32 }, { '$', 32, 32 }, { 0xa2, 48, 32 }, { 0xa3, 64, 32 }, { 0xa5, 80, 32 }, { 0x20ac, 96, 32 }, { 0xb7, 112, 32 }, { '{', 128, 32 }, { '}', 144, 32 }, { '\'', 160, 32 },
		{ KEY_CAPSLOCK, 0, 48 }, { 0xa9, 16, 48 }, { 0xae, 32, 48 }, { 0xb5, 48, 48 }, { 0xb6, 64, 48 }, { 0xa7, 80, 48 }, { '?', 96, 48 }, { '!', 112, 48 },  { '[', 128, 48 }, { ']', 144, 48 }, { KEY_CANCEL, 160, 48 },
		{ KEY_SHIFT, 0, 64 }, { KEY_BACKSPACE, 16, 64 }, { ' ', 32, 64 }, { '#', 48, 64 }, { '@', 64, 64 }, { '%', 80, 64 }, { 0xbf, 96, 64 }, { 0xa1, 112, 64 }, { '(', 128, 64 }, { ')', 144, 64 }, { KEY_ENTER, 160, 64 } 
	};
	
	// Map giving the position and size of keys in the key image.
	private final static int KEY_IMAGE_TABLE[][]={
		{ KEY_BACKSPACE,	112,	192,	16, 16 },
		{ KEY_TAB,			64,		192,	16, 16 },
		{ KEY_ENTER,		0,		192,	16, 16 },
		{ KEY_CANCEL,		16,		192,	16, 16 },
		{ KEY_ESCAPE,		128,	192,	16, 16 },
		
		{ ' ',		0,		0,		16, 16 },
		{ '!',		16,		0,		16, 16 },
		{ '"',		32,		0,		16, 16 },
		{ '#',		48,		0,		16, 16 },
		{ '$',		64,		0,		16, 16 },
		{ '%',		80,		0,		16, 16 },
		{ '&',		96,		0,		16, 16 },
		{ '\'', 	112,	0,		16, 16 },
		{ '(',		128,	0,		16, 16 },
		{ ')',		144,	0,		16, 16 },
		{ '*',		160,	0,		16, 16 },
		{ '+',		176,	0,		16, 16 },
		{ ',',		192,	0,		16, 16 },
		{ '-',		208,	0,		16, 16 },
		{ '.',		224,	0,		16, 16 },
		{ '/',		240,	0,		16, 16 },
		
		{ '0',		0,		16,		16, 16 },
		{ '1',		16,		16,		16, 16 },
		{ '2',		32,		16,		16, 16 },
		{ '3',		48,		16,		16, 16 },
		{ '4',		64,		16,		16, 16 },
		{ '5',		80,		16,		16, 16 },
		{ '6',		96,		16,		16, 16 },
		{ '7',		112,	16,		16, 16 },
		{ '8',		128,	16,		16, 16 },
		{ '9',		144,	16,		16, 16 },
		{ ':',		160,	16,		16, 16 },
		{ ';',		176,	16,		16, 16 },
		{ '<',		192,	16,		16, 16 },
		{ '=',		208,	16,		16, 16 },
		{ '>',		224,	16,		16, 16 },
		{ '?',		240,	16,		16, 16 },
		
		{ '@',		0,		32,		16, 16 },
		{ 'A',		16,		32,		16, 16 },
		{ 'B',		32,		32,		16, 16 },
		{ 'C',		48,		32,		16, 16 },
		{ 'D',		64,		32,		16, 16 },
		{ 'E',		80,		32,		16, 16 },
		{ 'F',		96,		32,		16, 16 },
		{ 'G',		112,	32,		16, 16 },
		{ 'H',		128,	32,		16, 16 },
		{ 'I',		144,	32,		16, 16 },
		{ 'J',		160,	32,		16, 16 },
		{ 'K',		176,	32,		16, 16 },
		{ 'L',		192,	32,		16, 16 },
		{ 'M',		208,	32,		16, 16 },
		{ 'N',		224,	32,		16, 16 },
		{ 'O',		240,	32,		16, 16 },
		
		{ 'P',		0,		48,		16, 16 },
		{ 'Q',		16,		48,		16, 16 },
		{ 'R',		32,		48,		16, 16 },
		{ 'S',		48,		48,		16, 16 },
		{ 'T',		64,		48,		16, 16 },
		{ 'U',		80,		48,		16, 16 },
		{ 'V',		96,		48,		16, 16 },
		{ 'W',		112,	48,		16, 16 },
		{ 'X',		128,	48,		16, 16 },
		{ 'Y',		144,	48,		16, 16 },
		{ 'Z',		160,	48,		16, 16 },
		{ '[',		176,	48,		16, 16 },
		{ '\\',		192,	48,		16, 16 },
		{ ']',		208,	48,		16, 16 },
		{ '^',		224,	48,		16, 16 },
		{ '_',		240,	48,		16, 16 },

		{ '`',		0,		64,		16, 16 },
		{ 'a',		16,		64,		16, 16 },
		{ 'b',		32,		64,		16, 16 },
		{ 'c',		48,		64,		16, 16 },
		{ 'd',		64,		64,		16, 16 },
		{ 'e',		80,		64,		16, 16 },
		{ 'f',		96,		64,		16, 16 },
		{ 'g',		112,	64,		16, 16 },
		{ 'h',		128,	64,		16, 16 },
		{ 'i',		144,	64,		16, 16 },
		{ 'j',		160,	64,		16, 16 },
		{ 'k',		176,	64,		16, 16 },
		{ 'l',		192,	64,		16, 16 },
		{ 'm',		208,	64,		16, 16 },
		{ 'n',		224,	64,		16, 16 },
		{ 'o',		240,	64,		16, 16 },

		{ 'p',			0,		80,		16, 16 },
		{ 'q',			16,		80,		16, 16 },
		{ 'r',			32,		80,		16, 16 },
		{ 's',			48,		80,		16, 16 },
		{ 't',			64,		80,		16, 16 },
		{ 'u',			80,		80,		16, 16 },
		{ 'v',			96,		80,		16, 16 },
		{ 'w',			112,	80,		16, 16 },
		{ 'x',			128,	80,		16, 16 },
		{ 'y',			144,	80,		16, 16 },
		{ 'z',			160,	80,		16, 16 },
		{ '{',			176,	80,		16, 16 },
		{ '|',			192,	80,		16, 16 },
		{ '}',			208,	80,		16, 16 },
		{ '~',			224,	80,		16, 16 },
		{ KEY_DELETE,	240,	80,		16, 16 },
		
		{ 0xa0,		0,		96,		16, 16 },
		{ 0xa1,		16,		96,		16, 16 },
		{ 0xa2,		32,		96,		16, 16 },
		{ 0xa3,		48,		96,		16, 16 },
		{ 0xa4,		64,		96,		16, 16 },
		{ 0xa5,		80,		96,		16, 16 },
		{ 0xa6,		96,		96,		16, 16 },
		{ 0xa7,		112,	96,		16, 16 },
		{ 0xa8,		128,	96,		16, 16 },
		{ 0xa9,		144,	96,		16, 16 },
		{ 0xaa,		160,	96,		16, 16 },
		{ 0xab,		176,	96,		16, 16 },
		{ 0xac,		192,	96,		16, 16 },
		{ 0xad,		208,	96,		16, 16 },
		{ 0xae,		224,	96,		16, 16 },
		{ 0xaf,		240,	96,		16, 16 },

		{ 0xb0,		0,		112,	16, 16 },
		{ 0xb1,		16,		112,	16, 16 },
		{ 0xb2,		32,		112,	16, 16 },
		{ 0xb3,		48,		112,	16, 16 },
		{ 0xb4,		64,		112,	16, 16 },
		{ 0xb5,		80,		112,	16, 16 },
		{ 0xb6,		96,		112,	16, 16 },
		{ 0xb7,		112,	112,	16, 16 },
		{ 0xb8,		128,	112,	16, 16 },
		{ 0xb9,		144,	112,	16, 16 },
		{ 0xba,		160,	112,	16, 16 },
		{ 0xbb,		176,	112,	16, 16 },
		{ 0xbc,		192,	112,	16, 16 },
		{ 0xbd,		208,	112,	16, 16 },
		{ 0xbe,		224,	112,	16, 16 },
		{ 0xbf,		240,	112,	16, 16 },

		{ 0xc0,		0,		128,	16, 16 },
		{ 0xc1,		16,		128,	16, 16 },
		{ 0xc2,		32,		128,	16, 16 },
		{ 0xc3,		48,		128,	16, 16 },
		{ 0xc4,		64,		128,	16, 16 },
		{ 0xc5,		80,		128,	16, 16 },
		{ 0xc6,		96,		128,	16, 16 },
		{ 0xc7,		112,	128,	16, 16 },
		{ 0xc8,		128,	128,	16, 16 },
		{ 0xc9,		144,	128,	16, 16 },
		{ 0xca,		160,	128,	16, 16 },
		{ 0xcb,		176,	128,	16, 16 },
		{ 0xcc,		192,	128,	16, 16 },
		{ 0xcd,		208,	128,	16, 16 },
		{ 0xce,		224,	128,	16, 16 },
		{ 0xcf,		240,	128,	16, 16 },

		{ 0xd0,		0,		144,	16, 16 },
		{ 0xd1,		16,		144,	16, 16 },
		{ 0xd2,		32,		144,	16, 16 },
		{ 0xd3,		48,		144,	16, 16 },
		{ 0xd4,		64,		144,	16, 16 },
		{ 0xd5,		80,		144,	16, 16 },
		{ 0xd6,		96,		144,	16, 16 },
		{ 0xd7,		112,	144,	16, 16 },
		{ 0xd8,		128,	144,	16, 16 },
		{ 0xd9,		144,	144,	16, 16 },
		{ 0xda,		160,	144,	16, 16 },
		{ 0xdb,		176,	144,	16, 16 },
		{ 0xdc,		192,	144,	16, 16 },
		{ 0xdd,		208,	144,	16, 16 },
		{ 0xde,		224,	144,	16, 16 },
		{ 0xdf,		240,	144,	16, 16 },

		{ 0xe0,		0,		160,	16, 16 },
		{ 0xe1,		16,		160,	16, 16 },
		{ 0xe2,		32,		160,	16, 16 },
		{ 0xe3,		48,		160,	16, 16 },
		{ 0xe4,		64,		160,	16, 16 },
		{ 0xe5,		80,		160,	16, 16 },
		{ 0xe6,		96,		160,	16, 16 },
		{ 0xe7,		112,	160,	16, 16 },
		{ 0xe8,		128,	160,	16, 16 },
		{ 0xe9,		144,	160,	16, 16 },
		{ 0xea,		160,	160,	16, 16 },
		{ 0xeb,		176,	160,	16, 16 },
		{ 0xec,		192,	160,	16, 16 },
		{ 0xed,		208,	160,	16, 16 },
		{ 0xee,		224,	160,	16, 16 },
		{ 0xef,		240,	160,	16, 16 },

		{ 0xf0,				0,		176,	16, 16 },
		{ 0xf1,				16,		176,	16, 16 },
		{ 0xf2,				32,		176,	16, 16 },
		{ 0xf3,				48,		176,	16, 16 },
		{ 0xf4,				64,		176,	16, 16 },
		{ 0xf5,				80,		176,	16, 16 },
		{ 0xf6,				96,		176,	16, 16 },
		{ 0xf7,				112,	176,	16, 16 },
		{ 0xf8,				128,	176,	16, 16 },
		{ 0xf9,				144,	176,	16, 16 },
		{ 0xfa,				160,	176,	16, 16 },
		{ 0xfb,				176,	176,	16, 16 },
		{ 0xfc,				192,	176,	16, 16 },
		{ 0xfd,				208,	176,	16, 16 },
		{ 0xfe,				224,	176,	16, 16 },
		{ 0xff,				240,	176,	16, 16 },

		{ 0x20ac,				64,		208,	16, 16 },		
		
		{ KEY_SHIFT,			32,		192,	16, 16 },
		{ KEY_CAPSLOCK,			48,		192,	16, 16 },
		{ KEY_ALTERNATE,		96,		192,	16, 16 },
		{ KEY_CONTROL,			112,	192,	16, 16 },
		
		{ KEY_ABC,				0,		208,	16, 16 },
		{ KEY_ACCENTS,			16,		208,	16, 16 },
		{ KEY_SYMBOLS,			32,		208,	16, 16 },
		{ KEY_132,				48,		208,	16, 16 },
		
		{ KEY_ENTER_LARGE,		0,		224,	32, 16 },
		{ KEY_CANCEL_LARGE,		32,		224,	32, 16 },
		{ KEY_SHIFT_LARGE,		64,		224,	32, 16 },
		{ KEY_CAPSLOCK_LARGE,	96,		224,	32, 16 },
		{ KEY_SPACE_LARGE,		128,	224,	32, 16 },
		{ KEY_BACKSPACE_LARGE,	160,	224,	32, 16 },
		{ KEY_RETURN_LARGE,		192,	224,	32, 16 },
		{ KEY_NUMLOCK_LARGE,	224,	224,	32, 16 },

		{ KEY_HELP_LARGE,		0,		240,	32, 16 },
		{ KEY_PAUSE_LARGE,		32,		240,	32, 16 },
		{ KEY_INSERT_LARGE,		64,		240,	32, 16 },
		{ KEY_UNDO_LARGE,		96,		240,	32, 16 },
		{ KEY_PAGE_UP_LARGE,	128,	240,	32, 16 },
		{ KEY_PAGE_DOWN_LARGE,	160,	240,	32, 16 },
	};

	// Width and height of the image of keys.
	private final static int KEY_IMAGE_WIDTH=256, KEY_IMAGE_HEIGHT=256;
		
	// Image of keys. It uses one bit per pixel.
	private final static int KEY_IMAGE[]={
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x82408000,0x80008000,0x80008080,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x82408000,0x80808000,0x80008080,0x80408100,0x80008000,0x80008000,0x80008020,
		0x80008080,0x82408000,0x80808200,0x80008080,0x80808080,0x80008000,0x80008000,0x80008020,
		0x80008080,0x82408140,0x83E08510,0x83808080,0x80808080,0x80008000,0x80008000,0x80008040,
		0x80008080,0x80008140,0x84908520,0x84408000,0x81008040,0x80008000,0x80008000,0x80008040,
		0x80008080,0x800087F0,0x84808240,0x84408000,0x81008040,0x80808080,0x80008000,0x80008080,
		0x80008080,0x80008140,0x83E08080,0x83808000,0x81008040,0x82A08080,0x80008000,0x80008080,
		0x80008080,0x80008140,0x80908120,0x84808000,0x81008040,0x81C083E0,0x800083E0,0x80008100,
		0x80008000,0x800087F0,0x84908250,0x84508000,0x81008040,0x82A08080,0x80008000,0x80008100,
		0x80008080,0x80008140,0x83E08450,0x84208000,0x81008040,0x80808080,0x80808000,0x80808200,
		0x80008080,0x80008140,0x80808020,0x83D08000,0x81008040,0x80008000,0x80808000,0x80808200,
		0x80008000,0x80008000,0x80808000,0x80008000,0x80808080,0x80008000,0x81008000,0x80008400,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80808080,0x80008000,0x80008000,0x80008400,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80408100,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x83C08080,0x83C083C0,0x804087E0,0x83C087E0,0x83C083C0,0x80008000,0x80008000,0x800081C0,
		0x84208380,0x84208420,0x80C08400,0x84008420,0x84208420,0x80008000,0x80008000,0x80008220,
		0x84608080,0x80208020,0x81408400,0x84008040,0x84208420,0x80808080,0x80408000,0x82008020,
		0x84E08080,0x80208020,0x824087C0,0x87C08040,0x84208420,0x80808080,0x80808000,0x81008020,
		0x85A08080,0x83C081C0,0x84408020,0x84208080,0x83C08420,0x80008000,0x810083E0,0x80808040,
		0x87208080,0x84008020,0x87E08020,0x84208080,0x842083E0,0x80008000,0x82008000,0x80408080,
		0x86208080,0x84008020,0x80408020,0x84208100,0x84208020,0x80008000,0x810083E0,0x80808000,
		0x84208080,0x84008420,0x80408420,0x84208100,0x84208020,0x80808080,0x80808000,0x81008080,
		0x83C083E0,0x87E083C0,0x804083C0,0x83C08100,0x83C083C0,0x80808080,0x80408000,0x82008080,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008100,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x800083C0,0x87C081E0,0x87C087E0,0x87E081E0,0x842083E0,0x81C08410,0x84008410,0x862081C0,
		0x81C08420,0x84208210,0x84208400,0x84008210,0x84208080,0x80408420,0x84008410,0x86208220,
		0x82208420,0x84208400,0x84108400,0x84008400,0x84208080,0x80408440,0x84008630,0x85208410,
		0x84908420,0x84208400,0x84108400,0x84008400,0x84208080,0x80408480,0x84008630,0x85208410,
		0x85508420,0x87C08400,0x841087C0,0x87C08400,0x87E08080,0x80408700,0x84008550,0x84A08410,
		0x855087E0,0x84208400,0x84108400,0x84008470,0x84208080,0x80408480,0x84008550,0x84A08410,
		0x84A08420,0x84208400,0x84108400,0x84008410,0x84208080,0x80408440,0x84008490,0x84608410,
		0x82008420,0x84208210,0x84208400,0x84008210,0x84208080,0x80408420,0x84008490,0x84608220,
		0x81E08420,0x87C081E0,0x87C087E0,0x840081E0,0x842083E0,0x83808410,0x87E08410,0x842081C0,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x800081C0,0x840081C0,0x80008000,
		0x87C081C0,0x87C083E0,0x87F08410,0x84108410,0x84108410,0x87F08100,0x84008040,0x80008000,
		0x84208220,0x84208410,0x80808410,0x84108410,0x84108410,0x80108100,0x82008040,0x80008000,
		0x84208410,0x84208400,0x80808410,0x84108410,0x82208220,0x80208100,0x82008040,0x80008000,
		0x84208410,0x84208400,0x80808410,0x82208410,0x81408140,0x80408100,0x81008040,0x80808000,
		0x87C08410,0x87C083E0,0x80808410,0x82208490,0x80808080,0x80808100,0x81008040,0x81408000,
		0x84008410,0x84808010,0x80808410,0x81408490,0x81408080,0x81008100,0x80808040,0x82208000,
		0x84008410,0x84408010,0x80808410,0x81408490,0x82208080,0x82008100,0x80808040,0x80008000,
		0x84008220,0x84208410,0x80808220,0x80808490,0x84108080,0x84008100,0x80408040,0x80008000,
		0x840081C0,0x841083E0,0x808081C0,0x80808360,0x84108080,0x87F08100,0x80408040,0x800087E0,
		0x80008040,0x80008000,0x80008000,0x80008000,0x80008000,0x80008100,0x80208040,0x80008000,
		0x80008030,0x80008000,0x80008000,0x80008000,0x80008000,0x80008100,0x80208040,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x800081C0,0x800081C0,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x81008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80808000,0x84008000,0x80208000,0x80E08000,0x84008080,0x80408400,0x81008000,0x80008000,
		0x80408000,0x84008000,0x80208000,0x81008000,0x84008080,0x80408400,0x81008000,0x80008000,
		0x80008000,0x84008000,0x80208000,0x81008000,0x84008000,0x80008400,0x81008000,0x80008000,
		0x800083C0,0x87C083C0,0x83E083C0,0x83C083E0,0x87C08380,0x81C08440,0x81008760,0x87C083C0,
		0x80008020,0x84208420,0x84208420,0x81008420,0x84208080,0x80408480,0x81008490,0x84208420,
		0x800083E0,0x84208400,0x842087E0,0x81008420,0x84208080,0x80408700,0x81008490,0x84208420,
		0x80008420,0x84208400,0x84208400,0x81008420,0x84208080,0x80408480,0x81008490,0x84208420,
		0x80008420,0x84208420,0x84208420,0x81008420,0x84208080,0x80408440,0x81008490,0x84208420,
		0x800083E0,0x87C083C0,0x83E083C0,0x810083E0,0x842083E0,0x80408420,0x80C08490,0x842083C0,
		0x80008000,0x80008000,0x80008000,0x80008020,0x80008000,0x80408000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008020,0x80008000,0x80408000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x800083C0,0x80008000,0x83808000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008060,0x80808300,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008080,0x80808080,0x80008000,
		0x80008000,0x80008000,0x81008000,0x80008000,0x80008000,0x80008080,0x80808080,0x80008000,
		0x80008000,0x80008000,0x81008000,0x80008000,0x80008000,0x80008080,0x80808080,0x80008C08,
		0x87C083E0,0x81E083C0,0x83C08420,0x82208410,0x86208420,0x87E08080,0x80808080,0x80008A48,
		0x84208420,0x82008400,0x81008420,0x82208410,0x81408420,0x80408080,0x80808080,0x83208AA8,
		0x84208420,0x820083C0,0x81008420,0x82208490,0x80808420,0x80808300,0x80808060,0x84C08AC8,
		0x84208420,0x82008020,0x81008420,0x82208490,0x81808420,0x81008080,0x80808080,0x80008C64,
		0x84208420,0x82008420,0x81008420,0x81408490,0x82408420,0x82008080,0x80808080,0x80008000,
		0x87C083E0,0x820083C0,0x80C083E0,0x80808360,0x843083E0,0x87E08080,0x80808080,0x80008000,
		0x84008020,0x80008000,0x80008000,0x80008000,0x80008020,0x80008080,0x80808080,0x80008000,
		0x84008020,0x80008000,0x80008000,0x80008000,0x80008020,0x80008080,0x80808080,0x80008000,
		0x84008020,0x80008000,0x80008000,0x80008000,0x800083C0,0x80008060,0x80808300,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80808000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008080,0x80008000,0x80008410,0x808083C0,0x80008000,0x81C08000,0x80008000,0x800087E0,
		0x80008080,0x800081C0,0x80008410,0x80808420,0x83608000,0x80208000,0x80008000,0x80008000,
		0x80008000,0x81008200,0x84108220,0x80808400,0x800081C0,0x81E08000,0x80008000,0x81C08000,
		0x80008080,0x83808200,0x83E08140,0x808083C0,0x80008220,0x82208120,0x80008000,0x82208000,
		0x80008080,0x85408200,0x82208080,0x80008420,0x800084D0,0x81E08240,0x80008000,0x85D08000,
		0x80008080,0x85008780,0x822087F0,0x80008420,0x80008510,0x80008480,0x87E08000,0x85908000,
		0x80008080,0x85408200,0x82208080,0x80008420,0x800084D0,0x80008240,0x80208000,0x85508000,
		0x80008080,0x83808200,0x83E08080,0x808083C0,0x80008220,0x80008120,0x80208000,0x82208000,
		0x80008080,0x810087E0,0x84108080,0x80808020,0x800081C0,0x80008000,0x80008000,0x81C08000,
		0x80008000,0x80008000,0x80008000,0x80808420,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x808083C0,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80808000,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80408000,0x80008000,0x80008000,0x80008000,0x82008200,0x86008000,
		0x81808000,0x81808180,0x80808000,0x83E08000,0x80008080,0x81C08000,0x86008600,0x81008080,
		0x82408000,0x80408040,0x81008000,0x87A08000,0x80008180,0x82208000,0x82108210,0x82108080,
		0x82408000,0x80808080,0x80008000,0x87A08000,0x80008080,0x82208000,0x82208220,0x81208000,
		0x81808080,0x81008040,0x80008420,0x87A08000,0x80008080,0x82208480,0x87408740,0x86408080,
		0x80008080,0x81C08180,0x80008420,0x83A08180,0x800081C0,0x81C08240,0x80808080,0x80808100,
		0x800083E0,0x80008000,0x80008420,0x80A08180,0x80008000,0x80008120,0x81508160,0x81508200,
		0x80008080,0x80008000,0x80008420,0x80A08000,0x80008000,0x80008240,0x82508210,0x82508200,
		0x80008080,0x80008000,0x80008660,0x80A08000,0x80008000,0x80008480,0x84708420,0x84708220,
		0x800083E0,0x80008000,0x800085A0,0x80A08000,0x80008000,0x80008000,0x80108040,0x801081C0,
		0x80008000,0x80008000,0x80008400,0x80008000,0x80408000,0x80008000,0x80108070,0x80108000,
		0x80008000,0x80008000,0x80008400,0x80008000,0x80408000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x81808000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFF7FFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x81008080,0x80808320,0x80008000,0x80008000,0x81008040,0x80808000,0x81008040,0x80808000,
		0x80808100,0x814084C0,0x86608080,0x80008000,0x80808080,0x81408660,0x80808080,0x81408360,
		0x80008000,0x80008000,0x80008140,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x83C083C0,0x83C083C0,0x83C08080,0x80F081E0,0x87E087E0,0x87E087E0,0x83E083E0,0x83E083E0,
		0x84208420,0x84208420,0x84208080,0x80C08210,0x84008400,0x84008400,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208140,0x81408400,0x84008400,0x84008400,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208140,0x81408400,0x84008400,0x84008400,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208220,0x82708400,0x87C087C0,0x87C087C0,0x80808080,0x80808080,
		0x87E087E0,0x87E087E0,0x87E083E0,0x83C08400,0x84008400,0x84008400,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208410,0x84408400,0x84008400,0x84008400,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208410,0x84408210,0x84008400,0x84008400,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208410,0x847081E0,0x87E087E0,0x87E087E0,0x83E083E0,0x83E083E0,
		0x80008000,0x80008000,0x80008000,0x80008040,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008040,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008180,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008320,0x81008040,0x808081A0,0x80008000,0x80008100,0x80408080,0x80008040,0x80008000,
		0x800084C0,0x80808080,0x814082C0,0x83608000,0x80008080,0x80808140,0x83608080,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80108000,0x80008000,0x80008000,0x80008000,
		0x83C08620,0x81C081C0,0x81C081C0,0x81C08000,0x81D08410,0x84108410,0x84108410,0x82008180,
		0x82208620,0x82208220,0x82208220,0x82208000,0x82208410,0x84108410,0x84108410,0x82008240,
		0x82108520,0x84108410,0x84108410,0x84108000,0x84508410,0x84108410,0x84108220,0x83C08240,
		0x82108520,0x84108410,0x84108410,0x84108220,0x84508410,0x84108410,0x84108140,0x822082C0,
		0x879084A0,0x84108410,0x84108410,0x84108140,0x84908410,0x84108410,0x84108080,0x82208220,
		0x821084A0,0x84108410,0x84108410,0x84108080,0x85108410,0x84108410,0x84108080,0x82208220,
		0x82108460,0x84108410,0x84108410,0x84108140,0x85108410,0x84108410,0x84108080,0x83C08220,
		0x82208460,0x82208220,0x82208220,0x82208220,0x82208220,0x82208220,0x82208080,0x82008220,
		0x83C08420,0x81C081C0,0x81C081C0,0x81C08000,0x85C081C0,0x81C081C0,0x81C08080,0x820082C0,
		0x80008000,0x80008000,0x80008000,0x80008000,0x84008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008080,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x81008080,0x80808320,0x80008140,0x80008000,0x81008080,0x81008000,0x81008080,0x80808000,
		0x80808100,0x814084C0,0x86608080,0x80008000,0x80808100,0x82808660,0x80808100,0x81408360,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x83C083C0,0x83C083C0,0x83C083C0,0x836083C0,0x83C083C0,0x83C083C0,0x83808380,0x83808380,
		0x80208020,0x80208020,0x80208020,0x80908420,0x84208420,0x84208420,0x80808080,0x80808080,
		0x83E083E0,0x83E083E0,0x83E083E0,0x83F08400,0x87E087E0,0x87E087E0,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208420,0x84808400,0x84008400,0x84008400,0x80808080,0x80808080,
		0x84208420,0x84208420,0x84208420,0x84908420,0x84208420,0x84208420,0x80808080,0x80808080,
		0x83E083E0,0x83E083E0,0x83E083E0,0x836083C0,0x83C083C0,0x83C083C0,0x83E083E0,0x83E083E0,
		0x80008000,0x80008000,0x80008000,0x80008080,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008080,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008300,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x81408320,0x81008080,0x80808320,0x80008000,0x80008100,0x80808080,0x80008080,0x82008000,
		0x808084C0,0x80808100,0x814084C0,0x86608000,0x80008080,0x81008140,0x86608100,0x82008660,
		0x81408000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x82008000,
		0x802087C0,0x83C083C0,0x83C083C0,0x83C08080,0x81A08420,0x84208420,0x84208420,0x83C08420,
		0x81E08420,0x84208420,0x84208420,0x84208000,0x82408420,0x84208420,0x84208420,0x82208420,
		0x82208420,0x84208420,0x84208420,0x842083E0,0x84A08420,0x84208420,0x84208420,0x82208420,
		0x82208420,0x84208420,0x84208420,0x84208000,0x85208420,0x84208420,0x84208420,0x82208420,
		0x82208420,0x84208420,0x84208420,0x84208080,0x82408420,0x84208420,0x84208420,0x82208420,
		0x81C08420,0x83C083C0,0x83C083C0,0x83C08000,0x858083E0,0x83E083E0,0x83E083E0,0x83C083E0,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008020,0x82008020,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008020,0x82008020,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x800083C0,0x820083C0,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x80008000,0x80808080,0x80008000,0x80008000,0x80008000,0x80008000,0x808081C0,0x80008000,
		0x80009FFC,0x81C081C0,0x80008000,0x80008000,0x80008000,0x80008000,0x81C081C0,0x80008000,
		0x801C9004,0x83608360,0x8006B000,0x800083FE,0xBFE08000,0x80008000,0x83E081C0,0x80008000,
		0x801C9634,0x86308630,0x8046B100,0x80008602,0xA0308000,0x80008000,0x87F081C0,0x84008010,
		0x821C9774,0x8C188C18,0x8066B300,0x9C988C8A,0xA8988490,0x9A089CCC,0x81C081C0,0x8C008018,
		0x861C93E4,0x9E3C9E3C,0xBFF6B7FE,0x89549852,0xA50C8A98,0xA3689110,0x81C081C0,0x9FFEBFFC,
		0x8FFC91C4,0x82208220,0xBFFEBFFE,0x89D8B022,0xA2068E90,0xA24899D0,0x81C081C0,0xBFFEBFFE,
		0x9FFC93E4,0x82208220,0xBFF6B7FE,0x89549852,0xA50C8A90,0xA2489050,0x81C081C0,0x9FFEBFFC,
		0x8FF89774,0x82208220,0x8066B300,0x89588C8A,0xA8988A48,0x99449D8C,0x81C081C0,0x8C008018,
		0x86009634,0x82208220,0x8046B100,0x80008602,0xA0308000,0x80008000,0x81C087F0,0x84008010,
		0x82009004,0x82208E38,0x8006B000,0x800083FE,0xBFE08000,0x80008000,0x81C083E0,0x80008000,
		0x80009FFC,0x82208808,0x80008000,0x80008000,0x80008000,0x80008000,0x81C081C0,0x80008000,
		0x80008000,0x83E08FF8,0x80008000,0x80008000,0x80008000,0x80008000,0x81C08080,0x80008000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0x800098A0,0x80008000,0x80008000,0x8000BFFE,0xA0028010,0xA082BEE0,0x80008080,0x8FF88000,
		0x8000A540,0x80008000,0x80008000,0xA400A000,0x90028018,0x93E4A410,0x80008140,0x90048000,
		0x80008000,0x9F008000,0x81E08000,0xAC00AFE0,0x88028014,0x8C98A808,0x83E08220,0x91C48000,
		0x99C698C6,0xA0948B9C,0x82108000,0xBFFEA840,0x84028072,0x8C98B404,0x94108810,0xA1C28000,
		0xA528A528,0xA6949842,0x84008000,0xBFFEA880,0x820A8094,0x92A4A202,0x98088C08,0xA0028000,
		0xA528A528,0xAABEA842,0x8FE08000,0xAC00A900,0x811A8118,0x91C48102,0x9404BA04,0xA3C28000,
		0xBDC8BD28,0xAA94898E,0x84008000,0xA400AA80,0x80AA8210,0x9084A082,0x9E04A102,0xA1C28000,
		0xA528A528,0xAD3E8A02,0x8FE08000,0x8012AC40,0x804A8400,0x91C4A042,0x8000BA04,0xA1C28000,
		0xA528A528,0xA0148A02,0x84008000,0x801AA820,0x808AB8FE,0x92A4A022,0x980C8C08,0xA1C28000,
		0xA5C6A4C6,0x9F948BDC,0x82108000,0xBFFEA010,0x810A8000,0x8C989004,0xA41E8810,0xA1C28000,
		0x80008004,0x80008000,0x81E08000,0xBFFEA008,0x83FA8000,0x8C988808,0xA41E8220,0x93E48000,
		0x80008008,0x80008000,0x80008000,0x801AA004,0x80028000,0x93E48410,0x980C8140,0x90048000,
		0x80008000,0x80008000,0x80008000,0x8012A002,0xBFFE8000,0xA08283E0,0x80008080,0x8FF88000,
		0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,0x80008000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80326300,0x80000000,0x80623500,0x80000000,0x802AA200,
		0x80000000,0x80000000,0x80000000,0x80455400,0x80000000,0x80554600,0x80000000,0x803AB600,
		0x80000000,0x80000000,0x80000000,0x80476700,0x80000000,0x80674600,0x80000000,0x803AAA00,
		0x80000000,0x80000000,0x80000000,0x80454100,0x80000000,0x80554500,0x80000000,0x802AA200,
		0x81D5DD80,0x83253740,0x806ABB80,0x80354600,0x80D88DC0,0x80653500,0x86775650,0x802BA200,
		0x811C9140,0x84574440,0x808AA100,0x80000000,0x81155100,0x80000000,0x85425570,0x80000000,
		0x819C99C0,0x84774640,0x80EEB100,0x80000000,0x81D9D180,0x80000000,0x87625770,0x80000000,
		0x81149180,0x84554440,0x802AA100,0x80000000,0x80515100,0x80000000,0x86425650,0x80000000,
		0x81D49D40,0x83553770,0x80CAA100,0x80473500,0x81914DC0,0x80D88DC0,0x85727550,0x80473500,
		0x80000000,0x80000000,0x80000000,0x80454600,0x80000000,0x81155100,0x80000000,0x80454600,
		0x80000000,0x80000000,0x80000000,0x80454600,0x80000000,0x81D9D180,0x80000000,0x80454600,
		0x80000000,0x80000000,0x80000000,0x80454500,0x80000000,0x80515100,0x80000000,0x80454500,
		0x80000000,0x80000000,0x80000000,0x80773500,0x80000000,0x81914DC0,0x80000000,0x80773500,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,
		0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFF,0xFFFFFFFE,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80623700,0x80623700,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80554400,0x80554400,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80675600,0x80675600,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80455400,0x80455400,0x80000000,0x80000000,
		0x80574600,0x81894DC0,0x82A6ECE0,0x80556700,0x80453700,0x80453700,0x80000000,0x80000000,
		0x80544500,0x81555100,0x82E88A40,0x80575500,0x80000000,0x80000000,0x80000000,0x80000000,
		0x80764600,0x819D5D80,0x82EECE40,0x80575500,0x80000000,0x80000000,0x80000000,0x80000000,
		0x80544400,0x81154500,0x82A28C40,0x80555500,0x80000000,0x80000000,0x80000000,0x80000000,
		0x80577400,0x8115D9C0,0x82ACEA40,0x80756700,0x80056000,0x80CE8A80,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80055000,0x80AA8B80,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80056000,0x80AAAB80,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80054000,0x80AADA80,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80074000,0x80CE8A80,0x80000000,0x80000000,
		0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,0x80000000,
	};
}
