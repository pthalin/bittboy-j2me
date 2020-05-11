/*
 * PJOGLES - Copyright (C) 2008 Guillaume Legris, Mathieu Legris
 * 
 * OGLJava - Copyright (C) 2004 Tom Dinneen
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

package org.thenesis.pjogles.glf;

import java.io.IOException;
import java.io.InputStream;

import org.thenesis.pjogles.GL11;
import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.lighting.GLColor;

public final class GLF implements GLFConstants, GLConstants {
	private static final int MAX_FONTS = 256;

	private float symbolDistance = 0.2f; // Distance between symbols (Variable constant)
	private float symbolDepth = 0.2f; // Symbol Depth in 3D space (Variable constant)
	private float spaceSize = 2.0f; // Space size (Variable constant)
	private float rotateAngle = 0.0f; // Rotate angle for string (vector font)

	/**
	 * Array of font pointers, up to MAX_FONTS fonts can be loaded at once.
	 * if (fonts[i] == NULL) then this font is not present in memory.
	 */
	private GLFFont[] fonts = new GLFFont[MAX_FONTS];

	private int currentFont = -1; // Index of current font pointer
	private int anchorPoint = GLF_CENTER; // Anchor point
	private boolean stringCenter = GL_FALSE; // String centering (vector fonts)

	private int stringDirection = GLF_LEFT; // String direction (vector fonts)

	private int texturing = GLF_NO;
	private int contouring = GLF_NO;
	private GLColor contouringColor = new GLColor();

	// Console mode variables
	private int consoleMessage = GLF_NO;
	private int consoleWidth, consoleHeight;// Console width and height
	private int consoleX = 0, consoleY = 0; // Console current X and Y
	private char[] consoleData; // Console data
	private int consoleFont = -1; // Console font
	private int consoleCursor = GLF_NO; // Console cursor Enabled|Disabled
	private int consoleCursorBlink = 10; // Console cursor blink rate
	private int consoleCursorCount = 10; // Console cursor blink counter
	private int consoleCursorMode = GLF_NO; // Console Cursor mode (on/off screen)

	// the GL handle

	private final GL11 gl;

	public GLF(final GL11 gl) {
		if (gl == null)
			throw new IllegalArgumentException("GLF(GL11) - The supplied GL is null.");

		this.gl = gl;
	}

	/**
	 * Library initialization (must be called before any usage of library).
	 */
	public void glfInit() {
		currentFont = -1;

		consoleMessage = GLF_NO;
		anchorPoint = GLF_CENTER; // Set anchor point to center of each symbol
		texturing = GLF_NO; // By default texturing is NOT Enabled
		contouring = GLF_NO; // By default contouring is NOT Enabled

		consoleData = null;

		glfSetConsoleParam(40, 20);
		glfConsoleClear();
		glfEnable(GLF_CONSOLE_CURSOR);
		glfSetCursorBlinkRate(10);
		glfStringCentering(GL_FALSE);
		glfStringDirection(GLF_LEFT);
	}

	/**
	 * Library closing (must be called after usage of library).
	 */
	public void glfClose() {
		consoleData = null;

		// unload all the fonts
		for (int i = 0; i < MAX_FONTS; ++i)
			glfUnloadFontD(i);
	}

	// Font loading/unloading functions

	/**
	 * Load Vector font to memory.
	 *
	 * @return GLF_ERROR if error. >=0 the returned font descriptor (load success).
	 */
	public int glfLoadFont(InputStream is) {
		if (is == null)
			throw new IllegalArgumentException("glfLoadFont(String) - The supplied stream is null.");

		int i;

		// First we find free font descriptor

		for (i = 0; i < MAX_FONTS; ++i) {
			if (fonts[i] == null) {
				// Initialize this font
				fonts[i] = new GLFFont();
				break;
			}
		}

		if (i == MAX_FONTS)
			return GLF_ERROR; // Free font not found

		if (readFont(is, fonts[i]) == GLF_OK) {
			currentFont = i; // Set currentFont to just loaded font
			return i;
		}

		if (fonts[i] != null)
			fonts[i] = null;

		return GLF_ERROR;
	}

	/**
	 * Unload current font from memory.
	 *
	 * @return GLF_OK if all OK, GLF_ERROR if error.
	 */
	public int glfUnloadFont() {
		if (currentFont < 0 || fonts[currentFont] == null)
			return GLF_ERROR;

		GLFFont font = fonts[currentFont];

		if (font != null) {
			for (int i = 0; i < MAX_FONTS; ++i) {
				GLFSymbol symbol = font.symbols[i];

				if (symbol != null) {
					symbol.vertices = null;
					symbol.facets = null;
					symbol.lines = null;
					font.symbols[i] = null;
				}
			}

			fonts[currentFont] = null;
		}

		currentFont = -1;

		return GLF_OK;
	}

	/**
	 * Unload font by fontDescriptor.
	 */
	public int glfUnloadFontD(final int fontDescriptor) {
		if (fontDescriptor < 0 || fonts[fontDescriptor] == null)
			return GLF_ERROR;

		final int tempFont = currentFont;
		currentFont = fontDescriptor;

		glfUnloadFont();

		if (tempFont != fontDescriptor)
			currentFont = tempFont;
		else
			currentFont = -1;

		return GLF_OK;
	}

	// Text drawing functions
	// Vector Fonts

	/**
	 * Draw wired symbol.
	 */
	public void glfDrawWiredSymbol(final char s) {
		if (currentFont < 0 || fonts[currentFont] == null)
			return;

		GLFSymbol symbol = fonts[currentFont].symbols[s];

		if (symbol == null)
			return;

		final float[] vertices = symbol.vertices;
		int currentVertex = 0;
		int currentLine = 0;

		gl.glBegin(GL_LINE_LOOP);

		for (int i = 0; i < symbol.numberOfVertices; ++i) {
			final float x = vertices[currentVertex++];
			final float y = vertices[currentVertex++];

			gl.glVertex2f(x, y);

			if (symbol.lines[currentLine] == i) {
				gl.glEnd();

				if (++currentLine < symbol.numberOfLines)
					gl.glBegin(GL_LINE_LOOP);
				else
					break; // No more lines
			}
		}
	}

	private static final int DRAW_WIRED_STRING = 0;
	private static final int DRAW_SOLID_STRING = 1;
	private static final int DRAW_3D_WIRED_STRING = 2;
	private static final int DRAW_3D_SOLID_STRING = 3;

	/**
	 * Draw wired string.
	 */
	public void glfDrawWiredString(final String s) {
		DrawString(s, DRAW_WIRED_STRING);
	}

	/**
	 * Draw wired string by fontDescriptor.
	 */
	public void glfDrawWiredStringF(final int fontDescriptor, final String s) {
		if (fontDescriptor < 0)
			throw new IllegalArgumentException("glfDrawWiredStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' < 0.");

		if (fonts[fontDescriptor] == null)
			throw new IllegalArgumentException("glfDrawWiredStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' is not valid.");

		if (s == null)
			return;

		final int tempFont = currentFont;
		currentFont = fontDescriptor;
		DrawString(s, DRAW_WIRED_STRING);
		currentFont = tempFont;
	}

	/**
	 * Draw solid symbol.
	 */
	public void glfDrawSolidSymbol(final char s) {
		if (currentFont < 0 || fonts[currentFont] == null)
			return;

		GLFSymbol symbol = fonts[currentFont].symbols[s];

		if (symbol == null)
			return;

		final int[] facets = symbol.facets;
		final float[] vertices = symbol.vertices;
		int facetIndex = 0;

		gl.glBegin(GL_TRIANGLES);

		for (int i = 0; i < symbol.numberOfFacets; ++i) {
			for (int j = 0; j < 3; ++j) {
				final int index = facets[facetIndex] << 1;
				final float x = vertices[index];
				final float y = vertices[index + 1];

				if (texturing == GLF_YES)
					gl.glTexCoord2f((x + 1) / 2, (y + 1) / 2);

				gl.glVertex2f(x, y);

				++facetIndex;
			}
		}

		gl.glEnd();

		// Draw contour, if enabled
		if (contouring == GLF_YES) {
			final float[] tempColor = new float[4];
			gl.glGetFloatv(GL_CURRENT_COLOR, tempColor);

			gl.glColor4f(contouringColor.r, contouringColor.g, contouringColor.b, contouringColor.a);
			glfDrawWiredSymbol(s);

			gl.glColor4fv(tempColor);
		}
	}

	/**
	 * Draw wired string.
	 */
	public void glfDrawSolidString(final String s) {
		DrawString(s, DRAW_SOLID_STRING);
	}

	/**
	 * Draw solid string by fontDescriptor.
	 */
	public void glfDrawSolidStringF(final int fontDescriptor, final String s) {
		if (fontDescriptor < 0)
			throw new IllegalArgumentException("glfDrawSolidStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' < 0.");

		if (fonts[fontDescriptor] == null)
			throw new IllegalArgumentException("glfDrawSolidStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' is not valid.");

		if (s == null)
			return;

		final int tempFont = currentFont;
		currentFont = fontDescriptor;
		DrawString(s, DRAW_SOLID_STRING);
		currentFont = tempFont;
	}

	/**
	 * Draw 3D wired symbol.
	 */
	public void glfDraw3DWiredSymbol(final char s) {
		if (currentFont < 0 || fonts[currentFont] == null)
			return;

		GLFSymbol symbol = fonts[currentFont].symbols[s];

		if (symbol == null)
			return;

		// Draw front symbol

		final float[] vertices = symbol.vertices;
		int vertexIndex = 0;
		int currentLine = 0;

		gl.glBegin(GL_LINE_LOOP);

		for (int i = 0; i < symbol.numberOfVertices; ++i) {
			final float x = vertices[vertexIndex++];
			final float y = vertices[vertexIndex++];

			gl.glVertex3f(x, y, 1);

			if (symbol.lines[currentLine] == i) {
				gl.glEnd();

				if (++currentLine < symbol.numberOfLines)
					gl.glBegin(GL_LINE_LOOP);
				else
					break; // No more lines
			}
		}

		// Draw back symbol

		gl.glBegin(GL_LINE_LOOP);
		vertexIndex = 0;
		currentLine = 0;

		for (int i = 0; i < symbol.numberOfVertices; ++i) {
			final float x = vertices[vertexIndex++];
			final float y = vertices[vertexIndex++];

			gl.glVertex3f(x, y, 1 + symbolDepth);

			if (symbol.lines[currentLine] == i) {
				gl.glEnd();

				if (++currentLine < symbol.numberOfLines)
					gl.glBegin(GL_LINE_LOOP);
				else
					break; // No more lines
			}
		}

		// Draw lines between back and front symbols

		gl.glBegin(GL_LINES);
		vertexIndex = 0;

		for (int i = 0; i < symbol.numberOfVertices; ++i) {
			final float x = vertices[vertexIndex++];
			final float y = vertices[vertexIndex++];

			gl.glVertex3f(x, y, 1);
			gl.glVertex3f(x, y, 1 + symbolDepth);
		}

		gl.glEnd();
	}

	/**
	 * Draw 3D wired string.
	 */
	public void glfDraw3DWiredString(final String s) {
		DrawString(s, DRAW_3D_WIRED_STRING);
	}

	/**
	 * Draw 3D wired string by fontDescriptor.
	 */
	public void glfDraw3DWiredStringF(final int fontDescriptor, final String s) {
		if (fontDescriptor < 0)
			throw new IllegalArgumentException("glfDraw3DWiredStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' < 0.");

		if (fonts[fontDescriptor] == null)
			throw new IllegalArgumentException("glfDraw3DWiredStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' is not valid.");

		if (s == null)
			return;

		final int tempFont = currentFont;
		currentFont = fontDescriptor;
		DrawString(s, DRAW_3D_WIRED_STRING);
		currentFont = tempFont;
	}

	/**
	 * Draw 3D solid symbol.
	 */
	public void glfDraw3DSolidSymbol(final char s) {
		if (currentFont < 0 || fonts[currentFont] == null)
			return;

		GLFSymbol symbol = fonts[currentFont].symbols[s];

		if (symbol == null)
			return;

		final int[] facets = symbol.facets;
		final float[] vertices = symbol.vertices;
		int facetIndex = 0;

		gl.glBegin(GL_TRIANGLES);
		gl.glNormal3f(0, 0, 1);

		for (int i = 0; i < symbol.numberOfFacets; ++i) {
			facetIndex += 2;

			for (int j = 0; j < 3; ++j) {
				final int index = facets[facetIndex] << 1;
				final float x = vertices[index];
				final float y = vertices[index + 1];

				gl.glVertex3f(x, y, 1 + symbolDepth);

				--facetIndex;
			}

			facetIndex += 4;
		}

		gl.glEnd();

		facetIndex = 0;

		gl.glBegin(GL_TRIANGLES);
		gl.glNormal3f(0, 0, -1);

		for (int i = 0; i < symbol.numberOfFacets; ++i) {
			for (int j = 0; j < 3; ++j) {
				final int index = facets[facetIndex] << 1;
				final float x = vertices[index];
				final float y = vertices[index + 1];

				gl.glVertex3f(x, y, 1);

				++facetIndex;
			}
		}

		gl.glEnd();

		boolean flag = false;

		gl.glBegin(GL_QUAD_STRIP);

		int vertexIndex = 0;
		int currentLine = 0;
		float bx = 0.0f;
		float by = 0.0f;

		for (int i = 0; i < symbol.numberOfVertices; ++i) {
			final float x = vertices[vertexIndex++];
			final float y = vertices[vertexIndex++];

			if (!flag) {
				bx = x;
				by = y;

				flag = true;
			}

			gl.glNormal3f(x, y, 0);
			gl.glVertex3f(x, y, 1);
			gl.glVertex3f(x, y, 1 + symbolDepth);

			if (symbol.lines[currentLine] == i) {
				gl.glVertex3f(bx, by, 1);
				gl.glVertex3f(bx, by, 1 + symbolDepth);

				flag = false;
				gl.glEnd();

				if (++currentLine < symbol.numberOfLines)
					gl.glBegin(GL_QUAD_STRIP);
				else
					break; // No more lines
			}
		}

		// Draw contour, if enabled

		if (contouring == GLF_YES) {
			float[] tempColor = new float[4];
			boolean lighting = gl.glIsEnabled(GL_LIGHTING);

			gl.glDisable(GL_LIGHTING);
			gl.glGetFloatv(GL_CURRENT_COLOR, tempColor);

			gl.glColor4f(contouringColor.r, contouringColor.g, contouringColor.b, contouringColor.a);
			glfDraw3DWiredSymbol(s);

			gl.glColor4fv(tempColor);

			if (lighting)
				gl.glEnable(GL_LIGHTING);
		}
	}

	/**
	 * Draw 3D solid string.
	 */
	public void glfDraw3DSolidString(final String s) {
		DrawString(s, DRAW_3D_SOLID_STRING);
	}

	/**
	 * Draw 3D solid string by fontDescriptor.
	 */
	public void glfDraw3DSolidStringF(final int fontDescriptor, final String s) {
		if (fontDescriptor < 0)
			throw new IllegalArgumentException("glfDraw3DSolidStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' < 0.");

		if (fonts[fontDescriptor] == null)
			throw new IllegalArgumentException("glfDraw3DSolidStringF(int, String) - The supplied fontDescriptor '"
					+ fontDescriptor + "' is not valid.");

		if (s == null)
			return;

		final int tempFont = currentFont;
		currentFont = fontDescriptor;
		DrawString(s, DRAW_3D_SOLID_STRING);
		currentFont = tempFont;
	}

	// Text control functions

	// Set space between symbols
	public void glfSetSymbolSpace(final float space) {
		symbolDistance = space;
	}

	/**
	 * Get space between symbols.
	 */
	public float glfGetSymbolSpace() {
		return symbolDistance;
	}

	/**
	 * Set space size.
	 */
	public void glfSetSpaceSize(final float space) {
		spaceSize = space;
	}

	/**
	 * Get current space size.
	 */
	public float glfGetSpaceSize() {
		return spaceSize;
	}

	/**
	 * Set depth of 3D symbol.
	 */
	public void glfSetSymbolDepth(final float depth) {
		symbolDepth = depth;
	}

	/**
	 * Get depth of 3D symbol.
	 */
	public float glfGetSymbolDepth() {
		return symbolDepth;
	}

	/**
	 * Set current font.
	 */
	public int glfSetCurrentFont(final int fontDescriptor) {
		if (fontDescriptor < 0 || fonts[fontDescriptor] == null)
			return GLF_ERROR;

		currentFont = fontDescriptor;

		return GLF_OK;
	}

	/**
	 * Get current font descriptor.
	 */
	public int glfGetCurrentFont() {
		return currentFont;
	}

	/**
	 * Set symbol anchor point.
	 */
	public void glfSetAnchorPoint(final int anchp) {
		if (anchp >= GLF_LEFT_UP && anchp <= GLF_RIGHT_DOWN)
			anchorPoint = anchp;
	}

	/**
	 * Contour GLFColor.
	 */
	public void glfSetContourColor(final float r, final float g, final float b, final float a) {
		contouringColor.r = r;
		contouringColor.g = g;
		contouringColor.b = b;
		contouringColor.a = a;
	}

	// Enable or Disable GLF features

	/**
	 * Enable GLF feature 'what'.
	 */
	public void glfEnable(final int what) {
		switch (what) {
		case GLF_CONSOLE_MESSAGES:
			consoleMessage = GLF_YES;
			break;
		case GLF_TEXTURING:
			texturing = GLF_YES;
			break;
		case GLF_CONSOLE_CURSOR:
			consoleCursor = GLF_YES;
			break;
		case GLF_CONTOURING:
			contouring = GLF_YES;
			break;
		}
	}

	/**
	 * Disable GLF feature 'what'.
	 */
	public void glfDisable(final int what) {
		switch (what) {
		case GLF_CONSOLE_MESSAGES:
			consoleMessage = GLF_NO;
			break;
		case GLF_TEXTURING:
			texturing = GLF_NO;
			break;
		case GLF_CONSOLE_CURSOR:
			consoleCursor = GLF_NO;
			break;
		case GLF_CONTOURING:
			contouring = GLF_NO;
			break;
		}
	}

	// Centering and direction

	/**
	 * Set string centering for vector fonts.
	 */
	public void glfStringCentering(final boolean center) {
		stringCenter = center;
	}

	/**
	 * Get string centering for vector fonts.
	 */
	public boolean glfGetStringCentering() {
		return stringCenter;
	}

	/**
	 * String direction for vector font (GLF_LEFT, GLF_RIGHT, GLF_UP, GLF_DOWN)
	 * GLF_LEFT by default.
	 */
	public void glfStringDirection(final int direction) {
		if (direction == GLF_LEFT || direction == GLF_RIGHT || direction == GLF_UP || direction == GLF_DOWN)
			stringDirection = direction;
	}

	public int glfGetStringDirection() {
		return stringDirection;
	}

	// Rotating

	/**
	 * Set rotate angle for vector fonts.
	 */
	public void glfSetRotateAngle(final float angle) {
		rotateAngle = angle;
	}

	// Console functions

	public void glfSetConsoleParam(final int width, final int height) {
		consoleWidth = width;
		consoleHeight = height;
		consoleData = new char[width * height];

		glfConsoleClear();
	}

	public int glfSetConsoleFont(final int fontDescriptor) {
		if (fontDescriptor < 0 || fonts[fontDescriptor] == null)
			return GLF_ERROR;

		consoleFont = fontDescriptor;

		return GLF_OK;
	}

	public void glfConsoleClear() {
		if (consoleData != null) {
			for (int i = 0; i < consoleData.length; ++i)
				consoleData[i] = 0;
		}

		consoleX = 0;
		consoleY = 0;
	}

	public void glfPrint(final String s, final int length) {
		if (s == null)
			throw new IllegalArgumentException("glfPrint(String, int) - The supplied String is null.");

		if (length > s.length())
			throw new IllegalArgumentException(
					"glfPrint(String, int) - The supplied length > the supplied Strings length.");

		for (int i = 0; i < length; ++i) {
			if (s.charAt(i) > 31) {
				consoleData[consoleY * consoleWidth + consoleX] = s.charAt(i);
				++consoleX;
			} else if (s.charAt(i) == '\n')
				consoleX = consoleWidth;

			if (consoleX >= consoleWidth) {
				consoleX = 0;
				++consoleY;

				if (consoleY >= consoleHeight) {
					// Shift all console contents up
					System.arraycopy(consoleData, consoleWidth, consoleData, 0, consoleWidth * (consoleHeight - 1));

					final int index = consoleWidth * (consoleHeight - 1);

					// Fill bottom line by spaces
					for (int j = 0; j < consoleWidth; ++j)
						consoleData[index + j] = 0;

					consoleY = consoleHeight - 1;
				}
			}
		}
	}

	public void glfPrintString(final String s) {
		if (s == null)
			throw new IllegalArgumentException("glfPrintString(String) - The supplied String is null.");

		glfPrint(s, s.length());
	}

	public void glfPrintChar(final char s) {
		glfPrint(Character.toString(s), 1);
	}

	public void glfConsoleDraw() {
		for (int i = 0; i < consoleHeight; ++i) {
			String s = new String(consoleData, i * consoleWidth, consoleWidth);

			if (consoleCursor == GLF_YES && i == consoleY) {
				--consoleCursorCount;

				if (consoleCursorCount < 0) {
					consoleCursorCount = consoleCursorBlink;

					if (consoleCursorMode == GLF_YES)
						consoleCursorMode = GLF_NO;
					else
						consoleCursorMode = GLF_YES;
				}

				if (consoleCursorMode == GLF_YES) {
					final int index = s.indexOf(0);

					if (index != -1)
						s = s.substring(0, index) + "_" + s.substring(index + 1);
				}
			}

			glfDrawSolidStringF(consoleFont, s);
			gl.glTranslatef(0, -2, 0);
		}
	}

	public void glfSetCursorBlinkRate(final int blinkRate) {
		if (blinkRate > 0) {
			consoleCursorBlink = blinkRate;
			consoleCursorCount = blinkRate;
			consoleCursorMode = GLF_YES;
		}
	}

	/**
	 * This function read font file and stores information in memory.
	 * @return GLF_OK if all OK, GLF_ERROR if any error.
	 */
	private static int readFont(InputStream in, final GLFFont glff) {

		try {

			final byte[] buffer = new byte[64];
			final byte[] nameBuffer = new byte[96];

			in.read(buffer, 0, 3);

			if (!new String(buffer, 0, 0, 3).equals("GLF"))
				return GLF_ERROR;

			in.read(nameBuffer);
			glff.fontname = new String(nameBuffer, 0, 0, nameBuffer.length);
			glff.numberOfSymbols = in.read(); // Read total symbols in font

			final GLFSymbol[] symbols = glff.symbols;

			for (int i = 0; i < MAX_FONTS; ++i)
				symbols[i] = null;

			in.skip(28); // Read unused data

			// Now start to read font data

			for (int i = 0; i < glff.numberOfSymbols; ++i) {
				final int code = in.read(); // Read symbol code
				final int vertices = in.read(); // Read numberOfVertices count
				final int facets = in.read(); // Read numberOfFacets count
				final int lines = in.read(); // Read lines count

				if (symbols[code] != null)
					return GLF_ERROR;

				symbols[code] = new GLFSymbol();
				GLFSymbol symbol = symbols[code];

				symbol.vertices = new float[8 * vertices];
				symbol.facets = new int[3 * facets];
				symbol.lines = new int[lines];

				symbol.numberOfVertices = vertices;
				symbol.numberOfFacets = facets;
				symbol.numberOfLines = lines;

				symbol.leftx = 10;
				symbol.rightx = -10;
				symbol.topy = 10;
				symbol.bottomy = -10;

				// Read numberOfVertices data

				for (int j = 0; j < vertices; ++j) {
					final float tempfx = Float.intBitsToFloat(in.read() | (in.read() << 8) | (in.read() << 16)
							| (in.read() << 24));
					final float tempfy = Float.intBitsToFloat(in.read() | (in.read() << 8) | (in.read() << 16)
							| (in.read() << 24));

					final int index = j << 1;
					symbol.vertices[index] = tempfx;
					symbol.vertices[index + 1] = tempfy;

					if (tempfx < symbol.leftx)
						symbol.leftx = tempfx;
					if (tempfx > symbol.rightx)
						symbol.rightx = tempfx;
					if (tempfy < symbol.topy)
						symbol.topy = tempfy;
					if (tempfy > symbol.bottomy)
						symbol.bottomy = tempfy;
				}

				for (int j = 0; j < facets; ++j) {
					final int index = j * 3;

					symbol.facets[index] = in.read();
					symbol.facets[index + 1] = in.read();
					symbol.facets[index + 2] = in.read();
				}

				for (int j = 0; j < lines; ++j)
					symbol.lines[j] = in.read();
			}

			in.close();

			return GLF_OK;
		} catch (IOException e) {
			return GLF_ERROR;
		}
	}

	private void DrawString(final String s, final int funct) {
		if (s == null)
			return;

		final int length = s.length();

		if (length == 0)
			return;

		if (currentFont == -1)
			return;

		float distance = 0.0f;

		GLFSymbol[] symbols = fonts[currentFont].symbols;

		// Calculate correction (if string centering enabled)

		if (stringCenter) {
			for (int i = 0; i < length; ++i) {
				final char chi = s.charAt(i);

				if (symbols[chi] == null || chi == ' ') {
					if (stringDirection == GLF_LEFT || stringDirection == GLF_UP)
						distance += spaceSize;
					else
						distance -= spaceSize;
				} else if (i < length - 1) {
					final char chiplus1 = s.charAt(i + 1);

					if (chiplus1 == ' ') {
						if (stringDirection == GLF_LEFT || stringDirection == GLF_UP)
							distance += symbolDistance;
						else
							distance -= symbolDistance;
					} else {
						if (symbols[chiplus1] == null)
							continue;

						if (stringDirection == GLF_LEFT || stringDirection == GLF_RIGHT) {
							float sda = Math.abs(symbols[chi].rightx);
							float sdb = Math.abs(symbols[chiplus1].leftx);

							if (stringDirection == GLF_LEFT)
								distance += sda + sdb + symbolDistance;
							else
								distance -= sda + sdb + symbolDistance;
						} else {
							float sda = Math.abs(symbols[chi].topy);
							float sdb = Math.abs(symbols[chi].bottomy); // TOMD -  chiplus1 ???

							if (stringDirection == GLF_DOWN)
								distance -= sda + sdb + symbolDistance;
							else
								distance += sda + sdb + symbolDistance;
						}
					}
				}
			}
		}

		gl.glPushMatrix();

		// Rotate if needed
		if (rotateAngle != 0.0f)
			gl.glRotatef(rotateAngle, 0, 0, 1);

		// Correct string position
		if (stringCenter) {
			switch (stringDirection) {
			case GLF_LEFT:
				gl.glTranslatef(-distance / 2, 0, 0);
				break;
			case GLF_RIGHT:
				gl.glTranslatef(distance / 2, 0, 0);
				break;
			case GLF_UP:
				gl.glTranslatef(0, distance / 2, 0);
				break;
			case GLF_DOWN:
				gl.glTranslatef(0, -distance / 2, 0);
				break;
			}
		} else if (s.charAt(0) != ' ') {
			final GLFSymbol symbol = symbols[s.charAt(0)];

			if (symbol != null) {
				switch (stringDirection) {
				case GLF_LEFT:
					gl.glTranslatef(-(1 - Math.abs(symbol.leftx)), 0, 0);
					break;
				case GLF_RIGHT:
					gl.glTranslatef((1 - Math.abs(symbol.rightx)), 0, 0);
					break;
				case GLF_UP:
					gl.glTranslatef(0, (1 - Math.abs(symbol.topy)), 0);
					break;
				case GLF_DOWN:
					gl.glTranslatef(0, -(1 - Math.abs(symbol.bottomy)), 0);
					break;
				}
			}
		}

		// Start to draw our string

		for (int i = 0; i < length; ++i) {
			final char chi = s.charAt(i);

			if (chi != ' ') {
				switch (funct) {
				case DRAW_WIRED_STRING:
					glfDrawWiredSymbol(chi);
					break;
				case DRAW_SOLID_STRING:
					glfDrawSolidSymbol(chi);
					break;
				case DRAW_3D_WIRED_STRING:
					glfDraw3DWiredSymbol(chi);
					break;
				case DRAW_3D_SOLID_STRING:
					glfDraw3DSolidSymbol(chi);
					break;
				}
			}

			if (symbols[chi] == null || chi == ' ') {
				switch (stringDirection) {
				case GLF_LEFT:
					gl.glTranslatef(spaceSize, 0, 0);
					break;
				case GLF_RIGHT:
					gl.glTranslatef(-spaceSize, 0, 0);
					break;
				case GLF_UP:
					gl.glTranslatef(0, spaceSize, 0);
					break;
				case GLF_DOWN:
					gl.glTranslatef(0, -spaceSize, 0);
					break;
				}
			} else if (i < length - 1) {
				final char chiplus1 = s.charAt(i + 1);

				if (chiplus1 == ' ') {
					switch (stringDirection) {
					case GLF_LEFT:
						gl.glTranslatef(symbolDistance, 0, 0);
						break;
					case GLF_RIGHT:
						gl.glTranslatef(-symbolDistance, 0, 0);
						break;
					case GLF_UP:
						gl.glTranslatef(0, symbolDistance, 0);
						break;
					case GLF_DOWN:
						gl.glTranslatef(0, -symbolDistance, 0);
						break;
					}
				} else {
					if (symbols[chiplus1] == null)
						continue;

					if (stringDirection == GLF_LEFT || stringDirection == GLF_RIGHT) {
						final float sda, sdb;

						if (stringDirection == GLF_LEFT) {
							sda = Math.abs(symbols[chi].rightx);
							sdb = Math.abs(symbols[chiplus1].leftx);
						} else {
							sda = Math.abs(symbols[chiplus1].rightx);
							sdb = Math.abs(symbols[chi].leftx);
						}

						if (stringDirection == GLF_LEFT)
							gl.glTranslatef(sda + sdb + symbolDistance, 0, 0);
						else
							gl.glTranslatef(-(sda + sdb + symbolDistance), 0, 0);
					} else {
						final float sda, sdb;

						if (stringDirection == GLF_DOWN) {
							sda = Math.abs(symbols[chi].topy);
							sdb = Math.abs(symbols[chiplus1].bottomy);
						} else {
							sda = Math.abs(symbols[chiplus1].topy);
							sdb = Math.abs(symbols[chi].bottomy);
						}

						if (stringDirection == GLF_DOWN)
							gl.glTranslatef(0, -(sda + sdb + symbolDistance), 0);
						else
							gl.glTranslatef(0, sda + sdb + symbolDistance, 0);
					}
				}
			}
		}

		gl.glPopMatrix();
	}
}
