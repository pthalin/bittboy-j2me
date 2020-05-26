package com.sun.midp.io.j2me.file;

import java.io.IOException;

public interface RandomAccessStream {
	
	 /**
     * Sets the position within <code>recordStream</code> to
     * <code>pos</code>.  This will implicitly grow
     * the underlying stream if <code>pos</code> is made greater
     * than the current length of the storage stream.
     *
     * @param pos position within the file to move the current_pos
     *        pointer to.
     *
     * @exception IOException if there is a problem with the seek.
     */
    void seek(int pos) throws IOException;

    /**
     * Write all of <code>buf</code> to <code>recordStream</code>.
     *
     * @param buf buffer to read out of.
     *
     * @exception IOException if a write error occurs.
     */
    int write(byte[] buf) throws IOException;

    /**
     * Write <code>buf</code> to <code>recordStream</code>, starting
     * at <code>offset</code> and continuing for <code>numBytes</code>
     * bytes.
     *
     * @param buf buffer to read out of.
     * @param offset starting point write offset, from beginning of buffer.
     * @param numBytes the number of bytes to write.
     *
     * @exception IOException if a write error occurs.
     */
    int write(byte[] buf, int offset, int numBytes) throws IOException;

    /**
     * Commit pending writes
     *
     * @exception IOException if an error occurs while flushing
     *            <code>recordStream</code>.
     */
    void flush() throws IOException;

    /**
     * Read up to <code>buf.length</code> into <code>buf</code>.
     *
     * @param buf buffer to read in to.
     *
     * @return the number of bytes read.
     *
     * @exception IOException if a read error occurs.
     */
    int read(byte[] buf) throws IOException;

    /**
     * Read up to <code>buf.length</code> into <code>buf</code>
     * starting at offset <code>offset</code> in <code>recordStream
     * </code> and continuing for up to <code>numBytes</code> bytes.
     *
     * @param buf buffer to read in to.
     * @param offset starting point read offset, from beginning of buffer.
     * @param numBytes the number of bytes to read.
     *
     * @return the number of bytes read.
     *
     * @exception IOException if a read error occurs.
     */
    int read(byte[] buf, int offset, int numBytes) throws IOException;

    /**
     * Disconnect from <code>recordStream</code> if it is
     * non null.  May be called more than once without error.
     *
     * @exception IOException if an error occurs closing
     *            <code>recordStream</code>.
     */
    void close() throws IOException;

    /**
     * Sets the length of this <code>RecordStoreFile</code>
     * <code>size</code> bytes.  If this file was previously
     * larger than <code>size</code> the extra data is lost.
     *
     * <code>size</code> must be <= the current length of
     * <code>recordStream</code>
     *
     * @param size new size for this file.
     *
     * @exception IOException if an error occurs, or if
     * <code>size</code> is less than zero.
     */
    void setLength(int size) throws IOException;

}
