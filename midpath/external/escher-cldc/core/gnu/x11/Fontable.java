package gnu.x11;


/** X fontable. */
public abstract class Fontable extends Resource {
	/** Predefined. */
	public Fontable(int id) {
		super(id);
	}

	/** Create. */
	public Fontable(Display display) {
		super(display);
	}

	/** Intern. */
	public Fontable(Display display, int id) {
		super(display, id);
	}

	/** Reply of {@link #info()}. */
	public static class FontReply extends Data {
		public FontReply(Data data) {
			super(data);
		}

		/**
		 * Encapsulate an additional font property.
		 */
		public class FontProperty {

			/**
			 * The number of bytes that an object of that type takes up in the
			 * data buffer.
			 */
			public static final int SIZE = 8;

			/**
			 * The starting index of the font property.
			 */
			private int index;

			/**
			 * Creates a new FontProperty that starts at the specified index.
			 *
			 * @param i the starting index of the font property
			 */
			private FontProperty(int i) {
				index = i;
			}

			/**
			 * Returns the name of the property as Atom ID.
			 *
			 * @return the name of the property as Atom ID
			 *
			 * @see Atom
			 */
			public int name() {
				return read4(index);
			}

			/**
			 * Returns the value of the property as 32 bit value.
			 *
			 * @return the value of the property as 32 bit value
			 */
			public int value() {
				return read4(index + 4);
			}
		}

		/**
		 * Encapsulates information about one character.
		 */
		public class CharInfo {

			/**
			 * The number of bytes that an object of that type takes up in the
			 * data buffer.
			 */
			public static final int SIZE = 12;

			/**
			 * The starting index of the field.
			 */
			private int index;

			/**
			 * Creates a new CharInfo instance that starts at the specified offset
			 * in the response.
			 *
			 * @param i the starting index of the CharInfo field
			 */
			private CharInfo(int i) {
				index = i;
			}

			/**
			 * Returns the left_side_bearing of the character.
			 *
			 * @return the left_side_bearing of the character
			 */
			public int left_side_bearing() {
				return read2(index);
			}

			/**
			 * Returns the right-side-bearing of the character.
			 *
			 * @return the right-side-bearing of the character
			 */
			public int right_side_bearing() {
				return read2(index + 2);
			}

			/**
			 * Returns the width of the character.
			 *
			 * @return the width of the character
			 */
			public int character_width() {
				return read2(index + 4);
			}

			/**
			 * Returns the ascent of the character.
			 *
			 * @return the ascent of the character
			 */
			public int ascent() {
				return read2(index + 6);
			}

			/**
			 * Returns the descent of the character.
			 *
			 * @return the descent of the character
			 */
			public int descent() {
				return read2(index + 8);
			}

			/**
			 * Returns the attributes of the character.
			 *
			 * @return the attributes of the character
			 */
			public int attributes() {
				return read2(index + 10);
			}
		}

		/**
		 * The list of font properties in the response.
		 */
		private class FontPropertiesList { //extends AbstractList {

			/**
			 * The starting index of the list.
			 */
			private int index;

			/**
			 * The number of font property fields in the list.
			 */
			private int num_props;

			/**
			 * Creates a new FontPropertiesList that starts at <code>i</code>
			 * and returns <code>n</code> instances of FontProperty.
			 *
			 * @param i the starting index
			 * @param n the number of font properties to return
			 */
			private FontPropertiesList(int i, int n) {
				index = i;
				num_props = n;
			}

			/**
			 * Returns the font property at the specified index. The object will
			 * be a {@link FontProperty} instance.
			 *
			 * @param i the index of the font property to fetch
			 *
			 * @return the font property at the specified index
			 *
			 * @throws IndexOutOfBoundsException when <code>i</code> is &gt;= the
			 *         size specified in the constructor
			 */
			public Object get(int i) {
				if (i >= num_props)
					throw new IndexOutOfBoundsException("Illegal index: " + i);

				return new FontProperty(i * FontProperty.SIZE + index);
			}

			/**
			 * Returns size of this list.
			 *
			 * @return size of this list
			 */
			public int size() {
				return num_props;
			}

		}

		/**
		 * Lists the char infos in the response.
		 */
		private class CharInfosList { //extends AbstractList {

			/**
			 * The current index of the list in the response array.
			 */
			private int index;

			/**
			 * The remaining number of items.
			 */
			private int num_infos;

			/**
			 * Creates a new CharInfosList that starts at the specified
			 * index <code>i</code> and returns <code>n</code> char info items.
			 *
			 * @param i the starting index
			 * @param n the number of char info items
			 */
			private CharInfosList(int i, int n) {
				index = i;
				num_infos = n;
			}

			/**
			 * Returns the char info at the specified index. The object will
			 * be a {@link CharInfo} instance.
			 *
			 * @param i the index of the char info to fetch
			 *
			 * @return the char info at the specified index
			 *
			 * @throws IndexOutOfBoundsException when <code>i</code> is &gt;= the
			 *         size specified in the constructor
			 */
			public Object get(int i) {
				if (i >= num_infos)
					throw new IndexOutOfBoundsException("Illegal index: " + i + ", num_infos: " + num_infos);

				return new CharInfo(i * CharInfo.SIZE + index);
			}

