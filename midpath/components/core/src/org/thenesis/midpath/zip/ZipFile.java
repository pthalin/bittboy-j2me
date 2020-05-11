/* ZipFile.java --
 Copyright (C) 2001, 2002, 2003, 2004, 2005, 2006
 Free Software Foundation, Inc.

 This file is part of GNU Classpath.

 GNU Classpath is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2, or (at your option)
 any later version.

 GNU Classpath is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with GNU Classpath; see the file COPYING.  If not, write to the
 Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 02110-1301 USA.

 Linking this library statically or dynamically with other modules is
 making a combined work based on this library.  Thus, the terms and
 conditions of the GNU General Public License cover the whole
 combination.

 As a special exception, the copyright holders of this library give you
 permission to link this library with independent modules to produce an
 executable, regardless of the license terms of these independent
 modules, and to copy and distribute the resulting executable under
 terms of your choice, provided that you also meet, for each linked
 independent module, the terms and conditions of the license of that
 module.  An independent module is a module which is not derived from
 or based on this library.  If you modify this library, you may extend
 this exception to your version of the library, but you are not
 obligated to do so.  If you do not wish to do so, delete this
 exception statement from your version. */

package org.thenesis.midpath.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import org.thenesis.microbackend.zip.Inflater;
import org.thenesis.microbackend.zip.InflaterInputStream;
import org.thenesis.microbackend.zip.ZipException;

import com.sun.midp.io.j2me.file.BaseFileHandler;
import com.sun.midp.io.j2me.file.RandomAccessStream;

/**
 * This class represents a Zip archive.  You can ask for the contained
 * entries, or get an input stream for a file entry.  The entry is
 * automatically decompressed.
 *
 * This class is thread safe:  You can open input streams for arbitrary
 * entries in different threads.
 *
 * @author Jochen Hoenicke
 * @author Artur Biesiadowski
 */
public class ZipFile implements ZipConstants {

	/**
	 * Mode flag to open a zip file for reading.
	 */
	public static final int OPEN_READ = 0x1;

	/**
	 * Mode flag to delete a zip file after reading.
	 */
	public static final int OPEN_DELETE = 0x4;

	/**
	 * This field isn't defined in the JDK's ZipConstants, but should be.
	 */
	static final int ENDNRD = 4;

	// File from which zip entries are read.
	private final BaseFileHandler fileHandler;

	// The entries of this zip file when initialized and not yet closed.
	private Hashtable entries; // LinkedHashMap<String, ZipEntry> entries;

	private boolean closed = false;

	//	/**
	//	 * Helper function to open RandomAccessFile and throw the proper
	//	 * ZipException in case opening the file fails.
	//	 *
	//	 * @param name the file name, or null if file is provided
	//	 *
	//	 * @param file the file, or null if name is provided
	//	 *
	//	 * @return the newly open RandomAccessFile, never null
	//	 */
	//	private RandomAccessFile openFile(String name, File file) throws ZipException, IOException {
	//		try {
	//			return (name != null) ? new RandomAccessFile(name, "r") : new RandomAccessFile(file, "r");
	//		} catch (FileNotFoundException f) {
	//			ZipException ze = new ZipException(f.getMessage());
	//			ze.initCause(f);
	//			throw ze;
	//		}
	//	}

	/**
	 * Opens a Zip file with the given name for reading.
	 * @exception IOException if a i/o error occured.
	 * @exception ZipException if the file doesn't contain a valid zip
	 * archive.  
	 */
	public ZipFile(BaseFileHandler bfh) throws ZipException, IOException {
		//this.raf = openFile(name, null);
		this.fileHandler = bfh;
		checkZipFile();
	}

	private void checkZipFile() throws ZipException {
		boolean valid = false;

		try {
			byte[] buf = new byte[4];
			fileHandler.readFully(buf, 0, buf.length);
			int sig = buf[0] & 0xFF | ((buf[1] & 0xFF) << 8) | ((buf[2] & 0xFF) << 16) | ((buf[3] & 0xFF) << 24);
			valid = sig == LOCSIG;
		} catch (IOException _) {
		}

		if (!valid) {
			try {
				fileHandler.close();
			} catch (IOException _) {
			}
			throw new ZipException("Not a valid zip file");
		}
	}

