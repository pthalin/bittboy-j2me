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
package org.thenesis.midpath.io;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.sun.midp.io.j2me.file.RandomAccessStream;
import com.sun.midp.log.Logging;


public class MemoryFileHandler extends AbstractFileHandler {
	
	private static Hashtable fileSystem = new Hashtable();
	private static String DEFAULT_ROOT_NAME = "/";
	
	private String rootName;
	private String absFile;
	private boolean canRead = true;
	private boolean canWrite = true;
	private RandomAccessArray randomAccessArray;
	private boolean isDirectory = false;
	private boolean isClosed = false;
	private boolean isHidden = false;
	private long timeStamp;
	
	public MemoryFileHandler() {}

	public long availableSize() {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.availableSize(): not implemented yet");
		return 200000;
		//return -1;
	}

	public boolean canRead() {
		return canRead;
	}

	public boolean canWrite() {
		return canWrite;
	}

	public void closeForRead() throws IOException {
		canRead = false;
	}

	public void closeForWrite() throws IOException {
		canWrite = false;
	}

	public void connect(String rootName, String absFile) {
		this.rootName = rootName;
		this.absFile = absFile;
		//file = new File(absFile);
	}

	public void create() throws IOException {
		
		if (!rootName.equals(DEFAULT_ROOT_NAME)) {
			throw new IOException("Unknown file system root");
		}
		
		fileSystem.put(rootName+ absFile, this);
		randomAccessArray = new RandomAccessArray(0);
		
		setTimeStamp();
		
		// TODO Check if out of memory
	}
	
	public void setTimeStamp() {
		timeStamp = System.currentTimeMillis();
	}

	public void createPrivateDir(String rootName) throws IOException {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.createPrivateDir(): not implemented yet");
	}

	public void delete() throws IOException {
		fileSystem.remove(rootName+ absFile);
	}

	public boolean exists() {
		return fileSystem.containsKey(rootName+ absFile);
	}

	public long fileSize() throws IOException {
		return randomAccessArray.getLength();
	}

	public String illegalFileNameChars() {
		return "";
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public long lastModified() {
		return timeStamp;
	}

	public Vector list(final String filter, boolean includeHidden) throws IOException {
		
		if(!isDirectory) {
			new IOException("File is not a directory");
		}
		
		Vector filteredList = new Vector();
		String directoryPath = rootName+ absFile;
		
		Enumeration e = fileSystem.keys();
		while(e.hasMoreElements()) {
			String node = (String)e.nextElement();
			MemoryFileHandler fileHandler = (MemoryFileHandler)fileSystem.get(node);
			
			String filename = node.substring(rootName.length());
			if (filterAccept(filter, filename))
				filteredList.addElement(filename);
			
		}
		
		return filteredList;
	}
	

	public Vector listRoots() {
		Vector v = new Vector();
		v.addElement(DEFAULT_ROOT_NAME);
		return v;
	}

	public void mkdir() throws IOException {
		isDirectory = true;
		fileSystem.put(rootName+ absFile, this);
	}

	public void openForRead() throws IOException {
		// Do nothing
	}

	public void openForWrite() throws IOException {
		// Do nothing
	}

	public void positionForWrite(long offset) throws IOException {
		if (randomAccessArray == null) {
			throw new IOException();
		}
		
		seek((int)offset);
	}

	public void rename(String newName) throws IOException {
		
		fileSystem.remove(rootName+ absFile);
		fileSystem.put(rootName+ newName, this);
		
//		System.out.println("[DEBUG] FileHandlerImpl.rename(): " + file);
//		System.out.println("[DEBUG] FileHandlerImpl.rename(): " + newFile);
	}

	public void setHidden(boolean hidden) throws IOException {
		isHidden = hidden;
	}

	public void setReadable(boolean readable) throws IOException {
		canRead = readable;
	}

	public void setWritable(boolean writable) throws IOException {
		canWrite = writable;
	}

	public long totalSize() {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.totalSize(): not implemented yet");
		return -1;
	}

	public void truncate(long byteOffset) throws IOException {
		if (randomAccessArray == null) {
			throw new IOException();
		}
		setLength((int)byteOffset);
	}

	public long usedSize() {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.usedSize(): not implemented yet");
		return -1;
	}
    
    /*
     * Interface RandomAccessStream 
     */

	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		if (randomAccessArray == null) {
			throw new IOException();
		}
		//System.out.println("[DEBUG] FileHandlerImpl.read: file: " + file);
		return randomAccessArray.read(b, off, len);
	}

	public void seek(int pos) throws IOException {
		randomAccessArray.seek(pos);
	}

	public void setLength(int size) throws IOException {
		randomAccessArray.setLength(size);
	}
	
	public int write(byte[] b, int off, int len) throws IOException {
		randomAccessArray.write(b, off, len);
		return len;
	}

	public int write(byte[] buf) throws IOException {
		return write(buf, 0, buf.length);
	}
	
	public void flush() throws IOException {
		// Do nothing
	}
	
	public void close() throws IOException {
		isClosed = true;
	}

	public RandomAccessStream getRandomAccessStream() {
		return this;
	}

}
