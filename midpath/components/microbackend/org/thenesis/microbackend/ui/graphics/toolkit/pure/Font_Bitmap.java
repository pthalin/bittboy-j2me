package org.thenesis.microbackend.ui.graphics.toolkit.pure;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.thenesis.microbackend.ui.graphics.Rectangle;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;
import org.thenesis.microbackend.ui.graphics.VirtualToolkit;

public class Font_Bitmap extends PureFont {

	private VirtualToolkit toolkit;

	private String bitmapFontDir;
	
	private String fontUrl;
	
	private boolean charsLoaded = false;
	
	private VirtualImage[] characterImage;

	private int fontImagesTotalNumber;
	
	private int totalNumberOfCharacters;

	private byte[] characterWidths;

	private int[] xPositions;

	private String characterMap;
	
	private int fontHeightPixels;
	
	private int charactersPerPng;

	private int qmarkIndex;

	/**
	 * Creates a new bitmap font.
	 * 
	 * @param fontUrl
	 *            the url of the *.bmf file containing the font-specification.
	 */
	public Font_Bitmap(String fontDirectory, String fontUrl) {
		this.fontUrl = fontUrl;
		this.bitmapFontDir = fontDirectory;
		type = TYPE_BW;
	}

	public void setToolkit(PureToolkit toolkit) {
		this.toolkit = toolkit;
	}

	public int getLineHeightPixels() {
		 return fontHeightPixels;
	}

	public int getFace() {
		return FACE_PROPORTIONAL;
	}
 
	public int getStyle() {
		return STYLE_PLAIN;
	}

	public int findChar(char ch) {
		// Binary search.
		if (totalNumberOfCharacters <= 0)
			return -1;
		int l = 0, r = totalNumberOfCharacters - 1;
		while (l <= r) {
			int m = (l + r) >> 1;
			char me = characterMap.charAt(m);
			if (me == ch)
				return m;
			if (me > ch)
				r = m - 1;
			else
				l = m + 1;
		}
		return -1;
	}

	public int charWidth(char ch) {
		int index = findChar(ch);
		if (index == -1) return maxWidth;
		return characterWidths[index] + 1;
	}

	public int charsWidth(char[] ch, int offset, int length) {
		int width = 0;
		for (int i = 0; i < length; i++) {
			width += charWidth(ch[offset + i]);
		}
		return width;
	}

	public int drawChar(VirtualGraphics g, char character, int x, int y) {
		int index = findChar(character);
		//use '?' if char not found
		if (index == -1) index = qmarkIndex; 
		//load the remaining char images for this stringItem 
		if (characterImage == null) this.characterImage = new VirtualImage[totalNumberOfCharacters];
		int l = index / charactersPerPng;
		try {
			if (characterImage[index] == null) {
				InputStream pngIn = this.getClass().getResourceAsStream(bitmapFontDir + String.valueOf(l) + ".png");
				VirtualImage image = toolkit.createImage(pngIn);
				if (this.characterWidths[index] != 0) {
					this.characterImage[index] = toolkit.createImage(image, this.xPositions[index], 0, this.characterWidths[index], this.fontHeightPixels, VirtualImage.TRANS_NONE);
				}
				try {
					pngIn.close();
				}catch (IOException e) {
					System.out.println("Unable to close bitmap-font stream: " + bitmapFontDir + String.valueOf(l) + ".png " + e);
				}
			}
		} catch (IOException e) {
			System.out.println("Unable to open bitmap-font stream: " + bitmapFontDir + String.valueOf(l) + ".png " + e);
		}

		renderCharImage(characterImage[index], g, x, y);
		return characterWidths[index] + 1;
	}