	/**
	 * Checks if file is closed and throws an exception.
	 */
	private void checkClosed() {
		if (closed)
			throw new IllegalStateException("ZipFile has closed");
	}

	/**
	 * Read the central directory of a zip file and fill the entries
	 * array.  This is called exactly once when first needed. It is called
	 * while holding the lock on <code>raf</code>.
	 *
	 * @exception IOException if a i/o error occured.
	 * @exception ZipException if the central directory is malformed 
	 */
	private void readEntries() throws ZipException, IOException {
		/* Search for the End Of Central Directory.  When a zip comment is 
		 * present the directory may start earlier.
		 * Note that a comment has a maximum length of 64K, so that is the
		 * maximum we search backwards.
		 */
		PartialInputStream inp = new PartialInputStream(fileHandler, 4096);
		long pos = fileHandler.fileSize() - ENDHDR;
		long top = Math.max(0, pos - 65536);
		do {
			if (pos < top)
				throw new ZipException("central directory not found, probably not a zip file");
			inp.seek(pos--);
		} while (inp.readLeInt() != ENDSIG);

		if (inp.skip(ENDTOT - ENDNRD) != ENDTOT - ENDNRD)
			throw new EOFException();
		int count = inp.readLeShort();
		if (inp.skip(ENDOFF - ENDSIZ) != ENDOFF - ENDSIZ)
			throw new EOFException();
		int centralOffset = inp.readLeInt();

		entries = new Hashtable(count + count / 2);
		inp.seek(centralOffset);

		for (int i = 0; i < count; i++) {
			if (inp.readLeInt() != CENSIG)
				throw new ZipException("Wrong Central Directory signature ");

			inp.skip(6);
			int method = inp.readLeShort();
			int dostime = inp.readLeInt();
			int crc = inp.readLeInt();
			int csize = inp.readLeInt();
			int size = inp.readLeInt();
			int nameLen = inp.readLeShort();
			int extraLen = inp.readLeShort();
			int commentLen = inp.readLeShort();
			inp.skip(8);
			int offset = inp.readLeInt();
			String name = inp.readString(nameLen);

			ZipEntry entry = new ZipEntry(name);
			entry.setMethod(method);
			entry.setCrc(crc & 0xffffffffL);
			entry.setSize(size & 0xffffffffL);
			entry.setCompressedSize(csize & 0xffffffffL);
			entry.setDOSTime(dostime);
			if (extraLen > 0) {
				byte[] extra = new byte[extraLen];
				inp.readFully(extra);
				entry.setExtra(extra);
			}
			if (commentLen > 0) {
				entry.setComment(inp.readString(commentLen));
			}
			entry.offset = offset;
			entries.put(name, entry);
		}
	}

	/**
	 * Closes the ZipFile.  This also closes all input streams given by
	 * this class.  After this is called, no further method should be
	 * called.
	 * 
	 * @exception IOException if a i/o error occured.
	 */
	public void close() throws IOException {
		RandomAccessStream raf = fileHandler.getRandomAccessStream();
		if (raf == null)
			return;

		synchronized (raf) {
			closed = true;
			entries = null;
			raf.close();
		}
	}

	/**
	 * Calls the <code>close()</code> method when this ZipFile has not yet
	 * been explicitly closed.
	 */
	protected void finalize() throws IOException {
		if (!closed && fileHandler != null)
			close();
	}

	/**
	 * Returns an enumeration of all Zip entries in this Zip file.
	 *
	 * @exception IllegalStateException when the ZipFile has already been closed
	 */
	public Enumeration entries() {
		checkClosed();

		try {
			return getEntries().elements();
			//return new ZipEntryEnumeration(getEntries().elements());
		} catch (IOException ioe) {
			return new EmptyEnumeration();
		}
	}

