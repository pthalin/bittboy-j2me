package javax.microedition.lcdui;

public interface FontPeer {

	/**
	 * Gets the style of the font. The value is an <code>OR'ed</code>
	 * combination of
	 * <code>STYLE_BOLD</code>, <code>STYLE_ITALIC</code>, and
	 * <code>STYLE_UNDERLINED</code>; or the value is
	 * zero (<code>STYLE_PLAIN</code>).
	 * @return style of the current font
	 *
	 * @see #isPlain()
	 * @see #isBold()
	 * @see #isItalic()
	 */
	public int getStyle();

	/**
	 * Gets the size of the font.
	 *
	 * @return one of <code>SIZE_SMALL</code>, <code>SIZE_MEDIUM</code>,
	 * <code>SIZE_LARGE</code>
	 */
	public int getSize();

	/**
	 * Gets the face of the font.
	 *
	 * @return one of <code>FACE_SYSTEM</code>,
	 * <code>FACE_PROPORTIONAL</code>, <code>FACE_MONOSPACE</code>
	 */
	public int getFace();

	/**
	 * Returns <code>true</code> if the font is plain.
	 * @see #getStyle()
	 * @return <code>true</code> if font is plain
	 */
	public boolean isPlain();

	/**
	 * Returns <code>true</code> if the font is bold.
	 * @see #getStyle()
	 * @return <code>true</code> if font is bold
	 */
	public boolean isBold();

	/**
	 * Returns <code>true</code> if the font is italic.
	 * @see #getStyle()
	 * @return <code>true</code> if font is italic
	 */
	public boolean isItalic();

	/**
	 * Returns <code>true</code> if the font is underlined.
	 * @see #getStyle()
	 * @return <code>true</code> if font is underlined
	 */
	public boolean isUnderlined();

	/**
	 * Gets the standard height of a line of text in this font. This value
	 * includes sufficient spacing to ensure that lines of text painted this
	 * distance from anchor point to anchor point are spaced as intended by the
	 * font designer and the device. This extra space (leading) occurs below 
	 * the text.
	 * @return standard height of a line of text in this font (a 
	 * non-negative value)
	 */
	public int getHeight();

	/**
	 * Gets the distance in pixels from the top of the text to the text's
	 * baseline.
	 * @return the distance in pixels from the top of the text to the text's
	 * baseline
	 */
	public int getBaselinePosition();

	/**
	 * Gets the advance width of the specified character in this Font.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>ch</code> were to be drawn using this <code>Font</code>, 
	 * including inter-character spacing following
	 * <code>ch</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param ch the character to be measured
	 * @return the total advance width (a non-negative value)
	 */
	public int charWidth(char ch);

	/**
	 * Returns the advance width of the characters in <code>ch</code>, 
	 * starting at the specified offset and for the specified number of
	 * characters (length).
	 * The advance width is the horizontal distance that would be occupied if
	 * the characters were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * the characters necessary for proper positioning of subsequent text.
	 *
	 * <p>The <code>offset</code> and <code>length</code> parameters must
	 * specify a valid range of characters
	 * within the character array <code>ch</code>. The <code>offset</code>
	 * parameter must be within the
	 * range <code>[0..(ch.length)]</code>, inclusive.
	 * The <code>length</code> parameter must be a non-negative
	 * integer such that <code>(offset + length) &lt;= ch.length</code>.</p>
	 *
	 * @param ch the array of characters
	 * @param offset the index of the first character to measure
	 * @param length the number of characters to measure
	 * @return the width of the character range
	 * @throws ArrayIndexOutOfBoundsException if <code>offset</code> and
	 * <code>length</code> specify an
	 * invalid range
	 * @throws NullPointerException if <code>ch</code> is <code>null</code>
	 */
	public int charsWidth(char[] ch, int offset, int length);

	/**
	 * Gets the total advance width for showing the specified
	 * <code>String</code>
	 * in this <code>Font</code>.
	 * The advance width is the horizontal distance that would be occupied if
	 * <code>str</code> were to be drawn using this <code>Font</code>, 
	 * including inter-character spacing following
	 * <code>str</code> necessary for proper positioning of subsequent text.
	 * 
	 * @param str the <code>String</code> to be measured
	 * @return the total advance width
	 * @throws NullPointerException if <code>str</code> is <code>null</code>
	 */
	public int stringWidth(String str);

	/**
	 * Gets the total advance width for showing the specified substring in this
	 * <code>Font</code>.
	 * The advance width is the horizontal distance that would be occupied if
	 * the substring were to be drawn using this <code>Font</code>,
	 * including inter-character spacing following
	 * the substring necessary for proper positioning of subsequent text.
	 *
	 * <p>
	 * The <code>offset</code> and <code>len</code> parameters must
	 * specify a valid range of characters
	 * within <code>str</code>. The <code>offset</code> parameter must
	 * be within the
	 * range <code>[0..(str.length())]</code>, inclusive.
	 * The <code>len</code> parameter must be a non-negative
	 * integer such that <code>(offset + len) &lt;= str.length()</code>.
	 * </p>
	 *
	 * @param str the <code>String</code> to be measured
	 * @param offset zero-based index of first character in the substring
	 * @param len length of the substring
	 * @return the total advance width
	 * @throws StringIndexOutOfBoundsException if <code>offset</code> and
	 * <code>length</code> specify an
	 * invalid range
	 * @throws NullPointerException if <code>str</code> is <code>null</code>
	 */
	public int substringWidth(String str, int offset, int len);
	
	public void render(Graphics g, String str, int x, int y, int anchor);

}