			/**
			 * Returns size of this list.
			 *
			 * @return size of this list
			 */
			public int size() {
				return num_infos;
			}

		}

		public static final int LEFT_TO_RIGHT = 0;
		public static final int RIGHT_TO_LEFT = 1;

		/**
		 * Reads a CharInfo type field from the response.
		 *
		 * @param index the starting index of the field
		 *
		 * @return the CharInfo at that field
		 */
		private CharInfo read_char_info(int index) {
			return new CharInfo(index);
		}

		/**
		 * Reads the sequence-number field of the response.
		 *
		 * @return the sequence-number field of the response
		 */
		public int sequence_number() {
			return read2(2);
		}

		/**
		 * Reads the reply-length field of the response. This is made
		 * up of 7 bytes + 2*n bytes (FontProperty) + 3*m bytes(CharInfo).
		 *
		 * @return the reply-length field of the response
		 */
		public int reply_length() {
			return read4(4);
		}

		/**
		 * Reads the min-bounds field of the response.
		 *
		 * @return the min-bounds field of the response
		 */
		public CharInfo min_bounds() {
			// This takes up 12 bytes. Afterwards are 4 bytes unused.
			return read_char_info(8);
		}

		/**
		 * Reads the max-bounds field of the response.
		 *
		 * @return the max-bounds field of the response
		 */
		public CharInfo max_bounds() {
			// This takes up 12 bytes. Afterwards are 4 bytes unused.
			return read_char_info(24);
		}

		public int min_char_or_byte2() {
			return read2(40);
		}

		public int max_char_or_byte2() {
			return read2(42);
		}

		public int default_char() {
			return read2(44);
		}

		/**
		 * Reads the number of font properties that follow in the response.
		 *
		 * @return the number of font properties
		 */
		private int number_of_font_properties() {
			return read2(46);
		}

		public int direction() {
			return read1(48);
		}

		public int min_byte1() {
			return read1(49);
		}

		public int max_byte1() {
			return read1(50);
		}

		public boolean all_chars_exist() {
			return read_boolean(51);
		}

		public int font_ascent() {
			return read2(52);
		}

		public int font_descent() {
			return read2(54);
		}

		/**
		 * Returns the number of char info fields that follow in the response.
		 *
		 * @return the number of char info fields
		 */
		public int number_of_char_infos() {
			return read4(56);
		}

//		/**
//		 * Returns additional font properties. The values in the enumeration will
//		 * be of the type {@link FontProperty}.
//		 *
//		 * @return additional font properties
//		 */
//		public List properties() {
//			int numProps = number_of_font_properties();
//			return new FontPropertiesList(60, numProps);
//		}
//
//		/**
//		 * Returns the char infos of the response. The items in the enumeration
//		 * are of the type {@link CharInfo}.
//		 *
//		 * @return the char infos of the response
//		 */
//		public List char_infos() {
//			int numCharInfos = number_of_char_infos();
//			int numProps = number_of_font_properties();
//			return new CharInfosList(60 + numProps * 8, numCharInfos);
//		}
	}

	// opcode 47 - query font  
	/**
	 * @see <a href="XQueryFont.html">XQueryFont</a>
	 */
	public FontReply info() {
		Request request = new Request(display, 47, 2);
		request.write4(id);

		return new FontReply(display.read_reply(request));
	}

	/** Reply of {@link #text_extent(String)}. */
	public static class TextExtentReply extends Data {
		public TextExtentReply(Data data) {
			super(data);
		}

		/**
		 * Returns the ascent of the font.
		 *
		 * @return the ascent of the font
		 */
		public int ascent() {
			return read2(8);
		}

		/**
		 * Returns the descent of the font.
		 *
		 * @return the descent of the font
		 */
		public int descent() {
			return read2(10);
		}

		/**
		 * Returns the overall ascent of the font.
		 *
		 * @return the overall ascent of the font
		 */
		public int overall_ascent() {
			return read2(12);
		}

		/**
		 * Returns the overall descent of the string in the queried font.
		 *
		 * @return the overall descent of the string in the queried font
		 */
		public int overall_descent() {
			return read2(14);
		}

		/**
		 * Returns the overall width of the string in the queried font.
		 *
		 * @return the overall width of the string in the queried font
		 */
		public int overall_width() {
			return read4(16);
		}

		/**
		 * Returns the overall left of the string in the queried font.
		 *
		 * @return the overall left of the string in the queried font
		 */
		public int overall_left() {
			return read4(20);
		}

		/**
		 * Returns the overall right of the string in the queried font.
		 *
		 * @return the overall right of the string in the queried font
		 */
		public int overall_right() {
			return read4(24);
		}
	}

	// opcode 48 - query text extents  
	/**
	 * @see <a href="XQueryTextExtents.html">XQueryTextExtents</a>
	 */
	public TextExtentReply text_extent(String s) {
		boolean odd = s.length() % 2 == 1;
		int pad = odd ? 2 : 0;
		int len = 2 + (2 * s.length() + pad) / 4;
		Request request = new Request(display, 48, odd, len);
		request.write4(id);
		request.write_string16(s);
		return new TextExtentReply(display.read_reply(request));
	}
}