	/**
	 * Checks that the ZipFile is still open and reads entries when necessary.
	 *
	 * @exception IllegalStateException when the ZipFile has already been closed.
	 * @exception IOException when the entries could not be read.
	 */
	private Hashtable getEntries() throws IOException {
		synchronized (fileHandler) {
			checkClosed();

			if (entries == null)
				readEntries();

			return entries;
		}
	}

	/**
	 * Searches for a zip entry in this archive with the given name.
	 *
	 * @param name the name. May contain directory components separated by
	 * slashes ('/').
	 * @return the zip entry, or null if no entry with that name exists.
	 *
	 * @exception IllegalStateException when the ZipFile has already been closed
	 */
	public ZipEntry getEntry(String name) {
		checkClosed();

		try {
			Hashtable entries = getEntries();
			ZipEntry entry = (ZipEntry) entries.get(name);
			// If we didn't find it, maybe it's a directory.
			if (entry == null && !name.endsWith("/"))
				entry = (ZipEntry) entries.get(name + '/');
			return entry != null ? new ZipEntry(entry, name) : null;
		} catch (IOException ioe) {
			return null;
		}
	}

	/**
	 * Creates an input stream reading the given zip entry as
	 * uncompressed data.  Normally zip entry should be an entry
	 * returned by getEntry() or entries().
	 *
	 * This implementation returns null if the requested entry does not
	 * exist.  This decision is not obviously correct, however, it does
	 * appear to mirror Sun's implementation, and it is consistant with
	 * their javadoc.  On the other hand, the old JCL book, 2nd Edition,
	 * claims that this should return a "non-null ZIP entry".  We have
	 * chosen for now ignore the old book, as modern versions of Ant (an
	 * important application) depend on this behaviour.  See discussion
	 * in this thread:
	 * http://gcc.gnu.org/ml/java-patches/2004-q2/msg00602.html
	 *
	 * @param entry the entry to create an InputStream for.
	 * @return the input stream, or null if the requested entry does not exist.
	 *
	 * @exception IllegalStateException when the ZipFile has already been closed
	 * @exception IOException if a i/o error occured.
	 * @exception ZipException if the Zip archive is malformed.  
	 */
	public InputStream getInputStream(ZipEntry entry) throws IOException {
		checkClosed();

		Hashtable entries = getEntries();
		String name = entry.getName();
		ZipEntry zipEntry = (ZipEntry) entries.get(name);
		if (zipEntry == null)
			return null;

		PartialInputStream inp = new PartialInputStream(fileHandler, 1024);
		inp.seek(zipEntry.offset);

		if (inp.readLeInt() != LOCSIG)
			throw new ZipException("Wrong Local header signature: " + name);

		inp.skip(4);

		if (zipEntry.getMethod() != inp.readLeShort())
			throw new ZipException("Compression method mismatch: " + name);

		inp.skip(16);

		int nameLen = inp.readLeShort();
		int extraLen = inp.readLeShort();
		inp.skip(nameLen + extraLen);

		inp.setLength(zipEntry.getCompressedSize());

		int method = zipEntry.getMethod();
		switch (method) {
		case ZipEntry.STORED:
			return inp;
		case ZipEntry.DEFLATED:
			inp.addDummyByte();
			final Inflater inflater = new Inflater(true);
			final int sz = (int) entry.getSize();
			return new InflaterInputStream(inp, inflater) {
				public int available() throws IOException {
					if (sz == -1)
						return super.available();
					if (super.available() != 0)
						return sz - inflater.getTotalOut();
					return 0;
				}
			};
		default:
			throw new ZipException("Unknown compression method " + method);
		}
	}

	/**
	 * Returns the number of entries in this zip file.
	 *
	 * @exception IllegalStateException when the ZipFile has already been closed
	 */
	public int size() {
		checkClosed();

		try {
			return getEntries().size();
		} catch (IOException ioe) {
			return 0;
		}
	}

	private static class ZipEntryEnumeration implements Enumeration {
		private final Enumeration enumeration;