	private void renderCharImage(VirtualImage i, VirtualGraphics g, int x, int y) {
		PureGraphics vg = (PureGraphics) g;
		Rectangle clipRect = vg.getClipRectangle();
		PureImage destSurface = (PureImage) vg.getImage();
		int dstSurfaceWidth = destSurface.getWidth();
		int[] dstSurfaceData = destSurface.pixels;

		int srcSurfaceWidth = i.getWidth();
		int srcSurfaceHeight = i.getHeight();
		int[] srcSurfaceData = ((PureImage)i).pixels;

		int sxmin = 0, symin = 0, sxmax = srcSurfaceWidth - 1, symax = srcSurfaceHeight - 1;

		// Clip destination rectangle in destination image.
		int dxmin = x, dymin = y, dxmax = x + sxmax - 1, dymax = y + symax - 1;
		if (dxmin < clipRect.xmin)
			dxmin = clipRect.xmin;
		if (dymin < clipRect.ymin)
			dymin = clipRect.ymin;
		if (dxmax > clipRect.xmax - 1)
			dxmax = clipRect.xmax - 1;
		if (dymax > clipRect.ymax - 1)
			dymax = clipRect.ymax - 1;

		// New source rectangle.
		sxmin = dxmin - x;
		symin = dymin - y;
		sxmax = dxmax - x;
		symax = dymax - y;

		int w = sxmax - sxmin + 1, h = symax - symin + 1;

		for (int ry = 0; ry < h; ry++) {
			int srcPosition = (symin + ry) * srcSurfaceWidth + sxmin;
			int dstPosition = (dymin + ry) * dstSurfaceWidth + dxmin;
			int length = w;
			for (int j = 0, sp = srcPosition, dp = dstPosition; j < length; j++, sp += 1, dp += 1) {
				if (((srcSurfaceData[j + srcPosition]) & 0xFF000000) == 0xFF000000) {
					dstSurfaceData[j + dstPosition] = vg.getInternalColor();
				}
			}
		}
	}

	/**
	 * Loads the the index-file and the font properties
	 * */
	public boolean loadFont() throws Exception {
		if (this.xPositions == null) {
			// try to load the *.bmf file:
			InputStream in = null;
			try {
				in = this.getClass().getResourceAsStream(
						this.fontUrl);
				if (in == null) {
					return false;
				}			
				DataInputStream dataIn = new DataInputStream(in);
				this.fontHeightPixels = dataIn.readInt();
				ascent = height = maxAscent = maxHeight = fontHeightPixels;
				maxAdvance = maxWidth = fontHeightPixels + 1;
				this.charactersPerPng = dataIn.readInt();
				this.fontImagesTotalNumber = dataIn.readInt();
				String map = dataIn.readUTF();		
				this.characterMap = map;
				this.qmarkIndex = map.indexOf('?');
				this.totalNumberOfCharacters = map.length();
				this.characterWidths = new byte[totalNumberOfCharacters];
				this.xPositions = new int[totalNumberOfCharacters];
				if (characterImage == null) {
					this.characterImage = new VirtualImage[totalNumberOfCharacters];
				}
				
				for (int l = 0; l < fontImagesTotalNumber; l++){
					int xPos = 0;
					for (int i = l * charactersPerPng; (i < ((l + 1) * charactersPerPng)) 
													&& (i < totalNumberOfCharacters) ; i++) {
						byte width = dataIn.readByte();
						this.characterWidths[i] = width;
						this.xPositions[i] = xPos;
						xPos += width;
					}
				}				
			} catch (IOException e) {
				System.out.println("Unable to load bitmap-font ["
						+ this.fontUrl + "]" + e);
				return false;
			} finally {
				try {
					in.close();
				} catch (IOException e) {
					System.out
							.println("Unable to close bitmap-font stream: " + e);
				}
			}
		}
		return true;
	}
	
	/**
	 * Loads the first 120 chars of the font into the cache.
	 * */
	public boolean loadChars() throws Exception {
		if (this.charsLoaded == true)
			return false;
		
		if (characterImage == null) {
			this.characterImage = new VirtualImage[totalNumberOfCharacters];
		}
		VirtualImage image = null;		
		int currentImage = 5;
		boolean currentImageChanged;
		for (int i =0; i < totalNumberOfCharacters && i < 120; i++){
			int newCurrentImage = i / this.charactersPerPng;
			if (newCurrentImage == currentImage) currentImageChanged = false;
			else currentImageChanged = true;
			currentImage = newCurrentImage;
			
			if (currentImageChanged){
				InputStream pngIn = this.getClass().getResourceAsStream( bitmapFontDir + String.valueOf(currentImage) + ".png");
				image = toolkit.createImage(pngIn);
				try {
					pngIn.close();
				}catch (IOException e) {
					System.out.println("Unable to close bitmap-font stream: " + bitmapFontDir + String.valueOf(currentImage) + ".png " + e);
				}
			}
			//TODO: try catch block?
			if (this.characterWidths[i] != 0){
				this.characterImage[i] = toolkit.createImage(image, this.xPositions[i], 0, this.characterWidths[i], this.fontHeightPixels, VirtualImage.TRANS_NONE);
			}
		}
		this.charsLoaded = true;
		return true;
	}	
}
