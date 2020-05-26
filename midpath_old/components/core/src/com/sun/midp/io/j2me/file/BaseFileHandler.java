/*
 *
 * Copyright  1990-2006 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions. 
 */

package com.sun.midp.io.j2me.file;

import java.io.IOException;
import java.util.Vector;

/**
 * Base file handler.
 */
public interface BaseFileHandler {

    // JAVADOC COMMENT ELIDED
    public void connect(String rootName, String absFile);

    // JAVADOC COMMENT ELIDED
    public void createPrivateDir(String rootName) throws IOException;

    // JAVADOC COMMENT ELIDED
    public void openForRead() throws IOException;

    // JAVADOC COMMENT ELIDED
    public void closeForRead() throws IOException;

    // JAVADOC COMMENT ELIDED
    public void openForWrite() throws IOException;

    // JAVADOC COMMENT ELIDED
    public void closeForWrite() throws IOException;

    // JAVADOC COMMENT ELIDED
    public Vector list(String filter, boolean includeHidden)
        throws IOException;

    // JAVADOC COMMENT ELIDED
    public Vector listRoots();

    // JAVADOC COMMENT ELIDED
    public void create() throws IOException;

    // JAVADOC COMMENT ELIDED
    public boolean exists();

    // JAVADOC COMMENT ELIDED
    public boolean isDirectory();

    // JAVADOC COMMENT ELIDED
    public void delete() throws IOException;

    // JAVADOC COMMENT ELIDED
    public void rename(String newName) throws IOException;

    // JAVADOC COMMENT ELIDED
    public void truncate(long byteOffset) throws IOException;

    // JAVADOC COMMENT ELIDED
    public long fileSize() throws IOException;

    // JAVADOC COMMENT ELIDED
    public boolean canRead();

    // JAVADOC COMMENT ELIDED
    public boolean canWrite();

    // JAVADOC COMMENT ELIDED
    public boolean isHidden();

    // JAVADOC COMMENT ELIDED
    public void setReadable(boolean readable) throws IOException;

    // JAVADOC COMMENT ELIDED
    public void setWritable(boolean writable) throws IOException;

    // JAVADOC COMMENT ELIDED
    public void setHidden(boolean hidden) throws IOException;

    // JAVADOC COMMENT ELIDED
    public long lastModified();

    // JAVADOC COMMENT ELIDED
    public void mkdir() throws IOException;

    // JAVADOC COMMENT ELIDED
    public int read(byte b[], int off, int len) throws IOException;

    // JAVADOC COMMENT ELIDED
    public int write(byte b[], int off, int len) throws IOException;

    // JAVADOC COMMENT ELIDED
    public void flush() throws IOException;

    // JAVADOC COMMENT ELIDED
    public void positionForWrite(long offset) throws IOException;

    // JAVADOC COMMENT ELIDED
    public long availableSize();

    // JAVADOC COMMENT ELIDED
    public long totalSize();

    // JAVADOC COMMENT ELIDED
    public String illegalFileNameChars();

    // JAVADOC COMMENT ELIDED
    public long usedSize();

    // JAVADOC COMMENT ELIDED
    public void close() throws IOException;
    
    public void readFully(byte b[], int off, int len) throws IOException;
    
    public RandomAccessStream getRandomAccessStream();
}