		public ZipEntryEnumeration(Enumeration enumeration) {
			this.enumeration = enumeration;
		}

		public boolean hasMoreElements() {
			return enumeration.hasMoreElements();
		}

		public Object nextElement() {
			return (ZipEntry) (enumeration.nextElement());

			// FIXME
			//      /* We return a clone, just to be safe that the user doesn't
			//       * change the entry.  
			//       */
			//      return (ZipEntry) (elements.next().clone());
		}
	}

	/**
	 * This is a helper class that produces an empty Enumerations. There is only
	 * one instance of this class that can be used whenever one needs a
	 * non-null but empty enumeration. Using this class prevents multiple
	 * small objects and inner classes. <code>getInstance()</code> returns
	 * the only instance of this class. It can be shared by multiple objects and
	 * threads.
	 *
	 * @author Mark Wielaard (mark@klomp.org)
	 */
	private static final class EmptyEnumeration implements Enumeration {
		/** The only instance of this class */
		private static final EmptyEnumeration instance = new EmptyEnumeration();

		/**
		 * Returns an instance of this class for Object.
		 * It can be shared by multiple objects and threads.
		 *
		 * @return the common empty enumeration
		 */
		public static EmptyEnumeration getInstance() {
			return instance;
		}

		/**
		 * Returns false, since there are no elements.
		 *
		 * @return false
		 */
		public boolean hasMoreElements() {
			return false;
		}

		/**
		 * Always throws <code>NoSuchElementException</code>, since it is empty.
		 *
		 * @throws NoSuchElementException this is empty
		 */
		public Object nextElement() {
			throw new NoSuchElementException();
		}
	}

	private static final class PartialInputStream extends InputStream {

		private final BaseFileHandler fHandler;
		private final byte[] buffer;
		private long bufferOffset;
		private int pos;
		private long end;
		// We may need to supply an extra dummy byte to our reader.
		// See Inflater.  We use a count here to simplify the logic
		// elsewhere in this class.  Note that we ignore the dummy
		// byte in methods where we know it is not needed.
		private int dummyByteCount;

		public PartialInputStream(BaseFileHandler fHandler, int bufferSize) throws IOException {
			this.fHandler = fHandler;
			buffer = new byte[bufferSize];
			bufferOffset = -buffer.length;
			pos = buffer.length;
			end = fHandler.fileSize();
		}

		void setLength(long length) {
			end = bufferOffset + pos + length;
		}

		private void fillBuffer() throws IOException {
			synchronized (fHandler) {
				long len = end - bufferOffset;
				if (len == 0 && dummyByteCount > 0) {
					buffer[0] = 0;
					dummyByteCount = 0;
				} else {
					RandomAccessStream ras = fHandler.getRandomAccessStream();
					ras.seek((int) bufferOffset);
					fHandler.readFully(buffer, 0, (int) Math.min(buffer.length, len));
				}
			}
		}

		public int available() {
			long amount = end - (bufferOffset + pos);
			if (amount > Integer.MAX_VALUE)
				return Integer.MAX_VALUE;
			return (int) amount;
		}

		public int read() throws IOException {
			if (bufferOffset + pos >= end + dummyByteCount)
				return -1;
			if (pos == buffer.length) {
				bufferOffset += buffer.length;
				pos = 0;
				fillBuffer();
			}

			return buffer[pos++] & 0xFF;
		}

		public int read(byte[] b, int off, int len) throws IOException {
			if (len > end + dummyByteCount - (bufferOffset + pos)) {
				len = (int) (end + dummyByteCount - (bufferOffset + pos));
				if (len == 0)
					return -1;
			}

			int totalBytesRead = Math.min(buffer.length - pos, len);
			System.arraycopy(buffer, pos, b, off, totalBytesRead);
			pos += totalBytesRead;
			off += totalBytesRead;
			len -= totalBytesRead;

			while (len > 0) {
				bufferOffset += buffer.length;
				pos = 0;
				fillBuffer();
				int remain = Math.min(buffer.length, len);
				System.arraycopy(buffer, pos, b, off, remain);
				pos += remain;
				off += remain;
				len -= remain;
				totalBytesRead += remain;
			}

			return totalBytesRead;
		}

