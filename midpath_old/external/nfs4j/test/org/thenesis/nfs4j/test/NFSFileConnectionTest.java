/*
 * NFS4J - Copyright (C) 2007 Guillaume Legris, Mathieu Legris
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.thenesis.nfs4j.test;

import java.io.IOException;
import java.util.Enumeration;

import org.thenesis.nfs4j.NFSFileConnection;

public class NFSFileConnectionTest {
	
	public static void main(String[] args) {
		NFSFileConnectionTest testSuite = new NFSFileConnectionTest();
		
		try {
			//testSuite.testFile();
			testSuite.testDirectory();
			//testSuite.testCreation();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void testURIParsing() {
//		connection.connect("///");
//		String[] pathElements = connection.getPathElements();
//		for (int i = 0; i < pathElements.length; i++) {
//			System.out.println(pathElements[i]);
//		}
//		System.out.println("path: " + connection.getPath());
//		System.out.println("name: " + connection.getName());
	}
	
	public void testFile() throws IOException {
		
		NFSFileConnection connection = new NFSFileConnection();
		connection.mount("/test");
		connection.connect("//192.168.1.1/test/dir1/dir1-file1");
		System.out.println("Available on file system: "  + connection.availableSize());
		System.out.println("Total size of the file system: "  + connection.totalSize());
		System.out.println("exists: "  + connection.exists());
		System.out.println("file size: "  + connection.fileSize());
		System.out.println("isDirectory: "  + connection.isDirectory());
		System.out.println("canRead: "  + connection.canRead());
		System.out.println("canWrite: "  + connection.canWrite());
		
		// Read file
		byte[] buffer = new byte[1000];
		int length = connection.read(buffer);
		for (int i = 0; i < length; i++) {
			System.out.print((char)buffer[i]);
		}
		System.out.println();
		
		// Write file
		connection.write("\nThis is a test\n".getBytes());
		connection.close();
	}
	
	public void testDirectory() throws IOException {
		
		NFSFileConnection connection = new NFSFileConnection();
		connection.mount("/test");
		connection.connect("//192.168.1.1/test/dir1/");
		System.out.println("Available on file system: "  + connection.availableSize());
		System.out.println("Total size of the file system: "  + connection.totalSize());
		System.out.println("exists: "  + connection.exists());
		System.out.println("isDirectory: "  + connection.isDirectory());
		System.out.println("canRead: "  + connection.canRead());
		System.out.println("canWrite: "  + connection.canWrite());
		Enumeration e = connection.list();
		while(e.hasMoreElements()) {
			System.out.println("file : " + (String)e.nextElement());
		}
		connection.setFileConnection("dir4");
		e = connection.list();
		while(e.hasMoreElements()) {
			System.out.println("file : " + (String)e.nextElement());
		}
	}
	
	public void testCreation() throws IOException {
		NFSFileConnection connection = new NFSFileConnection();
		connection.mount("/test");
//		connection.connect("//192.168.1.1/test/dir1/dir1-file2");
//		connection.create();
		connection.connect("//192.168.1.1/test/dir1/dir1-dir2/");
		//connection.mkdir();
		connection.list();
		Enumeration e = connection.list();
		while(e.hasMoreElements()) {
			System.out.println("file : " + (String)e.nextElement());
		}
		connection.rename("dir1-dir3");
		//connection.delete();
	}
}