		public long skip(long amount) throws IOException {
			if (amount < 0)
				return 0;
			if (amount > end - (bufferOffset + pos))
				amount = end - (bufferOffset + pos);
			seek(bufferOffset + pos + amount);
			return amount;
		}

		void seek(long newpos) throws IOException {
			long offset = newpos - bufferOffset;
			if (offset >= 0 && offset <= buffer.length) {
				pos = (int) offset;
			} else {
				bufferOffset = newpos;
				pos = 0;
				fillBuffer();
			}
		}

		void readFully(byte[] buf) throws IOException {
			if (read(buf, 0, buf.length) != buf.length)
				throw new EOFException();
		}

		void readFully(byte[] buf, int off, int len) throws IOException {
			if (read(buf, off, len) != len)
				throw new EOFException();
		}

		int readLeShort() throws IOException {
			int result;
			if (pos + 1 < buffer.length) {
				result = ((buffer[pos + 0] & 0xff) | (buffer[pos + 1] & 0xff) << 8);
				pos += 2;
			} else {
				int b0 = read();
				int b1 = read();
				if (b1 == -1)
					throw new EOFException();
				result = (b0 & 0xff) | (b1 & 0xff) << 8;
			}
			return result;
		}

		int readLeInt() throws IOException {
			int result;
			if (pos + 3 < buffer.length) {
				result = (((buffer[pos + 0] & 0xff) | (buffer[pos + 1] & 0xff) << 8) | ((buffer[pos + 2] & 0xff) | (buffer[pos + 3] & 0xff) << 8) << 16);
				pos += 4;
			} else {
				int b0 = read();
				int b1 = read();
				int b2 = read();
				int b3 = read();
				if (b3 == -1)
					throw new EOFException();
				result = (((b0 & 0xff) | (b1 & 0xff) << 8) | ((b2 & 0xff) | (b3 & 0xff) << 8) << 16);
			}
			return result;
		}

		/**
		 * Decode chars from byte buffer using UTF8 encoding.  This
		 * operation is performance-critical since a jar file contains a
		 * large number of strings for the name of each file in the
		 * archive.  This routine therefore avoids using the expensive
		 * utf8Decoder when decoding is straightforward.
		 *
		 * @param buffer the buffer that contains the encoded character
		 *        data
		 * @param pos the index in buffer of the first byte of the encoded
		 *        data
		 * @param length the length of the encoded data in number of
		 *        bytes.
		 *
		 * @return a String that contains the decoded characters.
		 */
		private String decodeChars(byte[] buffer, int pos, int length) throws IOException {
			String result;

			result = new String(buffer, pos, length);

			//			FIXME
			//			int i = length - 1;
			//			while ((i >= 0) && (buffer[i] <= 0x7f)) {
			//				i--;
			//			}
			//			
			//			if (i < 0) {
			//				result = new String(buffer, 0, pos, length);
			//			} else {
			//				ByteBuffer bufferBuffer = ByteBuffer.wrap(buffer, pos, length);
			//				if (utf8Decoder == null)
			//					utf8Decoder = UTF8CHARSET.newDecoder();
			//				utf8Decoder.reset();
			//				char[] characters = utf8Decoder.decode(bufferBuffer).array();
			//				result = String.valueOf(characters);
			//			}
			return result;
		}

		String readString(int length) throws IOException {
			if (length > end - (bufferOffset + pos))
				throw new EOFException();

			String result = null;
			try {
				if (buffer.length - pos >= length) {
					result = decodeChars(buffer, pos, length);
					pos += length;
				} else {
					byte[] b = new byte[length];
					readFully(b);
					result = decodeChars(b, 0, length);
				}
			} catch (UnsupportedEncodingException uee) {
				throw new IOException(uee.getMessage());
			}
			return result;
		}

		public void addDummyByte() {
			dummyByteCount = 1;
		}
	}

}
